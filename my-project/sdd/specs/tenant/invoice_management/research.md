# Research: Tenant Invoice Utility Meter Photo Viewing UI Parity

## Decision
Tái sử dụng 100% cấu trúc giao diện (Component structure & Styling) từ màn hình Chi tiết Hóa đơn của Ban quản lý (`manager/invoices/detail.jsp`) sang màn hình Chi tiết Hóa đơn của Người thuê (`tenant/invoice-detail.jsp`).

## Rationale
- **Tính nhất quán UI/UX**: Giúp người thuê và quản lý có cùng góc nhìn trực quan khi đối chiếu dữ liệu điện nước.
- **Tiết kiệm chi phí bảo trì**: Sử dụng chung CSS card tokens (`background: #fafafa; border-bottom: 1px solid #e2e8f0; font-size: 0.875rem`), hover micro-animation (`transform: scale(1.02)`), và mở tab ảnh gốc `target="_blank"`.
- **An toàn dữ liệu**: Người thuê chỉ xem được ảnh công tơ thuộc đúng phòng và hợp đồng của mình (kiểm tra `tenant_id` / `room_id` ở Service Layer).

## Alternatives Considered
- *Modal Lightbox JS*: Phức tạp hơn và có thể phát sinh lỗi trên thiết bị di động. Link xem ảnh dạng card phong cách Mintlify đồng bộ với Manager đã được kiểm chứng hoạt động hoàn hảo trên mọi kích thước màn hình.
