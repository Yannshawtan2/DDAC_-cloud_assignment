package com.example.qualification.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewQualificationRequestDto {
    @NotBlank(message = "Admin notes are required")
    private String adminNotes;

    @NotBlank(message = "Reviewer name is required")
    private String reviewedBy;

    // Constructors
    public ReviewQualificationRequestDto() {}

    public ReviewQualificationRequestDto(String adminNotes, String reviewedBy) {
        this.adminNotes = adminNotes;
        this.reviewedBy = reviewedBy;
    }

    // Getters and Setters
    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
