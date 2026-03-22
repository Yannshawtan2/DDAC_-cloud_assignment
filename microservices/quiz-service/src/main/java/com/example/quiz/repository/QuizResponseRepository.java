package com.example.quiz.repository;

import com.example.quiz.model.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {
    
    List<QuizResponse> findByPatientIdOrderByCompletedAtDesc(String patientId);
    
    List<QuizResponse> findTop10ByOrderByCompletedAtDesc();
    
    Long countByPatientId(String patientId);
    
    List<QuizResponse> findAllByOrderByCompletedAtDesc();
}