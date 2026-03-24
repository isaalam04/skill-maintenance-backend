package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// represents a reminder triggered when a skill approaches the forgetting zone
// reminders are predictive — triggered by health score, not fixed schedules
@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the skill this reminder is about
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    // the user who owns this reminder
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // the date the reminder was triggered
    private LocalDate triggeredDate;

    // the health score at the time the reminder was triggered
    private double healthAtTrigger;

    // whether the user has dismissed this reminder
    private boolean dismissed;

    // a human-readable message e.g. "your spanish is entering the forgetting zone"
    private String message;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getTriggeredDate() { return triggeredDate; }
    public void setTriggeredDate(LocalDate triggeredDate) { this.triggeredDate = triggeredDate; }

    public double getHealthAtTrigger() { return healthAtTrigger; }
    public void setHealthAtTrigger(double healthAtTrigger) { this.healthAtTrigger = healthAtTrigger; }

    public boolean isDismissed() { return dismissed; }
    public void setDismissed(boolean dismissed) { this.dismissed = dismissed; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}