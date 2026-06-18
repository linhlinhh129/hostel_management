# CONTEXT.md

## 1. Bối cảnh
Hệ thống quản lý nhà trọ cần có chức năng Quản lý Hợp đồng để lưu trữ văn bản pháp lý ràng buộc giữa Ban Quản Lý (Manager) và Người thuê đại diện (Tenant).
Hợp đồng là thực thể trung tâm kết nối Người thuê với Phòng, đồng thời quy định các thông tin tài chính cốt lõi như: Giá thuê cố định, Tiền cọc, và Thời hạn lưu trú.

## 2. Nỗi đau của User
Nếu không có chức năng quản lý hợp đồng số hóa, Ban quản lý (BQL) sẽ gặp các vấn đề:
- Quản lý trên giấy tờ dễ thất lạc, rách nát, khó tra cứu khi xảy ra tranh chấp.
- Thường xuyên quên ngày hết hạn hợp đồng, dẫn đến thất thu hoặc khách chuyển đi đột ngột.
- Khó theo dõi chính xác khách đã đóng đủ tiền cọc hay chưa.
- Rủi ro gán sai giá thuê phòng hàng tháng do không có dữ liệu gốc đối soát.

## 3. Mục tiêu
Feature Quản lý Hợp đồng giúp Manager:
- Tạo hợp đồng mới liên kết giữa 1 Phòng và 1 Người thuê đại diện.
- Lưu trữ các thông số tài chính: Giá thuê, Tiền cọc, Trạng thái thu cọc.
- Tải lên (Upload) và lưu trữ bản scan hợp đồng giấy (PDF/JPG) làm minh chứng pháp lý.
- Tự động theo dõi vòng đời hợp đồng (Sắp đến hạn, Quá hạn) thông qua hệ thống chạy ngầm (Scheduler).
- Thanh lý hợp đồng, tự động giải phóng phòng để đón khách mới.

## 4. Ràng buộc
- Một phòng chỉ được phép có TỐI ĐA MỘT hợp đồng ở trạng thái ACTIVE.
- Người thuê đại diện bắt buộc phải có tài khoản (đã tạo ở module Tenant) trước khi tạo hợp đồng.
- Không được phép thay đổi Giá thuê và Thời hạn hợp đồng sau khi hợp đồng đã ở trạng thái ACTIVE (Nếu muốn đổi, phải thanh lý và tạo hợp đồng mới).
- Hình ảnh/File scan hợp đồng tải lên không được vượt quá 5MB.
- Khi hợp đồng chuyển sang trạng thái TERMINATED (Thanh lý), trạng thái phòng tự động chuyển về AVAILABLE (Trống) và trạng thái Người thuê chuyển thành INACTIVE.

## 5. Định nghĩa trạng thái hợp đồng (Contract Status)
```text
ACTIVE
- Hợp đồng đang có hiệu lực pháp lý. Khách đang lưu trú bình thường.

EXPIRING_SOON
- Hợp đồng còn dưới 30 ngày là đến ngày hết hạn.

OVERDUE
- Hợp đồng đã vượt quá ngày hết hạn nhưng chưa được BQL thanh lý hoặc gia hạn.

TERMINATED
- Hợp đồng đã được thanh lý (kết thúc sớm hoặc đúng hạn). Không còn hiệu lực.