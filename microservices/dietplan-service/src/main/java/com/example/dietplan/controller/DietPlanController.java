package com.example.dietplan.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dietplan.dto.CreateDietPlanRequest;
import com.example.dietplan.dto.DietPlanDto;
import com.example.dietplan.dto.UpdateDietPlanRequest;
import com.example.dietplan.model.DietPlan;
import com.example.dietplan.service.DietPlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/dietplan/diet-plans")
@CrossOrigin(origins = "*")
public class DietPlanController {
    private static final Logger logger = LoggerFactory.getLogger(DietPlanController.class);

    @Autowired
    private DietPlanService dietPlanService;

    @PostMapping
    public ResponseEntity<?> createDietPlan(@Valid @RequestBody CreateDietPlanRequest request) {
        try {
            logger.info("Creating diet plan with title: {}", request.getTitle());
            DietPlanDto response = dietPlanService.createDietPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Diet plan creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during diet plan creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDietPlanById(@PathVariable Long id) {
        try {
            logger.info("Retrieving diet plan with ID: {}", id);
            DietPlanDto response = dietPlanService.getDietPlanById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Diet plan not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDietPlans() {
        try {
            logger.info("Retrieving all diet plans");
            List<DietPlanDto> response = dietPlanService.getAllDietPlans();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getDietPlansByPatientId(@PathVariable Long patientId) {
        try {
            logger.info("Retrieving diet plans for patient ID: {}", patientId);
            List<DietPlanDto> response = dietPlanService.getDietPlansByPatientId(patientId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid patient ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans by patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/dietitian/{dietitianId}")
    public ResponseEntity<?> getDietPlansByDietitianId(@PathVariable Long dietitianId) {
        try {
            logger.info("Retrieving diet plans for dietitian ID: {}", dietitianId);
            List<DietPlanDto> response = dietPlanService.getDietPlansByDietitianId(dietitianId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid dietitian ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans by dietitian: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getDietPlansByStatus(@PathVariable DietPlan.Status status) {
        try {
            logger.info("Retrieving diet plans with status: {}", status);
            List<DietPlanDto> response = dietPlanService.getDietPlansByStatus(status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/dietitian/{dietitianId}/status/{status}")
    public ResponseEntity<?> getDietPlansByDietitianIdAndStatus(@PathVariable Long dietitianId, 
                                                               @PathVariable DietPlan.Status status) {
        try {
            logger.info("Retrieving diet plans for dietitian ID: {} with status: {}", dietitianId, status);
            List<DietPlanDto> response = dietPlanService.getDietPlansByDietitianIdAndStatus(dietitianId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid dietitian ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans by dietitian and status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<?> getDietPlansByPatientIdAndStatus(@PathVariable Long patientId, 
                                                             @PathVariable DietPlan.Status status) {
        try {
            logger.info("Retrieving diet plans for patient ID: {} with status: {}", patientId, status);
            List<DietPlanDto> response = dietPlanService.getDietPlansByPatientIdAndStatus(patientId, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid patient ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving diet plans by patient and status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDietPlan(@PathVariable Long id, @Valid @RequestBody UpdateDietPlanRequest request) {
        try {
            logger.info("Updating diet plan with ID: {}", id);
            DietPlanDto response = dietPlanService.updateDietPlan(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Diet plan update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating diet plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDietPlanStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            DietPlan.Status status = DietPlan.Status.valueOf(statusStr.toUpperCase());
            logger.info("Updating diet plan status for ID: {} to status: {}", id, status);
            
            DietPlanDto response = dietPlanService.updateDietPlanStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Diet plan status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating diet plan status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDietPlan(@PathVariable Long id) {
        try {
            logger.info("Deleting diet plan with ID: {}", id);
            dietPlanService.deleteDietPlan(id);
            return ResponseEntity.ok(Map.of("message", "Diet plan deleted successfully"));
        } catch (IllegalArgumentException e) {
            logger.warn("Diet plan deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error deleting diet plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/stats/dietitian/{dietitianId}")
    public ResponseEntity<?> getDietitianStats(@PathVariable Long dietitianId) {
        try {
            logger.info("Getting stats for dietitian ID: {}", dietitianId);
            
            Long totalCount = dietPlanService.getDietPlanCountByDietitianId(dietitianId);
            Long activeCount = dietPlanService.getDietPlanCountByDietitianIdAndStatus(dietitianId, DietPlan.Status.ACTIVE);
            Long completedCount = dietPlanService.getDietPlanCountByDietitianIdAndStatus(dietitianId, DietPlan.Status.COMPLETED);
            Long suspendedCount = dietPlanService.getDietPlanCountByDietitianIdAndStatus(dietitianId, DietPlan.Status.SUSPENDED);
            List<Long> distinctPatientIds = dietPlanService.getDistinctPatientIdsByDietitianId(dietitianId);
            
            Map<String, Object> stats = Map.of(
                "totalDietPlans", totalCount,
                "activeDietPlans", activeCount,
                "completedDietPlans", completedCount,
                "suspendedDietPlans", suspendedCount,
                "totalPatients", distinctPatientIds.size(),
                "patientIds", distinctPatientIds
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Unexpected error getting dietitian stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/stats/patient/{patientId}")
    public ResponseEntity<?> getPatientStats(@PathVariable Long patientId) {
        try {
            logger.info("Getting stats for patient ID: {}", patientId);
            
            Long totalCount = dietPlanService.getDietPlanCountByPatientId(patientId);
            Long activeCount = (long) dietPlanService.getDietPlansByPatientIdAndStatus(patientId, DietPlan.Status.ACTIVE).size();
            Long completedCount = (long) dietPlanService.getDietPlansByPatientIdAndStatus(patientId, DietPlan.Status.COMPLETED).size();
            Long suspendedCount = (long) dietPlanService.getDietPlansByPatientIdAndStatus(patientId, DietPlan.Status.SUSPENDED).size();
            
            Map<String, Object> stats = Map.of(
                "totalDietPlans", totalCount,
                "activeDietPlans", activeCount,
                "completedDietPlans", completedCount,
                "suspendedDietPlans", suspendedCount
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Unexpected error getting patient stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDietPlans(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) DietPlan.Status status,
            @RequestParam(required = false) Long dietitianId) {
        try {
            logger.info("Searching diet plans with patientName: {}, status: {}, dietitianId: {}", 
                       patientName, status, dietitianId);
            
            List<DietPlanDto> dietPlans = dietPlanService.searchDietPlans(patientName, status, dietitianId);
            return ResponseEntity.ok(dietPlans);
        } catch (Exception e) {
            logger.error("Unexpected error searching diet plans: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "dietplan-service"));
    }
}