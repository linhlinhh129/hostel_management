# CONTEXT.md [Danh sách yêu cầu sửa chữa]

**Người viết:** Phạm Anh Tú  
**Ngày:** 2026-06-11

---

## 1. PROBLEM STATEMENT

Nhân viên vận hành hiện đang thiếu một công cụ tập trung để theo dõi và quản lý khối lượng công việc được giao. Khi không có cái nhìn tổng quan và khả năng phân loại công việc theo mức độ ưu tiên, vị trí (cơ sở/phòng) hoặc loại yêu cầu, họ dễ rơi vào trạng thái xử lý công việc thiếu hiệu quả, mất nhiều thời gian điều phối và theo dõi các sự cố, dẫn đến chậm trễ tiến độ xử lý và ảnh hưởng trực tiếp đến KPI của bộ phận.

---

## 2. DOMAIN KNOWLEDGE

### Yêu cầu sửa chữa (Ticket/Request)
Đơn vị công việc đại diện cho một sự cố cần được xử lý.

### Trạng thái (Status)
Vòng đời của một yêu cầu, hiện tại quy định gồm 3 trạng thái:

- `pending` (chờ xử lý)
- `in_progress` (đang xử lý)
- `completed` (đã hoàn thành)

### Phân cấp vị trí (Location Hierarchy)
Hệ thống quản lý không gian vật lý theo cấp độ:

`Cơ sở (Facility) → Phòng (Room)`

### Thể loại (Category)
Phân loại nhóm sự cố hoặc loại yêu cầu.

Ví dụ:

- Điện lạnh
- Điện nước
- Mộc

### KPI Vận hành
Các chỉ số đánh giá hiệu suất, thường gắn liền với:

- Thời gian tiếp nhận
- Thời gian xử lý
- Số lượng yêu cầu được giải quyết thành công

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (End-user trực tiếp)
Người sử dụng tính năng này hàng ngày để tiếp nhận yêu cầu, theo dõi công việc được giao và quản lý tiến độ xử lý. Đây là người hưởng lợi trực tiếp từ việc tối ưu hóa UI/UX.

### Manager
Người chịu ảnh hưởng gián tiếp, quan tâm đến việc các yêu cầu được giải quyết đúng hạn (đạt KPI) và nguồn lực được phân bổ hiệu quả.

### Người thuê
Người hưởng lợi gián tiếp khi sự cố của họ được xử lý nhanh chóng hơn nhờ quy trình được tự động hóa và sắp xếp hợp lý.

---

## 4. CONSTRAINTS

### Business

- Không cho phép cập nhật trạng thái trực tiếp trên màn hình danh sách (phải vào chi tiết).
- Không yêu cầu real-time update ở giai đoạn này.

### Tech (Performance)

- Thời gian phản hồi API load danh sách (kèm bộ lọc) phải dưới mức 500ms.

### Tech (Architecture)

- Bắt buộc sử dụng Server-side pagination để tối ưu payload.

### Tech (Database)

- Bắt buộc đánh index trên các trường:
  - `assignee_id`
  - `status`
  - `facility_id`

để tối ưu câu query tìm kiếm.

---

## 5. ASSUMPTIONS

### Authentication & Authorization

Giả định rằng hệ thống phân quyền đã hoạt động chuẩn xác.

- API trả về `401` cho thấy user phải đăng nhập hợp lệ.
- User chỉ có thể nhìn thấy danh sách các yêu cầu được giao cho chính họ (`assignee`).

### Data Volume

Giả định số lượng yêu cầu sửa chữa tích lũy theo thời gian là rất lớn.

Do đó:

- Bắt buộc áp dụng phân trang.
- Limit mặc định: `20` bản ghi/trang.
- Bắt buộc đánh index DB ngay từ đầu.

### Data Integrity

Giả định các trường dữ liệu:

- `category_id`
- `facility_id`
- `room_id`

luôn tồn tại bản ghi tương ứng trong các bảng master data.

---

## 6. OPEN QUESTIONS

### 1. Secondary Sort

Nếu có nhiều yêu cầu cùng chung một `appointment_date`, tiêu chí sắp xếp phụ (secondary sort) tiếp theo sẽ là gì?

Ví dụ:

- Sắp xếp theo ID giảm dần?
- Sắp xếp theo mức độ ưu tiên (priority) nếu hệ thống có hỗ trợ?

### 2. Overdue Indicator

Có cần hiển thị cảnh báo (UI indicator như đổi màu đỏ hoặc icon) cho các yêu cầu đã quá hạn xử lý (overdue) trên danh sách để nhân viên vận hành chú ý ngay lập tức không?

### 3. Multiple Assignees

Một yêu cầu sửa chữa có bao giờ được giao cho nhiều nhân viên vận hành cùng lúc (multiple assignees) không, hay luôn luôn theo tỷ lệ 1-1?

### 4. Master Data Deactivation

Hành vi của hệ thống sẽ như thế nào đối với các yêu cầu nếu:

- `room_id`
- `facility_id`

bị vô hiệu hóa hoặc xóa khỏi hệ thống master data trong khi yêu cầu vẫn đang ở trạng thái `pending`?
