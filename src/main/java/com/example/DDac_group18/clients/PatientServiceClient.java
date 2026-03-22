package com.example.DDac_group18.clients;

import com.example.DDac_group18.model.data_schema.Data;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class PatientServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceClient.class);
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${patient.service.url:https://your-patient-lambda-url/dev}")
    private String patientServiceUrl;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public boolean checkHealthDataExists(Long userId) {
        try {
            String url = patientServiceUrl + "/patient/health-data/user/" + userId + "/exists";
            logger.info("Checking if health data exists for user: {} at URL: {}", userId, url);
            
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            
            logger.info("Response status: {}, Response body: {}", response.getStatusCode(), response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                boolean exists = response.getBody();
                logger.info("Health data exists for user {}: {}", userId, exists);
                return exists;
            }
            logger.warn("Unexpected response status: {} or null body for user {}", response.getStatusCode(), userId);
            return false;
        } catch (Exception e) {
            logger.error("Error checking health data existence for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    public Data getLatestHealthDataByUserId(String userId) {
        try {
            logger.info("Getting latest health data for user: {}", userId);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(
                patientServiceUrl + "/api/health-data/latest/" + userId, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToData(response.getBody());
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting latest health data for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    public boolean saveHealthData(Data data) {
        try {
            logger.info("Saving health data for user: {}", data.getUserId());
            
            Map<String, Object> request = new HashMap<>();
            request.put("userId", data.getUserId());
            request.put("weight", data.getWeight());
            request.put("height", data.getHeight());
            request.put("waistCircumference", data.getWaistCircumference());
            if (data.getBloodPressure() != null) {
                request.put("bloodPressure", data.getBloodPressure());
            }
            if (data.getBloodGlucoseLevel() != null) {
                request.put("bloodGlucoseLevel", data.getBloodGlucoseLevel());
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.postForEntity(
                patientServiceUrl + "/api/health-data", entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception e) {
            logger.error("Error saving health data: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateHealthData(Data data) {
        try {
            logger.info("Updating health data for user: {}", data.getUserId());
            
            Map<String, Object> request = new HashMap<>();
            request.put("weight", data.getWeight());
            request.put("height", data.getHeight());
            request.put("waistCircumference", data.getWaistCircumference());
            if (data.getBloodPressure() != null) {
                request.put("bloodPressure", data.getBloodPressure());
            }
            if (data.getBloodGlucoseLevel() != null) {
                request.put("bloodGlucoseLevel", data.getBloodGlucoseLevel());
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<Map> response = restTemplate.exchange(
                patientServiceUrl + "/api/health-data/" + data.getUserId(), 
                HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error updating health data: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteMedicalReading(Long userId, String readingType) {
        try {
            logger.info("Deleting medical reading {} for user: {}", readingType, userId);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                patientServiceUrl + "/api/health-data/" + userId + "/reading/" + readingType,
                HttpMethod.DELETE, null, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error deleting medical reading: {}", e.getMessage());
            return false;
        }
    }

    private Data mapToData(Map<String, Object> dataMap) {
        if (dataMap == null) return null;
        
        Data data = new Data();
        if (dataMap.get("id") != null) {
            data.setId(Long.valueOf(dataMap.get("id").toString()));
        }
        if (dataMap.get("userId") != null) {
            data.setUserId(Long.valueOf(dataMap.get("userId").toString()));
        }
        if (dataMap.get("weight") != null) {
            data.setWeight(Double.valueOf(dataMap.get("weight").toString()));
        }
        if (dataMap.get("height") != null) {
            data.setHeight(Double.valueOf(dataMap.get("height").toString()));
        }
        if (dataMap.get("waistCircumference") != null) {
            data.setWaistCircumference(Double.valueOf(dataMap.get("waistCircumference").toString()));
        }
        if (dataMap.get("bloodPressure") != null) {
            data.setBloodPressure(dataMap.get("bloodPressure").toString());
        }
        if (dataMap.get("bloodGlucoseLevel") != null) {
            data.setBloodGlucoseLevel(Double.valueOf(dataMap.get("bloodGlucoseLevel").toString()));
        }
        
        return data;
    }
}