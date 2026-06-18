# PROMPT THIẾT KẾ UI/UX CHO WEB QUẢN LÝ NHÀ TRỌ - ROLE NHÂN VIÊN VẬN HÀNH (FACILITY OPERATOR)

## Vai trò của AI

Bạn là Senior Business Analyst (BA), Product Owner, System Analyst và UI/UX Designer có kinh nghiệm thiết kế hệ thống Property Management System (PMS), Facility Management System (FMS) và Apartment Management System.

Nhiệm vụ của bạn là phân tích nghiệp vụ, chuẩn hóa quy trình, thiết kế User Flow, Screen Flow, Sitemap, Wireframe, UI Layout, Navigation, Component, Data Display và Responsive Design cho hệ thống.

Mọi thiết kế phải tuân thủ đúng nghiệp vụ bên dưới. Không được tự ý thêm chức năng ngoài yêu cầu nghiệp vụ.

---

# 1. Bối cảnh hệ thống

Đây là hệ thống Web Quản Lý Nhà Trọ.

Trong phạm vi tài liệu này chỉ tập trung vào:

ROLE: FACILITY OPERATOR (NHÂN VIÊN VẬN HÀNH / KỸ THUẬT CƠ SỞ)

Nhân viên vận hành chịu trách nhiệm trực tiếp tại cơ sở được phân công với các tác vụ thực tế bao gồm:
- Cập nhật chỉ số điện
- Cập nhật chỉ số nước
- Xem danh sách yêu cầu hỗ trợ từ người thuê
- Xem chi tiết yêu cầu và cập nhật trạng thái xử lý sự cố cơ bản

---

# 2. Giới hạn quyền

Nhân viên vận hành KHÔNG được:
- Quản lý người thuê (Thêm/Sửa/Xóa/Xem hợp đồng)
- Quản lý nhân sự khác trong hệ thống
- Quản lý hoặc thay đổi cấu hình cơ sở
- Quản lý tài chính, doanh thu, dòng tiền
- Chỉnh sửa thông tin phòng (Diện tích, giá phòng cố định)
- Điều chỉnh hóa đơn hoặc thay đổi đơn giá điện nước
- Không phụ trách bảo trì hệ thống hạ tầng chung (Phần này do BQL và bên thứ 3 xử lý)

Nhân viên vận hành chỉ được:
- Cập nhật chỉ số điện nước định kỳ của các phòng thuộc cơ sở phụ trách.
- Xử lý, xác nhận và cập nhật tiến độ các yêu cầu hỗ trợ/sự cố kỹ thuật từ người thuê.

---

# 3. Luồng tổng thể

Trang chủ
→ Đăng nhập
→ Dashboard Nhân Viên Vận Hành

Từ Dashboard có thể truy cập các phân hệ hành động:
- Quản lý chỉ số điện nước
- Danh sách yêu cầu

---

# 4. Dashboard

Màn hình tổng quan tác vụ hàng ngày của Nhân viên vận hành.

## KPI Cards
Hiển thị:
- Tổng số phòng phụ trách
- Số phòng đã cập nhật điện nước (Kỳ này)
- Số phòng chưa cập nhật điện nước (Kỳ này)
- Số yêu cầu đang xử lý

## Dashboard Widgets

### Thống kê điện nước
- Tiến độ cập nhật chỉ số (Biểu đồ tròn hoặc thanh tiến trình): Đã cập nhật / Chưa cập nhật.

### Tình trạng yêu cầu hỗ trợ
- Số lượng yêu cầu phân loại theo trạng thái: Mới tạo | Đang xử lý | Hoàn thành.

---

# 5. Module Quản Lý Chỉ Số Điện Nước

## Danh sách chỉ số điện nước các phòng
Hiển thị bảng dữ liệu tổng hợp để theo dõi tiến độ đo đạc:

| Mã phòng | Chỉ số điện kỳ trước | Chỉ số nước kỳ trước | Trạng thái | Thời gian cập nhật | Thao tác |
|:---|:---|:---|:---|:---|:---|
| (Ví dụ: P101) | (Số kWh) | (Số m3) | Đã cập nhật / Chưa cập nhật | (Giờ, Ngày) | [Cập nhật] |

### Bộ lọc tìm kiếm
- Cơ sở
- Tầng
- Mã phòng
- Trạng thái cập nhật (Chưa cập nhật / Đã cập nhật)

---

# 6. Cập Nhật Chỉ Số Điện Nước

Giao diện Form/Màn hình thực hiện ghi nhận số liệu ngoài thực địa.

## Thông tin phòng (Read-only)
- Mã phòng
- Cơ sở

## Chỉ số cũ (Hệ thống tự động hiển thị)
- Số điện kỳ trước
- Số nước kỳ trước

## Chỉ số mới (Trường nhập liệu)
- Số điện mới
- Số nước mới

## Ảnh xác thực (Upload component bắt buộc)
- Ảnh công tơ điện mới
- Ảnh công tơ nước mới

## Thông tin hệ thống (Tự động ghi nhận sau khi lưu)
- Trạng thái (Chuyển thành "Đã cập nhật")
- Thời gian cập nhật

