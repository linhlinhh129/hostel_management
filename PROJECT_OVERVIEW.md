# 🏢 HỆ THỐNG QUẢN LÝ NHÀ TRỌ - PROJECT OVERVIEW

## 📌 GIỚI THIỆU TỔNG QUAN

**Tên dự án:** Hostel Management System (Hệ thống Quản lý Nhà trọ)

**Mô tả:** Hệ thống web-based giúp quản lý toàn diện hoạt động kinh doanh nhà trọ, từ quản lý cơ sở vật chất, khách thuê, hóa đơn điện nước, đến thanh toán trực tuyến và xử lý yêu cầu hỗ trợ.

**Mục tiêu:** 
- Số hóa và tự động hóa quy trình quản lý nhà trọ
- Giảm thiểu công việc thủ công cho ban quản lý
- Tăng trải nghiệm cho khách thuê qua thanh toán online và theo dõi hóa đơn
- Minh bạch trong quản lý tài chính và công nợ

---

## 🎯 NGHIỆP VỤ CHÍNH

### 1. Quản lý Cơ sở và Phòng trọ
- Quản lý nhiều cơ sở nhà trọ (facilities)
- Quản lý phòng: số phòng, diện tích, giá thuê, trạng thái (trống/đang thuê)
- Theo dõi tiện ích: điện, nước, internet, dịch vụ

### 2. Quản lý Khách thuê
- Lưu trữ thông tin khách thuê: CMND, ngày sinh, địa chỉ
- Quản lý người phụ thuộc (dependents)
- Quản lý hợp đồng thuê phòng

### 3. Quản lý Điện Nước
- Ghi chỉ số điện nước định kỳ (với ảnh công tơ)
- Tính toán tiêu thụ tự động
- Tạo hóa đơn dựa trên chỉ số

### 4. Quản lý Hóa đơn
- Tạo hóa đơn tháng: tiền phòng + điện + nước + dịch vụ
- Tính thuế và phí khác
- Theo dõi trạng thái: Chưa thanh toán / Đã thanh toán / Quá hạn
- **Tính phí phạt trễ hạn** tự động

### 5. Thanh toán
- **Thanh toán online qua VNPay** (ví điện tử, thẻ ATM, QR)
- Thanh toán chuyển khoản (cần duyệt)
- Lịch sử giao dịch chi tiết

### 6. Quản lý Yêu cầu/Sự cố
- Khách thuê gửi yêu cầu sửa chữa (kèm ảnh)
- Phân công và theo dõi xử lý
- Cập nhật tiến độ và hoàn thành

### 7. Thông báo
- Gửi thông báo đến khách thuê theo cơ sở/phòng
- Thông báo qua web và email

### 8. Báo cáo và Kiểm toán
- Báo cáo doanh thu theo cơ sở
- Báo cáo công nợ
- Audit logs (nhật ký hệ thống)

---

## 👥 CÁC VAI TRÒ TRONG HỆ THỐNG

### 1. 👨‍💼 ADMIN (Quản trị viên)

**Quyền hạn cao nhất trong hệ thống**

**Chức năng:**
- ✅ Quản lý tất cả cơ sở nhà trọ
- ✅ Tạo và quản lý tài khoản Manager, Operator
- ✅ Gán Manager/Operator cho từng cơ sở
- ✅ Xem báo cáo doanh thu toàn hệ thống
- ✅ Xem audit logs (nhật ký hoạt động)
- ✅ Quản lý thông báo toàn hệ thống
- ✅ Cấu hình hệ thống

**Tương tác với web:**
```
Dashboard → Hiển thị:
  - Tổng số cơ sở
  - Tổng doanh thu
  - Số lượng Manager/Operator/Tenant
  - Biểu đồ thống kê

Menu chính:
  📊 Dashboard
  🏢 Quản lý Cơ sở
  👤 Quản lý Nhân sự
  📋 Audit Logs
  📢 Thông báo
  💰 Báo cáo Doanh thu
```

**Luồng hoạt động tiêu biểu:**
```
1. Đăng nhập với role ADMIN
2. Tạo cơ sở mới (VD: Nhà trọ ABC)
3. Tạo tài khoản Manager cho nhà trọ ABC
4. Gửi email mật khẩu tạm thời cho Manager
5. Xem báo cáo doanh thu tất cả cơ sở
```


---

### 2. 🏠 MANAGER (Ban Quản Lý)

**Quản lý một cơ sở cụ thể**

**Chức năng:**
- ✅ Quản lý phòng trọ trong cơ sở
- ✅ Quản lý khách thuê và hợp đồng
- ✅ Tạo hóa đơn điện nước hàng tháng
- ✅ Duyệt/từ chối thanh toán chuyển khoản
- ✅ Xem và xử lý yêu cầu từ khách thuê
- ✅ Gửi thông báo đến khách thuê
- ✅ Xem báo cáo công nợ
- ✅ Xem báo cáo doanh thu cơ sở

