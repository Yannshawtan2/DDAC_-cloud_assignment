package com.example.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

/**
 * Request DTO for updating health data
 * 
 * Contains validation rules and field mappings for health data updates.
 * All fields are optional for partial updates.
 */
public class UpdateDataRequest {
    
    @JsonProperty("weight")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @DecimalMax(value = "1000.0", message = "Weight must be less than 1000 kg")
    private Double weight;
    
    @JsonProperty("height")
    @DecimalMin(value = "0.1", message = "Height must be greater than 0")
    @DecimalMax(value = "300.0", message = "Height must be less than 300 cm")
    private Double height;
    
    @JsonProperty("waistCircumference")
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
    public UpdateDataRequest() {}
    
    // Constructor with all fields
    public UpdateDataRequest(Double weight, Double height, Double waistCircumference, 
                            String bloodPressure, Double bloodGlucoseLevel) {
        this.weight = weight;
        this.height = height;
        this.waistCircumference = waistCircumference;
        this.bloodPressure = bloodPressure;
        this.bloodGlucoseLevel = bloodGlucoseLevel;
    }
    
    // Getters and Setters
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
    
    /**
     * Check if the request has any fields to update
     */
    public boolean hasUpdates() {
        return weight != null || height != null || waistCircumference != null || 
               bloodPressure != null || bloodGlucoseLevel != null;
    }
    
    @Override
    public String toString() {
        return "UpdateDataRequest{" +
                "weight=" + weight +
                ", height=" + height +
                ", waistCircumference=" + waistCircumference +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", bloodGlucoseLevel=" + bloodGlucoseLevel +
                '}';
    }
}