# Stack & Tooling

Every tool, library, and framework used in this project.

## Core Stack

| Layer | Technology |
|---|---|
| Language | ![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk) |
| Framework | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot) |
| ORM | ![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate) |
| Web Views | ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-optional-005F0F?logo=thymeleaf) |
| API | ![REST](https://img.shields.io/badge/REST-JSON-blue) |
| Database | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white) |
| File Storage | ![MinIO](https://img.shields.io/badge/MinIO-latest-C72E49?logo=minio&logoColor=white) |
| Build | ![Gradle](https://img.shields.io/badge/Gradle-8-02303A?logo=gradle) |

See also:

- [`NON_FUNCTIONAL_REQUIREMENTS.md`](./NON_FUNCTIONAL_REQUIREMENTS.md)
- [`TESTING.md`](./TESTING.md)
- [`ERRORS.md`](./ERRORS.md)
- [`LOGGING.md`](./LOGGING.md)

Tool roles:

- REST JSON is the primary API surface.
- Thymeleaf is the optional browser-facing view technology.
- PostgreSQL and Flyway own schema persistence and migration order.
- MinIO is the S3-compatible file storage service.
- Docker, Jenkins, Yaak, and Testcontainers support deployment, API checks,
  and environment-backed tests.

---

## Code Quality & Boilerplate

**Lombok** — annotation processor that generates getters, setters, constructors,
builders at compile time.

```java
// On JPA entities — @Data is forbidden (see Architecture docs)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity extends BaseEntity {
    private String name;
    private String email;
}

// @Data is fine on plain POJOs (non-entity classes)
@Data
public class SomePlainObject {
    private String name;
}
```

**Java Records** — a Java 21 feature for defining immutable DTOs and small
domain value types. One line replaces an entire class with fields,
constructor, getters, equals, hashCode, and toString. Records define the
*shape* of the DTO or value object. MapStruct handles the mapping between
entities and record DTOs where mappers are used.

```java
public record CreateEntityRequest(
    @NotBlank String name,
    @Email String email,
    @Min(18) int age
) {}
```

**MapStruct** — compile-time DTO mapper. Generates mapping code between entities
and DTOs.

When the target DTO is a Java Record (immutable, no setters), annotate the
mapper method with `@BeanMapping(builder = @Builder(disableBuilder = true))`.
This tells MapStruct to call the all-args constructor directly instead of using
setters.

```java
@Mapper(componentModel = "spring")
public interface EntityMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    EntityResponse toResponse(Entity entity);

    Entity toEntity(CreateEntityRequest dto);
}
```

---

## Database Migrations & Seeding

**Flyway** — SQL-based migration tool. Every schema change is a versioned SQL
file. Flyway runs on application startup and applies pending migrations in order.

```
src/main/resources/db/migration/
  V1__create_initial_tables.sql              ← DDL: all CREATE TABLE statements
  V2__add_constraints.sql                    ← Deferred CHECKs for V1 tables
  V3__add_auth_tables.sql                    ← App_User table (authentication)
  V4__add_auth_constraints.sql               ← App_User CHECK constraints
  V5__add_team_name.sql                      ← Adds team_name to Person_Supervision
  V6__add_internal_report.sql                ← Internal_Report table
  V7__add_internal_report_constraints.sql    ← Deferred CHECKs for V6
  V8__add_application_cv_storage.sql         ← Application current-CV metadata
  V9__add_application_cv_constraints.sql     ← Deferred CHECKs for V8
```

Numbered migrations run once, in order.
After the schema is shared or released, add a new migration instead of editing
an applied one. Pre-release baseline edits require a database reset.
`V3`/`V4` add the auth tables and constraints. `V6`/`V7` add the internal
reporting table and constraints. `V8`/`V9` add current application-CV
metadata and constraints. The `R__` prefix is Flyway's repeatable
migration convention for dev-only seed data.

A `CommandLineRunner` bean annotated with `@Profile("dev")` can run dev-only
startup automation such as bulk demo data generation, MinIO bucket setup, and
test user creation.

See [DATABASE.md](./DATABASE.md) for the full migration convention and rationale.

---

## API Documentation

**springdoc-openapi** — auto-generates Swagger UI from Spring controllers and DTOs:

- `/swagger-ui.html` — interactive UI
- `/v3/api-docs` — raw OpenAPI JSON spec (importable into Yaak)

---

## API Testing

**Yaak** — API client for manual testing and collection management.

**Yaak CLI + Python script** — automated collection runner for CI and
environment verification.

Runner shape:

- Location: `tests/api/`
- Runtime: Python 3.14, `venv`-managed
- Dependencies: `tests/api/requirements.txt`
- Test cases assert specific responses — not just status codes
- Local run:
  ```sh
  python3 -m venv .venv
  source .venv/bin/activate
  pip install -r tests/api/requirements.txt
  python3 tests/api/run_tests.py
  ```

Workflow:
1. Spring controllers define endpoints
2. springdoc-openapi generates OpenAPI spec at `/v3/api-docs`
3. Yaak imports spec (collection stays in sync)
4. `run_tests.py` runs Yaak CLI against target environment and asserts responses

---

## Testing

**JUnit 5** — test framework (ships with Spring Boot).

**Mockito** — mocking library for unit tests.

```java
@ExtendWith(MockitoExtension.class)
class SomeServiceTest {
    @Mock SomeRepository repository;
    @Mock SomeRules rules;
    @InjectMocks SomeService service;

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.getById(id));
    }
}
```

**Spring Boot Test + MockMvc** — integration tests that spin up the Spring
context and hit controllers via HTTP.

**Testcontainers** — spins up a real PostgreSQL Docker container during tests.
Ensures Flyway migrations and queries work against the actual database engine.

See [`TESTING.md`](./TESTING.md) for the testing policy and layer definitions.

---

## Security

**Spring Security** — filter chain handling JWT parsing, role extraction, CORS,
CSRF, endpoint access rules.

**JWT** — stateless authentication. Token issued on login, sent with every
request via `Authorization: Bearer` header.

---

## Logging

**SLF4J / Logback** — SLF4J is the facade API that your code imports. Logback
is the engine behind it. They always work together. You write `log.info(...)`
using SLF4J, Logback handles the output.

See [`LOGGING.md`](./LOGGING.md) for project logging rules and
[`ERRORS.md`](./ERRORS.md) for exception response behavior.

## Caching

**Spring Data Redis** — `@Cacheable` annotation on service methods. Redis
runs as a separate Docker container.
