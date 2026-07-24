# Test Specification: Danh sách yêu cầu sửa chữa (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Danh sách yêu cầu sửa chữa (`my-project/sdd/specs/operator/ListRequest`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic phân trang, lọc dữ liệu (Filter theo trạng thái, thể loại, cơ sở) của màn hình Danh sách Yêu cầu. Đảm bảo luồng xử lý tham số trên Controller được gán chính xác xuống Mock DAO và ném lỗi an toàn khi tham số sai lệch.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Truy cập mặc định**: KHI truy cập trang danh sách không truyền bộ lọc, HỆ THỐNG PHẢI gọi Mock DAO lấy danh sách trang 1 (limit 20), sắp xếp theo ngày hẹn gần nhất.
- **Lọc theo nhiều tiêu chí**: KHI truyền query `status=pending` và `room_id=101`, HỆ THỐNG PHẢI gọi đúng hàm DAO với tham số tương ứng và trả về View.
- **Tính toán Phân trang**: KHI Mock DAO trả về `total=50`, `limit=20`, HỆ THỐNG PHẢI tính ra `total_pages=3` và gắn vào Request Attribute.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Tham số Filter sai kiểu**: KHI truyền `category_id=abc` (chuỗi thay vì số), HỆ THỐNG PHẢI bắt lỗi `NumberFormatException` an toàn, bỏ qua bộ lọc đó hoặc báo lỗi Bad Request chứ không văng 500 trắng trang.
- **Unauthorized Role**: KHI User Tenant truy cập URL `/operator/requests`, HỆ THỐNG PHẢI ném lỗi 403 Forbidden.

### 2.3 Boundary Values (Các giá trị biên)
- **Trang rỗng (No Data)**: KHI truyền một bộ lọc không khớp dữ liệu nào, Mock DAO trả mảng rỗng, HỆ THỐNG PHẢI gắn mảng rỗng vào attribute và View xử lý an toàn (Báo "Không có yêu cầu nào phù hợp").
- **Vượt biên phân trang**: KHI truyền `page=9999`, Mock DAO trả về rỗng, HỆ THỐNG PHẢI xử lý an toàn mảng rỗng và thiết lập `page=1` hoặc giữ nguyên page ảo mà không sập.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Snapshot Paging**: KHI luồng 1 đang GET `page=2` thì luồng 2 xóa 1 request ở `page=1`. HỆ THỐNG PHẢI đảm bảo Pagination tính toán tĩnh theo snapshot của Mock DAO trả về ngay tại khoảnh khắc gọi, không bị lỗi toán học (vd: total_pages bị âm).
