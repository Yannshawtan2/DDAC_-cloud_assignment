package com.example.diagnosisnotes.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "diagnosis_notes")
public class DiagnosisNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id")
    private Long appointmentId;

    private String name;
    private String gender;
    private Integer age; 

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "foreign_id")
    private String foreignId;

    private Double height;
    private Double weight;

    @Column(name = "blood_sugar_index")
    private Double bloodSugarIndex;

    @Column(name = "diagnosis_note", columnDefinition = "TEXT")
    private String diagnosisNote;

    // Default constructor
    public DiagnosisNote() {}

    // Constructor with appointment ID
    public DiagnosisNote(Long appointmentId) {
        this.appointmentId = appointmentId;
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