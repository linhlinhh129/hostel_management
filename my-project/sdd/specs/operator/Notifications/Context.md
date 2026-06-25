# CONTEXT.md [Danh sách thông báo hệ thống]

**Người viết:** Antigravity (AI Assistant)  
**Ngày:** 2026-06-25

---

## 1. PROBLEM STATEMENT

Nhân viên vận hành (Operator) hiện chưa có giao diện hoặc tính năng để nhận và đọc các thông báo từ Quản trị viên (Admin) gửi xuống. Việc thiếu một kênh liên lạc chính thức trên hệ thống dẫn đến việc Nhân viên vận hành có thể bỏ lỡ các thông tin quan trọng về hệ thống, chính sách mới, hoặc các nhắc nhở công việc khẩn cấp. Điều này làm giảm hiệu quả phối hợp và quản lý khu trọ.

---

## 2. DOMAIN KNOWLEDGE

### Thông báo (Notification)
Đơn vị thông tin được gửi từ hệ thống hoặc Admin tới người dùng. 

### Phạm vi (Target Type)
Đối tượng nhận thông báo, bao gồm:
- `ALL` (Toàn hệ thống): Mọi người dùng đều nhận được.
- `FACILITY` (Khu trọ): Chỉ những người liên quan đến khu trọ đó (như Operator quản lý khu trọ, Tenant thuê phòng tại khu trọ) mới nhận được.
- `ROOM` (Phòng): Gửi cụ thể cho người thuê tại một phòng.

### Trạng thái gửi (Status)
- `SENT`: Đã được gửi đi.
- `DRAFT`: Bản nháp chưa gửi (Operator không thấy).

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (End-user trực tiếp)
Người sử dụng tính năng này hàng ngày để tiếp nhận thông tin chỉ đạo, cập nhật chính sách hoặc cảnh báo từ cấp trên để thực hiện đúng quy trình quản lý.

### Admin / Manager
Người hưởng lợi gián tiếp khi thông tin họ truyền đạt được đảm bảo đến đúng đối tượng (Operator) một cách nhanh chóng và chính xác.

---

## 4. CONSTRAINTS

### Business
- Chỉ xem (Read-only): Nhân viên vận hành không có quyền Thêm, Sửa, hoặc Xóa các thông báo hệ thống.
- Chỉ hiển thị thông báo đã gửi (`status = 'SENT'`).
- Chỉ hiển thị thông báo có phạm vi `ALL` hoặc `FACILITY` (khớp với cơ sở mà Operator đang quản lý).

### Tech (Performance)
- Thời gian load danh sách thông báo phải nhanh chóng, tối ưu payload.

### Tech (Database)
- Cần tối ưu truy vấn kết hợp để kiểm tra đúng `facility_id` mà Operator quản lý.

---

## 5. ASSUMPTIONS

### Authentication & Authorization
- Người dùng đã đăng nhập hợp lệ với vai trò `OPERATOR`.
- Hệ thống đã lưu trữ chính xác `facility_id` (Cơ sở) mà Operator đang quản lý trong phiên đăng nhập (Session) hoặc có thể truy vấn dễ dàng từ Database.

### Data Integrity
- Các bản ghi thông báo trong bảng `notifications` có cấu trúc chuẩn, trường `deleted_at` quản lý việc xóa mềm (soft-delete).

---

## 6. OPEN QUESTIONS

### 1. Trạng thái Đã đọc / Chưa đọc
Hệ thống hiện tại có bảng phụ trợ để lưu trữ trạng thái "Đã đọc" (Read) cho từng User đối với từng Thông báo không? Nếu không, trên UI sẽ không phân biệt được thông báo nào mới, thông báo nào cũ ngoài việc dựa vào ngày gửi. (Khuyến nghị: Chỉ cần sắp xếp theo ngày gửi mới nhất).

### 2. Thông báo Push (Real-time)
Có yêu cầu hiển thị số lượng thông báo mới (badge đỏ) trên thanh Sidebar hay không? (Khuyến nghị: Giai đoạn 1 chưa cần làm real-time).

### 3. Phân trang
Giới hạn hiển thị bao nhiêu thông báo trên một trang (ví dụ 10, 20 bản ghi) để tránh quá tải trang?
