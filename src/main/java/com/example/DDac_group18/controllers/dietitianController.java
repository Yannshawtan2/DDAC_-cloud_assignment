package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.DietPlan;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.services.DietPlanService;
import com.example.DDac_group18.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.DDac_group18.services.MaintenanceNotificationService;
import com.example.DDac_group18.model.data_schema.MaintenanceNotification;


// import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class dietitianController {
    @Autowired
    private MaintenanceNotificationService maintenanceNotificationService;

    @Autowired
    private DietPlanService dietPlanService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Override the dieticiandashboard from HomeController to add data
    @GetMapping("/dieticiandashboard")
    public String dieticianDashboard(Model model, Authentication authentication) {
        List<MaintenanceNotification> activeNotifications = maintenanceNotificationService.getAllActiveNotifications();
        String email = authentication.getName();
        Users dietitian = userRepository.findByEmail(email);

        if (dietitian != null) {
            List<DietPlan> dietPlans = dietPlanService.getDietPlansByDietitian(dietitian);
            model.addAttribute("dietPlans", dietPlans);
            model.addAttribute("dietitianName", dietitian.getName());
            model.addAttribute("activeNotifications", activeNotifications);
        }

        return "dieticiandashboard";
    }

    // Show all diet plans
    @GetMapping("/dietician/diet-plans")
    public String listDietPlans(Model model, Authentication authentication) {
        String email = authentication.getName();
        Users dietitian = userRepository.findByEmail(email);

        if (dietitian != null) {
            List<DietPlan> dietPlans = dietPlanService.getDietPlansByDietitian(dietitian);
            model.addAttribute("dietPlans", dietPlans);
        }

        return "dietician/diet-plans-list";
    }

    // Show form to create new diet plan
    @GetMapping("/dietician/diet-plans/new")
    public String showCreateForm(Model model) {
        List<Users> patients = userService.getUsersByRole(Users.Role.PATIENT);
        model.addAttribute("patients", patients);
        model.addAttribute("dietPlan", new DietPlan());
        return "dietician/diet-plan-form";
    }

    // Create new diet plan
    @PostMapping("/dietician/diet-plans")
    public String createDietPlan(@ModelAttribute DietPlan dietPlan,
            @RequestParam Long patientId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Users dietitian = userRepository.findByEmail(email);
            Optional<Users> patientOpt = userRepository.findById(patientId);

            if (dietitian != null && patientOpt.isPresent()) {
                Users patient = patientOpt.get();

                dietPlan.setDietitian(dietitian);
                dietPlan.setPatient(patient);
                dietPlan.setStatus(DietPlan.Status.ACTIVE);

                dietPlanService.createDietPlan(dietPlan);
                redirectAttributes.addFlashAttribute("successMessage", "Diet plan created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Error creating diet plan: Invalid user or patient.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating diet plan: " + e.getMessage());
        }

        return "redirect:/dietician/diet-plans";
    }

    // Show diet plan details
    @GetMapping("/dietician/diet-plans/{id}")
    public String viewDietPlan(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<DietPlan> dietPlanOpt = dietPlanService.getDietPlanById(id);
        if (dietPlanOpt.isPresent()) {
            DietPlan dietPlan = dietPlanOpt.get();
            String currentUserEmail = authentication.getName();

            // Check if the current dietitian owns this diet plan
            if (dietPlan.getDietitian().getEmail().equals(currentUserEmail)) {
                model.addAttribute("dietPlan", dietPlan);
                return "dietician/diet-plan-view";
            }
        }

        return "redirect:/dietician/diet-plans";
    }

    // Show form to edit diet plan
    @GetMapping("/dietician/diet-plans/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<DietPlan> dietPlanOpt = dietPlanService.getDietPlanById(id);

        if (dietPlanOpt.isPresent()) {
            DietPlan dietPlan = dietPlanOpt.get();
            String currentUserEmail = authentication.getName();

            // Check if the current dietitian owns this diet plan
            if (dietPlan.getDietitian().getEmail().equals(currentUserEmail)) {
                List<Users> patients = userService.getUsersByRole(Users.Role.PATIENT);
                model.addAttribute("dietPlan", dietPlan);
                model.addAttribute("patients", patients);
                return "dietician/diet-plan-edit";
            }
        }

        return "redirect:/dietician/diet-plans";
    }

    // Update diet plan
    @PostMapping("/dietician/diet-plans/{id}")
    public String updateDietPlan(@PathVariable Long id,
            @ModelAttribute DietPlan dietPlan,
            @RequestParam Long patientId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<DietPlan> existingDietPlanOpt = dietPlanService.getDietPlanById(id);
            Optional<Users> patientOpt = userRepository.findById(patientId);

            if (existingDietPlanOpt.isPresent() && patientOpt.isPresent()) {
                DietPlan existingDietPlan = existingDietPlanOpt.get();
                String currentUserEmail = authentication.getName();

                // Check if the current dietitian owns this diet plan
                if (existingDietPlan.getDietitian().getEmail().equals(currentUserEmail)) {
                    Users patient = patientOpt.get();

                    // Update the existing diet plan with new values
                    existingDietPlan.setTitle(dietPlan.getTitle());
                    existingDietPlan.setDescription(dietPlan.getDescription());
                    existingDietPlan.setStartDate(dietPlan.getStartDate());
                    existingDietPlan.setEndDate(dietPlan.getEndDate());
                    existingDietPlan.setBreakfast(dietPlan.getBreakfast());
                    existingDietPlan.setLunch(dietPlan.getLunch());
                    existingDietPlan.setDinner(dietPlan.getDinner());
                    existingDietPlan.setSnacks(dietPlan.getSnacks());
                    existingDietPlan.setDailyCalories(dietPlan.getDailyCalories());
                    existingDietPlan.setSpecialInstructions(dietPlan.getSpecialInstructions());
                    existingDietPlan.setDietaryRestrictions(dietPlan.getDietaryRestrictions());
                    existingDietPlan.setStatus(dietPlan.getStatus());
                    existingDietPlan.setPatient(patient);

                    dietPlanService.updateDietPlan(existingDietPlan);
                    redirectAttributes.addFlashAttribute("successMessage", "Diet plan updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update this diet plan.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Diet plan or patient not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating diet plan: " + e.getMessage());
        }

        return "redirect:/dietician/diet-plans";
    }

    // Delete diet plan
    @PostMapping("/dietician/diet-plans/{id}/delete")
    public String deleteDietPlan(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<DietPlan> dietPlanOpt = dietPlanService.getDietPlanById(id);

            if (dietPlanOpt.isPresent()) {
                DietPlan dietPlan = dietPlanOpt.get();
                String currentUserEmail = authentication.getName();

                // Check if the current dietitian owns this diet plan
                if (dietPlan.getDietitian().getEmail().equals(currentUserEmail)) {
                    dietPlanService.deleteDietPlan(id);
                    redirectAttributes.addFlashAttribute("successMessage", "Diet plan deleted successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to delete this diet plan.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Diet plan not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting diet plan: " + e.getMessage());
        }

        return "redirect:/dietician/diet-plans";
    }

    // Update diet plan status
    @PostMapping("/dietician/diet-plans/{id}/status")
    public String updateDietPlanStatus(@PathVariable Long id,
            @RequestParam DietPlan.Status status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<DietPlan> dietPlanOpt = dietPlanService.getDietPlanById(id);

            if (dietPlanOpt.isPresent()) {
                DietPlan dietPlan = dietPlanOpt.get();
                String currentUserEmail = authentication.getName();

                // Check if the current dietitian owns this diet plan
                if (dietPlan.getDietitian().getEmail().equals(currentUserEmail)) {
                    dietPlanService.updateDietPlanStatus(id, status);
                    redirectAttributes.addFlashAttribute("successMessage", "Diet plan status updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update this diet plan.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Diet plan not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating diet plan status: " + e.getMessage());
        }

        return "redirect:/dietician/diet-plans";
    }

    // Search diet plans
    @GetMapping("/dietician/diet-plans/search")
    public String searchDietPlans(@RequestParam(required = false) String patientName,
            @RequestParam(required = false) DietPlan.Status status,
            Model model,
            Authentication authentication) {
        String email = authentication.getName();
        Users dietitian = userRepository.findByEmail(email);

        if (dietitian != null) {
            List<DietPlan> dietPlans;

            if (patientName != null && !patientName.trim().isEmpty()) {
                // Search by patient name
                dietPlans = dietPlanService.searchDietPlansByUserName(patientName);
                // Filter by current dietitian
                dietPlans = dietPlans.stream()
                        .filter(dp -> dp.getDietitian().getId().equals(dietitian.getId()))
                        .collect(java.util.stream.Collectors.toList());
            } else if (status != null) {
                // Filter by status
                dietPlans = dietPlanService.getDietPlansByDietitianAndStatus(dietitian, status);
            } else {
                // Show all
                dietPlans = dietPlanService.getDietPlansByDietitian(dietitian);
            }

            model.addAttribute("dietPlans", dietPlans);
            model.addAttribute("searchPatientName", patientName);
            model.addAttribute("searchStatus", status);
        }

        return "dietician/diet-plans-list";
    }
}
