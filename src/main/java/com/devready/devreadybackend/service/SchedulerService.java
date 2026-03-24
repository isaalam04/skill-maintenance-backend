package com.devready.devreadybackend.service;

import com.devready.devreadybackend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

// automatically refreshes all users' skill health scores once per day
// @Scheduled uses a cron expression to run at a fixed time
@Service
public class SchedulerService {

    private final SkillService skillService;
    private final UserRepository userRepository;

    public SchedulerService(SkillService skillService, UserRepository userRepository) {
        this.skillService = skillService;
        this.userRepository = userRepository;
    }

    // runs every day at 8am
    // cron format: second minute hour day month weekday
    @Scheduled(cron = "0 0 8 * * *")
    public void dailyDecayRefresh() {
        // get every registered user and refresh their skills
        userRepository.findAll().forEach(user -> {
            skillService.refreshAllHealthScores(user.getEmail());
        });
    }
}