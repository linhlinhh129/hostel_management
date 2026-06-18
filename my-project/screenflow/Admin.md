# PROMPT THIẾT KẾ UI/UX CHO WEB QUẢN LÝ NHÀ TRỌ - ROLE ADMIN

## Vai trò của AI

Nhiệm vụ của bạn là phân tích nghiệp vụ, chuẩn hóa quy trình, thiết kế User Flow, Screen Flow, Sitemap, Wireframe, UI Layout, Navigation, Component, Data Display và Responsive Design cho hệ thống.

Không được tự ý thêm chức năng ngoài yêu cầu nghiệp vụ.

---

# 1. Bối cảnh hệ thống

Đây là hệ thống Web Quản Lý Nhà Trọ.

Trong phạm vi tài liệu này chỉ tập trung vào:

ROLE: ADMIN

Admin là người quản trị cấp cao nhất của hệ thống.

Admin quản lý hệ thống ở cấp toàn bộ cơ sở.

Admin không trực tiếp vận hành hoạt động thuê phòng hằng ngày.

---


# 3. Luồng tổng thể của Admin

Trang chủ
→ Đăng nhập
→ Admin Dashboard

Từ Dashboard Admin có thể truy cập:

- Quản lý cơ sở
- Quản lý thông báo
- Xem báo cáo doanh thu
- Quản lý nhân sự
- Cấu hình hệ thống
- Nhật ký kiểm tra (Audit Log)

---

# 4. Dashboard Admin

Dashboard là màn hình trung tâm.

## KPI Cards

Hiển thị:
- Tổng doanh thu tháng
- Tổng số cơ sở
- Tổng số nhân sự
- Tổng số thông báo
- Tổng số Audit Log hôm nay

---

## Dashboard Widgets

### Thống kê cơ sở

- Tổng số cơ sở
- Cơ sở đang hoạt động

### Thống kê nhân sự

- Tổng số nhân sự
- Ban quản lý
- Nhân viên

### Hoạt động gần đây

Hiển thị 10 hoạt động mới nhất:

- Thời gian
- Người thực hiện
- Hành động

### Doanh thu tháng 

- Tổng số doanh thu chi tháng của từng cơ sở sử dụng bảng tăng trưởng cho từng cơ sở
---

# 5. Module Quản Lý Cơ Sở

## Danh sách cơ sở

Hiển thị bảng:

| Cột |
|------|
| ID cơ sở |
| Tên cơ sở |
| Địa chỉ |
| Số tầng |
| Tổng số phòng |
| Người quản lý |
| Trạng thái |
| Thao tác |

---

### Bộ lọc

- ID cơ sở
- Tên cơ sở
- Trạng thái

---

### Chức năng

- Thêm cơ sở
- Chỉnh sửa cơ sở
- Xem chi tiết cơ sở
- Xóa cơ sở

---

# 6. Màn hình Thêm Cơ Sở

## Thông tin nhập

### Thông tin cơ sở

- ID cơ sở

Format:

CS001
CS002


- Tên cơ sở
- Địa chỉ chi tiết
- Số tầng
- Số phòng từng tầng

---

### Validation

ID cơ sở không được trùng.

Số tầng > 0

Số phòng > 0

---

# 7. Chi Tiết Cơ Sở

## Thông tin cơ sở

- ID cơ sở
- Tên cơ sở
- Địa chỉ
- Số tầng
- Ngày tạo

---


Hiển thị:

- Họ tên
- Ngày sinh
- SĐT
- CCCD
- Email
- Mối quan hệ
- Thuộc người thuê nào

---

### Điều hướng

Người thuê chính phải click được.

Chi tiết người phụ thuộc
→ Click người thuê chính
→ Chi tiết người thuê

---

# 8. Module Quản Lý Nhân Sự

## Danh sách nhân sự

Hiển thị:

| Mã nhân sự |
| Họ tên |
| SĐT |
| Email |
| Vai trò |
| Cơ sở phụ trách |
| Trạng thái |
| Thao tác |

---

### Chức năng

- Thêm nhân sự
- Xem chi tiết
- Chỉnh sửa
- Khóa tài khoản

