package com.example.quiz.dto;

import com.example.quiz.model.QuizAnswer;

public class QuizAnswerDto {
    private Long id;
    private Long questionId;
    private String questionText;
    private String section;
    private Integer answerValue;

    // Constructors
    public QuizAnswerDto() {}

    public QuizAnswerDto(QuizAnswer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.questionText = answer.getQuestion().getQuestionText();
        this.section = answer.getQuestion().getSection();
        this.answerValue = answer.getAnswerValue();
    }

    // Static factory method
    public static QuizAnswerDto fromEntity(QuizAnswer answer) {
        return new QuizAnswerDto(answer);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(Integer answerValue) {
        this.answerValue = answerValue;
    }
}