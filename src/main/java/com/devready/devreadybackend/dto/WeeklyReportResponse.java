package com.devready.devreadybackend.dto;

import java.util.List;
import java.util.Map;

// returned by GET /api/users/weekly-report
// gives the user a summary of their practice activity over the past 7 days
public class WeeklyReportResponse {

    private int totalPracticeSessions;     // how many sessions logged this week
    private int skillsImproved;            // skills that were practiced at least once
    private int skillsLost;               // skills that moved to the cemetery this week
    private double averageHealthScore;    // average health across all active skills
    private String overallTrend;          // "improving", "stable", or "declining"
    private String bestPracticeDay;       // day of the week with most practice sessions
    private List<String> mostNeglectedSkills;  // top 3 skills with lowest health scores
    private List<String> healthiestSkills;     // top 3 skills with highest health scores
    private Map<String, Integer> sessionsByDay; // number of sessions per day this week

    public WeeklyReportResponse(int totalPracticeSessions,
                                int skillsImproved,
                                int skillsLost,
                                double averageHealthScore,
                                String overallTrend,
                                String bestPracticeDay,
                                List<String> mostNeglectedSkills,
                                List<String> healthiestSkills,
                                Map<String, Integer> sessionsByDay) {
        this.totalPracticeSessions = totalPracticeSessions;
        this.skillsImproved = skillsImproved;
        this.skillsLost = skillsLost;
        this.averageHealthScore = averageHealthScore;
        this.overallTrend = overallTrend;
        this.bestPracticeDay = bestPracticeDay;
        this.mostNeglectedSkills = mostNeglectedSkills;
        this.healthiestSkills = healthiestSkills;
        this.sessionsByDay = sessionsByDay;
    }

    public int getTotalPracticeSessions() { return totalPracticeSessions; }
    public int getSkillsImproved() { return skillsImproved; }
    public int getSkillsLost() { return skillsLost; }
    public double getAverageHealthScore() { return averageHealthScore; }
    public String getOverallTrend() { return overallTrend; }
    public String getBestPracticeDay() { return bestPracticeDay; }
    public List<String> getMostNeglectedSkills() { return mostNeglectedSkills; }
    public List<String> getHealthiestSkills() { return healthiestSkills; }
    public Map<String, Integer> getSessionsByDay() { return sessionsByDay; }
}