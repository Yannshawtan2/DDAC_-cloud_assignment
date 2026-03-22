package com.example.DDac_group18.controllers;

import com.example.DDac_group18.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/s3")
    public String testS3() {
        try {
            boolean exists = s3Service.fileExists("test.txt");
            return "S3 connection successful. Bucket accessible: " + exists;
        } catch (Exception e) {
            return "S3 connection failed: " + e.getMessage();
        }
    }

    @GetMapping("/health")
    public String health() {
        return "Application is running successfully!";
    }
} 