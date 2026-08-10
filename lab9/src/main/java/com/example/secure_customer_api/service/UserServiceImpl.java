package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.*;
import com.example.secure_customer_api.entity.RefreshToken;
import com.example.secure_customer_api.entity.Role;
import com.example.secure_customer_api.entity.User;
import com.example.secure_customer_api.exception.DuplicateResourceException;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.repository.RefreshTokenRepository;
import com.example.secure_customer_api.repository.UserRepository;
import com.example.secure_customer_api.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshToken createRefreshToken(User user) {

        // Optional: delete old refresh tokens (one active session)
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {

        // 1. Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generate access token (JWT)
        String accessToken = tokenProvider.generateToken(authentication);

        // 3. Load user
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Optional: block deactivated users
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // 4. Generate refresh token
        RefreshToken refreshToken = createRefreshToken(user);

        // 5. Return both tokens
        return new LoginResponseDTO(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
    
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToDTO(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }

    @Override
    public void changePassword(String username, ChangePasswordDTO dto) {
        // 1. Load user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 3. Check new password confirmation
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // 4. Hash and update password
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedPassword);

        // 5. Save updated password
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        // 1. Generate reset token
        String token = UUID.randomUUID().toString();

        // 2. Save token + expiry (1 hour)
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        // 3. Return token (real app → send via email)
        System.out.println("Password reset token: " + token);
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {

        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        // 1. Check expiry
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        // 2. Confirm password
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // 3. Hash and update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        // 4. Clear token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    @Override
    public UserResponseDTO updateProfile(String username, UpdateProfileDTO dto) {

        // 1. Load current user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Update fields
        user.setFullName(dto.getFullName());

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        // 3. Save changes
        User updatedUser = userRepository.save(user);

        // 4. Return updated profile
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteAccount(String username, String password) {

        // 1. Load user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Soft delete
        user.setIsActive(false);

        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, Role role) {

        // 1. Load user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Update role
        user.setRole(role);

        // 3. Save changes
        User updatedUser = userRepository.save(user);

        // 4. Return updated user
        return convertToDTO(updatedUser);
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long userId) {

        // 1. Load user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Toggle isActive
        user.setIsActive(!user.getIsActive());

        // 3. Save changes
        User updatedUser = userRepository.save(user);

        // 4. Return updated user
        return convertToDTO(updatedUser);
    }

    @Override
    public LoginResponseDTO refreshAccessToken(String refreshToken) {

        // 1. Find refresh token in DB
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2. Check expiry
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        // 3. Get user
        User user = token.getUser();

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        // 4. Generate new access token
        String newAccessToken =
                tokenProvider.generateTokenFromUsername(user.getUsername());

        // 5. Rotate refresh token
        refreshTokenRepository.delete(token);
        RefreshToken newRefreshToken = createRefreshToken(user);

        // 6. Return new access token
        return new LoginResponseDTO(
                newAccessToken,
                newRefreshToken.getToken(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}