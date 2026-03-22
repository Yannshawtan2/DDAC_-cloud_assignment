package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.EventAd;
import com.example.DDac_group18.model.repository.EventAdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventAdService {

    @Autowired
    private EventAdRepository repository;

    // Retrieve all event ads
    public List<EventAd> findAll() {
        return repository.findAll();
    }

    // Find by ID
    public Optional<EventAd> findById(Long id) {
        return repository.findById(id);
    }

    // Create or update
    public EventAd save(EventAd eventAd) {
        return repository.save(eventAd);
    }

    // Delete by ID
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}