package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.TreatmentPlan;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    
    // Find all treatment plans created by a specific doctor
    List<TreatmentPlan> findByDoctorOrderByCreatedAtDesc(Users doctor);
    
    // Find all treatment plans for a specific patient
    List<TreatmentPlan> findByPatientOrderByCreatedAtDesc(Users patient);
    
    // Find treatment plans by status
    List<TreatmentPlan> findByStatusOrderByCreatedAtDesc(TreatmentPlan.Status status);
    
    // Find treatment plans by doctor and status
    List<TreatmentPlan> findByDoctorAndStatusOrderByCreatedAtDesc(Users doctor, TreatmentPlan.Status status);
    
    // Find treatment plans by patient and status
    List<TreatmentPlan> findByPatientAndStatusOrderByCreatedAtDesc(Users patient, TreatmentPlan.Status status);
    
    // Search treatment plans by title containing keyword
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.title LIKE %:keyword% AND tp.doctor = :doctor ORDER BY tp.createdAt DESC")
    List<TreatmentPlan> findByTitleContainingAndDoctor(@Param("keyword") String keyword, @Param("doctor") Users doctor);
    
    // Count treatment plans by doctor
    Long countByDoctor(Users doctor);
    
    // Count active treatment plans by doctor
    Long countByDoctorAndStatus(Users doctor, TreatmentPlan.Status status);
    
    // Find recent treatment plans (limit 10)
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.doctor = :doctor ORDER BY tp.createdAt DESC")
    List<TreatmentPlan> findTop10ByDoctorOrderByCreatedAtDesc(@Param("doctor") Users doctor);
} 