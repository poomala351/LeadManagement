package com.example.LeadManagement.service;


import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.model.ActionType;
import com.example.LeadManagement.model.EntityType;

import java.util.List;

public interface ActivityLogService {

    void saveLog(EntityType entityType,
                 String entityId,
                 ActionType actionType,
                 String message,
                 String performedBy,
                 String oldValue,
                 String newValue);

    PageResponseDTO<ActivityLogResponseDTO> getAllLogs();

    List<ActivityLogResponseDTO> getLogsByEntity(EntityType entityType, String entityId);

    PageResponseDTO<ActivityLogResponseDTO> searchLogs(SearchRequestDTO requestDTO);
}
