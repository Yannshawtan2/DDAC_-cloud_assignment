package com.example.quiz.dto;

public class UpdateQuizQuestionRequest {
    private String questionText;
    private String description;
    private String section;
    private Boolean isActive;

    // Constructors
    public UpdateQuizQuestionRequest() {}

    public UpdateQuizQuestionRequest(String questionText, String description, String section, Boolean isActive) {
        this.questionText = questionText;
        this.description = description;
        this.section = section;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}