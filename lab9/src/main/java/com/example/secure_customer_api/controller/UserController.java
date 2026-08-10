package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.UpdateProfileDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {

        // 1. Get current authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        // 2. Fetch user details
        UserResponseDTO user = userService.getCurrentUser(username);

        // 3. Return profile data
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody UpdateProfileDTO dto) {

        // 1. Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        // 2. Update profile
        UserResponseDTO updatedUser =
                userService.updateProfile(username, dto);

        // 3. Return updated user
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestParam String password) {

        // 1. Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        // 2. Delete account (soft delete)
        userService.deleteAccount(username, password);

        // 3. Return response
        return ResponseEntity.ok(
                Map.of("message", "Account deleted successfully"));
    }
}