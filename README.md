NutriDoc Cloud-Native (Microservices Migration)
Focus: Modernization using AWS Lambda, API Gateway, and S3 for high scalability.

☁️ NutriDoc: Modernized Microservices Platform
This project showcases the architectural migration of the NutriDoc consultation tool from a monolith to a decoupled, serverless microservices framework on AWS. The goal was to eliminate idle server costs and allow independent scaling of consultation services.

🏗️ Modernized Architecture
Entry Point: Amazon API Gateway for secure request routing and throttling.

Serverless Compute: Core business logic refactored into AWS Lambda functions (Pay-per-use model).

Hybrid Hosting: AWS Elastic Beanstalk retained for complex, long-running administrative dashboards.

Object Storage: Amazon S3 integrated for high-durability storage of medical imaging and patient files.

Database: Optimized Amazon RDS instances serving decoupled service requests.

🚀 Migration Benefits
Scalability: The image upload service (S3/Lambda) now scales independently of the appointment booking service.

Cost Efficiency: Significant reduction in operational overhead by transitioning to event-driven execution.

Fault Tolerance: Failure in the reporting service no longer affects the availability of the core consultation API.
