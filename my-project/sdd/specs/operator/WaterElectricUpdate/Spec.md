# Feature: Cập nhật chỉ số điện nước

**Status:** Draft  
**Author:** Tú Anh  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-11  
**Priority:** High

---

## 1. Business Context

Nhân viên vận hành thỉnh thoảng có thể nhập sai chỉ số điện nước hoặc tải nhầm ảnh minh chứng. Tính năng này cho phép nhân viên vận hành "Sửa" lại một bản ghi chỉ số điện nước ĐÃ NHẬP trước đó. 
Hệ thống sẽ lấy chính xác các dữ liệu (chỉ số, hình ảnh) của bản ghi đó hiển thị lên form để nhân viên có thể điều chỉnh lại cho đúng (thay vì nhập chỉ số mới cho tháng tiếp theo - đây là một tính năng khác).
Bên cạnh đó, tính năng này còn hỗ trợ xử lý nghiệp vụ **Thay công tơ mới** (do công tơ cũ bị hỏng) hoặc **Công tơ chạy hết vòng quay (Rollover)** khi phát hiện chỉ số mới nhập vào nhỏ hơn chỉ số kỳ trước.

---

## 2. User Stories

### Story 1 (Happy Path - Chỉnh sửa)

**As a** nhân viên vận hành,

**I want to** sửa lại thông tin của một bản ghi chỉ số điện nước đã nhập,

**so that** tôi có thể khắc phục các sai sót do nhập liệu nhầm lẫn.

### Story 2 (Hiển thị dữ liệu cần sửa)

**As a** nhân viên vận hành,

**when** tôi click vào nút "Sửa" của một bản ghi,

**I want** hệ thống hiển thị form điền sẵn toàn bộ dữ liệu hiện tại của bản ghi đó (chỉ số điện, nước, ảnh),

**so that** tôi biết mình đang sửa cái gì và sửa trên nền dữ liệu cũ.

### Story 3 (Validation & Xử lý bất thường)

**As a** nhân viên vận hành,

**when** tôi sửa chỉ số nhỏ hơn chỉ số của tháng liền kề trước đó,

**I want** hệ thống hiển thị cảnh báo và cho phép tôi chọn lý do (Nhập sai, Thay công tơ, Công tơ quay vòng),

**so that** tôi có thể xử lý các nghiệp vụ đặc biệt mà hệ thống vẫn tính đúng số lượng tiêu thụ.

### Story 4 (Thay công tơ mới)

**As a** nhân viên vận hành,

**when** tôi chọn lý do "Thay công tơ",

**I want** hệ thống hiển thị thêm ô nhập "Chỉ số chốt công tơ cũ" và "Chỉ số bắt đầu công tơ mới",

**so that** hệ thống tính toán chính xác tổng tiêu thụ dựa trên cả hai công tơ.

### Story 5 (Công tơ chạy hết vòng - Rollover)

**As a** nhân viên vận hành,

**when** tôi chọn lý do "Công tơ quay vòng",

**I want** hệ thống hiển thị thông tin "Giới hạn lớn nhất của công tơ" được cố định là 10.000,

**so that** hệ thống tự động cộng dồn chỉ số bị reset qua vòng lặp dựa trên giới hạn chuẩn của nhà trọ.

### Story 3 (Evidence)

**As a** nhân viên vận hành,

**I want to** tải lên ảnh công tơ điện và nước,

**so that** hệ thống lưu lại bằng chứng cho lần ghi nhận chỉ số.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị form điền sẵn dữ liệu

**WHEN** người dùng truy cập vào màn hình sửa chỉ số điện nước (ví dụ qua URL có chứa `meterId`)
**THE SYSTEM SHALL**
- Truy xuất bản ghi `meter_readings` tương ứng với `meterId`.
- Hiển thị form với dữ liệu đã được điền sẵn (pre-filled):
  - Số điện đã nhập của bản ghi này.
  - Số nước đã nhập của bản ghi này.
  - Ảnh minh chứng điện đã upload của bản ghi này.
  - Ảnh minh chứng nước đã upload của bản ghi này.

### AC02 – Lưu thông tin chỉnh sửa thành công

