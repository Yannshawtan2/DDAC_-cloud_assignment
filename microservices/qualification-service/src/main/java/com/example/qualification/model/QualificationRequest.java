package com.example.qualification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "qualification_requests")
public class QualificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Applicant name is required")
    @Column(nullable = false)
    private String applicantName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Column(nullable = false)
    private String applicantEmail;

    @NotNull(message = "Requested role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.Role requestedRole;

    @NotBlank(message = "License number is required")
    @Column(nullable = false)
    private String licenseNumber;

    @NotBlank(message = "License type is required")
    @Column(nullable = false)
    private String licenseType;

    @Column
    private String s3FileKey;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column
    private String fileContentType;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column
    private LocalDateTime reviewedAt;

    @Column
    private String reviewedBy;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public QualificationRequest() {}

    public QualificationRequest(String applicantName, String applicantEmail, User.Role requestedRole,
                              String licenseNumber, String licenseType, String s3FileKey,
                              String originalFileName, String fileContentType, String password) {
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.requestedRole = requestedRole;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.s3FileKey = s3FileKey;
        this.originalFileName = originalFileName;
        this.fileContentType = fileContentType;
        this.password = password;
        this.submittedAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public User.Role getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(User.Role requestedRole) {
        this.requestedRole = requestedRole;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getS3FileKey() {
        return s3FileKey;
    }

    public void setS3FileKey(String s3FileKey) {
        this.s3FileKey = s3FileKey;
    }

    public String getOriginalFilename() {
        return originalFileName;
    }

    public void setOriginalFilename(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
