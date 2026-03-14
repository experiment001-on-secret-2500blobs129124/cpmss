# COMPOUND MANAGEMENT SYSTEM: SOFTWARE FUNCTIONAL REQUIREMENTS

---

## 1. INTRODUCTION & SCOPE

This document defines the software functional requirements for the Compound Management System (CPMSS). The system is an internal tool for compound staff to manage property assets, residents, contracts, security, financial operations, and maintenance activities.

Requirements are numbered using the format **FR-XX-YYY** where **XX** identifies the module and **YYY** is a sequential number within that module.

| Module Code | Module Name |
|---|---|
| CE | Core Entities |
| OS | Organizational Structure |
| AC | Access Control |
| CR | Contract & Residency |
| FN | Financial |
| WO | Work Orders |
| HR | Human Resources |
| IA | Infrastructure & Assets |
| CM | Cross-Module Integration |
| RP | Reporting |

---

## 2. CORE ENTITIES

### 2.1 Person

The system must maintain a central registry of all individuals who interact with the compound in any capacity. Each person is uniquely identified by their National ID.

#### Stored Data

| Attribute | Description |
|---|---|
| National ID | Unique identifier (primary key) |
| First Name | Part of full name (composite) |
| Last Name | Part of full name (composite) |
| Nationality | Country of citizenship |
| Phone Number (up to 2) | Composite: Country Code + Phone Number (multivalued) |
| Email Address (up to 2) | Contact emails (multivalued) |
| City | Part of physical address (composite) |
| Street | Part of physical address (composite) |
| Date of Birth | For age verification and legal purposes |
| Gender | For demographic reporting |
| Person Type | Discriminator: "Staff", "Tenant", "Investor", "Visitor" |
| Qualification | Educational or professional credentials |
| Is Blacklisted | Boolean flag for security and screening |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-001 | A person's National ID may be updated only by users with Manager or Admin role. The system must log all National ID changes with: old value, new value, who changed it, and timestamp. |
| FR-CE-002 | The person_type attribute must store the highest-priority role according to the hierarchy: **Staff > Investor > Tenant > Visitor**. |
| FR-CE-003 | A person may participate in multiple roles simultaneously through relationships (e.g., a staff member who also rents a unit). |
| FR-CE-004 | Actual role determination must be derived from relationships: **Staff** = has an Employment_Offers record; **Investor** = has a Person_Invests_in_Compound record; **Tenant** = has a Person_Parties_to_Unit_or_Facility record with role = "Primary Signer"; **Visitor** = default when no other relationships exist. |
| FR-CE-005 | A person may have at most 2 phone numbers and at most 2 email addresses. |

---

### 2.1.1 Person Invests in Compound

The system must track investment relationships between persons and the compound.

#### Stored Data

| Attribute | Description |
|---|---|
| Investor National ID | Foreign key to Person |
| Compound ID | Foreign key to Compound |
| Timestamp | When the investment was recorded |
| Stock | Ownership percentage or share amount |

**Primary Key:** investor_national_id + compound_id + timestamp

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-006 | A person may have multiple investment records over time (timestamp in key allows this). |
| FR-CE-007 | The stock attribute represents ownership percentage or share count. |
| FR-CE-008 | Investors with person_type = "Investor" use this relationship for dividend payments. |

---

### 2.2 Compound

The system must store information about the top-level property entity. There is one Compound uniquely identified by a Compound ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Compound ID | Unique identifier (primary key) |
| Name | Official designation of the property |
| Country | Part of address (composite) |
| City | Part of address (composite) |
| District | Part of address (composite) |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-009 | The compound serves as the root entity in the physical hierarchy. All buildings, facilities, gates, and bank accounts ultimately belong to that only ONE compound. |

---

### 2.3 Building

The system must track all permanent structures within the compound. Each building is uniquely identified by a Building ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Building ID | Unique identifier (primary key) |
| Building Name | Official designation (e.g., "Tower A", "Villa Block", "Burj Elemarati") |
| Building Number | Physical identifier (may not be unique across compound) |
| Building Type | "Residential" (contains units ONLY) or "Non-Residential" (contains facilities ONLY) |
| Floors Count | Total number of floors |
| Construction Date | Original completion date |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-010 | Each building must belong to exactly one compound. |
| FR-CE-011 | Buildings of type "Residential" may contain Units only. Buildings of type "Non-Residential" may contain Facilities only. No mixing is allowed. |
| FR-CE-012 | The system must prevent creation of Units in Non-Residential buildings. |
| FR-CE-013 | The system must prevent creation of Facilities in Residential buildings. |

---

### 2.4 Unit

The system must store information about individual residential spaces available for lease or sale. Each unit is uniquely identified by a Unit ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Unit ID | Unique identifier (primary key) |
| Unit Number | Apartment/villa designation within the building |
| Floor Number | Location within the building |
| Number of Bedrooms | Count of sleeping rooms |
| Number of Bathrooms | Count of bathrooms |
| Number of Rooms | Total count of all rooms (excluding bathrooms) |
| Total Number of Rooms | **Derived:** Num Rooms + Num Bedrooms (auto-calculated) |
| Square Footage | Interior area measurement |
| Number of Balconies | Count of outdoor spaces |
| View Orientation | Direction the unit faces (e.g., "North", "South") |
| Current Status | State: "Vacant", "Occupied", "Reserved" |
| Water Meter Code | Utility tracking identifier |
| Gas Meter Code | Utility tracking identifier |
| Electricity Meter Code | Utility tracking identifier |

> [!IMPORTANT]
> **Listing Price** is NOT stored in the Unit table — it is tracked historically in Unit Pricing History (§2.4.1).
> **Current Status** is a cached state derived from active contracts and tracked historically in Unit Status History (§2.4.2).

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-014 | Each unit must belong to exactly one building. |
| FR-CE-015 | A unit cannot be covered by multiple active contracts simultaneously. The system must prevent activation of a contract for a unit that already has an active contract, raising an error: "Unit is currently occupied under Contract #X." |
| FR-CE-016 | Unit status is derived from the most recent Unit_Status_History record. A new history record is created automatically when contract status changes (e.g., contract activated → status becomes "Occupied"; contract terminated → status becomes "Vacant"). |
| FR-CE-017 | To retrieve the current listing price or status, query the most recent record from the respective history tables. |

---

### 2.4.1 Unit Pricing History

The system must track listing prices over time.

#### Stored Data

| Attribute | Description |
|---|---|
| Unit ID | Foreign key to Unit |
| Effective Date | When this price became active (part of PK) |
| Listing Price | Market asking price at this point in time |

**Primary Key:** unit_id + effective_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-018 | When listing price changes, a new row is inserted (history preserved, no updates). |
| FR-CE-019 | The current listing price is from the record with the most recent effective_date. |
| FR-CE-020 | Historical prices must be preserved for market analysis and audit purposes. |