**WHEN** người dùng sửa các chỉ số hợp lệ (hoặc upload ảnh mới nếu muốn đổi ảnh) và nhấn Lưu
**THE SYSTEM SHALL**
- Cập nhật đè lên bản ghi `meter_readings` đang sửa (`meterId`).
- Thay thế ảnh công tơ mới (nếu người dùng có chọn upload ảnh mới, nếu không chọn thì giữ nguyên ảnh cũ).
- Cập nhật thời gian chỉnh sửa (`updatedAt`) và người chỉnh sửa.
**AND** trả về HTTP 200 / Redirect về trang danh sách với thông báo thành công.

### AC03 – Chỉ số điện/nước không hợp lệ (Bất thường)

**WHEN** chỉ số điện hoặc nước được sửa nhỏ hơn chỉ số của tháng liền trước đó (nếu có)
**THE SYSTEM SHALL** cảnh báo người dùng và hiển thị dropdown "Lý do chỉ số bất thường":
1. **Nhập sai:** Hiển thị lỗi, không cho phép lưu (Hành vi mặc định).
2. **Thay công tơ mới:** Hiển thị thêm 2 ô nhập liệu: `Chỉ số chốt công tơ cũ` và `Chỉ số bắt đầu công tơ mới`.
3. **Công tơ quay vòng:** Hiển thị thông báo giới hạn công tơ được cố định là 10.000.

### AC04 – Tính toán tiêu thụ khi thay công tơ

**WHEN** người dùng chọn "Thay công tơ mới" và điền đầy đủ số chốt cũ, số bắt đầu mới
**THE SYSTEM SHALL**
- Validate: `Chỉ số chốt cũ` >= `Chỉ số tháng trước`.
- Validate: `Chỉ số cuối tháng (số mới)` >= `Chỉ số bắt đầu mới`.
- Lưu trữ các thông tin này vào cơ sở dữ liệu.

### AC04b – Tính toán tiêu thụ khi công tơ quay vòng

**WHEN** người dùng chọn "Công tơ quay vòng"
**THE SYSTEM SHALL**
- Tự động gán giới hạn công tơ `maxLimit` = 100000 ở phía Backend để tránh bị thay đổi từ Frontend.
- Validate: `Chỉ số tháng trước` < 100000.
- Validate: `Chỉ số cuối tháng (số mới)` <= 99999 (tối đa 5 chữ số), và phải là số nguyên không âm (>=0, không chứa dấu thập phân).
- **Trải nghiệm người dùng (UX)**: Khi người dùng cố gắng nhập số lớn hơn `99999`, hệ thống (Frontend JS) sẽ tự động giới hạn lại số thành `99999` và hiển thị cảnh báo đỏ ngay bên dưới ô nhập: *"Chỉ số điện không được vượt quá 99999 (tối đa 5 chữ số)."*. Nếu nhập số thập phân hoặc số âm, hệ thống sẽ cảnh báo *"Chỉ số không được chứa số thập phân hoặc số âm"*.
- Lưu trữ thông tin giới hạn 100000 vào cơ sở dữ liệu.

### AC05 – Validation ảnh

**WHEN** người dùng không tải ảnh mới lên
**THE SYSTEM SHALL** giữ nguyên ảnh cũ đã có của bản ghi (không báo lỗi thiếu ảnh vì ảnh đã có sẵn từ trước).

### AC06 – Bản ghi không tồn tại

**WHEN** truyền sai `meterId` không có trong hệ thống
**THE SYSTEM SHALL** trả về thông báo lỗi "Không tìm thấy bản ghi" hoặc HTTP 404.

### AC07 – Chặn sửa khi hóa đơn đã thanh toán

**WHEN** hóa đơn tương ứng với bản ghi này đã chuyển trạng thái `PAID`
**THE SYSTEM SHALL** chặn việc chỉnh sửa và hiển thị thông báo lỗi "Không thể sửa chỉ số vì hóa đơn đã được thanh toán".

---

## 4. Giao tiếp Hệ thống (System Flow)

