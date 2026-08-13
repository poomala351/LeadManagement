package com.example.LeadManagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDTO {
 // so i insert the validation it will be validate the values , cant be null

    // Pagination
    @NotNull(message = "Page is required")
    @Min(value = 1, message = "Page must be at least 1")
    private Integer page ;

    @NotNull(message = "Size is required")
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 500, message = "Size cannot exceed 500")
    private Integer size;

    // Sorting
    private String sortBy;
    private String sortOrder;

    // Filters
    private String search;

}
