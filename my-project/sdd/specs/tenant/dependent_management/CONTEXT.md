# [CONTEXT.md](http://CONTEXT.md) - Dependent Management

**Người viết:** Business Analyst\
**Ngày:** 2026-06-21

---

# 1. PROBLEM STATEMENT

Trong quá trình thuê nhà, một phòng có thể có nhiều người cùng sinh sống như vợ/chồng, con cái, cha mẹ hoặc người thân. Tuy nhiên, người thuê chính thường không có một nơi tập trung để kiểm tra thông tin của các thành viên đã được đăng ký cư trú.

Đối với Ban quản lý, việc thiếu thông tin nhân khẩu chính xác gây khó khăn trong công tác quản lý cư dân, xác minh người ra vào, xử lý các tình huống khẩn cấp và thống kê số lượng người đang sinh sống thực tế tại từng phòng.

Ngoài ra, thông tin người phụ thuộc chứa nhiều dữ liệu cá nhân nhạy cảm (PII) như CCCD, ngày sinh, số điện thoại và email. Nếu không kiểm soát tốt quyền truy cập hoặc hiển thị không đúng cách sẽ dẫn đến nguy cơ rò rỉ dữ liệu cá nhân.

Vì vậy, hệ thống cần cung cấp chức năng giúp người thuê xem danh sách và thông tin chi tiết của người phụ thuộc thuộc phòng mình, đồng thời đảm bảo dữ liệu được bảo vệ và chỉ những người có quyền mới được truy cập.

---

# 2. DOMAIN KNOWLEDGE

### Dependent

Là người cùng cư trú với người thuê chính nhưng không phải là chủ hợp đồng thuê.

Ví dụ:

- Vợ/Chồng

- Con

- Cha/Mẹ

- Anh/Chị/Em

- Người thân khác

---

### Primary Tenant

Là người đại diện ký hợp đồng thuê phòng và chịu trách nhiệm quản lý thông tin của các người phụ thuộc.

---

### Relationship

Mối quan hệ giữa người phụ thuộc và người thuê chính.

Các giá trị phổ biến:

- SPOUSE

- CHILD

- PARENT

- SIBLING

- RELATIVE

- OTHER

---

### Verification Status

Trạng thái xác minh của người phụ thuộc bởi Ban quản lý.

Ví dụ:

- VERIFIED

- UNVERIFIED

---

### PII (Personally Identifiable Information)

Là các thông tin cá nhân cần được bảo vệ, bao gồm:

- CCCD/CMND

- Ngày sinh

- Email

- Số điện thoại

- Ảnh đại diện

Các dữ liệu này phải tuân thủ chính sách bảo mật của hệ thống.

---

### Soft Delete

Người phụ thuộc không bị xóa vật lý khỏi cơ sở dữ liệu mà được đánh dấu bằng trường `deleted_at`.

Các bản ghi đã Soft Delete sẽ không hiển thị trên giao diện người dùng.

---

### Business Rules

- Người thuê chỉ được xem người phụ thuộc thuộc phòng hoặc hợp đồng thuê của mình.

- Người phụ thuộc đã Soft Delete không được hiển thị.

- CCCD/CMND phải được che (mask) khi hiển thị trên giao diện.

- Người thuê không được chỉnh sửa hoặc xóa người phụ thuộc trong phạm vi tính năng này.

- Ban quản lý có thể xem thông tin của tất cả người phụ thuộc để phục vụ công tác quản lý cư dân.

---

# 3. STAKEHOLDERS

### Primary Stakeholders

- **Tenant (Người thuê chính)**

  - Xem danh sách người phụ thuộc.

  - Xem chi tiết người phụ thuộc.

---

### Secondary Stakeholders

- **Ban Quản Lý**

  - Quản lý thông tin nhân khẩu.

  - Xác minh người phụ thuộc.

  - Hỗ trợ kiểm tra cư trú và an ninh.

- **Chủ nhà**

  - Theo dõi số lượng người đang cư trú trong phòng (nếu được phân quyền).

