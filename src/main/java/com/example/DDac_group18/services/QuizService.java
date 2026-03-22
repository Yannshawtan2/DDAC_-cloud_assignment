package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.*;
import com.example.DDac_group18.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizResponseRepository quizResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    // Quiz Question Management (for Dieticians)
    public List<QuizQuestion> getAllQuestions() {
        return quizQuestionRepository.findAllByOrderByCreatedAt();
    }

    public List<QuizQuestion> getActiveQuestions() {
        return quizQuestionRepository.findByIsActiveTrueOrderByCreatedAt();
    }

    public Optional<QuizQuestion> getQuestionById(Long id) {
        return quizQuestionRepository.findById(id);
    }

    public QuizQuestion createQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }

    public QuizQuestion updateQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
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

    public List<QuizQuestion> getQuestionsBySection(String section) {
        return quizQuestionRepository.findBySectionOrderByCreatedAt(section);
    }

    public List<QuizQuestion> getActiveQuestionsBySection(String section) {
        return quizQuestionRepository.findBySectionAndIsActiveTrueOrderByCreatedAt(section);
    }

    public Map<String, List<QuizQuestion>> getActiveQuestionsGroupedBySection() {
        List<QuizQuestion> questions = quizQuestionRepository.findActiveQuestionsGroupedBySection();
        return questions.stream()
                .collect(Collectors.groupingBy(
                    q -> q.getSection() != null ? q.getSection() : "General",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }

    public Map<String, List<QuizQuestion>> getAllQuestionsGroupedBySection() {
        List<QuizQuestion> questions = quizQuestionRepository.findAllByOrderByCreatedAt();
        return questions.stream()
                .collect(Collectors.groupingBy(
                    q -> q.getSection() != null ? q.getSection() : "General",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }

    // Quiz Taking (for Users)
    public QuizResponse submitQuiz(Users patient, Map<Long, Integer> answers) {
        List<QuizQuestion> questions = getActiveQuestions();
        
        int totalScore = 0;
        int maxPossibleScore = questions.size() * 5; // 5 is max rating per question
        
        // Calculate total score
        for (Map.Entry<Long, Integer> entry : answers.entrySet()) {
            totalScore += entry.getValue();
        }
        
        // Calculate section-based scores
        Map<String, Map<String, Integer>> sectionScores = calculateSectionScores(answers);
        
        // Create quiz response
        QuizResponse response = new QuizResponse(patient, totalScore, maxPossibleScore);
        response = quizResponseRepository.save(response);
        
        // Save individual answers
        for (Map.Entry<Long, Integer> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            Integer answerValue = entry.getValue();
            
            // Find the question
            Optional<QuizQuestion> questionOpt = quizQuestionRepository.findById(questionId);
            if (questionOpt.isPresent()) {
                QuizAnswer answer = new QuizAnswer(response, questionOpt.get(), answerValue);
                quizAnswerRepository.save(answer);
            }
        }
        
        return response;
    }

    public Map<String, Map<String, Integer>> calculateSectionScores(Map<Long, Integer> answers) {
        Map<String, Map<String, Integer>> sectionScores = new HashMap<>();
        Map<String, List<QuizQuestion>> questionsBySection = getActiveQuestionsGroupedBySection();
        
        for (Map.Entry<String, List<QuizQuestion>> sectionEntry : questionsBySection.entrySet()) {
            String section = sectionEntry.getKey();
            List<QuizQuestion> sectionQuestions = sectionEntry.getValue();
            
            int sectionScore = 0;
            int maxSectionScore = sectionQuestions.size() * 5;
            int answeredQuestions = 0;
            
            for (QuizQuestion question : sectionQuestions) {
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

    public List<QuizResponse> getUserQuizHistory(Users patient) {
        return quizResponseRepository.findByPatientOrderByCompletedAtDesc(patient);
    }

    public List<QuizResponse> getRecentQuizResponses() {
        return quizResponseRepository.findTop10ByOrderByCompletedAtDesc();
    }

    public Optional<QuizResponse> getQuizResponseById(Long id) {
        return quizResponseRepository.findById(id);
    }

    public Long getUserQuizCount(Users patient) {
        return quizResponseRepository.countByPatient(patient);
    }

    // Get all quiz responses for dietitian view
    public List<QuizResponse> getAllQuizResponses() {
        return quizResponseRepository.findAll();
    }

    // Get quiz responses ordered by completion date (newest first)
    public List<QuizResponse> getAllQuizResponsesOrderByDate() {
        return quizResponseRepository.findAllByOrderByCompletedAtDesc();
    }

    // Get user's answers for a specific quiz response
    public Map<Long, Integer> getQuizAnswers(QuizResponse response) {
        List<QuizAnswer> answers = quizAnswerRepository.findByQuizResponseOrderByQuestionId(response);
        Map<Long, Integer> answerMap = new HashMap<>();
        
        for (QuizAnswer answer : answers) {
            answerMap.put(answer.getQuestion().getId(), answer.getAnswerValue());
        }
        
        return answerMap;
    }

    // Get questions grouped by section for specific question IDs
    public Map<String, List<QuizQuestion>> getQuestionsGroupedBySection(Set<Long> questionIds) {
        List<QuizQuestion> questions = new ArrayList<>();
        
        for (Long questionId : questionIds) {
            Optional<QuizQuestion> questionOpt = quizQuestionRepository.findById(questionId);
            if (questionOpt.isPresent()) {
                questions.add(questionOpt.get());
            }
        }
        
        return questions.stream()
                .collect(Collectors.groupingBy(
                    q -> q.getSection() != null ? q.getSection() : "General",
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }

    // Helper method to create default diabetes diet questions with sections
    // // public void createDefaultQuestions(Users dietician) {
    //     // Diet section questions
    //     createQuestionWithSection("How often do you monitor your carbohydrate intake?",
    //             "Rate how consistently you track carbs in your daily meals", "Diet", dietician);
    //     createQuestionWithSection("How well do you follow portion control guidelines?",
    //             "Rate how well you control your serving sizes", "Diet", dietician);
    //     createQuestionWithSection("How often do you choose low glycemic index foods?",
    //             "Rate how often you choose foods that won't spike blood sugar", "Diet", dietician);
    //     createQuestionWithSection("How well do you avoid sugary drinks and snacks?",
    //             "Rate how successfully you avoid high-sugar beverages and treats", "Diet", dietician);
    //     createQuestionWithSection("How consistently do you read nutrition labels?",
    //             "Rate how often you check nutritional information before eating", "Diet", dietician);

    //     // Meal Planning section questions
    //     createQuestionWithSection("How frequently do you eat at regular meal times?",
    //             "Rate how regularly you eat breakfast, lunch, and dinner", "Meal Planning", dietician);
    //     createQuestionWithSection("How well do you plan your meals in advance?",
    //             "Rate how consistently you prepare and plan your diabetes-friendly meals", "Meal Planning", dietician);
    //     createQuestionWithSection("How well do you balance proteins, carbs, and fats in your diet?",
    //             "Rate how well you create balanced, diabetes-friendly meals", "Meal Planning", dietician);

    //     // Physical Activity section questions
    //     createQuestionWithSection("How often do you engage in physical exercise?",
    //             "Rate how frequently you participate in physical activities", "Physical Activity", dietician);
    //     createQuestionWithSection("How well do you monitor your blood sugar before and after exercise?",
    //             "Rate how consistently you check glucose levels around physical activity", "Physical Activity", dietician);

    //     // Sleep Schedule section questions
    //     createQuestionWithSection("How consistent is your sleep schedule?",
    //             "Rate how regularly you go to bed and wake up at the same times", "Sleep Schedule", dietician);
    //     createQuestionWithSection("How well do you manage stress that affects your blood sugar?",
    //             "Rate how effectively you handle stress-related glucose fluctuations", "Sleep Schedule", dietician);
    // }

    private void createQuestionWithSection(String questionText, String description, String section, Users dietician) {
        QuizQuestion question = new QuizQuestion(questionText, description, section, dietician);
        quizQuestionRepository.save(question);
    }
} 