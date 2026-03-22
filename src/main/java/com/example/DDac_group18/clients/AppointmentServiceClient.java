package com.example.DDac_group18.clients;

import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class AppointmentServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceClient.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${appointment.service.url:https://your-appointment-lambda-url/dev}")
    private String appointmentServiceUrl;
    
    @Autowired
    private UserServiceClient userServiceClient;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Doctor search methods
    public List<Users> searchDoctors(String name) {
        try {
            String url = appointmentServiceUrl + "/api/appointments/search-doctors";
            if (name != null && !name.trim().isEmpty()) {
                url += "?name=" + name;
            }
            ResponseEntity<Users[]> response = restTemplate.getForEntity(url, Users[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error searching doctors: {}", e.getMessage());
            return Arrays.asList();
        }
    }

    public List<Users> getAllDoctors() {
        try {
            String url = appointmentServiceUrl + "/api/appointments/doctors";
            ResponseEntity<Users[]> response = restTemplate.getForEntity(url, Users[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error getting all doctors: {}", e.getMessage());
            return Arrays.asList();
        }
    }

    // Appointment management methods
    public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
        try {
            String url = appointmentServiceUrl + "/api/appointments/doctor/" + doctorId;
            ResponseEntity<Appointment[]> response = restTemplate.getForEntity(url, Appointment[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error getting appointments for doctor {}: {}", doctorId, e.getMessage());
            return Arrays.asList();
        }
    }

    public List<Appointment> getPatientAppointments(String userId) {
        try {
            String url = appointmentServiceUrl + "/api/appointments/patient/" + userId;
            ResponseEntity<Appointment[]> response = restTemplate.getForEntity(url, Appointment[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error getting appointments for patient {}: {}", userId, e.getMessage());
            return Arrays.asList();
        }
    }

    public Appointment getAppointmentById(Long appointmentId) {
        try {
            String url = appointmentServiceUrl + "/api/appointments/" + appointmentId;
            ResponseEntity<Appointment> response = restTemplate.getForEntity(url, Appointment.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error getting appointment {}: {}", appointmentId, e.getMessage());
            return null;
        }
    }

    public List<Appointment> filterAppointments(List<Appointment> appointments, String status, String patientName) {
        try {
            // This could be enhanced to call a microservice endpoint for filtering
            // For now, implementing basic filtering logic
            return appointments.stream()
                .filter(apt -> status == null || status.isEmpty() || apt.getStatus().equalsIgnoreCase(status))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("Error filtering appointments: {}", e.getMessage());
            return appointments;
        }
    }

    public List<Appointment> searchAppointmentsByPatientName(List<Appointment> appointments, String patientName) {
        try {
            if (patientName == null || patientName.trim().isEmpty()) {
                return appointments;
            }
            // Since appointments don't have patient names directly, return all appointments
            // Patient name filtering would need to be done after fetching patient details
            return appointments;
        } catch (Exception e) {
            logger.error("Error searching appointments by patient name: {}", e.getMessage());
            return appointments;
        }
    }

    public Map<String, String> getPatientNamesForAppointments(List<Appointment> appointments) {
        try {
            Map<String, String> patientNames = new HashMap<>();
            for (Appointment appointment : appointments) {
                if (appointment.getUserId() != null) {
                    Users patient = userServiceClient.getUserById(Long.valueOf(appointment.getUserId()));
                    if (patient != null) {
                        patientNames.put(appointment.getUserId(), patient.getName());
                    }
                }
            }
            return patientNames;
        } catch (Exception e) {
            logger.error("Error getting patient names: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public boolean approveAppointment(Long appointmentId, String doctorId) {
        try {
            logger.info("Approving appointment {} by doctor {}", appointmentId, doctorId);
            
            Map<String, Object> request = new HashMap<>();
            request.put("doctorId", doctorId);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                appointmentServiceUrl + "/api/appointments/" + appointmentId + "/approve",
                HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error approving appointment {}: {}", appointmentId, e.getMessage());
            return false;
        }
    }

    public boolean rejectAppointment(Long appointmentId, String doctorId, String reason) {
        try {
            logger.info("Rejecting appointment {} by doctor {} with reason: {}", appointmentId, doctorId, reason);
            
            Map<String, Object> request = new HashMap<>();
            request.put("doctorId", doctorId);
            request.put("reason", reason);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                appointmentServiceUrl + "/api/appointments/" + appointmentId + "/reject",
                HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error rejecting appointment {}: {}", appointmentId, e.getMessage());
            return false;
        }
    }

    public boolean completeAppointment(Long appointmentId, String doctorId) {
        try {
            logger.info("Completing appointment {} by doctor {}", appointmentId, doctorId);
            
            Map<String, Object> request = new HashMap<>();
            request.put("doctorId", doctorId);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                appointmentServiceUrl + "/api/appointments/" + appointmentId + "/complete",
                HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error completing appointment {}: {}", appointmentId, e.getMessage());
            return false;
        }
    }

    public boolean saveDiagnosis(Long appointmentId, String doctorId, String diagnosis) {
        try {
            logger.info("Saving diagnosis for appointment {} by doctor {}", appointmentId, doctorId);
            
            Map<String, Object> request = new HashMap<>();
            request.put("doctorId", doctorId);
            request.put("diagnosis", diagnosis);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                appointmentServiceUrl + "/api/appointments/" + appointmentId + "/diagnosis",
                HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error saving diagnosis for appointment {}: {}", appointmentId, e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointment(Long appointmentId, String doctorId) {
        try {
            logger.info("Deleting appointment {} by doctor {}", appointmentId, doctorId);
            
            String url = UriComponentsBuilder.fromUriString(appointmentServiceUrl + "/api/appointments/" + appointmentId)
                .queryParam("doctorId", doctorId)
                .toUriString();
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error deleting appointment {}: {}", appointmentId, e.getMessage());
            return false;
        }
    }

    public Appointment createAppointment(Appointment appointment) {
        try {
            logger.info("Creating appointment for patient {} with doctor {}", 
                appointment.getUserId(), appointment.getDoctorId());
            
            HttpEntity<Appointment> entity = new HttpEntity<>(appointment, createHeaders());
            ResponseEntity<Appointment> response = restTemplate.postForEntity(
                appointmentServiceUrl + "/api/appointments", entity, Appointment.class);
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error creating appointment: {}", e.getMessage());
            return null;
        }
    }

    public boolean checkAppointmentConflict(String doctorId, String date, String time) {
        try {
            String url = UriComponentsBuilder.fromUriString(appointmentServiceUrl + "/api/appointments/check-conflict")
                .queryParam("doctorId", doctorId)
                .queryParam("date", date)
                .queryParam("time", time)
                .toUriString();
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object hasConflict = response.getBody().get("hasConflict");
                return Boolean.TRUE.equals(hasConflict);
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking appointment conflict: {}", e.getMessage());
            return true; // Assume conflict on error for safety
        }
    }

    public List<String> getAvailableTimeSlots(String doctorId, String date) {
        try {
            String url = UriComponentsBuilder.fromUriString(appointmentServiceUrl + "/api/appointments/available-slots")
                .queryParam("doctorId", doctorId)
                .queryParam("date", date)
                .toUriString();
            
            ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error getting available time slots: {}", e.getMessage());
            return Arrays.asList();
        }
    }
}