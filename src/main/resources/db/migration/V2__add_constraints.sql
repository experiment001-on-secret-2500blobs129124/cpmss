-- ============================================================================
-- DEFERRED CONSTRAINTS
-- Constraints, indexes, and deferred foreign keys extracted from compound_schema.sql.
-- V1 creates the tables; this migration adds rules that were documented as V2 blocks.
-- ============================================================================

-- ============================================================================
-- CORE ENTITIES
-- ============================================================================

-- Structural check: gender is limited to the three recognized system values.
ALTER TABLE Person
    ADD CONSTRAINT chk_person_gender CHECK (
        gender IN ('Male', 'Female')
    );

-- ============================================================================
-- COMPOUND STRUCTURE & PEOPLE RELATIONS
-- ============================================================================

-- Structural check: end_date must come after effective_date when a position history ends.
ALTER TABLE Staff_Position_History
    ADD CONSTRAINT chk_position_history_dates CHECK (
        end_date IS NULL OR end_date > effective_date
    );

-- Business rule: gate_status must be one of the recognized operational states.
ALTER TABLE Gate
    ADD CONSTRAINT chk_gate_status CHECK (
        gate_status IN ('Active', 'Under Maintenance', 'Closed')
    );

-- Business rule: building_type must be one of the two recognized structural categories.
ALTER TABLE Building
    ADD CONSTRAINT chk_building_type CHECK (
        building_type IN ('Residential', 'Non-Residential')
    );

-- Structural check: floors_count cannot be negative when supplied.
ALTER TABLE Building
    ADD CONSTRAINT chk_building_floors_count CHECK (
        floors_count IS NULL OR floors_count >= 0
    );

-- Structural check: a person cannot supervise themselves.
ALTER TABLE Person_Supervision
    ADD CONSTRAINT chk_no_self_supervision CHECK (
        supervisor_id != supervisee_id
    );

-- Structural check + Business rule: a bank account must belong to exactly one owner — a compound, a person, or a company.
ALTER TABLE Bank_Account
    ADD CONSTRAINT chk_bank_account_owner CHECK (
        (compound_id IS NOT NULL)::int +
        (account_owner_id IS NOT NULL)::int +
        (company_id IS NOT NULL)::int = 1
    );

-- Structural check: maximum salary must be positive.
ALTER TABLE Position_Salary_History
    ADD CONSTRAINT chk_max_salary_positive CHECK (
        maximum_salary > 0
    );

-- Structural check: base daily rate must be positive.
ALTER TABLE Position_Salary_History
    ADD CONSTRAINT chk_base_rate_positive CHECK (
        base_daily_rate > 0
    );

-- ============================================================================
-- UNITS, FACILITIES & PRICING
-- ============================================================================

-- Structural check: listing price must be zero or positive — a free unit is valid, a negative price is not.
ALTER TABLE Unit_Pricing_History
    ADD CONSTRAINT chk_listing_price CHECK (
        listing_price >= 0
    );

ALTER TABLE Unit_Pricing_History
    ADD CONSTRAINT chk_listing_price_currency CHECK (
        listing_price_currency ~ '^[A-Z]{3}$'
    );

-- Structural check: unit measurements cannot be negative and area must be positive when supplied.
ALTER TABLE Unit
    ADD CONSTRAINT chk_unit_measurements CHECK (
        (floor_no IS NULL OR floor_no >= 0)
        AND (no_of_rooms IS NULL OR no_of_rooms >= 0)
        AND (no_of_bathrooms IS NULL OR no_of_bathrooms >= 0)
        AND (no_of_bedrooms IS NULL OR no_of_bedrooms >= 0)
        AND (no_of_total_rooms IS NULL OR no_of_total_rooms >= 0)
        AND (no_of_balconies IS NULL OR no_of_balconies >= 0)
        AND (square_foot IS NULL OR square_foot > 0)
    );

-- Business rule: unit_status must be one of the recognized occupancy states.
ALTER TABLE Unit_Status_History
    ADD CONSTRAINT chk_unit_status CHECK (
        unit_status IN ('Vacant', 'Occupied', 'Under Maintenance', 'Reserved')
    );

-- Business rule: a facility is managed either by the compound or by an external vendor — no other type.
ALTER TABLE Facility
    ADD CONSTRAINT chk_facility_management_type CHECK (
        management_type IN ('Compound', 'Vendor')
    );

