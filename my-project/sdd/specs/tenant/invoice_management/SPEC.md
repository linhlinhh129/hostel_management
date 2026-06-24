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

THE SYSTEM SHALL tính toán tổng tiền bao gồm tiền gốc và phí phạt trễ hạn (0.05% mỗi ngày nếu có).

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

\---

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

\- Payment Date

\- Payment Method

\- VNPAY Transaction No

\- Status

\---

\# 6. Error Handling

\### ER01

Invoice không tồn tại

→ HTTP 404

\---

\### ER02

Invoice không thuộc Tenant

→ HTTP 403

\---

\### ER03

Invoice đã thanh toán

→ HTTP 409

\---

\### ER04

Invoice đang PROCESSING

→ HTTP 409

\---

\### ER05

Secure Hash sai

→ Reject IPN

→ ghi Security Log

\---

\### ER06

Amount từ VNPAY khác Invoice

→ Reject

→ Không cập nhật DB

\---

\### ER07

IPN gửi nhiều lần

→ áp dụng Idempotency

→ bỏ qua

→ trả HTTP 200

\---

\### ER08

DB update lỗi

→ Rollback toàn bộ Transaction

\---

\# 7. Technical Notes

\## APIs

\### Invoice List

GET /api/v1/tenant/invoices

\---

\### Invoice Detail

GET /api/v1/tenant/invoices/{invoiceId}

\---

\### Payment History

GET /api/v1/tenant/payments/history

\---

\### Create Payment URL

POST /api/v1/tenant/invoices/{invoiceId}/payment/vnpay

\---

\### VNPAY Return URL

GET /api/v1/payment/vnpay/return

\---

\### VNPAY IPN

POST /api/v1/payment/vnpay/ipn

\---

\# 8. Validation

\- User đã đăng nhập

\- Role = Tenant

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