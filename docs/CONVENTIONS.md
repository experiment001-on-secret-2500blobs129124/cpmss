# Conventions

Implementation patterns every developer follows in this project. Architecture
decisions live in [`ARCHITECTURE.md`](./ARCHITECTURE.md).

---

## Requirements-First Development

Before implementing workflow, authorization, persistence, or domain behavior,
read the relevant section of [`REQUIREMENTS.md`](./REQUIREMENTS.md).

Rules:

- Implement specific requirements directly.
- Do not invent broad behavior when the requirement is outside the approved
  implementation scope, partial, or an open question.
- If an implementation decision affects permissions, data scope, workflow
  state, money, time, or ownership, document the rule in code and tests.
- If the requirement is missing or ambiguous, record the product question
  before coding the behavior.

---

## Default-Deny Authorization

Authorization is default deny. A user may perform an action only when the
backend has an explicit rule allowing both:

- the route/action for their role, and
- the specific record scope for their identity.

Frontend UI hiding is not security. Service/rules checks must enforce
ownership for records such as own profile, own department, own supervisees,
assigned gate, assigned role inbox, own application, and own investment stake.

---

## No Catch-All Architecture Files

Do not solve business or security behavior with one large catch-all class when
the DDD-lite package layout provides a clear owner.

Preferred shapes:

- context-specific policy/rules classes,
- small shared interfaces or helpers only when multiple contexts use the same
  concept,
- explicit tests beside the domain behavior they protect.

---

## Selective CQRS

Keep normal CRUD in one service. Split command and query services only when a
bounded context has complex writes, read-heavy dashboards, or read models that
would make one service unclear.

Use the split for these pressure points:

- `finance`: payments, reconciliation, financial summaries,
- `workforce`: attendance, payroll close, salary dashboards,
- `performance`: KPI close, reviews, KPI dashboards,
- `leasing`: contract lifecycle, installments, occupancy views,
- `maintenance`: work-order lifecycle, queues, vendor workload,
- `communication`: report resolution, role inboxes, unread counts,
- `security`: gate entries, permit/vehicle assignment, gate logs,
- `hr`: recruitment, hiring, staff history, compensation views.

Rules:

- Command services own validation, authorization, transactions, and mutation.
- Query services are read-only and return DTOs/read models.
- Read models do not enforce business invariants.
- Catalog CRUD stays unsplit unless a real projection/dashboard exists.
- API routes stay stable unless a route change is explicitly approved.

---

## Entity Annotations

Never use `@Data` on JPA entities. Lombok's `@Data` generates `equals()` and
`hashCode()` from all fields — this breaks Hibernate proxy comparisons and
corrupts objects stored in `HashSet` / `HashMap` when fields change during
loading.

```java
// Use these annotations on every JPA entity — not @Data
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {Feature} extends BaseEntity {
    private String name;
    private String address;
    // equals() and hashCode() are inherited from BaseEntity — do not override
}

// @Data on entities is forbidden — it generates equals/hashCode from all fields
@Entity
@Data
public class {Feature} extends BaseEntity { ... }

// @Data is safe on plain POJOs (non-entity classes)
@Data
public class SomePlainObject {
    private String value;
}
```

---

## BaseAuditEntity And BaseEntity

JPA entities choose their mapped superclass based on primary-key shape.
Surrogate UUID entities extend `BaseEntity`. Composite-key entities and
shared-primary-key detail entities extend `BaseAuditEntity` and declare their
own IDs.

`BaseAuditEntity` provides only audit fields. `BaseEntity` extends it and adds
the UUID primary key plus identity semantics for Hibernate.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

`@CreatedBy` / `@LastModifiedBy` require two things:

1. `@EnableJpaAuditing` on the main application class or a `@Configuration` class.
2. An `AuditorAware<String>` bean in `config/`:

```java
@Component
public class SecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(auth -> auth instanceof JwtAuthenticationToken)
            .map(auth -> ((JwtAuthenticationToken) auth).getToken().getSubject());
    }
}
```

Without this bean, `createdBy` and `updatedBy` will silently be `null` for
every row.

---

## Domain Value Types

Use explicit domain value types when a primitive would hide an invariant.
Value types should protect pure validation and normalization rules without
changing service orchestration or API routes.

