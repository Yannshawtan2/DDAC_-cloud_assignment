package com.example.qualification.service;

import com.example.qualification.dto.CreateQualificationRequestDto;
import com.example.qualification.dto.QualificationRequestDto;
import com.example.qualification.dto.ReviewQualificationRequestDto;
import com.example.qualification.model.QualificationRequest;
import com.example.qualification.model.User;
import com.example.qualification.repository.QualificationRequestRepository;
import com.example.qualification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QualificationRequestService {

    @Autowired
    private QualificationRequestRepository qualificationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public QualificationRequestDto createRequest(CreateQualificationRequestDto requestDto) {
        // Validate that user doesn't already exist
        if (userRepository.existsByEmail(requestDto.getApplicantEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Check for existing pending request
        Optional<QualificationRequest> existingRequest = qualificationRequestRepository
                .findByApplicantEmailAndStatus(requestDto.getApplicantEmail(), QualificationRequest.Status.PENDING);
        if (existingRequest.isPresent()) {
            throw new IllegalArgumentException("A pending request already exists for this email");
        }

        // Validate password
        if (requestDto.getPassword() == null || requestDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (requestDto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Store password as plain text for assignment purposes
        // In production, this would be handled more securely
        String plainTextPassword = requestDto.getPassword();

        // Create qualification request
        QualificationRequest request = new QualificationRequest(
                requestDto.getApplicantName(),
                requestDto.getApplicantEmail(),
                requestDto.getRequestedRole(),
                requestDto.getLicenseNumber(),
                requestDto.getLicenseType(),
                requestDto.getS3FileKey(),
                requestDto.getOriginalFilename(),
                requestDto.getFileContentType(),
                plainTextPassword
        );

        QualificationRequest savedRequest = qualificationRequestRepository.save(request);
        return new QualificationRequestDto(savedRequest);
    }

    public List<QualificationRequestDto> getAllRequests() {
        return qualificationRequestRepository.findAllByOrderBySubmittedAtDesc()
                .stream()
                .map(QualificationRequestDto::new)
                .collect(Collectors.toList());
    }

    public List<QualificationRequestDto> getRequestsByStatus(QualificationRequest.Status status) {
        return qualificationRequestRepository.findByStatusOrderBySubmittedAtDesc(status)
                .stream()
                .map(QualificationRequestDto::new)
                .collect(Collectors.toList());
    }

    public List<QualificationRequestDto> getPendingRequests() {
        return getRequestsByStatus(QualificationRequest.Status.PENDING);
    }

    public List<QualificationRequestDto> getRequestsByEmail(String email) {
        return qualificationRequestRepository.findByApplicantEmail(email)
                .stream()
                .map(QualificationRequestDto::new)
                .collect(Collectors.toList());
    }

    public Optional<QualificationRequestDto> getRequestById(Long id) {
        return qualificationRequestRepository.findById(id)
                .map(QualificationRequestDto::new);
    }

    public QualificationRequestDto approveRequest(Long requestId, ReviewQualificationRequestDto reviewDto) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != QualificationRequest.Status.PENDING) {
            throw new IllegalArgumentException("Request is not in pending status");
        }

        // Update request status only - user creation will be handled by frontend
        request.setStatus(QualificationRequest.Status.APPROVED);
        request.setAdminNotes(reviewDto.getAdminNotes());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewDto.getReviewedBy());

        QualificationRequest savedRequest = qualificationRequestRepository.save(request);
        return new QualificationRequestDto(savedRequest);
    }

    public QualificationRequestDto rejectRequest(Long requestId, ReviewQualificationRequestDto reviewDto) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != QualificationRequest.Status.PENDING) {
            throw new IllegalArgumentException("Request is not in pending status");
        }

        // Update request status
        request.setStatus(QualificationRequest.Status.REJECTED);
        request.setAdminNotes(reviewDto.getAdminNotes());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewDto.getReviewedBy());

        QualificationRequest savedRequest = qualificationRequestRepository.save(request);
        return new QualificationRequestDto(savedRequest);
    }

    public void deleteRequest(Long requestId) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        qualificationRequestRepository.delete(request);
    }

    public List<QualificationRequestDto> getRequestsByRole(User.Role role) {
        return qualificationRequestRepository.findByRequestedRole(role)
                .stream()
                .map(QualificationRequestDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get the S3 file key for a qualification request
     * @param requestId The ID of the qualification request
     * @return The S3 file key
     */
    public String getLicenseFileKey(Long requestId) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        if (request.getS3FileKey() == null || request.getS3FileKey().trim().isEmpty()) {
            throw new IllegalArgumentException("No file associated with this request");
        }
        
        return request.getS3FileKey();
    }

    /**
     * Get the original filename for a qualification request
     * @param requestId The ID of the qualification request
     * @return The original filename
     */
    public String getLicenseFileName(Long requestId) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        return request.getOriginalFilename();
    }

    /**
     * Get the file content type for a qualification request
     * @param requestId The ID of the qualification request
     * @return The file content type
     */
    public String getLicenseFileContentType(Long requestId) {
        QualificationRequest request = qualificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        
        return request.getFileContentType();
    }
}
