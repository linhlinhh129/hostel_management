# Research & Decisions: Hiển thị Kỳ hợp đồng trong Chi tiết Hóa đơn

## Decision 1: Truy xuất Kỳ hợp đồng từ bảng `contracts`
- **Decision**: Lấy `c.start_date` và `c.end_date` từ hợp đồng của phòng (`room_id`) tương ứng với thời điểm hóa đơn hoặc hợp đồng `ACTIVE` mới nhất.
- **Rationale**: Đảm bảo hóa đơn gắn liền với mốc thời gian hiệu lực hợp đồng của người thuê phòng đó.
- **Formatting**: Chuỗi dạng `dd/MM/yyyy - dd/MM/yyyy`. Nếu không có hợp đồng, hiển thị fallback `"Chưa có hợp đồng"`.
