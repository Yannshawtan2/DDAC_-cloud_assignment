package com.example.quiz.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz.dto.CreateQuizQuestionRequest;
import com.example.quiz.dto.QuizAnswerDto;
import com.example.quiz.dto.QuizQuestionDto;
import com.example.quiz.dto.QuizResponseDto;
import com.example.quiz.dto.SubmitQuizRequest;
import com.example.quiz.dto.UpdateQuizQuestionRequest;
import com.example.quiz.model.QuizAnswer;
import com.example.quiz.model.QuizQuestion;
import com.example.quiz.model.QuizResponse;
import com.example.quiz.repository.QuizAnswerRepository;
import com.example.quiz.repository.QuizQuestionRepository;
import com.example.quiz.repository.QuizResponseRepository;

@Service
public class QuizService {
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizResponseRepository quizResponseRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    // Quiz Question Management (for Dieticians)
    public List<QuizQuestionDto> getAllQuestions() {
        return quizQuestionRepository.findAllByOrderByCreatedAt()
                .stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QuizQuestionDto> getActiveQuestions() {
        return quizQuestionRepository.findByIsActiveTrueOrderByCreatedAt()
                .stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<QuizQuestionDto> getQuestionById(Long id) {
        return quizQuestionRepository.findById(id)
                .map(QuizQuestionDto::fromEntity);
    }

    public QuizQuestionDto createQuestion(CreateQuizQuestionRequest request) {
        // Validate that the creator exists and has DIETICIAN role
        if (!userServiceClient.validateUserRole(request.getCreatedBy(), "DIETICIAN")) {
            logger.error("Invalid dietician user or insufficient privileges: {}", request.getCreatedBy());
            throw new RuntimeException("Only dieticians can create quiz questions");
        }

        QuizQuestion question = new QuizQuestion(
                request.getQuestionText(),
                request.getDescription(),
                request.getSection(),
                request.getCreatedBy()
        );
        question.setIsActive(request.getIsActive());
        QuizQuestion savedQuestion = quizQuestionRepository.save(question);
        logger.info("Created quiz question with ID: {} by dietician: {}", savedQuestion.getId(), request.getCreatedBy());
        return QuizQuestionDto.fromEntity(savedQuestion);
    }

    public QuizQuestionDto updateQuestion(Long id, UpdateQuizQuestionRequest request) {
        Optional<QuizQuestion> questionOpt = quizQuestionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Quiz question not found with id: " + id);
        }

        QuizQuestion question = questionOpt.get();
        if (request.getQuestionText() != null) {
            question.setQuestionText(request.getQuestionText());
        }
        if (request.getDescription() != null) {
            question.setDescription(request.getDescription());
        }
        if (request.getSection() != null) {
            question.setSection(request.getSection());
        }
        if (request.getIsActive() != null) {
            question.setIsActive(request.getIsActive());
        }

        QuizQuestion updatedQuestion = quizQuestionRepository.save(question);
        return QuizQuestionDto.fromEntity(updatedQuestion);
    }

    public void deleteQuestion(Long id) {
        if (!quizQuestionRepository.existsById(id)) {
            throw new RuntimeException("Quiz question not found with id: " + id);
        }
        quizQuestionRepository.deleteById(id);
    }

    public Long getActiveQuestionCount() {
        return quizQuestionRepository.countByIsActiveTrue();
    }

    // Section-related methods
    public List<String> getAllSections() {
        return quizQuestionRepository.findDistinctSections();
    }

    public List<String> getActiveSections() {
        return quizQuestionRepository.findDistinctActiveSections();
    }

    public List<QuizQuestionDto> getQuestionsBySection(String section) {
        return quizQuestionRepository.findBySectionOrderByCreatedAt(section)
                .stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QuizQuestionDto> getActiveQuestionsBySection(String section) {
        return quizQuestionRepository.findBySectionAndIsActiveTrueOrderByCreatedAt(section)
                .stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Map<String, List<QuizQuestionDto>> getActiveQuestionsGroupedBySection() {
        List<QuizQuestion> questions = quizQuestionRepository.findActiveQuestionsGroupedBySection();
        return questions.stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.groupingBy(
                    q -> q.getSection() != null ? q.getSection() : "General",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }

    public Map<String, List<QuizQuestionDto>> getAllQuestionsGroupedBySection() {
        List<QuizQuestion> questions = quizQuestionRepository.findAllByOrderByCreatedAt();
        return questions.stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.groupingBy(
                    q -> q.getSection() != null ? q.getSection() : "General",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }

    // Quiz Taking (for Users)
    public QuizResponseDto submitQuiz(SubmitQuizRequest request) {
        // Validate that the patient exists and has PATIENT role
        if (!userServiceClient.validateUserRole(request.getPatientId(), "PATIENT")) {
            logger.error("Invalid patient user or insufficient privileges: {}", request.getPatientId());
            throw new RuntimeException("Only patients can submit quiz responses");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByIsActiveTrueOrderByCreatedAt();
        
        int totalScore = 0;
        int maxPossibleScore = questions.size() * 5; // 5 is max rating per question
        
        // Calculate total score
        for (Map.Entry<Long, Integer> entry : request.getAnswers().entrySet()) {
            totalScore += entry.getValue();
        }
        
        // Create quiz response
        QuizResponse response = new QuizResponse(request.getPatientId(), totalScore, maxPossibleScore);
        response = quizResponseRepository.save(response);
        
        // Save individual answers
        for (Map.Entry<Long, Integer> entry : request.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            Integer answerValue = entry.getValue();
            
            // Find the question
            Optional<QuizQuestion> questionOpt = quizQuestionRepository.findById(questionId);
            if (questionOpt.isPresent()) {
                QuizAnswer answer = new QuizAnswer(response, questionOpt.get(), answerValue);
                quizAnswerRepository.save(answer);
            }
        }
        
        logger.info("Quiz submitted successfully for patient: {} with total score: {}/{}", 
            request.getPatientId(), totalScore, maxPossibleScore);
        
        // Calculate section scores and recommendations
        Map<String, Map<String, Integer>> sectionScores = calculateSectionScores(request.getAnswers());
        Map<String, String> sectionRecommendations = generateSectionRecommendations(sectionScores);
        
        QuizResponseDto responseDto = QuizResponseDto.fromEntity(response);
        responseDto.setSectionScores(sectionScores);
        responseDto.setSectionRecommendations(sectionRecommendations);
        
        return responseDto;
    }

    public Map<String, Map<String, Integer>> calculateSectionScores(Map<Long, Integer> answers) {
        Map<String, Map<String, Integer>> sectionScores = new HashMap<>();
        Map<String, List<QuizQuestionDto>> questionsBySection = getActiveQuestionsGroupedBySection();
        
        for (Map.Entry<String, List<QuizQuestionDto>> sectionEntry : questionsBySection.entrySet()) {
            String section = sectionEntry.getKey();
            List<QuizQuestionDto> sectionQuestions = sectionEntry.getValue();
            
            int sectionScore = 0;
            int maxSectionScore = sectionQuestions.size() * 5;
            int answeredQuestions = 0;
            
            for (QuizQuestionDto question : sectionQuestions) {
                Integer answer = answers.get(question.getId());
                if (answer != null) {
                    sectionScore += answer;
                    answeredQuestions++;
                }
            }
            
            Map<String, Integer> scoreData = new HashMap<>();
            scoreData.put("score", sectionScore);
            scoreData.put("maxScore", maxSectionScore);
            scoreData.put("questionCount", sectionQuestions.size());
            scoreData.put("answeredCount", answeredQuestions);
            
            sectionScores.put(section, scoreData);
        }
        
        return sectionScores;
    }

    public Map<String, String> generateSectionRecommendations(Map<String, Map<String, Integer>> sectionScores) {
        Map<String, String> recommendations = new HashMap<>();
        
        for (Map.Entry<String, Map<String, Integer>> entry : sectionScores.entrySet()) {
            String section = entry.getKey();
            Map<String, Integer> scores = entry.getValue();
            
            int score = scores.get("score");
            int maxScore = scores.get("maxScore");
            double percentage = maxScore > 0 ? (double) score / maxScore * 100 : 0;
            
            String recommendation = generateRecommendationForSection(section, percentage);
            recommendations.put(section, recommendation);
        }
        
        return recommendations;
    }

    private String generateRecommendationForSection(String section, double percentage) {
        String level;
        String baseRecommendation;
        
        if (percentage >= 80) {
            level = "Excellent";
        } else if (percentage >= 65) {
            level = "Good";
        } else if (percentage >= 50) {
            level = "Fair";
        } else {
            level = "Needs Improvement";
        }
        
        switch (section.toLowerCase()) {
            case "diet":
                if (percentage >= 80) {
                    baseRecommendation = "Outstanding dietary management! Continue your excellent carb monitoring and food choices.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good dietary habits! Consider fine-tuning portion sizes and meal timing.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate dietary management. Focus on consistent carb counting and choosing low-GI foods.";
                } else {
                    baseRecommendation = "Dietary habits need significant improvement. Consider working with a dietitian for personalized meal planning.";
                }
                break;
                
            case "meal planning":
                if (percentage >= 80) {
                    baseRecommendation = "Excellent meal planning! Your consistent schedule and balanced meals are perfect for diabetes management.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good meal planning habits! Try to be more consistent with meal timing and advance preparation.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate meal planning. Focus on regular meal times and preparing balanced diabetes-friendly meals.";
                } else {
                    baseRecommendation = "Meal planning needs improvement. Start with simple meal prep and establish regular eating schedules.";
                }
                break;
                
            case "physical activity":
                if (percentage >= 80) {
                    baseRecommendation = "Excellent physical activity! Keep up the great work with regular exercise and blood sugar monitoring.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good activity level! Try to increase consistency and always monitor blood sugar around exercise.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate activity level. Aim for more regular exercise and improve blood sugar monitoring habits.";
                } else {
                    baseRecommendation = "Physical activity needs significant improvement. Start with gentle daily walks and consult your doctor.";
                }
                break;
                
            case "sleep schedule":
                if (percentage >= 80) {
                    baseRecommendation = "Excellent sleep and stress management! Your routine supports stable blood sugar levels.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good sleep habits! Focus on more consistent bedtime routines and stress reduction techniques.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate sleep management. Work on establishing regular sleep schedules and managing daily stress.";
                } else {
                    baseRecommendation = "Sleep and stress management need improvement. Poor sleep affects blood sugar - prioritize sleep hygiene.";
                }
                break;
                
            case "blood sugar monitoring":
                if (percentage >= 80) {
                    baseRecommendation = "Excellent blood sugar monitoring! Your diligent tracking helps optimize your diabetes management.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good monitoring habits! Try to be more consistent, especially around meals and exercise.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate monitoring. Increase frequency of checks, particularly before and after meals.";
                } else {
                    baseRecommendation = "Blood sugar monitoring needs significant improvement. Regular checking is crucial for diabetes management.";
                }
                break;
                
            default:
                if (percentage >= 80) {
                    baseRecommendation = "Excellent management in this area! Continue your great habits.";
                } else if (percentage >= 65) {
                    baseRecommendation = "Good progress! Small improvements can lead to even better diabetes management.";
                } else if (percentage >= 50) {
                    baseRecommendation = "Moderate management. Focus on consistency and small daily improvements.";
                } else {
                    baseRecommendation = "This area needs attention. Consider discussing improvement strategies with your healthcare team.";
                }
        }
        
        return level + ": " + baseRecommendation;
    }

    public List<QuizResponseDto> getUserQuizHistory(String patientId) {
        return quizResponseRepository.findByPatientIdOrderByCompletedAtDesc(patientId)
                .stream()
                .map(QuizResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QuizResponseDto> getRecentQuizResponses() {
        return quizResponseRepository.findTop10ByOrderByCompletedAtDesc()
                .stream()
                .map(QuizResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<QuizResponseDto> getQuizResponseById(Long id) {
        Optional<QuizResponse> responseOpt = quizResponseRepository.findById(id);
        if (responseOpt.isEmpty()) {
            return Optional.empty();
        }
        
        QuizResponse response = responseOpt.get();
        QuizResponseDto responseDto = QuizResponseDto.fromEntity(response);
        
        // Get answers
        List<QuizAnswer> answers = quizAnswerRepository.findByQuizResponseOrderByQuestionId(response);
        List<QuizAnswerDto> answerDtos = answers.stream()
                .map(QuizAnswerDto::fromEntity)
                .collect(Collectors.toList());
        responseDto.setAnswers(answerDtos);
        
        return Optional.of(responseDto);
    }

    public Long getUserQuizCount(String patientId) {
        return quizResponseRepository.countByPatientId(patientId);
    }

    // Get all quiz responses for dietitian view
    public List<QuizResponseDto> getAllQuizResponses() {
        return quizResponseRepository.findAllByOrderByCompletedAtDesc()
                .stream()
                .map(QuizResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}