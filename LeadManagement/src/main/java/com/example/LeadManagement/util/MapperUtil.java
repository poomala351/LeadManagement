package com.example.LeadManagement.util;

import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.dto.request.ProjectRequestDTO;
import com.example.LeadManagement.dto.request.UserRequestDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.dto.response.LeadResponseDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.example.LeadManagement.model.*;


import java.time.LocalDateTime;

public final class MapperUtil {

    public static Users toUser(UserRequestDTO dto) {

        return Users.builder()
                .name(dto.getName()) // need to insert the id , it automatically insert in backend
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static UserResponseDTO toUserResponse(Users users) {
        return UserResponseDTO.builder()
                .id(users.getId())
                .name(users.getName())
                .email(users.getEmail())
                .phone(users.getPhone())
                .isActive(users.getIsActive())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }

    public static Leads toLead(LeadRequestDTO dto) {
        return Leads.builder()
                .leadName(dto.getLeadName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .source(dto.getSource())
                .description(dto.getDescription())
                .expectedBudget(dto.getExpectedBudget())
                .assignedUserId(dto.getAssignedUserId())
                .status(dto.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static LeadResponseDTO toLeadResponse(Leads lead) {

        return LeadResponseDTO.builder()
                .id(lead.getId())
                .leadName(lead.getLeadName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .description(lead.getDescription())
                .expectedBudget(lead.getExpectedBudget())
                .assignedUserId(lead.getAssignedUserId())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }

    public static Projects toProject(ProjectRequestDTO dto) {

        return Projects.builder()
                .projectName(dto.getProjectName())
                .clientName(dto.getClientName())
                .clientEmail(dto.getClientEmail())
                .clientPhone(dto.getClientPhone())
                .budget(dto.getBudget())
                .assignedUserId(dto.getAssignedUserId())
                .status(ProjectStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public static ProjectResponseDTO toProjectResponse(Projects project) {

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .clientName(project.getClientName())
                .clientEmail(project.getClientEmail())
                .clientPhone(project.getClientPhone())
                .budget(project.getBudget())
                .assignedUserId(project.getAssignedUserId())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public static ActivityLogResponseDTO toActivityLogResponse(ActivityLogs log) {

        return ActivityLogResponseDTO.builder()
                .id(log.getId())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .actionType(log.getActionType())
                .message(log.getMessage())
                .performedBy(log.getPerformedBy())
                .createdAt(log.getCreatedAt())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .build();
    }
}
