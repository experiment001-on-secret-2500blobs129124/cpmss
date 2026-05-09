# Non-Functional Requirements

This document captures quality requirements that apply across all CPMSS
features. `docs/REQUIREMENTS.md` defines what the product does; this file
defines the standards the implementation must meet while doing it.

## Security

- Backend authorization is default deny.
- Frontend route hiding is not a security boundary.
- Role checks and resource ownership checks are both required.
- Passwords must be stored only as BCrypt hashes.
- JWTs, refresh tokens, passwords, secrets, and raw card data must never be
  logged.
- `ADMIN` is a break-glass/setup role, not a normal business operator.

## Authorization

- Route-level authorization decides whether a role may call an endpoint.
- Service-level ownership decides whether the caller may touch a specific
  record.
- `GENERAL_MANAGER` may access all business records.
- `ADMIN` may recover or configure accounts according to production operating
  policy.
- Missing policy means deny until the requirement is clarified.

## Privacy

- No raw card data is stored.
- Payment provider demos must store provider references/statuses and only
  masked card display data if a UI needs it.
- Uploaded CVs and documents are private business records.
- Staff salary, attendance, KPI, and performance records are scoped data and
  must not be exposed through broad list endpoints without authorization.

## Auditability

- Business records should keep `created_at` and `updated_at` through the base
  audit model.
- Financial records, payroll snapshots, KPI summaries, contract history, and
  access events are historical records and should be corrected through explicit
  state changes instead of silent deletion.
- Request/correlation IDs connect logs, errors, and API calls for
  investigation.

## Data Integrity

- Flyway migrations are the database source of truth.
- JPA mappings must match table names, column names, nullability, lengths,
  primary keys, foreign keys, and join tables where the schema defines them.
- Business invariants should be enforced in services/rules and backed by
  database constraints when possible.
- Domain value types are required for meaningful primitive concepts such as
  money, periods, rates, scores, email addresses, and statuses.

## Timezone

- Business timezone is `Africa/Cairo`.
- Code must not rely on the server default timezone for business dates or
  month-close logic.
- Time calculations must use timezone database behavior so official
  summer/winter time changes are respected.

## Maintainability

- Source packages follow the DDD-lite bounded-context layout documented in
  `docs/ARCHITECTURE.md`.
- Do not add catch-all services, policies, or utility classes when an existing
  bounded context can own the behavior.
- Public Java code and non-obvious tests must follow `docs/DOCUMENTATION.md`.
- Requirements-first development is required for workflow and authorization
  work.

## Testability

- Value objects and converters need focused unit tests.
- Business rules need positive and negative rule tests.
- Workflow services need transactional service tests.
- API and integration tests cover route behavior, workflow behavior,
  authorization, persistence, and environment wiring.

## Observability and Logging

- Use SLF4J through the Spring Boot logging stack.
- Controllers should log request intent only when it helps operations.
- Services should log meaningful business transitions at `info` or `warn`,
  without leaking secrets or private payloads.
- Unexpected failures should be logged once at the boundary that handles them.
- Detailed logging guidance lives in `docs/LOGGING.md`.

## File Storage

- MinIO is the binary storage system.
- The database stores file metadata, ownership, access scope, and storage keys.
- The database should not store full file contents.
- Uploaded CVs and documents require authorization checks before download.

## Deployment and Environment

- Java 21 is required.
- Machine-specific Gradle settings belong in local ignored
  `gradle.properties`; the tracked example documents expected keys.
- Development targets the local PostgreSQL/Flyway setup.
- Dockerfile, Jenkins, API runner, and separate test/staging/production
  profiles follow the contracts in `docs/DEVOPS.md`.
