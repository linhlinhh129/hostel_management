# PLAN: Kế hoạch Thực thi Cấu hình hệ thống

**Status:** Planning  
**Date:** 2026-07-07  
**Priority:** Medium  
**Estimated Duration:** 3-4 tuần

---

## 1. Tổng quan Giải pháp

Feature Cấu hình hệ thống cho phép Admin cập nhật thông tin kết nối Email SMTP và VNPay trực tiếp trên giao diện, không cần chỉnh sửa file cấu hình hay restart server.

**Vấn đề kiến trúc cốt lõi cần giải quyết:**

Hiện tại `EmailService` và `VNPayConfig` load cấu hình một lần duy nhất trong `static` block khi JVM khởi động — hoàn toàn bất biến sau đó. Cần chuyển sang cơ chế đọc động từ DB để cấu hình mới có hiệu lực ngay sau khi Admin lưu.

**Kiến trúc giải pháp:**
- **Database:** Bảng `system_config` lưu cặp key-value cho Email và VNPay.
- **DAO:** `SystemConfigDAO` cung cấp `get()` / `set()` theo key.
- **Service:** `SystemConfigService` xử lý validation, mask.
- **Servlet:** `AdminSystemConfigServlet` xử lý `GET /admin/system-config`, `POST /admin/system-config/email`, `POST /admin/system-config/vnpay`.
- **Refactor:** `EmailService` và `VNPayConfig` đọc cấu hình từ `SystemConfigDAO` thay vì `ResourceBundle` / `Properties` tĩnh.
- **JSP:** `system-config.jsp` hiển thị form cập nhật Email và VNPay, mask các trường nhạy cảm.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Database (Tuần 1)

**Mục tiêu:** Thiết kế schema, migration script, DAO.

**Công việc:**
- Thiết kế bảng `system_config` (key, value, updatedAt, updatedBy).
- Viết migration script tạo bảng và seed dữ liệu từ `.properties` hiện tại.
- Implement `SystemConfigDAO` với các method `getValue(key)`, `setValue(key, value, userId)`, `getEmailConfig()`, `getVNPayConfig()`.
- Viết unit test cho `SystemConfigDAO`.

**Deliverables:**
- Migration script `system_config` table.
- `SystemConfigDAO.java` hoàn chỉnh.

**Risks:**
- Seed dữ liệu từ `.properties` phải đúng key mapping — cần verify kỹ trước khi chạy.

---

### Giai đoạn 2: Backend — Service & Servlet (Tuần 2)

**Mục tiêu:** Implement business logic, servlet, refactor EmailService và VNPayConfig.

**Công việc:**
- Implement `SystemConfigService` / `SystemConfigServiceImpl`:
  - `getEmailConfig()` — đọc 5 key Email, mask password.
  - `getVNPayConfig()` — đọc 5 key VNPay, mask secretKey.
  - `updateEmailConfig(params, userId)` — validate, lưu DB.
  - `updateVNPayConfig(params, userId)` — validate, lưu DB.
- Implement `AdminSystemConfigServlet`:
  - `doGet()` — load cả hai config, set attribute, forward JSP.
  - `doPost()` — phân nhánh theo action (`email` / `vnpay`), gọi service, redirect hoặc forward.
- Refactor `EmailService`: bỏ `static` block, đọc config từ `SystemConfigDAO` mỗi lần `getEmailSession()`.
- Refactor `VNPayConfig`: bỏ `static final`, cung cấp static method `getConfig(key)` đọc từ `SystemConfigDAO`.
- Đăng ký URL mapping trong `web.xml`.

**Deliverables:**
- `SystemConfigServiceImpl.java` hoàn chỉnh.
- `AdminSystemConfigServlet.java` hoàn chỉnh.
- `EmailService.java` và `VNPayConfig.java` đã refactor.

**Risks:**
- Refactor `EmailService` / `VNPayConfig` có thể ảnh hưởng đến các servlet đang dùng (`TenantPaymentServlet`, `FirstLoginServlet`, `AdminPersonnelServlet`...) — cần kiểm tra kỹ.
- Nếu DB chậm, mỗi lần gửi email sẽ chậm hơn — cân nhắc cache ngắn (30 giây).

---

### Giai đoạn 3: Frontend — JSP (Tuần 3)

