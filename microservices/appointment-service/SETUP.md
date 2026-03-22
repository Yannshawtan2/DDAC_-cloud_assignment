# Appointment Service Setup Guide

This guide provides comprehensive instructions for setting up and running the enhanced Appointment Service microservice with full doctor search, patient search, and advanced appointment management capabilities.

## Prerequisites

### Required Software

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+** (or compatible database)
- **AWS CLI** (for Lambda deployment)
- **Git**

### Development Tools (Recommended)

- IntelliJ IDEA or Eclipse
- Postman or similar API testing tool
- MySQL Workbench or similar database client

## Local Development Setup

### 1. Database Setup

#### Create Database and Tables

```sql
CREATE DATABASE appointment_db;
USE appointment_db;

-- Create users table for doctor/patient search functionality
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('DOCTOR', 'PATIENT', 'DIETICIAN', 'ADMIN') NOT NULL,
    name VARCHAR(255) NOT NULL,
    INDEX idx_role (role),
    INDEX idx_name (name),
    INDEX idx_email (email)
);

-- Create appointments table
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    doctor_id VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_date (date),
    INDEX idx_status (status),
    INDEX idx_doctor_date (doctor_id, date),
    INDEX idx_user_status (user_id, status)
);
```

#### Create User (Optional)

```sql
CREATE USER 'appointment_user'@'localhost' IDENTIFIED BY 'appointment_password';
GRANT ALL PRIVILEGES ON appointment_db.* TO 'appointment_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Insert Sample Data

```sql
-- Sample doctors
INSERT INTO users (email, password, role, name) VALUES
('dr.smith@hospital.com', 'password123', 'DOCTOR', 'Dr. John Smith'),
('dr.johnson@hospital.com', 'password123', 'DOCTOR', 'Dr. Sarah Johnson'),
('dr.brown@hospital.com', 'password123', 'DOCTOR', 'Dr. Michael Brown'),
('dr.davis@hospital.com', 'password123', 'DOCTOR', 'Dr. Emily Davis');

-- Sample patients
INSERT INTO users (email, password, role, name) VALUES
('patient1@email.com', 'password123', 'PATIENT', 'Alice Wilson'),
('patient2@email.com', 'password123', 'PATIENT', 'Bob Thompson'),
('patient3@email.com', 'password123', 'PATIENT', 'Carol Martinez'),
('patient4@email.com', 'password123', 'PATIENT', 'David Anderson');

-- Sample appointments
INSERT INTO appointments (user_id, doctor_id, date, time, status, notes) VALUES
('5', '1', '2024-01-15', '10:00:00', 'pending', 'Regular checkup'),
('6', '1', '2024-01-15', '11:00:00', 'confirmed', 'Follow-up visit'),
('7', '2', '2024-01-16', '14:30:00', 'pending', 'Consultation'),
('8', '2', '2024-01-16', '15:30:00', 'completed', 'Treatment completed');
```

### 2. Environment Configuration

#### Update Application Properties

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/appointment_db
spring.datasource.username=appointment_user
spring.datasource.password=appointment_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8082

# Application Configuration
spring.application.name=appointment-service

# Logging Configuration
logging.level.com.example.appointmentservice=DEBUG
logging.level.org.springframework.web=DEBUG

# Cross-Origin Configuration
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

### 3. Build and Run

#### Install Dependencies

```bash
cd microservices/appointment-service
mvn clean install
```

#### Run Application

```bash
mvn spring-boot:run
```

#### Verify Service

```bash
curl http://localhost:8082/api/appointments/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "appointment-service"
}
```

## Enhanced API Endpoints

### Appointment Management

- `GET /api/appointments` - Get all appointments
- `POST /api/appointments` - Create a new appointment
- `PUT /api/appointments/{id}` - Update an appointment
- `DELETE /api/appointments/{id}` - Delete an appointment
- `GET /api/appointments/{id}` - Get appointment by ID

### Patient Endpoints

- `GET /api/appointments/patient/{userId}` - Get all appointments for a patient

### Doctor Endpoints

- `GET /api/appointments/doctor/{doctorId}` - Get all appointments for a doctor
- `GET /api/appointments/doctor/{doctorId}/filtered` - Get doctor appointments with filters
- `GET /api/appointments/doctor/{doctorId}/with-patient-names` - Get appointments with patient names

### Doctor Search (NEW)

- `GET /api/appointments/search-doctors?name={name}` - Search doctors by name
- `GET /api/appointments/doctors` - Get all doctors

### Patient Search (NEW)

- `GET /api/appointments/search-patients?name={name}` - Search patients by name

### User Management (NEW)

- `GET /api/appointments/user/{userId}` - Get user by ID

### Appointment Status Management (NEW)

- `PUT /api/appointments/{appointmentId}/approve` - Approve an appointment
- `PUT /api/appointments/{appointmentId}/reject` - Reject an appointment
- `PUT /api/appointments/{appointmentId}/complete` - Complete an appointment
- `GET /api/appointments/confirmed` - Get all confirmed appointments

### Appointment Validation (NEW)

- `GET /api/appointments/check-conflict` - Check for appointment conflicts
- `POST /api/appointments/validate-booking` - Validate appointment booking

## Testing the Enhanced Service

### 1. Doctor Search Testing

```bash
# Search doctors by name
curl "http://localhost:8082/api/appointments/search-doctors?name=john"

