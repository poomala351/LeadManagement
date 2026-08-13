package com.example.LeadManagement.repository.custom;

import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.model.LeadStatus;
import com.example.LeadManagement.model.Leads;


import java.util.List;
import java.util.Optional;

public interface LeadRepositoryCustom {

        Leads createLead(Leads lead);

        List<Leads> getAllLeads();

        Optional<Leads> getLeadById(String id);

        Leads updateLead(String id, LeadRequestDTO requestDTO);

        void deleteLead(String id);

        Leads assignLead(String leadId, String userId);

        Leads updateLeadStatus(String leadId, LeadStatus status);

        List<Leads> searchLead(SearchRequestDTO requestDTO);

        long countLead(SearchRequestDTO requestDTO);
    }