---

### 2.4.2 Unit Status History

The system must track unit availability states over time.

#### Stored Data

| Attribute | Description |
|---|---|
| Unit ID | Foreign key to Unit |
| Date | When this status was recorded (part of PK) |
| Unit Status | "Vacant", "Occupied", "Under Maintenance", "Reserved" |

**Primary Key:** unit_id + date

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-021 | Status changes create new history records (not updates to existing records). |
| FR-CE-022 | The Unit table's current_status must match the most recent history record. |
| FR-CE-023 | The system must enable reporting on vacancy rates over time. |

---

### 2.5 Facility

The system must track shared amenities and commercial spaces within the compound. Each facility is uniquely identified by a Facility ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Facility ID | Unique identifier (primary key) |
| Facility Name | Descriptive identifier (e.g., "Main Gym", "Retail Shop #3") |
| Management Type | "Self-Managed" or "Third-Party Operated" |
| Facility Category | "Recreation", "Retail", "Service", "Common Area" |

> [!NOTE]
> Operating hours are NOT stored in the base Facility table. They are tracked historically in Facility Hours History (§2.5.1).

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-024 | All facilities must be contained within buildings (via "Has" relationship from Building). |
| FR-CE-025 | Each facility belongs to exactly one building. |

---

### 2.5.1 Facility Hours History

The system must track operating hours over time.

#### Stored Data

| Attribute | Description |
|---|---|
| Facility ID | Foreign key to Facility |
| Effective Date | When these hours became active (part of PK) |
| Opening Time | When the facility opens |
| Closing Time | When the facility closes |
| Operating Hours | **Derived** text representation (e.g., "6 AM - 10 PM", "24/7") |

**Primary Key:** facility_id + effective_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-026 | When operating hours change, a new row is inserted (history preserved). |
| FR-CE-027 | The current hours are those with the most recent effective_date. |

---

### 2.5.2 Facility Manager

The system must track facility management assignments with temporal history.

#### Stored Data

| Attribute | Description |
|---|---|
| Facility ID | Foreign key to Facility |
| Manager National ID | Foreign key to Person |
| Management Start Date | When this person became manager (part of PK) |
| Management End Date | When this person stopped being manager (NULL if current) |

**Primary Key:** facility_id + manager_national_id + management_start_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-028 | A facility can have at most one active manager at any time (management_end_date IS NULL). |
| FR-CE-029 | Management history must be preserved via start/end dates. |
| FR-CE-030 | The system does not track management shift rotations within a single management period. |

---

### 2.6 Company (Vendor)

The system must maintain records of external organizations providing services to the compound. Each company is uniquely identified by a Company ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Company ID | Unique identifier (primary key) |
| Company Name | Legal business name |
| Tax ID | Government registration number |
| Phone Number | Primary contact information |
| Company Type | "Maintenance Contractor", "Supplier", or "Commercial Tenant" |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-031 | Companies interact with the system as service vendors (executing work orders) or as commercial tenants (party to lease contracts). |
| FR-CE-032 | A company may own vehicles registered for compound access. |

---

### 2.6.1 Person Works for Company

The system must track which individuals are employees or representatives of external companies.

#### Stored Data

| Attribute | Description |
|---|---|
| Person Comp National ID | Foreign key to Person |
| Company ID | Foreign key to Company |

**Primary Key:** person_comp_national_id + company_id

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-033 | A person may work for multiple companies (e.g., contractor with multiple clients). |
| FR-CE-034 | This relationship enables tracking which company representatives sign contracts or perform work orders. |

---

### 2.7 Vehicle

The system must track all vehicles authorized to enter compound premises. Each vehicle is uniquely identified by its License Plate Number.

#### Stored Data

| Attribute | Description |
|---|---|
| License Number | Unique identifier / primary key (plate number) |
| Vehicle Model | Make and model designation |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-035 | Vehicles may be owned by persons, companies, or departments. |
| FR-CE-036 | Every vehicle entering the compound must be linked to a valid access permit. |
| FR-CE-037 | The relationship between vehicle and owner is many-to-one (multiple vehicles can belong to one owner, each vehicle has only one registered owner). |

---

### 2.7.1 Person Vehicles

Tracks vehicles owned by individuals (residents, staff).

#### Stored Data

| Attribute | Description |
|---|---|
| License No | Foreign key to Vehicle (also PK) |
| National ID | Foreign key to Person |

**Primary Key:** license_no

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-038 | A person may own multiple vehicles (1:M relationship). |
| FR-CE-039 | A vehicle can only be owned by ONE person at any time. |
| FR-CE-040 | If ownership transfers to another person, the old record is deleted and a new record is inserted. |
| FR-CE-041 | This table only stores current ownership (no historical tracking of ownership transfers). |

---

### 2.7.2 Department Vehicles

Tracks vehicles owned by compound departments (fleet vehicles).

#### Stored Data

| Attribute | Description |
|---|---|
| License No | Foreign key to Vehicle (also PK) |
| Department ID | Foreign key to Department |

**Primary Key:** license_no

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-042 | A department may own multiple vehicles (1:M relationship). |
| FR-CE-043 | A vehicle can only be owned by ONE department at any time. |
| FR-CE-044 | Vehicles owned by departments are typically fleet vehicles (patrol cars, maintenance trucks, delivery vans). |
| FR-CE-045 | If a vehicle is reassigned to another department, the old record is deleted and a new record is inserted. |

---

### 2.7.3 Company Vehicles

Tracks vehicles owned by external companies (vendor/contractor vehicles).

#### Stored Data

| Attribute | Description |
|---|---|
| License No | Foreign key to Vehicle (also PK) |
| Company ID | Foreign key to Company |

**Primary Key:** license_no

#### Requirements

| ID | Requirement |
|---|---|
| FR-CE-046 | A company may own multiple vehicles (1:M relationship). |
| FR-CE-047 | A vehicle can only be owned by ONE company at any time. |
| FR-CE-048 | If a vehicle's company ownership changes, the old record is deleted and a new record is inserted. |

---

### 2.7.4 Exclusive Vehicle Ownership (Cross-Table Rule)

| ID | Requirement |
|---|---|
| FR-CE-049 | A vehicle can only be owned by ONE entity at a time: a Person OR a Company OR a Department — never multiple owners simultaneously. |
| FR-CE-050 | A license_no can exist in only ONE of the 3 ownership tables (Person_Vehicles, Department_Vehicles, Company_Vehicles). |
| FR-CE-051 | Ownership transfer across entity types requires atomic deletion from one table and insertion into another. |
| FR-CE-052 | The system must block insertion of a vehicle into any ownership table if it already exists in another ownership table. |

---

