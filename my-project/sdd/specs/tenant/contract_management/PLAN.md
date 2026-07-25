# PLAN: Xem hợp đồng thuê (Tenant Contract)

Tài liệu này trình bày kế hoạch kỹ thuật (Technical Plan) chi tiết để triển khai chức năng **Xem hợp đồng thuê** cho người thuê (Tenant) theo mô hình MVC sử dụng Java Servlet, JSP và JDBC.

## 1. Kiến trúc tổng quan (Architecture Overview)

Chức năng tuân theo kiến trúc MVC chuẩn của dự án:
- **Model**: Đại diện bởi class `Contract`.
- **DAO**: Lớp truy xuất dữ liệu `ContractDAO` chịu trách nhiệm gọi các query SQL.
- **Controller**: `TenantContractServlet` điều hướng request từ client, tương tác với DAO và đẩy dữ liệu xuống View.
- **View**: Các trang JSP hiển thị giao diện danh sách và chi tiết hợp đồng (được render phía server).

## 2. Kế hoạch triển khai chi tiết (Implementation Plan)

### Bước 1: Cập nhật DAO (Data Access Object)
Thêm các phương thức cần thiết vào `ContractDAO` hiện tại (hoặc tạo mới nếu chưa có):
- **`List<Contract> getContractsByTenantId(int tenantId)`**:
  - Truy vấn SQL: `SELECT * FROM contracts WHERE tenant_id = ? ORDER BY start_date DESC`
  - Mapping dữ liệu từ `ResultSet` sang danh sách các đối tượng `Contract`.
- **`Contract getContractByIdAndTenantId(int contractId, int tenantId)`**:
  - Truy vấn SQL: `SELECT c.*, r.room_name, r.address FROM contracts c JOIN rooms r ON c.room_id = r.id WHERE c.id = ? AND c.tenant_id = ?` (Tuỳ chỉnh theo schema thực tế).
  - Trả về `Contract` nếu tìm thấy, hoặc `null` nếu không tìm thấy (giải quyết được đồng thời bài toán chống truy cập trái phép IDOR).

### Bước 2: Xây dựng Controller Servlets
Chức năng được chia thành 2 Servlet độc lập xử lý GET request từ Tenant:

1. **`TenantContractListServlet`**:
   - **URL Mapping**: `@WebServlet("/tenant/contracts")`
   - **Xử lý `doGet(...)`**:
     1. Lấy thông tin người dùng từ `request.getSession()`.
     2. Cho phép truy cập nếu có quyền/role `TENANT`.
     3. Lấy `tenantId` từ session người dùng hiện tại.
     4. Gọi `ContractDAO.getContractsByTenantId(tenantId)`.
     5. Gắn kết quả `request.setAttribute("contractList", contractList)`.
     6. Forward tới `/WEB-INF/views/tenant/contract-list.jsp`.

2. **`TenantContractDetailServlet`**:
   - **URL Mapping**: `@WebServlet("/tenant/contract-detail")`
   - **Xử lý `doGet(...)`**:
     1. Kiểm tra session đăng nhập và role `TENANT`.
     2. Đọc tham số `id` từ `request.getParameter("id")`.
     3. Gọi `ContractDAO.getContractByIdAndTenantId(id, tenantId)`.
     4. Nếu kết quả là `null` (không tồn tại hoặc không thuộc sở hữu): Forward tới trang lỗi 403 / 404 (`FORBIDDEN` / `CONTRACT_NOT_FOUND`).
     5. Nếu hợp lệ: Gắn `request.setAttribute("contract", contract)` và Forward tới `/WEB-INF/views/tenant/contract-detail.jsp`.

### Bước 3: Phát triển Giao diện (View)
- **`contract-list.jsp`** (`/WEB-INF/views/tenant/contract-list.jsp`):
  - Sử dụng JSTL (`<c:forEach>`) để lặp qua danh sách `contractList`.
  - Kiểm tra nếu `empty contractList` thì hiển thị thông báo "Bạn chưa có hợp đồng thuê nào."
  - Ngược lại hiển thị một bảng (Table) các hợp đồng. Mỗi hàng có nút "Xem chi tiết" dẫn tới `/tenant/contract-detail?id=${contract.id}`.
- **`contract-detail.jsp`** (`/WEB-INF/views/tenant/contract-detail.jsp`):
  - Sử dụng EL (Expression Language) để hiển thị chi tiết các trường của biến `contract`.
  - Bố cục giao diện mạch lạc, chia thành các nhóm thông tin: Thông tin phòng, Tiền cọc/Tiền thuê, Phí dịch vụ, Thời hạn.
  - Không chèn bất cứ form hay nút submit (Lưu/Chỉnh sửa) nào vào view này.

### Bước 4: Kiểm thử và Xử lý bảo mật
- **Bảo mật**: Cơ chế xác thực theo `tenant_id` ngay tại câu truy vấn SQL `AND tenant_id = ?` đảm bảo không có lỗ hổng IDOR.
- **Kiểm thử thủ công (Manual Testing)**:
  - Đăng nhập bằng tài khoản Tenant 1, kiểm tra danh sách hợp đồng đúng.
  - Nhấn xem chi tiết một hợp đồng, kiểm tra giao diện và dữ liệu khớp với DB.
  - Sửa tham số `?id=X` trên URL thành một ID hợp đồng của Tenant 2, kiểm tra hệ thống báo lỗi chặn lại.
  - Đăng nhập bằng tài khoản chưa có hợp đồng, giao diện thông báo hiển thị đúng như đặc tả.

## 3. Quản lý trạng thái và Timeline (Ước tính)
- **DAO & Model**: 20% thời gian (Xây dựng câu SQL và mapping).
- **Controller**: 30% thời gian (Xử lý logic, routing và bảo mật).
- **JSP Views**: 40% thời gian (Thiết kế UI/UX theo mockups nếu có, bind dữ liệu).
- **Testing**: 10% thời gian.
