# PLAN: Kế hoạch Thực thi Tiếp nhận và xử lý yêu cầu người thuê (Manager)

**Status:** Completed  
**Date:** 2026-07-13  
**Priority:** High  
**Estimated Duration:** Completed

---

## 1. Tổng quan Giải pháp

Tính năng Tiếp nhận và xử lý yêu cầu người thuê giúp Manager giám sát, xử lý các phản hồi/sự cố từ phía cư dân và Operator.

**Kiến trúc:**
- Backend API: Servlet Controller (`ManagerTicketsServlet.java`) tiếp nhận và phân chia luồng qua các Servlet Path.
- Service & DAO: `RequestServiceImpl.java` và `RequestDAO.java` xử lý logic lưu trữ trạng thái, lịch hẹn, ghi chú giải quyết và cập nhật hình ảnh hoàn thành.
- Database: Bảng `dbo.requests` lưu thông tin chi tiết sự cố, trạng thái, người phụ trách, ảnh đính kèm ban đầu (`attachment_urls1`) và ảnh nghiệm thu (`attachment_urls2`).
- Giao diện JSP: `list.jsp` hiển thị danh sách phân trang và các bộ lọc; `detail.jsp` hiển thị chi tiết, dòng thời gian lịch sử xử lý và các nút tác vụ (tiếp nhận, từ chối, lên lịch hẹn, hoàn thành).

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Hoàn thành)
- Thiết kế luồng Servlet điều phối yêu cầu: `/manager/tickets` và `/manager/tickets/{id}/...`.
- Thiết kế cơ cấu dòng thời gian lịch sử xử lý (History timeline) động dựa trên vai trò người gửi (Cư dân hoặc Operator) và trạng thái hiện tại.
- Thiết kế cấu trúc thư mục lưu trữ file nghiệm thu `/uploads/requests/`.

### Giai đoạn 2: Backend Development (Hoàn thành)
- Implement `getManagerTickets()` và `countManagerTickets()` hỗ trợ lọc theo keyword, phân loại, trạng thái và phân trang.
- Implement các phương thức cập nhật trạng thái trong DAO: `receiveTicket()`, `rejectTicket()`, `scheduleTicket()`, `completeTicket()`.
- Xây dựng phương thức sinh dòng thời gian lịch sử (`getManagerTicketDetail()`) tự động tùy biến mô tả theo các bước chuyển đổi trạng thái thực tế.

### Giai đoạn 3: Frontend Development (Hoàn thành)
- Phát triển trang danh sách sự cố hỗ trợ tìm kiếm và chuyển đổi phân loại (Cư dân/Operator).
- Phát triển trang chi tiết hiển thị toàn diện thông tin sự cố, ảnh đính kèm và dòng thời gian xử lý sự cố.
- Thiết kế các form tương tác trực quan: form nhập lý do từ chối, form đặt lịch ngày giờ sửa chữa, và form tải ảnh nghiệm thu thực tế.

### Giai đoạn 4: Testing & Deployment (Hoàn thành)

---

## 3. Key Technical Aspects

### Status Flow Control
- The servlet and services strictly check previous states before updating (e.g. `receive` requires `NEW` or `PENDING`, updates are rejected if status is already closed).
- Uses standard status codes: `PENDING`/`NEW`, `RECEIVED`, `ASSIGNED`, `IN_PROGRESS`, `DONE`, `REJECTED`, `CANCELLED`.

### Completion Uploads
- Handled via Servlet multipart config validation.
- Validates file extensions (jpg, png, pdf) and size limit (10MB).
- Stores file paths separated by commas in `attachment_urls2`.

### Timeline Generator
- Dynamically maps DB status records to clear human-readable activities (e.g., "Tiếp nhận yêu cầu", "Đang xử lý", "Từ chối yêu cầu", "Đã hoàn thành").

---

## 4. Success Criteria

- ✓ Manager can view, filter, paginated tickets.
- ✓ Status transitions (receive, reject, schedule, complete) working successfully.
- ✓ Completion images uploaded and saved correctly.
- ✓ Ticket timeline renders correct actions.
- ✓ Operations validated under scope restrictions.

---

## 5. Timeline
- Completed.
