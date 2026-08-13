package com.example.LeadManagement.controller;

import com.example.LeadManagement.dto.ApiResponse;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.dto.response.LeadResponseDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.model.LeadStatus;
import com.example.LeadManagement.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Slf4j
public class LeadController {

    private final LeadService leadService;

    // Create Lead
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponseDTO>> createLead(
            @Valid @RequestBody LeadRequestDTO requestDTO) {
        log.info("Creating lead with customer email {}",
                requestDTO.getEmail());
        LeadResponseDTO response = leadService.createLead(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        "Lead created successfully",
                        response));
    }

    // Get All Leads
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllLeads() {

        log.info("Fetching all leads");

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Leads fetched successfully",
                        leadService.getAllLeads()));
    }

    // Get Lead By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDTO>> getLeadById(
            @PathVariable String id) {

        log.info("Fetching lead with id : {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead fetched successfully",
                        leadService.getLeadById(id)));
    }

    // Update Lead
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDTO>> updateLead(
            @PathVariable String id,
            @Valid @RequestBody LeadRequestDTO requestDTO) {

        log.info("Updating lead : {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead updated successfully",
                        leadService.updateLead(id, requestDTO)));
    }

    // Delete Lead
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteLead(
            @PathVariable String id) {
        log.info("Deleting lead with id {}", id);
        leadService.deleteLead(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead deleted successfully",
                        null));
    }

    // Assign Lead
    @PatchMapping("/{leadId}/assign/{userId}")
    public ResponseEntity<ApiResponse<LeadResponseDTO>> assignLead(
            @PathVariable String leadId,
            @PathVariable String userId) {
        log.info("Assigning lead {} to user {}", leadId, userId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead assigned successfully",
                        leadService.assignLead(leadId, userId)));
    }

    // Update Lead Status
    @PatchMapping("/{leadId}/status")
    public ResponseEntity<ApiResponse<LeadResponseDTO>> updateLeadStatus(
            @PathVariable String leadId,
            @RequestParam LeadStatus status) {

        log.info("Updating status of lead {}", leadId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead status updated successfully",
                        leadService.updateLeadStatus(leadId, status)));
    }

    // Search Leads
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<LeadResponseDTO>>> searchLead(
            @Valid @RequestBody SearchRequestDTO requestDTO) {
        log.info("Searching leads with page {}, size {}",
                requestDTO.getPage(),
                requestDTO.getSize());
        PageResponseDTO<LeadResponseDTO> response = leadService.searchLead(requestDTO);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,
                        "Lead search completed successfully",
                        response));
    }

    // Convert Lead To Project
    @PostMapping("/{leadId}/convert_to_project")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> convertLeadToProject(
            @PathVariable String leadId) {

        log.info("Converting lead {} to project", leadId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Lead converted to project successfully",
                        leadService.convertLeadToProject(leadId)));
    }
}
