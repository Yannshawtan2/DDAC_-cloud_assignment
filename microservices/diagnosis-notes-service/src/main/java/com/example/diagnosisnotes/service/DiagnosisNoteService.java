package com.example.diagnosisnotes.service;

import com.example.diagnosisnotes.dto.CreateDiagnosisNoteRequest;
import com.example.diagnosisnotes.dto.DiagnosisNoteDto;
import com.example.diagnosisnotes.dto.UpdateDiagnosisNoteRequest;
import com.example.diagnosisnotes.model.DiagnosisNote;
import com.example.diagnosisnotes.repository.DiagnosisNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiagnosisNoteService {
    private static final Logger logger = LoggerFactory.getLogger(DiagnosisNoteService.class);

    @Autowired
    private DiagnosisNoteRepository repository;

    public List<DiagnosisNoteDto> getAllDiagnosisNotes() {
        logger.info("Getting all diagnosis notes");
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<DiagnosisNoteDto> getDiagnosisNoteById(Long id) {
        logger.info("Getting diagnosis note with id: {}", id);
        return repository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<DiagnosisNoteDto> getDiagnosisNoteByAppointmentId(Long appointmentId) {
        logger.info("Getting diagnosis note for appointment: {}", appointmentId);
        return repository.findByAppointmentId(appointmentId)
                .map(this::convertToDto);
    }

    public DiagnosisNoteDto createDiagnosisNote(CreateDiagnosisNoteRequest request) {
        logger.info("Creating diagnosis note for appointment: {}", request.getAppointmentId());
        
        if (request.getAppointmentId() == null) {
            throw new IllegalArgumentException("Appointment ID is required");
        }
        
        if (request.getDiagnosisNote() == null || request.getDiagnosisNote().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis note content is required");
        }

        // Check if diagnosis note already exists for this appointment
        Optional<DiagnosisNote> existingNote = repository.findByAppointmentId(request.getAppointmentId());
        if (existingNote.isPresent()) {
            throw new IllegalArgumentException("Diagnosis note already exists for appointment: " + request.getAppointmentId());
        }

        DiagnosisNote diagnosisNote = new DiagnosisNote();
        diagnosisNote.setAppointmentId(request.getAppointmentId());
        diagnosisNote.setName(request.getName());
        diagnosisNote.setGender(request.getGender());
        diagnosisNote.setAge(request.getAge());
        diagnosisNote.setAppointmentDate(request.getAppointmentDate());
        diagnosisNote.setForeignId(request.getForeignId());
        diagnosisNote.setHeight(request.getHeight());
        diagnosisNote.setWeight(request.getWeight());
        diagnosisNote.setBloodSugarIndex(request.getBloodSugarIndex());
        diagnosisNote.setDiagnosisNote(request.getDiagnosisNote());

        DiagnosisNote savedNote = repository.save(diagnosisNote);
        logger.info("Created diagnosis note with id: {}", savedNote.getId());
        
        return convertToDto(savedNote);
    }

    public DiagnosisNoteDto updateDiagnosisNote(Long id, UpdateDiagnosisNoteRequest request) {
        logger.info("Updating diagnosis note with id: {}", id);
        
        if (request.getDiagnosisNote() == null || request.getDiagnosisNote().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis note content is required");
        }

        DiagnosisNote diagnosisNote = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis note not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            diagnosisNote.setName(request.getName());
        }
        if (request.getGender() != null) {
            diagnosisNote.setGender(request.getGender());
        }
        if (request.getAge() != null) {
            diagnosisNote.setAge(request.getAge());
        }
        if (request.getAppointmentDate() != null) {
            diagnosisNote.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getForeignId() != null) {
            diagnosisNote.setForeignId(request.getForeignId());
        }
        if (request.getHeight() != null) {
            diagnosisNote.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            diagnosisNote.setWeight(request.getWeight());
        }
        if (request.getBloodSugarIndex() != null) {
            diagnosisNote.setBloodSugarIndex(request.getBloodSugarIndex());
        }
        
        diagnosisNote.setDiagnosisNote(request.getDiagnosisNote());

        DiagnosisNote updatedNote = repository.save(diagnosisNote);
        logger.info("Updated diagnosis note with id: {}", updatedNote.getId());
        
        return convertToDto(updatedNote);
    }

    public void deleteDiagnosisNote(Long id) {
        logger.info("Deleting diagnosis note with id: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Diagnosis note not found with id: " + id);
        }
        
        repository.deleteById(id);
        logger.info("Deleted diagnosis note with id: {}", id);
    }

    public DiagnosisNoteDto updateDiagnosisNoteByAppointmentId(Long appointmentId, UpdateDiagnosisNoteRequest request) {
        logger.info("Updating diagnosis note for appointment: {}", appointmentId);
        
        DiagnosisNote diagnosisNote = repository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Diagnosis note not found for appointment: " + appointmentId));

        return updateDiagnosisNote(diagnosisNote.getId(), request);
    }

    public void deleteDiagnosisNoteByAppointmentId(Long appointmentId) {
        logger.info("Deleting diagnosis note for appointment: {}", appointmentId);
        
        Optional<DiagnosisNote> diagnosisNote = repository.findByAppointmentId(appointmentId);
        if (diagnosisNote.isPresent()) {
            repository.deleteById(diagnosisNote.get().getId());
            logger.info("Deleted diagnosis note for appointment: {}", appointmentId);
        } else {
            throw new RuntimeException("Diagnosis note not found for appointment: " + appointmentId);
        }
    }

    private DiagnosisNoteDto convertToDto(DiagnosisNote diagnosisNote) {
        return new DiagnosisNoteDto(
                diagnosisNote.getId(),
                diagnosisNote.getAppointmentId(),
                diagnosisNote.getName(),
                diagnosisNote.getGender(),
                diagnosisNote.getAge(),
                diagnosisNote.getAppointmentDate(),
                diagnosisNote.getForeignId(),
                diagnosisNote.getHeight(),
                diagnosisNote.getWeight(),
                diagnosisNote.getBloodSugarIndex(),
                diagnosisNote.getDiagnosisNote()
        );
    }
} 