**Tương tác với web:**
```
Dashboard → Hiển thị:
  - Số phòng trống/đang thuê
  - Tổng doanh thu tháng
  - Số hóa đơn chưa thanh toán
  - Số yêu cầu chờ xử lý

Menu chính:
  📊 Dashboard
  🏠 Quản lý Phòng
  👥 Quản lý Khách thuê
  📄 Quản lý Hợp đồng
  ⚡ Quản lý Điện nước
  🧾 Quản lý Hóa đơn
  💳 Quản lý Thanh toán
  📝 Quản lý Yêu cầu
  📢 Thông báo
  📊 Báo cáo
```

**Luồng hoạt động tiêu biểu:**
```
THÁNG MỚI - TẠO HÓA ĐƠN:
1. Đợi Operator cập nhật chỉ số điện nước
2. Vào "Quản lý Hóa đơn" → Tạo hóa đơn mới
3. Chọn phòng và kỳ thanh toán (VD: 202406)
4. Hệ thống tự động tính:
   - Tiền điện = (Chỉ số mới - Chỉ số cũ) × Đơn giá
   - Tiền nước = (Chỉ số mới - Chỉ số cũ) × Đơn giá
   - Tổng = Tiền phòng + Điện + Nước + Internet + Dịch vụ + Thuế
5. Lưu hóa đơn → Khách thuê thấy ngay trên web

DUYỆT THANH TOÁN:
1. Khách thuê thanh toán qua VNPay (tự động duyệt)
2. Khách thuê chuyển khoản → Manager vào "Quản lý Thanh toán"
3. Xem ảnh chứng từ
4. Duyệt → Hóa đơn chuyển sang PAID
   Từ chối → Thông báo lại khách thuê

XỬ LÝ YÊU CẦU:
1. Khách thuê gửi yêu cầu sửa chữa
2. Manager xem yêu cầu → Phân công cho Operator
3. Theo dõi tiến độ xử lý
4. Đánh dấu hoàn thành
```

---

### 3. 🔧 OPERATOR (Nhân viên Vận hành)

**Nhân viên kỹ thuật/vận hành tại cơ sở**

**Chức năng:**
- ✅ Cập nhật chỉ số điện nước (+ chụp ảnh công tơ)
- ✅ Xem danh sách phòng cần cập nhật
- ✅ Nhận yêu cầu sửa chữa từ Manager
- ✅ Cập nhật tiến độ xử lý
- ✅ Đánh dấu công việc hoàn thành

**Tương tác với web:**
```
Dashboard → Hiển thị:
  - Số phòng chưa ghi điện nước
  - Số yêu cầu đang xử lý
  - Lịch sử cập nhật

Menu chính:
  📊 Dashboard
  ⚡ Ghi Điện Nước
  📝 Yêu cầu được giao
  📋 Lịch sử
```

**Luồng hoạt động tiêu biểu:**
```
GHI ĐIỆN NƯỚC HÀNG THÁNG:
1. Đăng nhập → Vào "Ghi Điện Nước"
2. Chọn tháng (VD: Tháng 6/2026)
3. Xem danh sách phòng chưa cập nhật
4. Click vào từng phòng:
   - Nhập chỉ số điện mới
   - Nhập chỉ số nước mới
   - Chụp ảnh công tơ điện
   - Chụp ảnh công tơ nước
   - Lưu
5. Hệ thống kiểm tra:
   - Chỉ số mới phải >= chỉ số cũ
   - Ảnh phải đúng format
6. Sau khi cập nhật xong → Manager tạo được hóa đơn

XỬ LÝ YÊU CẦU SỬA CHỮA:
1. Nhận thông báo yêu cầu mới
2. Xem chi tiết: "Bóng đèn phòng 101 hỏng"
3. Cập nhật trạng thái: "Đang xử lý"
4. Sửa xong → Cập nhật: "Hoàn thành"
5. Ghi chú nếu cần (VD: "Đã thay bóng mới")
```


---

### 4. 🏠 TENANT (Người thuê trọ)

**Khách thuê phòng trọ**

**Chức năng:**
- ✅ Xem thông tin cá nhân và hợp đồng
- ✅ Xem danh sách hóa đơn và chi tiết
- ✅ **Thanh toán hóa đơn online qua VNPay**
- ✅ Xem lịch sử thanh toán
- ✅ Gửi yêu cầu hỗ trợ/sửa chữa (kèm ảnh)
- ✅ Theo dõi trạng thái yêu cầu
- ✅ Xem thông báo từ ban quản lý
- ✅ Quản lý người phụ thuộc (dependents)
- ✅ Đổi mật khẩu

**Tương tác với web:**
```
Dashboard → Hiển thị:
  - Thông tin phòng đang thuê
  - Hóa đơn tháng này
  - Số dư công nợ (nếu có)
  - Thông báo mới

Menu chính:
  🏠 Dashboard
  🧾 Hóa đơn của tôi
  💳 Lịch sử Thanh toán
  📝 Yêu cầu Hỗ trợ
  👨‍👩‍👧‍👦 Người phụ thuộc
  📢 Thông báo
  ⚙️ Cài đặt
```