## 3. ORGANIZATIONAL STRUCTURE

### 3.1 Department

The system must maintain the organizational hierarchy for compound operations. Each department is uniquely identified by a Department ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Department ID | Unique identifier (primary key) |
| Department Name | Official designation (e.g., "Security", "Maintenance", "Administration") |

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-001 | Staff members are assigned to departments via the Task Monthly Salary relationship. |
| FR-OS-002 | Department managers are tracked via the "Department Managers" table with temporal history (§8.5). |
| FR-OS-003 | Departments may authorize access permits for their staff. |
| FR-OS-004 | Departments may own vehicles. |

---

### 3.1.1 Department Location History

The system must track where departments are physically located over time.

#### Stored Data

| Attribute | Description |
|---|---|
| Department ID | Foreign key to Department |
| Location Start Date in Building | When the department moved to this building (part of PK) |
| Location End Date in Building | When the department left (NULL if current) |
| Building ID | Foreign key to Building |

**Primary Key:** department_id + location_start_date_in_building

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-005 | A department may relocate to different buildings over time (history preserved). |
| FR-OS-006 | The current location is the record with the most recent start date where end date IS NULL. |
| FR-OS-007 | Historical data of organizational moves must be preserved. |

---

### 3.2 Position

The system must define job roles available within the organization. Each position is uniquely identified by a Position ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Position ID | Unique identifier (primary key) |
| Position Name | Title of the role (e.g., "Security Guard", "Plumber", "Receptionist") |

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-008 | Positions belong to departments (many positions can exist within one department). |
| FR-OS-009 | The Position entity stores only the identity (name); salary information is tracked separately in Position_Salary_History. |

---

### 3.2.1 Position Salary History

The system must track salary caps and hourly rates over time for each position.

#### Stored Data

| Attribute | Description |
|---|---|
| Position ID | Foreign key to Position |
| Salary Effective Date | When this salary structure became active (part of PK) |
| Maximum Salary | Monthly salary ceiling for this position at this point in time |
| Base Hourly Rate | Standard hourly rate for this position |

**Primary Key:** position_id + salary_effective_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-010 | When a position's salary structure changes, a new row is inserted (old rows preserved). |
| FR-OS-011 | The current salary structure is the one with the most recent salary_effective_date. |
| FR-OS-012 | Employment offers cannot exceed the maximum_salary active at the time of offer. |
| FR-OS-013 | The base_hourly_rate is used to calculate daily wages for staff in this position. |
| FR-OS-014 | Monthly salary constraint: (base_hourly_rate × average_monthly_hours) must be ≤ maximum_salary. |

---

### 3.3 Task

The system must catalog standard duties associated with staff assignments. Each task is uniquely identified by a Task ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Task ID | Unique identifier (primary key) |
| Task Title | Description of the duty |

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-015 | Tasks are connected to staff assignments, not to individual people. |
| FR-OS-016 | When a person is assigned to a department, they may be assigned specific tasks that belong to that department. |
| FR-OS-017 | Tasks model ongoing responsibilities, not one-time work orders. |

---

### 3.3.1 Assigned Task

The system must track task assignments to staff members by department, shift, and date.

#### Stored Data

| Attribute | Description |
|---|---|
| Staff National ID | Foreign key to Person |
| Task ID | Foreign key to Task |
| Department ID | Foreign key to Department |
| Shift ID | Foreign key to Shift Attendance Type |
| Year | Year of assignment |
| Month | Month of assignment |
| Day | Day of assignment |

**Primary Key:** staff_national_id + task_id + year + month + day

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-018 | A staff member may be assigned multiple tasks on the same day. |
| FR-OS-019 | The same task may be assigned to different staff members. |
| FR-OS-020 | This is the assignment table; actual attendance is tracked in Attends (§3.4.2). |
| FR-OS-021 | Monthly aggregation for salary calculation is in Task Monthly Salary (§8.4). |

---

### 3.4 Shift Attendance Type

The system must define shift categories. Each shift type is uniquely identified by a Shift ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Shift ID | Unique identifier (primary key) |
| Shift Name | Designation (e.g., "Morning Shift", "Night Shift") |

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-022 | Shift Attendance Type is the parent (identity). The actual rules (hours, bonuses) are stored in the child table Law of Shift Attendance. |

---

### 3.4.1 Law of Shift Attendance

The system must define the rules and policies for each shift type over time.

#### Stored Data

| Attribute | Description |
|---|---|
| Shift ID | Foreign key to Shift Attendance Type |
| Effective Date | When these rules became active (part of PK) |
| Start Time | Scheduled beginning time (supports minute precision: 09:01, 14:30) |
| End Time | Scheduled ending time (supports minute precision: 17:30, 22:15) |
| One Hour Extra Bonus | Compensation rate for overtime |
| One Hour Difference Discount | Penalty rate for arriving late or leaving early |
| Period Start End | **Derived:** shift duration |

**Primary Key:** shift_id + effective_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-023 | When shift rules change (hours, bonus rates), a new row is inserted with a new effective_date. |
| FR-OS-024 | The current rules are those with the most recent effective_date for each shift_id. |
| FR-OS-025 | The period_start_end is derived and should not be stored redundantly. |

---

### 3.4.2 Attends (Daily Attendance Log)

The system must track daily staff attendance.

#### Stored Data

| Attribute | Description |
|---|---|
| National ID | Foreign key to Person (staff member) |
| Shift ID | Foreign key to Shift Attendance Type |
| Date | Calendar date (part of PK) |
| Check In Time | Actual arrival time |
| Check Out Time | Actual departure time |
| Is Absent | Boolean flag for absence |
| Period Out In | Duration worked |
| Diff Hour | Difference from scheduled hours (+overtime / −undertime) |
| Daily Salary | Base pay for this day |
| Daily Bonus | Calculated bonus for this day |
| Daily Deduction | Penalties for this day |
| Daily Net Salary | Final pay = daily_salary + daily_bonus − daily_deduction |

**Primary Key:** national_id + shift_id + date

#### Requirements

| ID | Requirement |
|---|---|
| FR-OS-026 | Attendance is recorded daily per staff member per shift. |
| FR-OS-027 | Daily salary calculation: daily_salary = offered_hourly_rate × actual_hours_worked. The offered_hourly_rate comes from Employment_Offers for this staff member. |
| FR-OS-028 | Bonus calculation: daily_bonus = one_hour_extra_bonus × overtime_hours. Overtime_hours = MAX(0, actual_hours_worked − scheduled_shift_hours). |
| FR-OS-029 | Penalty calculation: daily_deduction = one_hour_diff_discount × undertime_hours + absence_penalty. Undertime_hours = MAX(0, scheduled_shift_hours − actual_hours_worked). |
| FR-OS-030 | Final calculation: daily_net_salary = daily_salary + daily_bonus − daily_deduction. |
| FR-OS-031 | Daily attendance records are aggregated to calculate monthly salary in Task_Monthly_Salary. |

