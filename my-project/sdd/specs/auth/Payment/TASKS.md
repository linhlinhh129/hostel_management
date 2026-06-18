# TASKS: Phân chia Chi tiết Đầu Việc - Thanh toán MoMo

**Total Story Points:** ~46 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8 weeks  
**Velocity:** ~11 points/sprint

---

## Epic 1: Payment Flow Backend (16 points)

### Task 1.1: Invoice Detail API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define GET /api/v1/invoices/{invoiceId}
- Specify fields necessary for payment

---

### Task 1.2: Payment Creation Service (5 points)
**Duration:** 2 days  
**Description:**
- Create payment transaction for pending invoice
- Call MoMo API and parse payment URL
- Store payment request and status

---

### Task 1.3: IPN Endpoint Implementation (5 points)
**Duration:** 2 days  
**Description:**
- Implement payment IPN callback endpoint
- Validate signature and request parameters
- Update invoice status idempotently

---

### Task 1.4: Duplicate Payment Protection (4 points)
**Duration:** 1 day  
**Description:**
- Prevent new payment for PAID invoice
- Handle repeated IPN safely
- Return 409 conflict for duplicate payment attempts

---

## Epic 2: API & Security (12 points)

### Task 2.1: Payment Request Endpoint (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint for residents to start payment
- Authorize resident and invoice ownership

---

### Task 2.2: IPN Signature Validation (4 points)
**Duration:** 2 days  
**Description:**
- Validate MoMo signature
- Reject invalid callbacks securely

---

### Task 2.3: Error Handling & Response Standardization (3 points)
**Duration:** 1 day  
**Description:**
- Standardize errors for invalid invoice, duplicate payment, invalid IPN
- Map to HTTP status codes

---

### Task 2.4: Security Review (2 points)
**Duration:** 1 day  
**Description:**
- Review payment flow for replay and tampering risks
- Ensure no client-trusted state influences invoice status

---

## Epic 3: Frontend Payment Flow (10 points)

### Task 3.1: Invoice Payment UI (4 points)
**Duration:** 2 days  
**Description:**
- Display invoice summary and outstanding amount
- Show payment status and payment button

---

### Task 3.2: Redirect to MoMo (3 points)
**Duration:** 1 day  
**Description:**
- Create payment request and redirect user to MoMo URL
- Show loading state while preparing request

---

### Task 3.3: Payment Result Page (3 points)
**Duration:** 1 day  
**Description:**
- Display success/failure based on invoice status after return
- Show helpful next step message

---

## Epic 4: Testing (8 points)

### Task 4.1: Unit Tests (3 points)
**Duration:** 2 days  
**Description:**
- Test payment creation service and invoice guard
- Test IPN signature validation

---

### Task 4.2: Integration Tests (3 points)
**Duration:** 2 days  
**Description:**
- Test payment flow with mocked MoMo API
- Test repeated IPN idempotency

---

### Task 4.3: End-to-End Validation (2 points)
**Duration:** 1 day  
**Description:**
- Validate resident payment end-to-end
- Verify status updates and error handling

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 16 | Backend payment flow |
| Sprint 2 | 12 | API security and validation |
| Sprint 3 | 10 | Frontend payment experience |
| Sprint 4 | 8 | Testing & polish |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (payment endpoint depends on service)
- Task 1.3 → Task 2.2 (IPN endpoint depends on signature validation)
- Task 3.2 → Task 1.2 (frontend redirect depends on successful transaction creation)
