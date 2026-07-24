# Test Specification: Quản lý người phụ thuộc (Unit Test Only)

**Status:** Draft
**Target Feature:** Quản lý người phụ thuộc (`my-project/sdd/specs/manager/dependentManagement`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic CRUD của `ManagerTenantsServlet` đối với Người phụ thuộc. Trọng tâm: che giấu CCCD (Masking), chặn thao tác vào người thuê đã `INACTIVE`, ghi Audit Log đầy đủ.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **AC01 - Thêm người phụ thuộc**: KHI Manager POST thông tin hợp lệ (tên, quan hệ) vào `/manager/tenants/{tenantId}/dependents/add` cho một tenant `ACTIVE`, HỆ THỐNG PHẢI tạo mới bản ghi, ghi Audit Log và chuyển hướng về trang chi tiết người thuê.
- **AC02 - Xem chi tiết & Che CCCD**: KHI Manager xem chi tiết người phụ thuộc, HỆ THỐNG PHẢI che phần giữa số CCCD (Ví dụ: `012***890`).
- **AC03 - Xóa mềm (Soft Delete)**: KHI POST xóa mềm một người phụ thuộc, HỆ THỐNG PHẢI đánh dấu `deleted_at`, ghi Audit Log và chuyển hướng thành công.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **AC04 - Ràng buộc Tenant Active**: KHI Manager cố thêm người phụ thuộc vào một hợp đồng/người thuê đang ở trạng thái `INACTIVE`, HỆ THỐNG PHẢI từ chối và báo lỗi "Người thuê chính phải ở trạng thái ACTIVE".
- **AC05 - Validation form**: KHI Manager bỏ trống `fullName` hoặc `relationship`, HỆ THỐNG PHẢI từ chối lưu và đẩy lỗi validation về UI.
- **AC06 - IDOR Chéo cơ sở**: KHI Manager A cố sửa (`edit`) hoặc xóa (`remove`) người phụ thuộc của Manager B, HỆ THỐNG PHẢI quăng 403 Forbidden.
- **AC07 - Lỗi 404 Not Found**: KHI cố xem hoặc xóa một `dependentId` không tồn tại, HỆ THỐNG PHẢI trả về 404.

### 2.3 Boundary Values (Các giá trị biên)
- **AC08 - Biên số điện thoại/CCCD**: KHI nhập số điện thoại độ dài chuẩn 10 chữ số, HỆ THỐNG PHẢI pass. KHI nhập 9 hoặc 11 số, HỆ THỐNG PHẢI báo lỗi định dạng.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **AC09 - Race Condition khi Xóa mềm kép**: KHI 2 Manager (hoặc Admin và Manager) cùng bấm nút "Xóa" một người phụ thuộc vào cùng 1 mili-giây. HỆ THỐNG CHỈ ĐƯỢC PHÉP ghi nhận 1 lần xóa thành công (và ghi 1 dòng Audit Log duy nhất), Thread còn lại phải bắt được ngoại lệ (DataIntegrity hoặc xử lý êm xuôi báo "Đã bị xóa").
