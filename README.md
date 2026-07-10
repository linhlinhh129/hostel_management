# Hệ thống Quản lý Phòng trọ (Hostel Management System)

Một ứng dụng nền web toàn diện được thiết kế để tối ưu hóa việc quản lý phòng trọ và chung cư mini. Được xây dựng dựa trên Java Servlets, JSP và SQL Server, hệ thống cung cấp các cổng thông tin (portals) chuyên biệt dành cho Quản trị viên (Admin), Quản lý (Manager), Nhân viên vận hành (Operator) và Người thuê (Tenant) nhằm xử lý hiệu quả cơ sở vật chất, việc thuê phòng, thanh toán hóa đơn và các yêu cầu bảo trì.

## 🌟 Các Tính năng Nổi bật

### Dành cho Admin & Quản lý (Manager)
- **Quản lý Cơ sở vật chất:** Thêm, cập nhật và quản lý nhiều tòa nhà, khu nhà trọ.
- **Quản lý Phòng:** Theo dõi tình trạng phòng (Trống, Đang thuê, Đang bảo trì), việc sắp xếp người thuê và giá phòng.
- **Quản lý Người dùng:** Quản lý phân quyền và vai trò người dùng (Admin, Manager, Operator, Tenant).
- **Tài chính & Hóa đơn:** Tự động tạo hóa đơn hàng tháng dựa trên tiền phòng, điện, nước và các dịch vụ khác. Theo dõi tình trạng thanh toán.
- **Báo cáo & Thống kê:** Xem doanh thu, tỷ lệ lấp đầy phòng và hiệu suất tổng thể của hệ thống.

### Dành cho Nhân viên vận hành (Operator)
- **Chốt số Điện/Nước:** Cập nhật, theo dõi chỉ số đồng hồ điện nước hàng tháng cho từng phòng kèm theo hình ảnh minh chứng.
- **Xử lý Yêu cầu (Request):** Tiếp nhận và phản hồi các yêu cầu hỗ trợ hoặc bảo trì từ người thuê.

### Dành cho Người thuê (Tenant)
- **Trang tổng quan (Dashboard):** Xem thông tin cá nhân, chi tiết phòng đang thuê và thông tin hợp đồng.
- **Hóa đơn & Thanh toán:** Xem hóa đơn hàng tháng, theo dõi lịch sử thanh toán và thực hiện đóng tiền.
- **Yêu cầu hỗ trợ:** Gửi yêu cầu (vd: sửa chữa, khiếu nại) và theo dõi tiến độ xử lý.
- **Quản lý người ở ghép (Dependents):** Đăng ký thông tin người thân hoặc bạn cùng phòng sống chung.

## 💻 Công nghệ Sử dụng

- **Backend:** Java 17, Jakarta EE 10 (Servlets)
- **Frontend:** HTML, CSS, JavaScript, JSP (JavaServer Pages), JSTL
- **Cơ sở dữ liệu:** Microsoft SQL Server 2022
- **Build Tool:** Maven
- **Server (Máy chủ):** Apache Tomcat 10
- **Thư viện:**
  - `mssql-jdbc`: Kết nối cơ sở dữ liệu
  - `jbcrypt`: Mã hóa mật khẩu
  - `slf4j`: Ghi log (Logging)
  - `jakarta.mail`: Gửi thông báo qua Email
  - `gson`: Xử lý JSON

## 📂 Cấu trúc Dự án

```text
src/
├── main/
│   ├── java/com/quanlyphongtro/
│   │   ├── controller/      # Servlet controller dùng để xử lý HTTP requests
│   │   ├── dao/             # Data Access Objects (Giao tiếp với Database)
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── filter/          # Bộ lọc xác thực và phân quyền (Authentication/Authorization)
│   │   ├── model/           # Các lớp Entity ánh xạ với Database
│   │   ├── service/         # Triển khai Business logic
│   │   └── util/            # Các tiện ích (Kết nối DB, mã hóa mật khẩu)
│   └── webapp/
│       ├── assets/          # File tĩnh (CSS, JS, hình ảnh)
│       └── WEB-INF/
│           └── views/       # Các trang JSP được chia theo vai trò (admin, manager, tenant, v.v.)
database/
├── schema.sql               # File SQL khởi tạo cơ sở dữ liệu và bảng
└── seed.sql                 # Dữ liệu mẫu dùng để test
```

## 🚀 Hướng dẫn Cài đặt

### Yêu cầu hệ thống
- JDK 17
- Apache Tomcat 10
- Microsoft SQL Server 2022
- Maven

### 1. Thiết lập Database
1. Mở SQL Server Management Studio (SSMS).
2. Chạy script `database (1)/schema.sql` để tạo database `HostelManagement` cùng các bảng liên quan.
3. (Tùy chọn) Chạy script `database (1)/seed.sql` để chèn dữ liệu mẫu và các tài khoản mặc định.

### 2. Cấu hình Kết nối Database
Cập nhật chuỗi kết nối database trong source code sao cho khớp với tài khoản SQL Server của bạn. Thông thường, cấu hình này nằm ở `src/main/java/com/quanlyphongtro/util/DBConnection.java` hoặc một file cấu hình tương đương.

### 3. Build & Chạy ứng dụng
1. Clone repository này về máy.
2. Mở dự án trong IDE yêu thích của bạn (IntelliJ IDEA, Eclipse, Apache NetBeans).
3. Build dự án bằng Maven:
   ```bash
   mvn clean install
   ```
4. Deploy file `.war` vừa được tạo (hoặc chạy trực tiếp) lên Tomcat 10.
5. Truy cập ứng dụng qua đường dẫn `http://localhost:8080/hostel-management`.

## 👥 Tài khoản Mặc định (Nếu dùng dữ liệu mẫu)

- **Admin:** `admin` / `password123` (hoặc tuân theo script seed)
- **Quản lý (Manager):** `manager` / `password123`
- **Nhân viên (Operator):** `operator` / `password123`
- **Người thuê (Tenant):** `tenant` / `password123`

*(Vui lòng kiểm tra lại file `seed.sql` hoặc DB để lấy mật khẩu chính xác và hãy nhớ thay đổi mật khẩu khi deploy thực tế).*

---
*Dự án được phát triển cho đồ án môn học SWP391 (Software Architecture and Design).*