**Luồng hoạt động tiêu biểu:**

#### A. THANH TOÁN HÓA ĐƠN (LUỒNG CHÍNH)
```
BƯỚC 1: XEM HÓA ĐƠN
1. Đăng nhập → Dashboard
2. Thấy thông báo: "Bạn có 1 hóa đơn mới"
3. Click "Xem hóa đơn"

BƯỚC 2: XEM CHI TIẾT
Hóa đơn tháng 6/2026:
├─ Tiền phòng:      3,000,000 đ
├─ Tiền điện:         500,000 đ (250 kWh × 2,000đ)
├─ Tiền nước:         150,000 đ (15 m³ × 10,000đ)
├─ Internet:          100,000 đ
├─ Dịch vụ:           150,000 đ
├─ Phí khác:               0 đ
├─ Thuế (5%):         195,000 đ
└─ TỔNG CỘNG:      4,095,000 đ

Hạn thanh toán: 05/07/2026
Trạng thái: CHƯA THANH TOÁN

⚠️ Nếu thanh toán trễ: 
Phạt 0.05% tổng hóa đơn mỗi ngày
(Hoặc 1% tiền phòng/ngày - tùy cấu hình)

BƯỚC 3: CHỌN THANH TOÁN
2 lựa chọn:
  A. Thanh toán Online qua VNPay
  B. Chuyển khoản (cần upload ảnh)

BƯỚC 4A: THANH TOÁN VNPAY
1. Click "Thanh toán qua VNPay"
2. Chọn phương thức:
   - Ví VNPay
   - Thẻ ATM nội địa
   - Thẻ Visa/Mastercard
   - QR Code
3. Nhập thông tin → Xác nhận OTP
4. VNPay xử lý giao dịch
5. Redirect về hệ thống:
   ✅ Thành công → Hóa đơn chuyển PAID
   ❌ Thất bại → Thử lại

BƯỚC 4B: CHUYỂN KHOẢN
1. Click "Chuyển khoản"
2. Xem thông tin tài khoản BQL
3. Chuyển khoản qua app ngân hàng
4. Quay lại web → Upload ảnh chuyển khoản
5. Đợi Manager duyệt (1-24h)

BƯỚC 5: XÁC NHẬN
- Email thông báo thanh toán thành công
- Có thể tải hóa đơn PDF (nếu có)
```

#### B. GỬI YÊU CẦU HỖ TRỢ
```
1. Click "Yêu cầu Hỗ trợ" → "Tạo yêu cầu mới"
2. Chọn loại: Sửa chữa / Thắc mắc / Khác
3. Nhập tiêu đề: "Bóng đèn phòng 101 hỏng"
4. Mô tả chi tiết
5. Chụp ảnh minh chứng (tối đa 3 ảnh)
6. Gửi yêu cầu
7. Theo dõi trạng thái:
   - PENDING (Chờ xử lý)
   - ASSIGNED (Đã phân công)
   - IN_PROGRESS (Đang xử lý)
   - DONE (Hoàn thành)
```

#### C. TRƯỜNG HỢP THANH TOÁN TRỄ
```
Hóa đơn: 4,095,000 đ
Hạn: 05/07/2026
Thanh toán: 15/07/2026 (trễ 10 ngày)

Tính phạt:
- Công thức hiện tại: 4,095,000 × 0.0005 × 10 = 20,475 đ
- Hoặc (nếu dùng công thức mới): 3,000,000 × 0.01 × 10 = 300,000 đ

Tenant thấy:
⚠️ Cảnh báo phạt trễ:
   Số ngày quá hạn: 10 ngày
   Phí phạt: 20,475 đ
   Tổng phải trả: 4,115,475 đ

→ Khi thanh toán VNPay, số tiền bị tự động cộng phạt
```

---

## 🔄 LUỒNG HOẠT ĐỘNG TỔNG THỂ

### 📅 Chu kỳ hàng tháng

```
CUỐI THÁNG (ngày 25-30):
├─ OPERATOR ghi chỉ số điện nước tất cả phòng
└─ Upload ảnh công tơ

ĐẦU THÁNG MỚI (ngày 1-5):
├─ MANAGER tạo hóa đơn cho từng phòng
│  └─ Hệ thống tự động tính tiền dựa trên:
│      • Tiền phòng (cố định)
│      • Điện nước (theo chỉ số)
│      • Internet, dịch vụ (cố định)
│      • Thuế (%)
└─ TENANT nhận thông báo hóa đơn mới

TRONG THÁNG (ngày 5-30):
├─ TENANT thanh toán hóa đơn
│  ├─ VNPay: Tự động duyệt
│  └─ Chuyển khoản: MANAGER duyệt
├─ TENANT gửi yêu cầu sửa chữa (nếu có)
└─ OPERATOR xử lý yêu cầu

QUÁ HẠN (sau ngày đến hạn):
├─ Hóa đơn chuyển status: OVERDUE
├─ Tính phạt trễ hạn tự động
└─ Gửi nhắc nhở qua email
```


