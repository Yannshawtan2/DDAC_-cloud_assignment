package com.example.dietplan.dto;

import com.example.dietplan.model.DietPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DietPlanDto {
    private Long id;
    private String title;
    private String description;
    private Long patientId;
    private Long dietitianId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String breakfast;
    private String lunch;
    private String dinner;
    private String snacks;
    private Integer dailyCalories;
    private String specialInstructions;
    private String dietaryRestrictions;
    private DietPlan.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DietPlanDto() {
    }

    public DietPlanDto(Long id, String title, String description, Long patientId, Long dietitianId,
                       LocalDate startDate, LocalDate endDate, String breakfast, String lunch, String dinner,
                       String snacks, Integer dailyCalories, String specialInstructions, String dietaryRestrictions,
                       DietPlan.Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.patientId = patientId;
        this.dietitianId = dietitianId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snacks = snacks;
        this.dailyCalories = dailyCalories;
        this.specialInstructions = specialInstructions;
        this.dietaryRestrictions = dietaryRestrictions;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDietitianId() {
        return dietitianId;
    }

    public void setDietitianId(Long dietitianId) {
        this.dietitianId = dietitianId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getSnacks() {
        return snacks;
    }

    public void setSnacks(String snacks) {
        this.snacks = snacks;
    }

    public Integer getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(Integer dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public DietPlan.Status getStatus() {
        return status;
    }

    public void setStatus(DietPlan.Status status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "DietPlanDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", patientId=" + patientId +
                ", dietitianId=" + dietitianId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}