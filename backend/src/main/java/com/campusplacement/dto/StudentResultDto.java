package com.campusplacement.dto;

import java.time.LocalDateTime;

public class StudentResultDto {
    private Long userId;
    private String username;
    private String email;
    private Long attemptId;
    private Integer score;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long timeTakenSeconds; // Duration in seconds
    private String startTimeIST; // Formatted in IST 12-hour format
    private String endTimeIST; // Formatted in IST 12-hour format
    private String timeTakenFormatted; // Formatted as mm:ss

    public StudentResultDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(Long timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public String getStartTimeIST() {
        return startTimeIST;
    }

    public void setStartTimeIST(String startTimeIST) {
        this.startTimeIST = startTimeIST;
    }

    public String getEndTimeIST() {
        return endTimeIST;
    }

    public void setEndTimeIST(String endTimeIST) {
        this.endTimeIST = endTimeIST;
    }

    public String getTimeTakenFormatted() {
        return timeTakenFormatted;
    }

    public void setTimeTakenFormatted(String timeTakenFormatted) {
        this.timeTakenFormatted = timeTakenFormatted;
    }
}

