# PLAN: Kế hoạch Thực thi Quản lý Hóa đơn & Thanh toán VNPAY (Tenant)

**Status:** Planning  
**Date:** 2026-06-21  
**Priority:** High  
**Risk Level:** High (Financial Transaction)  
**Estimated Duration:** 8-10 weeks  
**Target Release:** Sprint 3-4

---

## 1. Executive Summary

### Problem Statement
Hiện tại, người thuê không có nơi tập trung để xem hóa đơn và thanh toán các khoản phí hàng tháng. Các khoản phí như tiền phòng, điện, nước, Internet, dịch vụ... được thanh toán thủ công ngoài hệ thống, dẫn đến:
- Khó khăn trong xác nhận giao dịch
- Khó đối soát thanh toán
- Sai sót có thể xảy ra
- Tranh chấp về việc đã thanh toán hay chưa

### Solution Overview
Triển khai tính năng **Invoice Management & VNPAY Payment** cho phép Tenant:
- Xem danh sách hóa đơn với trạng thái thanh toán
- Xem chi tiết đầy đủ breakdown các khoản phí
- Thanh toán trực tuyến thông qua **VNPAY** (cổng thanh toán)
- Xem lịch sử các giao dịch thanh toán thành công
- Tự động cập nhật trạng thái hóa đơn sau khi thanh toán

### Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                       Frontend UI                            │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │Invoice List │→ │Invoice Detail│→ │VNPAY Payment     │   │
│  └─────────────┘  └──────────────┘  └──────────────────┘   │
│                        ↓                       ↓             │
│                   [Payment Redirect] → [VNPAY Gateway]      │
│                        ↓                       ↓             │
│              [Return URL Handler]  [IPN Callback Handler]   │
│                        ↓                       ↓             │
├────────────────────────────────────────────────────────────┤
│                      Backend API                             │
│  ┌──────────────────┐  ┌────────────────┐  ┌────────────┐  │
│  │Invoice Services  │  │Payment Services│  │VNPAY Adapter   │
│  └──────────────────┘  └────────────────┘  └────────────┘  │
│         ↓                       ↓                   ↓        │
├────────────────────────────────────────────────────────────┤
│                      Database                                │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │invoices      │  │payments       │  │VNPAY responses  │   │
│  └──────────────┘  └──────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Key Features & Requirements

### Functional Requirements

#### Invoice Management (Read-only)
- **FR01-03:** Danh sách hóa đơn với sắp xếp theo kỳ tính giảm dần
- **FR04:** Chi tiết đầy đủ: tiền phòng, điện, nước, Internet, phí dịch vụ, thuế, phụ phí, tổng cộng
- **FR08:** Lưu đầy đủ thông tin giao dịch vào bảng `payments`

#### VNPAY Payment Integration (Critical)
- **FR05-06:** Tạo URL thanh toán VNPAY với đầy đủ thông tin
- **FR07:** Xác thực callback từ VNPAY (Secure Hash)
- **FR08-09:** Cập nhật trạng thái invoice PAID/FAILED
- **FR10:** Cho phép thanh toán lại nếu thất bại
- **FR11-12:** Lịch sử thanh toán

#### Validation & Security
- Xác thực người dùng (JWT)
- Kiểm tra ownership của invoice
- **Verify Secure Hash** từ VNPAY
- Verify Amount
- Áp dụng **Idempotency** để tránh xử lý trùng lặp

### Non-Functional Requirements

| Yêu cầu | Chỉ số |
|---------|--------|
| **Performance** | List < 300ms, Detail < 300ms, Payment URL < 500ms (P95) |
| **Security** | HTTPS, Secret Key in .env, Secure Hash verification, No logging secrets |
| **Transaction** | Atomic transaction: save payment + update invoice |
| **Idempotency** | IPN có thể gửi nhiều lần → hệ thống bỏ qua lần thứ 2+ |
| **Availability** | Đảm bảo không phát sinh thanh toán trùng cho cùng hóa đơn |

### Invoice State Machine
```
UNPAID → [Click Thanh toán] → PROCESSING → [IPN Success] → PAID
           ↑                                                    
           └─ [IPN Failed] → FAILED → [Thanh toán lại] → PROCESSING
```

---

## 3. Implementation Phases

### Phase 1: Design & Infrastructure Setup (Tuần 1)
**Duration:** 4-5 ngày  
**Deliverables:**
- ✓ API contract finalization
- ✓ VNPAY integration design
- ✓ Database schema updates (payments table extensions)
- ✓ Security & idempotency strategy
- ✓ Test data & test accounts (VNPAY sandbox)

