# Implementation Plan: Sửa Báo cáo sự cố (Edit Incident Report)

## 1. Technical Context

- **Frontend**: JSP, Bootstrap 5, Vanilla JS (sử dụng cấu trúc `layout/head.jsp`, `layout/sidebar.jsp`, v.v.). Form chỉnh sửa giống form `create.jsp` với các trường được pre-fill.
- **Backend**: Java Servlet (`EditIncidentReportServlet`). Xử lý `doGet` (lấy dữ liệu đổ ra Form) và `doPost` (validate độ dài và gọi DAO để cập nhật).
- **Database**: Sử dụng `RequestDAO.updateIncidentReport(Request req)` đã có sẵn (dùng `PreparedStatement`, kiểm tra `status = 'PENDING'` và `sender_id`).

## 2. Constitution Check

- **Layered Architecture (MVC)**: Tuân thủ (Servlet -> DAO). `EditIncidentReportServlet` gọi `RequestDAO`.
- **Role-Based Access Control (RBAC)**: Sẽ kiểm tra session login và phân quyền Operator, chỉ update được request do chính User (sender_id) tạo và đang ở trạng thái `PENDING`.
- **Safe Database Operations**: `RequestDAO.updateIncidentReport` sử dụng `PreparedStatement` nên an toàn trước SQL Injection.

## 3. Implementation Details

### UI/UX (Frontend)
- **`src/main/webapp/WEB-INF/views/operator/incidents/edit.jsp`**: 
  - Giao diện form giống `create.jsp`.
  - Hiển thị dữ liệu cũ dựa vào biến `request` truyền từ Servlet.
  - Các ô input có `maxlength` (50, 50, 1000) và JS hiển thị lỗi viền đỏ/thông báo lỗi.

### API/Controllers (Backend)
- **`src/main/java/com/quanlyphongtro/controller/operator/EditIncidentReportServlet.java`**:
  - `doGet`: Nhận `id` báo cáo. Xác thực quyền sở hữu. Lấy chi tiết báo cáo từ DAO và đổ ra `edit.jsp`.
  - `doPost`: Validate dữ liệu (Tiêu đề, Vị trí <= 50; Mô tả <= 1000). Gọi `updateIncidentReport`. Upload lại ảnh nếu có thay đổi.

### Database/Models
- **`RequestDAO`**: Sử dụng method `updateIncidentReport(Request req)` và `getRequestById(int id)` có sẵn, không cần sửa đổi.

## 4. Risks & Mitigations

- **Risk**: Người dùng sửa một báo cáo khi nó vừa mới được Quản lý (Manager) tiếp nhận chuyển sang trạng thái `IN_PROGRESS` cùng lúc.
- **Mitigation**: Câu SQL `updateIncidentReport` đã có điều kiện `status = 'PENDING'`. Nếu update không thành công (affected rows = 0), Servlet trả về lỗi báo cho user biết.
