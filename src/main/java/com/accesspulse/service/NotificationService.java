package com.accesspulse.service;

import com.accesspulse.entity.Notification;
import com.accesspulse.entity.User;
import com.accesspulse.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(User recipient, String title, String message, String linkUrl) {
        if (recipient == null) {
            return null;
        }
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .linkUrl(linkUrl)
                .read(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public List<Notification> getRecentUserNotifications(User user) {
        return notificationRepository.findTop5ByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        for (Notification n : unread) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}
