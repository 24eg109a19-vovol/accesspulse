# AccessPulse - Backend Architecture & Services

![AccessPulse Backend Banner](https://img.shields.io/badge/AccessPulse-Backend%20Module-e63946?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-RBAC-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker&logoColor=white)

This repository contains the core **Backend Service & Business Logic Layer** for **AccessPulse**, an enterprise Role-Based Complaint & Accessibility Management System.

---

## 🏛️ Architecture & Tech Stack

- **Framework:** Java 21 & Spring Boot 4 (MVC Architecture)
- **Security:** Spring Security with BCrypt password hashing & strict Role-Based Access Control (RBAC):
  - `ROLE_ADMIN`: Global oversight, triage, staff assignments, campus location registry, user management.
  - `ROLE_MAINTENANCE`: Work order execution, resolution remarks, parts management, stage transitions.
  - `ROLE_STUDENT` / `ROLE_STAFF`: Self-service complaint logging, photo attachments, verification & feedback rating loop.
- **ORM & Database:** Spring Data JPA (Hibernate) with support for:
  - **Production:** Hosted MySQL 8
  - **Zero-Config Testing / Cloud Free-tier:** Embedded H2 in-memory (`SPRING_PROFILES_ACTIVE=h2`)
- **Algorithmic Priority Triage Engine:** Automatically computes a dynamic numerical priority score (1–25) using:
  $$\text{Priority Score} = (\text{Impact} \times 0.35) + (\text{Safety Risk} \times 0.30) + (\text{People Affected} \times 0.20) + (\text{Location Importance} \times 0.15)$$

---

## 📁 Repository Structure

```
accesspulse-backend/
├── src/
│   ├── main/
│   │   ├── java/com/accesspulse/
│   │   │   ├── config/              # SecurityConfig, WebConfig
│   │   │   ├── controller/          # Admin, Auth, Complaint, Maintenance, Dashboard
│   │   │   ├── dto/                 # ComplaintRequest, RegistrationRequest, PriorityUpdateRequest
│   │   │   ├── entity/              # User, Complaint, Location, Assignment, Resolution, StatusHistory
│   │   │   ├── enums/               # Role, ComplaintStatus, PriorityLevel, ComplaintCategory
│   │   │   ├── repository/          # JPA repositories
│   │   │   ├── security/            # UserDetailsService, AuthSuccessHandler
│   │   │   └── service/             # ComplaintService, PriorityService, UserService, FileStorage
│   │   └── resources/
│   │       ├── application.properties      # MySQL & Cloud Environment overrides
│   │       └── application-h2.properties   # In-Memory H2 zero-config testing profile
│   └── test/                        # Unit and integration test suites
├── pom.xml                          # Maven build & dependency definitions
├── Dockerfile                       # Multi-stage production container build (Java 21 JRE)
├── docker-compose.yml               # Local multi-container stack (App + MySQL 8)
├── render.yaml                      # 1-click cloud deployment blueprint
└── DEPLOYMENT_GUIDE.md              # Cloud deployment manual (Render & Railway)
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 installed
- Maven (or use included `./mvnw`)
- MySQL 8 (optional if using H2)

### Running Locally with In-Memory H2
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```
Access the application at `http://localhost:8080`.

### Running with Docker Compose (Full Stack)
```bash
docker compose up --build
```

---

## 🔑 Default Seeded Accounts
On first launch, the database automatically initializes the campus blocks (A through I, Facilities) and demo accounts:

| Role | Email | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin@accesspulse.edu` | `admin123` |
| **Maintenance Staff** | `maint@accesspulse.edu` | `maint123` |
| **Student** | Self-register at `/register` | *(Chosen password)* |

---

## 🔗 Related Repositories
- **Frontend Presentation Repository:** [AccessPulse Frontend](https://github.com/24eg109a19-vovol/accesspulse-frontend)
