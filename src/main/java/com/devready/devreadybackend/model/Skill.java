package com.devready.devreadybackend.model;

import jakarta.persistence.*;  // imports everything needed to map this class to a database table
import java.time.LocalDate;    // used to store dates without time (e.g. 2026-03-01)

// @Entity tells spring boot this class maps to a database table
@Entity
// @Table sets the actual table name in postgresql
@Table(name = "skills")
public class Skill {

    // @Id marks this as the primary key (unique identifier for each row)
    @Id
    // @GeneratedValue means the database auto-increments the id (1, 2, 3...)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the name of the skill e.g. "spanish" or "java"
    private String name;

    // @Enumerated(STRING) stores the enum as text in the db e.g. "LANGUAGE"
    // instead of a number, which makes the database readable
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
    // stored here so we don't recalculate it every time
    private double healthScore;

    // true if the skill has reached 0 health and moved to the skill cemetery
    private boolean inCemetery;

    // the user's level e.g. "beginner", "intermediate", "advanced", "mastery"
    private String proficiencyLevel;

    // --- getters and setters ---
    // these allow other classes to read and write each field
    // spring and jpa require these to function correctly

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
}