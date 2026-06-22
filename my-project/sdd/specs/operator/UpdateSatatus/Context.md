# CONTEXT.md Feature Cập nhật trạng thái sửa chữa

# Người viết: @Phạm Anh Tú | Ngày: 2026-06-19 (Cập nhật để đồng bộ CSDL)

## 1. PROBLEM STATEMENT

### Thiếu minh chứng thực tế trong nghiệm thu

Khó khăn trong việc xác thực kết quả xử lý sự cố tại hiện trường, dẫn đến rủi ro nhân viên vận hành báo cáo sai tiến độ, đánh giá KPI không chính xác và làm giảm chất lượng quản lý cơ sở vật chất.

### Thiếu dữ liệu xác nhận hoàn thành

Khi yêu cầu sửa chữa được chuyển sang trạng thái hoàn thành nhưng không có ghi chú hoặc hình ảnh minh chứng, quản lý vận hành khó xác nhận công việc đã được xử lý thực tế và khó đối chiếu khi phát sinh khiếu nại sau này.

---

## 2. DOMAIN KNOWLEDGE

### Visual Evidence (Minh chứng hình ảnh)

Là hình ảnh được chụp sau khi hoàn thành việc sửa chữa.
Đây là bằng chứng trực quan chứng minh sự cố đã được xử lý thành công.

### Repair Completion

Là hành động cập nhật kết quả xử lý cho một yêu cầu sửa chữa đã được tiếp nhận (từ trạng thái Đang xử lý sang Hoàn thành).

Thông tin bắt buộc bao gồm:

* Ghi chú kết quả xử lý.
* Hình ảnh sau sửa chữa.
* Thời gian hoàn thành (tự động ghi nhận).

### Multipart/Form-Data

Định dạng dữ liệu bắt buộc trên file `.jsp` khi gửi tệp đính kèm (file ảnh) từ giao diện client lên backend Java Servlet.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành

Người trực tiếp xử lý sự cố và tải minh chứng (ảnh + ghi chú) lên hệ thống.

### Quản lý vận hành

Theo dõi kết quả xử lý, kiểm tra ảnh hoàn thành và đánh giá hiệu quả vận hành.

### Người gửi yêu cầu (Tenant)

Được thông báo hoặc có thể xem được kết quả khi yêu cầu chuyển sang trạng thái hoàn thành để kiểm tra lại hiện trạng thiết bị.

---

## 4. CONSTRAINTS

### Ràng buộc kỹ thuật tối cao (KHÔNG SỬA CSDL)

Theo quy định bắt buộc, tính năng phải được triển khai mà **tuyệt đối không làm thay đổi cấu trúc Database hiện tại**.
Do bảng `requests` không có sẵn các trường chuyên biệt cho việc hoàn thành, ta sử dụng chiến thuật "Workaround" ánh xạ dữ liệu:
* **Ghi chú hoàn thành**: Được lưu vào trường `rejection_reason`.
* **Ảnh minh chứng**: Được lưu dưới dạng chuỗi phân cách dấu phẩy vào trường `attachment_urls2` (tách biệt với ảnh của Tenant ở `attachment_urls1`).
* **Thời gian hoàn thành**: Cập nhật thông qua lệnh tự động `GETDATE()` vào trường `updated_at`.

### File Validation

* Chỉ chấp nhận định dạng ảnh:
  * JPG
  * JPEG
  * PNG
* Dung lượng tối đa: 5MB/ảnh.
* Số lượng ảnh tối đa: 5 ảnh.

### Date Constraint

* Quá trình cập nhật thời gian hoàn thành hoàn toàn diễn ra tự động ở tầng Database (SQL `GETDATE()`). Vận hành viên không được phép chọn ngày giờ để đảm bảo tính minh bạch.

### Ràng buộc phạm vi

Không bao gồm:

* Chữ ký điện tử của người gửi yêu cầu.
* Chức năng đánh giá sao hay khảo sát mức độ hài lòng.
* Luồng bảo trì định kỳ.

---

## 5. ASSUMPTIONS

### Giả định 1

Hệ thống có một thư mục lưu trữ cục bộ (Local Storage) ở trên server (vd: `/uploads/requests/`) có quyền ghi (write permission) để Servlet có thể tiến hành lưu file vật lý.

### Giả định 2

Tính năng được tích hợp theo luồng Form Submit truyền thống (JSP + Servlet) thay vì REST API. Form gửi lên sẽ có method POST và thuộc tính `enctype="multipart/form-data"`.

### Giả định 3

Bảo mật truy cập (Authorization) được kiểm soát ở Servlet. Nếu một nhân viên không có quyền hoặc yêu cầu không ở trạng thái `IN_PROGRESS`, Servlet sẽ từ chối lưu và báo lỗi.

---

## 6. OPEN QUESTIONS

### Quản lý sửa đổi

Sau khi yêu cầu chuyển sang trạng thái Hoàn thành và đã có ảnh/ghi chú:

* Việc chỉnh sửa (sửa lại ghi chú, upload lại ảnh) có được phép không? Tạm thời trong thiết kế này là khóa cứng sau khi hoàn thành.

### Xử lý rác hệ thống (Garbage Collection)

Nếu tải lên nhiều ảnh nhưng có trục trặc trong quá trình ghi cơ sở dữ liệu:

* Có cần dọn dẹp các file rác đã ghi xuống ổ cứng không? Tạm thời sẽ để lại hoặc xử lý bằng Job định kỳ.
