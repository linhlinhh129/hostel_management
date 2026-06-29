# HOSTEL MANAGEMENT SYSTEM
## USE CASE SPECIFICATION & BUSINESS RULES

**Document Version:** 1.0  
**Date:** June 25, 2026  
**Project:** Hostel Management System  
**Document Type:** Use Case Specification & Business Rules  
**Status:** Approved

---

## TABLE OF CONTENTS

1. Introduction
2. Actors
3. Use Case Diagrams
4. Use Case Specifications
   - 4.1 Admin Use Cases
   - 4.2 Manager Use Cases
   - 4.3 Operator Use Cases
   - 4.4 Tenant Use Cases
5. Business Rules
6. Supplementary Specifications

---

## 1. INTRODUCTION

### 1.1 Purpose
This document describes the use cases and business rules for the Hostel Management System. It provides detailed specifications for system functionality from the perspective of different user roles.

### 1.2 Scope
The Hostel Management System is a web-based application designed to manage hostel operations including:
- Facility and room management
- Tenant management
- Meter reading and invoice generation
- Online payment processing (VNPay)
- Request/ticket management
- Notification system
- Audit logging and reporting

### 1.3 Definitions
- **Facility:** A hostel building/complex managed in the system
- **Room:** Individual rental unit within a facility
- **Invoice:** Monthly bill for room rent, utilities, and services
- **Payment:** Transaction record for invoice payment
- **Meter Reading:** Monthly recording of electricity and water consumption
- **Request:** Service request or maintenance ticket from tenant

---

## 2. ACTORS

### 2.1 Primary Actors

| Actor | Description | Goals |
|-------|-------------|-------|
| **ADMIN** | System administrator with highest privileges | Manage facilities, personnel, view system-wide reports |
| **MANAGER** | Facility manager (Ban Quản Lý) | Manage rooms, tenants, invoices, payments within assigned facility |
| **OPERATOR** | Operations staff | Record meter readings, handle maintenance requests |
| **TENANT** | Room renter | View invoices, make payments, submit requests |

### 2.2 Secondary Actors

| Actor | Description | Role |
|-------|-------------|------|
| **VNPay Gateway** | Third-party payment processor | Process online payments |
| **Email System** | SMTP email service | Send notifications and alerts |
| **Database System** | SQL Server | Store and retrieve data |


---

## 3. USE CASE OVERVIEW

### 3.1 Admin Use Cases
1. UC-A01: Manage Facilities
2. UC-A02: Manage Personnel (Manager/Operator)
3. UC-A03: View System-wide Reports
4. UC-A04: View Audit Logs
5. UC-A05: Send System-wide Notifications
6. UC-A06: Configure System Settings

### 3.2 Manager Use Cases
1. UC-M01: Manage Rooms
2. UC-M02: Manage Tenants
3. UC-M03: Manage Contracts
4. UC-M04: Create Monthly Invoices
5. UC-M05: Approve/Reject Payments
6. UC-M06: Manage Requests
7. UC-M07: Send Facility Notifications
8. UC-M08: View Facility Reports

### 3.3 Operator Use Cases
1. UC-O01: Record Meter Readings
2. UC-O02: Handle Maintenance Requests
3. UC-O03: Update Request Status

### 3.4 Tenant Use Cases
1. UC-T01: View Invoices
2. UC-T02: Pay Invoice Online (VNPay)
3. UC-T03: Pay Invoice by Bank Transfer
4. UC-T04: Submit Request
5. UC-T05: View Request Status
6. UC-T06: Manage Dependents
7. UC-T07: View Notifications
8. UC-T08: Change Password

---

## 4. USE CASE SPECIFICATIONS

### 4.1 ADMIN USE CASES


#### UC-A01: Manage Facilities

**Use Case ID:** UC-A01  
**Use Case Name:** Manage Facilities  
**Actor:** Admin  
**Priority:** High  
**Frequency of Use:** Low (setup phase)

**Description:**  
Admin creates, updates, views, and deletes hostel facilities in the system.

**Preconditions:**
- Admin is logged in
- Admin has ADMIN role

**Basic Flow:**
1. Admin navigates to "Facility Management"
2. System displays list of existing facilities
3. Admin clicks "Add New Facility"
4. System displays facility creation form
5. Admin enters facility information:
   - Facility name
   - Address
   - Electricity price per kWh
   - Water price per m³
   - Internet fee
   - Service fee
