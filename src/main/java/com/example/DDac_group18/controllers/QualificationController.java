package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.data_schema.QualificationRequest;
import com.example.DDac_group18.services.QualificationRequestService;
import com.example.DDac_group18.services.S3Service;
import com.example.DDac_group18.clients.QualificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/qualification")
public class QualificationController {

    @Autowired
    private QualificationRequestService qualificationRequestService;

    @Autowired
    private QualificationServiceClient qualificationServiceClient;

    @Autowired
    private S3Service s3Service;

    @GetMapping("/apply")
    public String showApplicationForm(Model model) {
        model.addAttribute("roles", new Users.Role[]{Users.Role.DOCTOR, Users.Role.DIETICIAN});
        return "qualification-apply";
    }

    @PostMapping("/submit")
    public String submitApplication(@RequestParam String applicantName,
                                  @RequestParam String applicantEmail,
                                  @RequestParam Users.Role requestedRole,
                                  @RequestParam String licenseNumber,
                                  @RequestParam String licenseType,
                                  @RequestParam String password,
                                  @RequestParam("licenseFile") MultipartFile licenseFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Log the submission attempt
            System.out.println("Submitting qualification request for: " + applicantEmail);
            
            // Step 1: Upload file to S3 (handled by main application)
            String folder = "qualifications/" + requestedRole.toString().toLowerCase();
            String s3FileKey = s3Service.uploadFile(licenseFile, folder);
            
            // Step 2: Send qualification data to microservice
            qualificationServiceClient.createRequest(
                applicantName, applicantEmail, requestedRole, licenseNumber, 
                licenseType, password, s3FileKey, licenseFile.getOriginalFilename(), 
                licenseFile.getContentType()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Your qualification request has been submitted successfully! An admin will review your application.");
            return "redirect:/qualification/apply";
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/qualification/apply";
        } catch (Exception e) {
            System.err.println("Unexpected error during submission: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "An error occurred while submitting your request. Please try again. Error: " + e.getMessage());
            return "redirect:/qualification/apply";
        }
    }

    @GetMapping("/status")
    public String checkStatus(@RequestParam String email, Model model) {
        try {
            System.out.println("Checking status for email: " + email);
            
            // Get requests from microservice
            List<QualificationRequest> requests;
            try {
                requests = qualificationServiceClient.getRequestsByEmail(email);
                System.out.println("Found " + requests.size() + " requests for email: " + email);
            } catch (Exception serviceException) {
                System.out.println("Microservice error for email " + email + ": " + serviceException.getMessage());
                model.addAttribute("errorMessage", "Unable to retrieve your application data from the qualification service. Please try again later.");
                model.addAttribute("hasRequest", false);
                return "qualification-status";
            }
            
            if (!requests.isEmpty()) {
                // Get the most recent request
                QualificationRequest mostRecentRequest = requests.get(0); // Assuming they're ordered by submission date
                
                System.out.println("Most recent request status: " + mostRecentRequest.getStatus());
                System.out.println("Request ID: " + mostRecentRequest.getId());
                
                model.addAttribute("request", mostRecentRequest);
                model.addAttribute("hasRequest", true);
            } else {
                model.addAttribute("hasRequest", false);
            }
            
        } catch (Exception e) {
            System.out.println("ERROR in checkStatus: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "An unexpected error occurred while checking your status. Please try again later or contact support.");
            model.addAttribute("hasRequest", false);
        }
        
        return "qualification-status";
    }
} 