package com.devready.devreadybackend.dto;

import java.time.LocalDate;

// what the api sends back for each skill in the cemetery
public class CemeteryResponse {

    private Long id;
    private String skillName;
    private String skillType;
    private String lastProficiencyLevel;
    private LocalDate dateLost;
    private long estimatedRelearningDays;
    private int timesLost;
    private boolean revived;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }

    public String getLastProficiencyLevel() { return lastProficiencyLevel; }
    public void setLastProficiencyLevel(String level) { this.lastProficiencyLevel = level; }

    public LocalDate getDateLost() { return dateLost; }
    public void setDateLost(LocalDate dateLost) { this.dateLost = dateLost; }

    public long getEstimatedRelearningDays() { return estimatedRelearningDays; }
    public void setEstimatedRelearningDays(long days) { this.estimatedRelearningDays = days; }

    public int getTimesLost() { return timesLost; }
    public void setTimesLost(int timesLost) { this.timesLost = timesLost; }

    public boolean isRevived() { return revived; }
    public void setRevived(boolean revived) { this.revived = revived; }
}