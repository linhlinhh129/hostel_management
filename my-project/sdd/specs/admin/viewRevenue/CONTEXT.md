# CONTEXT.md

## Problem Statement

Admin cần theo dõi doanh thu của toàn bộ các cơ sở trong hệ thống nhà trọ để đánh giá hiệu quả kinh doanh và khả năng thu hồi công nợ.

Hiện tại việc tổng hợp doanh thu từ nhiều cơ sở mất nhiều thời gian và dễ xảy ra sai sót khi thực hiện thủ công.

## User Pain Points

* Khó theo dõi doanh thu của tất cả cơ sở tại một nơi.
* Mất thời gian tổng hợp số liệu từ nhiều nguồn.
* Khó đánh giá tình trạng thanh toán và công nợ.
* Thiếu dữ liệu để hỗ trợ ra quyết định kinh doanh.

## Constraints

* Chỉ Admin được phép truy cập báo cáo doanh thu.
* Chỉ các hóa đơn hợp lệ được sử dụng để tính doanh thu.
* Khoảng thời gian lọc phải hợp lệ.
* Hệ thống phải hỗ trợ số lượng cơ sở lớn.

## Open Questions

1. Doanh thu được tính theo ngày lập hóa đơn hay ngày thanh toán?
2. Có cần hỗ trợ lọc theo từng cơ sở không?
3. Có cần hỗ trợ xem theo tháng, quý, năm không?
4. Có cần xuất báo cáo Excel hoặc PDF trong giai đoạn sau không?
5. Có cần phân quyền chi tiết hơn ngoài Admin không?
