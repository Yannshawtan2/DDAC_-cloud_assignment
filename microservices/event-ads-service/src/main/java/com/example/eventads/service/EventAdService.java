package com.example.eventads.service;

import com.example.eventads.dto.CreateEventAdRequest;
import com.example.eventads.dto.EventAdDto;
import com.example.eventads.dto.UpdateEventAdRequest;
import com.example.eventads.model.EventAd;
import com.example.eventads.repository.EventAdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventAdService {
    private static final Logger logger = LoggerFactory.getLogger(EventAdService.class);

    @Autowired
    private EventAdRepository repository;

    public List<EventAdDto> getAllEventAds() {
        logger.info("Getting all event ads");
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<EventAdDto> getEventAdById(Long id) {
        logger.info("Getting event ad with id: {}", id);
        return repository.findById(id)
                .map(this::convertToDto);
    }

    public EventAdDto createEventAd(CreateEventAdRequest request) {
        logger.info("Creating event ad with title: {}", request.getTitle());
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }

        EventAd eventAd = new EventAd();
        eventAd.setTitle(request.getTitle());
        eventAd.setContent(request.getContent());
        eventAd.setImageData(request.getImageData());

        EventAd savedEventAd = repository.save(eventAd);
        logger.info("Created event ad with id: {}", savedEventAd.getId());
        
        return convertToDto(savedEventAd);
    }

    public EventAdDto updateEventAd(Long id, UpdateEventAdRequest request) {
        logger.info("Updating event ad with id: {}", id);
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }

        EventAd eventAd = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event ad not found with id: " + id));

        eventAd.setTitle(request.getTitle());
        eventAd.setContent(request.getContent());
        
        // Only update image data if provided
        if (request.getImageData() != null) {
            eventAd.setImageData(request.getImageData());
        }

        EventAd updatedEventAd = repository.save(eventAd);
        logger.info("Updated event ad with id: {}", updatedEventAd.getId());
        
        return convertToDto(updatedEventAd);
    }

    public void deleteEventAd(Long id) {
        logger.info("Deleting event ad with id: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Event ad not found with id: " + id);
        }
        
        repository.deleteById(id);
        logger.info("Deleted event ad with id: {}", id);
    }

    private EventAdDto convertToDto(EventAd eventAd) {
        EventAdDto dto = new EventAdDto(
                eventAd.getId(),
                eventAd.getTitle(),
                eventAd.getContent(),
                null // image will be set below if available
        );
        
        // Convert image data to Base64 if available
        if (eventAd.getImageData() != null && eventAd.getImageData().length > 0) {
            String base64 = Base64.getEncoder().encodeToString(eventAd.getImageData());
            dto.setImage("data:image/jpeg;base64," + base64);
        }
        
        return dto;
    }
} 