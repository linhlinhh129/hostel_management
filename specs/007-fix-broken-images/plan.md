---
id: "007-fix-broken-images"
title: "Fix Broken Image Links in Ticket Attachments"
description: "Phân tích và khắc phục lỗi không hiển thị hình ảnh đính kèm trên cả hai phía Tenant và Operator do xung đột Context Path."
---

## 1. Phân tích nguyên nhân (Root Cause)

Qua quá trình kiểm tra mã nguồn, tôi phát hiện sự **không đồng nhất (inconsistent)** trong cách lưu đường dẫn ảnh vào Database và cách lấy đường dẫn ảnh ra để hiển thị:

1. **Khi Tenant gửi yêu cầu (TenantRequestServlet):**
   - Lưu vào DB: `attachmentUrls1` = `/uploads/tickets/hinhanh.jpg` (Không chứa Context Path).
   - Ở phía Tenant: `detail.jsp` lấy ra hiển thị bằng cách `<img src="${ctx}${ticket.attachmentUrls1}">`. Kết quả là `/hostel-management/uploads/tickets/hinhanh.jpg` (ĐÚNG, ảnh hiển thị bình thường cho Tenant).
   - Ở phía Operator: `detail.jsp` lấy ra hiển thị bằng cách `<img src="${img}">`. Kết quả là `/uploads/tickets/hinhanh.jpg` (SAI, thiếu Context Path, ảnh bị vỡ).

2. **Khi Operator báo cáo hoàn thành (DetailRequestServlet):**
   - Lưu vào DB: `attachmentUrls2` = `/hostel-management/uploads/requests/hinhanh.jpg` (Có chứa Context Path).
   - Ở phía Operator: `detail.jsp` hiển thị `<img src="${img}">`. Kết quả là `/hostel-management/uploads/requests/hinhanh.jpg` (ĐÚNG, hiển thị bình thường).
   - Ở phía Tenant: `detail.jsp` hiển thị `<img src="${ctx}${ticket.attachmentUrls2}">`. Kết quả là `/hostel-management/hostel-management/uploads/requests/hinhanh.jpg` (SAI, bị nhân đôi Context Path, ảnh bị vỡ).

**Kết luận:** Do sự không đồng nhất này, một bức ảnh luôn bị vỡ ở một bên nào đó (Tenant hoặc Operator).

## 2. Giải pháp (Solution)

Để hệ thống chuẩn chỉ và hoạt động ổn định:
- **Nguyên tắc Back-end:** Tất cả các đường dẫn ảnh lưu vào DB phải là **Đường dẫn tương đối (Relative URL)**, KHÔNG chứa Context Path (ví dụ: `/uploads/tickets/abc.jpg`).
- **Nguyên tắc Front-end:** Tất cả các tệp JSP khi hiển thị ảnh phải **luôn luôn** nối thêm `${pageContext.request.contextPath}` vào trước đường dẫn lấy từ DB.

Tuy nhiên, vì Database hiện tại có thể đang chứa một số dữ liệu cũ đã bị dính Context Path, để an toàn tuyệt đối và không làm hỏng dữ liệu cũ, ta sử dụng JSTL `fn:startsWith()` trên Front-end để kiểm tra: "Nếu chuỗi chưa có Context Path thì mới thêm vào".

## 3. Các bước thực hiện (Tasks)

1. **Cập nhật Backend (`DetailRequestServlet`, `IncidentReportServlet`, v.v.):** 
   - Xóa bỏ việc tự động nối `request.getContextPath()` trước khi lưu vào DB.
2. **Cập nhật Frontend (`operator/requests/detail.jsp`):**
   - Import JSTL Functions.
   - Thêm logic kiểm tra Context Path trước khi hiển thị `img` và Lightbox.
3. **Cập nhật Frontend (`tenant/tickets/detail.jsp`):**
   - Tương tự, dùng hàm kiểm tra và sửa lỗi double Context Path cho `attachmentUrls2`.
