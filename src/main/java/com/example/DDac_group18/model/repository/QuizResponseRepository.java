package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.QuizResponse;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResponseRepository extends JpaRepository<QuizResponse, Long> {
    
    List<QuizResponse> findByPatientOrderByCompletedAtDesc(Users patient);
    
    List<QuizResponse> findTop10ByOrderByCompletedAtDesc();
    
    Long countByPatient(Users patient);
    
    List<QuizResponse> findAllByOrderByCompletedAtDesc();
} 