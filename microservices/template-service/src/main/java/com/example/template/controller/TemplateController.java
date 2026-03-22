package com.example.template.controller;

import com.example.template.dto.CreateTemplateEntityRequest;
import com.example.template.dto.TemplateEntityDto;
import com.example.template.dto.UpdateTemplateEntityRequest;
import com.example.template.service.TemplateService;
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
@RequestMapping("/entities")
@CrossOrigin(origins = "*")
public class TemplateController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getAllEntities(@RequestParam(value = "name", required = false) String name) {
        try {
            logger.info("Getting entities with name filter: {}", name);
            List<TemplateEntityDto> entities;
            
            if (name != null && !name.trim().isEmpty()) {
                entities = templateService.searchEntitiesByName(name);
            } else {
                entities = templateService.getAllEntities();
            }
            
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            logger.error("Error getting entities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve entities"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable Long id) {
        try {
            logger.info("Getting entity with id: {}", id);
            Optional<TemplateEntityDto> entity = templateService.getEntityById(id);
            
            if (entity.isPresent()) {
                return ResponseEntity.ok(entity.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting entity {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve entity"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createEntity(@Valid @RequestBody CreateTemplateEntityRequest request) {
        try {
            logger.info("Creating entity with name: {}", request.getName());
            TemplateEntityDto entity = templateService.createEntity(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (IllegalArgumentException e) {
            logger.warn("Entity creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during entity creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable Long id, 
                                        @Valid @RequestBody UpdateTemplateEntityRequest request) {
        try {
            logger.info("Updating entity with id: {}", id);
            TemplateEntityDto entity = templateService.updateEntity(id, request);
            return ResponseEntity.ok(entity);
        } catch (IllegalArgumentException e) {
            logger.warn("Entity update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating entity {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update entity"));
        } catch (Exception e) {
            logger.error("Unexpected error during entity update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable Long id) {
        try {
            logger.info("Deleting entity with id: {}", id);
            templateService.deleteEntity(id);
            return ResponseEntity.ok(Map.of("message", "Entity deleted successfully", "id", id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting entity {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete entity"));
        } catch (Exception e) {
            logger.error("Unexpected error during entity deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchEntitiesByName(@RequestParam String name) {
        try {
            logger.info("Searching entities by name: {}", name);
            List<TemplateEntityDto> entities = templateService.searchEntitiesByName(name);
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            logger.error("Error searching entities by name {}: {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search entities"));
        }
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> checkEntityExists(@PathVariable Long id) {
        try {
            logger.info("Checking if entity exists with id: {}", id);
            boolean exists = templateService.existsById(id);
            return ResponseEntity.ok(Map.of("exists", exists, "id", id));
        } catch (Exception e) {
            logger.error("Error checking entity existence {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check entity existence"));
        }
    }
}
