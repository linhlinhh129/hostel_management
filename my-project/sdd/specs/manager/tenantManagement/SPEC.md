# Feature: Quản lý Người thuê

**Trạng thái:** Draft
**Người viết:** [Tên]
**Người duyệt:** [Tên]
**Ngày:** 2026-07-13
**Độ ưu tiên:** High

## 1. Bối cảnh nghiệp vụ

Tính năng Quản lý Người thuê cho phép Manager quản lý thông tin các người thuê chính đại diện cho các phòng thuộc cơ sở được giao. Người thuê chính là người có tài khoản đăng nhập hệ thống, đứng tên trên hợp đồng thuê và được liên kết với một phòng cụ thể.

Hệ thống không cho phép Manager tạo mới trực tiếp tài khoản người thuê từ phân hệ này. Thay vào đó, tài khoản người thuê bắt buộc phải được khởi tạo đi kèm với thông tin hợp đồng thuê tại phân hệ Hợp đồng (`/manager/contracts`). Mỗi phòng tại một thời điểm chỉ có tối đa một người thuê chính ở trạng thái ACTIVE.

Ngoài ra, Manager có quyền xem danh sách, lọc và tìm kiếm người thuê, cập nhật thông tin cá nhân, khóa/mở khóa tài khoản, kết thúc hợp đồng thuê (trả phòng) và thực hiện xóa mềm tài khoản khi ngừng thuê.

## 2. Câu chuyện người dùng

### Story 1 (Luồng chính)

Là Manager, tôi muốn xem danh sách và tìm kiếm người thuê ở các cơ sở của tôi để dễ dàng kiểm soát thông tin cư dân.

### Story 2 (Luồng chính)

Là Manager, tôi muốn xem hồ sơ chi tiết của người thuê để kiểm tra thông tin cá nhân, phòng đang ở, hợp đồng hiện tại và danh sách người phụ thuộc ở cùng phòng.

### Story 3 (Luồng chính)

Là Manager, tôi muốn chỉnh sửa và cập nhật thông tin cá nhân của người thuê (họ tên, email, SĐT, số định danh CCCD/CMND, giới tính, ngày sinh, địa chỉ thường trú) để đảm bảo dữ liệu luôn chính xác.

### Story 4 (Luồng chính)

Là Manager, tôi muốn khóa hoặc mở khóa tài khoản đăng nhập của người thuê để tạm ngưng truy cập của họ khi cần thiết hoặc khôi phục lại quyền truy cập sau khi xử lý vi phạm.

### Story 5 (Luồng chính)

Là Manager, khi người thuê trả phòng, tôi muốn thực hiện hành động kết thúc thuê để giải phóng phòng cho người khác thuê, đồng thời đưa tài khoản người thuê đó về trạng thái lưu trữ lịch sử cư trú.

### Story 6 (Luồng chính)

Là Manager, đối với các người thuê đã ngừng thuê, tôi muốn thực hiện xóa mềm họ khỏi danh sách hiển thị để giữ màn hình gọn gàng nhưng vẫn lưu lịch sử trong cơ sở dữ liệu.

### Story 7 (Trường hợp ngoại lệ)

Là Manager, khi tôi cố tạo trực tiếp người thuê từ phân hệ này, tôi muốn hệ thống hiển thị thông báo yêu cầu thực hiện thông qua chức năng tạo Hợp đồng.

## 3. Tiêu chí chấp nhận (EARS)

### AC-01 Ngăn chặn tạo trực tiếp người thuê

KHI Manager cố truy cập trang tạo mới người thuê trực tiếp
HỆ THỐNG PHẢI hiển thị thông báo yêu cầu tạo thông qua chi tiết hợp đồng và chuyển hướng đến trang Quản lý Hợp đồng.

### AC-02 Xem danh sách người thuê

KHI Manager mở trang danh sách người thuê
HỆ THỐNG PHẢI hiển thị danh sách người thuê thuộc cơ sở được quản lý kèm phân trang (10 bản ghi/trang). 

Giao diện hỗ trợ bộ lọc và tìm kiếm:
* `keyword`: Tìm theo tên hoặc mã người thuê.
* `status`: Lọc theo trạng thái cư trú (`ACTIVE`, `LOCKED`, `INACTIVE`).

### AC-03 Xem chi tiết người thuê

KHI Manager mở trang chi tiết người thuê
HỆ THỐNG PHẢI hiển thị đầy đủ thông tin cá nhân, thông tin phòng và cơ sở, thông tin hợp đồng liên quan, danh sách người phụ thuộc hiện tại, và các nút chức năng tương ứng với trạng thái (Sửa, Khóa/Mở khóa, Kết thúc thuê, Xóa mềm).

### AC-04 Cập nhật thông tin người thuê

KHI Manager cập nhật thông tin người thuê với dữ liệu hợp lệ qua biểu mẫu chỉnh sửa
HỆ THỐNG PHẢI lưu các thông tin thay đổi vào bảng `dbo.users`, ghi nhận log cập nhật và hiển thị thông báo thành công.

### AC-05 Khóa tài khoản người thuê

KHI Manager thực hiện hành động khóa tài khoản của người thuê đang `ACTIVE`
HỆ THỐNG PHẢI cập nhật trạng thái tài khoản thành `LOCKED`, ngăn không cho người thuê đăng nhập hệ thống và ghi nhận vào Audit Log.

### AC-06 Mở khóa tài khoản người thuê

KHI Manager mở khóa tài khoản của người thuê đang bị `LOCKED`
HỆ THỐNG PHẢI khôi phục trạng thái tài khoản về `ACTIVE`, reset bộ đếm số lần đăng nhập sai của tài khoản đó và ghi nhận vào Audit Log.

