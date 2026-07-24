# TASKS: Quản lý Nhân sự (personnelManagement)

**Status:** In Progress
**Priority:** High

Tài liệu quản lý danh sách đầu việc và tiến độ triển khai cho tính năng Quản lý Nhân sự của Admin.

---

## 1. Thiết kế & Cơ sở dữ liệu (Database)
Thiết kế và chuẩn bị các bảng dữ liệu liên quan đến tài khoản người dùng và cơ sở quản lý.

- [x] Sử dụng bảng `dbo.users` lưu trữ thông tin nhân sự với các cột: `identity_number` (CCCD), `dob` (ngày sinh), `gender` (giới tính), `permanent_address` (địa chỉ thường trú).
- [x] Thiết lập ràng buộc khóa ngoại `manager_id` và `operator_id` trong bảng `dbo.facilities` trỏ tới `dbo.users(user_id)`.
- [x] Đảm bảo các cột `email`, `phone`, và `identity_number` trong bảng `dbo.users` có ràng buộc UNIQUE và hỗ trợ chỉ số (Index) để tìm kiếm nhanh.

---

## 2. Tầng Data Access Object (DAO)
Xây dựng các câu truy vấn và phương thức tương tác CSDL trong lớp `PersonnelDAO`.

- [x] Triển khai `PersonnelDAO.findAll(...)` hỗ trợ tìm kiếm theo keyword (tên, email, số điện thoại, mã), lọc theo vai trò (`MANAGER`/`OPERATOR`), trạng thái (`ACTIVE`/`INACTIVE`), sắp xếp theo ngày tạo giảm dần (`createdAt DESC`), và phân trang.
- [x] Triển khai `PersonnelDAO.count(...)` để đếm tổng số lượng nhân sự thỏa mãn bộ lọc phục vụ phân trang.
- [x] Triển khai `PersonnelDAO.findById(id)` lấy thông tin chi tiết nhân sự.
- [x] Triển khai `PersonnelDAO.findFacilityNamesForUser(userId)` để lấy danh sách tên các cơ sở mà nhân sự đang quản lý/vận hành (Enrich data).
- [x] Triển khai `PersonnelDAO.findFacilityIdForUser(userId)` để xác định ID cơ sở mà nhân sự đang được gán.
- [x] Triển khai `PersonnelDAO.findFacilitiesForManager(...)` và `findFacilitiesForOperator(...)` để lấy danh sách cơ sở `ACTIVE` chưa được gán cho ai (kèm loại trừ chính ID đang chỉnh sửa để hiển thị trên form).
- [x] Triển khai các hàm kiểm tra trùng lặp: `existsByEmail(...)`, `existsByPhone(...)`, `existsByIdentityNumber(...)`.
- [x] Triển khai các hàm gán và hủy gán cơ sở: `assignFacility(...)`, `unassignFacility(...)`, `assignOperatorFacility(...)`, `unassignOperatorFacility(...)`.
- [x] Triển khai các hàm đếm số lượng nhân sự active trên một cơ sở để phục vụ kiểm tra ràng buộc duy nhất (mỗi cơ sở tối đa 1 Manager và 1 Operator): `countActiveManagerForFacility(...)`, `countActiveOperatorForFacility(...)`.

---

## 3. Tầng Service (Business Logic)
Đóng gói xử lý nghiệp vụ, kiểm tra tính hợp lệ dữ liệu trong `PersonnelService` và `PersonnelServiceImpl`.

- [x] Triển khai phương thức danh sách `list(...)` và chi tiết `getById(...)` kèm theo enrich dữ liệu tên cơ sở.
- [x] Triển khai nghiệp vụ Tạo nhân sự `create(...)`:
    - [x] Kiểm tra các trường bắt buộc không được trống (Họ tên, email, số điện thoại, CCCD).
    - [x] Kiểm tra định dạng email và số điện thoại di động Việt Nam (10 chữ số).
    - [x] Kiểm tra độ dài và tính hợp lệ của số CCCD (9 hoặc 12 số).
    - [x] Kiểm tra trùng lặp email, số điện thoại, CCCD trong hệ thống.
    - [x] Kiểm tra ngày sinh không được lớn hơn ngày hiện tại.
    - [x] Sinh mật khẩu tạm ngẫu nhiên, mã hóa BCrypt, thiết lập trạng thái tài khoản là `ACTIVE` và `forceChangePass = true`.
    - [x] **[BUG]** Kiểm tra bắt buộc phải chọn cơ sở quản lý (`facilityId` không được null/trống) đối với vai trò `MANAGER` và `OPERATOR` khi Tạo mới.
    - [x] Kiểm tra cơ sở được gán phải tồn tại và đang `ACTIVE`.
    - [x] Kiểm tra cơ sở chưa có nhân sự cùng vai trò hoạt động (chặn trùng lặp).
    - [x] Gọi DAO để lưu nhân sự và gán cơ sở.
    - [x] Gửi mật khẩu tạm thời đến email của nhân sự một cách bất đồng bộ (async).
