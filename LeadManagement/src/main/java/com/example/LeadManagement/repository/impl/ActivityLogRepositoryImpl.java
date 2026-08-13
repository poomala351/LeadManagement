package com.example.LeadManagement.repository.impl;

import com.example.LeadManagement.model.EntityType;
import com.example.LeadManagement.repository.custom.ActivityLogRepositoryCustom;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.response.ActivityLogResponseDTO;
import com.example.LeadManagement.model.ActivityLogs;
import com.example.LeadManagement.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ActivityLogRepositoryImpl implements ActivityLogRepositoryCustom {

        private final MongoTemplate mongoTemplate;

    @Override
    public ActivityLogs saveLog(ActivityLogs activityLog) {

        System.out.println("Saving Activity...");

        ActivityLogs saved = mongoTemplate.save(activityLog);

        System.out.println(saved);
        return saved;
    }

        @Override
        public PageResponseDTO<ActivityLogResponseDTO> getAllLogs() {

            Query query = new Query();

            long totalElements = mongoTemplate.count(query, ActivityLogs.class);

            List<ActivityLogs> logs = mongoTemplate.find(query, ActivityLogs.class);

            List<ActivityLogResponseDTO> response =
                    logs.stream()
                            .map(MapperUtil::toActivityLogResponse)
                            .collect(Collectors.toList());

            return PageResponseDTO.<ActivityLogResponseDTO>builder()
                    .content(response)
                    .pageNo(1)
                    .pageSize(response.size())
                    .totalElements(totalElements)
                    .totalPages(1)
                    .isFirst(true)
                    .isLast(true)
                    .build();
        }
    @Override
    public List<ActivityLogs> getLogsByEntity(EntityType entityType,
                                              String entityId) {

        Query query = new Query();

        query.addCriteria(Criteria.where("entityType").is(entityType)
                        .and("entityId").is(entityId)
        );

        return mongoTemplate.find(query, ActivityLogs.class);
    }
        @Override
        public PageResponseDTO<ActivityLogResponseDTO> searchLogs(SearchRequestDTO requestDTO) {

            Query query = new Query();
            // Sorting
            Sort.Direction direction =
                    requestDTO.getSortOrder().equalsIgnoreCase("ASC")
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            query.with(Sort.by(direction, requestDTO.getSortBy()));

            long totalElements = mongoTemplate.count(query, ActivityLogs.class);

            query.skip((long) (requestDTO.getPage() - 1) * requestDTO.getSize());

            query.limit(requestDTO.getSize());

            List<ActivityLogs> logs = mongoTemplate.find(query, ActivityLogs.class);

            List<ActivityLogResponseDTO> response =
                    logs.stream()
                            .map(MapperUtil::toActivityLogResponse)
                            .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) totalElements / requestDTO.getSize());

            return PageResponseDTO.<ActivityLogResponseDTO>builder()
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





