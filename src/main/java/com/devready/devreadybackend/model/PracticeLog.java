package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// this class represents a single practice session logged by the user
// every time someone says "i practiced java today", a row is added here
@Entity
@Table(name = "practice_logs")
public class PracticeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne means many practice logs can belong to one skill
    // @JoinColumn sets the foreign key column name in the database
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    // the date the practice session happened
    private LocalDate practiceDate;

    // how long the session lasted in minutes
    private int durationMinutes;

    // optional notes the user can add e.g. "revised chapter 3"
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public LocalDate getPracticeDate() { return practiceDate; }
    public void setPracticeDate(LocalDate practiceDate) { this.practiceDate = practiceDate; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}