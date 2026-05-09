# Logging Guide

CPMSS uses Spring Boot logging through SLF4J and Logback.

## Logging Contract

Application classes use SLF4J class loggers. Services log meaningful business
transitions, the exception boundary logs handled failures, and security
boundaries log authentication/authorization failures without leaking secrets.
Logs use stable event names and request IDs so production behavior can be
traced across controllers, services, errors, and security filters. This
standard covers application event logging, error logging, and security logging.

## Levels

| Level | Use |
|-------|-----|
| `trace` | Very detailed local debugging. Do not leave noisy trace logs in normal paths. |
| `debug` | Developer diagnostics that are useful during investigation but not normal operations. |
| `info` | Important business or system transitions. |
| `warn` | Denied actions, recoverable problems, validation/rule failures worth noticing. |
| `error` | Unexpected failures that require investigation. |

## Runtime Output

Application code logs through SLF4J, and Spring Boot routes those logs through
Logback.

Rules:

- The default operational sink is process stdout/stderr so the runtime,
  container platform, or service manager can capture logs.
- Deployment-specific rolling files or external collectors may be added without
  changing application log calls.
- Operational logs are not stored in CPMSS business tables or Flyway-managed
  schema.
- Durable business history belongs in domain tables and audit columns, not in
  the operational logging sink.

## Request ID Policy

Every request has one request ID used by logs, response headers, and error
responses.

Behavior:

- Header name: `X-Request-Id`.
- MDC key: `requestId`.
- If the client sends a safe `X-Request-Id`, reuse it.
- If it is missing or unsafe, generate a UUID.
- Put the request ID in the response header.
- Put the request ID in every error response.
- Clear MDC in a `finally` block.

Log pattern:

```properties
logging.pattern.level=%5p [requestId:%X{requestId}]
```

## Event Naming

Use stable event names in messages when the log represents a business
transition.

Pattern:

```text
context.action.outcome
```

Examples:

- `payment.installment.created`
- `payment.payroll.created`
- `contract.created`
- `permit.revoked`
- `gate.entry.recorded`
- `payroll.month.closed`
- `kpi.month.closed`
- `internal_report.resolved`
- `auth.login.succeeded`
- `auth.login.failed`
- `authorization.ownership.denied`

The event name should appear first:

```java
log.info("payment.installment.created paymentNo={} installmentId={}",
        payment.getPaymentNo(), installment.getId());
```

## Controllers

Controllers should stay thin. Do not log ordinary CRUD reads in controllers.
Log only when the request intent is operationally useful and not already logged
by a service or framework layer.

Examples:

- public setup/auth flow events without secrets,
- file upload/download attempts without file contents,
- unusual request routing decisions.

## Services

Services own business transitions and are the best place for meaningful logs.
These are application logs, not just error logs.

Good service logs:

- payment created or reconciled,
- payroll month closed,
- KPI month closed,
- permit revoked,
- work order completed,
- internal report resolved,
- user role/status changed,
- authorization denied for a specific ownership rule.

Avoid logging every ordinary CRUD read unless it helps diagnose production
behavior.

## Exception Boundary

`GlobalExceptionHandler` logs handled exceptions. Avoid logging the same
exception again before rethrowing unless the earlier log adds important domain
context.

Expected validation and business-rule failures should be `warn`, not `error`.
Unexpected failures should be `error` with stack trace at the boundary.

The exception boundary logs:

- `requestId`,
- error `code`,
- status,
- exception type,
- short sanitized message.

## Security Boundary

Spring Security can reject a request before it reaches controllers. The custom
`AuthenticationEntryPoint` and `AccessDeniedHandler` log:

- missing/invalid authentication at `warn` only when useful,
- denied role access at `warn`,
- denied ownership access in services/rules at `warn`,
- no JWT or refresh token contents.

## Sensitive Data Policy

Never log:

- passwords,
- password hashes,
- JWTs,
- refresh tokens,
- raw card data,
- CVV values,
- secrets or environment values,
- full uploaded file contents,
- complete CV/document contents,
- large request/response bodies containing private data.

Avoid logging directly identifying personal data at `info` when an internal ID
or business reference is enough:

- email addresses,
- phone numbers,
- passport numbers,
- Egyptian national IDs,
- full names,
- license plates,
- home addresses.

Allowed when needed:

- UUIDs for internal traceability,
- business references such as `paymentNo`, `contractReference`, `workOrderNo`,
  and `permitNo`,
- aggregate counts,
- status labels,
- month/period identifiers.

## Service Log Families

These log families use event names, actor IDs, request IDs, and sanitized
business references:

| Context | Event families |
|---------|----------------------------|
| `identity` | setup, login, account creation, applicant registration, role/status changes |
| `people` | person, role, and qualification create/update/delete |
| `finance` | bank account changes and payment creation |
| `leasing` | contract, party, resident, and installment changes |
| `security` | permit, vehicle, gate, gate assignment, and gate entry changes |
| `hr` | recruitment, interview, hiring, staff profile, and position changes |
| `workforce` | attendance, task assignment, payroll close, salary changes |
| `performance` | KPI policy, KPI records, KPI close, performance reviews |
| `property` | compound, building, facility, unit, pricing, status, manager, hours changes |
| `maintenance` | company and work order changes |
| `communication` | internal report filed/read/unread/resolved |

## Required Components

- Application classes use SLF4J loggers and follow the event-name taxonomy for
  business transitions.
- `RequestIdFilter` sets up MDC, adds `X-Request-Id`, and clears MDC in a
  `finally` block.
- Error response creation includes `requestId`.
- Spring Security 401/403 handlers reuse the same error response factory as
  controller-level errors.
- Service transition logs use the event-name taxonomy above.
- Runtime log retention is owned by stdout capture, file appenders, or an
  external log collector rather than the application database.
- Sensitive values are masked or replaced with UUIDs/business references.
- Tests cover request ID propagation, response headers, error body request IDs,
  and MDC cleanup.
