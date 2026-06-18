# Notification Management

# Người viết: Business Analyst | Ngày: 2026-06-10

## 1. PROBLEM STATEMENT

Trong quá trình quản lý nhà trọ/chung cư, Ban quản lý và Chủ nhà thường xuyên gửi các thông báo liên quan đến:

- Thu tiền phòng, tiền dịch vụ

- Bảo trì cơ sở vật chất

- Thay đổi nội quy

- Các sự kiện hoặc thông báo khẩn cấp

Hiện tại người thuê có thể bỏ lỡ các thông báo quan trọng do thông tin được truyền đạt qua nhiều kênh khác nhau (tin nhắn, điện thoại, giấy thông báo,...).

Hệ thống cần cung cấp một nơi tập trung để người thuê có thể xem lại lịch sử thông báo và theo dõi các thông tin mới nhất từ Ban quản lý hoặc Chủ nhà.

---

## 2. DOMAIN KNOWLEDGE

### Notification (Thông báo)

Là thông tin được gửi từ hệ thống, Ban quản lý hoặc Chủ nhà đến người thuê.

### Public Notification

Thông báo được gửi cho toàn bộ người thuê trong hệ thống.

Ví dụ:

- Thông báo bảo trì điện nước

- Thông báo thay đổi nội quy

### Targeted Notification

Thông báo chỉ gửi cho một hoặc một nhóm người thuê cụ thể.

Ví dụ:

- Nhắc thanh toán tiền phòng

- Thông báo liên quan đến phòng thuê cụ thể

### Notification List

Danh sách các thông báo mà người thuê được phép xem.

### Notification Detail

Nội dung đầy đủ của một thông báo.

### Created At

Thời điểm thông báo được tạo trong hệ thống.

### Business Rules

- Người thuê chỉ được xem các thông báo được gửi cho mình hoặc thông báo công khai.

- Thông báo mới nhất phải hiển thị đầu danh sách.

- Thông báo đã xem vẫn được lưu trong lịch sử.

- Người thuê không được chỉnh sửa hoặc xóa thông báo.

- Chỉ người có quyền quản trị mới được tạo hoặc quản lý thông báo.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

#### Tenant (Người thuê)

- Xem danh sách thông báo

- Xem chi tiết thông báo

- Theo dõi các thông tin mới nhất

### Secondary Stakeholders

#### Board Management (Ban quản lý)

- Gửi thông báo đến cư dân/người thuê

- Đảm bảo thông tin được truyền đạt đầy đủ

#### Landlord (Chủ nhà)

- Gửi các thông báo liên quan đến phòng thuê

- Theo dõi việc truyền tải thông tin đến người thuê

### System Administrator

- Quản lý quyền truy cập

- Quản lý dữ liệu thông báo

- Giám sát hệ thống

---

## 4. CONSTRAINTS

### Business Constraints

- Chỉ người dùng đã đăng nhập mới được truy cập chức năng.

- Người dùng phải có vai trò Tenant.

- Người thuê chỉ được xem các thông báo thuộc phạm vi được cấp quyền.

### Security Constraints

- Không được truy cập thông báo của người thuê khác.

- API phải xác thực người dùng trước khi trả dữ liệu.

- Dữ liệu thông báo phải được bảo vệ theo cơ chế phân quyền của hệ thống.

### Technical Constraints

- Hệ thống sử dụng API REST.

- Thời gian tải danh sách thông báo cần đáp ứng trải nghiệm người dùng.

- Thông báo phải được sắp xếp theo thời gian tạo giảm dần.

### UI Constraints

- Danh sách thông báo phải hỗ trợ trạng thái rỗng.

- Phải có cơ chế hiển thị lỗi tải dữ liệu.

- Người dùng phải có khả năng quay lại danh sách từ màn hình chi tiết.

---

## 5. ASSUMPTIONS

### A01

Mỗi thông báo có một mã định danh duy nhất (notificationId).

### A02

Thông báo không bị xóa khỏi hệ thống sau khi người thuê đã xem.

### A03

Thời gian hiển thị sử dụng cùng múi giờ với hệ thống.

### A04

Một thông báo có thể được gửi cho nhiều người thuê cùng lúc.

### A05

Tất cả thông báo đều được lưu trữ trong cơ sở dữ liệu trung tâm.

### A06

Người thuê chỉ có quyền đọc thông báo, không có quyền tạo, sửa hoặc xóa.

---

## 6. OPEN QUESTIONS

### Q01

Thông báo có cần đánh dấu trạng thái "Đã đọc / Chưa đọc" hay không?

### Q02

Hệ thống có cần gửi thông báo đẩy (Push Notification) khi có thông báo mới không?

### Q03

Thông báo có thời gian hết hạn hay không?

### Q04

Người thuê có được phép tìm kiếm hoặc lọc thông báo theo thời gian, loại thông báo không?

### Q05

Có cần hỗ trợ đính kèm tập tin trong thông báo không?

### Q06

Thông báo có cần phân loại theo mức độ ưu tiên (Khẩn cấp, Quan trọng, Thông thường) không?

### Q07

Thông báo đã bị quản trị viên thu hồi có còn hiển thị cho người thuê hay không?

### Q08

Có giới hạn số lượng thông báo được lưu hoặc hiển thị trong danh sách hay không?