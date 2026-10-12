package com.accesspulse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class AssignStaffRequest {

    private Long complaintId;

    @NotNull(message = "Please select a maintenance staff member")
    private Long staffId;

    private String instructions;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetCompletionDate;
}