### Đường dẫn (Endpoint)
* **Endpoint (Hiển thị form):** `GET /operator/meter-readings/update` (nhận tham số `meterId` thay vì hoặc cùng với `roomCode`)
* **Endpoint (Xử lý cập nhật):** `POST /operator/meter-readings/update`
* **Loại dữ liệu (Content-Type):** `multipart/form-data` (form submit chứa file ảnh)

### Request Dữ liệu (GET)
* `meterId` (number, bắt buộc) để xác định chính xác bản ghi cần sửa.

### Request Form Data (POST)
* `meterId` (number, bắt buộc)
* `electric` (number, bắt buộc)
* `electricStatus` (string: NORMAL, REPLACED, ROLLOVER)
* `electricOldFinal` (number, bắt buộc nếu REPLACED)
* `electricNewStart` (number, bắt buộc nếu REPLACED)
* `electricMaxLimit` (number, bắt buộc nếu ROLLOVER)
* `water` (number, bắt buộc)
* `waterStatus` (string: NORMAL, REPLACED, ROLLOVER)
* `waterOldFinal` (number, bắt buộc nếu REPLACED)
* `waterNewStart` (number, bắt buộc nếu REPLACED)
* `waterMaxLimit` (number, bắt buộc nếu ROLLOVER)
* `electricMeterImage` (file, tùy chọn - nếu không gửi thì giữ ảnh cũ)
* `waterMeterImage` (file, tùy chọn - nếu không gửi thì giữ ảnh cũ)

### Phản hồi Hệ thống (System Response)
* **Hiển thị form thành công (GET):** Forward tới trang JSP cập nhật kèm theo dữ liệu `electric`, `water`, `electricImg`, `waterImg` đang có sẵn.
* **Cập nhật thành công (POST):** Redirect về trang danh sách.
* **Cập nhật thất bại:** Trở lại form kèm thông báo lỗi.

---

## 5. Technical Constraints

- Chỉ số điện và nước phải là số nguyên không âm.
- Chỉ số mới mặc định phải lớn hơn hoặc bằng chỉ số kỳ trước. Trừ khi Trạng thái công tơ là `REPLACED` hoặc `ROLLOVER`.
- Chỉ chấp nhận file ảnh JPG, JPEG hoặc PNG.
- Kích thước mỗi ảnh tối đa 5MB.
- Thời gian phản hồi tối đa 500ms (P95).
- Toàn bộ thao tác lưu dữ liệu phải thực hiện trong một transaction.

---

## 6. Database Mapping

### Input Form

| Trường | Kiểu dữ liệu | Bắt buộc |
|---------|-------------|----------|
| meterId | Integer | Yes (Hidden) |
| electric | Integer | Yes |
| electricStatus | String | Yes (NORMAL, REPLACED, ROLLOVER) |
| electricOldFinal | Integer | Nếu Status = REPLACED |
| electricNewStart | Integer | Nếu Status = REPLACED |
| electricMaxLimit | Integer | Nếu Status = ROLLOVER |
| water | Integer | Yes |
| waterStatus | String | Yes (NORMAL, REPLACED, ROLLOVER) |
| waterOldFinal | Integer | Nếu Status = REPLACED |
| waterNewStart | Integer | Nếu Status = REPLACED |
| waterMaxLimit | Integer | Nếu Status = ROLLOVER |
| electricMeterImage | File | No (Chỉ upload khi đổi ảnh) |
| waterMeterImage | File | No (Chỉ upload khi đổi ảnh) |

### Dữ liệu đọc tự động từ DB (Dùng để hiển thị Form Sửa)

| Trường | Mô tả |
|---------|---------|
| currentElectric | Chỉ số điện đang lưu trong DB của bản ghi này |
| currentWater | Chỉ số nước đang lưu trong DB của bản ghi này |
| currentElectricImg | Hình ảnh điện đang lưu trong DB của bản ghi này |
| currentWaterImg | Hình ảnh nước đang lưu trong DB của bản ghi này |

---

## 7. Out of Scope

- OCR tự động đọc số từ ảnh công tơ.
- Chỉnh sửa dữ liệu sau khi đã khóa kỳ hóa đơn.
- Tự động tính tiền điện nước.
- Phê duyệt nhiều cấp cho lần cập nhật chỉ số.