-- Structural check + Business rule: Vendor-managed facilities must reference a company; Compound-managed must not.
ALTER TABLE Facility
    ADD CONSTRAINT chk_facility_management_ref CHECK (
        (management_type = 'Compound' AND managed_by_company_id IS NULL)::int +
        (management_type = 'Vendor'   AND managed_by_company_id IS NOT NULL)::int = 1
    );

-- Structural check: opening and closing times must be set together and ordered.
ALTER TABLE Facility_Hours_History
    ADD CONSTRAINT chk_facility_hours_window CHECK (
        (opening_time IS NULL AND closing_time IS NULL)
        OR (opening_time IS NOT NULL AND closing_time IS NOT NULL AND closing_time > opening_time)
    );

-- ============================================================================
-- CONTRACTS & OCCUPANCY
-- ============================================================================

-- Structural check: end_date must come after start_date when a contract end date is set.
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_dates CHECK (
        end_date IS NULL OR end_date > start_date
    );

-- Structural check: money values must carry currency when present and cannot be negative.
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_final_price_money CHECK (
        (final_price IS NULL AND final_price_currency IS NULL)
        OR (final_price IS NOT NULL AND final_price >= 0 AND final_price_currency ~ '^[A-Z]{3}$')
    );

-- Structural check: security deposit money must carry currency when present and cannot be negative.
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_security_deposit_money CHECK (
        (security_deposit_amount IS NULL AND security_deposit_currency IS NULL)
        OR (
            security_deposit_amount IS NOT NULL
            AND security_deposit_amount >= 0
            AND security_deposit_currency ~ '^[A-Z]{3}$'
        )
    );

-- Business rule: contract type determines whether the target is a unit (Residential) or a facility (Commercial).
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_type CHECK (
        contract_type IN ('Residential', 'Commercial')
    );

-- Business rule: contract status must reflect one of the recognized lifecycle stages.
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_status CHECK (
        contract_status IN ('Draft', 'Active', 'Expired', 'Terminated', 'Renewed')
    );

-- Structural check + Business rule: a contract covers exactly one target — a unit or a facility, never both, never neither.
ALTER TABLE Contract
    ADD CONSTRAINT chk_contract_target CHECK (
        (unit_id IS NOT NULL)::int +
        (facility_id IS NOT NULL)::int = 1
    );

-- Business rule: installment type must be one of the recognized payment categories.
ALTER TABLE Installment
    ADD CONSTRAINT chk_installment_type CHECK (
        installment_type IN ('Rent', 'Deposit', 'Penalty', 'Other')
    );

-- Business rule: installment status must reflect one of the recognized lifecycle stages.
ALTER TABLE Installment
    ADD CONSTRAINT chk_installment_status CHECK (
        installment_status IN ('Pending', 'Partially Paid', 'Paid', 'Overdue', 'Cancelled')
    );

-- Structural check: installment amount must be positive — zero or negative amounts are invalid.
ALTER TABLE Installment
    ADD CONSTRAINT chk_installment_amount CHECK (
        amount_expected > 0
    );

-- Structural check: installment money must carry an explicit ISO-style currency code.
ALTER TABLE Installment
    ADD CONSTRAINT chk_installment_amount_currency CHECK (
        amount_expected_currency ~ '^[A-Z]{3}$'
    );

-- Business rule: a party's role must be one of the recognized contract party designations.
ALTER TABLE Contract_Party
    ADD CONSTRAINT chk_contract_party_role CHECK (
        role IN (
            'Primary Signer', 'Guarantor', 'Emergency Contact',
            'Corporate Representative', 'Authorizing Staff'
        )
    );

-- Business rule: each contract may have at most one Primary Signer — co-equal primary signing is not permitted.
CREATE UNIQUE INDEX uq_one_primary_signer_per_contract
    ON Contract_Party (contract_id)
    WHERE role = 'Primary Signer';

-- Structural check: move_out_date must come after move_in_date when a resident vacates.
ALTER TABLE Person_Resides_Under
    ADD CONSTRAINT chk_residency_dates CHECK (
        move_out_date IS NULL OR move_out_date > move_in_date
    );

-- Business rule: household_relationship must reflect the person's relationship to the primary contract holder.
ALTER TABLE Person_Resides_Under
    ADD CONSTRAINT chk_household_relationship CHECK (
        household_relationship IN ('Primary', 'Spouse', 'Child', 'Sibling', 'Guardian', 'Co-tenant', 'Guest')
    );

-- ============================================================================
-- VEHICLE REGISTRY & OWNERSHIP
-- ============================================================================

