# PROMPT THIẾT KẾ UI/UX CHO WEB QUẢN LÝ NHÀ TRỌ - ROLE NGƯỜI THUÊ (APARTMENT OWNER)

## Vai trò của AI

Nhiệm vụ của bạn là phân tích nghiệp vụ, chuẩn hóa quy trình, thiết kế User Flow, Screen Flow, Sitemap, Wireframe, UI Layout, Navigation, Component, Data Display và Responsive Design cho hệ thống.

Không được tự ý thêm chức năng ngoài yêu cầu nghiệp vụ.

---

# 1. Bối cảnh hệ thống

Đây là hệ thống Web Quản Lý Nhà Trọ.

Trong phạm vi tài liệu này chỉ tập trung vào:

ROLE: NGƯỜI THUÊ (APARTMENT OWNER / TENANT)

Người thuê là người đại diện thuê căn hộ/phòng trọ.
Họ sử dụng hệ thống để theo dõi thông tin cá nhân, quản lý người phụ thuộc ở cùng, nhận thông báo, gửi yêu cầu hỗ trợ đến Ban Quản Lý (BQL), xem và thanh toán hóa đơn hàng tháng.

---

# 2. Giới hạn quyền của Người thuê

Người thuê KHÔNG được:

- Xem thông tin của các phòng khác, người thuê khác.
- Chỉnh sửa số tiền trên hóa đơn.
- Tự ý xóa hoặc thay đổi lịch sử giao dịch, thông báo.

Người thuê chỉ được:

- Xem thông tin cá nhân, thông tin hợp đồng và người phụ thuộc của phòng mình.
- Gửi yêu cầu (sự cố, dịch vụ) lên BQL.
- Xem chi tiết hóa đơn và lịch sử thanh toán của phòng mình.
- Đọc thông báo từ BQL.

---

# 3. Luồng tổng thể của Người thuê

Trang chủ
→ Đăng nhập
→ Apartment Owner Dashboard

Từ Dashboard, Người thuê có thể truy cập các chức năng:

- Quản lý thông báo
- Quản lý yêu cầu
- Quản lý thông tin cá nhân
- Danh sách người phụ thuộc
- Quản lý hóa đơn và thanh toán

---

# 4. Apartment Owner Dashboard

Dashboard là màn hình trung tâm của Người thuê.

## KPI Cards & Cảnh báo

Hiển thị:

- Số tiền hóa đơn chưa thanh toán (Nổi bật nếu có nợ)
- Số thông báo mới chưa đọc
- Số yêu cầu đang xử lý

## Dashboard Widgets

- **Thông báo mới nhất**: Hiển thị 3-5 thông báo gần nhất từ BQL.
- **Hóa đơn gần nhất**: Tóm tắt hóa đơn kỳ này (Tổng tiền, Hạn thanh toán, Trạng thái).
- **Trạng thái yêu cầu**: Cập nhật nhanh các yêu cầu hỗ trợ vừa gửi.

---

# 5. Module Quản Lý Thông Báo

## Danh sách thông báo
Hiển thị danh sách các thông báo được gửi từ BQL.
- Phân biệt rõ thông báo chưa đọc / đã đọc.

## Chi tiết thông báo
Hiển thị:
- Tiêu đề
- Nội dung
- Ngày tạo thông báo

---

# 6. Module Quản Lý Yêu Cầu

## Danh sách yêu cầu
Hiển thị lịch sử các yêu cầu đã gửi cho BQL (Ví dụ: báo hỏng điện, nước, khiếu nại...).
| Tiêu đề | Ngày tháng | Thể loại | Trạng thái | Thao tác |

## Tạo yêu cầu
Biểu mẫu cho phép người thuê gửi yêu cầu mới:
- Thể loại yêu cầu (Dropdown/Select)
- Tiêu đề
- Nội dung
- Ảnh đính kèm (Upload)
- Phòng gửi (Auto-fill dựa trên tài khoản)
- Ngày tháng (Auto-fill)

## Chi tiết yêu cầu
Xem lại nội dung đã gửi, hình ảnh đính kèm và cập nhật Trạng thái xử lý từ BQL.

---

# 7. Module Quản Lý Thông Tin Cá Nhân

## Chi tiết Thông tin cá nhân
Hiển thị hồ sơ của người đại diện thuê (Read-only hoặc chỉ sửa một số trường cơ bản):
- Mã người thuê
- Họ và tên
- Ngày tháng năm sinh
- SĐT
- CCCD
- Email
- Hợp đồng thuê phòng nào
- Người phụ thuộc (Danh sách tóm tắt)

---

# 8. Module Quản Lý Người Phụ Thuộc

## Danh sách người phụ thuộc
Danh sách những người ở cùng phòng với người thuê chính.

## Chi tiết thông tin người phụ thuộc
Hiển thị thông tin cụ thể:
- Mã người phụ thuộc
- Họ và tên
- Ngày tháng năm sinh
- SĐT
- CCCD
- Email
- Mối quan hệ
- Phụ thuộc bởi ai (Mã/Tên của người thuê chính)

---

# 9. Module Quản Lý Hóa Đơn và Thanh Toán

## Danh sách hóa đơn
Hiển thị các hóa đơn theo kỳ (tháng/năm).
| Kỳ hóa đơn | Tổng tiền | Hạn thanh toán | Trạng thái | Thao tác |

## Chi tiết hóa đơn
Hiển thị minh bạch các khoản phí:
- Phòng
- Kỳ hóa đơn (Ngày tháng/năm)
- Tiền phòng cố định
- Số điện cũ - Số điện mới - Thành tiền điện
- Số nước cũ - Số nước mới - Thành tiền nước
- Phí dịch vụ
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán (Chưa thanh toán / Đã thanh toán / Quá hạn)

*Lưu ý UX: Cần có hướng dẫn thanh toán (Thông tin chuyển khoản của BQL) hoặc nút chức năng hỗ trợ thanh toán.*

---

# 10. Navigation Rules (Screen Flow)

Trang chủ
│
└── Đăng nhập
    │
    └── Apartment Owner Dashboard
        │
        ├── Quản lý thông báo
        │   ├── Danh sách thông báo
        │   └── Chi tiết thông báo
        │
        ├── Quản lý yêu cầu
        │   ├── Danh sách yêu cầu
        │   │   └── Chi tiết yêu cầu
        │   └── Tạo yêu cầu
        │
        ├── Quản lý thông tin cá nhân
        │
        ├── Danh sách người phụ thuộc
        │   └── Chi tiết thông tin người phụ thuộc
        │
        └── Quản lý hóa đơn và thanh toán
            ├── Danh sách hóa đơn
            └── Chi tiết hóa đơn

---

# 11. Yêu cầu đầu ra của AI

Khi thiết kế phải tạo:

1. Sitemap
2. User Flow
3. Screen Flow
4. Information Architecture
5. Wireframe
6. UI Layout
7. Responsive Design
8. Component List
9. Data Fields
10. Validation Rules

Mỗi màn hình phải mô tả:

- Mục tiêu nghiệp vụ
- Thành phần giao diện
- Dữ liệu hiển thị
- Nút chức năng
- Luồng điều hướng
- Trải nghiệm người dùng (UX): Mobile-friendly ưu tiên hàng đầu do người thuê thường truy cập qua điện thoại.
- Thiết kế màu sắc cảnh báo trực quan cho Hóa đơn (Đỏ: Nợ/Quá hạn, Xanh: Đã thanh toán) và Yêu cầu hỗ trợ.