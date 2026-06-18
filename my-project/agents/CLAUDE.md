# Hostel Management System v1.0

## TL;DR (Đọc trước — 60 giây)

Hệ thống quản lý nhà trọ bao gồm:

* Quản lý cơ sở
* Quản lý phòng trọ
* Quản lý người thuê
* Quản lý phí dịch vụ
* Quản lý sự cố kỹ thuật
* Quản lý lịch làm việc
* Quản lý thông báo nội bộ
* Quản lý người dùng theo vai trò

### Công nghệ

| Thành phần     | Công nghệ                             |
| -------------- | ------------------------------------- |
| Backend        | Java 17 + Jakarta Servlet 6.0 + JDBC  |
| Frontend       | JSP + JSTL + Bootstrap 5              |
| Database       | SQL Server 2022                       |
| Authentication | Session-Based Authentication + BCrypt |
| Server         | Apache Tomcat 10.1                    |

### Async Processing

#### ExecutorService

Dùng cho:

* Gửi Email OTP
* Gửi thông báo nội bộ
* Ghi Audit Log (nếu cần)

#### Scheduler

Dùng cho:

* Tạo hóa đơn hàng tháng
* Quét công nợ quá hạn
* Dọn OTP hết hạn
* Nhắc lịch làm việc
* Nhắc sự cố chưa xử lý

### Realtime

Không bắt buộc trong Sprint 1.

Nếu cần realtime trong tương lai có thể dùng WebSocket cho:

* Thông báo nội bộ
* Cập nhật trạng thái sự cố
* Thông báo thanh toán

### Session

Sử dụng:

```java
HttpSession
```

Không sử dụng:

* JWT
* Redis Session

### CI/CD

Giai đoạn hiện tại:

* Build WAR
* Deploy lên Apache Tomcat

Có thể nâng cấp sau:

* GitHub Actions
* Docker
* VPS / Cloud Server
* Kubernetes (nếu cần scale lớn)

---

# KIẾN TRÚC HỆ THỐNG

## Các Module Chính

| Module          | URL Prefix                              | Mô tả                      | Package                   |
| --------------- | --------------------------------------- | -------------------------- | ------------------------- |
| Auth Module     | `/login`, `/logout`, `/forgot-password` | Đăng nhập, OTP, phân quyền | `controller.auth`         |
| User Management | `/admin/users`                          | Quản lý người dùng         | `controller.user`         |
| Notification    | `/notifications`                        | Quản lý thông báo          | `controller.notification` |
| Ticket          | `/tickets`                              | Quản lý yêu cầu/sự cố      | `controller.ticket`       |
| Finance         | `/finance`                              | Hóa đơn, công nợ, báo cáo  | `controller.finance`      |
| Payment         | `/payments`                             | Thanh toán                 | `controller.payment`      |
| Building        | `/buildings`                            | Cơ sở, phòng, người thuê   | `controller.building`     |
| Operator        | `/operator`                             | Xử lý sự cố kỹ thuật       | `controller.operator`     |

---

## Kiến trúc Layer

```text
Browser
   ↓
Servlet
   ↓
Service
   ↓
DAO
   ↓
SQL Server
   ↓
DAO
   ↓
Service
   ↓
Servlet
   ↓
JSP
   ↓
Browser
```

---

## Nguyên tắc bắt buộc

### Servlet

Chỉ xử lý:

* Request
* Response

Không xử lý business logic phức tạp.

### Service

Chỉ xử lý:

* Business Logic
* Validation
* Permission Checking
* Transaction

### DAO

Chỉ xử lý:

* SQL
* Mapping dữ liệu

### JSP

Chỉ hiển thị dữ liệu.

Không được:

* Viết SQL
* Gọi DAO
* Viết business logic

### Database

Bắt buộc dùng:

```java
PreparedStatement
```

Không dùng:

* Hibernate
* JPA
* Spring Data

### Framework

Không dùng:

* Spring Boot
* Spring Security

(trừ khi kiến trúc được thay đổi chính thức)

---

# AUTHENTICATION & AUTHORIZATION

## Login Flow

```text
User nhập username/password
        ↓
LoginServlet
        ↓
UserDAO
        ↓
PasswordUtil.verifyBCrypt()
```

### Sai mật khẩu

Nếu sai ≥ 5 lần:

