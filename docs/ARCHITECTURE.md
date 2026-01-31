# Architecture

## Overview

**Hybrid approach:** Spring Boot backend serving both Thymeleaf views (HTML)
and REST API (JSON). Same service layer, two types of controllers.

```
┌─────────────────────────────────────────────────────────────┐
│  @Controller (Web)      │  @RestController (API)           │
│  Returns: HTML views    │  Returns: JSON                   │
├─────────────────────────┴───────────────────────────────────┤
│                    Service Layer                            │
│                  (business logic)                           │
├─────────────────────────────────────────────────────────────┤
│                   Repository Layer                          │
│                  (database access)                          │
└─────────────────────────────────────────────────────────────┘
```

**Why hybrid?**
- Thymeleaf meets "all Java" requirement
- REST API allows testing with Yaak, future mobile/Flutter apps
- No code duplication (shared services)

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.x |
| Web Views | Thymeleaf |
| API | REST (JSON) |
| ORM | Spring Data JPA (Hibernate) |
| Database | PostgreSQL 16 |
| Auth | Spring Security |
| Build | Gradle |
| Containers | Docker Compose |

---

## Controller Patterns

### Web Controller (Thymeleaf)

```java
@Controller
@RequestMapping("/staff")
public class StaffWebController {
    private final StaffService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staff", service.findAll());
        return "staff/list";  // → templates/staff/list.html
    }
}
```

### API Controller (REST)

```java
@RestController
@RequestMapping("/api/staff")
public class StaffApiController {
    private final StaffService service;

    @GetMapping
    public List<StaffDto> list() {
        return service.findAll();  // → JSON
    }
}
```

### Shared Service

```java
@Service
public class StaffService {
    private final StaffRepository repository;

    public List<Staff> findAll() {
        return repository.findAll();
    }
}
```

---

## URL Routes

| Type | URL | Returns |
|------|-----|---------|
| Web | `/staff` | HTML page |
| Web | `/staff/{id}` | HTML page |
| API | `/api/staff` | JSON array |
| API | `/api/staff/{id}` | JSON object |

---

## Folder Structure

```
src/main/
├── java/com/cpmss/
│   ├── CpmssApplication.java
│   ├── config/               # Security, CORS, etc.
│   └── [feature]/            # Feature modules
│       ├── FeatureEntity.java
│       ├── FeatureRepository.java
│       ├── FeatureService.java
│       ├── FeatureWebController.java   # @Controller
│       ├── FeatureApiController.java   # @RestController
│       └── dto/
│           └── FeatureDto.java
│
└── resources/
    ├── application.yml
    ├── templates/            # Thymeleaf views
    ├── static/               # CSS, JS, images
    └── db/migration/         # Flyway SQL
```

> **TODO:** Define actual features based on requirements.

---

## Testing

| Layer | Annotation | Purpose |
|-------|------------|---------|
| Web Controller | `@WebMvcTest` | View rendering, forms |
| API Controller | `@WebMvcTest` | JSON responses |
| Service | JUnit + Mockito | Business logic |
| Repository | `@DataJpaTest` | Custom queries |
| Full flow | `@SpringBootTest` | Integration |
