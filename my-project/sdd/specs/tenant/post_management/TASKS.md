# TASKS: Manage Personal Posts

## 1. Database (SQL)
- [ ] Tạo script cập nhật database (thêm bảng `posts` và `post_images`) và bổ sung vào file `schema.sql`.

## 2. Backend (APIs)
- [ ] **Infrastructure & Models**:
  - [ ] Khởi tạo entity/model `Post` và `PostImage`.
  - [ ] Tạo các class DTO cho Request và Response.
- [ ] **Services & Controllers**:
  - [ ] Implement middleware/service chức năng Upload File (validate định dạng JPG/PNG/WEBP, giới hạn dung lượng 10MB, tối đa 10 files).
  - [ ] Implement API `POST /api/v1/posts` (Tạo bài viết, xử lý lưu thông tin và ảnh).
  - [ ] Implement API `GET /api/v1/posts/my` (Lấy danh sách bài viết cá nhân của Tenant).
  - [ ] Implement API `GET /api/v1/posts/{id}` (Xem chi tiết bài viết kèm mảng ảnh).
  - [ ] Implement API `DELETE /api/v1/posts/{id}` (Kiểm tra quyền sở hữu, kiểm tra trạng thái bài viết và thực hiện xóa).
- [ ] **Unit Testing**:
  - [ ] Viết test cases cho logic xác thực quyền xóa bài viết (trả về 403 nếu sai user).
  - [ ] Viết test cases cho validation khi tạo bài viết (dữ liệu rỗng, file ảnh không đúng chuẩn).

## 3. Frontend (UI)
- [ ] **Components**:
  - [ ] Tạo component Form tạo bài viết mới (inputs cho title, content, hỗ trợ drag-drop ảnh và sử dụng camera).
  - [ ] Tạo component Danh sách bài viết cá nhân (hiển thị tiêu đề, ngày tạo, thẻ trạng thái Pending/Approved).
  - [ ] Tạo component Chi tiết bài viết (modal hoặc trang riêng, hiển thị album ảnh).
- [ ] **Integration & State Management**:
  - [ ] Tích hợp gọi API `POST /api/v1/posts` khi submit form.
  - [ ] Tích hợp gọi API `GET /api/v1/posts/my` để tải danh sách bài viết lúc mở trang.
  - [ ] Tích hợp gọi API `GET /api/v1/posts/{id}` khi Tenant nhấn xem chi tiết.
  - [ ] Tích hợp gọi API `DELETE /api/v1/posts/{id}` (hiển thị dialog "Bạn có chắc chắn muốn xóa?" trước khi gọi).
- [ ] **Validation & Error Handling**:
  - [ ] Thêm logic kiểm tra (validation) phía client: bắt buộc nhập tiêu đề/nội dung, số lượng ảnh <= 10.
  - [ ] Bắt các mã lỗi HTTP (400, 401, 403) và hiển thị thông báo thân thiện cho Tenant bằng Toast/Snackbar.
