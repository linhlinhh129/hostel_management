# **Feature: Tiếp nhận và xử lý yêu cầu người thuê**

Status: Completed

Author: Antigravity

Reviewer: [Tên]

Date: 2026-07-14

Priority: High

---

## **1. Business Context**

Tính năng Tiếp nhận và xử lý yêu cầu người thuê giúp Manager giám sát và giải quyết các sự cố cơ sở vật chất, hỏng hóc hoặc phản ánh dịch vụ phát sinh từ người thuê. Nó cung cấp cho Manager một kênh tiếp nhận chính thức, hỗ trợ các thao tác như tiếp nhận sự cố, gán lịch hẹn sửa chữa, từ chối yêu cầu không hợp lý, và xác nhận hoàn tất kèm hình ảnh nghiệm thu thực tế, giúp nâng cao tính chuyên nghiệp và minh bạch trong vận hành.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Manager, I want to tiếp nhận một sự cố mới do cư dân gửi lên cơ sở của tôi so that trạng thái yêu cầu chuyển sang tiếp nhận và cư dân biết yêu cầu của họ đang được xử lý.

### **Story 2 (Happy Path)**

As a Manager, I want to cập nhật lịch hẹn ngày giờ cụ thể sẽ tiến hành sửa chữa sự cố so that cư dân biết thời điểm chuẩn bị và hệ thống chuyển trạng thái sang Đang xử lý (`IN_PROGRESS`).

### **Story 3 (Happy Path)**

As a Manager, I want to xác nhận hoàn tất sự cố, nhập ghi chú giải quyết và tải lên hình ảnh sau khi sửa chữa so that yêu cầu được đóng lại (`DONE`) và cư dân nghiệm thu kết quả thực tế.

### **Story 4 (Happy Path)**

As a Manager, I want to từ chối yêu cầu không hợp lý hoặc nằm ngoài phạm vi hỗ trợ kèm theo lý do từ chối rõ ràng so that cư dân nhận được phản hồi giải thích thỏa đáng.

---

## **3. Acceptance Criteria (EARS)**

### **Tiếp nhận yêu cầu**

WHEN Manager receives a ticket in `PENDING` or `NEW` status THE SYSTEM SHALL update ticket status to `RECEIVED`.

### **Đặt lịch hẹn xử lý**

WHEN Manager schedules an appointment date for a ticket THE SYSTEM SHALL update appointment schedule AND update status to `IN_PROGRESS`.

### **Hoàn thành yêu cầu**

WHEN Manager completes a ticket with notes and optional after images THE SYSTEM SHALL update status to `DONE` AND store completion notes and attachment paths.

### **Từ chối yêu cầu**

WHEN Manager rejects a ticket with reason THE SYSTEM SHALL update status to `REJECTED` AND store rejection reason.

---

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ManagerTicketsServlet` |
| **URL Pattern** | `GET /manager/tickets` — danh sách yêu cầu sự cố |
| **URL Pattern** | `GET /manager/tickets/{id}` — xem chi tiết yêu cầu và dòng thời gian |
| **URL Pattern** | `POST /manager/tickets/{id}/receive` — tiếp nhận yêu cầu |
| **URL Pattern** | `POST /manager/tickets/{id}/schedule` — đặt lịch hẹn sửa chữa |
| **URL Pattern** | `POST /manager/tickets/{id}/complete` — xác nhận hoàn thành yêu cầu |
| **URL Pattern** | `POST /manager/tickets/{id}/reject` — từ chối yêu cầu |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `tickets` | `List<Request>` | `requestService.getManagerTickets(...)` | Danh sách yêu cầu sự cố |
| `currentPage`, `totalPages` | `int` | Xử lý logic phân trang | Phục vụ điều hướng phân trang |
| `keyword`, `status`, `type` | `String` | Query Params | Giữ lại các bộ lọc trên giao diện (form) |

---

### **4.3 Request Attributes — Chi tiết (detail.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `ticket` | `Request` | `requestService.getManagerTicketDetail(ticketId)`| Chi tiết sự cố cư dân/Operator gửi |
| `timeline` | `List<TimelineNode>` | `requestService.getTicketTimeline(ticketId)` | Dòng thời gian lịch sử các mốc thay đổi trạng thái |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Tiếp nhận yêu cầu đã đóng (`DONE`/`REJECTED`) | Gán `error` message và redirect về trang chi tiết |
| Không điền `notes` khi hoàn thành hoặc `reason` khi từ chối | Gán `error` message và redirect về trang chi tiết |
| Tải ảnh nghiệm thu vượt dung lượng (10MB) hoặc sai định dạng | Gán `error` message và redirect về trang chi tiết |
| Thao tác yêu cầu thuộc cơ sở ngoài phạm vi | Trả về lỗi `403 Forbidden` |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Manager chỉ được xử lý các sự cố thuộc phòng của cơ sở được phân công quản lý (`manager_id` trong `dbo.facilities`).
- **Validate tệp tin tải lên (File Upload):**
  - Dung lượng file ảnh nghiệm thu tối đa là 10MB cho mỗi file (cấu hình trong servlet MultipartConfig). Chỉ hỗ trợ định dạng file ảnh: `jpg`, `jpeg`, `png`, `pdf`.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải chi tiết thông tin sự cố và dòng thời gian xử lý không vượt quá **250 ms (p95)**.
  - Thời gian xử lý ghi nhận thay đổi trạng thái không vượt quá **300 ms (p95)**.

---

## **6. Out of Scope**

- Tự động phân bổ lịch hẹn hoặc chỉ định nhân viên sửa chữa dựa trên trí tuệ nhân tạo.
- Cổng thanh toán chi phí sửa chữa trực tuyến trên trang chi tiết sự cố.
- Cho phép cư dân xếp hạng sao hay đánh giá chất lượng sửa chữa sau khi hoàn thành.
