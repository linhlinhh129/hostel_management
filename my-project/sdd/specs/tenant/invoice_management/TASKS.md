# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Hóa đơn & VNPAY (Tenant)

**Date:** 2026-06-21  
**Total Story Points:** ~56 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8-10 weeks  
**Velocity:** ~14 points/sprint

---

## Epic 1: Setup & Infrastructure (4 points)

### Task 1.0: VNPAY Integration Setup (2 points)
**Priority:** CRITICAL  
**Duration:** 1.5 days  
**Dependencies:** None  
**Assignee:** Backend Lead / DevOps

**Description:**
- Register VNPAY sandbox account
- Obtain VNPAY merchant credentials (Terminal Code, Hash Key)
- Document VNPAY API endpoints
- Setup environment variables (.env) for VNPAY config
- Verify sandbox connectivity
- Create test payment flows
- Prepare VNPAY production account application

**Deliverables:**
- VNPAY sandbox credentials in `.env`
- API endpoint documentation
- Test transaction logs
- Production account approved (or in progress)

**Acceptance Criteria:**
- ✅ Sandbox credentials working
- ✅ Can generate test payment URLs
- ✅ Can verify VNPAY responses
- ✅ Environment config secure (no secrets in code)

---

### Task 1.1: Database Schema Extension (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** None  
**Assignee:** DBA / Backend Lead

**Description:**
- Add VNPAY columns to `payments` table:
  - `vnp_transaction_no` - NVARCHAR(100)
  - `vnp_bank_code` - NVARCHAR(20)
  - `vnp_bank_tran_no` - NVARCHAR(100)
  - `vnp_response_code` - NVARCHAR(10)
  - `vnp_transaction_status` - NVARCHAR(10)
  - `raw_vnpay_response` - NVARCHAR(MAX)
  - `idempotency_key` - NVARCHAR(100) UNIQUE (for IPN deduplication)
- Create indexes for performance:
  - `IX_payments_idempotency_key`
  - `IX_payments_invoice_status`
  - `IX_payments_vnp_transaction_no`
- Add unique constraint to prevent duplicate transactions
- Backup existing data
- Test migration script

**Migration SQL:**
```sql
ALTER TABLE dbo.payments
ADD 
    vnp_transaction_no NVARCHAR(100) NULL,
    vnp_bank_code NVARCHAR(20) NULL,
    vnp_bank_tran_no NVARCHAR(100) NULL,
    vnp_response_code NVARCHAR(10) NULL,
    vnp_transaction_status NVARCHAR(10) NULL,
    raw_vnpay_response NVARCHAR(MAX) NULL,
    idempotency_key NVARCHAR(100) UNIQUE NULL;

CREATE INDEX IX_payments_idempotency_key 
    ON dbo.payments(idempotency_key);
CREATE INDEX IX_payments_invoice_status 
    ON dbo.invoices(room_id, status);
```

**Acceptance Criteria:**
- ✅ New columns added successfully
- ✅ Indexes created for performance
- ✅ Migration reversible if needed
- ✅ No data loss
- ✅ Tested on staging environment

---

## Epic 2: Backend Services - Invoice Core (9 points)

### Task 2.1: Invoice Service - List (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `InvoiceService.getInvoicesList(tenantId, page, pageSize)` method
- Filter invoices by tenant's room
- Support pagination
- Sort by created_date DESC (latest first)
- Return DTO with: invoiceId, code, invoicePeriod, totalAmount, dueDate, status, roomCode
- Handle edge cases (no invoices, pagination boundaries)

**Query Logic:**
```sql
SELECT i.invoice_id, i.code, i.created_at, i.total_amount, 
       i.due_date, i.status, r.code as room_code
FROM invoices i
JOIN rooms r ON i.room_id = r.room_id
WHERE r.tenant_id = @tenantId
ORDER BY i.created_at DESC
OFFSET @offset ROWS FETCH NEXT @pageSize ROWS ONLY
```

**Response DTO:**
```json
{
  "page": 1,
  "pageSize": 20,
  "totalItems": 42,
  "items": [
    {
      "invoiceId": 1,
      "code": "INV-HN0103-202606",
      "period": "06/2026",
      "totalAmount": 3500000,
      "dueDate": "2026-06-30",
      "status": "UNPAID",
      "roomCode": "HN0103"
    }
  ]
}
```

**Acceptance Criteria:**
- ✅ List returns only tenant's invoices
- ✅ Pagination works correctly
- ✅ Sorted newest first
- ✅ Response < 300ms (P95)
- ✅ Handle empty list
- ✅ Unit tests ≥85%

---

### Task 2.2: Invoice Service - Get Detail (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `InvoiceService.getInvoiceDetail(invoiceId, tenantId)` method
- Verify invoice belongs to tenant (authorization)
- Return full invoice with all breakdown details
- Include meter readings if available
- Calculate amounts based on rates + meter readings
- Throw 404 if not found or no access
- Throw 409 if already paid (cannot pay twice)

