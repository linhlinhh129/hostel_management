# CONTEXT.md - Payment Management (MoMo Integration)

**Người viết:** Tú Anh  
**Ngày:** 2026-06-11  

---

## 1. PROBLEM STATEMENT
* Hiện tại cư dân phải thực hiện thanh toán các khoản phí điện và nước thông qua quy trình thủ công, gây mất thời gian cho cả cư dân và ban quản lý.
* Cư dân không thể thanh toán trực tuyến ngay trên hệ thống và khó theo dõi trạng thái thanh toán của hóa đơn.
* Ban quản lý phải kiểm tra và đối soát các khoản thanh toán thủ công, dễ xảy ra sai sót và chậm cập nhật trạng thái hóa đơn.
* Hệ thống cần cung cấp khả năng thanh toán trực tuyến để cư dân có thể thanh toán hóa đơn điện và nước một cách nhanh chóng, đồng thời tự động cập nhật kết quả thanh toán vào hệ thống.

---

## 2. DOMAIN KNOWLEDGE

### Thuật ngữ (Terms)
* **Resident:** Người sở hữu hoặc thuê căn hộ, có quyền xem và thanh toán hóa đơn của căn hộ.
* **Invoice:** Hóa đơn điện hoặc nước được tạo dựa trên chỉ số tiêu thụ đã được ban quản lý cập nhật.
* **Meter Reading:** Chỉ số điện hoặc nước của căn hộ tại một thời điểm nhất định.
* **Payment Request:** Yêu cầu thanh toán được hệ thống tạo và gửi đến MoMo.
* **MoMo Payment Link:** Liên kết thanh toán do MoMo trả về sau khi tạo giao dịch.
* **IPN (Instant Payment Notification):** Thông báo từ MoMo gửi đến hệ thống để xác nhận kết quả thanh toán.

### Trạng thái thanh toán (Payment Status)
Các trạng thái dự kiến của hóa đơn:
* `PENDING`: Chưa thanh toán
* `PROCESSING`: Đang xử lý
* `PAID`: Đã thanh toán
* `FAILED`: Thanh toán thất bại

### Quy tắc nghiệp vụ (Business Rules)
* Mỗi hóa đơn chỉ được thanh toán một lần.
* Chỉ các hóa đơn ở trạng thái `PENDING` mới được phép thanh toán.
* Sau khi thanh toán thành công, hóa đơn không được chỉnh sửa.
* Kết quả thanh toán chính thức được xác nhận dựa trên IPN từ MoMo.
* Hệ thống phải lưu thời gian thanh toán thành công.
* Một hóa đơn đã ở trạng thái `PAID` không được tạo giao dịch thanh toán mới.

---

## 3. STAKEHOLDERS

* **Resident:**
  * Xem hóa đơn điện nước.
  * Thực hiện thanh toán trực tuyến.
  * Theo dõi trạng thái thanh toán.
* **Building Manager:**
  * Quản lý chỉ số điện nước.
  * Theo dõi tình trạng thanh toán của cư dân.
* **Accounting Staff:**
  * Đối soát các giao dịch thanh toán.
  * Kiểm tra lịch sử thanh toán.
* **System Administrator:**
  * Quản lý cấu hình tích hợp MoMo.
  * Theo dõi lỗi hệ thống và giao dịch.
* **MoMo:**
  * Cung cấp dịch vụ thanh toán.
  * Gửi kết quả thanh toán về hệ thống thông qua IPN.

---

## 4. CONSTRAINTS

### Technical Constraints
* Backend sử dụng **Java Servlet/JSP**.
* Database sử dụng **PostgreSQL**.
* Thanh toán tích hợp thông qua **API của MoMo**.
* Hệ thống phải lưu trữ lịch sử giao dịch thanh toán.

### Business Constraints
* Chỉ hỗ trợ thanh toán qua MoMo.
* Không hỗ trợ thanh toán tiền mặt trong chức năng này.
* Chỉ thanh toán toàn bộ số tiền hóa đơn.
* Không hỗ trợ thanh toán một phần hóa đơn.

### Security Constraints
* Phải xác thực chữ ký (*signature*) từ MoMo IPN.
* Không cập nhật trạng thái `PAID` nếu IPN không hợp lệ.
* Không tin tưởng kết quả redirect từ phía client.

---

## 5. ASSUMPTIONS
* Mỗi căn hộ có ít nhất một tài khoản cư dân.
* Chỉ số điện nước đã được cập nhật trước khi tạo hóa đơn.
* Cư dân có tài khoản MoMo hợp lệ.
* MoMo luôn gửi IPN về hệ thống sau khi giao dịch hoàn tất.
* Mỗi hóa đơn tương ứng với một giao dịch thanh toán tại một thời điểm.

---

## 6. OPEN QUESTIONS
* Cư dân có được xem lịch sử thanh toán không?
* Hệ thống có gửi email hoặc thông báo sau khi thanh toán thành công không?
* Nếu MoMo gửi IPN nhiều lần cho cùng một giao dịch thì xử lý như thế nào?
* Khi thanh toán thất bại, cư dân có được tạo yêu cầu thanh toán mới không?
* Có thời hạn thanh toán cho từng hóa đơn hay không?
* Hệ thống có cần hỗ trợ hoàn tiền (*refund*) trong tương lai không?
* Có cần lưu đầy đủ lịch sử giao dịch với MoMo để phục vụ đối soát không?