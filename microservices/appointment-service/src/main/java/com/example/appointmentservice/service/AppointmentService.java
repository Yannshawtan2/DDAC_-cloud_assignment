package com.example.appointmentservice.service;

import com.example.appointmentservice.model.Appointment;
import com.example.appointmentservice.model.Users;
import com.example.appointmentservice.repository.AppointmentRepository;
import com.example.appointmentservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Basic CRUD operations
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    // Patient appointment functions
    public List<Appointment> findByUserId(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public Appointment bookAppointment(String userId, String doctorId, LocalDate date, LocalTime time, String reason) {
        // Check for conflicts
        boolean hasConflict = appointmentRepository.findConfirmedAppointmentsByDoctorAndDate(doctorId, date)
                .stream()
                .anyMatch(a -> a.getTime().equals(time));

        if (hasConflict) {
            throw new RuntimeException("Time slot is no longer available");
        }

        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setDoctorId(doctorId);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setReason(reason);
        appointment.setStatus("PENDING");

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public void cancelAppointment(Long appointmentId, String userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this appointment");
        }

        appointmentRepository.deleteById(appointmentId);
    }

    // Doctor appointment functions
    public List<Appointment> findByDoctorId(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> findConfirmedAppointmentsByDoctorAndDate(String doctorId, LocalDate date) {
        return appointmentRepository.findConfirmedAppointmentsByDoctorAndDate(doctorId, date);
    }

    public List<Appointment> findAllAppointmentsByDoctorAndDate(String doctorId, LocalDate date) {
        return appointmentRepository.findAllAppointmentsByDoctorAndDate(doctorId, date);
    }

    public Appointment approveAppointment(Long appointmentId, String doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("Not authorized to manage this appointment");
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            throw new RuntimeException("Can only approve pending appointments");
        }

        // Check for time slot conflicts
        boolean hasConflict = appointmentRepository.findConfirmedAppointmentsByDoctorAndDate(
                doctorId, appointment.getDate())
                .stream()
                .anyMatch(a -> a.getTime().equals(appointment.getTime()));

        if (hasConflict) {
            throw new RuntimeException("Time slot is no longer available");
        }

        appointment.setStatus("CONFIRMED");
        return appointmentRepository.save(appointment);
    }

    public Appointment rejectAppointment(Long appointmentId, String doctorId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("Not authorized to manage this appointment");
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            throw new RuntimeException("Can only reject pending appointments");
        }

        appointment.setStatus("CANCELLED");
        appointment.setRejectReason(reason);
        return appointmentRepository.save(appointment);
    }

    public Appointment completeAppointment(Long appointmentId, String doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("Not authorized to manage this appointment");
        }

        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new RuntimeException("Can only complete confirmed appointments");
        }

        appointment.setStatus("COMPLETED");
        return appointmentRepository.save(appointment);
    }

    public Appointment saveDiagnosis(Long appointmentId, String doctorId, String diagnosisNote) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("Not authorized to manage this appointment");
        }

        appointment.setDiagnosisNote(diagnosisNote);
        return appointmentRepository.save(appointment);
    }

    // Doctor search functionality
    public List<Users> searchDoctors(String name) {
        if (name == null || name.trim().isEmpty()) {
            return userRepository.findByRole(Users.Role.DOCTOR);
        }
        return userRepository.findByNameContainingAndRole(name.trim(), Users.Role.DOCTOR);
    }

    public List<Users> getAllDoctors() {
        return userRepository.findByRole(Users.Role.DOCTOR);
    }

    // Patient search functionality
    public List<Users> searchPatients(String name) {
        if (name == null || name.trim().isEmpty()) {
            return userRepository.findByRole(Users.Role.PATIENT);
        }
        return userRepository.findByNameContainingAndRole(name.trim(), Users.Role.PATIENT);
    }

    // Get user by ID
    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Advanced appointment filtering
    public List<Appointment> getDoctorAppointmentsWithFilters(String doctorId, String status, String patientName) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);

        // Filter by status if provided
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            appointments = appointments.stream()
                    .filter(apt -> status.equals(apt.getStatus()))
                    .collect(Collectors.toList());
        }

        // Filter by patient name if provided
        if (patientName != null && !patientName.trim().isEmpty()) {
            appointments = appointments.stream()
                    .filter(apt -> {
                        Optional<Users> patient = userRepository.findById(Long.valueOf(apt.getUserId()));
                        return patient.isPresent() &&
                                patient.get().getName().toLowerCase().contains(patientName.toLowerCase());
                    })
                    .collect(Collectors.toList());
        }

        return appointments;
    }

    // Check for appointment conflicts
    public boolean hasAppointmentConflict(String doctorId, LocalDate date, LocalTime time, Long excludeAppointmentId) {
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorId(doctorId);

        return existingAppointments.stream()
                .filter(apt -> excludeAppointmentId == null || !apt.getId().equals(excludeAppointmentId))
                .anyMatch(apt -> apt.getDate().equals(date) && apt.getTime().equals(time)
                        && "confirmed".equals(apt.getStatus()));
    }

    // Get appointments with patient names
    public List<Map<String, Object>> getAppointmentsWithPatientNames(String doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);

        return appointments.stream().map(appointment -> {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("appointment", appointment);

            Optional<Users> patient = userRepository.findById(Long.valueOf(appointment.getUserId()));
            appointmentData.put("patientName", patient.map(Users::getName).orElse("Unknown"));

            return appointmentData;
        }).collect(Collectors.toList());
    }

    // Validate appointment booking
    public String validateAppointmentBooking(String doctorId, LocalDate date, LocalTime time) {
        // Check if the appointment is in the past
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            return "Cannot book appointments for past dates.";
        }

        // Check for conflicts
        if (hasAppointmentConflict(doctorId, date, time, null)) {
            return "This time slot is already booked. Please choose a different time.";
        }

        return null; // No validation errors
    }
}