**Full Detail Response:**
```json
{
  "invoiceId": 1,
  "code": "INV-HN0103-202606",
  "roomCode": "HN0103",
  "period": "06/2026",
  "dueDate": "2026-06-30",
  "status": "UNPAID",
  "breakdown": {
    "roomFee": 2500000,
    "electric": {
      "oldReading": 100,
      "newReading": 150,
      "usage": 50,
      "price": 5000,
      "total": 250000
    },
    "water": {
      "oldReading": 10,
      "newReading": 15,
      "usage": 5,
      "price": 20000,
      "total": 100000
    },
    "internet": 150000,
    "serviceFee": 200000,
    "tax": 200000,
    "otherFee": 100000
  },
  "totalAmount": 3500000,
  "createdDate": "2026-06-01T08:00:00"
}
```

**Acceptance Criteria:**
- ✅ Returns full invoice details
- ✅ Authorizes tenant ownership
- ✅ Breakdown calculations correct
- ✅ 404 for unauthorized access
- ✅ 409 if already paid
- ✅ Response < 300ms (P95)
- ✅ Unit tests ≥85%

---

### Task 2.3: Invoice Service - Validation & Authorization (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `InvoiceValidator.validateInvoiceAccess(invoiceId, tenantId)` method
- Check invoice exists
- Check invoice belongs to tenant (via room_id → tenant_id)
- Check invoice is not already PAID
- Implement fine-grained error messages
- Log unauthorized access attempts

**Validation Rules:**
```
1. Check invoice.invoice_id exists
2. Check room.tenant_id == current_tenantId
3. Check invoice.status != PAID
4. Return: VALID / INVALID_INVOICE / NOT_OWNER / ALREADY_PAID
```

**Acceptance Criteria:**
- ✅ Validates ownership correctly
- ✅ Prevents cross-tenant access
- ✅ Prevents double-payment
- ✅ Clear error messages
- ✅ Security audit logs
- ✅ Unit tests with 10+ scenarios

---

### Task 2.4: Payment Record Creation Service (3 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `PaymentService.createPaymentRecord(paymentData)` method
- Save new payment record with PROCESSING status
- Generate unique payment code
- Use transaction to ensure atomicity
- Lock invoice row during update (SELECT FOR UPDATE)
- Return payment record with payment_id
- Handle concurrent payment attempts (should only allow one)

**Payment Record Structure:**
```
payment_id: AUTO
code: GENERATED (e.g., "PAY-20260621-001")
invoice_id: From request
room_id: From invoice
status: PROCESSING (initially)
payment_date: TODAY
payment_method: VNPAY
payment_amount: From invoice.total_amount
payment_method_detail: Serialized VNPAY response
created_by: tenantId
idempotency_key: From VNPAY transaction (set later)
```

**Acceptance Criteria:**
- ✅ Payment record created in DB
- ✅ Unique payment code generated
- ✅ Transaction safe (atomic)
- ✅ Prevents concurrent duplicate payments
- ✅ Handles lock timeout gracefully
- ✅ Unit tests ≥85%

---

## Epic 3: VNPAY Integration (14 points)

### Task 3.1: VNPAY Adapter - URL Generation (3 points)
**Priority:** CRITICAL  
**Duration:** 2 days  
**Dependencies:** Task 1.0, 2.2  
**Assignee:** Backend Developer (Senior)

**Description:**
- Implement `VNPAYAdapter.generatePaymentUrl(invoiceId, amount, orderInfo, ipAddress)` method
- Build VNPAY request parameters:
  - `vnp_Version` = "2.1.0"
  - `vnp_Command` = "pay"
  - `vnp_TmnCode` = from .env
  - `vnp_Amount` = amount in VND * 100
  - `vnp_CreateDate` = current datetime
  - `vnp_CurrCode` = "VND"
  - `vnp_IpAddr` = client IP
  - `vnp_Locale` = "vn"
  - `vnp_OrderInfo` = order info
  - `vnp_OrderType` = "190000" (invoice)
  - `vnp_ReturnUrl` = configured return URL
  - `vnp_TxnRef` = unique transaction ref (e.g., invoiceId + timestamp)
- Generate secure hash (SHA256)
- Build final URL
- Return payment URL to frontend

**Secure Hash Algorithm:**
```
1. Sort parameters alphabetically
2. Concatenate: param1=value1&param2=value2&...
3. Add secret key at end
4. Compute SHA256 hash
5. Uppercase hex
```

**Example URL:**
```
https://sandbox.vnpayment.vn/paygate?vnp_Version=2.1.0&vnp_Command=pay&...&vnp_SecureHash=XXXXX
```

**Acceptance Criteria:**
- ✅ URL generation correct
- ✅ Hash verification works in VNPAY
- ✅ All parameters included
- ✅ Amount correctly formatted (VND * 100)
- ✅ Tested with VNPAY sandbox
- ✅ No hardcoded secrets
- ✅ Unit tests with multiple scenarios

---

