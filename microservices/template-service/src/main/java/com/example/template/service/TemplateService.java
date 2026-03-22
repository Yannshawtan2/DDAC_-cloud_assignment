package com.example.template.service;

import com.example.template.model.TemplateEntity;
import com.example.template.repository.TemplateEntityRepository;
import com.example.template.dto.TemplateEntityDto;
import com.example.template.dto.CreateTemplateEntityRequest;
import com.example.template.dto.UpdateTemplateEntityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Template Service for business logic
 * 
 * TODO: Replace with your actual service
 * Example: PatientService, DoctorService, DietPlanService, etc.
 * 
 * Instructions:
 * 1. Rename class from TemplateService to {Entity}Service
 * 2. Update all references to use your entity types
 * 3. Add your specific business logic
 * 4. Add validation rules
 * 5. Handle exceptions appropriately
 * 6. Add logging as needed
 */
@Service
public class TemplateService {

    @Autowired
    private TemplateEntityRepository repository;

    /**
     * Get all entities
     */
    public List<TemplateEntityDto> getAllEntities() {
        return repository.findAll()
                .stream()
                .map(TemplateEntityDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get entity by ID
     */
    public Optional<TemplateEntityDto> getEntityById(Long id) {
        return repository.findById(id)
                .map(TemplateEntityDto::fromEntity);
    }

    /**
     * Create new entity
     */
    public TemplateEntityDto createEntity(CreateTemplateEntityRequest request) {
        // TODO: Add validation logic here
        validateCreateRequest(request);
        
        TemplateEntity entity = request.toEntity();
        TemplateEntity savedEntity = repository.save(entity);
        
        return TemplateEntityDto.fromEntity(savedEntity);
    }

    /**
     * Update existing entity
     */
    public TemplateEntityDto updateEntity(Long id, UpdateTemplateEntityRequest request) {
        Optional<TemplateEntity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            TemplateEntity entity = optionalEntity.get();
            
            // TODO: Add validation logic here
            validateUpdateRequest(request);
            
            // Apply updates
            request.updateEntity(entity);
            
            TemplateEntity savedEntity = repository.save(entity);
            return TemplateEntityDto.fromEntity(savedEntity);
        }
        throw new RuntimeException("Entity not found with id: " + id);
    }

    /**
     * Delete entity
     */
    public void deleteEntity(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Check if entity exists
     */
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * Search entities by name
     */
    public List<TemplateEntityDto> searchEntitiesByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(TemplateEntityDto::fromEntity)
                .collect(Collectors.toList());
    }

    // TODO: Add your specific business methods here
    // Examples for different services:
    
    // For PatientService:
    // public List<PatientDto> getPatientsByUserId(Long userId)
    // public PatientDto getPatientByEmail(String email)
    // public List<PatientDto> getPatientsByAgeRange(int minAge, int maxAge)
    // public void updateMedicalHistory(Long patientId, String medicalHistory)
    
    // For DoctorService:
    // public List<DoctorDto> getDoctorsBySpecialization(String specialization)
    // public List<DoctorDto> getAvailableDoctors()
    // public void updateAvailability(Long doctorId, boolean isAvailable)
    // public DoctorDto getDoctorByLicenseNumber(String licenseNumber)
    
    // For DietPlanService:
    // public List<DietPlanDto> getDietPlansByPatientId(Long patientId)
    // public List<DietPlanDto> getDietPlansByDieticianId(Long dieticianId)
    // public List<DietPlanDto> getActiveDietPlans()
    // public void activateDietPlan(Long planId)
    // public void deactivateDietPlan(Long planId)

    // Private validation methods
    private void validateCreateRequest(CreateTemplateEntityRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        
        // Check for duplicate names if needed
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Entity with name '" + request.getName() + "' already exists");
        }
        
        // TODO: Add your specific validation rules here
    }

    private void validateUpdateRequest(UpdateTemplateEntityRequest request) {
        // TODO: Add your specific validation rules here
        // Example:
        // if (request.getName() != null && request.getName().trim().isEmpty()) {
        //     throw new IllegalArgumentException("Name cannot be empty");
        // }
    }
}
