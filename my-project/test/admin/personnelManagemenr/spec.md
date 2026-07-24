# Test Specification: Quản lý Nhân sự (Personnel Management - Unit Test Only)

**Status:** Draft
**Target Feature:** Admin Personnel Management (`my-project/sdd/specs/admin/personnelManagemenr`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Tuyệt đối không kết nối DB thật, mọi đối tượng phụ thuộc (PersonnelDAO, FacilityDAO, EmailService, v.v.) phải được cô lập bằng Mock.
Kiểm tra luồng tạo mới, chỉnh sửa, gán cơ sở, kích hoạt/khóa tài khoản theo các ràng buộc vai trò nghiêm ngặt, validate dữ liệu đầu vào.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Tạo nhân sự thành công**: KHI Admin gửi form hợp lệ cho role `MANAGER` / `OPERATOR` và chọn cơ sở `ACTIVE` hợp lệ chưa có người quản lý, HỆ THỐNG PHẢI tạo account, tự động sinh mã, sinh mật khẩu tạm, gọi `EmailService` và gán trạng thái `ACTIVE`.
- **Chỉnh sửa nhân sự thành công**: KHI Admin thay đổi role/facility hợp lệ, HỆ THỐNG PHẢI cập nhật và thu hồi/cấp lại quyền truy cập facility tương ứng.
- **Xem danh sách/chi tiết**: KHI xem danh sách hoặc chi tiết, HỆ THỐNG PHẢI gọi DAO và trả về danh sách tài khoản đã được enrich thêm `facilityNames`.
- **Khóa/Mở khóa thành công**: KHI gửi lệnh toggle status cho tài khoản hợp lệ (không phải chính mình), HỆ THỐNG PHẢI đổi trạng thái `ACTIVE <-> INACTIVE`.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Thiếu thông tin bắt buộc / Sai format**: 
  - KHI email sai format, số điện thoại chứa chữ, CCCD chứa chữ -> HỆ THỐNG PHẢI ném `VALIDATION_ERROR`.
- **Role không hợp lệ**: KHI cố tình tạo mới tài khoản với Role là `ADMIN` -> HỆ THỐNG ném `INVALID_ROLE`.
- **Cơ sở không hợp lệ**: 
  - KHI tạo/sửa nhưng không chọn cơ sở -> `FACILITY_REQUIRED`.
  - KHI gán cơ sở `INACTIVE` -> `FACILITY_NOT_ACTIVE`.
  - KHI gán MANAGER cho cơ sở đã có MANAGER -> `FACILITY_MANAGER_ALREADY_EXISTS`.
  - KHI gán OPERATOR cho cơ sở đã có OPERATOR -> `FACILITY_OPERATOR_ALREADY_EXISTS`.
- **Tự khóa tài khoản**: KHI Admin cố khóa chính mình -> ném `CANNOT_DEACTIVATE_SELF`.
- **Xóa nhân sự ACTIVE**: KHI Admin xóa tài khoản đang `ACTIVE` -> lỗi (chỉ xóa được khi `INACTIVE`).
- **Phân quyền**: KHI user không phải ADMIN hoặc chưa đăng nhập -> `FORBIDDEN` / `UNAUTHORIZED`.

### 2.3 Boundary Values (Các giá trị biên)
- **Độ dài và định dạng chuẩn**:
  - Số điện thoại đúng 10 số (Pass) vs 9 số/11 số (Fail).
  - CCCD đúng 12 số (Pass) vs 11 số/13 số (Fail).
  - Ngày sinh: đúng hôm nay (Pass) vs ngày mai (Fail `INVALID_DATE_OF_BIRTH`).
- **Trùng lặp dữ liệu (Unique Constraint)**:
  - CCCD/Email/Phone đã tồn tại -> `IDENTITY_NUMBER_ALREADY_EXISTS`, `EMAIL_ALREADY_EXISTS`, `PHONE_ALREADY_EXISTS`.
- **Khoảng trắng trong tìm kiếm**: KHI tìm kiếm `"  Nguyễn Văn A  "`, HỆ THỐNG PHẢI tự động trim và tìm kiếm thành công không phân biệt hoa thường.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Tranh chấp gán cơ sở**: KHI 2 Admin cùng lúc (cùng mili-giây) gán vai trò `MANAGER` cho cùng 1 cơ sở, HỆ THỐNG CHỈ ĐƯỢC PHÉP cho 1 request thành công (Dựa trên check Exception ở Mock DAO hoặc khóa luồng ở Service/Servlet). Người thứ 2 phải nhận được lỗi `FACILITY_MANAGER_ALREADY_EXISTS`.
- **Tranh chấp khóa tài khoản**: KHI 2 Admin cùng lúc khóa 1 tài khoản, HỆ THỐNG PHẢI xử lý an toàn mà không làm hỏng trạng thái dữ liệu (Thread-safety).
