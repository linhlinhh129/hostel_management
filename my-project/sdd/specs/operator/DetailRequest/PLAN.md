# Kế hoạch Thực thi (Implementation Plan) - Chi tiết Yêu cầu (DetailRequest)

## 1. Mục tiêu
Thiết kế lại hoàn chỉnh giao diện Chi tiết Yêu cầu sửa chữa (Layout 3 cột) và bổ sung tính năng giới hạn ký tự (1000) cho Ghi chú hoàn thành sửa chữa theo yêu cầu mới nhất của đặc tả.

## 2. Kế hoạch thay đổi
### 2.1. Frontend (detail.jsp)
- Giữ nguyên cấu trúc Layout 3 cột hiện tại.
- Tìm modal "Báo cáo hoàn thành sửa chữa" (`#completeModal`).
- Thêm thuộc tính `maxlength="1000"` vào trường `textarea` name="notes".
- Thêm thông báo lỗi (invalid-feedback) khi người dùng vượt quá số lượng ký tự.

### 2.2. Backend (DetailRequestServlet.java)
- Tại phương thức `doPost`, trong nhánh xử lý `action = "complete"`:
- Kiểm tra độ dài của chuỗi `notes`.
- Nếu `notes.length() > 1000`, sử dụng `request.setAttribute("error", "...")` và gọi lại `doGet` để trả về giao diện báo lỗi mà không cập nhật vào Database.

## 3. Xác thực (Verification)
- Mở một yêu cầu ở trạng thái Đang xử lý (`IN_PROGRESS`).
- Bấm nút "Báo cáo hoàn thành".
- Nhập ghi chú dài hơn 1000 ký tự (có thể giả lập bằng cách gỡ `maxlength` qua trình duyệt).
- Đảm bảo hệ thống báo lỗi chính xác và không thay đổi trạng thái Database.
