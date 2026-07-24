# Test Specification: Quản lý Thông báo (Notification Management - Unit Test Only)

**Status:** Draft
**Target Feature:** Admin Notification Management (`my-project/sdd/specs/admin/notificationManagement`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Tuyệt đối không kết nối DB thật, mọi đối tượng phụ thuộc (DAO, EmailService, v.v.) phải được cô lập bằng Mock.
Mục tiêu là đảm bảo API/Servlet hoạt động đúng logic, validate dữ liệu đầu vào chuẩn xác, kiểm soát lỗi an toàn và xử lý luồng đồng thời (thread-safe). 

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Tạo thông báo thành công**: KHI Admin gửi form tạo thông báo đầy đủ và đúng định dạng (`title`, `content`, `recipientType="ALL"`), HỆ THỐNG PHẢI trả về mã 201 (hoặc redirect success) và gọi lệnh insert vào Mock DAO.
- **Xem danh sách thông báo**: KHI Admin GET danh sách thông báo, HỆ THỐNG PHẢI hiển thị các thông báo từ Mock DAO (bao gồm phân trang chuẩn xác).
- **Xem chi tiết thông báo**: KHI Admin GET chi tiết thông báo hợp lệ, HỆ THỐNG PHẢI trả về đầy đủ các thông tin (Mã, Tiêu đề, Nội dung, Ngày tạo, Người tạo, Đối tượng nhận).
- **Tìm kiếm hợp lệ**: KHI Admin tìm kiếm với từ khóa đúng, HỆ THỐNG PHẢI map từ khóa xuống Service/DAO và trả về kết quả khớp với tiêu đề.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Thiếu trường bắt buộc (Blank/Null)**: KHI tạo thông báo mà tiêu đề hoặc nội dung rỗng, HỆ THỐNG PHẢI ném lỗi `VALIDATION_ERROR` (HTTP 400).
- **Sai đối tượng nhận**: KHI `recipientType` khác `ALL`, HỆ THỐNG PHẢI ném lỗi `VALIDATION_ERROR`.
- **Thông báo không tồn tại**: KHI Admin xem chi tiết ID không có thật, HỆ THỐNG PHẢI ném lỗi `NOTIFICATION_NOT_FOUND` (HTTP 404).
- **Lỗi bảo mật/Phân quyền**: 
  - KHI chưa đăng nhập -> HỆ THỐNG PHẢI trả lỗi `UNAUTHORIZED` (HTTP 401 hoặc redirect `/login`).
  - KHI có role không phải ADMIN -> HỆ THỐNG PHẢI ném lỗi `FORBIDDEN` (HTTP 403).

### 2.3 Boundary Values (Các giá trị biên)
- **Biên giới hạn ký tự**:
  - Tiêu đề đúng 255 ký tự (Hợp lệ) vs 256 ký tự (Lỗi `TITLE_TOO_LONG`).
  - Nội dung đúng 1000 ký tự (Hợp lệ) vs 1001 ký tự (Lỗi `CONTENT_TOO_LONG`).
- **Tìm kiếm rỗng**: KHI tìm kiếm không khớp dữ liệu nào, HỆ THỐNG PHẢI trả về mảng rỗng (Không được crash `NullPointerException`).
- **Phân trang biên**: 
  - KHI truyền `page < 0`, hệ thống PHẢI fallback về `page = 0`.
  - KHI truyền `size` quá lớn (vd 999999), hệ thống PHẢI giới hạn lại ở mức max (vd 100).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Tạo thông báo đồng thời**: KHI 10 Admin cùng tạo thông báo cùng lúc (Multi-threading), HỆ THỐNG PHẢI chứng minh an toàn luồng (không ghi đè request body của nhau) trong nội bộ Servlet.
- **Đọc/Ghi đồng thời**: KHI Thread A đang tạo thông báo và Thread B đang đọc danh sách, logic bên trong Service không văng `ConcurrentModificationException`. (Sử dụng Mockito + ExecutorService để xác minh Thread-safety).
