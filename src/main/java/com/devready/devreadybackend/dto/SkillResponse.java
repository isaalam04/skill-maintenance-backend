package com.devready.devreadybackend.dto;

// this is the data transfer object — it defines exactly what the api sends back to the frontend
// we use this instead of sending the raw Skill entity directly
// because the entity has database annotations and internal fields the frontend doesn't need
public class SkillResponse {

    private Long id;
    private String name;
    private String skillType;         // the skill type as a plain string e.g. "LANGUAGE"
    private double healthScore;       // current health 0.0 to 100.0
    private boolean inForgettingZone; // true if health is below 50%
    private boolean inCemetery;       // true if the skill has died
    private long daysUntilForgettingZone; // how many days before health drops below 50%
    private long daysUntilDead;           // how many days before health hits 0%
    private long estimatedRelearningDays; // how long it would take to relearn if it dies
    private String proficiencyLevel;  // e.g. "beginner", "intermediate"

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }

    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }

    public boolean isInForgettingZone() { return inForgettingZone; }
    public void setInForgettingZone(boolean inForgettingZone) { this.inForgettingZone = inForgettingZone; }

    public boolean isInCemetery() { return inCemetery; }
    public void setInCemetery(boolean inCemetery) { this.inCemetery = inCemetery; }

    public long getDaysUntilForgettingZone() { return daysUntilForgettingZone; }
    public void setDaysUntilForgettingZone(long d) { this.daysUntilForgettingZone = d; }

    public long getDaysUntilDead() { return daysUntilDead; }
    public void setDaysUntilDead(long d) { this.daysUntilDead = d; }

    public long getEstimatedRelearningDays() { return estimatedRelearningDays; }
    public void setEstimatedRelearningDays(long d) { this.estimatedRelearningDays = d; }

    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
}