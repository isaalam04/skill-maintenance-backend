package com.devready.devreadybackend.controller;

import com.devready.devreadybackend.dto.BestPracticeTimeResponse;
import com.devready.devreadybackend.dto.LeaderboardResponse;
import com.devready.devreadybackend.dto.WeeklyReportResponse;
import com.devready.devreadybackend.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// handles analytics endpoints — weekly report, leaderboard, best practice time
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET /api/analytics/weekly-report
    // returns a summary of the user's practice activity over the past 7 days
    // includes sessions logged, skills improved, skills lost, trend, best day
    @GetMapping("/weekly-report")
    public ResponseEntity<WeeklyReportResponse> getWeeklyReport(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getWeeklyReport(auth.getName()));
    }

    // GET /api/analytics/leaderboard
    // returns top 3 healthiest and top 3 most at risk skills
    // helps users see at a glance which skills need attention
    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponse> getLeaderboard(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getLeaderboard(auth.getName()));
    }

    // GET /api/analytics/best-practice-time
    // returns when the user practices most, their current streak, longest streak
    // and average sessions per week
    @GetMapping("/best-practice-time")
    public ResponseEntity<BestPracticeTimeResponse> getBestPracticeTime(Authentication auth) {
        return ResponseEntity.ok(analyticsService.getBestPracticeTime(auth.getName()));
    }
}