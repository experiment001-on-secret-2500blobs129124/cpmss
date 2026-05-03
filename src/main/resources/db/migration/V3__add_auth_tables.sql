-- ============================================================================
-- AUTHENTICATION & USER ACCOUNTS
-- (depends on: Person)
-- ============================================================================

-- Core entity: system login account — enables authentication for staff and administrators.
-- Not every Person gets an App_User row — only those who need software access (staff, managers, admins).
-- See DATABASE.md § "Role Architecture: Business Roles vs System Roles" for full rationale.
-- Note: person_id is nullable — the bootstrap admin (POST /setup) is created before any Person exists.
--       Once linked, the Person's business roles (Person_Role) and the App_User's system_role
--       are independent concerns: Person_Role = "what are you in the compound",
--       system_role = "what can you do in the software".
-- Note: email is the login credential — it is independent of Person_Email (multi-value contact info).
--       A Person may have many emails; the App_User email is the one used for authentication.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE App_User (
    user_id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email                 VARCHAR(255) NOT NULL UNIQUE,
    password_hash         VARCHAR(255) NOT NULL,
    system_role           VARCHAR(50)  NOT NULL,
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    force_password_change BOOLEAN      NOT NULL DEFAULT TRUE,
    person_id             UUID         UNIQUE REFERENCES Person(person_id) ON DELETE RESTRICT,
    -- Audit columns (mapped to BaseEntity)
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);
