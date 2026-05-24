# E-Commerce Microservices Platform

A scalable full-stack e-commerce platform built using modern microservice architecture.

The system consists of:

- Backend microservices built with Java Spring Boot
- Frontend application built with React
- Recommendation engine built with FastAPI
- API Gateway and Service Discovery
- Authentication and Authorization using Keycloak
- Dockerized infrastructure for local development and deployment

---

# Architecture

```text
React Frontend
       |
       v
API Gateway
       |
-------------------------------------------------
|            |            |          |           |
Product      Category     User       Search      Recommendation
Service      Service      Service    Service     Service (FastAPI)
       |
-------------------------------------------------
|            |            |          |
MongoDB      PostgreSQL   MinIO      Elasticsearch
