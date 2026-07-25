# Implementation Plan: Quản lý bài viết cộng đồng (Manager)

## 1. Mục tiêu (Goal)
Xây dựng tính năng "Quản lý bài viết cộng đồng" cho Ban quản lý (Manager), bao gồm các chức năng tạo bài viết, xem danh sách tất cả bài viết (bao gồm PENDING, APPROVED), duyệt bài viết và xóa mềm bài viết.

## 2. Thiết kế Database (ĐÃ HOÀN THÀNH)
- Cơ sở dữ liệu đã được khởi tạo với các bảng `community_posts`, `post_reactions`, và `post_comments`. 
- **CẤM** không được thay đổi, chỉnh sửa hay chạy lệnh tạo mới database.

## 3. Kiến trúc Backend (Java 17 + Servlet + JDBC)
- **Cấu trúc Package:** Theo chuẩn của dự án (`com.quanlyphongtro`).
- **Model:** Tạo lớp `CommunityPost` trong package `com.quanlyphongtro.model`.
- **DTO:** Tạo các lớp `CommunityPostDTO`, `CommunityPostCreateDTO` trong package `com.quanlyphongtro.dto`.
- **DAO:** Tạo lớp `CommunityPostDAO` trong package `com.quanlyphongtro.dao` (Sử dụng `PreparedStatement`, query phải lọc `deleted_at IS NULL`). Kế thừa từ `BaseDAO` nếu có thể.
- **Service:** Tạo interface `CommunityPostService` và class `CommunityPostServiceImpl` trong `com.quanlyphongtro.service` để xử lý business logic, upload file, và tính toán Cursor-based pagination.
- **Controller (Servlet):** Tạo `CommunityPostServlet` (kế thừa `BaseServlet`) trong package `com.quanlyphongtro.controller.manager` để xử lý các endpoint:
  - `GET /manager/articles/create` (Hiển thị Form tạo bài viết)
  - `POST /manager/articles/create` (Xử lý tạo bài viết)
  - `GET /manager/articles` (Lấy danh sách tất cả bài viết)
  - `GET /manager/articles/detail` (Xem chi tiết bài viết, hỗ trợ Modal phóng to ảnh)
  - `POST /manager/articles/approve` (Duyệt bài)
  - `POST /manager/articles/delete` (Xóa mềm bài viết)

## 4. Thiết kế Frontend (JSP + Bootstrap 5)
- **Thư mục:** `/WEB-INF/views/manager/postManagement/`
- **Files:**
  - `list-pending.jsp`: Danh sách tất cả bài viết (có phân trang).
  - `create.jsp`: Form tạo bài viết mới (Tiêu đề, Nội dung, Upload Ảnh).
  - `detail.jsp`: Hiển thị chi tiết bài viết và hỗ trợ Modal phóng to ảnh khi click vào ảnh đính kèm.
- Giao diện tuân thủ layout có sẵn trong dự án và hệ thống thiết kế `Mintlify` (theo DESIGN.md).

## 5. Ràng buộc Kỹ thuật & Bảo mật (Constraints & Security)
- **Rate Limit:** Trả về các header `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` nếu request là dạng API/JSON. Giới hạn 100 req/min.
- **Pagination:** Sử dụng Cursor-based pagination.
- **Soft Delete:** Logic xóa chỉ cập nhật `deleted_at = CURRENT_TIMESTAMP`. Tuyệt đối không dùng lệnh `DELETE` vật lý.
- **File Upload:** Chỉ nhận định dạng `.jpg`, `.jpeg`, `.png`, dung lượng tối đa 5MB. Lưu cục bộ.
- **Authorization:** Controller phải được cấu hình kiểm tra quyền (chỉ `MANAGER` được truy cập).
- **Logging:** Dùng SLF4J, tuyệt đối không dùng `System.out.println()`. Không log PII dạng thô.
