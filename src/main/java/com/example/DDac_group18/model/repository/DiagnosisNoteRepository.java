package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.DiagnosisNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisNoteRepository extends JpaRepository<DiagnosisNote, Long> {
    DiagnosisNote findByAppointment_Id(Long appointmentId);
}
