# Data Model: Báo cáo sự cố (Operator)

Tính năng này tập trung vào việc tạo mới (INSERT) dữ liệu vào bảng `requests` với các thông tin đã được gán sẵn mà không cần thay đổi cấu trúc bảng hiện tại.

## Entities

### `requests` (Ticket Báo cáo sự cố)
- `title` (VARCHAR 255): Gồm "[Cơ sở/Tòa nhà] [Phòng/Vị trí] Tiêu đề". Tối đa 50 ký tự cho phần tiêu đề người dùng nhập.
- `content` (TEXT): Gồm "Chi tiết vị trí: [chi tiết]\n\n[Mô tả chi tiết]". Phần mô tả chi tiết tối đa 1000 ký tự.
- `sender_id` (INT): ID của Operator (`staff_id`).
- `sender_role` (VARCHAR): 'OPERATOR'.
- `category` (VARCHAR): Phân loại sự cố (ELECTRIC, WATER, INFRASTRUCTURE, ...).
- `priority` (VARCHAR): Mức độ ưu tiên (NORMAL, URGENT).
- `attachment_urls_1` (TEXT): Link ảnh đính kèm hiện trường.
- `status` (VARCHAR): Trạng thái mặc định là 'PENDING'.
- `created_at` (DATETIME): Thời gian tạo báo cáo.

## Constraints & Validations
- **Tiêu đề (người dùng nhập)**: <= 50 ký tự.
- **Chi tiết vị trí**: <= 50 ký tự.
- **Mô tả chi tiết**: <= 1000 ký tự.
- Tất cả các trường bắt buộc (Tiêu đề, Cơ sở, Vị trí, Phân loại, Mô tả) không được null/empty.
- Độ dài và tính hợp lệ được kiểm tra cả ở frontend (HTML5 `maxlength`, JS) và backend (Java Servlet).
