package com.example.LeadManagement.repository.custom;


import com.example.LeadManagement.dto.PageResponseDTO;
import com.example.LeadManagement.dto.SearchRequestDTO;
import com.example.LeadManagement.dto.request.UserRequestDTO;
import com.example.LeadManagement.dto.response.UserResponseDTO;
import com.example.LeadManagement.model.Users;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

        Users createUser(Users users);

        List<Users> getAllUsers();

        Optional<Users> getUserById(String id);

        Users updateUser(String id, UserRequestDTO requestDTO);

        void deleteUser(String id);

        Optional<Users> findByEmail(String email);

        Optional<Users> findByPhone(String phone);

        PageResponseDTO<UserResponseDTO> searchUser(SearchRequestDTO requestDTO);
}
