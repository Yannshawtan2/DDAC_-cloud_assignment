package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.model.data_schema.DiagnosisNote;
import com.example.DDac_group18.model.repository.DiagnosisNoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiagnosisNoteService {
    private final DiagnosisNoteRepository repo;
    private final AppointmentService apptService;
    public DiagnosisNoteService(DiagnosisNoteRepository repo, AppointmentService apptService) {
        this.repo = repo;
        this.apptService = apptService;
    }

    public List<DiagnosisNote> findAll() {
        return repo.findAll();
    }

    public DiagnosisNote findById(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new RuntimeException("Note not found: " + id));
    }

    public DiagnosisNote save(Long appointmentId, String noteText) {
        Appointment appt = apptService.findById(appointmentId);
        if (appt == null) throw new RuntimeException("Appointment not found");
        DiagnosisNote dn = new DiagnosisNote();
        dn.setAppointment(appt);
        dn.setDiagnosisNote(noteText);
        return repo.save(dn);
    }

    /** edit **/
    public DiagnosisNote update(Long id, String noteText) {
        DiagnosisNote dn = findById(id);
        dn.setDiagnosisNote(noteText);
        return repo.save(dn);
    }

    /** delete **/
    public void delete(Long id) {
        repo.deleteById(id);
    }

    public DiagnosisNote findByAppointmentId(Long appointmentId) {
        return repo.findByAppointment_Id(appointmentId);
    }

    public DiagnosisNote save(DiagnosisNote note) {
        return repo.save(note);
    }
}
