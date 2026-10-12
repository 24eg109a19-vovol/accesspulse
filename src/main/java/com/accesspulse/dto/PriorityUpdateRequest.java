package com.accesspulse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityUpdateRequest {

    @NotNull
    private Long complaintId;

    @Min(1) @Max(5)
    private int accessibilityImpact;

    @Min(1) @Max(5)
    private int peopleAffected;

    @Min(1) @Max(5)
    private int safetyRisk;

    private String adminNotes;
}
