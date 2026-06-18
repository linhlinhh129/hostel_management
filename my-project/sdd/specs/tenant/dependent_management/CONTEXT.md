# [CONTEXT.md](http://CONTEXT.md) \[Dependent Management\]

**Người viết:** Business Analyst\
**Ngày:** 2026-06-10

## 1. PROBLEM STATEMENT

Trong quá trình thuê phòng, một người thuê có thể sinh sống cùng các thành viên khác như vợ/chồng, con cái, cha mẹ hoặc người thân. Ban quản lý cần lưu trữ và quản lý thông tin cư trú của các cá nhân này để phục vụ công tác quản lý nhân khẩu, an ninh, kiểm tra cư trú và xử lý các vấn đề phát sinh liên quan đến hợp đồng thuê.

Hiện tại người thuê chưa có khả năng tự xem danh sách và thông tin chi tiết của các người phụ thuộc đã được đăng ký trong hệ thống. Điều này gây khó khăn trong việc:

- Kiểm tra tính chính xác của thông tin cư trú đã khai báo.

- Theo dõi các thành viên đang được đăng ký theo hợp đồng thuê hiện tại.

- Xác minh thông tin khi có yêu cầu từ Ban quản lý.

- Đảm bảo dữ liệu người phụ thuộc luôn đồng bộ với thực tế cư trú.

Hệ thống cần cung cấp khả năng tra cứu danh sách người phụ thuộc và xem chi tiết thông tin của từng người phụ thuộc thuộc quyền quản lý của người thuê.

---

## 2. DOMAIN KNOWLEDGE

### Dependent (Người phụ thuộc)

Là cá nhân được đăng ký cư trú cùng người thuê chính trong một hợp đồng thuê phòng.

Người phụ thuộc có thể là:

- Vợ/Chồng

- Con

- Cha/Mẹ

- Anh/Chị/Em

- Người thân khác

### Tenant (Người thuê)

Là người đứng tên hợp đồng thuê và chịu trách nhiệm bảo trợ thông tin cư trú của các người phụ thuộc liên quan.

### Sponsored By

Thông tin xác định người thuê đang bảo trợ hoặc quản lý người phụ thuộc.

### Relationship

Mối quan hệ giữa người phụ thuộc và người thuê chính.

Ví dụ:

- Cha

- Mẹ

- Con

- Anh trai

- Chị gái

- Em trai

- Em gái

- Vợ

- Chồng

### Quy tắc nghiệp vụ

- Mỗi người phụ thuộc phải thuộc về đúng một Tenant.

- Tenant chỉ được xem người phụ thuộc thuộc quyền quản lý của mình.

- Tenant không được xem hoặc truy cập người phụ thuộc của Tenant khác.

- Người phụ thuộc không được đăng nhập hệ thống bằng chức năng Tenant.

- Dữ liệu người phụ thuộc được sử dụng cho mục đích quản lý cư trú và hành chính.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

- Tenant (Người thuê)

- Property Manager (Ban quản lý)

- System Administrator

### Secondary Stakeholders

- Chủ nhà

- Bộ phận quản lý cư trú

- Bộ phận hỗ trợ khách hàng

### Decision Makers

- Product Owner

- Business Analyst

- Property Manager

---

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

### Business Constraints

- Tenant chỉ được truy cập dữ liệu của chính mình.

- Không cho phép xem thông tin người phụ thuộc thuộc Tenant khác.

- Thông tin người phụ thuộc phải được quản lý theo hợp đồng thuê hiện hành.

- Hệ thống phải tuân thủ quy định bảo vệ dữ liệu cá nhân.

### Security Constraints

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Mọi truy cập trái phép phải bị từ chối với HTTP 403 Forbidden.

- Các truy cập chưa xác thực phải được chuyển hướng đến màn hình đăng nhập hoặc trả về HTTP 401.

### Technical Constraints

- Không thay đổi cấu trúc cơ sở dữ liệu hiện tại.

- Chỉ sử dụng dữ liệu người phụ thuộc đã tồn tại trong hệ thống.

- API phải hỗ trợ truy xuất danh sách và thông tin chi tiết người phụ thuộc.

- dependentId phải là mã hợp lệ theo quy định hệ thống.

---

## 5. ASSUMPTIONS (giả định cần confirm)

### A01

Mỗi người phụ thuộc chỉ được liên kết với một Tenant tại cùng một thời điểm.

### A02

Thông tin người phụ thuộc đã được tạo và phê duyệt bởi Ban quản lý trước khi Tenant truy cập.

### A03

Tenant không được phép thêm, sửa hoặc xóa người phụ thuộc trong phạm vi tính năng này.

### A04

Danh sách người phụ thuộc chỉ bao gồm các cá nhân đang còn hiệu lực cư trú theo hợp đồng hiện tại.

### A05

Thông tin CCCD, Email và Số điện thoại của người phụ thuộc luôn tồn tại trong hệ thống.

### A06

Số lượng người phụ thuộc của mỗi Tenant đủ nhỏ để không cần phân trang trong phiên bản đầu tiên.

---

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

### Q01

Tenant có được phép tìm kiếm người phụ thuộc theo tên hoặc mã người phụ thuộc không?

**Owner:** Product Owner

### Q02

Tenant có được phép cập nhật thông tin người phụ thuộc hay chỉ được xem?

**Owner:** Business Analyst

### Q03

Có cần hỗ trợ phân trang khi Tenant có số lượng người phụ thuộc lớn không?

**Owner:** Tech Lead

### Q04

Thông tin người phụ thuộc đã rời khỏi nơi cư trú có cần hiển thị trong lịch sử hay không?

**Owner:** Product Owner

### Q05

Có cần lưu nhật ký (Audit Log) mỗi lần Tenant truy cập thông tin chi tiết người phụ thuộc không?

**Owner:** Security Team

### Q06

Thông tin CCCD của người phụ thuộc có cần được che bớt (masking) khi hiển thị trên giao diện không?

**Owner:** Security Team