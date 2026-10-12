package com.accesspulse.repository;

import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    Optional<Resolution> findByComplaint(Complaint complaint);
}
