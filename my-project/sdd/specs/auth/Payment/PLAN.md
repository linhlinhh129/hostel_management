# PLAN: Kế hoạch Thực thi Thanh toán hóa đơn MoMo

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 7-8 weeks

---

## 1. Tổng quan Giải pháp

Feature thanh toán MoMo cho phép cư dân tạo giao dịch thanh toán điện nước, định tuyến sang cổng MoMo, và cập nhật trạng thái hóa đơn khi nhận IPN xác thực từ MoMo.

**Kiến trúc:**
- Backend API: `GET /api/v1/invoices/{invoiceId}`, `POST /api/v1/payments`, `POST /api/v1/payments/ipn`
- Frontend: invoice details, payment button, result page
- Security: verify MoMo signature, reject invalid IPN, prevent duplicate payments
- Persistence: payment transactions, invoice status, audit logs

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** nắm rõ luồng MoMo, payload IPN, và map trạng thái.

**Công việc:**
- Phân tích MoMo API contract
- Thiết kế transaction model và hóa đơn status mapping
- Xác định state machine cho invoice: `PENDING`, `PROCESSING`, `PAID`, `FAILED`
- Thiết kế bảng/luồng lưu lịch sử giao dịch

---

### Giai đoạn 2: Backend Payment Flow (Tuần 3-4)

**Mục tiêu:** triển khai tạo giao dịch và xử lý IPN.

**Công việc:**
- Implement invoice detail API
- Implement payment creation service
- Integrate MoMo request signing and response parsing
- Implement IPN endpoint with signature validation
- Ensure duplicate IPN handling idempotent

---

### Giai đoạn 3: Frontend Payment Experience (Tuần 5-6)

**Mục tiêu:** cung cấp trải nghiệm thanh toán mượt mà.

**Công việc:**
- Display invoice summary and payment button
- Redirect resident to MoMo payment URL
- Show payment result page based on invoice state

---

### Giai đoạn 4: Testing & Deployment (Tuần 7-8)

**Mục tiêu:** xác minh tính chính xác và bảo mật.

**Công việc:**
- Unit tests for payment service and IPN validation
- Integration tests with mocked MoMo API
- End-to-end validation of payment flow
- Security review for IPN and fraud prevention

---

## 3. Key Technical Challenges

### IPN Signature Validation
- Validate MoMo callback signatures strictly
- Reject IPN if signature invalid or payload tampered

### Duplicate Payment Protection
- Prevent duplicate payment creation for PAID invoices
- Handle repeated IPN without double-marking

### Transaction Audit
- Persist MoMo transaction reference and timestamps
- Log errors for failed payment events

---

## 4. Success Criteria

- ✓ Resident can create MoMo payment request for pending invoice
- ✓ System redirects to valid MoMo payment URL
- ✓ IPN callback verifies signature and updates invoice status to PAID
- ✓ Failed IPN preserves invoice state and logs failure
- ✓ Duplicate payment attempts return 409 conflict
- ✓ Test coverage >= 85%

---

## 5. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-4:** Backend payment flow
- **Week 5-6:** Frontend experience
- **Week 7-8:** Testing & deployment

**Total:** 8 weeks
