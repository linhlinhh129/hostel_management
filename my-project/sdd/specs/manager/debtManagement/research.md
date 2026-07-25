# Research: Quản lý công nợ

## 1. Cơ chế gửi thông báo In-app (In-app Notification)
- **Decision**: Sử dụng bảng `notifications` hiện có trong database để lưu thông báo cho người dùng, kết hợp lấy dữ liệu khi người dùng đăng nhập hoặc qua polling/WebSocket nếu hệ thống có hỗ trợ.
- **Rationale**: Đảm bảo người thuê nhận được thông báo ngay trên hệ thống một cách đáng tin cậy. Phù hợp với quyết định mới nhất từ người dùng (Clarification 2026-07-25).
- **Alternatives considered**: Gửi Email (bị loại vì không được yêu cầu trong luồng này).

## 2. Loại bỏ Thuế (Tax)
- **Decision**: Toàn bộ logic tính toán công nợ sẽ chỉ dựa trên Tạm tính (bao gồm tiền phòng, điện, nước, dịch vụ, internet, phí khác và phí phạt chậm nộp).
- **Rationale**: Yêu cầu nghiệp vụ mới đã loại bỏ hoàn toàn khái niệm thuế khỏi hệ thống.
- **Alternatives considered**: Giữ lại cột thuế nhưng mặc định bằng 0 (bị loại vì gây nhiễu code và rủi ro bảo trì).
