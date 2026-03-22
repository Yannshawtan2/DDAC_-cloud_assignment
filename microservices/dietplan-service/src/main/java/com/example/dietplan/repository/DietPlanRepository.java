package com.example.dietplan.repository;

import com.example.dietplan.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    
    List<DietPlan> findByPatientId(Long patientId);
    
    List<DietPlan> findByDietitianId(Long dietitianId);
    
    List<DietPlan> findByStatus(DietPlan.Status status);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.dietitianId = :dietitianId AND dp.status = :status")
    List<DietPlan> findByDietitianIdAndStatus(@Param("dietitianId") Long dietitianId, @Param("status") DietPlan.Status status);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.patientId = :patientId AND dp.status = :status")
    List<DietPlan> findByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") DietPlan.Status status);

    @Query("SELECT COUNT(dp) FROM DietPlan dp WHERE dp.dietitianId = :dietitianId")
    Long countByDietitianId(@Param("dietitianId") Long dietitianId);

    @Query("SELECT COUNT(dp) FROM DietPlan dp WHERE dp.dietitianId = :dietitianId AND dp.status = :status")
    Long countByDietitianIdAndStatus(@Param("dietitianId") Long dietitianId, @Param("status") DietPlan.Status status);

    @Query("SELECT COUNT(dp) FROM DietPlan dp WHERE dp.patientId = :patientId")
    Long countByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT DISTINCT dp.patientId FROM DietPlan dp WHERE dp.dietitianId = :dietitianId")
    List<Long> findDistinctPatientIdsByDietitianId(@Param("dietitianId") Long dietitianId);
}