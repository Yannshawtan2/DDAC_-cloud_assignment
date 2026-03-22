package com.example.DDac_group18.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import com.example.DDac_group18.model.data_schema.Data;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.clients.PatientServiceClient;
import com.example.DDac_group18.clients.UserServiceClient;
import com.example.DDac_group18.clients.AppointmentServiceClient;
import com.example.DDac_group18.services.DataValidation;
import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import com.example.DDac_group18.services.MaintenanceNotificationService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    @Autowired
    private PatientServiceClient patientServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private DataValidation dataValidation;

    @Autowired
    private AppointmentServiceClient appointmentServiceClient;

    @Autowired
    private MaintenanceNotificationService maintenanceNotificationService;

    @GetMapping("/initial-data")
    public String showInitialDataForm(Authentication authentication) {
        // Check if user is authenticated
        if (authentication == null) {
            logger.info("User not authenticated, redirecting to login");
            return "redirect:/login";
        }

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            logger.info("User not found for email: {}, redirecting to login", authentication.getName());
            return "redirect:/login";
        }

        logger.info("Checking health data existence for user ID: {}", currentUser.getId());
        // Check if data already exists for this user
        boolean hasHealthData = patientServiceClient.checkHealthDataExists(currentUser.getId());
        logger.info("Health data exists for user {}: {}", currentUser.getId(), hasHealthData);
        
        if (hasHealthData) {
            logger.info("User {} has existing health data, redirecting to dashboard", currentUser.getId());
            return "redirect:/patient/dashboard";
        }

        logger.info("User {} has no health data, showing initial data form", currentUser.getId());
        return "patient/initial-data-form";
    }

    @GetMapping("/test-health-check/{userId}")
    @ResponseBody
    public ResponseEntity<?> testHealthDataCheck(@PathVariable Long userId) {
        logger.info("Testing health data check for user: {}", userId);
        try {
            boolean exists = patientServiceClient.checkHealthDataExists(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("healthDataExists", exists);
            result.put("patientServiceUrl", "https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/patient");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error in test health data check: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("userId", userId);
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/submit-initial-data")
    @ResponseBody
    public ResponseEntity<?> submitInitialData(
            @RequestParam Double weight,
            @RequestParam Double height,
            @RequestParam Double waistCircumference,
            Authentication authentication) {

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Validate the data
        if (!dataValidation.validateHealthData(weight, height, waistCircumference)) {
            return ResponseEntity.badRequest().body("Invalid data provided");
        }

        // Create new Data object
        Data patientData = new Data();
        patientData.setUserId(currentUser.getId());
        patientData.setWeight(weight);
        patientData.setHeight(height);
        patientData.setWaistCircumference(waistCircumference);

        // Save the data
        try {
            patientServiceClient.saveHealthData(patientData);
            return ResponseEntity.ok().body("{\"redirect\": \"/patient/dashboard\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to save data");
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        List<MaintenanceNotification> activeNotifications = maintenanceNotificationService.getAllActiveNotifications();
        // Check if user is authenticated
        if (authentication == null) {
            return "redirect:/login";
        }

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get the data for the dashboard
        Data patientData = patientServiceClient.getLatestHealthDataByUserId(currentUser.getId().toString());

        if (patientData == null) {
            return "redirect:/patient/initial-data";
        }

        // Add data to model for the view
        model.addAttribute("notifications", activeNotifications);
        model.addAttribute("data", patientData);
        model.addAttribute("user", currentUser);

        return "patient/patientdashboard";
    }

    @PostMapping("/update-health-data")
    @ResponseBody
    public ResponseEntity<?> updateHealthData(
            @RequestParam Double weight,
            @RequestParam Double height,
            @RequestParam Double waistCircumference,
            @RequestParam(required = false) String bloodPressure,
            @RequestParam(required = false) Double bloodGlucoseLevel,
            Authentication authentication) {

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Find data by user ID
        Data patientData = patientServiceClient.getLatestHealthDataByUserId(currentUser.getId().toString());

        if (patientData == null) {
            return ResponseEntity.badRequest().body("No health data found");
        }

        // Validate all data
        if (!dataValidation.validateHealthData(weight, height, waistCircumference) ||
                !dataValidation.validateBloodPressure(bloodPressure) ||
                !dataValidation.validateBloodGlucose(bloodGlucoseLevel)) {
            return ResponseEntity.badRequest().body("Invalid data provided");
        }

        // Update all fields
        patientData.setWeight(weight);
        patientData.setHeight(height);
        patientData.setWaistCircumference(waistCircumference);
        patientData.setBloodPressure(
                bloodPressure != null && !bloodPressure.trim().isEmpty() ? bloodPressure.trim() : null);
        patientData.setBloodGlucoseLevel(bloodGlucoseLevel);

        try {
            patientServiceClient.saveHealthData(patientData);
            return ResponseEntity.ok().body("{\"redirect\": \"/patient/dashboard\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update data");
        }
    }

    @PostMapping("/delete-medical-reading")
    @ResponseBody
    public ResponseEntity<?> deleteMedicalReading(
            @RequestParam String readingType,
            Authentication authentication) {

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Find data by user ID
        Data patientData = patientServiceClient.getLatestHealthDataByUserId(currentUser.getId().toString());

        if (patientData == null) {
            return ResponseEntity.badRequest().body("No health data found");
        }

        try {
            switch (readingType) {
                case "blood_pressure":
                    patientData.setBloodPressure(null);
                    break;
                case "blood_glucose":
                    patientData.setBloodGlucoseLevel(null);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid reading type");
            }

            patientServiceClient.deleteMedicalReading(currentUser.getId(), readingType);
            return ResponseEntity.ok().body("{\"redirect\": \"/patient/dashboard\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete reading");
        }
    }

    @GetMapping("/appointmentBooking")
    public String navigateToAppointment(Model model, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (authentication == null) {
            return "redirect:/login";
        }

        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get the data for the appointment
        Data patientData = patientServiceClient.getLatestHealthDataByUserId(currentUser.getId().toString());

        if (patientData == null) {
            redirectAttributes.addFlashAttribute("error", "Please complete your health profile first");
            return "redirect:/patient/initial-data";
        }

        // Get all doctors
        List<Users> doctors = appointmentServiceClient.getAllDoctors();

        // Convert doctors to JSON-friendly format
        List<Map<String, Object>> doctorsJson = doctors.stream()
                .map(doctor -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doctor.getId());
                    map.put("name", doctor.getName());
                    map.put("email", doctor.getEmail());
                    return map;
                })
                .collect(Collectors.toList());

        // Add data to model for the appointment booking page
        model.addAttribute("data", patientData);
        model.addAttribute("user", currentUser);
        model.addAttribute("doctorsJson", doctorsJson);

        return "patient/appointmentBooking";
    }

    @GetMapping("/search-doctors")
    @ResponseBody
    public List<Map<String, Object>> searchDoctors(@RequestParam(required = false) String query) {
        List<Users> doctors;
        if (query != null && !query.trim().isEmpty()) {
            // Search for doctors whose names start with the query (case insensitive)
            doctors = appointmentServiceClient.searchDoctors(query.trim());
        } else {
            // If no query, return all doctors
            doctors = appointmentServiceClient.getAllDoctors();
        }

        // Convert to JSON-friendly format
        return doctors.stream()
                .map(doctor -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doctor.getId());
                    map.put("name", doctor.getName());
                    map.put("email", doctor.getEmail());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/get-available-slots")
    @ResponseBody
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam String doctorId,
            @RequestParam String date) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);

            // Get available time slots from appointment service
            List<String> availableSlots = appointmentServiceClient.getAvailableTimeSlots(doctorId, date);
            
            // Convert available slots to the expected format
            List<Map<String, Object>> formattedSlots = new ArrayList<>();
            
            // Generate all possible time slots (9 AM to 5 PM, excluding 12-1 PM)
            LocalTime currentTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(17, 0);

            while (currentTime.isBefore(endTime)) {
                // Skip lunch hour (12-1 PM)
                if (!currentTime.equals(LocalTime.of(12, 0))) {
                    Map<String, Object> slot = new HashMap<>();
                    slot.put("time", currentTime.toString());
                    slot.put("formatted", currentTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
                    slot.put("available", availableSlots.contains(currentTime.toString()));
                    formattedSlots.add(slot);
                }

                currentTime = currentTime.plusMinutes(30);
                if (currentTime.equals(LocalTime.of(13, 0))) {
                    currentTime = LocalTime.of(14, 0);
                }
            }

            return ResponseEntity.ok(formattedSlots);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get available slots: " + e.getMessage());
        }
    }

    @PostMapping("/book-appointment")
    @ResponseBody
    public ResponseEntity<?> bookAppointment(
            @RequestParam String doctorId,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam String reason,
            Authentication authentication) {

        // Check if user is authenticated
        if (authentication == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // Get current user
            Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
            if (currentUser == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse date and time
            LocalDate date = LocalDate.parse(appointmentDate);
            LocalTime time = LocalTime.parse(appointmentTime);

            // Validate appointment date (must be at least 2 days ahead)
            LocalDate minDate = LocalDate.now().plusDays(2);
            if (date.isBefore(minDate)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Appointments must be booked at least 2 days in advance");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if the time slot is available
            boolean hasConflict = appointmentServiceClient.checkAppointmentConflict(doctorId, appointmentDate, appointmentTime);

            if (hasConflict) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "This time slot is no longer available");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new appointment
            Appointment appointment = new Appointment();
            appointment.setUserId(currentUser.getId().toString());
            appointment.setDoctorId(doctorId);
            appointment.setDate(date);
            appointment.setTime(time);
            appointment.setReason(reason);
            appointment.setStatus("PENDING");

            // Save appointment
            Appointment savedAppointment = appointmentServiceClient.createAppointment(appointment);
            
            if (savedAppointment != null) {
                // Return success response with redirect URL
                Map<String, String> response = new HashMap<>();
                response.put("redirect", "/patient/dashboard?success=Appointment booked successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to create appointment");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to book appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model, Authentication authentication) {
        Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentServiceClient.getPatientAppointments(currentUser.getId().toString());

        // Get doctor names for each appointment
        Map<String, String> doctorNames = new HashMap<>();
        for (Appointment appointment : appointments) {
            Users doctor = userServiceClient.getUserById(Long.parseLong(appointment.getDoctorId()));
            if (doctor != null) {
                doctorNames.put(appointment.getDoctorId(), doctor.getName());
            }
        }

        // Filter to show only appointments from the last 30 days or future appointments
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        appointments = appointments.stream()
                .filter(a -> !a.getDate().isBefore(thirtyDaysAgo))
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);
        model.addAttribute("doctorNames", doctorNames);
        model.addAttribute("user", currentUser);
        model.addAttribute("today", LocalDate.now());

        return "patient/appointmentStatus";
    }

    @PostMapping("/delete-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id, Authentication authentication) {
        try {
            Users currentUser = userServiceClient.getUserByEmail(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Appointment appointment = appointmentServiceClient.getAppointmentById(id);
            if (appointment == null) {
                return ResponseEntity.badRequest().body("Appointment not found");
            }

            // Verify that this appointment belongs to the current user
            if (!appointment.getUserId().equals(currentUser.getId().toString())) {
                return ResponseEntity.status(403).body("Not authorized to delete this appointment");
            }

            // Only allow deletion of pending or cancelled appointments
            if (appointment.getStatus().equals("CONFIRMED")) {
                return ResponseEntity.badRequest()
                        .body("Cannot delete confirmed appointments. Please contact your doctor to cancel.");
            }

            if (appointment.getStatus().equals("COMPLETED")) {
                return ResponseEntity.badRequest()
                        .body("Cannot delete completed appointments.");
            }

            boolean deleted = appointmentServiceClient.deleteAppointment(id, currentUser.getId().toString());
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Failed to delete appointment");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete appointment: " + e.getMessage());
        }
    }
}
