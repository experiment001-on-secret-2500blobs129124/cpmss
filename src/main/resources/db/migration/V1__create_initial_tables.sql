-- Note: No CREATE DATABASE or USE statement here.
-- Flyway connects to the target database via application configuration (application.yml / Docker env).
-- The database must already exist before migrations are applied. PostgreSQL uses UTF-8 by default.

-- ============================================================================
-- CORE ENTITIES
-- (no dependencies — all other sections build on these)
-- ============================================================================

-- Core entity: represents a managed compound — the top-level entity all other entities belong to.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Compound (
    compound_id  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    compound_name VARCHAR(100) NOT NULL,
    country       VARCHAR(50)  NOT NULL,
    city          VARCHAR(50)  NOT NULL,
    district      VARCHAR(50),
    -- Audit columns (mapped to BaseEntity: @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

-- Catalog table: defines departments in the compound (e.g. 'Security', 'Maintenance', 'HR').
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Department (
    department_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    department_name VARCHAR(100) NOT NULL UNIQUE,
    -- Audit columns (mapped to BaseEntity)
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

-- Catalog table: defines task types available in the system — scoped to a department (e.g. 'Security Patrol' → Security, 'Payroll Processing' → Finance).
-- department_id here eliminates the need for department_id on Assigned_Task (avoids 3NF violation: task_id → department_id transitively).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Task (
    task_id       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    task_title    VARCHAR(50)  NOT NULL,
    department_id UUID         NOT NULL REFERENCES Department(department_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

-- Catalog table: defines shift types used in attendance tracking (e.g. 'Morning', 'Night', 'Rotating').
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Shift_Attendance_Type (
    shift_id   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    shift_name VARCHAR(50) NOT NULL,
    -- Audit columns (mapped to BaseEntity)
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Core entity: represents an external company associated with the compound (also acts as Vendor).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Company (
    company_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(150) NOT NULL,
    tax_id       VARCHAR(50),
    phone_no     VARCHAR(20),
    company_type VARCHAR(50),
    -- Audit columns (mapped to BaseEntity)
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

-- Catalog table: defines person roles in the system (e.g. 'Staff', 'Tenant', 'Investor', 'Visitor').
-- A person can hold multiple roles simultaneously — see Person_Role junction table.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Role (
    role_id    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    role_name  VARCHAR(50) NOT NULL UNIQUE,
    -- Audit columns (mapped to BaseEntity)
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Catalog table: defines qualification levels (e.g. 'High School Diploma', 'Bachelor''s Degree', 'PhD').
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Qualification (
    qualification_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    qualification_name VARCHAR(100) NOT NULL UNIQUE,
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- Core entity: represents a natural person in the system — the basis for all roles, contracts, and relationships.
-- Person has no FK dependencies — roles are assigned via Person_Role (see COMPOUND STRUCTURE section).
-- Note: enforcing "every Person must have at least one role at creation" is a business rule handled
-- in PersonService (@Transactional) + PersonRules.java. At the DB level this cannot be expressed as a
-- simple constraint due to the chicken-and-egg insertion order problem.
--
-- Gender is stored as a plain column, not a lookup table.
-- Rationale: a lookup table would cost 16 bytes per UUID FK per row vs 6 bytes for 'Female' directly,
-- plus an extra JOIN on every Person query — pure overhead for a fixed vocabulary.
--            Allowed values are enforced in PersonRules.java and V2__add_constraints.sql.
-- Note: at least one phone number and one email (Person_Phone, Person_Email) are required for adult persons.
--       Minors (household_relationship = 'Child' in Person_Resides_Under) are exempt — enforced in PersonRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person (
    person_id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    -- egyptian_national_id: 14-digit Egyptian national ID, nullable (Egyptians only).
    -- Whether an Egyptian must provide this is a business rule enforced in PersonRules.java.
    egyptian_national_id VARCHAR(14)  UNIQUE,
    passport_no          VARCHAR(20)  NOT NULL UNIQUE,
    first_name           VARCHAR(100) NOT NULL,
    middle_name          VARCHAR(100),
    last_name            VARCHAR(100) NOT NULL,
    nationality          VARCHAR(50),
    city                 VARCHAR(50),
    street               VARCHAR(150),
    date_of_birth        DATE,
    gender               VARCHAR(6),
    is_blacklisted       BOOLEAN      NOT NULL DEFAULT FALSE,
    -- Audit columns (mapped to BaseEntity)
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255)
);


-- ============================================================================
-- COMPOUND STRUCTURE & PEOPLE RELATIONS
-- (depends on: Compound, Department, Person, Company, Role, Qualification)
-- ============================================================================

-- Junction table: assigns one or more roles to a person (M:M — one person can be Staff AND Tenant etc.).
-- Full @Entity (not a silent @JoinTable) because role assignments are audited.
-- Note: every person must have at least one role — enforced transactionally in the service layer.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person_Role (
    person_id  UUID        NOT NULL REFERENCES Person(person_id)  ON DELETE CASCADE,
    role_id    UUID        NOT NULL REFERENCES Role(role_id)       ON DELETE RESTRICT,
    PRIMARY KEY (person_id, role_id),
    -- Audit columns (mapped to BaseEntity)
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Detail table (1:1 extension of Person): holds Staff-specific attributes.
-- Only persons with the 'Staff' role have a row here — no row exists for non-Staff persons.
-- Note: ensuring a Staff_Profile row is created when a person is assigned the Staff role is a
-- business rule handled in PersonService (@Transactional) + PersonRules.java — same chicken-and-egg
-- constraint as Person_Role. The DB cannot enforce "if Staff role exists, this row must also exist."
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_Profile (
    person_id           UUID PRIMARY KEY REFERENCES Person(person_id)               ON DELETE CASCADE,
    -- RESTRICT: Qualification is a catalog table — cannot delete a level that staff members still reference.
    qualification_id    UUID NOT NULL    REFERENCES Qualification(qualification_id) ON DELETE RESTRICT,
    qualification_date  DATE,
    -- cv_file_url: reference to the uploaded CV file in object storage (MinIO).
    -- The actual file is NOT stored in the DB — only the path or URL is.
    cv_file_url         VARCHAR(500),
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- Owned entity: a named job position that staff can hold, scoped to a department (e.g. 'Security Guard', 'Maintenance Tech') — owned by Department, cannot exist without it.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_Position (
    position_id   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    position_name VARCHAR(100) NOT NULL,
    department_id UUID         NOT NULL REFERENCES Department(department_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

-- Detail-History table: tracks which Position a Staff member has held over time (SCD Type 2).
-- Answers "what position does this person currently hold?" (end_date IS NULL = still active).
-- person_id must reference a Person with the 'Staff' role — enforced in StaffService + StaffRules.java.
-- Note: authorized_by_id is the manager who approved this position change (promotion, transfer, or demotion).
--       NULL only for the initial position at hiring time. Enforced in StaffPositionRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_Position_History (
    person_id        UUID        NOT NULL REFERENCES Person(person_id)             ON DELETE CASCADE,
    position_id      UUID        NOT NULL REFERENCES Staff_Position(position_id)   ON DELETE RESTRICT,
    effective_date   DATE        NOT NULL,
    end_date         DATE,
    authorized_by_id UUID        REFERENCES Person(person_id)                      ON DELETE RESTRICT,
    PRIMARY KEY (person_id, position_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Owned entity: a physical access gate belonging to the compound (e.g. 'Main Entrance', 'Service Gate') — owned by Compound, cannot exist without it.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Gate (
    gate_id     UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    gate_no     VARCHAR(20)  NOT NULL UNIQUE,
    gate_name   VARCHAR(100) NOT NULL,
    gate_type   VARCHAR(50),
    gate_status VARCHAR(50),
    compound_id UUID         NOT NULL REFERENCES Compound(compound_id) ON DELETE CASCADE,
    UNIQUE (compound_id, gate_name),
    -- Audit columns (mapped to BaseEntity)
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- Owned entity: a physical building within the compound — owned by Compound, cannot exist without it. Units and Facilities belong to a Building.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Building (
    building_id       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    building_no       VARCHAR(20)  NOT NULL,
    building_name     VARCHAR(100),
    building_type     VARCHAR(50),
    floors_count      INT,
    construction_date DATE,
    compound_id       UUID         NOT NULL REFERENCES Compound(compound_id) ON DELETE CASCADE,
    -- Audit columns (mapped to BaseEntity)
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255)
);


-- Child table: stores multiple phone numbers for a single Person (multi-value attribute).
-- No audit columns — managed as part of the Person entity lifecycle.
CREATE TABLE Person_Phone (
    person_id    UUID        NOT NULL REFERENCES Person(person_id) ON DELETE CASCADE,
    country_code VARCHAR(5)  NOT NULL,
    phone        VARCHAR(20) NOT NULL,
    PRIMARY KEY (person_id, country_code, phone)
);

-- Child table: stores multiple email addresses for a single Person (multi-value attribute).
-- No audit columns — managed as part of the Person entity lifecycle.
CREATE TABLE Person_Email (
    person_id UUID         NOT NULL REFERENCES Person(person_id) ON DELETE CASCADE,
    email     VARCHAR(150) NOT NULL,
    PRIMARY KEY (person_id, email)
);

-- Junction table: records supervision relationships between persons (M:M self-referential — one person can supervise many, and be supervised by many).
-- Full @Entity because supervision periods are time-bounded and audited.
-- Note: a person cannot supervise themselves — enforced in PersonRules.java and V2__add_constraints.sql.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person_Supervision (
    supervisor_id          UUID NOT NULL REFERENCES Person(person_id) ON DELETE CASCADE,
    supervisee_id          UUID NOT NULL REFERENCES Person(person_id) ON DELETE CASCADE,
    supervision_start_date DATE NOT NULL,
    supervision_end_date   DATE,
    PRIMARY KEY (supervisor_id, supervisee_id, supervision_start_date),
    -- Audit columns (mapped to BaseEntity)
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by             VARCHAR(255),
    updated_by             VARCHAR(255)
);

-- Junction table: records which persons work for an external company (M:M — a person can work for multiple companies).
-- Full @Entity because the position within the company is tracked as extra data.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person_Works_for_Company (
    employee_id         UUID         NOT NULL REFERENCES Person(person_id)    ON DELETE CASCADE,
    company_id          UUID         NOT NULL REFERENCES Company(company_id)  ON DELETE CASCADE,
    position_in_company VARCHAR(100),
    PRIMARY KEY (employee_id, company_id),
    -- Audit columns (mapped to BaseEntity)
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255)
);

-- Junction table: records investment stakes held by persons in compounds (M:M — a person can invest in multiple compounds).
-- Full @Entity because investment amount and timestamp are tracked as extra data.
-- Note: investment records are permanent financial history — never deleted. ON DELETE RESTRICT on both FKs
--       prevents deletion of a Person or Compound that has investment records.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person_Invests_in_Compound (
    investor_id UUID           NOT NULL REFERENCES Person(person_id)     ON DELETE RESTRICT,
    compound_id UUID           NOT NULL REFERENCES Compound(compound_id) ON DELETE RESTRICT,
    invested_at TIMESTAMPTZ    NOT NULL,
    stock       DECIMAL(10, 2),
    PRIMARY KEY (investor_id, compound_id, invested_at),
    -- Audit columns (mapped to BaseEntity)
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- Owned entity: a bank account belonging to exactly one of: a Compound, a Person, or a Company.
-- Note: exactly one owner must be set — mutual exclusion enforced in BankAccountRules.java and V2__add_constraints.sql.
-- Note: bank accounts are permanent financial records — never deleted. ON DELETE RESTRICT on all owner FKs
--       prevents deletion of any owner that still has bank accounts on record.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Bank_Account (
    account_id       UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_name        VARCHAR(100) NOT NULL,
    iban             VARCHAR(34),
    swift_code       VARCHAR(11),
    is_primary       BOOLEAN      NOT NULL DEFAULT FALSE,
    compound_id      UUID         REFERENCES Compound(compound_id) ON DELETE RESTRICT,
    account_owner_id UUID         REFERENCES Person(person_id)     ON DELETE RESTRICT,
    company_id       UUID         REFERENCES Company(company_id)   ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);



-- Detail-History table: tracks salary bands for a Staff_Position over time (SCD Type 2).
-- ORDER BY salary_effective_date DESC LIMIT 1 = current salary band.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Position_Salary_History (
    position_id           UUID           NOT NULL REFERENCES Staff_Position(position_id) ON DELETE CASCADE,
    salary_effective_date DATE           NOT NULL,
    maximum_salary        DECIMAL(12, 2) NOT NULL,
    base_daily_rate        DECIMAL(8,  2) NOT NULL,
    PRIMARY KEY (position_id, salary_effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- Junction table: records which persons have managed a department over time (M:M time-bounded — a department can have successive managers).
-- Full @Entity because management periods have a defined start and end date.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Department_Managers (
    department_id         UUID NOT NULL REFERENCES Department(department_id) ON DELETE CASCADE,
    manager_id            UUID NOT NULL REFERENCES Person(person_id)         ON DELETE RESTRICT,
    management_start_date DATE NOT NULL,
    management_end_date   DATE,
    PRIMARY KEY (department_id, manager_id, management_start_date),
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- Detail-History table: tracks which building a department is located in over time (SCD Type 2).
-- location_end_date_in_building IS NULL = currently active location.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Department_Location_History (
    department_id                   UUID NOT NULL REFERENCES Department(department_id) ON DELETE CASCADE,
    location_start_date_in_building DATE NOT NULL,
    location_end_date_in_building   DATE,
    building_id                     UUID NOT NULL REFERENCES Building(building_id)     ON DELETE RESTRICT,
    PRIMARY KEY (department_id, location_start_date_in_building),
    -- Audit columns (mapped to BaseEntity)
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255)
);

-- Detail-History table: defines the rule set for a given shift type at a point in time (SCD Type 2).
-- ORDER BY effective_date DESC LIMIT 1 = currently active rule set for a given shift.
-- Note: expected_hours anchors the daily pay calculation — backend uses base_daily_rate × (diff_hour / expected_hours).
--       one_hour_extra_bonus applies per hour ABOVE expected_hours (overtime).
--       one_hour_diff_discount applies per hour BELOW expected_hours (lateness/early leave).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Law_of_Shift_Attendance (
    shift_id               UUID           NOT NULL REFERENCES Shift_Attendance_Type(shift_id) ON DELETE CASCADE,
    effective_date         DATE           NOT NULL,
    start_time             TIME           NOT NULL,
    end_time               TIME           NOT NULL,
    expected_hours         DECIMAL(4,2)   NOT NULL,
    one_hour_extra_bonus   DECIMAL(8, 2),
    one_hour_diff_discount DECIMAL(8, 2),
    period_start_end       VARCHAR(50),
    PRIMARY KEY (shift_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at             TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by             VARCHAR(255),
    updated_by             VARCHAR(255)
);

-- ============================================================================
-- UNITS, FACILITIES & PRICING
-- (depends on: Building, Person)
-- ============================================================================

-- Owned entity: a residential or commercial unit within a Building — owned by Building, cannot exist without it.
-- Note: unit records are permanent — physical demolition is reflected by status change, never by deletion.
--       ON DELETE RESTRICT prevents silent data loss of tenancy, contract, and pricing history.
-- Note: unit_no uniqueness within a building is a business rule enforced in UnitService + UnitRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Unit (
    unit_id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_no                VARCHAR(20)  NOT NULL,
    floor_no               INT,
    no_of_rooms            INT,
    no_of_bathrooms        INT,
    no_of_bedrooms         INT,
    no_of_total_rooms      INT,
    no_of_balconies        INT,
    square_foot            DECIMAL(10, 2),
    view_orientation       VARCHAR(50),
    gas_meter_code         VARCHAR(50),
    water_meter_code       VARCHAR(50),
    electricity_meter_code VARCHAR(50),
    building_id            UUID         NOT NULL REFERENCES Building(building_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by             VARCHAR(255),
    updated_by             VARCHAR(255)
);

-- Detail-History table: tracks listing price of a Unit over time (SCD Type 2).
-- ORDER BY effective_date DESC LIMIT 1 = current listing price.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Unit_Pricing_History (
    unit_id        UUID           NOT NULL REFERENCES Unit(unit_id) ON DELETE RESTRICT,
    effective_date DATE           NOT NULL,
    listing_price  DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (unit_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255)
);

-- Detail-History table: tracks occupancy status of a Unit over time (SCD Type 2).
-- ORDER BY effective_date DESC LIMIT 1 = current status.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Unit_Status_History (
    unit_id        UUID        NOT NULL REFERENCES Unit(unit_id) ON DELETE RESTRICT,
    effective_date DATE        NOT NULL,
    unit_status    VARCHAR(50) NOT NULL,
    PRIMARY KEY (unit_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255)
);

-- Owned entity: a facility within a Building (e.g. gym, pool, parking) — owned by Building, cannot exist without it.
-- Note: facility records are permanent — closure or demolition is reflected by status/operational change, never by deletion.
--       ON DELETE RESTRICT prevents silent data loss of hours history, management records, and work orders.
-- Note: a facility is managed either by the compound directly or by an external vendor (Company).
--       managed_by_company_id must be set when management_type is 'Vendor', and null when 'Compound'.
--       Enforced in FacilityRules.java and V2__add_constraints.sql.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Facility (
    facility_id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_name        VARCHAR(150) NOT NULL,
    facility_category    VARCHAR(50),
    management_type      VARCHAR(20)  NOT NULL,
    managed_by_company_id UUID        REFERENCES Company(company_id) ON DELETE RESTRICT,
    building_id          UUID         NOT NULL REFERENCES Building(building_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255)
);

-- Detail-History table: tracks opening hours of a Facility over time (SCD Type 2).
-- ORDER BY effective_date DESC LIMIT 1 = current hours.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Facility_Hours_History (
    facility_id    UUID        NOT NULL REFERENCES Facility(facility_id) ON DELETE RESTRICT,
    effective_date DATE        NOT NULL,
    opening_time   TIME,
    closing_time   TIME,
    operating_hours VARCHAR(50),
    PRIMARY KEY (facility_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255)
);

-- Junction table: records which persons have managed a facility over time (M:M time-bounded — a facility can have successive managers).
-- Full @Entity because management periods have a defined start and end date.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Facility_Manager (
    facility_id           UUID NOT NULL REFERENCES Facility(facility_id) ON DELETE RESTRICT,
    manager_id            UUID NOT NULL REFERENCES Person(person_id)     ON DELETE RESTRICT,
    management_start_date DATE NOT NULL,
    management_end_date   DATE,
    PRIMARY KEY (facility_id, manager_id, management_start_date),
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- ============================================================================
-- CONTRACTS & OCCUPANCY
-- (depends on: Unit, Facility, Person)
-- ============================================================================

-- Owned entity: legally binding agreement between the compound and a tenant or lessee — owned by either a Unit (Residential) or a Facility (Commercial), cannot exist without one.
-- contract_reference is the human-readable document ID (e.g. "CON-2024-001") printed on signed copies.
-- Note: contract_reference uniqueness is enforced in ContractService + ContractRules.java;
--       the DB UNIQUE constraint is the safety net.
-- Note: a contract covers exactly one target — either a unit (Residential) or a facility (Commercial).
--       Mutual exclusion is enforced in ContractRules.java and V2__add_constraints.sql.
-- Note: contracts are permanent records — physical deletion is not permitted by business policy.
--       A contract is closed by status change (Terminated, Expired), never by deletion.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Contract (
    contract_id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_reference      VARCHAR(50)   NOT NULL UNIQUE,
    start_date              DATE          NOT NULL,
    end_date                DATE,
    contract_type           VARCHAR(50)   NOT NULL,
    contract_status         VARCHAR(50)   NOT NULL,
    payment_frequency       VARCHAR(50),
    final_price               DECIMAL(12, 2),
    final_price_currency      VARCHAR(10),
    security_deposit_amount   DECIMAL(12, 2),
    security_deposit_currency VARCHAR(10),
    renewal_terms           TEXT,
    unit_id                 UUID          REFERENCES Unit(unit_id)         ON DELETE RESTRICT,
    facility_id             UUID          REFERENCES Facility(facility_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255)
);

-- Child table: a single payment installment owed under a contract.
-- Installments are permanent financial records — they cannot be deleted and belong to exactly one contract.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Installment (
    installment_id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    installment_type           VARCHAR(50)    NOT NULL,
    due_date                   DATE           NOT NULL,
    installment_status         VARCHAR(50)    NOT NULL,
    amount_expected            DECIMAL(12, 2) NOT NULL,
    amount_expected_currency   VARCHAR(10)    NOT NULL,
    contract_id                UUID           NOT NULL REFERENCES Contract(contract_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- Junction table (@Entity): records every party involved in a contract with their role and signature date.
-- One row per person per role per contract — two people signing one contract produce two rows.
-- Note: each contract must have exactly one Primary Signer — enforced in ContractRules.java and V2__add_constraints.sql.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Contract_Party (
    person_id   UUID        NOT NULL REFERENCES Person(person_id)     ON DELETE RESTRICT,
    contract_id UUID        NOT NULL REFERENCES Contract(contract_id) ON DELETE RESTRICT,
    role        VARCHAR(50) NOT NULL,
    date_signed TIMESTAMPTZ,
    PRIMARY KEY (person_id, contract_id, role),
    -- Audit columns (mapped to BaseEntity)
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- Junction table (@Entity): records which persons physically reside in a unit under a residential contract.
-- One row per person per entry period. The same person can vacate and return under the same contract (move_in_date in PK).
-- Note: the Primary Signer is NOT automatically a resident — a separate row here is required if they occupy the unit.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Person_Resides_Under (
    resident_id            UUID        NOT NULL REFERENCES Person(person_id)     ON DELETE RESTRICT,
    contract_id            UUID        NOT NULL REFERENCES Contract(contract_id) ON DELETE RESTRICT,
    move_in_date           DATE        NOT NULL,
    move_out_date          DATE,
    household_relationship VARCHAR(50) NOT NULL,
    PRIMARY KEY (resident_id, contract_id, move_in_date),
    -- Audit columns (mapped to BaseEntity)
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by             VARCHAR(255),
    updated_by             VARCHAR(255)
);

-- ============================================================================
-- VEHICLE REGISTRY & OWNERSHIP
-- (depends on: Person, Department, Company)
-- ============================================================================

-- Core entity: a vehicle registered in the compound.
-- Owner is exactly one of: Person, Department, or Company — mutual exclusion enforced in VehicleRules.java and V2__add_constraints.sql.
-- Note: license_no is the real-world identifier (printed on plates/documents); vehicle_id is the system PK.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Vehicle (
    vehicle_id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    license_no          VARCHAR(20)  NOT NULL UNIQUE,
    vehicle_model       VARCHAR(100),
    owner_person_id     UUID         REFERENCES Person(person_id)         ON DELETE RESTRICT,
    owner_department_id UUID         REFERENCES Department(department_id) ON DELETE RESTRICT,
    owner_company_id    UUID         REFERENCES Company(company_id)       ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255)
);

-- Owned entity: a maintenance or service request raised by a person — owned by Person (requester), cannot exist without one.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Work_Order (
    work_order_id    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_no    VARCHAR(20)   NOT NULL UNIQUE,
    date_scheduled   DATE,
    date_completed   DATE,
    cost_amount      DECIMAL(12, 2),
    job_status       VARCHAR(50)   NOT NULL,
    description      TEXT,
    priority         VARCHAR(20),
    service_category VARCHAR(50),
    requester_id     UUID          NOT NULL REFERENCES Person(person_id)       ON DELETE RESTRICT,
    facility_id      UUID                   REFERENCES Facility(facility_id)   ON DELETE SET NULL,
    company_id       UUID                   REFERENCES Company(company_id)     ON DELETE SET NULL,
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- ============================================================================
-- ACCESS CONTROL & GATE MANAGEMENT
-- (depends on: Person, Contract, Department, Gate, Vehicle)
-- ============================================================================

-- Owned entity: an access permit (physical card/pass) issued to a Person — owned by Person (permit holder), cannot exist without one.
-- Note: one active permit per person per type — a staff-resident holds one Staff Badge and one Resident Card, not more.
--       Enforced by V2 partial unique index. Permits are revoked by status change, never deleted.
-- Note: each permit must reference exactly one entitlement basis — the record that justifies its issuance:
--       Staff Badge → staff_profile_id | Resident Card → contract_id |
--       Contractor Pass → work_order_id | Visitor Pass → invited_by_id
--       Mutual exclusion enforced in AccessPermitRules.java and V2__add_constraints.sql.
-- Note: issued_by_id is the staff member who physically created the permit in the system (always required).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Access_Permit (
    permit_id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    permit_no        VARCHAR(20) NOT NULL UNIQUE,
    permit_type      VARCHAR(50) NOT NULL,
    access_level     VARCHAR(50),
    permit_status    VARCHAR(50) NOT NULL,
    issue_date       DATE        NOT NULL,
    expiry_date      DATE,
    permit_holder_id UUID        NOT NULL REFERENCES Person(person_id)       ON DELETE RESTRICT,
    -- Entitlement basis — exactly one must be set, matched to permit_type:
    staff_profile_id UUID                 REFERENCES Staff_Profile(person_id) ON DELETE RESTRICT,
    contract_id      UUID                 REFERENCES Contract(contract_id)    ON DELETE RESTRICT,
    work_order_id    UUID                 REFERENCES Work_Order(work_order_id) ON DELETE RESTRICT,
    invited_by_id    UUID                 REFERENCES Person(person_id)         ON DELETE RESTRICT,
    issued_by_id     UUID        NOT NULL REFERENCES Person(person_id)        ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Bridge table: links a Vehicle to an Access_Permit (M:M — purely structural, no extra data or audit needed).
-- Managed as @ManyToMany @JoinTable in Java — no corresponding @Entity class.
CREATE TABLE Vehicle_Permits (
    vehicle_id UUID NOT NULL REFERENCES Vehicle(vehicle_id)          ON DELETE CASCADE,
    permit_id  UUID NOT NULL REFERENCES Access_Permit(permit_id)     ON DELETE CASCADE,
    PRIMARY KEY (vehicle_id, permit_id)
);

-- Junction table: records every gate access event — either a permit scan or an anonymous plate-only log.
-- Full @Entity because each event captures gate, timestamp, direction, and who authorized unregistered entry.
-- Note: permit_id is set for registered permit holders; manual_plate_entry is set for anonymous vehicles (delivery, guests).
--       Exactly one must be provided — enforced in EntersAtRules.java and V2__add_constraints.sql.
-- Note: processed_by_id is the guard who processed the gate event (required for anonymous entries, optional for permit scans).
-- Note: requested_by_id is the resident or staff the anonymous visitor is coming to see (optional — logged by the guard).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Enters_At (
    entry_id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    gate_id            UUID         NOT NULL REFERENCES Gate(gate_id)               ON DELETE RESTRICT,
    permit_id          UUID                  REFERENCES Access_Permit(permit_id)    ON DELETE SET NULL,
    manual_plate_entry VARCHAR(20),
    entered_at         TIMESTAMPTZ  NOT NULL,
    direction          VARCHAR(10)  NOT NULL,
    purpose            VARCHAR(100),
    processed_by_id    UUID         REFERENCES Person(person_id) ON DELETE RESTRICT,
    requested_by_id    UUID         REFERENCES Person(person_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- ============================================================================
-- MAINTENANCE & WORK ORDERS
-- (depends on: Person, Facility, Company)
-- ============================================================================

-- Junction table: records which company was assigned to a work order (M:M with assignment date).
-- Full @Entity because the assignment date is tracked as extra data.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Work_Order_Assigned_To (
    work_order_id UUID        NOT NULL REFERENCES Work_Order(work_order_id) ON DELETE CASCADE,
    company_id    UUID        NOT NULL REFERENCES Company(company_id)       ON DELETE RESTRICT,
    date_assigned DATE        NOT NULL,
    PRIMARY KEY (work_order_id, company_id),
    -- Audit columns (mapped to BaseEntity)
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

-- ============================================================================
-- HUMAN RESOURCES & ATTENDANCE
-- (depends on: Person, Position, Department, Shift_Attendance_Type, Task)
-- ============================================================================

-- Core entity: represents a job application — the root of the entire hiring pipeline.
-- Recruitment (interviews) and Hire_Agreement (offer terms) both hang off this entity.
-- Note: application status is DERIVED — if Recruitment rows exist → "Interviewing"; if Hire_Agreement exists → "Hired";
--       neither after a business-defined period → implicitly rejected. No status column stored here.
-- Note: composite PK allows the same person to re-apply for the same position at a later date.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Applications (
    applicant_id     UUID NOT NULL REFERENCES Person(person_id)         ON DELETE CASCADE,
    position_id      UUID NOT NULL REFERENCES Staff_Position(position_id) ON DELETE RESTRICT,
    application_date DATE NOT NULL,
    PRIMARY KEY (applicant_id, position_id, application_date),
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Child table (of Applications): records individual interview events per application.
-- One application can have MANY interviews (different interviewers, different dates).
-- Note: interview_result represents the outcome of THIS specific interview, not the final hiring decision.
--       The overall hiring decision is represented by the existence of a Hire_Agreement row.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Recruitment (
    interviewer_id   UUID NOT NULL REFERENCES Person(person_id)                               ON DELETE RESTRICT,
    applicant_id     UUID NOT NULL,
    position_id      UUID NOT NULL,
    application_date DATE NOT NULL,
    interview_date   DATE NOT NULL,
    interview_result VARCHAR(20),
    PRIMARY KEY (interviewer_id, applicant_id, position_id, application_date, interview_date),
    FOREIGN KEY (applicant_id, position_id, application_date)
        REFERENCES Applications(applicant_id, position_id, application_date) ON DELETE CASCADE,
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Detail table (1:1 extension of Applications): records the formal hire terms agreed with a successful applicant.
-- 1:1 with Applications — only successful applicants get a row; rejected applicants have no row here.
-- Note: this is the final, decided outcome of the entire interview process (not one specific interview).
--       The offered_base_daily_rate and offered_maximum_salary are the contractually agreed individual terms.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Hire_Agreement (
    applicant_id            UUID           NOT NULL,
    position_id             UUID           NOT NULL,
    application_date        DATE           NOT NULL,
    employment_start_date   DATE,
    offered_maximum_salary  DECIMAL(12, 2),
    offered_base_daily_rate DECIMAL(8,  2) NOT NULL,
    PRIMARY KEY (applicant_id, position_id, application_date),
    FOREIGN KEY (applicant_id, position_id, application_date)
        REFERENCES Applications(applicant_id, position_id, application_date) ON DELETE CASCADE,
    -- Audit columns (mapped to BaseEntity)
    created_at              TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255)
);

-- Junction table: records daily duty assignments for staff — which task and shift type they are scheduled for each day.
-- Full @Entity because assignments are operational records with a defined date and auditable changes.

-- Note: assignment_id is a surrogate PK so Gate_Guard_Assignment can FK to this record for gate-type duties.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Assigned_Task (
    assignment_id    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id         UUID         NOT NULL REFERENCES Person(person_id)                ON DELETE RESTRICT,
    task_id          UUID         NOT NULL REFERENCES Task(task_id)                    ON DELETE RESTRICT,
    shift_id         UUID         NOT NULL REFERENCES Shift_Attendance_Type(shift_id)  ON DELETE RESTRICT,
    assignment_date  DATE         NOT NULL,
    duty_description VARCHAR(200),
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    UNIQUE (staff_id, task_id, assignment_date)
);

-- Junction table: records actual daily attendance for a staff member on a given shift (actual vs. Assigned_Task scheduled).
-- Full @Entity because attendance records are financial/payroll snapshots — locked once payroll is processed.
-- Note: daily_salary, daily_bonus, daily_deduction, daily_net_salary are computed snapshots stored for auditability.
--       Formula: daily_salary = base_daily_rate × (diff_hour / expected_hours) from Law_of_Shift_Attendance.
--       Once payroll is run, these values are frozen — do not recalculate from current rates.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Attends (
    staff_id         UUID           NOT NULL REFERENCES Person(person_id)                ON DELETE RESTRICT,
    shift_id         UUID           NOT NULL REFERENCES Shift_Attendance_Type(shift_id)  ON DELETE RESTRICT,
    date             DATE           NOT NULL,
    is_absent        BOOLEAN        NOT NULL DEFAULT FALSE,
    check_in_time    TIME,
    check_out_time   TIME,
    period_out_in    VARCHAR(50),
    diff_hour        DECIMAL(5, 2),
    daily_bonus      DECIMAL(10, 2),
    daily_deduction  DECIMAL(10, 2),
    daily_salary     DECIMAL(10, 2),
    daily_net_salary DECIMAL(10, 2),
    PRIMARY KEY (staff_id, shift_id, date),
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Detail table: monthly payroll rollup for a staff member in a specific department.
-- PK includes department_id — a staff member may work across multiple departments in the same month, each getting a separate row.
-- Note: monthly_salary, monthly_bonus, monthly_deduction, tax, monthly_net_salary are payroll snapshots.
--       Aggregated from Attends daily records. Do not recalculate after month is closed.
--       monthly_net_salary must not exceed Staff_Salary_History.maximum_salary — enforced in PayrollRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Task_Monthly_Salary (
    staff_id           UUID           NOT NULL REFERENCES Person(person_id)               ON DELETE RESTRICT,
    department_id      UUID           NOT NULL REFERENCES Department(department_id)        ON DELETE RESTRICT,
    shift_id           UUID           NOT NULL REFERENCES Shift_Attendance_Type(shift_id)  ON DELETE RESTRICT,
    year               INT            NOT NULL,
    month              INT            NOT NULL,
    monthly_deduction  DECIMAL(12, 2),
    monthly_bonus      DECIMAL(12, 2),
    tax                DECIMAL(12, 2),
    monthly_salary     DECIMAL(12, 2),
    monthly_net_salary DECIMAL(12, 2),
    PRIMARY KEY (staff_id, department_id, year, month),
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- ============================================================================
-- COMPENSATION HISTORY & KPI
-- (depends on: Person, Department, Staff_Performance_Review)
-- ============================================================================

-- Detail-History table: tracks individual staff compensation changes over time (SCD Type 2).
-- Hire_Agreement.offered_base_daily_rate captures the starting rate at hire; each raise produces a new row here.
-- end_date IS NULL = currently active rate.  ORDER BY effective_date DESC LIMIT 1 = current rate.
-- Note: approved_by_id is the manager who authorized this raise — required for non-initial rows.
--       review_id is the performance review that triggered this change (NULL for the initial hire rate row).
--       Enforced in StaffSalaryRules.java (@Transactional).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_Salary_History (
    staff_id        UUID           NOT NULL REFERENCES Person(person_id)              ON DELETE RESTRICT,
    effective_date  DATE           NOT NULL,
    end_date        DATE,
    base_daily_rate DECIMAL(8,2)   NOT NULL,
    maximum_salary  DECIMAL(12,2)  NOT NULL,
    approved_by_id  UUID                    REFERENCES Person(person_id)             ON DELETE RESTRICT,
    review_id       UUID,
    PRIMARY KEY (staff_id, effective_date),
    -- Audit columns (mapped to BaseEntity)
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

-- Catalog table (time-bounded): defines KPI scoring tiers and their payroll impact per department.
-- A manager defines and approves this policy — it governs how Staff_KPI_Record scores map to bonuses and deductions.
-- Mirrors Law_of_Shift_Attendance: a dated rule, not a static enum.
-- Note: min_kpi_score and max_kpi_score define the tier range (inclusive). Tiers must not overlap within a department.
--       Overlap enforcement is a business rule in KpiPolicyRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE KPI_Policy (
    kpi_policy_id   UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id   UUID           NOT NULL REFERENCES Department(department_id)  ON DELETE RESTRICT,
    effective_date  DATE           NOT NULL,
    tier_label      VARCHAR(50)    NOT NULL,
    min_kpi_score   DECIMAL(5,2)   NOT NULL,
    max_kpi_score   DECIMAL(5,2)   NOT NULL,
    bonus_rate      DECIMAL(5,4)   NOT NULL DEFAULT 0,
    deduction_rate  DECIMAL(5,4)   NOT NULL DEFAULT 0,
    approved_by_id  UUID           NOT NULL REFERENCES Person(person_id)          ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

-- Junction table: records the daily KPI score assessed by a manager for a staff member in a department.
-- Daily granularity: scores accumulate per day and are rolled up into Staff_KPI_Monthly_Summary at month-end.
-- Full @Entity because KPI records are audited evaluations with a manager approval trail.
-- Note: kpi_policy_id links to the active policy tier for this department on this date — drives bonus/deduction.
--       recorded_by_id is the manager who assessed and approved this score.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_KPI_Record (
    staff_id        UUID           NOT NULL REFERENCES Person(person_id)            ON DELETE RESTRICT,
    department_id   UUID           NOT NULL REFERENCES Department(department_id)    ON DELETE RESTRICT,
    record_date     DATE           NOT NULL,
    kpi_score       DECIMAL(5,2)   NOT NULL,
    kpi_policy_id   UUID           NOT NULL REFERENCES KPI_Policy(kpi_policy_id)   ON DELETE RESTRICT,
    recorded_by_id  UUID           NOT NULL REFERENCES Person(person_id)            ON DELETE RESTRICT,
    notes           TEXT,
    PRIMARY KEY (staff_id, department_id, record_date),
    -- Audit columns (mapped to BaseEntity)
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

-- Junction table: month-end rollup of daily Staff_KPI_Record scores per staff per department (M:M — staff × department × month).
-- Generated at month-end by the backend — aggregates daily scores so past-month queries don't re-scan daily records.
-- Note: avg_kpi_score and total_score are snapshots — do not recalculate from Staff_KPI_Record after month is closed.
--       applicable_tier and payroll_bonus_rate are copied from the matching KPI_Policy at close time.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_KPI_Monthly_Summary (
    staff_id            UUID           NOT NULL REFERENCES Person(person_id)         ON DELETE RESTRICT,
    department_id       UUID           NOT NULL REFERENCES Department(department_id) ON DELETE RESTRICT,
    year                INT            NOT NULL,
    month               INT            NOT NULL,
    avg_kpi_score       DECIMAL(5,2)   NOT NULL,
    total_kpi_score     DECIMAL(8,2)   NOT NULL,
    days_scored         INT            NOT NULL,
    applicable_tier     VARCHAR(50),
    payroll_bonus_rate  DECIMAL(5,4),
    payroll_deduct_rate DECIMAL(5,4),
    kpi_policy_id       UUID           REFERENCES KPI_Policy(kpi_policy_id)         ON DELETE RESTRICT,
    closed_by_id        UUID           NOT NULL REFERENCES Person(person_id)         ON DELETE RESTRICT,
    PRIMARY KEY (staff_id, department_id, year, month),
    -- Audit columns (mapped to BaseEntity)
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255)
);

-- Owned entity: a formal periodic performance review of a staff member — owned by Person (staff), cannot exist without one.
-- Note: resulted_in_promotion = TRUE → backend must create a new Staff_Position_History row in the same @Transactional call.
--       resulted_in_raise = TRUE → backend must create a new Staff_Salary_History row (review_id = this review).
--       Both enforced in StaffPerformanceRules.java (@Transactional).
-- Note: reviewer_id must be the direct manager of the staff being reviewed — enforced in StaffPerformanceRules.java.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Staff_Performance_Review (
    review_id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id              UUID         NOT NULL REFERENCES Person(person_id)         ON DELETE RESTRICT,
    reviewer_id           UUID         NOT NULL REFERENCES Person(person_id)         ON DELETE RESTRICT,
    department_id         UUID         NOT NULL REFERENCES Department(department_id) ON DELETE RESTRICT,
    review_date           DATE         NOT NULL,
    overall_kpi_score     DECIMAL(5,2),
    overall_rating        VARCHAR(20),
    notes                 TEXT,
    resulted_in_promotion BOOLEAN      NOT NULL DEFAULT FALSE,
    resulted_in_raise     BOOLEAN      NOT NULL DEFAULT FALSE,
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- ============================================================================
-- GATE SECURITY & GUARD ASSIGNMENT
-- (depends on: Person, Gate, Shift_Attendance_Type)
-- ============================================================================

-- Junction table: records which guard was posted at which gate during a specific shift period.
-- Full @Entity because guard assignments are time-bounded operational records with an auditable start/end.
-- Note: task_assignment_id is NOT NULL — HR must create the Assigned_Task record first (even in emergencies),
--       then register the gate posting. This ensures every gate posting is formally authorized and auditable.
-- Note: this table is the bridge between Enters_At.processed_by_id and Attends — it answers
--       "who was on duty at gate X when this entry event was logged?"
-- Note: shift_end NULL = shift still active (guard not yet clocked out).
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Gate_Guard_Assignment (
    guard_post_id      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    guard_id           UUID        NOT NULL REFERENCES Person(person_id)               ON DELETE RESTRICT,
    gate_id            UUID        NOT NULL REFERENCES Gate(gate_id)                   ON DELETE RESTRICT,
    task_assignment_id UUID        NOT NULL REFERENCES Assigned_Task(assignment_id)    ON DELETE RESTRICT,
    shift_type_id      UUID                 REFERENCES Shift_Attendance_Type(shift_id) ON DELETE RESTRICT,
    shift_start        TIMESTAMPTZ NOT NULL,
    shift_end          TIMESTAMPTZ,
    -- Audit columns (mapped to BaseEntity)
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

-- ============================================================================
-- FINANCIAL & PAYMENTS
-- (depends on: Installment, Work_Order, Bank_Account, Person, Department, Task_Monthly_Salary)
-- ============================================================================

-- Core entity: the central ledger record for any financial transaction in the system.
-- Specialized by payment_type — exactly one child row (Installment_Payment, Work_Order_Payment, or Payroll_Payment) exists per row.
-- Note: the one-child-per-payment rule is enforced in PaymentRules.java (@Transactional).
--       payment_type discriminator enables type-specific queries without JOINing all three child tables.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Payment (
    payment_id            UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_no            VARCHAR(20)    NOT NULL UNIQUE,
    paid_at               TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    amount                DECIMAL(12, 2) NOT NULL,
    currency              VARCHAR(10)    NOT NULL,
    payment_type          VARCHAR(20)    NOT NULL,
    method                VARCHAR(50),
    direction             VARCHAR(20)    NOT NULL,
    reference_no          VARCHAR(100),
    reconciliation_status VARCHAR(50)    NOT NULL DEFAULT 'Pending',
    bank_account_id       UUID           NOT NULL REFERENCES Bank_Account(account_id)  ON DELETE RESTRICT,
    processed_by_id       UUID           REFERENCES Person(person_id)                  ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- Detail table (1:1 extension of Payment): records an inbound payment by a resident against a contract installment.
-- Note: payer is derivable via installment_id → Installment → Contract → Contract_Party — not stored redundantly here.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Installment_Payment (
    payment_id        UUID           PRIMARY KEY REFERENCES Payment(payment_id)      ON DELETE CASCADE,
    installment_id    UUID           NOT NULL REFERENCES Installment(installment_id) ON DELETE RESTRICT,
    late_fee_amount   DECIMAL(12, 2),
    late_fee_currency VARCHAR(10),
    -- Audit columns (mapped to BaseEntity)
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- Detail table (1:1 extension of Payment): records an outbound payment to a vendor for a work order.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Work_Order_Payment (
    payment_id     UUID           PRIMARY KEY REFERENCES Payment(payment_id)        ON DELETE CASCADE,
    work_order_id  UUID           NOT NULL REFERENCES Work_Order(work_order_id)     ON DELETE RESTRICT,
    invoice_no     VARCHAR(100),
    -- Audit columns (mapped to BaseEntity)
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255)
);

-- Detail table (1:1 extension of Payment): records an outbound payroll payment to a staff member for a specific month.
-- Note: (staff_id, department_id, year, month) forms a composite FK to Task_Monthly_Salary — the payroll calculation source.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Payroll_Payment (
    payment_id    UUID  PRIMARY KEY REFERENCES Payment(payment_id)                  ON DELETE CASCADE,
    staff_id      UUID  NOT NULL REFERENCES Person(person_id)                       ON DELETE RESTRICT,
    department_id UUID  NOT NULL REFERENCES Department(department_id)               ON DELETE RESTRICT,
    year          INT   NOT NULL,
    month         INT   NOT NULL,
    FOREIGN KEY (staff_id, department_id, year, month)
        REFERENCES Task_Monthly_Salary(staff_id, department_id, year, month)        ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);
