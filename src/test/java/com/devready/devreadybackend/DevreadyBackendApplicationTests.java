package com.devready.devreadybackend;

import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillType;
import com.devready.devreadybackend.service.DecayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DevreadyBackendApplicationTests {

    private DecayService decayService;

    @BeforeEach
    void setUp() {
        decayService = new DecayService();
    }

    // test 1 — a skill practiced today should have exactly 100% health
    @Test
    void skillPracticedTodayShouldHaveFullHealth() {
        Skill skill = buildSkill(SkillType.TECHNICAL, LocalDate.now(), 1.0);
        double health = decayService.calculateHealth(skill);
        assertEquals(100.0, health, 0.1);
    }

    // test 2 — language skills should decay faster than technical skills
    @Test
    void languageSkillShouldDecayFasterThanTechnical() {
        Skill language = buildSkill(SkillType.LANGUAGE, LocalDate.now().minusDays(14), 1.0);
        Skill technical = buildSkill(SkillType.TECHNICAL, LocalDate.now().minusDays(14), 1.0);
        assertTrue(
                decayService.calculateHealth(language) < decayService.calculateHealth(technical),
                "language should decay faster than technical"
        );
    }

    // test 3 — technical skills should decay faster than theoretical skills
    @Test
    void technicalSkillShouldDecayFasterThanTheoretical() {
        Skill technical = buildSkill(SkillType.TECHNICAL, LocalDate.now().minusDays(20), 1.0);
        Skill theoretical = buildSkill(SkillType.THEORETICAL, LocalDate.now().minusDays(20), 1.0);
        assertTrue(
                decayService.calculateHealth(technical) < decayService.calculateHealth(theoretical),
                "technical should decay faster than theoretical"
        );
    }

    // test 4 — theoretical skills should decay faster than physical skills
    @Test
    void theoreticalSkillShouldDecayFasterThanPhysical() {
        Skill theoretical = buildSkill(SkillType.THEORETICAL, LocalDate.now().minusDays(20), 1.0);
        Skill physical = buildSkill(SkillType.PHYSICAL, LocalDate.now().minusDays(20), 1.0);
        assertTrue(
                decayService.calculateHealth(theoretical) < decayService.calculateHealth(physical),
                "theoretical should decay faster than physical"
        );
    }

    // test 5 — physical skills should decay the slowest of all types
    @Test
    void physicalSkillShouldDecaySlowest() {
        LocalDate lastPracticed = LocalDate.now().minusDays(30);
        Skill language = buildSkill(SkillType.LANGUAGE, lastPracticed, 1.0);
        Skill technical = buildSkill(SkillType.TECHNICAL, lastPracticed, 1.0);
        Skill theoretical = buildSkill(SkillType.THEORETICAL, lastPracticed, 1.0);
        Skill physical = buildSkill(SkillType.PHYSICAL, lastPracticed, 1.0);

        double langHealth = decayService.calculateHealth(language);
        double techHealth = decayService.calculateHealth(technical);
        double theorHealth = decayService.calculateHealth(theoretical);
        double physHealth = decayService.calculateHealth(physical);

        assertTrue(physHealth > theorHealth, "physical should decay slower than theoretical");
        assertTrue(theorHealth > techHealth, "theoretical should decay slower than technical");
        assertTrue(techHealth > langHealth, "technical should decay slower than language");
    }

    // test 6 — low consistency should result in faster decay
    @Test
    void lowConsistencyShouldDecayFaster() {
        Skill highConsistency = buildSkill(SkillType.TECHNICAL, LocalDate.now().minusDays(10), 1.0);
        Skill lowConsistency = buildSkill(SkillType.TECHNICAL, LocalDate.now().minusDays(10), 0.0);
        assertTrue(
                decayService.calculateHealth(lowConsistency) < decayService.calculateHealth(highConsistency),
                "low consistency should result in lower health"
        );
    }

    // test 7 — health score should never go below zero
    @Test
    void healthShouldNeverGoBelowZero() {
        Skill skill = buildSkill(SkillType.LANGUAGE, LocalDate.now().minusDays(365), 0.0);
        assertTrue(decayService.calculateHealth(skill) >= 0.0);
    }

    // test 8 — a skill with no last practiced date should return 0 health
    @Test
    void skillWithNoPracticeDateShouldHaveZeroHealth() {
        Skill skill = new Skill();
        skill.setSkillType(SkillType.TECHNICAL);
        skill.setConsistencyScore(1.0);
        skill.setLastPracticed(null);
        assertEquals(0.0, decayService.calculateHealth(skill), 0.01);
    }

    // test 9 — a skill below 50% should be in the forgetting zone
    @Test
    void skillBelow50ShouldBeInForgettingZone() {
        assertTrue(decayService.isInForgettingZone(49.9));
        assertFalse(decayService.isInForgettingZone(50.0));
        assertFalse(decayService.isInForgettingZone(75.0));
    }

    // test 10 — a skill at exactly 0 should be flagged as dead
    @Test
    void skillAtZeroShouldBeDead() {
        assertTrue(decayService.isDead(0.0));
        assertFalse(decayService.isDead(0.1));
        assertFalse(decayService.isDead(50.0));
    }

    // test 11 — a skill already below threshold should return 0 days until threshold
    @Test
    void skillAlreadyBelowThresholdShouldReturnZeroDays() {
        Skill skill = buildSkill(SkillType.TECHNICAL, LocalDate.now().minusDays(50), 1.0);
        skill.setHealthScore(30.0);
        assertEquals(0, decayService.daysUntilThreshold(skill, 50.0));
    }

    // test 12 — a fresh skill should take more than 0 days to reach the forgetting zone
    @Test
    void freshSkillShouldTakeMoreThanZeroDaysToDecay() {
        Skill skill = buildSkill(SkillType.TECHNICAL, LocalDate.now(), 1.0);
        skill.setHealthScore(100.0);
        assertTrue(decayService.daysUntilThreshold(skill, 50.0) > 0);
    }

    // test 13 — simulation should return exactly the requested number of data points
    @Test
    void simulationShouldReturnCorrectNumberOfDays() {
        Skill skill = buildSkill(SkillType.TECHNICAL, LocalDate.now(), 1.0);
        skill.setHealthScore(100.0);
        assertEquals(30, decayService.simulate(skill, 30).getProjection().size());
    }

    // test 14 — projected health should decrease over time in the simulation
    @Test
    void simulationHealthShouldDecreaseOverTime() {
        Skill skill = buildSkill(SkillType.TECHNICAL, LocalDate.now(), 1.0);
        skill.setHealthScore(100.0);
        var points = decayService.simulate(skill, 10).getProjection();
        for (int i = 1; i < points.size(); i++) {
            assertTrue(
                    points.get(i).getProjectedHealth() <= points.get(i - 1).getProjectedHealth(),
                    "health should not increase day over day"
            );
        }
    }

    // test 15 — a custom decay rate should override the default rate
    @Test
    void customDecayRateShouldOverrideDefault() {
        Skill slowDecay = buildSkill(SkillType.LANGUAGE, LocalDate.now().minusDays(14), 1.0);
        slowDecay.setCustomDecayRate(0.001);

        Skill defaultDecay = buildSkill(SkillType.LANGUAGE, LocalDate.now().minusDays(14), 1.0);

        assertTrue(
                decayService.calculateHealth(slowDecay) > decayService.calculateHealth(defaultDecay),
                "custom slow decay rate should result in higher health than default"
        );
    }

    // test 16 — language should take longer to relearn than physical skills
    @Test
    void languageShouldTakeLongerToRelearn() {
        Skill language = buildSkill(SkillType.LANGUAGE, LocalDate.now().minusDays(60), 0.0);
        Skill physical = buildSkill(SkillType.PHYSICAL, LocalDate.now().minusDays(60), 0.0);
        assertTrue(
                decayService.estimateRelearningDays(language) > decayService.estimateRelearningDays(physical),
                "language should take longer to relearn than physical"
        );
    }

    // helper method — builds a test skill quickly without repeating setup code
    private Skill buildSkill(SkillType type, LocalDate lastPracticed, double consistency) {
        Skill skill = new Skill();
        skill.setSkillType(type);
        skill.setLastPracticed(lastPracticed);
        skill.setConsistencyScore(consistency);
        skill.setHealthScore(100.0);
        skill.setName("test skill");
        return skill;
    }
}