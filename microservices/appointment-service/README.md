# Appointment Service

A comprehensive microservice for managing appointments, doctors, and patients in the healthcare system.

## Features

- **Appointment Management**: Create, read, update, and delete appointments
- **Doctor Search**: Search for doctors by name with case-insensitive matching
- **Patient Search**: Search for patients by name with case-insensitive matching
- **Advanced Filtering**: Filter appointments by status and patient name
- **Appointment Validation**: Check for conflicts and validate booking requests
- **Status Management**: Approve, reject, and complete appointments
- **Patient Information**: Get appointments with patient names included

## API Endpoints

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
- `GET /api/appointments/doctor/{doctorId}/filtered` - Get doctor appointments with status and patient name filters
- `GET /api/appointments/doctor/{doctorId}/with-patient-names` - Get appointments with patient names included

### Doctor Search
- `GET /api/appointments/search-doctors?name={name}` - Search doctors by name (case-insensitive)
- `GET /api/appointments/doctors` - Get all doctors

### Patient Search
- `GET /api/appointments/search-patients?name={name}` - Search patients by name (case-insensitive)

### User Management
- `GET /api/appointments/user/{userId}` - Get user by ID

### Appointment Status Management
- `PUT /api/appointments/{appointmentId}/approve` - Approve an appointment
- `PUT /api/appointments/{appointmentId}/reject` - Reject an appointment
- `PUT /api/appointments/{appointmentId}/complete` - Complete an appointment
- `GET /api/appointments/confirmed` - Get all confirmed appointments

### Appointment Validation
- `GET /api/appointments/check-conflict` - Check for appointment time conflicts
  - Parameters: `doctorId`, `date`, `time`, `excludeAppointmentId` (optional)
- `POST /api/appointments/validate-booking` - Validate appointment booking
  - Parameters: `doctorId`, `date`, `time`

### Health Check
- `GET /api/appointments/health` - Service health check

## Data Models

### Appointment
- `id` (Long) - Unique identifier
- `userId` (String) - Patient ID
- `doctorId` (String) - Doctor ID
- `date` (LocalDate) - Appointment date
- `time` (LocalTime) - Appointment time
- `status` (String) - Appointment status (pending, confirmed, rejected, completed)
- `notes` (String) - Additional notes

### Users
- `id` (Long) - Unique identifier
- `email` (String) - User email (unique)
- `password` (String) - User password
- `role` (Role) - User role (DOCTOR, PATIENT, DIETICIAN, ADMIN)
- `name` (String) - User name

## Request/Response Examples

### Search Doctors
```http
GET /api/appointments/search-doctors?name=john
```
Response:
```json
[
  {
    "id": 1,
    "name": "Dr. John Smith",
    "email": "john.smith@hospital.com",
    "role": "DOCTOR"
  }
]
```

### Check Appointment Conflict
```http
GET /api/appointments/check-conflict?doctorId=1&date=2024-01-15&time=10:00
```
Response:
```json
{
  "hasConflict": false,
  "message": "Time slot is available"
}
```

### Approve Appointment
```http
PUT /api/appointments/123/approve
```
Response:
```json
{
  "success": true,
  "message": "Appointment approved successfully"
}
```

## Business Rules

### Appointment Creation
- Appointments must be scheduled for future dates
- No conflicting appointments allowed for the same doctor at the same time
- All required fields must be provided

### Appointment Updates
- Patients can only update their own pending appointments
- Date/time changes are subject to conflict detection
- Status changes are restricted based on current status

### Doctor Actions
- Doctors can only act on their own appointments
- Only pending appointments can be accepted/rejected
- Completed appointments cannot be modified

### Status Transitions
- PENDING → CONFIRMED (doctor accepts)
- PENDING → CANCELLED (doctor rejects or patient cancels)
- CONFIRMED → COMPLETED (doctor marks as done)
- CONFIRMED → CANCELLED (patient cancels)

## Database Schema

```sql
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    reason VARCHAR(500) NOT NULL,
    reject_reason VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    user_id VARCHAR(255) NOT NULL,
    doctor_id VARCHAR(255) NOT NULL,
    diagnosis_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_date (date),
    INDEX idx_status (status)
);
```

## Configuration

### Environment Variables
- `DB_URL`: Database connection URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `PORT`: Server port (default: 8080)

### Application Properties
See `src/main/resources/application.properties` for detailed configuration.

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- Spring Boot Validation
- MySQL Connector
- AWS Lambda Java Core
- Jackson for JSON processing

## Setup

1. **Database Configuration**: Update `application.properties` with your MySQL database details
2. **Dependencies**: Ensure all Maven dependencies are installed
3. **Run the application**: `mvn spring-boot:run`
4. **Access the service**: Available at `http://localhost:8082`

## Database Schema

The service uses two main tables:
- `appointments` - Stores appointment information
- `users` - Stores user information (doctors, patients, etc.)

## Error Handling

The service includes comprehensive error handling for:
- Invalid date/time formats
- Appointment conflicts
- Missing resources
- Validation errors

## CORS Configuration

The service is configured to accept cross-origin requests from any domain for development purposes.

## Integration with Frontend

### Patient Page Integration
- Use patient endpoints for appointment management
- Implement appointment creation forms
- Display appointment history and status
- Allow appointment updates and cancellations

### Doctor Appointment Page Integration
- Use doctor endpoints for appointment management
- Implement pending appointment approval interface
- Provide accept/reject functionality with reasons
- Allow diagnosis note updates

## Error Handling

The service returns appropriate HTTP status codes:
- `200 OK`: Successful operations
- `201 Created`: Successful appointment creation
- `400 Bad Request`: Validation errors or business rule violations
- `404 Not Found`: Appointment not found
- `500 Internal Server Error`: Unexpected errors

## Security Considerations

- User authorization checks for appointment access
- Input validation and sanitization
- SQL injection prevention through JPA
- CORS configuration for web integration

## Monitoring and Logging

- Structured logging with appropriate levels
- Health check endpoint for monitoring
- Database connection monitoring
- Performance metrics tracking