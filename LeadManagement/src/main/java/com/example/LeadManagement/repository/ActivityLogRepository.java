package com.example.LeadManagement.repository;

import com.example.LeadManagement.repository.custom.ActivityLogRepositoryCustom;


import com.example.LeadManagement.model.ActivityLogs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityLogRepository extends MongoRepository<ActivityLogs, String>, ActivityLogRepositoryCustom {
}

