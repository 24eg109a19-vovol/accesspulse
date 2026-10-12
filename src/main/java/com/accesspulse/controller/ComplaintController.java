package com.accesspulse.controller;

import com.accesspulse.dto.ComplaintRequest;
import com.accesspulse.dto.FeedbackRequest;
import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Feedback;
import com.accesspulse.entity.Resolution;
import com.accesspulse.entity.StatusHistory;
import com.accesspulse.entity.User;
import com.accesspulse.enums.ComplaintCategory;
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
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/student/dashboard")
    public String studentDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Complaint> complaints = complaintService.getComplaintsByReporter(user);

        long pendingCount = complaints.stream().filter(c -> c.getStatus().name().matches("SUBMITTED|UNDER_REVIEW|VERIFIED|ASSIGNED|IN_PROGRESS")).count();
        long resolvedCount = complaints.stream().filter(c -> c.getStatus().name().matches("RESOLVED|CLOSED")).count();

        model.addAttribute("currentUser", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalComplaints", complaints.size());
        model.addAttribute("pendingComplaints", pendingCount);
        model.addAttribute("resolvedComplaints", resolvedCount);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "student/dashboard";
    }

    @GetMapping({"/student/report", "/complaints/new"})
    public String showReportForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!model.containsAttribute("complaintRequest")) {
            model.addAttribute("complaintRequest", new ComplaintRequest());
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("categories", ComplaintCategory.values());
        model.addAttribute("locations", complaintService.getAllLocations());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "student/report-complaint";
    }

    @PostMapping({"/student/report", "/complaints/new"})
    public String submitComplaint(@Valid @ModelAttribute("complaintRequest") ComplaintRequest complaintRequest,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("categories", ComplaintCategory.values());
            model.addAttribute("locations", complaintService.getAllLocations());
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "student/report-complaint";
        }

        try {
            Complaint saved = complaintService.createComplaint(complaintRequest, user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Barrier report #" + saved.getId() + " submitted successfully! Initial calculated Priority: " + saved.getPriorityLevel().getDisplayName() + " (Score: " + saved.getPriorityScore() + ").");
            return "redirect:/complaints/" + saved.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error submitting report: " + e.getMessage());
            model.addAttribute("currentUser", user);
            model.addAttribute("categories", ComplaintCategory.values());
            model.addAttribute("locations", complaintService.getAllLocations());
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "student/report-complaint";
        }
    }

    @GetMapping("/student/my-complaints")
    public String myComplaints(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Complaint> complaints = complaintService.getComplaintsByReporter(user);

        model.addAttribute("currentUser", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "student/my-complaints";
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaintDetails(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint #" + id + " not found."));

        List<StatusHistory> timeline = complaintService.getComplaintTimeline(complaint);
        Resolution resolution = complaintService.getResolution(complaint).orElse(null);
        Feedback feedback = complaintService.getFeedback(complaint).orElse(null);

        model.addAttribute("currentUser", user);
        model.addAttribute("complaint", complaint);
        model.addAttribute("timeline", timeline);
        model.addAttribute("resolution", resolution);
        model.addAttribute("feedback", feedback);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "student/complaint-detail";
    }

    @GetMapping("/complaints/{id}/verify")
    public String showVerifyResolutionForm(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint #" + id + " not found."));

        if (!complaint.getReporter().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")) {
            return "redirect:/complaints/" + id;
        }

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setComplaintId(complaint.getId());

        Resolution resolution = complaintService.getResolution(complaint).orElse(null);

        model.addAttribute("currentUser", user);
        model.addAttribute("complaint", complaint);
        model.addAttribute("resolution", resolution);
        model.addAttribute("feedbackRequest", feedbackRequest);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "student/verify-resolution";
    }

    @PostMapping("/complaints/{id}/verify")
    public String processVerifyResolution(@PathVariable("id") Long id,
                                          @ModelAttribute("feedbackRequest") FeedbackRequest feedbackRequest,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        feedbackRequest.setComplaintId(id);

        try {
            complaintService.verifyAndCloseComplaint(feedbackRequest, user);
            if (feedbackRequest.isResolutionSatisfactory()) {
                redirectAttributes.addFlashAttribute("successMessage", "Thank you! The resolution has been verified and this complaint is now closed.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Resolution has been flagged as unsatisfactory and returned for administrative review.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error verifying resolution: " + e.getMessage());
        }

        return "redirect:/complaints/" + id;
    }
}
