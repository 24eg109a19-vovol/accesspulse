package com.accesspulse.controller;

import com.accesspulse.entity.User;
import com.accesspulse.enums.Role;
import com.accesspulse.service.ComplaintService;
import com.accesspulse.service.NotificationService;
import com.accesspulse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    @GetMapping({"/", "/home"})
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> stats = complaintService.getSystemStatistics();
        model.addAttribute("stats", stats);

        if (userDetails != null) {
            userService.findByEmail(userDetails.getUsername()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            });
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String roleDashboardDispatcher(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == Role.ROLE_ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole() == Role.ROLE_MAINTENANCE) {
            return "redirect:/maintenance/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}