### Task 3.2: VNPAY Adapter - Signature Verification (3 points)
**Priority:** CRITICAL  
**Duration:** 1.5 days  
**Dependencies:** Task 1.0  
**Assignee:** Backend Developer (Senior)

**Description:**
- Implement `VNPAYAdapter.verifySecureHash(vnpayResponse)` method
- Extract all response parameters (including vnp_SecureHash)
- Exclude `vnp_SecureHash` from hash calculation
- Recalculate hash using same algorithm as URL generation
- Compare with VNPAY's hash
- Log both hashes if mismatch (for debugging)
- Return boolean: valid/invalid
- **SECURITY:** Reject if mismatch, log as security incident

**Hash Verification Flow:**
```
1. Extract vnp_SecureHash from response
2. Remove vnp_SecureHash from params
3. Sort remaining params alphabetically
4. Build query string
5. Calculate SHA256 with secret key
6. Compare with received vnp_SecureHash
7. MUST match exactly (case-sensitive)
```

**Acceptance Criteria:**
- ✅ Hash verification works correctly
- ✅ Rejects invalid hashes
- ✅ NEVER logs secret key
- ✅ Security incident logging
- ✅ Unit tests with valid/invalid cases
- ✅ Performance optimized

---

### Task 3.3: Payment Processing - Validation & Update (3 points)
**Priority:** CRITICAL  
**Duration:** 2 days  
**Dependencies:** Task 3.2, 2.4  
**Assignee:** Backend Developer (Senior)

**Description:**
- Implement `PaymentProcessor.processVNPAYResponse(vnpayResponse)` method
- Verify secure hash (call Task 3.2)
- Verify amount matches invoice.total_amount
- Verify invoice exists and status is not already PAID
- Verify transaction not already processed (idempotency)
- Check response code: "00" = success, else = failed
- In transaction:
  - Save full VNPAY response to payments table
  - Update invoice status to PAID or FAILED
  - Save to audit_logs
  - Commit or rollback
- Return processing result

**Validation Checklist:**
```
✓ vnp_ResponseCode == "00"
✓ vnp_Amount == invoice.total_amount * 100
✓ vnp_SecureHash valid
✓ Invoice exists
✓ Invoice status != PAID
✓ Transaction not already processed (idempotency_key)
✓ Database transaction committed
```

**Acceptance Criteria:**
- ✅ All validations implemented
- ✅ Atomic DB transaction
- ✅ Success → invoice.status = PAID
- ✅ Failure → invoice.status = FAILED
- ✅ Full VNPAY response saved
- ✅ Idempotency working
- ✅ Unit tests ≥90% coverage

---

### Task 3.4: IPN Webhook Handler (3 points)
**Priority:** CRITICAL  
**Duration:** 2 days  
**Dependencies:** Task 3.3  
**Assignee:** Backend Developer (Senior)

**Description:**
- Implement `VNPAYWebhookHandler.handleIPN(ipnRequest)` method
- **IMPORTANT:** IPN can be sent multiple times → use idempotency
- Verify secure hash from IPN request
- Verify amount
- Check if already processed (idempotency_key)
- Process payment (call PaymentProcessor from Task 3.3)
- Always return HTTP 200 (even if already processed)
- Log all IPN events (success/duplicate/error)
- Implement retry logic for transient failures

**IPN Processing Flow:**
```
1. Extract vnp_TransactionNo (use as idempotency_key)
2. Calculate hash
3. Verify hash
4. Check if payments.idempotency_key exists
   → YES: Return 200 (already processed)
   → NO: Continue
5. Process payment (validations + DB update)
6. Save idempotency_key
7. Return 200
```

**Acceptance Criteria:**
- ✅ Idempotency working (no duplicate processing)
- ✅ Hash verification on IPN
- ✅ Always return 200 on success
- ✅ Comprehensive logging
- ✅ Handles transient failures
- ✅ Tested with multiple IPN submissions
- ✅ Unit tests ≥85%

---

### Task 3.5: VNPAY Adapter - Response Parsing (2 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 1.0  
**Assignee:** Backend Developer

**Description:**
- Implement response DTOs for VNPAY data
- Parse return URL parameters
- Parse IPN webhook payload
- Extract relevant fields:
  - vnp_ResponseCode
  - vnp_Amount
  - vnp_BankCode
  - vnp_BankTranNo
  - vnp_CardType
  - vnp_TransactionNo
  - vnp_TransactionStatus
  - vnp_OrderInfo
  - etc.
- Validate all required fields present
- Handle missing/malformed data gracefully

**Acceptance Criteria:**
- ✅ All VNPAY fields extracted
- ✅ Robust parsing (no crashes on bad data)
- ✅ DTOs properly typed
- ✅ Serialization/deserialization working
- ✅ Unit tests ≥80%

---

## Epic 4: REST APIs (10 points)

### Task 4.1: Get Invoices List Endpoint (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 2.1  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/tenant/invoices`
- Query params: `page` (1-based, default 1), `pageSize` (1-100, default 20)
- Call InvoiceService.getInvoicesList()
- Validate pagination parameters
- Return PaginatedResponse with invoices

