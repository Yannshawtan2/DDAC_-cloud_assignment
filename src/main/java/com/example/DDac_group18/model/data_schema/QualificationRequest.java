package com.example.DDac_group18.model.data_schema;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qualification_requests")
public class QualificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private String applicantEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Users.Role requestedRole;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String licenseType;

    @Column(nullable = false)
    private String s3FileKey;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String fileContentType;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column
    private LocalDateTime reviewedAt;

    @Column
    private String reviewedBy;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public QualificationRequest() {
        this.submittedAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public QualificationRequest(String applicantName, String applicantEmail, Users.Role requestedRole, 
                              String licenseNumber, String licenseType, String s3FileKey, 
                              String originalFileName, String fileContentType, String password) {
        this();
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.requestedRole = requestedRole;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.s3FileKey = s3FileKey;
        this.originalFileName = originalFileName;
        this.fileContentType = fileContentType;
        this.password = password;
    }

    // Getters and setters
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

    public Users.Role getRequestedRole() {
        return requestedRole;
    }

    public void setRequestedRole(Users.Role requestedRole) {
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

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
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

    public boolean isPasswordSet() {
        return password != null && !password.trim().isEmpty();
    }
} 