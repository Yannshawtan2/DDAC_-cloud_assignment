package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.model.data_schema.TreatmentPlan;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.AppointmentRepository;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.clients.AppointmentServiceClient;
import com.example.DDac_group18.services.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import com.example.DDac_group18.services.MaintenanceNotificationService;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private TreatmentPlanService treatmentPlanService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentServiceClient appointmentServiceClient;

    @Autowired
    private MaintenanceNotificationService maintenanceNotificationService;

    @GetMapping("/dashboard")
    public String showDoctorDashboard(Model model, Authentication authentication) {
        List<MaintenanceNotification> activeNotifications = maintenanceNotificationService.getAllActiveNotifications();
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }
        model.addAttribute("notifications", activeNotifications);
        model.addAttribute("doctor", doctor);
        return "doctor/doctor-dashboard";
    }

    @GetMapping("/appointment")
    public String showAppointmentChecking(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            Model model,
            Authentication authentication) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentServiceClient.getAppointmentsByDoctorId(doctor.getId().toString());
        LocalDate today = LocalDate.now();

        // Apply filters and search using service client
        appointments = appointmentServiceClient.filterAppointments(appointments, filter, search);
        
        // Get patient names for appointments
        Map<String, String> patientNames = appointmentServiceClient.getPatientNamesForAppointments(appointments);

        model.addAttribute("appointments", appointments);
        model.addAttribute("patientNames", patientNames);
        model.addAttribute("doctor", doctor);
        model.addAttribute("activeFilter", filter);
        model.addAttribute("searchTerm", search);
        model.addAttribute("today", today);

        return "doctor/appointmentChecking";
    }

    @PostMapping("/approve-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> approveAppointment(@PathVariable Long id, Authentication authentication) {
        try {
            Users doctor = getCurrentDoctor(authentication);
            if (doctor == null) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            // Use appointment service client to approve appointment
            appointmentServiceClient.approveAppointment(id, doctor.getId().toString());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to approve appointment: " + e.getMessage());
        }
    }

    @PostMapping("/reject-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> rejectAppointment(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            Users doctor = getCurrentDoctor(authentication);
            if (doctor == null) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            // Use appointment service client to reject appointment
            appointmentServiceClient.rejectAppointment(id, doctor.getId().toString(), reason);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reject appointment: " + e.getMessage());
        }
    }

    @PostMapping("/complete-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> completeAppointment(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Users doctor = getCurrentDoctor(authentication);
            if (doctor == null) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            // Use appointment service client to complete appointment
            appointmentServiceClient.completeAppointment(id, doctor.getId().toString());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to complete appointment: " + e.getMessage());
        }
    }

    @PostMapping("/save-diagnosis/{id}")
    @ResponseBody
    public ResponseEntity<?> saveDiagnosis(
            @PathVariable Long id,
            @RequestParam String diagnosisNote,
            Authentication authentication) {
        try {
            Users doctor = getCurrentDoctor(authentication);
            if (doctor == null) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Verify this appointment belongs to the doctor
            if (!appointment.getDoctorId().equals(doctor.getId().toString())) {
                return ResponseEntity.status(403).body("Not authorized to manage this appointment");
            }

            // Only allow adding diagnosis to completed appointments or confirmed
            // appointments that have passed
            if (!appointment.getStatus().equals("COMPLETED")) {
                LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getTime());
                if (!appointment.getStatus().equals("CONFIRMED") || appointmentDateTime.isAfter(LocalDateTime.now())) {
                    return ResponseEntity.badRequest().body(
                            "Can only add diagnosis to completed appointments or confirmed appointments that have passed");
                }
            }

            appointment.setDiagnosisNote(diagnosisNote);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save diagnosis: " + e.getMessage());
        }
    }

    @PostMapping("/delete-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, Authentication authentication) {
        try {
            Users doctor = getCurrentDoctor(authentication);
            if (doctor == null) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            // Use appointment service client to delete appointment
            appointmentServiceClient.deleteAppointment(id, doctor.getId().toString());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete appointment: " + e.getMessage());
        }
    }

    // Treatment Plans List
    @GetMapping("/treatment-plans")
    public String treatmentPlansList(@RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model, Authentication authentication) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        List<TreatmentPlan> treatmentPlans;

        if (search != null && !search.trim().isEmpty()) {
            treatmentPlans = treatmentPlanService.searchTreatmentPlans(search.trim(), doctor);
        } else if (status != null && !status.isEmpty()) {
            try {
                TreatmentPlan.Status statusEnum = TreatmentPlan.Status.valueOf(status.toUpperCase());
                treatmentPlans = treatmentPlanService.getTreatmentPlansByDoctorAndStatus(doctor, statusEnum);
            } catch (IllegalArgumentException e) {
                treatmentPlans = treatmentPlanService.getTreatmentPlansByDoctor(doctor);
            }
        } else {
            treatmentPlans = treatmentPlanService.getTreatmentPlansByDoctor(doctor);
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("treatmentPlans", treatmentPlans);
        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "doctor/treatment-plans-list";
    }

    // Create Treatment Plan Form
    @GetMapping("/treatment-plans/create")
    public String createTreatmentPlanForm(Model model, Authentication authentication) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        List<Users> patients = treatmentPlanService.getAllPatients();
        model.addAttribute("doctor", doctor);
        model.addAttribute("treatmentPlan", new TreatmentPlan());
        model.addAttribute("patients", patients);

        return "doctor/treatment-plan-form";
    }

    // Save Treatment Plan
    @PostMapping("/treatment-plans/save")
    public String saveTreatmentPlan(@ModelAttribute TreatmentPlan treatmentPlan,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        try {
            treatmentPlan.setDoctor(doctor);

            // Find patient by ID
            if (treatmentPlan.getPatient() != null && treatmentPlan.getPatient().getId() != null) {
                Optional<Users> patientOpt = userRepository.findById(treatmentPlan.getPatient().getId());
                if (patientOpt.isPresent()) {
                    treatmentPlan.setPatient(patientOpt.get());
                }
            }

            if (treatmentPlan.getId() == null) {
                // Creating new treatment plan
                treatmentPlan.setCreatedAt(LocalDateTime.now());
                treatmentPlan.setUpdatedAt(LocalDateTime.now());
                treatmentPlanService.createTreatmentPlan(treatmentPlan);
                redirectAttributes.addFlashAttribute("successMessage", "Treatment plan created successfully!");
                return "redirect:/doctor/treatment-plans";
            } else {
                // Updating existing treatment plan
                treatmentPlan.setUpdatedAt(LocalDateTime.now());
                treatmentPlanService.updateTreatmentPlan(treatmentPlan);
                redirectAttributes.addFlashAttribute("successMessage", "Treatment plan updated successfully!");
                return "redirect:/doctor/treatment-plans/" + treatmentPlan.getId();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving treatment plan: " + e.getMessage());
            if (treatmentPlan.getId() == null) {
                return "redirect:/doctor/treatment-plans/create";
            } else {
                return "redirect:/doctor/treatment-plans/" + treatmentPlan.getId() + "/edit";
            }
        }
    }

    // View Treatment Plan
    @GetMapping("/treatment-plans/{id}")
    public String viewTreatmentPlan(@PathVariable Long id, Model model, Authentication authentication) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Optional<TreatmentPlan> treatmentPlanOpt = treatmentPlanService.getTreatmentPlanById(id);
        if (treatmentPlanOpt.isEmpty() || !treatmentPlanService.isTreatmentPlanOwnedByDoctor(id, doctor)) {
            return "redirect:/doctor/treatment-plans";
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("treatmentPlan", treatmentPlanOpt.get());

        return "doctor/treatment-plan-view";
    }

    // Edit Treatment Plan Form
    @GetMapping("/treatment-plans/{id}/edit")
    public String editTreatmentPlanForm(@PathVariable Long id, Model model, Authentication authentication) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        Optional<TreatmentPlan> treatmentPlanOpt = treatmentPlanService.getTreatmentPlanById(id);
        if (treatmentPlanOpt.isEmpty() || !treatmentPlanService.isTreatmentPlanOwnedByDoctor(id, doctor)) {
            return "redirect:/doctor/treatment-plans";
        }

        List<Users> patients = treatmentPlanService.getAllPatients();
        model.addAttribute("doctor", doctor);
        model.addAttribute("treatmentPlan", treatmentPlanOpt.get());
        model.addAttribute("patients", patients);

        return "doctor/treatment-plan-edit";
    }

    // Delete Treatment Plan
    @PostMapping("/treatment-plans/{id}/delete")
    public String deleteTreatmentPlan(@PathVariable Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        try {
            if (treatmentPlanService.isTreatmentPlanOwnedByDoctor(id, doctor)) {
                treatmentPlanService.deleteTreatmentPlan(id);
                redirectAttributes.addFlashAttribute("successMessage", "Treatment plan deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You don't have permission to delete this treatment plan.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting treatment plan: " + e.getMessage());
        }

        return "redirect:/doctor/treatment-plans";
    }

    // Update Status
    @PostMapping("/treatment-plans/{id}/status")
    public String updateStatus(@PathVariable Long id,
            @RequestParam String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Users doctor = getCurrentDoctor(authentication);
        if (doctor == null) {
            return "redirect:/login";
        }

        try {
            if (treatmentPlanService.isTreatmentPlanOwnedByDoctor(id, doctor)) {
                TreatmentPlan.Status newStatus = TreatmentPlan.Status.valueOf(status.toUpperCase());
                treatmentPlanService.updateStatus(id, newStatus);
                redirectAttributes.addFlashAttribute("successMessage", "Treatment plan status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You don't have permission to update this treatment plan.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
        }

        return "redirect:/doctor/treatment-plans";
    }

    // Helper method to get current doctor
    private Users getCurrentDoctor(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);

        if (user != null && user.getRole() == Users.Role.DOCTOR) {
            return user;
        }

        return null;
    }
}