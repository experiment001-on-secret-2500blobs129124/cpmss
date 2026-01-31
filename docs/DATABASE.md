# Database

## Overview

| Aspect | Choice |
|--------|--------|
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| ORM | Spring Data JPA (Hibernate) |
| Naming | snake_case (SQL), camelCase (Java) |

---

## Migrations

Location: `src/main/resources/db/migration/`

Naming: `V{version}__{description}.sql`

**Rule:** Never edit a migration after it's applied. Create a new one.

---

## Schema

> **TODO:** Add ERD and table definitions based on requirements.

---

## Notes

- Using UUID for primary keys (non-guessable, distributed-safe)
- Add indexes for columns used in WHERE/JOIN
