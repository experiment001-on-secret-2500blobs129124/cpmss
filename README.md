# CPMSS - Compound Management System for Staff

A staff management system built with Java + Spring Boot, featuring both
server-rendered views (Thymeleaf) and REST API endpoints for flexibility.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Web Views | Thymeleaf (server-rendered HTML) |
| API | REST (JSON endpoints) |
| Database | PostgreSQL |
| Build | Gradle |
| Containers | Docker + Docker Compose |
| CI/CD | GitHub Actions |

## Architecture

**Hybrid approach** - Both Thymeleaf views and REST API, sharing the same
service layer:

```
/staff      → @Controller     → Thymeleaf HTML (web UI)
/api/staff  → @RestController → JSON (for testing, future mobile)
```

This provides:
- ✅ "All Java" frontend (Thymeleaf meets project requirement)
- ✅ Flexibility to add Flutter/mobile later (REST API ready)
- ✅ API testing with Yaak without building UI first

## Project Status

🚧 **In Development**

- [ ] Project scaffolding
- [ ] Database schema
- [ ] Core entities
- [ ] REST API endpoints
- [ ] Thymeleaf views
- [ ] Authentication
- [ ] Testing
- [ ] Docker setup
- [ ] CI/CD pipeline

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | System design, patterns |
| [Implementation Plan](docs/IMPLEMENTATION_PLAN.md) | Phased roadmap |
| [Database](docs/DATABASE.md) | Schema, migrations |
| [Brain Dump](docs/Brain_Dump.md) | Planning notes |
| [Commit Convention](COMMIT_CONVENTION.md) | Git commit style |

## Quick Start

```bash
# Prerequisites: Java 21, Docker

# Start database
docker-compose up -d postgres

# Run application
./gradlew bootRun

# Run tests
./gradlew test
```

## License

MIT