---

### Technical Stakeholders

- Product Owner

- Business Analyst

- Backend Developer

- Frontend Developer

- QA/Tester

- DevOps

---

# 4. CONSTRAINTS (Ràng buộc)

## Business Constraints

- Chỉ người dùng đã đăng nhập với vai trò Tenant hoặc Admin mới được truy cập.

- Tenant chỉ được xem dữ liệu thuộc phòng hoặc hợp đồng thuê của mình.

- Không cho phép chỉnh sửa, thêm mới hoặc xóa người phụ thuộc trong phạm vi feature này.

- Chỉ hiển thị các người phụ thuộc đang hoạt động (deleted_at IS NULL).

---

## Technical Constraints

- Sử dụng REST API hiện có.

- Không thay đổi cấu trúc cơ sở dữ liệu trong phạm vi tính năng này.

- Áp dụng cơ chế Soft Delete khi truy vấn dữ liệu.

- Tất cả API phải kiểm tra quyền truy cập trước khi trả dữ liệu.

---

## Security Constraints

- Bắt buộc xác thực bằng Access Token.

- Kiểm tra quyền truy cập trước khi lấy dữ liệu.

- CCCD/CMND phải được che theo quy định SEC-01.

- Không trả về dữ liệu của người phụ thuộc thuộc phòng khác.

- Không hiển thị dữ liệu đã Soft Delete.

---

## Performance Constraints

- API danh sách phản hồi dưới **200ms (P95)**.

- API chi tiết phản hồi dưới **200ms (P95)**.

- Hệ thống phải đảm bảo truy vấn nhanh ngay cả khi số lượng người phụ thuộc lớn.

---

# 5. ASSUMPTIONS (Giả định cần xác nhận)

- Mỗi người phụ thuộc có một `dependentId` duy nhất.

- Một người phụ thuộc chỉ thuộc về một người thuê chính hoặc một hợp đồng thuê tại một thời điểm.

- Quan hệ giữa Tenant và Dependent đã được thiết lập đầy đủ trong cơ sở dữ liệu.

- Ảnh đại diện là tùy chọn và có thể không tồn tại.

- Trạng thái xác minh được cập nhật bởi Ban quản lý.

- Dữ liệu người phụ thuộc không thay đổi trong quá trình người dùng đang xem.

- API chỉ trả về các bản ghi chưa bị Soft Delete.

---

# 6. OPEN QUESTIONS (Câu hỏi cần làm rõ)

 1. Người phụ thuộc có thể thuộc nhiều hợp đồng thuê hoặc nhiều phòng trong các thời điểm khác nhau không?

 2. Có cần hiển thị ảnh đại diện mặc định nếu người phụ thuộc chưa có ảnh không?

 3. Ban quản lý có được phép xem đầy đủ CCCD/CMND hay cũng phải áp dụng cơ chế che thông tin (masking)?

 4. Có cần ghi nhận lịch sử truy cập (Audit Log) khi người dùng xem thông tin người phụ thuộc để phục vụ kiểm tra bảo mật không?

 5. Sau khi người phụ thuộc chuyển đi hoặc không còn cư trú, hệ thống sẽ Soft Delete hay lưu thêm trạng thái "Inactive"?

 6. Có cần hỗ trợ tìm kiếm hoặc lọc người phụ thuộc theo tên hoặc mối quan hệ trong các phiên bản tiếp theo không?

 7. Có cần cho phép tải hoặc in thông tin người phụ thuộc dưới dạng PDF hoặc Excel không?

 8. Người phụ thuộc chưa được Ban quản lý xác minh có được hiển thị cho Tenant hay không, hay chỉ hiển thị sau khi được phê duyệt?

 9. Có cần gửi thông báo cho Tenant khi thông tin người phụ thuộc được Ban quản lý xác minh hoặc từ chối không?

10. Trong tương lai có cần tích hợp nhận diện khuôn mặt hoặc mã QR để hỗ trợ kiểm soát ra vào cho người phụ thuộc hay không?