| Use case | Pattern |
|---|---|
| Finite vocabulary | Java `enum`, usually with a JPA `AttributeConverter` |
| One-column scalar | Java `record` plus `AttributeConverter` |
| Multi-column concept | `@Embeddable` value object |
| Repository-backed rule | Service/rules method, not a value object |

Examples:

- `Money` stores amount and currency together so currency-aware arithmetic is
  impossible to forget.
- `EmailAddress`, `PhoneNumber`, `Iban`, `SwiftCode`, `PaymentNumber`, and
  `LicensePlate` normalize and validate externally meaningful identifiers.
- `DatePeriod`, `TimeWindow`, `PayrollPeriod`, and `KpiPeriod` validate date
  ordering or year/month bounds.
- Status and type concepts such as `PaymentType`, `PermitStatus`,
  `WorkOrderStatus`, and `InternalReportPriority` are enums rather than raw
  strings.

Rules:

- Keep database labels stable unless a migration is part of the task.
- Before adding a raw `String`, `BigDecimal`, `Integer`, date, or boolean for a
  domain concept, check whether it should be a primitive, enum, value object,
  embeddable, or repository-backed workflow rule.
- Put pure checks in the value object constructor or enum converter.
- Keep cross-aggregate checks in service/rules classes.
- Preserve existing JSON shapes by using `@JsonCreator` and `@JsonValue` when a
  value object is exposed by a DTO.
- Add focused unit tests for every value object, converter, and enum label set.

---

## Slug Pattern

Named resources that need human-readable web URLs carry a `slug` field
alongside the UUID primary key.

Slugs are for human-readable navigation, not for transactional identity.
UUIDs remain the canonical API identifiers unless a route is explicitly added
for slug lookup.

### Eligible Resources

Slug-bearing resources are named/catalog-like records where humans benefit
from readable URLs:

| Resource | Reason |
|----------|--------|
| `Compound` | public/name-based place identity |
| `Department` | stable organization page |
| `Building` | named/numbered property resource |
| `Facility` | public/name-based facility page |
| `Company` | vendor profile page |
| `Role` | catalog/admin lookup |
| `Qualification` | catalog/admin lookup |
| `StaffPosition` | catalog/admin lookup |
| `Task` | reusable task catalog entry |

Do not add slugs to records that already have business references, are
transactional, or are event/history rows:

| Do not add slugs | Existing identity |
|------------------|-------------------|
| `Payment` | `paymentNo` |
| `Contract` | `contractReference` |
| `Installment` | UUID plus contract/due date context |
| `WorkOrder` | `workOrderNo` |
| `AccessPermit` | `permitNo` |
| `InternalReport` | UUID/report ID |
| `EntersAt` | event timestamp/UUID |
| history tables | parent ID plus effective date |

```java
@Column(unique = true, nullable = false)
private String slug;  // e.g. "some-name"
```

| Route type | Pattern |
|------------|---------|
| Web | `/{resource}/{entity-slug}` |
| REST API | `/api/v1/{resource}/{id}` — UUID, not slug |

### Generation

Slugs are generated by `SlugUtils`, a wrapper around the
`com.github.slugify:slugify` library. It handles Unicode normalization —
accented and non-ASCII characters are transliterated to ASCII equivalents.

```java
public final class SlugUtils {
    private static final Slugify SLUGIFY = Slugify.builder().build();

    private SlugUtils() {}

    public static String generate(String name) {
        return SLUGIFY.slugify(name);
    }
}
```

| Input | Output |
|-------|--------|
| `"Some Name"` | `"some-name"` — spaces become hyphens, lowercased |
| `"Name & Co"` | `"name-co"` — special characters are stripped |
| `"Résumé"` | `"resume"` — diacritics are transliterated to ASCII |

### Uniqueness

`SlugUtils.generate()` is deterministic — the Service layer handles
uniqueness. The slug column has a unique database index; collisions get a
numeric suffix.

```java
String base   = SlugUtils.generate(request.getName());
String slug   = base;
int    suffix = 2;
while (repository.existsBySlug(slug)) {
    slug = base + "-" + suffix++;
}
entity.setSlug(slug);
```

### Persistence And Service Contract

Slug support consists of:

- Flyway migration columns with backfill SQL when rows already exist,
- unique indexes for each slug column,
- entity fields and repository `existsBySlug` / `findBySlug` methods,
- service-level generation and collision handling,
- read-only slug lookup endpoints for resources exposed by slug URLs,
- tests for generation, collision suffixing, and lookup.

