# Feature: Quản lý Thông tin Cá nhân (User Profile Management)

**Status:** Draft

**Author:** [Tên của bạn]

**Tech Lead Approval:** [Tên Tech Lead]

**Date:** 2026-06-13

**Risk Level:** Medium

---

# 1. Business Context & Goals

Hệ thống quản lý nhà trọ phục vụ nhiều đối tượng người dùng khác nhau với các vai trò riêng biệt (Admin, Ban quản lý, Người vận hành, Người thuê). Hiện tại, hệ thống chưa có một phân hệ quản lý hồ sơ tập trung, dẫn đến việc cập nhật thông tin liên lạc bị phân tán và gây khó khăn khi cần xác minh danh tính hoặc liên hệ khẩn cấp.

Mục tiêu của tính năng:

* Cung cấp giao diện tập trung cho tất cả các đối tượng người dùng tự quản lý thông tin cá nhân.
* Bảo mật tài khoản thông qua chức năng tự thay đổi mật khẩu định kỳ.
* Tùy biến hiển thị thông tin đặc thù theo từng vai trò (Role-based View) để tối ưu hóa trải nghiệm người dùng.
* Đồng bộ dữ liệu liên lạc theo thời gian thực phục vụ công tác quản lý vận hành và giao tiếp nội bộ.

---

# 2. Stakeholders & User Personas

### Tất cả người dùng (All Users)

* Xem thông tin cơ bản của chính mình.
* Cập nhật thông tin liên hệ và ảnh đại diện.
* Thay đổi mật khẩu tài khoản.

### Administrator (Admin)

* Theo dõi thông tin tài khoản hệ thống, ngày tạo và lịch sử đăng nhập gần nhất để kiểm soát an ninh.

### Building Manager (Ban quản lý)

* Xem chức vụ, ngày bắt đầu làm việc và danh sách các cơ sở/tòa nhà trọ đang được phân quyền quản lý tổng thể.

### Operator (Người vận hành / Nhân viên kỹ thuật, vệ sinh)

* Xem chức vụ, ca trực, danh sách đầu việc hoặc khu vực phòng trọ được giao phụ trách sửa chữa/vận hành.

### Tenant (Người thuê)

* Xem mã người thuê, thông tin phòng đang ở, trạng thái hợp đồng (ngày bắt đầu/hết hạn) và danh sách rút gọn các thành viên ở cùng phòng.

---

# 3. User Stories

## Story 1 – Xem hồ sơ cá nhân theo vai trò

**As a** người dùng hệ thống (Admin/Ban quản lý/Người vận hành/Người thuê)

**I want to** truy cập vào trang thông tin cá nhân

**so that** tôi có thể xem các thông tin cơ bản và các thông tin đặc thù gắn liền với vai trò của mình trong hệ thống.

---

## Story 2 – Cập nhật thông tin liên hệ

**As a** người dùng hệ thống

**I want to** cập nhật số điện thoại, email và địa chỉ thường trú

**so that** ban quản lý hoặc các bên liên quan có thể liên lạc với tôi khi có sự cố hoặc thông báo quan trọng.

---

## Story 3 – Thay đổi ảnh đại diện

**As a** người dùng hệ thống

**I want to** tải lên hình ảnh chân dung mới để làm ảnh đại diện

**so that** hồ sơ cá nhân hiển thị một cách trực quan và dễ dàng nhận diện khi tương tác trên hệ thống.

---

## Story 4 – Thay đổi mật khẩu

**As a** người dùng hệ thống

**I want to** thực hiện đổi mật khẩu bằng cách nhập mật khẩu cũ và mật khẩu mới

**so that** đảm bảo an toàn thông tin và bảo mật tài khoản của tôi.

---

# 4. Acceptance Criteria (EARS)

## AC01 – Hiển thị thông tin theo vai trò

**WHEN** người dùng mở màn hình hồ sơ cá nhân (`Profile`)

**THE SYSTEM SHALL**

