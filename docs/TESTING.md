# Testing Guide

This document defines how CPMSS tests should be organized, what each test layer
proves, and how test code documents required behavior.

## Test Stack

- JUnit Platform is enabled.
- Mockito is available for tests that need collaborators.
- Spring Boot Test, MockMvc, Spring Security Test, and Testcontainers are the
  standard tools for integration, API, security, and persistence tests.
- Yaak CLI collections are the API contract runner for environment-level
  request/response checks.

## Test Documentation Standard

Tests are documentation. A maintainer must be able to open a test file and
understand the business behavior being protected.

Required for every non-trivial test class:

- class-level Javadoc describing the domain behavior under test,
- `@DisplayName` on test classes when it improves failure output,
- test method names or `@DisplayName` values that read like behavior,
- helper methods named after business setup, not implementation mechanics,
- comments only where the business setup is not obvious.

Example:

```java
/**
 * Verifies that {@link Money} preserves the currency invariant required by
 * finance workflows.
 *
 * <p>Payments, installments, payroll, and work orders must not silently mix
 * currencies during arithmetic.
 */
@DisplayName("Money value")
class MoneyTest {

    @Test
    @DisplayName("rejects adding different currencies")
    void rejectsDifferentCurrencyAddition() {
        Money egp = Money.of("10.00", "EGP");
        Money usd = Money.of("10.00", "USD");

        assertThrows(BusinessException.class, () -> egp.add(usd));
    }
}
```

Do not add empty comments such as “calls the service.” Do add a short comment
when the setup encodes a rule, for example “primary signer already exists” or
“guard is assigned to another gate.”

## Test Type Matrix

| Test type | Purpose | Typical tool |
|-----------|---------|--------------|
| Compile check | Java source still compiles | `./gradlew compileJava` |
| Value object unit test | Pure domain validation/normalization | JUnit |
| Enum/converter test | Database labels stay stable | JUnit |
| Rules unit test | Pure business rule decisions | JUnit |
| Mapper test | DTO/entity mapping stays stable | JUnit / MapStruct generated mapper |
| Service unit test | Service orchestration with mocked dependencies | JUnit + Mockito |
| Service integration test | Transactional workflow across real Spring beans | Spring Boot Test |
| Repository/JPA test | Mapping, query, composite key, and constraint behavior | `@DataJpaTest` + Testcontainers |
| Controller slice test | Controller validation, status, envelope, and security binding | MockMvc |
| API/contract test | Running API behavior against an environment | Yaak CLI runner |
| Security test | Authentication, role rules, and ownership denials | Spring Security test + MockMvc |
| Error handling test | HTTP status, code, fields, requestId, and response shape | MockMvc |
| Logging/observability test | Request ID propagation and MDC cleanup | MockMvc / filter tests |
| File storage test | MinIO object key and metadata behavior | MinIO client / Testcontainers |
| Migration/schema test | Flyway migrations apply cleanly to PostgreSQL | Testcontainers |
| DevOps smoke test | App starts with expected environment wiring | CI pipeline |

## Value Object Tests

Use these for domain primitives such as `Money`, `KpiScore`, `EmailAddress`,
`LicensePlate`, periods, windows, rates, and reference numbers.

Cover:

- valid construction,
- invalid construction,
- normalization,
- equality,
- arithmetic or comparison behavior where relevant,
- JSON shape if the value object appears in request/response DTOs,
- converter round-trips when database labels must remain stable.

## Enum And Converter Tests

Use these when a Java enum or value object maps to an existing database label.

Cover:

- every allowed label converts to the correct Java value,
- every Java value converts back to the exact database label,
- blank/null behavior is explicit,
- unknown labels throw the expected business exception or persistence failure.

These tests protect Flyway/JPA compatibility. Add them whenever a string
database column becomes an enum or value object.

## Rules Tests

Use these for pure business-rule classes and repository-backed policy checks.

Cover:

- allowed action,
- denied action,
- edge case,
- expected exception type,
- expected error code when the rule maps to a stable API error,
- field-level business errors when the rule reports multiple problems.

Add rules tests before workflow or ownership logic changes rely on those rules.

## Mapper Tests

Use these when MapStruct mapping contains value-object conversion, flattened
fields, custom expressions, or compatibility getters.

Cover:

- create request to entity,
- entity to response,
- update request behavior when the mapper owns it,
- primitive DTO shape preserved when entities use value objects internally.

