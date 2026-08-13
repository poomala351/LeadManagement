package com.example.LeadManagement.dto.response;

import com.example.LeadManagement.model.ActionType;
import com.example.LeadManagement.model.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class ActivityLogResponseDTO {

        private String id;

        private EntityType entityType;

        private String entityId;

        private ActionType actionType;

        private String message;

        private String performedBy;

        private LocalDateTime createdAt;

        private String oldValue;

        private String newValue;

    }

