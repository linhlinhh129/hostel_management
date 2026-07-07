# Feature: Cấu hình hệ thống

**Status:** Draft  
**Author:** Nhóm phát triển  
**Reviewer:** Tech Lead  
**Date:** 2026-07-07  
**Priority:** Medium

---

## 1. Business Context

Tính năng **Cấu hình hệ thống** cho phép Quản trị viên quản lý và cập nhật các thông số cấu hình quan trọng của hệ thống, bao gồm cấu hình dịch vụ gửi Email (SMTP) và cổng thanh toán VNPay. Khi thông tin kết nối hoặc tài khoản của các dịch vụ này thay đổi, Quản trị viên có thể cập nhật trực tiếp trên giao diện quản trị mà không cần chỉnh sửa mã nguồn hoặc triển khai lại hệ thống.

Tính năng này giúp hệ thống dễ dàng bảo trì, đảm bảo các chức năng gửi email và thanh toán luôn hoạt động ổn định, đồng thời giảm thời gian và chi phí vận hành khi thay đổi thông tin tích hợp với các dịch vụ bên thứ ba.

---

## 2. User Stories

### Story 1 (Happy Path)

**Là Quản trị viên**, tôi muốn cập nhật cấu hình Email SMTP để hệ thống có thể sử dụng thông tin Email mới cho việc gửi thông báo đến người dùng.

### Story 2 (Happy Path)

**Là Quản trị viên**, tôi muốn cập nhật cấu hình VNPay để hệ thống sử dụng thông tin Merchant mới khi tạo mã QR và xử lý thanh toán.

### Story 3 (Edge Case)

**Là Quản trị viên**, khi nhập thiếu hoặc sai thông tin cấu hình Email hoặc VNPay, tôi muốn hệ thống thông báo lỗi và không lưu dữ liệu để tránh ảnh hưởng đến hoạt động của hệ thống.

---

## 3. Acceptance Criteria (EARS)

### Cấu hình Email

- **WHEN** Quản trị viên gửi biểu mẫu cấu hình Email với dữ liệu hợp lệ  
  **THE SYSTEM SHALL** lưu cấu hình mới và áp dụng cho tất cả email được gửi sau đó.

- **WHEN** Quản trị viên để trống bất kỳ trường bắt buộc nào (`host`, `port`, `username`, `password`, `from`)  
  **THE SYSTEM SHALL** hiển thị thông báo lỗi và không lưu cấu hình.

- **WHEN** Quản trị viên nhập `port` không phải số nguyên hoặc ngoài khoảng 1–65535  
  **THE SYSTEM SHALL** hiển thị thông báo lỗi và không lưu cấu hình.

### Cấu hình VNPay

- **WHEN** Quản trị viên gửi biểu mẫu cấu hình VNPay với dữ liệu hợp lệ  
  **THE SYSTEM SHALL** lưu cấu hình mới và sử dụng cho các giao dịch thanh toán tiếp theo.

- **WHEN** Quản trị viên để trống bất kỳ trường bắt buộc nào (`payUrl`, `returnUrl`, `tmnCode`, `secretKey`, `apiUrl`)  
  **THE SYSTEM SHALL** hiển thị thông báo lỗi và không lưu cấu hình.

### Phân quyền

- **WHILE** người dùng hiện tại không phải Quản trị viên  
  **THE SYSTEM SHALL** từ chối truy cập chức năng Cấu hình hệ thống.

---

## 4. Servlet Contract

### 4.1 Servlet Entry Points

| Thuộc tính | GET (Hiển thị) | POST Email | POST VNPay |
|---|---|---|---|
| **Servlet** | `AdminSystemConfigServlet` | `AdminSystemConfigServlet` | `AdminSystemConfigServlet` |
| **URL Pattern** | `GET /admin/system-config` | `POST /admin/system-config/email` | `POST /admin/system-config/vnpay` |
| **Kết quả** | Forward → `system-config.jsp` | Redirect → `?success=email_updated` | Redirect → `?success=vnpay_updated` |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `BaseServlet`) | Role = `ADMIN` | Role = `ADMIN` |

---

### 4.2 Request Parameters — POST /admin/system-config/email

| Tham số | Kiểu | Bắt buộc | Mô tả |
|---|---|:---:|---|
| `host` | `String` | ✔ | Địa chỉ SMTP server (VD: `smtp.gmail.com`) |
| `port` | `int` | ✔ | Cổng SMTP (VD: `587`) |
| `username` | `String` | ✔ | Tài khoản Email dùng để gửi |
| `password` | `String` | ✔ | Mật khẩu ứng dụng (App Password) |
| `from` | `String` | ✔ | Địa chỉ Email hiển thị trong trường "Từ:" |

---

### 4.3 Request Parameters — POST /admin/system-config/vnpay

