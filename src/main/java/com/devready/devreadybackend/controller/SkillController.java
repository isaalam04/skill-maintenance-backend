package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.*;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.service.DecayService;
import com.devready.devreadybackend.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// all endpoints here require authentication
// the Authentication object is injected by spring from the jwt token
// auth.getName() returns the email of the logged-in user
@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    private final SkillService skillService;
    private final DecayService decayService;

    public SkillController(SkillService skillService, DecayService decayService) {
        this.skillService = skillService;
        this.decayService = decayService;
    }

    // GET /api/skills
    // returns all active skills for the logged-in user with full decay info
    @GetMapping
    public List<SkillResponse> getActiveSkills(Authentication auth) {
        return skillService.getActiveSkills(auth.getName())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // GET /api/skills/{id}
    // returns a single skill by id — used for individual skill detail pages
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(
            @PathVariable Long id,
            Authentication auth) {
        Skill skill = skillService.getActiveSkills(auth.getName())
                .stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("skill not found"));
        return ResponseEntity.ok(toResponse(skill));
    }

    // POST /api/skills
    // adds a new skill for the logged-in user
    @PostMapping
    public ResponseEntity<SkillResponse> addSkill(
            @Valid @RequestBody Skill skill,
            Authentication auth) {
        Skill saved = skillService.addSkill(skill, auth.getName());
        return ResponseEntity.ok(toResponse(saved));
    }

    // PUT /api/skills/{id}
    // updates a skill's name or proficiency level
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String proficiencyLevel,
            Authentication auth) {
        Skill updated = skillService.updateSkill(id, auth.getName(), name, proficiencyLevel);
        return ResponseEntity.ok(toResponse(updated));
    }

    // DELETE /api/skills/{id}
    // deletes a skill belonging to the logged-in user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(
            @PathVariable Long id,
            Authentication auth) {
        skillService.deleteSkill(id, auth.getName());
        return ResponseEntity.ok("skill deleted.");
    }

    // POST /api/skills/{id}/practice
    // logs a practice session for a specific skill
    // resets health to 100 and updates consistency score
    @PostMapping("/{id}/practice")
    public ResponseEntity<SkillResponse> logPractice(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int durationMinutes,
            @RequestParam(defaultValue = "") String notes,
            Authentication auth) {
        Skill updated = skillService.logPractice(id, auth.getName(), durationMinutes, notes);
        return ResponseEntity.ok(toResponse(updated));
    }

    // POST /api/skills/refresh
    // recalculates health scores, creates reminders, moves dead skills to cemetery
    // also saves daily health snapshots for history tracking
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAll(Authentication auth) {
        skillService.refreshAllHealthScores(auth.getName());
        return ResponseEntity.ok("all health scores refreshed.");
    }

    // GET /api/skills/summary
    // returns a dashboard overview — total skills, forgetting zone count, average health etc.
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(Authentication auth) {
        return ResponseEntity.ok(skillService.getDashboardSummary(auth.getName()));
    }

    // GET /api/skills/cemetery
    // returns all cemetery entries for the logged-in user
    @GetMapping("/cemetery")
    public List<CemeteryResponse> getCemetery(Authentication auth) {
        return skillService.getCemetery(auth.getName())
                .stream()
                .map(this::toCemeteryResponse)
                .collect(Collectors.toList());
    }

    // POST /api/skills/cemetery/{id}/revive
    // revives a skill from the cemetery — starts at 25% health
    @PostMapping("/cemetery/{id}/revive")
    public ResponseEntity<SkillResponse> reviveSkill(
            @PathVariable Long id,
            Authentication auth) {
        Skill revived = skillService.reviveSkill(id, auth.getName());
        return ResponseEntity.ok(toResponse(revived));
    }

    // GET /api/skills/reminders
    // returns all active (not dismissed) reminders for the logged-in user
    @GetMapping("/reminders")
    public List<ReminderResponse> getReminders(Authentication auth) {
        return skillService.getReminders(auth.getName())
                .stream()
                .map(this::toReminderResponse)
                .collect(Collectors.toList());
    }

    // POST /api/skills/reminders/{id}/dismiss
    // dismisses a reminder so it no longer shows on the dashboard
    @PostMapping("/reminders/{id}/dismiss")
    public ResponseEntity<String> dismissReminder(
            @PathVariable Long id,
            Authentication auth) {
        skillService.dismissReminder(id, auth.getName());
        return ResponseEntity.ok("reminder dismissed.");
    }

    // GET /api/skills/{id}/history
    // returns the daily health snapshots for a skill
    // used to draw the health-over-time chart on the frontend
    @GetMapping("/{id}/history")
    public ResponseEntity<List<SkillHistoryResponse>> getHistory(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(skillService.getSkillHistory(id, auth.getName()));
    }

    // GET /api/skills/{id}/simulate?days=30
    // returns projected health scores for the next N days without practice
    // useful for the frontend to draw decay projection charts
    @GetMapping("/{id}/simulate")
    public ResponseEntity<SimulationResponse> simulate(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days,
            Authentication auth) {
        Skill skill = skillService.getActiveSkills(auth.getName())
                .stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("skill not found"));
        return ResponseEntity.ok(decayService.simulate(skill, days));
    }

    // PUT /api/skills/{id}/decay-rate
    // lets the user set a custom decay rate (λ) for a specific skill
    // overrides the default rate for that skill type
    @PutMapping("/{id}/decay-rate")
    public ResponseEntity<SkillResponse> setDecayRate(
            @PathVariable Long id,
            @RequestParam double rate,
            Authentication auth) {
        Skill updated = skillService.setCustomDecayRate(id, auth.getName(), rate);
        return ResponseEntity.ok(toResponse(updated));
    }

    // converts a Skill entity into a SkillResponse dto
    // this is where all the decay predictions get calculated and attached
    private SkillResponse toResponse(Skill skill) {
        SkillResponse r = new SkillResponse();
        r.setId(skill.getId());
        r.setName(skill.getName());
        r.setSkillType(skill.getSkillType().name());
        r.setHealthScore(skill.getHealthScore());
        r.setInForgettingZone(decayService.isInForgettingZone(skill.getHealthScore()));
        r.setInCemetery(skill.isInCemetery());
        r.setDaysUntilForgettingZone(decayService.daysUntilThreshold(skill, 50.0));
        r.setDaysUntilDead(decayService.daysUntilThreshold(skill, 1.0));
        r.setEstimatedRelearningDays(decayService.estimateRelearningDays(skill));
        r.setProficiencyLevel(skill.getProficiencyLevel());
        return r;
    }

    // converts a CemeteryEntry to a CemeteryResponse dto
    private CemeteryResponse toCemeteryResponse(
            com.devready.devreadybackend.model.CemeteryEntry entry) {
        CemeteryResponse r = new CemeteryResponse();
        r.setId(entry.getId());
        r.setSkillName(entry.getSkillName());
        r.setSkillType(entry.getSkillType().name());
        r.setLastProficiencyLevel(entry.getLastProficiencyLevel());
        r.setDateLost(entry.getDateLost());
        r.setEstimatedRelearningDays(entry.getEstimatedRelearningDays());
        r.setTimesLost(entry.getTimesLost());
        r.setRevived(entry.isRevived());
        return r;
    }

    // converts a Reminder to a ReminderResponse dto
    private ReminderResponse toReminderResponse(
            com.devready.devreadybackend.model.Reminder reminder) {
        ReminderResponse r = new ReminderResponse();
        r.setId(reminder.getId());
        r.setSkillName(reminder.getSkill().getName());
        r.setHealthAtTrigger(reminder.getHealthAtTrigger());
        r.setTriggeredDate(reminder.getTriggeredDate());
        r.setMessage(reminder.getMessage());
        r.setDismissed(reminder.isDismissed());
        return r;
    }
}