# Kế hoạch triển khai: Định dạng hiển thị Lịch hẹn

**Feature Branch**: `[005-title-format-appointment-date]`
**Created**: 2026-06-30

## Bối cảnh & Phương pháp tiếp cận
Hiện tại, khi Operator chọn ngày giờ hẹn thông qua input `type="datetime-local"`, trình duyệt sẽ gửi lên chuỗi có định dạng ISO `YYYY-MM-DDTHH:mm` (ví dụ: `2026-07-01T17:58`). Chuỗi này được lưu thẳng vào biến `rejectionReason` (khi status là `IN_PROGRESS`) và sau đó được hiển thị trực tiếp ra màn hình cho cả Operator và Tenant.

Để khắc phục vấn đề hiển thị thô cứng, chúng ta sẽ thêm một logic định dạng (formatting logic) ở phía Model `Request.java`. Khi view JSP gọi lấy lý do hiển thị, nếu nó là Lịch hẹn, nó sẽ tự động parse và định dạng lại thành chuỗi thân thiện `17:58 - 01/07/2026`.

## Các thay đổi dự kiến

### 1. Backend (Java)

#### [MODIFY] [Request.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/model/Request.java)
- Thêm một hàm getter mới tên là `getFormattedRejectionReason()` hoặc `getFormattedAppointmentDate()`.
- Logic của hàm:
  - Kiểm tra xem `status` có phải là `IN_PROGRESS` không, và `rejectionReason` có dữ liệu không.
  - Thử dùng `LocalDateTime.parse()` để phân tích chuỗi ngày giờ từ `rejectionReason`.
  - Nếu parse thành công, định dạng lại bằng `DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")` và trả về.
  - Nếu có Exception (do chuỗi không phải định dạng ngày giờ chuẩn), `catch` và fallback trả về nguyên vẹn chuỗi `rejectionReason`.
  - Đối với các trạng thái khác (như `REJECTED`, `DONE`), trả về nguyên `rejectionReason` để giữ nguyên Ghi chú / Lý do từ chối.

### 2. Frontend (JSP)

#### [MODIFY] [operator/requests/detail.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/operator/requests/detail.jsp)
- Tại khối hiển thị `Lịch hẹn xử lý`, thay thế biến `${reqDetail.rejectionReason}` bằng `${reqDetail.formattedAppointmentDate}`.

#### [MODIFY] [tenant/tickets/detail.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/tenant/tickets/detail.jsp)
- Tại khối hiển thị `Lịch hẹn xử lý`, thay thế biến `${ticket.rejectionReason}` bằng `${ticket.formattedAppointmentDate}`.

## Yêu cầu người dùng xem xét

> [!TIP]
> Việc đẩy logic parse ngày giờ xuống Model (Java) giúp chúng ta vừa đảm bảo tái sử dụng code cho cả Operator và Tenant, vừa có tính an toàn (fallback) nếu lỡ DB lưu chuỗi không chuẩn. Cách này cũng tuân thủ không sửa database. Bạn vui lòng xem qua để chốt kế hoạch.

## Kế hoạch kiểm thử

### Kiểm thử thủ công
1. Đăng nhập tư cách Operator.
2. Mở một yêu cầu đang `PENDING`, click **Tiếp nhận**.
3. Mở yêu cầu đó (lúc này là `ASSIGNED`), nhập lịch hẹn bất kỳ (ví dụ 10:30 sáng 22/10/2026) và Xác nhận.
4. Kiểm tra trang Chi tiết yêu cầu hiện tại, dòng Lịch hẹn hiển thị đúng `10:30 - 22/10/2026` chưa.
5. Đăng xuất, đăng nhập vào Tenant, mở yêu cầu đó lên, xem dòng Lịch hẹn có hiển thị giống hệt vậy không.
