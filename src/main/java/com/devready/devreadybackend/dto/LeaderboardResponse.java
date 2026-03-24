package com.devready.devreadybackend.dto;

import java.util.List;

// returned by GET /api/skills/leaderboard
// shows skills ranked by health score
// helps users see at a glance which skills need attention
public class LeaderboardResponse {

    private List<LeaderboardEntry> healthiest;  // top 3 healthiest skills
    private List<LeaderboardEntry> mostAtRisk;  // top 3 skills closest to dying

    public LeaderboardResponse(List<LeaderboardEntry> healthiest,
                               List<LeaderboardEntry> mostAtRisk) {
        this.healthiest = healthiest;
        this.mostAtRisk = mostAtRisk;
    }

    public List<LeaderboardEntry> getHealthiest() { return healthiest; }
    public List<LeaderboardEntry> getMostAtRisk() { return mostAtRisk; }

    // represents a single entry in the leaderboard
    public static class LeaderboardEntry {
        private String skillName;
        private double healthScore;
        private String skillType;
        private long daysUntilForgettingZone;

        public LeaderboardEntry(String skillName,
                                double healthScore,
                                String skillType,
                                long daysUntilForgettingZone) {
            this.skillName = skillName;
            this.healthScore = healthScore;
            this.skillType = skillType;
            this.daysUntilForgettingZone = daysUntilForgettingZone;
        }

        public String getSkillName() { return skillName; }
        public double getHealthScore() { return healthScore; }
        public String getSkillType() { return skillType; }
        public long getDaysUntilForgettingZone() { return daysUntilForgettingZone; }
    }
}