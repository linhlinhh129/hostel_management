# Data Model: Danh sách yêu cầu sửa chữa

## Entities

### Request (Yêu cầu)
Bảng lưu trữ thông tin về yêu cầu sửa chữa.
- `request_id` (INT, PK): Mã yêu cầu.
- `title` (VARCHAR): Tiêu đề yêu cầu.
- `category` (VARCHAR): Thể loại (ELECTRICITY, WATER, OTHER). Dữ liệu này sẽ được dịch sang tiếng Việt trên giao diện.
- `status` (VARCHAR): Trạng thái (PENDING, IN_PROGRESS, RESOLVED). Dữ liệu này sẽ được dịch sang tiếng Việt trên giao diện.
- `room_id` (INT, FK): Mã phòng. Liên kết với bảng `rooms` để lấy số phòng.
- `created_at` (DATETIME): Ngày tạo.
- `assignee_id` (INT, FK): Mã nhân viên vận hành được giao (Operator ID).
- `facility_id` (INT, FK): Cơ sở (dùng để lọc và kiểm soát).

## Relationships
- `Request.room_id` -> `Room.room_id`
- `Request.assignee_id` -> `User.user_id`
- `Request.facility_id` -> `Facility.facility_id`
