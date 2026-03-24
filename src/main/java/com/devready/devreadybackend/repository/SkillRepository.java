package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    // get all active skills for a specific user
    List<Skill> findByUserAndInCemeteryFalse(User user);

    // get all cemetery skills for a specific user
    List<Skill> findByUserAndInCemeteryTrue(User user);

    // find a specific skill that belongs to a specific user
    // the user check prevents one user accessing another user's skills
    Optional<Skill> findByIdAndUser(Long id, User user);
}