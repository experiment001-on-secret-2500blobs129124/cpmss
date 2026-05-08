# Documentation Standard

> Based on [Javadoc](https://docs.oracle.com/en/java/javase/21/docs/specs/javadoc/doc-comment-spec.html).
> Designed for Java 21 / Spring Boot 3.x projects.
>
> **See also**: [CODING_STYLE.md](CODING_STYLE.md) · [NAMING.md](NAMING.md)

---

## Block Format

A Javadoc block starts with `/**` and sits **directly above** the element it describes.
The **first sentence** is the brief description automatically — no special tag needed.

```java
/**
 * Short description — this is the brief (first sentence).
 *
 * <p>Extended description goes here. Can span multiple lines.
 *
 * @param paramName description
 * @return description of return value
 * @throws ExceptionType when condition is met
 */
```

Rules:
- `/**` starts (two asterisks — regular `/*` is not Javadoc).
- Each line inside starts with ` * `.
- First sentence = brief. Ends at the first period + whitespace.
- `<p>` starts a new paragraph for extended description.

---

## Documenting Methods

| Situation | Rule |
|-----------|------|
| Brief (first sentence) | **Mandatory** on public methods |
| Extended description | **Mandatory** when behavior is non-obvious |
| `@param` | **Mandatory** when parameters exist |
| `@return` (void) | Omit |
| `@return` (non-void) | **Mandatory** |
| `@throws` | **Mandatory** for documented exceptions |

### Examples

```java
/**
 * Retrieves a person by their unique identifier.
 *
 * @param id the person's UUID primary key
 * @return the matching person entity
 * @throws ResourceNotFoundException if no person exists with this ID
 */
public Person getById(UUID id) { ... }
```

```java
/**
 * Validates that a person can be assigned the given role.
 *
 * <p>Enforces: person must have at least one role,
 * duplicate role assignments are rejected.
 *
 * @param person the person to validate
 * @param role the role being assigned
 * @throws BusinessException if the role is already assigned
 */
public void validateCanAssign(Person person, Role role) { ... }
```

---

## Documenting Classes

```java
/**
 * Orchestrates person lifecycle operations.
 *
 * <p>Handles CRUD for Person entities. Delegates business rules
 * to {@link PersonRules} and data access to {@link PersonRepository}.
 *
 * @see PersonRules
 * @see PersonRepository
 */
@Service
public class PersonService { ... }
```

---

## Documenting Records (DTOs)

```java
/**
 * Request payload for creating a new person.
 *
 * @param firstName the person's first name
 * @param lastName the person's last name
 * @param email the person's primary email address
 */
public record CreatePersonRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Email String email
) {}
```

Records that act as domain value objects follow the same rule, but their
Javadoc explains the invariant they protect:

```java
/**
 * Currency-aware monetary value.
 *
 * <p>Amount is normalized to two decimal places and cannot be negative.
 *
 * @param amount the decimal monetary amount
 * @param currency the ISO currency code for the amount
 */
public record Money(BigDecimal amount, CurrencyCode currency) {}
```

---

## Documenting Enums

```java
/**
 * System-level permission roles for software access.
 *
 * <p>Independent from business roles (Person_Role).
 * See DATABASE.md § "Role Architecture".
 */
public enum SystemRole {
    /** Full system access. */
    ADMIN,
    /** Department-level access — approvals, reports. */
    MANAGER,
    /** Operational access — daily tasks, attendance. */
    STAFF
}
```

---

## Documenting Converters

JPA converters are public persistence boundary code. Document what Java type
they persist, which database representation they preserve, and whether blank
input becomes `null` or is rejected by the value object.

```java
/**
 * Converts {@link EmailAddress} values to and from their database string form.
 *
 * <p>The converter preserves the one-column schema while allowing the entity
 * to expose the validated domain value.
 */
@Converter(autoApply = false)
public class EmailAddressConverter implements AttributeConverter<EmailAddress, String> { ... }
```

---

## Documenting Interfaces

```java
/**
 * Spring Data repository for {@link Person} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query methods.
 */
public interface PersonRepository extends JpaRepository<Person, UUID> {
    /**
     * Checks whether a person with the given slug exists.
     *
     * @param slug the URL-friendly identifier
     * @return true if a matching person exists
     */
    boolean existsBySlug(String slug);
}
```

---

## Inline Field Documentation

```java
@Entity
public class Person extends BaseEntity {
    /** URL-friendly identifier generated from the person's name. */
    @Column(unique = true, nullable = false)
    private String slug;

    /** Nationally-issued ID number (masked in API responses). */
    @Column(name = "national_id")
    private String nationalId;
}
```

Javadoc always goes **above** the element — no trailing comment syntax.

---

## What to Document vs Skip

| Document | Skip |
|----------|------|
| Public service methods | Lombok-generated getters/setters |
| Rules class methods | Private helpers with obvious names |
| Entity classes | `BaseEntity` fields (documented once) |
| Record components in DTOs | Standard repo methods (`findById`, `save`) |
| Public value objects, enums, and converters | Private helpers with obvious names |
| Non-obvious field semantics | Self-documenting fields (`email`, `name`) |
| Custom repository queries | Config classes (annotations explain them) |

> If removing the Javadoc would leave a reader confused about **what** or **why**, it's mandatory.

---

## Planned Documentation Backlog

- `V8__seed_catalog_data.sql` (future)

---

## Tag Reference

| Tag | Purpose | Example |
|-----|---------|---------|
| *(first sentence)* | Brief | `Retrieves a person by ID.` |
| `<p>` | Extended description | `<p>Loads from cache first.` |
| `@param name desc` | Parameter | `@param id the person's UUID` |
| `@return desc` | Return value | `@return the matching entity` |
| `@throws Type desc` | Exception | `@throws ResourceNotFoundException if not found` |
| `@see` | Cross-reference | `@see PersonRules#validateCanAssign` |
| `{@link Class}` | Inline reference | `Delegates to {@link PersonRules}` |
| `{@code expr}` | Inline code | `Returns {@code null} if empty` |
| `<pre>{@code ...}</pre>` | Code block | Multi-line example |
| `@apiNote` | Usage guidance | `@apiNote Call after validation` |
| `@implNote` | Implementation detail | `@implNote Uses optimistic locking` |
| `@deprecated` | Deprecation | `@deprecated Use {@link #newMethod}` |
