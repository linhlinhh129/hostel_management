# Research: Freeze Overdue Fee

## Decision
Sử dụng truy vấn SQL subquery để lấy ngày tạo của giao dịch thanh toán PENDING mới nhất (`pending_payment_date`) cho mỗi hóa đơn. Trong Java, sử dụng ngày này thay cho ngày hiện tại (`LocalDate.now()`) làm mốc kết thúc tính phí quá hạn.

## Rationale
- Cách tiếp cận này đáp ứng yêu cầu đóng băng tiền phạt khi có giao dịch thanh toán chờ duyệt, vì việc lấy ngày tạo của `payment` cuối cùng (trạng thái `PENDING`) cho phép tính chính xác số ngày từ ngày đến hạn đến ngày thanh toán.
- Nếu `payment` bị từ chối, `status` của payment sẽ đổi (ví dụ sang `REJECTED`), do đó query `pending_payment_date` sẽ trả về `NULL`, và hệ thống tự động quay lại tính toán theo ngày hiện tại.

## Alternatives considered
- Lưu trực tiếp `late_fee` vào database ngay khi tạo payment: Không linh hoạt khi payment bị hủy/từ chối, và tạo dữ liệu rác/thừa.
- Tính toán toàn bộ trong SQL: Cú pháp phức tạp hơn khi kết hợp logic ứng dụng. Tính trong Java với dữ liệu query được là đủ nhanh và dễ bảo trì.
