# AccessPulse - Project Summary

## 1. Project Overview & Architecture
* **Project Purpose:** AccessPulse is a comprehensive, role-based Complaint Management System designed to streamline the process of reporting, assigning, and resolving facility or infrastructure issues.
* **Backend Framework:** Developed using **Java** and **Spring Boot**, following the MVC (Model-View-Controller) architecture for clean separation of concerns.
* **Database & ORM:** Uses **Spring Data JPA (Hibernate)** to map Java entities (like Users, Complaints, and Notifications) to the database seamlessly.
* **Security:** Integrated **Spring Security** to handle secure authentication and implement strict Role-Based Access Control (RBAC).
* **Frontend:** Built using **Thymeleaf** templates with HTML, vanilla JavaScript, and CSS. 

## 2. Role-Based Modules
You have structured the application to support three distinct user roles, each with their own dedicated dashboards and permissions:

* **Student / Regular User:**
  * Can securely log in and report new complaints.
  * Can view a personalized dashboard tracking the real-time status of their active complaints.
  * Can provide ratings and feedback on resolved complaints.
* **Maintenance Staff:**
  * Has a dedicated portal to view tasks assigned specifically to them.
  * Can update the status of complaints (e.g., from "Assigned" to "In Progress" to "Resolved").
  * Can add resolution remarks detailing how the issue was fixed.
* **Administrator:**
  * Has global oversight of the system.
  * Can view all incoming complaints and assign them to the appropriate maintenance staff.
  * Can manage user accounts, locations, and view system-wide analytics.

## 3. Key Technical Features Implemented
* **Complete Complaint Lifecycle:** Engineered an end-to-end flow where a `Complaint` moves through various states. Implemented a `StatusHistory` tracker so every change is logged for accountability.
* **Notification System:** Built a notification module to alert users when the status of their complaint changes.
* **Feedback & Resolution Loop:** Integrated `Feedback` and `Resolution` entities to ensure quality control, allowing the loop to be closed only when the user is satisfied.
* **Premium UI/UX Design:** Completely overhauled the frontend to feature a modern, dynamic, and premium **Black and Crimson Red dark theme**. This involved:
  * Implementing global CSS variables for consistent theming across all pages.
  * Designing custom authentication pages, interactive cards, and glowing focus states to make the application feel highly professional and responsive.
  * Ensuring the application adapts smoothly across different screen sizes.

## 4. Frontend & Backend Separation
The project has been separated into dedicated directories under `accesspulse/`:
* **`frontend/`**: Contains all presentation assets and client code:
  * `css/`: Theme (`style.css`), dashboard styles (`dashboard.css`), and authentication styles (`auth.css`).
  * `js/`: Form logic (`complaint-form.js`), analytics visualizer (`dashboard-charts.js`), and interactions (`main.js`).
  * `templates/`: HTML templates organized into `admin/`, `student/`, `maintenance/`, `auth/`, `fragments/`, and `index.html`.
* **`backend/`**: Contains the complete Spring Boot Java application:
  * `src/`: Java source code (controllers, services, JPA entities, repositories, security, DTOs) and resources.
  * `pom.xml`, Maven wrappers (`mvnw`, `mvnw.cmd`), and uploaded files (`uploads/`).

