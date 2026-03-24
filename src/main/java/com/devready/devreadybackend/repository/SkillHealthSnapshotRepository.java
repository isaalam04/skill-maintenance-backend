package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.SkillHealthSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillHealthSnapshotRepository extends JpaRepository<SkillHealthSnapshot, Long> {

    // get all snapshots for a specific skill ordered by date ascending
    // used to draw the health-over-time chart
    List<SkillHealthSnapshot> findBySkillOrderBySnapshotDateAsc(Skill skill);
}