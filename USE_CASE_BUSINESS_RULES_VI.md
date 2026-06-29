# HỆ THỐNG QUẢN LÝ NHÀ TRỌ
## ĐẶC TẢ USE CASE & QUY TẮC NGHIỆP VỤ

**Phiên bản Tài liệu:** 1.0  
**Ngày:** 25 tháng 6, 2026  
**Dự án:** Hệ thống Quản lý Nhà trọ  
**Loại Tài liệu:** Đặc tả Use Case & Quy tắc Nghiệp vụ  
**Trạng thái:** Đã duyệt

---

## MỤC LỤC

1. Giới thiệu
2. Các Actor
3. Sơ đồ Use Case
4. Đặc tả Use Case
   - 4.1 Use Case của Admin
   - 4.2 Use Case của Manager
   - 4.3 Use Case của Operator
   - 4.4 Use Case của Tenant
5. Quy tắc Nghiệp vụ
6. Đặc tả Bổ sung

---

## 1. GIỚI THIỆU

### 1.1 Mục đích
Tài liệu này mô tả các use case và quy tắc nghiệp vụ cho Hệ thống Quản lý Nhà trọ. Nó cung cấp đặc tả chi tiết về chức năng hệ thống từ góc độ của các vai trò người dùng khác nhau.

### 1.2 Phạm vi
Hệ thống Quản lý Nhà trọ là ứng dụng web được thiết kế để quản lý hoạt động nhà trọ bao gồm:
- Quản lý cơ sở và phòng trọ
- Quản lý khách thuê
- Ghi chỉ số và tạo hóa đơn
- Xử lý thanh toán trực tuyến (VNPay)
- Quản lý yêu cầu/ticket
- Hệ thống thông báo
- Kiểm toán và báo cáo

### 1.3 Định nghĩa
- **Cơ sở (Facility):** Tòa nhà/khu nhà trọ được quản lý trong hệ thống
- **Phòng (Room):** Đơn vị cho thuê riêng lẻ trong một cơ sở
- **Hóa đơn (Invoice):** Hóa đơn hàng tháng cho tiền phòng, tiện ích và dịch vụ
- **Thanh toán (Payment):** Bản ghi giao dịch thanh toán hóa đơn
- **Ghi chỉ số (Meter Reading):** Ghi nhận tiêu thụ điện nước hàng tháng
- **Yêu cầu (Request):** Yêu cầu dịch vụ hoặc phiếu bảo trì từ khách thuê

---

## 2. CÁC ACTOR

### 2.1 Actor Chính

| Actor | Mô tả | Mục tiêu |
|-------|-------|----------|
| **ADMIN** | Quản trị viên hệ thống có quyền cao nhất | Quản lý cơ sở, nhân sự, xem báo cáo toàn hệ thống |
| **MANAGER** | Quản lý cơ sở (Ban Quản Lý) | Quản lý phòng, khách thuê, hóa đơn, thanh toán trong cơ sở được gán |
| **OPERATOR** | Nhân viên vận hành | Ghi chỉ số điện nước, xử lý yêu cầu bảo trì |
| **TENANT** | Người thuê phòng | Xem hóa đơn, thanh toán, gửi yêu cầu |

### 2.2 Actor Phụ

| Actor | Mô tả | Vai trò |
|-------|-------|---------|
| **VNPay Gateway** | Cổng thanh toán bên thứ ba | Xử lý thanh toán trực tuyến |
| **Hệ thống Email** | Dịch vụ email SMTP | Gửi thông báo và cảnh báo |
| **Hệ thống Database** | SQL Server | Lưu trữ và truy xuất dữ liệu |


---

## 3. TỔNG QUAN USE CASE

### 3.1 Use Case của Admin
1. UC-A01: Quản lý Cơ sở
2. UC-A02: Quản lý Nhân sự (Manager/Operator)
3. UC-A03: Xem Báo cáo Toàn hệ thống
4. UC-A04: Xem Nhật ký Kiểm toán
5. UC-A05: Gửi Thông báo Toàn hệ thống
6. UC-A06: Cấu hình Hệ thống

