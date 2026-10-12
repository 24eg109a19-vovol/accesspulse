package com.accesspulse.repository;

import com.accesspulse.entity.Notification;
import com.accesspulse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findTop5ByRecipientOrderByCreatedAtDesc(User recipient);
    long countByRecipientAndReadFalse(User recipient);
}
