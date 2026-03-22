# Qualification Review System - DDac Group 18

## Overview
The Qualification Review System allows doctors and dieticians to submit their professional licenses for verification by administrators. Upon approval, the system automatically creates user accounts for the verified professionals.

## Features

### For Applicants (Doctors & Dieticians)
- **Professional Application Form**: Submit qualification requests with personal and license information
- **File Upload**: Upload license documents in PDF or image formats (PNG, JPG, JPEG)
- **Status Tracking**: Check application status using email address
- **Drag & Drop Interface**: Modern file upload with drag-and-drop functionality
- **Real-time Validation**: Client-side validation for file types and sizes

### For Administrators
- **Request Management**: View and manage all qualification requests
- **Document Review**: Download and review submitted license documents
- **Approval/Rejection**: Approve or reject requests with admin notes
- **Automatic Account Creation**: System automatically creates user accounts upon approval
- **Statistics Dashboard**: View pending, approved, and rejected request counts

## Technical Implementation

### Database Schema

#### QualificationRequest Entity
```sql
CREATE TABLE qualification_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_name VARCHAR(255) NOT NULL,
    applicant_email VARCHAR(255) NOT NULL UNIQUE,
    requested_role ENUM('DOCTOR', 'DIETICIAN') NOT NULL,
    license_number VARCHAR(255) NOT NULL,
    license_type VARCHAR(255) NOT NULL,
    s3_file_key VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_content_type VARCHAR(100) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_notes TEXT,
    submitted_at DATETIME NOT NULL,
    reviewed_at DATETIME,
    reviewed_by VARCHAR(255)
);
```

### File Storage
- **S3 Integration**: All license documents are stored in AWS S3
- **Organized Structure**: Files are organized by role (doctors/dieticians)
- **Secure Access**: Files are accessible only through admin download endpoints
- **Automatic Cleanup**: Files are deleted when requests are removed

### Security Features
- **File Type Validation**: Only PDF and image files are accepted
- **File Size Limits**: Maximum 10MB per file
- **Unique Email Validation**: Prevents duplicate applications
- **Admin-only Access**: Only administrators can review and manage requests
- **CSRF Protection**: All forms include CSRF protection

## API Endpoints

### Public Endpoints
- `GET /qualification/apply` - Display application form
- `POST /qualification/submit` - Submit qualification request
- `GET /qualification/status` - Check application status

### Admin Endpoints
- `GET /admin/qualification-requests` - View all requests
- `GET /admin/qualification-requests/{id}/download` - Download license file
- `POST /admin/qualification-requests/{id}/approve` - Approve request
- `POST /admin/qualification-requests/{id}/reject` - Reject request
- `POST /admin/qualification-requests/{id}/delete` - Delete request

## Configuration

### AWS S3 Configuration
Add the following to `application.properties`:
```properties
aws.s3.bucket-name=your-bucket-name
aws.s3.region=your-region
aws.access.key.id=your-access-key
aws.secret.access.key=your-secret-key
```

### Required Dependencies
The system uses the following AWS SDK dependency (already included in pom.xml):
```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-s3</artifactId>
    <version>1.12.647</version>
</dependency>
```

## User Workflow

### Application Process
1. **Access Application**: Navigate to `/qualification/apply`
2. **Fill Form**: Complete personal and license information
3. **Upload Document**: Upload license file (PDF/PNG/JPG)
4. **Submit**: System validates and stores the request
5. **Status Check**: Use email to check application status

### Admin Review Process
1. **View Requests**: Access `/admin/qualification-requests`
2. **Download Documents**: Review uploaded license files
3. **Make Decision**: Approve or reject with notes
4. **Automatic Account Creation**: Approved requests create user accounts
5. **Notification**: Applicants can check status for updates

## File Structure

```
src/main/java/com/example/DDac_group18/
├── model/
│   ├── data_schema/
│   │   └── QualificationRequest.java
│   └── repository/
│       └── QualificationRequestRepository.java
├── services/
│   ├── QualificationRequestService.java
│   └── S3Service.java
├── controllers/
│   ├── QualificationController.java
│   └── AdminController.java (updated)
└── resources/templates/
    ├── qualification-apply.html
    ├── qualification-status.html
    └── admin-qualification-requests.html
```

## Usage Examples

### Submitting an Application
```bash
# Navigate to application form
curl -X GET http://localhost:8080/qualification/apply

# Submit application (multipart form data)
curl -X POST http://localhost:8080/qualification/submit \
  -F "applicantName=Dr. John Doe" \
  -F "applicantEmail=john.doe@example.com" \
  -F "requestedRole=DOCTOR" \
  -F "licenseNumber=MD123456" \
  -F "licenseType=Medical License" \
  -F "licenseFile=@/path/to/license.pdf"
```

### Checking Application Status
```bash
# Check status by email
curl -X GET "http://localhost:8080/qualification/status?email=john.doe@example.com"
```

### Admin Operations
```bash
# View all requests
curl -X GET http://localhost:8080/admin/qualification-requests

# Download license file
curl -X GET http://localhost:8080/admin/qualification-requests/1/download

# Approve request
curl -X POST http://localhost:8080/admin/qualification-requests/1/approve \
  -F "adminNotes=License verified successfully"

# Reject request
curl -X POST http://localhost:8080/admin/qualification-requests/1/reject \
  -F "adminNotes=License expired, please provide current license"
```

## Error Handling

### Common Error Scenarios
- **Duplicate Email**: User already exists or has pending request
- **Invalid File Type**: Only PDF and image files accepted
- **File Too Large**: Maximum 10MB file size limit
- **Missing Fields**: Required form fields validation
- **S3 Upload Failure**: Network or configuration issues

### Error Messages
- "User with this email already exists"
- "A pending request already exists for this email"
- "Only PDF and image files are allowed"
- "File size must be less than 10MB"
- "Failed to upload file to S3"

## Security Considerations

### Data Protection
- License documents stored securely in S3
- Access controlled through admin authentication
- File downloads require admin privileges
- Automatic cleanup of rejected/deleted requests

### Input Validation
- Server-side validation of all inputs
- File type and size validation
- Email format validation
- SQL injection prevention through JPA

### Access Control
- Public access to application submission
- Admin-only access to review and management
- Role-based access control for different admin functions

## Future Enhancements

### Planned Features
- **Email Notifications**: Automatic email updates to applicants
- **Bulk Operations**: Process multiple requests simultaneously
- **Advanced Filtering**: Filter requests by status, role, date range
- **Document Preview**: In-browser document preview
- **Audit Logging**: Track all admin actions
- **API Rate Limiting**: Prevent abuse of public endpoints

### Integration Opportunities
- **Email Service**: Send approval/rejection notifications
- **SMS Notifications**: Mobile alerts for status updates
- **Document Verification**: Integration with license verification APIs
- **Background Processing**: Async processing for large file uploads

## Troubleshooting

### Common Issues
1. **S3 Upload Failures**: Check AWS credentials and bucket permissions
2. **File Download Issues**: Verify S3 bucket configuration
3. **Database Errors**: Ensure qualification_requests table exists
4. **Template Errors**: Check Thymeleaf template syntax

### Debug Information
- Enable debug logging in application.properties
- Check S3 bucket access logs
- Monitor database connection pool
- Review application logs for error details

## Support

For technical support or questions about the qualification review system, please refer to the main project documentation or contact the development team. 