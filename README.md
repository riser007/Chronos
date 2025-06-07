# Chronos – Distributed Job Scheduler

## Overview
Chronos is a distributed job scheduling backend supporting one-time and recurring tasks, with automatic retries, failure handling and monitoring.

## Features
- One-Time & Recurring Jobs via REST API  
- Dynamic Scheduling using Spring’s TaskScheduler  
- Automatic Retries with Spring Retry  
- Failure Notifications on exceeded retries  
- Logging & Monitoring with SLF4J + Actuator  

## Getting Started
1. Clone the repository  
   ```
   git clone https://github.com/yourorg/chronos.git
   ```
2. Build and run  
   ```
   ./gradlew bootRun
   ```
3. API Endpoints:  
   - POST `/api/jobs/one-time`  
   - POST `/api/jobs/recurring`  
   - GET  `/api/jobs`  
   - POST `/api/jobs/{id}/cancel`  
