package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.model.User;
import com.devready.devreadybackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// handles user profile endpoints
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/users/me
    // returns the logged-in user's profile info
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : ""
        ));
    }

    // PUT /api/users/me
    // updates the logged-in user's display name
    @PutMapping("/me")
    public ResponseEntity<Map<String, String>> updateProfile(
            @RequestParam String displayName,
            Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        if (displayName == null || displayName.isBlank()) {
            throw new RuntimeException("display name cannot be blank");
        }

        user.setDisplayName(displayName);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "displayName", user.getDisplayName()
        ));
    }
}