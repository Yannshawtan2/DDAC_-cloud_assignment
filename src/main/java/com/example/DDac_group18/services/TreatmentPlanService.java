package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.TreatmentPlan;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.TreatmentPlanRepository;
import com.example.DDac_group18.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentPlanService {

    @Autowired
    private TreatmentPlanRepository treatmentPlanRepository;

    @Autowired
    private UserRepository userRepository;

    // CRUD Operations
    public List<TreatmentPlan> getAllTreatmentPlans() {
        return treatmentPlanRepository.findAll();
    }

    public List<TreatmentPlan> getTreatmentPlansByDoctor(Users doctor) {
        return treatmentPlanRepository.findByDoctorOrderByCreatedAtDesc(doctor);
    }

    public List<TreatmentPlan> getTreatmentPlansByPatient(Users patient) {
        return treatmentPlanRepository.findByPatientOrderByCreatedAtDesc(patient);
    }

    public Optional<TreatmentPlan> getTreatmentPlanById(Long id) {
        return treatmentPlanRepository.findById(id);
    }

    public TreatmentPlan createTreatmentPlan(TreatmentPlan treatmentPlan) {
        return treatmentPlanRepository.save(treatmentPlan);
    }

    public TreatmentPlan updateTreatmentPlan(TreatmentPlan treatmentPlan) {
        treatmentPlan.preUpdate();
        return treatmentPlanRepository.save(treatmentPlan);
    }

    public void deleteTreatmentPlan(Long id) {
        treatmentPlanRepository.deleteById(id);
    }

    // Status management
    public List<TreatmentPlan> getTreatmentPlansByStatus(TreatmentPlan.Status status) {
        return treatmentPlanRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<TreatmentPlan> getTreatmentPlansByDoctorAndStatus(Users doctor, TreatmentPlan.Status status) {
        return treatmentPlanRepository.findByDoctorAndStatusOrderByCreatedAtDesc(doctor, status);
    }

    public TreatmentPlan updateStatus(Long id, TreatmentPlan.Status newStatus) {
        Optional<TreatmentPlan> planOpt = treatmentPlanRepository.findById(id);
        if (planOpt.isPresent()) {
            TreatmentPlan plan = planOpt.get();
            plan.setStatus(newStatus);
            return updateTreatmentPlan(plan);
        }
        throw new RuntimeException("Treatment plan not found with id: " + id);
    }

    // Search functionality
    public List<TreatmentPlan> searchTreatmentPlans(String keyword, Users doctor) {
        return treatmentPlanRepository.findByTitleContainingAndDoctor(keyword, doctor);
    }

    // Statistics
    public Long getTotalTreatmentPlansCount(Users doctor) {
        return treatmentPlanRepository.countByDoctor(doctor);
    }

    public Long getActiveTreatmentPlansCount(Users doctor) {
        return treatmentPlanRepository.countByDoctorAndStatus(doctor, TreatmentPlan.Status.ACTIVE);
    }

    public Long getCompletedTreatmentPlansCount(Users doctor) {
        return treatmentPlanRepository.countByDoctorAndStatus(doctor, TreatmentPlan.Status.COMPLETED);
    }

    public Long getPausedTreatmentPlansCount(Users doctor) {
        return treatmentPlanRepository.countByDoctorAndStatus(doctor, TreatmentPlan.Status.PAUSED);
    }

    // Recent plans
    public List<TreatmentPlan> getRecentTreatmentPlans(Users doctor) {
        return treatmentPlanRepository.findTop10ByDoctorOrderByCreatedAtDesc(doctor);
    }

    // Patient management
    public List<Users> getAllPatients() {
        return userRepository.findByRole(Users.Role.PATIENT);
    }

    public List<Users> searchPatients(String keyword) {
        return userRepository.findByNameContainingAndRole(keyword, Users.Role.PATIENT);
    }

    // Validate treatment plan ownership
    public boolean isTreatmentPlanOwnedByDoctor(Long treatmentPlanId, Users doctor) {
        Optional<TreatmentPlan> planOpt = treatmentPlanRepository.findById(treatmentPlanId);
        return planOpt.isPresent() && planOpt.get().getDoctor().getId().equals(doctor.getId());
    }
} 