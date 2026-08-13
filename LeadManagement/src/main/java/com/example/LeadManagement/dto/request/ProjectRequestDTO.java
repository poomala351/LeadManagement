package com.example.LeadManagement.dto.request;



import com.example.LeadManagement.model.ProjectFile;
import com.example.LeadManagement.model.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProjectRequestDTO {

        @NotBlank(message = "Project Name is required")
        private String projectName;

        @NotBlank(message = "Client Name is required")
        private String clientName;

        @Email(message="Invalid email format")
        @NotBlank(message = "clientEmail is required")
        private String clientEmail;

        @Pattern(regexp = "^[0-9]{10}$", message ="Phone number must contain exactly 10 digits")
        private String clientPhone;


        @Positive(message = "Expected Budget must be greater than 0")
        @NotNull
        private Double budget;

        private String assignedUserId;

        private ProjectStatus status;

        private List<ProjectFile> files;

    }
