# Feature: Quản lý bài viết cá nhân (Manage Personal Posts)

**Status:** Draft  
**Author:** [Tên]  
**Reviewer:** [Tên]  
**Date:** 2026-07-09  
**Priority:** Medium

---

# 1. Business Context

Tính năng Quản lý bài viết cá nhân cho phép Tenant tạo và quản lý các bài viết trước khi được hiển thị trên bản tin của hệ thống. Mọi bài viết sau khi được tạo sẽ được gửi đến Ban quản lý để kiểm duyệt nhằm đảm bảo nội dung phù hợp trước khi công khai.

Tính năng này hỗ trợ mục tiêu xây dựng kênh trao đổi thông tin giữa Tenant và Ban quản lý, đồng thời đảm bảo mọi nội dung được kiểm soát và duy trì chất lượng của hệ thống.

---

# 2. User Stories

### Story 1 (Happy Path)

Là một **Tenant**, tôi muốn tạo bài viết kèm tiêu đề, nội dung và hình ảnh (chụp trực tiếp hoặc tải lên) để gửi Ban quản lý xét duyệt.

### Story 2 (Happy Path)

Là một **Tenant**, tôi muốn xem danh sách các bài viết mình đã tạo để theo dõi trạng thái của từng bài viết.

### Story 3 (Happy Path)

Là một **Tenant**, tôi muốn xem chi tiết một bài viết đã tạo để kiểm tra toàn bộ nội dung và hình ảnh.

### Story 4 (Happy Path)

Là một **Tenant**, tôi muốn xóa bất kỳ bài viết nào do mình tạo để không còn hiển thị trong danh sách bài viết cá nhân.

### Story 5 (Edge Case)

Là một **Tenant**, khi bài viết đã được Ban quản lý duyệt, tôi sẽ không được phép xóa bài viết đó.

---

# 3. Acceptance Criteria (EARS)

### AC-01 – Tạo bài viết

WHEN Tenant gửi biểu mẫu tạo bài viết với dữ liệu hợp lệ

THE SYSTEM SHALL:

- Lưu bài viết.
- Đặt trạng thái bài viết là **Pending**.
- Lưu thông tin người tạo và thời gian tạo.
- Trả về HTTP 201 cùng thông tin bài viết.

---

### AC-02 – Upload hình ảnh

WHEN Tenant tải ảnh lên

THE SYSTEM SHALL:

- Chỉ chấp nhận định dạng JPG, JPEG, PNG hoặc WEBP.
- Lưu ảnh thành công và trả về đường dẫn ảnh.

---

### AC-03 – Chụp ảnh trực tiếp

WHEN Tenant sử dụng camera để chụp ảnh

THE SYSTEM SHALL:

- Cho phép đính kèm ảnh vào bài viết.
- Lưu ảnh giống như ảnh được tải lên từ thiết bị.

---

### AC-04 – Xem danh sách bài viết

WHEN Tenant mở trang "Bài viết của tôi"

THE SYSTEM SHALL:

- Chỉ hiển thị các bài viết do Tenant hiện tại tạo.
- Hiển thị:
  - Hình đại diện bài viết
  - Tiêu đề
  - Ngày tạo
  - Trạng thái (Pending / Approved)
- Sắp xếp theo thời gian tạo mới nhất.

---

### AC-05 – Xem chi tiết bài viết

WHEN Tenant chọn một bài viết

THE SYSTEM SHALL hiển thị:

- Tiêu đề
- Nội dung
- Hình ảnh
- Thời gian tạo
- Trạng thái duyệt

---

### AC-06 – Xóa bài viết

WHEN Tenant yêu cầu xóa một bài viết do chính mình tạo

THE SYSTEM SHALL:

- Kiểm tra Tenant là chủ sở hữu của bài viết.
- Xóa bài viết và các dữ liệu liên quan (hình ảnh, lượt thích, bình luận nếu có).
- Bài viết sẽ không còn xuất hiện trong danh sách bài viết cá nhân và trên bản tin (nếu đã được duyệt).
- Trả về HTTP 200.

