# Feature: Thanh toán hóa đơn điện nước qua MoMo

**Status:** Draft  
**Author:** [Tên của bạn]  
**Tech Lead Approval:** [Tên Tech Lead]  
**Date:** 2026-06-10  
**Risk Level:** High

---

# 1. Business Context & Goals

Hệ thống cho phép cư dân thanh toán hóa đơn điện nước trực tuyến thông qua cổng thanh toán MoMo.

Mục tiêu của tính năng:

- Giảm thao tác thu tiền thủ công.
- Đồng bộ trạng thái thanh toán theo thời gian thực.
- Tăng tính minh bạch trong quản lý công nợ.
- Lưu trữ lịch sử giao dịch phục vụ đối soát.

---

# 2. Stakeholders & User Personas

### Resident (Cư dân)

- Xem hóa đơn điện nước.
- Thanh toán trực tuyến.
- Theo dõi trạng thái giao dịch.

### Manager

- Theo dõi tình trạng thanh toán.
- Đối soát công nợ.

### System Administrator

- Quản lý tích hợp MoMo.
- Kiểm tra giao dịch lỗi.

---

# 3. User Stories

## Story 1 – Xem hóa đơn

**As a** cư dân

**I want to** xem hóa đơn điện nước

**so that** biết số tiền cần thanh toán.

---

## Story 2 – Tạo giao dịch

**As a** cư dân

**I want to** tạo yêu cầu thanh toán

**so that** nhận được link thanh toán MoMo.

---

## Story 3 – Thanh toán thành công

**As a** cư dân

**I want to** thanh toán trên MoMo

**so that** hóa đơn được ghi nhận đã thanh toán.

---

## Story 4 – Thanh toán thất bại

**As a** cư dân

**when** giao dịch bị hủy hoặc thất bại

**I want to** nhận được thông báo phù hợp.

---

# 4. Acceptance Criteria (EARS)

## AC01 – Xem hóa đơn

**WHEN** cư dân mở màn hình hóa đơn

**THE SYSTEM SHALL**

- Hiển thị:
  - Mã hóa đơn
  - Kỳ thanh toán
  - Chỉ số điện kỳ trước
  - Chỉ số điện kỳ hiện tại
  - Chỉ số nước kỳ trước
  - Chỉ số nước kỳ hiện tại
  - Sản lượng điện tiêu thụ
  - Sản lượng nước tiêu thụ
  - Tiền điện
  - Tiền nước
  - Tổng tiền
  - Trạng thái thanh toán

---

## AC02 – Tạo yêu cầu thanh toán

**WHEN** cư dân nhấn nút [Thanh toán]

**THE SYSTEM SHALL**

- Kiểm tra hóa đơn chưa thanh toán.
- Tạo giao dịch mới.
- Gửi request sang MoMo.
- Nhận payment URL.
- Trả URL về client.

---

## AC03 – Redirect MoMo

**WHEN** payment URL được trả về

**THE SYSTEM SHALL**

- Điều hướng cư dân tới trang thanh toán MoMo.

---

## AC04 – Nhận IPN thành công

**WHEN** MoMo gửi callback với kết quả SUCCESS

**THE SYSTEM SHALL**

- Xác thực chữ ký (signature).
- Kiểm tra transactionId.
- Cập nhật trạng thái hóa đơn thành PAID.
- Ghi nhận thời gian thanh toán.
- Lưu mã giao dịch MoMo.

---

## AC05 – Nhận IPN thất bại

**WHEN** MoMo gửi callback FAILED

**THE SYSTEM SHALL**

- Giữ nguyên trạng thái UNPAID.
- Lưu log giao dịch thất bại.

---

## AC06 – Tránh thanh toán trùng

**WHEN** hóa đơn đã ở trạng thái PAID

**THE SYSTEM SHALL**

- Không tạo giao dịch mới.
- Trả về HTTP 409 Conflict.

---

## AC07 – Hiển thị kết quả thanh toán

**WHEN** người dùng quay lại từ MoMo

**THE SYSTEM SHALL**

- Hiển thị:
  - Thanh toán thành công
  - Hoặc thanh toán thất bại

dựa trên trạng thái giao dịch trong hệ thống.

---

# 5. API Contracts

## Endpoint 1 – Xem hóa đơn

```http
GET /api/v1/invoices/{invoiceId}
```

### Response 200

```json
{
  "invoiceId": "INV001",
  "electricUsage": 120,
  "waterUsage": 15,
  "electricAmount": 420000,
  "waterAmount": 150000,
  "totalAmount": 570000,
  "status": "UNPAID"
}
```

---

## Endpoint 2 – Tạo thanh toán

```http
POST /api/v1/payments/momo/create
```

### Request

```json
{
  "invoiceId": "INV001"
}
```

### Response 200

```json
{
  "paymentUrl": "https://payment.momo.vn/..."
}
```

---

## Endpoint 3 – MoMo IPN Callback

```http
POST /api/v1/payments/momo/ipn
```

### Response

```json
{
  "resultCode": 0,
  "message": "Success"
}
```

---

## Endpoint 4 – Kiểm tra trạng thái giao dịch

```http
GET /api/v1/payments/{transactionId}
```

---

# 6. Data Models & DB Schema Changes

## Table: invoices

| Column | Type |
|----------|----------|
| id | bigint |
| resident_id | bigint |
| total_amount | decimal |
| status | varchar |
| paid_at | datetime |

---

## Table: payment_transactions

| Column | Type |
|----------|----------|
| id | bigint |
| invoice_id | bigint |
| momo_order_id | varchar |
| momo_trans_id | varchar |
| amount | decimal |
| status | varchar |
| created_at | datetime |
| paid_at | datetime |

---

# 7. Non-Functional Requirements

## Performance

- API response < 500ms (p95)
- IPN processing < 3 seconds

## Security

- HTTPS bắt buộc.
- Verify MoMo Signature.
- Không tin dữ liệu từ client.
- Chống replay attack.

## Availability

- Uptime ≥ 99.9%

---

# 8. Error Handling Matrix

| Error Code | HTTP | Description |
|------------|------|-------------|
| INVOICE_NOT_FOUND | 404 | Không tìm thấy hóa đơn |
| INVOICE_ALREADY_PAID | 409 | Hóa đơn đã thanh toán |
| INVALID_SIGNATURE | 401 | Sai chữ ký MoMo |
| PAYMENT_FAILED | 400 | Thanh toán thất bại |
| MOMO_TIMEOUT | 504 | Timeout khi gọi MoMo |

---

# 9. Edge Cases & Corner Cases

- Người dùng đóng trình duyệt sau khi thanh toán.
- MoMo gửi callback nhiều lần.
- Callback đến trước khi user redirect về hệ thống.
- Thanh toán thành công nhưng mất mạng.
- Hai request thanh toán cùng lúc.

---

# 10. Dependencies & Integration Points

- MoMo Payment Gateway
- Invoice Service
- Resident Service
- Notification Service

---

# 11. Testing Requirements

## Unit Test

- Tính tiền hóa đơn.
- Verify signature.

## Integration Test

- Tạo giao dịch MoMo.
- Nhận IPN.

## E2E Test

- Thanh toán thành công.
- Thanh toán thất bại.
- Thanh toán bị hủy.

---

# 12. Rollout Plan

- Deploy Sandbox MoMo.
- UAT Testing.
- Production Release.
- Monitoring giao dịch 7 ngày đầu.

---

# 13. Open Questions

### Q1

MoMo có phải là cổng thanh toán duy nhất không?

Owner: Product Owner

### Q2

Có cho phép thanh toán một phần hóa đơn hay không?

Owner: Business Analyst

---