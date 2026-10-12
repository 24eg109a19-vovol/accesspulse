package com.accesspulse.repository;

import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Location;
import com.accesspulse.entity.User;
import com.accesspulse.enums.ComplaintCategory;
import com.accesspulse.enums.ComplaintStatus;
import com.accesspulse.enums.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByReporterOrderByCreatedAtDesc(User reporter);
    List<Complaint> findByAssignedStaffOrderByPriorityScoreDescCreatedAtDesc(User assignedStaff);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByPriorityLevel(PriorityLevel priorityLevel);
    List<Complaint> findAllByOrderByPriorityScoreDescCreatedAtDesc();

    long countByStatus(ComplaintStatus status);
    long countByPriorityLevel(PriorityLevel priorityLevel);
    long countByCategory(ComplaintCategory category);
    boolean existsByLocation(Location location);

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();

    @Query("SELECT c.priorityLevel, COUNT(c) FROM Complaint c GROUP BY c.priorityLevel")
    List<Object[]> countComplaintsByPriority();
}
