package com.example.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateAppointmentRequest {
    
    @NotNull(message = "Date is required")
    @Future(message = "Appointment date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @NotNull(message = "Time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    // Default constructor
    public CreateAppointmentRequest() {}

    // Constructor
    public CreateAppointmentRequest(LocalDate date, LocalTime time, String reason, 
                                  String userId, String doctorId) {
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.userId = userId;
        this.doctorId = doctorId;
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
}