# PLAN.md: Quản lý hợp đồng (Contract Management)

## 1. Mục tiêu
Xây dựng module quản lý hợp đồng dành cho Ban quản lý (Manager), hỗ trợ số hóa việc lập, lưu trữ và theo dõi hợp đồng thuê phòng. Tự động hóa các luồng như trích xuất thông tin giá phòng/tiền cọc, sinh mã hợp đồng và kết nối người thuê với hệ thống tài khoản.

## 2. Phạm vi
- **Giao diện Danh sách:** Hiển thị hợp đồng thuộc các cơ sở do Ban quản lý phụ trách.
- **Giao diện Chi tiết:** Xem thông tin chi tiết hợp đồng, thông tin phòng, người thuê và in hợp đồng theo mẫu.
- **Tạo hợp đồng mới:** Tự động điền giá phòng, tiền cọc từ DB `rooms` / `facilities`. Sinh mã hợp đồng duy nhất.
- **Xử lý tài khoản người thuê:** Cho phép tạo tài khoản (Role `TENANT`) từ dữ liệu hợp đồng và liên kết `tenant_id` vào hợp đồng.
- **Xóa (Soft delete):** Chỉ cho phép xóa khi hợp đồng ở trạng thái `INACTIVE`.

## 3. Giải pháp kỹ thuật
### 3.1 Cấu trúc Dữ liệu (Database)
- **Bảng `contracts`**: 
  - `contract_id` (PK), `code` (Mã sinh tự động `HD-{roomCode}-{date}-{seq}`).
  - `room_id` (FK), `tenant_id` (FK - nullable lúc mới tạo chưa có tài khoản).
  - Thông tin snapshot (để giữ nguyên giá trị tại thời điểm tạo): `rent_price`, `deposit_amount`, `floor`, v.v.
  - Thông tin người thuê: `tenant_full_name`, `tenant_dob`, `tenant_identity_number`, `tenant_phone`, v.v.
  - Thông tin thời gian: `signed_date`, `start_date`, `end_date`, `created_at`, `updated_at`.
  - `status` (`ACTIVE`, `INACTIVE`).
- **Liên kết**: 
  - Join `rooms` và `facilities` để lấy dữ liệu in hợp đồng và xác thực quyền quản lý của Manager.
  - Cập nhật bảng `users` khi tạo mới tài khoản Tenant.

### 3.2 Luồng xử lý (Servlet & JSP)
Sử dụng kiến trúc MVC thuần túy (Servlet, Service, DAO, JSP).
- **Entry Point**: `ContractServlet` (`@WebServlet("/manager/contracts")`).
- **Giao diện**: Nằm tại thư mục `src/main/webapp/WEB-INF/views/manager/contracts/`.

| Hành động | Endpoint | JSP tương ứng | Logic xử lý chính |
|---|---|---|---|
| Xem danh sách | `GET /manager/contracts` | `list.jsp` | Truy vấn `ContractDAO`, phân quyền qua `managerId`. |
| Giao diện tạo | `GET /manager/contracts/create` | `create.jsp` | Lấy danh sách các phòng trống (`availableRooms`). |
| Lưu hợp đồng | `POST /manager/contracts/create` | `create.jsp` | Validate ngày, số CCCD, SDT. Lưu DB. Sinh mã `code`. |
| Xem chi tiết | `GET /manager/contracts/detail?id=...`| `detail.jsp` | Lấy dữ liệu hợp đồng, join với phòng và cơ sở. |
| In hợp đồng | Render tại Client | Trình duyệt | Gọi JS `window.print()` kết hợp CSS `@media print` trên `detail.jsp` / trang in chuyên dụng. |
| Form tạo Tenant| `GET /manager/contracts/add-tenant`| `add_tenant.jsp`| Prefill thông tin từ DB. |
| Lưu tạo Tenant | `POST /manager/contracts/add-tenant`| `add_tenant.jsp`| Validate Account, tạo user, gán `tenant_id`, đổi status phòng `OCCUPIED`. |
| Xóa hợp đồng | `POST /manager/contracts/delete` | (Redirect) | Chuyển `status` = `INACTIVE` (hoặc soft delete) nếu đủ điều kiện. |

*(Lưu ý: Mặc dù code có thể chứa thêm endpoint `/manager/contracts/extend`, nhưng tính năng Gia hạn được đặt là Out Of Scope trong SPEC, do vậy có thể bỏ qua hoặc coi là tính năng bổ sung không chính thức).*

## 4. Quy tắc nghiệp vụ & Validation
- Form yêu cầu kiểm tra kiểu dữ liệu và bắt buộc nhập đối với các trường thông tin cá nhân khách thuê và phòng thuê.
- Validation Backend: `NumberFormatException`, `IllegalArgumentException`, `DateTimeParseException` khi nhận POST parameters. Nếu lỗi sẽ forward lại form kèm `errorMessage`.
- Không thể tạo hợp đồng nếu phòng đã có hợp đồng `ACTIVE`.
- Chỉ người có quyền `MANAGER` hoặc `ADMIN` (xác thực qua session `currentUser`) mới được sử dụng các endpoint này.
- Khi tạo tài khoản Tenant: Email phải duy nhất, CCCD/Phone không trùng lặp.

## 5. Rủi ro & Giả định
- **Rủi ro:** Khi giá dịch vụ (Điện, Nước) trong bảng `facilities` thay đổi, bản in hợp đồng cũ có thể hiển thị sai lệch nếu không lưu snapshot giá dịch vụ. (Cách khắc phục: Lưu text nội dung in hoặc snapshot toàn bộ chi phí tại thời điểm `signed_date`).
- **Giả định:** Người thuê có thể không cần tạo tài khoản hệ thống ngay lúc làm hợp đồng (lưu trữ offline), nên trường `tenant_id` trong hợp đồng được phép NULL ban đầu.
