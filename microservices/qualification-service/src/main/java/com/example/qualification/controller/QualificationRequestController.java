package com.example.qualification.controller;

import com.example.qualification.dto.CreateQualificationRequestDto;
import com.example.qualification.dto.QualificationRequestDto;
import com.example.qualification.dto.ReviewQualificationRequestDto;
import com.example.qualification.model.QualificationRequest;
import com.example.qualification.model.User;
import com.example.qualification.service.QualificationRequestService;
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
@RequestMapping("qualify/qualification-requests")
@CrossOrigin(origins = "*")
public class QualificationRequestController {
    private static final Logger logger = LoggerFactory.getLogger(QualificationRequestController.class);

    @Autowired
    private QualificationRequestService qualificationRequestService;

    @GetMapping
    public ResponseEntity<?> getAllRequests(@RequestParam(value = "status", required = false) String status,
                                          @RequestParam(value = "role", required = false) String role,
                                          @RequestParam(value = "email", required = false) String email) {
        try {
            logger.info("Getting qualification requests with filters - status: {}, role: {}, email: {}", status, role, email);
            
            List<QualificationRequestDto> requests;
            
            if (email != null && !email.trim().isEmpty()) {
                requests = qualificationRequestService.getRequestsByEmail(email.trim());
            } else if (status != null && !status.trim().isEmpty()) {
                QualificationRequest.Status statusEnum = QualificationRequest.Status.valueOf(status.toUpperCase());
                requests = qualificationRequestService.getRequestsByStatus(statusEnum);
            } else if (role != null && !role.trim().isEmpty()) {
                User.Role roleEnum = User.Role.valueOf(role.toUpperCase());
                requests = qualificationRequestService.getRequestsByRole(roleEnum);
            } else {
                requests = qualificationRequestService.getAllRequests();
            }
            
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameter: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting qualification requests: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve qualification requests"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        try {
            logger.info("Getting qualification request with id: {}", id);
            Optional<QualificationRequestDto> request = qualificationRequestService.getRequestById(id);
            
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting qualification request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve qualification request"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@Valid @RequestBody CreateQualificationRequestDto requestDto) {
        try {
            logger.info("Creating qualification request for: {}", requestDto.getApplicantEmail());
            QualificationRequestDto request = qualificationRequestService.createRequest(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            logger.warn("Qualification request creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during qualification request creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            logger.info("Getting pending qualification requests");
            List<QualificationRequestDto> requests = qualificationRequestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Error getting pending qualification requests: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve pending qualification requests"));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, 
                                          @Valid @RequestBody ReviewQualificationRequestDto reviewDto) {
        try {
            logger.info("Approving qualification request with id: {}", id);
            QualificationRequestDto request = qualificationRequestService.approveRequest(id, reviewDto);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException e) {
            logger.warn("Qualification request approval failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error approving qualification request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve qualification request"));
        } catch (Exception e) {
            logger.error("Unexpected error during qualification request approval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, 
                                         @Valid @RequestBody ReviewQualificationRequestDto reviewDto) {
        try {
            logger.info("Rejecting qualification request with id: {}", id);
            QualificationRequestDto request = qualificationRequestService.rejectRequest(id, reviewDto);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException e) {
            logger.warn("Qualification request rejection failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error rejecting qualification request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject qualification request"));
        } catch (Exception e) {
            logger.error("Unexpected error during qualification request rejection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            logger.info("Deleting qualification request with id: {}", id);
            qualificationRequestService.deleteRequest(id);
            return ResponseEntity.ok(Map.of("message", "Qualification request deleted successfully", "id", id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting qualification request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete qualification request"));
        } catch (Exception e) {
            logger.error("Unexpected error during qualification request deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getRequestsByEmail(@PathVariable String email) {
        try {
            logger.info("Getting qualification requests for email: {}", email);
            List<QualificationRequestDto> requests = qualificationRequestService.getRequestsByEmail(email);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Error getting qualification requests for email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve qualification requests"));
        }
    }

    @GetMapping("/{id}/file-key")
    public ResponseEntity<?> getLicenseFileKey(@PathVariable Long id) {
        try {
            logger.info("Getting file key for qualification request: {}", id);
            String fileKey = qualificationRequestService.getLicenseFileKey(id);
            return ResponseEntity.ok(Map.of("fileKey", fileKey));
        } catch (IllegalArgumentException e) {
            logger.warn("File key request failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting file key for request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve file key"));
        }
    }

    @GetMapping("/{id}/file-info")
    public ResponseEntity<?> getLicenseFileInfo(@PathVariable Long id) {
        try {
            logger.info("Getting file info for qualification request: {}", id);
            String fileKey = qualificationRequestService.getLicenseFileKey(id);
            String fileName = qualificationRequestService.getLicenseFileName(id);
            String contentType = qualificationRequestService.getLicenseFileContentType(id);
            
            return ResponseEntity.ok(Map.of(
                "fileKey", fileKey,
                "fileName", fileName,
                "contentType", contentType
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("File info request failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting file info for request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve file information"));
        }
    }
}
