# Test Specification: Danh sách Thông báo (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Danh sách Thông báo (`my-project/sdd/specs/operator/Notifications`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng nghiệp vụ hiển thị Thông báo (View Only) của Nhân viên Vận hành. Đảm bảo dữ liệu thông báo được lọc CHÍNH XÁC theo quyền truy cập (`facility_id`), tránh tình trạng rò rỉ (IDOR) thông tin nội bộ của khu trọ khác.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem thông báo chung (ALL)**: KHI Mock DAO trả về một thông báo có `target_type = 'ALL'`, HỆ THỐNG PHẢI hiển thị thành công thông báo đó lên View.
- **Xem thông báo khu trọ (FACILITY)**: KHI Mock DAO trả về thông báo có `target_type = 'FACILITY'` VÀ `facility_id` khớp với `facility_id` của Operator đang đăng nhập, HỆ THỐNG PHẢI hiển thị thông báo.
- **Đọc chi tiết (Modal)**: KHI Servlet xử lý thành công, HỆ THỐNG BẮT BUỘC KHÔNG sanitize/cắt gọt thuộc tính `content` quá mức để Frontend có thể hiển thị đủ trên Modal.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Role Mismatch**: KHI User có Role là `TENANT` truy cập `/operator/notifications`, HỆ THỐNG PHẢI ném 403 Forbidden.
- **Missing Session Data**: KHI User session bị thiếu mất `facility_id` (trường hợp lỗi đăng nhập), HỆ THỐNG PHẢI bắt lỗi, log, và hiển thị giao diện "Lỗi không xác định" thay vì văng 500.
- **Chống Ghi đè (Read Only)**: KHI gửi POST Request tới URL, HỆ THỐNG PHẢI từ chối với 405 Method Not Allowed.

### 2.3 Boundary Values (Các giá trị biên)
- **Không có dữ liệu**: KHI Operator thuộc 1 khu trọ hoàn toàn mới chưa có thông báo nào, Mock DAO trả về Empty List, HỆ THỐNG PHẢI gắn list rỗng vào Attribute an toàn.
- **Phân trang quá đà**: KHI truyền tham số `?page=99999`, HỆ THỐNG PHẢI xử lý an toàn (trả về trang cuối hoặc list rỗng).
- **Văn bản siêu dài**: KHI Mock trả về một thông báo với chuỗi Nội dung rất dài (10,000 ký tự), Servlet PHẢI gán attribute thành công không bị memory leak hoặc cắt ngắn dữ liệu ngầm.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Snapshot Reading**: KHI Operator đang chuyển trang, đồng thời Admin thu hồi (Xóa mềm) một thông báo. Đảm bảo Servlet không có trạng thái chia sẻ (Shared State) để phòng tránh `ConcurrentModificationException`.