Do not test trivial generated mapping unless the mapping protects a public API
contract or a previous bug.

## Service Unit Tests

Use these when service orchestration can be checked with mocked repositories or
collaborators.

Cover:

- right rule method is called with loaded domain data,
- missing dependency raises `ResourceNotFoundException`,
- ownership denial happens before mutation,
- duplicate/conflict path,
- service returns stable DTO shape.

Service unit tests should not pretend to prove database constraints,
transaction rollback, or JPA mapping behavior.

## Service Integration Tests

Use these for workflows that span tables or aggregates.

Cover:

- transaction boundary behavior,
- orchestration order,
- cross-aggregate checks,
- idempotency/duplicate behavior when required,
- frozen snapshot behavior for payroll, KPI, and financial records,
- actual repository queries used by the workflow.

Use real Spring beans and a real PostgreSQL database when the behavior depends
on transactions, constraints, or persistence.

## Selective CQRS Tests

When a bounded context uses selective CQRS, command and query services have
separate test responsibilities.

| Surface | Required proof |
|---------|----------------|
| Command service | authorization, ownership, rules, transactions, orchestration, idempotency, snapshot/freeze behavior, mutation response shape |
| Query service | read-only scope, filters, sorting, pagination, dashboard/read-model shape, aggregate calculations, stable DTO fields |
| Shared mapper/read model | value conversion, masked fields, field names, backward-compatible JSON shape |

Command-service tests are the home for write behavior and business invariants.
Query-service tests are the home for read behavior and projection correctness.

## Repository And JPA Tests

Use these for persistence behavior.

Cover:

- table and column mappings,
- composite keys and shared primary keys,
- join tables and element collections,
- custom repository queries,
- uniqueness constraints,
- Flyway check constraints,
- enum/value-object converters.

Prefer Testcontainers PostgreSQL for these tests so Flyway and Hibernate run
against the same database engine used by the application.

## Controller And API Tests

Controller slice tests use MockMvc against Spring MVC. API/contract tests run
against a started environment with the Yaak CLI runner.

Controller tests cover:

- request validation,
- HTTP status,
- response envelope,
- error shape,
- security annotations and route rules,
- path variables and query parameters.

API/contract tests cover:

- real request/response compatibility,
- auth/login flows,
- workflow happy paths,
- workflow denial paths,
- environment-specific wiring.

## Security And Ownership Tests

Use these for route authorization and resource-scope authorization.

Cover:

- unauthenticated request returns 401,
- wrong role returns 403,
- allowed role can call the route,
- allowed role is still denied for someone else's record,
- `GENERAL_MANAGER` business visibility works,
- `ADMIN` break-glass behavior matches requirements,
- ownership checks happen in services/rules, not only route config.

Each ownership test should name the protected scope: own record, own
department, own supervisees, assigned gate, assigned role inbox, own
application, or own investment stake.

## Error And Observability Tests

Use these for the error and logging contract.

Cover:

- every custom exception maps to the documented status,
- every error has a stable code,
- validation errors include field details,
- business errors can include field details,
- 401 responses use the custom security error shape,
- `X-Request-Id` is returned on success and error,
- request ID appears in error responses,
- MDC is cleared between requests.

## File Storage Tests

Use these for MinIO upload/download behavior.

Cover:

- object key generation,
- metadata row creation,
- upload rollback behavior if metadata persistence fails,
- download authorization before presigned URL creation,
- applicant can access own CV,
- unauthorized actor cannot access another person's file,
- binary content is stored in MinIO, not PostgreSQL.

## Migration And Seed Tests

Use these for migrations or seed catalog data changes.

Cover:

- Flyway applies from an empty PostgreSQL database,
- required catalog rows exist after seed migrations,
- constraints reject invalid data,
- JPA mappings match migration names and constraints,
- seed data does not create environment-specific fake records unless it is a
  dev-only repeatable migration.

## Test Data

Prefer small builders or factory methods that use business language:

- `staffMember()`,
- `departmentManagerOf(department)`,
- `activePermitFor(person)`,
- `contractWithPrimarySigner(person)`,
- `guardAssignedTo(gate)`.

Avoid giant shared fixtures that make tests depend on unrelated data.

## Commands

```bash
./gradlew compileJava
./gradlew test
git diff --check
```

Run `./gradlew test` before commits that change Java code, mappings, or
committed documentation standards. Run `git diff --check` before every commit.

Every committed test runner must be listed here with its exact command.