---

### 3.5 Employment Offer

Employment offer is documented in Section 8.3 (HR Module) as it is a child of the Applications entity.

---

## 4. ACCESS CONTROL MODULE

### 4.1 Access Permit

The system must maintain a registry of all access credentials issued within the compound. Each permit is uniquely identified by a Permit ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Permit ID | Unique identifier (primary key) |
| Access Level | "Full Access", "Restricted Areas", "Common Areas Only" |
| Status | "Active", "Expired", "Suspended" |
| Issue Date | Date when credential was created |
| Expiry Date | Date when credential becomes invalid |
| Type | "Staff Badge", "Resident Card", "Vehicle Sticker", "Visitor Pass" |

#### Requirements

| ID | Requirement |
|---|---|
| FR-AC-001 | Every permit must have exactly one status at any time. |
| FR-AC-002 | A permit cannot be issued without an authorization source. |
| FR-AC-003 | There are three valid issuance pathways: (1) **Contract-Based** — permits generated when a rental contract is activated via the "Grants" relationship; (2) **Department-Based** — permits authorized when staff are assigned to departments via the "Authorizes Staff" relationship; (3) **Personal Ownership** — every active permit must be linked to a person who physically holds it via the "Holds" relationship. |
| FR-AC-004 | A person may hold multiple permits simultaneously (1:M relationship). |
| FR-AC-005 | Vehicle permits are linked via the "Identified By" relationship (one vehicle may have multiple permits). |

---

### 4.1.1 Vehicle Permits

The system must track which access permits are assigned to which vehicles.

#### Stored Data

| Attribute | Description |
|---|---|
| License No | Foreign key to Vehicle |
| Permit ID | Foreign key to Access Permit |

**Primary Key:** license_no + permit_id

#### Requirements

| ID | Requirement |
|---|---|
| FR-AC-006 | A vehicle may have multiple permits (e.g., different access levels, multiple stickers). |
| FR-AC-007 | A permit may be assigned to multiple vehicles (e.g., company fleet permit). |
| FR-AC-008 | Vehicle permits enable gate entry without manual plate recording. |

---

### 4.2 Gate

The system must track all physical entry and exit points. Each gate is uniquely identified by a Gate ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Gate ID | Unique identifier (primary key) |
| Gate Name | Descriptive designation (e.g., "Main Entrance", "Service Gate", "Rawda Gate") |
| Type | "Pedestrian", "Vehicle", "Combined" |
| Status | "Active", "Closed" |

#### Requirements

| ID | Requirement |
|---|---|
| FR-AC-009 | All gates belong to the compound via the "Has Point of Entry" relationship. |
| FR-AC-010 | Only gates with status "Active" may record entry events. |
| FR-AC-011 | The system must support multiple gates of the same type. |

---

### 4.3 Entry Logging

The system must record every instance of access permit usage at compound entry points via the "Enters At" relationship between Access_Permit and Gate.

#### Stored Data

| Attribute | Description |
|---|---|
| Timestamp | Exact date and time of the event (auto-generated) |
| Direction | "In" or "Out" |
| Purpose | Optional: "Work", "Delivery", "Personal Visit" |
| Manual Plate Entry | Optional: for unregistered visitor vehicles or when scanning fails |

#### Requirements

| ID | Requirement |
|---|---|
| FR-AC-012 | Entry events are immutable once recorded — they cannot be edited or deleted. |
| FR-AC-013 | Permits with status "Expired" or "Revoked" must not generate new entry events. |
| FR-AC-014 | The timestamp must be system-generated; manual backdating is prohibited. |
| FR-AC-015 | Direction tracking must enable occupancy calculations (entries without corresponding exits). |
| FR-AC-016 | If an entry was recorded incorrectly, it must NOT be deleted or updated. A separate notes/flagging mechanism should handle corrections. |

---

## 5. CONTRACT & RESIDENCY MODULE

### 5.1 Contract

The system must store all rental and lease agreements. Each contract is uniquely identified by a Contract ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Contract ID | Unique identifier (primary key) |
| Start Date | Effective beginning of the agreement |
| End Date | Scheduled termination date |
| Contract Type | "Residential Lease", "Commercial Lease", "Service Agreement" |
| Contract Status | "Draft", "Active", "Terminated", "Expired" |
| Payment Frequency | "Monthly", "Quarterly", "Semi-Annually", "Annually" |
| Final Price | Total monetary value of the contract term |
| Security Deposit Amount | Refundable amount collected at signing |
| Renewal Terms | Text description of extension conditions |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-001 | Each contract covers exactly one unit OR one facility (never both, never multiple). |
| FR-CR-002 | The "Covers" relationship links Contract to Unit; "Contract Facilities" (§5.5) links Contract to Facility. |
| FR-CR-003 | A contract cannot be activated (status = "Active") until at least one party has signed it via the "Parties to" relationship. |
| FR-CR-004 | The final_price must equal the sum of all generated installment amounts (validation check required). |

---

### 5.2 Person Parties to Contract (Tenant Signatures)

The system must track who signs contracts via the "Parties to" relationship between Person and Contract.

#### Stored Data

| Attribute | Description |
|---|---|
| Role | "Primary Signer", "Guarantor", "Corporate Representative", "Emergency Contact" |
| Date Signed | Timestamp of signature execution |

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-005 | Each contract must have exactly one person with role = "Primary Signer". Co-equal signers are not permitted. |
| FR-CR-006 | A contract may have additional parties with roles like "Guarantor" or "Emergency Contact", but only one Primary Signer. |

---

### 5.2.1 Person Parties to Contract Facility (Staff/Company Signatures)

The system must track signatures from staff members and company representatives on facility-related contracts (commercial leases, service agreements).

#### Stored Data

| Attribute | Description |
|---|---|
| Staff National ID | Foreign key to Person (staff member authorizing) |
| Person Comp National ID | Foreign key to Person (company representative signing) |
| Contract ID | Foreign key to Contract |
| Date Signed | Timestamp of signature execution |

**Primary Key:** staff_national_id + person_comp_national_id + contract_id

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-007 | This table handles commercial contract signatures separately from tenant contracts. |
| FR-CR-008 | Links compound staff (authorizer) with company representatives (signatory). |
| FR-CR-009 | Used for service agreements where a vendor company signs with compound staff approval. |

---

### 5.3 Resides Under (Occupancy Tracking)

The system must record who actually lives in units under each contract via the "Resides Under" relationship between Person and Contract.

#### Stored Data