6. Admin clicks "Save"
7. System validates input data
8. System creates facility in database
9. System logs action in audit_logs
10. System displays success message
11. Use case ends

**Alternative Flows:**

**A1: Edit Existing Facility**
- At step 3, Admin clicks "Edit" on existing facility
- System displays edit form with current data
- Admin modifies information
- Continue from step 6

**A2: Delete Facility**
- At step 3, Admin clicks "Delete" on facility
- System checks if facility has rooms
- If no rooms exist, system performs soft delete (set deleted_at)
- System displays success message

**Exception Flows:**

**E1: Validation Error**
- At step 7, if validation fails
- System displays error message
- Return to step 5

**E2: Facility Has Active Rooms**
- During delete, if facility has rooms
- System displays error: "Cannot delete facility with existing rooms"
- Return to step 2

**Postconditions:**
- Facility is created/updated/deleted
- Action logged in audit_logs
- Manager can be assigned to facility

**Business Rules:**
- BR-01: Facility name must be unique
- BR-02: Cannot delete facility with rooms
- BR-03: Price fields must be positive numbers

---

#### UC-A02: Manage Personnel

**Use Case ID:** UC-A02  
**Use Case Name:** Manage Personnel  
**Actor:** Admin  
**Priority:** High  
**Frequency of Use:** Medium

**Description:**  
Admin creates Manager and Operator accounts, assigns them to facilities.

**Preconditions:**
- Admin is logged in
- At least one facility exists

**Basic Flow:**
1. Admin navigates to "Personnel Management"
2. System displays list of Manager and Operator accounts
3. Admin clicks "Add New Personnel"
4. System displays personnel creation form
5. Admin enters information:
   - Full name
   - Email (becomes username)
   - Phone
   - Role (MANAGER or OPERATOR)
   - Assigned facility
6. Admin clicks "Create"
7. System generates temporary password
8. System creates user account with force_change_pass = true
9. System sends email with credentials
10. System logs action
11. System displays success message with temp password
12. Use case ends

**Alternative Flows:**

**A1: Assign to Multiple Facilities**
- At step 5, Admin can select multiple facilities
- System creates assignments in user_facilities table

**Exception Flows:**

**E1: Email Already Exists**
- At step 8, if email exists
- System displays error
- Return to step 5

**E2: Email Send Failed**
- At step 9, if email fails
- System still creates account
- System displays warning: "Account created but email failed"
- Show temp password on screen

**Postconditions:**
- Personnel account created
- Temporary password sent via email
- Personnel can login and must change password

**Business Rules:**
- BR-04: Email must be unique
- BR-05: Temporary password valid until first login
- BR-06: Must assign at least one facility


---

### 4.2 MANAGER USE CASES

#### UC-M04: Create Monthly Invoices

**Use Case ID:** UC-M04  
**Use Case Name:** Create Monthly Invoices  
**Actor:** Manager  
**Priority:** Critical  
**Frequency of Use:** High (monthly)

**Description:**  
Manager creates monthly invoices for tenants based on meter readings.

**Preconditions:**
- Manager is logged in
- Meter readings exist for the billing period
- Room has active tenant

**Basic Flow:**
1. Manager navigates to "Invoice Management"
2. Manager clicks "Create Invoice"
3. System displays invoice creation form
4. Manager selects:
   - Room (dropdown)
   - Billing period (YYYYMM format)
5. System validates meter reading exists for period
6. System retrieves data:
   - Room fee from room
   - Previous meter reading
   - Current meter reading
   - Utility prices from facility
7. System auto-calculates:
   - Electric usage = new_electric - old_electric
   - Water usage = new_water - old_water
   - Electric amount = usage × electricity_price
   - Water amount = usage × water_price
   - Subtotal = room_fee + electric + water + internet + service
8. Manager enters:
   - Tax rate (%)
   - Other fees (if any)
   - Due date
   - Notes
9. System calculates:
   - Tax amount = subtotal × tax_rate / 100
   - Total amount = subtotal + tax
