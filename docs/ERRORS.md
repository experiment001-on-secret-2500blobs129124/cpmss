# Error Handling

CPMSS uses a centralized REST exception handler at
`com.cpmss.platform.common.GlobalExceptionHandler`.

Successful controller responses use `ApiResponse<T>`. Error responses use the
standard error envelope documented here.

## Exception Sources

- `GlobalExceptionHandler` handles application exceptions raised after a
  request reaches Spring MVC.
- `MethodArgumentNotValidException` returns field validation errors with
  status `400`.
- `BusinessException` returns status `422`.
- `ConflictException` returns status `409`.
- `ForbiddenException` returns status `403`.
- `ResourceNotFoundException` returns status `404`.
- A fallback `Exception` handler returns status `500`.
- `401` responses are produced by Spring Security before controller handling.

## HTTP Mapping

| Status | Source | Meaning |
|--------|--------|---------|
| 400 | `MethodArgumentNotValidException` | Request DTO validation failed. |
| 401 | Spring Security/auth layer | Missing, invalid, or expired authentication. |
| 403 | `ForbiddenException` | Authenticated user is not allowed to perform the action. |
| 404 | `ResourceNotFoundException` | Requested resource does not exist. |
| 409 | `ConflictException` | Unique, duplicate, or state conflict. |
| 422 | `BusinessException` | Request shape is valid, but a business rule was violated. |
| 500 | fallback `Exception` handler | Unexpected server error. |

## Standard Error Envelope

Every error uses one shape:

```json
{
  "status": 422,
  "code": "MONEY_CURRENCY_MISMATCH",
  "error": "Unprocessable Entity",
  "message": "Cannot add money with different currencies",
  "fields": {
    "money.currency": [
      {
        "code": "MONEY_CURRENCY_MISMATCH",
        "message": "Currency must match the other money value"
      }
    ]
  },
  "requestId": "5c27db63-8c1f-44df-a06f-869cfbfc6ad5",
  "timestamp": "2026-05-09T12:00:00Z"
}
```

Rules:

- `code` is mandatory.
- `requestId` is mandatory.
- `fields` is optional and omitted when the error is not field-specific.
- Field paths use request/DTO names when the error is caused by client input:
  `money.currency`, `contract.parties`, `permit.entitlement`.
- Field paths use domain names when the error is not tied to one DTO:
  `payroll.period`, `kpi.policyRange`, `resource.owner`.
- `message` remains human-readable and can change; `code` is the stable client
  contract.

## Validation Error Shape

DTO validation uses the same field-detail shape as business errors:

```json
{
  "status": 400,
  "code": "VALIDATION_FAILED",
  "error": "Validation Failed",
  "message": "Request validation failed",
  "fields": {
    "email": [
      {
        "code": "VALIDATION_FIELD_INVALID",
        "message": "must be a well-formed email address"
      }
    ]
  },
  "requestId": "5c27db63-8c1f-44df-a06f-869cfbfc6ad5",
  "timestamp": "2026-05-09T12:00:00Z"
}
```

## Authentication Error Shape

401 responses come from Spring Security, not `GlobalExceptionHandler`. The
custom authentication entry point returns the API error envelope.

```json
{
  "status": 401,
  "code": "AUTHENTICATION_REQUIRED",
  "error": "Unauthorized",
  "message": "Authentication is required",
  "requestId": "5c27db63-8c1f-44df-a06f-869cfbfc6ad5",
  "timestamp": "2026-05-09T12:00:00Z"
}
```

Use:

- `AUTHENTICATION_REQUIRED` when no bearer token is present.
- `AUTHENTICATION_INVALID` when a bearer token is malformed or cannot be
  validated.
- `AUTHENTICATION_EXPIRED` only if token expiry is detected separately from
  other invalid-token cases.

## Error Code Naming

Rules:

- Upper snake case.
- Domain prefix first.
- Then the violated concept.
- Then the failure.
- Avoid putting HTTP status in the code.
- Do not create one-off message-shaped codes.

Examples:

- `MONEY_CURRENCY_MISMATCH`
- `CONTRACT_TARGET_INVALID`
- `AUTHENTICATION_REQUIRED`
- `RESOURCE_NOT_FOUND`

## Initial Error Code Catalog

This catalog is based on exception and rule usage across the backend. Add codes
only when a rule or error boundary needs a distinct client contract.

### Common