---

## 🏗️ KIẾN TRÚC HỆ THỐNG

### Công nghệ sử dụng

**Frontend:**
- JSP (JavaServer Pages)
- Bootstrap 5
- JavaScript (vanilla)

**Backend:**
- Java Servlet (Jakarta EE)
- JDBC (kết nối database)
- Maven (quản lý dependencies)

**Database:**
- Microsoft SQL Server 2022
- 11 bảng chính

**External Services:**
- VNPay Payment Gateway (sandbox)
- SMTP Email Service

**Architecture Pattern:**
- MVC (Model-View-Controller)
- DAO Pattern (Data Access Object)
- Service Layer Pattern

### Cấu trúc thư mục

```
hostel-management/
├── src/main/
│   ├── java/com/quanlyphongtro/
│   │   ├── controller/         # Servlet controllers
│   │   │   ├── admin/          # Admin controllers
│   │   │   ├── manager/        # Manager controllers
│   │   │   ├── operator/       # Operator controllers
│   │   │   ├── tenant/         # Tenant controllers
│   │   │   └── auth/           # Authentication
│   │   ├── service/            # Business logic
│   │   │   └── impl/           # Service implementations
│   │   ├── dao/                # Database access
│   │   ├── model/              # Entity models
│   │   ├── dto/                # Data transfer objects
│   │   ├── filter/             # Filters (Auth, CORS)
│   │   ├── util/               # Utilities (Email, VNPay)
│   │   └── constant/           # Constants
│   ├── webapp/
│   │   └── WEB-INF/views/      # JSP views
│   │       ├── admin/
│   │       ├── manager/
│   │       ├── operator/
│   │       ├── tenant/
│   │       └── layout/         # Shared layouts
│   └── resources/
│       ├── email.properties    # Email config
│       └── logback.xml         # Logging config
└── database/
    ├── schema.sql              # Database schema
    └── seed.sql                # Sample data
```

---

## 💾 DATABASE SCHEMA

### 11 Bảng chính:

```sql
1. users               # Tất cả user (Admin, Manager, Operator, Tenant)
2. facilities          # Cơ sở nhà trọ
3. rooms               # Phòng trọ
4. dependents          # Người phụ thuộc
5. contracts           # Hợp đồng thuê
6. meter_readings      # Chỉ số điện nước
7. invoices            # Hóa đơn
8. payments            # Thanh toán
9. requests            # Yêu cầu hỗ trợ
10. notifications      # Thông báo
11. audit_logs         # Nhật ký hệ thống
```

### Quan hệ chính:

```
users (Manager) ←1:N→ facilities ←1:N→ rooms
rooms ←1:N→ meter_readings
rooms ←1:N→ invoices ←1:N→ payments
rooms ←1:1→ users (Tenant)
users (Tenant) ←1:N→ dependents
rooms ←1:N→ requests
```

---

## 🔐 BẢO MẬT

### Authentication & Authorization

**1. Session-based Authentication:**
- Login → Create session với user info
- Session timeout: 30 phút
- Logout → Invalidate session

**2. Role-based Access Control (RBAC):**
```java
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminFilter {
    // Chỉ cho phép role ADMIN
}
```

**3. Anti-IDOR (Insecure Direct Object Reference):**
```java
// Kiểm tra ownership trước khi trả dữ liệu
if (!invoiceDAO.verifyInvoiceOwnership(invoiceId, tenantId)) {
    response.sendError(403, "Forbidden");
}
```

**4. Password Security:**
- BCrypt hashing (cost factor 12)
- Force change password lần đầu
- Password reset với token có thời hạn

**5. VNPay Security:**
- HMAC-SHA512 signature verification
- TmnCode và SecretKey riêng
- Validate return URL

**6. SQL Injection Prevention:**
- PreparedStatement cho tất cả queries
- Không concat string trong SQL

**7. Audit Logging:**
- Log tất cả thao tác quan trọng
- Lưu: user, action, entity, old/new value, timestamp, IP

---

## 📱 GIAO DIỆN NGƯỜI DÙNG

### Design System

**Color Palette:**
- Primary: #1976D2 (Blue)
- Success: #4CAF50 (Green)
- Warning: #FF9800 (Orange)
- Danger: #F44336 (Red)
- Info: #2196F3 (Light Blue)

**Layout:**
- Responsive design (mobile-friendly)
- Sidebar navigation (collapsible)
- Top bar với user info và notifications
- Breadcrumb navigation

**Components:**
- Tables với pagination
- Modal dialogs
- Alert messages (success/error/warning)
- Form validation
- Image upload với preview
- Filter bars
- Status badges


---

## 🎯 TÍNH NĂNG NỔI BẬT

### 1. 💳 Thanh toán Online qua VNPay
- **Lợi ích:** 
  - Tenant thanh toán mọi lúc, mọi nơi
  - Không cần Manager duyệt thủ công
  - Giảm thiểu sai sót và gian lận
