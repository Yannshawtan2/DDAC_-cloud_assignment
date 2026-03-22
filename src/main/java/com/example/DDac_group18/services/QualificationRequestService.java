package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.QualificationRequest;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.QualificationRequestRepository;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.clients.QualificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QualificationRequestService {

    @Autowired
    private QualificationRequestRepository qualificationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QualificationServiceClient qualificationServiceClient;

    public QualificationRequest submitRequest(String applicantName, String applicantEmail, 
                                           Users.Role requestedRole, String licenseNumber, 
                                           String licenseType, String password, MultipartFile licenseFile) throws IOException {
        
        try {
            // Check if user already exists
            if (userRepository.findByEmail(applicantEmail) != null) {
                throw new IllegalArgumentException("User with this email already exists");
            }

            // Check if there's already a pending request for this email
            Optional<QualificationRequest> existingRequest = qualificationRequestRepository
                    .findByApplicantEmailAndStatus(applicantEmail, QualificationRequest.Status.PENDING);
            if (existingRequest.isPresent()) {
                throw new IllegalArgumentException("A pending request already exists for this email");
            }

            // Validate password
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }

            // Validate file
            if (licenseFile == null || licenseFile.isEmpty()) {
                throw new IllegalArgumentException("License file is required");
            }

            // Validate file type
            String contentType = licenseFile.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
                throw new IllegalArgumentException("Only PDF and image files are allowed");
            }

            // Validate file size (max 10MB)
            if (licenseFile.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size must be less than 10MB");
            }

            // Upload file to S3
            String folder = "qualifications/" + requestedRole.toString().toLowerCase();
            String s3FileKey = s3Service.uploadFile(licenseFile, folder);

            // Encrypt password before storing
            String encryptedPassword = passwordEncoder.encode(password);

            // Create qualification request
            QualificationRequest request = new QualificationRequest(
                    applicantName, applicantEmail, requestedRole, licenseNumber, 
                    licenseType, s3FileKey, licenseFile.getOriginalFilename(), contentType, encryptedPassword
            );

            return qualificationRequestRepository.save(request);
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error submitting qualification request: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<QualificationRequest> getAllRequests() {
        return qualificationRequestRepository.findAll();
    }

    public List<QualificationRequest> getRequestsByStatus(QualificationRequest.Status status) {
        return qualificationRequestRepository.findByStatusOrderBySubmittedAtDesc(status);
    }

    public List<QualificationRequest> getPendingRequests() {
        return getRequestsByStatus(QualificationRequest.Status.PENDING);
    }

    public List<QualificationRequest> getRequestsByEmail(String email) {
        try {
            System.out.println("Service: Getting requests for email: " + email);
            List<QualificationRequest> requests = qualificationRequestRepository.findByApplicantEmail(email);
            System.out.println("Service: Found " + requests.size() + " requests");
            
            for (int i = 0; i < requests.size(); i++) {
                QualificationRequest req = requests.get(i);
                System.out.println("Request " + i + ": ID=" + req.getId() + ", Status=" + req.getStatus() + ", Email=" + req.getApplicantEmail());
            }
            
            return requests;
        } catch (Exception e) {
            System.out.println("ERROR in getRequestsByEmail: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public QualificationRequest getRequestById(Long id) {
        return qualificationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    public QualificationRequest approveRequest(Long requestId, String adminNotes, String reviewedBy) {
        QualificationRequest request = getRequestById(requestId);
        
        if (request.getStatus() != QualificationRequest.Status.PENDING) {
            throw new IllegalArgumentException("Request is not in pending status");
        }

        // Create user account
        Users newUser = new Users();
        newUser.setEmail(request.getApplicantEmail());
        newUser.setName(request.getApplicantName());
        newUser.setRole(request.getRequestedRole());
        
        // Use the password that was provided during application (already encrypted)
        // For legacy requests without passwords, generate a temporary password
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            newUser.setPassword(request.getPassword());
        } else {
            // Legacy record without password - generate a temporary one
            String tempPassword = UUID.randomUUID().toString().substring(0, 12);
            newUser.setPassword(passwordEncoder.encode(tempPassword));
            System.out.println("Generated temporary password for legacy record: " + tempPassword);
        }
        
        userRepository.save(newUser);

        // Update request status
        request.setStatus(QualificationRequest.Status.APPROVED);
        request.setAdminNotes(adminNotes);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewedBy);

        return qualificationRequestRepository.save(request);
    }

    public QualificationRequest rejectRequest(Long requestId, String adminNotes, String reviewedBy) {
        QualificationRequest request = getRequestById(requestId);
        
        if (request.getStatus() != QualificationRequest.Status.PENDING) {
            throw new IllegalArgumentException("Request is not in pending status");
        }

        // Update request status
        request.setStatus(QualificationRequest.Status.REJECTED);
        request.setAdminNotes(adminNotes);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewedBy);

        return qualificationRequestRepository.save(request);
    }

    public void deleteRequest(Long requestId) {
        QualificationRequest request = getRequestById(requestId);
        
        // Delete file from S3
        try {
            s3Service.deleteFile(request.getS3FileKey());
        } catch (IOException e) {
            // Log error but don't fail the deletion
            System.err.println("Failed to delete S3 file: " + e.getMessage());
        }
        
        qualificationRequestRepository.delete(request);
    }

    public InputStream downloadLicenseFile(Long requestId) throws IOException {
        try {
            // Get the file key from the microservice
            String fileKey = qualificationServiceClient.getLicenseFileKey(requestId);
            
            if (fileKey == null || fileKey.trim().isEmpty()) {
                throw new IllegalArgumentException("No file associated with this request");
            }
            
            // Use the local S3Service to download the file
            return s3Service.downloadFile(fileKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while getting file key", e);
        } catch (Exception e) {
            throw new IOException("Failed to download file: " + e.getMessage(), e);
        }
    }

    public String getLicenseFileUrl(Long requestId) {
        try {
            // Get the file key from the microservice
            String fileKey = qualificationServiceClient.getLicenseFileKey(requestId);
            return s3Service.getFileUrl(fileKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file URL: " + e.getMessage(), e);
        }
    }
} 