# Danh sách Công việc - Cập nhật trạng thái sửa chữa (UpdateSatatus)

## Backend
- [x] Tạo phương thức `completeRequest(int requestId, String notes, String attachmentUrls2)` trong `RequestDAO.java` và Interface/Impl tương ứng ở tầng Service.
- [x] Ánh xạ câu lệnh SQL UPDATE: Gắn `notes` vào cột `rejection_reason`, danh sách ảnh vào `attachment_urls2`, và tự động cập nhật thời gian hoàn thành qua trường `updated_at`.
- [x] Bổ sung annotation `@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)` vào `DetailRequestServlet.java` để hỗ trợ upload.
- [x] Thêm nhánh xử lý `if ("complete".equals(action))` trong hàm `doPost` của `DetailRequestServlet`.
- [x] Xử lý lưu các file upload vào thư mục máy chủ (`/uploads/requests/`), sinh chuỗi đường dẫn phân tách bằng dấu phẩy.
- [x] Cài đặt validation: báo lỗi nếu ghi chú rỗng hoặc không có ảnh (ngoại trừ khi checkbox `Lỗi đơn giản` được chọn).
- [x] Cập nhật tính năng ghi `AuditLog` để lưu lịch sử chuyển đổi trạng thái thành `DONE` khi hoàn tất cập nhật.

## Frontend
- [x] Tích hợp Form Báo cáo hoàn thành (có thể dạng Modal) vào giao diện chi tiết yêu cầu (`operator/requests/detail.jsp`).
- [x] Gắn thuộc tính `enctype="multipart/form-data"` cho Form.
- [x] Thêm ô Textarea `notes` và Input File `after_images` (thuộc tính `multiple`).
- [x] Thêm Input Checkbox `no_image_checkbox` (Lỗi đơn giản) để linh hoạt trong trường hợp sự cố nhỏ không cần chụp ảnh.
- [x] Tích hợp component thông báo Alert để hiển thị mượt mà các chuỗi lỗi validation trả về từ backend (vd: "Vui lòng đính kèm ít nhất 1 ảnh...").
