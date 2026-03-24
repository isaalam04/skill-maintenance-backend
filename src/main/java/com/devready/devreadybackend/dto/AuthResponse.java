package com.devready.devreadybackend.dto;

// what the backend sends back after a successful login or registration
// the frontend stores this JWT token and sends it with every future request
public class AuthResponse {

    private String token;
    private String email;
    private String displayName;

    public AuthResponse(String token, String email, String displayName) {
        this.token = token;
        this.email = email;
        this.displayName = displayName;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
}