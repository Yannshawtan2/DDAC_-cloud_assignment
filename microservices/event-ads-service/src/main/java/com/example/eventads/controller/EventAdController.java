package com.example.eventads.controller;

import com.example.eventads.dto.CreateEventAdRequest;
import com.example.eventads.dto.EventAdDto;
import com.example.eventads.dto.UpdateEventAdRequest;
import com.example.eventads.service.EventAdService;
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
@RequestMapping("EventAds/event-ads")
@CrossOrigin(origins = "*")
public class EventAdController {
    private static final Logger logger = LoggerFactory.getLogger(EventAdController.class);

    @Autowired
    private EventAdService eventAdService;

    @GetMapping
    public ResponseEntity<?> getAllEventAds() {
        try {
            logger.info("Getting all event ads");
            List<EventAdDto> eventAds = eventAdService.getAllEventAds();
            return ResponseEntity.ok(eventAds);
        } catch (Exception e) {
            logger.error("Error getting event ads: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve event ads"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventAdById(@PathVariable Long id) {
        try {
            logger.info("Getting event ad with id: {}", id);
            Optional<EventAdDto> eventAd = eventAdService.getEventAdById(id);
            
            if (eventAd.isPresent()) {
                return ResponseEntity.ok(eventAd.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting event ad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve event ad"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createEventAd(@Valid @RequestBody CreateEventAdRequest request) {
        try {
            logger.info("Creating event ad with title: {}", request.getTitle());
            EventAdDto eventAd = eventAdService.createEventAd(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(eventAd);
        } catch (IllegalArgumentException e) {
            logger.warn("Event ad creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during event ad creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEventAd(@PathVariable Long id, 
                                         @Valid @RequestBody UpdateEventAdRequest request) {
        try {
            logger.info("Updating event ad with id: {}", id);
            EventAdDto eventAd = eventAdService.updateEventAd(id, request);
            return ResponseEntity.ok(eventAd);
        } catch (IllegalArgumentException e) {
            logger.warn("Event ad update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating event ad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update event ad"));
        } catch (Exception e) {
            logger.error("Unexpected error during event ad update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventAd(@PathVariable Long id) {
        try {
            logger.info("Deleting event ad with id: {}", id);
            eventAdService.deleteEventAd(id);
            return ResponseEntity.ok(Map.of("message", "Event ad deleted successfully", "id", id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting event ad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete event ad"));
        } catch (Exception e) {
            logger.error("Unexpected error during event ad deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
} 