### 3.2 Use Case của Manager
1. UC-M01: Quản lý Phòng
2. UC-M02: Quản lý Khách thuê
3. UC-M03: Quản lý Hợp đồng
4. UC-M04: Tạo Hóa đơn Hàng tháng
5. UC-M05: Duyệt/Từ chối Thanh toán
6. UC-M06: Quản lý Yêu cầu
7. UC-M07: Gửi Thông báo Cơ sở
8. UC-M08: Xem Báo cáo Cơ sở

### 3.3 Use Case của Operator
1. UC-O01: Ghi Chỉ số Điện Nước
2. UC-O02: Xử lý Yêu cầu Bảo trì
3. UC-O03: Cập nhật Trạng thái Yêu cầu

### 3.4 Use Case của Tenant
1. UC-T01: Xem Hóa đơn
2. UC-T02: Thanh toán Hóa đơn Online (VNPay)
3. UC-T03: Thanh toán Hóa đơn bằng Chuyển khoản
4. UC-T04: Gửi Yêu cầu
5. UC-T05: Xem Trạng thái Yêu cầu
6. UC-T06: Quản lý Người phụ thuộc
7. UC-T07: Xem Thông báo
8. UC-T08: Đổi Mật khẩu

---

## 4. ĐẶC TẢ USE CASE

### 4.1 USE CASE CỦA ADMIN

#### UC-A01: Quản lý Cơ sở

**Mã Use Case:** UC-A01  
**Tên Use Case:** Quản lý Cơ sở  
**Actor:** Admin  
**Độ ưu tiên:** Cao  
**Tần suất sử dụng:** Thấp (giai đoạn thiết lập)

**Mô tả:**  
Admin tạo, cập nhật, xem và xóa các cơ sở nhà trọ trong hệ thống.

**Điều kiện tiên quyết:**
- Admin đã đăng nhập
- Admin có vai trò ADMIN

**Luồng chính:**
1. Admin điều hướng đến "Quản lý Cơ sở"
2. Hệ thống hiển thị danh sách cơ sở hiện có
3. Admin nhấn "Thêm Cơ sở Mới"
4. Hệ thống hiển thị form tạo cơ sở
5. Admin nhập thông tin cơ sở:
   - Tên cơ sở
   - Địa chỉ
   - Giá điện mỗi kWh
   - Giá nước mỗi m³
   - Phí internet
   - Phí dịch vụ
6. Admin nhấn "Lưu"
7. Hệ thống kiểm tra dữ liệu đầu vào
8. Hệ thống tạo cơ sở trong database
9. Hệ thống ghi lại hành động trong audit_logs
10. Hệ thống hiển thị thông báo thành công
11. Use case kết thúc

**Luồng thay thế:**

**A1: Chỉnh sửa Cơ sở Hiện có**
- Tại bước 3, Admin nhấn "Sửa" trên cơ sở hiện có
- Hệ thống hiển thị form chỉnh sửa với dữ liệu hiện tại
- Admin sửa đổi thông tin
- Tiếp tục từ bước 6

**A2: Xóa Cơ sở**
- Tại bước 3, Admin nhấn "Xóa" trên cơ sở
- Hệ thống kiểm tra xem cơ sở có phòng không
- Nếu không có phòng, hệ thống thực hiện xóa mềm (set deleted_at)
- Hệ thống hiển thị thông báo thành công

**Luồng ngoại lệ:**

**E1: Lỗi Kiểm tra**
- Tại bước 7, nếu kiểm tra thất bại
- Hệ thống hiển thị thông báo lỗi
- Quay lại bước 5

**E2: Cơ sở Có Phòng Đang Hoạt động**
- Trong quá trình xóa, nếu cơ sở có phòng
- Hệ thống hiển thị lỗi: "Không thể xóa cơ sở có phòng hiện tại"
- Quay lại bước 2

**Điều kiện hậu:**
- Cơ sở được tạo/cập nhật/xóa
- Hành động được ghi trong audit_logs
- Manager có thể được gán cho cơ sở

**Quy tắc nghiệp vụ:**
- BR-01: Tên cơ sở phải duy nhất
- BR-02: Không thể xóa cơ sở có phòng
- BR-03: Các trường giá phải là số dương


---

### 4.2 USE CASE CỦA MANAGER

#### UC-M04: Tạo Hóa đơn Hàng tháng

