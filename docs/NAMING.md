# Naming Conventions

> All naming rules for Java 21 / Spring Boot 3.x projects.
> Project-agnostic — drop into any repo.
>
> **See also**: [CODING_STYLE.md](CODING_STYLE.md) · [DOCUMENTATION.md](DOCUMENTATION.md) · [CONVENTIONS.md](CONVENTIONS.md)

---

## Table of Contents

1. [Summary Table](#1-summary-table)
2. [Philosophy](#2-philosophy)
3. [Classes, Interfaces, and Types](#3-classes-interfaces-and-types)
4. [Methods](#4-methods)
5. [Variables](#5-variables)
6. [Constants](#6-constants)
7. [Enums](#7-enums)
8. [Packages](#8-packages)
9. [Files](#9-files)
10. [Database vs Java vs JSON](#10-database-vs-java-vs-json)
11. [REST API Paths](#11-rest-api-paths)

---

## 1. Summary Table

| Element | Convention | Example |
|---------|-----------|---------|
| Classes / Interfaces / Records / Enums | **PascalCase** | `PersonService`, `CreateUnitRequest` |
| Methods | **camelCase** | `getById()`, `validateCanAssign()` |
| Local variables | **camelCase** | `count`, `slug`, `personList` |
| Parameters | **camelCase** | `compoundId`, `pageable` |
| Instance fields | **camelCase** (no prefix) | `repository`, `rules`, `slug` |
| Constants (`static final`) | **SCREAMING_SNAKE_CASE** | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| Enum values | **SCREAMING_SNAKE_CASE** | `ADMIN`, `PENDING`, `IN_PROGRESS` |
| Packages | **all lowercase** | `com.cpmss.property.compound.dto` |
| Files | **PascalCase** (match class) | `PersonService.java`, `BaseEntity.java` |
| DB columns | **snake_case** | `person_id`, `created_at` |
| JSON fields | **camelCase** | `firstName`, `createdAt` |

---

## 2. Philosophy

- **Descriptive names for public API.** Classes, public methods, and fields that others will read must be self-documenting.
- **Short names for local scope.** `i`, `e`, `dto` are fine inside a 5-line block.
- No Hungarian notation — the compiler knows the types.
- Abbreviations are treated as words: `HttpClient`, not `HTTPClient`. `JsonParser`, not `JSONParser`.
- No `m_` or `g_` prefixes — those are C++ conventions. Java uses bare field names.
- If a name needs a comment to explain it, the name is wrong.

---

## 3. Classes, Interfaces, and Types

Use **PascalCase** — every word starts with a capital letter.

```java
// Classes
public class PersonService { ... }
public class GlobalExceptionHandler { ... }

// Interfaces
public interface AuditorAware<String> { ... }

// Records (DTOs)
public record CreatePersonRequest(...) {}
public record PersonResponse(...) {}

// Abstract classes
public abstract class BaseEntity { ... }
```

### Feature class naming pattern

| Class type | Pattern | Example |
|-----------|---------|---------|
| Entity | `{Feature}` | `Person`, `Compound`, `Unit` |
| Repository | `{Feature}Repository` | `PersonRepository` |
| Service | `{Feature}Service` | `PersonService` |
| Rules | `{Feature}Rules` | `PersonRules` |
| Web Controller | `{Feature}Controller` | `PersonController` |
| API Controller | `{Feature}ApiController` | `PersonApiController` |
| Mapper | `{Feature}Mapper` | `PersonMapper` |
| Value object | Domain noun | `Money`, `EmailAddress`, `PaymentNumber` |
| JPA converter | `{ValueObject}Converter` | `MoneyConverter`, `IbanConverter` |
| Create DTO | `Create{Feature}Request` | `CreatePersonRequest` |
| Update DTO | `Update{Feature}Request` | `UpdatePersonRequest` |
| Response DTO | `{Feature}Response` | `PersonResponse` |

### Abbreviation rule

Treat abbreviations as words:

```java
HttpClient;      // ✅ correct
HTTPClient;      // ❌ wrong
JsonParser;      // ✅ correct
JSONParser;      // ❌ wrong
ApiController;   // ✅ correct
APIController;   // ❌ wrong
```

---

## 4. Methods

Use **camelCase** — first word lowercase, subsequent words capitalized.

```java
public Person getById(UUID id) { ... }
public PagedResponse<PersonResponse> listAll(Pageable pageable) { ... }
public void validateCanAssign(Person person, Role role) { ... }
public boolean existsBySlug(String slug) { ... }
```

### Method naming conventions

| Purpose | Prefix | Example |
|---------|--------|---------|
| Retrieve one | `get` | `getById()`, `getBySlug()` |
| Retrieve many | `list`, `find`, `search` | `listAll()`, `findByDepartment()` |
| Create | `create` | `createPerson()` |
| Update | `update` | `updatePerson()` |
| Delete | `delete` | `deletePerson()` |
| Boolean check | `is`, `has`, `can`, `exists` | `isActive()`, `existsByEmail()` |
| Validation | `validate` | `validateCanAssign()` |
| Conversion | `to` | `toResponse()`, `toEntity()` |
| Count | `count` | `countByDepartment()` |

---

## 5. Variables

### Local variables

Use **camelCase**. Keep names short when scope is small:

```java
int i;                    // loop counter — fine
String slug;              // clear enough
UUID id;                  // obvious
List<Person> persons;     // collection — plural noun
```

### Instance fields

Use **camelCase** with no prefix:

```java
public class PersonService {
    private final PersonRepository repository;
    private final PersonRules rules = new PersonRules();
    private final PersonMapper mapper;
}
```

**No `m_` prefix.** Use `this.field` only when disambiguating from a parameter:

```java
public PersonService(PersonRepository repository) {
    this.repository = repository;  // disambiguation needed
}
```

---

## 6. Constants

Use **SCREAMING_SNAKE_CASE** for `static final` fields:

```java
public final class ApiPaths {
    public static final String PERSONS = "/api/v1/persons";
    public static final String PERSONS_BY_ID = "/api/v1/persons/{id}";
}

public final class AppConstants {
    public static final int MAX_PAGE_SIZE = 50;
    public static final int DEFAULT_PAGE_SIZE = 20;
}
```

---

## 7. Enums

Enum type names use **PascalCase**. Enum values use **SCREAMING_SNAKE_CASE**:

```java
public enum SystemRole {
    ADMIN,
    MANAGER,
    STAFF
}

public enum ContractStatus {
    ACTIVE,
    TERMINATED,
    EXPIRED,
    PENDING_APPROVAL
}
```

---

## 8. Packages

Use **all lowercase**, no underscores, no hyphens:

```
com.cpmss                       ← root
com.cpmss.platform.common       ← shared classes
com.cpmss.platform.config       ← configuration
com.cpmss.platform.exception    ← exception hierarchy
com.cpmss.platform.util         ← utility classes
com.cpmss.people.person         ← feature package
com.cpmss.people.person.dto     ← feature DTOs
com.cpmss.property.compound     ← feature package
com.cpmss.finance.money         ← shared finance value objects
```

Package names are singular: `com.cpmss.people.person`, not
`com.cpmss.people.persons`.

---

## 9. Files

One public class per file. Filename matches class name exactly:

| Class | File |
|-------|------|
| `PersonService` | `PersonService.java` |
| `CreatePersonRequest` | `CreatePersonRequest.java` |
| `BaseEntity` | `BaseEntity.java` |

No exceptions. Java enforces this for public classes.

---

## 10. Database vs Java vs JSON

Three naming worlds exist in this project. Each has its own convention:

| Layer | Convention | Example |
|-------|-----------|---------|
| SQL (DDL, Flyway) | **snake_case** | `person_id`, `created_at`, `Person_Role` |
| Java (entities, fields) | **camelCase** | `personId`, `createdAt` |
| JSON (API response) | **camelCase** | `personId`, `createdAt` |

JPA handles the mapping between SQL snake_case and Java camelCase automatically
when `spring.jpa.hibernate.naming.physical-strategy` is configured, or via
explicit `@Column(name = "column_name")` annotations.

### Table names in SQL vs Entity names in Java

| SQL table | Java entity class | JPA annotation |
|-----------|-------------------|----------------|
| `Person` | `Person` | `@Entity` (matches by default) |
| `Person_Role` | `PersonRole` | `@Table(name = "Person_Role")` |
| `App_User` | `AppUser` | `@Table(name = "App_User")` |
| `Staff_Profile` | `StaffProfile` | `@Table(name = "Staff_Profile")` |

---

## 11. REST API Paths

REST paths use **kebab-case** (lowercase with hyphens):

```
/api/v1/persons
/api/v1/persons/{id}
/api/v1/staff-profiles
/api/v1/staff-profiles/{id}
/api/v1/work-orders
/api/v1/work-orders/{id}/assigned-to
```

Future web routes (if Thymeleaf is added) also use kebab-case:

```
/persons
/persons/{slug}
/staff-profiles
```

Constants for these routes live in `ApiPaths.java` using SCREAMING_SNAKE_CASE:

```java
public static final String STAFF_PROFILES = "/api/v1/staff-profiles";
public static final String STAFF_PROFILES_BY_ID = "/api/v1/staff-profiles/{id}";
```
