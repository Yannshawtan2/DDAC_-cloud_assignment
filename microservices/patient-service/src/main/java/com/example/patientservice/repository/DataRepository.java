package com.example.patientservice.repository;

import com.example.patientservice.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {
    Data findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Data findTopByUserIdOrderByWeightUpdatedAtDesc(Long userId);
}