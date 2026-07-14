# **Feature: Quản lý thông báo cho ban quản lý**

Status: Completed

Author: Antigravity

Reviewer: [Tên]

Date: 2026-07-14

Priority: High

---

## **1. Business Context**

Tính năng Quản lý thông báo cho Ban quản lý cho phép Manager gửi thông báo chung (theo cơ sở hoặc phòng được phân quyền) tới người thuê trọ. Ngoài ra, tính năng này còn tích hợp luồng gửi nhắc nợ tiền phòng quá hạn (đến phòng có hóa đơn nợ) và xử lý sai lệch chỉ số điện nước thông qua giao dịch báo cáo sai lệch chỉ số điện nước và gửi yêu cầu sửa đổi công việc trực tiếp cho Operator để kiểm tra thực tế.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Manager, I want to gửi thông báo chung cho toàn cơ sở hoặc một phòng cụ thể trong phạm vi quản lý so that cư dân nhận được thông tin vận hành quan trọng kịp thời.

### **Story 2 (Happy Path)**

As a Manager, I want to gửi thông báo nhắc nợ quá hạn dựa trên một hóa đơn chưa thanh toán so that tôi đôn đốc cư dân thanh toán tiền phòng đúng hẹn.

### **Story 3 (Happy Path)**

As a Manager, when phát hiện hóa đơn có chỉ số điện nước nhập sai, I want to báo cáo sai lệch và chuyển tiếp yêu cầu sửa đổi cho Operator kiểm tra so that chỉ số được cập nhật chính xác trước khi cư dân thanh toán.

---

## **3. Acceptance Criteria (EARS)**

### **Gửi thông báo**

WHEN Manager submit thông báo với tiêu đề, nội dung và phạm vi là cơ sở hoặc phòng được phân công THE SYSTEM SHALL chèn bản ghi mới vào bảng `dbo.notifications` với trạng thái `SENT`.

WHEN Manager tries to send a global notification to the entire system THE SYSTEM SHALL reject and return HTTP 403 Forbidden.

### **Nhắc nợ quá hạn**

WHEN Manager sends debt reminder for an overdue invoice THE SYSTEM SHALL generate a notification with code prefix `NTF-DEBT-` targeting the room of that invoice.

### **Báo cáo sai chỉ số điện nước**

WHEN Manager reports incorrect meter readings and sends operator request THE SYSTEM SHALL update meter reading status to `REPORTED` AND insert a new request under `UTILITY` category with status `PENDING` assigned to Operator.

---

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ManagerNotificationsServlet` |
| **URL Pattern** | `GET /manager/notifications` — danh sách thông báo và các hóa đơn báo lỗi |
| **URL Pattern** | `GET /manager/notifications/create` — form tạo thông báo chung |
| **URL Pattern** | `POST /manager/notifications/create` — submit gửi thông báo chung |
| **URL Pattern** | `GET /manager/notifications/send-debt-reminder` — form nhắc nợ quá hạn |
| **URL Pattern** | `POST /manager/notifications/send-debt-reminder` — submit gửi nhắc nợ |
| **URL Pattern** | `GET /manager/notifications/send-operator` — form gửi yêu cầu Operator |
| **URL Pattern** | `POST /manager/notifications/send-operator` — submit gửi yêu cầu Operator |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `notifications` | `List<Notification>` | `notificationService.getManagerNotifications(...)` | Danh sách thông báo (dành cho tab `general` hoặc `payment-reminder`) |
| `incorrectInvoices` | `List<Map<String, Object>>`| `notificationService.getIncorrectInvoices(...)` | Danh sách hóa đơn báo sai chỉ số (dành cho tab `incorrect-utility`) |
| `currentPage`, `totalPages` | `int` | Xử lý logic phân trang | Phục vụ điều hướng phân trang |
| `tab` | `String` | Query Params | Tab đang được hiển thị (`general`, `payment-reminder`, `incorrect-utility`) |

---

### **4.3 Request Attributes — Chi tiết / Tạo mới (send_operator.jsp & send_debt_reminder.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `invoice` | `Map<String, Object>` | `notificationService.getInvoiceDetails(...)` | Thông tin chi tiết hóa đơn phục vụ nhắc nợ / báo cáo sai số |
| `operators` | `List<User>` | `notificationService.getActiveOperatorsForFacility(...)`| Danh sách các Operator đang hoạt động tại cơ sở tương ứng |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Gửi thông báo toàn hệ thống (`ALL`) | Trả về lỗi `403 Forbidden` |
| Tiêu đề hoặc nội dung bỏ trống | Gán `error` message vào Session và redirect về lại trang điền form |
| Lỗi ghi nhận giao dịch Database | Thực hiện rollback giao dịch, gán `error` message và redirect |
| Thao tác ngoài cơ sở quản lý | Trả về lỗi `403 Forbidden` |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Manager chỉ được quản lý thông báo, gửi nhắc nợ và yêu cầu Operator trong phạm vi các phòng thuộc cơ sở được phân công quản lý (`manager_id` trong `dbo.facilities`).
- **Tính toàn vẹn dữ liệu (Transaction):**
  - Giao dịch báo cáo sai chỉ số điện nước (`sendOperatorRequestTransaction`) bắt buộc phải được bọc trong một Database Transaction để đảm bảo trạng thái chỉ số cập nhật sang `REPORTED` và chèn bản ghi yêu cầu hỗ trợ `'PENDING'` gán cho Operator luôn đồng bộ.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải danh sách thông báo theo các tab phân hệ không vượt quá **250 ms (p95)**.
  - Thời gian xử lý giao dịch báo cáo chỉ số điện nước gửi Operator không vượt quá **400 ms (p95)**.

---

## **6. Out of Scope**

- Hỗ trợ thu hồi hoặc xóa bỏ thông báo sau khi đã được phát đi.
- Gửi tin nhắn tự động thông qua SMS, Zalo hoặc Email bên thứ ba.
- Cho phép cư dân phản hồi hay thảo luận trực tiếp bên dưới thông báo.
