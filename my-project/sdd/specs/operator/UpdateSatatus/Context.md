# CONTEXT.md Feature Cập nhật trạng thái sửa chữa

# Người viết: @Phạm Anh Tú | Ngày: 2026-06-11

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

Là hành động cập nhật kết quả xử lý cho một yêu cầu sửa chữa đã được tiếp nhận.

Thông tin thường bao gồm:

* Ghi chú kết quả xử lý.
* Hình ảnh sau sửa chữa.
* Thời gian hoàn thành.

### Multipart/Form-Data

Định dạng dữ liệu bắt buộc khi gửi tệp đính kèm (file ảnh) từ giao diện đến backend.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành

Người trực tiếp xử lý sự cố và cập nhật kết quả sửa chữa lên hệ thống.

### Quản lý vận hành

Theo dõi kết quả xử lý, kiểm tra minh chứng hoàn thành và đánh giá hiệu quả vận hành.

### Người gửi yêu cầu

Được thông báo khi yêu cầu chuyển sang trạng thái hoàn thành để kiểm tra lại hiện trạng thiết bị hoặc cơ sở vật chất.

---

## 4. CONSTRAINTS

### Ràng buộc kỹ thuật

#### File Validation

* Chỉ chấp nhận:

  * JPG
  * JPEG
  * PNG
* Dung lượng tối đa: 5MB/ảnh.
* Số lượng ảnh tối thiểu: 1 ảnh.
* Số lượng ảnh tối đa: 5 ảnh.

#### Date Constraint

* Trường `completed_at` không được lớn hơn thời điểm hiện tại.
* Không cho phép ghi nhận ngày hoàn thành trong tương lai.

#### Database Constraint

* Dữ liệu hoàn thành phải được lưu vào bảng chi tiết sửa chữa.
* Các câu lệnh INSERT và UPDATE phải tuân thủ chuẩn Basic SQL Statements của dự án.
* Trường `notes` phải được sanitize trước khi ghi xuống cơ sở dữ liệu nhằm hạn chế lỗi cú pháp và SQL Injection.

### Ràng buộc phạm vi

Không bao gồm:

* Chữ ký điện tử của người gửi yêu cầu.
* Chức năng đánh giá sao.
* Chức năng phản hồi sau sửa chữa.
* Luồng bảo trì định kỳ.

---

## 5. ASSUMPTIONS

### Giả định 1

Frontend có cơ chế nén ảnh trước khi tải lên nhằm giảm dung lượng truyền tải và hạn chế timeout khi sử dụng mạng di động.

### Giả định 2

API hoàn thành yêu cầu đã được tích hợp cơ chế Authorization.

Nếu nhân viên vận hành A cố gắng cập nhật yêu cầu đang được giao cho nhân viên vận hành B thì hệ thống sẽ trả về:

```text
403 Forbidden
```

---

## 6. OPEN QUESTIONS

### Quản lý sửa đổi

Sau khi yêu cầu chuyển sang trạng thái Hoàn thành:

* Có cho phép chỉnh sửa ghi chú hoặc ảnh minh chứng trong một khoảng thời gian giới hạn hay không?
* Hay dữ liệu sẽ bị khóa hoàn toàn?

### Xử lý lỗi Upload

Nếu người dùng tải lên nhiều ảnh nhưng chỉ một phần ảnh được lưu thành công:

* Rollback toàn bộ transaction?
* Hay chấp nhận các ảnh hợp lệ và bỏ qua ảnh lỗi?

### Lưu trữ ảnh

Ảnh minh chứng sẽ được:

* Lưu trên máy chủ ứng dụng (Local Storage)?
* Hay lưu trên Cloud Storage (AWS S3, Cloudinary, Azure Blob Storage...) và chỉ lưu URL trong Database?
