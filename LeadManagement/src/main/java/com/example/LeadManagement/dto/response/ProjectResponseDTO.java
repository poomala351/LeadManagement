package com.example.LeadManagement.dto.response;


import com.example.LeadManagement.model.ProjectFile;
import com.example.LeadManagement.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {

    private String id;

    private String projectName;

    private String clientName;

    private String clientEmail;

    private String clientPhone;

    private Double budget;

    private String assignedUserId;

    private ProjectStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ProjectFile> files;
}


