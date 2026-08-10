package com.example.secure_customer_api.dto;

import com.example.secure_customer_api.entity.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleDTO {

    @NotNull(message = "Role is required")
    private Role role;
    // Constructor
    public UpdateRoleDTO() {
    }
    public UpdateRoleDTO(Role role) {
        this.role = role;
    }
    // Getter & Setter
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}