# CONTEXT.md - Feature Chi tiết yêu cầu sửa chữa

**Người viết:** Phạm Anh Tú  
**Ngày:** 2026-06-11

---

## 1. PROBLEM STATEMENT

### Thiếu thông tin đánh giá
Nhân viên vận hành cần nắm rõ tình trạng thực tế của sự cố (qua mô tả chi tiết và hình ảnh) trước khi tiếp nhận và điều phối xử lý.

Nếu thông tin không đầy đủ hoặc thiếu chính xác, việc phân công nguồn lực có thể bị chậm trễ hoặc sai lệch.

### Nút thắt trong phân bổ công việc
Cần có sự minh bạch về trách nhiệm.

Hệ thống và cấp quản lý phải biết chính xác một sự cố đang ở trạng thái:

- `Pending`
- `Accepted`
- `Rejected`

để kịp thời điều phối.

---

## 2. DOMAIN KNOWLEDGE

### Concurrency (Tranh chấp đồng thời)
Trạng thái xảy ra khi hai hoặc nhiều nhân viên vận hành cùng mở một yêu cầu và nhấn nút **"Nhận yêu cầu"** ở cùng một thời điểm.

Hệ thống cần có cơ chế kiểm soát lỗi này để đảm bảo một sự cố không bị giao cho nhiều người.

### Lightbox (Trình xem ảnh)
Thành phần giao diện (UI) cho phép người dùng nhấp vào một hình ảnh thu nhỏ (thumbnail) để xem ảnh ở kích thước đầy đủ trên một lớp phủ nền tối mà không cần chuyển trang.

### Quyền Read-only (Chỉ đọc)
Nguyên tắc bảo toàn dữ liệu gốc. Nhân viên tiếp nhận chỉ có thể xem mà không thể chỉnh sửa, xóa bỏ hay làm sai lệch nội dung khiếu nại/sự cố do người dùng gửi lên.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (Operations Staff)
Người dùng trực tiếp, tiếp nhận yêu cầu và thực hiện điều phối xử lý dựa trên thông tin được cung cấp.

### Người gửi yêu cầu (Requester)
Được hưởng lợi gián tiếp vì sự cố của họ được đánh giá và tiếp nhận xử lý nhanh chóng, minh bạch.

### Manager
Theo dõi luồng phân bổ công việc và xử lý các trường hợp sự cố bị nhân viên từ chối tiếp nhận.

---

## 4. CONSTRAINTS

### Ràng buộc Kỹ thuật (Technical Constraints)

#### Hiệu năng
Frontend bắt buộc phải tối ưu hóa việc tải hình ảnh bằng cách sử dụng ảnh thu nhỏ (thumbnails) ở màn hình xem trước.

#### Xử lý Concurrency dưới Database
Backend phải bắt và xử lý triệt để race condition khi cập nhật trạng thái.

Khi thực thi các câu lệnh cập nhật dữ liệu (`UPDATE`), bắt buộc phải tuân thủ việc viết code bằng **Basic SQL Statements** theo chuẩn hiện tại của dự án, đảm bảo ràng buộc kiểm tra trạng thái cũ trước khi ghi đè trạng thái mới.

#### Toàn vẹn luồng nghiệp vụ
Hành động **"Từ chối"** bắt buộc phải đi kèm dữ liệu `reject_reason`.

### Ràng buộc Phạm vi (Scope Constraints)

#### Nằm ngoài phạm vi
- Không cho phép chỉnh sửa nội dung yêu cầu gốc.
- Không bao gồm các thao tác cập nhật tiến độ công việc.

---

## 5. ASSUMPTIONS

- Giả định rằng hệ thống API trả về mảng `images` đã là các đường dẫn URL hợp lệ từ một máy chủ lưu trữ (CDN/Cloud Storage).
- Thiết bị di động/web của nhân viên có thể truy cập trực tiếp các URL này mà không cần thông qua token trung gian.
- Giả định rằng Frontend đã có sẵn các component chuẩn như:
  - Image Lightbox
  - Modal/Popup nhập lý do từ chối

  để tái sử dụng, giúp đảm bảo constraint về thời gian tải.

---

## 6. OPEN QUESTIONS

### Luồng sau khi Từ chối
Khi nhân viên nhấn **"Từ chối"**, trạng thái của yêu cầu sẽ thay đổi thành gì?

- Quay lại trạng thái `Pending` để người khác nhận?
- Hay chuyển sang `Rejected` để báo cáo cho Manager điều phối lại?

### Giới hạn số lượng hiển thị ảnh
Nếu người gửi yêu cầu đính kèm số lượng lớn hình ảnh (ví dụ 10–20 ảnh):

- Hiển thị tất cả?
- Hay chỉ hiển thị 3–4 ảnh đầu tiên và cung cấp nút **"Xem tất cả"**?

### Quyền thu hồi quyết định
Nếu nhân viên vận hành lỡ nhấn **"Nhận yêu cầu"**:

- Có được hủy việc tiếp nhận hay không?
- Hay cần thực hiện một quy trình điều phối/chuyển giao trách nhiệm khác?

---