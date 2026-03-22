package com.example.quiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_responses")
public class QuizResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id")
    private String patientId; // Patient identifier (email or user ID)

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer maxPossibleScore;

    @Column(nullable = false)
    private Double scorePercentage;

    @Column(length = 50)
    private String scoreCategory; // Poor, Fair, Good, Very Good, Excellent

    @Column(length = 1000)
    private String recommendations;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "quizResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizAnswer> answers;

    // Constructors
    public QuizResponse() {
        this.completedAt = LocalDateTime.now();
    }

    public QuizResponse(String patientId, Integer totalScore, Integer maxPossibleScore) {
        this();
        this.patientId = patientId;
        this.totalScore = totalScore;
        this.maxPossibleScore = maxPossibleScore;
        this.scorePercentage = (double) totalScore / maxPossibleScore * 100;
        this.scoreCategory = calculateScoreCategory(this.scorePercentage);
        this.recommendations = generateRecommendations(this.scoreCategory);
    }

    // Helper methods
    private String calculateScoreCategory(Double percentage) {
        if (percentage >= 90) return "Excellent";
        if (percentage >= 80) return "Very Good";
        if (percentage >= 70) return "Good";
        if (percentage >= 60) return "Fair";
        return "Needs Improvement";
    }

    private String generateRecommendations(String category) {
        switch (category) {
            case "Excellent":
                return "Great job! You're maintaining excellent diabetes diet habits. Keep up the good work!";
            case "Very Good":
                return "You're doing very well with your diabetes management. Minor adjustments could help you reach excellence.";
            case "Good":
                return "You have good diabetes diet habits. Consider consulting with your dietician for personalized improvements.";
            case "Fair":
                return "There's room for improvement in your diabetes diet management. Schedule a consultation with your dietician.";
            default:
                return "Your diabetes diet habits need attention. Please schedule an appointment with your dietician for a comprehensive plan.";
        }
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

    public List<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }
}