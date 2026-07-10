# UI Specification - Community News Feed

## 1. Overview

Thiết kế giao diện theo phong cách **Facebook News Feed**, hiện đại, tối giản, sử dụng Bootstrap 5.

Trang chỉ hiển thị các bài viết đã được **Ban quản lý phê duyệt** trong ngày hiện tại.

Màu sắc và khoảng cách phải thống nhất với toàn bộ Dashboard của hệ thống.

---

# 2. Layout

```
------------------------------------------------------------
 Sidebar |                  Top Navbar                Avatar
------------------------------------------------------------
         |  News Feed
         |
         |  ------------------------------------------
         |  Post Card
         |  ------------------------------------------
         |
         |  ------------------------------------------
         |  Post Card
         |  ------------------------------------------
         |
         |  ------------------------------------------
         |  Post Card
         |  ------------------------------------------
```

Sidebar và Navbar sử dụng lại template Dashboard hiện có.

Khu vực chính (Main Content) căn giữa màn hình.

Độ rộng khoảng:

- Desktop: 700 - 800px
- Tablet: 100%
- Mobile: Responsive.

Background màu xám rất nhạt (#f5f6fa).

---

# 3. Page Header

Hiển thị:

```
Community News Feed

Các bài viết đã được Ban quản lý phê duyệt hôm nay.
```

Header đơn giản, không có nút tạo bài viết.

---

# 4. News Feed

Hiển thị danh sách bài viết theo chiều dọc.

Sắp xếp:

- mới nhất lên trước
- chỉ lấy bài viết APPROVED
- chỉ lấy bài viết được duyệt trong ngày

Mỗi bài viết là một Card độc lập.

Khoảng cách giữa các Card:

24px

---

# 5. Post Card

Mỗi Card bo góc lớn.

Border mỏng.

Shadow nhẹ.

Padding khoảng 20-24px.

Card gồm các phần sau.

---

## 5.1 Post Header

Bên trái:

- Avatar hình tròn (40x40)
- Tên Ban quản lý
- Thời gian đăng

Ví dụ:

```
👤 Ban quản lý

08:30 AM • Hôm nay
```

Bên phải:

- Icon ba chấm (...)

Hiện tại chưa có chức năng.

---

## 5.2 Title

Tiêu đề nổi bật.

Font:

- Bold
- 22px

Ví dụ

```
Thông báo bảo trì thang máy
```

---

## 5.3 Content

Hiển thị nội dung đầy đủ của bài viết.

Không cắt bớt.

Có khoảng cách giữa các đoạn.

---

## 5.4 Image

Nếu bài viết có ảnh.

Hiển thị:

- full width
- border radius 12px
- object-fit: cover

Chiều cao khoảng:

350 - 450px

Nếu không có ảnh.

Không hiển thị vùng ảnh.

---

## 5.5 Statistics

Một dòng nhỏ hiển thị:

```
👍 28 lượt thích

💬 12 bình luận
```

Màu chữ xám.

Font nhỏ.

---

## 5.6 Divider

Một đường kẻ ngang.

---

## 5.7 Actions

Hiển thị hai nút.

```
👍 Thích

💬 Bình luận
```

Chiều ngang chia đều.

Hover đổi màu.

Nếu người dùng đã thích:

Icon Like đổi sang màu xanh.

---

# 6. Comment Section

Ngay dưới phần Actions.

Hiển thị:

Danh sách bình luận.

Mỗi bình luận gồm:

Avatar

Tên

Thời gian

Nội dung

Ví dụ

```
👤 Nguyễn Văn A

Bài viết rất hữu ích.

08:42
```

Các bình luận sắp xếp theo thời gian tăng dần.

---

# 7. Add Comment

Cuối mỗi bài viết.

Hiển thị:

Avatar người dùng.

Textbox.

Placeholder:

```
Viết bình luận...
```

Nút:

```
Gửi
```

Nhấn Enter hoặc nút Gửi đều đăng bình luận.

Sau khi thành công.

Comment mới xuất hiện cuối danh sách.

Không cần reload trang.

---

# 8. Empty State

Nếu hôm nay chưa có bài viết.

Hiển thị:

Icon Newspaper.

Tiêu đề:

```
Chưa có bài viết nào hôm nay
```

Mô tả:

```
Các bài viết được Ban quản lý phê duyệt sẽ xuất hiện tại đây.
```

---

# 9. Loading State

Trong khi tải dữ liệu.

Hiển thị Skeleton Card.

Khoảng 3 Card.

Không dùng spinner toàn màn hình.

---

# 10. Responsive

Desktop

Hiển thị Feed giữa màn hình.

Tablet

Card rộng khoảng 90%.

Mobile

Card chiếm gần toàn màn hình.

Padding nhỏ hơn.

Ảnh tự co giãn.

---

# 11. UI Style

Bootstrap 5.

Card trắng.

Border Radius:

16px.

Shadow:

Rất nhẹ.

Animation:

Fade in khi tải.

Hover nhẹ khi rê chuột.

Icon sử dụng Bootstrap Icons hoặc Lucide Icons.

---

# 12. Data Mapping

community_posts

- title
- content
- image_url
- author_id
- status
- created_at

post_reactions

- Tổng số lượt thích
- Người dùng đã thích hay chưa

post_comments

- Danh sách bình luận
- Tổng số bình luận

---

# 13. UX Requirements

Không có phân trang.

Sử dụng Infinite Scroll hoặc Load More.

Không hiển thị bài viết đã bị Soft Delete.

Không hiển thị bài viết PENDING.

Không hiển thị bài viết REJECTED.

Chỉ hiển thị bài viết APPROVED được duyệt trong ngày.

Ưu tiên trải nghiệm giống mạng xã hội Facebook nhưng giữ phong cách Dashboard doanh nghiệp.