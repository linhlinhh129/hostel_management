# [CONTEXT.md](http://CONTEXT.md) - Invoice Management

**Người viết:** Business Analyst\
**Ngày:** 2026-06-10

---

## 1. PROBLEM STATEMENT

Người thuê hiện không có một nơi tập trung để theo dõi toàn bộ các khoản phí phát sinh trong quá trình thuê phòng.

Các vấn đề chính bao gồm:

- Không biết tổng số tiền cần thanh toán của từng kỳ.

- Khó theo dõi chi tiết cách tính hóa đơn điện, nước và các khoản phí dịch vụ.

- Không biết hóa đơn nào đã thanh toán, chưa thanh toán hoặc đã quá hạn.

- Không có khả năng xem lại lịch sử các giao dịch thanh toán trước đó.

- Dễ xảy ra tranh chấp về chi phí nếu không có thông tin minh bạch và dễ tra cứu.

Hệ thống cần cung cấp chức năng quản lý hóa đơn giúp người thuê dễ dàng theo dõi, kiểm tra và đối chiếu các khoản phí cũng như lịch sử thanh toán của mình.

---

## 2. DOMAIN KNOWLEDGE

### Invoice (Hóa đơn)

Là bản ghi tổng hợp các khoản phí phát sinh của một kỳ thuê phòng.

Một hóa đơn có thể bao gồm:

- Tiền phòng

- Tiền điện

- Tiền nước

- Phí dịch vụ

- Các khoản phụ phí khác

### Billing Period (Kỳ hóa đơn)

Khoảng thời gian tính phí, thường theo tháng.

Ví dụ:

- 05/2026

- 06/2026

### Payment Status (Trạng thái thanh toán)

Các trạng thái hợp lệ:

- UNPAID (Chưa thanh toán)

- PAID (Đã thanh toán)

- OVERDUE (Quá hạn)

### Electricity Consumption

Tiền điện được tính dựa trên:

- Chỉ số điện cũ

- Chỉ số điện mới

- Đơn giá điện

### Water Consumption

Tiền nước được tính dựa trên:

- Chỉ số nước cũ

- Chỉ số nước mới

- Đơn giá nước

### Payment Transaction

Là giao dịch thanh toán của người thuê cho một hóa đơn.

Thông tin cần lưu:

- Mã giao dịch

- Hóa đơn liên quan

- Số tiền thanh toán

- Thời gian thanh toán

- Phương thức thanh toán

- Trạng thái giao dịch

### Ownership Rule

Người thuê chỉ được phép xem:

- Hóa đơn của chính mình

- Lịch sử thanh toán của chính mình

Không được phép truy cập dữ liệu của người thuê khác.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

#### Tenant (Người thuê)

- Xem danh sách hóa đơn

- Xem chi tiết hóa đơn

- Theo dõi trạng thái thanh toán

- Kiểm tra lịch sử thanh toán

### Secondary Stakeholders

#### Landlord (Chủ nhà)

- Tạo và quản lý hóa đơn

- Theo dõi tình trạng thanh toán của người thuê

#### Property Manager / Board (Ban quản lý)

- Kiểm tra công nợ

- Hỗ trợ xử lý tranh chấp hóa đơn

### Technical Stakeholders

#### Backend Developer

- Xây dựng API hóa đơn

- Kiểm soát phân quyền truy cập

#### Frontend Developer

- Hiển thị danh sách và chi tiết hóa đơn

- Hiển thị lịch sử thanh toán

#### QA Engineer

- Kiểm thử tính đúng đắn của dữ liệu hóa đơn

- Kiểm thử bảo mật truy cập dữ liệu

---

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)

### Business Constraints

- Người thuê chỉ được xem dữ liệu thuộc tài khoản của mình.

- Hệ thống phải đảm bảo tính minh bạch của các khoản phí.

- Trạng thái hóa đơn phải phản ánh chính xác tình trạng thanh toán thực tế.

### Security Constraints

- Bắt buộc xác thực người dùng trước khi truy cập.

- Bắt buộc phân quyền Tenant.

- Truy cập trái phép phải trả về HTTP 403 Forbidden.

### Data Constraints

- invoiceId phải tồn tại.

- invoiceId phải thuộc người thuê hiện tại.

- paymentId phải thuộc người thuê hiện tại.

- Các giao dịch thanh toán phải được lưu vết đầy đủ.

### Technical Constraints

- Sử dụng REST API.

- Dashboard và các module khác sử dụng cùng cơ chế Access Token.

