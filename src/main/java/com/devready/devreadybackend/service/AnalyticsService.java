package com.devready.devreadybackend.service;

import com.devready.devreadybackend.dto.BestPracticeTimeResponse;
import com.devready.devreadybackend.dto.LeaderboardResponse;
import com.devready.devreadybackend.dto.WeeklyReportResponse;
import com.devready.devreadybackend.model.PracticeLog;
import com.devready.devreadybackend.model.Skill;
import com.devready.devreadybackend.model.User;
import com.devready.devreadybackend.repository.CemeteryRepository;
import com.devready.devreadybackend.repository.PracticeLogRepository;
import com.devready.devreadybackend.repository.SkillRepository;
import com.devready.devreadybackend.repository.UserRepository;
import com.devready.devreadybackend.service.DecayService;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// handles all analytics — weekly reports, leaderboard, best practice time
@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final CemeteryRepository cemeteryRepository;
    private final DecayService decayService;

    public AnalyticsService(UserRepository userRepository,
                            SkillRepository skillRepository,
                            PracticeLogRepository practiceLogRepository,
                            CemeteryRepository cemeteryRepository,
                            DecayService decayService) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.cemeteryRepository = cemeteryRepository;
        this.decayService = decayService;
    }

    // helper to get user by email
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));
    }

    // generates a weekly report for the logged-in user
    public WeeklyReportResponse getWeeklyReport(String email) {
        User user = getUser(email);
        List<Skill> active = skillRepository.findByUserAndInCemeteryFalse(user);
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        // count total practice sessions this week across all skills
        int totalSessions = 0;
        int skillsImproved = 0;
        for (Skill skill : active) {
            List<PracticeLog> logs = practiceLogRepository
                    .findBySkillOrderByPracticeDateDesc(skill);
            long sessionsThisWeek = logs.stream()
                    .filter(l -> l.getPracticeDate().isAfter(weekAgo))
                    .count();
            totalSessions += sessionsThisWeek;
            if (sessionsThisWeek > 0) skillsImproved++;
        }

        // count skills lost to cemetery this week
        int skillsLost = (int) cemeteryRepository.findByUser(user).stream()
                .filter(e -> e.getDateLost().isAfter(weekAgo))
                .count();

        // calculate average health score
        double averageHealth = active.isEmpty() ? 0.0 :
                active.stream().mapToDouble(Skill::getHealthScore).average().orElse(0.0);
        averageHealth = Math.round(averageHealth * 10.0) / 10.0;

        // determine overall trend
        String trend;
        if (skillsImproved > skillsLost && averageHealth > 60) {
            trend = "improving";
        } else if (skillsLost > skillsImproved || averageHealth < 40) {
            trend = "declining";
        } else {
            trend = "stable";
        }

        // count sessions by day of week
        Map<String, Integer> sessionsByDay = new LinkedHashMap<>();
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String day : days) sessionsByDay.put(day, 0);

        for (Skill skill : active) {
            practiceLogRepository.findBySkillOrderByPracticeDateDesc(skill).stream()
                    .filter(l -> l.getPracticeDate().isAfter(weekAgo))
                    .forEach(l -> {
                        String dayName = l.getPracticeDate().getDayOfWeek()
                                .toString().toLowerCase();
                        sessionsByDay.merge(dayName, 1, Integer::sum);
                    });
        }

        // find best practice day
        String bestDay = sessionsByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("no data");

        // find most neglected skills (lowest health)
        List<String> mostNeglected = active.stream()
                .sorted(Comparator.comparingDouble(Skill::getHealthScore))
                .limit(3)
                .map(Skill::getName)
                .collect(Collectors.toList());

        // find healthiest skills (highest health)
        List<String> healthiest = active.stream()
                .sorted(Comparator.comparingDouble(Skill::getHealthScore).reversed())
                .limit(3)
                .map(Skill::getName)
                .collect(Collectors.toList());

        return new WeeklyReportResponse(
                totalSessions, skillsImproved, skillsLost,
                averageHealth, trend, bestDay, mostNeglected,
                healthiest, sessionsByDay
        );
    }

    // returns a leaderboard of healthiest and most at-risk skills
    public LeaderboardResponse getLeaderboard(String email) {
        User user = getUser(email);
        List<Skill> active = skillRepository.findByUserAndInCemeteryFalse(user);

        // top 3 healthiest skills
        List<LeaderboardResponse.LeaderboardEntry> healthiest = active.stream()
                .sorted(Comparator.comparingDouble(Skill::getHealthScore).reversed())
                .limit(3)
                .map(s -> new LeaderboardResponse.LeaderboardEntry(
                        s.getName(),
                        s.getHealthScore(),
                        s.getSkillType().name(),
                        decayService.daysUntilThreshold(s, 50.0)
                ))
                .collect(Collectors.toList());

        // top 3 most at risk skills (lowest health, not in cemetery)
        List<LeaderboardResponse.LeaderboardEntry> mostAtRisk = active.stream()
                .sorted(Comparator.comparingDouble(Skill::getHealthScore))
                .limit(3)
                .map(s -> new LeaderboardResponse.LeaderboardEntry(
                        s.getName(),
                        s.getHealthScore(),
                        s.getSkillType().name(),
                        decayService.daysUntilThreshold(s, 50.0)
                ))
                .collect(Collectors.toList());

        return new LeaderboardResponse(healthiest, mostAtRisk);
    }

    // returns best practice time analytics for the logged-in user
    public BestPracticeTimeResponse getBestPracticeTime(String email) {
        User user = getUser(email);
        List<Skill> active = skillRepository.findByUserAndInCemeteryFalse(user);

        // collect all practice logs across all skills
        List<PracticeLog> allLogs = new ArrayList<>();
        for (Skill skill : active) {
            allLogs.addAll(practiceLogRepository.findBySkillOrderByPracticeDateDesc(skill));
        }

        // count sessions by day of week
        Map<String, Integer> sessionsByDay = new LinkedHashMap<>();
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String day : days) sessionsByDay.put(day, 0);

        for (PracticeLog log : allLogs) {
            String dayName = log.getPracticeDate().getDayOfWeek().toString().toLowerCase();
            sessionsByDay.merge(dayName, 1, Integer::sum);
        }

        // find best day
        String bestDay = sessionsByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("no data");

        // calculate current streak
        Set<LocalDate> practiceDates = allLogs.stream()
                .map(PracticeLog::getPracticeDate)
                .collect(Collectors.toSet());

        int currentStreak = 0;
        LocalDate date = LocalDate.now();
        while (practiceDates.contains(date)) {
            currentStreak++;
            date = date.minusDays(1);
        }

        // calculate longest streak ever
        int longestStreak = 0;
        int streak = 0;
        List<LocalDate> sortedDates = practiceDates.stream()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < sortedDates.size(); i++) {
            if (i == 0 || sortedDates.get(i).equals(sortedDates.get(i - 1).plusDays(1))) {
                streak++;
                longestStreak = Math.max(longestStreak, streak);
            } else {
                streak = 1;
            }
        }

        // calculate average sessions per week
        double averageSessionsPerWeek = 0.0;
        if (!allLogs.isEmpty()) {
            LocalDate earliest = sortedDates.get(0);
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                    earliest, LocalDate.now()) + 1;
            double totalWeeks = totalDays / 7.0;
            averageSessionsPerWeek = totalWeeks > 0 ?
                    Math.round((allLogs.size() / totalWeeks) * 10.0) / 10.0 : 0.0;
        }

        return new BestPracticeTimeResponse(
                bestDay, sessionsByDay, currentStreak,
                longestStreak, averageSessionsPerWeek
        );
    }
}