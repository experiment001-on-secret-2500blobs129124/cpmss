# Functional Requirements, Business Rules & Workflows

> **Purpose**: This document is the *behaviour model* — it defines what the system **does**,
> who can do it, what rules govern it, and how multi-step processes flow.
> The migration files define the *data model* (what the system **stores**).
> This document bridges data → behaviour.

---

## Table of Contents

1. [Personas — Who Uses the System](#1-personas--who-uses-the-system)
2. [System Roles vs Business Roles](#2-system-roles-vs-business-roles)
3. [The Chicken-and-Egg Problem — Who Controls Whom?](#3-the-chicken-and-egg-problem--who-controls-whom)
4. [Functional Requirements by Role](#4-functional-requirements-by-role)
5. [Business Rules](#5-business-rules)
6. [User Stories (Multi-Step Workflows)](#6-user-stories-multi-step-workflows)
7. [Deferred Product Contracts and Open Questions](#7-deferred-product-contracts-and-open-questions)
8. [Admin Production Lockdown](#8-admin-production-lockdown)

---

## How to Read This Document

This document is the product contract used before implementation:

- **Functional requirements** define what the system does for each role.
- **Business rules** define domain rules the backend must enforce.
- **Permissions** define who can do each action.
- **Data scope** defines which records an allowed user can touch.
- **Workflows** define multi-step business processes that must be transactional
  where partial completion would corrupt the business state.
- **Open questions** mark behavior that is not safe to invent during coding.

This document defines product behavior. It does not track branch progress,
commit history, or implementation completion state.

---

## 1. Personas — Who Uses the System

These are the **real-world people** who interact with CPMSS. Derived from
the data structures in V1 — every table implies someone who reads or writes it.

| Persona | What they do in the real world | Tables they touch |
|---------|-------------------------------|-------------------|
| **Compound Owner / General Manager** | Owns/oversees the entire compound. Makes strategic decisions: investments, pricing, vendor contracts. Sees financial dashboards. | Compound, Person_Invests_in_Compound, Bank_Account, Payment (read), Contract (approve) |
| **HR Officer** | Recruits, onboards, manages staff profiles, position assignments, salary changes, performance reviews, KPI policies. | Applications, Recruitment, Hire_Agreement, Staff_Profile, Staff_Position_History, Staff_Salary_History, Staff_Performance_Review, KPI_Policy, Staff_KPI_Record, Staff_KPI_Monthly_Summary |
| **Finance / Accountant** | Manages contracts, installments, incoming/outgoing payments, payroll disbursement, bank accounts, reconciliation. | Contract, Installment, Payment, Installment_Payment, Work_Order_Payment, Payroll_Payment, Bank_Account, Task_Monthly_Salary |
| **Security Manager** | Oversees access permits, gate events, guard assignments, vehicle registry, blacklisting. | Access_Permit, Enters_At, Gate_Guard_Assignment, Gate, Vehicle, Vehicle_Permits |
| **Facility / Maintenance Manager** | Raises and tracks work orders, manages facilities, assigns vendors (companies), tracks facility hours. | Work_Order, Work_Order_Assigned_To, Facility, Facility_Hours_History, Facility_Manager, Building, Unit |
| **Department Manager** | Manages their own department's staff: daily task assignments, attendance, KPI scoring, performance reviews. | Assigned_Task, Attends, Staff_KPI_Record, Staff_Performance_Review, Department_Managers, Person_Supervision |
| **Gate Guard** | Processes gate entries, scans permits, logs anonymous vehicle plates. | Enters_At (write), Gate_Guard_Assignment (read own), Access_Permit (read/verify) |
| **Regular Staff** | Views own attendance, own tasks, own salary history, own profile. Cannot modify anything except maybe own contact info. | Attends (read own), Assigned_Task (read own), Staff_Salary_History (read own), Staff_Profile (read own) |
| **Tenant** | Views own contract, installment schedule, payment history. NOT a system user — interacts via **frontend portal**. | Contract (read own), Installment (read own), Payment (read own), Person_Resides_Under (read own) |
| **Investor** | Views investment stakes, compound financials (dashboards). **Logs in** as a non-staff read-only role. | Person_Invests_in_Compound (read own), Payment (read aggregates), Contract (read occupancy rates) |
| **Visitor** | No system access. Exists only as a Person record + Visitor role + Access_Permit for gate entry. | None directly |

> **Key insight**: Not every persona needs a unique `system_role`. Some personas
> never log in (Tenant, Visitor). Their identity is captured by their
> **business role** in the `Person_Role` table. Only personas who need software
> access get an `App_User` row with a `system_role`. **Investor** and
> **Applicant** are non-staff login roles. **Tenant** and **Visitor** remain
> non-login personas in this product scope.

---

## 2. System Roles vs Business Roles

### Business Roles (Person_Role table) — "What are you in the compound?"

These are **real-world facts**, stored in `Person_Role` via the `Role` catalog:

| Business Role | Who |
|--------------|-----|
| Staff | Anyone employed by the compound |
| Tenant | Anyone residing in a unit under a contract |
| Investor | Anyone holding investment stakes in the compound |
| Visitor | Anyone entering temporarily (guests, delivery, contractors) |

A person can hold **multiple** business roles simultaneously (e.g., Staff + Tenant).

### System Roles (App_User.system_role) — "What can you do in the software?"

These are **software permission levels**. Only people who need to LOG IN get one:

| System Role | Persona(s) it covers | Access scope | V1 tables they write |
|------------|----------------------|-------------|---------------------|
| `ADMIN` | IT / system developer | System config + AppUser management only. Not a business role — no dashboards. | App_User |
| `GENERAL_MANAGER` | Compound Owner / GM | **All business endpoints.** The real authority. Can also manage AppUser accounts. | Everything except system config |
| `HR_OFFICER` | HR staff | Recruitment, onboarding, staff profiles, positions, salary, KPI policies, performance reviews | Applications, Recruitment, Hire_Agreement, Staff_Profile, Staff_Position, Staff_Position_History, Staff_Salary_History, KPI_Policy, Staff_Performance_Review, Person (Staff), Person_Role, Law_of_Shift_Attendance |
| `ACCOUNTANT` | Finance / leasing staff | Contracts, installments, payments (all 3 types), bank accounts, pricing, reconciliation | Contract, Contract_Party, Person_Resides_Under, Installment, Payment, Installment_Payment, Work_Order_Payment, Payroll_Payment, Bank_Account, Unit_Pricing_History |
| `SECURITY_OFFICER` | Security manager / supervisor | Access permits, gates, guard assignments, vehicle registry, entry logs, blacklisting | Access_Permit, Vehicle, Vehicle_Permits, Gate_Guard_Assignment, Gate, Enters_At (read) |
| `FACILITY_OFFICER` | Maintenance / ops manager | Work orders, facilities, buildings, units, vendor assignments, facility hours | Work_Order, Work_Order_Assigned_To, Facility, Facility_Hours_History, Facility_Manager, Building, Unit, Unit_Status_History |
| `DEPARTMENT_MANAGER` | Department heads | Own department only: task assignments, attendance, KPI scoring, performance reviews, supervision | Assigned_Task, Attends, Staff_KPI_Record, Staff_KPI_Monthly_Summary, Staff_Performance_Review, Person_Supervision, Department_Managers (read own) |
| `SUPERVISOR` | Team leads within a department | Own team only (via Person_Supervision): views supervisees' attendance, KPI, tasks. Can file internal reports. | Internal_Report (write), Person_Supervision (read own team) |
| `GATE_GUARD` | Security guard at a gate | Gate entry logging only — most restricted write role | Enters_At (write), Access_Permit (read/verify), Gate_Guard_Assignment (read own) |
| `STAFF` | Regular employees | Read-only access to own records: attendance, tasks, salary, profile | Internal_Report (write own) |
| `INVESTOR` | Compound investor | **Read-only dashboard.** Investment stakes, compound financial summaries, occupancy rates. Non-staff. | None (read-only) |
| `APPLICANT` | Job applicants | Job portal only: browse positions, submit applications, upload CV. Non-staff. | Application (write own), Person (write own profile) |

> **Role model**: 12 roles. 9 staff-based + 3 non-staff
> (`ADMIN`, `INVESTOR`, `APPLICANT`). Leasing and sales duties are handled by
> `ACCOUNTANT` because this product treats contracts, pricing, installments,
> and payment collection as finance work.

### Permission Policy

- Backend authorization is **default deny**: if an action is not explicitly
  allowed, the server must deny it.
- Frontend hiding controls is not security. The backend must enforce every
  permission and data-scope rule.
- Broad route access and resource ownership are separate checks. A role may be
  allowed to call an endpoint but still be denied for a specific record.
- `GENERAL_MANAGER` can access all business records.
- `ADMIN` is break-glass access for setup and recovery, not normal business
  operations.

### Data Scopes

| Scope | Meaning |
|-------|---------|
| Own record | Records linked to the logged-in person's `person_id`. |
| Own department | Staff, tasks, attendance, KPI, and reviews for the department the actor manages or belongs to. |
| Own team / supervisees | Staff linked through active `Person_Supervision` rows. |
| Assigned gate | Gate entry work for the guard's active gate assignment. |
| Assigned role inbox | Internal reports assigned to the actor's `system_role`. |
| Own application | Applications and interviews linked to the applicant's own person record. |
| Own investment stake | Investment records linked to the investor's own person record. |
| All business records | `GENERAL_MANAGER` business visibility across the compound. |
| Break-glass | `ADMIN` recovery access, limited by production operating policy. |

---

## 3. The Chicken-and-Egg Problem — Who Controls Whom?

> "Who changes a user's role? The HR manager? But who appointed the HR manager?
> And who appointed the person who appointed them?"

### The Answer: Cascading Authority Chain

```
ADMIN (bootstrap via POST /setup — IT guy)
  │
  ├── Creates AppUser for GENERAL_MANAGER
  │     │
  │     ├── Creates/promotes HR_OFFICER
  │     ├── Creates/promotes ACCOUNTANT
  │     ├── Creates/promotes SECURITY_OFFICER
  │     ├── Creates/promotes FACILITY_OFFICER
  │     └── Creates/promotes DEPARTMENT_MANAGER
  │           │
  │           ├── Creates/promotes SUPERVISOR within their department
  │           │     └── Views own team (Person_Supervision)
  │           └── Manages STAFF and GATE_GUARD within their department
  │
  ├── APPLICANT (self-registration via job portal — no staff involvement)
  │     └── On hire: HR changes role APPLICANT → STAFF (or higher)
  │
  ├── INVESTOR (created by GENERAL_MANAGER or ACCOUNTANT finance authority)
  │     └── Read-only dashboard, no management capabilities
  │
  └── Emergency: can change ANY user's role (break-glass)
```

### The Rules:

| Action | Who can do it? | Rationale |
|--------|---------------|-----------|
| Create first ADMIN account | Nobody (POST /setup, one-time) | Bootstrap |
| Create GENERAL_MANAGER account | ADMIN only | IT sets up the owner |
| Create officer-level accounts (HR, Finance, Security, Facility) | ADMIN or GENERAL_MANAGER | Owner appoints their leadership team |
| Create DEPARTMENT_MANAGER account | ADMIN, GENERAL_MANAGER, or HR_OFFICER | HR onboards department heads |
| Create STAFF / GATE_GUARD account | ADMIN, GENERAL_MANAGER, HR_OFFICER, or DEPARTMENT_MANAGER | Lowest-level account creation |
| Create INVESTOR account | GENERAL_MANAGER or ACCOUNTANT system role | Investor access is provisioned after the business investment is recorded; this is a finance/owner workflow, not self-registration and not a department-manager permission |
| Change someone's system_role | ADMIN or GENERAL_MANAGER (anyone) · HR_OFFICER (below officer level only) | Promotion/demotion is an HR function, but you can't promote yourself above your own level |
| Deactivate an account (is_active = false) | Same rules as role change | Firing / access revocation |
| **Cannot ever do**: change your OWN role | Nobody | Prevents privilege escalation |

> **This is a business rule**, not a database constraint. Enforced in `AppUserService` + `AppUserRules.java`.

---

## 4. Functional Requirements by Role

> **Role Inheritance**: Every staff-based system role (HR_OFFICER, ACCOUNTANT,
> SECURITY_OFFICER, FACILITY_OFFICER, DEPARTMENT_MANAGER, GATE_GUARD) **inherits
> STAFF permissions**. An HR_OFFICER can do everything STAFF can do (view own
> paycheck, own attendance) PLUS their HR-specific operations.
>
> **"Can view" items** = frontend pages/dashboards. **"Can create/update/delete"** = API actions.

### STAFF (base permissions — inherited by all staff roles)

```
- Can view own attendance records (Attends — own rows only)
- Can view own daily task assignments (Assigned_Task — own rows only)
- Can view own salary history (Staff_Salary_History — own rows only)
- Can view own profile (Staff_Profile — own row only)
- Can view own KPI records (Staff_KPI_Record — own rows only)
- Can view own KPI monthly summaries (Staff_KPI_Monthly_Summary — own rows only)
- Can view own performance reviews (Staff_Performance_Review — own rows only)
- Can view own payroll history (Task_Monthly_Salary — own rows only)
- Can update own contact info (Person_Phone, Person_Email)
- Can view own position history (Staff_Position_History — own rows only)
- Can view own bank accounts (Bank_Account — own rows only)
```

### ADMIN

```
- Can create/update/deactivate any AppUser account
- Can assign any system_role to any user (including GENERAL_MANAGER)
- Can access all endpoints (break-glass emergency, not for daily use)
- Cannot be created after initial setup (POST /setup is one-time)
- Is NOT a business role — no compound dashboards, no financial views
- In production: deactivated after initial setup (is_active = false)
```

### GENERAL_MANAGER

```
- Can access ALL business endpoints (the real "sees everything" role)
- Can create/update/deactivate AppUser accounts (except ADMIN)
- Can view financial dashboard: total revenue, total expenses, net income
- Can view occupancy dashboard: units occupied vs vacant, occupancy rate
- Can view all investments (Person_Invests_in_Compound — all records)
- Can approve new investment records
- Can view all investors and their stakes
- Can manage compound settings (Compound — name, location)
- Can manage all buildings (Building — create, update)
- Can manage all gates (Gate — create, update, change status)
- Can view all contracts, payments, staff, and operational data
- Can override any department-level decision
- Can view all departments and their managers
- Can view compound-level bank accounts
- Inherits: all STAFF permissions (view own records)
```

### HR_OFFICER

> **User Story**: The HR Officer manages the hiring pipeline, staff profiles,
> positions, salary structures, KPI policies, and performance reviews.
> They also handle shift attendance rules and department management assignments.

```
Recruitment:
- Can view all incoming job applications (Applications — all records)
- Can create an Application on behalf of an applicant
- Can schedule interviews by creating Recruitment records
- Can record interview results on Recruitment records
- Can create Hire_Agreement when an applicant passes interviews
- Can view all Recruitment and Hire_Agreement records

Staff Management:
- Can create Person records (for new staff)
- Can assign the Staff business role to a person (Person_Role)
- Can create Staff_Profile for a newly hired person
- Can view all Staff_Profile records across the compound
- Can create/update Staff_Position catalog entries (job titles per department)
- Can create/update Position_Salary_History (salary bands for positions)
- Can create Staff_Position_History records (assign/transfer/promote staff)
- Can create Staff_Salary_History records (individual salary changes/raises)
- Can view all staff position and salary histories

KPI & Performance:
- Can create/update KPI_Policy (scoring tiers per department)
- Can view all Staff_KPI_Record and Staff_KPI_Monthly_Summary records
- Can view all Staff_Performance_Review records

Attendance & Shifts:
- Can create/update Shift_Attendance_Type catalog entries
- Can create/update Law_of_Shift_Attendance rules (shift hours, bonus/deduction rates)
- Can view all attendance records (Attends — all staff)

Department Admin:
- Can assign Department_Managers (who manages which department)
- Can create/update Person_Supervision records
- Can view all Department and Department_Location_History records

Inherits: all STAFF permissions (view own paycheck, own attendance, etc.)
```

### ACCOUNTANT

> **User Story**: The Accountant manages the financial lifecycle — leasing
> units/facilities to tenants via contracts, tracking installments, processing
> all payments (rent, vendor invoices, payroll), and managing bank accounts.

```
Contracts & Leasing:
- Can create/update Contract records (residential and commercial)
- Can add/update Contract_Party records (who signed)
- Can add/update Person_Resides_Under records (who lives in the unit)
- Can view all contracts, parties, and residency records

Installments:
- Can create Installment schedules for a contract
- Can update Installment status (Pending → Paid, Overdue, etc.)
- Can view all installments across all contracts

Payments:
- Can create Payment records (the central ledger entry)
- Can create Installment_Payment (tenant paying rent)
- Can create Work_Order_Payment (paying a vendor for maintenance)
- Can create Payroll_Payment (paying staff salaries)
- Can update Payment reconciliation status
- Can view all payment records and payment history

Bank Accounts:
- Can create/update Bank_Account records (compound, person, or company)
- Can view all bank accounts

Pricing:
- Can create Unit_Pricing_History records (set/update unit listing prices)
- Can view all pricing history

Payroll Processing:
- Can view Task_Monthly_Salary records (monthly payroll calculations)
- Can trigger payroll disbursement (creates Payroll_Payment from Task_Monthly_Salary)

Inherits: all STAFF permissions
```

### SECURITY_OFFICER

> **User Story**: The Security Officer manages compound access — who can enter,
> what permits exist, which guards are posted where, and which vehicles are registered.

```
Access Permits:
- Can create/update Access_Permit records (staff badges, resident cards, visitor passes)
- Can revoke permits (update permit_status)
- Can view all access permits

Vehicles:
- Can register vehicles (create Vehicle records)
- Can update vehicle ownership
- Can link vehicles to permits (Vehicle_Permits)
- Can view all registered vehicles

Gate Management:
- Can create Gate_Guard_Assignment records (post guards at gates)
- Can view all gate guard assignments
- Can view all gate entry logs (Enters_At — all records)

Blacklisting:
- Can update Person.is_blacklisted flag
- Can view all blacklisted persons

Gate Catalog:
- Can create/update Gate records (gate name, type, status)
- Can view all gates

Inherits: all STAFF permissions
```

### FACILITY_OFFICER

> **User Story**: The Facility Officer manages physical infrastructure — buildings,
> units, facilities, work orders, and vendor assignments.

```
Work Orders:
- Can create Work_Order records (maintenance/service requests)
- Can update Work_Order status (Scheduled → In Progress → Completed)
- Can assign vendors to work orders (Work_Order_Assigned_To)
- Can view all work orders

Facilities:
- Can create/update Facility records (gym, pool, parking, etc.)
- Can create/update Facility_Hours_History (opening/closing times)
- Can assign Facility_Manager records (who manages which facility)
- Can view all facilities and their histories

Buildings & Units:
- Can create/update Building records
- Can create/update Unit records
- Can create Unit_Status_History records (Vacant → Occupied → Under Maintenance)
- Can view all buildings, units, and status histories

Companies (Vendors):
- Can create/update Company records (external vendors)
- Can view all company records
- Can view Person_Works_for_Company records

Inherits: all STAFF permissions
```

### DEPARTMENT_MANAGER

> **User Story**: The Department Manager oversees their OWN department's daily
> operations — task assignments, attendance tracking, KPI scoring, and performance
> reviews. Their scope is limited to staff within their department (determined by
> their row in `Department_Managers`).

```
Task Assignments (own department only):
- Can create Assigned_Task records (assign staff to tasks for a date)
- Can update/delete Assigned_Task records
- Can view all task assignments in their department

Attendance (own department only):
- Can create/update Attends records (daily check-in/out, absence marking)
- Can view all attendance records in their department

KPI Scoring (own department only):
- Can create Staff_KPI_Record entries (daily KPI score per staff member)
- Can close monthly KPI (create Staff_KPI_Monthly_Summary at month-end)
- Can view all KPI records and summaries in their department

Performance Reviews (own department only):
- Can create Staff_Performance_Review for staff they supervise
- Can mark resulted_in_promotion or resulted_in_raise flags
- Can view all performance reviews in their department

Supervision (own department only):
- Can view Person_Supervision records within their department
- Can view their department's staff list

Inherits: all STAFF permissions
```

### GATE_GUARD

> **User Story**: The Gate Guard is posted at a specific gate and processes
> entry/exit events. Most restricted write role in the system.

```
- Can create Enters_At records (log gate entry/exit events)
- Can read/verify Access_Permit records (scan a permit, check validity)
- Can view own Gate_Guard_Assignment (which gate am I posted at?)
- Can view own Assigned_Task (what shift am I on?)
- Cannot create permits, cannot register vehicles, cannot manage anything
- Inherits: all STAFF permissions
```

### SUPERVISOR

> **User Story**: The Supervisor is a team lead within a department. They can
> see their direct reports' data (via Person_Supervision) and file internal
> reports to HR or management on behalf of their team.

```
Team Oversight (own supervisees only — via Person_Supervision):
- Can view supervisees' attendance records (Attends)
- Can view supervisees' task assignments (Assigned_Task)
- Can view supervisees' KPI records (Staff_KPI_Record)
- Can view supervisees' performance reviews (Staff_Performance_Review)

Internal Reporting:
- Can create Internal_Report (e.g. "request salary raise for team member")
- Can view own submitted reports and their status
- Can view reports assigned to SUPERVISOR role (pool model)
- Can mark reports as read/unread

Inherits: all STAFF permissions (view own paycheck, own attendance, etc.)
```

### APPLICANT

> **User Story**: The Applicant is a job seeker who registers on the portal.
> They are NOT staff — they have no access to any operational data.

```
- Can self-register (creates own Person + AppUser with role = APPLICANT)
- Can update own profile (name, contact info, qualifications)
- Can upload the current CV for each application (binary file in MinIO;
  current object metadata on the Applications row)
- Re-uploading a CV for the same application replaces that application's current
  CV reference; applying again can carry a different CV on the new Application
- Full previous-CV history and general applicant document history are deferred
  to a later document-metadata feature
- Can view open positions (Staff_Position catalog — read only)
- Can submit Applications (applicant_id = self, position_id, application_date)
- Can view own application status and history
- Can view own interview schedules (Recruitment — own rows only)
- Cannot access any staff, financial, security, or operational endpoints
- Does NOT inherit STAFF permissions (not an employee)
- On hire: HR changes system_role from APPLICANT → STAFF (or higher)
```

### INVESTOR

```
- Receives an INVESTOR login account from GENERAL_MANAGER or the ACCOUNTANT
  system role after the business investment is recorded; investors do not
  self-register
- Can view own investment stakes (Person_Invests_in_Compound — own records)
- Can view compound financial summaries (aggregate Payment data — read only)
- Can view occupancy rates (aggregate Contract/Unit_Status data — read only)
- Cannot modify any data
- Cannot access any staff, HR, security, or operational endpoints
- Does NOT inherit STAFF permissions (not employed by the compound)
```

---

## 5. Business Rules

> Each rule maps to: a `Rules.java` class (application enforcement)
> and optionally a V2 CHECK constraint (DB safety net).

### Person Rules

```
- Every Person must have at least one Role at creation
  → PersonRules.java (@Transactional — insert Person + Person_Role atomically)

- A person cannot supervise themselves (supervisor_id ≠ supervisee_id)
  → PersonSupervisionRules.java + V2 chk_no_self_supervision

- At least one phone number and one email required for adult persons
  (minors exempt — household_relationship = 'Child' in Person_Resides_Under)
  → PersonRules.java

- Gender must be one of: 'Male', 'Female'
  → PersonRules.java + V2 chk_person_gender

- Egyptian nationals must provide egyptian_national_id (14 digits)
  → PersonRules.java

- Blacklisted persons cannot be issued new Access_Permits
  → AccessPermitRules.java
```

### Supervision Rules

```
- A person cannot supervise themselves
  → Schema safety net: chk_no_self_supervision. Service enforcement remains
    explicit in supervision workflows.

- Open question: should supervision cycles be rejected across the whole chain?

- Open question: must supervisor and supervisee belong to the same department?

- Open question: can a supervisee have more than one active supervisor?

- Open question: must every supervision chain end at the department manager?
```

### Staff & Position Rules

```
- Staff_Profile can only exist for persons with the 'Staff' business role
  → StaffProfileRules.java

- A staff member can hold only ONE active position (end_date IS NULL)
  → StaffPositionHistoryRules.java

- A department can have only ONE active manager (management_end_date IS NULL)
  → DepartmentManagerRules.java

- Position_Salary_History: base_daily_rate and maximum_salary must be > 0
  → PositionSalaryRules.java

- Staff_Salary_History: individual maximum_salary ≤ active Position_Salary_History.maximum_salary
  → StaffSalaryRules.java

- authorized_by_id on Staff_Position_History is NULL only for the initial hire
  → StaffPositionHistoryRules.java
```

### Contract Rules

```
- A contract covers exactly one target (unit OR facility, not both)
  → ContractRules.java + V2 chk_contract_target

- Each contract must have exactly one Primary Signer (Contract_Party.role = 'Primary Signer')
  → ContractRules.java + V2 chk_contract_primary_signer

- Contract end_date must be >= start_date (if set)
  → ContractRules.java

- Contracts are never deleted — closed by status change (Terminated, Expired)
  → ContractRules.java (no delete endpoint)

- contract_reference must be unique
  → ContractRules.java + DB UNIQUE constraint
```

### Payment Rules

```
- Each Payment has exactly one child row (Installment_Payment OR Work_Order_Payment OR Payroll_Payment)
  → PaymentRules.java

- payment_type discriminator must match the child table type
  → PaymentRules.java

- Payment amount must be > 0
  → PaymentRules.java

- Every monetary amount must carry an explicit currency
  → Money value object + matching database currency column where applicable

- Money can only be added, compared, or rolled up within the same currency
  unless a later exchange-rate workflow explicitly converts it
  → Money value object + workflow rules

- Payments are never deleted — financial records are permanent
  → PaymentRules.java (no delete endpoint)

- Payroll_Payment FK (staff_id, department_id, year, month) must reference
  an existing Task_Monthly_Salary row
  → PayrollPaymentRules.java
```

### Recruitment Rules

```
- A person cannot apply for the same position on the same date twice
  → ApplicationRules.java (enforced by composite PK)

- Hire_Agreement can only be created if at least one Recruitment has interview_result = 'Pass'
  → HireAgreementRules.java

- employment_start_date must be >= application_date
  → HireAgreementRules.java

- offered_base_daily_rate must be > 0
  → HireAgreementRules.java

- When Hire_Agreement is created: system must create Staff_Profile + Staff_Position_History
  + initial Staff_Salary_History in the same transaction
  → HiringWorkflowService.java (@Transactional)
```

### Attendance & Payroll Rules

```
- A staff member cannot have two Attends rows for same shift + date
  → AttendsRules.java (enforced by composite PK)

- daily_salary, daily_bonus, daily_deduction, and daily_net_salary are computed snapshots — frozen after payroll close. During payroll close, the backend fills any missing Attends snapshot amounts from the active Staff_Salary_History and Law_of_Shift_Attendance for the attendance date: daily_salary uses the staff base_daily_rate (or zero when absent), daily_bonus uses positive diff_hour × one_hour_extra_bonus, daily_deduction uses absolute negative diff_hour × one_hour_diff_discount, and daily_net_salary is salary + bonus - deduction, floored at zero.
  → AttendsRules.java / PayrollService.java (amount and currency immutable after month close)

- Task_Monthly_Salary monthly_net_salary ≤ Staff_Salary_History.maximum_salary
  → PayrollRules.java

- Task_Monthly_Salary values are snapshots — do not recalculate after month is closed
  → PayrollRules.java (amount and currency immutable after close)

- A staff member must have an Assigned_Task before an Attends row can be created for that date
  → AttendsRules.java
```

### KPI Rules

```
- KPI_Policy tiers must not overlap within the same department policy version
  (same department_id + effective_date, min_kpi_score/max_kpi_score ranges)
  → KpiPolicyRules.java

- Staff_KPI_Record: kpi_policy_id must reference the latest active policy version
  for the department on that date, and kpi_score must fall inside the selected tier range
  → StaffKpiRecordRules.java + KpiPolicyRules.java

- Staff_KPI_Monthly_Summary applicable_tier/payroll rates must be copied from
  the latest active KPI_Policy tier that matches avg_kpi_score at period close
  → StaffKpiMonthlySummaryRules.java + KpiPolicyRules.java

- Staff_KPI_Monthly_Summary values are snapshots — do not recalculate after month close
  → StaffKpiMonthlySummaryRules.java

- closed_by_id must be the department manager or HR_OFFICER
  → StaffKpiMonthlySummaryRules.java
```

### Access Control Rules

```
- One active permit per person per type (partial unique index in V2)
  → AccessPermitRules.java

- Each permit must reference exactly one entitlement basis:
  Staff Badge → staff_profile_id | Resident Card → contract_id |
  Contractor Pass → work_order_id | Visitor Pass → invited_by_id
  → AccessPermitRules.java + V2 chk_permit_entitlement

- Enters_At: exactly one of permit_id or manual_plate_entry must be set
  → EntersAtRules.java + V2 chk_entry_source

- processed_by_id is required for anonymous entries (no permit)
  → EntersAtRules.java

- Permits are revoked by status change, never deleted
  → AccessPermitRules.java (no delete endpoint)
```

### Vehicle & Bank Account Rules

```
- Vehicle owner is exactly one of: Person, Department, or Company
  → VehicleRules.java + V2 chk_vehicle_owner

- Bank_Account owner is exactly one of: Compound, Person, or Company
  → BankAccountRules.java + V2 chk_bank_account_owner

- Facility management_type = 'Vendor' requires managed_by_company_id; 'Compound' requires NULL
  → FacilityRules.java + V2 chk_facility_management

- Gate gate_no must be unique system-wide
  → GateRules.java + DB UNIQUE constraint

- Unit unit_no must be unique within its building
  → UnitRules.java
```

### User Account Rules

```
- Cannot change your own system_role
  → AppUserRules.java

- Cannot promote someone to a role >= your own level (unless ADMIN)
  → AppUserRules.java

- Cannot deactivate your own account
  → AppUserRules.java

- DEPARTMENT_MANAGER can only create STAFF and GATE_GUARD accounts
  → AppUserRules.java

- POST /setup only works when App_User table is empty
  → AuthRules.java
```

### Time Policy

```
- Business timezone is Africa/Cairo.

- Backend code must not rely on the server default timezone for business dates,
  month closes, permit validity, payroll periods, or KPI periods.

- Date/time behavior must use timezone database rules so summer/winter time
  changes follow the official Africa/Cairo timezone.

- Open question: exact cutoff times for payroll close, KPI close, permit
  expiry, and overdue installment classification.
```

---

## 6. User Stories (Multi-Step Workflows)

> Each user story describes a real-world process that spans multiple tables.
> Each becomes one or more `@Transactional` service methods.

### US-1: Hiring Pipeline

> As an **HR_OFFICER**, I want to process a job application from submission
> through interviews to onboarding, so that a new employee is fully set up.

```
1. Applicant submits details → HR creates Person record + assigns "Staff" role (Person_Role)
2. HR creates Application (applicant_id, position_id, application_date)
3. HR schedules interview → creates Recruitment record (interviewer, date)
4. Interviewer records interview_result ('Pass' / 'Fail' / 'Pending')
5. If multiple interviews needed → repeat step 3-4
6. If at least one 'Pass' → HR creates Hire_Agreement (salary terms, start date)
7. On hire approval (single @Transactional):
   a. Create Staff_Profile (qualification, CV URL)
   b. Create Staff_Position_History (position, effective_date = employment_start_date)
   c. Create Staff_Salary_History (offered_base_daily_rate, maximum_salary)
   d. Optionally: create AppUser account (email + system_role = STAFF)
   e. Optionally: create Bank_Account for salary payments
```

### US-2: Monthly Payroll Close

> As an **ACCOUNTANT**, I want to close the month's payroll so that
> every staff member receives their calculated salary payment.

```
1. Throughout the month: DEPARTMENT_MANAGER records daily Attends rows
   (check_in, check_out, daily_salary computed from Law_of_Shift_Attendance)
2. At month-end: system aggregates daily Attends → creates Task_Monthly_Salary
   (monthly_salary, monthly_bonus, monthly_deduction, tax, monthly_net_salary)
3. HR/GM reviews and approves the Task_Monthly_Salary records
4. ACCOUNTANT creates Payment (direction = 'Outbound', type = 'Payroll')
5. ACCOUNTANT creates Payroll_Payment (links Payment to Task_Monthly_Salary)
6. After disbursement: ACCOUNTANT updates Payment.reconciliation_status → 'Reconciled'
7. Monthly salary snapshots are now FROZEN — cannot be recalculated
```

### US-3: Contract Lifecycle (Leasing)

> As an **ACCOUNTANT**, I want to lease a unit to a tenant by creating
> a contract with installments, so that rent payments are tracked.

```
1. ACCOUNTANT creates Contract (type = 'Residential', unit_id, start/end date, price)
2. ACCOUNTANT adds Contract_Party rows (Primary Signer + any co-signers)
3. ACCOUNTANT adds Person_Resides_Under rows (who actually lives there)
4. ACCOUNTANT generates Installment schedule (monthly due dates, expected amounts)
5. Each month: tenant pays → ACCOUNTANT creates Payment + Installment_Payment
6. ACCOUNTANT updates Installment.installment_status → 'Paid'
7. If overdue: late_fee_amount is added to the Installment_Payment
8. At contract end: ACCOUNTANT updates Contract.contract_status → 'Expired'
9. If renewed: new Contract created, old one stays as historical record
```

### US-4: Gate Entry (Permit Scan)

> As a **GATE_GUARD**, I want to process a registered person entering
> the compound by scanning their permit.

```
1. Person presents permit at gate
2. GATE_GUARD scans permit → system looks up Access_Permit by permit_no
3. System validates: permit_status = 'Active', expiry_date >= today,
   person is not blacklisted
4. If valid → GATE_GUARD creates Enters_At record:
   gate_id, permit_id, entered_at = now(), direction = 'In'
5. processed_by_id = optional (permit scan is self-service)
```

### US-5: Gate Entry (Anonymous Vehicle)

> As a **GATE_GUARD**, I want to log an unregistered vehicle (delivery,
> guest) entering the compound.

```
1. Vehicle arrives without a registered permit
2. GATE_GUARD records the plate number manually
3. GATE_GUARD asks who the visitor is here to see
4. GATE_GUARD creates Enters_At record:
   gate_id, manual_plate_entry = plate_no, permit_id = NULL,
   direction = 'In', purpose = 'Delivery',
   processed_by_id = guard's person_id (REQUIRED for anonymous),
   requested_by_id = the resident expecting the visitor
```

### US-6: Work Order Lifecycle

> As a **FACILITY_OFFICER**, I want to create and track a maintenance
> request from creation through vendor assignment to payment.

```
1. FACILITY_OFFICER or tenant (via officer) creates Work_Order
   (requester_id, description, priority, service_category, facility_id)
2. FACILITY_OFFICER assigns vendor → creates Work_Order_Assigned_To
   (company_id, date_assigned)
3. FACILITY_OFFICER updates Work_Order.job_status as work progresses
   (Scheduled → In Progress → Completed)
4. On completion: FACILITY_OFFICER sets date_completed and cost_amount
5. ACCOUNTANT creates Payment (direction = 'Outbound', type = 'WorkOrder')
6. ACCOUNTANT creates Work_Order_Payment (links Payment to Work_Order)
```

### US-7: Performance Review → Salary Raise

> As a **DEPARTMENT_MANAGER**, I want to conduct a performance review
> that can result in a promotion or salary raise.

```
1. DEPARTMENT_MANAGER creates Staff_Performance_Review
   (staff_id, reviewer_id = self, department_id, review_date, overall_rating)
2. If resulted_in_raise = true (single @Transactional):
   a. Close current Staff_Salary_History (set end_date = today)
   b. Create new Staff_Salary_History (new rate, approved_by_id, review_id)
3. If resulted_in_promotion = true (single @Transactional):
   a. Close current Staff_Position_History (set end_date = today)
   b. Create new Staff_Position_History (new position, authorized_by_id)
   c. Optionally also create new salary row (step 2)
```

### US-8: KPI Month-End Close

> As a **DEPARTMENT_MANAGER**, I want to close the monthly KPI scores
> so that bonuses and deductions are locked for payroll.

```
1. Throughout the month: DEPARTMENT_MANAGER records daily Staff_KPI_Record
   (kpi_score per staff member, linked to active KPI_Policy tier)
2. At month-end: DEPARTMENT_MANAGER triggers close
3. System aggregates daily records → creates Staff_KPI_Monthly_Summary
   (avg_kpi_score, total_kpi_score, days_scored, applicable_tier,
    payroll_bonus_rate, payroll_deduct_rate copied from the latest active
    KPI_Policy tier matching avg_kpi_score at period close)
4. closed_by_id = the manager who triggered the close
5. Summary values are now FROZEN — used directly by payroll (US-2)
```

### US-9: Internal Reporting (Ticketing)

> As a **SUPERVISOR** or **STAFF**, I want to file a report (e.g. salary
> raise request, complaint, maintenance issue) that gets routed to the
> appropriate role group, so that someone in authority can review and act on it.

```
1. STAFF/SUPERVISOR creates Internal_Report:
   reporter_id = self, assigned_to_role = 'HR_OFFICER' (or other role),
   subject, body, report_category = 'Salary_Request', priority = 'Normal'
2. Report appears in HR_OFFICER's inbox (all HR officers see it — pool model)
3. First HR officer to open it → system sets is_read = true, read_by_id, read_at
4. HR officer reviews and takes action (may create Staff_Salary_History, etc.)
5. HR officer updates report: report_status → 'Resolved',
   resolved_by_id, resolved_at, resolution_note
6. Reporter can view the resolution on their dashboard
7. "Notification" = frontend checks: count of Internal_Report WHERE
   assigned_to_role = my_role AND is_read = false → shows badge number
8. Any reader can mark_as_unread (is_read = false) to flag for re-review
```

### US-10: Applicant Self-Registration

> As a **job seeker**, I want to register on the portal, browse open
> positions, and submit my application without staff involvement.

```
1. Applicant visits /register → creates own Person record + AppUser (role = APPLICANT)
2. APPLICANT fills in profile: name, contact info, qualifications
3. APPLICANT browses open positions (Staff_Position catalog — public read)
4. APPLICANT submits Application (applicant_id = self, position_id, date)
5. APPLICANT uploads or replaces the current CV for that application (file
   stored in MinIO; object metadata saved on the Applications row, not Person
   and not Staff_Profile). The upload may be part of the application-submission
   request or a follow-up action against the created Application.
6. APPLICANT can list their own Applications and interview schedule/history,
   while HR retains broad Recruitment/Hire_Agreement review access.
7. From here → US-1 (Hiring Pipeline) takes over at step 3
8. On hire: HR changes AppUser.system_role from APPLICANT → STAFF
9. Old APPLICANT account is consumed — they now see the STAFF dashboard
```

---

## 7. Product Contracts and Deferred Open Questions

This section captures accepted product contracts plus remaining product
decisions that are intentionally deferred until the owning workflow is
implemented.

### Applicant Portal

```
- Applicant login through APPLICANT system role.
- Own profile management.
- Own applications and interview schedule/history.
- Current application CV upload/download through MinIO using object keys stored
  on Applications and short-lived response-time download URLs.
- Current CV metadata belongs to the Applications row; re-upload for the same
  application overwrites the current reference.
- Full previous-CV history and general applicant document history through a
  metadata table are deferred.
```

### Investor Portal

```
- Investor login through INVESTOR system role provisioned by GENERAL_MANAGER
  or the ACCOUNTANT system role after the investment is recorded.
- Investor accounts are not self-registered and are not created by a
  DEPARTMENT_MANAGER who merely manages an accounting department.
- Own investment stake visibility.
- Read-only dashboard.
- Aggregate financial and occupancy statistics.
- No operational write access.
```

### Payment Provider Demo

```
- Fake provider interface for demos, e.g. fake Fawry.
- Payment attempt history with provider reference and provider status.
- No raw card storage.
- Masked card display only if a UI needs it.
- Open question: exact provider statuses and retry/cancel behavior.
```

### Supervision Policy

```
- No self-supervision is required.
- Decide whether supervision cycles are forbidden.
- Decide whether supervisor and supervisee must share a department.
- Decide whether there can be only one active supervisor per supervisee.
- Decide whether every supervision chain must terminate at a department manager.
```

### Time and Calendar Policy

```
- Business timezone is Africa/Cairo.
- Do not depend on server default timezone.
- Use timezone database behavior for summer/winter time.
- Decide exact close/cutoff times before implementing payroll, KPI, permit,
  and installment overdue automation.
```

---

## 8. Admin Production Lockdown

When the system goes to production, the ADMIN account should be treated as a
"break-glass" emergency access account — not an everyday login.

### Standard Practice:

```
1. Day 1: IT runs POST /setup → creates ADMIN account
2. Day 1: ADMIN creates GENERAL_MANAGER account for the compound owner
3. Day 1: GENERAL_MANAGER creates all officer-level accounts
4. Day 2: IT deactivates the ADMIN account (is_active = false)
5. Normal ops: Everyone uses their real role account

Emergency: DBA runs direct SQL to re-enable ADMIN:
   UPDATE app_user SET is_active = true WHERE system_role = 'ADMIN';
   -- After emergency, deactivate again immediately.
```

### Why not just delete the ADMIN account?

- Audit trail: the ADMIN's `created_by` stamps appear on the initial accounts
- If GENERAL_MANAGER's account gets locked out, you need a recovery path
- `is_active = false` is reversible; `DELETE` is not

### Code implementation:

```java
// In AppUserRules.java:
public void validateRoleChangeAllowed(SystemRole changerRole, SystemRole targetRole) {
    // Cannot change your own role
    // Cannot promote someone to a role >= your own (unless ADMIN)
    // ADMIN can do anything
}
```

---

## Appendix A: Security Layer Mapping

Engineering mapping:

| This document section | Maps to code layer |
|----------------------|-------------------|
| System Roles | `SystemRole.java` enum + `V4__add_auth_constraints.sql` |
| Permissions / "who can do it" | Security configuration, role-policy classes, and method-level checks |
| Data scope / "which records" | Service and rules classes using repository-backed ownership checks |
| Business Rules | `{Feature}Rules.java` |
| Workflows | `{Feature}Service.java` (`@Transactional`) |
| Functional Requirements | Controller endpoints + Service methods |

---

## Appendix B: AppUser Management — Endpoints & DTOs

AppUser was originally just an auth flow (login/setup/refresh → no DTOs needed).
Now that user account management is a feature (create accounts, change roles,
deactivate), AppUser becomes a **managed resource** and needs DTOs.

### New endpoints needed:

| Method | Path | Role required | What it does |
|--------|------|--------------|-------------|
| `POST` | `/api/v1/users` | ADMIN, GENERAL_MANAGER, HR_OFFICER, DEPARTMENT_MANAGER, ACCOUNTANT for INVESTOR only | Create a new AppUser account |
| `GET` | `/api/v1/users` | ADMIN, GENERAL_MANAGER, HR_OFFICER | List all user accounts (paginated) |
| `GET` | `/api/v1/users/{id}` | ADMIN, GENERAL_MANAGER, HR_OFFICER | Get one user account |
| `PUT` | `/api/v1/users/{id}/role` | ADMIN, GENERAL_MANAGER, HR_OFFICER | Change a user's system_role |
| `PUT` | `/api/v1/users/{id}/status` | ADMIN, GENERAL_MANAGER, HR_OFFICER | Activate/deactivate a user |

### New DTOs:

```
auth/dto/
  CreateAppUserRequest.java    → email, password, systemRole, personId (nullable)
  UpdateUserRoleRequest.java   → systemRole
  UpdateUserStatusRequest.java → isActive
  AppUserResponse.java         → id, email, systemRole, isActive, personId,
                                  forcePasswordChange, createdAt
                                  (NEVER includes passwordHash)
```

### New Rules (AppUserRules.java):

```
- Cannot change your own role
- Cannot promote someone to a role >= your own level (unless ADMIN)
- Cannot deactivate your own account
- DEPARTMENT_MANAGER can only create STAFF and GATE_GUARD accounts
- HR_OFFICER can only create/promote up to DEPARTMENT_MANAGER level
- GENERAL_MANAGER can create/promote up to SECURITY_OFFICER level
- ACCOUNTANT can create INVESTOR accounts only as part of the investment
  onboarding workflow
- Only ADMIN can create GENERAL_MANAGER accounts
```

### Why AppUser didn't need DTOs before:

Auth endpoints are **action flows**, not resource CRUD:
- `POST /setup` → input: `SetupRequest {email, password}` → output: JWT tokens
- `POST /login` → input: `LoginRequest {email, password}` → output: JWT tokens

The frontend never "browsed" users. Now it does → now it needs DTOs.
