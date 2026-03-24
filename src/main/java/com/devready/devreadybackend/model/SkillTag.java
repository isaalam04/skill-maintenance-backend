package com.devready.devreadybackend.model;

import jakarta.persistence.*;
import java.util.List;

// represents a tag/category that can be applied to skills
// e.g. "work", "hobbies", "languages"
// one tag can belong to many skills, one skill can have many tags
@Entity
@Table(name = "skill_tags")
public class SkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the tag name e.g. "work", "hobbies", "languages"
    private String name;

    // the user who created this tag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // all the skills this tag is applied to
    @ManyToMany(mappedBy = "tags")
    private List<Skill> skills;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
}