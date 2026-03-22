package com.example.template.repository;

import com.example.template.model.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Template Repository Interface
 * 
 * TODO: Replace TemplateEntity with your actual entity
 * TODO: Add custom query methods specific to your entity
 * 
 * Instructions:
 * 1. Rename from TemplateEntityRepository to {Entity}Repository
 * 2. Update entity type from TemplateEntity to your entity
 * 3. Add custom query methods as needed
 * 4. Consider indexing for performance-critical queries
 */
@Repository
public interface TemplateEntityRepository extends JpaRepository<TemplateEntity, Long> {
    
    // Example custom query methods - replace with your specific needs
    
    /**
     * Find entities by name (case-insensitive)
     */
    List<TemplateEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find entities created after a specific date
     */
    List<TemplateEntity> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find entity by name (exact match)
     */
    Optional<TemplateEntity> findByName(String name);
    
    /**
     * Check if entity exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Custom JPQL query example
     */
    @Query("SELECT t FROM TemplateEntity t WHERE t.description IS NOT NULL AND LENGTH(t.description) > :minLength")
    List<TemplateEntity> findEntitiesWithDescriptionLongerThan(@Param("minLength") int minLength);
    
    /**
     * Native SQL query example
     */
    @Query(value = "SELECT * FROM template_entities WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<TemplateEntity> findEntitiesCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // TODO: Add your specific query methods here
    // Examples for different entities:
    
    // For Patient entity:
    // List<Patient> findByUserId(Long userId);
    // List<Patient> findByDateOfBirthBetween(LocalDate start, LocalDate end);
    // Optional<Patient> findByUserEmail(String email);
    
    // For Doctor entity:
    // List<Doctor> findBySpecialization(String specialization);
    // List<Doctor> findByIsAvailableTrue();
    // Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    // For DietPlan entity:
    // List<DietPlan> findByPatientId(Long patientId);
    // List<DietPlan> findByDieticianId(Long dieticianId);
    // List<DietPlan> findByIsActiveTrue();
    
    // For Quiz entity:
    // List<Quiz> findByCategory(String category);
    // List<Quiz> findByIsPublishedTrue();
    // List<Quiz> findByCreatedByUserId(Long userId);
}
