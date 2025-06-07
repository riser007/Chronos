# Chronos – Distributed Job Scheduler

## Overview
Chronos is a distributed job scheduling backend supporting one-time and recurring tasks, with automatic retries, failure handling and monitoring.

Core Features
Dynamic Scheduling: Submit jobs via API for immediate or future execution.
Recurring Jobs: Full CRON support for complex, recurring schedules.
Full Job Management: API to view, cancel, pause, and resume jobs.
Automatic Retries: Built-in failure handling with configurable retries.
Execution Logging: Complete audit trail of every job's execution history.
Scalable by Design: Runs in a clustered environment for high availability.
Technology Stack
Backend: Java 17, Spring Boot 3
Scheduling: Quartz Scheduler
Database: PostgreSQL
Security: Spring Security & JWT
API Docs: Springdoc OpenAPI (Swagger UI)
Getting Started
1. Prerequisites
Java 17+
PostgreSQL Server
API Client (Postman, curl)
2. Database Setup
Connect to your PostgreSQL instance and create a new database. The application will create the necessary tables on first run.
CREATE DATABASE job_scheduler_db;
Use code with caution.
SQL
3. Configuration
Edit src/main/resources/application.properties and update the database connection details:
spring.datasource.url=jdbc:postgresql://localhost:5432/job_scheduler_db
spring.datasource.username=your_postgres_user
spring.datasource.password=your_postgres_password
Use code with caution.
Properties
Important: Also change the jwt.secret for any production-like environment.
4. Build and Run
Use the Gradle wrapper to start the application.
./gradlew bootRun
Use code with caution.
Bash
The server will start on http://localhost:8080.
API Documentation & Usage
Full, interactive API documentation is available via Swagger UI once the application is running:
http://localhost:8080/swagger-ui.html
Use the Swagger UI or an API client to interact with the endpoints.
Example: Schedule a Job
After registering/logging in via /api/auth to get a token, you can schedule a job.
Request: POST /api/jobs
{
    "jobName": "weekly-report",
    "jobGroup": "reports",
    "cronExpression": "0 0 12 ? * MON",
    "jobType": "Email",
    "data": {
        "recipient": "admin@example.com",
        "subject": "Weekly System Report"
    }
}
Use code with caution.
Json
This schedules a job of type EmailJob to run every Monday at 12:00 PM. If cronExpression is omitted, the job runs once, immediately.