**Mục tiêu:** Implement giao diện trang cấu hình hệ thống.

**Công việc:**
- Tạo `system-config.jsp` trong `/WEB-INF/views/admin/`.
- Form Email: 5 trường input (host, port, username, password, from) — password field, mask placeholder.
- Form VNPay: 5 trường input (payUrl, returnUrl, tmnCode, secretKey, apiUrl) — secretKey field, mask placeholder.
- Hiển thị `successMessage` / `errorMessage` dạng Bootstrap alert.
- Thêm link điều hướng vào sidebar Admin.
- Client-side validation cơ bản (required, port là số).

**Deliverables:**
- `system-config.jsp` hoàn chỉnh, responsive Bootstrap 5.
- Sidebar Admin đã có link đến `/admin/system-config`.

---

### Giai đoạn 4: Kiểm thử & Hoàn thiện (Tuần 4)

**Mục tiêu:** Test toàn bộ luồng, kiểm tra bảo mật, hoàn thiện tài liệu.

**Công việc:**
- Test luồng happy path: Admin cập nhật Email → gửi email thử → xác nhận email đi được.
- Test luồng happy path: Admin cập nhật VNPay → tạo giao dịch → xác nhận URL hợp lệ.
- Test edge case: submit form thiếu trường → hiển thị lỗi đúng.
- Test phân quyền: MANAGER / OPERATOR / chưa đăng nhập → bị chặn đúng.
- Kiểm tra secret/password không bị log ra console hay response.
- Performance: thời gian lưu cấu hình < 500 ms.

**Deliverables:**
- Test report.
- SPEC.md cập nhật nếu phát hiện sai lệch.

---

## 3. Dependencies & Constraints

### Internal Dependencies
- `AuditLogDAO` — đã có sẵn, **không dùng** cho feature này.
- `DatabaseUtil` — connection pool đã có sẵn.
- Các servlet đang dùng `EmailService` và `VNPayConfig` (`TenantPaymentServlet`, `TenantPaymentReturnServlet`, `AdminPersonnelServlet`, `FirstLoginServlet`...) — cần test lại sau khi refactor.

### Technical Constraints
- Không dùng Spring, Hibernate, JWT — thuần Jakarta Servlet + JDBC.
- Không lưu secret/password dạng plain text vào log.
- Không hiển thị giá trị thật của password / secretKey trên JSP.
- Bắt buộc dùng `PreparedStatement` cho mọi thao tác DB.

---

## 4. Success Criteria

### Functional
- ✓ Admin xem được cấu hình Email và VNPay hiện tại (masked).
- ✓ Admin cập nhật cấu hình Email — email tiếp theo dùng config mới ngay, không restart.
- ✓ Admin cập nhật cấu hình VNPay — giao dịch tiếp theo dùng config mới ngay, không restart.
- ✓ Submit thiếu trường bắt buộc → hiển thị lỗi, không lưu.
- ✓ MANAGER / OPERATOR / chưa đăng nhập không truy cập được.

### Non-Functional
- ✓ Thời gian lưu cấu hình < 500 ms (P95).
- ✓ Secret / password không xuất hiện trong log hay HTML response.
- ✓ Không gây regression cho EmailService và VNPayConfig.

---

## 5. Risk Management

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Refactor EmailService / VNPayConfig gây regression | High | High | Test kỹ tất cả servlet dùng hai class này trước khi merge |
| DB chậm làm chậm gửi email | Medium | Medium | Cache config trong memory 30-60 giây |
| Secret key lộ trong log | Medium | High | Kiểm tra toàn bộ log statement, không log raw value |
| Migration seed sai key mapping | Low | High | Verify key mapping với `.properties` trước khi chạy |
| Race condition khi cập nhật VNPay lúc có giao dịch đang xử lý | Low | Medium | Ghi nhận là known limitation, xử lý trong sprint tiếp theo |

---

## 6. Timeline Overview

```
Tuần 1: [===] Database Schema + SystemConfigDAO
Tuần 2: [======] Service + Servlet + Refactor EmailService/VNPayConfig
Tuần 3: [====] JSP + Sidebar
Tuần 4: [===] Testing + Hoàn thiện
```

**Go-Live Date:** Cuối tuần 4 (dự kiến)
