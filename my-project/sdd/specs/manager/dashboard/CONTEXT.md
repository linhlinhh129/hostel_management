# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có một màn hình Dashboard (Bảng điều khiển) tập trung dành cho Ban quản lý (Manager). 

Màn hình này đóng vai trò là trung tâm giám sát hoạt động của cơ sở được phân công, giúp Manager nhanh chóng nắm bắt tình trạng lấp đầy phòng, tình hình nhân khẩu cư trú, trạng thái các yêu cầu sửa chữa, tiến độ thu nợ và dòng tiền doanh thu trong tháng hiện tại.

## 2. Nỗi đau của User

Nếu không có màn hình Dashboard tổng quan:

* Manager mất nhiều thời gian truy cập vào từng phân hệ (phòng, hóa đơn, sự cố, cư dân) để cộng dồn thủ công số liệu báo cáo.
* Khó có cái nhìn tổng quát về hiệu suất cho thuê (tỷ lệ lấp đầy phòng).
* Dễ bỏ sót các hóa đơn nợ quá hạn chưa được đôn đốc nhắc nhợ.
* Bỏ sót các yêu cầu sửa chữa mới nhận từ cư dân cần xử lý gấp.
* Khó theo dõi tổng doanh thu thực tế đã thu trong tháng hiện tại và tổng số tiền còn tồn đọng chưa thu được.

Vì vậy, hệ thống cần cung cấp giao diện Dashboard tổng hợp thông tin tự động theo thời gian thực cho từng Manager.

## 3. Mục tiêu

Feature Dashboard cho Ban quản lý giúp Manager:

* Theo dõi tổng số lượng phòng, phòng trống, phòng đã cho thuê và tỷ lệ lấp đầy phòng của cơ sở.
* Giám sát số lượng người thuê chính và người phụ thuộc đang sinh sống thực tế.
* Theo dõi các chỉ số tài chính: số hợp đồng đang hiệu lực, số hóa đơn chưa thanh toán, số hóa đơn nợ quá hạn, số giao dịch thanh toán đang chờ duyệt, doanh thu tháng này, và tổng nợ tồn đọng.
* Thống kê trạng thái sự cố (Mới, Đang xử lý, Đã hoàn thành, Đã từ chối) và danh sách 5 sự cố mới gửi gần nhất để xử lý kịp thời.
* Hiển thị thông tin tổng quan của cơ sở đang quản lý (Tên cơ sở, mã cơ sở, trạng thái hoạt động).

## 4. Ràng buộc

* Manager chỉ được xem số liệu thống kê thuộc cơ sở mình được giao quản lý (đối chiếu cột `manager_id` trong bảng `dbo.facilities`).
* Dashboard lấy thông tin tổng hợp của cơ sở đầu tiên được phân quyền cho Manager nếu Manager quản lý nhiều cơ sở.
* Các con số tài chính và vận hành phải phản ánh chính xác trạng thái thực tế trong database tại thời điểm tải trang.
* Doanh thu tháng hiện tại (`monthlyRevenue`) chỉ tính trên các hóa đơn có trạng thái `PAID` và được tạo trong tháng/năm hiện tại.
* Tổng nợ tồn đọng (`totalOutstanding`) là tổng giá trị của tất cả hóa đơn ở trạng thái `UNPAID` và `OVERDUE` thuộc cơ sở.
* Phân chia trạng thái sự cố hiển thị trên Dashboard:
  * Mới: Trạng thái `NEW` hoặc `PENDING`
  * Đang xử lý: Trạng thái `RECEIVED`, `ASSIGNED`, hoặc `IN_PROGRESS`
  * Hoàn thành: Trạng thái `RESOLVED` hoặc `DONE`
  * Từ chối: Trạng thái `REJECTED`

## 5. Câu hỏi mở

* **Có cần biểu đồ trực quan (chart) thể hiện biến động doanh thu hoặc tỷ lệ lấp đầy không?**
  * *Trả lời:* Hiện tại hệ thống chỉ hiển thị số liệu phẳng dưới dạng các thẻ chỉ số (card) và danh sách bảng biểu đơn giản, chưa tích hợp biểu đồ đồ họa.
* **Nếu Manager quản lý nhiều cơ sở, họ có thể chuyển đổi cơ sở để xem số liệu khác nhau không?**
  * *Trả lời:* Hệ thống hiện tại mặc định lấy cơ sở đầu tiên hoạt động của Manager (`TOP 1`) để hiển thị số liệu Dashboard, chưa hỗ trợ chọn/chuyển đổi cơ sở.
* **Có hỗ trợ xuất báo cáo định dạng Excel/PDF cho các số liệu trên Dashboard không?**
  * *Trả lời:* Không được triển khai trong phạm vi tính năng này.
* **Danh sách sự cố gần nhất hiển thị bao nhiêu bản ghi?**
  * *Trả lời:* Giới hạn hiển thị 5 yêu cầu sự cố mới nhất xếp theo thứ tự thời gian tạo giảm dần.
