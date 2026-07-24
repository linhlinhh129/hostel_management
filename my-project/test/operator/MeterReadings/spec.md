# Test Specification: Lịch sử Cập nhật Điện Nước (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Lịch sử Cập nhật Điện Nước (`my-project/sdd/specs/operator/MeterReadings`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng nghiệp vụ xem Lịch sử Điện nước (View Only) của Kỹ thuật viên. Đảm bảo bộ lọc Tháng/Năm được parse chính xác và hiển thị đầy đủ thông tin chi tiết (ảnh minh chứng) mà không có quyền thao tác chỉnh sửa.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem lịch sử tháng hiện tại**: KHI không truyền param, HỆ THỐNG PHẢI tự động lấy `month` và `year` hiện tại, gọi Mock DAO trả về danh sách lịch sử.
- **Lọc theo tháng năm cụ thể**: KHI truyền `month=5&year=2026`, HỆ THỐNG PHẢI parse đúng và truyền tham số xuống Mock DAO.
- **Load Chi tiết Ảnh Công tơ**: KHI nhấn xem chi tiết một bản ghi đã cập nhật, HỆ THỐNG PHẢI bóc tách được `electricMeterImage` và `waterMeterImage` (nếu có) để hiển thị Modal.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Tham số thời gian phi logic**: KHI truyền `month=13` hoặc `month=-1`, HỆ THỐNG PHẢI bắt lỗi Validation, fallback về tháng hiện tại hoặc báo lỗi Bad Request.
- **Tham số chữ**: KHI truyền `year=abc`, HỆ THỐNG PHẢI bắt exception `NumberFormatException` và fallback an toàn.
- **Ngăn chặn Cập nhật**: KHI gửi POST request lên URL `/operator/meter-readings/history` (cố tình thử), HỆ THỐNG PHẢI từ chối với 405 Method Not Allowed (Bảo vệ tính chất Read-only).

### 2.3 Boundary Values (Các giá trị biên)
- **Không có dữ liệu trong tháng**: KHI tháng được chọn chưa có bất kỳ bản ghi nào (tháng tương lai), Mock DAO trả về mảng rỗng, HỆ THỐNG PHẢI render an toàn list rỗng.
- **Ảnh đính kèm bị Null**: KHI một bản ghi trong lịch sử bị mất đường dẫn ảnh (`image = null`), HỆ THỐNG PHẢI xử lý an toàn trên UI Modal (không bị NullPointer).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Dirty Read khi Admin đang chốt sổ**: KHI Operator xem lịch sử đúng lúc Admin chốt sổ/xóa bản ghi tháng cũ. Mock DAO trả về snapshot, HỆ THỐNG PHẢI load list an toàn mà không văng `ConcurrentModificationException`.
