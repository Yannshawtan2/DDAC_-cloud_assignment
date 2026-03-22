package com.example.patientservice.controller;

import com.example.patientservice.dto.*;
import com.example.patientservice.model.Data;
import com.example.patientservice.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Health Data management
 * 
 * Provides endpoints for CRUD operations on user health data.
 */
@RestController
@RequestMapping("/patient/health-data")
@CrossOrigin(origins = "*")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Get all health data records
     */
    @GetMapping
    public ResponseEntity<List<DataDto>> getAllHealthData() {
        try {
            logger.info("Fetching all health data records");
            List<Data> healthDataList = dataService.getAllHealthData();
            List<DataDto> healthDataDtos = healthDataList.stream()
                    .map(DataDto::fromEntity)
                    .collect(Collectors.toList());

            logger.info("Successfully fetched {} health data records", healthDataDtos.size());
            return ResponseEntity.ok(healthDataDtos);
        } catch (Exception e) {
            logger.error("Error fetching all health data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get health data by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DataDto> getHealthDataById(@PathVariable Long id) {
        try {
            logger.info("Fetching health data with ID: {}", id);
            Optional<Data> healthData = dataService.getHealthDataById(id);

            if (healthData.isPresent()) {
                DataDto healthDataDto = DataDto.fromEntity(healthData.get());
                logger.info("Successfully fetched health data with ID: {}", id);
                return ResponseEntity.ok(healthDataDto);
            } else {
                logger.warn("Health data not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching health data with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get health data by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<DataDto> getHealthDataByUserId(@PathVariable Long userId) {
        try {
            logger.info("Fetching health data for user ID: {}", userId);
            Data healthData = dataService.getHealthDataByUserId(userId);

            if (healthData != null) {
                DataDto healthDataDto = DataDto.fromEntity(healthData);
                logger.info("Successfully fetched health data for user ID: {}", userId);
                return ResponseEntity.ok(healthDataDto);
            } else {
                logger.warn("Health data not found for user ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching health data for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new health data
     */
    @PostMapping
    public ResponseEntity<DataDto> createHealthData(@Valid @RequestBody CreateDataRequest request) {
        try {
            logger.info("Creating new health data for user ID: {}", request.getUserId());

            Data healthData = new Data(
                    request.getUserId(),
                    request.getWeight(),
                    request.getHeight(),
                    request.getWaistCircumference());

            if (request.getBloodPressure() != null) {
                healthData.setBloodPressure(request.getBloodPressure());
            }
            if (request.getBloodGlucoseLevel() != null) {
                healthData.setBloodGlucoseLevel(request.getBloodGlucoseLevel());
            }

            Data savedHealthData = dataService.createHealthData(healthData);
            DataDto healthDataDto = DataDto.fromEntity(savedHealthData);

            logger.info("Successfully created health data with ID: {}", savedHealthData.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(healthDataDto);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for creating health data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating health data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update health data by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<DataDto> updateHealthData(@PathVariable Long id,
            @Valid @RequestBody UpdateDataRequest request) {
        try {
            logger.info("Updating health data with ID: {}", id);

            Data updatedData = new Data();
            updatedData.setWeight(request.getWeight());
            updatedData.setHeight(request.getHeight());
            updatedData.setWaistCircumference(request.getWaistCircumference());
            updatedData.setBloodPressure(request.getBloodPressure());
            updatedData.setBloodGlucoseLevel(request.getBloodGlucoseLevel());

            Data savedHealthData = dataService.updateHealthData(id, updatedData);
            DataDto healthDataDto = DataDto.fromEntity(savedHealthData);

            logger.info("Successfully updated health data with ID: {}", id);
            return ResponseEntity.ok(healthDataDto);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating health data with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating health data with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update health data by user ID
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<DataDto> updateHealthDataByUserId(@PathVariable Long userId,
            @Valid @RequestBody UpdateDataRequest request) {
        try {
            logger.info("Updating health data for user ID: {}", userId);

            Data updatedData = new Data();
            updatedData.setWeight(request.getWeight());
            updatedData.setHeight(request.getHeight());
            updatedData.setWaistCircumference(request.getWaistCircumference());
            updatedData.setBloodPressure(request.getBloodPressure());
            updatedData.setBloodGlucoseLevel(request.getBloodGlucoseLevel());

            Data savedHealthData = dataService.updateHealthDataByUserId(userId, updatedData);
            DataDto healthDataDto = DataDto.fromEntity(savedHealthData);

            logger.info("Successfully updated health data for user ID: {}", userId);
            return ResponseEntity.ok(healthDataDto);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating health data for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating health data for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete health data by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthData(@PathVariable Long id) {
        try {
            logger.info("Deleting health data with ID: {}", id);
            dataService.deleteHealthData(id);
            logger.info("Successfully deleted health data with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Health data not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting health data with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if health data exists for user
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> checkHealthDataExists(@PathVariable Long userId) {
        try {
            logger.info("Checking if health data exists for user ID: {}", userId);
            boolean exists = dataService.existsByUserId(userId);
            logger.info("Health data exists for user ID {}: {}", userId, exists);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            logger.error("Error checking health data existence for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total health data count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalHealthDataCount() {
        try {
            logger.info("Fetching total health data count");
            long count = dataService.getTotalHealthDataCount();
            logger.info("Total health data count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error fetching total health data count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete specific medical reading for a user
     * Allows selective deletion of blood pressure or blood glucose readings
     */
    @PostMapping("/user/{userId}/delete-reading")
    public ResponseEntity<String> deleteMedicalReading(
            @PathVariable Long userId,
            @RequestParam String readingType) {
        try {
            logger.info("Deleting {} reading for user ID: {}", readingType, userId);

            Data healthData = dataService.getHealthDataByUserId(userId);
            if (healthData == null) {
                logger.warn("No health data found for user ID: {}", userId);
                return ResponseEntity.badRequest().body("No health data found");
            }

            switch (readingType.toLowerCase()) {
                case "blood_pressure":
                    healthData.setBloodPressure(null);
                    healthData.setBloodPressureUpdatedAt(null);
                    break;
                case "blood_glucose":
                    healthData.setBloodGlucoseLevel(null);
                    healthData.setBloodGlucoseLevelUpdatedAt(null);
                    break;
                default:
                    logger.warn("Invalid reading type: {}", readingType);
                    return ResponseEntity.badRequest()
                            .body("Invalid reading type. Supported types: blood_pressure, blood_glucose");
            }

            dataService.updateHealthData(healthData.getId(), healthData);
            logger.info("Successfully deleted {} reading for user ID: {}", readingType, userId);
            return ResponseEntity.ok("Reading deleted successfully");

        } catch (Exception e) {
            logger.error("Error deleting {} reading for user ID {}: {}", readingType, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete reading: " + e.getMessage());
        }
    }
}
