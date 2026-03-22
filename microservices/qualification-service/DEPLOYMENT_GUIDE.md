# Qualification Management Microservice Deployment Guide

## 🏗️ **Architecture Overview**

This microservice architecture splits qualification management into two parts:

### **Main Application (Elastic Beanstalk)**
- **Responsibilities**: File uploads to S3, User Interface, Frontend
- **Components**: QualificationController (UI), S3Service, QualificationServiceClient
- **Handles**: File validation, S3 uploads, calling qualification microservice

### **Qualification Microservice (AWS Lambda)**
- **Responsibilities**: Business logic, Data management, Database operations  
- **Components**: REST API endpoints, Database operations, User account creation
- **Handles**: CRUD operations, qualification status management, user creation

## 📋 **Deployment Steps**

### **Step 1: Deploy Qualification Microservice**

1. **Build the qualification service:**
   ```bash
   cd microservices/qualification-service
   mvn clean package
   ```

2. **Upload JAR to AWS Lambda:**
   - JAR file: `target/qualification-service-lambda.jar`
   - Handler: `com.example.qualification.lambda.QualificationLambdaHandler::handleRequest`
   - Runtime: Java 21
   - Memory: 2048 MB
   - Timeout: 30 seconds

3. **Set Environment Variables:**
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/ddac
   SPRING_DATASOURCE_USERNAME=your-username
   SPRING_DATASOURCE_PASSWORD=your-password
   ```

4. **Create API Gateway:**
   - Resource: `/{proxy+}` 
   - Method: `ANY`
   - Integration: Lambda Proxy Integration
   - Enable CORS

### **Step 2: Update Main Application Configuration**

1. **Update `application.properties`:**
   ```properties
   # Replace with your actual API Gateway URL
   qualification.service.url=https://your-api-gateway-id.execute-api.us-east-1.amazonaws.com/prod
   ```

2. **Deploy to Elastic Beanstalk:**
   - Build your main application
   - Upload WAR/JAR to Elastic Beanstalk
   - Ensure S3 credentials are configured

### **Step 3: Database Setup**

The qualification microservice will use the same database as your main application. Ensure these tables exist:

```sql
-- Users table (if not exists)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'DIETICIAN', 'PATIENT') NOT NULL
);

-- Qualification requests table (should already exist)
CREATE TABLE qualification_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_name VARCHAR(255) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    requested_role ENUM('DOCTOR', 'DIETICIAN') NOT NULL,
    license_number VARCHAR(255) NOT NULL,
    license_type VARCHAR(255) NOT NULL,
    s3_file_key VARCHAR(500),
    original_filename VARCHAR(255),
    file_content_type VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    admin_notes TEXT,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME,
    reviewed_by VARCHAR(255)
);
```

## 🔄 **How It Works**

### **Qualification Submission Flow:**
1. User uploads file through Elastic Beanstalk UI
2. Main app validates file and uploads to S3
3. Main app calls qualification microservice with S3 file key
4. Microservice stores qualification data in database
5. Returns success response to main app

### **Status Check Flow:**
1. User requests status through Elastic Beanstalk UI  
2. Main app calls qualification microservice
3. Microservice queries database and returns status
4. Main app displays status to user

### **Admin Review Flow:**
1. Admin accesses review through main app
2. Main app calls microservice to get pending requests
3. Admin approves/rejects through main app
4. Main app calls microservice approval/rejection endpoint
5. Microservice creates user account (if approved) and updates status

## 📡 **API Endpoints**

### **Qualification Microservice Endpoints:**

- `GET /qualification-requests` - Get all requests
- `GET /qualification-requests?email={email}` - Get requests by email
- `GET /qualification-requests/pending` - Get pending requests
- `GET /qualification-requests/{id}` - Get specific request
- `POST /qualification-requests` - Create new request
- `POST /qualification-requests/{id}/approve` - Approve request
- `POST /qualification-requests/{id}/reject` - Reject request
- `DELETE /qualification-requests/{id}` - Delete request

## 🧪 **Testing**

### **Test the Microservice:**
1. Use the test events in `test-events/` folder
2. Test in Lambda console first
3. Test through API Gateway
4. Test integration with main application

### **Test File Upload Flow:**
1. Submit qualification through main app
2. Verify S3 file upload
3. Verify microservice receives correct data
4. Check database for new record

## 🔧 **Benefits of This Architecture**

1. **Separation of Concerns**: File handling vs business logic
2. **Scalability**: Lambda auto-scales, Elastic Beanstalk handles web traffic
3. **Cost Efficiency**: Pay per microservice invocation
4. **Maintainability**: Independent deployment and updates
5. **Security**: S3 credentials stay in main app, database access in microservice

## 🚨 **Important Notes**

- Keep S3 service in main application (file upload handling)
- Update the qualification service URL after API Gateway deployment
- Ensure both services can access the same database
- Test thoroughly before production deployment
- Monitor Lambda cold starts and optimize if needed

## 📝 **Migration Checklist**

- [ ] Build and deploy qualification microservice  
- [ ] Create API Gateway for microservice
- [ ] Update main app configuration with microservice URL
- [ ] Test file upload flow
- [ ] Test status checking
- [ ] Test admin review process
- [ ] Deploy main app to Elastic Beanstalk
- [ ] End-to-end testing
