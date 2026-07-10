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

### Story 2 - Xem danh sách chờ duyệt

**Là** Ban quản lý, **tôi muốn** xem danh sách tất cả bài viết đang chờ duyệt **để** theo dõi và xử lý.

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

---

### UC02 - Dữ liệu không hợp lệ

- **WHEN** tiêu đề hoặc nội dung để trống
- **THE SYSTEM SHALL** từ chối tạo bài viết.
- **AND** trả về HTTP 400 cùng thông báo lỗi phù hợp.

---

### UC03 - Xem danh sách chờ duyệt

- **WHEN** Ban quản lý truy cập trang "Bài viết chờ duyệt"
- **THE SYSTEM SHALL** hiển thị danh sách các bài viết có trạng thái **PENDING**.
- **AND** mỗi bài viết hiển thị:
  - Tiêu đề
  - Nội dung tóm tắt
  - Hình ảnh (nếu có)
  - Người tạo
  - Thời gian tạo

---

### UC04 - Duyệt bài viết

- **WHEN** Ban quản lý chọn "Duyệt" đối với một bài viết
- **THE SYSTEM SHALL**
  - cập nhật trạng thái bài viết thành **APPROVED**
  - lưu người duyệt vào trường **reviewed_by**
  - cập nhật **updated_at**
  - bài viết không còn xuất hiện trong danh sách chờ duyệt.

---

### UC05 - Xóa bài viết

- **WHEN** Ban quản lý chọn "Xóa"
- **THE SYSTEM SHALL**
  - đánh dấu bài viết đã bị xóa bằng cách cập nhật **deleted_at**
  - bài viết không còn xuất hiện trong danh sách chờ duyệt.

---

### UC06 - Phân quyền

- **WHILE** người dùng không thuộc Ban quản lý
- **THE SYSTEM SHALL** không cho phép truy cập các chức năng tạo, duyệt hoặc xóa bài viết.

---

# 4. API Contract

## 4.1 Tạo bài viết

**Endpoint**

```
POST /api/v1/community-posts
```

**Request**

```json
{
  "title": "Thông báo bảo trì thang máy",
  "content": "Thang máy tòa A sẽ bảo trì từ 08:00 đến 12:00.",
  "imageUrl": "string | null"
}
```

**Response 201**

```json
{
  "success": true,
  "data": {
    "postId": 1,
    "status": "PENDING"
  }
}
```

**Response**

- 400: Dữ liệu không hợp lệ
- 401: Chưa đăng nhập
- 403: Không có quyền

---

## 4.2 Danh sách bài viết chờ duyệt

**Endpoint**

```
GET /api/v1/community-posts/pending
```

**Response 200**

```json
{
  "success": true,
  "data": [
    {
      "postId": 1,
      "title": "...",
      "status": "PENDING"
    }
  ]
}
```

---

## 4.3 Duyệt bài viết

**Endpoint**

```
PUT /api/v1/community-posts/{postId}/approve
```

**Response 200**

```json
{
  "success": true,
  "message": "Bài viết đã được duyệt."
}
```

---

## 4.4 Xóa bài viết

**Endpoint**

```
DELETE /api/v1/community-posts/{postId}
```

**Response 200**

```json
{
  "success": true,
  "message": "Bài viết đã được xóa."
}
```

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