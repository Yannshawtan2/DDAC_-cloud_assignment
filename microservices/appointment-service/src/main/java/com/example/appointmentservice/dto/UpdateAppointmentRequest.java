package com.example.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateAppointmentRequest {
    
    @Future(message = "Appointment date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    @Size(max = 500, message = "Reject reason must not exceed 500 characters")
    private String rejectReason;
    
    @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED|COMPLETED", message = "Status must be PENDING, CONFIRMED, CANCELLED, or COMPLETED")
    private String status;
    
    private String diagnosisNote;

    // Default constructor
    public UpdateAppointmentRequest() {}

    // Constructor
    public UpdateAppointmentRequest(LocalDate date, LocalTime time, String reason, 
                                  String rejectReason, String status, String diagnosisNote) {
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.rejectReason = rejectReason;
        this.status = status;
        this.diagnosisNote = diagnosisNote;
    }

    // Helper methods to check if fields are being updated
    public boolean hasDateUpdate() {
        return date != null;
    }

    public boolean hasTimeUpdate() {
        return time != null;
    }

    public boolean hasReasonUpdate() {
        return reason != null && !reason.trim().isEmpty();
    }

    public boolean hasRejectReasonUpdate() {
        return rejectReason != null;
    }

    public boolean hasStatusUpdate() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasDiagnosisNoteUpdate() {
        return diagnosisNote != null;
    }

    // Getters and Setters
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

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }
}