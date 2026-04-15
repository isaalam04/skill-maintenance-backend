package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.model.User;
import com.devready.devreadybackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

// handles user profile endpoints
// all endpoints require authentication via jwt
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/users/me
    // returns the logged-in user's profile
    @GetMapping("/me")
    public ResponseEntity<User> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/me
    // updates the logged-in user's display name
    @PutMapping("/me")
    @Transactional
    public ResponseEntity<User> updateProfile(
            @RequestParam String displayName,
            Authentication auth) {
        log.info("user {} is updating their display name", auth.getName());
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));
        user.setDisplayName(displayName);
        return ResponseEntity.ok(userRepository.save(user));
    }

    // DELETE /api/users/me
    // permanently deletes the logged-in user's account and all associated data
    // cascading deletes are handled by the database foreign key constraints
    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<Void> deleteAccount(Authentication auth) {
        log.info("user {} is deleting their account", auth.getName());
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}