* Hiển thị các thông tin chung: Họ tên, Số điện thoại, Email, Ngày sinh, Giới tính, Ảnh đại diện.
* **Nếu là Admin:** Hiển thị thêm ngày tạo tài khoản, lịch sử đăng nhập gần nhất.
* **Nếu là Ban quản lý:** Hiển thị thêm Chức vụ, Ngày bắt đầu làm việc, danh sách cơ sở quản lý.
* **Nếu là Operator:** Hiển thị thêm Chức vụ, Ca trực, danh sách khu vực/đầu việc phụ trách.
* **Nếu là Tenant:** Hiển thị thêm Mã người thuê, Số phòng - Tên tòa nhà, Thời hạn hợp đồng, Danh sách thành viên cùng phòng.

---

## AC02 – Cập nhật thông tin liên hệ

**WHEN** người dùng nhấn nút [Lưu thay đổi] sau khi chỉnh sửa thông tin

**THE SYSTEM SHALL**

* Kiểm tra tính hợp lệ của dữ liệu đầu vào (Format Email, Format Số điện thoại).
* Kiểm tra tính duy nhất: Số điện thoại và Email không được trùng với tài khoản khác trong Database.
* Cập nhật thông tin vào hệ thống và hiển thị thông báo thành công.
* Không cho phép người dùng tự ý chỉnh sửa trường Vai trò (`Role`) và Mã định danh (Mã nhân viên/Mã người thuê).

---

## AC03 – Thay đổi ảnh đại diện

**WHEN** người dùng chọn một tệp hình ảnh để tải lên làm Avatar

**THE SYSTEM SHALL**

* Kiểm tra dung lượng tệp (Không vượt quá 2MB).
* Kiểm tra định dạng tệp (Chỉ chấp nhận `.jpg`, `.png`, `.jpeg`).
* Lưu trữ tệp hình ảnh vào thư mục hệ thống hoặc dịch vụ lưu trữ, đồng thời cập nhật `avatar_url` trong Database.

---

## AC04 – Thay đổi mật khẩu an toàn

**WHEN** người dùng gửi biểu mẫu yêu cầu đổi mật khẩu

**THE SYSTEM SHALL**

* Xác thực mật khẩu hiện tại (Current Password) có khớp với mật khẩu đang lưu trong hệ thống hay không.
* Kiểm tra độ phức tạp của mật khẩu mới (Độ dài tối thiểu từ 6-8 ký tự trở lên).
* Kiểm tra mật khẩu mới không được trùng với mật khẩu hiện tại.
* Mã hóa mật khẩu mới trước khi cập nhật vào cơ sở dữ liệu.

---

## AC05 – Chống lỗi phân quyền (ID Spoofing)

**WHEN** người dùng gửi request xem hoặc chỉnh sửa thông tin hồ sơ

**THE SYSTEM SHALL**

* Kiểm tra định danh của Session/Token hiện tại.
* Chỉ cho phép chỉnh sửa hoặc xem dữ liệu của chính tài khoản đang đăng nhập.
* Trả về lỗi hệ thống nếu phát hiện cố tình thay đổi tham số ID trên URL để xem hồ sơ người khác.

---

# 5. API Contracts

## Endpoint 1 – Xem thông tin hồ sơ cá nhân

```http
GET /api/v1/profile

```

### Response 200 (Ví dụ cho vai trò Tenant)

```json
{
  "userId": 1024,
  "username": "tuanh05",
  "fullName": "Phạm Anh Tú",
  "email": "tuanh.phamanh@gmail.com",
  "phone": "0987654321",
  "avatarUrl": "/uploads/avatars/inv_1024.png",
  "dateOfBirth": "2005-03-15",
  "gender": "Male",
  "address": "Cầu Giấy, Hà Nội",
  "role": "TENANT",
  "tenantMetaData": {
    "tenantCode": "TN-2005",
    "roomNumber": "P.402",
    "buildingName": "Nhà trọ số 12",
    "contractStart": "2026-01-01",
    "contractEnd": "2026-12-31",
    "roommates": [
      {"name": "Nguyễn Văn A", "phone": "0912345678"},
      {"name": "Trần Văn B", "phone": "0923456789"}
    ]
  }
}

```

---

## Endpoint 2 – Cập nhật thông tin cơ bản