**Mã Use Case:** UC-M04  
**Tên Use Case:** Tạo Hóa đơn Hàng tháng  
**Actor:** Manager  
**Độ ưu tiên:** Rất cao  
**Tần suất sử dụng:** Cao (hàng tháng)

**Mô tả:**  
Manager tạo hóa đơn hàng tháng cho khách thuê dựa trên chỉ số điện nước.

**Điều kiện tiên quyết:**
- Manager đã đăng nhập
- Chỉ số điện nước đã được ghi cho kỳ thanh toán
- Phòng có khách thuê đang hoạt động

**Luồng chính:**
1. Manager điều hướng đến "Quản lý Hóa đơn"
2. Manager nhấn "Tạo Hóa đơn"
3. Hệ thống hiển thị form tạo hóa đơn
4. Manager chọn:
   - Phòng (dropdown)
   - Kỳ thanh toán (định dạng YYYYMM)
5. Hệ thống kiểm tra tồn tại chỉ số điện nước cho kỳ
6. Hệ thống lấy dữ liệu:
   - Tiền phòng từ bảng rooms
   - Chỉ số điện nước tháng trước
   - Chỉ số điện nước hiện tại
   - Giá tiện ích từ facility
7. Hệ thống tự động tính:
   - Tiêu thụ điện = chỉ số mới - chỉ số cũ
   - Tiêu thụ nước = chỉ số mới - chỉ số cũ
   - Tiền điện = tiêu thụ × giá điện
   - Tiền nước = tiêu thụ × giá nước
   - Tổng phụ = tiền phòng + điện + nước + internet + dịch vụ
8. Manager nhập:
   - Thuế suất (%)
   - Phí khác (nếu có)
   - Hạn thanh toán
   - Ghi chú
9. Hệ thống tính:
   - Tiền thuế = tổng phụ × thuế suất / 100
   - Tổng tiền = tổng phụ + thuế
10. Manager xem lại tính toán
11. Manager nhấn "Tạo Hóa đơn"
12. Hệ thống kiểm tra:
    - Mã hóa đơn duy nhất (INV-{mã phòng}-{kỳ})
    - Hạn thanh toán không quá khứ
    - Chỉ số mới >= chỉ số cũ
13. Hệ thống tạo hóa đơn với trạng thái = UNPAID
14. Hệ thống ghi lại hành động
15. Hệ thống gửi thông báo cho khách thuê
16. Hệ thống hiển thị thông báo thành công
17. Use case kết thúc

**Luồng ngoại lệ:**

**E1: Không có Chỉ số**
- Tại bước 5, nếu không tìm thấy chỉ số
- Hệ thống hiển thị lỗi: "Không có chỉ số điện nước cho kỳ {kỳ}"
- Quay lại bước 4

**E2: Chỉ số Không hợp lệ**
- Tại bước 12, nếu chỉ số mới < cũ
- Hệ thống hiển thị lỗi
- Quay lại bước 4

**Điều kiện hậu:**
- Hóa đơn được tạo với trạng thái UNPAID
- Khách thuê có thể xem hóa đơn
- Hóa đơn xuất hiện trong bảng thanh toán

**Quy tắc nghiệp vụ:**
- BR-07: Một hóa đơn cho mỗi phòng mỗi kỳ
- BR-08: Format mã hóa đơn: INV-{mã phòng}-{YYYYMM}
- BR-09: Chỉ số mới phải >= chỉ số cũ
- BR-10: Hạn thanh toán phải là ngày tương lai
- BR-11: Thuế suất 0-100%

---

### 4.4 USE CASE CỦA TENANT

#### UC-T02: Thanh toán Hóa đơn Online qua VNPay

**Mã Use Case:** UC-T02  
**Tên Use Case:** Thanh toán Hóa đơn Online qua VNPay  
**Actor:** Tenant, VNPay Gateway  
**Độ ưu tiên:** Rất cao  
**Tần suất sử dụng:** Cao (hàng tháng)

**Mô tả:**  
Khách thuê thanh toán hóa đơn trực tuyến sử dụng cổng thanh toán VNPay.

**Điều kiện tiên quyết:**
- Khách thuê đã đăng nhập
- Hóa đơn tồn tại với trạng thái UNPAID hoặc OVERDUE
- Khách thuê là chủ sở hữu hóa đơn

