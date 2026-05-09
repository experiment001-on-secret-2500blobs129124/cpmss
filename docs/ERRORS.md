# Error Handling

CPMSS uses a centralized REST exception handler at
`com.cpmss.platform.common.GlobalExceptionHandler`.

Successful controller responses use `ApiResponse<T>`. Error responses do not
use that envelope today; they use the handler-specific shapes documented here.

## Current HTTP Mapping

| Status | Source | Meaning |
|--------|--------|---------|
| 400 | `MethodArgumentNotValidException` | Request DTO validation failed. |
| 401 | Spring Security/auth layer | Missing, invalid, or expired authentication. |
| 403 | `ForbiddenException` | Authenticated user is not allowed to perform the action. |
| 404 | `ResourceNotFoundException` | Requested resource does not exist. |
| 409 | `ConflictException` | Unique or state conflict. |
| 422 | `BusinessException` | Request shape is valid, but a business rule was violated. |
| 500 | fallback `Exception` handler | Unexpected server error. |

## Single-Message Error Shape

Used for 403, 404, 409, 422, and 500 responses handled by
`GlobalExceptionHandler`.

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Business rule message",
  "timestamp": "2026-05-09T12:00:00Z"
}
```

## Validation Error Shape

Used for 400 request validation failures.

```json
{
  "status": 400,
  "error": "Validation Failed",
  "fields": {
    "email": "must not be blank"
  },
  "timestamp": "2026-05-09T12:00:00Z"
}
```

## 401 Responses

401 responses come from the Spring Security/authentication layer, not from
`GlobalExceptionHandler`. Their exact response body is owned by the security
configuration unless a custom authentication entry point is added later.

## Usage Rules

- Use 400 for invalid request shape, missing required DTO fields, or format
  validation.
- Use 403 when the user is authenticated but lacks role or ownership access.
- Use 404 when a referenced resource is not found.
- Use 409 when the operation conflicts with existing state, such as a unique
  duplicate or already-closed record.
- Use 422 when the request is structurally valid but violates a domain rule.
- Use 500 only for unexpected failures.

## Planned Improvements

- Stable machine-readable error codes.
- Request/correlation IDs on every error response.
- Field-level business errors if services need to report multiple rule
  failures at once.
- Custom 401 response shape aligned with the rest of the API.
