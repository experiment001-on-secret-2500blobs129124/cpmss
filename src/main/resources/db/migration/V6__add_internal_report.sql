-- ============================================================================
-- INTERNAL REPORTING / TICKETING SYSTEM
-- A lightweight internal report system for cross-role communication.
-- Supervisors, department managers, and officers file reports that are
-- routed to a target role (pool model — any user with that role can see it).
-- ============================================================================

-- Core entity: an internal report filed by any staff member, directed at a role group.
-- Reports are never deleted — closed by status change.
-- The is_read flag doubles as a notification indicator: unread reports show a badge count
-- in the frontend. mark_as_unread resets the flag.
-- Note: assigned_to_role is the system_role that should see this report (pool model).
--       Any user with that role can view it. Once someone acts on it, they become
--       the resolved_by person.
-- Audit columns capture when the record was added or changed, and which authenticated user did it.
CREATE TABLE Internal_Report (
    report_id       UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id     UUID           NOT NULL REFERENCES Person(person_id)  ON DELETE RESTRICT,
    assigned_to_role VARCHAR(30)   NOT NULL,
    subject         VARCHAR(200)   NOT NULL,
    body            TEXT           NOT NULL,
    report_category VARCHAR(50)    NOT NULL,
    priority        VARCHAR(20)    NOT NULL DEFAULT 'Normal',
    report_status   VARCHAR(20)    NOT NULL DEFAULT 'Open',
    is_read         BOOLEAN        NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMPTZ,
    read_by_id      UUID           REFERENCES Person(person_id)          ON DELETE RESTRICT,
    resolved_by_id  UUID           REFERENCES Person(person_id)          ON DELETE RESTRICT,
    resolved_at     TIMESTAMPTZ,
    resolution_note TEXT,
    -- Audit columns (mapped to BaseEntity)
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);
