package com.example.quiz.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz.dto.CreateQuizQuestionRequest;
import com.example.quiz.dto.QuizQuestionDto;
import com.example.quiz.dto.QuizResponseDto;
import com.example.quiz.dto.SubmitQuizRequest;
import com.example.quiz.dto.UpdateQuizQuestionRequest;
import com.example.quiz.service.QuizService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/quiz-service/quiz")
@CrossOrigin(origins = "*")
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizService quizService;

    // Quiz Question Management
    @GetMapping("/questions")
    public ResponseEntity<?> getAllQuestions(@RequestParam(value = "section", required = false) String section,
                                           @RequestParam(value = "active", required = false) Boolean active) {
        try {
            logger.info("Getting quiz questions with section: {}, active: {}", section, active);
            List<QuizQuestionDto> questions;
            
            if (section != null && !section.isEmpty()) {
                if (active != null && active) {
                    questions = quizService.getActiveQuestionsBySection(section);
                } else {
                    questions = quizService.getQuestionsBySection(section);
                }
            } else if (active != null && active) {
                questions = quizService.getActiveQuestions();
            } else {
                questions = quizService.getAllQuestions();
            }
            
            logger.info("Found {} quiz questions", questions.size());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error getting quiz questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz questions: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            logger.info("Getting quiz question with ID: {}", id);
            Optional<QuizQuestionDto> question = quizService.getQuestionById(id);
            
            if (question.isPresent()) {
                logger.info("Found quiz question: {}", question.get().getQuestionText());
                return ResponseEntity.ok(question.get());
            } else {
                logger.warn("Quiz question not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting quiz question by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz question: " + e.getMessage()));
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody CreateQuizQuestionRequest request) {
        try {
            logger.info("Creating new quiz question: {}", request.getQuestionText());
            QuizQuestionDto createdQuestion = quizService.createQuestion(request);
            logger.info("Created quiz question with ID: {}", createdQuestion.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
        } catch (RuntimeException e) {
            logger.error("Authorization or validation error creating quiz question", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating quiz question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create quiz question: " + e.getMessage()));
        }
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody UpdateQuizQuestionRequest request) {
        try {
            logger.info("Updating quiz question with ID: {}", id);
            QuizQuestionDto updatedQuestion = quizService.updateQuestion(id, request);
            logger.info("Updated quiz question: {}", updatedQuestion.getQuestionText());
            return ResponseEntity.ok(updatedQuestion);
        } catch (RuntimeException e) {
            logger.error("Quiz question not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating quiz question with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update quiz question: " + e.getMessage()));
        }
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            logger.info("Deleting quiz question with ID: {}", id);
            quizService.deleteQuestion(id);
            logger.info("Deleted quiz question with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Quiz question not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting quiz question with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete quiz question: " + e.getMessage()));
        }
    }

    // Quiz sections
    @GetMapping("/sections")
    public ResponseEntity<?> getSections(@RequestParam(value = "active", required = false) Boolean active) {
        try {
            logger.info("Getting quiz sections, active: {}", active);
            List<String> sections;
            
            if (active != null && active) {
                sections = quizService.getActiveSections();
            } else {
                sections = quizService.getAllSections();
            }
            
            logger.info("Found {} sections", sections.size());
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            logger.error("Error getting quiz sections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz sections: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/grouped")
    public ResponseEntity<?> getQuestionsGroupedBySection(@RequestParam(value = "active", required = false) Boolean active) {
        try {
            logger.info("Getting quiz questions grouped by section, active: {}", active);
            Map<String, List<QuizQuestionDto>> groupedQuestions;
            
            if (active != null && active) {
                groupedQuestions = quizService.getActiveQuestionsGroupedBySection();
            } else {
                groupedQuestions = quizService.getAllQuestionsGroupedBySection();
            }
            
            logger.info("Found questions in {} sections", groupedQuestions.size());
            return ResponseEntity.ok(groupedQuestions);
        } catch (Exception e) {
            logger.error("Error getting grouped quiz questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve grouped quiz questions: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getQuizStats() {
        try {
            logger.info("Getting quiz statistics");
            Long activeQuestionCount = quizService.getActiveQuestionCount();
            List<String> activeSections = quizService.getActiveSections();
            
            Map<String, Object> stats = Map.of(
                "activeQuestionCount", activeQuestionCount,
                "activeSectionCount", activeSections.size(),
                "activeSections", activeSections
            );
            
            logger.info("Quiz stats: {} active questions, {} active sections", activeQuestionCount, activeSections.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting quiz statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz statistics: " + e.getMessage()));
        }
    }

    // Quiz Taking and Responses
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@Valid @RequestBody SubmitQuizRequest request) {
        try {
            logger.info("Submitting quiz for patient: {}", request.getPatientId());
            QuizResponseDto response = quizService.submitQuiz(request);
            logger.info("Quiz submitted with ID: {} for patient: {}", response.getId(), request.getPatientId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Authorization or validation error submitting quiz", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting quiz for patient: {}", request.getPatientId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit quiz: " + e.getMessage()));
        }
    }

    @GetMapping("/responses")
    public ResponseEntity<?> getAllQuizResponses(@RequestParam(value = "patientId", required = false) String patientId,
                                                @RequestParam(value = "recent", required = false) Boolean recent) {
        try {
            logger.info("Getting quiz responses, patientId: {}, recent: {}", patientId, recent);
            List<QuizResponseDto> responses;
            
            if (patientId != null && !patientId.isEmpty()) {
                responses = quizService.getUserQuizHistory(patientId);
            } else if (recent != null && recent) {
                responses = quizService.getRecentQuizResponses();
            } else {
                responses = quizService.getAllQuizResponses();
            }
            
            logger.info("Found {} quiz responses", responses.size());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting quiz responses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz responses: " + e.getMessage()));
        }
    }

    @GetMapping("/responses/{id}")
    public ResponseEntity<?> getQuizResponseById(@PathVariable Long id) {
        try {
            logger.info("Getting quiz response with ID: {}", id);
            Optional<QuizResponseDto> response = quizService.getQuizResponseById(id);
            
            if (response.isPresent()) {
                logger.info("Found quiz response for patient: {}", response.get().getPatientId());
                return ResponseEntity.ok(response.get());
            } else {
                logger.warn("Quiz response not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting quiz response by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz response: " + e.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}/count")
    public ResponseEntity<?> getUserQuizCount(@PathVariable String patientId) {
        try {
            logger.info("Getting quiz count for patient: {}", patientId);
            Long count = quizService.getUserQuizCount(patientId);
            logger.info("Patient {} has completed {} quizzes", patientId, count);
            return ResponseEntity.ok(Map.of("patientId", patientId, "quizCount", count));
        } catch (Exception e) {
            logger.error("Error getting quiz count for patient: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz count: " + e.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<?> getUserQuizHistory(@PathVariable String patientId) {
        try {
            logger.info("Getting quiz history for patient: {}", patientId);
            List<QuizResponseDto> history = quizService.getUserQuizHistory(patientId);
            logger.info("Found {} quiz responses for patient: {}", history.size(), patientId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error getting quiz history for patient: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve quiz history: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "quiz-service"));
    }
}