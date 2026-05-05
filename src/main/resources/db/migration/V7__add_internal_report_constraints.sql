-- ============================================================================
-- INTERNAL REPORT — CONSTRAINTS
-- Paired with V6__add_internal_report.sql.
-- ============================================================================

-- Business rule: assigned_to_role must be a valid system role that can receive reports.
-- STAFF, GATE_GUARD, INVESTOR, and APPLICANT are excluded — they don't process reports.
ALTER TABLE Internal_Report
    ADD CONSTRAINT chk_report_assigned_role CHECK (
        assigned_to_role IN (
            'ADMIN',
            'GENERAL_MANAGER',
            'HR_OFFICER',
            'ACCOUNTANT',
            'SECURITY_OFFICER',
            'FACILITY_OFFICER',
            'DEPARTMENT_MANAGER',
            'SUPERVISOR'
        )
    );

-- Business rule: report_category must be one of the defined categories.
ALTER TABLE Internal_Report
    ADD CONSTRAINT chk_report_category CHECK (
        report_category IN (
            'Salary_Request',
            'Transfer_Request',
            'Complaint',
            'Maintenance_Request',
            'Security_Incident',
            'Policy_Suggestion',
            'General'
        )
    );

-- Business rule: priority must be one of the defined levels.
ALTER TABLE Internal_Report
    ADD CONSTRAINT chk_report_priority CHECK (
        priority IN ('Low', 'Normal', 'High', 'Urgent')
    );

-- Business rule: report_status must be one of the defined statuses.
ALTER TABLE Internal_Report
    ADD CONSTRAINT chk_report_status CHECK (
        report_status IN ('Open', 'In_Review', 'Resolved', 'Rejected')
    );
