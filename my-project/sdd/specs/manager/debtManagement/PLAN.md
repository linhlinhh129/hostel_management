# Implementation Plan: Quản lý công nợ

## 1. Technical Context
- **Feature**: Quản lý công nợ (Debt Management).
- **Core Strategy**: Không tạo bảng mới cho công nợ. Công nợ được xác định bằng các bản ghi trong bảng `invoices` có trạng thái `UNPAID` hoặc `OVERDUE`.
- **Architecture**: Servlet/JSP (MVC). Servlet `DebtPageServlet` gọi `DebtService` để lấy dữ liệu, sau đó chuyển tiếp sang các trang JSP. Tuân thủ tuyệt đối Core Principle I của Constitution.
- **Dependencies**: Bảng `invoices`, `rooms`, `users`, `facilities`, `payments`.
- **Unknowns**: None.

## 2. Constitution Check
- [x] **Core Principle I (Layered Architecture)**: Sử dụng mô hình Servlet -> Service -> DAO. Không dùng REST API trực tiếp.
- [x] **Core Principle II (Consistent UI)**: Các trang JSP phải kế thừa layout chung của Manager (`hostel-design.css`).
- [x] **Core Principle III (RBAC)**: Servlet `DebtPageServlet` phải check role `MANAGER` qua `UserSessionDTO`.
- [x] **Core Principle IV (Safe DB)**: Các truy vấn `SELECT` phải được phân trang (`page`, `size`) và không làm ảnh hưởng đến hiệu năng hệ thống.

## 3. Data Model
Sử dụng các bảng có sẵn, không tạo bảng mới:
- **Thực thể gốc**: `invoices` (status = `UNPAID`, `OVERDUE`).
- **DTOs cần tạo**:
  - `DebtListItemDTO`: Chứa thông tin rút gọn (invoiceId, mã hóa đơn, mã phòng, tên người thuê, kỳ hóa đơn, tổng tiền, ngày đến hạn, số ngày nợ, phí chậm nộp tạm tính, trạng thái).
  - `DebtDetailDTO`: Chứa thông tin chi tiết hóa đơn (tiền phòng, điện, nước, phí dịch vụ...), thông tin người thuê, và số tiền CÒN NỢ thực tế.

## 4. API / Servlet Contract
- `DebtPageServlet` (`/manager/debts`):
  - `GET /manager/debts`: Truy xuất danh sách công nợ (hỗ trợ phân trang, lọc theo status).
  - `GET /manager/debts?action=detail&id={id}`: Xem chi tiết một khoản công nợ.

## 5. Phases
### Phase 0: Research & Setup
- Đã hoàn tất. Không có Unknowns.

### Phase 1: Models & DAOs
- Tạo `DebtListItemDTO` và `DebtDetailDTO`.
- Cập nhật `InvoiceDAO` hoặc tạo `DebtDAO` chuyên dụng chứa câu query phức tạp (join 5 bảng) để lấy danh sách công nợ và tính toán số tiền đã trả (`paid_amount`).

### Phase 2: Services
- Tạo `DebtService` (và `DebtServiceImpl`).
- Implement hàm `getDebts` (xử lý logic phân trang, lọc status, tính số ngày nợ và phí chậm nộp tạm tính).
- Implement hàm `getDebtDetail` (tính toán chi tiết các khoản nợ).

### Phase 3: Servlet & UI
- Tạo `DebtPageServlet` xử lý routing và check phân quyền `MANAGER`.
- Cập nhật `list.jsp` và `detail.jsp` cho màn hình Quản lý công nợ.
