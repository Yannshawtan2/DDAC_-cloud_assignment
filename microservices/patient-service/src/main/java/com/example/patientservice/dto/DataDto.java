package com.example.patientservice.dto;

import com.example.patientservice.model.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Data entity
 * 
 * Used for API responses and data serialization.
 */
public class DataDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("weight")
    private Double weight;
    
    @JsonProperty("height")
    private Double height;
    
    @JsonProperty("waistCircumference")
    private Double waistCircumference;
    
    @JsonProperty("bloodPressure")
    private String bloodPressure;
    
    @JsonProperty("bloodGlucoseLevel")
    private Double bloodGlucoseLevel;
    
    @JsonProperty("weightUpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime weightUpdatedAt;
    
    @JsonProperty("heightUpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime heightUpdatedAt;
    
    @JsonProperty("waistCircumferenceUpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime waistCircumferenceUpdatedAt;
    
    @JsonProperty("bloodPressureUpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bloodPressureUpdatedAt;
    
    @JsonProperty("bloodGlucoseLevelUpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bloodGlucoseLevelUpdatedAt;
    
    // Default constructor
    public DataDto() {}
    
    // Constructor with all fields
    public DataDto(Long id, Long userId, Double weight, Double height, 
                   Double waistCircumference, String bloodPressure, 
                   Double bloodGlucoseLevel, LocalDateTime weightUpdatedAt,
                   LocalDateTime heightUpdatedAt, LocalDateTime waistCircumferenceUpdatedAt,
                   LocalDateTime bloodPressureUpdatedAt, LocalDateTime bloodGlucoseLevelUpdatedAt) {
        this.id = id;
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.waistCircumference = waistCircumference;
        this.bloodPressure = bloodPressure;
        this.bloodGlucoseLevel = bloodGlucoseLevel;
        this.weightUpdatedAt = weightUpdatedAt;
        this.heightUpdatedAt = heightUpdatedAt;
        this.waistCircumferenceUpdatedAt = waistCircumferenceUpdatedAt;
        this.bloodPressureUpdatedAt = bloodPressureUpdatedAt;
        this.bloodGlucoseLevelUpdatedAt = bloodGlucoseLevelUpdatedAt;
    }
    
    // Static factory method to create DTO from entity
    public static DataDto fromEntity(Data data) {
        if (data == null) {
            return null;
        }
        
        return new DataDto(
            data.getId(),
            data.getUserId(),
            data.getWeight(),
            data.getHeight(),
            data.getWaistCircumference(),
            data.getBloodPressure(),
            data.getBloodGlucoseLevel(),
            data.getWeightUpdatedAt(),
            data.getHeightUpdatedAt(),
            data.getWaistCircumferenceUpdatedAt(),
            data.getBloodPressureUpdatedAt(),
            data.getBloodGlucoseLevelUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getWeightUpdatedAt() {
        return weightUpdatedAt;
    }
    
    public void setWeightUpdatedAt(LocalDateTime weightUpdatedAt) {
        this.weightUpdatedAt = weightUpdatedAt;
    }
    
    public LocalDateTime getHeightUpdatedAt() {
        return heightUpdatedAt;
    }
    
    public void setHeightUpdatedAt(LocalDateTime heightUpdatedAt) {
        this.heightUpdatedAt = heightUpdatedAt;
    }
    
    public LocalDateTime getWaistCircumferenceUpdatedAt() {
        return waistCircumferenceUpdatedAt;
    }
    
    public void setWaistCircumferenceUpdatedAt(LocalDateTime waistCircumferenceUpdatedAt) {
        this.waistCircumferenceUpdatedAt = waistCircumferenceUpdatedAt;
    }
    
    public LocalDateTime getBloodPressureUpdatedAt() {
        return bloodPressureUpdatedAt;
    }
    
    public void setBloodPressureUpdatedAt(LocalDateTime bloodPressureUpdatedAt) {
        this.bloodPressureUpdatedAt = bloodPressureUpdatedAt;
    }
    
    public LocalDateTime getBloodGlucoseLevelUpdatedAt() {
        return bloodGlucoseLevelUpdatedAt;
    }
    
    public void setBloodGlucoseLevelUpdatedAt(LocalDateTime bloodGlucoseLevelUpdatedAt) {
        this.bloodGlucoseLevelUpdatedAt = bloodGlucoseLevelUpdatedAt;
    }
    
    @Override
    public String toString() {
        return "DataDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", weight=" + weight +
                ", height=" + height +
                ", waistCircumference=" + waistCircumference +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", bloodGlucoseLevel=" + bloodGlucoseLevel +
                ", weightUpdatedAt=" + weightUpdatedAt +
                ", heightUpdatedAt=" + heightUpdatedAt +
                ", waistCircumferenceUpdatedAt=" + waistCircumferenceUpdatedAt +
                ", bloodPressureUpdatedAt=" + bloodPressureUpdatedAt +
                ", bloodGlucoseLevelUpdatedAt=" + bloodGlucoseLevelUpdatedAt +
                '}';
    }
}