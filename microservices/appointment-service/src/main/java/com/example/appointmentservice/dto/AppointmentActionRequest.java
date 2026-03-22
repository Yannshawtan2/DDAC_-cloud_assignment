package com.example.appointmentservice.dto;

import jakarta.validation.constraints.*;

public class AppointmentActionRequest {
    
    @NotBlank(message = "Action is required")
    @Pattern(regexp = "ACCEPT|REJECT", message = "Action must be either ACCEPT or REJECT")
    private String action;
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason; // Used for reject reason or acceptance notes
    
    private String diagnosisNote; // Optional diagnosis note when accepting

    // Default constructor
    public AppointmentActionRequest() {}

    // Constructor
    public AppointmentActionRequest(String action, String reason, String diagnosisNote) {
        this.action = action;
        this.reason = reason;
        this.diagnosisNote = diagnosisNote;
    }

    // Helper methods
    public boolean isAccept() {
        return "ACCEPT".equalsIgnoreCase(action);
    }

    public boolean isReject() {
        return "REJECT".equalsIgnoreCase(action);
    }

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDiagnosisNote() {
        return diagnosisNote;
    }

    public void setDiagnosisNote(String diagnosisNote) {
        this.diagnosisNote = diagnosisNote;
    }
}