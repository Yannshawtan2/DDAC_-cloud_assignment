package com.example.qualification.dto;

import com.example.qualification.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateQualificationRequestDto {
    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String applicantEmail;

    @NotNull(message = "Requested role is required")
    private User.Role requestedRole;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "License type is required")
    private String licenseType;

    @NotBlank(message = "Password is required")
    private String password;

    private String s3FileKey;
    private String originalFilename;
    private String fileContentType;

    // Constructors
    public CreateQualificationRequestDto() {}

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
