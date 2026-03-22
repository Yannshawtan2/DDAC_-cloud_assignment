package com.example.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateQuizQuestionRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    private String description;
    
    private String section;
    
    @NotNull(message = "Created by is required")
    private String createdBy;
    
    private Boolean isActive = true;

    // Constructors
    public CreateQuizQuestionRequest() {}

    public CreateQuizQuestionRequest(String questionText, String description, String section, String createdBy) {
        this.questionText = questionText;
        this.description = description;
        this.section = section;
        this.createdBy = createdBy;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}