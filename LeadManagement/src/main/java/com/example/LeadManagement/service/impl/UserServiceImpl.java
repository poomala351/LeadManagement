package com.example.LeadManagement.service.impl;


import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.UserRequestDTO;
import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.example.LeadManagement.exception.AlreadyExistsException;
import com.example.LeadManagement.exception.ResourceNotFoundException;
import com.example.LeadManagement.model.ActionType;
import com.example.LeadManagement.model.EntityType;
import com.example.LeadManagement.model.Users;
import com.example.LeadManagement.repository.UserRepository;
import com.example.LeadManagement.service.ActivityLogService;
import com.example.LeadManagement.service.UserService;
import com.example.LeadManagement.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    // create user
    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        log.info("Creating user with email: {}", requestDTO.getEmail());

        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", requestDTO.getEmail());
            throw new AlreadyExistsException("Email already exists");
        }
        if (userRepository.findByPhone(requestDTO.getPhone()).isPresent()) {
            log.warn("Phone number already exists: {}", requestDTO.getPhone());
            throw new AlreadyExistsException("Phone number already exists");
        }
        Users user = MapperUtil.toUser(requestDTO);

        Users savedUser = userRepository.createUser(user);

// Save activity log of user
        activityLogService.saveLog(
                EntityType.USER,
                savedUser.getId(),
                ActionType.CREATE,
                "User " + savedUser.getName() + " created successfully",
                "Admin",
                null,null
        );

        log.info("User created successfully with id: {}", savedUser.getId());

        return MapperUtil.toUserResponse(savedUser);
    }
    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");
        List<Users> users = userRepository.getAllUsers();
        log.info("Total users fetched: {}", users.size());
        return users.stream()
                .map(MapperUtil::toUserResponse)
                .collect(Collectors.toList());
    }
    @Override
    public UserResponseDTO getUserById(String id) {
        log.info("Fetching user with id: {}", id);
        Users user = userRepository.getUserById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id : " + id);});
        log.info("User fetched successfully");
        return MapperUtil.toUserResponse(user);
    }
    @Override
    public UserResponseDTO updateUser(String id, UserRequestDTO requestDTO) {

        log.info("Updating user with id: {}", id);

        Users oldUser = userRepository.getUserById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found this id."));

        Users updatedUser = userRepository.updateUser(id, requestDTO);

        activityLogService.saveLog(
                EntityType.USER,
                updatedUser.getId(),
                ActionType.UPDATE,
                "User " + updatedUser.getName() + " updated successfully",
                "Admin",
                oldUser.toString(),
                updatedUser.toString()
        );

        log.info("User updated successfully with id: {}", id);

        return MapperUtil.toUserResponse(updatedUser);
    }
    @Override
    public void deleteUser(String id) {

        log.info("Deleting user with id: {}", id);

        Users user = userRepository.getUserById(id)
                .orElseThrow(() -> {log.error("User not found with id: {}", id);

                    return new ResourceNotFoundException("User not found with id : " + id);
                });

        // Delete user
        userRepository.deleteUser(id);

        // Save activity log
        activityLogService.saveLog(
                EntityType.USER,
                user.getId(),
                ActionType.DELETE,
                "User " + user.getName() + " deleted successfully",
                "Admin",
                user.toString(),
                null
        );
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public PageResponseDTO<UserResponseDTO> searchUser(SearchRequestDTO requestDTO) {

        log.info("Searching users");

        PageResponseDTO<UserResponseDTO> pageResponse =
                userRepository.searchUser(requestDTO);
        log.info("Total users found : {}", pageResponse.getTotalElements());
        return pageResponse;
    }
}