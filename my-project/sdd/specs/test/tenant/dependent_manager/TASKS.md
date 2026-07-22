# Tasks: Dependent Management (Test Strategy - 3 Tiers)

**Input**: Design documents from `/specs/test/tenant/dependent_manager/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md

---

## Tier 1: Unit Tests (Phase 1-7)

✅ **Trạng thái**: ĐÃ HOÀN THÀNH (`DependentServiceImplTest.java`)
- Hệ thống đã cover 100% các tiêu chí logic ở tầng Service (Mock DAO).

---

## Tier 2: Integration Tests (Testcontainers & REST Assured)

**Purpose**: Đảm bảo các câu lệnh SQL viết tay trong `DependentDAO` đúng cú pháp và Servlet xử lý đúng mã lỗi HTTP.

### Setup Database Test
- [ ] T020 Tạo file `src/test/resources/init-test-db.sql` chứa script `CREATE TABLE` và `INSERT` dữ liệu mẫu (2 Tenants, 3 Dependents, 1 deleted).
- [ ] T021 Tạo class `BaseTestContainer.java` cấu hình MS SQL Server Docker instance dùng chung.

### DAO Integration (SQL Verification)
- [ ] T022 [P] Viết `DependentDAOIT.java` test hàm `findByTenantId`. Đảm bảo trả về đúng dữ liệu, đúng trật tự và bỏ qua bản ghi soft-delete.
- [ ] T023 [P] Viết test cho `findByIdAndTenantId`. Đảm bảo trả về Optional.empty() nếu sai ID hoặc bị xóa.
- [ ] T024 [P] Viết test cho hàm `softDelete`. Đảm bảo cột `deletedAt` được cập nhật thay vì xóa vật lý (DELETE).

### Servlet/API Integration (HTTP Code Verification)
- [ ] T025 Viết `DependentServletIT.java` dùng REST Assured hoặc MockHttpServletRequest.
- [ ] T026 [P] Gửi request với Session giả mạo để test lỗi HTTP 403 Forbidden.
- [ ] T027 [P] Gửi request không có Session để test HTTP 401 Unauthorized.

---

## Tier 3: End-to-End Tests (Selenium WebDriver)

**Purpose**: Đóng vai trò là người dùng cuối, mở trình duyệt và tương tác trực tiếp với giao diện HTML/JSP để kiểm thử tích hợp toàn bộ hệ thống.

### Cấu hình WebDriver
- [ ] T028 Xóa bỏ cấu hình/files Cucumber cũ (`features/`, `DependentSteps.java`, `RunCucumberTest.java`) nếu có.
- [ ] T029 Cài đặt `WebDriverManager.chromedriver().setup()` trong base setup để tự động tải driver.

### Triển khai Code (JUnit 5 + Selenium)
- [ ] T030 Tạo class `DependentViewE2ETest.java` trong thư mục `com.quanlyphongtro.e2e`.
- [ ] T031 Viết setup khởi tạo `ChromeDriver` (hỗ trợ headless).
- [ ] T032 Viết `@Test` kiểm tra luồng Đăng nhập (Điền form và click button).
- [ ] T033 Bổ sung `WebDriverWait` (chờ trang `/dashboard`) để xử lý độ trễ phản hồi của Servlet.
- [ ] T034 Điều hướng sang trang `dependents` và kiểm tra logic hiển thị CCCD bị che mờ (`001******001`).

### Chạy E2E
- [ ] T035 [P] Khởi chạy Tomcat và chạy thử `DependentViewE2ETest` để nghiệm thu giao diện.
