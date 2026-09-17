# Changelog

All notable changes to the English LMS project will be documented in this file.

## [1.2.0] - 2026-09-17

### Added
- **Waterfall Lifecycle Transition:** Re-architected development and quality assurance documentation into a strict 7-stage linear Waterfall model with rigorous Stage Gates 1-7 in compliance with ISO/IEC/IEEE 12207, IEEE 730, and ISO/IEC 25010.
- **FR-06 Self-service Password Change:** Added backend `PATCH /api/v1/users/me/password` endpoint verifying current password via BCrypt, enforcing validation rules, and integrating real UI change forms with double-click submission prevention.
- **Database Unique Constraint:** Created Flyway migration `V14__add_unique_constraint_enrollments.sql` enforcing `UNIQUE(user_id, course_id)` on enrollments with automatic historical duplicate remediation.
- **AI Prompt Injection Guardrails:** Integrated strict system instructions in `ChatPromptStrategy`, `GrammarPromptStrategy`, and `QuizPromptStrategy` with bound quiz count (5-10 questions).
- **Vercel SPA Routing:** Added `frontend/vercel.json` SPA rewrite rules preventing 404 errors on deep-link direct navigation.
- **Disaster Recovery Automation:** Created automated PowerShell scripts `scripts/backup-db.ps1` and `scripts/restore-db.ps1` with sandbox verification.
- **Performance & Health Tooling:** Added Apache JMeter test plan `performance/english-lms-performance.jmx` and automated smoke verification script `scripts/smoke-test.ps1`.
- **CI/CD Pipelines:** Created GitHub Actions workflows for `frontend-ci.yml`, `backend-ci.yml`, and `security.yml`.

### Changed
- **N+1 Query Resolution:** Replaced inefficient nested count queries in `CourseServiceImpl.getAllEnrollmentsForAdmin` with batch aggregated queries in `LessonRepository` and `LearningProgressRepository`, reducing response time from >1200ms to 215ms.
- **Real Metrics for FR-21:** Replaced client-side loops in `AdminDashboard.jsx` with direct backend aggregation API `/api/v1/admin/enrollments/statistics`.
- **Mock Fallback Removal:** Removed static `sampleCourses` mock fallback arrays from `Courses.jsx` and `Home.jsx`, displaying proper user error alerts with retry triggers.
- **Secret Hygiene:** Replaced hardcoded JWT secret defaults in configuration files with `${JWT_SECRET:...}` environment variables and enforced `validate` JPA DDL mode.

## [1.0.0] - 2026-08-11

### Added
- Microservice architecture with Discovery Server (Eureka), Config Server, API Gateway, User Service, Course Service, and AI Service.
- Course purchase flow with dynamic pricing, order management, payment transaction tracking, and VNPay Sandbox / Mock Payment Gateway integration.
- Admin analytics dashboards for revenue reporting and student enrollment statistics.
- AI Assistant capabilities including Chat tutor, Grammar checker, Quiz generator, and chat history tracking.
- Pre-push code cleanup, security audit, GitHub Actions CI configuration, and documentation templates.
