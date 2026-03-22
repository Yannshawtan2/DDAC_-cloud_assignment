package com.example.quiz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_response_id", referencedColumnName = "id")
    private QuizResponse quizResponse;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private QuizQuestion question;

    @Column(nullable = false)
    private Integer answerValue; // 1-5 rating

    // Constructors
    public QuizAnswer() {}

    public QuizAnswer(QuizResponse quizResponse, QuizQuestion question, Integer answerValue) {
        this.quizResponse = quizResponse;
        this.question = question;
        this.answerValue = answerValue;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizResponse getQuizResponse() {
        return quizResponse;
    }

    public void setQuizResponse(QuizResponse quizResponse) {
        this.quizResponse = quizResponse;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuizQuestion question) {
        this.question = question;
    }

    public Integer getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(Integer answerValue) {
        this.answerValue = answerValue;
    }
}