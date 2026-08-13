package com.example.LeadManagement.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectFileUploadRequestDTO {

    @NotBlank(message="project Id is required.")
    private String projectId;

    private MultipartFile file;

}