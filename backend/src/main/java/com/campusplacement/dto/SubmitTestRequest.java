package com.campusplacement.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public class SubmitTestRequest {
    @NotEmpty(message = "Answers cannot be empty")
    @Valid
    private Map<Long, String> answers; // questionId -> selectedOption

    public SubmitTestRequest() {
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}