**Luồng chính:**
1. Tenant điều hướng đến "Hóa đơn của tôi"
2. Hệ thống hiển thị danh sách hóa đơn
3. Tenant nhấn vào hóa đơn chưa thanh toán
4. Hệ thống hiển thị chi tiết hóa đơn
5. Hệ thống kiểm tra xem hóa đơn có quá hạn không
6. Nếu quá hạn, hệ thống tính phí phạt trễ:
   - Số ngày trễ = ngày hiện tại - hạn thanh toán
   - Tỷ lệ phạt = 0.0005 × số ngày trễ
   - Tiền phạt = tổng hóa đơn × tỷ lệ phạt
   - Hiển thị cảnh báo với chi tiết phạt
7. Hệ thống tính tổng cần thanh toán:
   - Tổng = số tiền hóa đơn + tiền phạt
8. Tenant nhấn "Thanh toán qua VNPay"
9. Hệ thống tạo yêu cầu thanh toán gửi VNPay:
   - Số tiền (đơn vị xu = VND × 100)
   - Mã giao dịch: INV{id}T{timestamp}
   - URL trả về
   - Thông tin đơn hàng
   - Mã bảo mật (HMAC-SHA512)
10. Hệ thống chuyển hướng tenant đến VNPay
11. Tenant chọn phương thức thanh toán trên VNPay
12. Tenant hoàn tất thanh toán
13. VNPay xử lý giao dịch
14. VNPay chuyển hướng về với kết quả
15. Hệ thống nhận callback
16. Hệ thống kiểm tra mã bảo mật
17. Hệ thống phân tích phản hồi:
    - Mã phản hồi = "00" (thành công)
    - Số giao dịch
    - Số tiền
18. Hệ thống thực thi transaction database:
    - INSERT payment (status=SUCCESS, số tiền bao gồm phạt)
    - UPDATE invoice (status=PAID)
    - COMMIT transaction
19. Hệ thống gửi email xác nhận
20. Hệ thống hiển thị thông báo thành công
21. Use case kết thúc

**Luồng ngoại lệ:**

**E1: Mã Bảo mật Không hợp lệ**
- Tại bước 16, nếu mã không hợp lệ
- Hệ thống từ chối giao dịch
- Hiển thị lỗi: "Chữ ký giao dịch không hợp lệ"

**E2: Transaction Database Thất bại**
- Tại bước 18, nếu transaction thất bại
- Hệ thống rollback
- Hiển thị lỗi
- Liên hệ hỗ trợ

**Điều kiện hậu:**
- Thanh toán được ghi nhận với phí phạt trễ (nếu có)
- Hóa đơn được đánh dấu PAID
- Tenant nhận xác nhận
- Manager thấy thanh toán cập nhật

**Quy tắc nghiệp vụ:**
- BR-19: Phí phạt trễ tính real-time
- BR-20: Công thức phạt: số tiền × 0.0005 × số ngày trễ
- BR-21: Tổng tính phí bao gồm phạt
- BR-22: Giao dịch VNPay phải được kiểm tra
- BR-23: Anti-IDOR: Tenant chỉ thanh toán hóa đơn của mình


---

## 5. QUY TẮC NGHIỆP VỤ

### 5.1 Quy tắc Xác thực & Phân quyền

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-01 | Người dùng phải đăng nhập trước khi truy cập hệ thống | Tất cả |
| BR-02 | Session hết hạn sau 30 phút không hoạt động | Tất cả |
| BR-03 | Mật khẩu phải đáp ứng yêu cầu phức tạp (tối thiểu 8 ký tự, 1 chữ hoa, 1 số, 1 ký tự đặc biệt) | Đăng nhập, Đổi mật khẩu |
| BR-04 | Người dùng lần đầu phải đổi mật khẩu tạm thời | UC-A02 |
| BR-05 | Giới hạn 5 lần đăng nhập thất bại | Đăng nhập |
| BR-06 | Tài khoản bị khóa 15 phút sau 5 lần thất bại | Đăng nhập |
| BR-07 | Mỗi người dùng có đúng một vai trò: ADMIN, MANAGER, OPERATOR, hoặc TENANT | Tất cả |

