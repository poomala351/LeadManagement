package com.example.LeadManagement.service.impl;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.ProjectRequestDTO;
import com.example.LeadManagement.dto.response.ProjectResponseDTO;
import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.model.*;
import com.example.LeadManagement.repository.ProjectRepository;
import com.example.LeadManagement.repository.UserRepository;
import com.example.LeadManagement.service.ActivityLogService;
import com.example.LeadManagement.service.ProjectService;
import com.example.LeadManagement.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import java.time.OffsetDateTime;
import com.example.LeadManagement.dto.request.ProjectFileUploadRequestDTO;
import com.example.LeadManagement.dto.response.FileResponseDTO;
import com.example.LeadManagement.model.ProjectFile;
import com.example.LeadManagement.util.FileValidationUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ActivityLogService activityLogService;
    private final BlobContainerClient blobContainerClient;
    private final FileValidationUtil fileValidationUtil;

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO requestDTO) {

        log.info("Creating project");

        Projects project = MapperUtil.toProject(requestDTO);

        Projects savedProject = projectRepository.createProject(project);

        activityLogService.saveLog(
                EntityType.PROJECT,
                savedProject.getId(),
                ActionType.CREATE,
                "Project " + savedProject.getProjectName() + " created successfully",
                "Admin",
                null,
                null
        );
        return MapperUtil.toProjectResponse(savedProject);
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {

        log.info("Fetching all projects");

        return projectRepository.getAllProjects()
                .stream()
                .map(MapperUtil::toProjectResponse)
                .toList();
    }

    @Override
    public ProjectResponseDTO getProjectById(String id) {

        log.info("Fetching project {}", id);

        Projects project = projectRepository.getProjectById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id));
        return MapperUtil.toProjectResponse(project);
    }

    @Override
    public ProjectResponseDTO updateProject(String id,
                                            ProjectRequestDTO requestDTO) {

        log.info("Updating project {}", id);

        // Check project exists
        Projects oldProject = projectRepository.getProjectById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + id));

        // Old assigned user
        String oldUserId = oldProject.getAssignedUserId();

        // New assigned user from request
        String newUserId = requestDTO.getAssignedUserId();

        // Validate new assigned user
        if (newUserId != null) {

            Users newUser = userRepository.getUserById(newUserId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with id : " + newUserId));

            if (!newUser.getIsActive()) {
                throw new IllegalStateException(
                        "Inactive user cannot be assigned");
            }
        }

        if (Objects.equals(oldUserId, newUserId)) {

            throw new IllegalStateException(
                    "Project is already assigned to this user");
        }
        // Update Project
        Projects updatedProject =
                projectRepository.updateProject(id, requestDTO);

        // Reassignment Activity
        if (!Objects.equals(oldUserId, newUserId)) {

            Users oldUser = oldUserId != null
                    ? userRepository.getUserById(oldUserId).orElse(null) : null;

            Users newUser =
                    newUserId != null ? userRepository.getUserById(newUserId).orElse(null) : null;

            activityLogService.saveLog(
                    EntityType.PROJECT,
                    updatedProject.getId(),
                    ActionType.REASSIGNED,
                    "Project " + updatedProject.getProjectName()
                            + " reassigned from "
                            + (oldUser != null ? oldUser.getName() : oldUserId)
                            + " to "
                            + (newUser != null ? newUser.getName() : newUserId),
                    "Admin",
                    oldUserId,
                    newUserId
            );
        }
        // Update Activity
        addProjectChange(
                "Project Name",
                oldProject.getProjectName(),
                updatedProject.getProjectName(),
                updatedProject.getId());

        addProjectChange(
                "Client Name",
                oldProject.getClientName(),
                updatedProject.getClientName(),
                updatedProject.getId());

        addProjectChange(
                "Client Email",
                oldProject.getClientEmail(),
                updatedProject.getClientEmail(),
                updatedProject.getId());

        addProjectChange(
                "Client Phone",
                oldProject.getClientPhone(),
                updatedProject.getClientPhone(),
                updatedProject.getId());

        addProjectChange(
                "Budget",
                oldProject.getBudget(),
                updatedProject.getBudget(),
                updatedProject.getId());

        addProjectChange(
                "Assigned User",
                oldUserId,
                newUserId,
                updatedProject.getId());

        addProjectChange(
                "Status",
                oldProject.getStatus(),
                updatedProject.getStatus(),
                updatedProject.getId());


        return MapperUtil.toProjectResponse(updatedProject);
    }

    @Override
    public void deleteProject(String id) {

        log.info("Deleting project {}", id);
        Projects project = projectRepository.getProjectById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + id));

        projectRepository.deleteProject(id);

        activityLogService.saveLog(
                EntityType.PROJECT,
                project.getId(),
                ActionType.DELETE,
                "Project " + project.getProjectName()
                        + " deleted successfully",
                "Admin",
                project.toString(),
                null
        );
    }

    @Override
    public ProjectResponseDTO assignProject(String projectId, String userId) {

        log.info("Assigning project {} to user {}", projectId, userId);

        // Check Project
        Projects project = projectRepository.getProjectById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + projectId));

        // Already assigned
        if (project.getAssignedUserId() != null &&
                !project.getAssignedUserId().isBlank()) {

            throw new IllegalStateException(
                    "Project is already assigned");
        }

        // Check User
        Users user = userRepository.getUserById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + userId));

        if (!user.getIsActive()) {
            throw new IllegalStateException(
                    "Inactive user cannot be assigned");
        }

        // Assign
        Projects updatedProject =
                projectRepository.assignProject(projectId, userId);

        activityLogService.saveLog(
                EntityType.PROJECT,
                updatedProject.getId(),
                ActionType.ASSIGN,
                "Project " + updatedProject.getProjectName()
                        + " assigned to user " + user.getName(),
                "Admin",
                null,
                userId
        );

        return MapperUtil.toProjectResponse(updatedProject);
    }

    @Override
    public ProjectResponseDTO updateProjectStatus(String projectId,
                                                  ProjectStatus status) {

        log.info("Updating project status {}", projectId);

        Projects project = projectRepository.getProjectById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + projectId));

        switch (project.getStatus()) {

            case PLANNED:

                if (status != ProjectStatus.IN_PROGRESS &&
                        status != ProjectStatus.CANCELLED) {

                    throw new IllegalStateException(
                            "PLANNED project can move only to IN_PROGRESS or CANCELLED");
                }
                break;

            case IN_PROGRESS:

                if (status != ProjectStatus.ON_HOLD &&
                        status != ProjectStatus.COMPLETED) {

                    throw new IllegalStateException(
                            "IN_PROGRESS project can move only to ON_HOLD or COMPLETED");
                }
                break;

            case ON_HOLD:

                if (status != ProjectStatus.IN_PROGRESS &&
                        status != ProjectStatus.CANCELLED) {

                    throw new IllegalStateException(
                            "ON_HOLD project can move only to IN_PROGRESS or CANCELLED");
                }
                break;

            case COMPLETED:

                throw new IllegalStateException(
                        "Completed project cannot be updated");

            case CANCELLED:

                throw new IllegalStateException(
                        "Cancelled project cannot be updated");
        }

        ProjectStatus oldStatus = project.getStatus();

        Projects updatedProject =
                projectRepository.updateProjectStatus(projectId, status);

        activityLogService.saveLog(
                EntityType.PROJECT,
                updatedProject.getId(),
                ActionType.STATUS_CHANGE,
                "Project status changed from "
                        + oldStatus
                        + " to "
                        + updatedProject.getStatus(),
                "Admin",
                oldStatus.name(),
                updatedProject.getStatus().name()
        );

        return MapperUtil.toProjectResponse(updatedProject);
    }


    @Override
    public PageResponseDTO<ProjectResponseDTO> searchProject(
            SearchRequestDTO requestDTO) {
        log.info("Searching projects");
        return projectRepository.searchProject(requestDTO);
    }
    @Override
    public FileResponseDTO uploadProjectFile(ProjectFileUploadRequestDTO requestDTO) {

        log.info("Uploading file for project {}", requestDTO.getProjectId());

        // 1. Find Project
        Projects project = projectRepository.getProjectById(requestDTO.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + requestDTO.getProjectId()));

        // 2. Get File
        MultipartFile file = requestDTO.getFile();

        // 3. Validate File
        fileValidationUtil.validate(file);

        // 4. Create Unique File Name
        String uniqueFileName =
                UUID.randomUUID() + "-" + file.getOriginalFilename();

        String blobPath =
                "projects/" +
                        project.getId() +
                        "/" +
                        uniqueFileName;

        // 5. Azure Blob Client
        BlobClient blobClient = blobContainerClient.getBlobClient(blobPath);

        String fileUrl;

        try {
            blobClient.upload(
                    file.getInputStream(),
                    file.getSize(),
                    true);

            BlobSasPermission permission = new BlobSasPermission()
                    .setReadPermission(true);

            OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

            BlobServiceSasSignatureValues values =
                    new BlobServiceSasSignatureValues(expiryTime, permission);

            String sasToken = blobClient.generateSas(values);

            fileUrl = blobClient.getBlobUrl() + "?" + sasToken;

            log.info("Generated SAS URL: {}", fileUrl);
            System.out.println("Generated SAS URL = " + fileUrl);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload file to Azure Blob Storage",
                    e);
        }

        // 8. Initialize files list if null
        if (project.getFiles() == null) {
            project.setFiles(new ArrayList<>());
        }

        // 9. Create ProjectFile Object
        ProjectFile projectFile = ProjectFile.builder()
                .blobPath(blobPath)
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        // 10. Add into Project
        project.getFiles().add(projectFile);

        // 11. Save into MongoDB
        projectRepository.saveProject(project);

        // 12. Response
        return FileResponseDTO.builder()
                .fileName(projectFile.getOriginalFileName())
                .fileType(projectFile.getFileType())
                .fileSize(projectFile.getFileSize())
                .fileUrl(fileUrl)
                .message("File uploaded successfully")
                .build();
    }
    @Override
    public List<ProjectFile> getProjectFiles(String projectId) {

        Projects project = projectRepository.getProjectById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId));
        if (project.getFiles() == null) {
            return new ArrayList<>();
        }

        for (ProjectFile file : project.getFiles()) {

            BlobClient blobClient =
                    blobContainerClient.getBlobClient(file.getBlobPath());

            BlobSasPermission permission =
                    new BlobSasPermission()
                            .setReadPermission(true);

            BlobServiceSasSignatureValues values =
                    new BlobServiceSasSignatureValues(
                            OffsetDateTime.now().plusHours(1),
                            permission);

            String sasUrl =
                    blobClient.getBlobUrl() + "?"
                            + blobClient.generateSas(values);

            file.setFileUrl(sasUrl);
        }

        return project.getFiles();
    }

    @Override
    public void deleteProjectFile(
            String projectId,
            String blobPath) {

        log.info("Deleting file {} from project {}", blobPath, projectId);

        Projects project = projectRepository
                .getProjectById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + projectId));

        if (project.getFiles() == null) {
            throw new ResourceNotFoundException(
                    "No files found for this project");
        }

        ProjectFile projectFile = project.getFiles()
                .stream()
                .filter(file ->
                        file.getBlobPath().equals(blobPath))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "File not found"));

        BlobClient blobClient =
                blobContainerClient.getBlobClient(blobPath);

        if (blobClient.exists()) {
            blobClient.delete();
        }

        project.getFiles().remove(projectFile);

        projectRepository.saveProject(project);
    }
    @Override
    public String generateSasUrl(
            String projectId,
            String blobPath) {

        Projects project = projectRepository
                .getProjectById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id : " + projectId));

        ProjectFile projectFile = project.getFiles()
                .stream()
                .filter(file ->
                        file.getBlobPath().equals(blobPath))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "File not found"));

        BlobClient blobClient =
                blobContainerClient.getBlobClient(projectFile.getBlobPath());

        BlobSasPermission permission =
                new BlobSasPermission()
                        .setReadPermission(true);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                        OffsetDateTime.now().plusHours(1),
                        permission);

        return blobClient.getBlobUrl() + "?"
                + blobClient.generateSas(values);

    }
    private void addProjectChange(
            String field,
            Object oldValue,
            Object newValue,
            String projectId) {

        if (!Objects.equals(oldValue, newValue)) {

            activityLogService.saveLog(
                    EntityType.PROJECT,
                    projectId,
                    ActionType.UPDATE,
                    field + " changed from "
                            + oldValue
                            + " to "
                            + newValue,
                    "Admin",
                    String.valueOf(oldValue),
                    String.valueOf(newValue)
            );
        }
    }

}