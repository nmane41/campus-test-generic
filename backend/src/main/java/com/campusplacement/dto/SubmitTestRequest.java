package com.campusplacement.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public class SubmitTestRequest {
    @NotEmpty(message = "Answers cannot be empty")
    @Valid
    private Map<Long, String> answers; // questionId -> selectedOption

    private Map<Long, Integer> questionTimes; // questionId -> timeTakenSeconds

    public SubmitTestRequest() {
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }

    public Map<Long, Integer> getQuestionTimes() {
        return questionTimes;
    }

    public void setQuestionTimes(Map<Long, Integer> questionTimes) {
        this.questionTimes = questionTimes;
    }
}

