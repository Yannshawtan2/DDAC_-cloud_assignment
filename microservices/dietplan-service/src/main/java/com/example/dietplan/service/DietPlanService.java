package com.example.dietplan.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dietplan.dto.CreateDietPlanRequest;
import com.example.dietplan.dto.DietPlanDto;
import com.example.dietplan.dto.UpdateDietPlanRequest;
import com.example.dietplan.dto.UserDto;
import com.example.dietplan.model.DietPlan;
import com.example.dietplan.repository.DietPlanRepository;

@Service
public class DietPlanService {
    private static final Logger logger = LoggerFactory.getLogger(DietPlanService.class);

    @Autowired
    private DietPlanRepository dietPlanRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    public DietPlanDto createDietPlan(CreateDietPlanRequest request) {
        logger.info("Creating diet plan with title: {}", request.getTitle());
        
        // Validate that patient exists and has correct role
        if (!userServiceClient.validateUser(request.getPatientId(), "PATIENT")) {
            logger.warn("Invalid patient ID: {}", request.getPatientId());
            throw new IllegalArgumentException("Invalid patient ID or user is not a patient");
        }

        // Validate that dietitian exists and has correct role
        if (!userServiceClient.validateUser(request.getDietitianId(), "DIETICIAN")) {
            logger.warn("Invalid dietitian ID: {}", request.getDietitianId());
            throw new IllegalArgumentException("Invalid dietitian ID or user is not a dietician");
        }

        try {
            DietPlan dietPlan = new DietPlan();
            dietPlan.setTitle(request.getTitle());
            dietPlan.setDescription(request.getDescription());
            dietPlan.setPatientId(request.getPatientId());
            dietPlan.setDietitianId(request.getDietitianId());
            dietPlan.setStartDate(request.getStartDate());
            dietPlan.setEndDate(request.getEndDate());
            dietPlan.setBreakfast(request.getBreakfast());
            dietPlan.setLunch(request.getLunch());
            dietPlan.setDinner(request.getDinner());
            dietPlan.setSnacks(request.getSnacks());
            dietPlan.setDailyCalories(request.getDailyCalories());
            dietPlan.setSpecialInstructions(request.getSpecialInstructions());
            dietPlan.setDietaryRestrictions(request.getDietaryRestrictions());
            dietPlan.setStatus(request.getStatus());
            dietPlan.setCreatedAt(LocalDateTime.now());
            dietPlan.setUpdatedAt(LocalDateTime.now());

            DietPlan savedDietPlan = dietPlanRepository.save(dietPlan);
            logger.info("Successfully created diet plan with ID: {}", savedDietPlan.getId());
            
            return convertToDto(savedDietPlan);
        } catch (Exception e) {
            logger.error("Error creating diet plan: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create diet plan", e);
        }
    }

    public DietPlanDto getDietPlanById(Long id) {
        logger.info("Retrieving diet plan with ID: {}", id);
        
        Optional<DietPlan> dietPlan = dietPlanRepository.findById(id);
        if (dietPlan.isPresent()) {
            return convertToDto(dietPlan.get());
        } else {
            logger.warn("Diet plan not found with ID: {}", id);
            throw new IllegalArgumentException("Diet plan not found");
        }
    }

