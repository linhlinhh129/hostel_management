# Implementation Plan: Quản lý hóa đơn (Cập nhật hiển thị Kỳ hợp đồng)

## 1. Technical Context
- **Feature**: Quản lý hóa đơn (Invoice Management - Thêm hiển thị Kỳ hợp đồng trong Chi tiết hóa đơn).
- **Core Strategy**: Tạo, xem, điều chỉnh, và in hóa đơn cho từng phòng trong kỳ. Dữ liệu giá/phí được lấy tự động (snapshot) tại thời điểm tạo. Truy xuất kỳ hợp đồng (`c.start_date` đến `c.end_date`) từ hợp đồng liên kết với phòng để hiển thị trong trang chi tiết.
- **Architecture**: Mô hình MVC truyền thống với Jakarta EE Servlets và JSP (`InvoiceServlet`, `InvoiceDetailServlet`).
- **Dependencies**: Bảng `invoices`, `rooms`, `users`, `facilities`, `meter_readings`, `contracts`.

## 2. Constitution Check
- [x] **Core Principle I (Layered Architecture)**: Mọi thao tác xử lý qua `InvoiceServlet` và `InvoiceDetailServlet`, gọi xuống `InvoiceService`, DAO và trả view JSP.
- [x] **Core Principle II (Consistent UI)**: Các trang danh sách, chi tiết, chỉnh sửa, tạo mới tuân thủ giao diện dùng chung.
- [x] **Core Principle III (RBAC)**: Chỉ role `MANAGER` mới được phép truy cập.
- [x] **Core Principle IV (Safe DB Transactions)**: Đảm bảo giao dịch an toàn khi truy xuất và lưu trữ dữ liệu.

## 3. Data Model & Technical Updates
- **Cấu trúc DTO (`InvoiceDetailDTO`)**:
  - `contractPeriod` (`String`): Lưu chuỗi định dạng kỳ hợp đồng (ví dụ `"01/01/2026 - 31/12/2026"` hoặc `"Chưa có hợp đồng"`).
- **Truy vấn DAO (`InvoiceDAO.findById`)**:
  - Truy vấn `c.start_date AS contract_start_date, c.end_date AS contract_end_date` từ hợp đồng liên kết phòng `rooms.room_id`.
  - Format `dd/MM/yyyy - dd/MM/yyyy` và populate vào `dto.setContractPeriod(...)`.
- **View JSP (`detail.jsp`)**:
  - Thêm thẻ hiển thị `<c:out value="${invoice.contractPeriod}" default="Chưa có hợp đồng"/>` trong phần Thông tin chung.

## 4. API / Servlet Contract
- `/manager/invoices` (`InvoiceServlet`):
  - `GET`: Xem danh sách hóa đơn (có phân trang, filter).
  - `GET ?action=create`: Hiển thị Form tạo mới.
  - `POST ?action=create`: Xử lý submit tạo hóa đơn.
- `/manager/invoices/{id}` (`InvoiceDetailServlet`):
  - `GET`: Xem chi tiết (bao gồm cả Kỳ hợp đồng).
  - `GET .../edit`: Mở Form sửa hóa đơn.
  - `POST .../edit`: Lưu chỉnh sửa.
  - `POST .../update-status`: Đổi trạng thái.

## 5. Phases
### Phase 1: Models & DTOs
- Bổ sung trường `contractPeriod` vào `InvoiceDetailDTO`.

### Phase 2: DAO & Service
- Cập nhật phương thức `findById` trong `InvoiceDAO` để lấy `contract_start_date` và `contract_end_date` từ bảng `contracts`.

### Phase 3: Views (JSP)
- Bổ sung dòng hiển thị "Kỳ hợp đồng" trong `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp`.