| Attribute | Description |
|---|---|
| Person ID | Foreign key to Person |
| Contract ID | Foreign key to Contract |
| Move In Date | Part of primary key (allows re-entry) |
| Move Out Date | When the person vacates (NULL if currently residing) |
| Relationship to Signer | "Self", "Spouse", "Child", "Parent", "Roommate" |

**Primary Key:** person_id + contract_id + move_in_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-010 | The composite primary key allows the same person to reside under the same contract multiple times with different move-in dates (e.g., temporary relocation then return). |
| FR-CR-011 | A person may reside under multiple contracts simultaneously (e.g., maintains two residences). |
| FR-CR-012 | All residents under a contract are automatically evicted when Contract.status = "Terminated" (move_out_date set to termination date). |
| FR-CR-013 | The Primary Signer is NOT automatically a resident; they must have a "Resides Under" entry if they occupy the unit. |

---

### 5.4 Contract Unit (Unit Lease Relationship)

The system must track which units are leased under which contracts.

#### Stored Data

| Attribute | Description |
|---|---|
| Contract ID | Foreign key to Contract |
| Unit ID | Foreign key to Unit |
| Created At Timestamp | System-generated timestamp (part of PK) |

**Primary Key:** contract_id + unit_id + created_at_timestamp

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-014 | A contract may cover one unit (for residential leases). |
| FR-CR-015 | The timestamp in the primary key preserves historical changes if a contract is modified to cover different units over time. |
| FR-CR-016 | This is distinct from the "Contract Facilities" relationship (Contract → Facility) for commercial leases. |

---

### 5.5 Contract Facilities (Facility Lease Relationship)

The system must track which facilities are leased under which contracts.

#### Stored Data

| Attribute | Description |
|---|---|
| Contract ID | Foreign key to Contract |
| Facility ID | Foreign key to Facility |
| Created At Timestamp | System-generated timestamp |

**Primary Key:** contract_id + facility_id + created_at_timestamp

#### Requirements

| ID | Requirement |
|---|---|
| FR-CR-017 | A contract may lease one facility (for commercial leases). |
| FR-CR-018 | This is distinct from the "Covers" relationship (Contract → Unit) for residential leases. |

---

## 6. FINANCIAL MODULE

### 6.1 Installment (Payment Obligations)

The system must model the payment schedule for contracts as a series of individual obligations. Each installment is uniquely identified by an Installment ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Installment ID | Unique identifier (primary key) |
| Due Date | Date by which payment must be received |
| Amount Expected | Monetary value owed for this obligation |
| Status | "Pending", "Partially Paid", "Paid", "Overdue", "Cancelled" |
| Type | "Security Deposit", "Monthly Rent", "Quarterly Rent", "Final Payment", "Adjustment" |

#### Status Lifecycle

1. **Pending** → Created but no payment received yet
2. **Partially Paid** → Sum of linked payments < amount_expected
3. **Paid** → Sum of linked payments ≥ amount_expected
4. **Overdue** → Status remains "Pending" AND due_date < current_date
5. **Cancelled** → Contract terminated before this installment's due date

#### Requirements

| ID | Requirement |
|---|---|
| FR-FN-001 | Installments are generated when Contract.status changes from "Draft" to "Active". |
| FR-FN-002 | The sum of all Installment.amount_expected for a contract must equal Contract.final_price (validation check). |
| FR-FN-003 | Installments cannot be deleted once created; they must be marked "Cancelled" if the contract terminates early. |
| FR-FN-004 | An installment becomes "Overdue" automatically at midnight on (due_date + 1). |
| FR-FN-005 | The system natively supports custom payment schedules: balloon payments, discounted months, seasonal variations, and irregular schedules. Each installment is an independent row with its own amount_expected and due_date. |

---

### 6.2 Payment (Transaction Ledger)

The system must maintain a complete immutable record of all financial transactions. Each payment is uniquely identified by a Transaction ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Transaction ID | Unique identifier (primary key) |
| Date | Timestamp when the transaction occurred |
| Amount | Monetary value transferred |
| Method | "Bank Transfer", "Cash", "Check", "Credit Card", "Wire Transfer" |
| Direction | "Inbound" (income) or "Outbound" (expenses) |
| Reference Number | External tracking code (bank confirmation, check number, receipt ID) |
| Reconciliation Status | "Pending", "Reconciled", "Disputed" |
| Currency | Monetary unit (e.g., "USD", "EGP") |
| Account ID | Foreign key to Bank_Account (NOT NULL) |
| National ID | Foreign key to Person (NULLABLE) |

> [!IMPORTANT]
> **National ID** is nullable to support the "Chain of Truth" pattern:
> - **Rental Income Payments:** national_id IS NULL → follow Payment → Installment → Contract → Person
> - **Direct Person Payments:** national_id IS NOT NULL → salaries, dividends, refunds
> - **Vendor Payments:** national_id IS NULL → follow Payment → Work_Order → Company

#### Requirements

| ID | Requirement |
|---|---|
| FR-FN-006 | Payments are immutable events. Once reconciliation_status = "Reconciled", the payment cannot be edited or deleted. |
| FR-FN-007 | Corrections require creating a reversal transaction (negative amount) and a corrected transaction. |
| FR-FN-008 | Every payment must link to exactly one bank account via the "Processed via" relationship. |
| FR-FN-009 | Payment records are permanently retained for audit and tax compliance. |
| FR-FN-010 | Bank accounts and persons cannot be deleted if any payment references them (ON DELETE RESTRICT). |

---

### 6.3 Payment Routing (Polymorphic Targeting)

The system supports three distinct payment types based on what the payment accomplishes.

#### 6.3.1 Rental Income (Installment Payment)

When a tenant pays rent, the payment links to a specific installment via the "Pays_off" relationship.

| ID | Requirement |
|---|---|
| FR-FN-011 | Multiple payments can pay off one installment (partial payments allowed). |
| FR-FN-012 | One payment can only target one installment (no splitting; must create separate payment records). |
| FR-FN-013 | When SUM(payments linked to installment) ≥ amount_expected, installment status updates to "Paid". |
| FR-FN-014 | Overpayments create a credit balance that can be manually applied to future installments. |
| FR-FN-015 | To identify the paying tenant, follow the Chain of Truth: Payment → Installment → Contract → Person. Never link Payment directly to Person for rental income. |

#### 6.3.2 Direct Person Payment (Salary/Dividend)

When the compound pays staff salaries or investor dividends, the payment links directly to a person via the "Transfers With" relationship.

| ID | Requirement |
|---|---|
| FR-FN-016 | This relationship is used ONLY for payments without an underlying installment obligation. |
| FR-FN-017 | Direction must be "Outbound" (money leaving compound). |
| FR-FN-018 | Valid use cases: monthly staff salary, quarterly investor dividends, employee bonuses, emergency cash advances. |
| FR-FN-019 | Salaries are not modeled as installments because staff compensation is event-driven (paid when payroll runs), not debt-driven. |