| Code | Status | Use |
|------|--------|-----|
| `VALIDATION_FAILED` | 400 | DTO validation failed. |
| `VALIDATION_FIELD_INVALID` | 400 | One DTO field failed Bean Validation. |
| `AUTHENTICATION_REQUIRED` | 401 | Protected route called without authentication. |
| `AUTHENTICATION_INVALID` | 401 | Bearer token is malformed, invalid, or unusable. |
| `AUTHENTICATION_EXPIRED` | 401 | Bearer token is expired, if expiry is classified separately. |
| `ACCESS_DENIED` | 403 | Authenticated user lacks route or resource permission. |
| `RESOURCE_NOT_FOUND` | 404 | Requested entity or aggregate was not found. |
| `RESOURCE_CONFLICT` | 409 | Existing state conflicts with the requested change. |
| `BUSINESS_RULE_VIOLATION` | 422 | Temporary fallback while migrating older string-only rules. |
| `UNEXPECTED_ERROR` | 500 | Unexpected server failure. |

### Identity And Authorization

| Code | Status | Use |
|------|--------|-----|
| `AUTH_SETUP_ALREADY_DONE` | 422 | Setup attempted after users already exist. |
| `AUTH_INVALID_CREDENTIALS` | 422 | Login credentials are wrong. |
| `AUTH_REFRESH_TOKEN_INVALID` | 422 | Refresh token is invalid, expired, or wrong type. |
| `AUTH_ACCOUNT_DEACTIVATED` | 422 | Login attempted for inactive account. |
| `USER_EMAIL_ALREADY_REGISTERED` | 422 | AppUser email already exists. |
| `USER_SELF_ROLE_CHANGE_DENIED` | 403 | User attempted to change own role. |
| `USER_SELF_DEACTIVATION_DENIED` | 403 | User attempted to deactivate own account. |
| `USER_ROLE_CHANGE_DENIED` | 403 | Actor cannot assign or promote to the requested role. |
| `USER_CREATION_ROLE_DENIED` | 403 | Actor cannot create a user with the requested role. |
| `SECURITY_CONTEXT_MISSING` | 403 | Service expected an authenticated actor but none exists. |

### People And Organization

| Code | Status | Use |
|------|--------|-----|
| `PERSON_ROLE_REQUIRED` | 422 | Person creation has no role. |
| `PERSON_PASSPORT_REQUIRED` | 422 | Passport number is required for the current operation. |
| `PERSON_PASSPORT_INVALID` | 422 | Passport number format/length is invalid. |
| `PERSON_PASSPORT_DUPLICATE` | 409 | Passport number already belongs to another person. |
| `PERSON_NATIONAL_ID_REQUIRED` | 422 | Egyptian national ID is required. |
| `PERSON_NATIONAL_ID_INVALID` | 422 | Egyptian national ID is not 14 digits. |
| `PERSON_EMAIL_REQUIRED` | 422 | Email value object requires a value. |
| `PERSON_EMAIL_INVALID` | 422 | Email format/length is invalid. |
| `PERSON_PHONE_REQUIRED` | 422 | Phone value object requires a value. |
| `PERSON_PHONE_INVALID` | 422 | Phone format/length is invalid. |
| `PERSON_GENDER_REQUIRED` | 422 | Gender is required. |
| `PERSON_GENDER_INVALID` | 422 | Gender is not an allowed value. |
| `PERSON_SELF_SUPERVISION` | 422 | A person supervises themselves. |
| `DEPARTMENT_DUPLICATE` | 409 | Department name already exists. |
| `ROLE_DUPLICATE` | 409 | Business role name already exists. |
| `QUALIFICATION_DUPLICATE` | 409 | Qualification name already exists. |

### Finance

