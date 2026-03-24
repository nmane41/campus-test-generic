package com.campusplacement.dto;

import java.util.Map;

public class DetailedAnswerDto {
    private Long questionId;
    private String questionText;
    private Map<String, String> options; // A, B, C, D
    private String correctOption;
    private String selectedOption; // null if not answered
    private String status; // CORRECT, WRONG, NOT_ANSWERED
    private Integer timeTakenSeconds; // Time spent on this question

    public DetailedAnswerDto() {
    }

    public DetailedAnswerDto(Long questionId, String questionText, Map<String, String> options, 
                             String correctOption, String selectedOption, String status) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.selectedOption = selectedOption;
        this.status = status;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(Integer timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }
}
