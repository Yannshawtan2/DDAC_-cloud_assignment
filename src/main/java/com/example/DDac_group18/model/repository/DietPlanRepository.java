package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.DietPlan;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    
    List<DietPlan> findByPatient(Users patient);
    
    List<DietPlan> findByDietitian(Users dietitian);
    
    List<DietPlan> findByStatus(DietPlan.Status status);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.dietitian = :dietitian AND dp.status = :status")
    List<DietPlan> findByDietitianAndStatus(@Param("dietitian") Users dietitian, @Param("status") DietPlan.Status status);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.patient = :patient AND dp.status = :status")
    List<DietPlan> findByPatientAndStatus(@Param("patient") Users patient, @Param("status") DietPlan.Status status);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.patient.name LIKE %:userName%")
    List<DietPlan> findByPatientNameContaining(@Param("userName") String userName);
} 