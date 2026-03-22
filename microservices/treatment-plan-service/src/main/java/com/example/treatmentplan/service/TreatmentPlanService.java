package com.example.treatmentplan.service;

import com.example.treatmentplan.dto.CreateTreatmentPlanRequest;
import com.example.treatmentplan.dto.TreatmentPlanDto;
import com.example.treatmentplan.dto.UpdateTreatmentPlanRequest;
import com.example.treatmentplan.model.TreatmentPlan;
import com.example.treatmentplan.repository.TreatmentPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreatmentPlanService {
    private static final Logger logger = LoggerFactory.getLogger(TreatmentPlanService.class);

    @Autowired
    private TreatmentPlanRepository treatmentPlanRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    // CRUD Operations
    public List<TreatmentPlanDto> getAllTreatmentPlans() {
        logger.info("Fetching all treatment plans");
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findAll();
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TreatmentPlanDto> getTreatmentPlansByDoctor(Long doctorId) {
        logger.info("Fetching treatment plans for doctor ID: {}", doctorId);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TreatmentPlanDto> getTreatmentPlansByPatient(Long patientId) {
        logger.info("Fetching treatment plans for patient ID: {}", patientId);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<TreatmentPlanDto> getTreatmentPlanById(Long id) {
        logger.info("Fetching treatment plan with ID: {}", id);
        Optional<TreatmentPlan> treatmentPlan = treatmentPlanRepository.findById(id);
        return treatmentPlan.map(this::convertToDto);
    }

    public TreatmentPlanDto createTreatmentPlan(CreateTreatmentPlanRequest request) {
        logger.info("Creating treatment plan: {}", request.getTitle());
        
        // Validate that patient and doctor exist by calling user service
        UserServiceClient.UserInfo patient = userServiceClient.getUserById(request.getPatientId());
        UserServiceClient.UserInfo doctor = userServiceClient.getUserById(request.getDoctorId());
        
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + request.getPatientId());
        }
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + request.getDoctorId());
        }
        if (!"PATIENT".equals(patient.getRole())) {
            throw new IllegalArgumentException("User with ID " + request.getPatientId() + " is not a patient");
        }
        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new IllegalArgumentException("User with ID " + request.getDoctorId() + " is not a doctor");
        }
        
        TreatmentPlan treatmentPlan = new TreatmentPlan();
        treatmentPlan.setTitle(request.getTitle());
        treatmentPlan.setDescription(request.getDescription());
        treatmentPlan.setPatientId(request.getPatientId());
        treatmentPlan.setDoctorId(request.getDoctorId());
        treatmentPlan.setMedication(request.getMedication());
        treatmentPlan.setDosage(request.getDosage());
        treatmentPlan.setFrequency(request.getFrequency());
        treatmentPlan.setInstructions(request.getInstructions());
        treatmentPlan.setStartDate(request.getStartDate());
        treatmentPlan.setEndDate(request.getEndDate());
        treatmentPlan.setNotes(request.getNotes());
        
        TreatmentPlan savedPlan = treatmentPlanRepository.save(treatmentPlan);
        logger.info("Successfully created treatment plan with ID: {}", savedPlan.getId());
        
        return convertToDto(savedPlan);
    }

    public TreatmentPlanDto updateTreatmentPlan(Long id, UpdateTreatmentPlanRequest request) {
        logger.info("Updating treatment plan with ID: {}", id);
        
        Optional<TreatmentPlan> existingPlanOpt = treatmentPlanRepository.findById(id);
        if (!existingPlanOpt.isPresent()) {
            throw new IllegalArgumentException("Treatment plan not found with ID: " + id);
        }
        
        TreatmentPlan treatmentPlan = existingPlanOpt.get();
        
        if (request.getTitle() != null) {
            treatmentPlan.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            treatmentPlan.setDescription(request.getDescription());
        }
        if (request.getMedication() != null) {
            treatmentPlan.setMedication(request.getMedication());
        }
        if (request.getDosage() != null) {
            treatmentPlan.setDosage(request.getDosage());
        }
        if (request.getFrequency() != null) {
            treatmentPlan.setFrequency(request.getFrequency());
        }
        if (request.getInstructions() != null) {
            treatmentPlan.setInstructions(request.getInstructions());
        }
        if (request.getStartDate() != null) {
            treatmentPlan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            treatmentPlan.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            treatmentPlan.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            treatmentPlan.setNotes(request.getNotes());
        }
        
        treatmentPlan.preUpdate();
        TreatmentPlan updatedPlan = treatmentPlanRepository.save(treatmentPlan);
        logger.info("Successfully updated treatment plan with ID: {}", updatedPlan.getId());
        
        return convertToDto(updatedPlan);
    }

    public void deleteTreatmentPlan(Long id) {
        logger.info("Deleting treatment plan with ID: {}", id);
        
        if (!treatmentPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("Treatment plan not found with ID: " + id);
        }
        
        treatmentPlanRepository.deleteById(id);
        logger.info("Successfully deleted treatment plan with ID: {}", id);
    }

    // Status management
    public List<TreatmentPlanDto> getTreatmentPlansByStatus(TreatmentPlan.Status status) {
        logger.info("Fetching treatment plans with status: {}", status);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findByStatusOrderByCreatedAtDesc(status);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TreatmentPlanDto> getTreatmentPlansByDoctorAndStatus(Long doctorId, TreatmentPlan.Status status) {
        logger.info("Fetching treatment plans for doctor ID: {} with status: {}", doctorId, status);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findByDoctorIdAndStatusOrderByCreatedAtDesc(doctorId, status);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TreatmentPlanDto updateStatus(Long id, TreatmentPlan.Status newStatus) {
        logger.info("Updating status for treatment plan ID: {} to: {}", id, newStatus);
        
        Optional<TreatmentPlan> planOpt = treatmentPlanRepository.findById(id);
        if (!planOpt.isPresent()) {
            throw new IllegalArgumentException("Treatment plan not found with ID: " + id);
        }
        
        TreatmentPlan plan = planOpt.get();
        plan.setStatus(newStatus);
        plan.preUpdate();
        TreatmentPlan updatedPlan = treatmentPlanRepository.save(plan);
        
        return convertToDto(updatedPlan);
    }

    // Search functionality
    public List<TreatmentPlanDto> searchTreatmentPlans(String keyword, Long doctorId) {
        logger.info("Searching treatment plans with keyword: {} for doctor ID: {}", keyword, doctorId);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findByTitleContainingAndDoctorId(keyword, doctorId);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Statistics
    public Long getTotalTreatmentPlansCount(Long doctorId) {
        logger.info("Getting total treatment plans count for doctor ID: {}", doctorId);
        return treatmentPlanRepository.countByDoctorId(doctorId);
    }

    public Long getActiveTreatmentPlansCount(Long doctorId) {
        logger.info("Getting active treatment plans count for doctor ID: {}", doctorId);
        return treatmentPlanRepository.countByDoctorIdAndStatus(doctorId, TreatmentPlan.Status.ACTIVE);
    }

    public Long getCompletedTreatmentPlansCount(Long doctorId) {
        logger.info("Getting completed treatment plans count for doctor ID: {}", doctorId);
        return treatmentPlanRepository.countByDoctorIdAndStatus(doctorId, TreatmentPlan.Status.COMPLETED);
    }

    public Long getPausedTreatmentPlansCount(Long doctorId) {
        logger.info("Getting paused treatment plans count for doctor ID: {}", doctorId);
        return treatmentPlanRepository.countByDoctorIdAndStatus(doctorId, TreatmentPlan.Status.PAUSED);
    }

    // Recent plans
    public List<TreatmentPlanDto> getRecentTreatmentPlans(Long doctorId) {
        logger.info("Getting recent treatment plans for doctor ID: {}", doctorId);
        List<TreatmentPlan> treatmentPlans = treatmentPlanRepository.findTop10ByDoctorIdOrderByCreatedAtDesc(doctorId);
        return treatmentPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Validate treatment plan ownership
    public boolean isTreatmentPlanOwnedByDoctor(Long treatmentPlanId, Long doctorId) {
        logger.info("Validating ownership of treatment plan ID: {} by doctor ID: {}", treatmentPlanId, doctorId);
        Optional<TreatmentPlan> planOpt = treatmentPlanRepository.findById(treatmentPlanId);
        return planOpt.isPresent() && planOpt.get().getDoctorId().equals(doctorId);
    }

    private TreatmentPlanDto convertToDto(TreatmentPlan treatmentPlan) {
        TreatmentPlanDto dto = new TreatmentPlanDto();
        dto.setId(treatmentPlan.getId());
        dto.setTitle(treatmentPlan.getTitle());
        dto.setDescription(treatmentPlan.getDescription());
        dto.setPatientId(treatmentPlan.getPatientId());
        dto.setDoctorId(treatmentPlan.getDoctorId());
        dto.setMedication(treatmentPlan.getMedication());
        dto.setDosage(treatmentPlan.getDosage());
        dto.setFrequency(treatmentPlan.getFrequency());
        dto.setInstructions(treatmentPlan.getInstructions());
        dto.setStartDate(treatmentPlan.getStartDate());
        dto.setEndDate(treatmentPlan.getEndDate());
        dto.setStatus(treatmentPlan.getStatus());
        dto.setCreatedAt(treatmentPlan.getCreatedAt());
        dto.setUpdatedAt(treatmentPlan.getUpdatedAt());
        dto.setNotes(treatmentPlan.getNotes());
        
        // Populate user information from User Service
        try {
            UserServiceClient.UserInfo patient = userServiceClient.getUserById(treatmentPlan.getPatientId());
            if (patient != null) {
                dto.setPatientName(patient.getName());
                dto.setPatientEmail(patient.getEmail());
            }
            
            UserServiceClient.UserInfo doctor = userServiceClient.getUserById(treatmentPlan.getDoctorId());
            if (doctor != null) {
                dto.setDoctorName(doctor.getName());
                dto.setDoctorEmail(doctor.getEmail());
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch user information for treatment plan ID: {}, error: {}", treatmentPlan.getId(), e.getMessage());
        }
        
        return dto;
    }
}