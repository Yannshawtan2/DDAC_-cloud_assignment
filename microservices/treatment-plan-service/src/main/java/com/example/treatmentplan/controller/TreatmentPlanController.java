package com.example.treatmentplan.controller;

import com.example.treatmentplan.dto.CreateTreatmentPlanRequest;
import com.example.treatmentplan.dto.TreatmentPlanDto;
import com.example.treatmentplan.dto.UpdateTreatmentPlanRequest;
import com.example.treatmentplan.model.TreatmentPlan;
import com.example.treatmentplan.service.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/treatmentplan/treatment-plans")
@CrossOrigin(origins = "*")
public class TreatmentPlanController {
    private static final Logger logger = LoggerFactory.getLogger(TreatmentPlanController.class);

    @Autowired
    private TreatmentPlanService treatmentPlanService;

    @PostMapping
    public ResponseEntity<?> createTreatmentPlan(@Valid @RequestBody CreateTreatmentPlanRequest request) {
        try {
            logger.info("Creating treatment plan: {}", request.getTitle());
            TreatmentPlanDto response = treatmentPlanService.createTreatmentPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Treatment plan creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during treatment plan creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTreatmentPlanById(@PathVariable Long id) {
        try {
            logger.info("Retrieving treatment plan with ID: {}", id);
            Optional<TreatmentPlanDto> response = treatmentPlanService.getTreatmentPlanById(id);
            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTreatmentPlans() {
        try {
            logger.info("Retrieving all treatment plans");
            List<TreatmentPlanDto> response = treatmentPlanService.getAllTreatmentPlans();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plans: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getTreatmentPlansByDoctor(@PathVariable Long doctorId) {
        try {
            logger.info("Retrieving treatment plans for doctor ID: {}", doctorId);
            List<TreatmentPlanDto> response = treatmentPlanService.getTreatmentPlansByDoctor(doctorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plans by doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getTreatmentPlansByPatient(@PathVariable Long patientId) {
        try {
            logger.info("Retrieving treatment plans for patient ID: {}", patientId);
            List<TreatmentPlanDto> response = treatmentPlanService.getTreatmentPlansByPatient(patientId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plans by patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/doctor/{doctorId}/status/{status}")
    public ResponseEntity<?> getTreatmentPlansByDoctorAndStatus(
            @PathVariable Long doctorId, 
            @PathVariable TreatmentPlan.Status status) {
        try {
            logger.info("Retrieving treatment plans for doctor ID: {} with status: {}", doctorId, status);
            List<TreatmentPlanDto> response = treatmentPlanService.getTreatmentPlansByDoctorAndStatus(doctorId, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plans by doctor and status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTreatmentPlansByStatus(@PathVariable TreatmentPlan.Status status) {
        try {
            logger.info("Retrieving treatment plans with status: {}", status);
            List<TreatmentPlanDto> response = treatmentPlanService.getTreatmentPlansByStatus(status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plans by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTreatmentPlans(
            @RequestParam String keyword,
            @RequestParam Long doctorId) {
        try {
            logger.info("Searching treatment plans with keyword: {} for doctor ID: {}", keyword, doctorId);
            List<TreatmentPlanDto> response = treatmentPlanService.searchTreatmentPlans(keyword, doctorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error searching treatment plans: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/doctor/{doctorId}/recent")
    public ResponseEntity<?> getRecentTreatmentPlans(@PathVariable Long doctorId) {
        try {
            logger.info("Retrieving recent treatment plans for doctor ID: {}", doctorId);
            List<TreatmentPlanDto> response = treatmentPlanService.getRecentTreatmentPlans(doctorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving recent treatment plans: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/doctor/{doctorId}/stats")
    public ResponseEntity<?> getTreatmentPlanStats(@PathVariable Long doctorId) {
        try {
            logger.info("Retrieving treatment plan statistics for doctor ID: {}", doctorId);
            
            Map<String, Long> stats = Map.of(
                "total", treatmentPlanService.getTotalTreatmentPlansCount(doctorId),
                "active", treatmentPlanService.getActiveTreatmentPlansCount(doctorId),
                "completed", treatmentPlanService.getCompletedTreatmentPlansCount(doctorId),
                "paused", treatmentPlanService.getPausedTreatmentPlansCount(doctorId)
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving treatment plan statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTreatmentPlan(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateTreatmentPlanRequest request) {
        try {
            logger.info("Updating treatment plan with ID: {}", id);
            TreatmentPlanDto response = treatmentPlanService.updateTreatmentPlan(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Treatment plan update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating treatment plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTreatmentPlanStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusStr = statusUpdate.get("status");
            TreatmentPlan.Status status = TreatmentPlan.Status.valueOf(statusStr.toUpperCase());
            
            logger.info("Updating status for treatment plan ID: {} to: {}", id, status);
            TreatmentPlanDto response = treatmentPlanService.updateStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating treatment plan status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTreatmentPlan(@PathVariable Long id) {
        try {
            logger.info("Deleting treatment plan with ID: {}", id);
            treatmentPlanService.deleteTreatmentPlan(id);
            return ResponseEntity.ok(Map.of("message", "Treatment plan deleted successfully"));
        } catch (IllegalArgumentException e) {
            logger.warn("Treatment plan deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error deleting treatment plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}/ownership/{doctorId}")
    public ResponseEntity<?> checkTreatmentPlanOwnership(
            @PathVariable Long id, 
            @PathVariable Long doctorId) {
        try {
            logger.info("Checking ownership of treatment plan ID: {} by doctor ID: {}", id, doctorId);
            boolean isOwned = treatmentPlanService.isTreatmentPlanOwnedByDoctor(id, doctorId);
            return ResponseEntity.ok(Map.of("owned", isOwned));
        } catch (Exception e) {
            logger.error("Unexpected error checking treatment plan ownership: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "treatment-plan-service"));
    }
}