package com.example.treatmentplan.dto;

import com.example.treatmentplan.model.TreatmentPlan;
import java.time.LocalDateTime;

public class TreatmentPlanDto {
    private Long id;
    private String title;
    private String description;
    private Long patientId;
    private Long doctorId;
    private String medication;
    private String dosage;
    private String frequency;
    private String instructions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TreatmentPlan.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    
    // These will be populated from User Service API calls
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String doctorEmail;

    public TreatmentPlanDto() {
    }

    public TreatmentPlanDto(Long id, String title, String description, Long patientId, Long doctorId, 
                           TreatmentPlan.Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public TreatmentPlan.Status getStatus() {
        return status;
    }

    public void setStatus(TreatmentPlan.Status status) {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    @Override
    public String toString() {
        return "TreatmentPlanDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}