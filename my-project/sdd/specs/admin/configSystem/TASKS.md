# TASKS: Phân chia Chi tiết Đầu Việc — Cấu hình hệ thống

**Total Story Points:** ~38 points  
**Sprint Duration:** 2 tuần × 2 sprints = 4 tuần  
**Velocity:** ~19 points/sprint

---

## Epic 1: Database & DAO (10 points)

### Task 1.1: Thiết kế và tạo bảng `system_config` (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 ngày  
**Description:**
- Thiết kế bảng `system_config` với các cột:
  - `config_key` VARCHAR(100) PRIMARY KEY
  - `config_value` NVARCHAR(500) NOT NULL
  - `updated_at` DATETIME2 DEFAULT GETDATE()
  - `updated_by` INT NULL (FK → `users.id`)
- Viết migration script tạo bảng.
- Viết seed script chuyển toàn bộ giá trị từ `email.properties` và `vnpay.properties` hiện tại vào bảng.

**Key mapping seed:**

| config_key | Nguồn gốc |
|---|---|
| `email.host` | `email.properties` → `email.host` |
| `email.port` | `email.properties` → `email.port` |
| `email.username` | `email.properties` → `email.username` |
| `email.password` | `email.properties` → `email.password` |
| `email.from` | `email.properties` → `email.from` |
| `vnpay.payUrl` | `vnpay.properties` → `vnpay.payUrl` |
| `vnpay.returnUrl` | `vnpay.properties` → `vnpay.returnUrl` |
| `vnpay.tmnCode` | `vnpay.properties` → `vnpay.tmnCode` |
| `vnpay.secretKey` | `vnpay.properties` → `vnpay.secretKey` |
| `vnpay.apiUrl` | `vnpay.properties` → `vnpay.apiUrl` |

**Acceptance Criteria:**
- ✓ Bảng tạo thành công trên SQL Server 2022.
- ✓ Seed script chạy không lỗi, đủ 10 key.
- ✓ FK `updated_by` → `users.id` hợp lệ (nullable).

**Dependencies:** Không có.

---

### Task 1.2: Implement `SystemConfigDAO` (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 ngày  
**Description:**
- Tạo `SystemConfigDAO.java` trong package `com.quanlyphongtro.dao`.
- Implement các method:
  - `String getValue(String key)` — trả null nếu không tìm thấy key.
  - `void setValue(String key, String value, Integer updatedBy)` — upsert (INSERT nếu chưa có, UPDATE nếu đã có).
  - `Map<String, String> getEmailConfig()` — trả Map chứa 5 key email.
  - `Map<String, String> getVNPayConfig()` — trả Map chứa 5 key vnpay.
- Dùng `PreparedStatement` + try-with-resources.
- Kế thừa hoặc dùng `DatabaseUtil.getConnection()`.

**Acceptance Criteria:**
- ✓ `getValue` trả đúng giá trị theo key.
- ✓ `setValue` upsert đúng, cập nhật `updated_at` và `updated_by`.
- ✓ `getEmailConfig` và `getVNPayConfig` trả đủ 5 key tương ứng.
- ✓ Không có SQL nối chuỗi, không có SQL injection.

**Dependencies:** Task 1.1.

---

### Task 1.3: Refactor `EmailService` — đọc config động từ DB (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 ngày  
**Description:**
- Xóa `static` block load từ `email.properties`.
- Sửa `getEmailSession()` để gọi `SystemConfigDAO.getEmailConfig()` mỗi lần (hoặc cache TTL 30s).
- Đảm bảo các method `sendTempPassword()` và `sendResetLink()` vẫn hoạt động đúng.
- Kiểm tra toàn bộ caller của `EmailService` (`AdminPersonnelServlet`, `FirstLoginServlet`...) không bị regression.

**Acceptance Criteria:**
- ✓ `EmailService` không còn `static` block load `.properties`.
- ✓ Thay đổi config trong DB → email gửi đi dùng config mới ngay, không restart.
- ✓ Các test thủ công gửi email vẫn thành công sau refactor.

**Dependencies:** Task 1.2.

---

## Epic 2: Backend — Service & Servlet (16 points)

### Task 2.1: Refactor `VNPayConfig` — đọc config động từ DB (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 ngày  
**Description:**
- Xóa các field `static final` load từ `ResourceBundle`.
- Cung cấp static method `get(String key)` gọi `SystemConfigDAO.getValue(key)` (hoặc cache TTL 30s).
- Cập nhật tất cả caller của `VNPayConfig` (`TenantPaymentServlet`, `TenantPaymentReturnServlet`...) dùng `VNPayConfig.get("vnpay.tmnCode")` thay vì field trực tiếp.