-- Structural check + Business rule: a vehicle must have exactly one owner — a person, department, or company.
ALTER TABLE Vehicle
    ADD CONSTRAINT chk_vehicle_owner CHECK (
        (owner_person_id IS NOT NULL)::int +
        (owner_department_id IS NOT NULL)::int +
        (owner_company_id IS NOT NULL)::int = 1
    );

-- ============================================================================
-- ACCESS CONTROL & GATE MANAGEMENT
-- ============================================================================

-- Business rule: permit_type must be one of the recognized physical permit formats.
ALTER TABLE Access_Permit
    ADD CONSTRAINT chk_permit_type CHECK (
        permit_type IN ('Staff Badge', 'Resident Card', 'Visitor Pass', 'Contractor Pass', 'Vehicle Sticker')
    );

-- Business rule: access_level must be one of the recognized access tiers.
ALTER TABLE Access_Permit
    ADD CONSTRAINT chk_access_level CHECK (
        access_level IN ('Full Access', 'Restricted Areas', 'Common Areas Only')
    );

-- Business rule: permit_status must be one of the recognized lifecycle states.
ALTER TABLE Access_Permit
    ADD CONSTRAINT chk_permit_status CHECK (
        permit_status IN ('Active', 'Expired', 'Suspended', 'Revoked')
    );

-- Structural check: expiry_date must be on or after issue_date when set.
ALTER TABLE Access_Permit
    ADD CONSTRAINT chk_permit_dates CHECK (
        expiry_date IS NULL OR expiry_date >= issue_date
    );

-- Business rule: only one active permit per person per type at any time.
CREATE UNIQUE INDEX uq_one_active_permit_per_person_per_type
    ON Access_Permit (permit_holder_id, permit_type)
    WHERE permit_status = 'Active';

-- Structural check + Business rule: exactly one entitlement FK must be set — the basis for this permit's issuance.
ALTER TABLE Access_Permit
    ADD CONSTRAINT chk_permit_entitlement CHECK (
        (staff_profile_id IS NOT NULL)::int +
        (contract_id      IS NOT NULL)::int +
        (work_order_id    IS NOT NULL)::int +
        (invited_by_id    IS NOT NULL)::int = 1
    );

-- Structural check: exactly one of permit_id or manual_plate_entry must be set per entry event.
ALTER TABLE Enters_At
    ADD CONSTRAINT chk_entry_type CHECK (
        (permit_id IS NOT NULL)::int +
        (manual_plate_entry IS NOT NULL)::int = 1
    );

-- Business rule: direction must be either 'In' or 'Out'.
ALTER TABLE Enters_At
    ADD CONSTRAINT chk_gate_direction CHECK (
        direction IN ('In', 'Out')
    );

-- ============================================================================
-- MAINTENANCE & WORK ORDERS
-- ============================================================================

-- Structural check: date_completed must be on or after date_scheduled when set.
ALTER TABLE Work_Order
    ADD CONSTRAINT chk_work_order_dates CHECK (
        date_completed IS NULL OR date_completed >= date_scheduled
    );

-- Structural check: work-order cost must carry currency when present and be positive.
ALTER TABLE Work_Order
    ADD CONSTRAINT chk_work_order_cost_money CHECK (
        (cost_amount IS NULL AND cost_currency IS NULL)
        OR (cost_amount IS NOT NULL AND cost_amount > 0 AND cost_currency ~ '^[A-Z]{3}$')
    );

-- Business rule: job_status must reflect one of the recognized lifecycle stages.
ALTER TABLE Work_Order
    ADD CONSTRAINT chk_job_status CHECK (
        job_status IN ('Pending', 'Assigned', 'In Progress', 'Completed', 'Paid', 'Cancelled')
    );

-- Business rule: priority must be one of the recognized urgency levels.
ALTER TABLE Work_Order
    ADD CONSTRAINT chk_priority CHECK (
        priority IN ('Low', 'Normal', 'High', 'Emergency')
    );

-- Business rule: service_category must be one of the recognized trade categories.
ALTER TABLE Work_Order
    ADD CONSTRAINT chk_service_category CHECK (
        service_category IN ('Plumbing', 'Electrical', 'HVAC', 'Landscaping', 'Cleaning', 'Security')
    );

-- ============================================================================
-- HUMAN RESOURCES & ATTENDANCE
-- ============================================================================

-- Business rule: interview_result must be one of the three recognized outcomes.
ALTER TABLE Recruitment
    ADD CONSTRAINT chk_interview_result CHECK (
        interview_result IN ('Pass', 'Fail', 'Pending')
    );

