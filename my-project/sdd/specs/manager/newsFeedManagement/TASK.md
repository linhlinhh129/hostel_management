# DANH SÁCH CÔNG VIỆC: BẢNG TIN CỘNG ĐỒNG (BEAUTIFUL UI)

- [x] **1. Tầng Giao diện (JSP & CSS) - Cốt lõi của UI**
  - [x] Khởi tạo file `/WEB-INF/views/manager/newsFeedManagement/news-feed.jsp`.
  - [x] Viết bộ CSS chuyên dụng (hoặc inline style class) đảm bảo:
    - [x] Nền xám nhạt `#f5f6fa`, vùng nội dung rộng `700-800px` căn giữa.
    - [x] `Post Card` bo góc `16px`, viền mỏng, đổ bóng siêu nhẹ, padding `20-24px`.
    - [x] Ảnh bài viết full width, bo góc `12px`, `object-fit: cover`, cao tối đa `400px`.
  - [x] Xây dựng Component **Skeleton Loading State**: 3 thẻ Card nhấp nháy mờ hiển thị khi đang chờ API.
  - [x] Xây dựng Component **Empty State**: Bảng tin trống với Icon Newspaper lớn và câu thông báo.
  - [x] Cấu trúc HTML cho từng bài viết: Header (Avatar, Tên, Giờ), Title (22px Bold), Content (khoảng cách đoạn rõ ràng), Statistics (Likes, Comments text xám), Divider, Actions (Thích, Bình luận - hover đổi màu), Comment Section (List, Form nhập).

- [x] **2. Tầng Logic Frontend (JavaScript)**
  - [x] Viết hàm Fetch API lấy danh sách bài viết (Load initial và Load More).
  - [x] Tích hợp logic xử lý sự kiện:
    - [x] Nút "Thích" (đổi màu xanh khi active) không reload trang.
    - [x] Input "Bình luận" (bấm Enter hoặc nút Gửi), render ngay lập tức bình luận mới xuống dưới.
  - [x] Ngăn chặn tấn công XSS bằng hàm escape HTML trước khi render nội dung bình luận vào DOM.
  - [x] Quản lý CSRF Token trong mọi Headers gửi đi.

- [x] **3. Tầng Data Access (DAO)**
  - [x] Tạo `NewsFeedDAO`: Truy vấn `community_posts` (chỉ `APPROVED` & trong ngày).
    - *Chú ý*: Tối ưu hoá SQL JOIN với `post_reactions` và `post_comments` để đếm tổng Like, tổng Comment, và trạng thái Like của người dùng (isLiked) trong 1 lần query.
  - [x] Kiểm tra các hàm `insertReaction`, `deleteReaction` trong `ReactionDAO` (nếu chưa có thì tạo).
  - [x] Kiểm tra các hàm `insertComment`, `getCommentsByPostId` trong `CommentDAO` (nếu chưa có thì tạo).

- [x] **4. Tầng Controller & Service (Backend)**
  - [x] Tạo `ManagerNewsFeedServlet` mapping `/manager/news-feed` để render khung JSP tĩnh.
  - [x] Tạo các API Servlet:
    - [x] `NewsFeedApiServlet`: API `GET` trả về danh sách bài viết dạng JSON.
    - [x] `ReactionApiServlet`: API `POST/DELETE` thêm/bỏ Like. (Đã gộp vào NewsFeedApiServlet)
    - [x] `CommentApiServlet`: API `POST` đăng bình luận (validate tối đa 1000 ký tự), API `GET` lấy danh sách bình luận cũ. (Đã gộp vào NewsFeedApiServlet)
  - [x] Đảm bảo giới hạn thời gian phản hồi API (viết query tối ưu).
  
- [x] **5. Tích hợp & Kiểm thử**
  - [x] Thêm liên kết "Bảng Tin" vào `sidebar.jsp` dẫn về `/manager/news-feed`.
  - [x] Kiểm thử Responsive (trên Desktop, Tablet, Mobile).
  - [x] Kiểm thử trải nghiệm người dùng (hover, loading, thao tác mượt mà).
