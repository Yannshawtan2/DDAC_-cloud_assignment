package com.example.DDac_group18.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.DDac_group18.services.UserService;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.model.repository.DataRepository;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.data_schema.Data;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataRepository dataRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Spring Boot with Thymeleaf!");
        return "home"; // This will look for home.html in src/main/resources/templates
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) boolean newUser, Model model) {
        if (newUser) {
            model.addAttribute("message", "Registration successful! Please login to enter your health data.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admindashboard")
    public String adminDashboard() {
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String name,
            Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }
        boolean success = userService.registerdefaultUser(email, password, name);
        if (success) {
            return "redirect:/login?newUser=true";
        } else {
            model.addAttribute("error", "Email already registered.");
            return "register";
        }
    }

    // Temporary endpoint to create test user - remove after testing
    @GetMapping("/create-test-user")
    @ResponseBody
    public String createTestUser() {
        try {
            // Use a unique email for each test
            String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
            boolean success = userService.registerdefaultUser(uniqueEmail, "testpassword", "Test Patient");
            if (success) {
                return "Test user created successfully: " + uniqueEmail + " / testpassword";
            } else {
                return "Failed to create test user";
            }
        } catch (Exception e) {
            return "Error creating test user: " + e.getMessage();
        }
    }

    // Test endpoint to check if user exists - remove after testing
    @GetMapping("/check-user/{email}")
    @ResponseBody
    public String checkUser(@PathVariable String email) {
        try {
            Users user = userService.getUserByEmail(email);
            if (user != null) {
                return "User found: " + user.getEmail() + " with role: " + user.getRole();
            } else {
                return "User not found: " + email;
            }
        } catch (Exception e) {
            return "Error checking user: " + e.getMessage();
        }
    }

    // Test endpoint to try authentication with different passwords - remove after
    // testing
    @GetMapping("/authenticate-test")
    @ResponseBody
    public String authenticateTest(@RequestParam String email, @RequestParam String password) {
        try {
            boolean result = userService.authenticateUser(email, password);
            return "Authentication result for " + email + " with password '" + password + "': " + result;
        } catch (Exception e) {
            return "Error during authentication: " + e.getMessage();
        }
    }

    @GetMapping("/redirectDashboard")
    public String redirectDashboard(Authentication authentication) {
        if (authentication == null)
            return "redirect:/login";

        // Save the current user to "currentUser"
        Users currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // For patients, check if they have submitted their initial data
        if (role.equals("ROLE_PATIENT")) {
            if (!dataRepository.existsByUserId(currentUser.getId())) {
                return "redirect:/patient/initial-data";
            }
            return "redirect:/patient/dashboard";
        }

        System.out.println(role);
        switch (role) {
            case "ROLE_ADMIN":
                return "redirect:/admin/dashboard";
            case "ROLE_DOCTOR":
                return "redirect:/doctor/dashboard";
            case "ROLE_DIETICIAN":
                return "redirect:/dieticiandashboard";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/test-user")
    @ResponseBody
    public String testUser(@RequestParam String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found";
        }
        return "User found with role: " + user.getRole();
    }

    @GetMapping("/test-auth")
    @ResponseBody
    public String testAuth(@RequestParam String email, @RequestParam String password) {
        try {
            boolean authenticated = userService.authenticateUser(email, password);
            return "Authentication result for " + email + ": " + authenticated;
        } catch (Exception e) {
            return "Authentication failed for " + email + ": " + e.getMessage();
        }
    }

    // @GetMapping("/findAllByRole")
    // public String findAllByRole(Model model) {
    // List<Users> users = userRepository.findAllByRole("patient");
    // model.addAttribute("users", users);
    // return "findAllByRole";
    // }

    // Add this method to serve CSS files with the correct MIME type
    @GetMapping(value = "/css/{filename:.+}", produces = "text/css")
    @ResponseBody
    public ResponseEntity<Resource> getCssFile(@PathVariable String filename) {
        try {
            Resource resource = new ClassPathResource("static/css/" + filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/css"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}