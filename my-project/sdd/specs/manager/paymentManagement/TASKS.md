# Checklist Task Quản lý Thanh toán

Dưới đây là các đầu mục cần code theo thứ tự từ dưới lên trên (Bottom-Up) để đảm bảo không gặp lỗi phụ thuộc trong Java:

## 1. Init Database Models & DTOs
- [x] Task 1.1: Tạo class `PaymentTransaction.java` trong thư mục model ánh xạ bảng `payments`.
- [x] Task 1.2: Tạo class `PaymentListItemDTO.java` (các trường hiển thị ở list.jsp).
- [x] Task 1.3: Tạo class `PaymentDetailDTO.java` (cho màn hình xem ảnh và duyệt).

## 2. Xây dựng Data Access Object (DAO)
- [x] Task 2.1: Khởi tạo `PaymentDAO.java` kế thừa `BaseDAO`.
- [x] Task 2.2: Viết Query SQL cho `findPayments` & `countPayments` bao gồm cả filter từ khoá và logic Parameter.
- [x] Task 2.3: Viết Query SQL cho `findById`.
- [x] Task 2.4: Viết Query UPDATE trạng thái `payments` (hỗ trợ update sang `SUCCESS` hoặc `REJECTED`, lưu vết `approvedBy` và `approvedAt`) và Insert vào `audit_logs`.
- [x] Task 2.5: Viết Query UPDATE trạng thái `invoices` thành PAID (cô lập độc lập, không xài `InvoiceDAO`).

## 3. Tầng Business Service
- [x] Task 3.1: Định nghĩa Interface `PaymentService.java`.
- [x] Task 3.2: Triển khai `PaymentServiceImpl.java`. Xử lý phân trang Pagination logic.
- [x] Task 3.3: Triển khai luồng Check "Transaction Not Found" và kiểm tra State Machine (không cho Duyệt lại/Từ chối giao dịch đã `SUCCESS`) trước khi gọi DAO approve/reject.
- [x] Task 3.4: Triển khai logic Service hỗ trợ cả luồng Duyệt (Approve) và Từ chối (Reject) thanh toán.

## 4. Tầng Servlet/Controllers
- [x] Task 4.1: Xây dựng `PaymentServlet.java` (`GET /manager/payments`). Xử lý Exception gọn gàng.
- [x] Task 4.2: Xây dựng `PaymentDetailServlet.java` (`GET /manager/payments/*`).
- [x] Task 4.3: Tích hợp method `POST` trong `PaymentDetailServlet` cho cả hai hành động `/approve` (Duyệt) và `/reject` (Từ chối). Bọc `try-catch` chuyển hướng lỗi phù hợp.

## 5. UI/UX Views (JSP)
- [x] Task 5.1: Xây dựng `list.jsp` bám sát template Dashboard cũ. Hiển thị thông báo, nút Xem, table UI.
- [x] Task 5.2: Xây dựng `detail.jsp` hiển thị 2 cột, 1 bên text, 1 bên `paymentProofUrl`. Có Form cho 2 thao tác Duyệt và Từ chối kèm CSRF hidden.
- [x] Task 5.3: Mở file `sidebar.jsp`, chèn mục `Giao dịch` trỏ tới `/manager/payments` ngay bên dưới mục Hóa đơn.

## 6. Review & Testing
- [x] Thử Build chạy Clean Compile không bắt Error Java.
- [x] Kiểm tra Linter lỗi Cú pháp HTML/Java.
- [x] Trigger POST /approve để xác minh việc Update Status cho cả bảng Payments & Invoices hoạt động như ý trên UI.
