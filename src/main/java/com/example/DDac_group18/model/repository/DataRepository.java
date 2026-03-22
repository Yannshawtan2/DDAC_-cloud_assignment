package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<Data, Long> {
    Data findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Data findTopByUserIdOrderByWeightUpdatedAtDesc(Long userId);
}