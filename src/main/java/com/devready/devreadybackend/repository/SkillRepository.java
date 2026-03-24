package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository gives us free database methods like save(), findById(), findAll(), delete()
// we just extend it and spring generates the actual SQL automatically
// the two types in the angle brackets are: the entity class, and the type of its id
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // spring reads the method name and generates the SQL automatically
    // this one translates to: SELECT * FROM skills WHERE in_cemetery = false
    List<Skill> findByInCemeteryFalse();

    // SELECT * FROM skills WHERE in_cemetery = true
    List<Skill> findByInCemeteryTrue();

    // SELECT * FROM skills WHERE health_score < threshold
    // used to find skills approaching the forgetting zone
    List<Skill> findByHealthScoreLessThan(double threshold);
}