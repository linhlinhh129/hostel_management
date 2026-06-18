# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần cho phép Ban Quản Lý theo dõi các cơ sở và phòng thuộc phạm vi được Admin phân công.

Mỗi Ban Quản Lý có thể được phân công một hoặc nhiều cơ sở. Khi truy cập hệ thống, Ban Quản Lý chỉ được xem dữ liệu cơ sở và phòng thuộc phạm vi đó.

Feature này không dùng để tạo, sửa hoặc xóa cơ sở/phòng. Feature chỉ tập trung vào việc xem danh sách cơ sở được phân công, xem danh sách phòng theo cơ sở và xem chi tiết phòng.

## 2. Nỗi đau của User

Nếu không có chức năng quản lý phòng cơ sở theo phạm vi phân quyền, hệ thống có thể gặp các vấn đề sau:

* Ban Quản Lý không biết rõ mình đang phụ trách cơ sở nào.
* Ban Quản Lý khó theo dõi danh sách phòng thuộc cơ sở được giao.
* Người dùng có thể truy cập nhầm dữ liệu của cơ sở không thuộc phạm vi quản lý.
* Dữ liệu toàn hệ thống có thể bị lộ cho người không có quyền.
* Admin khó kiểm soát trách nhiệm vận hành của từng Ban Quản Lý.
* Việc theo dõi tình trạng phòng có thể bị sai phạm vi hoặc không minh bạch.

Vì vậy, hệ thống cần giới hạn dữ liệu cơ sở và phòng theo phạm vi được Admin phân công cho từng Ban Quản Lý.

## 3. Mục tiêu

Feature Quản lý phòng cơ sở giúp Ban Quản Lý:

* Xem danh sách cơ sở được phân công.
* Biết rõ phạm vi cơ sở mình cần quản lý.
* Xem danh sách phòng thuộc cơ sở được phân công.
* Xem chi tiết thông tin từng phòng.
* Theo dõi trạng thái phòng trong phạm vi quản lý.
* Không truy cập nhầm hoặc vượt quyền sang cơ sở khác.

## 4. Ràng buộc

* Chỉ người dùng có vai trò MANAGER mới được truy cập chức năng này.
* Ban Quản Lý chỉ được xem dữ liệu thuộc các cơ sở được Admin phân công.
* Ban Quản Lý không được truy cập dữ liệu cơ sở ngoài phạm vi quản lý.
* Ban Quản Lý không được truy cập dữ liệu toàn hệ thống.
* Ban Quản Lý không được tự gán hoặc thay đổi cơ sở quản lý của mình.
* Nếu Ban Quản Lý chưa được phân công cơ sở nào, hệ thống phải hiển thị thông báo phù hợp.
* Hệ thống phải kiểm tra quyền truy cập cơ sở trước khi trả về danh sách phòng.
* Hệ thống phải kiểm tra quyền truy cập cơ sở trước khi trả về chi tiết phòng.
* Danh sách phòng phải hỗ trợ phân trang.
* API phải trả về lỗi rõ ràng khi người dùng truy cập sai phạm vi quyền hạn.
* Các thao tác truy cập dữ liệu quan trọng của Ban Quản Lý cần được ghi log.

## 5. Câu hỏi mở

* Ban Quản Lý có được xem cơ sở INACTIVE đã từng được phân công không?
* Ban Quản Lý có được xem phòng INACTIVE không?
* Có cần cho Ban Quản Lý lọc phòng theo trạng thái không?
* Có cần cho Ban Quản Lý tìm kiếm phòng theo mã phòng không?
* Có cần hiển thị số người đang ở trong từng phòng không?
* Có cần hiển thị phòng đang trống, đang thuê hoặc đang bảo trì không?
* Có cần ghi Audit Log cho mỗi lần xem chi tiết phòng không?
* Nếu Admin gỡ cơ sở khỏi Ban Quản Lý, dữ liệu cũ có còn xem được không?
