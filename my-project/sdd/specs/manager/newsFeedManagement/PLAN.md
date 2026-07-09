# KẾ HOẠCH TRIỂN KHAI: BẢNG TIN CỘNG ĐỒNG (MANAGER NEWS FEED)

## 1. Mục tiêu
Xây dựng Bảng tin cộng đồng (News Feed) cho Ban quản lý hiển thị các bài viết được duyệt trong ngày. Hướng tới một giao diện **tuyệt đẹp, hiện đại và tối giản** mang đậm phong cách mạng xã hội (tương tự Facebook), cung cấp trải nghiệm mượt mà, thân thiện nhưng vẫn giữ được nét chuyên nghiệp của một hệ thống quản lý (Dashboard).

## 2. Kiến trúc & Thiết kế

### 2.1. Frontend (Giao diện chuẩn UI/UX)
- **Layout tổng thể:** Tái sử dụng Sidebar và Navbar hiện có. Vùng nội dung chính (Feed) được căn giữa, chiều rộng tối ưu 700-800px trên Desktop và responsive 100% trên Mobile/Tablet. Nền trang màu xám nhạt (`#f5f6fa`) để làm nổi bật các bài viết.
- **Post Card (Thẻ bài viết):** Thiết kế dạng thẻ trắng với góc bo tròn lớn (`16px`), viền mỏng và hiệu ứng đổ bóng mờ (rất nhẹ). Nội dung rõ ràng, khoảng cách thoáng đãng (padding 20-24px).
- **Hình ảnh:** Tự động căn chỉnh full-width bên trong thẻ, bo góc 12px, cắt cúp `object-fit: cover`, giới hạn chiều cao 350-450px để khung hình luôn cân đối.
- **Trạng thái & Hiệu ứng (Animations):**
  - **Loading State:** Sử dụng *Skeleton Card* (hiệu ứng khung xương tải trang) thay vì spinner quay tròn, mang lại cảm giác mượt mà.
  - **Empty State:** Trạng thái trống đẹp mắt khi không có bài viết (Icon Newspaper lớn, thông báo rõ ràng).
  - **Hover & Transitions:** Hiệu ứng mờ dần (Fade-in) khi tải bài, nút tương tác đổi màu mượt mà khi rê chuột. Nút "Like" sẽ chuyển sang màu xanh dương khi người dùng đã thích.
- **Tương tác (Fetch API):** Cơ chế Thích và Bình luận hoạt động tức thời (Real-time feel) mà không cần tải lại trang. Các bình luận cũ nhất xếp trên, mới nhất xếp dưới.

### 2.2. Backend (Xử lý nghiệp vụ & Data)
- **Servlet & API:** 
  - `ManagerNewsFeedServlet`: Render khung giao diện `/WEB-INF/views/manager/newsFeedManagement/news-feed.jsp`.
  - Các API nội bộ (`/api/v1/news-feed`, `/api/v1/posts/reactions`, `/api/v1/posts/comments`): Cung cấp dữ liệu JSON để giao diện có thể Infinite Scroll hoặc Load More.
- **DAO & Tối ưu:** 
  - `NewsFeedDAO`: Truy vấn bài viết (`APPROVED` & duyệt `trong ngày`). Bắt buộc dùng lệnh `JOIN` hoặc truy vấn gom (Batch) để đếm số Like, số Comment và trạng thái Like của người xem chỉ trong 1 lần query (tránh lỗi N+1 làm chậm hệ thống).
  - Tái sử dụng các bảng `post_reactions` và `post_comments` đã có sẵn, tuyệt đối KHÔNG can thiệp sửa đổi schema Database.

## 3. Các thành phần cốt lõi cần thực thi
1. **Tầng CSS & Styles:** Xây dựng file CSS riêng cho News Feed (hoặc viết inline block `<style>`) định nghĩa các class `.post-card`, `.skeleton-loader`, `.comment-box` theo đúng Spec `NEWS_FEED.md`.
2. **Tầng JS Logic:** Module JS quản lý việc fetch dữ liệu, render HTML động, xử lý event click Like/Comment và bảo vệ bằng CSRF Token.
3. **Tầng Controller & DAO:** Các endpoint bảo mật cao, giới hạn 1000 ký tự comment, chặn spam, phản hồi dưới 500ms.

## 4. Ràng buộc (Tuân thủ nghiêm ngặt)
- Không thay đổi các file code của chức năng khác, chỉ tạo mới các file dành riêng cho News Feed.
- Bắt buộc xử lý XSS khi render nội dung bài viết và bình luận (dùng JSTL `<c:out>` hoặc hàm Escape HTML trong JS).

## 5. Câu hỏi mở (Cần Ban Quản Lý xác nhận)
1. Trong Spec có nhắc đến **Infinite Scroll hoặc Load More**. Để mang lại trải nghiệm mượt mà nhất, tôi sẽ ưu tiên nút "Tải thêm bài viết" (Load More) hoặc tự động cuộn (Infinite Scroll). Bạn thích phương án nào hơn?
2. Bộ icon sử dụng sẽ ưu tiên dùng thư viện có sẵn (FontAwesome / Bootstrap Icons) đang được tích hợp trong dự án.

Vui lòng duyệt Kế hoạch (PLAN) và Task list mới được cập nhật. Nếu bạn đồng ý, tôi sẽ bắt tay vào "phù phép" giao diện này!
