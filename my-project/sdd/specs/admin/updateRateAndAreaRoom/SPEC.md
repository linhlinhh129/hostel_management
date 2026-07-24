# Feature: Cập nhật thông tin phòng (Diện tích và Giá phòng)

**Status:** Draft  
**Author:** Nhóm phát triển  
**Reviewer:** Tech Lead  
**Date:** 2026-07-13
**Priority:** Medium

## 1. Business Context

Tính năng **Cập nhật thông tin phòng** cho phép Quản trị viên (Admin) xem chi tiết một phòng trong hệ thống và trực tiếp cập nhật các thông tin cơ bản của phòng bao gồm: Diện tích (m²) và Giá phòng (VNĐ).

Mục tiêu của tính năng là giúp Quản trị viên dễ dàng điều chỉnh, bổ sung hoặc xóa bỏ các thông tin về diện tích và mức giá áp dụng cho phòng, phục vụ việc quản lý phòng và làm cơ sở cho các hoạt động tính toán, thu phí sau này.

## 2. User Stories

### Story 1 (Happy Path)
Là Quản trị viên, tôi muốn cập nhật diện tích và giá phòng của một phòng đang hoạt động bình thường, để thông tin phòng được phản ánh chính xác nhất trên hệ thống.

### Story 2 (Happy Path)
Là Quản trị viên, tôi muốn để trống các trường diện tích hoặc giá phòng khi cập nhật, để hệ thống hiểu rằng các giá trị này chưa được thiết lập hoặc muốn xóa giá trị cũ đi.

### Story 3 (Edge Case)
Là Quản trị viên, tôi không muốn cập nhật được thông tin phòng nếu cơ sở chứa phòng đó đã bị vô hiệu hóa (INACTIVE), để đảm bảo dữ liệu không bị thay đổi trong một cơ sở ngừng hoạt động.

## 3. Acceptance Criteria (EARS)

### 3.1 Xem chi tiết phòng
WHEN Quản trị viên truy cập trang chi tiết phòng hợp lệ
THE SYSTEM SHALL hiển thị thông tin chi tiết của phòng và biểu mẫu để chỉnh sửa Diện tích, Giá phòng (nếu cơ sở đang hoạt động).

### 3.2 Cập nhật thông tin phòng hợp lệ
WHEN Quản trị viên gửi biểu mẫu cập nhật với giá trị diện tích và giá phòng là số dương hoặc để trống
THE SYSTEM SHALL cập nhật thông tin thành công và hiển thị thông báo "Cập nhật thông tin phòng thành công."

### 3.3 Xóa thông tin (để trống)
WHEN Quản trị viên để trống ô diện tích hoặc giá phòng và lưu thay đổi
THE SYSTEM SHALL cập nhật giá trị của trường tương ứng trong cơ sở dữ liệu thành rỗng (null).

### 3.4 Cơ sở vô hiệu hóa
WHILE trạng thái của cơ sở chứa phòng là `INACTIVE`
THE SYSTEM SHALL ẩn biểu mẫu cập nhật trên giao diện và hiển thị cảnh báo "Cơ sở đã bị vô hiệu hoá. Không thể chỉnh sửa thông tin phòng."

WHEN Quản trị viên cố tình gửi request POST cập nhật cho phòng thuộc cơ sở `INACTIVE`
THE SYSTEM SHALL từ chối cập nhật và trả về thông báo lỗi "Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng."

### 3.5 Kiểm tra tính hợp lệ dữ liệu
WHEN Quản trị viên nhập diện tích hoặc giá phòng là số âm, hoặc ký tự không hợp lệ
THE SYSTEM SHALL từ chối cập nhật và hiển thị thông báo lỗi "Diện tích không được âm." hoặc "Giá phòng không được âm." hoặc "[Trường] không hợp lệ."

## 4. Servlet Contract

### 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `AdminRoomServlet` |
| **URL Pattern** | `GET /admin/rooms/{roomId}` — hiển thị chi tiết |
| **URL Pattern** | `POST /admin/rooms/{roomId}/update` — lưu cập nhật |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `BaseServlet`) |

---

### 4.2 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `room` | `Map<String, Object>` | `RoomDAO.findDetailForAdmin(roomId)` | Thông tin chi tiết phòng (bao gồm mã, diện tích, giá, tenant) |
| `success` | `String` | Flash attribute | Thông báo khi cập nhật thành công |
| `error` | `String` | Flash/Request attribute | Thông báo lỗi khi cập nhật thất bại |

---

### 4.3 Request Parameters — POST update

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `area` | `String` | Diện tích phòng. Có thể rỗng. Chuyển thành `BigDecimal`. |
| `roomFee` | `String` | Giá phòng. Có thể rỗng. Chuyển thành `BigDecimal`. |

---

### 4.4 Validation — POST update

| Form param | Điều kiện hợp lệ | Lỗi ném ra |
|---|---|---|
| `area` | Không được âm, đúng định dạng số | `ValidationException` ("Diện tích không hợp lệ/không được âm") |
| `roomFee` | Không được âm, đúng định dạng số | `ValidationException` ("Giá phòng không hợp lệ/không được âm") |
| `facilityStatus` | Cơ sở chứa phòng phải khác `INACTIVE` | `ValidationException` ("Cơ sở đã bị vô hiệu hóa...") |

---

### 4.5 Xử lý lỗi và Hành vi đặc biệt

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập / Sai Role | Redirect về `/login` hoặc HTTP 403 (xử lý bởi `BaseServlet`) |
| Phòng không tồn tại | Ném `NotFoundException` → HTTP 404 |
| Validation thất bại | Flash message `error`, redirect về `/admin/rooms/{roomId}` (hoặc forward lại detail) |
| Cập nhật thành công | Flash message `success`, redirect về `/admin/rooms/{roomId}` (Post-Redirect-Get) |

## 5. Technical Constraints

- Chỉ Quản trị viên (Admin) mới có quyền sử dụng chức năng cập nhật.
- Giao diện phải vô hiệu hóa form ngay lập tức nếu dữ liệu trả về cho thấy `facilityStatus` là `INACTIVE`.
- Giá trị diện tích và giá phòng phải lưu bằng kiểu dữ liệu Decimal/Numeric có độ chính xác cao trong DB để tránh sai số thập phân.
