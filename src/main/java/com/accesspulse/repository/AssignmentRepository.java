package com.accesspulse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accesspulse.entity.Assignment;
import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.User;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByAssignedStaffOrderByAssignedAtDesc(User assignedStaff);
    List<Assignment> findByAssignedStaffAndStatusOrderByAssignedAtDesc(User assignedStaff, String status);
    Optional<Assignment> findTopByComplaintOrderByAssignedAtDesc(Complaint complaint);
    List<Assignment> findByComplaintOrderByAssignedAtDesc(Complaint complaint);
}
