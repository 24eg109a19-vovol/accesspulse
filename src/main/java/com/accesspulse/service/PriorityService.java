package com.accesspulse.service;

import com.accesspulse.entity.Complaint;
import com.accesspulse.entity.Location;
import com.accesspulse.enums.PriorityLevel;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PriorityService {

    /**
     * Calculates the composite priority score for a complaint.
     * Formula:
     * Priority Score = Accessibility Impact (1-5)
     *                + Number of People Affected (1-5)
     *                + Safety Risk (1-5)
     *                + Location Importance (1-5)
     *                + Days Unresolved
     */
    public double calculatePriorityScore(int accessibilityImpact,
                                         int peopleAffected,
                                         int safetyRisk,
                                         int locationImportance,
                                         LocalDateTime createdAt) {
        long daysUnresolved = 0;
        if (createdAt != null) {
            daysUnresolved = Duration.between(createdAt, LocalDateTime.now()).toDays();
            if (daysUnresolved < 0) {
                daysUnresolved = 0;
            }
        }

        // Cap days unresolved factor at 10 to prevent unbounded overflow while prioritizing stalled issues
        double daysFactor = Math.min(daysUnresolved, 10);

        return accessibilityImpact + peopleAffected + safetyRisk + locationImportance + daysFactor;
    }

    /**
     * Recalculates and updates the priority score and level for an existing complaint.
     */
    public void updateComplaintPriority(Complaint complaint) {
        int locImportance = 3;
        Location loc = complaint.getLocation();
        if (loc != null && loc.getImportanceScore() > 0) {
            locImportance = loc.getImportanceScore();
        }

        double score = calculatePriorityScore(
                complaint.getAccessibilityImpact(),
                complaint.getPeopleAffected(),
                complaint.getSafetyRisk(),
                locImportance,
                complaint.getCreatedAt()
        );

        complaint.setLocationImportance(locImportance);
        complaint.setPriorityScore(score);
        complaint.setPriorityLevel(determinePriorityLevel(score));
    }

    /**
     * Determines PriorityLevel enum based on calculated score:
     * 0-7: LOW
     * 8-14: MEDIUM
     * 15-20: HIGH
     * 21+: CRITICAL
     */
    public PriorityLevel determinePriorityLevel(double score) {
        if (score <= 7.0) {
            return PriorityLevel.LOW;
        } else if (score <= 14.0) {
            return PriorityLevel.MEDIUM;
        } else if (score <= 20.0) {
            return PriorityLevel.HIGH;
        } else {
            return PriorityLevel.CRITICAL;
        }
    }
}
