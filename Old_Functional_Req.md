COMPOUND MANAGEMENT SYSTEM: FUNCTIONAL
REQUIREMENTS

1. INTRODUCTION & SCOPE
This document defines the functional requirements for the Compound Management System,
focusing on data storage and business rules rather than user interface design. The system is an
internal tool for compound staff to manage property assets, residents, contracts, security,

financial operations, and maintenance activities. All requirements are expressed from a data-
centric perspective, describing what information must be stored and how it relates, rather than

how screens will appear.

2. CORE ENTITIES
2.1 Person
The system must maintain a central registry of all individuals who interact with the compound
in any capacity. Each person is uniquely identified by their National ID.
The stored data must include:

Business Rule: A person's National ID cannot be changed once recorded.
Database Enforcement: Trigger trg_immutable_national_id prevents any UPDATE that
attempts to change national_id . Attempting to modify this field will raise error: "National ID
cannot be changed once recorded." All other attributes may be updated as needed. The
person_type attribute indicates the primary role, but a person may participate in multiple roles
simultaneously through relationships (e.g., a staff member who also rents a unit).
Role Priority Rule: When a person participates in multiple roles simultaneously, the
person_type attribute stores the highest-priority role according to the following hierarchy:
Staff > Investor > Tenant > Visitor
For example, if Ahmed is hired as a Security Guard (Staff) and later signs a lease for Unit 101
(Tenant), his person_type remains "Staff" because Staff takes precedence over Tenant. The
system determines actual roles through relationships:
Full Name (composite attribute):
- First Name
- Last Name
Nationality: Country of citizenship
Phone Number (multivalued composite attribute - Up to 2 phone numbers only):
Country Code
Phone Number
Email Address (multivalued - Up to 2 Emails only): To support multiple contact emails
Physical Address (composite attribute):
City
Street
Date of Birth: For age verification and legal purposes
Gender: For demographic reporting
Person Type: A discriminator attribute indicating primary classification ("Staff", "Tenant",
"Investor", "Visitor")
Qualification: Educational or professional credentials (relevant for staff)
Is Blacklisted: Boolean flag for security and screening purposes

Staff: Has an Employment_Offers record

This approach avoids storing multiple roles in one field (which would violate 1NF) while
maintaining a single discriminator for quick filtering.
2.1.1 Person Invests in Compound
The system must track investment relationships between persons and the compound. Each
investment record is uniquely identified by a composite key. The stored data must include:

Primary Key: investor_national_id + compound_id + timestamp
Business Rules:
Investor: Has a Person_Invests_in_Compound record
Tenant: Has a Person_Parties_to_Unit_or_Facility record with role = "Primary Signer"
Visitor: Default classification when no other relationships exist

Investor National ID: The person investing (foreign key to Person)
Compound ID: The compound being invested in (foreign key to Compound)
Timestamp: When the investment was recorded
Stock: The ownership percentage or share amount

This tracks ownership stakes in the compound property
A person may have multiple investment records over time (timestamp in key)
The stock attribute represents ownership percentage or share count
Investors with person_type = "Investor" use this relationship for dividend payments

The system must store information about the top-level property entity. It's one Compound that
has the following info, uniquely identified by a Compound ID:

2.3 Building
The system must track all permanent structures within the compound. Each building is uniquely
identified by a Building ID. The stored data must include:

Business Rule: Each building must belong to exactly one compound. Buildings contain either
units (residential) or facilities (non-residential), but not both within the same building.
Database Enforcement:
Building type content rules are enforced by triggers:

Example Violation (Blocked):
Name: Official designation of the property
Address (composite attribute):
Country
City
District
The compound serves as the root entity in the physical hierarchy. All buildings,
facilities, gates, and bank accounts ultimately belong to that only ONE compound.

Building Name: Official designation (e.g., "Tower A", "Villa Block", “Burj Elemarati“)
Building Number: Physical identifier (may not be unique across the compound, as multiple
buildings can share the same number in different zones)
Building Type: Classification as "Residential" (contains units ONLY) or "Non-Residential"
(contains facilities ONLY), there is no mix.
Floors Count: Total number of floors in the building.
Construction Date: Original completion date.

trg_unit_residential_building_only - Prevents Units in Non-Residential buildings
trg_facility_nonresidential_building_only - Prevents Facilities in Residential
buildings

-- Assume Building 'B001' is Non-Residential
INSERT INTO Unit (unit_id, unit_no, building_id) VALUES ('U001', '101',
'B001');
-- ERROR: Units can only be created in Residential buildings

2.4 Unit
The system must store information about individual residential spaces available for lease or
sale. Each unit is uniquely identified by a Unit ID. The stored data must include:

Design Note: To get current listing price or status, query the most recent record from the
respective history tables. This avoids denormalization and ensures full audit trail.
Business Rule: Each unit must belong to exactly one building. A unit cannot be covered by
multiple active contracts simultaneously (no double-booking). The current_status attribute is a
cached state derived from active contracts and should be updated automatically when contract
status changes.
2.4.1 Unit Pricing History
The system must track listing prices over time. Each pricing record is uniquely identified by a
composite key. The stored data must include:

Primary Key: unit_id + effective_date
Business Rules:
Unit Number: The apartment/villa designation within the building
Floor Number: Location of the unit within the building
Number of Bedrooms: Count of sleeping rooms
Number of Bathrooms: Count of bathrooms
Number of Rooms: Total count of all rooms (excluding bathrooms)
Total Number of Rooms: Derived attribute calculated automatically = Num Rooms + Num
Bedrooms
Square Footage: Interior area measurement
Listing Price: NOT stored in Unit table - tracked historically in Unit_Pricing_History (§2.4.1)
Current Status: NOT stored in Unit table - tracked historically in Unit_Status_History (§2.4.2)
Number of Balconies: Count of outdoor spaces that belongs to the unit
View Orientation: Direction the unit faces (e.g., "North", "South")
Current Status: State of availability ("Vacant", "Occupied", "Reserved")
Water Meter Code: Utility tracking identifier
Gas Meter Code: Utility tracking identifier
Electricity Meter Code: Utility tracking identifier

Unit ID: The unit (foreign key to Unit)
Effective Date: When this price became active (part of primary key)
Listing Price: Market asking price at this point in time

When listing price changes, a new row is inserted (history preserved)
The current listing_price is from the record with the most recent effective_date

2.4.2 Unit Status History
The system must track unit availability states over time. Each status record is uniquely
identified by a composite key. The stored data must include:

Primary Key: unit_id + date
Business Rules:
Historical prices are preserved for market analysis and audit purposes

