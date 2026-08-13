package com.example.LeadManagement.repository.custom;

import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.ProjectRequestDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.model.ProjectStatus;
import com.example.LeadManagement.model.Projects;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryCustom {

    Projects createProject(Projects project);

    List<Projects> getAllProjects();

    Optional<Projects> getProjectById(String id);

    Projects updateProject(String id, ProjectRequestDTO requestDTO);

    void deleteProject(String id);

    Projects assignProject(String projectId, String userId);

    Projects updateProjectStatus(String projectId, ProjectStatus status);

    PageResponseDTO<ProjectResponseDTO> searchProject(SearchRequestDTO requestDTO);

    Projects saveProject(Projects project);

}
