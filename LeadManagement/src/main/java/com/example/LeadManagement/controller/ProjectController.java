    package com.example.LeadManagement.controller;

    import com.example.LeadManagement.dto.ApiResponse;
    import com.example.LeadManagement.dto.PageResponseDTO;
    import com.example.LeadManagement.dto.SearchRequestDTO;
    import com.example.LeadManagement.dto.request.ProjectFileUploadRequestDTO;
    import com.example.LeadManagement.dto.request.ProjectRequestDTO;
    import com.example.LeadManagement.dto.response.FileResponseDTO;
    import com.example.LeadManagement.dto.response.ProjectResponseDTO;
    import com.example.LeadManagement.model.ProjectFile;
    import com.example.LeadManagement.model.ProjectStatus;
    import com.example.LeadManagement.service.ProjectService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.*;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/projects")
    @RequiredArgsConstructor
    @Slf4j
    public class ProjectController {

        private final ProjectService projectService;

        @PostMapping
        public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(
                @Valid @RequestBody ProjectRequestDTO requestDTO){

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(
                            "Project created successfully",
                            projectService.createProject(requestDTO)));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<?>> getAllProjects(){

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Projects fetched successfully",
                            projectService.getAllProjects()));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(
                @PathVariable String id){

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Project fetched successfully",
                            projectService.getProjectById(id)));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
                @PathVariable String id,
                @Valid @RequestBody ProjectRequestDTO requestDTO){

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Project updated successfully",
                            projectService.updateProject(id,requestDTO)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> deleteProject(
                @PathVariable String id){

            projectService.deleteProject(id);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Project deleted successfully",
                            null));
        }

        @PostMapping("/search")
        public ResponseEntity<ApiResponse<PageResponseDTO<ProjectResponseDTO>>> searchProject(
                @RequestBody SearchRequestDTO requestDTO){

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Projects fetched successfully",
                            projectService.searchProject(requestDTO)));
        }
        @PatchMapping("/{projectId}/assign/{userId}")
        public ResponseEntity<ApiResponse<ProjectResponseDTO>> assignProject(
                @PathVariable String projectId,
                @PathVariable String userId) {

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Project assigned successfully",
                            projectService.assignProject(projectId, userId)));
        }
        @PatchMapping("/{projectId}/status")
        public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProjectStatus(
                @PathVariable String projectId,
                @RequestParam ProjectStatus status) {

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Project status updated successfully",
                            projectService.updateProjectStatus(projectId, status)));
        }
        @PostMapping("/upload")
        public ResponseEntity<ApiResponse<FileResponseDTO>> uploadProjectFile(
                @Valid @ModelAttribute ProjectFileUploadRequestDTO requestDTO) {

            FileResponseDTO response =
                    projectService.uploadProjectFile(requestDTO);

            ApiResponse<FileResponseDTO> apiResponse =
                    ApiResponse.success(
                            HttpStatus.OK,
                            "File uploaded successfully",
                            response);

            return ResponseEntity.ok(apiResponse);
        }

        @GetMapping("/{projectId}/files")
        public ResponseEntity<ApiResponse<List<ProjectFile>>> getProjectFiles(
                @PathVariable String projectId) {

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Files fetched successfully",
                            projectService.getProjectFiles(projectId)));
        }
        @DeleteMapping("/{projectId}/files/{fileName}")
        public ResponseEntity<ApiResponse<Object>> deleteProjectFile(
                @PathVariable String projectId,
                @PathVariable String fileName) {

            projectService.deleteProjectFile(projectId, fileName);
            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "File deleted successfully",
                            null));
        }
        @GetMapping("/{projectId}/download")
        public ResponseEntity<ApiResponse<String>> downloadFile(
                @PathVariable String projectId,
                @RequestParam String blobPath) {

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "File URL fetched successfully",
                            projectService.generateSasUrl(projectId, blobPath)));
        }

    }