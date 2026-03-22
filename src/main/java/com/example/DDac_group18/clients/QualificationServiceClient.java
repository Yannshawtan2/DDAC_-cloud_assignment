package com.example.DDac_group18.clients;

import com.example.DDac_group18.model.data_schema.QualificationRequest;
import com.example.DDac_group18.model.data_schema.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QualificationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(QualificationServiceClient.class);

    @Value("${qualification.service.url:https://dafoepzir0.execute-api.us-east-1.amazonaws.com/dev/qualify}")
    private String qualificationServiceUrl;

    private final ObjectMapper objectMapper;

    public QualificationServiceClient() {
        this.objectMapper = new ObjectMapper();
    }

    public QualificationRequest createRequest(String applicantName, String applicantEmail, 
                                           Users.Role requestedRole, String licenseNumber, 
                                           String licenseType, String password, 
                                           String s3FileKey, String originalFilename, 
                                           String contentType) throws IOException, InterruptedException {
        
        Map<String, Object> requestBody = Map.of(
                "applicantName", applicantName,
                "applicantEmail", applicantEmail,
                "requestedRole", requestedRole.toString(),
                "licenseNumber", licenseNumber,
                "licenseType", licenseType,
                "password", password,
                "s3FileKey", s3FileKey,
                "originalFilename", originalFilename,
                "fileContentType", contentType
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequest(response.body());
        } else {
            throw new RuntimeException("Failed to create qualification request: " + response.body());
        }
    }

    public List<QualificationRequest> getRequestsByEmail(String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests?email=" + email))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequestList(response.body());
        } else {
            throw new RuntimeException("Failed to get qualification requests: " + response.body());
        }
    }

    public List<QualificationRequest> getAllRequests() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests"))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequestList(response.body());
        } else {
            throw new RuntimeException("Failed to get qualification requests: " + response.body());
        }
    }

    public List<QualificationRequest> getPendingRequests() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests/pending"))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequestList(response.body());
        } else {
            throw new RuntimeException("Failed to get pending qualification requests: " + response.body());
        }
    }

    public QualificationRequest approveRequest(Long requestId, String adminNotes, String reviewedBy) 
            throws IOException, InterruptedException {
        
        String approveUrl = qualificationServiceUrl + "/qualification-requests/" + requestId + "/approve";
        logger.info("Attempting to approve qualification request {} at URL: {}", requestId, approveUrl);
        
        Map<String, String> requestBody = Map.of(
                "adminNotes", adminNotes,
                "reviewedBy", reviewedBy
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        logger.debug("Request body: {}", jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(approveUrl))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(25))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
        logger.info("Received response with status code: {} for request {}", response.statusCode(), requestId);
        logger.debug("Response body: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequest(response.body());
        } else {
            logger.error("Failed to approve qualification request {}: Status {}, Body: {}", 
                requestId, response.statusCode(), response.body());
            throw new RuntimeException("Failed to approve qualification request: " + response.body());
        }
    }

    public QualificationRequest rejectRequest(Long requestId, String adminNotes, String reviewedBy) 
            throws IOException, InterruptedException {
        
        String rejectUrl = qualificationServiceUrl + "/qualification-requests/" + requestId + "/reject";
        logger.info("Attempting to reject qualification request {} at URL: {}", requestId, rejectUrl);
        
        Map<String, String> requestBody = Map.of(
                "adminNotes", adminNotes,
                "reviewedBy", reviewedBy
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        logger.debug("Request body: {}", jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(rejectUrl))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(25))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
        logger.info("Received response with status code: {} for request {}", response.statusCode(), requestId);
        logger.debug("Response body: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequest(response.body());
        } else {
            logger.error("Failed to reject qualification request {}: Status {}, Body: {}", 
                requestId, response.statusCode(), response.body());
            throw new RuntimeException("Failed to reject qualification request: " + response.body());
        }
    }

    public void deleteRequest(Long requestId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests/" + requestId))
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to delete qualification request: " + response.body());
        }
    }

    public QualificationRequest getRequestById(Long requestId) throws IOException, InterruptedException {
        String getUrl = qualificationServiceUrl + "/qualification-requests/" + requestId;
        logger.info("Attempting to get qualification request {} at URL: {}", requestId, getUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrl))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
        logger.info("Received response with status code: {} for request {}", response.statusCode(), requestId);
        logger.debug("Response body: {}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return parseQualificationRequest(response.body());
        } else {
            logger.error("Failed to get qualification request {}: Status {}, Body: {}", 
                requestId, response.statusCode(), response.body());
            throw new RuntimeException("Failed to get qualification request: " + response.body());
        }
    }

    private QualificationRequest parseQualificationRequest(String json) throws IOException {
        Map<String, Object> data = objectMapper.readValue(json, Map.class);
        
        QualificationRequest request = new QualificationRequest();
        request.setId(((Number) data.get("id")).longValue());
        request.setApplicantName((String) data.get("applicantName"));
        request.setApplicantEmail((String) data.get("applicantEmail"));
        request.setRequestedRole(Users.Role.valueOf((String) data.get("requestedRole")));
        request.setLicenseNumber((String) data.get("licenseNumber"));
        request.setLicenseType((String) data.get("licenseType"));
        request.setS3FileKey((String) data.get("s3FileKey"));
        request.setOriginalFileName((String) data.get("originalFilename"));
        request.setFileContentType((String) data.get("fileContentType"));
        request.setPassword((String) data.get("password")); // Include password from qualification service
        request.setStatus(QualificationRequest.Status.valueOf((String) data.get("status")));
        request.setAdminNotes((String) data.get("adminNotes"));
        request.setReviewedBy((String) data.get("reviewedBy"));
        
        return request;
    }

    private List<QualificationRequest> parseQualificationRequestList(String json) throws IOException {
        List<Map<String, Object>> dataList = objectMapper.readValue(json, List.class);
        List<QualificationRequest> requests = new ArrayList<>();
        
        for (Map<String, Object> data : dataList) {
            requests.add(parseQualificationRequestFromMap(data));
        }
        
        return requests;
    }

    private QualificationRequest parseQualificationRequestFromMap(Map<String, Object> data) {
        QualificationRequest request = new QualificationRequest();
        request.setId(((Number) data.get("id")).longValue());
        request.setApplicantName((String) data.get("applicantName"));
        request.setApplicantEmail((String) data.get("applicantEmail"));
        request.setRequestedRole(Users.Role.valueOf((String) data.get("requestedRole")));
        request.setLicenseNumber((String) data.get("licenseNumber"));
        request.setLicenseType((String) data.get("licenseType"));
        request.setS3FileKey((String) data.get("s3FileKey"));
        request.setOriginalFileName((String) data.get("originalFileName"));
        request.setFileContentType((String) data.get("fileContentType"));
        request.setPassword((String) data.get("password")); // Include password from qualification service
        request.setStatus(QualificationRequest.Status.valueOf((String) data.get("status")));
        request.setAdminNotes((String) data.get("adminNotes"));
        request.setReviewedBy((String) data.get("reviewedBy"));
        
        return request;
    }

    /**
     * Get the file key for a qualification request from the microservice
     * @param requestId The qualification request ID
     * @return The S3 file key
     */
    public String getLicenseFileKey(Long requestId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests/" + requestId + "/file-key"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Map<String, Object> data = objectMapper.readValue(response.body(), Map.class);
            return (String) data.get("fileKey");
        } else {
            throw new RuntimeException("Failed to get file key: " + response.body());
        }
    }

    /**
     * Get complete file information for a qualification request from the microservice
     * @param requestId The qualification request ID
     * @return Map containing fileKey, fileName, and contentType
     */
    public Map<String, String> getLicenseFileInfo(Long requestId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(qualificationServiceUrl + "/qualification-requests/" + requestId + "/file-info"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Map<String, Object> data = objectMapper.readValue(response.body(), Map.class);
            Map<String, String> result = new java.util.HashMap<>();
            result.put("fileKey", (String) data.get("fileKey"));
            result.put("fileName", (String) data.get("fileName"));
            result.put("contentType", (String) data.get("contentType"));
            return result;
        } else {
            throw new RuntimeException("Failed to get file info: " + response.body());
        }
    }
}
