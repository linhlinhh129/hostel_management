# PROMPT THIẾT KẾ UI/UX & SCREEN FLOW CHO WEB QUẢN LÝ NHÀ TRỌ (VAI TRÒ BAN QUẢN LÝ)


Không được tự ý thêm chức năng ngoài phạm vi nghiệp vụ nếu không có lý do hợp lý.

---

# 1. Bối cảnh hệ thống

Đây là hệ thống Web Quản Lý Nhà Trọ.

Người dùng chính trong phạm vi thiết kế:

- Ban Quản Lý (Manager/Admin)

Ban quản lý chịu trách nhiệm:

- Quản lý thông báo
- Quản lý người thuê
- Quản lý căn hộ/phòng
- Quản lý người phụ thuộc
- Tiếp nhận và xử lý yêu cầu của người thuê


---

# 2. Luồng tổng thể của Ban Quản Lý

## Đăng nhập

Trang chủ
→ Đăng nhập
→ Dashboard Ban Quản Lý

---

## Dashboard

Dashboard là màn hình trung tâm.

Hiển thị:

### KPI Cards

- Tổng số căn hộ/phòng
- Số phòng đang thuê
- Số phòng trống
- Tổng số người thuê
- Tổng số người phụ thuộc
- Tổng số yêu cầu đang xử lý
- Tổng số thông báo đã gửi

### Quick Actions

- Tạo thông báo
- Thêm người thuê
- Xem danh sách căn hộ
- Xem danh sách yêu cầu

### Thống kê

- Biểu đồ tỷ lệ phòng trống/phòng đang thuê
- Biểu đồ số lượng yêu cầu theo trạng thái
- Biểu đồ người thuê mới theo tháng

---

# 3. Module Quản Lý Thông Báo

## Danh sách thông báo

Hiển thị bảng:

| Cột |
|------|
| Mã thông báo |
| Tiêu đề |
| Ngày tạo |
| Người tạo |
| Đối tượng nhận |
| Trạng thái |
| Thao tác |

Thao tác:

- Xem chi tiết
- Chỉnh sửa
- Xóa

---

## Tạo thông báo

Thông tin:

- Tiêu đề
- Nội dung
- Đối tượng nhận

Đối tượng nhận:

- Tất cả người thuê
- Theo căn hộ/phòng

Hệ thống tự sinh:

- Mã thông báo
- Ngày tạo

Nút:

- Lưu nháp
- Gửi thông báo
- Hủy

---

## Chi tiết thông báo

Hiển thị:

- Mã thông báo
- Tiêu đề
- Nội dung
- Ngày tạo
- Người tạo
- Đối tượng nhận

---

# 4. Module Quản Lý Người Thuê

## Danh sách người thuê

Hiển thị bảng:

| Cột |
|------|
| Mã người thuê |
| Họ tên |
| SĐT |
| Email |
| Căn hộ |
| Trạng thái |
| Ngày bắt đầu thuê |
| Thao tác |

Bộ lọc:

- Tên
- Mã người thuê
- SĐT
- Căn hộ
- Trạng thái

Thao tác:

- Xem chi tiết
- Chỉnh sửa
- Xem hợp đồng

---

## Thêm người thuê

Thông tin:

- Mã người thuê
- Họ tên
- Ngày sinh
- Giới tính
- CCCD
- SĐT
- Email
- Địa chỉ

Thông tin thuê:

- Căn hộ/phòng thuê
- Ngày bắt đầu thuê
- Ngày kết thúc
- Trạng thái

Đính kèm:

- Hợp đồng thuê

---

## Chi tiết người thuê

Hiển thị:

### Thông tin cá nhân

- Mã người thuê
- Họ tên
- Ngày sinh
- Giới tính
- CCCD
- SĐT
- Email

### Thông tin thuê

- Căn hộ đang thuê
- Ngày bắt đầu thuê
- Trạng thái thuê
- Hợp đồng

### Người phụ thuộc

Danh sách:

- Mã người phụ thuộc
- Họ tên
- Quan hệ

Có thể bấm vào từng người phụ thuộc để xem chi tiết.

---

# 5. Module Quản Lý Căn Hộ / Phòng

## Danh sách căn hộ

Hiển thị bảng:

| Cột |
|------|
| Mã căn hộ |
| Diện tích |
| Chủ thuê |
| Trạng thái |
| Số người đang ở |
| Thao tác |

Bộ lọc:

- Mã căn hộ
- Trạng thái
- Chủ thuê

---

## Chi tiết căn hộ

Hiển thị:

### Thông tin căn hộ

