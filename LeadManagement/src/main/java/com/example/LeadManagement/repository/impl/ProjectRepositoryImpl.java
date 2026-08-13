package com.example.LeadManagement.repository.impl;

import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.model.ProjectStatus;
import com.example.LeadManagement.repository.custom.ProjectRepositoryCustom;


import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.ProjectRequestDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.model.Projects;
import com.example.LeadManagement.util.MapperUtil;
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
    public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

        private final MongoTemplate mongoTemplate;

        @Override
        public Projects createProject(Projects project) {
            return mongoTemplate.save(project);
        }

        @Override
        public List<Projects> getAllProjects() {
            return mongoTemplate.findAll(Projects.class);
        }

        @Override
        public Optional<Projects> getProjectById(String id) {
            return Optional.ofNullable(
                    mongoTemplate.findById(id, Projects.class));
        }

        @Override
        public Projects updateProject(String id, ProjectRequestDTO requestDTO) {

            Projects project = mongoTemplate.findById(id, Projects.class);

            if (project == null) {
                throw new ResourceNotFoundException("Project not found with id : " + id);
            }
            project.setProjectName(requestDTO.getProjectName());
            project.setClientName(requestDTO.getClientName());
            project.setClientEmail(requestDTO.getClientEmail());
            project.setClientPhone(requestDTO.getClientPhone());
            project.setBudget(requestDTO.getBudget());
            project.setAssignedUserId(requestDTO.getAssignedUserId());
            project.setStatus(requestDTO.getStatus());
            project.setUpdatedAt(LocalDateTime.now());
            return mongoTemplate.save(project);
        }

        @Override
        public void deleteProject(String id) {

            Projects project = mongoTemplate.findById(id, Projects.class);

            if (project == null) {
                throw new ResourceNotFoundException("Project not found with id : " + id);
            }
            mongoTemplate.remove(project);
        }
        @Override
        public Projects assignProject(String projectId, String userId) {

            Query query =
                    new Query(Criteria.where("id").is(projectId));

            Update update = new Update()
                    .set("assignedUserId", userId)
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Projects.class);

            return mongoTemplate.findOne(query, Projects.class);
        }

        @Override
        public Projects updateProjectStatus(String projectId,
                                            ProjectStatus status) {

            Query query = new Query(
                    Criteria.where("id").is(projectId));

            Update update = new Update()
                    .set("status", status)
                    .set("updatedAt", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, Projects.class);

            return mongoTemplate.findOne(query, Projects.class);
        }

        @Override
        public PageResponseDTO<ProjectResponseDTO> searchProject(SearchRequestDTO requestDTO) {

            Query query = new Query();

            if (requestDTO.getSearch() != null &&
                    !requestDTO.getSearch().isBlank()) {

                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("projectName").regex(requestDTO.getSearch(), "i"),
                        Criteria.where("clientName").regex(requestDTO.getSearch(), "i"),
                        Criteria.where("clientEmail").regex(requestDTO.getSearch(), "i"),
                        Criteria.where("clientPhone").regex(requestDTO.getSearch(), "i")
                ));
            }

            long totalElements = mongoTemplate.count(query, Projects.class);

            Sort.Direction direction =
                    "DESC".equalsIgnoreCase(requestDTO.getSortOrder())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            query.with(Sort.by(direction, requestDTO.getSortBy()));

            query.skip((long) (requestDTO.getPage() - 1) * requestDTO.getSize());

            query.limit(requestDTO.getSize());

            List<ProjectResponseDTO> response =
                    mongoTemplate.find(query, Projects.class)
                            .stream()
                            .map(MapperUtil::toProjectResponse)
                            .toList();

            int totalPages =
                    (int) Math.ceil((double) totalElements / requestDTO.getSize());

            return PageResponseDTO.<ProjectResponseDTO>builder()
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
        public Projects saveProject(Projects project) {
            project.setUpdatedAt(LocalDateTime.now());
            return mongoTemplate.save(project);
        }
    }

