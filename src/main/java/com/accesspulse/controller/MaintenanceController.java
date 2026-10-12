package com.accesspulse.controller;

import com.accesspulse.dto.ResolutionRequest;
import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Resolution;
import com.accesspulse.entity.StatusHistory;
import com.accesspulse.entity.User;
import com.accesspulse.enums.ComplaintStatus;
import com.accesspulse.service.ComplaintService;
import com.accesspulse.service.NotificationService;
import com.accesspulse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public String maintenanceDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User staff = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Complaint> assignedComplaints = complaintService.getComplaintsAssignedTo(staff);

        long activeTasks = assignedComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.ASSIGNED || c.getStatus() == ComplaintStatus.IN_PROGRESS)
                .count();

        long resolvedTasks = assignedComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED)
                .count();

        model.addAttribute("currentUser", staff);
        model.addAttribute("complaints", assignedComplaints);
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("resolvedTasks", resolvedTasks);
        model.addAttribute("totalTasks", assignedComplaints.size());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(staff));

        return "maintenance/dashboard";
    }

    @GetMapping("/complaints/{id}")
    public String viewAssignedTask(@PathVariable("id") Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        User staff = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Complaint complaint = complaintService.getComplaintById(id).orElseThrow();

        ResolutionRequest resolutionRequest = new ResolutionRequest();
        resolutionRequest.setComplaintId(complaint.getId());

        List<StatusHistory> timeline = complaintService.getComplaintTimeline(complaint);
        Resolution resolution = complaintService.getResolution(complaint).orElse(null);

        model.addAttribute("currentUser", staff);
        model.addAttribute("complaint", complaint);
        model.addAttribute("resolutionRequest", resolutionRequest);
        model.addAttribute("timeline", timeline);
        model.addAttribute("resolution", resolution);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(staff));

        return "maintenance/update-complaint";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") ComplaintStatus newStatus,
                               @RequestParam(value = "remarks", defaultValue = "Status updated by maintenance staff") String remarks,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User staff = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            complaintService.updateMaintenanceProgress(id, newStatus, remarks, staff);
            redirectAttributes.addFlashAttribute("successMessage", "Work status updated to " + newStatus.getDisplayName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
        }
        return "redirect:/maintenance/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/resolve")
    public String submitResolution(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("resolutionRequest") ResolutionRequest request,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        User staff = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        request.setComplaintId(id);

        if (bindingResult.hasErrors()) {
            Complaint complaint = complaintService.getComplaintById(id).orElseThrow();
            model.addAttribute("currentUser", staff);
            model.addAttribute("complaint", complaint);
            model.addAttribute("timeline", complaintService.getComplaintTimeline(complaint));
            model.addAttribute("unreadCount", notificationService.getUnreadCount(staff));
            return "maintenance/update-complaint";
        }

        try {
            complaintService.submitResolution(request, staff);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Resolution details and completion proof recorded! Issue is now marked RESOLVED and pending student verification.");
            return "redirect:/maintenance/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Resolution submission failed: " + e.getMessage());
            return "redirect:/maintenance/complaints/" + id;
        }
    }
}
