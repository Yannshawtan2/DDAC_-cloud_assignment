package com.example.template.dto;

import java.time.LocalDateTime;

/**
 * Template Entity DTO for API responses
 * 
 * TODO: Replace with your actual entity DTO
 * Example: PatientDto, DoctorDto, DietPlanDto, etc.
 * 
 * Instructions:
 * 1. Rename class from TemplateEntityDto to {Entity}Dto
 * 2. Add your specific fields
 * 3. Update constructor and getters/setters
 * 4. Consider which fields should be exposed in API responses
 */
public class TemplateEntityDto {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // TODO: Add your specific fields here
    // Example fields for different entities:
    
    // For PatientDto:
    // private String email;
    // private String phoneNumber;
    // private LocalDate dateOfBirth;
    // private String address;
    // private String medicalHistory;
    
    // For DoctorDto:
    // private String email;
    // private String specialization;
    // private String licenseNumber;
    // private boolean isAvailable;
    // private String biography;
    
    // For DietPlanDto:
    // private Long patientId;
    // private Long dieticianId;
    // private String planType;
    // private LocalDate startDate;
    // private LocalDate endDate;
    // private boolean isActive;
    
    // Constructors
    public TemplateEntityDto() {}
    
    public TemplateEntityDto(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Static factory method for creating DTO from Entity
    public static TemplateEntityDto fromEntity(com.example.template.model.TemplateEntity entity) {
        return new TemplateEntityDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    @Override
    public String toString() {
        return "TemplateEntityDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
