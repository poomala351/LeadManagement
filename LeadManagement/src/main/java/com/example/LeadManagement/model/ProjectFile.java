package com.example.LeadManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFile {

    private String blobPath;

    private String originalFileName;

    private String fileType;

    private Long fileSize;

    private String fileUrl;

    private LocalDateTime uploadedAt;

}