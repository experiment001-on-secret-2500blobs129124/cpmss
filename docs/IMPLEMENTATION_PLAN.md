# Implementation Plan

## Phases

### Phase 0: Setup
- [ ] Spring Boot project (Spring Initializr)
- [ ] Dependencies: Spring Web, Spring Data JPA, Thymeleaf, PostgreSQL, Flyway, Spring Security, Validation
- [ ] Docker Compose for local Postgres
- [ ] Flyway migration structure
- [ ] Base Thymeleaf layout template
- [ ] Verify: app starts, connects to DB

### Phase 1: Database
- [ ] Design schema based on requirements
- [ ] Write Flyway migrations
- [ ] Create JPA entities

### Phase 2: Core Features
- [ ] Service layer for each feature
- [ ] REST API controllers (`@RestController`)
- [ ] Test API with Yaak
- [ ] Thymeleaf web controllers (`@Controller`)
- [ ] Views and forms

### Phase 3: Auth
- [ ] Spring Security setup
- [ ] Login/logout pages (Thymeleaf)
- [ ] JWT or session-based auth for API
- [ ] Role-based access

### Phase 4: Testing
- [ ] Unit tests for services
- [ ] `@WebMvcTest` for controllers
- [ ] Integration tests for key flows

### Phase 5: Docker & CI
- [ ] Dockerfile
- [ ] GitHub Actions (build, test)

---

## Progress

| Phase | Status |
|-------|--------|
| 0: Setup | ⬜ Not Started |
| 1: Database | ⬜ Not Started |
| 2: Core Features | ⬜ Not Started |
| 3: Auth | ⬜ Not Started |
| 4: Testing | ⬜ Not Started |
| 5: Docker & CI | ⬜ Not Started |
