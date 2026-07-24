# Feature: Quản lý chỉ số điện nước

**Status:** Draft  
**Author:** [Tên Của Bạn / Agent]  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-07-24  
**Priority:** High

---

## 1. Business Context

Để đảm bảo quy trình thu phí hàng tháng diễn ra chính xác và minh bạch, nhân viên vận hành cần một công cụ chuyên dụng để theo dõi, cập nhật chỉ số điện nước và lưu trữ hình ảnh minh chứng cho từng phòng. Việc số hóa quy trình này giúp giảm thiểu sai sót do ghi chép thủ công, đồng thời cung cấp bằng chứng rõ ràng giải quyết các khiếu nại của người thuê.

---

## 2. User Stories

### Story 1 (Xem danh sách tháng hiện tại)
**As a** nhân viên vận hành,  
**I want to** xem danh sách các phòng thuộc cơ sở mình quản lý kèm theo trạng thái cập nhật điện nước của tháng hiện tại  
**so that** tôi biết phòng nào đã ghi chỉ số, phòng nào chưa để tiến hành đi kiểm tra.

### Story 2 (Cập nhật chỉ số)
**As a** nhân viên vận hành,  
**when** tôi kiểm tra công tơ điện nước tại phòng,  
**I want to** nhập số điện/nước mới và tải lên hình ảnh minh chứng  
**so that** hệ thống lưu lại mức tiêu thụ của phòng để phục vụ việc tính toán hóa đơn.

### Story 3 (Xem lịch sử)
**As a** nhân viên vận hành,  
**when** cần tra cứu hoặc giải đáp thắc mắc của người thuê,  
**I want to** xem lại lịch sử ghi chỉ số điện nước của các tháng trước theo từng cơ sở và mã phòng  
**so that** tôi có căn cứ đối chiếu và kiểm tra tính liên tục của dữ liệu.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách điện nước tháng hiện tại
**WHEN** user truy cập vào trang "Danh sách điện nước" (`/operator/meter-readings`)  
**THE SYSTEM SHALL**
- Hiển thị danh sách các phòng thuộc cơ sở user quản lý.
- Hiển thị trạng thái cập nhật trong tháng hiện tại: "CHƯA CẬP NHẬT" hoặc "ĐÃ CẬP NHẬT".
- Hỗ trợ lọc theo Mã phòng và Cơ sở.

### AC02 – Validation cập nhật chỉ số
**WHEN** user nhập chỉ số điện mới hoặc nước mới  
**THE SYSTEM SHALL**
- Kiểm tra số mới không được nhỏ hơn số cũ (của tháng liền trước).
- Nếu không hợp lệ, chặn lưu và hiển thị thông báo lỗi rõ ràng (ví dụ: "Số mới không được nhỏ hơn số cũ").

### AC03 – Yêu cầu hình ảnh minh chứng
**WHEN** user submit form cập nhật chỉ số  
**THE SYSTEM SHALL**
- Bắt buộc phải có file hình ảnh tải lên cho cả công tơ điện và công tơ nước.
- Trả về lỗi nếu thiếu ảnh.

### AC04 – Xử lý cập nhật thành công
**WHEN** user nhập số liệu hợp lệ và có đầy đủ hình ảnh  
**THE SYSTEM SHALL**
- Lưu dữ liệu vào hệ thống (INSERT nếu là lần đầu trong tháng, UPDATE nếu sửa lại của tháng hiện tại).
- Lưu file hình ảnh vào thư mục cấu hình và gắn URL vào record.
- Ghi nhận Audit Log (lưu lại thay đổi chỉ số cũ/mới).
- Chuyển hướng về màn hình danh sách và hiển thị thông báo thành công.

### AC05 – Tra cứu lịch sử
**WHEN** user truy cập trang "Lịch sử điện nước" (`/operator/meter-readings/history`)  
**THE SYSTEM SHALL**
- Hiển thị danh sách chỉ số đã ghi nhận trong quá khứ.
- Cho phép lọc theo Tháng, Năm, Cơ sở và Mã phòng.

---

## 4. API Contract / Endpoints (Server-side rendered)

Vì tính năng này chủ yếu dùng Servlet để render JSP, thay vì API trả JSON, đây là danh sách các route tương tác:

| Endpoint | Method | Chức năng | Tham số (Query/Form Data) |
|----------|--------|-----------|----------------------------|
| `/operator/meter-readings` | GET | Hiển thị danh sách tháng hiện tại | `facility`, `roomCode` |
| `/operator/meter-readings/update` | GET | Render form cập nhật / sửa chỉ số | `roomCode` |
| `/operator/meter-readings/update` | POST | Xử lý cập nhật dữ liệu và upload ảnh | `roomCode`, `newElectric`, `newWater`, file `electricMeterImage`, file `waterMeterImage` |
| `/operator/meter-readings/history` | GET | Hiển thị lịch sử ghi nhận theo tháng/năm | `facility`, `roomCode`, `month`, `year` |

---

## 5. Technical Constraints

- **File Upload Limits:** Mỗi ảnh không vượt quá 5MB. Tổng dung lượng request không vượt quá 25MB.
- **Audit Logging:** Thay đổi chỉ số cần được gọi qua `AuditLogHelper.log` với thông tin tường minh (số cũ, số mới, hành động INSERT/UPDATE).
- **Data Validation:** Xử lý ngoại lệ `NumberFormatException` trong trường hợp dữ liệu nhập vào bị can thiệp thành dạng không phải số, đảm bảo không sập ứng dụng.

---

## 6. Out of Scope

- Tự động nhận diện số điện nước từ hình ảnh (OCR) để điền vào form (có thể cân nhắc cho giai đoạn sau).
- Tính toán hóa đơn tự động trực tiếp trên màn hình này (logic tính tiền nằm ở module Quản lý hóa đơn / Hợp đồng).
