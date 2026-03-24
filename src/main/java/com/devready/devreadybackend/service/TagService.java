package com.devready.devreadybackend.service;

import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillTag;
import com.devready.devreadybackend.model.User;
import com.devready.devreadybackend.repository.SkillRepository;
import com.devready.devreadybackend.repository.SkillTagRepository;
import com.devready.devreadybackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

// handles skill tag creation and assignment
@Service
public class TagService {

    private final SkillTagRepository tagRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public TagService(SkillTagRepository tagRepository,
                      SkillRepository skillRepository,
                      UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    // helper to get user by email
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));
    }

    // get all tags for this user
    public List<SkillTag> getTags(String email) {
        User user = getUser(email);
        return tagRepository.findByUser(user);
    }

    // create a new tag for this user
    // if the tag already exists, return the existing one
    public SkillTag createTag(String name, String email) {
        User user = getUser(email);

        // check if tag already exists for this user
        return tagRepository.findByNameAndUser(name, user)
                .orElseGet(() -> {
                    SkillTag tag = new SkillTag();
                    tag.setName(name.toLowerCase().trim());
                    tag.setUser(user);
                    return tagRepository.save(tag);
                });
    }

    // add a tag to a skill
    public Skill addTagToSkill(Long skillId, Long tagId, String email) {
        User user = getUser(email);

        Skill skill = skillRepository.findByIdAndUser(skillId, user)
                .orElseThrow(() -> new RuntimeException("skill not found or access denied"));

        SkillTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("tag not found"));

        if (!tag.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("access denied");
        }

        // add tag if not already present
        if (!skill.getTags().contains(tag)) {
            skill.getTags().add(tag);
            skillRepository.save(skill);
        }

        return skill;
    }

    // remove a tag from a skill
    public Skill removeTagFromSkill(Long skillId, Long tagId, String email) {
        User user = getUser(email);

        Skill skill = skillRepository.findByIdAndUser(skillId, user)
                .orElseThrow(() -> new RuntimeException("skill not found or access denied"));

        skill.getTags().removeIf(t -> t.getId().equals(tagId));
        return skillRepository.save(skill);
    }

    // delete a tag entirely
    public void deleteTag(Long tagId, String email) {
        User user = getUser(email);
        SkillTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("tag not found"));

        if (!tag.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("access denied");
        }

        tagRepository.delete(tag);
    }

    // get all skills with a specific tag
    public List<Skill> getSkillsByTag(Long tagId, String email) {
        User user = getUser(email);
        SkillTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("tag not found"));

        if (!tag.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("access denied");
        }

        return skillRepository.findByUserAndInCemeteryFalse(user).stream()
                .filter(s -> s.getTags().stream().anyMatch(t -> t.getId().equals(tagId)))
                .collect(java.util.stream.Collectors.toList());
    }
}