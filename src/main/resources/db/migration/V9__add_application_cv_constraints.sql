-- ============================================================================
-- APPLICATION CV STORAGE — CONSTRAINTS
-- Paired with V8__add_application_cv_storage.sql.
-- ============================================================================

-- Structural check: CV metadata is all-or-none; an application either has no
-- uploaded CV yet or has a complete current CV metadata snapshot.
ALTER TABLE Applications
    ADD CONSTRAINT chk_application_cv_metadata_complete CHECK (
        (
            (cv_object_key IS NOT NULL)::int +
            (cv_original_filename IS NOT NULL)::int +
            (cv_content_type IS NOT NULL)::int +
            (cv_size_bytes IS NOT NULL)::int +
            (cv_uploaded_at IS NOT NULL)::int +
            (cv_uploaded_by_id IS NOT NULL)::int
        ) IN (0, 6)
    );

-- Structural check: the MinIO object key cannot be blank when present.
ALTER TABLE Applications
    ADD CONSTRAINT chk_application_cv_object_key_not_blank CHECK (
        cv_object_key IS NULL OR BTRIM(cv_object_key) <> ''
    );

-- Structural check: the display filename cannot be blank when present.
ALTER TABLE Applications
    ADD CONSTRAINT chk_application_cv_original_filename_not_blank CHECK (
        cv_original_filename IS NULL OR BTRIM(cv_original_filename) <> ''
    );

-- Structural check: the validated MIME type cannot be blank when present.
ALTER TABLE Applications
    ADD CONSTRAINT chk_application_cv_content_type_not_blank CHECK (
        cv_content_type IS NULL OR BTRIM(cv_content_type) <> ''
    );

-- Structural check: uploaded CV size must be positive when present.
ALTER TABLE Applications
    ADD CONSTRAINT chk_application_cv_size_positive CHECK (
        cv_size_bytes IS NULL OR cv_size_bytes > 0
    );
