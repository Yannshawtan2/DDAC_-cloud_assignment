package com.example.DDac_group18.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.access.key.id}")
    private String accessKeyId;

    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    @Value("${aws.session.token:}")
    private String sessionToken;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        if (sessionToken != null && !sessionToken.isEmpty() && !sessionToken.equals("YOUR_SESSION_TOKEN_HERE")) {
            // Use session credentials for temporary access
            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(accessKeyId, secretAccessKey, sessionToken);
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .build();
        } else {
            // Use basic credentials (for permanent access keys)
            com.amazonaws.auth.BasicAWSCredentials awsCredentials = new com.amazonaws.auth.BasicAWSCredentials(accessKeyId, secretAccessKey);
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
        }
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            // Generate unique file key
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileKey = folder + "/" + UUID.randomUUID().toString() + fileExtension;

            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // Upload to S3
            PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata);
            s3Client.putObject(request);

            return fileKey;
        } catch (AmazonServiceException e) {
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFile(String fileKey) throws IOException {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileKey);
            return s3Object.getObjectContent();
        } catch (AmazonServiceException e) {
            throw new IOException("Failed to download file from S3: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileKey) throws IOException {
        try {
            s3Client.deleteObject(bucketName, fileKey);
        } catch (AmazonServiceException e) {
            throw new IOException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }

    public String getFileUrl(String fileKey) {
        return s3Client.getUrl(bucketName, fileKey).toString();
    }

    public boolean fileExists(String fileKey) {
        return s3Client.doesObjectExist(bucketName, fileKey);
    }
} 