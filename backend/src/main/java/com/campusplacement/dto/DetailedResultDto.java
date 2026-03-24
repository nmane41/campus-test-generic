package com.campusplacement.dto;

import java.util.List;

public class DetailedResultDto {
    private Long userId;
    private String userName;
    private String email;
    private Integer score;
    private String timeTaken;
    private String startTimeIST;
    private String endTimeIST;
    private List<DetailedAnswerDto> answers;

    public DetailedResultDto() {
    }

    public DetailedResultDto(Long userId, String userName, String email, Integer score, 
                             String timeTaken, String startTimeIST, String endTimeIST, 
                             List<DetailedAnswerDto> answers) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.score = score;
        this.timeTaken = timeTaken;
        this.startTimeIST = startTimeIST;
        this.endTimeIST = endTimeIST;
        this.answers = answers;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
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

    public List<DetailedAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<DetailedAnswerDto> answers) {
        this.answers = answers;
    }
}
