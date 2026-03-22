package com.example.diagnosisnotes.dto;

import java.time.LocalDate;

public class DiagnosisNoteDto {
    private Long id;
    private Long appointmentId;
    private String name;
    private String gender;
    private Integer age;
    private LocalDate appointmentDate;
    private String foreignId;
    private Double height;
    private Double weight;
    private Double bloodSugarIndex;
    private String diagnosisNote;

    // Default constructor
    public DiagnosisNoteDto() {}

    // Constructor with all fields
    public DiagnosisNoteDto(Long id, Long appointmentId, String name, String gender, Integer age,
                           LocalDate appointmentDate, String foreignId, Double height, Double weight,
                           Double bloodSugarIndex, String diagnosisNote) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.appointmentDate = appointmentDate;
        this.foreignId = foreignId;
        this.height = height;
        this.weight = weight;
        this.bloodSugarIndex = bloodSugarIndex;
        this.diagnosisNote = diagnosisNote;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

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