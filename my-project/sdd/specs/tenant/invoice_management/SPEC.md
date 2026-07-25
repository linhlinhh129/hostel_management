# # Feature: Invoice Management & VNPAY Payment (Quản lý Hóa đơn & Thanh toán VNPAY)

**Status:** Draft\\

**Author:** Business Analyst\\

**Date:** 2026-06-21\\

**Priority:** High\\

**Risk Level:** High (Financial Transaction)

\---

\# 1. Business Context & Goal

Tính năng Invoice Management cho phép người thuê xem danh sách hóa đơn, xem chi tiết từng hóa đơn, theo dõi lịch sử thanh toán và thanh toán trực tuyến thông qua cổng VNPAY.

Sau khi VNPAY xác nhận giao dịch thành công, hệ thống sẽ tự động cập nhật trạng thái hóa đơn, đồng thời lưu toàn bộ thông tin giao dịch vào bảng `payments` phục vụ đối soát.

\---

\# 2. User Story

**As a** Tenant

**I want to**

\- xem danh sách hóa đơn

\- xem chi tiết hóa đơn

\- thanh toán trực tuyến bằng VNPAY

\- xem lịch sử thanh toán

**So that**

tôi có thể theo dõi và thanh toán các khoản phí của mình.

\---

\# 3. Actors & Roles

\### Tenant

\- Xem hóa đơn

\- Thanh toán VNPAY

\- Xem lịch sử thanh toán

\### System

\- Lấy dữ liệu hóa đơn

\- Sinh URL thanh toán

\- Xử lý callback/IPN

\- Cập nhật hóa đơn

\- Lưu giao dịch thanh toán

\### VNPAY

\- Xử lý thanh toán

\- Trả kết quả giao dịch

\- Gửi IPN/Webhook

\---

\# 4. Invoice State Diagram

