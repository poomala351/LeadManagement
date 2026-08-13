package com.example.LeadManagement.repository.impl;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.repository.custom.UserRepositoryCustom;

import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.UserRequestDTO;
import com.example.LeadManagement.model.Users;
import com.example.LeadManagement.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    //mongotemplate used to communicate the mongodb
    private final MongoTemplate mongoTemplate;


    @Override
    public Users createUser(Users users) {
        return mongoTemplate.save(users);
    }

    @Override
    public List<Users> getAllUsers() {
        return mongoTemplate.findAll(Users.class);
    }

    @Override
    public Optional<Users> getUserById(String id) {

        Users user = mongoTemplate.findById(id, Users.class);

        return Optional.ofNullable(user);
    }

    @Override
    public Users updateUser(String id, UserRequestDTO requestDTO) {

        Users user = mongoTemplate.findById(id, Users.class);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found with id : " + id);
        }
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setIsActive(requestDTO.isActive());
        user.setUpdatedAt(LocalDateTime.now());

        return mongoTemplate.save(user);
    }

    @Override
    public void deleteUser(String id) {

        Users user = mongoTemplate.findById(id, Users.class);
        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found with id : " + id);
        }

        mongoTemplate.remove(user);
    }

    @Override
    public Optional<Users> findByEmail(String email) {

        Query query = new Query();

        query.addCriteria(Criteria.where("email").is(email));

        Users user = mongoTemplate.findOne(query, Users.class);

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Users> findByPhone(String phone) {

        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(phone));
        Users user = mongoTemplate.findOne(query, Users.class);
        return Optional.ofNullable(user);
    }

    @Override
    public PageResponseDTO<UserResponseDTO> searchUser(SearchRequestDTO requestDTO) {
        Query query = new Query();

        if (requestDTO.getSearch() != null &&
                !requestDTO.getSearch().isBlank()) {

            Criteria criteria = new Criteria().orOperator(

                    Criteria.where("name")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("email")
                            .regex(requestDTO.getSearch(), "i"),

                    Criteria.where("phone")
                            .regex(requestDTO.getSearch(), "i")
            );

            query.addCriteria(criteria);
        }
        // Count before pagination
        long totalElements = mongoTemplate.count(query, Users.class);

        Sort.Direction direction =
                "ASC".equalsIgnoreCase(requestDTO.getSortOrder())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        query.with(Sort.by(direction, requestDTO.getSortBy()));

        query.skip((long) (requestDTO.getPage() - 1) * requestDTO.getSize());

        query.limit(requestDTO.getSize());

        List<Users> users = mongoTemplate.find(query, Users.class);

        int totalPages = (int) Math.ceil((double) totalElements / requestDTO.getSize());

        List<UserResponseDTO> response = users.stream()
                .map(MapperUtil::toUserResponse)
                .toList();

        return PageResponseDTO.<UserResponseDTO>builder()
                .content(response)
                .pageNo(requestDTO.getPage())
                .pageSize(requestDTO.getSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(requestDTO.getPage() == 1)
                .isLast(requestDTO.getPage() >= totalPages)
                .build();
    }

}