- Không thay đổi cấu trúc cơ sở dữ liệu hiện tại (DB Changes = None).

---

## 5. ASSUMPTIONS (Giả định cần xác nhận)

### A01

Mỗi hóa đơn chỉ thuộc về một người thuê.

### A02

Mỗi kỳ hóa đơn chỉ có một hóa đơn cho một phòng thuê.

### A03

Tiền điện và tiền nước đã được tính toán trước khi hóa đơn được phát hành.

### A04

Một giao dịch thanh toán chỉ liên kết với một hóa đơn.

### A05

Người thuê không được chỉnh sửa hoặc hủy hóa đơn.

### A06

Lịch sử thanh toán chỉ hiển thị các giao dịch thành công.

### A07

Tất cả hóa đơn đều được tạo theo chu kỳ hàng tháng.

### A08

Các khoản phụ phí đã được tính vào serviceFee hoặc một nhóm phí khác trong hệ thống.

---

## 6. OPEN QUESTIONS (Câu hỏi cần làm rõ)

### OQ01

Một hóa đơn có cho phép thanh toán nhiều lần (partial payment) hay không?

### OQ02

Khi thanh toán một phần, trạng thái hóa đơn sẽ là gì?

### OQ03

Có cần lưu lịch sử thay đổi hóa đơn sau khi phát hành hay không?

### OQ04

Người thuê có được tải hóa đơn dưới dạng PDF không?

### OQ05

Hệ thống có hỗ trợ thanh toán trực tuyến trực tiếp từ màn hình hóa đơn không?

### OQ06

Có cần hiển thị chi tiết đơn giá điện và đơn giá nước hay không?

### OQ07

Khi hóa đơn quá hạn, hệ thống có áp dụng phí phạt hay không?

### OQ08

Có cần hỗ trợ tìm kiếm hoặc lọc hóa đơn theo kỳ hóa đơn, trạng thái thanh toán hoặc khoảng thời gian không?

### OQ09

Có cần hỗ trợ xuất lịch sử thanh toán ra Excel hoặc PDF không?

### OQ10

Nếu một giao dịch thanh toán thất bại hoặc đang xử lý, có cần hiển thị trong lịch sử thanh toán của người thuê không?[CONTEXT.md](http://CONTEXT.md) - Invoice Management

**Người viết:** Business Analyst\
**Ngày:** 2026-06-10

---

## 1. PROBLEM STATEMENT

Người thuê hiện không có một nơi tập trung để theo dõi toàn bộ các khoản phí phát sinh trong quá trình thuê phòng.

Các vấn đề chính bao gồm:

- Không biết tổng số tiền cần thanh toán của từng kỳ.

- Khó theo dõi chi tiết cách tính hóa đơn điện, nước và các khoản phí dịch vụ.

- Không biết hóa đơn nào đã thanh toán, chưa thanh toán hoặc đã quá hạn.

- Không có khả năng xem lại lịch sử các giao dịch thanh toán trước đó.

- Dễ xảy ra tranh chấp về chi phí nếu không có thông tin minh bạch và dễ tra cứu.

Hệ thống cần cung cấp chức năng quản lý hóa đơn giúp người thuê dễ dàng theo dõi, kiểm tra và đối chiếu các khoản phí cũng như lịch sử thanh toán của mình.

---

## 2. DOMAIN KNOWLEDGE

### Invoice (Hóa đơn)

Là bản ghi tổng hợp các khoản phí phát sinh của một kỳ thuê phòng.

Một hóa đơn có thể bao gồm:

- Tiền phòng

- Tiền điện

- Tiền nước

- Phí dịch vụ

- Các khoản phụ phí khác

### Billing Period (Kỳ hóa đơn)

Khoảng thời gian tính phí, thường theo tháng.

Ví dụ:

- 05/2026

- 06/2026

### Payment Status (Trạng thái thanh toán)

Các trạng thái hợp lệ:

- UNPAID (Chưa thanh toán)

- PAID (Đã thanh toán)

- OVERDUE (Quá hạn)

### Electricity Consumption

Tiền điện được tính dựa trên:

- Chỉ số điện cũ

- Chỉ số điện mới

- Đơn giá điện

### Water Consumption

Tiền nước được tính dựa trên:

- Chỉ số nước cũ

- Chỉ số nước mới

- Đơn giá nước

### Payment Transaction

Là giao dịch thanh toán của người thuê cho một hóa đơn.

Thông tin cần lưu:

- Mã giao dịch

- Hóa đơn liên quan

- Số tiền thanh toán

- Thời gian thanh toán

- Phương thức thanh toán

- Trạng thái giao dịch

### Ownership Rule

Người thuê chỉ được phép xem:

- Hóa đơn của chính mình

- Lịch sử thanh toán của chính mình

Không được phép truy cập dữ liệu của người thuê khác.

---

## 3. STAKEHOLDERS

### Primary Stakeholders

#### Tenant (Người thuê)

- Xem danh sách hóa đơn

- Xem chi tiết hóa đơn

- Theo dõi trạng thái thanh toán

- Kiểm tra lịch sử thanh toán

### Secondary Stakeholders

#### Landlord (Chủ nhà)

- Tạo và quản lý hóa đơn

- Theo dõi tình trạng thanh toán của người thuê

#### Property Manager / Board (Ban quản lý)

- Kiểm tra công nợ

- Hỗ trợ xử lý tranh chấp hóa đơn

### Technical Stakeholders

#### Backend Developer

- Xây dựng API hóa đơn

- Kiểm soát phân quyền truy cập

#### Frontend Developer

- Hiển thị danh sách và chi tiết hóa đơn

- Hiển thị lịch sử thanh toán

#### QA Engineer

- Kiểm thử tính đúng đắn của dữ liệu hóa đơn

- Kiểm thử bảo mật truy cập dữ liệu

---

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)