10. Manager reviews calculation
11. Manager clicks "Create Invoice"
12. System validates:
    - Invoice code unique (INV-{roomCode}-{period})
    - Due date not in past
    - New meter >= old meter
13. System creates invoice with status = UNPAID
14. System logs action
15. System sends notification to tenant
16. System displays success message
17. Use case ends

**Alternative Flows:**

**A1: Invoice Already Exists**
- At step 12, if invoice exists for room+period
- System displays error
- Return to step 4

**Exception Flows:**

**E1: No Meter Reading**
- At step 5, if no meter reading found
- System displays error: "No meter reading for {period}"
- Return to step 4

**E2: Invalid Meter Reading**
- At step 12, if new < old
- System displays error
- Return to step 4

**Postconditions:**
- Invoice created with status UNPAID
- Tenant can view invoice
- Invoice appears in payment dashboard

**Business Rules:**
- BR-07: One invoice per room per period
- BR-08: Invoice code format: INV-{roomCode}-{YYYYMM}
- BR-09: New meter must >= old meter
- BR-10: Due date must be future date
- BR-11: Tax rate 0-100%


---

#### UC-M05: Approve/Reject Payments

**Use Case ID:** UC-M05  
**Use Case Name:** Approve/Reject Payments  
**Actor:** Manager  
**Priority:** High  
**Frequency of Use:** High (daily)

**Description:**  
Manager reviews and approves/rejects bank transfer payments from tenants.

**Preconditions:**
- Manager is logged in
- Payment with status PENDING exists

**Basic Flow:**
1. Manager navigates to "Payment Management"
2. System displays list of payments filtered by manager's facility
3. System highlights PENDING payments
4. Manager clicks on payment to review
5. System displays payment details:
   - Transaction code
   - Invoice information
   - Amount
   - Payment date
   - Payment method
   - Proof image (if bank transfer)
6. Manager reviews proof of payment
7. Manager clicks "Approve"
8. System validates manager has authority
9. System updates payment status = SUCCESS
10. System updates invoice status = PAID
11. System updates invoice.updated_at
12. System logs approval action
13. System sends confirmation to tenant
14. System displays success message
15. Use case ends

**Alternative Flows:**

**A1: Reject Payment**
- At step 7, Manager clicks "Reject"
- Manager enters rejection reason
- System updates payment status = REJECTED
- System logs rejection with reason
- System sends notification to tenant
- Return to step 2

**A2: VNPay Payment (Auto-approved)**
- VNPay payments have status SUCCESS immediately
- No approval needed
- Manager can only view details

**Exception Flows:**

**E1: Payment Already Processed**
- At step 9, if payment already SUCCESS/REJECTED
- System displays error
- Return to step 2

**E2: Invoice Not Found**
- At step 10, if invoice doesn't exist
- System rollback transaction
- Display error

**Postconditions:**
- Payment status updated
- Invoice status updated if approved
- Tenant notified
- Action logged

**Business Rules:**
- BR-12: Only PENDING payments can be approved/rejected
- BR-13: VNPay payments auto-approved
- BR-14: Manager can only approve payments for their facility
- BR-15: Approval action logged in audit


---

### 4.3 OPERATOR USE CASES

#### UC-O01: Record Meter Readings

**Use Case ID:** UC-O01  
**Use Case Name:** Record Meter Readings  
**Actor:** Operator  
**Priority:** Critical  
**Frequency of Use:** High (monthly)

**Description:**  
Operator records monthly electricity and water meter readings with photos.

**Preconditions:**
- Operator is logged in
- Assigned to facility

**Basic Flow:**
1. Operator navigates to "Meter Readings"
2. Operator selects billing period (month/year)
3. System displays list of rooms needing updates
4. Operator clicks on room
5. System displays meter entry form with previous readings
6. Operator enters:
   - New electric reading
   - New water reading
7. Operator uploads:
   - Electric meter photo
   - Water meter photo
8. Operator clicks "Save"
9. System validates:
   - New reading >= old reading
   - Photos are valid images
10. System saves meter_reading record
11. System logs action
12. System displays success message
13. Use case ends

**Exception Flows:**

**E1: Invalid Reading**
- At step 9, if new < old
- Display error: "New reading must be >= previous reading"
- Return to step 6

**E2: Photo Upload Failed**
- At step 9, if photo invalid
- Display error
- Return to step 7

