# Feature: Chi tiết yêu cầu sửa chữa

**Status:** Draft  
**Author:** [Tên của bạn]  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-10  
**Priority:** High

---

# 1. Business Context

Tính năng này cung cấp toàn bộ thông tin về một sự cố hoặc yêu cầu sửa chữa để nhân viên vận hành có thể đánh giá mức độ nghiêm trọng và quyết định việc tiếp nhận hay từ chối xử lý yêu cầu.

Đây là bước quan trọng trong quy trình xử lý sự cố, giúp phân bổ công việc chính xác, minh bạch và đảm bảo trách nhiệm của người thực hiện.

---

# 2. User Stories

## Story 1 (Happy Path - Xem chi tiết)

**As a** nhân viên vận hành,

**I want to** xem toàn bộ thông tin chi tiết của yêu cầu sửa chữa (bao gồm cả hình ảnh đính kèm),

**so that** tôi hiểu rõ vấn đề cần xử lý trước khi tiếp nhận yêu cầu.

## Story 2 (Hành động - Nhận/Từ chối)

**As a** nhân viên vận hành,

**I want to** nhận hoặc từ chối yêu cầu được giao,

**so that** hệ thống và người quản lý biết ai đang chịu trách nhiệm xử lý sự cố.

## Story 3 (Điều hướng)

**As a** nhân viên vận hành,

**when** tôi đã tiếp nhận yêu cầu,

**I want to** chuyển sang màn hình "Cập nhật trạng thái",

**so that** tôi có thể cập nhật kết quả xử lý sau khi hoàn thành công việc.

---

# 3. Acceptance Criteria (EARS)

## AC01 – Hiển thị chi tiết yêu cầu

**WHEN** user truy cập trang "Chi tiết yêu cầu" từ màn hình "Danh sách yêu cầu"

**THE SYSTEM SHALL**

- Gửi request lấy dữ liệu chi tiết từ server
- Hiển thị đầy đủ thông tin yêu cầu gồm:
  - Thể loại
  - Tiêu đề
  - Nội dung
  - Ảnh đính kèm (nếu có)
  - Phòng gửi yêu cầu
  - Cơ sở
  - Ngày tạo yêu cầu
  - Ngày hẹn sửa
  - Trạng thái hiện tại

## AC02 – Hiển thị nút nhận/từ chối

**WHEN** trạng thái yêu cầu là `PENDING`

**THE SYSTEM SHALL** hiển thị:

- Nút [Nhận yêu cầu]
- Nút [Từ chối]

## AC03 – Nhận yêu cầu thành công

**WHEN** user nhấn nút [Nhận yêu cầu]

**THE SYSTEM SHALL**

- Gửi request cập nhật trạng thái (POST form)
- Hiển thị thông báo thành công
- Ẩn nút [Nhận yêu cầu]
- Ẩn nút [Từ chối]
- Hiển thị nút [Cập nhật trạng thái]

## AC04 – Từ chối yêu cầu

**WHEN** user nhấn nút [Từ chối]

**THE SYSTEM SHALL**

- Hiển thị popup nhập lý do từ chối

**AND WHEN** user gửi lý do hợp lệ

**THE SYSTEM SHALL**

- Gửi request cập nhật trạng thái (POST form)
- Lưu lý do từ chối
- Điều hướng người dùng về màn hình "Danh sách yêu cầu"

## AC05 – Chuyển sang cập nhật trạng thái

**WHEN** user nhấn nút [Cập nhật trạng thái]

**AND** yêu cầu đã được tiếp nhận

**THE SYSTEM SHALL**

- Điều hướng sang màn hình "Cập nhật trạng thái sửa chữa"

---

## 4. Giao tiếp Hệ thống (System Flow)

### Đường dẫn (Endpoint)
* **Endpoint xem chi tiết:** `GET /operator/requests/detail?id={id}`
* **Endpoint thao tác (Nhận/Từ chối/Hẹn):** `POST /operator/requests/detail`
* **Loại dữ liệu (Content-Type):** Trả về HTML (JSP) (cho phương thức GET) và Form Submit truyền thống (`application/x-www-form-urlencoded` hoặc `multipart/form-data`) cho POST.

### Phản hồi Hệ thống (System Response)
* **GET - Xem chi tiết:** Forward đến `/WEB-INF/views/operator/requests/detail.jsp` với attribute `reqDetail`.
* **POST - Thao tác thành công:** Điều hướng (Redirect 302) trở lại trang chi tiết kèm theo thông báo thành công `successMessage`.
* **POST - Thất bại:** Forward lại trang chi tiết kèm theo `errorMessage`.
