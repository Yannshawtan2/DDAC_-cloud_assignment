# Microservices Directory

This directory contains all microservice implementations for the DDac healthcare system.

## Directory Structure

```
microservices/
├── README.md                     # This file
├── template-service/             # Template for creating new microservices
├── user-service/                 # ✅ User authentication and management
├── patient-service/              # 🚧 Patient data and appointments
├── doctor-service/               # 🚧 Doctor profiles and schedules
├── dietician-service/            # 🚧 Diet plans and nutrition
├── quiz-service/                 # 🚧 Health assessments and quizzes
├── notification-service/         # 🚧 Email, SMS, and alerts
├── file-service/                 # 🚧 Document upload and S3 management
└── admin-service/                # 🚧 Admin panel and qualification requests
```

## Status Legend
- ✅ Completed and deployed
- 🚧 Planned for migration
- ❌ Not yet started

## Getting Started

### For New Microservice Development

1. **Copy the template:**
   ```bash
   cp -r template-service your-new-service
   cd your-new-service
   ```

2. **Follow the setup guide:**
   - Read `SETUP.md` in the template directory
   - Update placeholders in all files
   - Configure database settings
   - Test locally before deployment

3. **Deploy to AWS:**
   - Build: `mvn clean package`
   - Deploy using AWS CLI or console
   - Update frontend configuration

### Service Dependencies

```
User Service (✅ Deployed)
├── Patient Service (depends on User)
├── Doctor Service (depends on User)
├── Dietician Service (depends on User)
└── Admin Service (depends on User)

Quiz Service
├── Patient Service (for responses)
└── User Service (for identification)

Notification Service
├── All Services (for notifications)
```

## Migration Order Recommendation

1. **User Service** ✅ (Completed)
2. **Patient Service** (Core entity, many dependencies)
3. **Doctor Service** (Core entity, treatment plans)
4. **Dietician Service** (Diet plans, nutritional advice)
5. **Quiz Service** (Health assessments)
6. **Admin Service** (User management)
7. **Notification Service** (Cross-cutting concern)
8. **File Service** (S3 uploads, documents)

## Best Practices

- Each microservice has its own database
- Use feature flags for gradual rollout
- Maintain backward compatibility
- Test thoroughly before deployment
- Document API endpoints
- Monitor service health

## Team Collaboration

- Create a new branch for each microservice
- Use pull requests for code review
- Update this README when adding services
- Follow the naming convention: `{service-name}-service`

## Configuration

All microservices share common configuration patterns:
- Database: RDS MySQL with service-specific schemas
- Security: BCrypt for password encryption
- API: RESTful endpoints with JSON
- Deployment: AWS Lambda + API Gateway
- Monitoring: CloudWatch logs and metrics

## Contact

For questions about microservice development, contact the development team or refer to the migration guide in `MICROSERVICE_MIGRATION_TEMPLATE.md`.