| Code | Status | Use |
|------|--------|-----|
| `MONEY_AMOUNT_REQUIRED` | 422 | Money amount is missing. |
| `MONEY_AMOUNT_NEGATIVE` | 422 | Money amount is below zero. |
| `MONEY_AMOUNT_NOT_POSITIVE` | 422 | Positive money is required. |
| `MONEY_CURRENCY_REQUIRED` | 422 | Currency is missing. |
| `MONEY_CURRENCY_INVALID` | 422 | Currency is not ISO-4217. |
| `MONEY_CURRENCY_MISMATCH` | 422 | Currency-aware arithmetic mixes currencies. |
| `PAYMENT_TYPE_REQUIRED` | 422 | Payment type is missing. |
| `PAYMENT_TYPE_INVALID` | 422 | Payment type is not allowed. |
| `PAYMENT_DIRECTION_REQUIRED` | 422 | Payment direction is missing. |
| `PAYMENT_DIRECTION_INVALID` | 422 | Payment direction is not allowed. |
| `PAYMENT_METHOD_REQUIRED` | 422 | Payment method is missing. |
| `PAYMENT_METHOD_INVALID` | 422 | Payment method is not allowed. |
| `PAYMENT_RECONCILIATION_STATUS_REQUIRED` | 422 | Reconciliation status is missing. |
| `PAYMENT_RECONCILIATION_STATUS_INVALID` | 422 | Reconciliation status is not allowed. |
| `PAYMENT_NUMBER_REQUIRED` | 422 | Payment number is required. |
| `PAYMENT_NUMBER_INVALID` | 422 | Payment number format/length is invalid. |
| `PAYMENT_REFERENCE_REQUIRED` | 422 | Payment reference is required for a required-reference path. |
| `PAYMENT_REFERENCE_INVALID` | 422 | Payment reference format/length is invalid. |
| `PAYMENT_SUBTYPE_MISMATCH` | 422 | Payment type does not match child payment table. |
| `PAYMENT_CHILD_REQUIRED` | 422 | Payment parent lacks the required child record. |
| `BANK_ACCOUNT_OWNER_INVALID` | 422 | Bank account has zero or multiple owners. |
| `BANK_IBAN_REQUIRED` | 422 | IBAN is required for a required-IBAN path. |
| `BANK_IBAN_INVALID` | 422 | IBAN length, format, or checksum is invalid. |
| `BANK_SWIFT_REQUIRED` | 422 | SWIFT/BIC is required for a required-SWIFT path. |
| `BANK_SWIFT_INVALID` | 422 | SWIFT/BIC format is invalid. |

### Leasing

| Code | Status | Use |
|------|--------|-----|
| `CONTRACT_TARGET_INVALID` | 422 | Contract targets neither or both unit/facility. |
| `CONTRACT_REFERENCE_DUPLICATE` | 409 | Contract reference already exists. |
| `CONTRACT_PRIMARY_SIGNER_REQUIRED` | 422 | Contract has no primary signer when one is required. |
| `CONTRACT_PRIMARY_SIGNER_DUPLICATE` | 409 | Contract already has a primary signer. |
| `CONTRACT_PERIOD_INVALID` | 422 | Contract dates are missing or out of order. |
| `CONTRACT_TYPE_REQUIRED` | 422 | Contract type is missing. |
| `CONTRACT_TYPE_INVALID` | 422 | Contract type is not allowed. |
| `CONTRACT_STATUS_REQUIRED` | 422 | Contract status is missing. |
| `CONTRACT_STATUS_INVALID` | 422 | Contract status is not allowed. |
| `CONTRACT_PARTY_ROLE_REQUIRED` | 422 | Contract party role is missing. |
| `CONTRACT_PARTY_ROLE_INVALID` | 422 | Contract party role is not allowed. |
| `INSTALLMENT_AMOUNT_INVALID` | 422 | Installment amount is not positive. |
| `INSTALLMENT_TYPE_REQUIRED` | 422 | Installment type is missing. |
| `INSTALLMENT_TYPE_INVALID` | 422 | Installment type is not allowed. |
| `INSTALLMENT_STATUS_REQUIRED` | 422 | Installment status is missing. |
| `INSTALLMENT_STATUS_INVALID` | 422 | Installment status is not allowed. |
| `RESIDENCY_PERIOD_INVALID` | 422 | Move-in/move-out dates are missing or out of order. |
| `HOUSEHOLD_RELATIONSHIP_REQUIRED` | 422 | Household relationship is missing. |
| `HOUSEHOLD_RELATIONSHIP_INVALID` | 422 | Household relationship is not allowed. |

### Security

| Code | Status | Use |
|------|--------|-----|
| `PERMIT_ENTITLEMENT_INVALID` | 422 | Permit has zero or multiple entitlement bases. |
| `PERMIT_TYPE_REQUIRED` | 422 | Permit type is missing. |
| `PERMIT_TYPE_INVALID` | 422 | Permit type is not allowed. |
| `PERMIT_STATUS_REQUIRED` | 422 | Permit status is missing. |
| `PERMIT_STATUS_INVALID` | 422 | Permit status is not allowed. |
| `PERMIT_ACCESS_LEVEL_REQUIRED` | 422 | Access level is required for a path that needs it. |
| `PERMIT_ACCESS_LEVEL_INVALID` | 422 | Access level is not allowed. |
| `PERMIT_VALIDITY_INVALID` | 422 | Permit issue/expiry dates are missing or out of order. |
| `ENTRY_SOURCE_INVALID` | 422 | Gate entry has neither or both permit/manual plate. |
| `GATE_DIRECTION_REQUIRED` | 422 | Gate entry direction is missing. |
| `GATE_DIRECTION_INVALID` | 422 | Gate entry direction is not allowed. |
| `GATE_NUMBER_DUPLICATE` | 409 | Gate number already exists. |
| `GATE_STATUS_REQUIRED` | 422 | Gate status is missing. |
| `GATE_STATUS_INVALID` | 422 | Gate status is not allowed. |
| `VEHICLE_OWNER_INVALID` | 422 | Vehicle has zero or multiple owners. |
| `VEHICLE_LICENSE_DUPLICATE` | 409 | Vehicle license number already exists. |
| `LICENSE_PLATE_REQUIRED` | 422 | License plate is missing. |
| `LICENSE_PLATE_INVALID` | 422 | License plate format/length is invalid. |

