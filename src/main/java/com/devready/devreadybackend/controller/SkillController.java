package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.SkillResponse;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.service.DecayService;
import com.devready.devreadybackend.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// @RestController means this class handles http requests and returns JSON automatically
// @RequestMapping sets the base url path for all endpoints in this class
// @CrossOrigin allows the React frontend to call this api from a different port
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
    // returns all active skills with their current decay information
    @GetMapping
    public List<SkillResponse> getActiveSkills() {
        return skillService.getActiveSkills()
                .stream()
                .map(this::toResponse) // converts each Skill to a SkillResponse
                .collect(Collectors.toList());
    }

    // GET /api/skills/cemetery
    // returns all skills that have decayed to zero
    @GetMapping("/cemetery")
    public List<SkillResponse> getCemeterySkills() {
        return skillService.getCemeterySkills()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // POST /api/skills
    // adds a new skill — expects a JSON body with name, skillType, proficiencyLevel
    @PostMapping
    public ResponseEntity<SkillResponse> addSkill(@RequestBody Skill skill) {
        Skill saved = skillService.addSkill(skill);
        return ResponseEntity.ok(toResponse(saved));
    }

    // POST /api/skills/{id}/practice
    // logs a practice session for a specific skill
    // durationMinutes and notes are optional query parameters
    @PostMapping("/{id}/practice")
    public ResponseEntity<SkillResponse> logPractice(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int durationMinutes,
            @RequestParam(defaultValue = "") String notes) {
        Skill updated = skillService.logPractice(id, durationMinutes, notes);
        return ResponseEntity.ok(toResponse(updated));
    }

    // POST /api/skills/refresh
    // recalculates health scores for all active skills
    // call this once a day to keep scores up to date
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAll() {
        skillService.refreshAllHealthScores();
        return ResponseEntity.ok("all health scores refreshed.");
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
}