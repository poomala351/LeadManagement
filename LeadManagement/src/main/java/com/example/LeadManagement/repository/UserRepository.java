package com.example.LeadManagement.repository;

import com.example.LeadManagement.model.Users;
import com.example.LeadManagement.repository.custom.UserRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, String>, UserRepositoryCustom {

}