### 5.2 Quy tắc Cơ sở & Phòng

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-10 | Tên cơ sở phải duy nhất | UC-A01 |
| BR-11 | Không thể xóa cơ sở có phòng hiện tại | UC-A01 |
| BR-12 | Mã phòng phải duy nhất trong cơ sở | UC-M01 |
| BR-13 | Phòng chỉ có thể có một khách thuê đang hoạt động | UC-M02 |
| BR-14 | Trạng thái phòng: AVAILABLE, OCCUPIED, MAINTENANCE | UC-M01 |
| BR-15 | Giá thuê phòng phải là số dương | UC-M01 |

### 5.3 Quy tắc Ghi Chỉ số

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-20 | Chỉ số mới phải >= chỉ số trước | UC-O01 |
| BR-21 | Một chỉ số cho mỗi phòng mỗi tháng | UC-O01 |
| BR-22 | Yêu cầu ảnh cho cả công tơ điện và nước | UC-O01 |
| BR-23 | Ảnh công tơ phải là định dạng hợp lệ (JPG, PNG) | UC-O01 |

### 5.4 Quy tắc Hóa đơn

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-30 | Format mã hóa đơn: INV-{mã phòng}-{YYYYMM} | UC-M04 |
| BR-31 | Một hóa đơn cho mỗi phòng mỗi kỳ thanh toán | UC-M04 |
| BR-32 | Hóa đơn yêu cầu chỉ số điện nước cho kỳ đó | UC-M04 |
| BR-33 | Trạng thái hóa đơn: UNPAID, PAID, OVERDUE | UC-M04, UC-T02 |
| BR-34 | Hóa đơn tự động chuyển sang OVERDUE khi quá due_date | Hệ thống |
| BR-35 | Thuế suất phải từ 0-100% | UC-M04 |
| BR-36 | Hạn thanh toán phải là ngày tương lai khi tạo | UC-M04 |
| BR-37 | Hóa đơn PAID không thể sửa hoặc xóa | UC-M04 |
| BR-38 | Công thức hóa đơn:<br>Tổng phụ = tiền phòng + điện + nước + internet + dịch vụ + khác<br>Thuế = tổng phụ × thuế suất / 100<br>Tổng = tổng phụ + thuế | UC-M04 |

### 5.5 Quy tắc Thanh toán

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-40 | Phương thức thanh toán: VNPAY, BANK_TRANSFER | UC-T02, UC-T03 |
| BR-41 | Thanh toán VNPay tự động duyệt (status=SUCCESS) | UC-T02 |
| BR-42 | Thanh toán chuyển khoản cần manager duyệt | UC-T03, UC-M05 |
| BR-43 | Trạng thái thanh toán: PENDING, SUCCESS, REJECTED | Tất cả UC thanh toán |
| BR-44 | Chỉ thanh toán PENDING mới có thể duyệt/từ chối | UC-M05 |
| BR-45 | Thanh toán được duyệt cập nhật hóa đơn sang PAID | UC-M05 |
| BR-46 | Số tiền thanh toán có thể là một phần (nhiều thanh toán cho một hóa đơn) | UC-T02, UC-T03 |
| BR-47 | Hóa đơn đánh dấu PAID khi tổng thanh toán >= số tiền hóa đơn | Hệ thống |

### 5.6 Quy tắc Phạt Trễ Thanh toán

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-50 | Phí phạt trễ được tính nếu ngày_thanh_toán > hạn_thanh_toán | UC-T02 |
| BR-51 | Công thức phạt hiện tại:<br>phạt = tổng_tiền × 0.0005 × số_ngày_trễ | UC-T02 |
| BR-52 | Công thức phạt thay thế (có thể cấu hình):<br>phạt = tiền_phòng × 0.01 × số_ngày_trễ | UC-T02 |
| BR-53 | Số ngày trễ = ngày_thanh_toán - hạn_thanh_toán | UC-T02 |
| BR-54 | Phạt được tính real-time khi thanh toán | UC-T02 |
| BR-55 | Tenant thấy cảnh báo phạt trước khi thanh toán | UC-T02 |
| BR-56 | Số tiền thanh toán bao gồm phạt | UC-T02 |
| BR-57 | Không giới hạn tối đa cho phạt (tăng vô hạn) | UC-T02 |