#### 6.3.3 Vendor Payment (Work Order Settlement)

When the compound pays a vendor for maintenance or services, the payment links to the work order via the "Pays Vendor For" relationship.

| ID | Requirement |
|---|---|
| FR-FN-020 | This relationship is used for operational expenses (repairs, supplies, contracted services). |
| FR-FN-021 | Direction must be "Outbound". |
| FR-FN-022 | The payment amount may differ from Work_Order.cost_amount (e.g., negotiated discount, progress payment). |
| FR-FN-023 | A work order may have multiple payments (installment-based vendor contracts). |
| FR-FN-024 | Work_Order.job_status should update to "Paid" when SUM(Payments) ≥ Work_Order.cost_amount. |

---

### 6.4 Bank Account

The system must track financial institutions used for transaction processing. Each bank account is uniquely identified by an Account ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Account ID | Unique identifier (primary key) |
| Bank Name | Name of the financial institution |
| IBAN | International Bank Account Number |
| SWIFT Code | Bank identifier code (entered manually) |
| Is Primary | Boolean flag indicating default account for transactions |

#### Requirements

| ID | Requirement |
|---|---|
| FR-FN-025 | The compound can maintain at least one bank account with is_primary = true, though initially it might not have a bank account (partial participation). |
| FR-FN-026 | Multiple entities may use bank accounts: Person (refunds, direct deposit), Company (vendor payment processing), Department (operating account). |
| FR-FN-027 | The system does NOT track current_balance as an attribute. It only records which account processed each transaction. |

---

### 6.5 Financial Integrity Rules

| ID | Requirement |
|---|---|
| FR-FN-028 | **Double-Entry Principle:** Every payment must link to exactly one target. Inbound → Installment or external source. Outbound → Person (salary/dividend) or Work Order (expense). No zero-target or multi-target payments. |
| FR-FN-029 | **Payment Allocation Priority:** If a tenant overpays, the system must allocate to overdue installments first (application logic). |
| FR-FN-030 | **Immutability After Reconciliation:** Once reconciled, corrections require a reversal payment + corrected payment. |
| FR-FN-031 | **Currency Consistency:** All payments linked to installments from the same contract must use the same currency. |
| FR-FN-032 | **Temporal Validation:** Payment.date cannot be in the future (date ≤ today()). |

---

## 7. WORK ORDER MODULE

### 7.1 Work Order

The system must track maintenance requests and repairs. Each work order is uniquely identified by a Work Order ID.

#### Stored Data

| Attribute | Description |
|---|---|
| Work Order ID | Unique identifier (primary key) |
| Date Scheduled | When the work is planned to begin |
| Date Completed | When the work was finished (NULL if not yet done) |
| Cost Amount | Total expense for the job |
| Job Status | "Pending", "Assigned", "In Progress", "Completed", "Paid", "Cancelled" |
| Description | Text summary of the required work |
| Priority | "Low", "Normal", "High", "Emergency" |
| Service Category | "Plumbing", "Electrical", "HVAC", "Landscaping", "Cleaning", "Security" |

#### Status Lifecycle

1. **Pending** → Created but not yet assigned to a vendor
2. **Assigned** → Vendor selected, work not yet started (date_assigned set)
3. **In Progress** → Vendor has begun work (date_scheduled reached)
4. **Completed** → Work finished (date_completed set)
5. **Paid** → Vendor payment processed (via "Pays Vendor For" relationship)
6. **Cancelled** → Work order voided before completion

#### Requirements

| ID | Requirement |
|---|---|
| FR-WO-001 | Work orders are created by compound staff via the "Requests" relationship (Person → Requests → Work Order). |
| FR-WO-002 | Only persons with person_type = "Staff" may create work orders (application logic). |
| FR-WO-003 | Residents and visitors CANNOT create work orders directly; they must report issues to staff. |
| FR-WO-004 | Work orders are assigned to vendors via the "Executes" relationship (Company → Executes → Work Order), which stores the date_assigned attribute. |
| FR-WO-005 | Work orders target facilities via the "Performs On" relationship (Work Order → Performs On → Facility) OR units via a "Targets Unit" relationship (Work Order → Targets → Unit). Each work order must target at least one facility or one unit. |

---

## 8. HUMAN RESOURCES MODULE

### 8.1 Applications (Parent Entity)

The system must track all job applications as the central anchor for the hiring process. Each application is uniquely identified by a composite key.

#### Stored Data

| Attribute | Description |
|---|---|
| Applicant National ID | Foreign key to Person |
| Position ID | Foreign key to Position |
| Application Date | When the application was submitted (part of PK) |

**Primary Key:** applicant_national_id + position_id + application_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-001 | Applications is a pure anchor entity with no descriptive attributes. Its status is implicitly derived from its children. |
| FR-HR-002 | If Recruitment records exist → Application is in "Interviewing" state. If Employment_Offers record exists → Application resulted in "Hired". If neither exists after a business-defined period → implicitly rejected or withdrawn. |
| FR-HR-003 | The composite primary key allows the same person to re-apply for the same position at different times. |
| FR-HR-004 | An application must exist before any interview or offer can be recorded. |

---

### 8.2 Recruitment (Interview Log)

The system must track interview events as a child of Applications.

#### Stored Data

| Attribute | Description |
|---|---|
| Applicant National ID | FK to Applications (inherited) |
| Position ID | FK to Applications (inherited) |
| Application Date | FK to Applications (inherited) |
| Staff National ID | The interviewer (FK to Person, must be staff) |
| Interview Date | When the interview occurred (part of PK) |
| Interview Result | "Pass", "Fail", "Pending" |

**Primary Key:** applicant_national_id + position_id + application_date + staff_national_id + interview_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-005 | One application can have MANY interviews (different staff members, different dates). |
| FR-HR-006 | Interview results ("Pass", "Fail") inform the hiring decision but do not directly grant employment. |
| FR-HR-007 | Multiple interviewers may evaluate the same candidate for the same application. |

---

### 8.3 Employment Offers (Terms Entity)

The system must track employment offers as a sibling entity to Recruitment, both children of Applications.

#### Stored Data

| Attribute | Description |
|---|---|
| Applicant National ID | FK to Applications (same as parent key) |
| Position ID | FK to Applications (same as parent key) |
| Application Date | FK to Applications (same as parent key) |
| Offered Maximum Salary | Monthly salary cap for this hire |
| Offered Hourly Rate | Actual hourly rate for this individual |
| Employment Start Date | When the employee will begin work |

