package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.ChangePasswordDTO;
import com.example.secure_customer_api.dto.ForgotPasswordDTO;
import com.example.secure_customer_api.dto.LoginRequestDTO;
import com.example.secure_customer_api.dto.LoginResponseDTO;
import com.example.secure_customer_api.dto.RegisterRequestDTO;
import com.example.secure_customer_api.dto.ResetPasswordDTO;
import com.example.secure_customer_api.dto.UpdateProfileDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.entity.Role;

import java.util.List;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);

    void changePassword(String username, ChangePasswordDTO dto);

    void forgotPassword(ForgotPasswordDTO dto);

    void resetPassword(ResetPasswordDTO dto);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO dto);

    void deleteAccount(String username, String password);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long userId, Role role);

    UserResponseDTO toggleUserStatus(Long userId);

    LoginResponseDTO refreshAccessToken(String refreshToken);

}