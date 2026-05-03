-- ============================================================================
-- AUTHENTICATION & USER ACCOUNTS — CONSTRAINTS
-- Paired with V3__add_auth_tables.sql.
-- ============================================================================

-- Business rule: system_role must be one of the recognized software permission levels.
ALTER TABLE App_User
    ADD CONSTRAINT chk_app_user_system_role CHECK (
        system_role IN ('ADMIN', 'MANAGER', 'STAFF')
    );
