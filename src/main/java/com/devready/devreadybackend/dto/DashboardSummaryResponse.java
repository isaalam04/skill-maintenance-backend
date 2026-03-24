package com.devready.devreadybackend.dto;

// a summary of the user's overall skill health
// returned by GET /api/skills/summary
// saves the frontend making multiple separate api calls
public class DashboardSummaryResponse {

    private int totalSkills;
    private int skillsInForgettingZone;  // health below 50%
    private int skillsInCemetery;         // health at 0%
    private double averageHealthScore;    // average across all active skills
    private int activeReminders;          // reminders not yet dismissed
    private int practiceStreak;           // days in a row the user has practiced any skill

    public DashboardSummaryResponse(int totalSkills,
                                    int skillsInForgettingZone,
                                    int skillsInCemetery,
                                    double averageHealthScore,
                                    int activeReminders,
                                    int practiceStreak) {
        this.totalSkills = totalSkills;
        this.skillsInForgettingZone = skillsInForgettingZone;
        this.skillsInCemetery = skillsInCemetery;
        this.averageHealthScore = averageHealthScore;
        this.activeReminders = activeReminders;
        this.practiceStreak = practiceStreak;
    }

    public int getTotalSkills() { return totalSkills; }
    public int getSkillsInForgettingZone() { return skillsInForgettingZone; }
    public int getSkillsInCemetery() { return skillsInCemetery; }
    public double getAverageHealthScore() { return averageHealthScore; }
    public int getActiveReminders() { return activeReminders; }
    public int getPracticeStreak() { return practiceStreak; }
}