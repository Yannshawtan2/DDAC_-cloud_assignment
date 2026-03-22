package com.example.quiz.dto;

import java.time.LocalDateTime;

import com.example.quiz.model.QuizQuestion;

public class QuizQuestionDto {
    private Long id;
    private String questionText;
    private String description;
    private String section;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public QuizQuestionDto() {}

    public QuizQuestionDto(QuizQuestion question) {
        this.id = question.getId();
        this.questionText = question.getQuestionText();
        this.description = question.getDescription();
        this.section = question.getSection();
        this.isActive = question.getIsActive();
        this.createdBy = question.getCreatedBy();
        this.createdAt = question.getCreatedAt();
        this.updatedAt = question.getUpdatedAt();
    }

    // Static factory method
    public static QuizQuestionDto fromEntity(QuizQuestion question) {
        return new QuizQuestionDto(question);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}