-- Structural check: offered_base_daily_rate must be a positive value.
ALTER TABLE Hire_Agreement
    ADD CONSTRAINT chk_offered_base_daily_rate CHECK (offered_base_daily_rate > 0);

-- Structural check: offered_maximum_salary must be positive when set.
ALTER TABLE Hire_Agreement
    ADD CONSTRAINT chk_offered_max_salary CHECK (
        offered_maximum_salary IS NULL OR offered_maximum_salary > 0
    );

-- Structural check: shift-law windows and expected hours must be positive.
ALTER TABLE Law_of_Shift_Attendance
    ADD CONSTRAINT chk_shift_law_time_and_hours CHECK (
        end_time > start_time AND expected_hours > 0
    );

-- Structural check: hourly bonus/deduction rates must carry explicit currency when present.
ALTER TABLE Law_of_Shift_Attendance
    ADD CONSTRAINT chk_shift_law_money_rates CHECK (
        (
            (one_hour_extra_bonus IS NULL AND one_hour_extra_bonus_currency IS NULL)
            OR (
                one_hour_extra_bonus IS NOT NULL
                AND one_hour_extra_bonus >= 0
                AND one_hour_extra_bonus_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (one_hour_diff_discount IS NULL AND one_hour_diff_discount_currency IS NULL)
            OR (
                one_hour_diff_discount IS NOT NULL
                AND one_hour_diff_discount >= 0
                AND one_hour_diff_discount_currency ~ '^[A-Z]{3}$'
            )
        )
    );

-- Structural check: attendance windows are either absent or ordered.
ALTER TABLE Attends
    ADD CONSTRAINT chk_attends_time_window CHECK (
        (check_in_time IS NULL AND check_out_time IS NULL)
        OR (check_in_time IS NOT NULL AND check_out_time IS NOT NULL AND check_out_time > check_in_time)
    );

-- Structural check: daily payroll snapshots use non-negative money pairs.
ALTER TABLE Attends
    ADD CONSTRAINT chk_attends_daily_money CHECK (
        (
            (daily_bonus IS NULL AND daily_bonus_currency IS NULL)
            OR (
                daily_bonus IS NOT NULL
                AND daily_bonus >= 0
                AND daily_bonus_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (daily_deduction IS NULL AND daily_deduction_currency IS NULL)
            OR (
                daily_deduction IS NOT NULL
                AND daily_deduction >= 0
                AND daily_deduction_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (daily_salary IS NULL AND daily_salary_currency IS NULL)
            OR (
                daily_salary IS NOT NULL
                AND daily_salary >= 0
                AND daily_salary_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (daily_net_salary IS NULL AND daily_net_salary_currency IS NULL)
            OR (
                daily_net_salary IS NOT NULL
                AND daily_net_salary >= 0
                AND daily_net_salary_currency ~ '^[A-Z]{3}$'
            )
        )
    );

-- Structural check: payroll periods must be valid calendar month keys.
ALTER TABLE Task_Monthly_Salary
    ADD CONSTRAINT chk_task_monthly_salary_period CHECK (
        year > 0 AND month BETWEEN 1 AND 12
    );

-- Structural check: monthly payroll snapshots use non-negative money pairs.
ALTER TABLE Task_Monthly_Salary
    ADD CONSTRAINT chk_task_monthly_salary_money CHECK (
        (
            (monthly_deduction IS NULL AND monthly_deduction_currency IS NULL)
            OR (
                monthly_deduction IS NOT NULL
                AND monthly_deduction >= 0
                AND monthly_deduction_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (monthly_bonus IS NULL AND monthly_bonus_currency IS NULL)
            OR (
                monthly_bonus IS NOT NULL
                AND monthly_bonus >= 0
                AND monthly_bonus_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (tax IS NULL AND tax_currency IS NULL)
            OR (
                tax IS NOT NULL
                AND tax >= 0
                AND tax_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (monthly_salary IS NULL AND monthly_salary_currency IS NULL)
            OR (
                monthly_salary IS NOT NULL
                AND monthly_salary >= 0
                AND monthly_salary_currency ~ '^[A-Z]{3}$'
            )
        )
        AND (
            (monthly_net_salary IS NULL AND monthly_net_salary_currency IS NULL)
            OR (
                monthly_net_salary IS NOT NULL
                AND monthly_net_salary >= 0
                AND monthly_net_salary_currency ~ '^[A-Z]{3}$'
            )
        )
    );

-- Structural check: payroll payment periods must be valid calendar month keys.
ALTER TABLE Payroll_Payment
    ADD CONSTRAINT chk_payroll_payment_period CHECK (
        year > 0 AND month BETWEEN 1 AND 12
    );

-- ============================================================================
-- COMPENSATION HISTORY & KPI
-- ============================================================================

-- Structural check: end_date must be after effective_date when set.
ALTER TABLE Staff_Salary_History
    ADD CONSTRAINT chk_salary_history_dates CHECK (
        end_date IS NULL OR end_date > effective_date
    );

-- Structural check: base_daily_rate and maximum_salary must be positive.
ALTER TABLE Staff_Salary_History
    ADD CONSTRAINT chk_salary_positive CHECK (
        base_daily_rate > 0 AND maximum_salary > 0
    );

-- Structural link: review_id references the performance review that triggered this salary change.
-- Deferred because Staff_Performance_Review is defined after Staff_Salary_History in migration order.
ALTER TABLE Staff_Salary_History
    ADD CONSTRAINT fk_salary_history_review
    FOREIGN KEY (review_id) REFERENCES Staff_Performance_Review(review_id) ON DELETE SET NULL;

-- Business rule: tier_label must be one of the recognized performance levels.
ALTER TABLE KPI_Policy
    ADD CONSTRAINT chk_kpi_tier_label CHECK (
        tier_label IN ('Excellent', 'Good', 'Average', 'Poor')
    );

-- Structural check: max_kpi_score must be greater than min_kpi_score.
ALTER TABLE KPI_Policy
    ADD CONSTRAINT chk_kpi_score_range CHECK (
        max_kpi_score > min_kpi_score
    );

-- Structural check: kpi_score must be a non-negative value.
ALTER TABLE Staff_KPI_Record
    ADD CONSTRAINT chk_kpi_score_positive CHECK (
        kpi_score >= 0
    );

-- Business rule: overall_rating must be one of the recognized performance levels.
ALTER TABLE Staff_Performance_Review
    ADD CONSTRAINT chk_review_rating CHECK (
        overall_rating IN ('Excellent', 'Good', 'Average', 'Poor')
    );

-- ============================================================================
-- GATE SECURITY & GUARD ASSIGNMENT
-- ============================================================================

-- Structural check: shift_end must be after shift_start when the shift has ended.
ALTER TABLE Gate_Guard_Assignment
    ADD CONSTRAINT chk_guard_shift_dates CHECK (
        shift_end IS NULL OR shift_end > shift_start
    );

-- ============================================================================
-- FINANCIAL & PAYMENTS
-- ============================================================================

-- Structural check: payment amount must be positive.
ALTER TABLE Payment
    ADD CONSTRAINT chk_payment_amount CHECK (amount > 0);

-- Structural check: payment money must carry an explicit ISO-style currency code.
ALTER TABLE Payment
    ADD CONSTRAINT chk_payment_currency CHECK (currency ~ '^[A-Z]{3}$');

-- Business rule: payment_type must be one of the three recognized payment categories.
ALTER TABLE Payment
    ADD CONSTRAINT chk_payment_type CHECK (
        payment_type IN ('Installment', 'WorkOrder', 'Payroll')
    );

-- Business rule: method must be one of the recognized payment methods.
ALTER TABLE Payment
    ADD CONSTRAINT chk_payment_method CHECK (
        method IN ('Cash', 'Bank Transfer', 'Cheque', 'Card', 'Other')
    );

-- Business rule: direction must be Inbound (received) or Outbound (paid out).
ALTER TABLE Payment
    ADD CONSTRAINT chk_payment_direction CHECK (
        direction IN ('Inbound', 'Outbound')
    );

-- Business rule: reconciliation_status must be one of the three lifecycle states.
ALTER TABLE Payment
    ADD CONSTRAINT chk_reconciliation_status CHECK (
        reconciliation_status IN ('Pending', 'Reconciled', 'Disputed')
    );

-- Structural check: late_fee_amount must be non-negative when set.
ALTER TABLE Installment_Payment
    ADD CONSTRAINT chk_late_fee_positive CHECK (
        late_fee_amount IS NULL OR late_fee_amount >= 0
    );

ALTER TABLE Installment_Payment
    ADD CONSTRAINT chk_late_fee_money CHECK (
        (late_fee_amount IS NULL AND late_fee_currency IS NULL)
        OR (late_fee_amount IS NOT NULL AND late_fee_currency ~ '^[A-Z]{3}$')
    );
