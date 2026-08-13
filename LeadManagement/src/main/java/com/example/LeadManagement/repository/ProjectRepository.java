package com.example.LeadManagement.repository;


import com.example.LeadManagement.model.Projects;
import com.example.LeadManagement.repository.custom.ProjectRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

    public interface ProjectRepository extends MongoRepository<Projects,String>, ProjectRepositoryCustom {

    }

