# [CONTEXT.md](http://CONTEXT.md) - Invoice Management & VNPAY Payment

**Người viết:** Business Analyst\
**Ngày:** 2026-06-21

---

# 1. PROBLEM STATEMENT

Trong quá trình thuê trọ, người thuê cần theo dõi các khoản phí phát sinh hàng tháng như tiền phòng, tiền điện, tiền nước, phí Internet, phí dịch vụ và các khoản phụ thu khác. Nếu không có một hệ thống quản lý hóa đơn tập trung, người thuê khó biết được số tiền cần thanh toán, hạn thanh toán và trạng thái của từng hóa đơn.

Bên cạnh đó, việc thanh toán thủ công (tiền mặt hoặc chuyển khoản ngoài hệ thống) khiến cả người thuê và Ban quản lý gặp khó khăn trong việc xác nhận giao dịch, đối soát thanh toán và lưu trữ lịch sử. Sai sót trong quá trình xác nhận có thể dẫn đến tranh chấp về việc đã thanh toán hay chưa.

Do đó, hệ thống cần cung cấp chức năng xem hóa đơn, thanh toán trực tuyến thông qua VNPAY và tự động cập nhật trạng thái hóa đơn cũng như lưu vết giao dịch sau khi thanh toán thành công.

---

# 2. DOMAIN KNOWLEDGE

### Invoice

Là hóa đơn thanh toán của một phòng trong một kỳ tính tiền, bao gồm toàn bộ các khoản phí mà người thuê phải thanh toán.

Một hóa đơn có thể bao gồm:

- Tiền phòng

- Tiền điện

- Tiền nước

- Phí Internet

- Phí dịch vụ

- Thuế

- Các khoản phụ phí khác

---

### Billing Period

Khoảng thời gian áp dụng của hóa đơn (ví dụ: tháng 06/2026).

Một phòng chỉ có một hóa đơn cho mỗi kỳ thanh toán.

---

### Payment

Là giao dịch thanh toán cho một hóa đơn.

Mỗi giao dịch lưu các thông tin phục vụ đối soát như:

- Mã giao dịch

- Phương thức thanh toán

- Số tiền

- Thời gian thanh toán

- Trạng thái giao dịch

---

### VNPAY

Là cổng thanh toán trực tuyến của bên thứ ba.

Hệ thống chỉ tạo yêu cầu thanh toán và nhận kết quả giao dịch từ VNPAY thông qua Return URL và IPN (Instant Payment Notification).

---

### Payment History

Là danh sách các giao dịch thanh toán đã hoàn thành của người thuê.

---

### Invoice Status

- **UNPAID**: Chưa thanh toán.

- **PROCESSING**: Đang xử lý thanh toán qua VNPAY.

- **PAID**: Đã thanh toán thành công.

- **FAILED**: Thanh toán thất bại.

- **OVERDUE**: Quá hạn thanh toán (nếu hệ thống hỗ trợ).

---

### Business Rules

- Người thuê chỉ được xem hóa đơn thuộc phòng mình đang thuê.

- Một hóa đơn chỉ được thanh toán một lần.

- Hóa đơn đã thanh toán không được phép thanh toán lại.

- Chỉ khi VNPAY xác nhận giao dịch thành công thì hóa đơn mới được cập nhật sang trạng thái **PAID**.

- Thông tin giao dịch phải được lưu lại để phục vụ đối soát và tra cứu.

- Mỗi giao dịch thanh toán phải liên kết với đúng một hóa đơn.

- Lịch sử thanh toán chỉ hiển thị các giao dịch đã được ghi nhận thành công.

---

# 3. STAKEHOLDERS

### Primary Stakeholders

- **Tenant (Người thuê):**

  - Xem danh sách hóa đơn.

  - Xem chi tiết hóa đơn.

  - Thanh toán hóa đơn.

  - Xem lịch sử thanh toán.

---

### Secondary Stakeholders

- **Landlord (Chủ nhà):**

  - Theo dõi trạng thái thanh toán của hóa đơn.

- **Ban quản lý:**

  - Quản lý hóa đơn.

  - Đối soát các giao dịch thanh toán.

---

### External Stakeholders

- **VNPAY**

  - Xử lý giao dịch thanh toán.

  - Gửi kết quả giao dịch về hệ thống.

---

### Technical Stakeholders

- Product Owner

