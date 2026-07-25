<<<<<<< HEAD
# PLAN: Kế hoạch Thực thi Quản lý Hợp đồng (Manager)

**Status:** Planned  
**Date:** 2026-07-24  
**Author:** Nhật 
**Priority:** High  
**Estimated Duration:** 3 Sprints (6 tuần)

---

## 1. Tổng quan Giải pháp

Feature **Quản lý hợp đồng** cung cấp quy trình quản lý vòng đời hợp đồng thuê phòng trọ dành cho Ban quản lý (Manager), bao gồm: lập hợp đồng mới, tra cứu danh sách, xem chi tiết, tự động trích xuất thông tin phòng/giá/cọc, tạo nhanh tài khoản người thuê (`TENANT`) từ hợp đồng, in hợp đồng theo mẫu chuẩn và xóa mềm các hợp đồng đã hết hiệu lực (`INACTIVE`).

**Kiến trúc:**
- **Backend Entry Point**: `ContractServlet.java` (`/manager/contracts/*`) xử lý điều phối request (List, Detail, Create, Print, Add-Tenant, Delete).
- **Service Layer**: `ContractServiceImpl.java` thực thi quy tắc nghiệp vụ validate dữ liệu, tự động sinh mã hợp đồng (`HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`), chụp ảnh snapshot thông tin phòng/giá tại thời điểm lập hợp đồng, kiểm tra xung đột hợp đồng `ACTIVE` trên phòng, và giao dịch liên kết người thuê (`tenant_id`).
- **Data Access Layer**: `ContractDAOImpl.java` sử dụng JDBC thuần (`PreparedStatement`), truy vấn phân quyền theo cơ sở được phân công (`manager_facilities`), hỗ trợ transaction khi tạo hợp đồng & gán người thuê.
- **Frontend JSP**: 
  - `list.jsp`: Danh sách hợp đồng phân trang, bộ lọc tìm kiếm theo tên khách thuê/mã hợp đồng.
  - `detail.jsp`: Chi tiết hợp đồng, hiển thị đầy đủ thông tin bên A, bên B, thông tin phòng, giá thuê, tiền cọc và nút hành động (In, Tạo tài khoản tenant, Xóa).
  - `create.jsp`: Form lập hợp đồng mới, tự động fill dữ liệu phòng khi chọn.
  - `add_tenant.jsp`: Form điền thông tin người thuê từ hợp đồng để cấp tài khoản.
  - `print.jsp`: Giao diện in hợp đồng thuê phòng trọ đúng chuẩn pháp lý.

---

## 2. Giai đoạn Thực thi (Phân chia công việc)

### Giai đoạn 1: Cơ sở dữ liệu & Data Access (Database & DAO Layer)
- Cấu trúc bảng `contracts` (`contract_id`, `code`, `room_id`, `tenant_id`, `tenant_full_name`, `tenant_dob`, `tenant_address`, `tenant_identity`, `tenant_identity_date`, `tenant_identity_place`, `tenant_phone`, `rent_price`, `deposit_amount`, `amount_in_words`, `signed_date`, `start_date`, `end_date`, `status`, `created_by`, `created_at`, `updated_at`, `deleted_at`).
- Xây dựng `ContractDAOImpl.java` với các hàm query: `getContractsByManager`, `getContractById`, `createContract`, `updateContractStatus`, `linkTenantId`, `softDeleteContract`.
- Đảm bảo tất cả query đều join `rooms` và `facilities` để kiểm tra phân quyền Manager cơ sở (`manager_facilities`).

### Giai đoạn 2: Nghiệp vụ Backend & Servlet Routing (Service & Controller Layer)
- Xây dựng `ContractServiceImpl.java`:
  - Logic tự động sinh mã `code` (`HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`).
  - Validation: Bắt buộc họ tên, CCCD, ngày lập, ngày hết hạn (`endDate >= signedDate`), phòng không được có hợp đồng `ACTIVE` khác.
  - Giao dịch tạo tài khoản `TENANT` từ hợp đồng (kiểm tra trùng Email/CCCD, gửi pass tạm, kích hoạt lại người dùng cũ nếu bị vô hiệu hóa).
