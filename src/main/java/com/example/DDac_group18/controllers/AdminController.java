package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.MaintenanceNotification;
import com.example.DDac_group18.model.data_schema.QualificationRequest;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.services.UserService;
import com.example.DDac_group18.services.QualificationRequestService;
import com.example.DDac_group18.clients.MaintenanceNotificationServiceClient;
import com.example.DDac_group18.clients.QualificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MaintenanceNotificationServiceClient maintenanceNotificationServiceClient;

    @Autowired
    private QualificationServiceClient qualificationServiceClient;

    @Autowired
    private QualificationRequestService qualificationRequestService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        try {
            List<Users> allUsers = userService.getAllUsers();
            List<MaintenanceNotification> activeNotifications = maintenanceNotificationServiceClient.getAllActiveNotifications();
            List<QualificationRequest> pendingRequests = qualificationServiceClient.getPendingRequests();
            
            model.addAttribute("users", allUsers);
            model.addAttribute("notifications", activeNotifications);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("roles", Users.Role.values());
            model.addAttribute("priorities", MaintenanceNotification.Priority.values());
        } catch (Exception e) {
            // Log error and continue with empty lists
            List<Users> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);
            model.addAttribute("notifications", List.of());
            model.addAttribute("pendingRequests", List.of());
            model.addAttribute("roles", Users.Role.values());
            model.addAttribute("priorities", MaintenanceNotification.Priority.values());
        }
        
        return "admin/admindashboard";
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public Users getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/notifications")
    public String allNotifications(Model model, 
        @RequestParam(value = "successMessage", required = false) String successMessage,
        @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        List<MaintenanceNotification> allNotifications = maintenanceNotificationServiceClient.getAllNotifications();

        long activeCount = allNotifications.stream().filter(MaintenanceNotification::isActive).count();
        long inactiveCount = allNotifications.size() - activeCount;
        long criticalCount = allNotifications.stream()
            .filter(n -> n.getPriority() == MaintenanceNotification.Priority.CRITICAL)
            .count();

        model.addAttribute("notifications", allNotifications);
        model.addAttribute("priorities", MaintenanceNotification.Priority.values());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);
        model.addAttribute("criticalCount", criticalCount);
        model.addAttribute("totalCount", allNotifications.size());

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "admin/maintenance-notifications";
    }

    

    @PostMapping("/create-user")
    public String createUser(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           @RequestParam Users.Role role,
                           RedirectAttributes redirectAttributes) {
        try {
            boolean success = userService.createUser(email, password, name, role);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create user. Email might already exist.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam Long id,
                           @RequestParam String email,
                           @RequestParam(required = false) String password,
                           @RequestParam String name,
                           @RequestParam Users.Role role,
                           RedirectAttributes redirectAttributes) {
        try {
            boolean success = userService.updateUser(id, email, password, name, role);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user. User might not exist or email might already be taken.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user. User might not exist or you cannot delete admin users.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/create-notification")
    public String createNotification(@RequestParam String title,
                                   @RequestParam String message,
                                   @RequestParam MaintenanceNotification.Priority priority,
                                   @RequestHeader(value = "Referer", required = false) String referer,
                                   RedirectAttributes redirectAttributes) {
        try {
            MaintenanceNotification notification = maintenanceNotificationServiceClient.createNotification(title, message, priority);
            if (notification != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Maintenance notification created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create maintenance notification.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating notification: " + e.getMessage());
        }
        
        // Redirect based on where the request came from
        if (referer != null && referer.contains("/admin/notifications")) {
            return "redirect:/admin/notifications";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/deactivate-notification/{id}")
    public String deactivateNotification(@PathVariable Long id, 
                                       @RequestHeader(value = "Referer", required = false) String referer,
                                       RedirectAttributes redirectAttributes) {
        try {
            boolean success = maintenanceNotificationServiceClient.deactivateNotification(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Notification deactivated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to deactivate notification.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating notification: " + e.getMessage());
        }
        
        // Redirect based on where the request came from
        if (referer != null && referer.contains("/admin/notifications")) {
            return "redirect:/admin/notifications";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/activate-notification/{id}")
    public String activateNotification(@PathVariable Long id, 
                                     @RequestHeader(value = "Referer", required = false) String referer,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Deactivate all notifications first
            List<MaintenanceNotification> allNotifications = maintenanceNotificationServiceClient.getAllNotifications();
            for (MaintenanceNotification n : allNotifications) {
                if (n.isActive() && !n.getId().equals(id)) {
                    maintenanceNotificationServiceClient.deactivateNotification(n.getId());
                }
            }
            boolean success = maintenanceNotificationServiceClient.activateNotification(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Notification activated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to activate notification.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error activating notification: " + e.getMessage());
        }
        
        // Redirect based on where the request came from
        if (referer != null && referer.contains("/admin/notifications")) {
            return "redirect:/admin/notifications";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete-notification/{id}")
    public String deleteNotification(@PathVariable Long id, 
                                   @RequestHeader(value = "Referer", required = false) String referer,
                                   RedirectAttributes redirectAttributes) {
        try {
            boolean success = maintenanceNotificationServiceClient.deleteNotification(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Notification deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete notification.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting notification: " + e.getMessage());
        }
        
        // Redirect based on where the request came from
        if (referer != null && referer.contains("/admin/notifications")) {
            return "redirect:/admin/notifications";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/update-notification")
    public String updateNotification(@RequestParam Long id,
                                    @RequestParam String title,
                                    @RequestParam String message,
                                    @RequestParam MaintenanceNotification.Priority priority,
                                    @RequestHeader(value = "Referer", required = false) String referer,
                                    RedirectAttributes redirectAttributes) {
        try {
            boolean success = maintenanceNotificationServiceClient.updateNotification(id, title, message, priority);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Notification updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update notification.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating notification: " + e.getMessage());
        }
        if (referer != null && referer.contains("/admin/notifications")) {
            return "redirect:/admin/notifications";
        }
        return "redirect:/admin/dashboard";
    }

    // Qualification Request Management
    @GetMapping("/qualification-requests")
    public String qualificationRequests(Model model) {
        try {
            List<QualificationRequest> allRequests = qualificationServiceClient.getAllRequests();
            List<QualificationRequest> pendingRequests = qualificationServiceClient.getPendingRequests();
            
            // Calculate approved and rejected counts
            long approvedCount = allRequests.stream()
                    .filter(r -> r.getStatus() == QualificationRequest.Status.APPROVED)
                    .count();
            long rejectedCount = allRequests.stream()
                    .filter(r -> r.getStatus() == QualificationRequest.Status.REJECTED)
                    .count();
            
            model.addAttribute("allRequests", allRequests);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("approvedCount", approvedCount);
            model.addAttribute("rejectedCount", rejectedCount);
            model.addAttribute("statuses", QualificationRequest.Status.values());
        } catch (Exception e) {
            // Log error and continue with empty lists
            model.addAttribute("allRequests", List.of());
            model.addAttribute("pendingRequests", List.of());
            model.addAttribute("approvedCount", 0L);
            model.addAttribute("rejectedCount", 0L);
            model.addAttribute("statuses", QualificationRequest.Status.values());
        }
        
        return "admin/admin-qualification-requests";
    }

    @GetMapping("/qualification-requests/{id}/download")
    public ResponseEntity<?> downloadLicenseFile(@PathVariable Long id) {
        try {
            logger.info("Attempting to download license file for qualification request: {}", id);
            
            // Get file information from the microservice
            Map<String, String> fileInfo = qualificationServiceClient.getLicenseFileInfo(id);
            String fileKey = fileInfo.get("fileKey");
            String fileName = fileInfo.get("fileName");
            String contentType = fileInfo.get("contentType");
            
            if (fileKey == null || fileKey.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("No file associated with this request");
            }
            
            // Download the file from S3 using the main application's S3Service
            InputStream fileInputStream = qualificationRequestService.downloadLicenseFile(id);
            
            // Set the filename for download
            String downloadFileName = fileName != null ? fileName : "license_file";
            
            // Convert InputStream to byte array for proper response handling
            byte[] fileBytes = fileInputStream.readAllBytes();
            fileInputStream.close();
            
            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"")
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(fileBytes);
                    
        } catch (IllegalArgumentException e) {
            logger.warn("File download failed for request {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error downloading file for request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to download file: " + e.getMessage());
        }
    }

    @PostMapping("/qualification-requests/{id}/approve")
    public String approveRequest(@PathVariable Long id,
                               @RequestParam String adminNotes,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String reviewerName = authentication != null ? authentication.getName() : "Admin";
            logger.info("Attempting to approve qualification request {} by reviewer: {}", id, reviewerName);
            
            // First, get the qualification request details
            QualificationRequest request = qualificationServiceClient.getRequestById(id);
            if (request == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Qualification request not found.");
                return "redirect:/admin/qualification-requests";
            }
            
            // Approve the qualification request (fast operation)
            qualificationServiceClient.approveRequest(id, adminNotes, reviewerName);
            logger.info("Successfully approved qualification request {}", id);
            
            // Create user account using the user service (separate operation)
            try {
                logger.info("Creating user account for approved qualification request {}", id);
                boolean userCreated = userService.createUser(
                    request.getApplicantEmail(),
                    request.getPassword(), // This is plain text password - user service will encrypt it
                    request.getApplicantName(),
                    Users.Role.valueOf(request.getRequestedRole().name())
                );
                
                if (userCreated) {
                    logger.info("Successfully created user account for {}", request.getApplicantEmail());
                    redirectAttributes.addFlashAttribute("successMessage", 
                        "Qualification request approved and user account created successfully!");
                } else {
                    logger.warn("Failed to create user account for {}", request.getApplicantEmail());
                    redirectAttributes.addFlashAttribute("successMessage", 
                        "Qualification request approved, but user account creation failed. Please create the account manually.");
                }
            } catch (Exception userCreationException) {
                logger.error("Error creating user account for {}: {}", request.getApplicantEmail(), userCreationException.getMessage());
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Qualification request approved, but user account creation failed. Please create the account manually.");
            }
            
        } catch (Exception e) {
            logger.error("Error approving qualification request {}: {}", id, e.getMessage(), e);
            
            // Provide more specific error messages based on the exception type
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Gateway Timeout")) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Request timed out. The qualification service may be experiencing delays. Please try again later.");
            } else if (errorMessage != null && errorMessage.contains("Connection refused")) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot connect to qualification service. Please contact the administrator.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error approving request: " + errorMessage);
            }
        }
        return "redirect:/admin/qualification-requests";
    }

    @PostMapping("/qualification-requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id,
                              @RequestParam String adminNotes,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String reviewerName = authentication != null ? authentication.getName() : "Admin";
            logger.info("Attempting to reject qualification request {} by reviewer: {}", id, reviewerName);
            
            qualificationServiceClient.rejectRequest(id, adminNotes, reviewerName);
            
            logger.info("Successfully rejected qualification request {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Qualification request rejected successfully!");
        } catch (Exception e) {
            logger.error("Error rejecting qualification request {}: {}", id, e.getMessage(), e);
            
            // Provide more specific error messages based on the exception type
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Gateway Timeout")) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Request timed out. The qualification service may be experiencing delays. Please try again later.");
            } else if (errorMessage != null && errorMessage.contains("Connection refused")) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot connect to qualification service. Please contact the administrator.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + errorMessage);
            }
        }
        return "redirect:/admin/qualification-requests";
    }

    @PostMapping("/qualification-requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            qualificationServiceClient.deleteRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Qualification request deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting request: " + e.getMessage());
        }
        return "redirect:/admin/qualification-requests";
    }
} 