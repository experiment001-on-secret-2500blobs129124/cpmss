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
| Build | ![Gradle](https://img.shields.io/badge/Gradle-8-02303A?logo=gradle) |

---

## Code Quality & Boilerplate

**Lombok** — annotation processor that generates getters, setters, constructors,
builders at compile time.

```java
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity extends BaseEntity {
    private String name;
    private String email;
}
```

**Java Records** — a Java 21 feature for defining DTOs. One line replaces an
entire class with fields, constructor, getters, equals, hashCode, toString.
Records define the *shape* of the DTO. MapStruct handles the *mapping* between
entities and these record DTOs.

```java
public record CreateEntityRequest(
    @NotBlank String name,
    @Email String email,
    @Min(18) int age
) {}
```

**MapStruct** — compile-time DTO mapper. Generates mapping code between entities
and DTOs.

```java
@Mapper(componentModel = "spring")
public interface EntityMapper {
    EntityResponse toResponse(Entity entity);
    Entity toEntity(CreateEntityRequest dto);
}
```

---

## Database Migrations

**Flyway** — SQL-based migration tool. Every schema change is a versioned SQL
file. Flyway runs on application startup and applies pending migrations in order.

```
src/main/resources/db/migration/
  V1__create_initial_tables.sql
  V2__add_constraints.sql
  R__seed_default_data.sql         ← repeatable: re-runs when file changes
```

The `R__` prefix is Flyway's convention for repeatable migrations — seed data
that evolves during development. Numbered migrations (`V1`, `V2`) are for
schema changes only. Additionally, a `CommandLineRunner` bean annotated with
`@Profile("dev")` can generate larger volumes of randomized demo data on
startup for development and showcasing.

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

Workflow:
1. Spring controllers define endpoints
2. springdoc-openapi generates OpenAPI spec at `/v3/api-docs`
3. Yaak imports spec (collection stays in sync)
4. Python script runs Yaak CLI against target environment

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
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }
}
```

**Spring Boot Test + MockMvc** — integration tests that spin up the Spring
context and hit controllers via HTTP.

**Testcontainers** — spins up a real PostgreSQL Docker container during tests.
Ensures Flyway migrations and queries work against the actual database engine.

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

---

## Data Seeding

See Flyway migration files above — `V3__seed_default_data.sql` contains
structural seed data (admin user, lookup values). For larger demo datasets,
a `CommandLineRunner` bean annotated with `@Profile("dev")` generates
randomized data on startup.

---

## Future: Caching

**Spring Data Redis** — `@Cacheable` annotation on service methods. Redis
runs as a separate Docker container.
