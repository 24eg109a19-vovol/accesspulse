package com.accesspulse.repository;

import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByComplaint(Complaint complaint);
}