---

# 9. Màn Hình Thêm Nhân Sự

## Thông tin cá nhân

- Họ tên
- Ngày sinh
- SĐT
- CCCD
- Email

---

## Thông tin tài khoản

- Username
- Vai trò

Vai trò:

- Ban Quản Lý
- Nhân Viên 

---

# 10. Chi Tiết Nhân Sự

## Hiển thị

- Mã nhân sự
- Họ tên
- SĐT
- Email
- CCCD
- Vai trò
- Cơ sở phụ trách

---

### Chức năng

- Chỉnh sửa
- Gán cơ sở quản lý
- Khóa tài khoản

---

# 11. Module Gán Cơ Sở Quản Lý

## Mục tiêu

Cho phép Admin gán Ban Quản Lý phụ trách một hoặc nhiều cơ sở.

---

## Thông tin hiển thị

### Nhân sự

- Mã nhân sự
- Họ tên

---

### Danh sách cơ sở

Checkbox:

□ CS001


---

### Kết quả

Sau khi được gán:

Ban Quản Lý chỉ nhìn thấy các cơ sở được phân công.

---

# 12. Module Quản Lý Thông Báo

## Danh sách thông báo

Hiển thị:

| Mã |
| Tiêu đề |
| Người tạo |
| Ngày tạo |
| Đối tượng |
| Trạng thái |

---

## Tạo thông báo

Thông tin:

- Tiêu đề
- Nội dung

---

### Đối tượng gửi

- Tất cả cơ sở

---

### Hệ thống tự sinh

- Mã thông báo
- Ngày tạo

---

# 13. Module Cấu Hình Hệ Thống

## Cấu hình hệ thống

Admin được phép:

- Quản lý tham số hệ thống
- Cấu hình bảo mật
- Cấu hình thông báo
- Cấu hình Audit Log

---

# 14. Module Nhật Ký Kiểm Tra (Audit Log)

## Danh sách log

Hiển thị:

| Log ID |
| Thời gian |
| Người thực hiện |
| Chức năng |
| Hành động |
| Địa chỉ IP |
| Trạng thái |

---

### Bộ lọc

- Khoảng thời gian
- Người thực hiện
- Chức năng
- Hành động

---

## Chi tiết log

Hiển thị:

- Log ID
- Tài khoản thực hiện
- Hành động

Ví dụ:

- Tạo cơ sở
- Sửa cơ sở
- Xóa cơ sở
- Tạo nhân sự
- Khóa tài khoản
- Gửi thông báo

- Thời gian
- Địa chỉ IP
- Trạng thái

---

# 15. Navigation Rules

Dashboard
│
├── Quản lý cơ sở
│   ├── Danh sách cơ sở
│   ├── Thêm cơ sở
│   └── Chi tiết cơ sở
│  
│
├── Báo cáo doanh thu
├   ├── Doanh thu toàn hệ thống
├   ├── Doanh thu theo cơ sở
└   └── Doanh thu theo kỳ
│
├── Quản lý nhân sự
│   ├── Danh sách nhân sự
│   ├── Thêm nhân sự
│   ├── Chi tiết nhân sự
│   └── Gán cơ sở quản lý
│
├── Quản lý thông báo
│   ├── Danh sách thông báo
│   └── Tạo thông báo
│
├── Cấu hình hệ thống
│
└── Nhật ký kiểm tra
    ├── Danh sách log
    └── Chi tiết log

---

# 20. Yêu cầu đầu ra của AI

Khi thiết kế phải tạo:

1. Sitemap
2. User Flow
3. Screen Flow
4. Information Architecture
5. Wireframe
6. UI Layout
7. Responsive Design
8. Component List
9. CRUD Matrix
10. Data Fields
11. Validation Rules
12. Permission Matrix

Mỗi màn hình phải mô tả:

- Mục tiêu nghiệp vụ
- Thành phần giao diện
- Dữ liệu hiển thị
- Nút chức năng
- Luồng điều hướng
- Validation
- Empty State
- Error State

Thiết kế phải theo chuẩn ERP/PMS chuyên nghiệp và có khả năng mở rộng trong tương lai.