- **Flow:** Tenant → VNPay → Auto update invoice → Done
- **Phương thức:** Ví VNPay, ATM, Visa/Master, QR Code

### 2. ⚡ Tự động tính Hóa đơn
- **Lợi ích:** 
  - Không cần tính toán thủ công
  - Giảm thiểu sai sót
- **Logic:** 
  ```
  Tổng = Tiền phòng 
       + (Điện mới - Điện cũ) × Đơn giá điện
       + (Nước mới - Nước cũ) × Đơn giá nước
       + Internet + Dịch vụ + Phí khác
       + Thuế (%)
  ```

### 3. 📸 Upload ảnh Công tơ
- **Lợi ích:** 
  - Minh bạch trong ghi chỉ số
  - Tránh tranh chấp về điện nước
- **Operator:** Chụp ảnh công tơ khi ghi chỉ số
- **Tenant:** Xem được ảnh công tơ trong hóa đơn

### 4. 💰 Phạt trễ hạn tự động
- **Lợi ích:** 
  - Khuyến khích thanh toán đúng hạn
  - Tự động tính toán, không cần can thiệp
- **Logic hiện tại:** 0.05% tổng hóa đơn/ngày
- **Có thể cấu hình:** 1% tiền phòng/ngày

### 5. 📝 Hệ thống Ticket/Request
- **Lợi ích:** 
  - Theo dõi yêu cầu một cách có hệ thống
  - Không bị quên hoặc bỏ sót
- **Workflow:** 
  ```
  Tenant tạo → Manager phân công 
  → Operator xử lý → Cập nhật tiến độ 
  → Hoàn thành
  ```

### 6. 🔔 Thông báo đa cấp
- **Admin:** Thông báo toàn hệ thống
- **Manager:** Thông báo theo cơ sở/phòng
- **Channels:** Web notification + Email

### 7. 📊 Báo cáo và Thống kê
- **Admin:** Doanh thu toàn hệ thống
- **Manager:** Doanh thu cơ sở, công nợ
- **Charts:** Line, Bar, Pie charts

### 8. 🔍 Audit Trail
- **Lợi ích:** Truy vết mọi thao tác trong hệ thống
- **Log:** WHO (user), WHAT (action), WHEN (timestamp), WHERE (IP)
- **Use case:** Điều tra sai sót, kiểm toán

---

## 📈 WORKFLOW EXAMPLES

### Workflow 1: Tạo tài khoản Tenant mới

```
MANAGER:
1. Vào "Quản lý Khách thuê" → "Thêm khách thuê mới"
2. Nhập thông tin:
   - Họ tên: Nguyễn Văn A
   - CMND: 001234567890
   - Email: tenant@example.com
   - Số điện thoại
   - Ngày sinh, giới tính
3. Chọn phòng: P101
4. Chọn ngày bắt đầu thuê: 01/07/2026
5. Tạo hợp đồng:
   - Thời hạn: 12 tháng
   - Tiền đặt cọc: 3,000,000 đ
   - Upload file hợp đồng PDF
6. Lưu → Hệ thống:
   - Tạo user với username = email
   - Tạo mật khẩu tạm thời
   - Gửi email cho tenant
   - Phòng chuyển status: OCCUPIED

TENANT (Email nhận được):
---
Chào Nguyễn Văn A,

Tài khoản của bạn đã được tạo:
Username: tenant@example.com
Mật khẩu tạm: Abc@12345

Vui lòng đăng nhập và đổi mật khẩu ngay.
Link: http://hostel-system.com/login
---

TENANT:
1. Đăng nhập với mật khẩu tạm
2. Bắt buộc đổi mật khẩu
3. Vào Dashboard → Xem thông tin phòng và hợp đồng
```

### Workflow 2: Gửi và xử lý yêu cầu sửa chữa

```
TENANT (Thứ 2, 9:00 AM):
1. "Yêu cầu Hỗ trợ" → "Tạo mới"
2. Loại: Sửa chữa
3. Tiêu đề: "Bồn cầu bị rò nước"
4. Mô tả: "Bồn cầu rò nước từ sáng nay, cần sửa gấp"
5. Upload 2 ảnh hiện trường
6. Gửi → Status: PENDING

MANAGER (nhận notification):
1. "Quản lý Yêu cầu" → Xem yêu cầu mới
2. Đọc chi tiết và xem ảnh
3. Phân công cho Operator Nguyễn Văn B
4. Thêm ghi chú: "Ưu tiên xử lý"
5. Lưu → Status: ASSIGNED

OPERATOR (nhận notification):
1. "Yêu cầu được giao" → Xem chi tiết
2. Click "Bắt đầu xử lý" → Status: IN_PROGRESS
3. (Thứ 2, 2:00 PM) Đến phòng sửa
4. Cập nhật tiến độ: "Đã thay phớt bồn cầu"
5. Click "Hoàn thành" → Status: DONE

TENANT (nhận notification):
"Yêu cầu của bạn đã được hoàn thành: Bồn cầu bị rò nước"
```


