package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.PracticeLog;
import com.devready.devreadybackend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {

    // gets all practice logs for a specific skill, newest first
    // used to calculate consistency score based on recent practice history
    List<PracticeLog> findBySkillOrderByPracticeDateDesc(Skill skill);
}