| Tham số | Kiểu | Bắt buộc | Mô tả |
|---|---|:---:|---|
| `payUrl` | `String` | ✔ | URL cổng thanh toán VNPay |
| `returnUrl` | `String` | ✔ | URL nhận kết quả sau thanh toán |
| `tmnCode` | `String` | ✔ | Mã Merchant (Terminal ID) |
| `secretKey` | `String` | ✔ | Khóa bí mật ký giao dịch |
| `apiUrl` | `String` | ✔ | URL API truy vấn giao dịch |

---

### 4.4 Request Attributes — GET /admin/system-config

Servlet set các attribute sau trước khi forward sang JSP:

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `emailConfig` | `EmailConfigDTO` | `SystemConfigDAO.getEmailConfig()` | Cấu hình Email hiện tại (password không có trong DTO — luôn mask ở UI) |
| `vnpayConfig` | `VNPayConfigDTO` | `SystemConfigDAO.getVNPayConfig()` | Cấu hình VNPay hiện tại (secretKey không có trong DTO — luôn mask ở UI) |
| `successMessage` | `String` | Query param `success` | Thông báo sau redirect thành công (`email_updated` / `vnpay_updated`) |
| `errorMessage` | `String` | Flash attribute | Thông báo lỗi khi lưu thất bại |

---

### 4.5 EmailConfigDTO

| Field | Type | Mô tả |
|---|---|---|
| `host` | `String` | Địa chỉ SMTP server |
| `port` | `String` | Cổng SMTP |
| `username` | `String` | Tài khoản Email dùng để gửi |
| `from` | `String` | Địa chỉ Email hiển thị trong trường "Từ:" |
| `updatedAt` | `String` | Thời điểm cập nhật gần nhất, định dạng `"dd/MM/yyyy HH:mm"` |
| `updatedBy` | `String` | Tên người cập nhật gần nhất (fallback: `"Hệ thống"`) |

> `password` **không có trong DTO** — UI luôn hiển thị placeholder `••••••••`, không pre-fill giá trị thật.

---

### 4.6 VNPayConfigDTO

| Field | Type | Mô tả |
|---|---|---|
| `payUrl` | `String` | URL cổng thanh toán VNPay |
| `returnUrl` | `String` | URL nhận kết quả sau thanh toán |
| `tmnCode` | `String` | Mã Merchant (Terminal ID) |
| `apiUrl` | `String` | URL API truy vấn giao dịch |
| `updatedAt` | `String` | Thời điểm cập nhật gần nhất, định dạng `"dd/MM/yyyy HH:mm"` |
| `updatedBy` | `String` | Tên người cập nhật gần nhất (fallback: `"Hệ thống"`) |

> `secretKey` **không có trong DTO** — UI luôn hiển thị placeholder `••••••••`, không pre-fill giá trị thật.

---

### 4.7 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| Thiếu hoặc rỗng trường bắt buộc | Forward lại `system-config.jsp` kèm `errorMessage` — không lưu |
| `port` không phải số nguyên hợp lệ | Forward lại `system-config.jsp` kèm `errorMessage` |
| `port` ngoài khoảng 1–65535 | Forward lại `system-config.jsp` kèm `errorMessage` |
| Lỗi ghi DB | Log `ERROR`, forward lại `system-config.jsp` kèm `errorMessage` chung |
| Cập nhật thành công | Redirect `GET /admin/system-config?success=email_updated` hoặc `?success=vnpay_updated` |

---

## 5. Technical Constraints

- Chỉ Quản trị viên (Admin) mới được phép truy cập chức năng.
- Các trường bắt buộc phải được kiểm tra trước khi lưu. `port` phải là số nguyên trong khoảng 1–65535.
- `password` (Email) và `secretKey` (VNPay) không được có mặt trong DTO trả về JSP — UI luôn hiển thị placeholder, không pre-fill giá trị thật.
- Cấu hình được lưu vào bảng `system_config` trong database. `EmailService` và `VNPayConfig` **không được dùng `static` initializer** để load config — phải đọc từ DB mỗi lần sử dụng để đảm bảo cấu hình mới có hiệu lực ngay sau khi Admin lưu, không cần restart server.
- Thời gian phản hồi khi lưu cấu hình không vượt quá **500 ms (p95)** (không bao gồm thời gian kết nối tới SMTP hoặc VNPay).

---

## 6. Out of Scope

- Cấu hình các cổng thanh toán khác (MoMo, PayOS, ZaloPay,...).
- Quản lý lịch sử thay đổi cấu hình.
- Khôi phục cấu hình về phiên bản trước.
- Đồng bộ cấu hình giữa nhiều máy chủ.
- Tự động kiểm tra trạng thái hoạt động của Email hoặc VNPay.