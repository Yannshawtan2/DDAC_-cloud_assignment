package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.QuizAnswer;
import com.example.DDac_group18.model.data_schema.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    List<QuizAnswer> findByQuizResponse(QuizResponse quizResponse);
    
    List<QuizAnswer> findByQuizResponseOrderByQuestionId(QuizResponse quizResponse);
} 