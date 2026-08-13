package com.example.LeadManagement.repository;

import com.example.LeadManagement.model.Leads;
import com.example.LeadManagement.repository.custom.LeadRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeadRepository extends MongoRepository<Leads,String>, LeadRepositoryCustom {
    }

