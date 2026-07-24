# Implementation Plan: Personnel Management Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminPersonnelServlet.java`, `PersonnelDAO`, `FacilityDAO`, `EmailService`
- **Constraint**: Đảm bảo sử dụng 100% Mockito để test logic nghiệp vụ, các ràng buộc validate và giao tiếp Email (bằng Mock).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminPersonnelServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_Create_Success`: Điền đầy đủ form cho `MANAGER`, chọn cơ sở hợp lệ. Xác minh gọi DAO insert, sinh mật khẩu, gửi email.
- `testDoPost_Update_Success`: Thay đổi Role hợp lệ, cập nhật và thay đổi quyền/cơ sở quản lý.
- `testDoPost_ToggleStatus_Success`: Đổi trạng thái (Active <-> Inactive).
- `testDoGet_ListAndDetail`: GET danh sách và chi tiết hợp lệ.

### 3.2 Error Cases
- `testDoPost_Create_InvalidRole`: Cố tình tạo role `ADMIN`.
- `testDoPost_FacilityErrors`: 
  - Gán cơ sở nhưng cơ sở đã có `MANAGER` khác.
  - Gán cơ sở `INACTIVE`.
- `testDoPost_CannotDeactivateSelf`: Admin khóa tài khoản của chính mình -> ném Exception.
- `testAuth_Permissions`: Role khác ADMIN hoặc chưa login bị chặn.

### 3.3 Boundary Values
- `testDoPost_Create_PhoneBoundary`: Truyền số điện thoại 9 số, 10 số (pass), 11 số.
- `testDoPost_Create_CCCDBoundary`: Truyền CCCD 11 số, 12 số (pass), 13 số.
- `testDoPost_UniqueConstraints`: Trùng Email/Phone ném lỗi từ Validation hoặc DAO (nếu DAO bắt).

### 3.4 Concurrent Scenarios
- `testConcurrency_AssignManager`: 2 Threads cùng gửi request POST tạo `MANAGER` trên cùng 1 Facility, verify chỉ 1 pass, 1 bị lỗi `FACILITY_MANAGER_ALREADY_EXISTS`.

## 4. Các bước thực hiện
1. Setup class `AdminPersonnelServletTest` và các Mock dependency.
2. Implement Happy path cho Create/Update/ToggleStatus.
3. Implement Validation Error Cases.
4. Implement Thread Concurrency Test.
