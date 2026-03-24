package com.devready.devreadybackend.dto;

import java.util.List;

// returned by GET /api/skills/{id}/simulate?days=30
// shows projected health scores for each day going forward
public class SimulationResponse {

    private String skillName;
    private double currentHealth;
    private long daysUntilForgettingZone;
    private long daysUntilDead;
    private List<SimulationDataPoint> projection;

    public SimulationResponse(String skillName,
                              double currentHealth,
                              long daysUntilForgettingZone,
                              long daysUntilDead,
                              List<SimulationDataPoint> projection) {
        this.skillName = skillName;
        this.currentHealth = currentHealth;
        this.daysUntilForgettingZone = daysUntilForgettingZone;
        this.daysUntilDead = daysUntilDead;
        this.projection = projection;
    }

    public String getSkillName() { return skillName; }
    public double getCurrentHealth() { return currentHealth; }
    public long getDaysUntilForgettingZone() { return daysUntilForgettingZone; }
    public long getDaysUntilDead() { return daysUntilDead; }
    public List<SimulationDataPoint> getProjection() { return projection; }

    // represents a single projected data point — one day in the future
    public static class SimulationDataPoint {
        private int day;
        private double projectedHealth;

        public SimulationDataPoint(int day, double projectedHealth) {
            this.day = day;
            this.projectedHealth = projectedHealth;
        }

        public int getDay() { return day; }
        public double getProjectedHealth() { return projectedHealth; }
    }
}