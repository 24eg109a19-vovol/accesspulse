package com.accesspulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResolutionRequest {

    private Long complaintId;

    @NotBlank(message = "Please provide detailed notes on how the barrier was resolved")
    private String completionNotes;

    private MultipartFile completionImage;
}
