package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// stores a daily snapshot of a skill's health score
// used to draw decay history charts on the frontend
// one row is created per skill per day when refreshAllHealthScores runs
@Entity
@Table(name = "skill_health_snapshots")
public class SkillHealthSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the skill this snapshot belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    // the user who owns the skill
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // the date this snapshot was taken
    private LocalDate snapshotDate;

    // the health score on this date
    private double healthScore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDate snapshotDate) { this.snapshotDate = snapshotDate; }

    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }
}