package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

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

    // many skills can belong to one user
    // @JoinColumn sets the foreign key column in the skills table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // optional custom decay rate set by the user
    // if null, the system uses the default rate for the skill type
    // allows users to override λ for individual skills
    private Double customDecayRate;

    public Double getCustomDecayRate() { return customDecayRate; }
    public void setCustomDecayRate(Double customDecayRate) { this.customDecayRate = customDecayRate; }

}