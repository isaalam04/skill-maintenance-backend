package com.devready.devreadybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

// represents a registered user in the system
// each user owns their own set of skills, reminders, tags, cemetery entries and snapshots
// cascade = ALL ensures all user data is deleted when the user is deleted
// orphanRemoval = true ensures orphaned records are cleaned up
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // must be unique — used as the login identifier and jwt subject
    @Column(unique = true, nullable = false)
    private String email;

    // stored as a bcrypt hash (strength 12) — never stored as plain text
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String displayName;

    // one user can have many skills
    // deleting the user deletes all their skills
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Skill> skills;

    // one user can have many reminders
    // deleting the user deletes all their reminders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reminder> reminders;

    // one user can have many cemetery entries
    // deleting the user deletes all their cemetery entries
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CemeteryEntry> cemeteryEntries;

    // one user can have many health snapshots
    // deleting the user deletes all their snapshot history
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SkillHealthSnapshot> snapshots;

    // one user can have many tags
    // deleting the user deletes all their tags
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SkillTag> tags;

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

    public List<Reminder> getReminders() { return reminders; }
    public void setReminders(List<Reminder> reminders) { this.reminders = reminders; }

    public List<CemeteryEntry> getCemeteryEntries() { return cemeteryEntries; }
    public void setCemeteryEntries(List<CemeteryEntry> cemeteryEntries) { this.cemeteryEntries = cemeteryEntries; }

    public List<SkillHealthSnapshot> getSnapshots() { return snapshots; }
    public void setSnapshots(List<SkillHealthSnapshot> snapshots) { this.snapshots = snapshots; }

    public List<SkillTag> getTags() { return tags; }
    public void setTags(List<SkillTag> tags) { this.tags = tags; }
}