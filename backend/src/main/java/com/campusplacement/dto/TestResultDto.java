package com.campusplacement.dto;

import java.time.LocalDateTime;

public class TestResultDto {
    private Long attemptId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TestResultDto() {
    }

    public TestResultDto(Long attemptId, Integer score, Integer totalQuestions, LocalDateTime startTime, LocalDateTime endTime) {
        this.attemptId = attemptId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
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
}

