package com.devready.devreadybackend.security;

import com.devready.devreadybackend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

// this filter runs on every incoming http request before it reaches the controller
// it checks if the request has a valid jwt token in the authorization header
// if valid, it sets the authenticated user in spring's security context
// if not, the request continues unauthenticated and security config will block it
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    // this filter runs before every request reaches a controller
    // without a valid token, spring security blocks the request automatically
    // this means we never need to check authentication inside individual endpoints

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // get the authorization header from the request
        // it should look like: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // no token present — let the request continue
            // security config will block it if the endpoint requires auth
            filterChain.doFilter(request, response);
            return;
        }

        // strip the "Bearer " prefix to get just the token string
        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            // token is invalid or expired — let it continue unauthenticated
            filterChain.doFilter(request, response);
            return;
        }

        // extract the email from the token and load the user from the database
        String email = jwtService.extractEmail(token);

        // only set authentication if not already set in this request
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userRepository.findByEmail(email).ifPresent(user -> {
                // create an authentication object and put it in the security context
                // this tells spring that this request is authenticated as this user
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(), null, new ArrayList<>()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        filterChain.doFilter(request, response);
    }
}