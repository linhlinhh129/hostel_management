# PLAN: Kế hoạch Thực thi Dashboard cho Ban quản lý (Manager)

**Status:** Completed  
**Date:** 2026-07-13  
**Priority:** High  
**Estimated Duration:** Completed

---

## 1. Tổng quan Giải pháp

Tính năng Dashboard cho Ban quản lý cung cấp màn hình chính khi Manager đăng nhập hệ thống, hiển thị toàn bộ các chỉ số vận hành và tài chính của cơ sở được phân công theo thời gian thực.

**Kiến trúc:**
- Controller: `ManagerDashboardServlet.java` xử lý yêu cầu GET `/manager/dashboard`, nhận diện `managerId` của Manager đang đăng nhập từ Session, gọi Service để lấy bản đồ thống kê rồi lưu vào Request Attributes.
- Service & DAO: `DashboardServiceImpl.java` và `DashboardDAO.java` phối hợp truy vấn và tính toán các số liệu chỉ số từ 7 thực thể khác nhau trong cơ sở dữ liệu.
- Frontend: JSP hiển thị các số liệu dạng thẻ chỉ số (metrics card) trực quan và bảng danh sách sự cố gần nhất.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Thiết lập SQL (Hoàn thành)
- Thiết kế bố cục giao diện Dashboard gồm 4 nhóm thẻ chính: Chỉ số phòng/Cư dân, Chỉ số tài chính, Chỉ số sự cố, và Bảng sự cố gần nhất.
- Viết các câu lệnh SQL tối ưu hóa để gom nhóm, đếm và tính toán doanh thu tháng hiện tại cũng như tổng nợ quá hạn.

### Giai đoạn 2: Phát triển Backend (Hoàn thành)
- Xây dựng `DashboardDAO.java` thực hiện tuần tự các truy vấn lấy thông tin cơ sở, tính toán tỷ lệ lấp đầy, tính tổng tiền hóa đơn chưa thu và đã thu trong tháng hiện tại.
- Xây dựng `DashboardServiceImpl.java` làm cầu nối gọi DAO và xử lý các giá trị mặc định khi Manager chưa được gán cơ sở.
- Phát triển `ManagerDashboardServlet.java` nạp dữ liệu vào Request và chuyển tiếp (forward) sang trang hiển thị.

### Giai đoạn 3: Phát triển Giao diện JSP (Hoàn thành)
- Thiết kế giao diện `dashboard.jsp` sử dụng các class CSS định hình khung hiển thị.
- Hiển thị danh sách 5 sự cố mới nhất kèm thẻ trạng thái (badge) đổi màu linh hoạt theo từng tình trạng của yêu cầu.

### Giai đoạn 4: Kiểm thử và Tích hợp (Hoàn thành)

---

## 3. Key Technical Aspects

### Financial Calculation Logic
- **Monthly Revenue:** Sums up `total_amount` of invoices with status `'PAID'` created in the current month (`MONTH(created_at) = MONTH(GETDATE())`).
- **Total Outstanding:** Sums up `total_amount` of invoices with status `'UNPAID'` or `'OVERDUE'`.

### Ticket Aggregation
- Maps DB statuses (`PENDING`, `NEW`, `RECEIVED`, `ASSIGNED`, `IN_PROGRESS`, `DONE`, `REJECTED`) into 4 main categories displayed on the Dashboard dashboard metrics card.

---

## 4. Success Criteria

- ✓ Dashboard loads within < 300ms despite multiple queries.
- ✓ Displays stats only for the manager's assigned facility.
- ✓ Safe fallback if manager has no assigned facilities.
- ✓ Ticket timeline and badges display correct localized labels.

---

## 5. Timeline
- Completed.
