package com.example.LeadManagement.controller;

import com.example.LeadManagement.dto.ApiResponse;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.model.EntityType;
import com.example.LeadManagement.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/activitylogs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ApiResponse<PageResponseDTO<ActivityLogResponseDTO>> getAllLogs() {

        PageResponseDTO<ActivityLogResponseDTO> response = activityLogService.getAllLogs();

        return ApiResponse.success(HttpStatus.OK,
                "Activity Logs fetched successfully", response);
    }
    @GetMapping("/{entityType}/{entityId}")
    public ApiResponse<List<ActivityLogResponseDTO>> getLogsByEntity(
            @PathVariable EntityType entityType,
            @PathVariable String entityId) {

        List<ActivityLogResponseDTO> response =
                activityLogService.getLogsByEntity(entityType, entityId);

        return ApiResponse.success(HttpStatus.OK, "Activity Logs fetched successfully", response);
    }

    @PostMapping("/search")
    public ApiResponse<PageResponseDTO<ActivityLogResponseDTO>> searchLogs(
            @RequestBody SearchRequestDTO requestDTO) {

        PageResponseDTO<ActivityLogResponseDTO> response = activityLogService.searchLogs(requestDTO);

        return ApiResponse.success(
                HttpStatus.OK,
                "Activity Logs fetched successfully",
                response);
    }
}