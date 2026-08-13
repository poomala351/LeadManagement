package com.example.LeadManagement.service;

import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.dto.response.LeadResponseDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.model.LeadStatus;

import java.util.List;

public interface LeadService {

    LeadResponseDTO createLead(LeadRequestDTO requestDTO);

    List<LeadResponseDTO> getAllLeads();

    LeadResponseDTO getLeadById(String id);

    LeadResponseDTO updateLead(String id, LeadRequestDTO requestDTO);

    void deleteLead(String id);

    LeadResponseDTO assignLead(String leadId, String userId);

    LeadResponseDTO updateLeadStatus(String leadId, LeadStatus status);

    PageResponseDTO<LeadResponseDTO> searchLead(SearchRequestDTO requestDTO);

    ProjectResponseDTO convertLeadToProject(String leadId);
}