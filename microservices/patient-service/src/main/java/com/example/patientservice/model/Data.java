package com.example.patientservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PatientData")
public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDateTime weightUpdatedAt;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private LocalDateTime heightUpdatedAt;

    @Column(nullable = false)
    private Double waistCircumference;

    @Column(nullable = false)
    private LocalDateTime waistCircumferenceUpdatedAt;

    @Column(nullable = true)
    private String bloodPressure;

    @Column(nullable = true)
    private LocalDateTime bloodPressureUpdatedAt;

    @Column(nullable = true)
    private Double bloodGlucoseLevel;

    @Column(nullable = true)
    private LocalDateTime bloodGlucoseLevelUpdatedAt;

    public Data() {
    }

    public Data(Long userId, Double weight, Double height, Double waistCircumference) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.waistCircumference = waistCircumference;

        LocalDateTime now = LocalDateTime.now();
        this.weightUpdatedAt = now;
        this.heightUpdatedAt = now;
        this.waistCircumferenceUpdatedAt = now;
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
        this.weightUpdatedAt = LocalDateTime.now();
    }

    public LocalDateTime getWeightUpdatedAt() {
        return weightUpdatedAt;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
        this.heightUpdatedAt = LocalDateTime.now();
    }

    public LocalDateTime getHeightUpdatedAt() {
        return heightUpdatedAt;
    }

    public Double getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
        this.waistCircumferenceUpdatedAt = LocalDateTime.now();
    }

    public LocalDateTime getWaistCircumferenceUpdatedAt() {
        return waistCircumferenceUpdatedAt;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
        this.bloodPressureUpdatedAt = LocalDateTime.now();
    }

    public LocalDateTime getBloodPressureUpdatedAt() {
        return bloodPressureUpdatedAt;
    }

    public void setBloodPressureUpdatedAt(LocalDateTime bloodPressureUpdatedAt) {
        this.bloodPressureUpdatedAt = bloodPressureUpdatedAt;
    }

    public Double getBloodGlucoseLevel() {
        return bloodGlucoseLevel;
    }

    public void setBloodGlucoseLevel(Double bloodGlucoseLevel) {
        this.bloodGlucoseLevel = bloodGlucoseLevel;
        this.bloodGlucoseLevelUpdatedAt = LocalDateTime.now();
    }

    public LocalDateTime getBloodGlucoseLevelUpdatedAt() {
        return bloodGlucoseLevelUpdatedAt;
    }

    public void setBloodGlucoseLevelUpdatedAt(LocalDateTime bloodGlucoseLevelUpdatedAt) {
        this.bloodGlucoseLevelUpdatedAt = bloodGlucoseLevelUpdatedAt;
    }
}