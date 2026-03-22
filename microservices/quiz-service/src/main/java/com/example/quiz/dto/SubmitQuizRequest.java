package com.example.quiz.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class SubmitQuizRequest {
    @NotNull(message = "Patient ID is required")
    private String patientId;
    
    @NotNull(message = "Answers are required")
    private Map<Long, Integer> answers; // questionId -> answerValue (1-5)

    // Constructors
    public SubmitQuizRequest() {}

    public SubmitQuizRequest(String patientId, Map<Long, Integer> answers) {
        this.patientId = patientId;
        this.answers = answers;
    }

    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Map<Long, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, Integer> answers) {
        this.answers = answers;
    }
}