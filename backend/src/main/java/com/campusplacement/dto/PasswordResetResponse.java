package com.campusplacement.dto;

public class PasswordResetResponse {
    private String temporaryPassword;
    private String message;

    public PasswordResetResponse() {
    }

    public PasswordResetResponse(String temporaryPassword, String message) {
        this.temporaryPassword = temporaryPassword;
        this.message = message;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

