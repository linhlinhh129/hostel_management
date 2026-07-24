# Test Specification: Quản lý hợp đồng (Unit Test Only)

**Status:** Draft
**Target Feature:** Quản lý hợp đồng (`my-project/sdd/specs/manager/contractmanagement`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng toàn bộ chu trình sống của Hợp đồng thông qua `ContractServlet`, bao gồm Xem, Tạo mới, Thêm người thuê và Xóa mềm (Soft-delete). Cô lập hoàn toàn với tầng Database.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **AC01 - Xem danh sách hợp đồng**: KHI Manager truy cập `/manager/contracts`, HỆ THỐNG PHẢI lấy danh sách hợp đồng đúng theo `facilityId` đang phụ trách và forward.
- **AC02 - Tạo hợp đồng thành công**: KHI Manager submit form `POST /create` với đầy đủ thông tin hợp lệ (tên, CCCD, ID phòng đang trống), HỆ THỐNG PHẢI tính toán tự động sinh mã `contract_id` định dạng `HD-{roomCode}-{date}-{seq}`, lưu thành công và gán trạng thái `ACTIVE`.
- **AC03 - Tạo tài khoản người thuê**: KHI Manager gọi `POST /add-tenant`, HỆ THỐNG PHẢI gọi Service sinh mật khẩu, gửi Email, gán `tenant_id` vào hợp đồng, chuyển trạng thái phòng thành `OCCUPIED`.
- **AC04 - Xóa mềm hợp đồng**: KHI submit `POST /delete` lên một hợp đồng đang ở trạng thái `INACTIVE`, HỆ THỐNG PHẢI thực hiện soft-delete và ghi Log.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **AC05 - Validation Tạo hợp đồng**: KHI thiếu Tên, CCCD, hoặc Ngày hết hạn bé hơn Ngày bắt đầu, HỆ THỐNG PHẢI chặn lại, gán thông báo lỗi `errorMessage` và đẩy ngược lại trang `create.jsp`.
- **AC06 - Phân quyền IDOR**: KHI Manager A cố ý gửi `roomId` thuộc cơ sở của Manager B, HỆ THỐNG PHẢI bắt lỗi quyền sở hữu và trả về `ROOM_ACCESS_DENIED`.
- **AC07 - Trùng hợp đồng**: KHI chọn tạo hợp đồng cho 1 phòng đang có một hợp đồng `ACTIVE` khác, HỆ THỐNG PHẢI báo lỗi `ROOM_ALREADY_HAS_ACTIVE_CONTRACT`.
- **AC08 - Xóa hợp đồng trái phép**: KHI cố tình xóa một hợp đồng đang có trạng thái `ACTIVE`, HỆ THỐNG BẮT BUỘC chặn đứng và báo lỗi "Không thể xóa hợp đồng đang hiệu lực".

### 2.3 Boundary Values (Các giá trị biên)
- **Validation CCCD/CMND**: KHI nhập CCCD độ dài đúng 9 hoặc 12 số (Biên đúng), HỆ THỐNG PHẢI cho phép vượt qua. KHI nhập 11 số (Biên sai), HỆ THỐNG PHẢI báo lỗi `IllegalArgumentException` hoặc lỗi Validation.
- **Ngày lập hợp đồng**: KHI `startDate` bằng đúng `signedDate`, HỆ THỐNG PHẢI cho phép.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Race Condition - Ký hợp đồng kép**: KHI 2 Manager (hoặc Admin và Manager) cùng thao tác Tạo hợp đồng cho **cùng 1 phòng** tại cùng 1 phần nghìn giây. Thread 1 được Service xử lý pass, Thread 2 Mock Service văng lỗi `OptimisticLockException` hoặc `DataIntegrityViolation`. Servlet PHẢI catch an toàn Thread 2, báo lỗi "Phòng đã được cho thuê bởi người khác, vui lòng kiểm tra lại".
