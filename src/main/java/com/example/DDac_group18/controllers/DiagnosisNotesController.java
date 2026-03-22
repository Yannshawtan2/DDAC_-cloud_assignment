package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.model.data_schema.DiagnosisNote;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.data_schema.Data;
import com.example.DDac_group18.services.AppointmentService;
import com.example.DDac_group18.services.DiagnosisNoteService;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.model.repository.AppointmentRepository;
import com.example.DDac_group18.model.repository.DataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import java.util.List;

@Controller
// @RequestMapping("/api/appointments")
public class DiagnosisNotesController {
    private final AppointmentService apptService;
    private final DiagnosisNoteService dnService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DataRepository dataRepository;

    public DiagnosisNotesController(AppointmentService apptService,
            DiagnosisNoteService dnService,
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            DataRepository dataRepository) {
        this.apptService = apptService;
        this.dnService = dnService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.dataRepository = dataRepository;
    }

    @GetMapping("/diagnosis-notes")
    public String page(@RequestParam(value = "appointmentId", required = false) Long appointmentId, Model model) {
        model.addAttribute("appointments", apptService.findAll());
        if (appointmentId != null) {
            model.addAttribute("selectedAppointmentId", appointmentId);
            Appointment appt = appointmentRepository.findById(appointmentId).orElse(null);
            model.addAttribute("selectedAppointment", appt);
            if (appt != null) {
                // Get latest Data for the patient
                Data patientData = dataRepository
                        .findTopByUserIdOrderByWeightUpdatedAtDesc(Long.parseLong(appt.getUserId()));
                model.addAttribute("patientData", patientData);
                // Get patient's User object for name
                Users patientUser = userRepository.findById(Long.parseLong(appt.getUserId())).orElse(null);
                model.addAttribute("patientUser", patientUser);
                // Get diagnosis note for this appointment
                DiagnosisNote diagnosisNote = dnService.findByAppointmentId(appointmentId);
                model.addAttribute("diagnosisNote", diagnosisNote != null ? diagnosisNote : new DiagnosisNote());
            }
        }
        return "doctor/diagnosis-notes";
    }

    // ============== Table 1 APIs ==============

    // @ResponseBody
    // @GetMapping("/api/appointments")
    // public List<Appointment> allAppointments() {
    // return apptService.findAll();
    // }

    // @ResponseBody
    // @GetMapping("/api/appointments/{id}")
    // public Appointment oneAppointment(@PathVariable Long id) {
    // return apptService.findById(id);
    // }

    // // ============== Table 2 APIs ==============

    // @ResponseBody
    // @GetMapping("/api/diagnosis-notes")
    // public List<DiagnosisNote> getAllNotes() {
    // return dnService.findAll();
    // }

    // @ResponseBody
    // @GetMapping("/api/diagnosis-notes/{id}")
    // public DiagnosisNote getNoteById(@PathVariable Long id) {
    // return dnService.findById(id);
    // }

    // @ResponseBody
    // @PostMapping("/api/diagnosis-notes")
    // public DiagnosisNote createNote(@RequestBody SaveDto dto) {
    // return dnService.save(dto.getAppointmentId(), dto.getDiagnosisNote());
    // }

    // @ResponseBody
    // @PutMapping("/api/diagnosis-notes/{id}")
    // public DiagnosisNote updateNote(@PathVariable Long id, @RequestBody UpdateDto
    // dto) {
    // return dnService.update(id, dto.getDiagnosisNote());
    // }

    // @ResponseBody
    // @DeleteMapping("/api/diagnosis-notes/{id}")
    // public void deleteNote(@PathVariable Long id) {
    // dnService.delete(id);
    // }

    @PostMapping("/api/diagnosis-notes/save")
    @ResponseBody
    public ResponseEntity<?> saveDiagnosisNote(@RequestParam Long appointmentId,
            @RequestParam String diagnosisNote) {
        DiagnosisNote note = dnService.findByAppointmentId(appointmentId);
        if (note == null) {
            note = new DiagnosisNote();
            Appointment appt = appointmentRepository.findById(appointmentId).orElse(null);
            if (appt == null)
                return ResponseEntity.badRequest().body("Appointment not found");
            note.setAppointment(appt);
            // Optionally set patient info from appt
        }
        note.setDiagnosisNote(diagnosisNote);
        dnService.save(note);
        return ResponseEntity.ok().build();
    }

    // Delete diagnosis note
    @DeleteMapping("/api/diagnosis-notes/delete")
    @ResponseBody
    public ResponseEntity<?> deleteDiagnosisNote(@RequestParam Long appointmentId) {
        DiagnosisNote note = dnService.findByAppointmentId(appointmentId);
        if (note != null) {
            dnService.delete(note.getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Diagnosis note not found");
    }

    // ============== Approve/Reject Appointment (moved from patientController)
    // ==============
    @PostMapping("/approve-appointment/{id}")
    @ResponseBody
    public ResponseEntity<?> approveAppointment(@PathVariable Long id, Authentication authentication) {
        try {
            Users doctor = userRepository.findByEmail(authentication.getName());
            if (doctor == null || !doctor.getRole().toString().equals("doctor")) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Verify this appointment belongs to the doctor
            if (!appointment.getDoctorId().equals(doctor.getId().toString())) {
                return ResponseEntity.status(403).body("Not authorized to manage this appointment");
            }

            // Only allow approval of pending appointments
            if (!appointment.getStatus().equals("PENDING")) {
                return ResponseEntity.badRequest().body("Can only approve pending appointments");
            }

            // Check for time slot conflicts
            boolean hasConflict = appointmentRepository.findConfirmedAppointmentsByDoctorAndDate(
                    doctor.getId().toString(), appointment.getDate())
                    .stream()
                    .anyMatch(a -> a.getTime().equals(appointment.getTime()));

            if (hasConflict) {
                return ResponseEntity.badRequest().body("Time slot is no longer available");
            }

            appointment.setStatus("CONFIRMED");
            appointmentRepository.save(appointment);
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
            Users doctor = userRepository.findByEmail(authentication.getName());
            if (doctor == null || !doctor.getRole().toString().equals("doctor")) {
                return ResponseEntity.status(403).body("Not authorized");
            }

            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Verify this appointment belongs to the doctor
            if (!appointment.getDoctorId().equals(doctor.getId().toString())) {
                return ResponseEntity.status(403).body("Not authorized to manage this appointment");
            }

            // Only allow rejection of pending appointments
            if (!appointment.getStatus().equals("PENDING")) {
                return ResponseEntity.badRequest().body("Can only reject pending appointments");
            }

            appointment.setStatus("CANCELLED");
            appointment.setRejectReason(reason);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reject appointment: " + e.getMessage());
        }
    }

    // public static class SaveDto {
    // private Long appointmentId;
    // private String diagnosisNote;
    // public Long getAppointmentId() { return appointmentId; }
    // public void setAppointmentId(Long appointmentId) { this.appointmentId =
    // appointmentId; }
    // public String getDiagnosisNote() { return diagnosisNote; }
    // public void setDiagnosisNote(String diagnosisNote) { this.diagnosisNote =
    // diagnosisNote; }
    // }

    // public static class UpdateDto {
    // private String diagnosisNote;
    // public String getDiagnosisNote() { return diagnosisNote; }
    // public void setDiagnosisNote(String diagnosisNote) { this.diagnosisNote =
    // diagnosisNote; }
    // }
}