**Endpoint:**
```
GET /api/v1/tenant/invoices?page=1&pageSize=20
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "page": 1,
  "pageSize": 20,
  "totalItems": 42,
  "items": [...]
}
```

**Error Responses:**
- 400: Invalid pagination
- 401: Unauthorized
- 403: Forbidden (non-tenant)
- 500: Server error

**Acceptance Criteria:**
- ✅ Returns paginated invoices
- ✅ Validates parameters
- ✅ Proper error codes
- ✅ Performance < 300ms
- ✅ Swagger documented

---

### Task 4.2: Get Invoice Detail Endpoint (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 2.2, 2.3  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/tenant/invoices/{invoiceId}`
- Validate invoiceId is positive integer
- Call InvoiceValidator.validateInvoiceAccess()
- Call InvoiceService.getInvoiceDetail()
- Return full invoice DTO

**Endpoint:**
```
GET /api/v1/tenant/invoices/1
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "invoiceId": 1,
  "code": "INV-HN0103-202606",
  "breakdown": {...},
  "totalAmount": 3500000,
  "status": "UNPAID"
}
```

**Error Responses:**
- 400: Invalid invoiceId
- 401: Unauthorized
- 403: Forbidden (not owner)
- 404: Not found or already paid
- 409: Conflict (already paid)

**Acceptance Criteria:**
- ✅ Returns full invoice
- ✅ Authorization enforced
- ✅ Proper error codes
- ✅ Performance < 300ms
- ✅ Swagger documented

---

### Task 4.3: Create VNPAY Payment URL Endpoint (2 points)
**Priority:** CRITICAL  
**Duration:** 1.5 days  
**Dependencies:** Task 3.1, 2.2, 2.3, 2.4  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `POST /api/v1/tenant/invoices/{invoiceId}/payment/vnpay`
- Validate invoice access (authorization)
- Extract client IP address
- Call VNPAYAdapter.generatePaymentUrl()
- Create payment record (PROCESSING status)
- Return redirect URL

**Request:**
```
POST /api/v1/tenant/invoices/1/payment/vnpay
Authorization: Bearer <jwt_token>
Content-Type: application/json
Body: {} (empty or optional orderInfo)
```

**Response (200 OK):**
```json
{
  "paymentUrl": "https://sandbox.vnpayment.vn/paygate?...",
  "invoiceId": 1,
  "amount": 3500000,
  "transactionRef": "1-1624276800"
}
```

**Error Responses:**
- 400: Invalid invoiceId
- 401: Unauthorized
- 403: Not owner
- 404: Invoice not found
- 409: Already paid / Already processing

**Acceptance Criteria:**
- ✅ URL generated correctly
- ✅ Hash verified in VNPAY sandbox
- ✅ Payment record created
- ✅ Prevents double-payment
- ✅ Returns valid VNPAY URL
- ✅ Performance < 500ms
- ✅ Tested with sandbox

---

### Task 4.4: VNPAY Return URL Handler (2 points)
**Priority:** CRITICAL  
**Duration:** 1.5 days  
**Dependencies:** Task 3.3  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/payment/vnpay/return`
- Handle VNPAY redirect after user completes/cancels payment
- **IMPORTANT:** This is synchronous, but may fail → use IPN as authority
- Extract query parameters from VNPAY
- Process VNPAY response
- Redirect to frontend with status (success/fail)
- Do NOT rely solely on return URL for DB updates (wait for IPN)

**Flow:**
```
User completes payment at VNPAY
    ↓
VNPAY redirects to: /api/v1/payment/vnpay/return?vnp_ResponseCode=00&...
    ↓
