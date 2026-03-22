package com.example.eventads.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_ads")
public class EventAd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Binary Blob originally stored in the database
     */
    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    /**
     * Temporary field used to store Base64 Data-URIs at runtime
     * Thymeleaf templates will read it directly to display the image
     */
    @Transient
    private String image;

    // Default constructor
    public EventAd() {}

    // Constructor with title and content
    public EventAd(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * Base64 URI used for rendering at runtime
     */
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
} 