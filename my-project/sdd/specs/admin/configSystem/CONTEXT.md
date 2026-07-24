# CONTEXT.md — Cấu hình hệ thống

## 1. Bối cảnh

Hệ thống quản lý nhà trọ tích hợp hai dịch vụ bên thứ ba quan trọng: **Email SMTP** (dùng để gửi thông báo, mật khẩu tạm thời, link khôi phục mật khẩu cho người dùng) và **VNPay** (dùng để tạo QR thanh toán và xử lý kết quả giao dịch của người thuê).

Hiện tại, toàn bộ thông tin kết nối của hai dịch vụ này được lưu cứng trong file `email.properties` và `vnpay.properties` — được load một lần duy nhất khi server khởi động thông qua `static` block trong `EmailService` và `VNPayConfig`. Khi thông tin tài khoản hoặc merchant thay đổi, đội vận hành buộc phải chỉnh sửa file cấu hình rồi restart server — gây gián đoạn dịch vụ.

Feature **Cấu hình hệ thống** cho phép Admin cập nhật thông tin kết nối Email và VNPay trực tiếp trên giao diện quản trị, không cần chạm vào source code hay restart server.

## 2. Nỗi đau của User

Khi thông tin SMTP hoặc merchant VNPay thay đổi, Admin hiện tại phải:

* SSH vào server, mở file `.properties`, sửa tay từng dòng — rủi ro nhập sai.
* Restart Tomcat để cấu hình mới có hiệu lực — toàn bộ session đang hoạt động bị mất.
* Không có giao diện để kiểm tra cấu hình hiện tại đang là gì — không biết mật khẩu đã đúng chưa.
* Không có ai kiểm tra xem ai đã thay đổi cấu hình, thay đổi lúc nào — không có audit trail.
* Secret Key và App Password bị lộ trong file plain text trên server — rủi ro bảo mật.

## 3. Mục tiêu

Feature Cấu hình hệ thống giúp Admin:

* Xem cấu hình Email SMTP và VNPay hiện tại trực tiếp trên giao diện (các trường nhạy cảm được mask).
* Cập nhật cấu hình Email SMTP mà không cần restart server.
* Cập nhật cấu hình VNPay mà không cần restart server.
* Cấu hình mới được áp dụng ngay cho tất cả email và giao dịch thanh toán tiếp theo.
* Chỉ Admin mới được phép truy cập và thay đổi cấu hình.

## 4. Ràng buộc

### Phân quyền
* Chỉ Admin được truy cập trang Cấu hình hệ thống.
* Người dùng chưa đăng nhập bị chuyển về trang login.
* Người dùng không phải Admin nhận lỗi FORBIDDEN (403).

### Cấu hình Email
* Các trường `host`, `port`, `username`, `password`, `from` đều bắt buộc.
* `port` phải là số nguyên dương hợp lệ.
* `password` (App Password) và `from` không được hiển thị đầy đủ trên giao diện — mask thành `"••••••••"`.
* Cấu hình mới phải được áp dụng ngay sau khi lưu thành công.

### Cấu hình VNPay
* Các trường `payUrl`, `returnUrl`, `tmnCode`, `secretKey`, `apiUrl` đều bắt buộc.
* `secretKey` không được hiển thị đầy đủ trên giao diện — mask thành `"••••••••"`.
* Cấu hình mới phải được áp dụng ngay cho các giao dịch thanh toán tiếp theo.

### Lưu trữ
* Cấu hình được lưu vào bảng `system_config` trong database (dạng key-value), thay thế cơ chế load từ `.properties` file tĩnh.
* `EmailService` và `VNPayConfig` phải đọc cấu hình động từ DB (hoặc cache có TTL) thay vì `static` block.

### Hiệu năng
* Thời gian phản hồi khi lưu cấu hình không vượt quá 500 ms (P95), không tính thời gian kết nối SMTP/VNPay.

## 5. Nguồn dữ liệu

| DAO | Dữ liệu lấy | Phạm vi |
|---|---|---|
| `SystemConfigDAO` | Đọc / ghi cấu hình hệ thống | Bảng `system_config` |

## 6. Câu hỏi mở

* Có cần thêm nút "Test kết nối" để kiểm tra SMTP hoặc VNPay sau khi lưu không?
* Có cần lưu lịch sử các phiên bản cấu hình trước (rollback) không?
* Có cần thêm cơ chế encrypt giá trị nhạy cảm trong DB không (AES-256)?
* Có cần cache cấu hình trong memory (TTL bao lâu) hay đọc DB mỗi lần gửi email / tạo giao dịch?
* Có cần thêm cấu hình cho các cổng thanh toán khác (MoMo, ZaloPay) trong tương lai không?
* Khi cập nhật VNPay trong lúc đang có giao dịch đang xử lý, cần xử lý race condition như thế nào?
* Có cần hiển thị tên người cập nhật gần nhất và thời điểm cập nhật ngay trên form không?
