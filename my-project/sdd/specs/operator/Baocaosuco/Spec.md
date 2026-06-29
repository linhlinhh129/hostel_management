Ok Tú, mình cập nhật lại sang PreparedStatements theo ý bạn đây. Bạn cứ check lại kỹ xem project lần này thầy đã cho phép dùng PreparedStatement chưa nha, kẻo lại nhầm nhọt lệch mất form chuẩn mà thầy yêu cầu mọi khi đấy.

Dưới đây là bản Spec đã được chỉnh sửa lại phần kỹ thuật:

---

# Feature: Báo cáo sự cố tại hiện trường

**Status:** Draft

**Author:** Phạm Anh Tú

**Reviewer:** [Tên Reviewer]

**Date:** 2026-06-13

**Priority:** High

---

## 1. Business Context

Trong quá trình quản lý và vận hành nhà trọ, nhân viên là những người trực tiếp kiểm tra cơ sở vật chất (vệ sinh, tuần tra) và thường xuyên phát hiện các sự cố phát sinh tại hiện trường.

Việc báo cáo thủ công qua các ứng dụng tin nhắn (Zalo, Messenger) gây ra nhiều rủi ro: trôi tin nhắn, khó phân loại mức độ khẩn cấp, và Ban quản lý không có dữ liệu để theo dõi tiến độ hoặc thống kê tình trạng tài sản. Tính năng "Báo cáo sự cố" được xây dựng để nhân viên vận hành khởi tạo một Ticket báo cáo ngay lập tức trên hệ thống bằng thiết bị di động, đính kèm hình ảnh hiện trường, giúp thông tin được số hóa, lưu trữ và luân chuyển đến Ban quản lý một cách nhanh chóng, minh bạch.

---

## 2. User Stories

### Story 1 (Xác định Vị trí & Phân loại)

**As a** nhân viên vận hành,

**I want to** chỉ định rõ vị trí xảy ra sự cố (Khu vực chung hoặc Phòng cụ thể) và phân loại sự cố (Điện, Nước, An ninh...),

**so that** Ban quản lý và bộ phận bảo trì biết chính xác loại hỏng hóc, cần chuẩn bị vật tư gì và đến khu vực nào để xử lý.

### Story 2 (Mô tả & Minh chứng đa phương tiện)

**As a** nhân viên vận hành,

**I want to** nhập mô tả chi tiết và tải lên (nhiều) hình ảnh chụp tại hiện trường,

**so that** Quản lý có cái nhìn trực quan, đánh giá đúng mức độ nghiêm trọng và phê duyệt phương án sửa chữa hợp lý.

### Story 3 (Gửi & Ghi nhận báo cáo)

**As a** nhân viên vận hành,

**when** tôi hoàn tất việc điền thông tin,

**I want to** gửi báo cáo lên hệ thống,

**so that** sự cố được ghi nhận chính thức, gắn với mã nhân viên của tôi và chuyển sang trạng thái chờ xử lý (`PENDING`).

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị Form báo cáo

**WHEN** user truy cập vào chức năng "Báo cáo sự cố"

**THE SYSTEM SHALL** hiển thị một biểu mẫu nhập liệu bao gồm:

* **Cơ sở/Tòa nhà:** (Dropdown chọn tòa nhà)
* **Vị trí:** (Radio button: `Khu vực chung` / `Phòng`).
* Nếu chọn `Khu vực chung`: Hiển thị Dropdown (Hành lang, cầu thang bộ, thang máy, sân thượng, cổng chính).
* Nếu chọn `Phòng`: Hiển thị Dropdown chọn Mã phòng (`room_id`).


* **Phân loại:** (Dropdown: Điện; Nước; An ninh & Kiểm soát ra vào; Cơ sở vật chất & PCCC; Vệ sinh & Môi trường).
* **Mức độ ưu tiên:** (Dropdown: Bình thường, Khẩn cấp).
* **Mô tả chi tiết:** (Textarea).
* **Ảnh đính kèm:** (Nút upload/chụp ảnh trực tiếp, cho phép chọn nhiều ảnh).

### AC02 – Validate dữ liệu bắt buộc

**WHEN** user nhấn nút `[Gửi báo cáo]`

**AND** các trường thông tin bắt buộc (Cơ sở, Vị trí, Phân loại, Mô tả) bị bỏ trống

**THE SYSTEM SHALL** ngăn chặn hành động gửi và hiển thị text cảnh báo màu đỏ bên dưới các trường bị thiếu: "Vui lòng nhập thông tin này".

### AC03 – Tối ưu hóa Media (Tải và nén hình ảnh)

**WHEN** user chọn hình ảnh từ thư viện hoặc chụp ảnh mới bằng điện thoại

**THE SYSTEM SHALL**:

* Tự động nén ảnh ở phía Frontend trước khi gọi API để tiết kiệm băng thông 3G/4G.
* Hiển thị ảnh thu nhỏ (thumbnail) trên giao diện Form.
* Cho phép user xóa ảnh đã chọn trước khi gửi.

### AC04 – Gửi báo cáo thành công (Happy Path)

**WHEN** user điền đầy đủ dữ liệu hợp lệ và nhấn nút `[Gửi báo cáo]`

**THE SYSTEM SHALL**:

* Gọi API để lưu thông tin báo cáo sự cố vào cơ sở dữ liệu.
* Tự động lấy `staff_id` của session hiện tại lưu thành người báo cáo (Reporter).
* Gán trạng thái mặc định của sự cố là `PENDING` (Chờ xử lý).
* Hiển thị Toast thông báo: "Báo cáo sự cố thành công".
* Điều hướng user về màn hình "Danh sách sự cố" hoặc làm mới Form.

---

## 4. Technical Guidelines (Góc độ Database)

Yêu cầu kỹ thuật cốt lõi cho module này là quá trình tương tác với cơ sở dữ liệu (`INSERT`, `UPDATE`, `DELETE`) **sử dụng PreparedStatements** thay vì nối chuỗi SQL thông thường. Việc này giúp tự động escape các ký tự đặc biệt do người dùng nhập vào (như dấu nháy đơn `'` trong phần mô tả), đảm bảo an toàn dữ liệu và tối ưu hiệu suất thực thi truy vấn.

*Ví dụ logic cấu trúc câu lệnh SQL bằng PreparedStatement (Giả mã cho Backend):*

```sql
-- Sử dụng placeholder (?) để gán giá trị
INSERT INTO IncidentReports 
(
    facility_id, 
    room_id, 
    public_area,
    category, 
    priority, 
    description, 
    image_urls, 
    reported_by_staff_id, 
    status, 
    created_at
) 
VALUES 
(?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE());

```

*(Lưu ý: Backend sẽ thực hiện truyền các tham số tương ứng thông qua các method của PreparedStatement. Nếu sự cố thuộc "Khu vực chung", tham số cho `room_id` sẽ được set là `NULL` và ngược lại).*

---

## 5. Open Questions (Cần chốt với PO/Manager)

1. **Quyền thao tác của nhân viên:** Có cho phép nhân viên tự sửa/xóa Ticket báo cáo của mình trong một khoảng thời gian ngắn (15 - 30 phút) sau khi tạo không, hay chỉ Quản lý mới có quyền hủy Ticket điền sai?
2. **Hệ thống Alerting:** Với mức độ sự cố "Khẩn cấp", hệ thống có cần tích hợp API gửi thông báo khẩn (Zalo ZNS/Push Noti) ngay lập tức cho Manager không, hay chỉ cần sắp xếp lên đầu bảng theo Priority trên màn hình quản lý?