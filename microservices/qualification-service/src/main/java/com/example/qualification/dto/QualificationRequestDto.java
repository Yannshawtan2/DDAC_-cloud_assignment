package com.example.qualification.dto;

import com.example.qualification.model.QualificationRequest;
import com.example.qualification.model.User;

import java.time.LocalDateTime;

public class QualificationRequestDto {
    private Long id;
    private String applicantName;
    private String applicantEmail;
    private User.Role requestedRole;
    private String licenseNumber;
    private String licenseType;
    private String s3FileKey;
    private String originalFilename;
    private String fileContentType;
    private String password; // Added for assignment purposes - normally wouldn't expose this
    private QualificationRequest.Status status;
    private String adminNotes;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;

    // Constructors
    public QualificationRequestDto() {}

    public QualificationRequestDto(QualificationRequest request) {
        this.id = request.getId();
        this.applicantName = request.getApplicantName();
        this.applicantEmail = request.getApplicantEmail();
        this.requestedRole = request.getRequestedRole();
        this.licenseNumber = request.getLicenseNumber();
        this.licenseType = request.getLicenseType();
        this.s3FileKey = request.getS3FileKey();
        this.originalFilename = request.getOriginalFilename();
        this.fileContentType = request.getFileContentType();
        this.password = request.getPassword(); // Include password for assignment
        this.status = request.getStatus();
        this.adminNotes = request.getAdminNotes();
        this.submittedAt = request.getSubmittedAt();
        this.reviewedAt = request.getReviewedAt();
        this.reviewedBy = request.getReviewedBy();
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
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public QualificationRequest.Status getStatus() {
        return status;
    }

    public void setStatus(QualificationRequest.Status status) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