**Postconditions:**
- Meter reading recorded
- Manager can create invoice

**Business Rules:**
- BR-16: New reading >= old reading
- BR-17: Photos required for transparency
- BR-18: One reading per room per month

---

### 4.4 TENANT USE CASES

#### UC-T02: Pay Invoice Online (VNPay)

**Use Case ID:** UC-T02  
**Use Case Name:** Pay Invoice Online via VNPay  
**Actor:** Tenant, VNPay Gateway  
**Priority:** Critical  
**Frequency of Use:** High (monthly)

**Description:**  
Tenant pays invoice online using VNPay payment gateway.

**Preconditions:**
- Tenant is logged in
- Invoice exists with status UNPAID or OVERDUE
- Tenant owns the invoice

**Basic Flow:**
1. Tenant navigates to "My Invoices"
2. System displays invoice list
3. Tenant clicks on unpaid invoice
4. System displays invoice details with amounts
5. System checks if invoice is overdue
6. If overdue, system calculates late penalty:
   - Days late = current_date - due_date
   - Penalty rate = 0.0005 × days_late
   - Penalty = total_amount × penalty_rate
   - Display warning with penalty details
7. System calculates total to pay:
   - Total = invoice_amount + penalty
8. Tenant clicks "Pay with VNPay"
9. System creates payment request to VNPay:
   - Amount (in xu = VND × 100)
   - Transaction reference: INV{id}T{timestamp}
   - Return URL
   - Order info
   - Secure hash (HMAC-SHA512)
10. System redirects tenant to VNPay
11. Tenant selects payment method on VNPay
12. Tenant completes payment
13. VNPay processes transaction
14. VNPay redirects back with result
15. System receives callback
16. System validates secure hash
17. System parses response:
    - Response code = "00" (success)
    - Transaction number
    - Amount
18. System executes database transaction:
    - INSERT payment (status=SUCCESS, amount includes penalty)
    - UPDATE invoice (status=PAID)
    - COMMIT transaction
19. System sends confirmation email
20. System displays success message
21. Use case ends

**Alternative Flows:**

**A1: Payment Failed**
- At step 14, if VNPay returns error code
- System displays error message
- System does NOT create payment record
- Allow tenant to retry

**A2: Payment Cancelled**
- Tenant cancels on VNPay page
- Return to invoice list

**Exception Flows:**

**E1: Invalid Secure Hash**
- At step 16, if hash invalid
- System rejects transaction
- Display error: "Invalid transaction signature"

**E2: Database Transaction Failed**
- At step 18, if transaction fails
- System rollback
- Display error
- Contact support

**E3: Concurrent Payment**
- If invoice already PAID
- Display: "Invoice already paid"
- Redirect to invoice list

**Postconditions:**
- Payment recorded with late penalty (if any)
- Invoice marked as PAID
- Tenant receives confirmation
- Manager sees updated payment

**Business Rules:**
- BR-19: Late penalty calculated real-time
- BR-20: Penalty formula: amount × 0.0005 × days_late
- BR-21: Total charged includes penalty
- BR-22: VNPay transaction must be validated
- BR-23: Anti-IDOR: Tenant can only pay own invoices


---

## 5. BUSINESS RULES

### 5.1 Authentication & Authorization Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-01 | Users must login before accessing system | All |
| BR-02 | Sessions expire after 30 minutes of inactivity | All |
| BR-03 | Passwords must meet complexity requirements (min 8 chars, 1 uppercase, 1 number, 1 special) | Login, Change Password |
| BR-04 | First-time users must change temporary password | UC-A02 |
| BR-05 | Failed login attempts limited to 5 times | Login |
| BR-06 | Account locked for 15 minutes after 5 failed attempts | Login |
| BR-07 | Each user has exactly one role: ADMIN, MANAGER, OPERATOR, or TENANT | All |

### 5.2 Facility & Room Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-10 | Facility name must be unique | UC-A01 |
| BR-11 | Cannot delete facility with existing rooms | UC-A01 |
| BR-12 | Room code must be unique within facility | UC-M01 |
| BR-13 | Room can have only one active tenant at a time | UC-M02 |
| BR-14 | Room status: AVAILABLE, OCCUPIED, MAINTENANCE | UC-M01 |
| BR-15 | Room rent must be positive number | UC-M01 |

