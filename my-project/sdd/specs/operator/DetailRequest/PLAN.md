# Kế hoạch Re-design Bố cục (Layout) - DetailRequest

## 1. Mục tiêu
Thiết kế lại hoàn chỉnh giao diện Chi tiết Yêu cầu sửa chữa, áp dụng cấu trúc Layout 3-cột đặc trưng của Mintlify được mô tả trong `DESIGN.md`, nhằm tạo trải nghiệm chuyên nghiệp, phân tách rõ ràng giữa nội dung chính và các thao tác/thông tin phụ.

## 2. Thiết kế Bố cục 3-Cột (3-Column Grid)
- **Cột 1 (Trái - Sidebar Nav):** Giữ nguyên `layout/sidebar.jsp` của hệ thống để điều hướng chung.
- **Cột 2 (Giữa - Center Prose):** Khu vực hiển thị nội dung chính (~720px max-width).
  - Bao gồm: Tiêu đề yêu cầu, Thẻ Category, Nội dung chi tiết văn bản (Prose), và Danh sách ảnh đính kèm (Lightbox).
- **Cột 3 (Phải - Right Panel):** Khu vực Metadata và Thao tác (~300px, tương tự cột TOC của Mintlify).
  - Bao gồm thẻ Trạng thái (Pending/In-progress/Rejected).
  - Các thông tin phụ: Người gửi, Cơ sở, Phòng, Ngày tạo.
  - Các nút hành động chính (CTAs): Nút "Nhận yêu cầu" (Black pill `button-primary`), Nút "Từ chối" (`button-secondary`), Nút "Cập nhật trạng thái". Căn chỉnh dạng block để dễ bấm.

## 3. Tech Stack & Styling
- Sử dụng Bootstrap Grid (`row`, `col-lg-8` cho Prose, `col-lg-4` cho Right Panel) để tạo 2 cột Giữa và Phải. (Do layout chung đã có Cột Trái là `#wrapper` sidebar).
- Vẫn dùng chung file `detail.jsp` nhưng cấu trúc HTML bên trong sẽ thay đổi hoàn toàn để ra dáng một trang Document của Mintlify.
