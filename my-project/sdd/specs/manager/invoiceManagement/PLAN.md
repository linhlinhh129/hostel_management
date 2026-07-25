# Implementation Plan: Quản lý hóa đơn

## 1. Technical Context
- **Feature**: Quản lý hóa đơn (Invoice Management).
- **Core Strategy**: 
  - Tạo, xem, điều chỉnh, và in hóa đơn cho từng phòng trong kỳ. 
  - Truy xuất kỳ hợp đồng từ hợp đồng liên kết với phòng để hiển thị trong trang chi tiết.
  - Loại bỏ hoàn toàn việc tính Thuế (Tax/VAT) và chức năng Xóa Hóa Đơn khỏi hệ thống. 
  - Nút Báo cáo sai số sẽ không thay đổi trạng thái hóa đơn.
  - **Quy tắc đóng băng phí phạt (Late Fee Freeze)**: Phí phạt muộn sẽ tự động bị "đóng băng" (ngừng đếm số ngày nợ) nếu có giao dịch thanh toán chờ duyệt (PENDING) liên quan đến hóa đơn đó. Khi quản lý duyệt, phí phạt bị đóng băng được lưu vào CSDL; nếu từ chối, hệ thống tiếp tục đếm lại phí phạt.
- **Architecture**: Mô hình MVC truyền thống với Jakarta EE Servlets và JSP (`InvoiceServlet`, `InvoiceDetailServlet`).
- **Dependencies**: Bảng `invoices`, `rooms`, `users`, `facilities`, `meter_readings`, `contracts`, `notifications`, `payments`.

## 2. Constitution Check
- [x] **Core Principle I (Layered Architecture)**: Mọi thao tác xử lý qua `InvoiceServlet` và `InvoiceDetailServlet`, gọi xuống `InvoiceService`, DAO và trả view JSP.
- [x] **Core Principle II (Consistent UI)**: Các trang danh sách, chi tiết, chỉnh sửa, tạo mới tuân thủ giao diện dùng chung.
- [x] **Core Principle III (RBAC)**: Chỉ role `MANAGER` mới được phép truy cập.
- [x] **Core Principle IV (Safe DB Transactions)**: Đảm bảo giao dịch an toàn khi truy xuất và lưu trữ dữ liệu.

## 3. Data Model & Technical Updates
- **Cấu trúc DTO (`InvoiceDetailDTO`)**:
  - `contractPeriod` (`String`): Lưu chuỗi định dạng kỳ hợp đồng.
  - Xóa bỏ các trường `taxRate`, `taxAmount`.
- **Thực thể Model (`Invoice`)**:
  - Xóa bỏ trường `taxRate`.
- **Truy vấn DAO (`InvoiceDAO`)**:
  - Lấy `contract_start_date`, `contract_end_date` từ `contracts`.
  - Loại bỏ các logic tính toán `tax_amount` hoặc lưu `tax_rate`.
  - Tính `total_amount` = `subtotal` + `late_fee` (không có tax).
  - **Freeze Late Fee**: Sử dụng subquery `(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date` để nạp vào thay cho ngày hiện tại khi tính toán ngày quá hạn.
- **View JSP (`detail.jsp`, `create.jsp`, `edit.jsp`)**:
  - Thêm thẻ hiển thị `contractPeriod` trong chi tiết.
  - Loại bỏ các field nhập liệu `taxRate` và các dòng hiển thị "Tiền thuế" hoặc "Thuế (%)".
  - Loại bỏ nút "Xóa hóa đơn".
  - Cập nhật logic submit của nút "Báo cáo sai số" để không đổi trạng thái hóa đơn.

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
- Xóa bỏ trường `taxRate`, `taxAmount` khỏi `Invoice`, `InvoiceDetailDTO` và `InvoiceCreateDTO`/`InvoiceUpdateDTO` (nếu có).

### Phase 2: DAO & Service
- Cập nhật phương thức `findById` trong `InvoiceDAO` để lấy `contract_start_date` và `contract_end_date`.
- Cập nhật các câu SQL Insert, Update trong `InvoiceDAO` để loại bỏ `tax_rate`.
- Cập nhật logic tính `total_amount` không bao gồm thuế.
- Bổ sung truy xuất `pending_payment_date` vào các câu query đọc dữ liệu tính `late_fee`. *(Lưu ý: Đã hoàn tất trong mã nguồn)*.
