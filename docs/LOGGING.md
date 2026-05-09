# Logging Guide

CPMSS uses Spring Boot logging through SLF4J and Logback.

## Levels

| Level | Use |
|-------|-----|
| `trace` | Very detailed local debugging. Do not leave noisy trace logs in normal paths. |
| `debug` | Developer diagnostics that are useful during investigation but not normal operations. |
| `info` | Important business or system transitions. |
| `warn` | Denied actions, recoverable problems, validation/rule failures worth noticing. |
| `error` | Unexpected failures that require investigation. |

## Controllers

Controllers should stay thin. Log only when the request intent is operationally
useful and not already logged by a service or framework layer.

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
- authorization denied for a specific ownership rule.

Avoid logging every ordinary CRUD read unless it helps diagnose production
behavior.

## Exception Boundary

`GlobalExceptionHandler` logs handled exceptions. Avoid logging the same
exception again before rethrowing unless the earlier log adds important domain
context.

Expected validation and business-rule failures should be `warn`, not `error`.
Unexpected failures should be `error` with stack trace at the boundary.

## Never Log

- passwords,
- password hashes,
- JWTs,
- refresh tokens,
- raw card data,
- secrets or environment values,
- full uploaded file contents,
- complete CV/document contents,
- large request/response bodies containing private data.

## Future Correlation IDs

Request/correlation IDs are planned. Once added, every request log and error
response should include the same ID so a production issue can be traced across
controller, service, persistence, and external-provider boundaries.
