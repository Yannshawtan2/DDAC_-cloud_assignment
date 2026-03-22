package com.example.template.dto;

/**
 * Request DTO for updating existing template entities
 * 
 * TODO: Replace with your actual update request DTO
 * Example: UpdatePatientRequest, UpdateDoctorRequest, etc.
 * 
 * Instructions:
 * 1. Rename class from UpdateTemplateEntityRequest to Update{Entity}Request
 * 2. Add your specific fields that can be updated
 * 3. Make fields optional (nullable) as updates might be partial
 * 4. Add validation annotations if needed
 */
public class UpdateTemplateEntityRequest {
    
    private String name;
    private String description;
    
    // TODO: Add your specific fields here
    // Example fields for different entities:
    
    // For UpdatePatientRequest:
    // private String email;
    // private String phoneNumber;
    // private LocalDate dateOfBirth;
    // private String address;
    // private String medicalHistory;
    // Note: userId should typically not be updatable
    
    // For UpdateDoctorRequest:
    // private String email;
    // private String specialization;
    // private String licenseNumber;
    // private String biography;
    // private Boolean isAvailable;
    // Note: userId should typically not be updatable
    
    // For UpdateDietPlanRequest:
    // private String planType;
    // private LocalDate startDate;
    // private LocalDate endDate;
    // private String instructions;
    // private Boolean isActive;
    // Note: patientId and dieticianId should typically not be updatable
    
    // Constructors
    public UpdateTemplateEntityRequest() {}
    
    public UpdateTemplateEntityRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Method to apply updates to existing entity
    public void updateEntity(com.example.template.model.TemplateEntity entity) {
        if (name != null) {
            entity.setName(name);
        }
        if (description != null) {
            entity.setDescription(description);
        }
        // TODO: Add your specific field updates here
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "UpdateTemplateEntityRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
