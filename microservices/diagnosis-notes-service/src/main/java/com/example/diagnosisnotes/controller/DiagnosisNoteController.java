package com.example.diagnosisnotes.controller;

import com.example.diagnosisnotes.dto.CreateDiagnosisNoteRequest;
import com.example.diagnosisnotes.dto.DiagnosisNoteDto;
import com.example.diagnosisnotes.dto.UpdateDiagnosisNoteRequest;
import com.example.diagnosisnotes.service.DiagnosisNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("DiagnosisNotes/diagnosis-notes")
@CrossOrigin(origins = "*")
public class DiagnosisNoteController {
    private static final Logger logger = LoggerFactory.getLogger(DiagnosisNoteController.class);

    @Autowired
    private DiagnosisNoteService diagnosisNoteService;

    @GetMapping
    public ResponseEntity<?> getAllDiagnosisNotes() {
        try {
            logger.info("Getting all diagnosis notes");
            List<DiagnosisNoteDto> diagnosisNotes = diagnosisNoteService.getAllDiagnosisNotes();
            return ResponseEntity.ok(diagnosisNotes);
        } catch (Exception e) {
            logger.error("Error getting diagnosis notes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve diagnosis notes"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiagnosisNoteById(@PathVariable Long id) {
        try {
            logger.info("Getting diagnosis note with id: {}", id);
            Optional<DiagnosisNoteDto> diagnosisNote = diagnosisNoteService.getDiagnosisNoteById(id);
            
            if (diagnosisNote.isPresent()) {
                return ResponseEntity.ok(diagnosisNote.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting diagnosis note {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve diagnosis note"));
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getDiagnosisNoteByAppointmentId(@PathVariable Long appointmentId) {
        try {
            logger.info("Getting diagnosis note for appointment: {}", appointmentId);
            Optional<DiagnosisNoteDto> diagnosisNote = diagnosisNoteService.getDiagnosisNoteByAppointmentId(appointmentId);
            
            if (diagnosisNote.isPresent()) {
                return ResponseEntity.ok(diagnosisNote.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting diagnosis note for appointment {}: {}", appointmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve diagnosis note"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createDiagnosisNote(@Valid @RequestBody CreateDiagnosisNoteRequest request) {
        try {
            logger.info("Creating diagnosis note for appointment: {}", request.getAppointmentId());
            DiagnosisNoteDto diagnosisNote = diagnosisNoteService.createDiagnosisNote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisNote);
        } catch (IllegalArgumentException e) {
            logger.warn("Diagnosis note creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during diagnosis note creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiagnosisNote(@PathVariable Long id, 
                                               @Valid @RequestBody UpdateDiagnosisNoteRequest request) {
        try {
            logger.info("Updating diagnosis note with id: {}", id);
            DiagnosisNoteDto diagnosisNote = diagnosisNoteService.updateDiagnosisNote(id, request);
            return ResponseEntity.ok(diagnosisNote);
        } catch (IllegalArgumentException e) {
            logger.warn("Diagnosis note update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating diagnosis note {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update diagnosis note"));
        } catch (Exception e) {
            logger.error("Unexpected error during diagnosis note update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> updateDiagnosisNoteByAppointmentId(@PathVariable Long appointmentId,
                                                               @Valid @RequestBody UpdateDiagnosisNoteRequest request) {
        try {
            logger.info("Updating diagnosis note for appointment: {}", appointmentId);
            DiagnosisNoteDto diagnosisNote = diagnosisNoteService.updateDiagnosisNoteByAppointmentId(appointmentId, request);
            return ResponseEntity.ok(diagnosisNote);
        } catch (IllegalArgumentException e) {
            logger.warn("Diagnosis note update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating diagnosis note for appointment {}: {}", appointmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update diagnosis note"));
        } catch (Exception e) {
            logger.error("Unexpected error during diagnosis note update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiagnosisNote(@PathVariable Long id) {
        try {
            logger.info("Deleting diagnosis note with id: {}", id);
            diagnosisNoteService.deleteDiagnosisNote(id);
            return ResponseEntity.ok(Map.of("message", "Diagnosis note deleted successfully", "id", id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting diagnosis note {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete diagnosis note"));
        } catch (Exception e) {
            logger.error("Unexpected error during diagnosis note deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> deleteDiagnosisNoteByAppointmentId(@PathVariable Long appointmentId) {
        try {
            logger.info("Deleting diagnosis note for appointment: {}", appointmentId);
            diagnosisNoteService.deleteDiagnosisNoteByAppointmentId(appointmentId);
            return ResponseEntity.ok(Map.of("message", "Diagnosis note deleted successfully", "appointmentId", appointmentId));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting diagnosis note for appointment {}: {}", appointmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete diagnosis note"));
        } catch (Exception e) {
            logger.error("Unexpected error during diagnosis note deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
} 