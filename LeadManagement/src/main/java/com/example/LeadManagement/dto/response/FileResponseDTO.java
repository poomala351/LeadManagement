package com.example.LeadManagement.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String fileUrl;

    private String message;

}
