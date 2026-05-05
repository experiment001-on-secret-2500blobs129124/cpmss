-- ============================================================================
-- SUPERVISION TEAM SUPPORT
-- Adds team_name column to Person_Supervision for grouping supervisees.
-- ============================================================================

-- Allows supervisors to name their teams (e.g. "Alpha Team", "Night Shift Guards").
-- NULL means the supervisee is not part of a named team (direct individual supervision).
ALTER TABLE Person_Supervision
    ADD COLUMN team_name VARCHAR(100);