### Ràng buộc dữ liệu (Validation Rules)
- **Số điện mới >= Số điện kỳ trước**
- **Số nước mới >= Số nước kỳ trước**
- Bắt buộc phải đính kèm đầy đủ 2 ảnh xác thực công tơ trước khi nhấn nút Hoàn tất.

---

# 7. Module Danh Sách Yêu Cầu

Nơi tiếp nhận các sự cố kỹ thuật từ phòng người thuê gửi lên.

## Danh sách yêu cầu
Hiển thị bảng dữ liệu:

| Mã yêu cầu | Loại yêu cầu | Tiêu đề | Phòng gửi | Ngày gửi | Trạng thái | Thao tác |
|:---|:---|:---|:---|:---|:---|:---|

### Bộ lọc tìm kiếm
- Loại yêu cầu (Điện, nước, cơ sở vật chất, internet...)
- Trạng thái yêu cầu
- Khoảng ngày gửi

### Trạng thái yêu cầu
- Mới tạo
- Đang xử lý
- Hoàn thành
- Từ chối

---

# 8. Chi Tiết Yêu Cầu

## Thông tin yêu cầu
- Mã yêu cầu
- Loại yêu cầu
- Tiêu đề
- Nội dung chi tiết sự cố

## Thông tin gửi
- Phòng gửi
- Người gửi
- Ngày gửi

## Đính kèm trực quan
- Khung hiển thị Ảnh đính kèm
- Trình phát Video đính kèm (nếu có)

## Thông tin xử lý & Phân công
- Trạng thái hiện tại
- Người phụ trách (Mặc định là tài khoản đang đăng nhập)
- Ngày hẹn sửa (Trường chọn ngày tháng năm cụ thể, không ghi chú text tự do)
- Kết quả xử lý (Ghi chú lại tình trạng sau khi kiểm tra/sửa chữa xong)

---

# 9. Quy Tắc Nghiệp Vụ Điện Nước (Business Rules)

Nhân viên vận hành chỉ được thực hiện:
- Cập nhật chỉ số điện đầu kỳ/cuối kỳ.
- Cập nhật chỉ số nước đầu kỳ/cuối kỳ.

Tuyệt đối KHÔNG ĐƯỢC phép:
- Sửa đổi hóa đơn tài chính.
- Sửa đơn giá điện định mức.
- Sửa đơn giá nước định mức.
- Sửa đổi tình trạng công nợ của phòng.

Mọi thay đổi về đơn giá tài chính và xử lý hóa đơn bắt buộc phải do **Ban Quản Lý** hoặc **Quản Lý Tài Chính** thực hiện sau khi nhận số liệu từ phân hệ này gửi sang.

---

# 10. Navigation Rules (Sơ đồ điều hướng)

```
Dashboard
│
├── Quản lý điện nước
│   ├── Danh sách chỉ số điện nước
│   └── Cập nhật chỉ số điện nước (Yêu cầu ảnh công tơ)
│
└── Quản lý yêu cầu
    ├── Danh sách yêu cầu
    └── Chi tiết yêu cầu (Đặt lịch hẹn sửa theo ngày/tháng/năm)
```

---

# 11. Yêu cầu đầu ra của AI

Khi nhận được dữ liệu này, bạn phải xuất ra tài liệu thiết kế hệ thống đầy đủ bao gồm:
1. **Sitemap**: Cấu trúc phân cấp toàn bộ trang web.
2. **User Flow**: Luồng thao tác của nhân viên từ lúc nhận phòng chưa đo đến khi cập nhật xong điện nước / xử lý xong yêu cầu.
3. **Screen Flow**: Luồng chuyển dịch giữa các màn hình.
4. **Information Architecture**: Kiến trúc thông tin của từng cụm module.
5. **Wireframe**: Bản phác thảo bố cục đen trắng cho các màn hình chính (Dashboard, Form nhập số điện nước, Chi tiết yêu cầu).
6. **UI Layout**: Gợi ý giao diện màu sắc trực quan (Ưu tiên thiết kế Mobile Responsive hoặc Tablet vì nhân viên vận hành sẽ di chuyển liên tục để đo đạc và kiểm tra).
7. **Responsive Design**: Quy tắc hiển thị trên Smartphone.
8. **Component List**: Danh sách các thành phần UI (Button, Input, Upload Card, Status Badge).
9. **CRUD Matrix**: Ma trận quyền thao tác dữ liệu (Create, Read, Update, Delete) cho role này.
10. **Data Fields**: Chi tiết định dạng dữ liệu cho từng màn hình.
11. **Validation Rules**: Các quy tắc kiểm tra lỗi dữ liệu đầu vào.
12. **Permission Matrix**: Bảng phân quyền so với các Role khác để đảm bảo không lấn quyền tài chính và quản lý hệ thống.

Mỗi màn hình được thiết kế yêu cầu mô tả rõ: Mục tiêu nghiệp vụ, Thành phần giao diện, Dữ liệu hiển thị, Nút chức năng, Luồng điều hướng cụ thể, Validation, Trạng thái trống (Empty State), và Trạng thái lỗi (Error State).