package com.example.LeadManagement.repository.impl;


import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.LeadRequestDTO;
import com.example.LeadManagement.model.LeadStatus;
import com.example.LeadManagement.model.Leads;
import com.example.LeadManagement.repository.custom.LeadRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LeadRepositoryImpl implements LeadRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Leads createLead(Leads lead) {
        return mongoTemplate.save(lead);
    }

    @Override
    public List<Leads> getAllLeads() {
        return mongoTemplate.findAll(Leads.class);
    }
    @Override
    public Optional<Leads> getLeadById(String id) {
        return Optional.ofNullable(
                mongoTemplate.findById(id, Leads.class));
    }
    @Override
    public Leads updateLead(String id, LeadRequestDTO requestDTO) {
        Leads lead = mongoTemplate.findById(id, Leads.class);
        if (lead == null) {
            return null;
        }
        lead.setLeadName(requestDTO.getLeadName());
        lead.setEmail(requestDTO.getEmail());
        lead.setPhone(requestDTO.getPhone());
        lead.setSource(requestDTO.getSource());
        lead.setDescription(requestDTO.getDescription());
        lead.setExpectedBudget(requestDTO.getExpectedBudget());
        lead.setUpdatedAt(LocalDateTime.now());
        return mongoTemplate.save(lead);
    }

    @Override
    public void deleteLead(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Leads.class);
    }
    @Override
    public Leads assignLead(String leadId, String userId) {

        Query query = new Query(Criteria.where("id").is(leadId));

        Update update = new Update()
                .set("assignedUserId", userId)
                .set("status", LeadStatus.ASSIGNED)
                .set("updatedAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, Leads.class);

        return mongoTemplate.findOne(query, Leads.class);
    }
    @Override
    public Leads updateLeadStatus(String leadId,
                                  LeadStatus status) {

        Query query =
                new Query(Criteria.where("id").is(leadId));

        Update update = new Update()
                .set("status", status)
                .set("updatedAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, Leads.class);

        return mongoTemplate.findOne(query, Leads.class);
    }
    @Override
    public List<Leads> searchLead(SearchRequestDTO requestDTO) {
        Query query = new Query();
        if (requestDTO.getSearch() != null &&
                !requestDTO.getSearch().isBlank()) {

            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("leadName")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("email")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("phone")
                            .regex(requestDTO.getSearch(), "i"));

            query.addCriteria(criteria);
        }
        Sort.Direction direction =
                requestDTO.getSortOrder().equalsIgnoreCase("ASC")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;
        query.with(Sort.by(direction, requestDTO.getSortBy()));

        query.skip((long) (requestDTO.getPage() - 1) * requestDTO.getSize());

        query.limit(requestDTO.getSize());

        return mongoTemplate.find(query, Leads.class);
    }
    @Override
    public long countLead(SearchRequestDTO requestDTO) {
        Query query = new Query();
        if (requestDTO.getSearch() != null &&
                !requestDTO.getSearch().isBlank()) {

            Criteria criteria = new Criteria().orOperator(

                    Criteria.where("leadName")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("email")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("phone")
                            .regex(requestDTO.getSearch(), "i"));
            query.addCriteria(criteria);
        }
        return mongoTemplate.count(query, Leads.class);
    }
}
