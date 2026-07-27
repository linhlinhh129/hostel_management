# Data Model: Báo cáo sai số điện nước (Manager & Operator Integration)

## 1. Request Entity (`dbo.requests`)

| Field Name | Data Type | Constraint | Description |
| --- | --- | --- | --- |
| `request_id` | INT | Primary Key, IDENTITY | Mã định danh yêu cầu |
| `code` | VARCHAR(20) | Unique | Mã công việc (ví dụ `REQ-UTI-001`) |
| `sender_id` | INT | Foreign Key (`users.user_id`) | Người gửi yêu cầu (Manager ID) |
| `category` | VARCHAR(20) | `'UTILITY'` | Phân loại yêu cầu sai số điện nước |
| `title` | NVARCHAR(200) | NOT NULL | Tiêu đề báo cáo sai số |
| `content` | NVARCHAR(MAX) | NOT NULL | Nội dung chi tiết sự cố điện nước |
| `status` | VARCHAR(20) | `'PENDING'`, `'IN_PROGRESS'`, `'COMPLETED'` | Trạng thái yêu cầu Operator |
| `assigned_staff_id` | INT | Foreign Key (`users.user_id`) | Operator được giao nhiệm vụ |
| `rejection_reason` / `notes` | NVARCHAR(MAX) | NULL (Optional) | Ghi chú hoàn thành/lý do từ Operator |
| `created_at` | DATETIME | NOT NULL | Thời gian tạo |
| `updated_at` | DATETIME | NOT NULL | Thời gian cập nhật |

## 2. Meter Reading Entity (`dbo.meter_readings`)

| Field Name | Data Type | Constraint | Description |
| --- | --- | --- | --- |
| `meter_id` | INT | Primary Key, IDENTITY | Mã định danh bản ghi điện nước |
| `room_id` | INT | Foreign Key (`rooms.room_id`) | Phòng tương ứng |
| `electric` | INT | NOT NULL | Chỉ số điện |
| `water` | INT | NOT NULL | Chỉ số nước |
| `status` | VARCHAR(20) | `'NORMAL'`, `'REPORTED'`, `'CORRECTED'` | Trạng thái chỉ số |

## 3. State Transition Matrix

```
[MeterReading: NORMAL] ──(Manager "Báo sai số")──► [MeterReading: REPORTED] ──(Operator Finish)──► [MeterReading: CORRECTED/NORMAL]
[Request: N/A]        ──(Manager Submit)──────► [Request: PENDING]        ──(Operator Accept)──► [Request: IN_PROGRESS] ──(Operator Complete)──► [Request: COMPLETED]
```
