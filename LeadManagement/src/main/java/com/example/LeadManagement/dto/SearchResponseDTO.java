package com.example.LeadManagement.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponseDTO<T> {

    private List<T> data;

    private int page;
// page size
    private int Size;

    private int totalPages;

    private long totalElements;

    private boolean isFirst;

    private boolean isLast;
}