Unit ID: The unit (foreign key to Unit)
Date: When this status was recorded (part of primary key)
Unit Status: State of availability ("Vacant", "Occupied", "Under Maintenance", "Reserved")

Status changes create new history records (not updates)
Enables reporting on vacancy rates over time
The Unit table's current_status should match the most recent history record

2.5 Facility
The system must track shared amenities and commercial spaces within the compound.
Each facility is uniquely identified by a Facility ID.
The stored data must include:

Note: Operating hours are NOT stored in the base Facility table. They are tracked historically in
the Facility Hours History table (Section 2.5.1) to preserve changes over time.
Business Rule: All facilities must be contained within buildings (via "Has" relationship from
Building). Each facility belongs to exactly one building.
2.5.1 Facility Hours History
The system must track operating hours over time. Each hours record is uniquely identified by a
composite key. The stored data must include:

Primary Key: facility_id + effective_date
Business Rules:

2.5.2 Facility Manager
The system must track facility management assignments with temporal history. Each
management assignment is uniquely identified by a composite key. The stored data must
include:
Facility ID: Unique identifier (primary key)
Facility Name: Descriptive identifier (e.g., "Main Gym", "Retail Shop #3")
Management Type: Classification such as "Self-Managed" or "Third-Party Operated"
Facility Category: Purpose classification ("Recreation", "Retail", "Service", "Common Area")

Facility ID: The facility (foreign key to Facility)
Effective Date: When these hours became active (part of primary key)
Opening Time: When the facility opens
Closing Time: When the facility closes
Operating Hours: Derived text representation (e.g., "6 AM - 10 PM", "24/7")

When operating hours change, a new row is inserted (history preserved)
The current hours are those with the most recent effective_date

Facility ID: The facility being managed (foreign key to Facility)
Manager National ID: The person managing (foreign key to Person)
Management Start Date: When this person became manager (part of primary key)
Management End Date: When this person stopped being manager (NULL if current)

Primary Key: facility_id + manager_national_id + management_start_date
Business Rules:
A facility can have at most one active manager at any time (management_end_date IS
NULL)
Management history is preserved via start/end dates
The system does not track management shift rotations within a single management period

2.6 Company (Vendor)
The system must maintain records of external organizations providing services to the
compound. Each company is uniquely identified by a Company ID. The stored data must include:

Business Rule: Companies interact with the system in two ways: as service vendors (executing
work orders) or as commercial tenants (party to lease contracts). A company may own vehicles
registered for compound access.
2.6.1 Person Works for Company
The system must track which individuals are employees or representatives of external
companies. Each employment record is uniquely identified by a composite key. The stored data
must include:

Primary Key: person_comp_national_id + company_id
Business Rules:

2.7 Vehicle
The system must track all vehicles authorized to enter compound premises. Each vehicle is
uniquely identified by its License Plate Number. The stored data must include:

Business Rule: Vehicles may be owned by persons, companies, or departments. Every vehicle
entering the compound must be linked to a valid access permit. The relationship between
Company Name: Legal business name
Tax ID: Government registration number for financial reporting
Phone Number: Primary contact information
Company Type: Classification such as "Maintenance Contractor", "Supplier", or "Commercial
Tenant"

Person Comp National ID: The person who works for the company (foreign key to Person)
Company ID: The company they work for (foreign key to Company)

This relationship links vendor employees to their company
A person may work for multiple companies (e.g., contractor with multiple clients)
This relationship enables tracking which company representatives sign contracts or perform
work orders

License Number: Primary key (plate number)
Vehicle Model: Make and model designation

vehicle and owner is many-to-one (multiple vehicles can belong to one owner, but each vehicle
has only one registered owner in the system).
Ownership Model (3-Table Design):
The system enforces exclusive ownership through a 3-table structure. A vehicle can only be
owned by ONE entity at a time: a Person OR a Company OR a Department (never multiple
owners simultaneously). This design choice prevents the ambiguity and update anomalies that
would occur with nullable foreign keys in a single table.
2.7.1 Person Vehicles
The system must track vehicles owned by individuals (residents, staff). Each ownership record is
uniquely identified by license number. The stored data must include:

Primary Key: license_no (sole primary key enforces exclusive ownership)
Business Rules:

2.7.2 Department Vehicles
The system must track vehicles owned by compound departments (fleet vehicles for security,
maintenance, etc.). Each ownership record is uniquely identified by license number. The stored
data must include:

Primary Key: license_no (sole primary key enforces exclusive ownership)
Business Rules:
License No: The vehicle (foreign key to Vehicle, also serves as primary key)
National ID: The person who owns this vehicle (foreign key to Person)

A person may own multiple vehicles (1:M relationship)
A vehicle can only be owned by ONE person at any time
If ownership transfers to another person, the old record is DELETED and a new record is
INSERTED
This table only stores current ownership (no historical tracking of ownership transfers)

License No: The vehicle (foreign key to Vehicle, also serves as primary key)
Department ID: The department that owns this vehicle (foreign key to Department)

A department may own multiple vehicles (1:M relationship)
A vehicle can only be owned by ONE department at any time
Vehicles owned by departments are typically fleet vehicles (patrol cars, maintenance trucks,
delivery vans)
If a vehicle is reassigned to another department, the old record is DELETED and a new
record is INSERTED

2.7.3 Company Vehicles
The system must track vehicles owned by external companies (vendor/contractor vehicles).
Each ownership record is uniquely identified by license number. The stored data must include:

Primary Key: license_no (sole primary key enforces exclusive ownership)
Business Rules:

Design Rationale for 3-Table Split:
Without this split (using a single Vehicle table with nullable owner FKs):

With the 3-table split (current design):

Query Examples:
License No: The vehicle (foreign key to Vehicle, also serves as primary key)
Company ID: The company that owns this vehicle (foreign key to Company)

A company may own multiple vehicles (1:M relationship)
A vehicle can only be owned by ONE company at any time
Company vehicles are typically vendor trucks, contractor vans, or delivery vehicles
If a vehicle's company ownership changes, the old record is DELETED and a new record is
INSERTED

Cannot enforce "exactly one owner" constraint (database allows all 3 FKs to be NULL or
multiple to be non-NULL)
Update anomaly: Changing ownership requires UPDATE with risk of invalid states
Violates 3NF: Nullable foreign keys create multi-valued dependencies

Exclusive ownership enforced: A license_no can exist in only ONE of the 3 tables
No update anomalies: Ownership transfer = DELETE from one table + INSERT into another
(atomic operation)
3NF compliant: No nullable FKs, no multi-valued dependencies
Type safety: Query Person_Vehicles returns only personally-owned vehicles
Database-level enforcement: Cannot accidentally create dual ownership

1. Find all vehicles owned by Person #123:
SELECT v.* FROM Vehicle v
JOIN Person_Vehicles pv ON v.license_no = pv.license_no
WHERE pv.national_id = '123';

2. Find owner of vehicle ABC-1234:

Database Enforcement:
The exclusive ownership rule is enforced by triggers:

Example Violation (Blocked):
-- Check Person ownership
SELECT 'Person' as owner_type, p.first_name, p.last_name
FROM Person_Vehicles pv
JOIN Person p ON pv.national_id = p.national_id
WHERE pv.license_no = 'ABC-1234'
UNION ALL
-- Check Department ownership
SELECT 'Department' as owner_type, d.department_name, NULL
FROM Department_Vehicles dv
JOIN Department d ON dv.department_id = d.department_id
WHERE dv.license_no = 'ABC-1234'
UNION ALL
-- Check Company ownership
SELECT 'Company' as owner_type, c.company_name, NULL
FROM Company_Vehicles cv
JOIN Company c ON cv.company_id = c.company_id
WHERE cv.license_no = 'ABC-1234';

3. Transfer vehicle from Person to Department:

BEGIN TRANSACTION;
DELETE FROM Person_Vehicles WHERE license_no = 'ABC-1234';

INSERT INTO Department_Vehicles (license_no, department_id) VALUES ('ABC-
1234', 'DEPT-01');

COMMIT;

trg_exclusive_person_vehicle_ownership - Blocks Person ownership if vehicle exists in
Department/Company tables
trg_exclusive_dept_vehicle_ownership - Blocks Department ownership if vehicle exists
in Person/Company tables
trg_exclusive_company_vehicle_ownership - Blocks Company ownership if vehicle
exists in Person/Department tables

INSERT INTO Person_Vehicles (license_no, national_id) VALUES ('ABC123',
'P001');
INSERT INTO Department_Vehicles (license_no, department_id) VALUES ('ABC123',

3. ORGANIZATIONAL STRUCTURE
3.1 Department
The system must maintain the organizational hierarchy for compound operations. Each
department is uniquely identified by a Department ID. The stored data must include:

Business Rule: Staff members are assigned to departments via the Task Monthly Salary
relationship. Department managers are tracked via the separate "Department Managers" table
with temporal history (see Section 8.5). Departments may authorize access permits for their
staff and may own vehicles.
3.1.1 Department Location History
The system must track where departments are physically located over time. Each location
record is uniquely identified by a composite key. The stored data must include:

Primary Key: department_id + location_start_date_in_building
Business Rules:

3.2 Position
The system must define job roles available within the organization. Each position is uniquely
identified by a Position ID. The stored data must include:
'DEPT01');
-- ERROR: Vehicle already owned by another entity (Person)

Department Name: Official designation (e.g., "Security", "Maintenance", "Administration")

Department ID: The department (foreign key to Department)
Location Start Date in Building: When the department moved to this building (part of
primary key)
Location End Date in Building: When the department left this building (NULL if currently
there)
Building ID: The building where department is/was located (foreign key to Building)

A department may relocate to different buildings over time (history preserved)
The current location is the record with the most recent location_start_date_in_building
where location_end_date_in_building IS NULL
This enables tracking of organizational moves without losing historical data

Business Rule: Positions belong to departments (many positions can exist within one
department). The Position entity stores only the identity (name); salary information is tracked
separately in Position_Salary_History to preserve historical records.
3.2.1 Position Salary History
The system must track salary caps and hourly rates over time for each position. Each salary
record is uniquely identified by a composite key. The stored data must include:

Primary Key: position_id + salary_effective_date
Business Rules:

3.3 Task
The system must catalog standard duties associated with staff assignments. Each task is
uniquely identified by a Task ID. The stored data must include:

Business Rule: Tasks are connected to staff assignments, not to individual people.
When a person is assigned to a department, they may be assigned specific tasks that belong
to that department. This models ongoing responsibilities, not one-time work orders.
3.3.1 Assigned Task
The system must track task assignments to staff members by department, shift, and date. Each
assignment record is uniquely identified by a composite key. The stored data must include:
Position Name: Title of the role (e.g., "Security Guard", "Plumber", "Receptionist")

Position ID: The position (foreign key to Position)
Salary Effective Date: When this salary structure became active (part of primary key)
Maximum Salary: The monthly salary ceiling for this position at this point in time
Base Hourly Rate: The standard hourly rate for this position (used to calculate daily_salary)

When a position's salary structure changes, a new row is inserted (old rows preserved)
The current salary structure is the one with the most recent salary_effective_date
Employment offers cannot exceed the maximum_salary active at the time of offer
The base_hourly_rate is used to calculate daily wages for staff in this position
Monthly salary constraint: (base_hourly_rate × average_monthly_hours) must be ≤
maximum_salary

Task Title: Description of the duty.

Department ID: The department context (foreign key to Department)
Shift ID: The shift during which the task is assigned (foreign key to Shift Attendance Type)

Primary Key: staff_national_id + task_id + year + month + day
Business Rules:

3.4 Shift Attendance Type
The system must define shift categories. Each shift type is uniquely identified by a Shift ID. The
stored data must include:

Business Rule: Shift Attendance Type is the "Parent" (identity). The actual rules (hours, bonuses)
are stored in the child table Law of Shift Attendance.
3.4.1 Law of Shift Attendance
The system must define the rules and policies for each shift type over time. Each shift rule is
uniquely identified by a composite key. The stored data must include:

Primary Key: shift_id + effective_date
Business Rules:
Staff National ID: The employee assigned (foreign key to Person)
Task ID: The specific task being assigned (foreign key to Task)
Year: The year of the assignment
Month: The month of the assignment
Day: The day of the assignment

This table records which staff member is assigned which task on which day
A staff member may be assigned multiple tasks on the same day
The same task may be assigned to different staff members
This is the "assignment" table; actual attendance is tracked in Attends (3.4.2)
The monthly aggregation for salary calculation is in Task Monthly Salary (8.4)

Shift ID: Unique identifier (primary key)
Shift Name: Designation of the shift (e.g., "Morning Shift", "Night Shift")

Shift ID: The shift type (foreign key to Shift Attendance Type)
Effective Date: When these rules became active (part of primary key)
Start Time: Scheduled beginning time (TIME datatype allows minute precision: 09:01,
14:30)
End Time: Scheduled ending time (TIME datatype allows minute precision: 17:30, 22:15)
One Hour Extra Bonus: Compensation rate for overtime
One Hour Difference Discount: Penalty rate for arriving late or leaving early
Period Start End: Derived attribute calculating shift duration

3.4.2 Attends (Daily Attendance Log)
The system must track daily staff attendance. Each attendance record is uniquely identified by
a composite key. The stored data must include:

Primary Key: national_id + shift_id + date
Business Rules:
When shift rules change (hours, bonus rates), a new row is inserted with a new
effective_date
The current rules are those with the most recent effective_date for each shift_id
The period_start_end is derived and should not be stored redundantly

National ID: The staff member (foreign key to Person)
Shift ID: The shift worked (foreign key to Shift Attendance Type)
Date: The calendar date (part of primary key)
Check In Time: Actual arrival time
Check Out Time: Actual departure time
Is Absent: Boolean flag for absence
Period Out In: Duration worked
Diff Hour: Difference from scheduled hours (positive = overtime, negative = undertime)
Daily Salary: Base pay for this day (derived from position salary / working days)
Daily Bonus: Calculated bonus for this day (overtime, performance)
Daily Deduction: Penalties for this day (late arrival, early departure, absence)
Daily Net Salary: Final pay for this day (daily_salary + daily_bonus - daily_deduction)

Attendance is recorded daily per staff member per shift
Daily salary calculation: daily_salary = offered_hourly_rate × actual_hours_worked
offered_hourly_rate comes from Employment_Offers for this staff member
actual_hours_worked = (check_out_time - check_in_time) converted to hours
Bonus calculation: daily_bonus = one_hour_extra_bonus × overtime_hours
overtime_hours = MAX(0, actual_hours_worked - scheduled_shift_hours)
one_hour_extra_bonus comes from Law_of_Shift_Attendance for this shift on this date
Penalty calculation: daily_deduction = one_hour_diff_discount × undertime_hours +
absence_penalty
undertime_hours = MAX(0, scheduled_shift_hours - actual_hours_worked)
one_hour_diff_discount comes from Law_of_Shift_Attendance
absence_penalty applied if is_absent = TRUE
Final calculation: daily_net_salary = daily_salary + daily_bonus - daily_deduction
The daily attendance records are aggregated to calculate monthly salary in
Task_Monthly_Salary

3.5 Employment Offer
Employment offer is documented in Section 8.3 as it is a child of the Applications entity in the
HR Module.

4. ACCESS CONTROL MODULE
4.1 Access Permit
The system must maintain a registry of all access credentials issued within the compound. Each
permit is uniquely identified by a Permit ID. The stored data must include:

The access permit serves as the central authorization instrument in the security model.
It does not directly represent a person or vehicle, but rather acts as a credential token that can
be issued through multiple pathways and held by various entities.
This design enables sophisticated access control where one person may hold multiple permits
for different purposes (e.g., a staff badge for work access and a resident card for personal unit
access).
Business Rule: Every permit must have exactly one status at any time. A permit cannot be
issued without an authorization source.
There are three valid issuance pathways:

A person may hold multiple permits simultaneously (1-to-many relationship).
Vehicle permits are linked via the "Identified By" relationship
(one vehicle may have multiple permits).
Permit ID: Unique identifier (primary key)
Access Level: Privilege classification ("Full Access", "Restricted Areas", "Common Areas
Only")
Status: Current validity state ("Active", "Expired", "Suspended")
Issue Date: Date when credential was created
Expiry Date: Date when credential becomes invalid
Type: Category of credential ("Staff Badge", "Resident Card", "Vehicle Sticker", "Visitor
Pass")

1. Contract-Based Issuance: When a rental contract is activated, permits may be generated for
the contract parties via the "Grants" relationship
2. Department-Based Issuance: When staff are assigned to departments, permits may be
authorized via the "Authorizes Staff" relationship
3. Personal Ownership: Every active permit must be linked to a person who physically holds it
via the "Holds" relationship

4.1.1 Vehicle Permits

The system must track which access permits are assigned to which vehicles. Each vehicle-
permit linkage is uniquely identified by a composite key. The stored data must include:

Primary Key: license_no + permit_id
Business Rules:
License No: The vehicle (foreign key to Vehicle)
Permit ID: The access permit (foreign key to Access Permit)

A vehicle may have multiple permits (e.g., different access levels, multiple stickers)
A permit may be assigned to multiple vehicles (e.g., company fleet permit)
This implements the "Identified By" relationship
Vehicle permits enable gate entry without manual plate recording

4.2 Gate
The system must track all physical entry and exit points. Each gate is uniquely identified by a
Gate ID. The stored data must include:

Business Rule: All gates belong to the compound via the "Has Point of Entry" relationship. Only
gates with status "Active" may record entry events. The system must support multiple gates of
the same type.

4.3 Entry Logging
The system must record every instance of access permit usage at compound entry points. This
is implemented via the "Enters At" relationship between Access_Permit and Gate. The
relationship stores the following attributes:

Business Rules:

Database Enforcement: Triggers trg_immutable_entry_log_update and
trg_immutable_entry_log_delete block all UPDATE and DELETE operations on Enters_At
table. Any attempt to modify entry logs raises error: "Entry events are immutable and cannot be
updated/deleted."
Workaround for Corrections: If an entry was recorded incorrectly:
Gate ID: Unique identifier (primary key)
Gate Name: Descriptive designation (e.g., "Main Entrance", "Service Gate", "Gate #6",
“Rawda Gate”, “Bonus Gate”)
Type: Classification ("Pedestrian", "Vehicle", "Combined")
Status: Operational state ("Active", "Closed")

Timestamp: Exact date and time of the event (automatically generated)
Direction: Whether the movement was entry or exit ("In" / "Out")
Purpose: Optional declaration of visit reason (e.g., "Work", "Delivery", "Personal Visit")
Manual Plate Entry: Optional field for recording vehicle plates when automated scanning
fails or for unregistered visitor vehicles

Entry events are immutable once recorded (cannot be edited, only flagged for review)
Permits with status "Expired" or "Revoked" must not generate new entry events
The timestamp must be system-generated; manual backdating is prohibited
Direction tracking enables occupancy calculations (entries without corresponding exits)

1. Do NOT delete or update the wrong entry (blocked by trigger)
2. Add a comment/flag in a separate Entry_Log_Notes table (future enhancement)

3. Contact system administrator to investigate (database audit logs track all attempts)

5. CONTRACT & RESIDENCY MODULE
5.1 Contract
The system must store all rental and lease agreements. Each contract is uniquely identified by a
Contract ID. The stored data must include:

Business Rule: Each contract covers exactly one unit OR one facility (never both, never
multiple). The relationship "Covers" links Contract to Unit, while "Contract Facilities" (see
Section 5.4) links Contract to Facility. A contract cannot be activated (status = "Active") until at
least one party has signed it via the "Parties to" relationship. The final_price must equal the
sum of all generated installment amounts (validation check required).
Contract ID: Unique identifier (primary key)
Start Date: Effective beginning of the agreement
End Date: Scheduled termination date
Contract Type: Classification ("Residential Lease", "Commercial Lease", "Service
Agreement")
Contract Status: Current state ("Draft", "Active", "Terminated", "Expired")
Payment Frequency: Billing cycle ("Monthly", "Quarterly", "Semi-Annually", "Annually")
Final Price: Total monetary value of the contract term
Security Deposit Amount: Refundable amount collected at signing
Renewal Terms: Text description of extension conditions

The system must track who signs contracts via the "Parties to" relationship between Person and
Contract. This relationship stores:

Business Rules:

Staff Contracts: Company entities and Staff persons may also be parties to contracts for
commercial leases or service agreements. This uses a second "Parties to" relationship instance
to maintain clean separation between tenant contracts and staff/vendor contracts.
5.2.1 Person Parties to Contract Facility (Staff/Company Signatures)

The system must track signatures from staff members and company representatives on facility-
related contracts (commercial leases, service agreements). Each signature record is uniquely

identified by a composite key. The stored data must include:

Primary Key: staff_national_id + person_comp_national_id + contract_id
Business Rules:
Role: The signatory's legal designation ("Primary Signer", "Guarantor", "Corporate
Representative", "Emergency Contact")
Date Signed: Timestamp of signature execution

Single Primary Signer Rule: Each contract must have exactly one person with role =
"Primary Signer". Co-equal signers are not permitted in this system (business policy
decision).
Multiple Parties Allowed: A contract may have additional parties with roles like "Guarantor"
or "Emergency Contact", but only one Primary Signer.

Staff National ID: The staff member witnessing or authorizing (foreign key to Person)
Person Comp National ID: The company representative signing (foreign key to Person, who
works for a Company)
Contract ID: The contract being signed (foreign key to Contract)
Date Signed: Timestamp of signature execution

This table handles commercial contract signatures separately from tenant contracts
Links compound staff (authorizer) with company representatives (signatory)
Used for service agreements where a vendor company signs with compound staff approval
Distinct from Section 5.2 which handles tenant/resident contract parties

The system must record who actually lives in units under each contract via the "Resides Under"
relationship between Person and Contract. This relationship has a composite primary key
consisting of:

Additional stored attributes:

Design Rationale: The composite primary key allows the same person to reside under the same
contract multiple times with different move-in dates (e.g., temporary relocation then return).
This structure captures residency history without requiring a separate occupancy entity.
Business Rules:

5.4 Contract Unit (Unit Lease Relationship)
The system must track which units are leased under which contracts. Each contract-unit
linkage is uniquely identified by a composite key. The stored data must include:

Primary Key: contract_id + unit_id + created_at_timestamp
Business Rules:
Person ID (foreign key)
Contract ID (foreign key)
Move In Date (part of primary key to allow re-entry)

Move Out Date: When the person vacates (NULL if currently residing)
Relationship to Signer: Connection to Primary Signer ("Self", "Spouse", "Child", "Parent",
"Roommate")

A person may reside under multiple contracts simultaneously (e.g., maintains two
residences)
All residents under a contract are automatically evicted when Contract.status =
"Terminated" (move_out_date set to termination date)
The Primary Signer is not automatically a resident; they must have a "Resides Under" entry
if they occupy the unit
Query: "Who currently lives in Unit 101?" → Find Contract covering Unit 101 WHERE
status='Active', then find all Persons via "Resides Under" WHERE move_out_date IS NULL

Contract ID: The contract (foreign key to Contract)
Unit ID: The unit being leased (foreign key to Unit)
Created At Timestamp: System-generated timestamp when this linkage was created (part of
primary key)

5.5 Contract Facilities (Facility Lease Relationship)
The system must track which facilities are leased under which contracts. Each contract-facility
linkage is uniquely identified by a composite key. The stored data must include:

Primary Key: contract_id + facility_id + created_at_timestamp
Business Rules:
This table implements the "Covers" relationship between Contract and Unit for residential
leases
A contract may cover one unit (for residential leases)
The created_at_timestamp allows tracking if the same contract-unit linkage is created
multiple times (e.g., contract amended)
This is distinct from the "Contract Facilities" relationship (Contract → Facility) for
commercial leases
The timestamp in the primary key preserves historical changes if a contract is modified to
cover different units over time

Contract ID: The contract (foreign key to Contract)
Facility ID: The facility being leased (foreign key to Facility)
Created At Timestamp: System-generated timestamp when this linkage was created

This table implements the "Leases" relationship between Contract and Facility
A contract may lease one facility (for commercial leases)
The created_at_timestamp captures when the facility was added to the contract
This is distinct from the "Covers" relationship (Contract → Unit) for residential leases

6. FINANCIAL MODULE
6.1 Installment (Payment Obligations)
The system must model the payment schedule for contracts as a series of individual obligations.
Each installment is uniquely identified by an Installment ID. The stored data must include:

Installments are linked to contracts via the "Has Schedule" relationship (one contract has many
installments). This structure separates what is owed (Installment) from what was paid
(Payment), enabling accurate arrears tracking without re-calculating payment history.
Status Lifecycle:

Schedule Flexibility: The system natively supports custom payment schedules. Because each
installment is an independent row with its own amount_expected and due_date, the system can
model:

No special "override mechanism" is needed; staff simply insert the correct installment rows
when activating the contract.
Installment ID: Unique identifier (primary key)
Due Date: Date by which payment must be received
Amount Expected: Monetary value owed for this obligation
Status: Current state of the obligation ("Pending", "Partially Paid", "Paid", "Overdue",
"Cancelled")
Type: Classification of the installment ("Security Deposit", "Monthly Rent", "Quarterly Rent",
"Final Payment", "Adjustment")

1. Pending: Created but no payment received yet
2. Partially Paid: Sum of linked payments < amount_expected
3. Paid: Sum of linked payments >= amount_expected
4. Overdue: Status remains "Pending" AND due_date < current_date
5. Cancelled: Contract terminated before this installment's due date

Balloon payments (e.g., $5,000 deposit + $500/month)
Discounted months (e.g., first month $0, remaining months $1100)
Seasonal variations (e.g., higher rent in summer months)
Irregular schedules (e.g., pay on the 1st and 15th of each month)

Business Rules:
Installments are generated when Contract.status changes from "Draft" to "Active"
The sum of all Installment.amount_expected for a contract must equal Contract.final_price
(validation check)
Installments cannot be deleted once created; they must be marked "Cancelled" if the
contract terminates early
An installment becomes "Overdue" automatically at midnight on (due_date + 1)

6.2 Payment (Transaction Ledger)
The system must maintain a complete immutable record of all financial transactions. Each
payment is uniquely identified by a Transaction ID. The stored data must include:

Design Note: The national_id field is nullable to support the "Chain of Truth" pattern for
rental income:

This prevents linking rental payments directly to persons (which would duplicate the contract-
party relationship).

Payments are immutable events. Once a payment is recorded with reconciliation_status =
"Reconciled", it cannot be edited or deleted. Corrections require creating a reversal transaction
(negative amount) and a corrected transaction.
Business Rule: Every payment must link to exactly one bank account via the "Processed via"
relationship. This tracks which financial institution handled the transaction.

6.2.1 Payment Data Retention Policy
Critical Constraint: Payment records are permanently retained for audit and tax compliance.
Foreign Key Enforcement:
Transaction ID: Unique identifier (primary key)
Date: Timestamp when the transaction occurred
Amount: Monetary value transferred
Method: Channel used ("Bank Transfer", "Cash", "Check", "Credit Card", "Wire Transfer")
Direction: Cash flow classification ("Inbound" for income, "Outbound" for expenses)
Reference Number: External tracking code (bank confirmation, check number, receipt ID)
Reconciliation Status: Verification state ("Pending", "Reconciled", "Disputed")
Currency: Monetary unit (e.g., "USD", "EGP") for multi-currency support if needed
Account ID: Bank account used (foreign key to Bank_Account, NOT NULL)
National ID: Person reference (foreign key to Person, NULLABLE)

Rental Income Payments: national_id IS NULL (follow Payment → Installment → Contract
→ Person)
Direct Person Payments: national_id IS NOT NULL (salaries, dividends, refunds)
Vendor Payments: national_id IS NULL (follow Payment → Work_Order → Company)

Payment.account_id → Bank_Account uses ON DELETE RESTRICT
Payment.national_id → Person uses ON DELETE RESTRICT

Business Rule: Bank accounts and persons cannot be deleted if any payment references them.
This ensures:

Workaround: To "delete" an account/person with payment history:
Financial audit trail is never destroyed
IRS/tax compliance (7+ year retention requirement)
Legal protection in disputes

1. Mark as inactive (add is_active flag to table)
2. Prevent new transactions via application logic
3. Preserve all historical payment records

6.3 Payment Routing (Polymorphic Targeting)
The system supports three distinct payment types based on what the payment accomplishes:
6.3.1 Rental Income (Installment Payment)
When a tenant pays rent, the payment links to a specific installment via the "Pays_off"
relationship. This relationship stores no additional attributes; the linkage itself is sufficient.
Example Flow:
Payment #101 (Amount: $1000) → Pays_off → Installment #5 (Due: Feb 1st, Expected: $1000)
Business Rules:

Chain of Truth: To answer "Which tenant paid this?", follow: Payment → Installment → Contract
→ Person (via "Parties to"). Never link Payment directly to Person for rental income, as this
loses the connection to what specific obligation was satisfied.
Multiple payments can pay off one installment (partial payments are allowed)
One payment can only target one installment (no splitting; must create separate payment
records)
When SUM(Payment.amount WHERE Pays_off → Installment #X ) >=
Installment.amount_expected, the installment status updates to "Paid"
Overpayments (amount > amount_expected) create a credit balance that can be manually
applied to future installments

6.3.2 Direct Person Payment (Salary/Dividend)
When the compound pays staff salaries or investor dividends, the payment links directly to a
person via the "Transfers With" relationship. This relationship stores no additional attributes.
Example Flow:
Payment #202 (Amount: $5000, Direction: Outbound) → Transfers With → Person #7 (Investor)
Business Rules:

Key Distinction: Salaries are not modeled as installments because staff compensation is event-
driven (paid when payroll runs), not debt-driven (owed regardless of payment). If an employee

quits, future salary payments stop; if a tenant breaks lease, future rent installments still exist
(as "Cancelled" records for audit purposes).
This relationship is used ONLY for payments without an underlying installment obligation
Direction must be "Outbound" (money leaving compound)
Valid use cases: monthly staff salary, quarterly investor dividends, employee bonuses,
emergency cash advances
The system does not enforce person_type validation (i.e., it trusts staff to pay the right
people), but reports should flag unusual patterns

6.3.3 Vendor Payment (Work Order Settlement)
When the compound pays a vendor for maintenance or services, the payment links to the work
order via the "Pays Vendor For" relationship. This relationship stores no additional attributes.
Example Flow:
Payment #303 (Amount: $1500, Direction: Outbound) → Pays Vendor For → Work Order #88
(Cost: $1500)
Business Rules:

Expense Categorization: To answer "How much did we spend on maintenance last quarter?",
sum all payments WHERE "Pays Vendor For" relationship exists AND date in quarter.
This relationship is used for operational expenses (repairs, supplies, contracted services)
Direction must be "Outbound"
The payment amount may differ from Work_Order.cost_amount (e.g., negotiated discount,
progress payment, retainage)
A work order may have multiple payments (installment-based vendor contracts)
Work Order.job_status should update to "Paid" when SUM(Payments) >=
Work_Order.cost_amount

6.4 Bank Account
The system must track financial institutions used for transaction processing. Each bank
account is uniquely identified by an Account ID. The stored data must include:

Business Rules:
Account ID: Unique identifier (primary key)
Bank Name: Name of the financial institution
IBAN: International Bank Account Number (for domestic and international transfers)
SWIFT Code: Bank identifier code (for international wire transfers), not derived and entered
manually for now
Is Primary: Boolean flag indicating the default account for transactions

The compound can maintain at least one bank account with is_primary = true, because
when it is first created in the first few days it might not have a bank account yet, but it
should do, It’s partial but it can have.
Multiple entities may use bank accounts:
Person → Has → Bank Account: For tenant security deposit refunds and staff direct
deposit
Company → Has Banking → Bank Account: For vendor payment processing
Department → Has Banking → Bank Account: For the compound's operating account
(the "safe")
The system does NOT track current_balance as an attribute. This system is not a banking
platform; it only records which account processed each transaction.

6.5 Financial Integrity Rules
The following business rules ensure data consistency:
1. Double-Entry Principle: Every payment must link to exactly one target:
Inbound payments link to Installment (rental income) OR are received from an external
source
Outbound payments link to Person (salary/dividend) OR Work Order (expense)
A payment cannot have zero targets or multiple targets
2. Payment Allocation Priority: If a tenant pays $1500 when owing $1000 (current installment)
+ $500 (overdue installment), the system must allocate to overdue installments first
(enforced via application logic, not database constraint)
3. Immutability After Reconciliation: Once Payment.reconciliation_status = "Reconciled", the
record is locked. Corrections require:
Create reversal payment (negative amount, same relationships)
Create corrected payment (correct amount and relationships)
Link both to an incident report (future enhancement)
4. Currency Consistency: All payments linked to installments from the same contract must use
the same currency (enforced via application validation)
5. Temporal Validation: Payment.date cannot be in the future (system enforces date <=
today())

7. WORK ORDER MODULE
7.1 Work Order
The system must track maintenance requests and repairs. Each work order is uniquely
identified by a Work Order ID. The stored data must include:

Business Rules:

Status Lifecycle:
Work Order ID: Unique identifier (primary key)
Date Scheduled: When the work is planned to begin
Date Completed: When the work was finished (NULL if not yet done)
Cost Amount: Total expense for the job
Job Status: Current state ("Pending", "Assigned", "In Progress", "Completed", "Paid",
"Cancelled")
Description: Text summary of the required work
Priority: Urgency level ("Low", "Normal", "High", "Emergency")
Service Category: Type of work ("Plumbing", "Electrical", "HVAC", "Landscaping", "Cleaning",
"Security")

Work orders are created by compound staff via the "Requests" relationship (Person →
Requests → Work Order)
Only persons with person_type = "Staff" may create work orders (enforced via application
logic)
Residents and visitors CANNOT create work orders directly; they must report issues to staff,
or they do external work order but the compound doesn’t care about it, so it doesn’t track it.
Work orders are assigned to vendors via the "Executes" relationship (Company → Executes
→ Work Order), which stores the date_assigned attribute
Work orders target facilities via the "Performs On" relationship (Work Order → Performs On
→ Facility)

1. Pending: Created but not yet assigned to a vendor
2. Assigned: Vendor selected, work not yet started (date_assigned set)
3. In Progress: Vendor has begun work (date_scheduled reached)
4. Completed: Work finished (date_completed set)
5. Paid: Vendor payment processed (via "Pays Vendor For" relationship)
6. Cancelled: Work order voided before completion

8. HUMAN RESOURCES MODULE
8.1 Applications (The Parent Entity)
The system must track all job applications as the central anchor for the hiring process. Each
application is uniquely identified by a composite key. The stored data must include:

Primary Key: applicant_national_id + position_id + application_date
Design Rationale: Applications is a pure anchor entity with no descriptive attributes. Its sole
purpose is to group the hiring process (Recruitment interviews) and outcome
(Employment_Offers) under one composite key. The application's "status" is **implicitly
derived" from its children:

This design follows the "Process vs. Outcome" pattern (see War Diary Section R): the parent
holds identity, while children hold the events and results.
Business Rules:
Applicant National ID: The person applying (foreign key to Person)
Position ID: The job being applied for (foreign key to Position)
Application Date: When the application was submitted (part of primary key)

If Recruitment records exist → Application is in "Interviewing" state
If Employment_Offers record exists → Application resulted in "Hired"
If neither exists after a business-defined period → Implicitly rejected or applicant withdrew

The composite primary key allows the same person to re-apply for the same position at
different times
Applications serve as the "contextual anchor" for both interviews (Recruitment) and
employment offers (Employment_Offers)
An application must exist before any interview or offer can be recorded
Application status is derived from child records, not stored explicitly

8.2 Recruitment (Interview Log)
The system must track interview events as a child of Applications. Each interview is uniquely
identified by a composite key. The stored data must include:

Primary Key: applicant_national_id + position_id + application_date +
staff_national_id + interview_date
Business Rules:
Applicant National ID, Position ID, Application Date: Foreign key to Applications (inherited
from parent)
Staff National ID: The interviewer (foreign key to Person, must be staff)
Interview Date: When the interview occurred (part of primary key)
Interview Result: Outcome of this specific interview ("Pass", "Fail", "Pending")

One application can have MANY interviews (different staff members, different dates)
The interview records the process (meetings), not the final hiring decision
Interview results ("Pass", "Fail") inform the hiring decision but do not directly grant
employment
Multiple interviewers may evaluate the same candidate for the same application
The presence of interview records indicates the application is being actively evaluated

8.3 Employment Offers (Terms Entity)
The system must track employment offers as a sibling entity to Recruitment, both children of
Applications. Each offer is uniquely identified by the same key as Applications. The stored data
must include:

Primary Key: applicant_national_id + position_id + application_date
Design Rationale (Why Employment_Offers is NOT a child of Recruitment):

Business Rules:
Applicant National ID, Position ID, Application Date: Foreign key to Applications (same as
parent key)
Offered Maximum Salary: The monthly salary cap for this hire (must be ≤
Position_Salary_History.maximum_salary at time of offer)
Offered Hourly Rate: The actual hourly rate for this individual (must be ≤
Position_Salary_History.base_hourly_rate at time of offer)
Employment Start Date: When the employee will begin work

A salary is not attached to a specific interview. The offer is the result of the entire
evaluation process, not one specific meeting.
Employment_Offers and Recruitment are "siblings"—both point to Applications, but they do
not touch each other directly.

One application results in at most ONE employment offer (1:1 relationship)
The offered_maximum_salary is a monthly CAP; the offered_hourly_rate is the actual base
rate used for daily_salary calculations
Salary constraint: offered_hourly_rate ≤ Position_Salary_History.base_hourly_rate (active at
time of offer)
Salary constraint: offered_maximum_salary ≤ Position_Salary_History.maximum_salary
(active at time of offer)
Employment_start_date may differ from Application.application_date (time for onboarding)
This table exists only for successful applications; rejected applicants have no
Employment_Offers record

8.4 Task Monthly Salary (Staff Assignment)
The system must track staff work assignments and monthly compensation. Each assignment
record is uniquely identified by a composite key. The stored data must include:

Primary Key: staff_national_id + year + month
Business Rules:
Department ID: The department where the staff member works
Shift ID: The shift type assigned
Staff National ID: The employee (foreign key to Person)
Task ID: The specific duty assigned (foreign key to Task)
Year, Month: The temporal key for this assignment record
Monthly Salary: Base compensation for this month (aggregated from daily_salary in
Attends)
Monthly Bonus: Additional compensation for this month (aggregated from daily_bonus in
Attends)
Monthly Deduction: Penalties for this month (aggregated from daily_deduction in Attends)
Tax: Tax deduction amount
Monthly Net Salary: Final pay for this month (monthly_salary + monthly_bonus -
monthly_deduction - tax)

This table captures the monthly payroll calculation for each staff member
The monthly values are aggregated from the daily attendance records (Attends table)
A staff member may have multiple assignment records for different tasks or periods
Monthly net salary cannot exceed the Employment_Offers.offered_maximum_salary cap
Hourly Rate Constraint:
Position_Salary_History.base_hourly_rate ≥
Employment_Offers.offered_hourly_rate
Monthly Salary Constraint:
Position_Salary_History.maximum_salary ≥
Employment_Offers.offered_maximum_salary ≥
Task_Monthly_Salary.monthly_net_salary
Daily Calculation Formula:
daily_salary = Employment_Offers.offered_hourly_rate × actual_hours_worked

8.5 Department Managers
The system must track management assignments with temporal history. Each management
assignment is uniquely identified by a composite key. The stored data must include:

Primary Key: department_id + manager_national_id + management_start_date
Business Rules:

8.6 Supervision Hierarchy
The system must track managerial relationships via the "Supervision" unary relationship on
Person (Person → Person). Each supervision record is uniquely identified by a composite key.
The stored data must include:

Primary Key: super_national_id + national_id + supervision_start_date
Business Rules:
Department ID: The department being managed (foreign key to Department)
Manager National ID: The person managing (foreign key to Person)
Management Start Date: When this person became manager (part of primary key)
Management End Date: When this person stopped being manager (NULL if current)

A department may have multiple managers over time (history preserved via start/end dates)
Only ONE manager may be active per department at any time (management_end_date IS
NULL)
The start_date in the key allows tracking re-appointments (same person, different periods)
When a new manager is assigned, the previous manager's management_end_date must be
set

Super National ID: The supervisor (foreign key to Person)
National ID: The supervisee (foreign key to Person)
Supervision Start Date: When this supervision began (part of primary key)
Supervision End Date: When this supervision ended (NULL if current)

One person (Supervisor) may supervise many people (Supervisees) (1:M relationship)
A person may have at most one direct supervisor at any given time
The relationship does NOT enforce department boundaries; a manager from Security can
supervise someone in Maintenance if business rules permit
Circular supervision chains must be prevented via application logic (A supervises B
supervises A is invalid)
Supervision history is preserved via start/end dates

9. INFRASTRUCTURE & ASSET TRACKING
9.1 Physical Hierarchy
The system enforces a strict containment hierarchy:
Compound (1)
├── Has (1:M) → Building (M)
│ └── Has (1:M) → Unit (M) [if Building.building_type = "Residential"]
│ └── Has (1:M) → Facility (M) [if Building.building_type = "Non-Residential"]
└── Own (1:M) → Facility (M) [compound-level facilities not in buildings]
└── Has Point of Entry (1:M) → Gate (M)
Business Rules:

9.2 Vehicle Ownership
The system must track vehicle registration via multiple ownership pathways:

Business Rule: A vehicle can only be owned by ONE entity at a time (exclusive ownership). The
relationship type determines ownership category. All vehicles entering the compound must be
identified by an active access permit via the "Identified By" relationship.
A Building belongs to exactly one Compound (Total Participation)
A Unit belongs to exactly one Building (Total Participation)
All Facilities are indoor units and must belong to exactly one Building. Direct ownership of
facilities by the Compound (e.g., outdoor parks) is out of scope for this version.
Gates belong only to the Compound (not to Buildings)

Person → Owns → Vehicle (1:M): Personal vehicles of residents and staff
Company → Has → Vehicle (1:M): Company-owned vehicles (e.g., vendor trucks)
Department → Has → Vehicle (1:M): Compound fleet vehicles (e.g., security patrol cars)

10. CROSS-MODULE INTEGRATION RULES
10.1 Contract Termination Cascade
When Contract.status changes from "Active" to "Terminated", the following automated actions
must occur:

Business Rule: Future payments cannot be linked to cancelled installments (enforced via
application validation).

10.2 Person Blacklisting Impact
When Person.is_blacklisted changes from false to true:

Business Rule: Blacklisted persons cannot sign new contracts (enforced via application
validation at contract creation).
10.2.1 Database Enforcement of Blacklist Restrictions
The following business rules are automatically enforced by database triggers:
Enforced Rules:
1. Installment Cancellation: All installments with due_date > termination_date must update to
status = "Cancelled"
2. Permit Revocation: All access permits issued via "Grants" relationship must update to
status = "Revoked"
3. Residency Closure: All persons linked via "Resides Under" with move_out_date = NULL must
have move_out_date set to the termination_date
4. Financial Settlement Calculation: The system must calculate: (Total Paid via Installments) -
(Total Expected from Non-Cancelled Installments) = Refund Amount (if positive) or Arrears
(if negative)

1. Contract Review: All contracts where Person is linked via "Parties to" should be flagged for
manual review (status update optional, depends on business policy)
2. Permit Suspension: All access permits held by Person (via "Holds") must update to status =
"Suspended"
3. Entry Restriction: No new entry events may be created for suspended permits (enforced at
gate access control system)
4. Financial Obligations Persist: Existing installments and payment obligations remain valid;
blacklisting does not erase debt

1. Contract Signing: Blacklisted persons cannot sign new contracts (via
trg_blacklist_contract_check )

Application-Enforced Rules (Not in Database):

Rationale: Database triggers prevent new violations; application logic handles remediation of
existing records.
2. Facility Contracts: Blacklisted persons cannot participate in facility contracts (via
trg_blacklist_facility_contract_check )
3. Compound Entry: Blacklisted permit holders cannot enter compound (via
trg_valid_permit_only )

1. 📋 Permit Suspension: When person is blacklisted, application must update all their permits
to status='Suspended'
2. 📋 Contract Review: When person is blacklisted, application must flag all contracts where
they are a party for manual review

10.3 Department Dissolution
If a department is deleted or dissolved:

Business Rule: A department cannot be deleted while it has active assignments, authorized
permits, or owned assets.
1. Staff Reassignment Required: All persons with "Assigned" relationship to this department
must be reassigned to a different department before deletion (orphaned staff not permitted)
2. Permit Transfer: Access permits issued via "Authorizes Staff" must either be revoked or
transferred to new department
3. Asset Transfer: Vehicles and bank accounts owned by the department must be transferred
to another department or the compound

11. REPORTING & QUERY REQUIREMENTS
The system must efficiently support the following business intelligence queries:
11.1 Financial Reports
1. Arrears Report (Outstanding Rent)
Purpose: Identify tenants with overdue payments
Query: Join Contract → Installment → Paymen