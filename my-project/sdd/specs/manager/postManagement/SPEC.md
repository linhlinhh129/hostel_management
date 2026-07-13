# Feature: Quản lý bài viết cộng đồng

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** 2026-07-08\
**Priority:** High

---

# 1. Business Context

Ban quản lý cần một chức năng để tạo và kiểm duyệt các bài viết trước khi chúng được hiển thị đến cư dân. Điều này giúp đảm bảo nội dung được kiểm soát, chính xác và phù hợp với quy định của khu trọ/chung cư. Tính năng này hỗ trợ quy trình quản lý thông tin nội bộ, giúp nâng cao chất lượng truyền thông giữa ban quản lý và người thuê.

---

# 2. User Stories
### Story 1 - Tạo bài viết
**Là** Ban quản lý, **tôi muốn** tạo bài viết với tiêu đề, nội dung và hình ảnh (chụp trực tiếp hoặc tải từ thiết bị) **để** gửi bài viết vào danh sách chờ duyệt.

### Story 2 - Xem danh sách bài viết
**Là** Ban quản lý, **tôi muốn** xem danh sách tất cả bài viết trên hệ thống (bao gồm PENDING, APPROVED) **để** theo dõi và quản lý.

### Story 3 - Duyệt bài viết
**Là** Ban quản lý, **tôi muốn** duyệt một bài viết trong danh sách chờ **để** bài viết được phép hiển thị cho người dùng.

### Story 4 - Xóa bài viết
**Là** Ban quản lý, **khi** phát hiện bài viết không còn cần thiết hoặc có nội dung không phù hợp, **tôi muốn** xóa bài viết khỏi danh sách chờ duyệt.

---

# 3. Acceptance Criteria (EARS)
### UC01 - Tạo bài viết
- **WHEN** Ban quản lý nhập đầy đủ tiêu đề và nội dung bài viết
- **AND** chọn hình ảnh bằng cách chụp trực tiếp hoặc tải ảnh từ thiết bị (không bắt buộc)
- **THE SYSTEM SHALL** tạo bài viết mới với trạng thái **PENDING**.
- **AND** lưu thời gian tạo, người tạo và đường dẫn ảnh (nếu có).


### UC02 - Dữ liệu không hợp lệ
- **WHEN** tiêu đề hoặc nội dung để trống
- **THE SYSTEM SHALL** từ chối tạo bài viết.
- **AND** trả về HTTP 400 cùng thông báo lỗi phù hợp.


### UC03 - Xem danh sách bài viết
- **WHEN** Ban quản lý truy cập trang "Danh sách bài viết"
- **THE SYSTEM SHALL** hiển thị danh sách tất cả bài viết trên hệ thống (không phân biệt trạng thái).
- **AND** mỗi dòng trong bảng hiển thị:
  - Tiêu đề
  - Tác giả
  - Thời gian đăng
  - Trạng thái (Chờ duyệt, Đã duyệt)
  - Các nút thao tác (Chi tiết, Duyệt, Xóa)


### UC04 - Duyệt bài viết
- **WHEN** Ban quản lý chọn "Duyệt" đối với một bài viết PENDING
- **THE SYSTEM SHALL**
  - gọi API qua AJAX POST.
  - cập nhật trạng thái bài viết thành **APPROVED**.
  - tải lại trang danh sách hoặc chi tiết.


### UC05 - Xóa bài viết
- **WHEN** Ban quản lý chọn "Xóa"
- **THE SYSTEM SHALL**
  - gọi API qua AJAX POST.
  - đánh dấu bài viết đã bị xóa bằng cách cập nhật **deleted_at** (Soft delete).
  - tải lại trang hoặc chuyển hướng về danh sách bài viết.


### UC06 - Phân quyền
- **WHILE** người dùng không thuộc Ban quản lý
- **THE SYSTEM SHALL** không cho phép truy cập các chức năng tạo, duyệt hoặc xóa bài viết.

# 4. Servlet & API Contract
Quản lý bài viết kết hợp giữa Servlet render giao diện (JSP/Form HTML) và API xử lý AJAX cho các tác vụ cần thiết (Duyệt/Xóa/Load JSON).

