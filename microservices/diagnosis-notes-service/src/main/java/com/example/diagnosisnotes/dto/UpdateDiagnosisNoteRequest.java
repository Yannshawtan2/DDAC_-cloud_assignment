package com.example.diagnosisnotes.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class UpdateDiagnosisNoteRequest {
    private String name;
    private String gender;
    private Integer age;
    private LocalDate appointmentDate;
    private String foreignId;
    private Double height;
    private Double weight;
    private Double bloodSugarIndex;
    
    @NotNull(message = "Diagnosis note is required")
    private String diagnosisNote;

    // Default constructor
    public UpdateDiagnosisNoteRequest() {}

    // Constructor with diagnosis note
    public UpdateDiagnosisNoteRequest(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getBloodSugarIndex() {
        return bloodSugarIndex;
    }

    public void setBloodSugarIndex(Double bloodSugarIndex) {
        this.bloodSugarIndex = bloodSugarIndex;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }
} 