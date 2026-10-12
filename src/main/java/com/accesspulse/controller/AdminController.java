package com.accesspulse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.accesspulse.dto.AssignStaffRequest;
import com.accesspulse.dto.PriorityUpdateRequest;
import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Location;
import com.accesspulse.entity.User;
import com.accesspulse.service.ComplaintService;
import com.accesspulse.service.NotificationService;
import com.accesspulse.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> stats = complaintService.getSystemStatistics();
        List<Complaint> complaints = complaintService.getAllComplaints();

        model.addAttribute("currentUser", user);
        model.addAttribute("stats", stats);
        model.addAttribute("recentComplaints", complaints.stream().limit(6).toList());
        model.addAttribute("maintenanceStaff", userService.getMaintenanceStaff());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "admin/dashboard";
    }

    @GetMapping("/complaints")
    public String viewAllComplaints(@RequestParam(value = "status", required = false) String statusFilter,
            @RequestParam(value = "priority", required = false) String priorityFilter,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Complaint> complaints = complaintService.getAllComplaints();

        if (statusFilter != null && !statusFilter.isBlank()) {
            complaints = complaints.stream().filter(c -> c.getStatus().name().equalsIgnoreCase(statusFilter)).toList();
        }
        if (priorityFilter != null && !priorityFilter.isBlank()) {
            complaints = complaints.stream().filter(c -> c.getPriorityLevel().name().equalsIgnoreCase(priorityFilter))
                    .toList();
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("priorityFilter", priorityFilter);
        model.addAttribute("maintenanceStaff", userService.getMaintenanceStaff());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "admin/complaints";
    }

    @PostMapping("/complaints/{id}/verify")
    public String verifyComplaint(@PathVariable("id") Long id,
            @RequestParam(value = "adminNotes", required = false) String adminNotes,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            complaintService.verifyComplaint(id, admin, adminNotes);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint #" + id + " has been verified.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error verifying: " + e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    @PostMapping("/complaints/{id}/reject")
    public String rejectComplaint(@PathVariable("id") Long id,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            complaintService.rejectComplaint(id, admin, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint #" + id + " has been rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting: " + e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    @PostMapping("/complaints/{id}/priority")
    public String updatePriority(@PathVariable("id") Long id,
            @ModelAttribute PriorityUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        request.setComplaintId(id);
        try {
            complaintService.updatePriority(request, admin);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Priority scores updated and recalculated for #" + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating priority: " + e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    @GetMapping("/complaints/{id}/assign")
    public String showAssignForm(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Complaint complaint = complaintService.getComplaintById(id).orElseThrow();

        AssignStaffRequest assignStaffRequest = new AssignStaffRequest();
        assignStaffRequest.setComplaintId(complaint.getId());

        model.addAttribute("currentUser", user);
        model.addAttribute("complaint", complaint);
        model.addAttribute("assignStaffRequest", assignStaffRequest);
        model.addAttribute("maintenanceStaff", userService.getMaintenanceStaff());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "admin/assign-complaint";
    }

    @PostMapping("/complaints/{id}/assign")
    public String assignStaff(@PathVariable("id") Long id,
            @Valid @ModelAttribute("assignStaffRequest") AssignStaffRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        request.setComplaintId(id);

        if (bindingResult.hasErrors()) {
            Complaint complaint = complaintService.getComplaintById(id).orElseThrow();
            model.addAttribute("currentUser", admin);
            model.addAttribute("complaint", complaint);
            model.addAttribute("maintenanceStaff", userService.getMaintenanceStaff());
            model.addAttribute("unreadCount", notificationService.getUnreadCount(admin));
            return "admin/assign-complaint";
        }

        try {
            User staff = userService.findById(request.getStaffId())
                    .orElseThrow(() -> new IllegalArgumentException("Selected maintenance staff not found"));

            complaintService.assignComplaint(request, admin, staff);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Complaint #" + id + " assigned to " + staff.getFullName() + " successfully.");
            return "redirect:/admin/complaints";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Assignment failed: " + e.getMessage());
            return "redirect:/admin/complaints/" + id + "/assign";
        }
    }

    @GetMapping("/analytics")
    public String analyticsDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> stats = complaintService.getSystemStatistics();

        model.addAttribute("currentUser", user);
        model.addAttribute("stats", stats);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/analytics";
    }

    @GetMapping("/locations")
    public String manageLocations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("currentUser", user);
        model.addAttribute("locations", complaintService.getAllLocations());
        model.addAttribute("newLocation", new Location());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/manage-locations";
    }

    @PostMapping("/locations")
    public String addLocation(@ModelAttribute("newLocation") Location location,
            RedirectAttributes redirectAttributes) {
        try {
            complaintService.saveLocation(location);
            redirectAttributes.addFlashAttribute("successMessage", "Campus location added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving location: " + e.getMessage());
        }
        return "redirect:/admin/locations";
    }

    @GetMapping("/users")
    public String manageUsers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("currentUser", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/manage-users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated.");
        return "redirect:/admin/users";
    }
}