### Workflow 3: Quy trình thanh toán hóa đơn đầy đủ

```
[NGÀY 28 THÁNG 6]
OPERATOR:
- Ghi chỉ số điện nước tất cả phòng
- P101: Điện 1250 kWh, Nước 25 m³

[NGÀY 1 THÁNG 7]
MANAGER:
1. "Quản lý Hóa đơn" → "Tạo hóa đơn"
2. Chọn phòng: P101
3. Chọn kỳ: 202406 (tháng 6/2026)
4. Hệ thống tự động tính:
   - Điện cũ: 1000 kWh → Điện mới: 1250 kWh → Tiêu thụ: 250 kWh
   - Nước cũ: 10 m³ → Nước mới: 25 m³ → Tiêu thụ: 15 m³
   - Tiền điện: 250 × 2,000 = 500,000 đ
   - Tiền nước: 15 × 10,000 = 150,000 đ
   - Tiền phòng: 3,000,000 đ
   - Internet: 100,000 đ
   - Dịch vụ: 150,000 đ
   - Subtotal: 3,900,000 đ
   - Thuế 5%: 195,000 đ
   - TỔNG: 4,095,000 đ
5. Hạn thanh toán: 05/07/2026
6. Lưu hóa đơn → TENANT nhận thông báo

[NGÀY 2 THÁNG 7]
TENANT (Email):
"Bạn có hóa đơn mới: INV-P101-202406
Tổng tiền: 4,095,000 đ
Hạn thanh toán: 05/07/2026
Xem chi tiết: [Link]"

TENANT (Web):
1. Đăng nhập → Xem hóa đơn chi tiết
2. Kiểm tra chỉ số điện nước (có ảnh công tơ)
3. Click "Thanh toán qua VNPay"
4. Chọn: Thẻ ATM nội địa
5. Redirect sang VNPay
6. Chọn ngân hàng: Vietcombank
7. Nhập số thẻ, ngày hết hạn
8. Nhận OTP → Nhập OTP
9. VNPay xử lý: Success
10. Redirect về hệ thống

HỆ THỐNG (TenantPaymentReturnServlet):
1. Nhận response từ VNPay
2. Verify secure hash ✓
3. Parse: responseCode = "00" (Success)
4. Execute transaction:
   - INSERT INTO payments (5,095,000 đ, status: SUCCESS)
   - UPDATE invoices SET status = 'PAID'
5. Commit transaction
6. Show message: "Thanh toán thành công!"

TENANT:
- Xem hóa đơn → Status: PAID ✓
- Download receipt (nếu có)
- Nhận email xác nhận

MANAGER:
- Dashboard cập nhật: +4,095,000 đ doanh thu
- "Quản lý Thanh toán" → Thấy giao dịch mới
```

### Workflow 4: Trường hợp thanh toán trễ

```
[NGÀY 15 THÁNG 7 - TRỄ 10 NGÀY]
TENANT:
1. Đăng nhập → Xem hóa đơn
2. Hệ thống hiển thị:
   ⚠️ HÓA ĐƠN QUÁ HẠN
   Số ngày trễ: 10 ngày
   Tiền gốc: 4,095,000 đ
   Phí phạt: 20,475 đ (0.05% × 10 ngày)
   ---
   TỔNG PHẢI TRẢ: 4,115,475 đ

3. Click "Thanh toán" → VNPay
4. Số tiền charge: 4,115,475 đ (bao gồm phạt)
5. Thanh toán thành công

HỆ THỐNG:
- Lưu payment: 4,115,475 đ
- (Trong đó: gốc 4,095,000 + phạt 20,475)
- Update invoice: PAID
- Lưu audit log: Late payment với penalty

MANAGER:
- Xem payment detail: Thấy có phạt trễ
- Báo cáo doanh thu: Tách riêng tiền phạt (nếu có tính năng)
```

---

## 🎨 SCREENSHOTS (Mô tả)

### 1. Login Page
```
┌─────────────────────────────────────┐
│         🏠 HOSTEL MANAGEMENT        │
│                                     │
│    [  Username  ]                   │
│    [  Password  ]                   │
│                                     │
│    [ Remember me ]  [Forgot pwd?]   │
│                                     │
│         [ LOGIN BUTTON ]            │
│                                     │
│    Powered by VNPay                 │
└─────────────────────────────────────┘
```

### 2. Manager Dashboard
```
┌─────────────────────────────────────────────────┐
│ [☰] Dashboard              👤 Manager A  [🔔] │
├─────────────────────────────────────────────────┤
│ Sidebar:       │ Content:                       │
│ 📊 Dashboard   │ ┌──────┐ ┌──────┐ ┌──────┐   │
│ 🏠 Phòng       │ │  15  │ │  12  │ │ 5tr  │   │
│ 👥 Khách thuê  │ │Phòng │ │Khách │ │Doanh │   │
│ 🧾 Hóa đơn     │ │      │ │Thuê  │ │Thu   │   │
│ 💳 Thanh toán  │ └──────┘ └──────┘ └──────┘   │
│ 📝 Yêu cầu     │                                │
│ 📢 Thông báo   │ 📊 Biểu đồ doanh thu           │
│ 📊 Báo cáo     │ [Line Chart showing revenue]   │
│                │                                │
│                │ 📋 Hóa đơn gần đây             │
│                │ [Table with recent invoices]   │
└────────────────┴────────────────────────────────┘
```