### HR And Workforce

| Code | Status | Use |
|------|--------|-----|
| `SALARY_AMOUNT_REQUIRED` | 422 | Salary amount is missing. |
| `SALARY_AMOUNT_INVALID` | 422 | Salary amount is not positive. |
| `STAFF_PROFILE_DUPLICATE` | 409 | Staff profile already exists for a person. |
| `STAFF_POSITION_DUPLICATE` | 409 | Staff position title already exists in the department. |
| `HIRE_AGREEMENT_INTERVIEW_REQUIRED` | 422 | Hire agreement lacks a passed interview. |
| `HIRE_AGREEMENT_START_DATE_INVALID` | 422 | Employment start date is before application date. |
| `RECRUITMENT_RESULT_FINAL` | 422 | Interview result cannot be changed after final state. |
| `SHIFT_TIME_WINDOW_INVALID` | 422 | Shift start/end time is missing or out of order. |
| `ATTENDANCE_TIME_WINDOW_INVALID` | 422 | Check-in/check-out time is missing or out of order. |
| `ATTENDANCE_ABSENT_TIME_INVALID` | 422 | Absent attendance includes check-in/out times. |
| `ATTENDANCE_PRESENT_TIME_INVALID` | 422 | Present attendance lacks check-in/out times. |
| `ATTENDANCE_DUPLICATE` | 409 | Staff already has the shift/date attendance or task row. |
| `PAYROLL_PERIOD_REQUIRED` | 422 | Payroll period is missing. |
| `PAYROLL_PERIOD_INVALID` | 422 | Payroll period year/month is invalid. |
| `HOURS_AMOUNT_REQUIRED` | 422 | Hours amount is missing. |
| `HOURS_AMOUNT_INVALID` | 422 | Hours amount is not positive. |
| `HOUR_DELTA_REQUIRED` | 422 | Hour delta is missing. |
| `TASK_DUPLICATE` | 409 | Task title already exists in the department. |
| `SHIFT_ATTENDANCE_TYPE_DUPLICATE` | 409 | Shift attendance type already exists. |

### Performance

| Code | Status | Use |
|------|--------|-----|
| `KPI_SCORE_REQUIRED` | 422 | KPI score is missing. |
| `KPI_SCORE_INVALID` | 422 | KPI score is negative. |
| `KPI_SCORE_RANGE_REQUIRED` | 422 | KPI policy range is missing. |
| `KPI_SCORE_RANGE_INVALID` | 422 | KPI policy range has max <= min. |
| `KPI_POLICY_INACTIVE` | 422 | KPI record uses an inactive policy for the date. |
| `KPI_SUMMARY_CLOSE_INVALID` | 422 | KPI monthly close has no valid records or actor. |
| `PERCENTAGE_RATE_REQUIRED` | 422 | Percentage/rate value is missing. |
| `PERCENTAGE_RATE_INVALID` | 422 | Percentage/rate value is negative. |
| `PERFORMANCE_RATING_REQUIRED` | 422 | Performance rating is missing. |
| `PERFORMANCE_RATING_INVALID` | 422 | Performance rating is not allowed. |
| `PERFORMANCE_SELF_REVIEW_DENIED` | 403 | Staff member attempts to review themselves. |

### Property, Maintenance, And Communication

