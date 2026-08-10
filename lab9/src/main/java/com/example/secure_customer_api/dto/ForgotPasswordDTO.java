package com.example.secure_customer_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class ForgotPasswordDTO {
    @NotBlank
    @Email
    private String email;
    public ForgotPasswordDTO(){}
    public ForgotPasswordDTO (String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail (String email) {
        this.email = email;
    }
}
