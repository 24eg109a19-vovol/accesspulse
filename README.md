# pus Accessibility Barrier Reporting and Re AccessPulse: Cam solution System
### (AccessPulse: Campus Accessibility Barrier Reporting and Resolution System)

**AccessPulse** is an enterprise full-stack Spring Boot web application designed for universities, colleges, and educational campuses to systematically detect, triage, dispatch, and resolve physical accessibility barriers (e.g. broken elevators, blocked wheelchair ramps, damaged tactile paving, and inaccessible restrooms).

---

## 🌟 Key Application Features

1. **Role-Based Access Control (RBAC)**:
   - **Student / Staff / Faculty**: Report barriers, upload photos, track live status progression, verify completed repairs, and submit 1–5 star quality feedback.
   - **Administrator**: Moderate all campus complaints, calibrate priority parameters, dispatch work orders to maintenance engineers, oversee campus locations, manage user accounts, and view graphical analytics.
   - **Maintenance Staff**: Access priority-ordered work order queue, update active repair status (`IN_PROGRESS`), and upload resolution completion notes and proof photographs.
2. **Dynamic Algorithmic Priority Matrix**:
   - Automated priority calculation based on:
     $$\text{Priority Score} = \text{Impact (1-5)} + \text{People Affected (1-5)} + \text{Safety Risk (1-5)} + \text{Location Footfall (1-5)} + \text{Days Unresolved}$$
   - **Priority Classification**:
     - `0 – 7`: **LOW**
     - `8 – 14`: **MEDIUM**
     - `15 – 20`: **HIGH**
     - `21+`: **CRITICAL** (with visual pulse alerts)
3. **Full Lifecycle Status Machine**:
   $$\text{SUBMITTED} \rightarrow \text{UNDER\_REVIEW} \rightarrow \text{VERIFIED} \rightarrow \text{ASSIGNED} \rightarrow \text{IN\_PROGRESS} \rightarrow \text{RESOLVED} \rightarrow \text{CLOSED}$$
4. **Interactive UI / UX**:
   - Built with Thymeleaf, Vanilla CSS, FontAwesome 6, and Chart.js.
   - Real-time client-side priority slider preview, drag-and-drop photo upload preview, unread notifications badge, and responsive mobile-first navigation.
5. **Auto-Seeded Out-of-the-Box Demo Data**:
   - System automatically seeds sample demo accounts and campus locations on first boot.

---

## 🔑 Pre-Seeded Demo Login Credentials

You can use the 1-click login buttons on the login page or enter:

| Role | Email Address | Password | Functionality |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@accesspulse.edu` | `admin123` | Full admin control, assignments, locations & analytics |
| **Maintenance Lead** | `maint@accesspulse.edu` | `maint123` | Assigned repair orders, progress updates & proof upload |
| **Elevator Tech** | `elena.maint@accesspulse.edu` | `maint123` | Specialized elevator & mechanical tasks |
| **Student** | `student@accesspulse.edu` | `student123` | Barrier submission, live tracking & verification |
| **Faculty / Staff** | `staff@accesspulse.edu` | `staff123` | Campus barrier reporting & department tracking |

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.4+, Spring MVC, Spring Security 6, Spring Data JPA, Hibernate, Lombok
- **Frontend**: HTML5, Vanilla Modern CSS, JavaScript (ES6+), Thymeleaf, Thymeleaf Extras Spring Security 6, FontAwesome 6, Chart.js
- **Database**: MySQL 8.0+ (with optional H2 in-memory profile for instant zero-config testing)
- **Build Tool**: Maven

---

## 🚀 Step-by-Step Setup Guide in Visual Studio Code

### Step 1: Clone or Open Project in VS Code
Open the project directory in VS Code:
```bash
cd "accesspulse"
code .
```

### Step 2: Database Setup (MySQL)
Open MySQL Workbench or MySQL CLI and create the database:
```sql
CREATE DATABASE accesspulse_db;
```
Check `src/main/resources/application.properties` to ensure the password matches your MySQL root password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/accesspulse_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

> [!TIP]
> **Instant Zero-Config Testing with H2 (No MySQL Needed)**:
> If you don't have MySQL running, you can run immediately with the pre-configured H2 profile:
> ```bash
> ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
> ```

### Step 3: Run the Application
In your VS Code terminal (PowerShell or Bash), run:
```bash
./mvnw spring-boot:run
```
*(Or use `mvn spring-boot:run` if Maven is in your system PATH).*

### Step 4: Open in Web Browser
Navigate to:
👉 **`http://localhost:8080`**

---

## 🌐 Deployment Guide (Render & Netlify)

### Backend & MySQL on Render
1. Push your repository to GitHub.
2. Go to [Render.com](https://render.com) and create a **New MySQL Database** instance.
3. Create a **New Web Service** connected to your GitHub repository.
4. Set Build & Start commands:
   - **Environment**: Java / Docker
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/accesspulse-0.0.1-SNAPSHOT.jar`
5. Set Environment Variables in Render:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=true`
   - `SPRING_DATASOURCE_USERNAME`: `${MYSQL_USER}`
   - `SPRING_DATASOURCE_PASSWORD`: `${MYSQL_PASSWORD}`

### Optional Static Landing on Netlify
If you want a lightweight static landing page or documentation portal hosted on Netlify, place static assets in a `public/` directory and configure standard Netlify static site publishing.

---

## 📄 License & Academic Citation
Developed for institutional accessibility compliance, barrier-free educational infrastructure, and rapid facilities maintenance management.