- Xây dựng `ContractServlet.java` map URL `/manager/contracts/*`:
  - `GET /manager/contracts` (danh sách)
  - `GET /manager/contracts/detail` (chi tiết)
  - `GET/POST /manager/contracts/create` (form & lưu hợp đồng)
  - `GET /manager/contracts/print` (xem mẫu in)
  - `GET/POST /manager/contracts/add-tenant` (cấp tài khoản)
  - `POST /manager/contracts/delete` (xóa hợp đồng `INACTIVE`)

### Giai đoạn 3: Phát triển Giao diện JSP (Frontend Layer)
- Xây dựng các trang JSP trong `/WEB-INF/views/manager/contracts/`:
  - `list.jsp`: Bảng hợp đồng, trạng thái badge, thanh tìm kiếm.
  - `detail.jsp`: Thẻ thông tin khách thuê, phòng thuê, nút in hợp đồng & cấp tài khoản.
  - `create.jsp`: Dropdown chọn phòng rảnh, tự động load giá phòng & tiền cọc qua JavaScript AJAX/Event.
  - `print.jsp`: Layout CSS `@media print` cho hợp đồng in 2 bản chuẩn Việt Nam.
  - `add_tenant.jsp`: Form điền thông tin cấp tài khoản người thuê.

### Giai đoạn 4: Kiểm thử & Đảm bảo Chất lượng (QA & Verification)
- Unit tests & Integration tests cho `ContractServiceImpl` và `ContractDAOImpl`.
- Test phân quyền RBAC: Manager cơ sở A không xem/sửa được hợp đồng cơ sở B.
- Test mẫu in: Hiển thị đúng 4 Điều khoản, số tiền bằng chữ, chữ ký 2 bên.

---

## 3. Các Điểm Kỹ thuật Trọng tâm (Key Technical Aspects)

### 1. Snapshot Dữ liệu Phòng & Tiền Cọc
- Khi tạo hợp đồng, lưu trực tiếp `rent_price` và `deposit_amount` vào bản ghi hợp đồng tại thời điểm lập. Việc này đảm bảo giá hợp đồng cũ không bị ảnh hưởng nếu bảng `rooms` hoặc `facilities` thay đổi giá sau này.

### 2. Thuật toán Sinh Mã Hợp đồng Tự động
- Mã hợp đồng có định dạng: `HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`.
- Hệ thống truy vấn số hợp đồng trong ngày để tăng giá trị `seq` (ví dụ: `001`, `002`), tránh trùng lặp mã hợp đồng khi lập nhiều hợp đồng trong 1 ngày cho cùng 1 phòng.

### 3. Quy trình Cấp Tài khoản Người Thuê từ Hợp đồng
- Lấy thông tin từ hợp đồng làm mặc định.
- Nếu Email/CCCD chưa tồn tại: Tạo user mới với vai trò `TENANT`, cấp pass tạm thời, đổi status phòng sang `OCCUPIED`, gán `tenant_id` vào hợp đồng.
- Nếu người dùng từng ở và bị vô hiệu hóa (`INACTIVE`): Hiển thị modal confirm để kích hoạt lại account.

### 4. Xóa Mềm Hợp đồng (Soft Delete)
- Chỉ cho phép xóa khi hợp đồng có trạng thái `INACTIVE`.
- Đánh dấu `deleted_at = GETDATE()`, tuyệt đối không `DELETE` vật lý để bảo toàn dữ liệu đối soát lịch sử tài chính & công nợ.

---

## 4. Tiêu chí Thành công (Success Criteria)

- ✓ Manager xem đúng danh sách hợp đồng thuộc cơ sở phụ trách.
- ✓ Không thể tạo 2 hợp đồng `ACTIVE` trên cùng một phòng tại một thời điểm.
- ✓ Tự động trích xuất giá phòng, tiền cọc và tầng khi chọn phòng.
- ✓ In hợp đồng đẹp mắt, đúng định dạng A4 kèm đầy đủ 4 Điều khoản pháp lý.
- ✓ Tạo tài khoản người thuê thành công và cập nhật `tenant_id` vào hợp đồng.
- ✓ Ghi AuditLog đầy đủ cho mọi thao tác CREATE, ADD_TENANT, DELETE hợp đồng.

---

## 5. Timeline dự kiến
- **Sprint 1**: Database schema & DAO & Service validation.
- **Sprint 2**: Controller routing & Forms (List, Create, Detail, Print).
- **Sprint 3**: Add-Tenant integration, Soft Delete & Quality Assurance.
=======
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
>>>>>>> 0a012ed157162344f6596bc5e8c74df558911c85
