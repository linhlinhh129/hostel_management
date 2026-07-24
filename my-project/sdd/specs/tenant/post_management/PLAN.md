# Implementation Plan: Manage Personal Posts (Tenant)

## 1. Context and Goals
Tính năng Quản lý bài viết cá nhân (Manage Personal Posts) cho phép Tenant tạo, xem chi tiết, xem danh sách và xóa các bài viết cá nhân (kèm hình ảnh). Các bài viết này ban đầu sẽ ở trạng thái **Pending** và chờ Ban quản lý phê duyệt trước khi hiển thị trên Bản tin (News Feed).

## 2. Open Questions & Spec Contradictions
> [!WARNING]
> **Mâu thuẫn trong SPEC.md:**
> - **Story 5** ghi: "khi bài viết đã được Ban quản lý duyệt, tôi sẽ **không được phép** xóa bài viết đó."
> - **AC-06** ghi: "Bài viết sẽ không còn xuất hiện trong danh sách bài viết cá nhân và trên bản tin (**nếu đã được duyệt**)." => Hàm ý là có thể xóa bài viết đã duyệt.
> - **Context Assumption 3** cũng đề cập "Tenant có thể xóa bài viết ở mọi trạng thái (Pending hoặc Approved)."
> 
> **Quyết định đề xuất (Action needed):** Cần chốt lại với PO/BA để thống nhất việc Tenant có được phép xóa bài viết đã duyệt hay không để cài đặt validation ở API `DELETE /api/v1/posts/{id}` cho phù hợp.

## 3. Database Design
Bổ sung thêm bảng mới vào thiết kế cơ sở dữ liệu (`schema.sql`) để lưu trữ bài viết và hình ảnh:

### `posts` table
- `post_id` (INT IDENTITY PRIMARY KEY)
- `tenant_id` (INT, FK to `users`)
- `title` (NVARCHAR(200) NOT NULL)
- `content` (NVARCHAR(MAX) NOT NULL)
- `status` (NVARCHAR(20) DEFAULT 'PENDING') -- PENDING, APPROVED, REJECTED
- `created_at` (DATETIME2 DEFAULT GETDATE())
- `updated_at` (DATETIME2 DEFAULT GETDATE())
- `deleted_at` (DATETIME2 NULL)

### `post_images` table
Do yêu cầu tối đa 10 ảnh mỗi bài, ta nên lưu thành bảng riêng để dễ quản lý hoặc mở rộng sau này:
- `image_id` (INT IDENTITY PRIMARY KEY)
- `post_id` (INT, FK to `posts`)
- `image_url` (NVARCHAR(500) NOT NULL)
- `created_at` (DATETIME2 DEFAULT GETDATE())

## 4. Backend (API Services)
- **POST /api/v1/posts**: 
  - Validate dữ liệu đầu vào (title, content không được rỗng).
  - Xử lý upload ảnh (tối đa 10 ảnh, kích thước tối đa 10MB/ảnh, format: JPG, JPEG, PNG, WEBP).
  - Lưu bản ghi vào bảng `posts` và `post_images`. Trả về `HTTP 201`.
- **GET /api/v1/posts/my**:
  - Lấy danh sách bài viết dựa trên `tenant_id` (từ context xác thực của user đang đăng nhập).
  - Trả về danh sách được sắp xếp theo `created_at` giảm dần (DESC). Trả về `HTTP 200`.
- **GET /api/v1/posts/{id}**:
  - Trả về chi tiết bài viết (bao gồm nội dung và mảng `images`). 
  - Đảm bảo kiểm tra quyền truy cập (nếu cần giới hạn chỉ chủ bài viết hoặc admin được xem bài Pending).
- **DELETE /api/v1/posts/{id}**:
  - Xác thực quyền sở hữu của Tenant (trả về `HTTP 403` nếu không phải chủ sở hữu).
  - Áp dụng rule ở phần 2 (được phép xóa bài đã Approve hay không).
  - Thực hiện Soft Delete đối với bài viết và xóa các dữ liệu liên quan. Trả về `HTTP 200`.

## 5. Frontend (Tenant Web/App)
- **UI Components**:
  - Form tạo bài viết: Nhập tiêu đề, nội dung, component upload nhiều file (tích hợp chọn ảnh từ thư viện hoặc chụp camera thiết bị di động).
  - Danh sách bài viết: Layout dạng danh sách hiển thị Thumbnail bài viết, Title, Date, Status (badge màu cho Pending/Approved).
  - Chi tiết bài viết: Hiển thị đầy đủ nội dung văn bản và bộ sưu tập (gallery) hình ảnh.
  - Nút Xóa bài viết: Nút xóa kèm popup xác nhận an toàn trước khi xóa, hiển thị toast message nếu xóa lỗi (VD: không được phép xóa).
- **Integration**:
  - Gọi các RESTful API đã định nghĩa.
  - Hiển thị toast notifications phản hồi cho Tenant (tạo thành công, xóa thành công, lỗi xác thực, lỗi format ảnh...).
