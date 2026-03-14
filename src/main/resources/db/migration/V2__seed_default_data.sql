-- =============================================================================
-- CPMSS: Seed Data — Bootstraps system with default records
-- =============================================================================

-- ========================
-- 1. DEFAULT COMPOUND
-- ========================
INSERT INTO compound (id, name, country, city, district) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Compound #1', 'Country', 'City', 'District');

-- ========================
-- 2. AUTH ROLES
-- ========================
INSERT INTO app_role (id, role_name, description) VALUES
    ('ROLE_ADMIN',   'Admin',   'Full system access. Can update National IDs, manage all modules.'),
    ('ROLE_MANAGER', 'Manager', 'Department access. Can approve contracts, manage payroll.'),
    ('ROLE_STAFF',   'Staff',   'Operational access. Can create work orders, log entries, record payments.');

-- ========================
-- 3. ADMIN USER (Person + AppUser)
-- ========================
INSERT INTO person (id, national_id, first_name, last_name, person_type, gender, is_blacklisted) VALUES
    ('00000000-0000-0000-0000-000000000010', 'SYS-ADMIN-001', 'System', 'Administrator', 'Staff', 'Male', false);

-- Password: admin (BCrypt hash for $2a$10$...)
INSERT INTO app_user (id, username, password_hash, person_id, role_id, is_active) VALUES
    ('00000000-0000-0000-0000-000000000011', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '00000000-0000-0000-0000-000000000010', 'ROLE_ADMIN', true);

-- ========================
-- 4. DEFAULT DEPARTMENTS
-- ========================
INSERT INTO department (id, department_name) VALUES
    ('00000000-0000-0000-0000-000000000100', 'Administration'),
    ('00000000-0000-0000-0000-000000000101', 'Security'),
    ('00000000-0000-0000-0000-000000000102', 'Maintenance'),
    ('00000000-0000-0000-0000-000000000103', 'Finance');

-- ========================
-- 5. DEFAULT POSITIONS
-- ========================
INSERT INTO position (id, position_name, department_id) VALUES
    ('00000000-0000-0000-0000-000000000200', 'Admin Manager',          '00000000-0000-0000-0000-000000000100'),
    ('00000000-0000-0000-0000-000000000201', 'Security Guard',         '00000000-0000-0000-0000-000000000101'),
    ('00000000-0000-0000-0000-000000000202', 'Maintenance Technician', '00000000-0000-0000-0000-000000000102'),
    ('00000000-0000-0000-0000-000000000203', 'Receptionist',           '00000000-0000-0000-0000-000000000100'),
    ('00000000-0000-0000-0000-000000000204', 'Accountant',             '00000000-0000-0000-0000-000000000103');

-- ========================
-- 6. POSITION SALARY HISTORY (Initial)
-- ========================
INSERT INTO position_salary_history (position_id, salary_effective_date, maximum_salary, base_hourly_rate) VALUES
    ('00000000-0000-0000-0000-000000000200', '2026-01-01', 15000.00, 85.00),
    ('00000000-0000-0000-0000-000000000201', '2026-01-01', 5000.00, 28.00),
    ('00000000-0000-0000-0000-000000000202', '2026-01-01', 6000.00, 35.00),
    ('00000000-0000-0000-0000-000000000203', '2026-01-01', 4500.00, 25.00),
    ('00000000-0000-0000-0000-000000000204', '2026-01-01', 8000.00, 45.00);

-- ========================
-- 7. DEFAULT SHIFT TYPES
-- ========================
INSERT INTO shift_attendance_type (id, shift_name) VALUES
    ('00000000-0000-0000-0000-000000000300', 'Morning Shift'),
    ('00000000-0000-0000-0000-000000000301', 'Evening Shift'),
    ('00000000-0000-0000-0000-000000000302', 'Night Shift');

INSERT INTO law_of_shift_attendance (shift_id, effective_date, start_time, end_time, one_hour_extra_bonus, one_hour_difference_discount) VALUES
    ('00000000-0000-0000-0000-000000000300', '2026-01-01', '06:00', '14:00', 50.00, 25.00),
    ('00000000-0000-0000-0000-000000000301', '2026-01-01', '14:00', '22:00', 60.00, 30.00),
    ('00000000-0000-0000-0000-000000000302', '2026-01-01', '22:00', '06:00', 75.00, 35.00);

-- ========================
-- 8. DEFAULT GATES
-- ========================
INSERT INTO gate (id, compound_id, gate_name, gate_type, status) VALUES
    ('00000000-0000-0000-0000-000000000400', '00000000-0000-0000-0000-000000000001', 'Main Gate', 'Combined', 'Active'),
    ('00000000-0000-0000-0000-000000000401', '00000000-0000-0000-0000-000000000001', 'Service Gate', 'Vehicle', 'Active');

-- ========================
-- 9. DEFAULT BANK ACCOUNT (Compound Account)
-- ========================
INSERT INTO bank_account (id, bank_name, iban, swift_code, is_primary, owner_type) VALUES
    ('00000000-0000-0000-0000-000000000500', 'Primary Bank', 'XX00000000000000000000', 'BNKXXX', true, NULL);
