-- ============================================================================
-- AUTHENTICATION & USER ACCOUNTS — CONSTRAINTS
-- Paired with V3__add_auth_tables.sql.
-- ============================================================================

-- Business rule: system_role must be one of the recognized software permission levels.
-- See docs/REQUIREMENTS.md § 2 for the full role architecture.
ALTER TABLE App_User
    ADD CONSTRAINT chk_app_user_system_role CHECK (
        system_role IN (
            'ADMIN',              -- IT bootstrap / break-glass (non-staff)
            'GENERAL_MANAGER',    -- Compound owner — sees everything
            'HR_OFFICER',         -- Recruitment, staff, KPI, salary
            'ACCOUNTANT',         -- Contracts, payments, bank accounts
            'SECURITY_OFFICER',   -- Permits, gates, vehicles, blacklist
            'FACILITY_OFFICER',   -- Work orders, facilities, buildings, units
            'DEPARTMENT_MANAGER', -- Own department: tasks, attendance, reviews
            'SUPERVISOR',         -- Own team: views supervisees' data
            'GATE_GUARD',         -- Gate entry logging only
            'STAFF',              -- Read-only access to own records
            'INVESTOR',           -- Read-only financial dashboard (non-staff)
            'APPLICANT'           -- Job portal only (non-staff)
        )
    );