- Mã căn hộ
- Diện tích
- Trạng thái
- Ngày tạo

### Người thuê hiện tại

Hiển thị card hoặc bảng:

- Mã người thuê
- Họ tên
- SĐT
- Email
- Trạng thái thuê

### Yêu cầu điều hướng bắt buộc

Khi Ban Quản Lý bấm vào người thuê trong màn hình Chi Tiết Căn Hộ:

Chi Tiết Căn Hộ
→ Click Người Thuê
→ Chuyển hướng sang
→ Chi Tiết Người Thuê

AI phải thiết kế liên kết trực tiếp giữa:

Apartment Detail Screen
→ Tenant Detail Screen

để Ban Quản Lý có thể xem đầy đủ hồ sơ người thuê.

Không được chỉ hiển thị tên người thuê dạng text tĩnh.

Tên người thuê phải là:

- Hyperlink
hoặc
- Button "Xem hồ sơ"
hoặc
- Clickable Card

---

# 6. Module Người Phụ Thuộc

## Danh sách người phụ thuộc

Hiển thị:

| Cột |
|------|
| Mã |
| Họ tên |
| Ngày sinh |
| Quan hệ |
| Người thuê chính |
| Thao tác |

---

## Chi tiết người phụ thuộc

Hiển thị:

- Mã người phụ thuộc
- Họ tên
- Ngày sinh
- Giới tính
- CCCD
- Email
- Quan hệ
- Thuộc người thuê nào

Yêu cầu:

Tên người thuê chính phải click được.

Luồng:

Chi Tiết Người Phụ Thuộc
→ Click Người Thuê Chính
→ Chi Tiết Người Thuê

---

# 7. Module Quản Lý Yêu Cầu

## Danh sách yêu cầu

Hiển thị:

| Cột |
|------|
| Mã yêu cầu |
| Loại yêu cầu |
| Tiêu đề |
| Người gửi |
| Phòng gửi |
| Ngày gửi |
| Trạng thái |
| Thao tác |

Bộ lọc:

- Loại yêu cầu
- Trạng thái
- Người gửi

---

## Chi tiết yêu cầu

Hiển thị:

- Mã yêu cầu
- Loại yêu cầu
- Tiêu đề
- Nội dung
- Ảnh đính kèm
- Người gửi
- Phòng gửi
- Ngày gửi
- Trạng thái
- Người phụ trách

Các hành động:

- Tiếp nhận
- Đang xử lý
- Hoàn thành
- Từ chối

---

# 8. Module Audit Log

## Danh sách log

Hiển thị:

| Cột |
|------|
| Log ID |
| Thời gian |
| Người thực hiện |
| Chức năng |
| Hành động |
| Dữ liệu thay đổi |
| IP |
| Kết quả |

Bộ lọc:

- Khoảng thời gian
- Người thực hiện
- Chức năng
- Hành động

---

## Chi tiết Log

Hiển thị:

- Log ID
- Thời gian
- Người thực hiện
- Vai trò
- Hành động
- Dữ liệu trước thay đổi
- Dữ liệu sau thay đổi
- IP Address
- Trạng thái

---

# 9. Quy tắc Navigation bắt buộc

AI phải thiết kế điều hướng theo các quy tắc sau:

1. Dashboard → Danh sách người thuê → Chi tiết người thuê
2. Dashboard → Danh sách căn hộ → Chi tiết căn hộ
3. Dashboard → Danh sách yêu cầu → Chi tiết yêu cầu
4. Dashboard → Danh sách thông báo → Chi tiết thông báo
5. Dashboard → Danh sách người phụ thuộc → Chi tiết người phụ thuộc

Liên kết chéo:

6. Chi tiết căn hộ → Người thuê → Chi tiết người thuê
7. Chi tiết người thuê → Người phụ thuộc → Chi tiết người phụ thuộc
8. Chi tiết người phụ thuộc → Người thuê chính → Chi tiết người thuê

Các liên kết này là bắt buộc.

---

# 10. Yêu cầu đầu ra của AI

Khi thiết kế giao diện phải tạo:

1. Sitemap
2. User Flow
3. Screen Flow
4. Wireframe
5. UI Layout
6. Component List
7. Data Fields
8. Navigation Rules
9. Responsive Design
10. CRUD Actions

Mỗi màn hình phải mô tả:

- Mục đích
- Thành phần giao diện
- Dữ liệu hiển thị
- Nút chức năng
- Điều hướng
- Validation
- Empty State
- Error State

Thiết kế phải theo chuẩn hệ thống quản lý doanh nghiệp chuyên nghiệp và đảm bảo khả năng mở rộng trong tương lai.