\`\`\`text

UNPAID

   │

   │ Click Thanh toán

   ▼

PROCESSING

   │

   ├──────────────► PAID

   │                  ▲

   │                  │

   │                  │ IPN Success

   │

   └──────────────► FAILED

                      │

                      │ Thanh toán lại

                      ▼

                  PROCESSING

\`\`\`

\---

\# 5. Functional Requirements (EARS)

\## AC04 - Thanh toán trực tuyến VNPay Sandbox

WHEN người thuê thanh toán hóa đơn

THE SYSTEM SHALL tính toán tổng tiền bao gồm tiền gốc và phí phạt trễ hạn (1% tiền phòng mỗi ngày nếu có).

AND tạo phiên giao dịch VNPay Sandbox với mã tham chiếu (TxnRef) định dạng `INV{id}T{timestamp}`.

AND chuyển hướng người thuê sang cổng thanh toán.

\---

\## AC05 - Xử lý Return từ VNPay (Tự động cập nhật Database)

WHEN VNPay trả về kết quả thanh toán cho hệ thống

THE SYSTEM SHALL xác thực chữ ký bảo mật (SecureHash).

AND nếu giao dịch thành công (Mã 00), hệ thống thực thi Transaction Database:

1\. Thêm một bản ghi vào bảng `payments` (Lưu lịch sử thanh toán thành công).

2\. Cập nhật `status = PAID` cho hóa đơn trong bảng `invoices`.

AND đảm bảo tính toàn vẹn dữ liệu (Không thêm bản ghi rác nếu bị lỗi giữa chừng - Rollback).

\---

\### FR03

**WHEN** danh sách được tải

**THE SYSTEM SHALL**

Sắp xếp theo kỳ hóa đơn giảm dần.

\---

\## Invoice Detail

\### FR04

**WHEN** Tenant chọn một hóa đơn

**THE SYSTEM SHALL**

Hiển thị:

\- Mã hóa đơn

\- Phòng

\- Tiền phòng

\- Chỉ số điện cũ

\- Chỉ số điện mới

\- Đơn giá điện

\- Thành tiền điện

\- Chỉ số nước cũ

\- Chỉ số nước mới

\- Đơn giá nước

\- Thành tiền nước

\- Internet

\- Phí dịch vụ

\- Thuế

\- Phụ phí

\- Tổng tiền

\- Hạn thanh toán

\- Trạng thái

---

## Luồng xử lý giao dịch thanh toán (Chuyển khoản)

### FR04.1 - Khởi tạo giao dịch qua VNPAY
**WHEN** Người dùng thực hiện thanh toán qua cổng VNPAY và nhận thông báo thành công từ VNPAY
**THE SYSTEM SHALL**
- Lưu một bản ghi vào bảng `payments` với phương thức thanh toán là `VNPAY`.
- Đặt trạng thái giao dịch (`status`) mặc định là `PENDING` (Đang xử lý).
- Hóa đơn vẫn giữ trạng thái `UNPAID` nhưng trên giao diện web phải hiển thị trạng thái "Chờ duyệt" và không hiện cổng thanh toán VNPAY nữa.

### FR04.2 - Ban quản lý Duyệt giao dịch
**WHEN** Ban quản lý phê duyệt giao dịch thanh toán
**THE SYSTEM SHALL**
- Chỉ khi Ban quản lý phê duyệt giao dịch, cập nhật trạng thái bản ghi trong bảng `payments` thành `SUCCESS`.
- Cập nhật trạng thái hóa đơn thành `PAID` và hiển thị "Đã thanh toán" trên web.

### FR04.3 - Ban quản lý Hủy giao dịch
**WHEN** Ban quản lý từ chối hoặc giao dịch bị hủy
**THE SYSTEM SHALL**
- Cập nhật trạng thái bản ghi trong bảng `payments` thành `REJECTED`.
- Trạng thái bảng `invoices` vẫn là `UNPAID`, giao diện sẽ hiển thị lại cổng thanh toán VNPAY để người dùng tiến hành thanh toán lại.

----

\## Thanh toán VNPAY

\### FR05

**WHEN** Tenant chọn **Thanh toán VNPAY**

**AND** hóa đơn có trạng thái UNPAID

**THE SYSTEM SHALL**

\- tạo URL thanh toán VNPAY

\- cập nhật trạng thái invoice thành PROCESSING

\- redirect sang VNPAY

\---

\### FR06

URL thanh toán SHALL bao gồm

\- invoiceId

\- amount

\- createDate

\- expireDate

\- orderInfo

\- returnUrl

\- ipAddr

\- secureHash

\---

\### FR07

**WHEN** VNPAY trả kết quả thành công

(vnp_ResponseCode = 00)

**THE SYSTEM SHALL**

\- Verify Secure Hash

\- Verify Amount

\- Verify Invoice

\- Verify Transaction chưa xử lý

Sau đó

\- lưu transaction vào bảng payments

\- cập nhật invoice.status = PAID

\---

\### FR08

Thông tin lưu vào payments

\- invoice_id

\- payment_amount

\- payment_date

\- payment_method = VNPAY

\- status = SUCCESS

\- vnp_transaction_no

\- vnp_bank_code

\- vnp_bank_tran_no

\- vnp_response_code

\- raw_vnpay_response

\---

\### FR09

**WHEN**

VNPAY trả về giao dịch thất bại

**THE SYSTEM SHALL**

\- cập nhật invoice.status = FAILED

\- ghi log lỗi

\- hiển thị thông báo thanh toán thất bại

\---

\### FR10

**WHEN**

Tenant thanh toán lại hóa đơn FAILED

**THE SYSTEM SHALL**

cho phép tạo giao dịch VNPAY mới.

\---

\## Payment History

\### FR11

**WHEN**

Tenant truy cập Payment History

**THE SYSTEM SHALL**

Hiển thị danh sách các giao dịch thành công.

\---

\### FR12

Thông tin hiển thị

\- Payment Code

\- Invoice Code

\- Billing Period

\- Amount

# 6. Servlet Routes & Page Controller Contract

## 6.1 Màn hình Danh sách hóa đơn

### Servlet Mapping
```http
GET /tenant/invoices
```
- **Servlet:** `TenantInvoiceListServlet`
- **Scope & Attribute Name:** `request.setAttribute("invoiceList", List<InvoiceDTO>)`
- **Forward View:** `/WEB-INF/views/tenant/invoice-list.jsp`

---

## 6.2 Màn hình Chi tiết hóa đơn

### Servlet Mapping
```http
GET /tenant/invoice-detail?id={invoiceId}
```
- **Servlet:** `TenantInvoiceDetailServlet`
- **Parameter:** `id`
- **Scope & Attribute Name:** `request.setAttribute("invoice", InvoiceDetailDTO)`
- **Forward View:** `/WEB-INF/views/tenant/invoice-detail.jsp`

---

## 6.3 Lịch sử thanh toán

### Servlet Mapping
```http
GET /tenant/payment-history
```
- **Servlet:** `TenantPaymentHistoryServlet`
- **Scope & Attribute Name:** `request.setAttribute("paymentList", List<PaymentDTO>)`
- **Forward View:** `/WEB-INF/views/tenant/payment-history.jsp`

---

## 6.4 Thanh toán VNPAY (Khởi tạo URL & Redirect)

### Servlet Mapping
```http
POST /tenant/vnpay-payment
```
- **Servlet:** `TenantVnPayPaymentServlet`
- **Parameters:** `invoiceId`
- **Xử lý:** Tạo URL giao dịch VNPAY Sandbox, cập nhật trạng thái hóa đơn thành `PROCESSING` và thực hiện `response.sendRedirect(vnpayUrl)`.

---

## 6.5 VNPAY Return Callback Handling

### Servlet Mapping
```http
GET /payment/vnpay-return
```
- **Servlet:** `VnPayReturnServlet`
- **Xử lý:** Nhận kết quả từ VNPAY, xác thực chữ ký Secure Hash. Nếu hợp lệ và `vnp_ResponseCode = 00`, thực thi DB Transaction lưu `payments` và cập nhật hóa đơn `PAID`.
- **View:** Forward `/WEB-INF/views/tenant/payment-result.jsp` hoặc Redirect `/tenant/invoice-detail?id={id}&status=success`.

---

# 7. Error Handling & Redirection

| Error Code | Status / Action | Description |
| --- | --- | --- |
| UNAUTHORIZED | Redirect `/login` | Chưa đăng nhập (Session không tồn tại) |
| FORBIDDEN | Forward 403 Page | Hóa đơn không thuộc về tài khoản đang đăng nhập |
| INVOICE_NOT_FOUND | Forward 404 Page | Không tìm thấy mã hóa đơn yêu cầu |
| INVOICE_ALREADY_PAID | Redirect `/tenant/invoice-detail` | Hóa đơn đã được thanh toán từ trước |
| INVALID_HASH | Forward Error Page | Chữ ký checksum VNPAY không hợp lệ (Security Violation) |

---

# 8. Validation

\- invoiceId tồn tại

\- invoice thuộc Tenant

\- invoice.status = UNPAID

\- Amount &gt; 0

\- Verify Secure Hash

\- Verify Amount

\- Verify Transaction

\---

\# 9. Database

\## invoices

Sử dụng bảng hiện tại.

Thay đổi trạng thái

\- UNPAID

\- PROCESSING

\- PAID

\- FAILED

\---

\## payments

Bổ sung các cột

\`\`\`sql

vnp_transaction_no NVARCHAR(100)

vnp_bank_code NVARCHAR(20)

vnp_bank_tran_no NVARCHAR(100)

vnp_response_code NVARCHAR(10)

vnp_transaction_status NVARCHAR(10)

raw_vnpay_response NVARCHAR(MAX)

\`\`\`

\---

\# 10. Non-functional Requirements

\### Performance

Danh sách hóa đơn

&lt;300ms

\---

Chi tiết hóa đơn

&lt;300ms

\---

Tạo URL VNPAY

&lt;500ms

\---

\### Security

\- Verify Secure Hash

\- Secret Key lưu trong .env

\- HTTPS bắt buộc

\- Không log Secret Key

\---

\### Transaction

Lưu payment

\- 

Update invoice

phải nằm trong cùng một Database Transaction.

Nếu một bước lỗi

Rollback toàn bộ.

\---

\# 11. Acceptance Criteria

\- Hiển thị đúng danh sách hóa đơn.

\- Hiển thị đúng chi tiết hóa đơn.

\- Chỉ hóa đơn UNPAID có nút Thanh toán VNPAY.

\- Click Thanh toán tạo đúng URL và chuyển hướng sang VNPAY.

\- Thanh toán thành công cập nhật invoice = PAID.

\- Lưu đầy đủ thông tin giao dịch vào bảng payments.

\- Thanh toán thất bại cập nhật invoice = FAILED.

\- Reject IPN có Secure Hash không hợp lệ.

\- Không thể thanh toán cùng một hóa đơn nhiều lần đồng thời.

\- Payment History hiển thị đầy đủ các giao dịch thành công.

\---

\# 12. UI Components

\- Invoice List

\- Invoice Card

\- Invoice Detail View

\- Payment Button (VNPAY)

\- Payment Success Screen

\- Payment Failed Screen

\- Payment History

\- Loading State

\- Empty State

\- Error State

\- Retry Button

\---

\# 13. Out of Scope

\- Thanh toán Momo.

\- Thanh toán ZaloPay.

\- Thanh toán Stripe.

\- Hoàn tiền (Refund).

\- Thanh toán một phần hóa đơn.

\- Thanh toán nhiều hóa đơn trong một giao dịch.

\- Lưu thông tin thẻ ngân hàng trên hệ thống.

\- Thanh toán ngoại tệ.