**Key Tasks:**
1. Define DTO structures (list, detail, payment)
2. Design VNPAY URL generation algorithm
3. Plan secure hash verification logic
4. Design idempotency key strategy
5. Setup VNPAY sandbox account & credentials
6. Define database transaction rollback strategy

---

### Phase 2: Backend Development - Core Services (Tuần 2-3)
**Duration:** 8-10 ngày  
**Deliverables:**
- ✓ Invoice services (list, get detail)
- ✓ Payment services (create, validate)
- ✓ VNPAY adapter (URL generation, signature verification)
- ✓ IPN handler (webhook for VNPAY callbacks)
- ✓ Unit & integration tests ≥85% coverage

**Key Components:**
1. InvoiceService (list invoices, get detail, validate access)
2. PaymentService (create payment record, update status)
3. VNPAYAdapter (URL generation, hash verification)
4. VNPAYWebhookHandler (IPN processing, idempotency)
5. Transaction Manager (atomic operations)

---

### Phase 3: Backend Development - APIs & Integration (Tuần 3-4)
**Duration:** 6-8 ngày  
**Deliverables:**
- ✓ REST APIs implemented & documented
- ✓ VNPAY integration tested with sandbox
- ✓ Error handling & retry logic
- ✓ API documentation (Swagger)

**Key Endpoints:**
- `GET /api/v1/tenant/invoices` - List
- `GET /api/v1/tenant/invoices/{id}` - Detail
- `POST /api/v1/tenant/invoices/{id}/payment/vnpay` - Create payment URL
- `GET /api/v1/payment/vnpay/return` - Return URL handler
- `POST /api/v1/payment/vnpay/ipn` - IPN webhook handler
- `GET /api/v1/tenant/payments/history` - Payment history

---

### Phase 4: Frontend Development (Tuần 4-6)
**Duration:** 10-12 ngày  
**Deliverables:**
- ✓ Invoice list & detail pages
- ✓ Payment flow UI
- ✓ Success/Fail/Processing screens
- ✓ Payment history view
- ✓ Responsive design verified

**Key Components:**
1. InvoiceListPage
2. InvoiceDetailPage with Payment Button
3. PaymentProcessingScreen (loader)
4. PaymentSuccessScreen
5. PaymentFailedScreen + Retry
6. PaymentHistoryPage

---

### Phase 5: Testing & Deployment (Tuần 7-8)
**Duration:** 8-10 ngày  
**Deliverables:**
- ✓ E2E tests passed
- ✓ Security testing (hash verification, authorization)
- ✓ Load testing (performance targets)
- ✓ UAT signed off
- ✓ Production deployment completed
- ✓ VNPAY production account activated

---

## 4. Technical Design Decisions

### VNPAY Integration Flow
```
User clicks Thanh Toán
        ↓
Backend creates vnp_Url with params + SecureHash
        ↓
Frontend redirects to VNPAY
        ↓
User enters payment info at VNPAY
        ↓
VNPAY processes payment
        ↓
VNPAY sends Return URL (synchronous)
VNPAY sends IPN (asynchronous)
        ↓
Backend verifies SecureHash
Backend validates Amount, Invoice, Status
        ↓
Backend updates invoice.status = PAID/FAILED
Backend saves payment record with all VNPAY info
        ↓
Frontend shows Success/Failed screen
```

### Idempotency Strategy
- Use **transaction number** from VNPAY as idempotency key
- Check if payment already recorded before updating DB
- Return HTTP 200 even if already processed

### Database Transactions
```
BEGIN TRANSACTION
  ├─ Save payment record to `payments` table
  ├─ Update invoice.status in `invoices` table
  ├─ Log transaction to audit_logs
  └─ COMMIT or ROLLBACK
```

### Error Handling
| Error | HTTP | Action |
|-------|------|--------|
| Invalid invoice | 400 | Bad Request |
| Not found/no access | 404 | Not Found |
| Already paid | 409 | Conflict |
| Bad hash | 500 + Log | Security alert |
| Amount mismatch | 500 + Reject | Fraud alert |
| DB error | 500 | Rollback transaction |

---

## 5. Security Considerations

### Critical Security Points
1. **Secure Hash Verification**
   - Compute SHA-256 hash on backend
   - Compare with VNPAY's hash
   - Reject if mismatch → log security event
   - NEVER log secret key

