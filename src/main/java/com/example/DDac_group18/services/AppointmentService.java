package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.Appointment;
import com.example.DDac_group18.model.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository repo;
    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }
    public List<Appointment> findAll() {
        return repo.findAll();
    }
    public Appointment findById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
