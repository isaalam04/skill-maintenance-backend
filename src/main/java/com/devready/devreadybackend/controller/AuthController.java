package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.AuthResponse;
import com.devready.devreadybackend.dto.LoginRequest;
import com.devready.devreadybackend.dto.RegisterRequest;
import com.devready.devreadybackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// handles register, login, and password reset
// these endpoints are public — no jwt token needed
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    // expects: { email, password, displayName }
    // returns: jwt token + user info
    // @Valid triggers the validation annotations on RegisterRequest
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST /api/auth/login
    // expects: { email, password }
    // returns: jwt token + user info
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/reset-request?email=user@example.com
    // step 1 of password reset — generates a reset token
    // in production this would send an email with the token
    // for now it returns the token directly so it can be tested
    @PostMapping("/reset-request")
    public ResponseEntity<String> requestReset(@RequestParam String email) {
        String token = authService.requestPasswordReset(email);
        return ResponseEntity.ok("reset token: " + token);
    }

    // POST /api/auth/reset-password?email=x&token=x&newPassword=x
    // step 2 of password reset — validates token and sets new password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String token,
            @RequestParam String newPassword) {
        authService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok("password reset successfully.");
    }
}