| Code | Status | Use |
|------|--------|-----|
| `AREA_REQUIRED` | 422 | Area is missing. |
| `AREA_INVALID` | 422 | Area is not positive. |
| `COUNT_REQUIRED` | 422 | Count value is missing. |
| `COUNT_INVALID` | 422 | Count value is negative. |
| `BUILDING_TYPE_REQUIRED` | 422 | Building type is missing. |
| `BUILDING_TYPE_INVALID` | 422 | Building type is not allowed. |
| `FACILITY_MANAGEMENT_REQUIRED` | 422 | Facility management type is missing. |
| `FACILITY_MANAGEMENT_INVALID` | 422 | Facility management owner does not match management type. |
| `OPERATING_HOURS_INVALID` | 422 | Facility hours are missing as a pair or out of order. |
| `UNIT_DUPLICATE` | 409 | Unit number already exists in a building. |
| `UNIT_STATUS_REQUIRED` | 422 | Unit status is missing. |
| `UNIT_STATUS_INVALID` | 422 | Unit status is not allowed. |
| `WORK_ORDER_STATUS_REQUIRED` | 422 | Work order status is missing. |
| `WORK_ORDER_STATUS_INVALID` | 422 | Work order status is not allowed. |
| `WORK_ORDER_PRIORITY_REQUIRED` | 422 | Work order priority is missing. |
| `WORK_ORDER_PRIORITY_INVALID` | 422 | Work order priority is not allowed. |
| `WORK_ORDER_SCHEDULE_INVALID` | 422 | Work order completion date is before scheduled date. |
| `WORK_ORDER_COST_INVALID` | 422 | Work order cost is not positive. |
| `SERVICE_CATEGORY_REQUIRED` | 422 | Service category is missing. |
| `SERVICE_CATEGORY_INVALID` | 422 | Service category is not allowed. |
| `COMPANY_DUPLICATE` | 409 | Company uniqueness conflict, if a unique field is enforced. |
| `REPORT_ASSIGNED_ROLE_REQUIRED` | 422 | Internal report assigned role is missing. |
| `REPORT_ASSIGNED_ROLE_INVALID` | 422 | Internal report assigned role is not allowed. |
| `REPORT_CATEGORY_REQUIRED` | 422 | Report category is missing. |
| `REPORT_CATEGORY_INVALID` | 422 | Report category is not allowed. |
| `REPORT_PRIORITY_REQUIRED` | 422 | Report priority is missing. |
| `REPORT_PRIORITY_INVALID` | 422 | Report priority is not allowed. |
| `REPORT_STATUS_REQUIRED` | 422 | Report status is missing. |
| `REPORT_STATUS_INVALID` | 422 | Report status is not allowed. |

### Shared Platform Values

| Code | Status | Use |
|------|--------|-----|
| `DATE_RANGE_INVALID` | 422 | Date range is missing or out of order. |
| `TIME_WINDOW_INVALID` | 422 | Local time window is missing or out of order. |
| `INSTANT_WINDOW_INVALID` | 422 | Instant window is missing or out of order. |
| `YEAR_MONTH_PERIOD_INVALID` | 422 | Year/month pair is invalid. |

## Usage Rules

- Use 400 for invalid request shape, missing required DTO fields, or format
  validation handled by Bean Validation.
- Use 401 when authentication is missing or invalid.
- Use 403 when the user is authenticated but lacks role or ownership access.
- Use 404 when a referenced resource is not found.
- Use 409 when the operation conflicts with existing state, such as a unique
  duplicate or already-closed record.
- Use 422 when the request is structurally valid but violates a domain rule.
- Use 500 only for unexpected failures.

## Required Components

### Stable Codes

- Error codes live in a typed Java enum under `platform.common` or
  `platform.exception`.
- Custom exceptions carry a stable code and human-readable message.
- `BUSINESS_RULE_VIOLATION` is only a fallback for legacy string-only rules;
  new rules use specific codes.
- Tests prove that each exception maps to the intended status and code.

### Request IDs

- `RequestIdFilter` extends `OncePerRequestFilter`.
- Header name is `X-Request-Id`.
- Inbound IDs are accepted only when short and safe to log; otherwise the
  server generates a UUID.
- SLF4J MDC key is `requestId`.
- Every response includes `X-Request-Id`.
- Every error response includes `requestId`.
- MDC is cleared in a `finally` block.

### Field-Level Business Errors

- Field-error records contain `field`, `code`, and `message`.
- Code-bearing business exceptions can hold zero or more field errors.
- Field-level business errors are used when a service can report multiple
  specific rule failures at once.
- Simple single-rule exceptions stay simple unless the field path helps the
  client fix the request.

### Custom 401 And Security 403

- A JSON `AuthenticationEntryPoint` owns 401 responses.
- A JSON `AccessDeniedHandler` owns Spring Security 403 responses.
- Both security handlers reuse the same error response factory as
  `GlobalExceptionHandler`.
- `SecurityConfig.exceptionHandling(...)` wires both handlers.
- Tests cover missing token, invalid token, wrong role, and service-level
  forbidden cases.