---

### AC-07 – Không có quyền xóa bài viết của người khác

WHEN Tenant yêu cầu xóa bài viết không thuộc quyền sở hữu của mình

THE SYSTEM SHALL:

- Từ chối yêu cầu.
- Trả về HTTP 403.
- Hiển thị thông báo:

> "Bạn không có quyền xóa bài viết này."

---

### AC-08 – Dữ liệu không hợp lệ

WHEN Tenant gửi biểu mẫu với tiêu đề hoặc nội dung rỗng

THE SYSTEM SHALL:

- Không tạo bài viết.
- Trả về HTTP 400.
- Trả về mã lỗi tương ứng.

---

# 4. Routing & Navigation

## 4.1 Danh sách bài viết của tôi

### Route
`GET /tenant/posts`

### View Data
- Forward sang view: `/WEB-INF/views/tenant/posts/list.jsp`
- Dữ liệu truyền xuống view (Request Attributes):
  - `posts`: Danh sách bài viết (List<Post>)
  - `activeMenu`: "posts"

---

## 4.2 Tạo bài viết

### Route (Hiển thị Form)
`GET /tenant/posts/create`
- Forward sang view: `/WEB-INF/views/tenant/posts/create.jsp`

### Route (Xử lý Form)
`POST /tenant/posts/create`
- Tham số truyền lên (Form Data): `title`, `content`, `images`
- Xử lý thành công: Redirect về `/tenant/posts` kèm Flash Message thành công.
- Xử lý thất bại: Lưu lỗi vào `request` và forward lại `create.jsp`.

---

## 4.3 Xem chi tiết bài viết

### Route
`GET /tenant/posts/{id}` (hoặc `GET /tenant/posts?id={id}`)

### View Data
- Forward sang view: `/WEB-INF/views/tenant/posts/detail.jsp`
- Dữ liệu truyền xuống view (Request Attributes):
  - `post`: Chi tiết bài viết (Post)
  - `activeMenu`: "posts"

---

## 4.4 Xóa bài viết

### Route
`POST /tenant/posts/delete` (hoặc `POST /tenant/posts?action=delete&id={id}`)
- Tham số truyền lên (Form Data): `id`
- Logic: Chỉ cho phép xóa khi `status == Pending`.
- Xử lý thành công: Redirect về `/tenant/posts` kèm Flash Message thành công.
- Xử lý thất bại (Lỗi hoặc cấm): Set `errorCode` và forward tới `error.jsp`.

---

# 5. Technical Constraints

- Thời gian phản hồi tối đa: **500 ms (P95)**.
- Rate limit: **100 requests/phút/Tenant**.
- Mỗi bài viết được phép tải lên tối đa **10 hình ảnh**.
- Kích thước mỗi ảnh không vượt quá **10 MB**.
- Chỉ chấp nhận các định dạng ảnh: **JPG, JPEG, PNG, WEBP**.
- Chỉ Tenant sở hữu bài viết mới có quyền xem hoặc xóa bài viết của mình.
- Chỉ Tenant là chủ sở hữu bài viết mới có quyền xem và xóa bài viết.
- Khi bài viết bị xóa, toàn bộ dữ liệu liên quan (hình ảnh, lượt thích, bình luận) cũng phải được xóa hoặc xử lý theo chính sách lưu trữ của hệ thống.

---

# 6. Out of Scope

- Chỉnh sửa bài viết sau khi đã tạo.
- Thông báo (Notification) khi bài viết được Ban quản lý duyệt hoặc từ chối.
- Chức năng từ chối bài viết và hiển thị lý do từ chối.
- Tìm kiếm hoặc lọc danh sách bài viết.
- Quản lý bài viết của Ban quản lý (được đặc tả trong một Specification khác).
- Hiển thị bài viết trên trang Bản tin (được đặc tả trong **Manage News Feed**).