package com.example.dietplan.dto;

import com.example.dietplan.model.DietPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class CreateDietPlanRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be positive")
    private Long patientId;
    
    @NotNull(message = "Dietitian ID is required")
    @Positive(message = "Dietitian ID must be positive")
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
    private DietPlan.Status status = DietPlan.Status.ACTIVE;

    public CreateDietPlanRequest() {
    }

    public CreateDietPlanRequest(String title, String description, Long patientId, Long dietitianId) {
        this.title = title;
        this.description = description;
        this.patientId = patientId;
        this.dietitianId = dietitianId;
    }

    // Getters and setters
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
}