package com.example.quiz.repository;

import com.example.quiz.model.QuizAnswer;
import com.example.quiz.model.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    List<QuizAnswer> findByQuizResponse(QuizResponse quizResponse);
    
    List<QuizAnswer> findByQuizResponseOrderByQuestionId(QuizResponse quizResponse);
}