**Primary Key:** applicant_national_id + position_id + application_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-008 | One application results in at most ONE employment offer (1:1 relationship). |
| FR-HR-009 | offered_maximum_salary ≤ Position_Salary_History.maximum_salary (active at time of offer). |
| FR-HR-010 | offered_hourly_rate ≤ Position_Salary_History.base_hourly_rate (active at time of offer). |
| FR-HR-011 | Employment_start_date may differ from application_date (time for onboarding). |
| FR-HR-012 | This table exists only for successful applications; rejected applicants have no Employment_Offers record. |
| FR-HR-013 | Employment_Offers and Recruitment are "siblings" — both point to Applications but do not reference each other directly. |

---

### 8.4 Task Monthly Salary (Staff Assignment)

The system must track staff work assignments and monthly compensation.

#### Stored Data

| Attribute | Description |
|---|---|
| Staff National ID | Foreign key to Person |
| Department ID | The department where the staff member works |
| Shift ID | The shift type assigned |
| Task ID | Foreign key to Task |
| Year | Temporal key |
| Month | Temporal key |
| Monthly Salary | Base compensation (aggregated from daily_salary in Attends) |
| Monthly Bonus | Additional compensation (aggregated from daily_bonus in Attends) |
| Monthly Deduction | Penalties (aggregated from daily_deduction in Attends) |
| Tax | Tax deduction amount |
| Monthly Net Salary | Final = monthly_salary + monthly_bonus − monthly_deduction − tax |

**Primary Key:** staff_national_id + year + month

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-014 | Monthly values are aggregated from the daily attendance records (Attends table). |
| FR-HR-015 | A staff member may have multiple assignment records for different tasks or periods. |
| FR-HR-016 | Monthly net salary cannot exceed the Employment_Offers.offered_maximum_salary cap. |
| FR-HR-017 | Salary chain constraint: Position_Salary_History.maximum_salary ≥ Employment_Offers.offered_maximum_salary ≥ Task_Monthly_Salary.monthly_net_salary. |
| FR-HR-018 | Hourly rate constraint: Position_Salary_History.base_hourly_rate ≥ Employment_Offers.offered_hourly_rate. |
| FR-HR-019 | Daily calculation formula: daily_salary = Employment_Offers.offered_hourly_rate × actual_hours_worked. |

---

### 8.5 Department Managers

The system must track management assignments with temporal history.

#### Stored Data

| Attribute | Description |
|---|---|
| Department ID | Foreign key to Department |
| Manager National ID | Foreign key to Person |
| Management Start Date | Part of PK |
| Management End Date | NULL if current |

**Primary Key:** department_id + manager_national_id + management_start_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-020 | A department may have multiple managers over time (history preserved via start/end dates). |
| FR-HR-021 | Only ONE manager may be active per department at any time (management_end_date IS NULL). |
| FR-HR-022 | The start_date in the key allows tracking re-appointments (same person, different periods). |
| FR-HR-023 | When a new manager is assigned, the previous manager's management_end_date must be set. |

---

### 8.6 Supervision Hierarchy

The system must track managerial relationships via the "Supervision" unary relationship on Person.

#### Stored Data

| Attribute | Description |
|---|---|
| Super National ID | The supervisor (FK to Person) |
| National ID | The supervisee (FK to Person) |
| Supervision Start Date | Part of PK |
| Supervision End Date | NULL if current |

**Primary Key:** super_national_id + national_id + supervision_start_date

#### Requirements

| ID | Requirement |
|---|---|
| FR-HR-024 | One supervisor may supervise many supervisees (1:M relationship). |
| FR-HR-025 | A person may have at most one direct supervisor at any given time. |
| FR-HR-026 | Supervision does NOT enforce department boundaries (cross-department supervision is permitted). |
| FR-HR-027 | Circular supervision chains must be prevented (A supervises B supervises A is invalid). |
| FR-HR-028 | Supervision history must be preserved via start/end dates. |

---

## 9. INFRASTRUCTURE & ASSET TRACKING

### 9.1 Physical Hierarchy

The system enforces a strict containment hierarchy:

```
Compound (1)
├── Has (1:M) → Building (M)
│   ├── Has (1:M) → Unit (M)       [if Building.building_type = "Residential"]
│   └── Has (1:M) → Facility (M)   [if Building.building_type = "Non-Residential"]
└── Has Point of Entry (1:M) → Gate (M)
```

| ID | Requirement |
|---|---|
| FR-IA-001 | A Building belongs to exactly one Compound (Total Participation). |
| FR-IA-002 | A Unit belongs to exactly one Building (Total Participation). |
| FR-IA-003 | All Facilities are indoor units and must belong to exactly one Building. Direct ownership of facilities by the Compound (e.g., outdoor parks) is out of scope for this version. |
| FR-IA-004 | Gates belong only to the Compound (not to Buildings). |

---

### 9.2 Vehicle Ownership

The system must track vehicle registration via multiple ownership pathways.

| ID | Requirement |
|---|---|
| FR-IA-005 | Person → Owns → Vehicle (1:M): Personal vehicles of residents and staff. |
| FR-IA-006 | Company → Has → Vehicle (1:M): Company-owned vehicles (e.g., vendor trucks). |
| FR-IA-007 | Department → Has → Vehicle (1:M): Compound fleet vehicles (e.g., security patrol cars). |
| FR-IA-008 | A vehicle can only be owned by ONE entity at a time (exclusive ownership). |
| FR-IA-009 | All vehicles entering the compound must be identified by an active access permit via the "Identified By" relationship. |

---

## 10. CROSS-MODULE INTEGRATION RULES

### 10.1 Contract Termination Cascade

When Contract.status changes from "Active" to "Terminated", the following automated actions must occur:

| ID | Requirement |
|---|---|
| FR-CM-001 | **Installment Cancellation:** All installments with due_date > termination_date must update to status = "Cancelled". |
| FR-CM-002 | **Permit Revocation:** All access permits issued via "Grants" relationship must update to status = "Revoked". |
| FR-CM-003 | **Residency Closure:** All persons linked via "Resides Under" with move_out_date = NULL must have move_out_date set to the termination_date. |
| FR-CM-004 | **Financial Settlement Calculation:** The system must calculate: (Total Paid via Installments) − (Total Expected from Non-Cancelled Installments) = Refund Amount (if positive) or Arrears (if negative). |
| FR-CM-005 | Future payments cannot be linked to cancelled installments (application validation). |

---

### 10.2 Person Blacklisting Impact

When Person.is_blacklisted changes from false to true:

| ID | Requirement |
|---|---|
| FR-CM-006 | **Contract Review:** All contracts where Person is linked via "Parties to" must be flagged for manual review. |
| FR-CM-007 | **Permit Suspension:** All access permits held by Person (via "Holds") must update to status = "Suspended". |
| FR-CM-008 | **Entry Restriction:** No new entry events may be created for suspended permits (gate access control). |
| FR-CM-009 | **Financial Obligations Persist:** Existing installments and payment obligations remain valid; blacklisting does not erase debt. |
| FR-CM-010 | Blacklisted persons cannot sign new contracts. |
| FR-CM-011 | Blacklisted persons cannot participate in facility contracts. |
| FR-CM-012 | Blacklisted permit holders cannot enter the compound. |

