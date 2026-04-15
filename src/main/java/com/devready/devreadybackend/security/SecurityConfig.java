package com.devready.devreadybackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// configures spring security for the entire application
// defines which endpoints are public and which require a jwt token
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // disable csrf — not needed for stateless jwt apis
                // csrf protection is for session-based apps, not token-based ones
                .csrf(csrf -> csrf.disable())

                // define which endpoints need authentication
                .authorizeHttpRequests(auth -> auth
                        // register and login are public — no token needed
                        .requestMatchers("/api/auth/**").permitAll()
                        // every other endpoint requires a valid jwt token
                        .anyRequest().authenticated()
                )

                // use stateless sessions — no server-side session storage
                // every single request must include the jwt token
                // stateless means the server never stores session data
                // every request must prove who it is via the jwt token
                // this makes the api scalable — any server can handle any request
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // add our jwt filter before spring's default authentication filter
                // this ensures the token is validated before anything else runs
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // bcrypt password encoder with strength factor 12
    // factor 12 means 2^12 hashing rounds — strong against brute force attacks
    // bcrypt automatically handles salting so no two hashes are the same
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}