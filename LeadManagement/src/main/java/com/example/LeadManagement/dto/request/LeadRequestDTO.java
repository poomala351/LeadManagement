package com.example.LeadManagement.dto.request;


import com.example.LeadManagement.model.LeadStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class LeadRequestDTO {

        @NotBlank(message = "Lead Name is required")
        private String leadName;

        @Email(message = "Invalid Email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Phone Number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
        private String phone;

        @NotBlank(message = "Source is required")
        private String source;

        @NotBlank(message = "description is required.")
        private String description;

        @NotNull(message = "Expected Budget is required")
        @Positive(message = "Expected Budget must be greater than 0")
        private Double expectedBudget;

        private String assignedUserId;

        @Builder.Default
        private LeadStatus status = LeadStatus.NEW;
    }

