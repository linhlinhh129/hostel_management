# Research: Tối giản thao tác Hóa đơn & Loại bỏ luồng Xóa Hóa đơn

## Decision 1: Cột thao tác bảng danh sách Hóa đơn chỉ duy nhất nút "Xem"
- **Decision**: Giữ duy nhất 1 nút "Xem" tại cột thao tác của `/manager/invoices`.
- **Rationale**: Đơn giản hóa giao diện bảng danh sách, buộc Manager phải xem kỹ chi tiết hóa đơn trước khi thực hiện các tác nghiệp chuyên sâu như Báo cáo sai số.

## Decision 2: Loại bỏ luồng Xóa Hóa đơn chưa thanh toán
- **Decision**: Gỡ bỏ hoàn toàn nút và form `POST /manager/invoices/{id}/delete` khỏi giao diện Hóa đơn.
- **Rationale**: Đảm bảo tính toàn vẹn số liệu tài chính và lịch sử chỉ số điện nước, tránh việc Manager lỡ tay xóa hóa đơn gây mất dữ liệu theo dõi.
