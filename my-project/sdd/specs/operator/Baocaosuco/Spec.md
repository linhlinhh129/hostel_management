Dưới đây là bản đặc tả (Spec) chi tiết cho **Feature: Báo cáo sự cố**, được xây dựng độc lập và tập trung hoàn toàn vào luồng nhân viên vận hành chủ động ghi nhận lỗi tại hiện trường.

---

# Feature: Báo cáo sự cố tại hiện trường

**Status:** Draft

**Author:** Phạm Anh Tú

**Reviewer:** [Tên Reviewer]

**Date:** 2026-06-13

**Priority:** High

---

## 1. Business Context

Trong quá trình quản lý và vận hành nhà trọ, nhân viên thường xuyên đi kiểm tra cơ sở vật chất và có thể phát hiện các sự cố phát sinh (như cháy bóng đèn hành lang, rò rỉ nước, hỏng khóa cổng, v.v.).

Tính năng "Báo cáo sự cố" cho phép nhân viên vận hành khởi tạo một báo cáo ngay lập tức trên hệ thống bằng thiết bị di động, kèm theo hình ảnh hiện trường. Điều này giúp loại bỏ việc báo cáo thủ công qua tin nhắn (dễ trôi rớt), đồng thời giúp Quản lý nắm bắt ngay lập tức tình trạng cơ sở vật chất để điều phối sửa chữa kịp thời.

---

## 2. User Stories

### Story 1 (Chọn vị trí và phân loại)

**As a** nhân viên vận hành,

**I want to** chỉ định rõ vị trí xảy ra sự cố (Khu vực chung hoặc Phòng cụ thể) và phân loại sự cố (Điện, Nước, An ninh...),

**so that** bộ phận bảo trì biết chính xác cần mang theo vật tư gì và đến đâu để sửa chữa.

### Story 2 (Mô tả và Đính kèm minh chứng)

**As a** nhân viên vận hành,

**I want to** nhập mô tả chi tiết và tải lên các hình ảnh chụp tại hiện trường,

**so that** Quản lý có thể đánh giá được mức độ nghiêm trọng và phê duyệt chi phí sửa chữa hợp lý.

### Story 3 (Gửi báo cáo)

**As a** nhân viên vận hành,

**when** tôi hoàn tất việc điền thông tin,

**I want to** gửi báo cáo lên hệ thống,

**so that** sự cố được ghi nhận chính thức và chuyển sang trạng thái chờ xử lý.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị Form báo cáo

**WHEN** user truy cập vào chức năng "Báo cáo sự cố"

**THE SYSTEM SHALL** hiển thị một biểu mẫu nhập liệu bao gồm:

* **Cơ sở/Tòa nhà:** (Dropdown chọn tòa nhà)
* **Vị trí:** (Radio button: `Khu vực chung` / `Phòng`. Nếu chọn `Phòng`, hiển thị thêm dropdown chọn Mã phòng)
* **Phân loại:** (Dropdown: Điện, Nước, Nội thất, Vệ sinh, Khác)
* **Mức độ ưu tiên:** (Dropdown: Bình thường, Khẩn cấp)
* **Mô tả chi tiết:** (Textarea)
* **Ảnh đính kèm:** (Nút upload/chụp ảnh trực tiếp)

### AC02 – Validate dữ liệu bắt buộc

**WHEN** user nhấn nút `[Gửi báo cáo]`

**AND** các trường thông tin bắt buộc (Cơ sở, Vị trí, Phân loại, Mô tả) bị bỏ trống

**THE SYSTEM SHALL** - Ngăn chặn hành động gửi.

* Hiển thị text cảnh báo màu đỏ bên dưới các trường bị thiếu: "Vui lòng nhập thông tin này".

### AC03 – Tải và nén hình ảnh

**WHEN** user chọn hình ảnh từ thư viện hoặc chụp ảnh mới

**THE SYSTEM SHALL** - Tự động nén ảnh ở phía Frontend (để giảm dung lượng tối đa).

* Hiển thị ảnh thu nhỏ (thumbnail) trên giao diện Form.
* Cho phép user xóa ảnh đã chọn trước khi gửi.

### AC04 – Gửi báo cáo thành công (Happy Path)

**WHEN** user điền đầy đủ dữ liệu hợp lệ và nhấn nút `[Gửi báo cáo]`

**THE SYSTEM SHALL** - Gọi API để lưu thông tin báo cáo sự cố vào cơ sở dữ liệu.

* Tự động lấy `staff_id` của user đang đăng nhập lưu thành người báo cáo (Reporter).
* Gán trạng thái mặc định của sự cố là `PENDING` (Chờ xử lý).
* Hiển thị Toast/Thông báo: "Báo cáo sự cố thành công".
* Điều hướng user về màn hình "Danh sách sự cố" hoặc làm mới Form.

---

## 4. Technical Guidelines (Góc độ Database)

Vì dự án yêu cầu việc tương tác với cơ sở dữ liệu phải dùng **Basic SQL Statements** (ghép chuỗi thông thường) thay vì PreparedStatements, khi thực hiện lệnh `INSERT` báo cáo sự cố mới, Backend cần đảm bảo nối chuỗi chính xác và xử lý escape các ký tự đặc biệt (ví dụ: dấu nháy đơn `'` trong nội dung mô tả) để tránh lỗi cú pháp SQL.

*Ví dụ logic SQL để lưu báo cáo (Giả mã cho Backend):*

```sql
-- Cần xử lý escape chuỗi mô tả (ví dụ: thay ' thành '') trước khi nối chuỗi
INSERT INTO IncidentReports 
(
    facility_id, 
    room_id, 
    category, 
    priority, 
    description, 
    image_urls, 
    reported_by_staff_id, 
    status, 
    created_at
) 
VALUES 
(
    'FCL_001', 
    'RM_102', 
    'WATER', 
    'NORMAL', 
    N'Vỡ ống nước dưới bồn rửa mặt, nước rỉ liên tục', 
    'https://cdn.domain.com/img1.jpg,https://cdn.domain.com/img2.jpg', 
    'STF_999', 
    'PENDING', 
    GETDATE()
);

```

*(Lưu ý: Nếu vị trí là "Khu vực chung", giá trị của trường `room_id` khi nối chuỗi sẽ để là `NULL` hoặc rỗng tùy theo thiết kế DB).*