2. **Amount Verification**
   - Verify payment amount matches invoice total
   - Prevent underpayment/overpayment
   - Check decimal places (2 decimals for VND)

3. **Authorization**
   - Verify tenantId from JWT token
   - Verify invoice belongs to tenant
   - Prevent cross-tenant access

4. **Idempotency**
   - Use transaction number as unique key
   - Prevent duplicate payments
   - Log attempted duplicates

5. **Configuration**
   - VNPAY secret key in `.env`
   - VNPAY terminal code in `.env`
   - HTTPS only (enforce SSL)

---

## 6. Success Criteria

### Functional Success
- ✅ Tenant xem được danh sách hóa đơn
- ✅ Chi tiết hóa đơn hiển thị đầy đủ breakdown
- ✅ Thanh toán VNPAY hoạt động end-to-end
- ✅ Hóa đơn cập nhật sang PAID sau khi thanh toán
- ✅ Payment history chính xác
- ✅ Thanh toán thất bại → trạng thái FAILED
- ✅ Không thanh toán cùng hóa đơn 2 lần
- ✅ Empty state & error handling

### Security Success
- ✅ Secure Hash verification hoạt động
- ✅ No cross-tenant access
- ✅ No secret key in logs
- ✅ All validations in place
- ✅ Idempotency working

### Non-Functional Success
- ✅ API response time < 300-500ms (P95)
- ✅ Code coverage ≥ 85%
- ✅ No critical vulnerabilities
- ✅ Mobile responsive
- ✅ VNPAY integration tested with sandbox

### Business Success
- ✅ UAT passed with stakeholders
- ✅ VNPAY production account approved
- ✅ Deployment to production successful
- ✅ Zero financial losses/discrepancies
- ✅ User feedback positive

---

## 7. Risk Assessment & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| VNPAY integration complexity | High | High | Early sandbox testing, detailed design |
| Secure Hash implementation | Medium | Critical | Security review, external audit |
| Amount validation edge cases | Medium | High | Test with various amounts (VND decimals) |
| IPN processing delays | Medium | Medium | Implement retry logic, polling backup |
| Database transaction failures | Low | High | Comprehensive transaction tests, rollback scenarios |
| Duplicate payments | Low | Critical | Idempotency key + unique constraints |

---

## 8. Team & Resources

### Team Composition
- **Backend Developer:** 1-2 people (15-18 days)
- **Frontend Developer:** 1 person (10-12 days)
- **QA/Tester:** 1 person (6-8 days)
- **DevOps/DBA:** 0.5 person (for database setup)
- **Security Lead:** 0.5 person (for security review)
- **Product Owner:** Oversight & sign-off

### Key Dependencies
- VNPAY sandbox account setup
- VNPAY production account approval
- Database schema updates approved
- Authentication service (JWT) already implemented
- SSL/HTTPS infrastructure ready

---

## 9. Timeline & Milestones

```
Week 1  : Design + Infrastructure (VNPAY account)
Week 2-3: Backend Services + VNPAY Adapter
Week 3-4: APIs + Webhook Handler (sandbox testing)
Week 4-6: Frontend Development
Week 6-7: Integration Testing (end-to-end)
Week 7-8: UAT + Production Deployment

Total: 8-10 weeks
```

### Key Milestones
- **M1 (End of Week 1):** Design & VNPAY sandbox account approved
- **M2 (End of Week 3):** Backend services tested with VNPAY sandbox
- **M3 (End of Week 4):** APIs documented & frontend integration ready
- **M4 (End of Week 6):** Frontend UI complete & E2E tests passing
- **M5 (End of Week 7):** UAT completed & VNPAY production approved
- **M6 (End of Week 8):** Live deployment

---

## 10. Definition of Done

For each task to be considered complete:
- ✅ Code written & peer-reviewed
- ✅ Unit tests passed (≥85% coverage)
- ✅ Integration tests passed
- ✅ Security review completed (for payment-related code)
- ✅ Code follows project standards
- ✅ API documentation updated (if applicable)
- ✅ No critical/blocker issues
- ✅ Manual testing in sandbox environment (for payment tasks)
- ✅ Product owner approved

---

## 11. Post-Launch Monitoring

- Monitor payment success rate
- Alert on failed transactions > threshold
- Monitor VNPAY IPN response times
- Track duplicate payment attempts
- Monitor database transaction failures
- Audit log review for security incidents
