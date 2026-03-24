package com.devready.devreadybackend.dto;

import java.time.LocalDate;

// represents a single point on the health history chart
public class SkillHistoryResponse {

    private LocalDate date;
    private double healthScore;

    public SkillHistoryResponse(LocalDate date, double healthScore) {
        this.date = date;
        this.healthScore = healthScore;
    }

    public LocalDate getDate() { return date; }
    public double getHealthScore() { return healthScore; }
}