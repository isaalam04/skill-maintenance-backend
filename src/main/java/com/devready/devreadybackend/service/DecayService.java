package com.devready.devreadybackend.service;

import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillType;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// @Service marks this as a spring service — it contains business logic
// spring will automatically create one instance of this and share it across the app
@Service
public class DecayService {

    // base decay rate (λ) per skill type
    // higher value = skill fades faster
    // these values are based on the ebbinghaus forgetting curve research
    private double getBaseDecayRate(SkillType skillType) {
        return switch (skillType) {
            case LANGUAGE    -> 0.05;  // languages fade the fastest without practice
            case TECHNICAL   -> 0.03;  // coding syntax fades medium-fast
            case THEORETICAL -> 0.02;  // concepts and theory fade more slowly
            case PHYSICAL    -> 0.01;  // muscle memory fades the slowest
        };
    }

    // calculates the actual lambda value used in the decay formula
    // consistency adjusts the rate — consistent practice slows decay down
    // consistencyScore of 1.0 → multiplier of 1.0 (normal decay)
    // consistencyScore of 0.0 → multiplier of 2.0 (twice as fast decay)
    private double calculateLambda(SkillType skillType, double consistencyScore) {
        double base = getBaseDecayRate(skillType);
        double consistencyMultiplier = 1.0 + (1.0 - consistencyScore);
        return base * consistencyMultiplier;
    }

    // the core decay formula: H(t) = 100 * e^(-λt)
    // t = number of days since the skill was last practiced
    // λ = decay rate adjusted for skill type and consistency
    // result is clamped to 0 at minimum and rounded to 1 decimal place
    public double calculateHealth(Skill skill) {
        if (skill.getLastPracticed() == null) return 0.0;

        // calculate how many days have passed since last practice
        long t = ChronoUnit.DAYS.between(skill.getLastPracticed(), LocalDate.now());

        double lambda = calculateLambda(skill.getSkillType(), skill.getConsistencyScore());

        // apply the exponential decay formula
        double health = 100.0 * Math.exp(-lambda * t);

        // Math.round to 1dp, then floor at 0
        return Math.max(0.0, Math.round(health * 10.0) / 10.0);
    }

    // predicts how many days until a skill reaches a given health threshold
    // rearranges H(t) = 100 * e^(-λt) to solve for t:
    // t = -ln(threshold / currentHealth) / λ
    // used to tell the user "your spanish will hit the forgetting zone in 4 days"
    public long daysUntilThreshold(Skill skill, double threshold) {
        double lambda = calculateLambda(skill.getSkillType(), skill.getConsistencyScore());
        double currentHealth = skill.getHealthScore();

        // already at or below the threshold — return 0
        if (currentHealth <= threshold) return 0;

        double daysRemaining = -Math.log(threshold / currentHealth) / lambda;
        return Math.max(0, Math.round(daysRemaining));
    }

    // estimates how long it would take to relearn a dead skill
    // based on how long it was neglected and what type of skill it is
    public long estimateRelearningDays(Skill skill) {
        if (skill.getLastPracticed() == null) return 30;

        long neglectedDays = ChronoUnit.DAYS.between(skill.getLastPracticed(), LocalDate.now());

        // languages take longest to relearn, physical skills shortest
        // capped at sensible maximums to avoid unrealistic estimates
        return switch (skill.getSkillType()) {
            case LANGUAGE    -> Math.min(neglectedDays / 2, 90);
            case TECHNICAL   -> Math.min(neglectedDays / 3, 60);
            case THEORETICAL -> Math.min(neglectedDays / 4, 45);
            case PHYSICAL    -> Math.min(neglectedDays / 5, 30);
        };
    }

    // below 50% health means the skill is entering the forgetting zone
    public boolean isInForgettingZone(double health) { return health < 50.0; }

    // at 0% the skill is considered lost and moves to the cemetery
    public boolean isDead(double health) { return health <= 0.0; }
}