# **Feature: Quản lý thanh toán**

Status: Draft

Author: Bùi Đỉnh

Reviewer: \[Tên\]

Date: \[YYYY-MM-DD\]

Priority: High

---

## **1. Business Context**

Trong quá trình thuê phòng, người thuê cần thực hiện thanh toán các khoản chi phí như tiền thuê phòng, tiền điện, tiền nước, phí dịch vụ và các khoản phát sinh khác. Để chứng minh việc thanh toán đã được thực hiện, người thuê tải lên ảnh xác nhận thanh toán sau khi chuyển khoản.

Feature Quản lý thanh toán cho phép Ban quản lý xem danh sách giao dịch thanh toán, kiểm tra thông tin giao dịch, xem ảnh xác nhận thanh toán và xác nhận giao dịch đã thanh toán thành công. Chức năng này giúp đảm bảo các khoản thanh toán được ghi nhận chính xác, hạn chế sai sót trong quá trình quản lý tài chính.

Feature này hỗ trợ mục tiêu quản lý dòng tiền, theo dõi tình trạng thanh toán của người thuê và cập nhật chính xác trạng thái công nợ trong hệ thống.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Ban quản lý,

I want to xem danh sách các giao dịch thanh toán

so that tôi có thể theo dõi tình trạng thanh toán của người thuê.

---

### **Story 2 (Happy Path)**

As a Ban quản lý,

I want to xem ảnh xác nhận thanh toán

so that tôi có thể kiểm tra bằng chứng thanh toán trước khi duyệt giao dịch.

---

### **Story 3 (Happy Path)**

As a Ban quản lý,

I want to xác nhận giao dịch thanh toán thành công

so that hệ thống có thể cập nhật trạng thái thanh toán và công nợ tương ứng.

---

### **Story 4 (Edge Case)**

As a Ban quản lý,

when ảnh xác nhận thanh toán không tồn tại

I want the system to từ chối duyệt giao dịch.

---

### **Story 5 (Edge Case)**

As a Ban quản lý,

when giao dịch không tồn tại

I want the system to hiển thị lỗi phù hợp.

---

### **Story 6 (Edge Case)**

As a Ban quản lý,

when tôi chưa đăng nhập hoặc không có quyền truy cập

I want the system to từ chối thao tác.

---

## **3. Acceptance Criteria (EARS)**

### **Xem danh sách giao dịch**

WHEN Management Board opens payment transaction list

THE SYSTEM SHALL display all payment transactions.

WHEN payment transaction list is displayed

THE SYSTEM SHALL show:

- Transaction ID
- Transaction Code
- Tenant Name
- Room Code
- Payment Amount
- Payment Date
- Payment Method
- Payment Status

WHEN no transaction exists

THE SYSTEM SHALL return an empty list.

---

### **Xem ảnh xác nhận thanh toán**

WHEN Management Board selects a transaction

THE SYSTEM SHALL display transaction details.

WHEN transaction details are displayed

THE SYSTEM SHALL display uploaded payment proof image.

WHEN payment proof image does not exist

THE SYSTEM SHALL return HTTP 404 with error code PAYMENT_PROOF_NOT_FOUND.

---

### **Duyệt thanh toán thành công**

WHEN Management Board approves a valid transaction

THE SYSTEM SHALL update transaction status to PAID AND return HTTP 200.

WHEN transaction is approved successfully

THE SYSTEM SHALL record approvedAt and approvedBy.

WHEN transaction is approved successfully

THE SYSTEM SHALL update the related debt status to PAID.

WHEN transaction is already approved

THE SYSTEM SHALL return HTTP 400 with error code PAYMENT_ALREADY_APPROVED.

---

### **Kiểm tra giao dịch không tồn tại**

WHEN Management Board approves a non-existing transaction

THE SYSTEM SHALL return HTTP 404 with error code TRANSACTION_NOT_FOUND.

---

### **Kiểm tra chưa đăng nhập**

WHILE user is unauthenticated

THE SYSTEM SHALL prevent viewing transactions AND return HTTP 401 with error code UNAUTHORIZED.

WHILE user is unauthenticated

THE SYSTEM SHALL prevent approving transactions AND return HTTP 401 with error code UNAUTHORIZED.

---

### **Kiểm tra không phải Ban quản lý**

