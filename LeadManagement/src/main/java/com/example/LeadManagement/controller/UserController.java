package com.example.LeadManagement.controller;

import com.example.LeadManagement.dto.ApiResponse;
import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.UserRequestDTO;
import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.example.LeadManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create User
    @PostMapping
    public ApiResponse<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {

        UserResponseDTO response = userService.createUser(requestDTO);

        return ApiResponse.created(
                "User Created Successfully",
                response
        );
    }

    // Get All Users
    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = userService.getAllUsers();

        return ApiResponse.success(
                HttpStatus.OK,
                "Users fetched successfully",
                users
        );
    }

    // Get User By Id
    @GetMapping("/{id}")
    public ApiResponse<UserResponseDTO> getUserById(
            @PathVariable String id) {

        UserResponseDTO user = userService.getUserById(id);

        return ApiResponse.success(
                HttpStatus.OK,
                "User fetched successfully",
                user
        );
    }

    // Update User
    @PutMapping("/{id}")
    public ApiResponse<UserResponseDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequestDTO requestDTO) {

        UserResponseDTO response =
                userService.updateUser(id, requestDTO);

        return ApiResponse.success(
                HttpStatus.OK,
                "User updated successfully",
                response
        );
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteUser(
            @PathVariable String id) {

        userService.deleteUser(id);

        return ApiResponse.success(
                HttpStatus.OK,
                "User deleted successfully",
                null
        );
    }

    // Search Users
    @PostMapping("/search")
    public ApiResponse<PageResponseDTO<UserResponseDTO>> searchUser(@Valid
            @RequestBody SearchRequestDTO requestDTO) {

        PageResponseDTO<UserResponseDTO> response =
                userService.searchUser(requestDTO);

        return ApiResponse.success(
                HttpStatus.OK,
                "Users fetched successfully",
                response
        );
    }
}