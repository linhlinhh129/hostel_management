# **Feature: Quản lý thanh toán**

Status: Draft

Author: Bùi Đỉnh

Reviewer: \[Tên\]

Date: \[YYYY-MM-DD\]

Priority: High

---

## **1. Business Context**

Trong quá trình thuê phòng, người thuê cần thực hiện thanh toán các khoản chi phí thông qua VNPAY hoặc Chuyển khoản ngân hàng.

Feature Quản lý thanh toán cho phép Ban quản lý xem danh sách giao dịch thanh toán, kiểm tra thông tin chi tiết giao dịch (cùng hóa đơn liên quan), và xác nhận (duyệt/từ chối) giao dịch thanh toán. Chức năng này giúp đảm bảo các khoản thanh toán được ghi nhận chính xác, hạn chế sai sót trong quá trình quản lý tài chính.

Feature này hỗ trợ mục tiêu quản lý dòng tiền, theo dõi tình trạng thanh toán của người thuê và cập nhật chính xác trạng thái công nợ trong hệ thống.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Ban quản lý, I want to xem danh sách các giao dịch thanh toán so that tôi có thể theo dõi tình trạng thanh toán của người thuê.

### **Story 2 (Happy Path)**

As a Ban quản lý, I want to xem chi tiết giao dịch và hóa đơn liên quan so that tôi có thể đối chiếu thông tin nộp tiền.

### **Story 3 (Happy Path)**

As a Ban quản lý, I want to xác nhận duyệt giao dịch thanh toán so that hệ thống có thể cập nhật trạng thái thanh toán và hóa đơn liên quan thành SUCCESS.

### **Story 4 (Happy Path)**

As a Ban quản lý, I want to từ chối giao dịch không hợp lệ so that hệ thống ghi nhận giao dịch đã bị từ chối.

### **Story 5 (Edge Case)**

As a Ban quản lý, I want to duyệt lại các giao dịch đã bị từ chối trước đó so that tôi có thể khắc phục nếu có sai sót khi từ chối nhầm.

### **Story 6 (Edge Case)**

As a Ban quản lý, when giao dịch không tồn tại I want the system to hiển thị lỗi phù hợp.

### **Story 7 (Edge Case)**

As a Ban quản lý, when tôi chưa đăng nhập hoặc không có quyền truy cập I want the system to từ chối thao tác.

## **3. Acceptance Criteria (EARS)**

### **Xem danh sách giao dịch**

WHEN Management Board opens payment transaction list THE SYSTEM SHALL display all payment transactions.

WHEN payment transaction list is displayed THE SYSTEM SHALL show:

- Transaction ID
- Transaction Code
- Tenant Name
- Room Code
- Payment Amount
- Payment Date
- Payment Method
- Payment Status

WHEN no transaction exists THE SYSTEM SHALL return an empty list.

### **Xem chi tiết giao dịch**

WHEN Management Board selects a transaction THE SYSTEM SHALL display transaction details.

WHEN transaction details are displayed THE SYSTEM SHALL show tenant info, payment amount, payment method, date, and status.

WHEN transaction has an associated invoice THE SYSTEM SHALL display invoice details (code, due date, total, note).

### **Duyệt thanh toán thành công**

WHEN Management Board approves a valid transaction (PENDING or REJECTED) THE SYSTEM SHALL update transaction status to SUCCESS AND return HTTP 200 (or redirect).

WHEN transaction is approved successfully THE SYSTEM SHALL update the related invoice/debt status to PAID.

WHEN transaction is already approved THE SYSTEM SHALL return HTTP 400 with error code PAYMENT_ALREADY_APPROVED.

### **Từ chối giao dịch**

WHEN Management Board rejects a PENDING transaction THE SYSTEM SHALL update transaction status to REJECTED.

### **Kiểm tra giao dịch không tồn tại**

WHEN Management Board approves a non-existing transaction THE SYSTEM SHALL return HTTP 404 with error code TRANSACTION_NOT_FOUND.

