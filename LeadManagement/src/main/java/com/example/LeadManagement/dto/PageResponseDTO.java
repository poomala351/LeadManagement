package com.example.LeadManagement.dto;

import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDTO<T>{

    private List<T> content;

    private int pageNo;

    private int pageSize;

    private Long totalElements;

    private Integer totalPages;

    @JsonProperty("isFirst")
    private boolean isFirst;

    @JsonProperty("isLast")
    private boolean isLast;
}