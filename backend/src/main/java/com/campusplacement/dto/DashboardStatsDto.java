package com.campusplacement.dto;

public class DashboardStatsDto {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalTestAttempts;
    private Double averageScore;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(Long totalUsers, Long activeUsers, Long totalTestAttempts, Double averageScore) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalTestAttempts = totalTestAttempts;
        this.averageScore = averageScore;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getTotalTestAttempts() {
        return totalTestAttempts;
    }

    public void setTotalTestAttempts(Long totalTestAttempts) {
        this.totalTestAttempts = totalTestAttempts;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}

