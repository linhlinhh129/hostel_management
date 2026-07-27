# Feature Specification: operator-utility-request-display

**Feature Branch**: `[###-operator-utility-request-display]`

**Created**: 2026-07-27

**Status**: Draft

**Input**: User description: "khi mà ban quản lý gửi báo cáo sai số sang bên nhân viên vận hành nó nhận được ở trong trang danh sách yêu cầu thì cái thể loại nó hiện là utility, giờ mình muốn chuyển thành tiếng việt và hiện số phòng đang yêu cầu và ở trong phần thông tin chi tiết cũng hiện phòng và cơ sở chi tiết cho mình"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Hiển thị đúng thể loại và thông tin phòng trên Danh sách yêu cầu (Priority: P1)

Là một Nhân viên vận hành (Operator),
Tôi muốn thể loại "UTILITY" được hiển thị là "Điện nước" và thấy được thông tin Số phòng trên trang Danh sách yêu cầu,
Để tôi dễ dàng nhận biết các báo cáo sai số điện nước từ Ban quản lý và biết ngay yêu cầu đó thuộc phòng nào.

**Why this priority**: Cải thiện trải nghiệm UX, giúp Operator phân loại và định danh nhanh yêu cầu mà không bị nhầm lẫn bởi thuật ngữ tiếng Anh, đồng thời hiển thị thông tin Phòng bị thiếu.

**Independent Test**: Có thể test độc lập bằng cách đăng nhập vào tài khoản Operator, vào trang Danh sách yêu cầu, kiểm tra các yêu cầu được tạo từ báo cáo sai số của Quản lý.

**Acceptance Scenarios**:

1. **Given** một yêu cầu báo lỗi điện nước được Ban quản lý gửi, **When** Operator xem danh sách yêu cầu, **Then** cột "Thể loại" hiển thị "SỰ CỐ ĐIỆN NƯỚC" (hoặc ĐIỆN NƯỚC).
2. **Given** một yêu cầu báo lỗi điện nước, **When** Operator xem danh sách yêu cầu, **Then** cột "Phòng" hiển thị đúng mã phòng (VD: P.101) và tên cơ sở tương ứng thay vì dấu gạch ngang (—).
3. **Given** tính năng lọc theo thể loại trên danh sách, **When** Operator nhấn vào hộp chọn Thể loại, **Then** có tuỳ chọn "Điện nước".

---

### User Story 2 - Hiển thị thông tin phòng và cơ sở trong Chi tiết yêu cầu (Priority: P1)

Là một Nhân viên vận hành (Operator),
Tôi muốn xem được thông tin Phòng và Cơ sở trong trang Chi tiết yêu cầu của một báo cáo sai số điện nước,
Để tôi biết chính xác vị trí cần đến kiểm tra mà không cần phải đoán dựa vào tiêu đề.

**Why this priority**: Giúp Operator thực hiện nghiệp vụ kiểm tra số điện nước chính xác tại thực địa.

**Independent Test**: Có thể test độc lập bằng cách nhấn vào xem chi tiết một yêu cầu "Utility".

**Acceptance Scenarios**:

1. **Given** một yêu cầu báo lỗi điện nước, **When** Operator vào trang Chi tiết yêu cầu, **Then** trường "Phòng / Cơ sở" hiển thị đúng mã phòng và tên cơ sở (VD: P.101 — Cơ sở A).

### Edge Cases

- Điều gì xảy ra nếu yêu cầu được tạo mà không có thông tin Phòng (ví dụ báo lỗi chung của cơ sở)? Hệ thống sẽ hiển thị tên cơ sở và bỏ qua số phòng (đã được xử lý bởi logic UI hiện tại nếu dữ liệu db chuẩn).
- Dữ liệu cũ trong DB đang bị thiếu `room_id` và `facility_id` trong bảng `requests` đối với category = UTILITY thì xử lý thế nào? (Chỉ áp dụng cho dữ liệu mới, hoặc cần script update db nếu thực sự cần thiết, thông thường fix từ luồng tạo mới là đủ).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST lưu thông tin `room_id` và `facility_id` vào bảng `requests` khi Ban quản lý tạo yêu cầu gửi Operator từ tính năng Báo cáo sai số điện nước (hóa đơn).
- **FR-002**: Trên giao diện Danh sách yêu cầu của Operator, hệ thống MUST hiển thị text "Điện nước" hoặc "Sự cố điện nước" cho các yêu cầu có category là `UTILITY`.
- **FR-003**: Dropdown bộ lọc Thể loại trên trang Danh sách yêu cầu MUST hiển thị tuỳ chọn "Điện nước" cho `UTILITY`.

### Key Entities

- **Request (Yêu cầu)**: Bổ sung/đảm bảo việc mapping trường `room_id` và `facility_id` từ `Invoice` sang `Request` trong quá trình khởi tạo báo cáo.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% các yêu cầu báo cáo sai số điện nước mới được tạo từ BQL phải chứa thông tin Phòng và Cơ sở.
- **SC-002**: 100% yêu cầu loại UTILITY hiển thị đúng nhãn tiếng Việt trên giao diện Danh sách và Chi tiết.
- **SC-003**: Operator có thể nhìn thấy ngay thông tin Phòng / Cơ sở trên giao diện Chi tiết thay vì "P. —".

## Assumptions

- Mã hóa đơn hoặc luồng gửi báo cáo sai số hiện tại đã có sẵn thông tin `room_id` và `facility_id` tại tầng Service/Controller để truyền xuống DAO.
- Các request `UTILITY` cũ bị thiếu `room_id` có thể chấp nhận việc không hiển thị phòng trên UI (hiển thị "—"), không bắt buộc phải viết script backfill dữ liệu.