## 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `CommunityPostServlet` |
| **URL Pattern** | `/manager/articles`, `/manager/articles/*`, `/manager/community-posts/*`, `/manager/articles/detail` |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `currentUser` / `UserSessionDTO` từ Session) |

---

## 4.2 Giao diện và Request Attributes (JSP/HTML Form)

### Xem danh sách (list-pending.jsp)
- **Endpoint:** `GET /manager/articles`
- **Query Params:** `cursor`, `limit` (tùy chọn)
- **Attribute:** `posts` (`List<CommunityPostDTO>`), `nextCursor`
- **Lưu ý:** Nếu request mang header `X-Requested-With: XMLHttpRequest` (AJAX) hoặc `Accept: application/json`, hệ thống sẽ trả về chuỗi JSON. Giao diện hiển thị dạng bảng (Table) với các cột Tiêu đề, Tác giả, Thời gian đăng, Trạng thái, Thao tác.

### Xem chi tiết (detail.jsp)
- **Endpoint:** `GET /manager/articles/detail?id={postId}`
- **Attribute:** `post` (`CommunityPostDTO`)
- **Tính năng UI:** Hỗ trợ Modal phóng to hình ảnh (Image Viewer Modal) khi người dùng click vào ảnh đính kèm bài viết.

### Màn hình tạo bài viết (create.jsp)
- **Endpoint GET:** `GET /manager/articles/create` (forward form)
- **Endpoint POST:** `POST /manager/articles/create`
  - **Payload:** `multipart/form-data` chứa `title`, `content` (text) và `image` (file upload)
  - **Xử lý thành công:** Redirect (`sendRedirect`) về trang danh sách kèm cờ `?success=create`.
  - **Xử lý thất bại:** Bắt lỗi và forward ngược lại trang `create.jsp` với thuộc tính `error` hiển thị trên màn hình.

---

## 4.3 AJAX Endpoints (Trả về JSON)

### Duyệt bài viết
- **Endpoint:** `POST /manager/articles/approve`
- **Tham số Request:** `postId`
- **Response 200 (Thành công):**
  ```json
  {
    "success": true,
    "message": "Bài viết đã được duyệt."
  }
  ```

### Xóa bài viết
- **Endpoint:** `POST /manager/articles/delete`
- **Tham số Request:** `postId`
- **Response 200 (Thành công):**
  ```json
  {
    "success": true,
    "message": "Bài viết đã được xóa."
  }
  ```

---

## 4.4 Xử lý lỗi (Servlet Behavior)

| Tình huống | Hành vi |
|---|---|
| `GET /manager/articles/detail` lỗi ID / Không tìm thấy | Chuyển hướng về trang danh sách kèm tham số query `?error=invalid` hoặc `?error=notfound` |
| AJAX Endpoint (Duyệt/Xóa) bị lỗi Validation | Trả về HTTP `400 Bad Request` kèm JSON chứa `"error"` |
| Có Exception hoặc Lỗi Server | Trả về HTTP `500 Internal Server Error` kèm JSON chứa lỗi hệ thống hoặc chuyển hướng kèm lỗi tuỳ context gọi |

---

# 5. Technical Constraints

- Chỉ Ban quản lý được phép truy cập chức năng này.
- Tiêu đề không được vượt quá **250 ký tự**.
- Nội dung bài viết không được để trống.
- Hỗ trợ:
  - Chụp ảnh trực tiếp từ thiết bị.
  - Tải ảnh từ thư viện thiết bị.
- Chỉ chấp nhận định dạng ảnh JPG, JPEG, PNG.
- Kích thước ảnh tối đa: **5 MB**.
- Thời gian phản hồi API không vượt quá **500 ms (P95)**.
- Rate limit: **100 requests/phút/người dùng**.
- Bài viết mới luôn được tạo với trạng thái **PENDING**.
- Khi xóa bài viết sử dụng **Soft Delete** bằng trường `deleted_at`.

---

# 6. Out of Scope

- Chỉnh sửa bài viết sau khi đã tạo.
- Từ chối bài viết và nhập lý do từ chối.
- Bình luận hoặc thả cảm xúc cho bài viết.
- Thông báo tự động đến cư dân sau khi bài viết được duyệt.
- Lên lịch đăng bài tự động.