package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.AuthResponse;
import com.devready.devreadybackend.dto.LoginRequest;
import com.devready.devreadybackend.dto.RegisterRequest;
import com.devready.devreadybackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// handles register and login — these endpoints are public (no token needed)
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
}