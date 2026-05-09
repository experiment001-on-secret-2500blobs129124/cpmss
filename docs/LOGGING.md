# Logging Guide

CPMSS uses Spring Boot logging through SLF4J and Logback.

## Current Implementation Baseline

Logging is already in place, but observability is not finished.

Current implementation:

- Many services define a class logger with `LoggerFactory.getLogger(...)`.
- Services log important create/update/delete and workflow events such as
  payment creation, contract updates, gate entries, KPI close, payroll close,
  user account changes, internal-report updates, and hiring steps.
- `GlobalExceptionHandler` logs handled application exceptions.
- `JwtUtils` logs invalid JWT parsing at `debug`.
- No `RequestIdFilter`, MDC request ID, or `X-Request-Id` response header is
  currently implemented.
- No custom Spring Security `AuthenticationEntryPoint` or `AccessDeniedHandler`
  currently emits the unified error/log shape.
- Logs are not yet governed by a consistent event-name taxonomy.

So logging does not need a total rewrite. It needs a focused cross-cutting
refactor plus a service-log privacy/consistency pass.

## Levels

| Level | Use |
|-------|-----|
| `trace` | Very detailed local debugging. Do not leave noisy trace logs in normal paths. |
| `debug` | Developer diagnostics that are useful during investigation but not normal operations. |
| `info` | Important business or system transitions. |
| `warn` | Denied actions, recoverable problems, validation/rule failures worth noticing. |
| `error` | Unexpected failures that require investigation. |

## Request ID Policy

Every request should eventually have one request ID used by logs, response
headers, and error responses.

Target behavior:

- Header name: `X-Request-Id`.
- MDC key: `requestId`.
- If the client sends a safe `X-Request-Id`, reuse it.
- If it is missing or unsafe, generate a UUID.
- Put the request ID in the response header.
- Put the request ID in every error response.
- Clear MDC in a `finally` block.

Suggested log pattern once the filter exists:

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

After the error refactor, the exception boundary should log:

- `requestId`,
- error `code`,
- status,
- exception type,
- short sanitized message.

## Security Boundary

Spring Security can reject a request before it reaches controllers. After the
custom `AuthenticationEntryPoint` and `AccessDeniedHandler` are added, they
should log:

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

## Current Service Log Review Targets

The current code already logs many service transitions. During the logging
refactor, review these log families for event names, actor IDs, request IDs,
and sensitive-data exposure:

| Context | Current examples to review |
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

## Implementation Checklist

1. Add `RequestIdFilter` with MDC setup/cleanup and `X-Request-Id` response
   header.
2. Update log pattern so request ID appears in every line.
3. Update error response creation so `requestId` is included.
4. Add custom Spring Security 401/403 handlers and reuse the same response
   factory.
5. Add event names to service transition logs as files are touched for
   authorization/workflow work.
6. Replace sensitive `info` logs with IDs or business references where
   practical.
7. Add tests for request ID propagation and error response request IDs.

## Refactor Size

This is a medium, cross-cutting refactor:

- small new platform filter,
- small shared error response factory,
- small security handler wiring,
- small log-pattern config,
- incremental service log cleanup.

It should be done before or alongside authorization work because ownership
denials and security failures are much easier to debug when request IDs and
consistent error codes already exist.
