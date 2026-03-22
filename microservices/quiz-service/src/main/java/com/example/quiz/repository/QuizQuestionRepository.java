package com.example.quiz.repository;

import com.example.quiz.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    
    List<QuizQuestion> findByIsActiveTrueOrderByCreatedAt();
    
    List<QuizQuestion> findByCreatedByOrderByCreatedAt(String createdBy);
    
    List<QuizQuestion> findAllByOrderByCreatedAt();
    
    Long countByIsActiveTrue();
    
    // Section-based queries
    List<QuizQuestion> findBySectionOrderByCreatedAt(String section);
    
    List<QuizQuestion> findBySectionAndIsActiveTrueOrderByCreatedAt(String section);
    
    @Query("SELECT DISTINCT q.section FROM QuizQuestion q WHERE q.section IS NOT NULL ORDER BY q.section")
    List<String> findDistinctSections();
    
    @Query("SELECT DISTINCT q.section FROM QuizQuestion q WHERE q.section IS NOT NULL AND q.isActive = true ORDER BY q.section")
    List<String> findDistinctActiveSections();
    
    // Group questions by section
    @Query("SELECT q FROM QuizQuestion q WHERE q.isActive = true ORDER BY q.section, q.createdAt")
    List<QuizQuestion> findActiveQuestionsGroupedBySection();
}