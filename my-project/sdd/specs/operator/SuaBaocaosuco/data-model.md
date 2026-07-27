# Data Model: Sửa Báo cáo sự cố (Edit Incident Report)

## 1. Entities

Feature này thao tác trên bảng dữ liệu hiện có, không tạo Entity mới. 

### `Request` (Yêu cầu/Báo cáo)

Thực thể chính chứa thông tin của báo cáo sự cố (Tương ứng với bảng `requests` trong CSDL).

#### Fields (Trường thông tin)

| Tên trường (DB) | Kiểu dữ liệu | Ánh xạ Java | Mục đích sử dụng | Ràng buộc validation |
|---|---|---|---|---|
| `request_id` | INT | `id` | Khóa chính, định danh báo cáo | Bắt buộc (sử dụng trong mệnh đề WHERE) |
| `sender_id` | INT | `senderId` | ID của nhân viên vận hành tạo báo cáo | Bắt buộc (kiểm tra quyền sở hữu báo cáo) |
| `status` | VARCHAR | `status` | Trạng thái hiện tại | Bắt buộc `PENDING` mới cho phép sửa |
| `category` | VARCHAR | `category` | Phân loại sự cố (Điện, nước, v.v.) | Bắt buộc |
| `title` | VARCHAR | `title` | Tiêu đề báo cáo sự cố | **Tối đa 50 ký tự**, Bắt buộc |
| `content` | NVARCHAR | `content` | Mô tả chi tiết kèm chi tiết vị trí | **Tối đa 1000 ký tự**, Bắt buộc (chi tiết vị trí <= 50 ký tự) |
| `attachment_urls1` | VARCHAR | `attachmentUrls1` | Các URL ảnh đính kèm của sự kiện | Tùy chọn (Tối đa 3 ảnh) |
| `updated_at` | DATETIME | `updatedAt` | Thời gian cập nhật cuối cùng | Tự động cập nhật thành `GETDATE()` |

## 2. Relationships

- `Request` (`sender_id`) -> `User` (`user_id`): Many-to-One. Một user có thể tạo nhiều request. Khi cập nhật cần đảm bảo User đang đăng nhập (`currentUser`) trùng khớp với `sender_id`.

## 3. Data Flow

Khi sửa báo cáo:
1. Servlet lấy `request_id` từ URL (VD: `?id=123`).
2. Servlet kiểm tra trạng thái (`status == 'PENDING'`) và người tạo (`sender_id == currentUser.getId()`).
3. Servlet nhận dữ liệu Form và tiến hành Validate (Title <= 50, Location <= 50, Content <= 1000).
4. Servlet truyền đối tượng `Request` sang `RequestDAO.updateIncidentReport(Request)`.
5. DAO thực thi: `UPDATE requests SET category=?, title=?, content=?, attachment_urls1=?, updated_at=GETDATE() WHERE request_id=? AND status='PENDING' AND sender_id=?`.
6. Trả kết quả: True (Redirect kèm success) / False (Trả lại Form kèm error).
