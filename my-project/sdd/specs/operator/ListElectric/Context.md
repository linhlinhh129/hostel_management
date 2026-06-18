# CONTEXT.md [Chức năng 2: Danh sách chỉ số điện nước các phòng]
# Người viết: Nguyễn Văn A | Ngày: 2026-06-11

## 1. PROBLEM STATEMENT
- **Bối cảnh:** Vào cuối kỳ thu tiền, người quản lý cần kiểm tra tiến độ ghi điện nước của toàn bộ tòa nhà hoặc dãy trọ để biết phòng nào đã hoàn thành, phòng nào còn sót để kịp thời xử lý trước khi xuất hóa đơn tổng.
- **Nỗi đau (Pain point):** Nếu không có một trang danh sách tổng hợp, người quản lý phải bấm vào từng phòng để kiểm tra trạng thái nhập liệu, dẫn đến mất thời gian, dễ bỏ sót phòng chưa ghi số, làm chậm trễ tiến độ tính tiền và gửi thông báo hóa đơn cho khách thuê.

## 2. DOMAIN KNOWLEDGE
- **Trạng thái chốt sổ (Closing Status):** Tiêu chí phân loại tiến độ hoàn thành tác vụ ghi số liệu trong tháng của một phòng.
  - *Chưa cập nhật:* Phòng chưa được thực hiện Chức năng 1 trong kỳ hiện tại.
  - *Đã cập nhật:* Phòng đã được hoàn thiện nhập số liệu mới và upload ảnh minh chứng thành công.
- **Chỉ đọc (Read-only):** Màn hình hiển thị thông tin tĩnh được lấy ra từ cơ sở dữ liệu, không cung cấp các ô nhập liệu (input), nút chỉnh sửa (edit) hay nút xóa (delete) trực tiếp trên bảng dữ liệu này.

## 3. STAKEHOLDERS
- **Quản lý / Chủ nhà (End-user):** Sử dụng màn hình này như một bảng điều khiển (Dashboard) thu nhỏ để theo dõi, giám sát tiến độ công việc chốt số điện nước đầu tháng.
- **Kế toán / Bộ phận xuất hóa đơn:** Sử dụng dữ liệu tổng hợp từ danh sách này để tiến hành tính toán tiền phòng, tiền điện, tiền nước và kết xuất hóa đơn hàng loạt.

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)
- **Tính toàn vẹn dữ liệu:** Tuyệt đối không tích hợp bất kỳ form nhập liệu hay chỉnh sửa trực tiếp nào trên màn hình này (Strictly Read-only). Mọi thay đổi về mặt số liệu bắt buộc phải được thực hiện thông qua luồng xử lý riêng biệt (như Chức năng 1).
- **Trạng thái chuẩn hóa:** Cột trạng thái chỉ được phép hiển thị đúng một trong hai giá trị nghiêm ngặt: "Chưa cập nhật" hoặc "Đã cập nhật". Không dùng các thuật ngữ mơ hồ khác.

## 5. ASSUMPTIONS (Giả định cần confirm)
- **Giả định 1:** Dữ liệu hiển thị trên danh sách này mặc định là dữ liệu của `Kỳ hiện tại` (Tháng hiện tại / Năm hiện tại).
- **Giả định 2:** Thời gian cập nhật hiển thị trên bảng sẽ lấy trực tiếp từ trường dữ liệu được ghi nhận ở Chức năng 1. Nếu trạng thái là "Chưa cập nhật", trường thời gian này sẽ hiển thị trống (null) hoặc dấu gạch ngang (-).

## 6. OPEN QUESTIONS (Câu hỏi chưa có câu trả lời)
- **Câu hỏi 1:** Có cần bổ sung bộ lọc (Filter) theo Tháng/Năm để người quản lý có thể xem lại lịch sử chỉ số điện nước của các tháng trước hay không?
- **Câu hỏi 2:** Có cần tính năng Tìm kiếm nhanh (Search) theo `Mã phòng` hoặc Lọc nhanh theo `Trạng thái chốt sổ` (Chưa cập nhật / Đã cập nhật) để tối ưu trải nghiệm khi số lượng phòng lớn (ví dụ: dãy trọ > 100 phòng) không?
- **Câu hỏi 3:** Hệ thống có cần hỗ trợ chức năng Xuất dữ liệu (Export) ra file Excel từ danh sách này để phục vụ mục đích lưu trữ nội bộ hoặc tính toán thủ công bên ngoài không?