```sql
UPDATE users
SET locked = 1
```

Khóa tài khoản.

---

## Forgot Password Flow

### GET /forgot-password

Hiển thị form email.

### POST /forgot-password

1. Kiểm tra email tồn tại
2. Tạo token
3. Lưu token
4. Thiết lập thời hạn
5. Gửi email reset password

### GET /reset-password

Kiểm tra:

* token tồn tại
* token còn hạn

Nếu hợp lệ:

Hiển thị form đặt lại mật khẩu.

### POST /reset-password

1. Kiểm tra token
2. Kiểm tra xác nhận mật khẩu
3. BCrypt password
4. Update password
5. Hủy token
6. Redirect login

---

## DAO liên quan

### PasswordResetTokenDAO

```java
createToken()
findByToken()
isValidToken()
markAsUsed()
deleteExpiredTokens()
```

### UserDAO

```java
findByEmail()
updatePassword()
```

---

## Login Success

```java
HttpSession session = request.getSession();

session.setAttribute(
    "currentUser",
    userSessionDTO
);
```

---

## Role Redirect

| Role         | Redirect            |
| ------------ | ------------------- |
| ADMIN        | /admin/dashboard    |
| MANAGER      | /manager/dashboard  |
| TENANT       | /tenant/dashboard   |
| OPERATOR     | /operator/dashboard |
---

## Filter

### AuthenticationFilter

Kiểm tra:

* Đã đăng nhập chưa

Nếu chưa:

```text
→ redirect /login
```

### RoleFilter

Kiểm tra:

* Quyền truy cập

Nếu không đủ quyền:

```text
→ /WEB-INF/views/error/403.jsp
```

---

# FINANCE FLOW

## Tạo hóa đơn hàng tháng

Scheduler chạy ngày 1.

```text
MonthlyInvoiceScheduler
        ↓
FinanceService.generateMonthlyInvoices()
        ↓
InvoiceDAO
```

### Hóa đơn gồm

* Phí quản lý
* Phí dịch vụ
* Điện
* Nước
* Phí sửa chữa
* Nợ tháng trước

---

## Thanh toán

```text
PaymentServlet
        ↓
PaymentService
        ↓
PaymentDAO
```

Nếu thanh toán đủ:

```sql
UPDATE invoice
SET status = 'PAID'
```

Gửi thông báo thanh toán thành công.

---

## Sprint 1

Thanh toán:

* Thủ công

Chưa bắt buộc:

* VNPay
* MoMo

---

# TICKET FLOW

## Tạo Ticket

```text
Tenant
    ↓
TicketServlet
    ↓
TicketService
    ↓
TicketDAO
```

Nếu có ảnh:

```text
TicketAttachmentDAO
```

---

## Phân công

```text
Manager
    ↓
TicketService.assignTicket()
```

```sql
status = ASSIGNED
```

---

## Operator xử lý

```text
Operator
    ↓
updateStatus()
```

Các trạng thái:

* IN_PROGRESS
* DONE

---

## Quy tắc

### Tenant

Chỉ xem ticket của mình.

### Manager

Chỉ xem ticket thuộc cơ sở mình quản lý.

### Operator

Chỉ xem ticket được giao.

---

# DEBT FLOW

## Quét công nợ

```text
DebtScheduler
        ↓
FinanceService.checkOverdueInvoices()
```

Điều kiện:

```sql
status = UNPAID
AND due_date < CURRENT_DATE
```

---

## Báo cáo

### FinanceServlet

```text
GET /finance/debts
GET /finance/reports
```

Hiển thị:

* Tổng thu
* Tổng nợ
* Tỷ lệ thanh toán

---

# NOTIFICATION FLOW

## Tạo thông báo

```text
GET /notifications/create
```

Hiển thị form.

---

## Gửi thông báo

```text
POST /notifications/create
```

```text
NotificationService
    ↓
NotificationDAO
```

### Target

* FACILITIES
* ROOMS

---

## Nguyên tắc

Không:

* Hardcode userId
* Query notification từ JSP

Phải lấy:

```java
currentUser
```

từ Session.

---

# TENANT & ROOM MANAGEMENT

## Quản lý người thuê

```text
GET /tenants
```

```text
TenantService
```

---

## Tạo người thuê

```text
POST /tenants/create
```

Các DAO liên quan:

