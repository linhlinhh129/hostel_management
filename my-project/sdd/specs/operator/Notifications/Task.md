# Checklist công việc (Task): Chức năng xem thông báo Operator

- [x] **1. Tầng Dữ liệu (DAO)**
  - [x] Mở file `NotificationDAO.java`.
  - [x] Thêm hàm `findNotificationsForOperator(int facilityId, int offset, int limit)`.
  - [x] Thêm hàm `countNotificationsForOperator(int facilityId)`.
  
- [x] **2. Tầng Điều khiển (Servlet)**
  - [x] Tạo file `NotificationListServlet.java` tại `com/quanlyphongtro/controller/operator`.
  - [x] Viết logic lấy `facilityId` từ session/DB.
  - [x] Gọi DAO để lấy danh sách thông báo và truyền vào Request.
  
- [x] **3. Tầng Giao diện (JSP & UI)**
  - [x] Tạo file `notifications.jsp` tại `/WEB-INF/views/operator/`.
  - [x] Code HTML/CSS/Tailwind để hiển thị danh sách dạng thẻ (Card) hoặc bảng (Table).
  - [x] Bổ sung hiệu ứng (Modal hoặc Accordion) để xem nội dung chi tiết.
  - [x] Tích hợp thanh phân trang (Pagination).
  
- [x] **4. Tầng Điều hướng (Sidebar)**
  - [x] Mở file Sidebar (vd: `sidebar-operator.jsp`).
  - [x] Thêm liên kết `<a href="${ctx}/operator/notifications">` với icon phù hợp.
  
- [ ] **5. Kiểm thử (Testing)**
  - [ ] Login tài khoản `OPERATOR` và truy cập trang xem thông báo.
  - [ ] Đảm bảo dữ liệu hiển thị đúng (có nội dung từ `ALL` và `FACILITY` trùng khớp).
  - [ ] Đảm bảo chặn quyền với người dùng `TENANT` hoặc chưa đăng nhập.
