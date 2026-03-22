package com.example.patientservice.service;

import com.example.patientservice.model.Data;
import com.example.patientservice.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Data entity
 * 
 * Handles business logic for patient health data management including
 * CRUD operations, validation, and business rules.
 */
@Service
@Transactional
public class DataService {
    
    private final DataRepository dataRepository;
    
    @Autowired
    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }
    
    /**
     * Get all health data records
     */
    @Transactional(readOnly = true)
    public List<Data> getAllHealthData() {
        return dataRepository.findAll();
    }
    
    /**
     * Get health data by ID
     */
    @Transactional(readOnly = true)
    public Optional<Data> getHealthDataById(Long id) {
        return dataRepository.findById(id);
    }
    
    /**
     * Get health data by user ID
     */
    @Transactional(readOnly = true)
    public Data getHealthDataByUserId(Long userId) {
        return dataRepository.findByUserId(userId);
    }
    
    /**
     * Create new health data
     */
    public Data createHealthData(Data data) {
        if (data.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // Check if health data already exists for this user
        if (dataRepository.existsByUserId(data.getUserId())) {
            throw new IllegalArgumentException("Health data already exists for user ID: " + data.getUserId());
        }
        
        return dataRepository.save(data);
    }
    
    /**
     * Update existing health data
     */
    public Data updateHealthData(Long id, Data updatedData) {
        Optional<Data> existingData = dataRepository.findById(id);
        if (existingData.isEmpty()) {
            throw new IllegalArgumentException("Health data not found with ID: " + id);
        }
        
        Data data = existingData.get();
        
        // Update fields if provided
        if (updatedData.getWeight() != null) {
            data.setWeight(updatedData.getWeight());
        }
        if (updatedData.getHeight() != null) {
            data.setHeight(updatedData.getHeight());
        }
        if (updatedData.getWaistCircumference() != null) {
            data.setWaistCircumference(updatedData.getWaistCircumference());
        }
        if (updatedData.getBloodPressure() != null) {
            data.setBloodPressure(updatedData.getBloodPressure());
        }
        if (updatedData.getBloodGlucoseLevel() != null) {
            data.setBloodGlucoseLevel(updatedData.getBloodGlucoseLevel());
        }
        
        return dataRepository.save(data);
    }
    
    /**
     * Update health data by user ID
     */
    public Data updateHealthDataByUserId(Long userId, Data updatedData) {
        Data existingData = dataRepository.findByUserId(userId);
        if (existingData == null) {
            throw new IllegalArgumentException("Health data not found for user ID: " + userId);
        }
        
        // Update fields if provided
        if (updatedData.getWeight() != null) {
            existingData.setWeight(updatedData.getWeight());
        }
        if (updatedData.getHeight() != null) {
            existingData.setHeight(updatedData.getHeight());
        }
        if (updatedData.getWaistCircumference() != null) {
            existingData.setWaistCircumference(updatedData.getWaistCircumference());
        }
        if (updatedData.getBloodPressure() != null) {
            existingData.setBloodPressure(updatedData.getBloodPressure());
        }
        if (updatedData.getBloodGlucoseLevel() != null) {
            existingData.setBloodGlucoseLevel(updatedData.getBloodGlucoseLevel());
        }
        
        return dataRepository.save(existingData);
    }
    
    /**
     * Delete health data by ID
     */
    public void deleteHealthData(Long id) {
        if (!dataRepository.existsById(id)) {
            throw new IllegalArgumentException("Health data not found with ID: " + id);
        }
        dataRepository.deleteById(id);
    }
    
    /**
     * Check if health data exists for user
     */
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return dataRepository.existsByUserId(userId);
    }
    
    /**
     * Get total health data count
     */
    @Transactional(readOnly = true)
    public long getTotalHealthDataCount() {
        return dataRepository.count();
    }
}