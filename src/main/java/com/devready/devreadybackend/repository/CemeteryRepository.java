package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.CemeteryEntry;
import com.devready.devreadybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CemeteryRepository extends JpaRepository<CemeteryEntry, Long> {

    // get all cemetery entries for a specific user
    List<CemeteryEntry> findByUser(User user);

    // check how many times a skill has been lost before
    // used to increment the timesLost counter
    List<CemeteryEntry> findByUserAndSkillName(User user, String skillName);
}