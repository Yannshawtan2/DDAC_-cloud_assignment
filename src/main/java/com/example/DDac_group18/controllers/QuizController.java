package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.QuizQuestion;
import com.example.DDac_group18.model.data_schema.QuizResponse;
import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.UserRepository;
import com.example.DDac_group18.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/dietician/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserRepository userRepository;

    // List all quiz questions
    @GetMapping("/questions")
    public String listQuestions(Model model, Authentication authentication,
                               @RequestParam(required = false) String section,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        List<QuizQuestion> allQuestions;
        Map<String, List<QuizQuestion>> questionsBySection = quizService.getAllQuestionsGroupedBySection();
        List<String> allSections = quizService.getAllSections();
        
        if (section != null && !section.isEmpty()) {
            allQuestions = quizService.getQuestionsBySection(section);
            model.addAttribute("selectedSection", section);
        } else {
            allQuestions = quizService.getAllQuestions();
        }
        
        // Calculate pagination
        int totalQuestions = allQuestions.size();
        int totalPages = (int) Math.ceil((double) totalQuestions / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalQuestions);
        
        List<QuizQuestion> paginatedQuestions = totalQuestions > 0 
            ? allQuestions.subList(startIndex, endIndex) 
            : new ArrayList<>();
        
        // Calculate stats for current view (all or filtered by section)
        long activeCount, inactiveCount;
        if (section != null && !section.isEmpty()) {
            // Count active/inactive questions in the current section
            activeCount = allQuestions.stream().mapToLong(q -> q.getIsActive() ? 1L : 0L).sum();
            inactiveCount = totalQuestions - activeCount;
        } else {
            // Count active/inactive questions across all sections
            activeCount = quizService.getActiveQuestionCount();
            inactiveCount = totalQuestions - activeCount;
        }
        
        model.addAttribute("questions", paginatedQuestions);
        model.addAttribute("questionsBySection", questionsBySection);
        model.addAttribute("allSections", allSections);
        model.addAttribute("activeQuestionCount", activeCount);
        model.addAttribute("inactiveQuestionCount", inactiveCount);
        
        // Pagination attributes
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("startIndex", startIndex + 1);
        model.addAttribute("endIndex", endIndex);
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        
        return "dietician/quiz-questions-list";
    }

    // Show form to create new question
    @GetMapping("/questions/new")
    public String showCreateForm(Model model) {
        model.addAttribute("question", new QuizQuestion());
        model.addAttribute("allSections", quizService.getAllSections());
        return "dietician/quiz-question-form";
    }

    // Create new question
    @PostMapping("/questions")
    public String createQuestion(@ModelAttribute QuizQuestion question,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Users dietician = userRepository.findByEmail(email);
            
            if (dietician != null) {
                question.setCreatedBy(dietician);
                
                quizService.createQuestion(question);
                redirectAttributes.addFlashAttribute("successMessage", "Quiz question created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating question: " + e.getMessage());
        }
        
        return "redirect:/dietician/quiz/questions";
    }

    // Show question details
    @GetMapping("/questions/{id}")
    public String viewQuestion(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<QuizQuestion> questionOpt = quizService.getQuestionById(id);
        
        if (questionOpt.isPresent()) {
            QuizQuestion question = questionOpt.get();
            String currentUserEmail = authentication.getName();
            
            // Check if the current dietician owns this question or allow viewing all
            model.addAttribute("question", question);
            return "dietician/quiz-question-view";
        }
        
        return "redirect:/dietician/quiz/questions";
    }

    // Show form to edit question
    @GetMapping("/questions/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<QuizQuestion> questionOpt = quizService.getQuestionById(id);
        
        if (questionOpt.isPresent()) {
            QuizQuestion question = questionOpt.get();
            String currentUserEmail = authentication.getName();
            
            // Check if the current dietician owns this question
            if (question.getCreatedBy().getEmail().equals(currentUserEmail)) {
                model.addAttribute("question", question);
                model.addAttribute("allSections", quizService.getAllSections());
                return "dietician/quiz-question-edit";
            }
        }
        
        return "redirect:/dietician/quiz/questions";
    }

    // Update question
    @PostMapping("/questions/{id}")
    public String updateQuestion(@PathVariable Long id,
                                @ModelAttribute QuizQuestion question,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<QuizQuestion> existingQuestionOpt = quizService.getQuestionById(id);
            
            if (existingQuestionOpt.isPresent()) {
                QuizQuestion existingQuestion = existingQuestionOpt.get();
                String currentUserEmail = authentication.getName();
                
                // Check if the current dietician owns this question
                if (existingQuestion.getCreatedBy().getEmail().equals(currentUserEmail)) {
                    // Update fields including section
                    existingQuestion.setQuestionText(question.getQuestionText());
                    existingQuestion.setDescription(question.getDescription());
                    existingQuestion.setSection(question.getSection());
                    existingQuestion.setIsActive(question.getIsActive());
                    
                    quizService.updateQuestion(existingQuestion);
                    redirectAttributes.addFlashAttribute("successMessage", "Quiz question updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update this question.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Question not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating question: " + e.getMessage());
        }
        
        return "redirect:/dietician/quiz/questions";
    }

    // Delete question
    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<QuizQuestion> questionOpt = quizService.getQuestionById(id);
            
            if (questionOpt.isPresent()) {
                QuizQuestion question = questionOpt.get();
                String currentUserEmail = authentication.getName();
                
                // Check if the current dietician owns this question
                if (question.getCreatedBy().getEmail().equals(currentUserEmail)) {
                    quizService.deleteQuestion(id);
                    redirectAttributes.addFlashAttribute("successMessage", "Quiz question deleted successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to delete this question.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Question not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting question: " + e.getMessage());
        }
        
        return "redirect:/dietician/quiz/questions";
    }

    // API endpoint to get sections (for AJAX calls)
    @GetMapping("/api/sections")
    @ResponseBody
    public ResponseEntity<List<String>> getAllSections() {
        List<String> sections = quizService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    // API endpoint to get questions by section
    @GetMapping("/api/sections/{section}/questions")
    @ResponseBody
    public ResponseEntity<List<QuizQuestion>> getQuestionsBySection(@PathVariable String section) {
        List<QuizQuestion> questions = quizService.getQuestionsBySection(section);
        return ResponseEntity.ok(questions);
    }

    // View quiz results from all patients
    @GetMapping("/results")
    public String viewQuizResults(Model model, Authentication authentication) {
        List<QuizResponse> quizResponses = quizService.getAllQuizResponsesOrderByDate();
        
        // Calculate some summary statistics
        long totalResponses = quizResponses.size();
        long uniquePatients = quizResponses.stream()
                .map(response -> response.getPatient().getId())
                .distinct()
                .count();
        
        double averageScore = quizResponses.stream()
                .mapToDouble(QuizResponse::getScorePercentage)
                .average()
                .orElse(0.0);
        
        model.addAttribute("quizResponses", quizResponses);
        model.addAttribute("totalResponses", totalResponses);
        model.addAttribute("uniquePatients", uniquePatients);
        model.addAttribute("averageScore", Math.round(averageScore * 100.0) / 100.0);
        
        return "dietician/quiz-results-list";
    }

    // View detailed quiz result for a specific response
    @GetMapping("/results/{id}")
    public String viewQuizResult(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<QuizResponse> responseOpt = quizService.getQuizResponseById(id);
        
        if (responseOpt.isPresent()) {
            QuizResponse response = responseOpt.get();
            Map<Long, Integer> answers = quizService.getQuizAnswers(response);
            
            // Get questions grouped by section for this response
            Set<Long> questionIds = answers.keySet();
            Map<String, List<QuizQuestion>> questionsBySection = quizService.getQuestionsGroupedBySection(questionIds);
            
            // Calculate section scores
            Map<String, Map<String, Integer>> sectionScores = quizService.calculateSectionScores(answers);
            Map<String, String> sectionRecommendations = quizService.generateSectionRecommendations(sectionScores);
            
            model.addAttribute("quizResponse", response);
            model.addAttribute("answers", answers);
            model.addAttribute("questionsBySection", questionsBySection);
            model.addAttribute("sectionScores", sectionScores);
            model.addAttribute("sectionRecommendations", sectionRecommendations);
            
            return "dietician/quiz-result-detail";
        }
        
        return "redirect:/dietician/quiz/results";
    }
} 