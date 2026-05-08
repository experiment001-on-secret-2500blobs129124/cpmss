# Java Coding Style

> Formatting and structural rules for Java 21 / Spring Boot 3.x projects.
> Project-agnostic — drop into any repo.
>
> **See also**: [NAMING.md](NAMING.md) · [DOCUMENTATION.md](DOCUMENTATION.md) · [CONVENTIONS.md](CONVENTIONS.md)

---

## Table of Contents

1. [Indentation](#1-indentation)
2. [Line Length](#2-line-length)
3. [Braces](#3-braces)
4. [Spacing](#4-spacing)
5. [Imports](#5-imports)
6. [Class Structure](#6-class-structure)
7. [Methods](#7-methods)
8. [Annotations](#8-annotations)
9. [Lambdas and Streams](#9-lambdas-and-streams)
10. [Strings](#10-strings)
11. [Null Handling](#11-null-handling)
12. [Quick Reference](#12-quick-reference)

---

## 1. Indentation

Use **4 spaces** per indentation level. **Never use tabs.**

```java
public class PersonService {
    private final PersonRepository repository;

    public Person getById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Person", id));
    }
}
```

Rules:
- One statement per line.
- No trailing whitespace.
- Method chains: indent continuation lines by 4 spaces.

---

## 2. Line Length

Soft limit: **100 columns**. Hard limit: **120 columns**.

Break long lines at logical points:

```java
// Method signature — break after each parameter
public PagedResponse<PersonResponse> searchPersons(
        String query,
        Pageable pageable,
        boolean includeInactive) {
    ...
}

// Builder chains — one call per line
Person person = Person.builder()
    .firstName(request.firstName())
    .lastName(request.lastName())
    .slug(SlugUtils.generate(request.firstName()))
    .build();

// Annotations — one per line (see Section 8)
```

---

## 3. Braces

Opening brace on the **same line** as the statement (K&R style). This applies
to classes, methods, `if`, `else`, `for`, `while`, `switch`, `try`, `catch`.

```java
if (person.isActive()) {
    activate(person);
} else {
    deactivate(person);
}
```

```java
for (Role role : person.getRoles()) {
    validate(role);
}
```

**Always use braces** — even for single-statement blocks:

```java
// ✅ correct
if (id == null) {
    throw new IllegalArgumentException("ID must not be null");
}

// ❌ wrong — no braces
if (id == null)
    throw new IllegalArgumentException("ID must not be null");
```

---

## 4. Spacing

### After keywords

```java
if (condition)        // ✅ space before parenthesis
for (int i = 0; ...)  // ✅
while (running)       // ✅
switch (role)         // ✅
```

### No space for method calls

```java
getById(id)           // ✅ no space before parenthesis
person.getName()      // ✅
```

### Binary operators — space on both sides

```java
int total = base + bonus;
boolean valid = age >= 18 && hasConsent;
String name = first + " " + last;
```

### No trailing whitespace

Never leave whitespace at the end of any line.

---

## 5. Imports

### Rules

- **No wildcard imports** (`import java.util.*` is forbidden).
- Each import is explicit: `import java.util.List;`
- IDE manages imports — unused imports are removed on save.

### Order

Group imports in this order, separated by blank lines:

```java
import com.cpmss.platform.common.BaseEntity;  // 1. Project imports
import com.cpmss.people.person.dto.*;

import jakarta.persistence.*;              // 2. Jakarta / javax imports
import jakarta.validation.constraints.*;

import lombok.*;                           // 3. Third-party libraries

import org.springframework.stereotype.*;   // 4. Spring Framework

import java.util.*;                        // 5. Java standard library
import java.time.*;
```

---

## 6. Class Structure

Organize class members in this order:

```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseEntity {

    // 1. Static constants
    public static final int MAX_ROLES = 10;

    // 2. Instance fields
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String slug;

    // 3. Relationships
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private Set<PersonRole> roles = new HashSet<>();

    // 4. Constructors (if not using Lombok)

    // 5. Public methods

    // 6. Private/helper methods
}
```

For service classes:

```java
@Service
public class PersonService {

    // 1. Dependencies (injected via constructor)
    private final PersonRepository repository;
    private final PersonMapper mapper;

    // 2. Rules (instantiated directly — not injected)
    private final PersonRules rules = new PersonRules();

    // 3. Constructor
    public PersonService(PersonRepository repository, PersonMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // 4. Read methods (@Transactional(readOnly = true))
    // 5. Write methods (@Transactional)
    // 6. Private helpers
}
```

---

## 7. Methods

- Maximum length: aim for **40 lines**. If longer, consider splitting.
- Separate methods with **one blank line**.
- Return early for guard clauses:

```java
public Person getById(UUID id) {
    if (id == null) {
        throw new IllegalArgumentException("ID must not be null");
    }
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Person", id));
}
```

---

## 8. Annotations

### Class annotations — one per line, above the class

```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseEntity { ... }
```

### Method annotations — one per line, above the method

```java
@Override
@Transactional(readOnly = true)
public Person getById(UUID id) { ... }
```

### Parameter annotations — inline

```java
public record CreatePersonRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Email String email
) {}
```

### Field annotations — above the field, one per line

```java
@Column(unique = true, nullable = false)
private String slug;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id", nullable = false)
private Department department;
```

---

## 9. Lambdas and Streams

Short lambdas stay inline:

```java
persons.stream()
    .filter(Person::isActive)
    .map(mapper::toResponse)
    .toList();
```

Complex lambdas get extracted to methods:

```java
// ❌ hard to read
persons.stream()
    .filter(p -> p.isActive() && p.getRoles().stream()
        .anyMatch(r -> r.getName().equals("Staff")))
    .toList();

// ✅ extract the predicate
persons.stream()
    .filter(this::isActiveStaff)
    .toList();

private boolean isActiveStaff(Person person) {
    return person.isActive() && person.getRoles().stream()
        .anyMatch(r -> r.getName().equals("Staff"));
}
```

---

## 10. Strings

Use **text blocks** for multi-line strings (Java 21 feature):

```java
String query = """
    SELECT p FROM Person p
    WHERE p.isActive = true
    ORDER BY p.lastName
    """;
```

Use `String.format()` or string concatenation for simple cases:

```java
throw new ResourceNotFoundException(
    "Person with ID " + id + " not found"
);
```

---

## 11. Null Handling

- Prefer `Optional<T>` for return types that may be empty.
- Never return `null` from a public service method — throw an exception or return `Optional`.
- Use `@NotNull` / `@NotBlank` on DTO fields for input validation.

```java
// ✅ Repository returns Optional — service unwraps or throws
public Person getById(UUID id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Person", id));
}

// ❌ Never return null
public Person getById(UUID id) {
    return repository.findById(id).orElse(null);  // BAD
}
```

---

## 12. Quick Reference

| # | Rule | Requirement |
|---|------|-------------|
| 1 | Indentation | 4 spaces (no tabs) |
| 2 | Line length | ≤ 100 soft, ≤ 120 hard |
| 3 | Brace style | K&R — opening brace on same line |
| 4 | Braces on single statements | Always use braces |
| 5 | Space after keywords | `if (`, `for (`, `while (` — yes |
| 6 | Space on method calls | `method()` — no space |
| 7 | Wildcard imports | Forbidden |
| 8 | Annotations | One per line, above the element |
| 9 | Method length | ≤ 40 lines |
| 10 | Trailing whitespace | Never |
| 11 | Null returns | Never from public methods |
| 12 | Method separation | One blank line between methods |