### 5.3 Meter Reading Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-20 | New meter reading must be >= previous reading | UC-O01 |
| BR-21 | One meter reading per room per month | UC-O01 |
| BR-22 | Photos required for both electric and water meters | UC-O01 |
| BR-23 | Meter photos must be valid image formats (JPG, PNG) | UC-O01 |

### 5.4 Invoice Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-30 | Invoice code format: INV-{roomCode}-{YYYYMM} | UC-M04 |
| BR-31 | One invoice per room per billing period | UC-M04 |
| BR-32 | Invoice requires meter reading for that period | UC-M04 |
| BR-33 | Invoice status: UNPAID, PAID, OVERDUE | UC-M04, UC-T02 |
| BR-34 | Invoice auto-changes to OVERDUE when due_date passed | System |
| BR-35 | Tax rate must be 0-100% | UC-M04 |
| BR-36 | Due date must be future date at creation | UC-M04 |
| BR-37 | PAID invoices cannot be edited or deleted | UC-M04 |
| BR-38 | Invoice calculation:<br>Subtotal = room_fee + electric + water + internet + service + other<br>Tax = subtotal × tax_rate / 100<br>Total = subtotal + tax | UC-M04 |

### 5.5 Payment Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-40 | Payment methods: VNPAY, BANK_TRANSFER | UC-T02, UC-T03 |
| BR-41 | VNPay payments auto-approved (status=SUCCESS) | UC-T02 |
| BR-42 | Bank transfer payments require manager approval | UC-T03, UC-M05 |
| BR-43 | Payment status: PENDING, SUCCESS, REJECTED | All payment UCs |
| BR-44 | Only PENDING payments can be approved/rejected | UC-M05 |
| BR-45 | Approved payment updates invoice to PAID | UC-M05 |
| BR-46 | Payment amount can be partial (multiple payments per invoice) | UC-T02, UC-T03 |
| BR-47 | Invoice marked PAID when total payments >= invoice amount | System |

### 5.6 Late Payment Penalty Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-50 | Late penalty calculated if payment_date > due_date | UC-T02 |
| BR-51 | Current penalty formula:<br>penalty = total_amount × 0.0005 × days_late | UC-T02 |
| BR-52 | Alternative penalty formula (configurable):<br>penalty = room_fee × 0.01 × days_late | UC-T02 |
| BR-53 | Days late = payment_date - due_date | UC-T02 |
| BR-54 | Penalty calculated real-time at payment | UC-T02 |
| BR-55 | Tenant sees penalty warning before payment | UC-T02 |
| BR-56 | Payment amount includes penalty | UC-T02 |
| BR-57 | No maximum limit on penalty (grows indefinitely) | UC-T02 |

### 5.7 Request/Ticket Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-60 | Request types: MAINTENANCE, INQUIRY, OTHER | UC-T04 |
| BR-61 | Request status flow: PENDING → ASSIGNED → IN_PROGRESS → DONE | UC-M06, UC-O02 |
| BR-62 | Cannot skip status in workflow | UC-O02 |
| BR-63 | Tenant can only create requests for their room | UC-T04 |
| BR-64 | Photos optional but recommended | UC-T04 |
| BR-65 | Maximum 3 photos per request | UC-T04 |

### 5.8 Notification Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-70 | Admin can send to all users | UC-A05 |
| BR-71 | Manager can send to facility tenants only | UC-M07 |
| BR-72 | Notifications can target: all, facility, specific rooms | UC-A05, UC-M07 |
| BR-73 | Notification channels: web + email | UC-A05, UC-M07 |
| BR-74 | System auto-sends notification on: invoice created, payment success | System |

### 5.9 Audit & Security Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-80 | All critical actions logged in audit_logs | All |
| BR-81 | Audit log includes: user, action, entity, old/new value, timestamp, IP | All |
| BR-82 | Passwords hashed with BCrypt (cost 12) | Login, Change Password |
| BR-83 | Anti-IDOR: Users can only access their own resources | All |
| BR-84 | SQL injection prevented via PreparedStatement | All |
| BR-85 | VNPay transactions validated with HMAC-SHA512 | UC-T02 |
| BR-86 | Soft delete used (deleted_at timestamp) | All delete operations |

