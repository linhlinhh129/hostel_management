# Kế hoạch triển khai: Hiển thị Lịch hẹn và Kết quả cho Tenant

**Feature Branch**: `[004-title-tenant-schedule]`
**Created**: 2026-06-29

## Bối cảnh & Phương pháp tiếp cận
Hiện tại, khi Operator thao tác Xác nhận lịch hoặc Báo cáo hoàn thành, thông tin ngày hẹn và ghi chú đều được lưu tạm vào trường `rejection_reason` (và ảnh hoàn thành ở `attachment_urls2`).
Vì vậy, thay vì chỉ hiển thị nó dưới dạng "Lý do từ chối", trên giao diện chi tiết yêu cầu của Tenant (`tenant/tickets/detail.jsp`), chúng ta sẽ rẽ nhánh việc hiển thị dựa vào `ticket.status`:
- Trạng thái `REJECTED`: Hiển thị "Lý do từ chối".
- Trạng thái `IN_PROGRESS`: Hiển thị "Lịch hẹn xử lý".
- Trạng thái `DONE`: Hiển thị "Ghi chú hoàn thành" và các ảnh sau xử lý (`after_images`).

## Các thay đổi dự kiến

### 1. Frontend (JSP)

#### [MODIFY] [detail.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/tenant/tickets/detail.jsp)
- Tại khu vực `Thông tin xử lý` (Cột 4), bên dưới `Người phụ trách`:
  - **Thêm `<c:if test="${ticket.status == 'REJECTED' && not empty ticket.rejectionReason}">`**: Hiển thị Lý do từ chối (Chữ đỏ).
  - **Thêm `<c:if test="${ticket.status == 'IN_PROGRESS' && not empty ticket.rejectionReason}">`**: Hiển thị Lịch hẹn xử lý (Màu xanh dương/info). Dữ liệu nằm ở `ticket.rejectionReason`.
  - **Thêm `<c:if test="${ticket.status == 'DONE' && not empty ticket.rejectionReason}">`**: Hiển thị Ghi chú hoàn thành (Màu xanh lá/success). Dữ liệu cũng nằm ở `ticket.rejectionReason`.
- Tại khu vực `Nội dung yêu cầu` (Cột 8):
  - **Thêm phần hiển thị hình ảnh hoàn thành**:
    ```jsp
    <c:set var="images" value="${ticket.images}" />
    <!-- Ở Request.java hàm getImages() đã gộp cả attachmentUrls1 và attachmentUrls2,
         Tốt nhất là lọc riêng, hoặc vì hàm getImages() trả về tất cả, nên ta sẽ chỉ cần đảm bảo attachmentUrls2 hiển thị ở mục Kết quả -->
    ```
  - Thay vì dùng `getImages()`, ta dùng trực tiếp thuộc tính `ticket.attachmentUrls2`. Cắt chuỗi theo dấu phẩy nếu có nhiều ảnh.
    - **Thêm `<c:if test="${ticket.status == 'DONE' && not empty ticket.attachmentUrls2}">`**:
      Tạo một khối "Kết quả xử lý bằng hình ảnh" để Tenant thấy các bức ảnh Operator đã tải lên.

### 2. Backend (Java)

Không có thay đổi nào cần thiết ở Backend. `Request.java` và DAO đã xử lý việc lưu trữ và truy xuất các trường này đầy đủ. `TicketController.java` bên Tenant đã truyền toàn bộ object `Request` ra view qua biến `${ticket}`.

## Yêu cầu người dùng xem xét

> [!IMPORTANT]
> Tôi sử dụng lại cơ chế đã có (lưu đè lịch hẹn và ghi chú hoàn thành vào chung trường `rejection_reason` tùy theo Status). Nhờ vậy, chúng ta không cần sửa database, hoàn toàn tuân thủ lệnh cấm sửa database của bạn. Bạn vui lòng xem qua để chốt kế hoạch.

## Câu hỏi mở (Open Questions)
- Bạn có muốn thêm tính năng "Đánh giá sao" (Rating) từ phía Tenant khi yêu cầu đã hoàn thành không? (Hiện tại kế hoạch chỉ dừng ở mức hiển thị kết quả).

## Kế hoạch kiểm thử

### Kiểm thử thủ công
1. Đăng nhập tư cách Tenant.
2. Click vào một yêu cầu Đang xử lý (`IN_PROGRESS`), kiểm tra có thấy mục Lịch hẹn hiển thị rõ ràng bên cột phải không.
3. Click vào một yêu cầu Đã hoàn thành (`DONE`), kiểm tra có thấy mục Ghi chú hoàn thành và Hình ảnh sau xử lý không.
