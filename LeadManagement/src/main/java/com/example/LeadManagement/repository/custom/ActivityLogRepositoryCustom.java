package com.example.LeadManagement.repository.custom;

import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.model.ActivityLogs;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.model.EntityType;


import java.util.List;
import java.util.Optional;

public interface ActivityLogRepositoryCustom {

        ActivityLogs saveLog(ActivityLogs activityLog);

        PageResponseDTO<ActivityLogResponseDTO> getAllLogs();

        List<ActivityLogs> getLogsByEntity(EntityType entityType, String entityId);

        PageResponseDTO<ActivityLogResponseDTO> searchLogs(SearchRequestDTO requestDTO);

    }
