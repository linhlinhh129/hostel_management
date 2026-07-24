# CONTEXT.md [Quản lý chỉ số điện nước]

**Người viết:** [Tên Của Bạn / Agent]  
**Ngày:** 2026-07-24

---

## 1. PROBLEM STATEMENT

Hàng tháng, nhân viên vận hành cần ghi nhận chỉ số điện nước cho từng phòng để tính toán chi phí (hóa đơn) cho người thuê. Quá trình này cần diễn ra chính xác, minh bạch và có minh chứng rõ ràng (hình ảnh) để tránh các tranh chấp về sau. Nếu không có một công cụ quản lý trạng thái ghi chỉ số, nhân viên vận hành dễ bỏ sót phòng, ghi sai lệch số liệu hoặc không có bằng chứng khi người thuê thắc mắc, gây ảnh hưởng đến uy tín và tiến độ thu phí của hệ thống.

---

## 2. DOMAIN KNOWLEDGE

### Chỉ số điện nước (Meter Reading)
Số liệu ghi nhận từ công tơ điện và đồng hồ nước tại một thời điểm cụ thể (thường là hàng tháng) cho từng phòng.

### Minh chứng (Proof)
Hình ảnh chụp công tơ điện/nước thực tế tại thời điểm ghi nhận, dùng để đối chiếu khi có sai sót hoặc khiếu nại từ người thuê.

### Trạng thái ghi nhận (Status)
Trong một tháng cụ thể, trạng thái ghi chỉ số của một phòng:
- `CHƯA CẬP NHẬT`: Chưa ghi nhận chỉ số cho tháng hiện tại.
- `ĐÃ CẬP NHẬT`: Đã có dữ liệu chỉ số và hình ảnh minh chứng cho tháng hiện tại.

### Lịch sử (History)
Dữ liệu chỉ số các tháng trước đó, dùng để làm cơ sở (số cũ) tính toán tiêu thụ cho tháng hiện tại và phục vụ tra cứu.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (End-user trực tiếp)
Người trực tiếp đi kiểm tra các phòng, sử dụng tính năng này để nhập số điện/nước mới và tải lên hình ảnh minh chứng. Theo dõi danh sách phòng nào đã/chưa cập nhật trong tháng.

### Manager
Người quản lý chung, cần đảm bảo toàn bộ các phòng đều được ghi nhận chỉ số đầy đủ và chính xác mỗi tháng để tiến hành lập hóa đơn.

### Người thuê (Tenant)
Người hưởng lợi gián tiếp, được đảm bảo tính minh bạch nhờ vào hình ảnh minh chứng và quy trình ghi nhận chính xác, không sợ bị tính dư số điện nước.

---

## 4. CONSTRAINTS

### Business

- Chỉ số điện và nước mới nhập vào **không được nhỏ hơn** chỉ số cũ của tháng liền trước đó.
- Bắt buộc phải có hình ảnh minh chứng cho cả công tơ điện và nước khi cập nhật.
- Chỉ những phòng có mã phòng hợp lệ và đang ở trạng thái thuê (hoặc đã có dữ liệu trước đó) mới cho phép cập nhật.

### Tech (Performance)

- Tối ưu việc truy xuất hình ảnh và hiển thị danh sách phòng, đảm bảo không quá tải khi số lượng phòng lớn.
- Hỗ trợ client-side pagination trên danh sách phòng.

### Tech (Architecture)

- Tách biệt các màn hình: Danh sách tháng hiện tại, Cập nhật, và Lịch sử.
- Phải ghi nhận lại thay đổi qua hệ thống Audit Log (INSERT/UPDATE) để truy vết.

### Tech (Database)

- Lưu trữ URL ảnh minh chứng một cách hệ thống trong thư mục `uploads/meter_readings`.

---

## 5. ASSUMPTIONS

### Authentication & Authorization

- Hệ thống phân quyền hoạt động chuẩn xác, nhân viên vận hành chỉ xem và thao tác trên các cơ sở (facility) mà họ được phân công quản lý (`operatorId`).

### Data Continuity

- Tháng đầu tiên hệ thống hoạt động hoặc phòng mới thuê sẽ lấy số cũ mặc định là `0` nếu chưa từng có dữ liệu trước đó.
- Việc ghi nhận chỉ số được thực hiện mỗi tháng một lần cho mỗi phòng. Tuy nhiên, trong cùng một tháng, nhân viên vẫn có thể "Sửa" lại chỉ số (update đè lên record hiện tại của tháng đó) nếu phát hiện sai sót.

---

## 6. OPEN QUESTIONS

### 1. Retention Policy cho hình ảnh minh chứng
Hình ảnh minh chứng tốn khá nhiều dung lượng lưu trữ, hệ thống có cần quy định thời gian lưu trữ hình ảnh (ví dụ: tự động xóa ảnh sau 6 tháng/1 năm) hay giữ vô thời hạn?

### 2. Mobile / Tablet Optimization
Do nhân viên vận hành thường sử dụng điện thoại/tablet khi đi ghi số trực tiếp tại phòng, tính năng upload file có cần tối ưu nén ảnh trực tiếp ở phía client để giảm dung lượng mạng và storage không?
