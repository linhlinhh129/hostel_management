# Người viết: Bùi Đỉnh | Ngày: 2026-06-13

## 1. PROBLEM STATEMENT

Trong quá trình vận hành chuỗi nhà trọ hoặc chung cư, Ban quản lý liên tục đối mặt với các "nỗi đau" lớn về quản lý tài chính và hóa đơn nếu chỉ thực hiện thủ công:

- **Sai sót dữ liệu trước phát hành:** Việc nhập sai chỉ số điện, nước, tính nhầm tiền phòng cố định hoặc phí dịch vụ phát sinh rất dễ xảy ra\[cite: 7\]. Nếu không có cơ chế điều chỉnh linh hoạt và cảnh báo logic trước khi gửi cho cư dân, hóa đơn sai sẽ gây mất uy tín và minh bạch\[cite: 7\].
- **Mất kiểm soát trạng thái công nợ và thu tiền:** Ban quản lý không có một giao diện tổng hợp để theo dõi xem khoản nào chưa thanh toán (`UNPAID`), khoản nào đã thanh toán (`PAID`), và đặc biệt là khoản nào đã chuyển sang nợ xấu quá hạn (`OVERDUE`) để kịp thời thu hồi dòng tiền\[cite: 7\].
- **Khó khăn trong tra cứu và cung cấp chứng từ:** Khi cư dân thắc mắc về thông tin tính phí hoặc yêu cầu cung cấp hóa đơn, Ban quản lý mất nhiều thời gian tìm kiếm, không thể xuất file lưu trữ nhanh chóng để gửi cho các bên liên quan\[cite: 7\].

## 2. DOMAIN KNOWLEDGE

- **Hóa đơn (Invoice):** Tài liệu tài chính dùng để ghi nhận tổng hợp tất cả các khoản phí phát sinh của một phòng thuê trong một kỳ cụ thể (gồm tiền phòng, điện, nước, phí dịch vụ, thuế...) làm căn cứ thu tiền\[cite: 7\].
- **Kỳ hóa đơn (Billing Period):** Được xác định định kỳ theo định dạng Tháng/Năm (Ví dụ: `06/2026`)\[cite: 7\].
- **Nguyên tắc khóa hóa đơn:** Một quy tắc nghiệp vụ bất thành văn quan trọng: Tuyệt đối không được chỉnh sửa bất kỳ thông tin nào đối với các hóa đơn đã ghi nhận trạng thái thanh toán thành công (`PAID`)\[cite: 7\].
- **Công thức tính toán tự động hệ thống:**
  - `Tạm tính (Subtotal)` = Tiền phòng + Tiền điện + Tiền nước + Phí dịch vụ\[cite: 7\].
  - `Tiền thuế` = Tạm tính × Thuế (%)\[cite: 7\].
  - `Tổng tiền phải nộp` = Tạm tính + Tiền thuế\[cite: 7\].

## 3. STAKEHOLDERS

- **Ban quản lý (Management Board):** Người vận hành trực tiếp hệ thống, được lợi từ việc kiểm soát dòng tiền, tra cứu thông tin nhanh và điều chỉnh sai sót hóa đơn\[cite: 7\].
- **Người thuê phòng (Tenants):** Người chịu ảnh hưởng trực tiếp (nhận hóa đơn, kiểm tra tính đúng đắn của chỉ số tiêu thụ và thực hiện thanh toán)\[cite: 7\].
- **Chủ nhà trọ / Nhà đầu tư:** Người có quyền quyết định cao nhất, gián tiếp giám sát hiệu quả thu tiền thông qua hệ thống dữ liệu hóa đơn chuẩn hóa\[cite: 7\].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- **Ràng buộc phân quyền (Role-based):** Chỉ người dùng có vai trò `Management Board` mới có quyền truy cập và thao tác trên tính năng này\[cite: 7\]. Hệ thống trả về `401` nếu chưa đăng nhập và `403` nếu sai role\[cite: 7\].
- **Ràng buộc nghiệp vụ nhập liệu (Business Logic Constraints):**
  - Chỉ số điện mới phải $\\ge$ chỉ số điện cũ\[cite: 7\].
  - Chỉ số nước mới phải $\\ge$ chỉ số nước cũ\[cite: 7\].
  - Thuế phải $\\ge 0%$\[cite: 7\]. Người dùng không được phép nhập trực tiếp số tiền thuế mà hệ thống phải tự tính\[cite: 7\].
  - Hạn thanh toán của hóa đơn không được nhỏ hơn ngày hiện tại khi điều chỉnh (`INVALID_DUE_DATE`)\[cite: 7\].
- **Định dạng file xuất:** Bắt buộc phải tạo và xuất ra file ở định dạng `PDF`\[cite: 7\].
- **Hiệu năng hệ thống (Performance Constraints - p95):**
  - Xem danh sách và điều chỉnh hóa đơn: &lt; 1000ms\[cite: 7\].
  - Xem chi tiết hóa đơn: &lt; 500ms\[cite: 7\].
  - Xuất file PDF hóa đơn: &lt; 2000ms\[cite: 7\].

## 5. ASSUMPTIONS (giả định cần confirm)

- **Giả định 1:** Hệ thống đã có một module/chức năng chạy ngầm (Cronjob) tự động quét hàng ngày, nếu hóa đơn có trạng thái `UNPAID` và `ngày hiện tại > hạn thanh toán` thì tự động chuyển sang trạng thái `OVERDUE`\[cite: 7\].
- **Giả định 2:** Đơn giá của 1 số điện và 1 khối nước được cấu hình ở một module quản lý giá dịch vụ khác, module Hóa đơn này chỉ lấy đơn giá đó nhân với số lượng tiêu thụ (mới trừ cũ)\[cite: 7\].
- **Giả định 3:** Việc cập nhật trạng thái hóa đơn từ `UNPAID` sang `PAID` sẽ do module Quản lý thanh toán kích hoạt sau khi đối soát thành công, không thực hiện đổi trạng thái thủ công tại module này\[cite: 7\].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- **Câu hỏi 1:** Khi điều chỉnh chỉ số điện/nước mới hoặc tiền phòng, hệ thống có cần lưu lại lịch sử thay đổi (Giá trị cũ, Giá trị mới) để phục vụ việc giải trình với cư dân sau này hay chỉ ghi nhận log chỉnh sửa chung (`updatedBy`, `updatedAt`)\[cite: 7\]?
- **Câu hỏi 2:** Hóa đơn sau khi được xuất file PDF thành công, URL tải về (`downloadUrl`) sẽ được lưu trữ công khai trên cloud storage (như S3) hay cần một cơ chế bảo mật (Presigned URL) để tránh việc người ngoài mò ra link tải hóa đơn của phòng khác\[cite: 7\]?
- **Câu hỏi 3:** Có quy định giới hạn số lần Ban quản lý được phép điều chỉnh một hóa đơn trước khi phát hành hay không\[cite: 7\]?