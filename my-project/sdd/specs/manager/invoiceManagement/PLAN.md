# Implementation Plan: Quản lý hóa đơn

## 1. Technical Context
- **Feature**: Quản lý hóa đơn (Invoice Management).
- **Core Strategy**: Tạo, xem, điều chỉnh, và in hóa đơn cho từng phòng trong kỳ. Dữ liệu giá/phí được lấy tự động (snapshot) tại thời điểm tạo.
- **Architecture**: Mô hình MVC truyền thống với Jakarta EE Servlets và JSP (`InvoiceServlet`, `InvoiceDetailServlet`).
- **Dependencies**: Bảng `invoices`, `rooms`, `users`, `facilities`, `meter_readings`.

## 2. Constitution Check
- [x] **Core Principle I (Layered Architecture)**: Không sử dụng REST API. Mọi thao tác xử lý qua `InvoiceServlet` và `InvoiceDetailServlet`, gọi xuống `InvoiceService`, trả view bằng `JSP`.
- [x] **Core Principle II (Consistent UI)**: Các trang danh sách, chi tiết, chỉnh sửa, tạo mới tuân thủ giao diện dùng chung. Khi in ấn (`window.print()`), ẩn sidebar/header theo luật CSS.
- [x] **Core Principle III (RBAC)**: Chỉ role `MANAGER` (hoặc `ADMIN`) mới được phép truy cập. Kiểm tra auth ở filter hoặc đầu mỗi phương thức Servlet.
- [x] **Core Principle IV (Safe DB Transactions)**: Thao tác tạo hóa đơn phải nằm trong 1 Database Transaction để đảm bảo tính toàn vẹn khi snapshot dữ liệu từ nhiều bảng.

## 3. Data Model
Sử dụng các bảng hiện có:
- **Thực thể chính**: `invoices`.
- **Cấu trúc DTO**:
  - `InvoiceListItemDTO`: Hiển thị danh sách (id, code, roomCode, billingPeriod, totalAmount, dueDate, status).
  - `InvoiceDetailDTO`: Chứa dữ liệu chi tiết, các loại phí thành phần, chỉ số công tơ điện nước cũ/mới, thông tin người thuê.

## 4. API / Servlet Contract
- `/manager/invoices` (`InvoiceServlet`):
  - `GET`: Xem danh sách hóa đơn (có phân trang, filter).
  - `GET ?action=create`: Hiển thị Form tạo mới.
  - `POST ?action=create`: Xử lý submit tạo hóa đơn.
- `/manager/invoices/{id}` (`InvoiceDetailServlet`):
  - `GET`: Xem chi tiết.
  - `GET .../edit`: Mở Form sửa hóa đơn.
  - `POST .../edit`: Lưu chỉnh sửa.
  - `POST .../update-status`: Đổi trạng thái.
  - `POST .../delete`: Xóa.

## 5. Phases
### Phase 0: Research & Setup
- Unknowns: Không có. Yêu cầu rõ ràng: lấy giá snapshot, in hóa đơn bằng `window.print()` trên frontend, không dùng thư viện backend sinh PDF.

### Phase 1: Models & DAOs
- Định nghĩa `InvoiceListItemDTO`, `InvoiceDetailDTO`.
- Bổ sung `InvoiceDAO` các phương thức CRUD, list phân trang và transaction tạo hóa đơn.
- Bổ sung các DAO phụ trợ để lấy snapshot (VD: giá từ `facilities`, `meter_readings`).

### Phase 2: Services
- Tạo `InvoiceService` & `InvoiceServiceImpl`.
- Xử lý logic tính tiền: tự động tính `totalAmount` từ các thông số, không cho nhập tay.
- Bắt lỗi khi không tìm thấy bảng giá cơ sở (`FACILITY_PRICE_NOT_FOUND`) hoặc thiếu chỉ số điện nước (`METER_READING_NOT_FOUND`).

### Phase 3: Servlets & UI (JSPs)
- Phát triển `InvoiceServlet`, `InvoiceDetailServlet`.
- Xây dựng `list.jsp`, `create.jsp`, `detail.jsp`, `edit.jsp`.
- Thêm script in ấn `window.print()` vào `detail.jsp` (ẩn sidebar/header bằng `@media print`).