```http
PUT /api/v1/profile/update

```

### Request

```json
{
  "fullName": "Phạm Anh Tú",
  "email": "tuanh.phamanh@gmail.com",
  "phone": "0987654321",
  "dateOfBirth": "2005-03-15",
  "gender": "Male",
  "address": "Cầu Giấy, Hà Nội"
}

```

### Response 200

```json
{
  "resultCode": 0,
  "message": "Update profile successfully"
}

```

---

## Endpoint 3 – Thay đổi mật khẩu

```http
PUT /api/v1/profile/change-password

```

### Request

```json
{
  "currentPassword": "OldPassword123@",
  "newPassword": "NewSecurePassword456@"
}

```

### Response 200

```json
{
  "resultCode": 0,
  "message": "Password changed successfully"
}

```

---

# 6. Data Models & DB Schema Changes

## Table: users

*(Chứa thông tin xác thực cốt lõi)*

| Column | Type | Description |
| --- | --- | --- |
| id | bigint (PK) | Mã tăng tự động |
| username | varchar | Tên đăng nhập duy nhất |
| password | varchar | Mật khẩu đã được mã hóa |
| role | varchar | Vai trò (ADMIN, MANAGER, OPERATOR, TENANT) |
| status | varchar | Trạng thái tài khoản (ACTIVE, LOCKED) |
| created_at | timestamp | Thời gian tạo tài khoản |

---

## Table: user_profiles

*(Chứa thông tin hồ sơ chi tiết, quan hệ 1:1 với table users)*

| Column | Type | Description |
| --- | --- | --- |
| id | bigint (PK) | Mã tăng tự động |
| user_id | bigint (FK) | Liên kết với bảng users(id) |
| full_name | varchar | Họ và tên |
| email | varchar | Email liên hệ (Unique) |
| phone | varchar | Số điện thoại liên hệ (Unique) |
| avatar_url | varchar | Đường dẫn lưu trữ tệp ảnh đại diện |
| date_of_birth | date | Ngày sinh |
| gender | varchar | Giới tính |
| address | varchar | Địa chỉ thường trú |
| updated_at | timestamp | Thời gian cập nhật gần nhất |

---

# 7. Non-Functional Requirements

## Performance

* API tải dữ liệu Profile phản hồi < 300ms (p95).
* Thời gian xử lý upload ảnh, nén ảnh và lưu trữ < 1.5 giây.

## Security

* Mật khẩu bắt buộc phải được băm bằng các thuật toán một chiều an toàn (Bcrypt hoặc SHA-256 kèm Salt) trước khi lưu xuống PostgreSQL.
* Dữ liệu đầu vào từ các trường văn bản (Họ tên, Địa chỉ) phải được lọc để chống tấn công XSS (Cross-Site Scripting).
* Phải kiểm tra quyền sở hữu bản ghi dựa trên thông tin định danh từ Session trên Server để ngăn chặn hoàn toàn lỗi ID Spoofing / IDor.

## Usability

* Giao diện đáp ứng tốt trên cả máy tính và thiết bị di động (Responsive Web Design), giúp người thuê dễ dàng thao tác trên điện thoại.

---

# 8. Error Handling Matrix

| Error Code | HTTP Status | Description |
| --- | --- | --- |
| PROFILE_NOT_FOUND | 404 | Không tìm thấy hồ sơ người dùng |
| EMAIL_ALREADY_EXISTS | 409 | Email đã được sử dụng bởi tài khoản khác |
| PHONE_ALREADY_EXISTS | 409 | Số điện thoại đã được sử dụng bởi tài khoản khác |
| INVALID_CURRENT_PASSWORD | 400 | Mật khẩu hiện tại nhập vào không chính xác |
| PASSWORD_TOO_WEAK | 400 | Mật khẩu mới không đạt yêu cầu độ dài hoặc độ phức tạp |
| INVALID_FILE_TYPE | 400 | Định dạng tệp ảnh đại diện không hợp lệ |
| FILE_TOO_LARGE | 400 | Dung lượng ảnh vượt quá giới hạn cho phép (2MB) |

---

