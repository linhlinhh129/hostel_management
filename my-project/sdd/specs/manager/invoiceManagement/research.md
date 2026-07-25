# Research & Decisions: Hiển thị Kỳ hợp đồng trong Chi tiết Hóa đơn

## Decision 1: Truy xuất Kỳ hợp đồng từ bảng `contracts`
- **Decision**: Lấy `c.start_date` và `c.end_date` từ hợp đồng của phòng (`room_id`) tương ứng với thời điểm hóa đơn hoặc hợp đồng `ACTIVE` mới nhất.
- **Rationale**: Đảm bảo hóa đơn gắn liền với mốc thời gian hiệu lực hợp đồng của người thuê phòng đó.
- **Formatting**: Chuỗi dạng `dd/MM/yyyy - dd/MM/yyyy`. Nếu không có hợp đồng, hiển thị fallback `"Chưa có hợp đồng"`.

## Decision 2: Đóng băng phí phạt (Late Fee Freeze)
- **Decision**: Tính toán `late_fee` runtime sử dụng subquery lấy ngày tạo của giao dịch `payment` có trạng thái `PENDING` (nếu có) thay vì luôn dùng `LocalDate.now()`.
- **Rationale**: Đảm bảo đúng yêu cầu nghiệp vụ: khi tenant gửi yêu cầu thanh toán chờ duyệt, số ngày quá hạn bị ngưng đếm. Nếu bị từ chối, query không còn lấy payment đó nên tự động tiếp tục đếm đến hiện tại.