### AC-07 Kết thúc thuê (Trả phòng)

KHI Manager xác nhận kết thúc hợp đồng thuê của một người thuê `ACTIVE` và nhập ngày kết thúc cùng lý do trả phòng
HỆ THỐNG PHẢI cập nhật trạng thái người thuê thành `INACTIVE`, giải phóng phòng tương ứng (gỡ liên kết `tenant_id` tại phòng), vô hiệu hóa tài khoản đăng nhập của người thuê nhưng vẫn giữ nguyên dữ liệu lịch sử để tra cứu.

### AC-08 Xóa mềm người thuê

KHI Manager thực hiện xóa người thuê đã ở trạng thái `INACTIVE`
HỆ THỐNG PHẢI thực hiện xóa mềm bằng cách cập nhật trường `deleted_at = GETDATE()` trong cơ sở dữ liệu để ẩn tài khoản khỏi các danh sách hoạt động.

## 4. Hợp đồng API

Mọi hành động được xử lý thông qua [ManagerTenantsServlet.java](file:///d:/Ki_5/hostel_management/src/main/java/com/quanlyphongtro/controller/manager/ManagerTenantsServlet.java):

### 4.1 Tạo người thuê trực tiếp (Bị chặn)
* **URL:** `GET /manager/tenants/create` hoặc `POST /manager/tenants/create`
* **Redirect:** Quay về `/manager/contracts` kèm thông báo flash báo lỗi.

## 4.2 Lấy danh sách người thuê
* **URL:** `GET /manager/tenants`
* **Query Parameters:**
  * `keyword`: Từ khóa tìm kiếm theo họ tên hoặc mã người thuê.
  * `status`: Trạng thái cần lọc (`ACTIVE`, `LOCKED`, `INACTIVE`).
  * `page`: Trang hiện tại (mặc định trang 1).

## 4.3 Lấy chi tiết người thuê
* **URL:** `GET /manager/tenants/{id}`

## 4.4 Kết thúc thuê
* **URL:** `POST /manager/tenants/{id}/end-rental`
* **Request Parameters:**
  * `endDate`: Ngày kết thúc thuê (bắt buộc).
  * `reason`: Ghi chú lý do kết thúc thuê.
* **Redirect:** Chuyển hướng về trang chi tiết người thuê `/manager/tenants/{id}` kèm thông báo flash.

### 4.5 Cập nhật thông tin cá nhân
* **URL:** `POST /manager/tenants/{id}/edit`
* **Request Parameters:** `fullName` (bắt buộc), `email` (bắt buộc), `phone` (bắt buộc), `identityNumber` (bắt buộc), `gender`, `dob`, `permanentAddress`.

### 4.6 Khóa tài khoản
* **URL:** `POST /manager/tenants/{id}/lock`

### 4.7 Mở khóa tài khoản
* **URL:** `POST /manager/tenants/{id}/unlock`

### 4.8 Xóa mềm người thuê
* **URL:** `POST /manager/tenants/{id}/delete`

## 5. Ràng buộc kỹ thuật

* Mọi thông tin tài khoản người thuê được lưu trữ trong bảng `dbo.users` (với vai trò `role = 'TENANT'`).
* Số điện thoại (`phone`) và số CCCD/CMND (`identityNumber`) của người thuê phải tuân thủ định dạng số hợp lệ tại Việt Nam khi Manager cập nhật thông tin.
* Manager chỉ được xem và quản lý những người thuê thuộc các cơ sở được phân công (đối chiếu cột `manager_id` trong bảng `dbo.facilities`).
* Khi kết thúc thuê, hệ thống sẽ thực hiện cập nhật trạng thái cư trú của người thuê thành `INACTIVE` và gỡ bỏ `tenant_id` tại bảng `dbo.rooms` của phòng đó.
* Các hành động khóa, mở khóa, cập nhật thông tin và kết thúc thuê đều phải ghi nhận log chi tiết vào bảng `dbo.audit_logs`.
* Chỉ cho phép xóa mềm người thuê (cập nhật trường `deleted_at = GETDATE()`) để đảm bảo không mất mát dữ liệu lịch sử đối chiếu tài chính.

## 6. Định nghĩa trạng thái người thuê

| Trạng thái | Ý nghĩa |
| --- | --- |
| `ACTIVE` | Người thuê hiện tại của phòng, có tài khoản hoạt động bình thường |
| `LOCKED` | Tài khoản của người thuê tạm thời bị khóa đăng nhập |
| `INACTIVE`| Người thuê đã ngừng thuê phòng (trả phòng), tài khoản bị vô hiệu hóa cư trú |

## 7. Phụ thuộc

* **Quản lý Cơ sở:** Để xác minh quyền quản lý của Manager đối với cơ sở của người thuê.
* **Quản lý Phòng:** Để gán/gỡ bỏ người thuê đại diện của phòng.
* **Quản lý Hợp đồng:** Cung cấp chức năng tạo người thuê đi kèm khi ký kết hợp đồng thuê phòng mới.
* **Quản lý Người phụ thuộc:** Hiển thị danh sách đi kèm trong hồ sơ chi tiết người thuê.
* **Audit Log:** Ghi nhận nhật ký lịch sử quản lý.

## 8. Ngoài phạm vi

* Quản lý chuyển phòng chi tiết của cư dân.
* Thiết lập hóa đơn/chỉ số điện nước trực tiếp tại giao diện người thuê.
* Khôi phục mật khẩu tài khoản người thuê bằng tay (người thuê tự thực hiện qua luồng quên mật khẩu).
