package com.example.diagnosisnotes.repository;

import com.example.diagnosisnotes.model.DiagnosisNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosisNoteRepository extends JpaRepository<DiagnosisNote, Long> {
    Optional<DiagnosisNote> findByAppointmentId(Long appointmentId);
} 