package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.DietPlan;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.DietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DietPlanService {

    @Autowired
    private DietPlanRepository dietPlanRepository;

    // Create a new diet plan
    public DietPlan createDietPlan(DietPlan dietPlan) {
        dietPlan.setCreatedAt(LocalDateTime.now());
        dietPlan.setUpdatedAt(LocalDateTime.now());
        return dietPlanRepository.save(dietPlan);
    }

    // Get all diet plans
    public List<DietPlan> getAllDietPlans() {
        return dietPlanRepository.findAll();
    }

    // Get diet plan by ID
    public Optional<DietPlan> getDietPlanById(Long id) {
        return dietPlanRepository.findById(id);
    }

    // Get diet plans by dietitian
    public List<DietPlan> getDietPlansByDietitian(Users dietitian) {
        return dietPlanRepository.findByDietitian(dietitian);
    }

    // Get diet plans by user
    public List<DietPlan> getDietPlansByUser(Users user) {
        return dietPlanRepository.findByPatient(user);
    }

    // Get diet plans by status
    public List<DietPlan> getDietPlansByStatus(DietPlan.Status status) {
        return dietPlanRepository.findByStatus(status);
    }

    // Get diet plans by dietitian and status
    public List<DietPlan> getDietPlansByDietitianAndStatus(Users dietitian, DietPlan.Status status) {
        return dietPlanRepository.findByDietitianAndStatus(dietitian, status);
    }

    // Update diet plan
    public DietPlan updateDietPlan(DietPlan dietPlan) {
        dietPlan.setUpdatedAt(LocalDateTime.now());
        return dietPlanRepository.save(dietPlan);
    }

    // Delete diet plan
    public void deleteDietPlan(Long id) {
        dietPlanRepository.deleteById(id);
    }

    // Check if diet plan exists
    public boolean existsById(Long id) {
        return dietPlanRepository.existsById(id);
    }

    // Search diet plans by user name
    public List<DietPlan> searchDietPlansByUserName(String userName) {
        return dietPlanRepository.findByPatientNameContaining(userName);
    }

    // Update diet plan status
    public DietPlan updateDietPlanStatus(Long id, DietPlan.Status status) {
        Optional<DietPlan> optionalDietPlan = dietPlanRepository.findById(id);
        if (optionalDietPlan.isPresent()) {
            DietPlan dietPlan = optionalDietPlan.get();
            dietPlan.setStatus(status);
            dietPlan.setUpdatedAt(LocalDateTime.now());
            return dietPlanRepository.save(dietPlan);
        }
        return null;
    }
} 