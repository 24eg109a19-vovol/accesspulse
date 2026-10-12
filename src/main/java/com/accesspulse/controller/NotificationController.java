package com.accesspulse.controller;

import com.accesspulse.entity.Notification;
import com.accesspulse.entity.User;
import com.accesspulse.service.NotificationService;
import com.accesspulse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/notifications")
    public String viewNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Notification> notifications = notificationService.getUserNotifications(user);

        model.addAttribute("currentUser", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));

        return "student/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }
}
