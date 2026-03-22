package com.example.template;

import com.example.template.service.TemplateService;
import com.example.template.dto.CreateTemplateEntityRequest;
import com.example.template.dto.TemplateEntityDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Template Service
 * 
 * TODO: Replace with your actual service test
 * Example: PatientServiceTest, DoctorServiceTest, etc.
 * 
 * Instructions:
 * 1. Rename class from TemplateServiceTest to {Entity}ServiceTest
 * 2. Update service reference
 * 3. Add your specific test cases
 * 4. Configure test database if needed
 * 5. Add test data setup/cleanup
 */
@SpringBootTest
@ActiveProfiles("test")
class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Test
    void testCreateEntity() {
        // Given
        CreateTemplateEntityRequest request = new CreateTemplateEntityRequest();
        request.setName("Test Entity");
        request.setDescription("Test Description");

        // When
        TemplateEntityDto result = templateService.createEntity(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Entity", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testGetEntityById() {
        // Given
        CreateTemplateEntityRequest request = new CreateTemplateEntityRequest();
        request.setName("Test Entity 2");
        request.setDescription("Test Description 2");
        TemplateEntityDto created = templateService.createEntity(request);

        // When
        TemplateEntityDto result = templateService.getEntityById(created.getId()).orElse(null);

        // Then
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Test Entity 2", result.getName());
    }

    @Test
    void testGetAllEntities() {
        // Given
        CreateTemplateEntityRequest request1 = new CreateTemplateEntityRequest();
        request1.setName("Entity 1");
        request1.setDescription("Description 1");
        
        CreateTemplateEntityRequest request2 = new CreateTemplateEntityRequest();
        request2.setName("Entity 2");
        request2.setDescription("Description 2");

        templateService.createEntity(request1);
        templateService.createEntity(request2);

        // When
        var entities = templateService.getAllEntities();

        // Then
        assertNotNull(entities);
        assertTrue(entities.size() >= 2);
    }

    // TODO: Add your specific test cases here
    // Examples for different services:
    
    // For PatientServiceTest:
    // @Test
    // void testCreatePatientWithUser() { ... }
    // 
    // @Test
    // void testGetPatientsByUserId() { ... }
    // 
    // @Test
    // void testUpdateMedicalHistory() { ... }
    
    // For DoctorServiceTest:
    // @Test
    // void testCreateDoctorWithSpecialization() { ... }
    // 
    // @Test
    // void testGetDoctorsBySpecialization() { ... }
    // 
    // @Test
    // void testUpdateDoctorAvailability() { ... }
    
    // For DietPlanServiceTest:
    // @Test
    // void testCreateDietPlanForPatient() { ... }
    // 
    // @Test
    // void testGetActiveDietPlans() { ... }
    // 
    // @Test
    // void testDeactivateDietPlan() { ... }
}
