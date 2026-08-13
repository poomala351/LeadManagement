package com.example.LeadManagement.service.impl;

import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.model.ActionType;
import com.example.LeadManagement.model.ActivityLogs;
import com.example.LeadManagement.model.EntityType;
import com.example.LeadManagement.repository.ActivityLogRepository;
import com.example.LeadManagement.service.ActivityLogService;
import com.example.LeadManagement.util.MapperUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

@RequiredArgsConstructor
@Slf4j
public class ActivityLogServiceImpl implements ActivityLogService {

        private final ActivityLogRepository activityLogRepository;

    @Override
    public void saveLog(EntityType entityType,
                        String entityId,
                        ActionType actionType,
                        String message,
                        String performedBy,
                        String oldValue,
                        String newValue) {

        log.info("========== Activity Log ==========");
        log.info("EntityType : {}", entityType);
        log.info("EntityId   : {}", entityId);
        log.info("Action     : {}", actionType);
        log.info("Message    : {}", message);

        ActivityLogs activityLog = ActivityLogs.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType(actionType)
                .message(message)
                .performedBy(performedBy)
                .oldValue(oldValue)
                .newValue(newValue)
                .createdAt(LocalDateTime.now())
                .build();

        ActivityLogs saved = activityLogRepository.saveLog(activityLog);

        log.info("Saved Activity Id : {}", saved.getId());
        log.info("===============================");

        log.info("Activity log saved successfully");
    }
        @Override
        public PageResponseDTO<ActivityLogResponseDTO> getAllLogs() {

            log.info("Fetching all activity logs");
            return activityLogRepository.getAllLogs();
        }

    @Override
    public List<ActivityLogResponseDTO> getLogsByEntity(EntityType entityType,
                                                        String entityId) {

        log.info("Fetching activity logs for {} : {}", entityType, entityId);

        List<ActivityLogs> logs = activityLogRepository.getLogsByEntity(entityType, entityId);

        if (logs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No Activity Logs found");
        }

        return logs.stream()
                .map(MapperUtil::toActivityLogResponse)
                .toList();
    }

        @Override
        public PageResponseDTO<ActivityLogResponseDTO> searchLogs(SearchRequestDTO requestDTO) {
            log.info("Searching activity logs");
            return activityLogRepository.searchLogs(requestDTO);
        }

    }

