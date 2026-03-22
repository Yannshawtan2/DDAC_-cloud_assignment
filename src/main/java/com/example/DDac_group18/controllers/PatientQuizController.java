package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.DietPlan;
import com.example.DDac_group18.model.data_schema.QuizQuestion;
import com.example.DDac_group18.model.data_schema.QuizResponse;
import com.example.DDac_group18.model.data_schema.TreatmentPlan;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.services.DietPlanService;
import com.example.DDac_group18.services.PatientService;
import com.example.DDac_group18.services.QuizService;
import com.example.DDac_group18.services.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientQuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DietPlanService dietPlanService;

    @Autowired
    private TreatmentPlanService treatmentPlanService;


    // View All Diet Plans
    @GetMapping("/diet-plans")
    public String viewDietPlans(@RequestParam(required = false) String status,
                               Model model, Authentication authentication) {
        Users patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        List<DietPlan> dietPlans = dietPlanService.getDietPlansByUser(patient);

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            try {
                DietPlan.Status statusEnum = DietPlan.Status.valueOf(status.toUpperCase());
                dietPlans = dietPlans.stream()
                    .filter(plan -> plan.getStatus() == statusEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                // Invalid status, show all plans
            }
        }

        model.addAttribute("patient", patient);
        model.addAttribute("dietPlans", dietPlans);
        model.addAttribute("selectedStatus", status);

        return "patient/diet-plans-list";
    }

    // View Diet Plan Details
    @GetMapping("/diet-plans/{id}")
    public String viewDietPlan(@PathVariable Long id, Model model, Authentication authentication) {
        Users patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        Optional<DietPlan> dietPlanOpt = dietPlanService.getDietPlanById(id);
        if (dietPlanOpt.isEmpty() || !dietPlanOpt.get().getPatient().getId().equals(patient.getId())) {
            return "redirect:/patient/diet-plans";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("dietPlan", dietPlanOpt.get());

        return "patient/diet-plan-view";
    }

    // View All Treatment Plans
    @GetMapping("/treatment-plans")
    public String viewTreatmentPlans(@RequestParam(required = false) String status,
                                   Model model, Authentication authentication) {
        Users patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        List<TreatmentPlan> treatmentPlans = treatmentPlanService.getTreatmentPlansByPatient(patient);

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            try {
                TreatmentPlan.Status statusEnum = TreatmentPlan.Status.valueOf(status.toUpperCase());
                treatmentPlans = treatmentPlans.stream()
                    .filter(plan -> plan.getStatus() == statusEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                // Invalid status, show all plans
            }
        }

        model.addAttribute("patient", patient);
        model.addAttribute("treatmentPlans", treatmentPlans);
        model.addAttribute("selectedStatus", status);

        return "patient/treatment-plans-list";
    }

    // View Treatment Plan Details
    @GetMapping("/treatment-plans/{id}")
    public String viewTreatmentPlan(@PathVariable Long id, Model model, Authentication authentication) {
        Users patient = getCurrentPatient(authentication);
        if (patient == null) {
            return "redirect:/login";
        }

        Optional<TreatmentPlan> treatmentPlanOpt = treatmentPlanService.getTreatmentPlanById(id);
        if (treatmentPlanOpt.isEmpty() || !treatmentPlanOpt.get().getPatient().getId().equals(patient.getId())) {
            return "redirect:/patient/treatment-plans";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("treatmentPlan", treatmentPlanOpt.get());

        return "patient/treatment-plan-view";
    }

    // Show quiz home page
    @GetMapping("/quiz")
    public String quizHome(Model model, Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        
        if (user != null) {
            if (user.getRole() == Users.Role.PATIENT) {
                Long quizCount = quizService.getUserQuizCount(user);
                List<QuizResponse> recentQuizzes = quizService.getUserQuizHistory(user);
                Long activeQuestionCount = quizService.getActiveQuestionCount();
                
                model.addAttribute("patient", user);
                model.addAttribute("quizCount", quizCount);
                model.addAttribute("recentQuizzes", recentQuizzes);
                model.addAttribute("activeQuestionCount", activeQuestionCount);
            }
        }
        
        return "patient/quiz-home";
    }

    // Start a new quiz
    @GetMapping("/quiz/take")
    public String takeQuiz(Model model, Authentication authentication) {

        Long activeQuestionCount = quizService.getActiveQuestionCount();
        
        if (activeQuestionCount == 0) {
            model.addAttribute("errorMessage", "No quiz questions are currently available.");
            return "patient/quiz-home";
        }
        
        // Now fetch the actual questions since we know there are some
        List<QuizQuestion> questions = quizService.getActiveQuestions();
        
        // Group questions by sections
        Map<String, List<QuizQuestion>> questionsBySection = quizService.getActiveQuestionsGroupedBySection();
        
        // Create section data for JavaScript (section name -> question count)
        Map<String, Integer> sectionData = new HashMap<>();
        questionsBySection.forEach((section, sectionQuestions) -> {
            sectionData.put(section, sectionQuestions.size());
        });
        
        model.addAttribute("questions", questions);
        model.addAttribute("questionsBySection", questionsBySection);
        model.addAttribute("sectionData", sectionData);
        model.addAttribute("totalQuestions", activeQuestionCount.intValue()); // Convert Long to int
        return "patient/quiz-take";
    }

    // Submit quiz answers
    @PostMapping("/quiz/submit")
    public String submitQuiz(@RequestParam Map<String, String> allParams,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Users user = userRepository.findByEmail(email);
            
            if (user != null) {
                if (user.getRole() == Users.Role.PATIENT) {
                    Map<Long, Integer> answers = new HashMap<>();
                    
                    // Parse answers from form parameters
                    for (Map.Entry<String, String> entry : allParams.entrySet()) {
                        if (entry.getKey().startsWith("question_")) {
                            String questionIdStr = entry.getKey().substring("question_".length());
                            Long questionId = Long.parseLong(questionIdStr);
                            Integer answerValue = Integer.parseInt(entry.getValue());
                            answers.put(questionId, answerValue);
                        }
                    }
                    
                    // Submit quiz
                    QuizResponse response = quizService.submitQuiz(user, answers);
                    
                    // Calculate section scores and recommendations
                    Map<String, Map<String, Integer>> sectionScores = quizService.calculateSectionScores(answers);
                    Map<String, String> recommendations = quizService.generateSectionRecommendations(sectionScores);
                    
                    // Store in session for results page
                    redirectAttributes.addFlashAttribute("sectionScores", sectionScores);
                    redirectAttributes.addFlashAttribute("recommendations", recommendations);
                    redirectAttributes.addFlashAttribute("successMessage", "Quiz completed successfully!");
                    return "redirect:/patient/quiz/results/" + response.getId();
                }
            }
            
            redirectAttributes.addFlashAttribute("errorMessage", "Error: User or patient not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting quiz: " + e.getMessage());
        }
        
        return "redirect:/patient/quiz";
    }

    // Show quiz results
    @GetMapping("/quiz/results/{id}")
    public String showResults(@PathVariable Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        
        if (user != null) {
            if (user.getRole() == Users.Role.PATIENT) {
                // Get the quiz response and verify it belongs to the current patient
                List<QuizResponse> patientResponses = quizService.getUserQuizHistory(user);
                QuizResponse response = patientResponses.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);
                
                if (response != null) {
                    model.addAttribute("response", response);
                    model.addAttribute("patient", user);
                    
                    // If section scores and recommendations are not in flash attributes (e.g., viewing old results),
                    // we won't have the detailed section analysis, but we can still show the basic results
                    if (!model.containsAttribute("sectionScores")) {
                        // For historical results, we could recalculate if needed
                        // For now, just show basic results
                        model.addAttribute("showBasicResults", true);
                    }
                    
                    return "patient/quiz-results";
                }
            }
        }
        
        return "redirect:/patient/quiz";
    }

    // Show quiz history
    @GetMapping("/quiz/history")
    public String showHistory(Model model, Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        
        if (user != null) {
            if (user.getRole() == Users.Role.PATIENT) {
                List<QuizResponse> quizHistory = quizService.getUserQuizHistory(user);
                
                // Calculate statistics
                if (quizHistory != null && !quizHistory.isEmpty()) {
                    double bestScore = quizHistory.stream()
                        .mapToDouble(QuizResponse::getScorePercentage)
                        .max()
                        .orElse(0.0);
                    
                    double averageScore = quizHistory.stream()
                        .mapToDouble(QuizResponse::getScorePercentage)
                        .average()
                        .orElse(0.0);
                    
                    double latestScore = quizHistory.get(0).getScorePercentage();
                    
                    model.addAttribute("bestScore", Math.round(bestScore));
                    model.addAttribute("averageScore", Math.round(averageScore));
                    model.addAttribute("latestScore", Math.round(latestScore));
                }
                
                model.addAttribute("patient", user);
                model.addAttribute("quizHistory", quizHistory);
                return "patient/quiz-history";
            }
        }
        
        return "redirect:/patient/quiz";
    }

    // Show detailed answers for a specific quiz
    @GetMapping("/quiz/history/{id}/answers")
    public String showQuizAnswers(@PathVariable Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        
        if (user != null && user.getRole() == Users.Role.PATIENT) {
            // Get the quiz response and verify it belongs to the current patient
            List<QuizResponse> patientResponses = quizService.getUserQuizHistory(user);
            QuizResponse response = patientResponses.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (response != null) {
                // Get user's answers for this quiz
                Map<Long, Integer> userAnswers = quizService.getQuizAnswers(response);
                
                // Get the questions that were answered (we need to get all questions and filter by answered ones)
                Map<String, List<QuizQuestion>> questionsBySection = quizService.getQuestionsGroupedBySection(userAnswers.keySet());
                
                // Calculate section scores for this specific quiz
                Map<String, Map<String, Integer>> sectionScores = quizService.calculateSectionScores(userAnswers);
                
                model.addAttribute("response", response);
                model.addAttribute("patient", user);
                model.addAttribute("userAnswers", userAnswers);
                model.addAttribute("questionsBySection", questionsBySection);
                model.addAttribute("sectionScores", sectionScores);
                
                return "patient/quiz-answers";
            }
        }
        
        return "redirect:/patient/quiz/history";
    }

    // Helper method to get current patient
    private Users getCurrentPatient(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        
        if (user != null && user.getRole() == Users.Role.PATIENT) {
            return user;
        }
        
        return null;
    }
} 