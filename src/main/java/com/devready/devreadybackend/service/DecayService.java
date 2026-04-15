package com.devready.devreadybackend.service;

import com.devready.devreadybackend.dto.SimulationResponse;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillType;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DecayService {

    // base decay rates per skill type
    private double getBaseDecayRate(SkillType skillType) {
        return switch (skillType) {
            case LANGUAGE    -> 0.05;
            case TECHNICAL   -> 0.03;
            case THEORETICAL -> 0.02;
            case PHYSICAL    -> 0.01;
        };
    }

    // calculates lambda — adjusted for consistency and custom rate if set
    // if the user has set a custom decay rate, that overrides the default
    private double calculateLambda(Skill skill) {
        // use custom rate if the user has set one
        if (skill.getCustomDecayRate() != null && skill.getCustomDecayRate() > 0) {
            return skill.getCustomDecayRate();
        }
        double base = getBaseDecayRate(skill.getSkillType());
        double consistencyMultiplier = 1.0 + (1.0 - skill.getConsistencyScore());
        return base * consistencyMultiplier;
    }

    // H(t) = 100 * e^(-λt)
    // we use exponential decay because ebbinghaus (1885) showed memory loss
    // follows this curve, skills drop fast when neglected and then more slowly
    // this means a small refresh early prevents a larger loss later
    public double calculateHealth(Skill skill) {
        if (skill.getLastPracticed() == null) return 0.0;
        long t = ChronoUnit.DAYS.between(skill.getLastPracticed(), LocalDate.now());
        double lambda = calculateLambda(skill);
        double health = 100.0 * Math.exp(-lambda * t);
        return Math.max(0.0, Math.round(health * 10.0) / 10.0);
    }

    // calculates health at a specific number of days from now
    // used for simulation/projection
    public double calculateHealthInDays(Skill skill, int daysFromNow) {
        if (skill.getLastPracticed() == null) return 0.0;
        long daysSincePractice = ChronoUnit.DAYS.between(
                skill.getLastPracticed(), LocalDate.now());
        long totalDays = daysSincePractice + daysFromNow;
        double lambda = calculateLambda(skill);
        double health = 100.0 * Math.exp(-lambda * totalDays);
        return Math.max(0.0, Math.round(health * 10.0) / 10.0);
    }

    // predicts how many days until health hits a threshold
    public long daysUntilThreshold(Skill skill, double threshold) {
        double lambda = calculateLambda(skill);
        double currentHealth = skill.getHealthScore();
        if (currentHealth <= threshold) return 0;
        double daysRemaining = -Math.log(threshold / currentHealth) / lambda;
        return Math.max(0, Math.round(daysRemaining));
    }

    // simulates health decay for the next N days
    // returns a list of projected health scores day by day
    public SimulationResponse simulate(Skill skill, int days) {
        List<SimulationResponse.SimulationDataPoint> projection = new ArrayList<>();

        for (int day = 1; day <= days; day++) {
            double projectedHealth = calculateHealthInDays(skill, day);
            projection.add(new SimulationResponse.SimulationDataPoint(day, projectedHealth));
        }

        return new SimulationResponse(
                skill.getName(),
                skill.getHealthScore(),
                daysUntilThreshold(skill, 50.0),
                daysUntilThreshold(skill, 1.0),
                projection
        );
    }

    // estimates relearning time for a dead skill
    public long estimateRelearningDays(Skill skill) {
        if (skill.getLastPracticed() == null) return 30;
        long neglectedDays = ChronoUnit.DAYS.between(
                skill.getLastPracticed(), LocalDate.now());
        return switch (skill.getSkillType()) {
            case LANGUAGE    -> Math.min(neglectedDays / 2, 90);
            case TECHNICAL   -> Math.min(neglectedDays / 3, 60);
            case THEORETICAL -> Math.min(neglectedDays / 4, 45);
            case PHYSICAL    -> Math.min(neglectedDays / 5, 30);
        };
    }

    public boolean isInForgettingZone(double health) { return health < 50.0; }
    public boolean isDead(double health)              { return health <= 0.0; }
}