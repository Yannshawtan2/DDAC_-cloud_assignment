package com.example.appointmentservice.controller;

import com.example.appointmentservice.dto.AppointmentDto;
import com.example.appointmentservice.model.Appointment;
import com.example.appointmentservice.model.Users;
import com.example.appointmentservice.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/appointment/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Basic CRUD endpoints
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.findById(id);
        return appointment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment saved = appointmentService.save(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Patient endpoints
    @PostMapping("/patient/book")
    public ResponseEntity<Appointment> bookAppointment(
            @RequestParam String userId,
            @RequestParam String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam String reason) {
        try {
            Appointment appointment = appointmentService.bookAppointment(userId, doctorId, date, time, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/patient/{userId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(userId));
    }

    @DeleteMapping("/patient/{userId}/{appointmentId}")
    public ResponseEntity<Void> cancelPatientAppointment(
            @PathVariable String userId,
            @PathVariable Long appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Doctor endpoints
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable String doctorId) {
        return ResponseEntity.ok(appointmentService.findByDoctorId(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<Appointment>> getDoctorAppointmentsByDate(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.findAllAppointmentsByDoctorAndDate(doctorId, date));
    }

    @GetMapping("/doctor/{doctorId}/confirmed/{date}")
    public ResponseEntity<List<Appointment>> getConfirmedAppointmentsByDate(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.findConfirmedAppointmentsByDoctorAndDate(doctorId, date));
    }

    @PostMapping("/doctor/{doctorId}/{appointmentId}/approve")
    public ResponseEntity<Appointment> approveAppointment(
            @PathVariable String doctorId,
            @PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.approveAppointment(appointmentId, doctorId);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/doctor/{doctorId}/{appointmentId}/reject")
    public ResponseEntity<Appointment> rejectAppointment(
            @PathVariable String doctorId,
            @PathVariable Long appointmentId,
            @RequestParam String reason) {
        try {
            Appointment appointment = appointmentService.rejectAppointment(appointmentId, doctorId, reason);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/doctor/{doctorId}/{appointmentId}/complete")
    public ResponseEntity<Appointment> completeAppointment(
            @PathVariable String doctorId,
            @PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.completeAppointment(appointmentId, doctorId);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/doctor/{doctorId}/{appointmentId}/diagnosis")
    public ResponseEntity<Appointment> saveDiagnosis(
            @PathVariable String doctorId,
            @PathVariable Long appointmentId,
            @RequestParam String diagnosisNote) {
        try {
            Appointment appointment = appointmentService.saveDiagnosis(appointmentId, doctorId, diagnosisNote);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "appointment-service"));
    }

    // Doctor search endpoint
    @GetMapping("/search-doctors")
    public ResponseEntity<List<Users>> searchDoctors(@RequestParam(required = false) String name) {
        List<Users> doctors = appointmentService.searchDoctors(name);
        return ResponseEntity.ok(doctors);
    }

    // Get all doctors
    @GetMapping("/doctors")
    public ResponseEntity<List<Users>> getAllDoctors() {
        List<Users> doctors = appointmentService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    // Patient search endpoint
    @GetMapping("/search-patients")
    public ResponseEntity<List<Users>> searchPatients(@RequestParam(required = false) String name) {
        List<Users> patients = appointmentService.searchPatients(name);
        return ResponseEntity.ok(patients);
    }

    // Get user by ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Users> getUserById(@PathVariable Long userId) {
        Optional<Users> user = appointmentService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get doctor appointments with filters
    @GetMapping("/doctor/{doctorId}/filtered")
    public ResponseEntity<List<Appointment>> getDoctorAppointmentsWithFilters(
            @PathVariable String doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String patientName) {
        List<Appointment> appointments = appointmentService.getDoctorAppointmentsWithFilters(doctorId, status,
                patientName);
        return ResponseEntity.ok(appointments);
    }

    // Get appointments with patient names
    @GetMapping("/doctor/{doctorId}/with-patient-names")
    public ResponseEntity<List<Map<String, Object>>> getAppointmentsWithPatientNames(@PathVariable String doctorId) {
        List<Map<String, Object>> appointments = appointmentService.getAppointmentsWithPatientNames(doctorId);
        return ResponseEntity.ok(appointments);
    }

    // Check appointment conflicts
    @GetMapping("/check-conflict")
    public ResponseEntity<Map<String, Object>> checkAppointmentConflict(
            @RequestParam String doctorId,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam(required = false) Long excludeAppointmentId) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            boolean hasConflict = appointmentService.hasAppointmentConflict(doctorId, appointmentDate, appointmentTime,
                    excludeAppointmentId);

            Map<String, Object> response = new HashMap<>();
            response.put("hasConflict", hasConflict);
            response.put("message", hasConflict ? "Time slot is already booked" : "Time slot is available");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid date or time format");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Validate appointment booking
    @PostMapping("/validate-booking")
    public ResponseEntity<Map<String, Object>> validateAppointmentBooking(
            @RequestParam String doctorId,
            @RequestParam String date,
            @RequestParam String time) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            String validationError = appointmentService.validateAppointmentBooking(doctorId, appointmentDate,
                    appointmentTime);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", validationError == null);
            if (validationError != null) {
                response.put("error", validationError);
            } else {
                response.put("message", "Appointment booking is valid");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Invalid date or time format");
            return ResponseEntity.badRequest().body(response);
        }
    }
}