- [x] Triển khai nghiệp vụ Cập nhật thông tin `update(...)`:
    - [x] Kiểm tra các trường bắt buộc, định dạng dữ liệu, ngày sinh, tính duy nhất của email/sđt/CCCD (loại trừ tài khoản hiện tại).
    - [x] Kiểm tra bắt buộc phải chọn cơ sở quản lý cho `MANAGER` hoặc `OPERATOR`.
    - [x] Thu hồi cơ sở cũ và gán cơ sở mới (xử lý đổi vai trò hoặc đổi cơ sở).
- [x] Triển khai nghiệp vụ Khóa/Mở khóa tài khoản `toggleStatus(...)`:
    - [x] Chuyển đổi trạng thái tài khoản giữa `ACTIVE` và `INACTIVE`.
    - [x] Ngăn chặn Admin tự khóa tài khoản của chính mình (ném lỗi).

---

## 4. Tầng Controller (Servlet)
Xây dựng lớp điều phối luồng web `AdminPersonnelServlet`.

- [x] Ánh xạ URL patterns: `/admin/personnel` và `/admin/personnel/*`.
- [x] Phân quyền truy cập thông qua bộ lọc `RoleFilter` (bắt buộc vai trò là `ADMIN`).
- [x] Xử lý `doGet` chuyển tiếp dữ liệu đến các trang JSP:
    - [x] Trang danh sách `/admin/personnel` (nhận tham số lọc, phân trang).
    - [x] Trang tạo mới `/admin/personnel/create` (tải danh sách cơ sở có thể gán).
    - [x] Trang chi tiết `/admin/personnel/{id}`.
    - [x] Trang chỉnh sửa `/admin/personnel/{id}/edit`.
- [x] Xử lý `doPost` nhận request cập nhật:
    - [x] Tạo mới `/admin/personnel/create`. Nếu validation thất bại, giữ lại dữ liệu form bằng `PersonnelFormDTO` và forward lại trang kèm `errorMessage`.
    - [x] Chỉnh sửa `/admin/personnel/{id}/edit`. Nếu lỗi, forward lại kèm dữ liệu cũ và thông báo lỗi.
    - [x] Thay đổi trạng thái `/admin/personnel/{id}/status`.

---

## 5. Giao diện người dùng (JSP / View)
Thiết kế trang giao diện theo hệ thống thiết kế chung của dự án.

- [x] Trang danh sách (`list.jsp`): Hiển thị bảng nhân sự, bộ lọc tìm kiếm/vai trò/trạng thái, phân trang, nút chuyển sang chi tiết/sửa/khóa.
- [x] Trang chi tiết (`detail.jsp`): Xem toàn bộ thông tin cá nhân và thông tin cơ sở quản lý.
- [x] Trang tạo mới (`create.jsp`): Biểu mẫu nhập liệu, script JS tự động chuyển đổi danh sách cơ sở khả dụng tùy thuộc vào vai trò được chọn (`MANAGER` hay `OPERATOR`).
- [x] Trang chỉnh sửa (`edit.jsp`): Hiển thị dữ liệu cũ, hỗ trợ cập nhật vai trò và đổi cơ sở quản lý.

---

## 6. Kiểm thử & Đánh giá (Testing)

- [ ] Viết Unit Test kiểm tra logic kiểm tra ràng buộc duy nhất của Manager/Operator trên cơ sở và kiểm tra ngày sinh trong Service.
- [ ] Thực hiện Manual Test luồng tạo mới tài khoản không chọn cơ sở để đảm bị chặn lỗi.
- [ ] Thực hiện Manual Test luồng gửi email và xác thực mật khẩu tạm thời.
- [ ] Thực hiện Manual Test luồng tự khóa tài khoản của bản thân để đảm bảo bị chặn lỗi.
