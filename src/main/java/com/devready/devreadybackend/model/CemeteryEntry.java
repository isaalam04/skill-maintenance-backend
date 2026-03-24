package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// represents a skill that has decayed to zero health and been "buried"
// keeps a record of what the skill was and how long it would take to relearn
@Entity
@Table(name = "skill_cemetery")
public class CemeteryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the user who lost this skill
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // the skill name e.g. "spanish" — stored separately since the skill object may be gone
    private String skillName;

    // what type of skill it was
    @Enumerated(EnumType.STRING)
    private SkillType skillType;

    // what level they had reached before the skill died
    private String lastProficiencyLevel;

    // the date the skill reached zero and was moved to the cemetery
    private LocalDate dateLost;

    // estimated number of days to relearn the skill
    private long estimatedRelearningDays;

    // how many times this skill has been lost — tracks repeat neglect
    private int timesLost;

    // whether the user has revived this skill
    private boolean revived;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

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