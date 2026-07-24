# Kế hoạch Thực hiện - Cập nhật trạng thái sửa chữa (UpdateSatatus)

## 1. Mục tiêu
Thiết kế luồng thao tác cho Nhân viên vận hành (Operator) cập nhật kết quả sau khi hoàn tất sửa chữa. Luồng này bắt buộc yêu cầu ghi chú (notes) và đính kèm hình ảnh minh chứng để đảm bảo tính xác thực. Do ràng buộc nghiêm ngặt không được sửa đổi cấu trúc CSDL hiện tại, hệ thống sẽ sử dụng kỹ thuật ánh xạ trường dữ liệu trên bảng `requests` để lưu thông tin.

## 2. Kiến trúc Backend (Java Servlet & DAO)
- **DAO (`RequestDAO.java`):** Bổ sung hàm ánh xạ dữ liệu:
  - `boolean completeRequest(int requestId, String notes, String attachmentUrls2)`
  - Thực thi câu lệnh SQL UPDATE: 
    - `status` = 'COMPLETED'
    - `rejection_reason` = [Giá trị notes]
    - `attachment_urls2` = [Giá trị attachmentUrls2]
    - `updated_at` = GETDATE()
- **Service (`RequestService.java`):** Tích hợp gọi DAO để thực hiện logic hoàn thành yêu cầu.
- **Servlet (`DetailRequestServlet.java`):**
  - Mapped URL: `/operator/requests/detail`
  - Thêm annotation `@MultipartConfig` để xử lý upload file minh chứng.
  - Xử lý **POST** với tham số `action="complete"`:
    - Đọc nội dung ghi chú (`notes`).
    - Hỗ trợ checkbox "Lỗi đơn giản" (bỏ qua upload ảnh nếu cần).
    - Lưu file ảnh đính kèm vào thư mục vật lý `uploads/requests/` và thu thập các đường dẫn.
    - Hợp nhất thành chuỗi (phân cách bởi dấu phẩy) để lưu vào cột `attachment_urls2`.
    - Validate bắt buộc: nếu không tích "Lỗi đơn giản" mà không có ảnh, báo lỗi.

## 3. Kiến trúc Frontend (JSP)
- **File:** `detail.jsp` trong thư mục `/WEB-INF/views/operator/requests/`
- **Thành phần giao diện (Modal / Form):**
  - Form với thuộc tính `enctype="multipart/form-data"`.
  - Ô Textarea nhập `Ghi chú kết quả` (bắt buộc).
  - Khối Input Upload File hỗ trợ chọn nhiều file ảnh.
  - Checkbox "Lỗi đơn giản không cần ảnh".
  - Component cảnh báo (Alert) để hiển thị thông báo lỗi từ phía Servlet truyền về (ví dụ: thiếu ảnh).