### 5.10 Data Validation Rules

| Rule ID | Rule Description | Affected Use Cases |
|---------|-----------------|-------------------|
| BR-90 | Email must be valid format and unique | UC-A02, UC-M02 |
| BR-91 | Phone format: 10-11 digits | UC-A02, UC-M02 |
| BR-92 | Identity number (CMND): 9 or 12 digits | UC-M02 |
| BR-93 | All monetary values must be non-negative | UC-M01, UC-M04 |
| BR-94 | Dates must be valid calendar dates | UC-M02, UC-M04 |
| BR-95 | Image files: JPG, PNG, max 5MB | UC-O01, UC-T04 |


---

## 6. SUPPLEMENTARY SPECIFICATIONS

### 6.1 Performance Requirements

| Requirement | Specification |
|-------------|---------------|
| Response Time | < 2 seconds for normal operations |
| Payment Processing | < 5 seconds for VNPay redirect |
| Page Load | < 3 seconds for dashboard |
| Concurrent Users | Support 100+ simultaneous users |
| Database Query | < 1 second for complex queries |

### 6.2 Security Requirements

| Requirement | Specification |
|-------------|---------------|
| Authentication | Session-based with 30-min timeout |
| Password Policy | Min 8 chars, 1 uppercase, 1 number, 1 special |
| Data Encryption | HTTPS for all communications |
| SQL Injection | Prevented via PreparedStatement |
| XSS Protection | Input sanitization and output encoding |

### 6.3 Usability Requirements

| Requirement | Specification |
|-------------|---------------|
| User Interface | Responsive design (mobile-friendly) |
| Browser Support | Chrome, Firefox, Safari, Edge (latest versions) |
| Accessibility | WCAG 2.1 Level A compliance |
| Language | Vietnamese (primary), English (planned) |
| Help System | Contextual help and user manual |

### 6.4 Reliability Requirements

| Requirement | Specification |
|-------------|---------------|
| Availability | 99% uptime (excluding maintenance) |
| Backup | Daily automated backup |
| Error Handling | Graceful error messages, no stack traces to user |
| Transaction Integrity | ACID compliance for payment transactions |
| Data Recovery | RPO < 24 hours, RTO < 4 hours |

---

## 7. TRACEABILITY MATRIX

### Use Cases to Business Rules Mapping

| Use Case | Business Rules Applied |
|----------|------------------------|
| UC-A01 | BR-10, BR-11, BR-93 |
| UC-A02 | BR-04, BR-90, BR-91 |
| UC-M04 | BR-30 through BR-38, BR-93, BR-94 |
| UC-M05 | BR-40 through BR-47, BR-80, BR-81 |
| UC-O01 | BR-20 through BR-23, BR-95 |
| UC-T02 | BR-19, BR-40, BR-41, BR-50 through BR-57, BR-83, BR-85 |
| UC-T04 | BR-60 through BR-65, BR-95 |

---

## 8. GLOSSARY

| Term | Definition |
|------|------------|
| ACID | Atomicity, Consistency, Isolation, Durability |
| Anti-IDOR | Prevention of Insecure Direct Object Reference attacks |
| BCrypt | Password hashing algorithm |
| CMND | Chứng Minh Nhân Dân (Identity Card) |
| HMAC-SHA512 | Hash-based Message Authentication Code using SHA-512 |
| RPO | Recovery Point Objective |
| RTO | Recovery Time Objective |
| Soft Delete | Marking records as deleted without physical deletion |
| VNPay | Vietnamese payment gateway service |
| WCAG | Web Content Accessibility Guidelines |

---

## 9. CHANGE HISTORY

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 0.1 | 2026-06-20 | Development Team | Initial draft |
| 0.5 | 2026-06-23 | QA Team | Added business rules |
| 1.0 | 2026-06-25 | Project Manager | Approved version |

---

## 10. APPROVAL

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Product Owner | [Name] | _________ | 2026-06-25 |
| Development Lead | [Name] | _________ | 2026-06-25 |
| QA Lead | [Name] | _________ | 2026-06-25 |
| Project Manager | [Name] | _________ | 2026-06-25 |

---

**END OF DOCUMENT**

*This document is confidential and proprietary. Unauthorized distribution is prohibited.*
