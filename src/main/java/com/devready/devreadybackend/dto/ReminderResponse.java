package com.devready.devreadybackend.dto;

import java.time.LocalDate;

// what the api sends back when the frontend requests active reminders
public class ReminderResponse {

    private Long id;
    private String skillName;
    private double healthAtTrigger;
    private LocalDate triggeredDate;
    private String message;
    private boolean dismissed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public double getHealthAtTrigger() { return healthAtTrigger; }
    public void setHealthAtTrigger(double healthAtTrigger) { this.healthAtTrigger = healthAtTrigger; }

    public LocalDate getTriggeredDate() { return triggeredDate; }
    public void setTriggeredDate(LocalDate triggeredDate) { this.triggeredDate = triggeredDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isDismissed() { return dismissed; }
    public void setDismissed(boolean dismissed) { this.dismissed = dismissed; }
}