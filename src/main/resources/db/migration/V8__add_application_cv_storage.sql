-- ============================================================================
-- APPLICATION CV STORAGE
-- Adds current-CV object-storage metadata directly to Applications.
-- ============================================================================

-- Applications are the applicant-owned hiring attempt. A rejected applicant must
-- not receive a Staff_Profile row, so applicant CV storage belongs to the
-- application workflow instead of Staff_Profile.
--
-- This migration stores the current CV reference for one application. Binary
-- file bytes live in MinIO; PostgreSQL stores only object metadata. Re-uploading
-- a CV for the same application overwrites these current metadata fields. A
-- later document-history feature can add a separate document metadata table
-- without changing the application primary key.
ALTER TABLE Applications
    ADD COLUMN cv_object_key VARCHAR(500),
    ADD COLUMN cv_original_filename VARCHAR(255),
    ADD COLUMN cv_content_type VARCHAR(100),
    ADD COLUMN cv_size_bytes BIGINT,
    ADD COLUMN cv_uploaded_at TIMESTAMPTZ,
    ADD COLUMN cv_uploaded_by_id UUID REFERENCES Person(person_id) ON DELETE RESTRICT;
