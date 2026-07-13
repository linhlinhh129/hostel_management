# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng Cập nhật thông tin phòng (Diện tích và Giá phòng) để Admin có thể cấu hình chi tiết cho từng phòng sau khi phòng được tạo hoặc khi có sự thay đổi về mức giá cho thuê.

Việc thiết lập chính xác diện tích và giá phòng là cơ sở quan trọng để tiến hành ký kết hợp đồng thuê phòng, tính toán các chi phí và doanh thu sau này.

## 2. Nỗi đau của User

Nếu không có chức năng này, Admin có thể gặp các vấn đề sau:
* Không thể cập nhật được giá trị cốt lõi của một phòng sau khi khởi tạo.
* Gặp khó khăn khi muốn thay đổi giá phòng hàng loạt hoặc theo từng thời điểm.
* Không có thông tin diện tích để khách thuê tham khảo.
* Thiếu thông tin cơ bản để cấu hình hợp đồng.
* Nếu phòng thuộc một cơ sở đã ngừng hoạt động (INACTIVE), việc vô tình thay đổi thông tin có thể gây sai lệch dữ liệu lịch sử.

## 3. Mục tiêu

Feature Cập nhật thông tin phòng giúp Admin:
* Xem thông tin chi tiết hiện tại của một phòng.
* Cập nhật diện tích (m²) của phòng.
* Cập nhật giá phòng (VNĐ) của phòng.
* Xóa thông tin diện tích và giá phòng (để trống) nếu cần thiết.
* Ngăn chặn việc chỉnh sửa phòng nếu cơ sở chứa phòng đó đã bị vô hiệu hóa.

## 4. Ràng buộc

* Chỉ Admin được phép cập nhật thông tin phòng (Tenant và Manager không có quyền hoặc có quyền hạn chế tuỳ theo thiết kế).
* Diện tích và Giá phòng nếu được nhập phải là số không âm.
* Có thể để trống diện tích và giá phòng (giá trị `null` trong Database).
* Nếu `facilityStatus` của cơ sở chứa phòng là `INACTIVE`, tuyệt đối không cho phép cập nhật thông tin phòng.
* Việc cập nhật phải được phản ánh ngay lập tức trên giao diện.

## 5. Câu hỏi mở

* Có cần lưu lại lịch sử thay đổi giá phòng (Price History) để theo dõi biến động giá không?
* Khi giá phòng thay đổi, các hợp đồng đang thuê (Active Contracts) có bị ảnh hưởng hay không? (Thường là không, chỉ áp dụng cho hợp đồng mới).
* Có cần giới hạn mức giá tối đa/tối thiểu cho một phòng không?
