# [CONTEXT.md](http://CONTEXT.md) - Tenant Contract Viewing

**Người viết:** @name\
**Ngày:** YYYY-MM-DD

---

# 1. PROBLEM STATEMENT

Trong hệ thống quản lý nhà trọ, sau khi Ban quản lý tạo hợp đồng thuê, người thuê hiện không có cách để chủ động xem lại thông tin hợp đồng của mình trên hệ thống.

Điều này khiến người thuê phải liên hệ Ban quản lý mỗi khi cần kiểm tra thời hạn hợp đồng, tiền thuê, tiền cọc hoặc các điều khoản đã thỏa thuận. Việc phụ thuộc vào Ban quản lý làm giảm tính minh bạch và gây mất thời gian cho cả hai bên.

Hệ thống cần cung cấp khả năng để người thuê tự tra cứu hợp đồng của chính mình một cách thuận tiện, đồng thời vẫn đảm bảo dữ liệu hợp đồng của các người thuê khác được bảo mật.

---

# 2. DOMAIN KNOWLEDGE

### Contract (Hợp đồng thuê)

Là tài liệu ghi nhận thỏa thuận giữa Ban quản lý (bên cho thuê) và người thuê về việc sử dụng một phòng trong khoảng thời gian xác định.

Một hợp đồng thường bao gồm:

- Mã hợp đồng

- Người thuê

- Phòng thuê

- Ngày bắt đầu

- Ngày kết thúc

- Tiền thuê

- Tiền cọc

- Các khoản phí dịch vụ

- Điều khoản của hợp đồng

- Trạng thái hợp đồng

### Tenant

Là người đang thuê phòng và có tài khoản đăng nhập vào hệ thống.

### Contract Ownership

Một hợp đồng chỉ thuộc về duy nhất một người thuê (`tenant_id`).

Người thuê chỉ được phép truy cập các hợp đồng có `tenant_id` trùng với tài khoản đang đăng nhập.

### Read-only Contract

Người thuê chỉ có quyền xem dữ liệu hợp đồng.

Mọi thay đổi đối với hợp đồng đều do Ban quản lý thực hiện.

---

# 3. STAKEHOLDERS

### Người thuê (Tenant)

- Tra cứu hợp đồng của mình.

- Kiểm tra thời hạn thuê.

- Kiểm tra các điều khoản đã ký.

- Theo dõi tiền thuê, tiền cọc và các khoản phí.

### Ban quản lý

- Tạo và quản lý hợp đồng.

- Đảm bảo thông tin hợp đồng hiển thị chính xác cho người thuê.

- Không cần xử lý các yêu cầu xem hợp đồng thủ công.

### Chủ cơ sở

- Đảm bảo tính minh bạch giữa người thuê và bên cho thuê.

- Giảm khối lượng công việc của Ban quản lý.

---

# 4. CONSTRAINTS

### Business Constraints

- Người thuê chỉ được xem hợp đồng của chính mình.

- Không được xem hợp đồng của người thuê khác.

- Người thuê không được tạo, sửa, xóa hoặc thay đổi trạng thái hợp đồng.

- Nội dung hợp đồng hiển thị phải đúng với dữ liệu do Ban quản lý tạo.

### Technical Constraints

- Xác thực người dùng bằng tài khoản đăng nhập.

- Mọi truy vấn hợp đồng phải được lọc theo `tenant_id` của người dùng hiện tại.

- API không được trả về dữ liệu của hợp đồng không thuộc quyền sở hữu của người dùng.

### Security Constraints

- Trả về HTTP 403 khi người thuê truy cập hợp đồng không thuộc quyền sở hữu.

- Không được để lộ thông tin hợp đồng thông qua việc thay đổi `contractId` trên URL hoặc API.

---

# 5. ASSUMPTIONS

- Mỗi người thuê có một tài khoản đăng nhập trong hệ thống.

- Mỗi hợp đồng được liên kết với đúng một `tenant_id`.

- Người thuê đã được Ban quản lý gán vào hợp đồng khi hợp đồng được tạo.

- Người thuê chỉ cần quyền xem hợp đồng, không có nhu cầu chỉnh sửa.

- Hợp đồng đã được Ban quản lý xác nhận trước khi hiển thị cho người thuê.

---

# 6. OPEN QUESTIONS

1. Người thuê có được xem các hợp đồng đã hết hiệu lực (`INACTIVE`) hay chỉ hợp đồng đang hiệu lực (`ACTIVE`)?

2. Người thuê có được tải xuống hoặc in hợp đồng dưới dạng PDF không?

3. Có cần hiển thị toàn bộ nội dung hợp đồng hay chỉ các thông tin tóm tắt?

4. Nếu một người thuê từng thuê nhiều phòng, hệ thống có hiển thị toàn bộ lịch sử hợp đồng hay chỉ hợp đồng hiện tại?

5. Trường hợp nhiều người cùng đứng tên trong một hợp đồng thì quyền truy cập sẽ được xử lý như thế nào?

6. Có cần ghi log (Audit Log) khi người thuê truy cập xem hợp đồng hay không?