### 3. Tenant Invoice Detail
```
┌─────────────────────────────────────────────────┐
│         HÓA ĐƠN CHI TIẾT                        │
├─────────────────────────────────────────────────┤
│ Mã HĐ: INV-P101-202406                          │
│ Phòng: P101                                     │
│ Kỳ: Tháng 6/2026                                │
│ Hạn TT: 05/07/2026                              │
│ Trạng thái: ⚠️ QUÁ HẠN (10 ngày)               │
│                                                 │
│ ┌───────────────────────────────────────────┐   │
│ │ CHI TIẾT                                  │   │
│ │ Tiền phòng:           3,000,000 đ        │   │
│ │ Tiền điện (250 kWh):    500,000 đ        │   │
│ │ Tiền nước (15 m³):      150,000 đ        │   │
│ │ Internet:               100,000 đ        │   │
│ │ Dịch vụ:                150,000 đ        │   │
│ │ Thuế (5%):              195,000 đ        │   │
│ │ ─────────────────────────────────        │   │
│ │ Subtotal:             4,095,000 đ        │   │
│ │ Phạt trễ (10 ngày):      20,475 đ        │   │
│ │ ═════════════════════════════════        │   │
│ │ TỔNG CỘNG:            4,115,475 đ        │   │
│ └───────────────────────────────────────────┘   │
│                                                 │
│ 📸 Ảnh công tơ điện     📸 Ảnh công tơ nước    │
│ [Image thumbnail]       [Image thumbnail]       │
│                                                 │
│ [  💳 Thanh toán VNPay  ] [ 🏦 Chuyển khoản ]   │
└─────────────────────────────────────────────────┘
```


---

## 🚀 DEPLOYMENT

### Requirements
- Java JDK 11+
- Apache Tomcat 10+
- SQL Server 2022
- Maven 3.6+

### Setup Steps

```bash
# 1. Clone repository
git clone <repository-url>
cd hostel-management

# 2. Setup database
# Chạy file schema.sql trong SQL Server
# Chạy file seed.sql để có dữ liệu mẫu

# 3. Configure application
# Sửa file DatabaseUtil.java với connection string
# Sửa file email.properties với SMTP config
# Sửa file VNPayConfig.java với VNPay credentials

# 4. Build project
mvn clean package

# 5. Deploy to Tomcat
# Copy file .war vào thư mục webapps của Tomcat
# Hoặc deploy qua Tomcat Manager

# 6. Start Tomcat
# Truy cập: http://localhost:8080/hostel-management

# 7. Login với tài khoản mặc định
# Admin: admin / Admin@123
# Manager: manager / Manager@123
# Tenant: tenant / Tenant@123
```

---

## 📊 THỐNG KÊ DỰ ÁN

### Số lượng Code
- **Total Lines:** ~15,000 lines
- **Java Files:** ~80 files
- **JSP Files:** ~50 files
- **SQL Files:** 2 files (schema + seed)

### Phân bổ Code
```
Controllers:   ~3,000 lines (20%)
Services:      ~2,500 lines (17%)
DAO:           ~4,000 lines (27%)
Models/DTOs:   ~1,500 lines (10%)
Views (JSP):   ~3,500 lines (23%)
Utils:         ~500 lines (3%)
```

### Database
- **11 Tables**
- **50+ Columns** (trung bình 5-10 columns/table)
- **Foreign Keys:** 15+ relationships
- **Indexes:** Auto-generated PKs + custom indexes

---

## 🎓 BUSINESS RULES

### Quy tắc Phòng
1. Phòng chỉ có thể cho 1 tenant thuê tại một thời điểm
2. Phòng phải trống (AVAILABLE) mới tạo hợp đồng mới được
3. Giá phòng được set theo cơ sở, có thể override cho từng phòng

### Quy tắc Hóa đơn
1. Mỗi phòng chỉ có 1 hóa đơn cho 1 kỳ (tháng)
2. Hóa đơn chỉ tạo được khi có chỉ số điện nước của tháng đó
3. Chỉ số mới phải >= chỉ số cũ
4. Hóa đơn PAID không thể sửa hoặc xóa
5. Hóa đơn tự động chuyển OVERDUE khi quá due_date

### Quy tắc Thanh toán
1. Chỉ thanh toán được hóa đơn có status UNPAID hoặc OVERDUE
2. VNPay payment tự động duyệt khi thành công
3. Bank transfer cần Manager duyệt thủ công
4. Một hóa đơn có thể có nhiều payment (nếu thanh toán từng phần)
5. Tổng payments >= invoice total → status = PAID

