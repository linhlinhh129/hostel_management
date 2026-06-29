# Kế hoạch triển khai: Luồng xử lý yêu cầu của Operator

**Feature Branch**: `[003-title-operator-request-workflow]`
**Created**: 2026-06-29

## Bối cảnh & Phương pháp tiếp cận
Dựa trên tài liệu Đặc tả (spec.md) vừa cập nhật, luồng nghiệp vụ chuẩn được định nghĩa gồm 3 bước:
1. Tiếp nhận (PENDING -> ASSIGNED)
2. Xác nhận lịch (ASSIGNED -> IN_PROGRESS)
3. Hoàn thành (IN_PROGRESS -> DONE)

Tại backend, `DetailRequestServlet.java` đã có sẵn các logic (`action=accept`, `action=schedule`, `action=complete`) gọi xuống `RequestServiceImpl`. Mục tiêu chính của kế hoạch này là kết nối chuẩn xác luồng trên giao diện frontend (`operator/requests/detail.jsp`) để tương tác đúng với các action của backend.

## Các thay đổi dự kiến

### 1. Frontend (JSP)

#### [MODIFY] [detail.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/operator/requests/detail.jsp)
- Bổ sung cấu trúc rẽ nhánh (`<c:choose>`) để hiển thị form/nút bấm tùy theo thuộc tính `reqDetail.status`:
  - **Khi status là 'PENDING'**: Hiển thị nút "Tiếp nhận" (Submit form với `action=accept`).
  - **Khi status là 'ASSIGNED'**: Hiển thị form "Xác nhận lịch", yêu cầu nhập ngày hẹn `appointmentDate` (Submit form với `action=schedule`).
  - **Khi status là 'IN_PROGRESS'**: Hiển thị form "Hoàn thành", yêu cầu nhập ghi chú `notes` và tùy chọn tải ảnh đính kèm (Submit form với `action=complete`).
- Ẩn toàn bộ các nút thao tác nếu trạng thái đã chuyển sang 'DONE', 'REJECTED', hoặc 'CANCELLED'.

### 2. Backend (Java)

#### [MODIFY] [RequestServiceImpl.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/service/impl/RequestServiceImpl.java) (Nếu cần thiết)
- Đảm bảo hàm `acceptRequest` chỉ xử lý khi trạng thái đang là PENDING và chuyển nó sang ASSIGNED.
- Đảm bảo hàm `scheduleAppointmentText` xử lý đúng trạng thái ASSIGNED và cập nhật lên IN_PROGRESS, đồng thời ghi lại nội dung ngày hẹn.
- Đảm bảo hàm `completeRequest` xử lý chính xác trạng thái IN_PROGRESS và cập nhật lên DONE cùng với ghi chú và ảnh.

#### [MODIFY] [RequestDAO.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/dao/RequestDAO.java) (Nếu cần thiết)
- Kiểm tra lại các truy vấn UPDATE để đảm bảo không bị lỗi dữ liệu khi cập nhật trường trạng thái.

## Yêu cầu người dùng xem xét

> [!IMPORTANT]
> Các form thao tác sẽ được thiết kế liền mạch ngay dưới nội dung chi tiết yêu cầu, giúp Operator không phải chuyển trang. Xin hãy xác nhận nếu bạn đồng ý với cách hiển thị này!

## Câu hỏi mở (Open Questions)

- Nếu người dùng nhập ngày hẹn (Xác nhận lịch) bằng chuỗi văn bản tự do thay vì một công cụ chọn ngày (Date Picker) cụ thể thì có sao không? Hiện backend đang lưu nó ở dạng text.

## Kế hoạch kiểm thử

### Kiểm thử thủ công (Manual Verification)
1. Đăng nhập với tài khoản Operator.
2. Mở một yêu cầu PENDING -> Click "Tiếp nhận" -> Kiểm tra thấy giao diện hiện ra form Xác nhận lịch.
3. Nhập ngày hẹn -> Click "Xác nhận lịch" -> Kiểm tra giao diện cập nhật sang form Hoàn thành.
4. Nhập ghi chú -> Click "Hoàn thành" -> Kiểm tra yêu cầu được đóng (DONE).
