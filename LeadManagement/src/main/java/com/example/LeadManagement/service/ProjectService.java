package com.example.LeadManagement.service;

import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.ProjectFileUploadRequestDTO;
import com.example.LeadManagement.dto.request.ProjectRequestDTO;
import com.example.LeadManagement.dto.response.FileResponseDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.model.ProjectFile;
import com.example.LeadManagement.model.ProjectStatus;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO createProject(@Valid ProjectRequestDTO requestDTO);

    List<ProjectResponseDTO> getAllProjects();

    ProjectResponseDTO getProjectById(String id);

    ProjectResponseDTO updateProject(String id, @Valid ProjectRequestDTO requestDTO);

    void deleteProject(String id);

    ProjectResponseDTO assignProject(String projectId, String userId);

    ProjectResponseDTO updateProjectStatus(String projectId, ProjectStatus status);

    PageResponseDTO<ProjectResponseDTO> searchProject(SearchRequestDTO requestDTO);

    FileResponseDTO uploadProjectFile(ProjectFileUploadRequestDTO requestDTO);

    List<ProjectFile> getProjectFiles(String projectId);

    void deleteProjectFile(String projectId, String fileName);

    String generateSasUrl(String projectId, String fileName);
}
