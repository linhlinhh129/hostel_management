# CONTEXT.md [Quản lý chỉ số điện nước]

**Người viết:** Nguyễn Văn A  
**Ngày:** 2026-06-11

---

## 1. PROBLEM STATEMENT

### Bối cảnh

Việc quản lý chỉ số điện nước tại các phòng hiện nay gặp nhiều khó khăn trong khâu nhập liệu, kiểm tra và theo dõi tiến độ cập nhật. Nhân viên vận hành cần vừa ghi nhận chỉ số điện nước mới, vừa theo dõi tình trạng cập nhật của toàn bộ các phòng trong kỳ hiện tại để đảm bảo quá trình lập hóa đơn diễn ra chính xác và đúng thời hạn.

### Nỗi đau (Pain Points)

- Nhân viên vận hành phải tra cứu thủ công chỉ số kỳ trước trước khi nhập dữ liệu mới.
- Dễ xảy ra sai sót khi nhập nhầm chỉ số điện hoặc nước.
- Thiếu hình ảnh minh chứng để đối chiếu khi người thuê khiếu nại hóa đơn.
- Không có màn hình tổng hợp giúp theo dõi phòng nào đã cập nhật hoặc chưa cập nhật trong kỳ hiện tại.
- Việc kiểm tra tiến độ cập nhật trên số lượng lớn phòng tốn nhiều thời gian và dễ bỏ sót.

---

## 2. DOMAIN KNOWLEDGE

### Chỉ số kỳ trước (Previous Reading)

Là chỉ số điện (kWh) hoặc nước (m³) được ghi nhận ở kỳ gần nhất trước đó. Đây là cơ sở để tính lượng tiêu thụ của kỳ hiện tại.

### Chỉ số mới (Current Reading)

Là số liệu được ghi nhận từ công tơ tại thời điểm kiểm tra.

Giá trị này phải lớn hơn hoặc bằng chỉ số kỳ trước, trừ trường hợp công tơ được thay mới hoặc reset.

### Công tơ (Meter)

Thiết bị dùng để đo lượng điện hoặc nước tiêu thụ.

### Trạng thái cập nhật

Thể hiện tiến độ ghi nhận chỉ số điện nước của phòng trong kỳ hiện tại.

- Chưa cập nhật
- Đã cập nhật

### Thời gian cập nhật (Timestamp)

Thời điểm hệ thống ghi nhận thành công dữ liệu vào cơ sở dữ liệu.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (End-user)

Người trực tiếp thực hiện việc cập nhật chỉ số điện nước và theo dõi tiến độ cập nhật của các phòng.

### Manager

Theo dõi tình trạng cập nhật dữ liệu điện nước của toàn bộ hệ thống và đảm bảo việc lập hóa đơn đúng thời hạn.

### Người thuê

Là đối tượng chịu ảnh hưởng trực tiếp từ dữ liệu điện nước được ghi nhận và cần có tính minh bạch khi phát sinh tranh chấp.

### Đội ngũ Phát triển (Developers)

Thiết kế cơ sở dữ liệu, API và hệ thống lưu trữ ảnh phục vụ việc quản lý chỉ số điện nước.

---

## 4. CONSTRAINTS

### Ràng buộc nghiệp vụ

- Nhân viên vận hành bắt buộc phải nhập đầy đủ chỉ số điện mới và chỉ số nước mới.
- Bắt buộc phải tải lên:
  - 01 ảnh công tơ điện.
  - 01 ảnh công tơ nước.
- Không cho phép lưu dữ liệu khi thiếu ảnh minh chứng.
- Không cho phép người dùng chỉnh sửa trực tiếp chỉ số kỳ trước.
- Chỉ số kỳ trước phải được hệ thống tự động lấy từ cơ sở dữ liệu.
- Sau khi lưu thành công:
  - Hệ thống tự động ghi nhận thời gian cập nhật.
  - Hệ thống tự động chuyển trạng thái sang "Đã cập nhật".

### Ràng buộc dữ liệu

- Mỗi phòng chỉ được có một bản ghi điện nước cho một kỳ.
- Trạng thái chỉ được hiển thị dưới hai giá trị:
  - Chưa cập nhật
  - Đã cập nhật

### Ràng buộc kỹ thuật

- Mỗi ảnh tối đa 5MB.
- Hệ thống phải hỗ trợ upload ảnh từ thiết bị di động.
- Màn hình danh sách chỉ cho phép xem dữ liệu (Read-only).
- Dữ liệu danh sách phải được phân trang khi số lượng phòng lớn.

---

## 5. ASSUMPTIONS

### Giả định 1

Hệ thống đã có sẵn danh sách phòng hợp lệ để người dùng lựa chọn.

### Giả định 2

Đối với phòng mới chưa từng có dữ liệu điện nước:

- Chỉ số kỳ trước mặc định bằng 0.
- Hoặc sử dụng giá trị khởi tạo được cấu hình khi bàn giao phòng.

### Giả định 3

Thiết bị của nhân viên vận hành có kết nối Internet ổn định khi tải ảnh lên hệ thống.

### Giả định 4

Danh sách mặc định hiển thị dữ liệu của kỳ hiện tại (tháng hiện tại/năm hiện tại).

---

## 6. OPEN QUESTIONS

### Câu hỏi 1

Có cần tích hợp AI/OCR để tự động nhận diện chỉ số từ ảnh công tơ nhằm giảm thao tác nhập liệu không?

### Câu hỏi 2

Có cần chặn ngay tại giao diện khi:

- Chỉ số điện mới < Chỉ số điện kỳ trước?
- Chỉ số nước mới < Chỉ số nước kỳ trước?

### Câu hỏi 3

Định dạng ảnh nào được chấp nhận?

- JPG
- PNG
- WEBP

Và ảnh sẽ được:

- Lưu trực tiếp trong Database?
- Hay lưu trên Cloud Storage và chỉ lưu URL?

### Câu hỏi 4

Có cần hỗ trợ tìm kiếm và lọc theo:

- Mã phòng
- Trạng thái cập nhật
- Tháng/Năm

để phục vụ việc quản lý số lượng lớn phòng không?

### Câu hỏi 5

Có cần hỗ trợ xuất dữ liệu Excel trong các giai đoạn tiếp theo hay không?
