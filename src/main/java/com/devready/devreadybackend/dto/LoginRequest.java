package com.devready.devreadybackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// the data the frontend sends when a user logs in
public class LoginRequest {

    @Email(message = "must be a valid email address")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}