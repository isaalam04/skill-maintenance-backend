package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.SkillTag;
import com.devready.devreadybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {

    // get all tags created by a specific user
    List<SkillTag> findByUser(User user);

    // find a specific tag by name for a specific user
    // used to avoid creating duplicate tags
    Optional<SkillTag> findByNameAndUser(String name, User user);
}