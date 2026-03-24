package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.SkillResponse;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillTag;
import com.devready.devreadybackend.service.DecayService;
import com.devready.devreadybackend.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// handles skill tag endpoints
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private final TagService tagService;
    private final DecayService decayService;

    public TagController(TagService tagService, DecayService decayService) {
        this.tagService = tagService;
        this.decayService = decayService;
    }

    // GET /api/tags
    // returns all tags for the logged-in user
    @GetMapping
    public ResponseEntity<List<SkillTag>> getTags(Authentication auth) {
        return ResponseEntity.ok(tagService.getTags(auth.getName()));
    }

    // POST /api/tags?name=work
    // creates a new tag for the logged-in user
    @PostMapping
    public ResponseEntity<SkillTag> createTag(
            @RequestParam String name,
            Authentication auth) {
        return ResponseEntity.ok(tagService.createTag(name, auth.getName()));
    }

    // DELETE /api/tags/{id}
    // deletes a tag
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(
            @PathVariable Long id,
            Authentication auth) {
        tagService.deleteTag(id, auth.getName());
        return ResponseEntity.ok("tag deleted.");
    }

    // POST /api/tags/{tagId}/skills/{skillId}
    // adds a tag to a skill
    @PostMapping("/{tagId}/skills/{skillId}")
    public ResponseEntity<SkillResponse> addTagToSkill(
            @PathVariable Long tagId,
            @PathVariable Long skillId,
            Authentication auth) {
        Skill updated = tagService.addTagToSkill(skillId, tagId, auth.getName());
        return ResponseEntity.ok(toResponse(updated));
    }

    // DELETE /api/tags/{tagId}/skills/{skillId}
    // removes a tag from a skill
    @DeleteMapping("/{tagId}/skills/{skillId}")
    public ResponseEntity<SkillResponse> removeTagFromSkill(
            @PathVariable Long tagId,
            @PathVariable Long skillId,
            Authentication auth) {
        Skill updated = tagService.removeTagFromSkill(skillId, tagId, auth.getName());
        return ResponseEntity.ok(toResponse(updated));
    }

    // GET /api/tags/{id}/skills
    // returns all skills with a specific tag
    @GetMapping("/{id}/skills")
    public ResponseEntity<List<SkillResponse>> getSkillsByTag(
            @PathVariable Long id,
            Authentication auth) {
        List<SkillResponse> skills = tagService.getSkillsByTag(id, auth.getName())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    // converts a Skill entity to a SkillResponse dto
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