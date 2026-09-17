## 1. Summary
<!-- Tóm tắt ngắn gọn mục đích và nội dung thay đổi trong PR này -->

## 2. Related Requirement / Issue / Stage Gate
- **FR / NFR ID:** (ví dụ: `FR-06`, `NFR-08`, `NFR-10`)
- **Stage Gate:** (Gate 1 | Gate 2 | Gate 3 | Gate 4 | Gate 5 | Gate 6)
- **Issue Link:** 

## 3. Scope of Changes
### Frontend Changes
- [ ] UI Components / Styles
- [ ] Routing / Vercel SPA Configuration
- [ ] API Service & Context Integration
- [ ] None

### Backend Changes
- [ ] API Controller / Service Logic
- [ ] Security / JWT / Authorization Filter
- [ ] AI Prompt Strategies & Guardrails
- [ ] None

### Database Migration
- [ ] New Flyway Migration added (e.g. `V14__add_unique_constraint_enrollments.sql`)
- [ ] Schema remains unchanged

## 4. Security & Privacy Impact
- [ ] Header Spoofing prevented (Downstream validates JWT independently)
- [ ] RBAC `@PreAuthorize` verified for Admin routes
- [ ] User privacy isolated by owner (`userId` / `userEmail`)
- [ ] NO plaintext secrets, passwords, or API keys committed

## 5. Verification & Test Evidence
- **Maven Test Suite:** (e.g. `mvn clean test` - 65+ passed)
- **Frontend Build:** (e.g. `npm run build` - PASS)
- **JMeter / Performance Impact:** (e.g. CRUD response time < 2s)
- **Vercel Preview URL:** (nếu áp dụng cho frontend)
- **Screenshots / Logs / Artifacts:**

## 6. Pre-merge Checklist
- [ ] Code builds cleanly without compile errors.
- [ ] Unit & integration tests pass locally.
- [ ] .gitignore excludes all `.env`, `target/`, and `node_modules/`.
- [ ] Documentation updated in `docs/` and synced with source code.
- [ ] Rollback plan considered in `docs/ROLLBACK.md`.
