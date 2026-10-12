package com.accesspulse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FeedbackRequest {

    private Long complaintId;

    @Min(1) @Max(5)
    private int rating = 5;

    private boolean resolutionSatisfactory = true;

    private String comments;
}
