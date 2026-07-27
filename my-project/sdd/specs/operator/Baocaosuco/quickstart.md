# Quickstart & Validation Guide: Báo cáo sự cố (Operator)

Tài liệu này hướng dẫn cách kiểm thử và xác thực chức năng "Báo cáo sự cố tại hiện trường" dành cho Nhân viên Vận hành.

## 1. Prerequisites
- Server Tomcat đang chạy thành công qua Netbeans.
- Trình duyệt web hiện đại.
- Tài khoản đăng nhập với quyền `OPERATOR`.

## 2. Kiểm thử Frontend Validation (Độ dài ký tự)
- **Bước 1**: Truy cập đường dẫn: `/operator/incidents/create`.
- **Bước 2**: Trong form nhập liệu, cố tình nhập:
  - **Tiêu đề**: 55 ký tự.
  - **Chi tiết vị trí**: 55 ký tự.
  - **Mô tả chi tiết**: 1005 ký tự.
- **Expected Outcome**: Form hiển thị viền đỏ và báo lỗi bên dưới input, không cho phép Submit. HTML5 validation chặn ngay từ trình duyệt.

## 3. Kiểm thử Backend Validation
- **Bước 1**: Tắt HTML5 Validation ở trình duyệt (Xóa thuộc tính `maxlength` qua DevTools) hoặc dùng Postman gửi request POST tới `/operator/incidents/create`.
- **Payload**:
  ```json
  {
      "title": "A very long title that exceeds the fifty characters limit...",
      "locationDetail": "Some extremely long location detail text exceeding fifty...",
      "content": "Very long description..." // > 1000 chars
  }
  ```
- **Expected Outcome**: Nhận thông báo lỗi (Flash message "danger"): "Tiêu đề không được vượt quá 50 ký tự" và "Mô tả không được vượt quá 1000 ký tự". Dữ liệu không được ghi vào DB.

## 4. Happy Path
- **Bước 1**: Nhập dữ liệu chuẩn xác vào form.
- **Bước 2**: Upload ảnh.
- **Bước 3**: Nhấn "Gửi báo cáo".
- **Expected Outcome**: Nhận thông báo thành công và chuyển hướng về trang danh sách yêu cầu (`/operator/requests`). Record mới xuất hiện ở trên cùng danh sách với status `PENDING`.
