# Kế hoạch Triển khai (Implementation Plan) - Payment Management

Dựa trên tài liệu `CONTEXT.md` và `SPEC.md`, đây là kế hoạch chi tiết để triển khai module Quản lý Thanh toán mà không làm ảnh hưởng đến các module khác. Kế hoạch này áp dụng cấu trúc Java Servlet kết hợp JSP truyền thống đang dùng (tương tự module Hóa đơn).

## 1. Cấu trúc Database và DAO
Bảng dữ liệu `payments` đã tồn tại trong `schema.sql` (chứa các trường `payment_id`, `code`, `invoice_id`, `room_id`, `status`, `payment_date`, `payment_method`, `payment_amount`, `created_by`, v.v.). Do đó:
- **KHÔNG SỬA ĐỔI DATABASE**.
- Khởi tạo Data Access Object: `PaymentDAO.java` kế thừa `BaseDAO.java`.
- Trong `PaymentDAO`, sẽ viết các truy vấn SQL thuần sử dụng `PreparedStatement` thay vi dùng ORM để bám sát kiến trúc cũ.
- Các hàm SQL cần viết:
  - Lấy danh sách giao dịch có phân trang: `findPayments(keyword, status, offset, limit)`.
  - Đếm tổng số giao dịch để phân trang: `countPayments(keyword, status)`.
  - Lấy chi tiết một giao dịch: `findById(paymentId)`.
  - Cập nhật trạng thái `payments`: `updatePaymentStatus()`.
  - Thiết lập hàm phụ cập nhật trạng thái hóa đơn `invoices` liên quan sang 'PAID' thông qua một câu Query UPDATE an toàn, bảo đảm gọi kèm Audit Log theo quy định của SPEC. (Note: Không thay đổi `InvoiceDAO.java` để tránh đứt gãy hệ thống cũ).

## 2. API & Servlets Controller
Tuân thủ Servlet API Contract đã lên trong SPEC, nhưng ánh xạ vào Controller hiện hành `BaseServlet`.
Sẽ tách thành 2 Servlet riêng biệt để tránh đụng độ wild-card mapping như đã từng bị:

- **1. PaymentServlet.java (`GET /manager/payments`)**
  - Nhận Params: `keyword`, `status`, `page`.
  - Gọi Service lấy DTO list và số liệu Pagination.
  - Forward attribute trang tới view: `WEB-INF/views/manager/payments/list.jsp`.

- **2. PaymentDetailServlet.java (`GET /manager/payments/*` và `POST /manager/payments/*/approve`)**
  - HTTP `GET`: Parse ID từ URL, kiểm tra điều kiện NotFound. Lấy DTO detail, forward trả về view `detail.jsp` kèm url ảnh chứng từ.
  - HTTP `POST` cho Approve:
    - Bắt Error: Nếu ID sai `404`, nếu user chưa auth báo `401`/`403`.
    - Handle IllegalStateException (`PAYMENT_ALREADY_APPROVED`).
    - Gọi DAO Update payment status sang `PAID`, sau đó gọi DAO update invoice thành `PAID`.
    - Trả về Flash Message và redirect lại màn Chi tiết.

## 3. Các tầng Service và Model DTO
- **Models**: Tạo file `PaymentTransaction.java` ánh xạ table.
- **DTOs**:
  - `PaymentListItemDTO.java` (cho màn List).
  - `PaymentDetailDTO.java` (cho màn Detail).
- **Service**: Tạo Interface `PaymentService` và Implement `PaymentServiceImpl`. Ở đây đặt Logic catch lỗi, kiểm tra transaction tồn tại mớ cho duyệt, phân trang.

## 4. JSP Views & Security
- Tạo các file trong thư mục: `src/main/webapp/WEB-INF/views/manager/payments/`
  1. `list.jsp`: Hiển thị Table các Payments từ DTO có Select Filter.
  2. `detail.jsp`: Hiển thị form duyệt và `<img>` URL Proof. Form chứa CSRF Token: `<input type="hidden" name="csrfToken" value="${csrfToken}" />`.
- Phân quyền (Security): 2 Servlet trên sẽ được tự động chặn bởi `RoleFilter` và `AuthFilter` được quy ước sẵn với pattern `/manager/*`.

---

> [!IMPORTANT]
> **Không được dùng `/*` kết hợp với gốc cho một Servlet duy nhất.** Chúng ta đã học bài học lỗi `404/500`, phải tách rõ 2 Servlet cho `List` và `Detail`. Cấm sửa Database schema. Không dùng ORM phức tạp. Không thay đổi các DAO cũ. Tất cả mã nguồn chỉ sinh ra Class/File mới.