Backend processes response (but doesn't update DB)
    ↓
Redirect to frontend: /payment-result?status=processing&invoiceId=1
    ↓
VNPAY sends IPN → DB updated
    ↓
Frontend polls or receives websocket notification
```

**Response (Redirect 302):**
```
Location: /payment-result?status=processing&invoiceId=1
Or on error:
Location: /payment-result?status=failed&invoiceId=1&reason=...
```

**Acceptance Criteria:**
- ✅ VNPAY response parsed
- ✅ Proper redirect URLs
- ✅ Hash verified
- ✅ Doesn't update DB (wait for IPN)
- ✅ Handles invalid/malformed requests
- ✅ Tested with sandbox

---

### Task 4.5: VNPAY IPN Webhook Endpoint (1 point)
**Priority:** CRITICAL  
**Duration:** 1 day  
**Dependencies:** Task 3.4  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `POST /api/v1/payment/vnpay/ipn`
- **CRITICAL:** Must not require authentication (VNPAY calls from outside)
- Parse POST body (VNPAY sends form-encoded data)
- Call VNPAYWebhookHandler.handleIPN()
- Always return HTTP 200 (even on error)
- Log all requests (for audit)
- Implement rate limiting (optional)

**Request (from VNPAY):**
```
POST /api/v1/payment/vnpay/ipn
Content-Type: application/x-www-form-urlencoded

vnp_Amount=350000000&vnp_BankCode=NCB&vnp_BankTranNo=VCB01234567&...
```

**Response (200 OK):**
```json
{
  "status": 0,
  "message": "Success"
}
```

**Acceptance Criteria:**
- ✅ Accepts POST without auth
- ✅ Always returns 200
- ✅ Idempotency working
- ✅ Comprehensive logging
- ✅ Tested with VNPAY sandbox IPN simulator

---

### Task 4.6: Get Payment History Endpoint (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 2.1  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/tenant/payments/history`
- Query params: `page`, `pageSize`
- Return payments for tenant
- Filter: status = SUCCESS only
- Sort by payment_date DESC
- Include invoice_code, payment_code, amount, method, date

**Endpoint:**
```
GET /api/v1/tenant/payments/history?page=1&pageSize=20
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "page": 1,
  "pageSize": 20,
  "totalItems": 10,
  "items": [
    {
      "paymentId": 1,
      "paymentCode": "PAY-20260621-001",
      "invoiceCode": "INV-HN0103-202606",
      "amount": 3500000,
      "paymentDate": "2026-06-21T10:30:00",
      "paymentMethod": "VNPAY",
      "status": "SUCCESS",
      "vnpTransactionNo": "12345678"
    }
  ]
}
```

**Acceptance Criteria:**
- ✅ Returns only successful payments
- ✅ Scoped to tenant
- ✅ Proper pagination
- ✅ Performance < 300ms
- ✅ Swagger documented

---

### Task 4.7: Error Response & Exception Handling (2 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 4.1-4.6  
**Assignee:** Backend Developer

**Description:**
- Create consistent error response format
- Implement exceptions:
  - InvoiceNotFoundException (404)
  - InvoiceAlreadyPaidException (409)
  - UnauthorizedException (403)
  - BadRequestException (400)
  - VNPAYSecurityException (500 + alert)
  - PaymentProcessingException (500 + retry info)
- Map exceptions to HTTP status codes
- Add logging & alerting for security errors
- Return helpful error messages (Vietnamese)

**Standard Error Format:**
```json
{
  "statusCode": 404,
  "message": "Hóa đơn không tồn tại hoặc bạn không có quyền truy cập.",
  "errorCode": "INVOICE_NOT_FOUND",
  "timestamp": "2026-06-21T10:30:00Z",
  "details": "..."
}
```

**Acceptance Criteria:**
- ✅ All exceptions mapped
- ✅ Proper HTTP codes
- ✅ Helpful Vietnamese messages
- ✅ No sensitive data in errors
- ✅ Security incidents logged
- ✅ All scenarios tested

---

## Epic 5: Frontend - Invoice Views (15 points)

### Task 5.1: Invoice List Page (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 4.1  
**Assignee:** Frontend Developer

**Description:**
- Create InvoiceListPage component
- Display table/card layout with columns:
  - Period (e.g., "06/2026")
  - Room Code
  - Amount (formatted currency)
  - Due Date (formatted: 21/06/2026)
  - Status (colored badge: UNPAID=red, PAID=green, OVERDUE=orange)
- Implement pagination (page numbers, prev/next)
- Click row → navigate to detail
- Call API: `GET /api/v1/tenant/invoices?page=1&pageSize=20`
- Loading indicator while fetching
- Empty state: "Chưa có hóa đơn nào."

**Table Layout:**
```
┌─────────┬──────────┬──────────┬────────────┬──────────┐
│ Period  │ Room     │ Amount   │ Due Date   │ Status   │
├─────────┼──────────┼──────────┼────────────┼──────────┤
│ 06/2026 │ HN0103   │ 3.5M VND │ 21/06/2026 │ UNPAID   │
│ 05/2026 │ HN0103   │ 3.2M VND │ 31/05/2026 │ PAID     │
└─────────┴──────────┴──────────┴────────────┴──────────┘
[< Page 1/5 >]
```

**Acceptance Criteria:**
- ✅ Data loads from API
- ✅ Pagination working
- ✅ Responsive design
- ✅ Click navigates to detail
- ✅ Loading/empty states shown
- ✅ Formatted dates & amounts

---

### Task 5.2: Invoice Detail Page (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 4.2  
**Assignee:** Frontend Developer

**Description:**
- Create InvoiceDetailPage component
- Display full invoice breakdown:
  - Header: Invoice Code, Period, Due Date, Status
  - Breakdown section:
    - Room Fee
    - Electric (old reading, new reading, usage, rate, total)
    - Water (old reading, new reading, usage, rate, total)
    - Internet
    - Service Fee
    - Tax
    - Other Fee
  - Total Amount (large, bold)
  - Payment Button (if status = UNPAID)
- Call API: `GET /api/v1/tenant/invoices/{invoiceId}`
- Show loading indicator
- Back button to list
- Display error state if invoice not found/already paid

**Detail Layout:**
```
┌──────────────────────────────────┐
│ < Quay lại                       │
├──────────────────────────────────┤
│ INV-HN0103-202606 | 06/2026     │
│ Hạn thanh toán: 21/06/2026      │
│ Trạng thái: Chưa thanh toán      │
├──────────────────────────────────┤
│ Chi tiết hóa đơn:                │
│  Tiền phòng ........ 2.500.000   │
│  Điện (50 số) ....... 250.000    │
│  Nước (5 số) ........ 100.000    │
│  Internet ........... 150.000    │
│  Phí dịch vụ ........ 200.000    │
│  Thuế ............... 200.000    │
├──────────────────────────────────┤
│ Tổng cộng: 3.500.000 VND         │
├──────────────────────────────────┤
│ [Thanh toán VNPAY]               │
└──────────────────────────────────┘
```

**Acceptance Criteria:**
- ✅ Loads invoice from API
- ✅ Full breakdown displayed
- ✅ Numbers formatted (currency, decimals)
- ✅ Payment button visible if UNPAID
- ✅ Back button works
- ✅ Error handling (404, 403, already paid)
- ✅ Responsive layout

---

### Task 5.3: VNPAY Payment Flow (5 points)
**Priority:** CRITICAL  
**Duration:** 2-3 days  
**Dependencies:** Task 4.3, 4.4  
**Assignee:** Frontend Developer (Senior)

**Description:**
- Create payment flow screens:
  1. **Confirmation Dialog:** Show amount, confirm before payment
  2. **Processing Screen:** Loader, "Đang xử lý thanh toán..."
  3. **Success Screen:** "Thanh toán thành công!", show receipt
  4. **Failed Screen:** Error message, Retry button
- Implement payment button click handler:
  - Show confirmation dialog
  - Call POST `/api/v1/tenant/invoices/{invoiceId}/payment/vnpay`
  - Receive payment URL from backend
  - Redirect to VNPAY (window.location.href)
- Handle return from VNPAY:
  - Parse URL parameters (status, invoiceId, reason)
  - Show appropriate screen
  - Poll backend for final status (or wait for IPN via websocket)
  - Allow "Back to Invoices" after result

**Payment Flow Diagram:**
```
Click Thanh toán
    ↓
[Confirmation Dialog]
    ↓ Confirm
    ↓
POST /payment/vnpay → Get URL
    ↓
Redirect to VNPAY
    ↓
User enters payment details
    ↓
VNPAY redirects to /payment-result
    ↓
Show Processing Screen (polling)
    ↓
Backend receives IPN → Status updates
    ↓
Show Success/Failed Screen
```

**Success Screen:**
```
┌──────────────────────────────────┐
│ ✓ Thanh toán thành công          │
│                                  │
│ Mã thanh toán: PAY-20260621-001  │
│ Số tiền: 3.500.000 VND           │
│ Thời gian: 21/06/2026 10:30:00   │
│                                  │
│ [Quay lại Danh sách]              │
└──────────────────────────────────┘
```

**Acceptance Criteria:**
- ✅ Payment button redirects to VNPAY
- ✅ Return URL handled correctly
- ✅ Success/fail screens displayed
- ✅ Confirmation dialog shown
- ✅ Retry button on fail screen
- ✅ Responsive design
- ✅ HTTPS enforced

---

### Task 5.4: Payment History Page (2 points)
**Priority:** MEDIUM  
**Duration:** 1.5 days  
**Dependencies:** Task 4.6  
**Assignee:** Frontend Developer

**Description:**
- Create PaymentHistoryPage component
- Display table with:
  - Payment Code
  - Invoice Code / Period
  - Amount
  - Payment Date
  - Payment Method (VNPAY)
  - Status (SUCCESS)
  - VNPAY Transaction No (optional)
- Call API: `GET /api/v1/tenant/payments/history?page=1&pageSize=20`
- Implement pagination
- Show loading & empty states
- Tab or link to access from Invoice List page

**History Table:**
```
┌──────────────┬──────────────────┬──────────┬────────────────┐
│ Payment Code │ Invoice/Period   │ Amount   │ Date           │
├──────────────┼──────────────────┼──────────┼────────────────┤
│ PAY-2606-001 │ INV-HN0103/06    │ 3.5M VND │ 21/06/2026 10:30
│ PAY-2605-001 │ INV-HN0103/05    │ 3.2M VND │ 31/05/2026 14:15
└──────────────┴──────────────────┴──────────┴────────────────┘
```

**Acceptance Criteria:**
- ✅ Loads payment history from API
- ✅ Pagination working
- ✅ Formatted dates & amounts
- ✅ Empty state handled
- ✅ Responsive design

---

### Task 5.5: UI Polish & Responsive Design (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 5.1-5.4  
**Assignee:** Frontend Developer

**Description:**
- Review styling consistency
- Ensure responsive:
  - Mobile (320-480px): Stack layouts, scrollable tables
  - Tablet (481-1024px): 2-column layouts
  - Desktop (1025px+): Full layouts
- Cross-browser testing (Chrome, Firefox, Safari)
- Test on actual devices
- Optimize images & loading
- Accessibility review (WCAG basics)

**Checklist:**
- ✅ Consistent color scheme
- ✅ Readable fonts
- ✅ Touch targets ≥44px
- ✅ No horizontal scroll on mobile
- ✅ Tables scrollable on small screens
- ✅ Dark mode compatible (if applicable)
- ✅ Loading indicators present
- ✅ Empty states clear

---

## Epic 6: Testing & QA (8 points)

### Task 6.1: Backend Unit Tests (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1-2.4, 3.1-3.5  
**Assignee:** QA / Backend

**Description:**
- Unit tests for all services:
  - InvoiceService (list, detail)
  - InvoiceValidator (authorization)
  - PaymentService (creation)
  - VNPAYAdapter (URL generation, hash verification)
  - VNPAYWebhookHandler (IPN processing)
- Test coverage target: ≥85%
- Test scenarios:
  - Normal flow
  - Invalid input
  - Authorization errors
  - Edge cases (boundary values)
  - Security scenarios (bad hash, amount mismatch)

**Test Scenarios:**
```
✓ Generate payment URL correctly
✓ Hash verification (valid & invalid)
✓ Amount formatting (VND * 100)
✓ Idempotency (duplicate IPN)
✓ Authorization (wrong tenant)
✓ Invoice already paid
✓ Invalid invoice ID
✓ Decimal precision (VND)
```

**Acceptance Criteria:**
- ✅ Unit tests coverage ≥85%
- ✅ All critical paths tested
- ✅ Mocks used for VNPAY
- ✅ Tests pass locally
- ✅ CI/CD pipeline integration

---

### Task 6.2: Integration Tests - Payment Flow (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 4.1-4.6  
**Assignee:** QA / Backend

**Description:**
- Integration tests for full payment flow:
  1. Get invoice list
  2. Get invoice detail
  3. Create VNPAY payment (get URL)
  4. Simulate VNPAY return
  5. Simulate VNPAY IPN
  6. Verify DB updated (invoice PAID, payment record created)
  7. Get payment history
- Test with test data (sandbox invoices)
- Test error scenarios:
  - Invalid amount
  - Bad hash
  - Duplicate IPN
  - Concurrent payments
  - DB transaction failure

**Test Flow:**
```
1. Create test invoice
2. POST /invoices/{id}/payment/vnpay
   → Get payment URL
3. Simulate user at VNPAY
4. GET /payment/vnpay/return?vnp_ResponseCode=00&...
   → Verify no DB change yet
5. POST /payment/vnpay/ipn (full VNPAY params)
   → Verify DB updated
6. Check payments table has record
7. Check invoices.status = PAID
```

**Acceptance Criteria:**
- ✅ Full flow tested end-to-end
- ✅ Database state verified
- ✅ Error scenarios tested
- ✅ Idempotency verified
- ✅ Concurrent requests tested
- ✅ Test data cleanup

---

### Task 6.3: VNPAY Sandbox Testing (1.5 points)
**Priority:** CRITICAL  
**Duration:** 2 days  
**Dependencies:** Task 3.1-3.5, 4.3-4.5  
**Assignee:** QA / Backend

**Description:**
- Test integration with VNPAY sandbox:
  1. Register test merchant account in VNPAY sandbox
  2. Generate payment URLs using sandbox credentials
  3. Test payment flow in VNPAY sandbox UI
  4. Capture VNPAY response parameters
  5. Verify secure hash in responses
  6. Test IPN webhook delivery
  7. Verify all fields in IPN
  8. Test multiple IPN deliveries (idempotency)
  9. Test payment failure scenarios
  10. Document VNPAY sandbox settings for team

**Test Checklist:**
- ✅ Login to VNPAY sandbox portal
- ✅ Generate valid payment URL
- ✅ Complete payment in sandbox
- ✅ Receive return URL redirect
- ✅ Receive IPN webhook
- ✅ Verify hash on all responses
- ✅ Test duplicate IPN handling
- ✅ Document for team

**Acceptance Criteria:**
- ✅ Successful payment simulation
- ✅ IPN delivery confirmed
- ✅ Hash verification working
- ✅ All fields captured
- ✅ Team documented
- ✅ Ready for UAT

---

### Task 6.4: Frontend E2E Tests (1.5 points)
**Priority:** MEDIUM  
**Duration:** 1.5 days  
**Dependencies:** Task 5.1-5.5, 4.1-4.6  
**Assignee:** QA / Frontend

**Description:**
- E2E tests (Selenium/Cypress/Playwright):
  1. Login as tenant
  2. Navigate to invoices list
  3. Click invoice → detail view
  4. Click payment button
  5. Confirm payment dialog
  6. Verify redirect to VNPAY
  7. Simulate return from VNPAY
  8. Verify success/failed screen shown
  9. Click "Back to Invoices"
  10. Verify invoice status updated (PAID)
  11. Check payment history

**Test Cases:**
```
✓ List invoices
✓ View invoice detail
✓ Payment success flow
✓ Payment failure flow
✓ Retry failed payment
✓ View payment history
✓ Responsive on mobile
✓ Error states (404, 403)
```

**Acceptance Criteria:**
- ✅ All flows tested
- ✅ Success path working
- ✅ Error paths handled
- ✅ Mobile tested
- ✅ Tests automated & repeatable

---

### Task 6.5: Security Testing (1 point)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 3.1-3.5, 4.3-4.5  
**Assignee:** Security / QA

**Description:**
- Security testing checklist:
  1. Secure hash verification:
     - Valid hash → accepted
     - Invalid hash → rejected
     - Missing hash → rejected
  2. Authorization:
     - Tenant A cannot see Tenant B's invoices
     - Tenant cannot create fake payment records
  3. Amount validation:
     - Tamper with amount in VNPAY response → rejected
     - Underpayment → rejected
  4. Idempotency:
     - Send duplicate IPN → no duplicate DB records
  5. Secret key security:
     - Not logged anywhere
     - Not in frontend code
     - Only in .env
  6. HTTPS enforcement:
     - /api/v1/payment endpoints HTTPS only

**Security Checklist:**
- ✅ Hash verification robust
- ✅ No cross-tenant access
- ✅ Amount tamper-proof
- ✅ Idempotency working
- ✅ Secrets protected
- ✅ HTTPS enforced
- ✅ Security incidents logged

---

### Task 6.6: UAT & Sign-off (2 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 6.1-6.5  
**Assignee:** QA / Product Owner

**Description:**
- User Acceptance Testing:
  1. Test with real VNPAY sandbox account
  2. Verify all business requirements met
  3. Test with business users (if possible)
  4. Verify error messages in Vietnamese
  5. Verify data accuracy (amounts, dates)
  6. Verify no financial discrepancies
  7. Performance acceptable (< 300-500ms)
  8. Collect feedback & document issues
  9. Get product owner sign-off
  10. Create UAT report

**UAT Scenarios:**
```
✓ Tenant views invoices
✓ Tenant pays invoice via VNPAY
✓ Invoice status updates PAID
✓ Payment history recorded
✓ Cannot pay twice
✓ Failed payment can retry
✓ Error messages clear
✓ Performance acceptable
✓ Mobile experience good
```

**Acceptance Criteria:**
- ✅ All UAT tests passed
- ✅ Business requirements met
- ✅ No critical issues
- ✅ Product owner signed off
- ✅ UAT report documented
- ✅ Ready for production

---

## Summary by Sprint

| Sprint | Points | Duration | Focus |
|--------|--------|----------|-------|
| **Sprint 1** | 14 | Weeks 1-2 | Setup, Infrastructure, Core Services |
| **Sprint 2** | 14 | Weeks 2-3 | VNPAY Integration, Payment Processing |
| **Sprint 3** | 14 | Weeks 4-5 | APIs, Frontend, Initial Testing |
| **Sprint 4** | 14 | Weeks 6-8 | Frontend Polish, E2E Testing, UAT, Deploy |

---

## Critical Dependencies Graph

```
1.0 (VNPAY Setup)
 ↓
1.1 (DB Schema)
 ├→ 2.1 (List Service) ──→ 4.1 (List API) ──→ 5.1 (List UI)
 ├→ 2.2 (Detail Service) → 4.2 (Detail API) → 5.2 (Detail UI)
 ├→ 3.1 (URL Generation) ──────────→ 4.3 (Payment API)
 ├→ 3.2 (Hash Verification) ──────→ 3.3, 3.4
 ├→ 3.3 (Payment Processing) ────→ 4.4 (Return Handler)
 └→ 3.4 (IPN Handler) ──────────→ 4.5 (IPN Endpoint)
                           ↓
                        5.3 (Payment Flow UI)
                           ↓
                        6.2-6.6 (Testing)
```

---

## Definition of Done for Each Task

✅ **Code Quality:**
- Peer-reviewed
- Follows coding standards
- No code smells
- Commented (complex logic)

✅ **Testing:**
- Unit tests ≥85% coverage
- Integration tests pass
- No critical issues
- Security validated

✅ **Documentation:**
- Code comments where needed
- API docs (Swagger) updated
- Technical decisions documented
- Environment variables documented

✅ **Security (Payment Tasks):**
- Secure hash verified
- No secrets in code
- Authorization tested
- Security incidents logged

✅ **Product Review:**
- Acceptance criteria met
- Product owner approved
- No blocking issues

---

## Production Deployment Checklist

Before going live:
- [ ] VNPAY production account approved
- [ ] Production credentials in .env
- [ ] HTTPS enforced on all endpoints
- [ ] Database backups working
- [ ] Monitoring/alerting configured
- [ ] Error logging configured
- [ ] Performance baseline established
- [ ] Load testing passed
- [ ] Security audit completed
- [ ] Incident response plan ready
- [ ] Rollback plan documented
- [ ] Team trained on monitoring
- [ ] Customer support briefed
