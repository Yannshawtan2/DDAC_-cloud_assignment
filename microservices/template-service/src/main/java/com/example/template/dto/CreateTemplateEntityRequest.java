package com.example.template.dto;

/**
 * Request DTO for creating new template entities
 * 
 * TODO: Replace with your actual create request DTO
 * Example: CreatePatientRequest, CreateDoctorRequest, etc.
 * 
 * Instructions:
 * 1. Rename class from CreateTemplateEntityRequest to Create{Entity}Request
 * 2. Add your specific fields needed for creation
 * 3. Add validation annotations if needed
 * 4. Consider which fields are required vs optional
 */
public class CreateTemplateEntityRequest {
    
    private String name;
    private String description;
    
    // TODO: Add your specific fields here
    // Example fields for different entities:
    
    // For CreatePatientRequest:
    // @NotBlank(message = "Email is required")
    // @Email(message = "Email should be valid")
    // private String email;
    // 
    // @NotBlank(message = "Phone number is required")
    // private String phoneNumber;
    // 
    // @NotNull(message = "Date of birth is required")
    // private LocalDate dateOfBirth;
    // 
    // private String address;
    // private String medicalHistory;
    // private Long userId; // Reference to User entity
    
    // For CreateDoctorRequest:
    // @NotBlank(message = "Email is required")
    // private String email;
    // 
    // @NotBlank(message = "Specialization is required")
    // private String specialization;
    // 
    // @NotBlank(message = "License number is required")
    // private String licenseNumber;
    // 
    // private String biography;
    // private Long userId; // Reference to User entity
    
    // For CreateDietPlanRequest:
    // @NotNull(message = "Patient ID is required")
    // private Long patientId;
    // 
    // @NotNull(message = "Dietician ID is required")
    // private Long dieticianId;
    // 
    // @NotBlank(message = "Plan type is required")
    // private String planType;
    // 
    // @NotNull(message = "Start date is required")
    // private LocalDate startDate;
    // 
    // private LocalDate endDate;
    // private String instructions;
    
    // Constructors
    public CreateTemplateEntityRequest() {}
    
    public CreateTemplateEntityRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Method to convert to Entity
    public com.example.template.model.TemplateEntity toEntity() {
        return new com.example.template.model.TemplateEntity(name, description);
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
        return "CreateTemplateEntityRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
