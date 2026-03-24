package com.devready.devreadybackend.service;

import com.devready.devreadybackend.model.*;
import com.devready.devreadybackend.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final DecayService decayService;
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final CemeteryRepository cemeteryRepository;

    public SkillService(SkillRepository skillRepository,
                        PracticeLogRepository practiceLogRepository,
                        DecayService decayService,
                        UserRepository userRepository,
                        ReminderRepository reminderRepository,
                        CemeteryRepository cemeteryRepository) {
        this.skillRepository = skillRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.decayService = decayService;
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.cemeteryRepository = cemeteryRepository;
    }

    // helper to get user by email — throws if not found
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));
    }

    // returns all active skills belonging to this user
    public List<Skill> getActiveSkills(String email) {
        User user = getUser(email);
        return skillRepository.findByUserAndInCemeteryFalse(user);
    }

    // adds a new skill for this user
    public Skill addSkill(Skill skill, String email) {
        User user = getUser(email);
        skill.setUser(user);
        skill.setHealthScore(100.0);
        skill.setLastPracticed(LocalDate.now());
        skill.setConsistencyScore(1.0);
        skill.setInCemetery(false);
        return skillRepository.save(skill);
    }

    // logs a practice session for a skill owned by this user
    public Skill logPractice(Long skillId, String email, int durationMinutes, String notes) {
        User user = getUser(email);

        // verify the skill belongs to this user
        Skill skill = skillRepository.findByIdAndUser(skillId, user)
                .orElseThrow(() -> new RuntimeException("skill not found or access denied"));

        PracticeLog log = new PracticeLog();
        log.setSkill(skill);
        log.setPracticeDate(LocalDate.now());
        log.setDurationMinutes(durationMinutes);
        log.setNotes(notes);
        practiceLogRepository.save(log);

        List<PracticeLog> history = practiceLogRepository
                .findBySkillOrderByPracticeDateDesc(skill);
        skill.setConsistencyScore(calculateConsistency(history));
        skill.setLastPracticed(LocalDate.now());
        skill.setHealthScore(100.0);
        skill.setInCemetery(false);

        return skillRepository.save(skill);
    }

    // deletes a skill owned by this user
    public void deleteSkill(Long skillId, String email) {
        User user = getUser(email);
        Skill skill = skillRepository.findByIdAndUser(skillId, user)
                .orElseThrow(() -> new RuntimeException("skill not found or access denied"));
        skillRepository.delete(skill);
    }

    // updates skill name or proficiency level
    public Skill updateSkill(Long skillId, String email, String name, String proficiencyLevel) {
        User user = getUser(email);
        Skill skill = skillRepository.findByIdAndUser(skillId, user)
                .orElseThrow(() -> new RuntimeException("skill not found or access denied"));

        if (name != null && !name.isBlank()) skill.setName(name);
        if (proficiencyLevel != null && !proficiencyLevel.isBlank()) {
            skill.setProficiencyLevel(proficiencyLevel);
        }

        return skillRepository.save(skill);
    }

    // refreshes health scores for all active skills belonging to this user
    // creates reminders for skills entering the forgetting zone
    // moves dead skills to the cemetery
    public void refreshAllHealthScores(String email) {
        User user = getUser(email);
        List<Skill> active = skillRepository.findByUserAndInCemeteryFalse(user);

        for (Skill skill : active) {
            double health = decayService.calculateHealth(skill);
            skill.setHealthScore(health);

            if (decayService.isDead(health)) {
                skill.setInCemetery(true);
                createCemeteryEntry(skill, user);
            } else if (decayService.isInForgettingZone(health)) {
                createReminderIfNeeded(skill, user, health);
            }

            skillRepository.save(skill);
        }
    }

    // revives a skill from the cemetery
    public Skill reviveSkill(Long cemeteryEntryId, String email) {
        User user = getUser(email);
        CemeteryEntry entry = cemeteryRepository.findById(cemeteryEntryId)
                .orElseThrow(() -> new RuntimeException("cemetery entry not found"));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("access denied");
        }

        Skill revived = new Skill();
        revived.setUser(user);
        revived.setName(entry.getSkillName());
        revived.setSkillType(entry.getSkillType());
        revived.setProficiencyLevel(entry.getLastProficiencyLevel());
        revived.setHealthScore(25.0);
        revived.setLastPracticed(LocalDate.now());
        revived.setConsistencyScore(0.5);
        revived.setInCemetery(false);

        entry.setRevived(true);
        cemeteryRepository.save(entry);

        return skillRepository.save(revived);
    }

    // get all cemetery entries for this user
    public List<CemeteryEntry> getCemetery(String email) {
        User user = getUser(email);
        return cemeteryRepository.findByUser(user);
    }

    // get all active reminders for this user
    public List<Reminder> getReminders(String email) {
        User user = getUser(email);
        return reminderRepository.findByUserAndDismissedFalse(user);
    }

    // dismiss a reminder
    public void dismissReminder(Long reminderId, String email) {
        User user = getUser(email);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("reminder not found"));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("access denied");
        }

        reminder.setDismissed(true);
        reminderRepository.save(reminder);
    }

    // creates a cemetery entry when a skill reaches zero health
    private void createCemeteryEntry(Skill skill, User user) {
        int timesLost = cemeteryRepository
                .findByUserAndSkillName(user, skill.getName()).size();

        CemeteryEntry entry = new CemeteryEntry();
        entry.setUser(user);
        entry.setSkillName(skill.getName());
        entry.setSkillType(skill.getSkillType());
        entry.setLastProficiencyLevel(skill.getProficiencyLevel());
        entry.setDateLost(LocalDate.now());
        entry.setEstimatedRelearningDays(decayService.estimateRelearningDays(skill));
        entry.setTimesLost(timesLost + 1);
        entry.setRevived(false);
        cemeteryRepository.save(entry);
    }

    // creates a reminder only if one hasn't already been created for this skill
    private void createReminderIfNeeded(Skill skill, User user, double health) {
        List<Reminder> existing = reminderRepository.findByUserAndDismissedFalse(user);

        boolean alreadyExists = existing.stream()
                .anyMatch(r -> r.getSkill().getId().equals(skill.getId()));

        if (!alreadyExists) {
            Reminder reminder = new Reminder();
            reminder.setSkill(skill);
            reminder.setUser(user);
            reminder.setTriggeredDate(LocalDate.now());
            reminder.setHealthAtTrigger(health);
            reminder.setDismissed(false);
            reminder.setMessage("your " + skill.getName() +
                    " skill is entering the forgetting zone — time for a quick refresh");
            reminderRepository.save(reminder);
        }
    }

    // consistency = proportion of last 7 days where practice occurred
    private double calculateConsistency(List<PracticeLog> history) {
        if (history.isEmpty()) return 0.0;
        LocalDate cutoff = LocalDate.now().minusDays(7);
        long recentSessions = history.stream()
                .filter(l -> l.getPracticeDate().isAfter(cutoff))
                .count();
        return Math.min(1.0, recentSessions / 7.0);
    }
}