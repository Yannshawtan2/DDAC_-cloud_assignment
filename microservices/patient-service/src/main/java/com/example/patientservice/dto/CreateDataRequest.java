package com.example.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating new health data
 * 
 * Contains validation rules and field mappings for health data creation.
 */
public class CreateDataRequest {
    
    @JsonProperty("userId")
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @JsonProperty("weight")
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Weight must be less than 1000 kg")
    private Double weight;
    
    @JsonProperty("height")
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.1", message = "Height must be greater than 0")
    @DecimalMax(value = "300.0", message = "Height must be less than 300 cm")
    private Double height;
    
    @JsonProperty("waistCircumference")
    @NotNull(message = "Waist circumference is required")
    @DecimalMin(value = "0.1", message = "Waist circumference must be greater than 0")
    @DecimalMax(value = "500.0", message = "Waist circumference must be less than 500 cm")
    private Double waistCircumference;
    
    @JsonProperty("bloodPressure")
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "Blood pressure must be in format 'systolic/diastolic' (e.g., 120/80)")
    private String bloodPressure;
    
    @JsonProperty("bloodGlucoseLevel")
    @DecimalMin(value = "0.1", message = "Blood glucose level must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Blood glucose level must be less than 1000 mg/dL")
    private Double bloodGlucoseLevel;
    
    // Default constructor
    public CreateDataRequest() {}
    
    // Constructor with required fields
    public CreateDataRequest(Long userId, Double weight, Double height, Double waistCircumference) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.waistCircumference = waistCircumference;
    }
    
    // Constructor with all fields
    public CreateDataRequest(Long userId, Double weight, Double height, 
                            Double waistCircumference, String bloodPressure, 
                            Double bloodGlucoseLevel) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.waistCircumference = waistCircumference;
        this.bloodPressure = bloodPressure;
        this.bloodGlucoseLevel = bloodGlucoseLevel;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getWaistCircumference() {
        return waistCircumference;
    }
    
    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }
    
    public String getBloodPressure() {
        return bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public Double getBloodGlucoseLevel() {
        return bloodGlucoseLevel;
    }
    
    public void setBloodGlucoseLevel(Double bloodGlucoseLevel) {
        this.bloodGlucoseLevel = bloodGlucoseLevel;
    }
    
    @Override
    public String toString() {
        return "CreateDataRequest{" +
                "userId=" + userId +
                ", weight=" + weight +
                ", height=" + height +
                ", waistCircumference=" + waistCircumference +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", bloodGlucoseLevel=" + bloodGlucoseLevel +
                '}';
    }
}