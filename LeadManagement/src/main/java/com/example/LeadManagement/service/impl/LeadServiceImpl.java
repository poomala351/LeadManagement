package com.example.LeadManagement.service.impl;

import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.dto.response.LeadResponseDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.exception.IllegalAssignException;
import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.model.*;
import com.example.LeadManagement.repository.LeadRepository;
import com.example.LeadManagement.repository.ProjectRepository;
import com.example.LeadManagement.repository.UserRepository;
import com.example.LeadManagement.service.ActivityLogService;
import com.example.LeadManagement.service.LeadService;
import com.example.LeadManagement.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ActivityLogService activityLogService;

    @Override
    public LeadResponseDTO createLead(LeadRequestDTO requestDTO) {

        log.info("Creating lead");

        Leads lead = MapperUtil.toLead(requestDTO);

        Leads savedLead = leadRepository.createLead(lead);

        activityLogService.saveLog(
                EntityType.LEAD,
                savedLead.getId(),
                ActionType.CREATE,
                "Lead " + savedLead.getLeadName() + " created successfully",
                "Admin",
                null,
                null
        );

        return MapperUtil.toLeadResponse(savedLead);
    }


    @Override
    public List<LeadResponseDTO> getAllLeads() {

        log.info("Fetching all leads");

        return leadRepository.getAllLeads()
                .stream()
                .map(MapperUtil::toLeadResponse)
                .toList();
    }

    @Override
    public LeadResponseDTO getLeadById(String id) {

        log.info("Fetching lead {}", id);

        Leads lead = leadRepository.getLeadById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lead not found with id : " + id));

        return MapperUtil.toLeadResponse(lead);
    }

    @Override
    public LeadResponseDTO updateLead(String id, LeadRequestDTO requestDTO) {

        log.info("Updating lead {}", id);

        Leads lead = leadRepository.getLeadById(id).orElseThrow(() ->
                new ResourceNotFoundException("Lead not found with id : " + id));

        Leads updatedLead = leadRepository.updateLead(id, requestDTO);

        addChange(
                "Lead Name",
                lead.getLeadName(),
                updatedLead.getLeadName(),
                updatedLead.getId());

        addChange(
                "Email",
                lead.getEmail(),
                updatedLead.getEmail(),
                updatedLead.getId());

        addChange(
                "Phone",
                lead.getPhone(),
                updatedLead.getPhone(),
                updatedLead.getId());

        addChange(
                "Source",
                lead.getSource(),
                updatedLead.getSource(),
                updatedLead.getId());

        addChange(
                "Description",
                lead.getDescription(),
                updatedLead.getDescription(),
                updatedLead.getId());

        addChange(
                "Expected Budget",
                lead.getExpectedBudget(),
                updatedLead.getExpectedBudget(),
                updatedLead.getId());

        return MapperUtil.toLeadResponse(updatedLead);
    }

    @Override
    public void deleteLead(String id) {

        log.info("Deleting lead {}", id);
        Leads lead = leadRepository.getLeadById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lead not found with id : " + id));

        leadRepository.deleteLead(id);

        activityLogService.saveLog(
                EntityType.LEAD,
                lead.getId(),
                ActionType.DELETE,
                "Lead " + lead.getLeadName() + " deleted successfully",
                "Admin",
                lead.toString(),
                null

        );

        log.info("Lead deleted successfully");
    }

    @Override
    public LeadResponseDTO assignLead(String leadId, String userId) {

        log.info("Assigning lead {} to user {}", leadId, userId);

        // Check Lead exists
        Leads lead = leadRepository.getLeadById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lead not found with id : " + leadId));

        // Business Rule
        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new IllegalStateException(
                    "Converted lead cannot be assigned");
        }
        if (lead.getStatus() == LeadStatus.ASSIGNED
                || (lead.getAssignedUserId() != null
                && !lead.getAssignedUserId().isBlank())) {

            throw new IllegalAssignException(
                    "Lead assignment is not allowed because the lead is already assigned");
        }
        // Check User exists

        Users user = userRepository.getUserById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id " + userId));
        if (!user.getIsActive()) {
            throw new IllegalStateException(
                    "Inactive user cannot be assigned");
        }
        // Assign Lead
        Leads updatedLead = leadRepository.assignLead(leadId, userId);

        // Activity Log
        activityLogService.saveLog(
                EntityType.LEAD,
                updatedLead.getId(),
                ActionType.ASSIGN,
                "Lead " + updatedLead.getLeadName()
                        + " assigned to user " + user.getName(),
                "Admin",
                null,
                user.getName());

        return MapperUtil.toLeadResponse(updatedLead);
    }

    @Override
    public LeadResponseDTO updateLeadStatus(String leadId,
                                            LeadStatus status) {

        log.info("Updating lead status : {}", leadId);

        Leads lead = leadRepository.getLeadById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lead not found with id : " + leadId));

        switch (lead.getStatus()) {

            case NEW:
                if (status != LeadStatus.ASSIGNED) {
                    throw new IllegalStateException(
                            "NEW lead can only move to ASSIGNED");
                }
                break;

            case ASSIGNED:
                if (status != LeadStatus.IN_PROGRESS) {
                    throw new IllegalStateException(
                            "ASSIGNED lead can only move to IN_PROGRESS");
                }
                break;

            case IN_PROGRESS:
                if (status != LeadStatus.FOLLOW_UP
                        && status != LeadStatus.QUALIFIED
                        && status != LeadStatus.REJECTED) {

                    throw new IllegalStateException(
                            "Invalid status transition");
                }
                break;

            case FOLLOW_UP:
                if (status != LeadStatus.QUALIFIED
                        && status != LeadStatus.REJECTED) {

                    throw new IllegalStateException(
                            "FOLLOW_UP lead can only move to QUALIFIED or REJECTED");
                }
                break;

            case QUALIFIED:
                throw new IllegalStateException(
                        "Qualified lead move to converted.");

            case REJECTED:
                throw new IllegalStateException(
                        "Rejected lead cannot be assigned no more status");

            case CONVERTED:
                throw new IllegalStateException(
                        "Converted lead cannot be updated");
        }
        LeadStatus oldStatus = lead.getStatus();

        Leads updatedLead =
                leadRepository.updateLeadStatus(leadId, status);

        activityLogService.saveLog(
                EntityType.LEAD,
                updatedLead.getId(),
                ActionType.STATUS_CHANGE,
                "Lead status changed from "
                        + oldStatus
                        + " to "
                        + updatedLead.getStatus(),
                "Admin",
                oldStatus.name(),
                updatedLead.getStatus().name());

        return MapperUtil.toLeadResponse(updatedLead);
    }

    @Override
    public PageResponseDTO<LeadResponseDTO> searchLead(
            SearchRequestDTO requestDTO) {

        log.info("Searching leads");

        List<LeadResponseDTO> response =
                leadRepository.searchLead(requestDTO)
                        .stream()
                        .map(MapperUtil::toLeadResponse)
                        .toList();

        long totalElements =
                leadRepository.countLead(requestDTO);

        int totalPages = (int) Math.ceil(
                (double) totalElements /
                        requestDTO.getSize());

        return PageResponseDTO.<LeadResponseDTO>builder()
                .content(response)
                .pageNo(requestDTO.getPage())
                .pageSize(requestDTO.getSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(requestDTO.getPage() == 1)
                .isLast(requestDTO.getPage() >= totalPages)
                .build();
    }

    @Override
    public ProjectResponseDTO convertLeadToProject(String leadId) {

        log.info("Converting lead {} into project", leadId);

        Leads lead = leadRepository.getLeadById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lead not found with id : " + leadId));

        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new IllegalStateException("Lead already converted");
        }

        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new IllegalStateException(
                    "Only QUALIFIED leads can be converted");
        }
        Projects project = Projects.builder()
                .leadId(lead.getId())
                .projectName(lead.getLeadName())
                .clientName(lead.getLeadName())
                .clientEmail(lead.getEmail())
                .clientPhone(lead.getPhone())
                .budget(lead.getExpectedBudget())
                .assignedUserId(lead.getAssignedUserId())
                .status(ProjectStatus.PLANNED)
                .build();

        Projects savedProject = projectRepository.createProject(project);
        activityLogService.saveLog(
                EntityType.PROJECT,
                savedProject.getId(),
                ActionType.CREATE,
                "Project "
                        + savedProject.getProjectName()
                        + " created successfully from Lead conversion",
                "Admin",
                null,
                null
        );

        leadRepository.updateLeadStatus(leadId, LeadStatus.CONVERTED);

        activityLogService.saveLog(
                EntityType.LEAD,
                lead.getId(),
                ActionType.CONVERT,
                "Lead " + lead.getLeadName() + " converted to Project",
                "Admin",
                LeadStatus.QUALIFIED.name(),
                LeadStatus.CONVERTED.name());

        log.info("Lead converted successfully");

        return MapperUtil.toProjectResponse(savedProject);
    }

    private void addChange(String field,
                           Object oldValue,
                           Object newValue,
                           String leadId) {

        if (!java.util.Objects.equals(oldValue, newValue)) {

            activityLogService.saveLog(
                    EntityType.LEAD,
                    leadId,
                    ActionType.UPDATE,
                    field + " changed from " + oldValue + " to " + newValue,
                    "Admin",
                    String.valueOf(oldValue),
                    String.valueOf(newValue)
            );
        }
    }
}

