# Testing Guide

This document defines how CPMSS tests should be organized and what each layer
is expected to prove.

## Current Test Reality

- JUnit Platform is enabled.
- Current tests are focused unit tests.
- Mockito is available for tests that need collaborators.
- Existing coverage mainly protects domain value objects, enum converters, and
  small validation behavior.
- Full service integration tests, API tests, and Testcontainers database tests
  are planned, not yet the main test suite.

## Test Layers

### Value Object Tests

Use these for domain primitives such as `Money`, `KpiScore`, `EmailAddress`,
`LicensePlate`, periods, windows, rates, and reference numbers.

Cover:

- valid construction,
- invalid construction,
- normalization,
- equality,
- arithmetic or comparison behavior where relevant,
- converter round-trips when database labels must remain stable.

### Rules Tests

Use these for pure business-rule classes and repository-backed policy checks.

Cover:

- allowed action,
- denied action,
- edge case,
- exception type/message when it is part of API behavior.

Rules tests should be the first target when workflow or ownership logic
changes.

### Service Tests

Use these for workflows that span tables or aggregates.

Cover:

- transaction boundary behavior,
- orchestration order,
- cross-aggregate checks,
- idempotency/duplicate behavior when required,
- frozen snapshot behavior for payroll, KPI, and financial records.

### API Tests

API tests are planned after route authorization and workflow services settle.

Cover:

- HTTP status,
- response envelope,
- validation errors,
- authorization failures,
- path and request/response compatibility.

### Integration Tests

Testcontainers is planned for PostgreSQL/Flyway integration coverage.

Use integration tests when behavior depends on:

- Flyway constraints,
- JPA mappings,
- composite keys,
- transaction rollback,
- database uniqueness,
- real repository queries.

## Documentation Expectations

Tests are documentation. Follow `docs/DOCUMENTATION.md` when a test class,
helper, or fixture protects a business rule that would not be obvious from the
method name alone.

Required style:

- test class names should point at the domain concept,
- display names should read like business behavior,
- helpers should explain domain setup rather than hide it,
- assertions should make the protected rule obvious.

## Commands

```bash
./gradlew compileJava
./gradlew test
git diff --check
```

Run `./gradlew test` before commits that change Java code, mappings, or
committed documentation standards. Run `git diff --check` before every commit.
