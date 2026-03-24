package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.util.List;

// represents a registered user in the system
// each user owns their own set of skills
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // must be unique — used as the login identifier
    @Column(unique = true, nullable = false)
    private String email;

    // stored as a bcrypt hash — never stored as plain text
    @Column(nullable = false)
    private String password;

    private String displayName;

    // one user can have many skills
    // cascade = if user is deleted, their skills are deleted too
    // fetch lazy = skills are only loaded from db when actually needed
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Skill> skills;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
}