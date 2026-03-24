package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // used during login to find a user by their email address
    // returns Optional so we can handle the case where no user is found
    Optional<User> findByEmail(String email);

    // used during registration to check if the email is already taken
    boolean existsByEmail(String email);
}