package com.example.template.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Template Entity Model
 * 
 * TODO: Replace this with your actual entity
 * Example: Patient, Doctor, DietPlan, etc.
 * 
 * Instructions:
 * 1. Rename class from TemplateEntity to your entity name
 * 2. Update table name annotation
 * 3. Add your specific fields
 * 4. Configure relationships with other entities
 * 5. Add validation annotations as needed
 */
@Entity
@Table(name = "template_entities")
public class TemplateEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // TODO: Add your specific fields here
    // Example fields:
    // private String email;
    // private String phoneNumber;
    // private LocalDate dateOfBirth;
    // private String address;
    
    // TODO: Add relationships if needed
    // @ManyToOne
    // @JoinColumn(name = "user_id")
    // private User user;
    
    // Constructors
    public TemplateEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TemplateEntity(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "TemplateEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
