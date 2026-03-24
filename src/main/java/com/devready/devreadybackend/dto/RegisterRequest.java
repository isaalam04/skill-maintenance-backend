package com.devready.devreadybackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// the data the frontend sends when a user registers
public class RegisterRequest {

    // @Email checks it's a valid email format
    @Email(message = "must be a valid email address")
    @NotBlank(message = "email is required")
    private String email;

    // @Size enforces minimum password length
    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

    @NotBlank(message = "display name is required")
    private String displayName;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}