# 9. Edge Cases & Corner Cases

* **Tải lên tệp hỏng:** Người dùng cố tình đổi đuôi file của một file không phải ảnh (ví dụ: đổi `document.pdf` thành `document.png`) rồi upload. Hệ thống phải kiểm tra file content/magic bytes ở Backend chứ không chỉ kiểm tra đuôi file extension.
* **Cập nhật số điện thoại trùng:** Hai người dùng đồng thời nhấn cập nhật hồ sơ với cùng một số điện thoại mới. Hệ thống phải xử lý kiểm tra ràng buộc Unique tại tầng database để tránh xung đột dữ liệu.
* **Hợp đồng hết hạn hoặc chưa được xếp phòng:** Đối với vai trò Người thuê, nếu thông tin phòng hoặc hợp đồng chưa có hoặc bị trống trong DB, hệ thống cần ẩn vùng thông tin đó đi hoặc hiển thị trạng thái "Chưa cập nhật phòng ở" thay vì văng lỗi NullPointerException.
* **Đổi mật khẩu khi phiên đăng nhập sắp hết hạn:** Người dùng gửi request đổi mật khẩu đúng lúc Session trên Server vừa hết hạn. Hệ thống cần điều hướng về trang Login và hiển thị thông báo yêu cầu đăng nhập lại một cách thân thiện.

---

# 10. Dependencies & Integration Points

* **Authentication & Session Service:** Để lấy thông tin định danh và quyền hạn của người dùng đang thao tác.
* **File Storage System:** Thư mục lưu trữ local trên máy chủ hoặc dịch vụ lưu trữ để quản lý các tệp ảnh đại diện được tải lên.
* **Room & Contract Sub-system:** Điểm truy xuất thông tin bổ sung để lấy dữ liệu phòng ở, thời hạn hợp đồng và bạn cùng phòng đối với vai trò Người thuê.

---

# 11. Testing Requirements

## Unit Test

* Kiểm tra tính hợp lệ của hàm validate định dạng Email và Số điện thoại.
* Kiểm tra hàm mã hóa mật khẩu và hàm so khớp mật khẩu cũ.
* Kiểm tra logic phân loại thông tin hiển thị dựa theo quyền hạn (`Role`).

## Integration Test

* Kiểm tra luồng tải lên ảnh đại diện: Lưu file thành công vào bộ nhớ vật lý và ghi nhận đúng `avatar_url` vào PostgreSQL.
* Kiểm tra ràng buộc Unique đối với trường Email và Số điện thoại khi có thao tác update đồng thời.

## E2E Test

* Thực hiện trọn vẹn kịch bản đổi mật khẩu thành công -> Đăng xuất khỏi hệ thống -> Sử dụng mật khẩu mới để đăng nhập lại vào hệ thống.
* Đăng nhập bằng tài khoản Người thuê và xác minh xem giao diện có hiển thị chính xác thông tin phòng ở, ẩn đi các thông tin nội bộ của nhân viên hay không.

---

# 12. Rollout Plan

* Thực hiện chạy scripts DB migration để tạo bảng `user_profiles` và liên kết với bảng `users` hiện tại trên môi trường Staging/UAT.
* Triển khai thử nghiệm (Alpha Test) cho nhóm nhân viên vận hành và Ban quản lý để kiểm tra tính ổn định của chức năng đổi ảnh đại diện và cập nhật thông tin.
* Phát hành chính thức (Production Release) cho toàn bộ cư dân và người dùng trong hệ thống.

---

# 13. Open Questions

### Q1

Hệ thống có cần tích hợp mã OTP gửi về số điện thoại hoặc link xác nhận về Email khi người dùng thực hiện thay đổi Số điện thoại/Email trong trang Profile hay không?

**Owner:** Product Owner / Security Lead

### Q2

Người thuê có được phép tự cập nhật hoặc chỉnh sửa số Căn cước công dân (CCCD) và ảnh chụp CCCD trên trang cá nhân của họ hay không, hay việc này bắt buộc phải do Ban quản lý kiểm tra và nhập khi làm hợp đồng?

**Owner:** Business Analyst