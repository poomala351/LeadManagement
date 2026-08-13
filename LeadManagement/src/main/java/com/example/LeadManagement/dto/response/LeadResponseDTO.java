package com.example.LeadManagement.dto.response;


import com.example.LeadManagement.model.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadResponseDTO {

        private String id;

        private String leadName;

        private String email;

        private String phone;

        private String source;

        private String description;

        private Double expectedBudget;

        private String assignedUserId;

        private LeadStatus status;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }

