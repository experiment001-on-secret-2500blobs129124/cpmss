# COMPOUND MANAGEMENT SYSTEM: FIGMA SCREEN INVENTORY

This document defines the screens/displays required to implement the CPMSS UI in Figma. Screens are grouped by module and ordered by user flow priority.

---

## Summary

| Module | Screen Count |
|---|---|
| Global / Navigation | 3 |
| Core Entities — Person | 4 |
| Core Entities — Compound, Building, Unit | 7 |
| Core Entities — Facility | 4 |
| Core Entities — Company & Vehicle | 5 |
| Organizational Structure | 7 |
| Access Control | 5 |
| Contract & Residency | 6 |
| Financial | 8 |
| Work Orders | 4 |
| Human Resources | 6 |
| Reporting & Dashboards | 9 |
| **TOTAL** | **68** |

---

## 1. GLOBAL / NAVIGATION (3 screens)

### Screen 1.1 — Login Page
- Username / password fields
- Compound branding/logo
- "Forgot password" link

### Screen 1.2 — Main Dashboard (Home)
- Summary cards: total units, occupancy rate, active contracts, open work orders, pending payments
- Quick-action buttons: New Contract, New Work Order, Register Person
- Alerts panel: overdue installments, expiring contracts, expiring permits
- Recent activity feed

### Screen 1.3 — Global Navigation Shell
- Sidebar with module links: People, Properties, Contracts, Finance, Work Orders, HR, Security, Reports
- Top bar: search, notifications bell, user profile menu
- Breadcrumb navigation
- This shell wraps all other screens

---

## 2. CORE ENTITIES — PERSON (4 screens)

### Screen 2.1 — Person List View
- Paginated/searchable table of all persons
- Columns: National ID, Full Name, Person Type, Phone, Email, Blacklisted badge
- Filters: Person Type (Staff/Investor/Tenant/Visitor), Blacklisted status, Nationality
- Bulk actions: Export, Blacklist toggle
- "Add Person" button

### Screen 2.2 — Person Detail View
- Header: Name, photo placeholder, National ID, Person Type badge, Blacklisted status
- **Info tab:** Personal details (DOB, gender, nationality, qualification), contact info (phones, emails, address)
- **Roles tab:** Derived roles with links (Staff → Employment Offers, Investor → Investment records, Tenant → Contract parties)
- **Contracts tab:** All contracts where person is a party, with role and status
- **Permits tab:** Access permits held (via "Holds")
- **Vehicles tab:** Vehicles owned
- **Residency tab:** Current and historical residencies (via "Resides Under")
- **Financial tab:** Payments linked directly (salary, dividends)
- Actions: Edit, Blacklist/Unblacklist

### Screen 2.3 — Person Create/Edit Form
- Fields: First Name, Last Name, National ID (read-only on edit), Nationality, Date of Birth, Gender, Qualification
- Dynamic phone fields (up to 2): Country Code + Number, with add/remove
- Dynamic email fields (up to 2), with add/remove
- Address fields: City, Street
- Person Type selector (auto-calculated, display-only)

### Screen 2.4 — Investment Records View
- Table of all investments for a person (or across persons)
- Columns: Investor Name, Compound, Timestamp, Stock (%)
- "Add Investment" form/modal: Investor selector, Stock amount

---

## 3. CORE ENTITIES — COMPOUND, BUILDING, UNIT (7 screens)

### Screen 3.1 — Compound Overview
- Single compound info card: Name, Address (Country/City/District)
- Summary stats: Total buildings, total units, total facilities, total gates
- Quick links to buildings list, gates list, bank accounts
- Edit compound details button

### Screen 3.2 — Building List View
- Table/card grid of all buildings
- Columns: Building Name, Number, Type (Residential/Non-Residential), Floors Count, Construction Date
- Filter by type
- "Add Building" button

### Screen 3.3 — Building Detail View
- Header: Building Name, Number, Type badge
- **Info tab:** Construction date, floors count
- **Units tab** (if Residential): List of units within this building — Unit Number, Floor, Status, Listing Price
- **Facilities tab** (if Non-Residential): List of facilities — Name, Category, Management Type
- **Departments tab:** Departments currently located in this building (via Department Location History)

### Screen 3.4 — Building Create/Edit Form
- Fields: Building Name, Building Number, Building Type (Residential/Non-Residential), Floors Count, Construction Date

### Screen 3.5 — Unit List View
- Paginated table of all units (across buildings)
- Columns: Unit Number, Building, Floor, Bedrooms, Bathrooms, Sq Ft, Current Status, Listing Price
- Filters: Building, Status (Vacant/Occupied/Reserved/Under Maintenance), Floor range, Bedroom count
- Color-coded status badges

### Screen 3.6 — Unit Detail View
- Header: Unit Number, Building Name, Floor, Status badge
- **Info tab:** Bedrooms, Bathrooms, Rooms, Total Rooms, Sq Ft, Balconies, View Orientation, Meter codes (Water/Gas/Electric)
- **Pricing History tab:** Timeline/table of listing prices with effective dates
- **Status History tab:** Timeline of status changes with dates
- **Contracts tab:** Current and past contracts covering this unit
- **Residents tab:** Current occupants (via active contract → Resides Under)
- Actions: Edit unit, Update price (adds new pricing history), Update status

### Screen 3.7 — Unit Create/Edit Form
- Fields: Unit Number, Building selector (Residential only), Floor Number, Bedrooms, Bathrooms, Rooms, Sq Ft, Balconies, View Orientation, Water/Gas/Electricity Meter codes
- Initial listing price field (creates first pricing history record)

---

## 4. CORE ENTITIES — FACILITY (4 screens)

### Screen 4.1 — Facility List View
- Table of all facilities
- Columns: Facility Name, Building, Category, Management Type, Current Hours, Current Manager
- Filters: Category (Recreation/Retail/Service/Common Area), Management Type

### Screen 4.2 — Facility Detail View
- Header: Facility Name, Category badge, Management Type
- **Info tab:** Building, current operating hours
- **Hours History tab:** Timeline of operating hours changes with effective dates
- **Manager History tab:** Timeline of manager assignments with start/end dates
- **Contracts tab:** Commercial lease contracts for this facility
- **Work Orders tab:** Maintenance work orders targeting this facility
- Actions: Edit, Update hours, Assign manager

### Screen 4.3 — Facility Create/Edit Form
- Fields: Facility Name, Building selector (Non-Residential only), Management Type, Category
- Initial operating hours: Opening Time, Closing Time

### Screen 4.4 — Facility Manager Assignment Modal
- Select person (search by name/National ID, filtered to staff)
- Start date picker
- Display current manager (if any) with option to end their assignment

---

## 5. CORE ENTITIES — COMPANY & VEHICLE (5 screens)

### Screen 5.1 — Company List View
- Table of all companies/vendors
- Columns: Company Name, Tax ID, Company Type, Phone
- Filters: Company Type
- "Add Company" button

### Screen 5.2 — Company Detail View
- Header: Company Name, Type badge
- **Info tab:** Tax ID, Phone
- **Employees tab:** Persons who work for this company (via Person_Works_for_Company)
- **Work Orders tab:** Work orders assigned to this company (via "Executes")
- **Vehicles tab:** Vehicles owned by this company
- **Contracts tab:** Contracts where company is a party
- Actions: Edit, Add Employee link

### Screen 5.3 — Company Create/Edit Form
- Fields: Company Name, Tax ID, Phone Number, Company Type

### Screen 5.4 — Vehicle List View
- Table of all registered vehicles
- Columns: License Plate, Model, Owner (Person/Company/Department name), Owner Type, Active Permit status
- Filters: Owner Type, Permit Status
- "Register Vehicle" button

### Screen 5.5 — Vehicle Detail / Register Form
- Fields: License Plate (read-only on edit), Vehicle Model
- Owner assignment section: radio select (Person/Company/Department) + searchable selector
- Permit linkages: list of associated permits with statuses
- Transfer ownership action (delete from one table, insert into another)

---

## 6. ORGANIZATIONAL STRUCTURE (7 screens)

### Screen 6.1 — Department List View
- Table of all departments
- Columns: Department Name, Current Manager, Current Location (Building), Staff Count
- "Add Department" button

### Screen 6.2 — Department Detail View
- Header: Department Name, Current Manager name
- **Info tab:** Current building location
- **Staff tab:** All staff assigned to this department (via Task Monthly Salary)
- **Manager History tab:** Timeline of managers with start/end dates
- **Location History tab:** Timeline of building locations with start/end dates
- **Vehicles tab:** Fleet vehicles owned by this department
- **Permits tab:** Access permits authorized by this department
- Actions: Edit, Assign manager, Relocate department

### Screen 6.3 — Position List View
- Table of all positions
- Columns: Position Name, Department, Current Max Salary, Current Base Hourly Rate
- "Add Position" button

### Screen 6.4 — Position Detail View
- Header: Position Name, Department
- **Salary History tab:** Timeline of salary structure changes (Max Salary, Base Hourly Rate, Effective Date)
- **Staff tab:** Current employees in this position (via Employment Offers → Applications)
- Actions: Update salary (creates new history record)

### Screen 6.5 — Shift Type List View
- Table of all shift attendance types
- Columns: Shift Name, Current Start Time, Current End Time, OT Bonus Rate, Late Penalty Rate
- "Add Shift Type" button

### Screen 6.6 — Shift Type Detail View
- Header: Shift Name
- **Current Rules:** Start Time, End Time, OT Bonus, Late Penalty
- **Rules History tab:** Timeline of rule changes with effective dates
- **Assigned Staff tab:** Staff currently on this shift
- Actions: Update rules (creates new law of shift record)

### Screen 6.7 — Task Management View
- Table of all tasks
- Columns: Task Title, Linked Department
- "Add Task" button
- Task assignment section: Assign staff to tasks by date (date picker, staff selector)

---

## 7. ACCESS CONTROL (5 screens)

### Screen 7.1 — Access Permit List View
- Table of all permits
- Columns: Permit ID, Type, Access Level, Holder Name, Status, Issue Date, Expiry Date
- Filters: Status (Active/Expired/Suspended), Type, Access Level
- Color-coded status badges
- "Issue Permit" button

### Screen 7.2 — Access Permit Detail View
- Header: Permit ID, Type badge, Status badge
- **Info tab:** Access Level, Issue Date, Expiry Date, Issuance pathway (Contract/Department/Personal)
- **Holder tab:** Person who holds this permit
- **Vehicle Linkages tab:** Vehicles assigned to this permit
- **Entry Log tab:** All entry/exit events using this permit (read-only)
- Actions: Suspend, Revoke, Renew

### Screen 7.3 — Issue Permit Form (Modal or Page)
- Issuance pathway selector: Contract-Based / Department-Based / Personal
- Conditional fields based on pathway:
  - Contract-Based: Contract selector, auto-populate holder
  - Department-Based: Department selector, Staff selector
  - Personal: Person selector
- Fields: Type, Access Level, Issue Date, Expiry Date
- Optional vehicle linkage: License Plate selector

### Screen 7.4 — Gate List View
- Table/cards of all gates
- Columns: Gate Name, Type (Pedestrian/Vehicle/Combined), Status (Active/Closed)
- Quick toggle for status
- "Add Gate" button with form: Gate Name, Type, Status

### Screen 7.5 — Entry Log View (Gate Activity)
- Paginated log table (read-only, immutable)
- Columns: Timestamp, Permit ID, Holder Name, Gate Name, Direction (In/Out), Purpose, Manual Plate
- Filters: Gate, Date range, Direction, Permit Type
- Search by permit ID or holder name
- Real-time feed mode toggle (auto-refresh)

---

## 8. CONTRACT & RESIDENCY (6 screens)

### Screen 8.1 — Contract List View
- Table of all contracts
- Columns: Contract ID, Type, Status, Primary Signer, Unit/Facility covered, Start Date, End Date, Final Price
- Filters: Status (Draft/Active/Terminated/Expired), Type, Date range
- Color-coded status badges
- "New Contract" button

### Screen 8.2 — Contract Detail View
- Header: Contract ID, Type badge, Status badge
- **Info tab:** Start/End dates, Payment Frequency, Final Price, Security Deposit, Renewal Terms
- **Parties tab:** All signatories with roles and signature dates (Person parties + Staff/Company parties)
- **Coverage tab:** Unit or Facility covered (with link to detail view)
- **Residents tab:** Persons residing under this contract (move-in/out dates, relationship to signer)
- **Installments tab:** Full payment schedule with status per installment
- **Permits tab:** Access permits granted through this contract
- Actions: Activate (Draft → Active), Terminate, Edit (Draft only)

### Screen 8.3 — Contract Create/Edit Wizard (Multi-step)
- **Step 1 — Basic Info:** Contract Type, Start Date, End Date, Payment Frequency, Final Price, Security Deposit, Renewal Terms
- **Step 2 — Coverage:** Select Unit (residential) OR Facility (commercial) — searchable selector filtered by availability
- **Step 3 — Parties:** Add Primary Signer (required), add additional parties (Guarantor, Emergency Contact). For commercial: add Staff authorizer + Company representative
- **Step 4 — Payment Schedule:** Auto-generate installments based on frequency & price, with override capability for each installment amount and due date. Validation: sum must equal final_price
- **Step 5 — Review & Submit:** Summary of all fields, submit as "Draft"

### Screen 8.4 — Resident Management View (per Contract)
- Table of residents under a specific contract
- Columns: Name, Relationship to Signer, Move-In Date, Move-Out Date, Status
- "Add Resident" form: Person selector, Relationship, Move-In Date
- "Move Out" action per resident (sets move_out_date)

### Screen 8.5 — Contract Activation Confirmation Modal
- Summary of what will happen upon activation:
  - Installment schedule will be generated
  - Access permits will be issued
  - Unit status will update to "Occupied"
- Confirm / Cancel buttons

### Screen 8.6 — Contract Termination Flow
- Reason for termination field
- Preview of cascade effects:
  - Installments to be cancelled (count + amount)
  - Permits to be revoked (count)
  - Residents to be evicted (names)
  - Settlement calculation (refund or arrears)
- Confirm / Cancel buttons

---

## 9. FINANCIAL (8 screens)

### Screen 9.1 — Installment List View
- Table of all installments (across contracts)
- Columns: Installment ID, Contract ID, Tenant, Due Date, Amount Expected, Amount Paid (derived), Status, Type
- Filters: Status (Pending/Partially Paid/Paid/Overdue/Cancelled), Date range, Contract
- Color-coded status: red for Overdue, yellow for Partial, green for Paid

### Screen 9.2 — Installment Detail View
- Header: Installment ID, Contract link, Status badge
- Info: Due Date, Amount Expected, Type
- **Payments tab:** All payments linked via "Pays_off" — Transaction ID, Date, Amount, Method
- Balance: Amount Expected − Sum of Payments = Remaining
- Actions: Record Payment (links to payment form)

### Screen 9.3 — Payment List View (Transaction Ledger)
- Paginated table of all payments
- Columns: Transaction ID, Date, Amount, Direction (In/Out), Method, Reconciliation Status, Bank Account, Linked Entity
- Filters: Direction, Method, Reconciliation Status, Date range, Bank Account
- "Record Payment" button

### Screen 9.4 — Payment Detail View
- Header: Transaction ID, Direction badge, Reconciliation Status badge
- Info: Date, Amount, Method, Currency, Reference Number
- Bank Account: Name, IBAN
- Linked Target: Installment (rental) / Person (salary) / Work Order (vendor) — with link
- Actions: Reconcile, Dispute, Create Reversal (if reconciled)

### Screen 9.5 — Record Payment Form
- Payment type selector: Rental Income / Staff Salary / Investor Dividend / Vendor Payment
- Conditional fields:
  - Rental: Contract selector → Installment selector (show overdue first per allocation priority)
  - Salary/Dividend: Person selector
  - Vendor: Work Order selector
- Common fields: Amount, Method, Reference Number, Currency, Bank Account selector
- Date: auto-populated (today), non-editable (temporal validation)

### Screen 9.6 — Bank Account List View
- Table of bank accounts
- Columns: Bank Name, IBAN, SWIFT Code, Is Primary badge, Linked Entity Type
- "Add Bank Account" button
- Toggle primary account

### Screen 9.7 — Bank Account Create/Edit Form
- Fields: Bank Name, IBAN, SWIFT Code, Is Primary toggle
- Ownership linkage: Entity type (Person/Company/Department) + entity selector

### Screen 9.8 — Financial Settlement View (per Contract)
- Shows settlement calculation for a contract:
  - Total expected (non-cancelled installments)
  - Total paid
  - Balance: Refund (positive) or Arrears (negative)
- Linked payments list
- Action: Process refund payment

---

## 10. WORK ORDERS (4 screens)

### Screen 10.1 — Work Order List View
- Table of all work orders
- Columns: Work Order ID, Description (truncated), Priority, Status, Service Category, Assigned Vendor, Date Scheduled, Date Completed
- Filters: Status, Priority (Low/Normal/High/Emergency), Service Category, Vendor, Date range
- Color-coded priority badges (red for Emergency, orange for High)
- "New Work Order" button

### Screen 10.2 — Work Order Detail View
- Header: Work Order ID, Priority badge, Status badge
- **Info tab:** Description, Service Category, Cost Amount, Date Scheduled, Date Completed
- **Requester:** Staff member who created the work order (link to Person)
- **Vendor Assignment:** Assigned company, date assigned (via "Executes")
- **Target Facility:** Facility this work targets (via "Performs On")
- **Payments tab:** Payments linked via "Pays Vendor For"
- Status progression bar (Pending → Assigned → In Progress → Completed → Paid)
- Actions: Assign Vendor, Mark In Progress, Mark Completed, Process Payment

### Screen 10.3 — Work Order Create Form
- Fields: Description (text area), Priority, Service Category, Date Scheduled
- Target Facility selector (searchable)
- Cost estimate field
- Requester: auto-populated with logged-in staff member

### Screen 10.4 — Work Order Assign Vendor Modal
- Vendor company selector (searchable, filtered by Company Type matching Service Category)
- Date assigned (auto-today)
- Confirm / Cancel

---

## 11. HUMAN RESOURCES (6 screens)

### Screen 11.1 — Application List View
- Table of all job applications
- Columns: Applicant Name, Position, Application Date, Status (derived: Interviewing/Offered/Pending/Rejected)
- Filters: Status, Position, Date range
- "New Application" button

### Screen 11.2 — Application Detail View
- Header: Applicant Name (link to Person), Position (link to Position), Application Date, Derived Status badge
- **Interviews tab:** All interview records — Interviewer, Date, Result (Pass/Fail/Pending)
- **Employment Offer tab:** If exists: Offered Max Salary, Offered Hourly Rate, Start Date
- Actions: Schedule Interview, Make Offer (if no offer exists)

### Screen 11.3 — Schedule Interview Modal
- Interviewer selector (staff only)
- Interview Date picker
- Confirm button
- After interview: update result (Pass/Fail/Pending)

### Screen 11.4 — Make Employment Offer Form
- Applicant + Position (pre-populated, read-only)
- Position salary constraints displayed: Current Max Salary, Current Base Hourly Rate
- Fields: Offered Maximum Salary (≤ position max), Offered Hourly Rate (≤ position base rate), Employment Start Date
- Validation warnings if offer exceeds constraints

### Screen 11.5 — Staff Attendance Grid
- Calendar-style grid view per month
- Rows: Staff members (filterable by department, shift)
- Columns: Days of the month
- Cells: Color-coded (green = present, red = absent, yellow = late, blue = overtime)
- Click cell → Day detail: Check-in/out times, hours worked, daily salary, bonus, deductions
- "Record Attendance" action for individual staff

### Screen 11.6 — Monthly Payroll View
- Table per month
- Columns: Staff Name, Department, Position, Monthly Salary, Monthly Bonus, Monthly Deduction, Tax, Net Salary
- Filters: Department, Month/Year
- Summary row: Totals for all columns
- Actions: Process Payroll (generate payments), Export

---

## 12. REPORTING & DASHBOARDS (9 screens)

### Screen 12.1 — Financial Reports Dashboard
- **Arrears Report:** Overdue tenants list with amounts
- **Revenue Summary:** Bar/line charts — inbound payments by period and type
- **Expense Summary:** Bar/line charts — outbound payments by category
- Date range picker, export to PDF/Excel

### Screen 12.2 — Payment Reconciliation Report
- Table of unreconciled payments (Pending/Disputed)
- Columns: Transaction ID, Date, Amount, Linked Entity, Age (days pending)
- Bulk reconcile action

### Screen 12.3 — Occupancy Reports Dashboard
- **Vacancy Rate:** Gauge chart + trend line over time
- **Occupancy Map:** Visual building/floor grid showing unit status with color coding
- **Turnover chart:** Units changing tenants per period
- Filter: Building, Date range

### Screen 12.4 — HR Reports Dashboard
- **Payroll Summary:** Monthly payroll totals by department (bar chart)
- **Attendance Summary:** Absence rate, overtime rate per department
- **Hiring Pipeline:** Funnel chart (Applied → Interviewing → Offered → Hired)
- Filter: Department, Period

### Screen 12.5 — Security Reports Dashboard
- **Gate Traffic:** Line chart of entries/exits over time per gate
- **Active Permits:** Pie chart by type, count badges
- **Expiring Permits:** Table of permits expiring within threshold
- **Blacklisted Persons:** Count badge + list link
- Filter: Gate, Date range

### Screen 12.6 — Work Order Reports Dashboard
- **Open Work Orders:** Count by priority (stacked bar)
- **Cost Analysis:** Total spend by service category and vendor (bar chart)
- **Vendor Performance:** Completion rate, average time, cost per vendor (table)
- Filter: Service Category, Vendor, Period

### Screen 12.7 — Contract Reports Dashboard
- **Expiring Contracts:** Table of contracts nearing end date
- **Contract Summary:** Searchable, detailed view per contract
- **Pricing History:** Line chart of listing price changes per unit
- Filter: Contract Type, Date range

### Screen 12.8 — Tenant Payment History View
- Per-tenant full payment history
- Timeline: All installments with due dates, amounts paid, outstanding balances
- Payment receipts linked per installment
- Search by tenant name or National ID

### Screen 12.9 — Investor Dividend Report
- Table: Investor name, Stock %, Dividends paid per period
- Cross-reference with investment records
- Filter: Period, Investor

---

## SCREEN COUNT BREAKDOWN

| # | Module | Screens |
|---|---|---|
| 1 | Global / Navigation | 3 |
| 2 | Person Management | 4 |
| 3 | Compound, Building, Unit | 7 |
| 4 | Facility Management | 4 |
| 5 | Company & Vehicle | 5 |
| 6 | Organizational Structure | 7 |
| 7 | Access Control | 5 |
| 8 | Contract & Residency | 6 |
| 9 | Financial | 8 |
| 10 | Work Orders | 4 |
| 11 | Human Resources | 6 |
| 12 | Reporting & Dashboards | 9 |
| | **TOTAL** | **68** |

> [!NOTE]
> This count (68 screens) represents unique page layouts. Many screens include modals and inline forms that are counted together with their parent screen. If modals are designed as separate Figma frames, the count may increase to ~80–85.
