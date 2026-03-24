package com.devready.devreadybackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

// handles everything related to jwt tokens
// jwt = JSON web token — a secure way to prove a user is logged in
// the token is sent with every request so the server knows who is making it
@Service
public class JwtService {

    // secret key used to sign tokens
    // in production this should be stored in environment variables, not hardcoded
    // must be at least 256 bits for the HS256 algorithm to work
    private static final String SECRET = "devready-secret-key-must-be-at-least-256-bits-long-for-hs256";

    // tokens expire after 24 hours (86400000 milliseconds)
    private static final long EXPIRY_MS = 86400000;

    // converts the secret string into a cryptographic key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // generates a jwt token containing the user's email
    // this token is sent to the frontend after login and stored there
    // the frontend sends it back with every subsequent request
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // extracts the email address from a jwt token
    // used to identify which user is making the request
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // checks if a token is valid and not expired
    // returns false if the token has been tampered with or has expired
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // token is invalid, expired, or tampered with
            return false;
        }
    }
}