### 5.7 Quy tắc Yêu cầu/Ticket

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-60 | Loại yêu cầu: MAINTENANCE, INQUIRY, OTHER | UC-T04 |
| BR-61 | Luồng trạng thái: PENDING → ASSIGNED → IN_PROGRESS → DONE | UC-M06, UC-O02 |
| BR-62 | Không thể bỏ qua trạng thái trong workflow | UC-O02 |
| BR-63 | Tenant chỉ có thể tạo yêu cầu cho phòng của mình | UC-T04 |
| BR-64 | Ảnh tùy chọn nhưng được khuyến khích | UC-T04 |
| BR-65 | Tối đa 3 ảnh cho mỗi yêu cầu | UC-T04 |

### 5.8 Quy tắc Thông báo

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-70 | Admin có thể gửi cho tất cả người dùng | UC-A05 |
| BR-71 | Manager chỉ có thể gửi cho tenant trong cơ sở | UC-M07 |
| BR-72 | Thông báo có thể nhắm: tất cả, cơ sở, phòng cụ thể | UC-A05, UC-M07 |
| BR-73 | Kênh thông báo: web + email | UC-A05, UC-M07 |
| BR-74 | Hệ thống tự động gửi thông báo khi: tạo hóa đơn, thanh toán thành công | Hệ thống |

### 5.9 Quy tắc Kiểm toán & Bảo mật

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-80 | Tất cả hành động quan trọng được ghi trong audit_logs | Tất cả |
| BR-81 | Audit log bao gồm: user, action, entity, giá trị cũ/mới, timestamp, IP | Tất cả |
| BR-82 | Mật khẩu được hash với BCrypt (cost 12) | Đăng nhập, Đổi mật khẩu |
| BR-83 | Anti-IDOR: Người dùng chỉ truy cập tài nguyên của mình | Tất cả |
| BR-84 | SQL injection được ngăn chặn qua PreparedStatement | Tất cả |
| BR-85 | Giao dịch VNPay được kiểm tra với HMAC-SHA512 | UC-T02 |
| BR-86 | Sử dụng xóa mềm (timestamp deleted_at) | Tất cả thao tác xóa |

### 5.10 Quy tắc Kiểm tra Dữ liệu

| Mã QT | Mô tả Quy tắc | Use Case liên quan |
|-------|---------------|-------------------|
| BR-90 | Email phải có định dạng hợp lệ và duy nhất | UC-A02, UC-M02 |
| BR-91 | Định dạng điện thoại: 10-11 chữ số | UC-A02, UC-M02 |
| BR-92 | Số CMND: 9 hoặc 12 chữ số | UC-M02 |
| BR-93 | Tất cả giá trị tiền tệ phải không âm | UC-M01, UC-M04 |
| BR-94 | Ngày phải là ngày hợp lệ trong lịch | UC-M02, UC-M04 |
| BR-95 | File ảnh: JPG, PNG, tối đa 5MB | UC-O01, UC-T04 |

---

## 6. ĐẶC TẢ BỔ SUNG

### 6.1 Yêu cầu Hiệu năng

| Yêu cầu | Đặc tả |
|---------|--------|
| Thời gian phản hồi | < 2 giây cho các thao tác thông thường |
| Xử lý thanh toán | < 5 giây cho chuyển hướng VNPay |
| Tải trang | < 3 giây cho dashboard |
| Người dùng đồng thời | Hỗ trợ 100+ người dùng cùng lúc |
| Truy vấn Database | < 1 giây cho truy vấn phức tạp |

### 6.2 Yêu cầu Bảo mật

| Yêu cầu | Đặc tả |
|---------|--------|
| Xác thực | Session-based với timeout 30 phút |
| Chính sách mật khẩu | Tối thiểu 8 ký tự, 1 chữ hoa, 1 số, 1 ký tự đặc biệt |
| Mã hóa dữ liệu | HTTPS cho tất cả truyền thông |
| SQL Injection | Ngăn chặn qua PreparedStatement |
| Bảo vệ XSS | Sanitize input và encode output |

---

**KẾT THÚC TÀI LIỆU**

*Tài liệu này là bí mật và độc quyền. Cấm phân phối trái phép.*
