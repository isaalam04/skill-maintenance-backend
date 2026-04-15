package com.devready.devreadybackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// represents a single skill tracked by a user
// each skill has a health score that decays over time using H(t) = 100 * e^(-λt)
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank ensures the skill name cannot be empty or just whitespace
    @NotBlank(message = "skill name cannot be blank")
    private String name;

    // @NotNull ensures a skill type must always be provided
    @NotNull(message = "skill type is required")
    @Enumerated(EnumType.STRING)
    private SkillType skillType;

    // the last date the user practiced this skill
    // used as the starting point for decay calculations
    private LocalDate lastPracticed;

    // a score from 0.0 to 1.0 representing how consistently the user practices
    // 1.0 = very consistent = slower decay
    // 0.0 = never practices = faster decay
    private double consistencyScore;

    // the current health of the skill from 0.0 to 100.0
    // calculated using H(t) = 100 * e^(-λt)
    private double healthScore;

    // true if the skill has reached 0 health and moved to the skill cemetery
    private boolean inCemetery;

    // the user's level e.g. "beginner", "intermediate", "advanced", "mastery"
    private String proficiencyLevel;

    // optional custom decay rate set by the user
    // if null, the system uses the default rate for the skill type
    private Double customDecayRate;

    // many skills can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // one skill can have many practice logs
    // cascade = ALL ensures logs are deleted when the skill is deleted
    // orphanRemoval = true cleans up any orphaned logs
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PracticeLog> practiceLogs = new ArrayList<>();

    // one skill can have many reminders
    // cascade = ALL ensures reminders are deleted when the skill is deleted
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reminder> reminders = new ArrayList<>();

    // one skill can have many health snapshots
    // cascade = ALL ensures snapshots are deleted when the skill is deleted
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SkillHealthSnapshot> snapshots = new ArrayList<>();

    // many skills can have many tags — many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "skill_tag_mapping",
            joinColumns = @JoinColumn(name = "skill_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<SkillTag> tags = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

    public LocalDate getLastPracticed() { return lastPracticed; }
    public void setLastPracticed(LocalDate lastPracticed) { this.lastPracticed = lastPracticed; }

    public double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }

    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }

    public boolean isInCemetery() { return inCemetery; }
    public void setInCemetery(boolean inCemetery) { this.inCemetery = inCemetery; }

    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }

    public Double getCustomDecayRate() { return customDecayRate; }
    public void setCustomDecayRate(Double customDecayRate) { this.customDecayRate = customDecayRate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<PracticeLog> getPracticeLogs() { return practiceLogs; }
    public void setPracticeLogs(List<PracticeLog> practiceLogs) { this.practiceLogs = practiceLogs; }

    public List<Reminder> getReminders() { return reminders; }
    public void setReminders(List<Reminder> reminders) { this.reminders = reminders; }

    public List<SkillHealthSnapshot> getSnapshots() { return snapshots; }
    public void setSnapshots(List<SkillHealthSnapshot> snapshots) { this.snapshots = snapshots; }

    public List<SkillTag> getTags() { return tags; }
    public void setTags(List<SkillTag> tags) { this.tags = tags; }
}