    public List<DietPlanDto> getAllDietPlans() {
        logger.info("Retrieving all diet plans");
        
        List<DietPlan> dietPlans = dietPlanRepository.findAll();
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DietPlanDto> getDietPlansByPatientId(Long patientId) {
        logger.info("Retrieving diet plans for patient ID: {}", patientId);
        
        // Validate that patient exists and has correct role
        if (!userServiceClient.validateUser(patientId, "PATIENT")) {
            logger.warn("Invalid patient ID: {}", patientId);
            throw new IllegalArgumentException("Invalid patient ID or user is not a patient");
        }

        List<DietPlan> dietPlans = dietPlanRepository.findByPatientId(patientId);
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DietPlanDto> getDietPlansByDietitianId(Long dietitianId) {
        logger.info("Retrieving diet plans for dietitian ID: {}", dietitianId);
        
        // Validate that dietitian exists and has correct role
        if (!userServiceClient.validateUser(dietitianId, "DIETICIAN")) {
            logger.warn("Invalid dietitian ID: {}", dietitianId);
            throw new IllegalArgumentException("Invalid dietitian ID or user is not a dietician");
        }

        List<DietPlan> dietPlans = dietPlanRepository.findByDietitianId(dietitianId);
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DietPlanDto> getDietPlansByStatus(DietPlan.Status status) {
        logger.info("Retrieving diet plans with status: {}", status);
        
        List<DietPlan> dietPlans = dietPlanRepository.findByStatus(status);
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DietPlanDto> getDietPlansByDietitianIdAndStatus(Long dietitianId, DietPlan.Status status) {
        logger.info("Retrieving diet plans for dietitian ID: {} with status: {}", dietitianId, status);
        
        List<DietPlan> dietPlans = dietPlanRepository.findByDietitianIdAndStatus(dietitianId, status);
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DietPlanDto> getDietPlansByPatientIdAndStatus(Long patientId, DietPlan.Status status) {
        logger.info("Retrieving diet plans for patient ID: {} with status: {}", patientId, status);
        
        List<DietPlan> dietPlans = dietPlanRepository.findByPatientIdAndStatus(patientId, status);
        return dietPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DietPlanDto updateDietPlan(Long id, UpdateDietPlanRequest request) {
        logger.info("Updating diet plan with ID: {}", id);
        
        Optional<DietPlan> existingDietPlanOpt = dietPlanRepository.findById(id);
        if (!existingDietPlanOpt.isPresent()) {
            logger.warn("Diet plan not found with ID: {}", id);
            throw new IllegalArgumentException("Diet plan not found");
        }

        DietPlan existingDietPlan = existingDietPlanOpt.get();

        // Validate patient if provided
        if (request.getPatientId() != null) {
            if (!userServiceClient.validateUser(request.getPatientId(), "PATIENT")) {
                logger.warn("Invalid patient ID: {}", request.getPatientId());
                throw new IllegalArgumentException("Invalid patient ID or user is not a patient");
            }
        }

        try {
            // Update diet plan information
            existingDietPlan.setTitle(request.getTitle());
            existingDietPlan.setDescription(request.getDescription());
            if (request.getPatientId() != null) {
                existingDietPlan.setPatientId(request.getPatientId());
            }
            existingDietPlan.setStartDate(request.getStartDate());
            existingDietPlan.setEndDate(request.getEndDate());
            existingDietPlan.setBreakfast(request.getBreakfast());
            existingDietPlan.setLunch(request.getLunch());
            existingDietPlan.setDinner(request.getDinner());
            existingDietPlan.setSnacks(request.getSnacks());
            existingDietPlan.setDailyCalories(request.getDailyCalories());
            existingDietPlan.setSpecialInstructions(request.getSpecialInstructions());
            existingDietPlan.setDietaryRestrictions(request.getDietaryRestrictions());
            if (request.getStatus() != null) {
                existingDietPlan.setStatus(request.getStatus());
            }
            existingDietPlan.setUpdatedAt(LocalDateTime.now());

            DietPlan updatedDietPlan = dietPlanRepository.save(existingDietPlan);
            logger.info("Successfully updated diet plan with ID: {}", updatedDietPlan.getId());
            
            return convertToDto(updatedDietPlan);
        } catch (Exception e) {
            logger.error("Error updating diet plan: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update diet plan", e);
        }
    }

    public DietPlanDto updateDietPlanStatus(Long id, DietPlan.Status status) {
        logger.info("Updating diet plan status for ID: {} to status: {}", id, status);
        
        Optional<DietPlan> dietPlanOpt = dietPlanRepository.findById(id);
        if (!dietPlanOpt.isPresent()) {
            logger.warn("Diet plan not found with ID: {}", id);
            throw new IllegalArgumentException("Diet plan not found");
        }

        try {
            DietPlan dietPlan = dietPlanOpt.get();
            dietPlan.setStatus(status);
            dietPlan.setUpdatedAt(LocalDateTime.now());
            
            DietPlan updatedDietPlan = dietPlanRepository.save(dietPlan);
            logger.info("Successfully updated diet plan status with ID: {}", updatedDietPlan.getId());
            
            return convertToDto(updatedDietPlan);
        } catch (Exception e) {
            logger.error("Error updating diet plan status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update diet plan status", e);
        }
    }

    public void deleteDietPlan(Long id) {
        logger.info("Deleting diet plan with ID: {}", id);
        
        Optional<DietPlan> dietPlan = dietPlanRepository.findById(id);
        if (!dietPlan.isPresent()) {
            logger.warn("Diet plan not found with ID: {}", id);
            throw new IllegalArgumentException("Diet plan not found");
        }

        try {
            dietPlanRepository.deleteById(id);
            logger.info("Successfully deleted diet plan with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting diet plan: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete diet plan", e);
        }
    }

    public Long getDietPlanCountByDietitianId(Long dietitianId) {
        logger.info("Getting diet plan count for dietitian ID: {}", dietitianId);
        return dietPlanRepository.countByDietitianId(dietitianId);
    }

    public Long getDietPlanCountByDietitianIdAndStatus(Long dietitianId, DietPlan.Status status) {
        logger.info("Getting diet plan count for dietitian ID: {} with status: {}", dietitianId, status);
        return dietPlanRepository.countByDietitianIdAndStatus(dietitianId, status);
    }

    public Long getDietPlanCountByPatientId(Long patientId) {
        logger.info("Getting diet plan count for patient ID: {}", patientId);
        return dietPlanRepository.countByPatientId(patientId);
    }

    public List<Long> getDistinctPatientIdsByDietitianId(Long dietitianId) {
        logger.info("Getting distinct patient IDs for dietitian ID: {}", dietitianId);
        return dietPlanRepository.findDistinctPatientIdsByDietitianId(dietitianId);
    }

    public List<DietPlanDto> searchDietPlans(String patientName, DietPlan.Status status, Long dietitianId) {
        logger.info("Searching diet plans with patientName: {}, status: {}, dietitianId: {}", 
                   patientName, status, dietitianId);
        
        try {
            List<DietPlan> dietPlans = new ArrayList<>();
            
            if (patientName != null && !patientName.trim().isEmpty()) {
                // Search by patient name using user service
                List<UserDto> users = userServiceClient.getAllUsers();
                List<Long> matchingPatientIds = users.stream()
                        .filter(user -> user.isPatient() && 
                               user.getName().toLowerCase().contains(patientName.toLowerCase()))
                        .map(UserDto::getId)
                        .toList();
                
                if (!matchingPatientIds.isEmpty()) {
                    for (Long patientId : matchingPatientIds) {
                        dietPlans.addAll(dietPlanRepository.findByPatientId(patientId));
                    }
                }
            } else {
                // Get all diet plans if no patient name filter
                dietPlans = dietPlanRepository.findAll();
            }
            
            // Filter by dietitian if provided
            if (dietitianId != null) {
                dietPlans = dietPlans.stream()
                        .filter(dp -> dp.getDietitianId().equals(dietitianId))
                        .collect(Collectors.toList());
            }
            
            // Filter by status if provided
            if (status != null) {
                dietPlans = dietPlans.stream()
                        .filter(dp -> dp.getStatus() == status)
                        .collect(Collectors.toList());
            }
            
            return dietPlans.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error searching diet plans: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search diet plans", e);
        }
    }

    private DietPlanDto convertToDto(DietPlan dietPlan) {
        DietPlanDto dto = new DietPlanDto();
        dto.setId(dietPlan.getId());
        dto.setTitle(dietPlan.getTitle());
        dto.setDescription(dietPlan.getDescription());
        dto.setPatientId(dietPlan.getPatientId());
        dto.setDietitianId(dietPlan.getDietitianId());
        dto.setStartDate(dietPlan.getStartDate());
        dto.setEndDate(dietPlan.getEndDate());
        dto.setBreakfast(dietPlan.getBreakfast());
        dto.setLunch(dietPlan.getLunch());
        dto.setDinner(dietPlan.getDinner());
        dto.setSnacks(dietPlan.getSnacks());
        dto.setDailyCalories(dietPlan.getDailyCalories());
        dto.setSpecialInstructions(dietPlan.getSpecialInstructions());
        dto.setDietaryRestrictions(dietPlan.getDietaryRestrictions());
        dto.setStatus(dietPlan.getStatus());
        dto.setCreatedAt(dietPlan.getCreatedAt());
        dto.setUpdatedAt(dietPlan.getUpdatedAt());
        return dto;
    }
}