### Quy tắc Phạt trễ
1. Tính từ ngày sau due_date
2. Công thức: 0.05% total_amount/ngày (hoặc 1% room_fee/ngày)
3. Không giới hạn số ngày tối đa
4. Phạt được tính real-time khi thanh toán
5. Phạt không lưu riêng (tính vào payment_amount)

### Quy tắc Yêu cầu
1. Chỉ tenant của phòng mới tạo request được
2. Request phải qua các status: PENDING → ASSIGNED → IN_PROGRESS → DONE
3. Không thể skip status
4. Chỉ Manager/Operator mới update được status

### Quy tắc Thông báo
1. Admin gửi được cho tất cả
2. Manager gửi được cho tenant trong cơ sở
3. Có thể filter theo facility hoặc room
4. Tenant chỉ thấy notification của phòng mình

---

## 🔮 FUTURE ENHANCEMENTS

### Short-term (3-6 tháng)
- [ ] Export hóa đơn ra PDF
- [ ] Gửi SMS notification (Twilio/Esms)
- [ ] Dashboard với real-time charts
- [ ] Mobile app (React Native)
- [ ] Multi-language support (EN/VI)

### Medium-term (6-12 tháng)
- [ ] Quản lý bảo trì định kỳ
- [ ] Tự động tạo hóa đơn hàng tháng (scheduled job)
- [ ] Tích hợp thêm payment gateway (Momo, ZaloPay)
- [ ] AI chatbot hỗ trợ tenant
- [ ] Facial recognition check-in/out

### Long-term (12+ tháng)
- [ ] Mobile app cho Manager/Operator
- [ ] IoT integration (smart meter, smart lock)
- [ ] Predictive analytics (dự đoán công nợ)
- [ ] Blockchain cho hợp đồng thông minh
- [ ] Marketplace kết nối chủ nhà và tenant

---

## 🤝 STAKEHOLDERS

### Primary Users
1. **Chủ nhà trọ** (Admin/Manager)
   - Mục tiêu: Quản lý hiệu quả, tăng doanh thu
   - Pain points: Quản lý thủ công mất thời gian, khó theo dõi công nợ

2. **Nhân viên** (Operator)
   - Mục tiêu: Làm việc hiệu quả, ít sai sót
   - Pain points: Ghi chép nhiều, dễ nhầm lẫn

3. **Người thuê trọ** (Tenant)
   - Mục tiêu: Thanh toán tiện lợi, minh bạch chi phí
   - Pain points: Thanh toán bất tiện, không rõ chi tiết hóa đơn

### Secondary Stakeholders
- **Kế toán:** Cần báo cáo tài chính chính xác
- **Pháp lý:** Cần lưu trữ hợp đồng, audit trail
- **Marketing:** Cần data để phân tích khách hàng

---

## 📞 SUPPORT & CONTACT

### Technical Support
- **Email:** tech-support@hostel-system.com
- **Hotline:** 1900-xxxx
- **Documentation:** [Link to docs]

### Business Contact
- **Sales:** sales@hostel-system.com
- **Partnership:** partner@hostel-system.com

### Development Team
- **GitHub:** [Repository URL]
- **Issue Tracker:** [JIRA/GitHub Issues]
- **Wiki:** [Confluence/GitHub Wiki]

---

## 📚 TÀI LIỆU LIÊN QUAN

1. **Technical Documents:**
   - Database Schema (schema.sql)
   - API Documentation (nếu có REST API)
   - Deployment Guide
   - Developer Guide

2. **Business Documents:**
   - User Manual (Admin/Manager/Operator/Tenant)
   - Business Requirements Document (BRD)
   - System Requirements Specification (SRS)

3. **Design Documents:**
   - Context Diagram
   - Use Case Diagram
   - Entity Relationship Diagram (ERD)
   - Sequence Diagrams

---

## ✅ CONCLUSION

**Hostel Management System** là một giải pháp toàn diện cho việc quản lý nhà trọ hiện đại, kết hợp giữa:
- ✅ Quản lý truyền thống (phòng, khách, hợp đồng)
- ✅ Công nghệ hiện đại (thanh toán online, thông báo tự động)
- ✅ Bảo mật tốt (RBAC, audit logs, password hashing)
- ✅ Trải nghiệm người dùng tốt (responsive, intuitive UI)

Hệ thống phù hợp cho:
- 🏢 Chủ nhà trọ có nhiều cơ sở
- 🏠 Quản lý nhà trọ vừa và nhỏ
- 🏘️ Doanh nghiệp cho thuê căn hộ dịch vụ

**Key Differentiators:**
1. Thanh toán online qua VNPay (hiếm có ở hệ thống nhà trọ)
2. Tự động tính phạt trễ hạn
3. Upload ảnh công tơ (minh bạch)
4. Audit trail đầy đủ
5. Multi-facility support

---

**Version:** 1.0  
**Last Updated:** 25/06/2026  
**Document Owner:** Development Team  
**Status:** ✅ Production Ready