### **Kiểm tra chưa đăng nhập**

WHILE user is unauthenticated THE SYSTEM SHALL prevent viewing transactions AND return HTTP 401 with error code UNAUTHORIZED.

WHILE user is unauthenticated THE SYSTEM SHALL prevent approving transactions AND return HTTP 401 with error code UNAUTHORIZED.

### **Kiểm tra không phải Ban quản lý**

WHILE user role is not Management Board THE SYSTEM SHALL prevent approving transactions AND return HTTP 403 with error code FORBIDDEN.

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `PaymentServlet` (danh sách) và `PaymentDetailServlet` (chi tiết và duyệt/từ chối) |
| **URL Pattern** | `GET /manager/payments` — danh sách thanh toán |
| **URL Pattern** | `GET /manager/payments/{id}` — xem chi tiết thanh toán |
| **URL Pattern** | `POST /manager/payments/{id}/approve` — duyệt thanh toán thành công |
| **URL Pattern** | `POST /manager/payments/{id}/reject` — từ chối thanh toán |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `payments` | `List<PaymentListItemDTO>` | `PaymentService.findPayments(...)` | Danh sách giao dịch thanh toán |
| `currentPage`, `totalPages` | `int` | Xử lý logic phân trang | Phục vụ điều hướng phân trang |
| `keyword`, `status`, `fromDate`, `toDate`, `month`, `year` | `String` | Query Params | Giữ lại các bộ lọc trên giao diện (form) |

---

### **4.3 Request Attributes — Chi tiết (detail.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `payment` | `PaymentDetailDTO` | `PaymentService.findById(userId, paymentId)` | Thông tin chi tiết giao dịch (chứa thông tin giao dịch và hóa đơn liên quan) |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect hoặc HTTP 401 Unauthorized |
| `id` không tồn tại hoặc sai format | Trả về HTTP 400 (`Invalid payment ID`) |
| Giao dịch không tồn tại | Forward tới `404.jsp` với thuộc tính `error` = `"Giao dịch không tồn tại."` |
| Phê duyệt / Từ chối thành công | Gán `success` message vào Session và chuyển hướng (`sendRedirect`) về lại trang chi tiết `/manager/payments/{id}` |
| Có Exception (thất bại logic) | Gán `error` message vào Session và chuyển hướng (`sendRedirect`) về lại trang chi tiết |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Chỉ người dùng có vai trò Ban quản lý (`MANAGER`) mới được phép truy cập và thực hiện thao tác duyệt/từ chối giao dịch.
- **Tính toàn vẹn dữ liệu (Transaction):**
  - Thao tác duyệt giao dịch phải được đặt trong một Database Transaction duy nhất để đảm bảo trạng thái giao dịch (`SUCCESS`) và trạng thái hóa đơn liên quan (`PAID`) luôn đồng bộ.
  - Mọi thao tác Duyệt hoặc Từ chối đều phải cập nhật vết: người thực hiện (`approvedBy` lấy từ Session) và thời gian (`approvedAt`).
- **Luồng chuyển đổi trạng thái (State Machine):**
  - Giao dịch ở trạng thái `PENDING` hoặc `REJECTED` đều được phép Duyệt (chuyển thành `SUCCESS`).
  - Giao dịch đang ở trạng thái `SUCCESS` thì không cho phép Từ chối hoặc hủy bỏ.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải danh sách giao dịch (có kèm bộ lọc và phân trang) không vượt quá **1 giây (p95)**.
  - Thời gian xử lý logic cập nhật trạng thái Duyệt/Từ chối không vượt quá **500 ms (p95)**.

---

## **6. Out of Scope**

- Hủy giao dịch đã duyệt.
- Chỉnh sửa ảnh xác nhận thanh toán.
- Đối soát ngân hàng tự động.
- Gửi email xác nhận thanh toán.
- Gửi SMS xác nhận thanh toán.
- Báo cáo doanh thu.