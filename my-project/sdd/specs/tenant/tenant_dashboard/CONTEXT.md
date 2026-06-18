# \[Tenant Dashboard\]

**Người viết:** Business Analyst\
**Ngày:** 2026-06-10

---

## 1. PROBLEM STATEMENT

Người thuê sau khi đăng nhập cần nhanh chóng nắm được tình trạng tài khoản và các thông tin quan trọng liên quan đến việc thuê phòng mà không phải truy cập từng chức năng riêng lẻ.

Hiện tại, nếu không có một màn hình tổng quan, người thuê phải:

- Tự kiểm tra hóa đơn để biết còn khoản thanh toán nào chưa hoàn thành.

- Tự truy cập danh sách yêu cầu để theo dõi tiến độ xử lý.

- Tự kiểm tra thông báo mới từ Ban quản lý hoặc Chủ nhà.

- Tự kiểm tra danh sách người phụ thuộc.

- Mất thời gian điều hướng giữa nhiều màn hình khác nhau.

Điều này làm giảm trải nghiệm người dùng và khiến các thông tin quan trọng có thể bị bỏ sót.

Mục tiêu của Dashboard là cung cấp một điểm truy cập trung tâm giúp người thuê:

- Nắm bắt nhanh tình trạng tài khoản.

- Theo dõi các thông tin quan trọng.

- Truy cập nhanh đến các chức năng chính của hệ thống.

---

## 2. DOMAIN KNOWLEDGE

### Tenant

Người thuê đang sở hữu hoặc thuê một phòng/căn hộ trong hệ thống.

### Dashboard

Màn hình tổng quan đầu tiên sau khi đăng nhập thành công.

### First Login

Trạng thái tài khoản khi người dùng đăng nhập lần đầu bằng tài khoản được hệ thống cấp.

Người dùng ở trạng thái này:

- Bắt buộc đổi mật khẩu.

- Không được truy cập Dashboard cho đến khi đổi mật khẩu thành công.

### Invoice

Hóa đơn phát sinh cho người thuê.

Các trạng thái liên quan đến Dashboard:

- Chưa thanh toán

- Quá hạn

Dashboard chỉ hiển thị tổng số hóa đơn thuộc hai trạng thái này.

### Request

Yêu cầu do người thuê gửi đến Ban quản lý hoặc Chủ nhà.

Các trạng thái liên quan:

- Chờ xử lý

- Đang xử lý

Dashboard chỉ hiển thị tổng số yêu cầu thuộc hai trạng thái trên.

### Dependent

Người phụ thuộc được đăng ký bởi người thuê.

Ví dụ:

- Vợ/chồng

- Con

- Người thân cùng sinh sống

Dashboard hiển thị tổng số người phụ thuộc đang được quản lý.

### Notification

Thông báo được gửi từ hệ thống, Chủ nhà hoặc Ban quản lý.

Dashboard chỉ hiển thị số lượng thông báo được tạo trong vòng 30 ngày gần nhất.

### Access Token

Token xác thực được sử dụng để xác định người dùng hiện tại trước khi trả dữ liệu Dashboard.

---

## 3. STAKEHOLDERS

### Tenant (Người thuê)

- Người sử dụng chính.

- Theo dõi thông tin tổng quan.

- Truy cập các chức năng nghiệp vụ.

### Property Owner (Chủ nhà)

- Cung cấp và quản lý thông tin liên quan đến người thuê.

- Theo dõi tương tác của người thuê với hệ thống.

### Board Management (Ban quản lý)

- Quản lý vận hành hệ thống.

- Gửi thông báo và xử lý yêu cầu của người thuê.

### System Administrator

- Quản trị tài khoản.

- Thiết lập quyền truy cập.

- Đảm bảo Dashboard hoạt động ổn định.

### Product Owner / Business Owner

- Quyết định phạm vi nghiệp vụ.

- Phê duyệt yêu cầu chức năng.

---

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)

### Business Constraints

- Chỉ người dùng có vai trò Tenant được truy cập Tenant Dashboard.

- Người dùng chỉ được xem dữ liệu thuộc tài khoản của chính mình.

- Người dùng ở trạng thái First Login phải đổi mật khẩu trước khi sử dụng hệ thống.

### Security Constraints

- Access Token phải được kiểm tra trước khi trả dữ liệu.

- Không được trả dữ liệu của người thuê khác.

- Không cho phép truy cập Dashboard khi chưa xác thực.

### Functional Constraints

- Dashboard chỉ hiển thị dữ liệu tổng hợp.

- Không hiển thị danh sách chi tiết trên màn hình Dashboard.

- Dashboard phải cung cấp điều hướng đến các chức năng chính của hệ thống.

### Data Constraints

- Thông báo chỉ được tính trong vòng 30 ngày gần nhất.

- Hóa đơn chỉ tính các trạng thái Chưa thanh toán và Quá hạn.

- Yêu cầu chỉ tính các trạng thái Chờ xử lý và Đang xử lý.

---

## 5. ASSUMPTIONS (Giả định cần xác nhận)

### A01

Trạng thái "First Login" được lưu trong hồ sơ tài khoản người dùng.

### A02

Tất cả hóa đơn đều có trạng thái rõ ràng để xác định hóa đơn chưa thanh toán hoặc quá hạn.

### A03

Tất cả yêu cầu đều có trạng thái xử lý chuẩn hóa.

### A04

Thông báo có trường ngày tạo (createdAt) để xác định phạm vi 30 ngày gần nhất.

### A05

Một người thuê chỉ có một hồ sơ Dashboard tại một thời điểm.

### A06

Các số liệu Dashboard được tính theo thời gian thực khi người dùng truy cập.

### A07

Người thuê luôn có quyền truy cập vào các module:

- Notification

- Request

- Profile

- Invoice

- Dependent

---

## 6. OPEN QUESTIONS (Câu hỏi chưa có câu trả lời)

### Q01

Dashboard có cần tự động làm mới dữ liệu (auto refresh) hay chỉ tải khi người dùng mở màn hình?

### Q02

Thông báo 30 ngày gần nhất được tính theo:

- Ngày tạo thông báo?

- Ngày người dùng nhận thông báo?

### Q03

Nếu số lượng hóa đơn, yêu cầu hoặc thông báo quá lớn, có cần giới hạn số lượng hiển thị không?

### Q04

Dashboard có cần hiển thị lời chào cá nhân hóa theo tên người thuê không?

### Q05

Dashboard có cần hiển thị ảnh đại diện của người thuê không?

### Q06

Có cần hiển thị các cảnh báo ưu tiên như:

- Hóa đơn quá hạn

- Yêu cầu bị từ chối

- Thông báo khẩn

hay chỉ hiển thị số lượng tổng hợp?

### Q07

Có cần lưu lịch sử truy cập Dashboard phục vụ mục đích kiểm toán (audit log) hay không?

### Q08

Khi API Dashboard bị lỗi, hệ thống sẽ:

- Hiển thị dữ liệu cũ đã cache?

- Hay chỉ hiển thị thông báo lỗi và yêu cầu tải lại?

### Q09

Dashboard có cần hỗ trợ phân quyền mở rộng trong tương lai cho các vai trò khác ngoài Tenant hay không?