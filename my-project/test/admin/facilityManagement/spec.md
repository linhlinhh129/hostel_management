# Test Specification: Quản lý Cơ sở (Facility Management - Unit Test Only)

**Status:** Draft
**Target Feature:** Admin Facility Management (`my-project/sdd/specs/admin/facilityManagement`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Tuyệt đối không gọi trực tiếp xuống CSDL thật, mọi DAO/Service phải được cô lập bằng Mock.
Xác minh luồng tạo mới, cập nhật, kích hoạt và vô hiệu hóa cơ sở, đặc biệt là nghiệp vụ tự động sinh danh sách phòng, phân quyền truy cập, các ràng buộc validate và xử lý xung đột dữ liệu. Bắt buộc kiểm thử 4 khía cạnh theo chuẩn.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Tạo cơ sở thành công**: KHI Admin nhập đủ các thông tin hợp lệ (Code, Tên, Địa chỉ, Max Floors, Max Rooms), HỆ THỐNG PHẢI lưu cơ sở ở trạng thái mặc định `DRAFT` và tự động chuyển Code thành in hoa (VD: `hl` -> `HL`).
- **Chỉnh sửa cơ sở DRAFT**: KHI cơ sở đang ở `DRAFT`, Admin PHẢI CÓ THỂ cập nhật toàn bộ thông tin (Code, Tên, Địa chỉ, Số tầng, Số phòng).
- **Kích hoạt cơ sở (Activate)**: KHI cơ sở `DRAFT` được kích hoạt, HỆ THỐNG PHẢI chuyển trạng thái sang `ACTIVE` VÀ tự động sinh danh sách phòng đúng chuẩn `[Mã][Tầng][Phòng]` (VD: 5 tầng x 4 phòng = 20 phòng) ở trạng thái `AVAILABLE`.
- **Chỉnh sửa cơ sở ACTIVE**: KHI cơ sở đã `ACTIVE`, Admin CHỈ ĐƯỢC PHÉP sửa Tên cơ sở. Các field khác bị khóa.
- **Vô hiệu hóa (Deactivate)**: KHI Admin vô hiệu hóa cơ sở `ACTIVE` (không có phòng occupied), HỆ THỐNG PHẢI chuyển sang `INACTIVE` nhưng KHÔNG được xóa phòng đã sinh hoặc lịch sử.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Validation lỗi trống (Blank/Null)**: KHI form thiếu Code, Name, Tầng hoặc Phòng, HỆ THỐNG PHẢI trả về 400 kèm mã lỗi tương ứng (VD: `FACILITY_CODE_REQUIRED`).
- **Validation lỗi định dạng/logic**: 
  - Code chứa ký tự đặc biệt/số, độ dài < 2 hoặc > 10.
  - Số tầng/Số phòng < 1 hoặc > 99.
  HỆ THỐNG PHẢI nén HTTP 400 và lỗi tương ứng.
- **Trùng mã cơ sở (Conflict)**: KHI tạo mới hoặc sửa Code trùng với cơ sở đã có, HỆ THỐNG PHẢI trả về HTTP 409 `FACILITY_CODE_ALREADY_EXISTS`.
- **Chặn sửa cấu hình khi ACTIVE**: KHI Admin cố update Code/Tầng/Phòng/Địa chỉ của cơ sở đã `ACTIVE`, HỆ THỐNG PHẢI trả về 400 (`FACILITY_CODE_IMMUTABLE`, v.v.).
- **Chặn kích hoạt sai trạng thái**: KHI cố kích hoạt cơ sở đã `ACTIVE` hoặc `INACTIVE`, HỆ THỐNG trả lỗi 400 `FACILITY_INVALID_STATUS_FOR_ACTIVATION`.
- **Sai Role/Chưa đăng nhập**: Bị chặn ở `BaseServlet` trả về 403 hoặc redirect.
- **Sinh phòng thất bại (Transaction Rollback)**: NẾU trong lúc kích hoạt mà quá trình `generateRooms()` sinh lỗi (VD mã trùng), quá trình chuyển sang `ACTIVE` PHẢI bị rollback hoàn toàn (vẫn ở `DRAFT`) và trả về lỗi 500.

### 2.3 Boundary Values (Các giá trị biên)
- **Giới hạn số phòng/tầng (Min/Max)**:
  - Giá trị hợp lệ nhỏ nhất: Tầng = 1, Phòng/Tầng = 1 (Sinh đúng 1 phòng).
  - Giá trị hợp lệ lớn nhất: Tầng = 99, Phòng/Tầng = 99 (Sinh tối đa 9801 phòng).
- **Giới hạn độ dài chuỗi**:
  - Code có đúng 2 ký tự (hợp lệ) và 10 ký tự (hợp lệ). 1 và 11 ký tự (không hợp lệ).
  - Tên cơ sở dài đúng 255 ký tự (hợp lệ), 256 (báo lỗi).
  - Địa chỉ dài 500 ký tự (hợp lệ), 501 (báo lỗi).
- **Danh sách cơ sở rỗng**: Màn hình list.jsp hiển thị thông báo "Không có cơ sở nào".

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Tạo cơ sở trùng mã đồng thời**: KHI 2 Admin cùng lúc (cùng 1ms) submit form tạo cơ sở với chung mã "HL", CHỈ ĐƯỢC PHÉP 1 người thành công, người kia bị lỗi 409 do unique constraint từ database/DAO.
- **Kích hoạt đồng thời**: KHI 2 Admin cùng nhấn kích hoạt 1 cơ sở `DRAFT`, CHỈ CÓ 1 luồng được sinh phòng. Luồng thứ 2 sẽ phát hiện trạng thái đã đổi hoặc bị lock (Optimistic Locking/Transaction) và văng lỗi, không được phép sinh trùng lặp số phòng.
