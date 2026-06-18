# \[Profile Management\]

**Người viết:** Business Analyst\
**Ngày:** 2026-06-10

## 1. PROBLEM STATEMENT

Người thuê cần có khả năng xem và cập nhật thông tin cá nhân của mình trong hệ thống quản lý nhà trọ/chung cư.

Hiện tại, thông tin liên hệ của người thuê có thể thay đổi theo thời gian (số điện thoại, email), dẫn đến khó khăn cho Ban quản lý hoặc Chủ nhà trong việc liên hệ khi cần thiết. Nếu không có cơ chế tự cập nhật, dữ liệu cá nhân có thể trở nên không chính xác, ảnh hưởng đến việc gửi thông báo, xử lý yêu cầu hỗ trợ, quản lý cư trú và các hoạt động vận hành khác.

Hệ thống cần cung cấp một nơi tập trung để người thuê:

- Xem thông tin hồ sơ cá nhân.

- Kiểm tra thông tin phòng đang thuê.

- Xem danh sách người phụ thuộc đã đăng ký.

- Cập nhật các thông tin liên hệ được phép chỉnh sửa.

---

## 2. DOMAIN KNOWLEDGE

### Tenant (Người thuê)

Người đang thuê phòng trong hệ thống và có tài khoản đăng nhập riêng.

### Profile (Hồ sơ cá nhân)

Tập hợp thông tin nhận dạng và liên hệ của người thuê bao gồm:

- Mã người thuê

- Họ tên

- Ngày sinh

- CCCD

- Số điện thoại

- Email

### Room Information (Thông tin phòng thuê)

Thông tin phòng hiện tại mà người thuê đang sử dụng.

### Dependent (Người phụ thuộc)

Người có liên kết cư trú với người thuê chính, ví dụ:

- Con

- Vợ/chồng

- Anh/chị/em

- Người thân khác

### Citizen ID (CCCD)

Thông tin định danh pháp lý của người thuê.

### Email

Là kênh liên lạc chính của hệ thống.\
Email phải duy nhất trong toàn bộ hệ thống.

### Phone Number

Là số điện thoại liên hệ của người thuê.\
Được sử dụng cho các hoạt động liên lạc và xác minh thông tin.

### Ownership Rule

Người thuê chỉ được phép xem và cập nhật hồ sơ của chính mình.\
Không được truy cập dữ liệu của người dùng khác.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

- Tenant (Người thuê)

- Ban quản lý

- Chủ nhà

### Secondary Stakeholders

- Quản trị hệ thống (Admin)

- Bộ phận chăm sóc khách hàng (nếu có)

### Decision Makers

- Product Owner

- Business Owner

- Ban quản lý hệ thống

---

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)

### Security Constraints

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Chỉ được truy cập dữ liệu thuộc tài khoản hiện tại.

- Truy cập trái phép phải trả về HTTP 403 Forbidden.

### Data Constraints

#### Email

- Không được để trống.

- Đúng định dạng email.

- Tối đa 100 ký tự.

- Phải là duy nhất trong hệ thống.

#### Phone Number

- Không được để trống.

- Chỉ chứa chữ số.

- Độ dài từ 10 đến 11 ký tự.

### System Constraints

- Không thay đổi cấu trúc cơ sở dữ liệu hiện tại.

- Sử dụng API hiện có:

  - GET /api/v1/tenant/profile

  - PUT /api/v1/tenant/profile

### Business Constraints

- Tenant không được phép thay đổi:

  - Mã người thuê

  - Họ tên

  - Ngày sinh

  - CCCD

  - Thông tin phòng thuê

  - Danh sách người phụ thuộc

- Tenant chỉ được cập nhật:

  - Email

  - Số điện thoại

---

## 5. ASSUMPTIONS (Giả định cần xác nhận)

1. Mỗi Tenant chỉ có một hồ sơ cá nhân duy nhất trong hệ thống.

2. Tenant luôn có tối đa một phòng đang thuê tại cùng một thời điểm.

3. Danh sách người phụ thuộc được quản lý ở module khác và chỉ hiển thị tại màn hình hồ sơ.

4. Email là duy nhất trên toàn bộ hệ thống, bao gồm Tenant, Staff và Admin.

5. CCCD là dữ liệu chỉ đọc và không cho phép cập nhật từ giao diện Tenant.

6. Ngày sinh là dữ liệu chỉ đọc và không cho phép chỉnh sửa.

7. Hệ thống đã có cơ chế xác thực Access Token trước khi gọi API.

8. Tenant không được phép xóa hồ sơ cá nhân của mình.

---

## 6. OPEN QUESTIONS (Câu hỏi cần làm rõ)

 1. Tenant có được phép thay đổi họ tên hay không trong tương lai?

 2. Tenant có được phép cập nhật CCCD khi thông tin bị sai hoặc thay đổi không?

 3. Có cần xác thực OTP khi thay đổi Email không?

 4. Có cần xác thực OTP khi thay đổi Số điện thoại không?

 5. Khi Email thay đổi, hệ thống có cần gửi email xác nhận không?

 6. Nếu Email mới đã tồn tại nhưng thuộc tài khoản đã bị khóa thì có được phép sử dụng lại không?

 7. Có cần lưu lịch sử thay đổi thông tin cá nhân để phục vụ kiểm toán (audit log) hay không?

 8. Tenant có được phép tải xuống hoặc in hồ sơ cá nhân của mình không?

 9. Danh sách người phụ thuộc có cần hiển thị thêm ngày sinh, giới tính hoặc trạng thái cư trú không?

10. Trường hợp Tenant đang thuê nhiều phòng trong tương lai thì màn hình hồ sơ sẽ hiển thị như thế nào?