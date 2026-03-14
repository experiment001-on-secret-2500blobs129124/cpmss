-- =============================================================================
-- CPMSS: Full Database Schema
-- V1: All core tables for the Compound Management System
-- =============================================================================

-- ========================
-- 1. COMPOUND
-- ========================
CREATE TABLE compound (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    country         VARCHAR(100),
    city            VARCHAR(100),
    district        VARCHAR(100),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 2. PERSON
-- ========================
CREATE TABLE person (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    national_id     VARCHAR(50) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    nationality     VARCHAR(100),
    phone1_country  VARCHAR(10),
    phone1_number   VARCHAR(30),
    phone2_country  VARCHAR(10),
    phone2_number   VARCHAR(30),
    email1          VARCHAR(255),
    email2          VARCHAR(255),
    city            VARCHAR(100),
    street          VARCHAR(255),
    date_of_birth   DATE,
    gender          VARCHAR(20),
    person_type     VARCHAR(20) NOT NULL DEFAULT 'Visitor'
                    CHECK (person_type IN ('Staff', 'Investor', 'Tenant', 'Visitor')),
    qualification   VARCHAR(255),
    is_blacklisted  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_person_type ON person(person_type);
CREATE INDEX idx_person_blacklisted ON person(is_blacklisted);

-- ========================
-- 3. BUILDING
-- ========================
CREATE TABLE building (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    compound_id     UUID NOT NULL REFERENCES compound(id),
    building_name   VARCHAR(255) NOT NULL,
    building_number VARCHAR(50),
    building_type   VARCHAR(20) NOT NULL
                    CHECK (building_type IN ('Residential', 'Non-Residential')),
    floors_count    INTEGER,
    construction_date DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_building_compound ON building(compound_id);
CREATE INDEX idx_building_type ON building(building_type);

-- ========================
-- 4. UNIT
-- ========================
CREATE TABLE unit (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    building_id         UUID NOT NULL REFERENCES building(id),
    unit_number         VARCHAR(50) NOT NULL,
    floor_number        INTEGER,
    bedrooms            INTEGER DEFAULT 0,
    bathrooms           INTEGER DEFAULT 0,
    rooms               INTEGER DEFAULT 0,
    square_footage      DECIMAL(10, 2),
    balconies           INTEGER DEFAULT 0,
    view_orientation    VARCHAR(50),
    current_status      VARCHAR(30) NOT NULL DEFAULT 'Vacant'
                        CHECK (current_status IN ('Vacant', 'Occupied', 'Reserved', 'Under Maintenance')),
    listing_price       DECIMAL(12, 2),
    water_meter_code    VARCHAR(100),
    gas_meter_code      VARCHAR(100),
    electricity_meter_code VARCHAR(100),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_unit_building ON unit(building_id);
CREATE INDEX idx_unit_status ON unit(current_status);

-- ========================
-- 5. UNIT PRICING HISTORY
-- ========================
CREATE TABLE unit_pricing_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id         UUID NOT NULL REFERENCES unit(id),
    effective_date  DATE NOT NULL,
    listing_price   DECIMAL(12, 2) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (unit_id, effective_date)
);

-- ========================
-- 6. UNIT STATUS HISTORY
-- ========================
CREATE TABLE unit_status_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id         UUID NOT NULL REFERENCES unit(id),
    status_date     DATE NOT NULL,
    unit_status     VARCHAR(30) NOT NULL
                    CHECK (unit_status IN ('Vacant', 'Occupied', 'Reserved', 'Under Maintenance')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (unit_id, status_date)
);

-- ========================
-- 7. FACILITY
-- ========================
CREATE TABLE facility (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    building_id     UUID NOT NULL REFERENCES building(id),
    facility_name   VARCHAR(255) NOT NULL,
    management_type VARCHAR(50)
                    CHECK (management_type IN ('Self-Managed', 'Third-Party Operated')),
    facility_category VARCHAR(50)
                    CHECK (facility_category IN ('Recreation', 'Retail', 'Service', 'Common Area')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_facility_building ON facility(building_id);

-- ========================
-- 8. FACILITY HOURS HISTORY
-- ========================
CREATE TABLE facility_hours_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_id     UUID NOT NULL REFERENCES facility(id),
    effective_date  DATE NOT NULL,
    opening_time    TIME,
    closing_time    TIME,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (facility_id, effective_date)
);

-- ========================
-- 9. FACILITY MANAGER
-- ========================
CREATE TABLE facility_manager (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_id             UUID NOT NULL REFERENCES facility(id),
    manager_national_id     UUID NOT NULL REFERENCES person(id),
    management_start_date   DATE NOT NULL,
    management_end_date     DATE,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fac_mgr_facility ON facility_manager(facility_id);

-- ========================
-- 10. DEPARTMENT
-- ========================
CREATE TABLE department (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_name VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 11. DEPARTMENT LOCATION HISTORY
-- ========================
CREATE TABLE department_location_history (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id               UUID NOT NULL REFERENCES department(id),
    building_id                 UUID NOT NULL REFERENCES building(id),
    location_start_date         DATE NOT NULL,
    location_end_date           DATE,
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 12. DEPARTMENT MANAGER
-- ========================
CREATE TABLE department_manager (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id           UUID NOT NULL REFERENCES department(id),
    manager_id              UUID NOT NULL REFERENCES person(id),
    management_start_date   DATE NOT NULL,
    management_end_date     DATE,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 13. POSITION
-- ========================
CREATE TABLE position (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id   UUID NOT NULL REFERENCES department(id),
    position_name   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_position_department ON position(department_id);

-- ========================
-- 14. POSITION SALARY HISTORY
-- ========================
CREATE TABLE position_salary_history (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    position_id         UUID NOT NULL REFERENCES position(id),
    salary_effective_date DATE NOT NULL,
    maximum_salary      DECIMAL(12, 2) NOT NULL,
    base_hourly_rate    DECIMAL(10, 2) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (position_id, salary_effective_date)
);

-- ========================
-- 15. SHIFT ATTENDANCE TYPE
-- ========================
CREATE TABLE shift_attendance_type (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shift_name  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 16. LAW OF SHIFT ATTENDANCE
-- ========================
CREATE TABLE law_of_shift_attendance (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shift_id                    UUID NOT NULL REFERENCES shift_attendance_type(id),
    effective_date              DATE NOT NULL,
    start_time                  TIME NOT NULL,
    end_time                    TIME NOT NULL,
    one_hour_extra_bonus        DECIMAL(10, 2) DEFAULT 0,
    one_hour_difference_discount DECIMAL(10, 2) DEFAULT 0,
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (shift_id, effective_date)
);

-- ========================
-- 17. TASK
-- ========================
CREATE TABLE task (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_title  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 18. ASSIGNED TASK
-- ========================
CREATE TABLE assigned_task (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id            UUID NOT NULL REFERENCES person(id),
    task_id             UUID NOT NULL REFERENCES task(id),
    department_id       UUID NOT NULL REFERENCES department(id),
    shift_id            UUID NOT NULL REFERENCES shift_attendance_type(id),
    assignment_year     INTEGER NOT NULL,
    assignment_month    INTEGER NOT NULL,
    assignment_day      INTEGER NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (staff_id, task_id, assignment_year, assignment_month, assignment_day)
);

-- ========================
-- 19. ATTENDS (Daily Attendance)
-- ========================
CREATE TABLE attends (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id            UUID NOT NULL REFERENCES person(id),
    shift_id            UUID NOT NULL REFERENCES shift_attendance_type(id),
    attendance_date     DATE NOT NULL,
    check_in_time       TIME,
    check_out_time      TIME,
    is_absent           BOOLEAN DEFAULT FALSE,
    period_out_in       DECIMAL(5, 2),
    diff_hour           DECIMAL(5, 2),
    daily_salary        DECIMAL(10, 2),
    daily_bonus         DECIMAL(10, 2) DEFAULT 0,
    daily_deduction     DECIMAL(10, 2) DEFAULT 0,
    daily_net_salary    DECIMAL(10, 2),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (staff_id, shift_id, attendance_date)
);

CREATE INDEX idx_attends_date ON attends(attendance_date);

-- ========================
-- 20. COMPANY (Vendor)
-- ========================
CREATE TABLE company (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name    VARCHAR(255) NOT NULL,
    tax_id          VARCHAR(100),
    phone           VARCHAR(50),
    company_type    VARCHAR(50)
                    CHECK (company_type IN ('Maintenance Contractor', 'Supplier', 'Commercial Tenant')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 21. PERSON WORKS FOR COMPANY
-- ========================
CREATE TABLE person_works_for_company (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    person_id   UUID NOT NULL REFERENCES person(id),
    company_id  UUID NOT NULL REFERENCES company(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (person_id, company_id)
);

-- ========================
-- 22. VEHICLE
-- ========================
CREATE TABLE vehicle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    license_number  VARCHAR(50) NOT NULL UNIQUE,
    vehicle_model   VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 23. PERSON VEHICLES
-- ========================
CREATE TABLE person_vehicles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id  UUID NOT NULL UNIQUE REFERENCES vehicle(id),
    person_id   UUID NOT NULL REFERENCES person(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 24. DEPARTMENT VEHICLES
-- ========================
CREATE TABLE department_vehicles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id      UUID NOT NULL UNIQUE REFERENCES vehicle(id),
    department_id   UUID NOT NULL REFERENCES department(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 25. COMPANY VEHICLES
-- ========================
CREATE TABLE company_vehicles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id  UUID NOT NULL UNIQUE REFERENCES vehicle(id),
    company_id  UUID NOT NULL REFERENCES company(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 26. PERSON INVESTS IN COMPOUND
-- ========================
CREATE TABLE person_invests_in_compound (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investor_id         UUID NOT NULL REFERENCES person(id),
    compound_id         UUID NOT NULL REFERENCES compound(id),
    investment_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stock               DECIMAL(10, 4),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_investment_investor ON person_invests_in_compound(investor_id);

-- ========================
-- 27. ACCESS PERMIT
-- ========================
CREATE TABLE access_permit (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    holder_id       UUID REFERENCES person(id),
    access_level    VARCHAR(50)
                    CHECK (access_level IN ('Full Access', 'Restricted Areas', 'Common Areas Only')),
    status          VARCHAR(20) NOT NULL DEFAULT 'Active'
                    CHECK (status IN ('Active', 'Expired', 'Suspended', 'Revoked')),
    issue_date      DATE NOT NULL,
    expiry_date     DATE,
    permit_type     VARCHAR(50)
                    CHECK (permit_type IN ('Staff Badge', 'Resident Card', 'Vehicle Sticker', 'Visitor Pass')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_permit_holder ON access_permit(holder_id);
CREATE INDEX idx_permit_status ON access_permit(status);

-- ========================
-- 28. VEHICLE PERMIT (M:N)
-- ========================
CREATE TABLE vehicle_permit (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id  UUID NOT NULL REFERENCES vehicle(id),
    permit_id   UUID NOT NULL REFERENCES access_permit(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (vehicle_id, permit_id)
);

-- ========================
-- 29. GATE
-- ========================
CREATE TABLE gate (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    compound_id UUID NOT NULL REFERENCES compound(id),
    gate_name   VARCHAR(255) NOT NULL,
    gate_type   VARCHAR(20)
                CHECK (gate_type IN ('Pedestrian', 'Vehicle', 'Combined')),
    status      VARCHAR(20) NOT NULL DEFAULT 'Active'
                CHECK (status IN ('Active', 'Closed')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 30. ENTRY LOG (Enters At)
-- ========================
CREATE TABLE entry_log (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    permit_id           UUID NOT NULL REFERENCES access_permit(id),
    gate_id             UUID NOT NULL REFERENCES gate(id),
    entry_timestamp     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    direction           VARCHAR(10) NOT NULL
                        CHECK (direction IN ('In', 'Out')),
    purpose             VARCHAR(100),
    manual_plate_entry  VARCHAR(50),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_entry_log_timestamp ON entry_log(entry_timestamp);
CREATE INDEX idx_entry_log_permit ON entry_log(permit_id);
CREATE INDEX idx_entry_log_gate ON entry_log(gate_id);

-- ========================
-- 31. CONTRACT
-- ========================
CREATE TABLE contract (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    contract_type       VARCHAR(50) NOT NULL
                        CHECK (contract_type IN ('Residential Lease', 'Commercial Lease', 'Service Agreement')),
    contract_status     VARCHAR(20) NOT NULL DEFAULT 'Draft'
                        CHECK (contract_status IN ('Draft', 'Active', 'Terminated', 'Expired')),
    payment_frequency   VARCHAR(20)
                        CHECK (payment_frequency IN ('Monthly', 'Quarterly', 'Semi-Annually', 'Annually')),
    final_price         DECIMAL(14, 2) NOT NULL,
    security_deposit    DECIMAL(12, 2) DEFAULT 0,
    renewal_terms       TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_status ON contract(contract_status);
CREATE INDEX idx_contract_type ON contract(contract_type);

-- ========================
-- 32. PERSON PARTIES TO CONTRACT (Tenant)
-- ========================
CREATE TABLE person_contract (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    person_id       UUID NOT NULL REFERENCES person(id),
    contract_id     UUID NOT NULL REFERENCES contract(id),
    role            VARCHAR(50) NOT NULL
                    CHECK (role IN ('Primary Signer', 'Guarantor', 'Corporate Representative', 'Emergency Contact')),
    date_signed     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (person_id, contract_id, role)
);

-- ========================
-- 33. STAFF PARTIES TO CONTRACT FACILITY
-- ========================
CREATE TABLE staff_contract_facility (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id            UUID NOT NULL REFERENCES person(id),
    company_rep_id      UUID NOT NULL REFERENCES person(id),
    contract_id         UUID NOT NULL REFERENCES contract(id),
    date_signed         TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 34. RESIDES UNDER
-- ========================
CREATE TABLE resides_under (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    person_id               UUID NOT NULL REFERENCES person(id),
    contract_id             UUID NOT NULL REFERENCES contract(id),
    move_in_date            DATE NOT NULL,
    move_out_date           DATE,
    relationship_to_signer  VARCHAR(50)
                            CHECK (relationship_to_signer IN ('Self', 'Spouse', 'Child', 'Parent', 'Roommate')),
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resides_contract ON resides_under(contract_id);

-- ========================
-- 35. CONTRACT UNIT
-- ========================
CREATE TABLE contract_unit (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id     UUID NOT NULL REFERENCES contract(id),
    unit_id         UUID NOT NULL REFERENCES unit(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_unit_contract ON contract_unit(contract_id);
CREATE INDEX idx_contract_unit_unit ON contract_unit(unit_id);

-- ========================
-- 36. CONTRACT FACILITY
-- ========================
CREATE TABLE contract_facility (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id     UUID NOT NULL REFERENCES contract(id),
    facility_id     UUID NOT NULL REFERENCES facility(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 37. BANK ACCOUNT
-- ========================
CREATE TABLE bank_account (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_name   VARCHAR(255) NOT NULL,
    iban        VARCHAR(50),
    swift_code  VARCHAR(20),
    is_primary  BOOLEAN DEFAULT FALSE,
    owner_type  VARCHAR(20)
                CHECK (owner_type IN ('Person', 'Company', 'Department')),
    person_id   UUID REFERENCES person(id),
    company_id  UUID REFERENCES company(id),
    department_id UUID REFERENCES department(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 38. INSTALLMENT
-- ========================
CREATE TABLE installment (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id     UUID NOT NULL REFERENCES contract(id),
    due_date        DATE NOT NULL,
    amount_expected DECIMAL(12, 2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'Pending'
                    CHECK (status IN ('Pending', 'Partially Paid', 'Paid', 'Overdue', 'Cancelled')),
    installment_type VARCHAR(50)
                    CHECK (installment_type IN ('Security Deposit', 'Monthly Rent', 'Quarterly Rent', 'Final Payment', 'Adjustment')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_installment_contract ON installment(contract_id);
CREATE INDEX idx_installment_status ON installment(status);
CREATE INDEX idx_installment_due ON installment(due_date);

-- ========================
-- 39. PAYMENT
-- ========================
CREATE TABLE payment (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_date            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount                  DECIMAL(12, 2) NOT NULL,
    method                  VARCHAR(30)
                            CHECK (method IN ('Bank Transfer', 'Cash', 'Check', 'Credit Card', 'Wire Transfer')),
    direction               VARCHAR(10) NOT NULL
                            CHECK (direction IN ('Inbound', 'Outbound')),
    reference_number        VARCHAR(100),
    reconciliation_status   VARCHAR(20) NOT NULL DEFAULT 'Pending'
                            CHECK (reconciliation_status IN ('Pending', 'Reconciled', 'Disputed')),
    currency                VARCHAR(10) DEFAULT 'USD',
    bank_account_id         UUID NOT NULL REFERENCES bank_account(id),
    person_id               UUID REFERENCES person(id),
    installment_id          UUID REFERENCES installment(id),
    work_order_id           UUID,  -- FK added after work_order table
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_direction ON payment(direction);
CREATE INDEX idx_payment_reconciliation ON payment(reconciliation_status);
CREATE INDEX idx_payment_installment ON payment(installment_id);

-- ========================
-- 40. WORK ORDER
-- ========================
CREATE TABLE work_order (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requested_by_id     UUID NOT NULL REFERENCES person(id),
    assigned_company_id UUID REFERENCES company(id),
    target_facility_id  UUID REFERENCES facility(id),
    target_unit_id      UUID REFERENCES unit(id),
    date_scheduled      DATE,
    date_completed      DATE,
    date_assigned       DATE,
    cost_amount         DECIMAL(12, 2),
    job_status          VARCHAR(20) NOT NULL DEFAULT 'Pending'
                        CHECK (job_status IN ('Pending', 'Assigned', 'In Progress', 'Completed', 'Paid', 'Cancelled')),
    description         TEXT,
    priority            VARCHAR(20) DEFAULT 'Normal'
                        CHECK (priority IN ('Low', 'Normal', 'High', 'Emergency')),
    service_category    VARCHAR(50)
                        CHECK (service_category IN ('Plumbing', 'Electrical', 'HVAC', 'Landscaping', 'Cleaning', 'Security', 'General')),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_work_order_status ON work_order(job_status);
CREATE INDEX idx_work_order_priority ON work_order(priority);

-- Add FK from payment to work_order now that it exists
ALTER TABLE payment ADD CONSTRAINT fk_payment_work_order
    FOREIGN KEY (work_order_id) REFERENCES work_order(id);

-- ========================
-- 41. HR: APPLICATION
-- ========================
CREATE TABLE application (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id        UUID NOT NULL REFERENCES person(id),
    position_id         UUID NOT NULL REFERENCES position(id),
    application_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (applicant_id, position_id, application_date)
);

-- ========================
-- 42. HR: RECRUITMENT (Interview)
-- ========================
CREATE TABLE recruitment (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id      UUID NOT NULL REFERENCES application(id),
    interviewer_id      UUID NOT NULL REFERENCES person(id),
    interview_date      DATE NOT NULL,
    interview_result    VARCHAR(20)
                        CHECK (interview_result IN ('Pass', 'Fail', 'Pending')),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 43. HR: EMPLOYMENT OFFER
-- ========================
CREATE TABLE employment_offer (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id          UUID NOT NULL UNIQUE REFERENCES application(id),
    offered_maximum_salary  DECIMAL(12, 2) NOT NULL,
    offered_hourly_rate     DECIMAL(10, 2) NOT NULL,
    employment_start_date   DATE NOT NULL,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 44. HR: TASK MONTHLY SALARY
-- ========================
CREATE TABLE task_monthly_salary (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id            UUID NOT NULL REFERENCES person(id),
    department_id       UUID NOT NULL REFERENCES department(id),
    shift_id            UUID NOT NULL REFERENCES shift_attendance_type(id),
    task_id             UUID NOT NULL REFERENCES task(id),
    salary_year         INTEGER NOT NULL,
    salary_month        INTEGER NOT NULL,
    monthly_salary      DECIMAL(12, 2),
    monthly_bonus       DECIMAL(12, 2) DEFAULT 0,
    monthly_deduction   DECIMAL(12, 2) DEFAULT 0,
    tax                 DECIMAL(12, 2) DEFAULT 0,
    monthly_net_salary  DECIMAL(12, 2),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (staff_id, salary_year, salary_month)
);

-- ========================
-- 45. SUPERVISION
-- ========================
CREATE TABLE supervision (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supervisor_id           UUID NOT NULL REFERENCES person(id),
    supervisee_id           UUID NOT NULL REFERENCES person(id),
    supervision_start_date  DATE NOT NULL,
    supervision_end_date    DATE,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- 46. AUTH: APP ROLE
-- ========================
CREATE TABLE app_role (
    id          VARCHAR(50) PRIMARY KEY,
    role_name   VARCHAR(100) NOT NULL,
    description TEXT
);

-- ========================
-- 47. AUTH: APP USER
-- ========================
CREATE TABLE app_user (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    person_id       UUID REFERENCES person(id),
    role_id         VARCHAR(50) NOT NULL REFERENCES app_role(id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_app_user_username ON app_user(username);
