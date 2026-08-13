package com.example.LeadManagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
//non null error is cant coz given the crt json

    private LocalDateTime timestamp;

    private HttpStatus status;

    private int statusCode;

    private String message;

    private T data;

    private Object errors;


    public static <T> ApiResponse<T> success(HttpStatus status,
                                             String message,
                                             T data) {

        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }
}