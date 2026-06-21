# Feature: Cập nhật trạng thái sửa chữa

**Status:** Approved (Revised for DB constraints)  
**Author:** Phạm Anh Tú  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-19  
**Priority:** High

---

## 1. Business Context

Tính năng này là bước cuối cùng trong quy trình xử lý yêu cầu sửa chữa.

Sau khi hoàn thành công việc tại hiện trường, nhân viên vận hành cần cập nhật kết quả thực hiện, đính kèm hình ảnh minh chứng và ghi nhận ngày hoàn thành thực tế. Dữ liệu này phục vụ cho việc nghiệm thu, theo dõi lịch sử sửa chữa, báo cáo KPI và đánh giá hiệu quả vận hành.

---

## 2. User Stories

### Story 1 (Happy Path - Lưu kết quả thành công)

**As a** nhân viên vận hành,
**I want to** nhập ghi chú kết quả, đính kèm ảnh sau sửa chữa,
**so that** tôi có thể xác nhận công việc đã hoàn tất và cập nhật trạng thái yêu cầu sang "Hoàn thành".

### Story 2 (Edge Case - Thiếu minh chứng hình ảnh)

**As a** manager,
**I want to** bắt buộc nhân viên vận hành phải đính kèm ít nhất 1 hình ảnh sau sửa chữa,
**so that** đảm bảo tính minh bạch và xác thực của kết quả xử lý.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị form cập nhật trạng thái

**WHEN** user đang ở giao diện Chi tiết yêu cầu sửa chữa (trạng thái Đang xử lý)
**THE SYSTEM SHALL** hiển thị nút "Báo cáo hoàn thành", khi click mở ra form gồm:
- Ghi chú kết quả (Textarea)
- Đính kèm ảnh minh chứng (File Upload)

### AC02 – Tự động cập nhật thời gian

**WHEN** user báo cáo hoàn thành
**THE SYSTEM SHALL** tự động lấy thời gian hiện tại (`GETDATE()`) làm thời gian hoàn thành (không yêu cầu Vận hành chọn thủ công để tránh gian lận).

### AC03 – Cập nhật thành công

**WHEN** user nhấn nút **[Xác nhận lưu]** với đầy đủ dữ liệu hợp lệ
**THE SYSTEM SHALL**
- Gọi Form Submit lên máy chủ.
- Lưu file vật lý vào ổ cứng server.
- Lưu đường dẫn ảnh vào database.
- Cập nhật trạng thái yêu cầu thành `COMPLETED`.
- Điều hướng người dùng lại màn hình Chi tiết kèm thông báo thành công.

### AC04 – Thiếu dữ liệu bắt buộc

**WHEN** user nhấn nút **[Xác nhận lưu]** nhưng chưa nhập ghi chú kết quả hoặc chưa đính kèm ảnh
**THE SYSTEM SHALL**
- Hiển thị lỗi validation (trên giao diện HTML5 hoặc báo lỗi từ Servlet).
- Không gửi hoặc không lưu dữ liệu.

---

## 4. Technical Integration (Servlet & Form)

Thay vì thiết kế REST API, hệ thống sử dụng kiến trúc MVC thuần túy (JSP & Servlet).

### Request Flow
- **URL**: `POST /operator/requests/detail`
- **Encoding**: `multipart/form-data`
- **Parameters**:
  - `action`: "complete"
  - `id`: Mã ID yêu cầu
  - `notes`: Ghi chú hoàn thành
  - `after_images`: Danh sách file ảnh upload.

### Database Mapping Workaround
Vì ràng buộc tuyệt đối **không được chỉnh sửa cấu trúc CSDL hiện tại**, dữ liệu sẽ được ánh xạ vào bảng `requests` như sau:
1. `notes` -> Ghi vào cột `rejection_reason` (khi status = COMPLETED, cột này đóng vai trò là ghi chú).
2. `after_images` -> Ghi danh sách tên file vào cột `attachment_urls2` (phân cách bởi dấu phẩy).
3. `completed_at` -> Dựa vào cột `updated_at` (do lệnh UPDATE GETDATE() thiết lập).
4. `status` -> Ghi nhận `COMPLETED`.

### Validation Rules
- `notes`: Không được để trống.
- `after_images`: Giới hạn các file `.jpg, .jpeg, .png`, dung lượng tối đa 5MB/file, nhiều nhất 5 file. Tối thiểu 1 file.

---

## 5. Technical Constraints

- Cần bổ sung `@MultipartConfig` vào `DetailRequestServlet.java` để hỗ trợ Servlet đọc File Upload.
- File sẽ được lưu tạm ở một thư mục trên ổ đĩa máy chủ (ví dụ `uploads/requests/`). 
- Tên file lưu xuống DB phải được ghép chuỗi bằng dấu phẩy (vd: `image1.jpg,image2.png`).

---

## 6. Out of Scope

- Ký xác nhận điện tử (Digital Signature)
- Khảo sát sự hài lòng
- Gửi thông báo Email/SMS tự động