Default mutation policy: preserve an existing slug after creation. Slug
regeneration requires redirect/history behavior in the same change so links do
not silently break.

---

## {Feature}Rules.java

Rules classes are the explicit home for business invariants. The contract is strict:

| Rule | Why |
|------|-----|
| No repositories injected | All data is loaded by the Service before Rules are called |
| Methods return `void` | Pass silently, throw explicitly — no boolean returns |
| No side effects | Same input always produces the same result |
| Business invariants only | Format validation belongs to `@Valid` on the DTO |

```java
// No Spring annotations — Rules classes are stateless and have no injected dependencies.
// The Service instantiates them directly as a plain field.
public class {Feature}Rules {

    public void validateCanAddUnit({Feature} entity) {
        if (entity.getUnits().size() >= entity.getMaxUnits()) {
            throw new BusinessException("{Feature} has reached maximum unit capacity");
        }
    }
}

// In {Feature}Service.java — instantiate directly, do not @Autowired inject
public class {Feature}Service {
    private final {Feature}Rules rules = new {Feature}Rules();
}
```

---

## Centralized Routes (ApiPaths.java)

All route strings are constants in one file. Controllers import constants —
never hardcode strings.

```java
public final class ApiPaths {
    private ApiPaths() {}

    // All REST routes are versioned under /api/v1
    // Pattern: RESOURCE + action
    // public static final String RESOURCE        = "/api/v1/resource";
    // public static final String RESOURCE_BY_ID  = "/api/v1/resource/{id}";
    // ... defined as features are implemented
}
```

---

## Pagination

All list endpoints return `PagedResponse<T>` instead of Spring's raw `Page<T>`.
This keeps the API response shape stable and independent of Spring internals.

```java
public record PagedResponse<T>(
    List<T> content,
    long    totalElements,
    int     totalPages,
    int     pageNumber,
    int     pageSize
) {}
```

---

## Transaction Boundaries

`@Transactional` is applied at the service layer only.

| Method prefix | Annotation |
|---------------|-----------|
| `get`, `find`, `list`, `search`, `count`, `exists` | `@Transactional(readOnly = true)` |
| All others | `@Transactional` |

---

## MapStruct + Records

When the target DTO is a Java Record (immutable, no setters), annotate the
mapper method with `@BeanMapping(builder = @Builder(disableBuilder = true))`.
This tells MapStruct to call the all-args constructor directly.

```java
@Mapper(componentModel = "spring")
public interface {Feature}Mapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    {Feature}Response toResponse({Feature} entity);

    {Feature} toEntity(Create{Feature}Request dto);
}
```

---

## Bridge Tables — `@JoinTable` vs `@Entity`

M:M relationships require a bridge table. JPA offers two ways to map it.

| | Silent `@JoinTable` | Full `@Entity` |
|---|---|---|
| Java class for the bridge? | No — Hibernate manages it invisibly | Yes — extends `BaseEntity` |
| Audit columns? | Cannot have | Inherited from `BaseEntity` |
| Extra columns beyond two FKs? | Cannot have | Yes |
| Total participation rule? | Cannot enforce | Enforced in service layer |
| Who inserts rows? | Hibernate automatically | Your service layer |

**Decision rule:**

> If the bridge table has **any** column beyond the two FK columns — or if you need to record who/when/why the relationship was created — it **must** be a full `@Entity`.
>
> If it has only two FK columns and no audit or participation constraint: silent `@JoinTable` is fine.

```java
// Silent @JoinTable — purely structural M:M, no history needed
// No {Parent}{Child}.java class — Hibernate manages the rows invisibly
@ManyToMany
@JoinTable(
    name = "{Parent}_{Child}",
    joinColumns        = @JoinColumn(name = "{parent}_id"),
    inverseJoinColumns = @JoinColumn(name = "{child}_id")
)
private Set<{Child}> {children};

// Full @Entity — when audit or extra data is required
@Entity
@IdClass({Parent}{Child}Id.class)
public class {Parent}{Child} extends BaseAuditEntity {  // audit fields inherited
    @Id @ManyToOne @JoinColumn(name = "{parent}_id") private {Parent} {parent};
    @Id @ManyToOne @JoinColumn(name = "{child}_id")  private {Child}  {child};
    // any additional columns here
}
```