# Get all doctors
curl "http://localhost:8082/api/appointments/doctors"
```

### 2. Patient Search Testing

```bash
# Search patients by name
curl "http://localhost:8082/api/appointments/search-patients?name=alice"
```

### 3. Advanced Appointment Filtering

```bash
# Get doctor appointments with status filter
curl "http://localhost:8082/api/appointments/doctor/1/filtered?status=pending"

# Get doctor appointments with patient name filter
curl "http://localhost:8082/api/appointments/doctor/1/filtered?patientName=alice"

# Get doctor appointments with both filters
curl "http://localhost:8082/api/appointments/doctor/1/filtered?status=pending&patientName=alice"
```

### 4. Appointment Validation Testing

```bash
# Check for appointment conflicts
curl "http://localhost:8082/api/appointments/check-conflict?doctorId=1&date=2024-01-15&time=10:00"

# Validate appointment booking
curl -X POST "http://localhost:8082/api/appointments/validate-booking" \
     -d "doctorId=1&date=2024-01-20&time=14:00"
```

### 5. Appointment Status Management

```bash
# Approve appointment
curl -X PUT "http://localhost:8082/api/appointments/1/approve"

# Reject appointment
curl -X PUT "http://localhost:8082/api/appointments/2/reject"

# Complete appointment
curl -X PUT "http://localhost:8082/api/appointments/3/complete"
```

## Frontend Integration

### 1. Enhanced Service Client

Create `AppointmentServiceClient.java` in your main application:

```java
@Component
public class AppointmentServiceClient {

    @Value("${microservices.appointment.url}")
    private String appointmentServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    // Doctor search methods
    public List<Users> searchDoctors(String name) {
        String url = appointmentServiceUrl + "/api/appointments/search-doctors";
        if (name != null && !name.trim().isEmpty()) {
            url += "?name=" + name;
        }
        ResponseEntity<Users[]> response = restTemplate.getForEntity(url, Users[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Users> getAllDoctors() {
        String url = appointmentServiceUrl + "/api/appointments/doctors";
        ResponseEntity<Users[]> response = restTemplate.getForEntity(url, Users[].class);
        return Arrays.asList(response.getBody());
    }

    // Patient search methods
    public List<Users> searchPatients(String name) {
        String url = appointmentServiceUrl + "/api/appointments/search-patients";
        if (name != null && !name.trim().isEmpty()) {
            url += "?name=" + name;
        }
        ResponseEntity<Users[]> response = restTemplate.getForEntity(url, Users[].class);
        return Arrays.asList(response.getBody());
    }

    // Enhanced appointment methods
    public List<Appointment> getDoctorAppointmentsWithFilters(String doctorId, String status, String patientName) {
        String url = appointmentServiceUrl + "/api/appointments/doctor/" + doctorId + "/filtered";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (status != null && !status.trim().isEmpty()) {
            builder.queryParam("status", status);
        }
        if (patientName != null && !patientName.trim().isEmpty()) {
            builder.queryParam("patientName", patientName);
        }
        ResponseEntity<Appointment[]> response = restTemplate.getForEntity(builder.toUriString(), Appointment[].class);
        return Arrays.asList(response.getBody());
    }

    // Appointment management methods
    public boolean approveAppointment(Long appointmentId) {
        String url = appointmentServiceUrl + "/api/appointments/" + appointmentId + "/approve";
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, null, Map.class);
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("success");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean rejectAppointment(Long appointmentId) {
        String url = appointmentServiceUrl + "/api/appointments/" + appointmentId + "/reject";
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, null, Map.class);
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("success");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean completeAppointment(Long appointmentId) {
        String url = appointmentServiceUrl + "/api/appointments/" + appointmentId + "/complete";
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, null, Map.class);
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("success");
        } catch (Exception e) {
            return false;
        }
    }

    // Validation methods
    public boolean hasAppointmentConflict(String doctorId, String date, String time) {
        String url = appointmentServiceUrl + "/api/appointments/check-conflict";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("doctorId", doctorId)
            .queryParam("date", date)
            .queryParam("time", time);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(builder.toUriString(), Map.class);
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("hasConflict");
        } catch (Exception e) {
            return true; // Assume conflict on error
        }
    }

