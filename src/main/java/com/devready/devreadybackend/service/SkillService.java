package com.devready.devreadybackend.service;

import com.devready.devreadybackend.model.PracticeLog;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.repository.PracticeLogRepository;
import com.devready.devreadybackend.repository.SkillRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

// this service sits between the controller and the database
// it handles all the business logic so the controller stays clean
@Service
public class SkillService {

    // spring automatically injects these dependencies via the constructor
    private final SkillRepository skillRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final DecayService decayService;

    public SkillService(SkillRepository skillRepository,
                        PracticeLogRepository practiceLogRepository,
                        DecayService decayService) {
        this.skillRepository = skillRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.decayService = decayService;
    }

    // returns all skills that haven't died yet (not in the cemetery)
    public List<Skill> getActiveSkills() {
        return skillRepository.findByInCemeteryFalse();
    }

    // returns all skills that have decayed to 0 and are in the cemetery
    public List<Skill> getCemeterySkills() {
        return skillRepository.findByInCemeteryTrue();
    }

    // adds a brand-new skill for the user
    // starts at full health (100) with perfect consistency (1.0)
    public Skill addSkill(Skill skill) {
        skill.setHealthScore(100.0);
        skill.setLastPracticed(LocalDate.now());
        skill.setConsistencyScore(1.0);
        skill.setInCemetery(false);
        return skillRepository.save(skill);
    }

    // logs a practice session for a skill and updates all related scores
    public Skill logPractice(Long skillId, int durationMinutes, String notes) {

        // find the skill or throw an error if it doesn't exist
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("skill not found: " + skillId));

        // save the practice session to the practice_logs table
        PracticeLog log = new PracticeLog();
        log.setSkill(skill);
        log.setPracticeDate(LocalDate.now());
        log.setDurationMinutes(durationMinutes);
        log.setNotes(notes);
        practiceLogRepository.save(log);

        // recalculate consistency based on the full practice history
        List<PracticeLog> history = practiceLogRepository
                .findBySkillOrderByPracticeDateDesc(skill);
        skill.setConsistencyScore(calculateConsistency(history));

        // practicing resets health to 100 and updates the last practiced date
        skill.setLastPracticed(LocalDate.now());
        skill.setHealthScore(100.0);
        skill.setInCemetery(false); // revives the skill if it was in the cemetery

        return skillRepository.save(skill);
    }

    // recalculates health scores for all active skills
    // should be called once per day (we can add a scheduler later)
    public void refreshAllHealthScores() {
        List<Skill> active = skillRepository.findByInCemeteryFalse();
        for (Skill skill : active) {
            double health = decayService.calculateHealth(skill);
            skill.setHealthScore(health);

            // if health has hit zero, move the skill to the cemetery
            if (decayService.isDead(health)) {
                skill.setInCemetery(true);
            }
            skillRepository.save(skill);
        }
    }

    // calculates consistency as the proportion of the last 7 days in which practice occurred
    // e.g. practiced 5 out of 7 days → consistency = 0.71
    private double calculateConsistency(List<PracticeLog> history) {
        if (history.isEmpty()) return 0.0;
        LocalDate cutoff = LocalDate.now().minusDays(7);
        long recentSessions = history.stream()
                .filter(l -> l.getPracticeDate().isAfter(cutoff))
                .count();
        return Math.min(1.0, recentSessions / 7.0);
    }
}