**Acceptance Criteria:**
- ✓ `VNPayConfig` không còn `static final` load từ `ResourceBundle`.
- ✓ Thay đổi config trong DB → giao dịch tiếp theo dùng config mới ngay.
- ✓ Các caller đã cập nhật, không compile error.

**Dependencies:** Task 1.2.

---

### Task 2.2: Implement `SystemConfigService` (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 ngày  
**Description:**
- Tạo interface `SystemConfigService.java` và `SystemConfigServiceImpl.java`.
- Implement `getEmailConfig()`:
  - Gọi `SystemConfigDAO.getEmailConfig()`.
  - Map dữ liệu sang `EmailConfigDTO` — **không đưa `email.password` vào DTO**.
  - Set `updatedAt`, `updatedBy` từ metadata của key `email.host` (key đại diện).
  - Trả về `EmailConfigDTO`.
- Implement `getVNPayConfig()`:
  - Gọi `SystemConfigDAO.getVNPayConfig()`.
  - Map dữ liệu sang `VNPayConfigDTO` — **không đưa `vnpay.secretKey` vào DTO**.
  - Trả về `VNPayConfigDTO`.
- Implement `updateEmailConfig(Map<String, String> params, Integer userId)`:
  - Validate: `host`, `port`, `username`, `from` không được rỗng. (Lưu ý `password` có thể rỗng).
  - Validate: `port` phải là số nguyên dương.
  - Gọi `SystemConfigDAO.setValue()` cho từng key.
- Implement `updateVNPayConfig(Map<String, String> params, Integer userId)`:
  - Validate: `payUrl`, `returnUrl`, `tmnCode`, `apiUrl` không được rỗng. (Lưu ý `secretKey` có thể rỗng).
  - Gọi `SystemConfigDAO.setValue()` cho từng key.
- Throw `ValidationException` khi validate thất bại.

**Acceptance Criteria:**
- ✓ Validation đúng cho cả Email và VNPay.
- ✓ `ValidationException` được throw khi thiếu trường.
- ✓ Password / secretKey không bị ghi vào bất kỳ log nào dạng plain text.

**Dependencies:** Task 1.2, Task 1.3, Task 2.1.

---

### Task 2.3: Implement `AdminSystemConfigServlet` (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 ngày  
**Description:**
- Tạo `AdminSystemConfigServlet.java` trong `com.quanlyphongtro.controller.admin`.
- Kế thừa `BaseServlet`, annotate `@WebServlet(urlPatterns = {"/admin/system-config", "/admin/system-config/email", "/admin/system-config/vnpay"})`.
- Implement `doGet("/admin/system-config")`:
  - Kiểm tra role ADMIN (BaseServlet).
  - Gọi `SystemConfigService.getEmailConfig()` và `getVNPayConfig()`.
  - Set attribute `emailConfig`, `vnpayConfig`, xử lý query param `success`.
  - Forward `/WEB-INF/views/admin/system-config.jsp`.
- Implement `doPost("/admin/system-config/email")`:
  - Đọc 5 tham số Email từ request.
  - Gọi `SystemConfigService.updateEmailConfig()`.
  - Thành công: redirect `/admin/system-config?success=email_updated`.
  - `ValidationException`: set `errorMessage`, forward lại JSP.
  - Exception chung: log ERROR, set `errorMessage` chung, forward lại JSP.
- Implement `doPost("/admin/system-config/vnpay")`:
  - Tương tự doPost Email nhưng cho VNPay.
  - Thành công: redirect `/admin/system-config?success=vnpay_updated`.

**Acceptance Criteria:**
- ✓ GET hiển thị đúng form với giá trị hiện tại (masked).
- ✓ POST Email: lưu thành công → redirect kèm success param.
- ✓ POST VNPay: lưu thành công → redirect kèm success param.
- ✓ Validate thất bại → forward lại JSP kèm `errorMessage`.
- ✓ Exception không lộ stack trace ra giao diện.

**Dependencies:** Task 2.2.

---

