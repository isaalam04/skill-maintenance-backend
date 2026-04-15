package com.devready.devreadybackend.service;

import com.devready.devreadybackend.dto.AuthResponse;
import com.devready.devreadybackend.dto.LoginRequest;
import com.devready.devreadybackend.dto.RegisterRequest;
import com.devready.devreadybackend.model.User;
import com.devready.devreadybackend.repository.UserRepository;
import com.devready.devreadybackend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// handles user registration, login, and password reset logic
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // stores password reset tokens temporarily in memory
    // maps email -> reset token
    private final Map<String, String> resetTokens = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // registers a new user
    // hashes the password with bcrypt before saving
    // returns a jwt token so the user is immediately logged in after registering
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("an account with this email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        userRepository.save(user);

        log.info("new user registered: {}", request.getEmail());

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getDisplayName());
    }

    // logs in an existing user
    // checks the hashed password using bcrypt's built-in comparison
    // returns a jwt token if credentials are correct
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("no account found with this email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("incorrect password");
        }

        log.info("user logged in: {}", request.getEmail());

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getDisplayName());
    }

    // step 1 of password reset
    // generates a short uppercase token and emails it to the user via sendgrid
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("no account found with this email"));

        // generate a short 8-character uppercase token — easier to type from email
        String token = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        // store the token against the user's email
        resetTokens.put(email, token);

        // send the token via sendgrid
        emailService.sendPasswordResetEmail(email, token);

        log.info("password reset token generated and emailed to {}", email);
    }

    // step 2 of password reset
    // validates the token and sets the new password
    // the token is removed after use so it cannot be reused
    public void resetPassword(String email, String token, String newPassword) {
        if (!resetTokens.containsKey(email)) {
            throw new RuntimeException("no reset request found for this email");
        }

        if (!resetTokens.get(email).equals(token)) {
            throw new RuntimeException("invalid or expired reset token");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("password must be at least 8 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("no account found with this email"));

        // hash the new password before saving — never store plain text
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // remove the token so it cannot be used again
        resetTokens.remove(email);

        log.info("password reset successfully for {}", email);
    }
}