- Business Analyst

- Backend Developer

- Frontend Developer

- QA/Tester

- DevOps

---

# 4. CONSTRAINTS (Ràng buộc)

## Business Constraints

- Người dùng phải đăng nhập với vai trò Tenant.

- Người thuê chỉ được xem hóa đơn và lịch sử thanh toán của chính mình.

- Không được phép chỉnh sửa nội dung hóa đơn sau khi đã phát hành.

- Hóa đơn đã thanh toán không được thanh toán lại.

---

## Technical Constraints

- Tích hợp với cổng thanh toán VNPAY.

- Giao tiếp thông qua REST API.

- Kết quả thanh toán được xác nhận thông qua Return URL và IPN.

- Việc cập nhật bảng `invoices` và lưu dữ liệu vào bảng `payments` phải thực hiện trong cùng một Database Transaction.

- Áp dụng cơ chế Idempotency để tránh xử lý trùng lặp khi VNPAY gửi lại IPN.

---

## Security Constraints

- Bắt buộc xác thực người dùng trước khi truy cập.

- Kiểm tra quyền sở hữu hóa đơn trước khi trả dữ liệu.

- Xác thực chữ ký `vnp_SecureHash` trước khi xử lý kết quả thanh toán.

- Không lưu thông tin tài khoản hoặc thẻ ngân hàng của người dùng trong hệ thống.

- Khóa bí mật (`vnp_HashSecret`) phải được lưu trong biến môi trường, không được hardcode.

---

## Performance Constraints

- Danh sách hóa đơn phản hồi dưới **300ms (P95)**.

- Chi tiết hóa đơn phản hồi dưới **300ms (P95)**.

- Tạo URL thanh toán phản hồi dưới **500ms**.

- Hệ thống phải đảm bảo không phát sinh thanh toán trùng cho cùng một hóa đơn.

---

# 5. ASSUMPTIONS (Giả định cần xác nhận)

- Mỗi hóa đơn có một mã định danh (`invoiceId`) duy nhất.

- Mỗi hóa đơn chỉ được thanh toán bằng một giao dịch thành công.

- Mỗi giao dịch VNPAY có `vnp_TransactionNo` là duy nhất.

- API của VNPAY luôn trả đầy đủ các trường cần thiết để đối soát.

- Người dùng có kết nối Internet ổn định trong quá trình thanh toán.

- Trạng thái hóa đơn chỉ được thay đổi bởi hệ thống sau khi nhận được kết quả xác thực từ VNPAY.

- Thông tin hóa đơn không thay đổi trong thời gian người dùng thực hiện thanh toán.

- Thời gian trên máy chủ và VNPAY được đồng bộ để tránh lỗi xác thực giao dịch.

---

# 6. OPEN QUESTIONS (Câu hỏi cần làm rõ)

 1. Hệ thống có cho phép thanh toán một phần hóa đơn (Partial Payment) hay chỉ thanh toán toàn bộ?

 2. Khi hóa đơn đã quá hạn (OVERDUE), người thuê có còn được thanh toán trực tuyến qua VNPAY không?

 3. Nếu người dùng đóng trình duyệt sau khi thanh toán nhưng trước khi quay lại hệ thống, trạng thái hóa đơn sẽ được cập nhật dựa trên Return URL hay chỉ dựa vào IPN?

 4. Hệ thống có cần gửi Email hoặc thông báo sau khi thanh toán thành công không?

 5. Có cần sinh và tải biên lai (Receipt) sau khi thanh toán thành công không?

 6. Khi giao dịch thất bại, hệ thống có cần giới hạn số lần người dùng thử thanh toán lại không?

 7. Thông tin phản hồi đầy đủ từ VNPAY (`raw_vnpay_response`) sẽ được lưu trong bảng `payments` hay lưu tại bảng log riêng?

 8. Hệ thống có cần hỗ trợ hoàn tiền (Refund) hoặc hủy giao dịch trong các phiên bản tiếp theo không?

 9. Hóa đơn có thể được thanh toán bằng nhiều phương thức khác nhau (VNPAY, chuyển khoản, tiền mặt...) hay chỉ sử dụng VNPAY trong giai đoạn đầu?

10. Sau khi thanh toán thành công, hệ thống có cần tự động cập nhật các báo cáo doanh thu và công nợ của Ban quản lý hay được xử lý bởi một module khác?