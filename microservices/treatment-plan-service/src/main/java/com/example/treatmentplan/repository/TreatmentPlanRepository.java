package com.example.treatmentplan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.treatmentplan.model.TreatmentPlan;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {
    
    // Find all treatment plans created by a specific doctor
    List<TreatmentPlan> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
    
    // Find all treatment plans for a specific patient
    List<TreatmentPlan> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    
    // Find treatment plans by status
    List<TreatmentPlan> findByStatusOrderByCreatedAtDesc(TreatmentPlan.Status status);
    
    // Find treatment plans by doctor and status
    List<TreatmentPlan> findByDoctorIdAndStatusOrderByCreatedAtDesc(Long doctorId, TreatmentPlan.Status status);
    
    // Find treatment plans by patient and status
    List<TreatmentPlan> findByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, TreatmentPlan.Status status);
    
    // Search treatment plans by title containing keyword
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.title LIKE %:keyword% AND tp.doctorId = :doctorId ORDER BY tp.createdAt DESC")
    List<TreatmentPlan> findByTitleContainingAndDoctorId(@Param("keyword") String keyword, @Param("doctorId") Long doctorId);
    
    // Count treatment plans by doctor
    Long countByDoctorId(Long doctorId);
    
    // Count active treatment plans by doctor
    Long countByDoctorIdAndStatus(Long doctorId, TreatmentPlan.Status status);
    
    // Find recent treatment plans (limit 10)
    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.doctorId = :doctorId ORDER BY tp.createdAt DESC")
    List<TreatmentPlan> findTop10ByDoctorIdOrderByCreatedAtDesc(@Param("doctorId") Long doctorId);
}