### Business Constraints

- Người thuê chỉ được xem dữ liệu thuộc tài khoản của mình.

- Hệ thống phải đảm bảo tính minh bạch của các khoản phí.

- Trạng thái hóa đơn phải phản ánh chính xác tình trạng thanh toán thực tế.

### Security Constraints

- Bắt buộc xác thực người dùng trước khi truy cập.

- Bắt buộc phân quyền Tenant.

- Truy cập trái phép phải trả về HTTP 403 Forbidden.

### Data Constraints

- invoiceId phải tồn tại.

- invoiceId phải thuộc người thuê hiện tại.

- paymentId phải thuộc người thuê hiện tại.

- Các giao dịch thanh toán phải được lưu vết đầy đủ.

### Technical Constraints

- Sử dụng REST API.

- Dashboard và các module khác sử dụng cùng cơ chế Access Token.

- Không thay đổi cấu trúc cơ sở dữ liệu hiện tại (DB Changes = None).

---

## 5. ASSUMPTIONS (Giả định cần xác nhận)

### A01

Mỗi hóa đơn chỉ thuộc về một người thuê.

### A02

Mỗi kỳ hóa đơn chỉ có một hóa đơn cho một phòng thuê.

### A03

Tiền điện và tiền nước đã được tính toán trước khi hóa đơn được phát hành.

### A04

Một giao dịch thanh toán chỉ liên kết với một hóa đơn.

### A05

Người thuê không được chỉnh sửa hoặc hủy hóa đơn.

### A06

Lịch sử thanh toán chỉ hiển thị các giao dịch thành công.

### A07

Tất cả hóa đơn đều được tạo theo chu kỳ hàng tháng.

### A08

Các khoản phụ phí đã được tính vào serviceFee hoặc một nhóm phí khác trong hệ thống.

---

## 6. OPEN QUESTIONS (Câu hỏi cần làm rõ)

### OQ01

Một hóa đơn có cho phép thanh toán nhiều lần (partial payment) hay không?

### OQ02

Khi thanh toán một phần, trạng thái hóa đơn sẽ là gì?

### OQ03

Có cần lưu lịch sử thay đổi hóa đơn sau khi phát hành hay không?

### OQ04

Người thuê có được tải hóa đơn dưới dạng PDF không?

### OQ05

Hệ thống có hỗ trợ thanh toán trực tuyến trực tiếp từ màn hình hóa đơn không?

### OQ06

Có cần hiển thị chi tiết đơn giá điện và đơn giá nước hay không?

### OQ07

Khi hóa đơn quá hạn, hệ thống có áp dụng phí phạt hay không?

### OQ08

Có cần hỗ trợ tìm kiếm hoặc lọc hóa đơn theo kỳ hóa đơn, trạng thái thanh toán hoặc khoảng thời gian không?

### OQ09

Có cần hỗ trợ xuất lịch sử thanh toán ra Excel hoặc PDF không?

### OQ10

Nếu một giao dịch thanh toán thất bại hoặc đang xử lý, có cần hiển thị trong lịch sử thanh toán của người thuê không?