WHILE user role is not Management Board

THE SYSTEM SHALL prevent approving transactions AND return HTTP 403 with error code FORBIDDEN.

---

## **4. API Contract**

### **Endpoint**

GET /api/v1/payments

### **Mục đích**

Lấy danh sách giao dịch thanh toán.

### **Query Params**

- keyword (optional)
- status (optional)
- page (optional)
- size (optional)

### **Response 200**

{

"success": true,

"data": \[

{

"transactionId": "uuid",

"transactionCode": "PAY001",

"tenantName": "Nguyễn Văn A",

"roomCode": "A101",

"amount": 3000000,

"paymentDate": "2026-06-10",

"status": "PENDING"

}

\]

}

---

### **Endpoint**

GET /api/v1/payments/{transactionId}

### **Mục đích**

Xem chi tiết giao dịch và ảnh xác nhận thanh toán.

### **Response 200**

{

"success": true,

"data": {

"transactionId": "uuid",

"transactionCode": "PAY001",

"tenantName": "Nguyễn Văn A",

"roomCode": "A101",

"amount": 3000000,

"paymentDate": "2026-06-10",

"paymentProofUrl": "proof-image.jpg",

"status": "PENDING"

}

}

### **Response 404**

{

"success": false,

"error": {

"code": "TRANSACTION_NOT_FOUND",

"message": "Không tìm thấy giao dịch"

}

}

---

### **Endpoint**

POST /api/v1/payments/{transactionId}/approve

### **Mục đích**

Duyệt giao dịch thanh toán thành công.

### **Request**

{

"note": "Đã xác nhận giao dịch hợp lệ"

}

### **Response 200**

{

"success": true,

"data": {

"transactionId": "uuid",

"status": "PAID",

"approvedAt": "2026-06-10T10:00:00",

"approvedBy": 5

}

}

### **Response 400**

{

"success": false,

"error": {

"code": "PAYMENT_ALREADY_APPROVED",

"message": "Giao dịch đã được duyệt"

}

}

### **Response 404**

{

"success": false,

"error": {

"code": "TRANSACTION_NOT_FOUND",

"message": "Không tìm thấy giao dịch"

}

}

### **Response 401**

{

"success": false,

"error": {

"code": "UNAUTHORIZED",

"message": "Người dùng chưa đăng nhập"

}

}

### **Response 403**

{

"success": false,

"error": {

"code": "FORBIDDEN",

"message": "Không có quyền duyệt thanh toán"

}

}

---

## **5. Technical Constraints**

- Feature này chỉ dành cho Management Board.
- Tất cả API phải yêu cầu Authentication Token.
- Chỉ Management Board được xem và duyệt giao dịch thanh toán.
- Mỗi giao dịch phải liên kết với một khoản công nợ hợp lệ.
- Mỗi giao dịch phải có một người thuê hợp lệ.
- Ảnh xác nhận thanh toán phải được lưu trữ trước khi giao dịch được duyệt.
- Chỉ các giao dịch ở trạng thái PENDING mới được phép duyệt.
- Sau khi duyệt thành công, trạng thái giao dịch phải chuyển sang PAID.
- Sau khi duyệt thành công, trạng thái công nợ liên quan phải được cập nhật thành PAID.
- Hệ thống phải lưu:
  - createdAt
  - createdBy
  - approvedAt
  - approvedBy
- Tất cả thao tác duyệt thanh toán phải được ghi Audit Log.
- API lấy danh sách giao dịch phải phản hồi dưới 1 giây (p95).
- API xem chi tiết giao dịch phải phản hồi dưới 500ms (p95).
- API duyệt giao dịch phải phản hồi dưới 1 giây (p95).
- Rate limit: 100 requests/minute per user.

---

## **6. Out of Scope**

- Tích hợp cổng thanh toán trực tuyến.
- Thanh toán tự động qua ngân hàng.
- Hoàn tiền.
- Hủy giao dịch đã duyệt.
- Chỉnh sửa ảnh xác nhận thanh toán.
- OCR đọc nội dung ảnh chuyển khoản.
- Đối soát ngân hàng tự động.
- Gửi email xác nhận thanh toán.
- Gửi SMS xác nhận thanh toán.
- AI kiểm tra tính hợp lệ của ảnh chuyển khoản.
- Báo cáo doanh thu.