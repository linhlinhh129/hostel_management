# Validation Quickstart: Sửa Báo cáo sự cố (Edit Incident Report)

## 1. Mục đích
Tài liệu này cung cấp các kịch bản kiểm thử nhanh để xác minh tính năng **Sửa Báo cáo sự cố** hoạt động đúng với các yêu cầu đã đặt ra, đặc biệt là các quy tắc Validation mới (Tiêu đề <= 50, Chi tiết vị trí <= 50, Mô tả <= 1000).

## 2. Chuẩn bị (Prerequisites)

- Khởi chạy Tomcat server (`mvn clean tomcat7:run` hoặc tương đương).
- CSDL đã có sẵn ít nhất một báo cáo sự cố có `status = 'PENDING'` và `sender_id` trùng với tài khoản Operator đang test.
- Đăng nhập vào hệ thống bằng tài khoản Operator (role: `OPERATOR`).

## 3. Các kịch bản kiểm thử (Test Scenarios)

### Scenario 1: Kiểm tra form đổ dữ liệu cũ (Pre-fill Data)
1. Từ danh sách "Lịch sử báo cáo", nhấn nút "Sửa" vào một báo cáo đang có trạng thái `PENDING`.
2. **Kỳ vọng:** 
   - Điều hướng sang trang `EditIncidentReport`.
   - Các trường Tiêu đề, Cơ sở, Vị trí, Phân loại, Ưu tiên, Mô tả được điền sẵn với dữ liệu cũ từ Database.
   - Hiển thị đúng các hình ảnh đã đính kèm trước đó.

### Scenario 2: Cố tình vi phạm Validation độ dài
1. Xóa trắng tiêu đề hoặc nhập chuỗi ký tự dài > 50 ký tự vào ô Tiêu đề.
2. Nhập chuỗi ký tự dài > 50 ký tự vào ô Chi tiết vị trí.
3. Nhập chuỗi ký tự dài > 1000 ký tự vào textarea Mô tả.
4. Nhấn nút "Lưu báo cáo".
5. **Kỳ vọng:**
   - Hành động gửi bị chặn.
   - Các ô vi phạm bị viền màu đỏ (`is-invalid`).
   - Hiển thị lỗi màu đỏ phía dưới từng ô vi phạm tương ứng (ví dụ: "Tiêu đề không được vượt quá 50 ký tự").

### Scenario 3: Backend Validation Protection
1. Mở công cụ Developer Tools của trình duyệt (F12).
2. Xóa thuộc tính `maxlength="50"` của ô Tiêu đề trên giao diện HTML, sau đó nhập vào 60 ký tự.
3. Nhấn nút "Lưu báo cáo" để ép submit form vượt qua Frontend Validation.
4. **Kỳ vọng:**
   - Server không thực hiện cập nhật.
   - Trang trả về kèm thông báo lỗi (Error message ở đầu trang) thông báo Tiêu đề vượt quá giới hạn.

### Scenario 4: Chỉnh sửa thành công (Happy Path)
1. Chỉnh sửa một vài trường hợp lệ (ví dụ: Thay đổi tiêu đề thành "Hỏng đèn tầng 2", bổ sung mô tả).
2. Nhấn nút "Lưu báo cáo".
3. **Kỳ vọng:**
   - Server báo "Cập nhật thành công" bằng Toast message.
   - Trở lại trang Lịch sử báo cáo.
   - Dữ liệu mới đã được cập nhật thành công (kiểm tra bằng cách nhấn xem chi tiết lại báo cáo).

### Scenario 5: Chặn sửa khi sai trạng thái (Security check)
1. Đăng nhập DB (SQL Server), tìm `request_id` của báo cáo đang thử nghiệm và đổi `status` thành `IN_PROGRESS` (tức là quản lý đã nhận xử lý).
2. Trên màn hình trình duyệt, nhập thông tin hợp lệ vào Form (đang mở trước khi DB bị sửa) và nhấn "Lưu báo cáo".
3. **Kỳ vọng:**
   - Cập nhật thất bại.
   - Hiển thị thông báo lỗi (ví dụ: "Không thể chỉnh sửa báo cáo đã được xử lý").
