package com.example.appointmentservice.dto;

import com.example.appointmentservice.model.Appointment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDto {
    private Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    
    private String reason;
    private String rejectReason;
    private String status;
    private String userId;
    private String doctorId;
    private String diagnosisNote;

    // Default constructor
    public AppointmentDto() {}

    // Constructor
    public AppointmentDto(Long id, LocalDate date, LocalTime time, String reason, 
                         String rejectReason, String status, String userId, 
                         String doctorId, String diagnosisNote) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.rejectReason = rejectReason;
        this.status = status;
        this.userId = userId;
        this.doctorId = doctorId;
        this.diagnosisNote = diagnosisNote;
    }

    // Factory method to create DTO from entity
    public static AppointmentDto fromEntity(Appointment appointment) {
        return new AppointmentDto(
            appointment.getId(),
            appointment.getDate(),
            appointment.getTime(),
            appointment.getReason(),
            appointment.getRejectReason(),
            appointment.getStatus(),
            appointment.getUserId(),
            appointment.getDoctorId(),
            appointment.getDiagnosisNote()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }
}