package com.example.quiz.dto;

import com.example.quiz.model.QuizResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class QuizResponseDto {
    private Long id;
    private String patientId;
    private Integer totalScore;
    private Integer maxPossibleScore;
    private Double scorePercentage;
    private String scoreCategory;
    private String recommendations;
    private LocalDateTime completedAt;
    private Map<String, Map<String, Integer>> sectionScores;
    private Map<String, String> sectionRecommendations;
    private List<QuizAnswerDto> answers;

    // Constructors
    public QuizResponseDto() {}

    public QuizResponseDto(QuizResponse response) {
        this.id = response.getId();
        this.patientId = response.getPatientId();
        this.totalScore = response.getTotalScore();
        this.maxPossibleScore = response.getMaxPossibleScore();
        this.scorePercentage = response.getScorePercentage();
        this.scoreCategory = response.getScoreCategory();
        this.recommendations = response.getRecommendations();
        this.completedAt = response.getCompletedAt();
    }

    // Static factory method
    public static QuizResponseDto fromEntity(QuizResponse response) {
        return new QuizResponseDto(response);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getMaxPossibleScore() {
        return maxPossibleScore;
    }

    public void setMaxPossibleScore(Integer maxPossibleScore) {
        this.maxPossibleScore = maxPossibleScore;
    }

    public Double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(Double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public String getScoreCategory() {
        return scoreCategory;
    }

    public void setScoreCategory(String scoreCategory) {
        this.scoreCategory = scoreCategory;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Map<String, Map<String, Integer>> getSectionScores() {
        return sectionScores;
    }

    public void setSectionScores(Map<String, Map<String, Integer>> sectionScores) {
        this.sectionScores = sectionScores;
    }

    public Map<String, String> getSectionRecommendations() {
        return sectionRecommendations;
    }

    public void setSectionRecommendations(Map<String, String> sectionRecommendations) {
        this.sectionRecommendations = sectionRecommendations;
    }

    public List<QuizAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerDto> answers) {
        this.answers = answers;
    }
}