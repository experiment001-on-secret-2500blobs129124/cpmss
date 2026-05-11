-- Seed stable catalog/reference rows required for normal workflow setup.
-- This migration deliberately avoids fake dev data such as persons, contracts,
-- applications, gates, compounds, KPI policies, salary bands, and payments.
-- Those records are environment-specific or depend on real business actors.

-- Business person roles.
INSERT INTO Role (role_id, role_name, created_by, updated_by)
VALUES
    ('11111111-1111-4111-8111-111111111101', 'Staff', 'migration', 'migration'),
    ('11111111-1111-4111-8111-111111111102', 'Tenant', 'migration', 'migration'),
    ('11111111-1111-4111-8111-111111111103', 'Investor', 'migration', 'migration'),
    ('11111111-1111-4111-8111-111111111104', 'Visitor', 'migration', 'migration')
ON CONFLICT (role_name) DO NOTHING;

-- Core operating departments.
INSERT INTO Department (department_id, department_name, created_by, updated_by)
VALUES
    ('22222222-2222-4222-8222-222222222201', 'Management', 'migration', 'migration'),
    ('22222222-2222-4222-8222-222222222202', 'HR', 'migration', 'migration'),
    ('22222222-2222-4222-8222-222222222203', 'Finance', 'migration', 'migration'),
    ('22222222-2222-4222-8222-222222222204', 'Security', 'migration', 'migration'),
    ('22222222-2222-4222-8222-222222222205', 'Facility', 'migration', 'migration'),
    ('22222222-2222-4222-8222-222222222206', 'Maintenance', 'migration', 'migration')
ON CONFLICT (department_name) DO NOTHING;

-- Common qualification levels used by staff/applicant workflows.
INSERT INTO Qualification (qualification_id, qualification_name, created_by, updated_by)
VALUES
    ('33333333-3333-4333-8333-333333333301', 'High School Diploma', 'migration', 'migration'),
    ('33333333-3333-4333-8333-333333333302', 'Bachelor''s Degree', 'migration', 'migration'),
    ('33333333-3333-4333-8333-333333333303', 'Master''s Degree', 'migration', 'migration'),
    ('33333333-3333-4333-8333-333333333304', 'PhD', 'migration', 'migration')
ON CONFLICT (qualification_name) DO NOTHING;

-- Common shift catalog rows. Shift_Attendance_Type has no unique constraint,
-- so each row is guarded by NOT EXISTS on the catalog label.
INSERT INTO Shift_Attendance_Type (shift_id, shift_name, created_by, updated_by)
SELECT '44444444-4444-4444-8444-444444444401', 'Morning', 'migration', 'migration'
WHERE NOT EXISTS (
    SELECT 1 FROM Shift_Attendance_Type WHERE shift_name = 'Morning'
);

INSERT INTO Shift_Attendance_Type (shift_id, shift_name, created_by, updated_by)
SELECT '44444444-4444-4444-8444-444444444402', 'Evening', 'migration', 'migration'
WHERE NOT EXISTS (
    SELECT 1 FROM Shift_Attendance_Type WHERE shift_name = 'Evening'
);

INSERT INTO Shift_Attendance_Type (shift_id, shift_name, created_by, updated_by)
SELECT '44444444-4444-4444-8444-444444444403', 'Night', 'migration', 'migration'
WHERE NOT EXISTS (
    SELECT 1 FROM Shift_Attendance_Type WHERE shift_name = 'Night'
);

INSERT INTO Shift_Attendance_Type (shift_id, shift_name, created_by, updated_by)
SELECT '44444444-4444-4444-8444-444444444404', 'Rotating', 'migration', 'migration'
WHERE NOT EXISTS (
    SELECT 1 FROM Shift_Attendance_Type WHERE shift_name = 'Rotating'
);