```text
UserDAO
DependentDAO
```

---

## Cập nhật phòng

```sql
UPDATE rooms
SET status =
```

Giá trị:

* OCCUPIED
* VACANT
* MAINTENANCE

---

## Nguyên tắc

Một phòng:

* Có 1 Primary Contact

Người phụ thuộc:

* Không bắt buộc có tài khoản

Không nhân bản dữ liệu cư dân.

---

# TECHNICAL INCIDENT FLOW

## Tiếp nhận sự cố

```text
TicketServlet
OperatorServlet
```

---

## Nhận việc

```text
POST /operator/tickets/accept
```

```sql
status = IN_PROGRESS
```

---

## Hoàn thành

```sql
status = DONE
```

hoặc

```sql
status = CANCELLED
```

---

## Quy tắc

Không cho Tenant:

* sửa trạng thái ticket

Không xóa ticket trực tiếp.

Dùng:

```sql
status = CANCELLED
```

---

# RBAC

## ADMIN

* Cấu hình hệ thống
* Quản lý cơ sở
* Tạo thông báo
* Xem báo cáo doanh thu
* Quản lý nhân sự
* Audit Log

## MANAGER

* Quản lý cơ sở được phân công
* Quản lý phòng
* Quản lý người thuê
* Quản lý ticket
* Tạo thông báo
* Xem công nợ
* Quản lý hóa đơn
* Cập nhậ giá dịch vụ
* Xem thanh toán
* Quản lí người phụ thuộc
* Quản lí hợp đồng

## TENANT

* Xem phòng
* Xem hóa đơn
* Gửi ticket
* Xem thông báo
* Thanh toán

## OPERATOR

* Xem ticket được giao
* Cập nhật ticket
* Cập nhật số điện nước
* Báo cáo sự cố




---

# ADR

## ADR-001

Không dùng Kafka trong Sprint 1.

Thay bằng:

* Service
* ExecutorService

---

## ADR-002

Không dùng Microservice.

Dùng:

```text
Monolithic Application
```

---

## ADR-003

Dùng JDBC + PreparedStatement.

Không dùng:

* Hibernate
* JPA

---

## ADR-004

Realtime không bắt buộc.

Thông báo lưu DB.

WebSocket xem xét sau.

---

## ADR-005

File Upload

Lưu:

```text
/uploads
```

Database chỉ lưu:

* filename
* path
* mime_type
* size
* uploaded_by

Không lưu file trực tiếp vào DB.

---

# PATTERNS

## DAO Pattern

```text
Service
    ↓
DAO
    ↓
Database
```

Không dùng Repository Pattern.

---

## Error Handling

Không hiển thị:

```java
e.getMessage()
```

Hiển thị:

```text
An error occurred.
Please try again later.
```

---

## HTTP Status

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

---

## Transaction Pattern

```java
conn.setAutoCommit(false);

try {
    ...
    conn.commit();
} catch(Exception e) {
    conn.rollback();
}
```

Áp dụng cho:

* Tạo hóa đơn
* Thanh toán
* Tạo ticket
* Tạo phòng + người thuê

---

## Idempotency

Áp dụng cho:

* Thanh toán
* OTP
* Hóa đơn tháng
* Payment Callback

Quy tắc:

* Không tạo hóa đơn trùng
* Không ghi nhận thanh toán trùng
* Có unique constraint nếu cần

---

# FILE STRUCTURE

```text
project-root/
│
├── src/main/java/com/quanlyphongtro/
│   ├── controller/
│   ├── service/
│   ├── service/impl/
│   ├── dao/
│   ├── model/
│   ├── dto/
│   ├── filter/
│   ├── util/
│   ├── exception/
│   ├── constant/
│   └── scheduler/
│
├── src/main/webapp/WEB-INF/views/
│   ├── auth/
│   ├── dashboard/
│   ├── users/
│   ├── hostels/
│   ├── rooms/
│   ├── invoices/
│   ├── payments/
│   ├── tickets/
│   ├── notifications/
│   ├── finance/
│   ├── assignments/
│   └── error/
│
├── database/
│   ├── schema.sql
│   ├── seed.sql
│   └── migration/
│
├── docs/specs/
│
├── uploads/
│
├── pom.xml
├── README.md
├── AGENTS.md
└── CLAUDE.md
```