### Task 2.4: Đăng ký URL mapping trong `web.xml` (1 point)
**Assignee:** Backend Developer  
**Duration:** 30 phút  
**Description:**
- Thêm `<servlet>` và `<servlet-mapping>` cho `AdminSystemConfigServlet` trong `web.xml`.
- Hoặc dùng `@WebServlet` annotation trực tiếp nếu project đang dùng annotation-based config.
- Kiểm tra `RoleFilter` đã bao phủ pattern `/admin/*`.

**Acceptance Criteria:**
- ✓ `GET /admin/system-config` trả về JSP đúng.
- ✓ `POST /admin/system-config/email` và `/vnpay` được route đúng servlet.
- ✓ MANAGER / OPERATOR / chưa đăng nhập bị chặn bởi filter.

**Dependencies:** Task 2.3.

---

### Task 2.5: Implement `EmailConfigDTO` và `VNPayConfigDTO` (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 ngày  
**Description:**
- Tạo `EmailConfigDTO.java` trong `com.quanlyphongtro.dto`.
  - Các field: `host` (String), `port` (String), `username` (String), `from` (String), `updatedAt` (String), `updatedBy` (String).
  - **Không có field `password`** — UI luôn hiển thị placeholder `••••••••`, không pre-fill giá trị thật.
- Tạo `VNPayConfigDTO.java` trong `com.quanlyphongtro.dto`.
  - Các field: `payUrl` (String), `returnUrl` (String), `tmnCode` (String), `apiUrl` (String), `updatedAt` (String), `updatedBy` (String).
  - **Không có field `secretKey`** — UI luôn hiển thị placeholder `••••••••`, không pre-fill giá trị thật.

**Acceptance Criteria:**
- ✓ `EmailConfigDTO` có đủ 6 field theo SPEC 4.5, không có `password`.
- ✓ `VNPayConfigDTO` có đủ 6 field theo SPEC 4.6, không có `secretKey`.
- ✓ Không có getter nào trả raw value của trường nhạy cảm.

**Dependencies:** Task 1.2.

---

## Epic 3: Frontend — JSP (8 points)

### Task 3.1: Tạo `system-config.jsp` (5 points)
**Assignee:** Backend Developer / Frontend  
**Duration:** 2-3 ngày  
**Description:**
- Tạo file `/WEB-INF/views/admin/system-config.jsp`.
- Layout theo chuẩn Bootstrap 5, dùng include `head.jsp`, sidebar Admin.
- **Section Email SMTP:**
  - Form `POST /admin/system-config/email`.
  - 5 input: host (text), port (number), username (text), password (**type="password"**, placeholder `••••••••`), from (text).
  - Pre-fill từ `${emailConfig}` (password luôn hiện placeholder, không pre-fill).
  - Button "Lưu cấu hình Email".
- **Section VNPay:**
  - Form `POST /admin/system-config/vnpay`.
  - 5 input: payUrl (text), returnUrl (text), tmnCode (text), secretKey (**type="password"**, placeholder `••••••••`), apiUrl (text).
  - Pre-fill từ `${vnpayConfig}` (secretKey luôn hiện placeholder, không pre-fill).
  - Button "Lưu cấu hình VNPay".
- Alert thành công khi `${successMessage}` không rỗng (Bootstrap `alert-success`).
- Alert lỗi khi `${errorMessage}` không rỗng (Bootstrap `alert-danger`).
- CSRF token hidden input trong mỗi form.
- Client-side validation: required và port là số nguyên dương.

**Acceptance Criteria:**
- ✓ Hai form hiển thị đúng dữ liệu pre-fill (không lộ password / secretKey thật).
- ✓ Alert thành công / lỗi hiển thị đúng.
- ✓ Responsive trên mobile và desktop.
- ✓ Không có scriptlet Java trong JSP, dùng JSTL/EL.

**Dependencies:** Task 2.3.

---

### Task 3.2: Thêm link điều hướng vào Sidebar Admin (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 ngày  
**Description:**
- Mở file sidebar/layout Admin.
- Thêm mục "Cấu hình hệ thống" với icon phù hợp, trỏ đến `/admin/system-config`.
- Active state khi URL hiện tại là `/admin/system-config`.

**Acceptance Criteria:**
- ✓ Link xuất hiện đúng vị trí trong sidebar Admin.
- ✓ Active state highlight khi ở trang cấu hình.
- ✓ Không hiển thị link này cho MANAGER / OPERATOR.

**Dependencies:** Task 3.1.

---

