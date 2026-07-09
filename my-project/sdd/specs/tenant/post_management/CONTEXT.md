# CONTEXT.md - Manage Personal Posts (Tenant)
**Người viết:** @name  
**Ngày:** 2026-07-09

---

## 1. PROBLEM STATEMENT

Hiện tại Tenant chưa có một kênh chính thức để gửi các thông tin, thông báo hoặc phản ánh đến Ban quản lý ngay trên hệ thống. Việc trao đổi thông tin thông qua các nền tảng bên ngoài (như Zalo hoặc Facebook) khiến nội dung bị phân tán, khó quản lý và không thể theo dõi trạng thái xử lý.

Ngoài ra, Tenant cũng không có khả năng quản lý các bài viết mà mình đã gửi, chẳng hạn như xem lại nội dung, theo dõi trạng thái hoặc xóa bài viết khi không còn nhu cầu chia sẻ.

---

## 2. DOMAIN KNOWLEDGE

### Tenant
Người đang sinh sống trong tòa nhà hoặc khu ký túc xá và có quyền tạo bài viết gửi đến Ban quản lý.

### Post
Một bài viết do Tenant tạo, bao gồm:
- Tiêu đề
- Nội dung
- Một hoặc nhiều hình ảnh (tùy chọn)

### Pending
Trạng thái mặc định của mọi bài viết sau khi được tạo. Bài viết ở trạng thái này chưa được hiển thị trên Bản tin và đang chờ Ban quản lý kiểm duyệt.

### Approved
Trạng thái của bài viết sau khi được Ban quản lý phê duyệt. Chỉ các bài viết ở trạng thái này mới được hiển thị trên Bản tin.

### News Feed
Trang hiển thị các bài viết đã được Ban quản lý phê duyệt trong ngày hiện tại để toàn bộ Tenant có thể xem, thích và bình luận.

### Ownership
Tenant chỉ được quản lý (xem, xóa) các bài viết do chính mình tạo.

### Image
Hình ảnh có thể được:
- Chụp trực tiếp bằng camera của thiết bị.
- Tải lên từ thư viện ảnh của thiết bị.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

- **Tenant**
  - Tạo bài viết.
  - Theo dõi các bài viết của mình.
  - Xóa bài viết khi không còn nhu cầu.

### Secondary Stakeholders

- **Ban quản lý**
  - Nhận các bài viết từ Tenant.
  - Thực hiện kiểm duyệt trước khi công khai.

### System Administrator

- Quản lý hạ tầng lưu trữ bài viết và hình ảnh.
- Đảm bảo dữ liệu được lưu trữ an toàn.

---

## 4. CONSTRAINTS

### Business Constraints

- Mọi bài viết mới đều phải được đưa vào danh sách chờ duyệt.
- Chỉ Ban quản lý mới có quyền phê duyệt bài viết.
- Chỉ bài viết đã được phê duyệt mới được hiển thị trên Bản tin.
- Tenant chỉ được xem và xóa các bài viết do chính mình tạo.

### Technical Constraints

- Hệ thống phải hỗ trợ tải ảnh từ thiết bị và chụp ảnh trực tiếp.
- Hình ảnh phải được lưu trữ trên hệ thống lưu trữ tập trung (Cloud Storage hoặc File Storage).
- API phải yêu cầu người dùng đã xác thực (Authenticated User).
- Thời gian phản hồi của API không vượt quá 500 ms (P95).

---

## 5. ASSUMPTIONS

Các giả định dưới đây cần được xác nhận với Product Owner hoặc Business Analyst:

1. Tenant có thể tạo bài viết với nhiều hình ảnh.
2. Hình ảnh là trường không bắt buộc.
3. Tenant có thể xóa bài viết ở mọi trạng thái (Pending hoặc Approved).
4. Khi Tenant xóa bài viết đã được phê duyệt, bài viết sẽ không còn hiển thị trên Bản tin.
5. Khi bài viết bị xóa, các lượt thích và bình luận liên quan cũng sẽ bị xóa.
6. Tenant không được chỉnh sửa bài viết sau khi đã tạo (nếu có, sẽ được đặc tả ở một feature khác).
7. Tenant chỉ có thể truy cập các bài viết do chính mình tạo.

---

## 6. OPEN QUESTIONS

1. Tenant có được chỉnh sửa bài viết trước khi Ban quản lý duyệt không?
2. Ban quản lý có thể từ chối bài viết không, hay chỉ có hai lựa chọn là duyệt hoặc xóa?
3. Khi bài viết bị từ chối, Tenant có được xem lý do từ chối không?
4. Số lượng hình ảnh tối đa cho mỗi bài viết là bao nhiêu?
5. Kích thước tối đa của mỗi hình ảnh là bao nhiêu?
6. Có cần hỗ trợ video hoặc các loại tệp đính kèm khác trong tương lai không?
7. Khi Tenant xóa một bài viết đã được phê duyệt và đã có lượt thích hoặc bình luận, hệ thống sẽ xóa hoàn toàn dữ liệu hay lưu lại để phục vụ mục đích kiểm toán (audit)?
8. Có cần lưu lịch sử (Audit Log) khi Tenant tạo hoặc xóa bài viết không?
9. Có cần gửi thông báo (Notification) cho Ban quản lý khi Tenant tạo bài viết mới không?
10. Tenant có được xem số lượt xem (View Count) của bài viết hay không?