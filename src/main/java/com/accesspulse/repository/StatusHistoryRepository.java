package com.accesspulse.repository;

import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByComplaintOrderByTimestampAsc(Complaint complaint);
}
