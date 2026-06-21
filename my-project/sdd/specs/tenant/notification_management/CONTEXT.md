# [CONTEXT.md](http://CONTEXT.md) - Notification Management

**Người viết:** Business Analyst\
**Ngày:** 2026-06-20

---

# 1. PROBLEM STATEMENT

Hiện tại người thuê không có một nơi tập trung để xem lại các thông báo từ Chủ nhà hoặc Ban quản lý. Các thông tin như lịch bảo trì, thông báo thu tiền phòng, thay đổi nội quy hoặc các thông báo khẩn cấp có thể bị bỏ lỡ nếu chỉ được gửi qua các kênh bên ngoài (Zalo, Facebook, SMS,...).

Việc thiếu một chức năng quản lý thông báo trong hệ thống khiến người thuê khó tra cứu lại các thông báo cũ, đồng thời Ban quản lý cũng không có một kênh chính thức để truyền tải thông tin đến cư dân.

---

# 2. DOMAIN KNOWLEDGE

### Notification

Là thông báo được tạo bởi Chủ nhà hoặc Ban quản lý nhằm truyền tải thông tin đến người thuê.

Ví dụ:

- Thông báo bảo trì

- Thông báo thu tiền phòng

- Thông báo thay đổi nội quy

- Thông báo sự kiện

- Thông báo khẩn

---

### Tenant

Người đang thuê phòng trong hệ thống và đã có tài khoản đăng nhập hợp lệ.

---

### Public Notification

Thông báo được gửi đến tất cả người thuê trong hệ thống hoặc toàn bộ người thuê thuộc một khu nhà.

---

### Private Notification

Thông báo chỉ được gửi cho một hoặc nhiều người thuê cụ thể.

---

### Notification Detail

Là nội dung đầy đủ của một thông báo, bao gồm tiêu đề, nội dung và thời gian tạo.

---

### Business Rules

- Người thuê chỉ được xem các thông báo thuộc quyền truy cập của mình.

- Người thuê không được chỉnh sửa hoặc xóa thông báo.

- Thông báo được hiển thị theo thời gian tạo mới nhất.

- Một thông báo có thể được gửi cho nhiều người thuê.

- Thông báo đã tạo chỉ dùng để tra cứu, không chỉnh sửa trong phạm vi tính năng này.

---

# 3. STAKEHOLDERS

### Primary Stakeholders

- Người thuê (Tenant): Xem danh sách và nội dung thông báo.

### Secondary Stakeholders

- Chủ nhà (Landlord): Tạo và gửi thông báo cho người thuê.

- Ban quản lý: Quản lý việc gửi thông báo trong hệ thống.

### Technical Stakeholders

- Backend Developer

- Frontend Developer

- QA/Tester

- Business Analyst

- Product Owner

---

# 4. CONSTRAINTS (Ràng buộc)

## Business Constraints

- Chỉ người dùng đã đăng nhập mới được truy cập chức năng.

- Người thuê chỉ được xem các thông báo được gửi cho mình hoặc thông báo công khai.

- Không cho phép chỉnh sửa hoặc xóa thông báo từ phía người thuê.

---

## Technical Constraints

- Sử dụng REST API hiện có.

- Không thay đổi schema cơ sở dữ liệu trong phạm vi feature này.

- API phải hỗ trợ phân trang để xử lý số lượng thông báo lớn.

---

## Performance Constraints

- Danh sách thông báo phải tải nhanh (&lt;300ms ở P95).

- Hệ thống phải hỗ trợ số lượng lớn thông báo mà không ảnh hưởng đến hiệu năng.

---

## Security Constraints

- Bắt buộc xác thực bằng Access Token.

- Kiểm tra quyền truy cập trước khi trả về dữ liệu.

- Không cho phép truy cập thông báo của người thuê khác thông qua việc thay đổi `notificationId`.

---

# 5. ASSUMPTIONS (Giả định)

- Mỗi thông báo đều có tiêu đề, nội dung và thời gian tạo.

- Mỗi thông báo có một `notificationId` duy nhất.

- API trả về đúng các thông báo mà Tenant được phép xem.

- Danh sách thông báo đã được sắp xếp theo thời gian tạo giảm dần từ phía Backend.

- Người thuê luôn có kết nối Internet khi sử dụng chức năng.

- Thông báo không bị thay đổi hoặc xóa trong quá trình người dùng đang xem.

---

# 6. OPEN QUESTIONS (Cần xác nhận)

 1. Có cần hỗ trợ trạng thái **đã đọc/chưa đọc** (Read/Unread) hay không?

 2. Khi mở chi tiết thông báo có cần tự động đánh dấu là **đã đọc** không?

 3. Có cần hiển thị số lượng thông báo chưa đọc trên Header hoặc Dashboard không?

 4. Có cần chức năng tìm kiếm hoặc lọc thông báo theo loại hoặc khoảng thời gian không?

 5. Thông báo có được phép đính kèm file (PDF, Word, hình ảnh...) không?

 6. Khi thông báo bị thu hồi hoặc xóa sau khi đã gửi, người thuê sẽ nhìn thấy thông báo gì?

 7. Có cần lưu lịch sử người thuê đã xem thông báo vào thời điểm nào không?

 8. Có giới hạn thời gian lưu trữ thông báo (ví dụ 6 tháng hoặc 1 năm) hay hiển thị toàn bộ lịch sử?

 9. Trong tương lai có cần hỗ trợ Push Notification hoặc thông báo theo thời gian thực (WebSocket/Firebase) hay không?

10. Có cần phân loại thông báo theo mức độ ưu tiên (Khẩn cấp, Quan trọng, Thông thường) để hỗ trợ hiển thị trên giao diện không?