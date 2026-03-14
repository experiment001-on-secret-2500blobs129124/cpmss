# Implementation Plan

> See **[ROADMAP.md](./ROADMAP.md)** for the full engineering plan with time estimates, Gantt chart, ERD, seed data, and CLI commands.

## Phases

### Phase 0: Setup (3–5 days)
- [ ] Spring Boot project via Spring Initializr
- [ ] Dependencies: Spring Web, Spring Data JPA, Thymeleaf, PostgreSQL, Flyway, Spring Security, Validation
- [ ] Docker Compose for local Postgres
- [ ] Flyway migration structure
- [ ] Base Thymeleaf layout template (glass UI from dashboard-demo.html)
- [ ] Spring Security skeleton (login page, role stubs)
- [ ] Verify: app starts, connects to DB

### Phase 1: Database (8–10 days)
- [ ] Core schema: Person, Building, Unit, Facility (`V1__create_schema.sql`)
- [ ] Contract + Financial schema: Contract, Installment, Payment, Bank Account
- [ ] Access Control: Permit, Gate, Entry Log
- [ ] HR + Org: Department, Position, Shift, Task, Application
- [ ] Vehicle + Company schema
- [ ] Seed data: Compound, Admin, Departments, Positions, Shifts, Roles (`V2__seed_default_data.sql`)
- [ ] Indexes (`V3__add_indexes.sql`)
- [ ] JPA Entities + Spring Data Repositories for all tables

### Phase 2: Core Services (3–4 weeks)
- [ ] PersonService + CRUD
- [ ] BuildingService + UnitService (with status/pricing history)
- [ ] ContractService (full lifecycle: Draft → Active → Terminated, with cascades via Spring Events)
- [ ] InstallmentService + PaymentService (polymorphic routing, chain of truth)
- [ ] WorkOrderService (lifecycle + vendor assignment)
- [ ] AccessPermitService + EntryLogService

### Phase 3: Web UI (~3 weeks for MVP screens)
- [ ] Person: List, Detail, Create/Edit
- [ ] Building + Unit: List, Detail, Create
- [ ] Contract: List, Detail, Create Wizard (multi-step)
- [ ] Finance: Installment List, Payment List, Record Payment form
- [ ] Work Orders: List, Detail, Create
- [ ] Access Control: Permit List, Gate List, Entry Log
- [ ] Dashboard (summary cards, charts, activity feed)

### Phase 4: Auth & Testing (1–2 weeks)
- [ ] Spring Security: Admin/Manager/Staff roles
- [ ] Login/logout pages
- [ ] Role-based access control on routes
- [ ] Unit tests for all services
- [ ] `@WebMvcTest` for critical controllers
- [ ] Integration tests for key flows (contract lifecycle, payment routing)

### Phase 5: Docker & CI (2–3 days)
- [ ] Dockerfile (multi-stage build)
- [ ] docker-compose.prod.yml
- [ ] GitHub Actions: build, test

---

## Progress

| Phase | Status |
|-------|--------|
| 0: Setup | ✅ Done |
| 1: Database | ✅ Done |
| 2: Core Services | ✅ Done |
| 3: Web UI | ✅ Done |
| 4: Auth & Testing | ⬜ Not Started |
| 5: Docker & CI | ⬜ Not Started |