    public String validateAppointmentBooking(String doctorId, String date, String time) {
        String url = appointmentServiceUrl + "/api/appointments/validate-booking";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("doctorId", doctorId);
        params.add("date", date);
        params.add("time", time);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
            Map<String, Object> result = response.getBody();
            Boolean valid = (Boolean) result.get("valid");
            return valid ? null : (String) result.get("error");
        } catch (Exception e) {
            return "Validation service unavailable";
        }
    }

    // Standard appointment methods
    public List<Appointment> getPatientAppointments(String userId) {
        String url = appointmentServiceUrl + "/api/appointments/patient/" + userId;
        ResponseEntity<Appointment[]> response = restTemplate.getForEntity(url, Appointment[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Appointment> getDoctorAppointments(String doctorId) {
        String url = appointmentServiceUrl + "/api/appointments/doctor/" + doctorId;
        ResponseEntity<Appointment[]> response = restTemplate.getForEntity(url, Appointment[].class);
        return Arrays.asList(response.getBody());
    }

    public Appointment createAppointment(Appointment appointment) {
        String url = appointmentServiceUrl + "/api/appointments";
        return restTemplate.postForObject(url, appointment, Appointment.class);
    }
}
```

### 2. Update Main Application Properties

Add to your main `application.properties`:

```properties
# Microservice URLs
microservices.appointment.url=http://localhost:8082
# For production: microservices.appointment.url=https://your-api-gateway-url
```

### 3. Enhanced Controller Integration

```java
// Patient Controller Updates
@GetMapping("/search-doctors")
@ResponseBody
public List<Users> searchDoctors(@RequestParam(required = false) String name) {
    return appointmentServiceClient.searchDoctors(name);
}

@PostMapping("/book-appointment")
public String bookAppointment(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
    // Validate appointment
    String validationError = appointmentServiceClient.validateAppointmentBooking(
        appointment.getDoctorId(),
        appointment.getDate().toString(),
        appointment.getTime().toString()
    );

    if (validationError != null) {
        redirectAttributes.addFlashAttribute("error", validationError);
        return "redirect:/patient/appointmentBooking";
    }

    // Create appointment
    appointmentServiceClient.createAppointment(appointment);
    redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
    return "redirect:/patient/appointments";
}

// Doctor Controller Updates
@GetMapping("/appointment")
public String viewDoctorAppointments(Model model, HttpSession session,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String patientName) {
    String doctorId = (String) session.getAttribute("userId");
    List<Appointment> appointments = appointmentServiceClient.getDoctorAppointmentsWithFilters(doctorId, status, patientName);
    model.addAttribute("appointments", appointments);
    return "doctorAppointment";
}

@PostMapping("/approve-appointment/{appointmentId}")
public String approveAppointment(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
    boolean success = appointmentServiceClient.approveAppointment(appointmentId);
    if (success) {
        redirectAttributes.addFlashAttribute("success", "Appointment approved successfully!");
    } else {
        redirectAttributes.addFlashAttribute("error", "Failed to approve appointment.");
    }
    return "redirect:/doctor/appointment";
}
```

## AWS Lambda Deployment

### 1. Build Lambda Package

```bash
mvn clean package
```

### 2. Create Lambda Function

```bash
aws lambda create-function \
    --function-name appointment-service \
    --runtime java21 \
    --role arn:aws:iam::YOUR_ACCOUNT:role/lambda-execution-role \
    --handler com.example.appointmentservice.lambda.AppointmentLambdaHandler \
    --zip-file fileb://target/appointment-service-1.0.0-aws.jar \
    --timeout 30 \
    --memory-size 1024
```

### 3. Configure Environment Variables

```bash
aws lambda update-function-configuration \
    --function-name appointment-service \
    --environment Variables='{"DB_URL":"jdbc:mysql://your-rds-endpoint:3306/appointment_db","DB_USERNAME":"your-username","DB_PASSWORD":"your-password"}'
```

## Performance Optimization

### Database Optimization

```sql
-- Enhanced indexes for new functionality
CREATE INDEX idx_users_role_name ON users(role, name);
CREATE INDEX idx_users_name_role ON users(name, role);
CREATE INDEX idx_appointments_doctor_date_time ON appointments(doctor_id, date, time);
CREATE INDEX idx_appointments_status_date ON appointments(status, date);
```

### Connection Pooling

```properties
# Enhanced HikariCP configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## Troubleshooting

### Common Issues

1. **Doctor Search Not Working**: Ensure users table exists and has sample data
2. **Appointment Conflicts**: Check date/time format and timezone settings
3. **CORS Issues**: Verify CORS configuration for frontend integration
4. **Performance Issues**: Check database indexes and connection pool settings

### Debug Commands

```bash
# Test doctor search
curl "http://localhost:8082/api/appointments/search-doctors?name=john"

# Test appointment validation
curl "http://localhost:8082/api/appointments/check-conflict?doctorId=1&date=2024-01-15&time=10:00"

# Check service health
curl "http://localhost:8082/api/appointments/health"
```

## Next Steps

1. **Enhanced Security**: Implement JWT authentication for all endpoints
2. **Real-time Notifications**: Add WebSocket support for real-time updates
3. **Advanced Search**: Implement Elasticsearch for complex search queries
4. **Caching**: Add Redis caching for frequently accessed doctor/patient data
5. **API Documentation**: Generate comprehensive Swagger documentation
6. **Load Testing**: Test with realistic appointment booking loads

## Support

For issues and questions:

1. Check the troubleshooting section
2. Review application logs with DEBUG level enabled
3. Test individual endpoints using the provided curl commands
4. Consult the enhanced README.md for API documentation
5. Contact the development team for complex integration issues