---

### 10.3 Department Dissolution

If a department is deleted or dissolved:

| ID | Requirement |
|---|---|
| FR-CM-013 | A department cannot be deleted while it has active assignments, authorized permits, or owned assets. |
| FR-CM-014 | **Staff Reassignment Required:** All persons assigned to this department must be reassigned to a different department before deletion (orphaned staff not permitted). |
| FR-CM-015 | **Permit Transfer:** Access permits issued via "Authorizes Staff" must either be revoked or transferred to the new department. |
| FR-CM-016 | **Asset Transfer:** Vehicles and bank accounts owned by the department must be transferred to another department or the compound. |

---

## 11. REPORTING & QUERY REQUIREMENTS

The system must efficiently support the following business intelligence queries.

### 11.1 Financial Reports

| ID | Requirement |
|---|---|
| FR-RP-001 | **Arrears Report (Outstanding Rent):** Identify tenants with overdue payments. Join Contract → Installment → Payment to show: tenant name, unit, amount owed, amount paid, days overdue. Must filter by installment status = "Overdue" or "Partially Paid" where due_date < current_date. |
| FR-RP-002 | **Revenue Summary Report:** Show total inbound payments grouped by period (monthly, quarterly, annually) and by type (rental income, service agreements, commercial leases). Must support filtering by date range and contract type. |
| FR-RP-003 | **Expense Summary Report:** Show total outbound payments grouped by category (staff salaries, vendor payments, dividends). Must support filtering by date range, department, and service category. |
| FR-RP-004 | **Payment Reconciliation Report:** List all payments with reconciliation_status = "Pending" or "Disputed". Show transaction details, linked entity (installment/person/work order), and age of the pending item. |
| FR-RP-005 | **Tenant Payment History:** For a given tenant, show complete payment history with all installments, due dates, amounts paid, and outstanding balances. Follow Chain of Truth: Payment → Installment → Contract → Person. |
| FR-RP-006 | **Investor Dividend Report:** Show all dividend payments made to investors, grouped by person and period. Cross-reference with Person_Invests_in_Compound stock values. |
| FR-RP-007 | **Bank Account Activity Report:** For each bank account, list all transactions processed through it with running balances per period. |

### 11.2 Occupancy Reports

| ID | Requirement |
|---|---|
| FR-RP-008 | **Vacancy Rate Report:** Show percentage of vacant units over a given period. Derive from Unit_Status_History records grouped by building and time period. |
| FR-RP-009 | **Occupancy Dashboard:** Show current status of all units (Vacant, Occupied, Reserved, Under Maintenance) with counts per building and compound-wide totals. |
| FR-RP-010 | **Residency Report:** For each active contract, list all current residents (via "Resides Under" where move_out_date IS NULL), their relationship to signer, and move-in date. |
| FR-RP-011 | **Unit Turnover Report:** Show how frequently units change tenants over a given period. Derive from Contract start/end dates grouped by unit. |

### 11.3 HR & Payroll Reports

| ID | Requirement |
|---|---|
| FR-RP-012 | **Monthly Payroll Report:** For a given month, show all staff with: base salary, bonuses, deductions, tax, and net salary (from Task_Monthly_Salary). Must support grouping by department. |
| FR-RP-013 | **Attendance Report:** Show daily attendance records for staff over a period, including check-in/out times, overtime hours, undertime hours, and absence flags. Grouped by department and shift. |
| FR-RP-014 | **Staff Roster Report:** List all currently employed staff with their position, department, supervisor, shift assignment, and employment start date. |
| FR-RP-015 | **Hiring Pipeline Report:** Show all open applications with their current stage (Interviewing, Offered, Pending) derived from child records (Recruitment, Employment_Offers). |

### 11.4 Security & Access Reports

| ID | Requirement |
|---|---|
| FR-RP-016 | **Gate Traffic Report:** Show entry/exit counts per gate per period, with breakdown by direction, purpose, and permit type. Support hourly, daily, and monthly granularity. |
| FR-RP-017 | **Active Permits Report:** List all current access permits with status = "Active", grouped by type (Staff Badge, Resident Card, Vehicle Sticker, Visitor Pass). Show holder, expiry date, and access level. |
| FR-RP-018 | **Expired/Suspended Permits Report:** List all permits with status "Expired" or "Suspended" that have not been renewed or reactivated. Flag permits approaching expiry within a configurable threshold (e.g., 30 days). |
| FR-RP-019 | **Vehicle Registry Report:** List all registered vehicles with owner (Person/Company/Department), current permit status, and last entry timestamp. |
| FR-RP-020 | **Blacklisted Persons Report:** List all persons with is_blacklisted = true, their former contracts, suspended permits, and outstanding financial obligations. |

### 11.5 Maintenance & Work Order Reports

| ID | Requirement |
|---|---|
| FR-RP-021 | **Open Work Orders Report:** List all work orders with job_status not in ("Completed", "Paid", "Cancelled"), showing priority, assigned vendor, days since creation, and target facility. |
| FR-RP-022 | **Work Order Cost Analysis:** Show total maintenance expenditure grouped by service category, vendor, and facility over a given period. |
| FR-RP-023 | **Vendor Performance Report:** For each vendor company, show total work orders assigned, completed on time, average completion time, total cost, and payment status. |

### 11.6 Contract Reports

| ID | Requirement |
|---|---|
| FR-RP-024 | **Expiring Contracts Report:** List all contracts where end_date is within a configurable threshold (e.g., 60 days), showing tenant, unit/facility, final price, and renewal terms. |
| FR-RP-025 | **Contract Summary Report:** For a given contract, show all parties, covered unit/facility, installment schedule, payment history, and current residents. |
| FR-RP-026 | **Pricing History Report:** For a given unit, show the full history of listing prices with effective dates and any associated contracts during each pricing period. |

---

## REQUIREMENTS SUMMARY

| Module | Code | Count |
|---|---|---|
| Core Entities | FR-CE | 52 |
| Organizational Structure | FR-OS | 31 |
| Access Control | FR-AC | 16 |
| Contract & Residency | FR-CR | 18 |
| Financial | FR-FN | 32 |
| Work Orders | FR-WO | 5 |
| Human Resources | FR-HR | 28 |
| Infrastructure & Assets | FR-IA | 9 |
| Cross-Module Integration | FR-CM | 16 |
| Reporting | FR-RP | 26 |
| **TOTAL** | | **233** |