### Task 3.3: UI Polish — Thông báo & UX (1 point)
**Assignee:** Backend Developer  
**Duration:** 0.5 ngày  
**Description:**
- Kiểm tra auto-dismiss alert sau 5 giây (JS).
- Thêm icon ổ khóa trước label các trường password / secretKey để nhắc nhở là trường nhạy cảm.
- Tooltip hoặc helper text cho các trường (VD: "Nhập App Password của Gmail, không phải mật khẩu đăng nhập").
- Disable button "Lưu" trong khi đang submit (tránh double-submit).

**Acceptance Criteria:**
- ✓ Alert tự ẩn sau 5 giây.
- ✓ Trường nhạy cảm có dấu hiệu nhận biết rõ ràng.
- ✓ Button không cho submit lần hai khi đang xử lý.

**Dependencies:** Task 3.1.

---

## Epic 4: Kiểm thử (4 points)

### Task 4.1: Test Backend — Service và DAO (2 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 ngày  
**Description:**
- Test `SystemConfigDAO.getValue()` / `setValue()` với dữ liệu thật trên DB test.
- Test `SystemConfigService.updateEmailConfig()`: thiếu `host` → `ValidationException`.
- Test `SystemConfigService.updateEmailConfig()`: `port = "abc"` → `ValidationException`.
- Test `SystemConfigService.updateVNPayConfig()`: thiếu `secretKey` → `ValidationException`.
- Test `EmailService` dùng config mới sau khi DB thay đổi (không restart).
- Test `VNPayConfig.get()` trả đúng giá trị mới sau khi DB thay đổi.

**Acceptance Criteria:**
- ✓ Tất cả trường hợp trên pass.
- ✓ Không có giá trị nhạy cảm trong log output khi chạy test.

**Dependencies:** Task 2.2, Task 1.3, Task 2.1.

---

### Task 4.2: Test End-to-End — Luồng Admin cập nhật cấu hình (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 ngày  
**Description:**
- Luồng 1: Admin đăng nhập → vào Cấu hình hệ thống → cập nhật Email → gửi email thử → xác nhận email đến đúng.
- Luồng 2: Admin cập nhật VNPay → tạo hóa đơn → bấm thanh toán → URL redirect đúng `returnUrl` mới.
- Luồng 3: Submit form thiếu `host` → alert lỗi, form không lưu, DB không đổi.
- Luồng 4: MANAGER đăng nhập → truy cập `/admin/system-config` → nhận 403.

**Acceptance Criteria:**
- ✓ Cả 4 luồng pass.
- ✓ Không có lỗi 500 trong quá trình test.
- ✓ Secret / password không lộ trong HTML source của trang.

**Dependencies:** Task 3.1, Task 3.2.

---

## Tổng hợp theo Sprint

| Sprint | Tuần | Points | Nội dung |
|---|---|---|---|
| Sprint 1 | Tuần 1-2 | 20 | DB schema, SystemConfigDAO, Refactor EmailService/VNPayConfig, SystemConfigDTO, SystemConfigService |
| Sprint 2 | Tuần 3-4 | 18 | AdminSystemConfigServlet, web.xml, system-config.jsp, Sidebar, UI Polish, Testing |

---

## Dependency Graph

```
Task 1.1 (Tạo bảng system_config)
  ↓
Task 1.2 (SystemConfigDAO)
  ├→ Task 1.3 (Refactor EmailService)
  ├→ Task 2.1 (Refactor VNPayConfig)
  └→ Task 2.5 (EmailConfigDTO + VNPayConfigDTO)
       ↓
Task 2.2 (SystemConfigService)  ← phụ thuộc 1.2, 1.3, 2.1
  ↓
Task 2.3 (AdminSystemConfigServlet)
  ↓
Task 2.4 (web.xml mapping)
  ↓
Task 3.1 (system-config.jsp)
  ↓
Task 3.2 (Sidebar link)
  ↓
Task 3.3 (UI Polish)

Task 2.2 → Task 4.1 (Test Backend)
Task 3.1 → Task 4.2 (Test E2E)
```

---

## Prioritization Guidelines

**Must Have (P0):**
- Task 1.1 — 1.3 (DB + DAO + Refactor EmailService)
- Task 2.1 — 2.3 (Refactor VNPayConfig + Service + Servlet)
- Task 3.1 (JSP)

**Should Have (P1):**
- Task 2.4 (web.xml)
- Task 2.5 (DTO)
- Task 3.2 (Sidebar)
- Task 4.1 — 4.2 (Testing)

**Nice to Have (P2):**
- Task 3.3 (UI Polish)
