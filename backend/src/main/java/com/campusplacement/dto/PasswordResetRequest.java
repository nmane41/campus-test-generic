package com.campusplacement.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class PasswordResetRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    public PasswordResetRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

