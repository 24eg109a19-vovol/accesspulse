package com.accesspulse.dto;

import com.accesspulse.enums.ComplaintCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ComplaintRequest {

    @NotBlank(message = "Please provide a clear title for the issue")
    private String title;

    @NotNull(message = "Please select a category")
    private ComplaintCategory category;

    @NotNull(message = "Please select the campus location")
    private Long locationId;

    @NotBlank(message = "Please describe the barrier in detail")
    private String description;

    private MultipartFile imageFile;

    @Min(1) @Max(5)
    private int accessibilityImpact = 3;

    @Min(1) @Max(5)
    private int peopleAffected = 3;

    @Min(1) @Max(5)
    private int safetyRisk = 3;
}
