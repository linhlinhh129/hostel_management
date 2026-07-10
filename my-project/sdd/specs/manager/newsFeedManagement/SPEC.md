# Feature: Bảng tin cộng đồng

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** 2026-07-08\
**Priority:** High

---

# 1. Business Context

Bảng tin cộng đồng là nơi hiển thị các bài viết đã được Ban quản lý phê duyệt nhằm truyền tải thông báo, tin tức và các hoạt động đến người dùng theo dạng mạng xã hội. Tính năng này giúp tăng khả năng tương tác giữa Ban quản lý và cư dân thông qua việc thích và bình luận bài viết.

Tính năng góp phần nâng cao hiệu quả truyền thông nội bộ và tăng mức độ tương tác trong hệ thống.

---

# 2. User Stories

### Story 1 - Xem bảng tin

**Là** Ban quản lý, **tôi muốn** xem các bài viết đã được duyệt trong ngày **để** theo dõi nội dung đã được công bố.

### Story 2 - Xem chi tiết bài viết

**Là** Ban quản lý, **tôi muốn** xem đầy đủ nội dung của một bài viết **để** đọc toàn bộ thông tin và các bình luận liên quan.

### Story 3 - Thích hoặc bỏ thích bài viết

**Là** Ban quản lý, **tôi muốn** thích hoặc bỏ thích một bài viết **để** thể hiện sự quan tâm đối với bài viết đó.

### Story 4 - Bình luận bài viết

**Là** Ban quản lý, **tôi muốn** bình luận vào bài viết **để** trao đổi hoặc bổ sung thông tin.

### Story 5 - Xem danh sách bình luận

**Là** Ban quản lý, **tôi muốn** xem toàn bộ bình luận của một bài viết **để** theo dõi các trao đổi giữa người dùng.

---

# 3. Acceptance Criteria (EARS)

### UC01 - Xem bảng tin

- **WHEN** Ban quản lý truy cập trang Bảng tin
- **THE SYSTEM SHALL** hiển thị tất cả bài viết có trạng thái **APPROVED**.
- **AND** chỉ hiển thị các bài viết được duyệt trong ngày hiện tại.
- **AND** sắp xếp bài viết theo thời gian tạo mới nhất.

---

### UC02 - Hiển thị thông tin bài viết

- **WHEN** bài viết được hiển thị trên bảng tin
- **THE SYSTEM SHALL** hiển thị:
  - Hình ảnh (nếu có)
  - Tiêu đề
  - Nội dung
  - Người đăng
  - Thời gian đăng
  - Số lượt thích
  - Số lượng bình luận

---

### UC03 - Xem chi tiết bài viết

- **WHEN** Ban quản lý chọn một bài viết
- **THE SYSTEM SHALL** hiển thị toàn bộ nội dung bài viết.
- **AND** hiển thị toàn bộ danh sách bình luận của bài viết.

---

### UC04 - Thích bài viết

- **WHEN** Ban quản lý chọn nút "Thích"
- **THE SYSTEM SHALL** lưu một bản ghi vào bảng `post_reactions`.
- **AND** cập nhật tổng số lượt thích của bài viết.

---

### UC05 - Bỏ thích bài viết

- **WHEN** Ban quản lý chọn "Bỏ thích"
- **THE SYSTEM SHALL** xóa bản ghi tương ứng trong bảng `post_reactions`.
- **AND** cập nhật lại tổng số lượt thích.

---

### UC06 - Bình luận bài viết

- **WHEN** Ban quản lý nhập nội dung bình luận hợp lệ
- **THE SYSTEM SHALL** tạo một bản ghi mới trong bảng `post_comments`.
- **AND** cập nhật thời gian tạo bình luận.

---

### UC07 - Dữ liệu bình luận không hợp lệ

- **WHEN** nội dung bình luận để trống
- **THE SYSTEM SHALL** từ chối lưu bình luận.
- **AND** trả về HTTP 400 cùng thông báo lỗi.

---

### UC08 - Xem danh sách bình luận

- **WHEN** Ban quản lý mở chi tiết bài viết
- **THE SYSTEM SHALL** hiển thị tất cả bình luận chưa bị xóa.
- **AND** sắp xếp bình luận theo thời gian tạo tăng dần.

---

### UC09 - Phân quyền

- **WHILE** người dùng chưa đăng nhập
- **THE SYSTEM SHALL** không cho phép thích hoặc bình luận bài viết.

---

# 4. API Contract

## 4.1 Lấy danh sách bảng tin

**Endpoint**

```
GET /api/v1/news-feed
```

**Response 200**

```json
{
  "success": true,
  "data": [
    {
      "postId": 1,
      "title": "Thông báo",
      "content": "...",
      "imageUrl": "...",
      "author": "Manager",
      "totalLikes": 15,
      "totalComments": 6
    }
  ]
}
```

---

## 4.2 Xem chi tiết bài viết

**Endpoint**

```
GET /api/v1/news-feed/{postId}
```

**Response 200**

```json
{
  "success": true,
  "data": {
    "postId": 1,
    "title": "...",
    "content": "...",
    "comments": []
  }
}
```

---

## 4.3 Thích bài viết

**Endpoint**

```
POST /api/v1/posts/{postId}/reactions
```

**Response 201**

```json
{
  "success": true
}
```

---

## 4.4 Bỏ thích bài viết

**Endpoint**

```
DELETE /api/v1/posts/{postId}/reactions
```

**Response 200**

```json
{
  "success": true
}
```

---

## 4.5 Bình luận bài viết

**Endpoint**

```
POST /api/v1/posts/{postId}/comments
```

**Request**

```json
{
  "content": "Bài viết rất hữu ích."
}
```

**Response 201**

```json
{
  "success": true
}
```

---

## 4.6 Danh sách bình luận

**Endpoint**

```
GET /api/v1/posts/{postId}/comments
```

**Response 200**

```json
{
  "success": true,
  "data": []
}
```

---

# 5. Technical Constraints

- Chỉ hiển thị bài viết có trạng thái **APPROVED**.
- Chỉ hiển thị bài viết được duyệt trong ngày hiện tại.
- Mỗi người dùng chỉ được thích một lần trên mỗi bài viết.
- Một người dùng có thể bình luận nhiều lần trên cùng một bài viết.
- Nội dung bình luận tối đa **1000 ký tự**.
- Hình ảnh bài viết được lấy từ trường `image_url`.
- Thời gian phản hồi API không vượt quá **500 ms (P95)**.
- Rate limit: **100 requests/phút/người dùng**.

---

# 6. Out of Scope

- Chỉnh sửa hoặc xóa bình luận.
- Chia sẻ bài viết.
- Gắn thẻ người dùng.
- Đăng bài trực tiếp từ Bảng tin.
- Thông báo thời gian thực khi có lượt thích hoặc bình luận mới.
- Tìm kiếm và lọc bài viết.