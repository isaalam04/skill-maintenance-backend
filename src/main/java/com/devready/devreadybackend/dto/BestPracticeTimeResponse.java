package com.devready.devreadybackend.dto;

import java.util.Map;

// returned by GET /api/users/best-practice-time
// tells the user when they practice most so they can plan their sessions
public class BestPracticeTimeResponse {

    private String bestDay;             // e.g. "monday"
    private Map<String, Integer> sessionsByDay;   // sessions per day of week
    private int currentStreak;          // current consecutive days streak
    private int longestStreak;          // longest streak ever achieved
    private double averageSessionsPerWeek; // average weekly practice frequency

    public BestPracticeTimeResponse(String bestDay,
                                    Map<String, Integer> sessionsByDay,
                                    int currentStreak,
                                    int longestStreak,
                                    double averageSessionsPerWeek) {
        this.bestDay = bestDay;
        this.sessionsByDay = sessionsByDay;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.averageSessionsPerWeek = averageSessionsPerWeek;
    }

    public String getBestDay() { return bestDay; }
    public Map<String, Integer> getSessionsByDay() { return sessionsByDay; }
    public int getCurrentStreak() { return currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public double getAverageSessionsPerWeek() { return averageSessionsPerWeek; }
}