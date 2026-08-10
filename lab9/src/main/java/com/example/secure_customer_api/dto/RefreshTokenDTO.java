package com.example.secure_customer_api.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    // Constructor
    public RefreshTokenDTO() {
    }
    public RefreshTokenDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    // Getter & Setter
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}