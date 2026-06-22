# Feature: Quản lý Nhân sự

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Quản lý Nhân sự cho phép Admin quản lý toàn bộ tài khoản nhân sự trong hệ thống, bao gồm tạo mới, cập nhật thông tin, tìm kiếm, xem chi tiết, kiểm soát trạng thái hoạt động của tài khoản và gán cơ sở quản lý cho nhân sự.

Hệ thống sử dụng mô hình vai trò cố định gồm:

* Admin
* Ban Quản Lý
* Nhân viên vận hành

Tài khoản Admin được khởi tạo sẵn bởi hệ thống và chỉ tồn tại duy nhất một tài khoản.

Chức năng Quản lý Nhân sự chỉ cho phép Admin quản lý các nhân sự thuộc các vai trò Ban Quản Lý, Nhân viên vận hành.

Đối với nhân sự có vai trò Ban Quản Lý hoặc Nhân viên vận hành, Admin phải gán cơ sở quản lý để xác định phạm vi làm việc của từng nhân sự trong hệ thống.

Một nhân sự có thể được gán quản lý một cơ sở. Việc gán cơ sở giúp hệ thống giới hạn dữ liệu mà nhân sự được phép xem và thao tác, đảm bảo nhân sự chỉ xử lý thông tin thuộc phạm vi được phân công.

Tính năng này giúp đảm bảo việc phân công trách nhiệm, quản lý quyền truy cập, giới hạn phạm vi quản lý theo cơ sở và vận hành hệ thống được thực hiện tập trung, minh bạch và an toàn.

## 2. User Stories

### Story 1 (Happy Path)

Là Admin, tôi muốn quản lý danh sách nhân sự để kiểm soát tài khoản và quyền truy cập của từng nhân sự trong hệ thống.

### Story 2 (Happy Path)

Là Admin, tôi muốn xem thông tin chi tiết nhân sự để theo dõi đầy đủ thông tin tài khoản và phạm vi cơ sở được phân công.

### Story 3 (Happy Path)

Là Admin, tôi muốn tạo mới nhân sự và gán vai trò để nhân sự có thể đăng nhập và sử dụng các chức năng phù hợp với quyền hạn được cấp.

### Story 4 (Happy Path)

Là Admin, tôi muốn cập nhật thông tin nhân sự để đảm bảo dữ liệu luôn chính xác.

### Story 5 (Edge Case)

Là Admin, khi email hoặc số điện thoại đã tồn tại trong hệ thống, tôi muốn nhận được thông báo lỗi phù hợp.

### Story 6 (Edge Case)

Là Admin, khi một nhân sự nghỉ việc hoặc tạm ngưng công tác, tôi muốn khóa tài khoản để ngăn truy cập vào hệ thống.

### Story 7 (Edge Case)

Là Admin, khi nhân sự có vai trò Ban Quản Lý hoặc Nhân viên vận hành chưa được gán cơ sở quản lý, tôi muốn hệ thống từ chối tạo mới hoặc cập nhật để đảm bảo nhân sự luôn có phạm vi quản lý rõ ràng.

## 3. Acceptance Criteria (EARS)

### 3.1 Xem danh sách nhân sự

WHEN Admin truy cập màn hình quản lý nhân sự
THE SYSTEM SHALL hiển thị danh sách nhân sự thuộc các vai trò MANAGER, OPERATOR

WHEN danh sách nhân sự có nhiều dữ liệu
THE SYSTEM SHALL hỗ trợ phân trang.

WHEN hệ thống chưa có nhân sự nào thuộc các vai trò MANAGER, OPERATOR
THE SYSTEM SHALL hiển thị thông báo "Chưa có nhân sự nào trong hệ thống".

WHEN hiển thị danh sách nhân sự
THE SYSTEM SHALL hiển thị tối thiểu các thông tin:

* Mã nhân sự
* Họ và tên
* Email
* Số điện thoại
* Vai trò
* Trạng thái tài khoản
* Cơ sở quản lý

WHEN Admin truy cập màn hình quản lý nhân sự
THE SYSTEM SHALL sắp xếp danh sách nhân sự theo thời gian tạo giảm dần (createdAt DESC) theo mặc định.

### 3.2 Xem chi tiết nhân sự

WHEN Admin chọn một nhân sự từ danh sách
THE SYSTEM SHALL hiển thị thông tin chi tiết của nhân sự.

WHEN hiển thị chi tiết nhân sự
THE SYSTEM SHALL hiển thị tối thiểu:

* Mã nhân sự
* Họ và tên
* Ngày sinh
* CCCD
* Email
* Số điện thoại
* Vai trò
* Trạng thái tài khoản
* Cơ sở quản lý

WHEN nhân sự không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi EMPLOYEE_NOT_FOUND.

### 3.3 Tìm kiếm nhân sự

WHEN Admin nhập từ khóa tìm kiếm
THE SYSTEM SHALL lọc danh sách theo mã nhân sự, họ tên, email hoặc số điện thoại.

WHEN không tìm thấy kết quả phù hợp
THE SYSTEM SHALL hiển thị thông báo "Không tìm thấy nhân sự phù hợp".

WHEN Admin nhập từ khóa tìm kiếm
THE SYSTEM SHALL thực hiện tìm kiếm không phân biệt chữ hoa và chữ thường.

WHEN Admin nhập từ khóa có khoảng trắng ở đầu hoặc cuối
THE SYSTEM SHALL tự động loại bỏ khoảng trắng trước khi thực hiện tìm kiếm.

### 3.4 Thêm nhân sự

WHEN Admin chọn chức năng "Thêm nhân sự"
THE SYSTEM SHALL hiển thị biểu mẫu nhập thông tin nhân sự.

WHEN Admin nhập đầy đủ và hợp lệ các trường bắt buộc
AND chọn một vai trò hợp lệ
THE SYSTEM SHALL tạo mới nhân sự và lưu vào hệ thống.

WHEN Admin tạo mới nhân sự thành công
THE SYSTEM SHALL tự động sinh mã nhân sự.

WHEN hệ thống tự động sinh mã nhân sự
THE SYSTEM SHALL không cho phép Admin chỉnh sửa mã nhân sự.

WHEN Admin tạo mới nhân sự thành công
THE SYSTEM SHALL hiển thị thông báo "Tạo nhân sự thành công".

WHEN Admin tạo mới nhân sự thành công
THE SYSTEM SHALL gán trạng thái mặc định là "Hoạt động".

WHEN Admin tạo mới nhân sự thành công
THE SYSTEM SHALL tạo mật khẩu tạm thời.

WHEN hệ thống tạo mật khẩu tạm thời
THE SYSTEM SHALL gửi mật khẩu tạm thời đến email của nhân sự.

WHEN nhân sự đăng nhập lần đầu bằng mật khẩu tạm thời
THE SYSTEM SHALL yêu cầu đổi mật khẩu.

WHEN Admin tạo mới hoặc cập nhật nhân sự
THE SYSTEM SHALL cho phép lựa chọn các vai trò sau:

* Ban Quản Lý
* Nhân viên vận hành

WHEN Admin tạo mới hoặc cập nhật nhân sự
THE SYSTEM SHALL không hiển thị vai trò Admin trong danh sách lựa chọn.

WHEN Admin tạo mới nhân sự với vai trò MANAGER hoặc OPERATOR
THE SYSTEM SHALL yêu cầu Admin duy nhất một cơ sở quản lý.

WHEN Admin tạo mới nhân sự với vai trò MANAGER hoặc OPERATOR
AND không chọn cơ sở quản lý
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_REQUIRED.

WHEN Admin gán cơ sở quản lý cho nhân sự
THE SYSTEM SHALL chỉ cho phép chọn các cơ sở đang ở trạng thái ACTIVE.

WHEN Admin gán cơ sở không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_NOT_FOUND.

WHEN Admin gán cơ sở không ở trạng thái ACTIVE
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_NOT_ACTIVE.

WHEN Admin gán vai trò MANAGER vào cơ sở đã có MANAGER khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_MANAGER_ALREADY_EXISTS.

WHEN Admin gán vai trò OPERATOR vào cơ sở đã có OPERATOR khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_OPERATOR_ALREADY_EXISTS.

WHEN ngày sinh lớn hơn ngày hiện tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_DATE_OF_BIRTH.

WHEN CCCD không gồm 12 chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_IDENTITY_NUMBER.

WHEN CCCD đã tồn tại trong hệ thống
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi IDENTITY_NUMBER_ALREADY_EXISTS.

WHEN email không đúng định dạng email hợp lệ
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_EMAIL_FORMAT.

WHEN email đã tồn tại trong hệ thống
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi EMAIL_ALREADY_EXISTS.

WHEN số điện thoại chứa ký tự không phải chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_PHONE_FORMAT.

WHEN số điện thoại có độ dài không gồm đúng 10 chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_PHONE_FORMAT.

WHEN số điện thoại đã tồn tại trong hệ thống
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi PHONE_ALREADY_EXISTS.

WHEN thiếu trường bắt buộc
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi VALIDATION_ERROR.

WHEN vai trò được gửi lên là ADMIN
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_ROLE.

WHEN Admin thay đổi vai trò trên biểu mẫu thêm nhân sự
THE SYSTEM SHALL tải lại danh sách cơ sở có thể lựa chọn theo vai trò đã chọn.

WHEN Admin chọn vai trò MANAGER
THE SYSTEM SHALL chỉ hiển thị các cơ sở ACTIVE chưa có MANAGER.

WHEN Admin chọn vai trò MANAGER
THE SYSTEM SHALL không hiển thị các cơ sở đã có MANAGER trong danh sách lựa chọn.

WHEN Admin chọn vai trò OPERATOR
THE SYSTEM SHALL hiển thị các cơ sở ACTIVE được phép gán cho OPERATOR.

WHEN Admin chọn vai trò OPERATOR
THE SYSTEM SHALL không hiển thị các cơ sở đã có OPERATOR trong danh sách lựa chọn.

### 3.5 Chỉnh sửa thông tin nhân sự

WHEN Admin cập nhật thông tin nhân sự với dữ liệu hợp lệ
THE SYSTEM SHALL lưu thay đổi và cập nhật dữ liệu.

WHEN Admin cập nhật thành công
THE SYSTEM SHALL hiển thị thông báo "Cập nhật nhân sự thành công".

WHEN Admin cập nhật thông tin nhân sự
THE SYSTEM SHALL không cho phép chỉnh sửa mã nhân sự.

WHEN Admin thay đổi vai trò trên biểu mẫu chỉnh sửa nhân sự
THE SYSTEM SHALL tải lại danh sách cơ sở có thể lựa chọn theo vai trò mới.

WHEN Admin cập nhật vai trò hoặc cơ sở quản lý thành công
THE SYSTEM SHALL cập nhật phạm vi dữ liệu mà nhân sự được phép truy cập theo vai trò và cơ sở mới.

WHEN Admin chỉnh sửa nhân sự đang được phân công vào một cơ sở
THE SYSTEM SHALL vẫn hiển thị cơ sở hiện tại của nhân sự trong danh sách lựa chọn.

WHEN Admin thay đổi vai trò của nhân sự
THE SYSTEM SHALL xác nhận lại việc phân công cơ sở theo vai trò mới.

WHEN Admin chỉnh sửa nhân sự
THE SYSTEM SHALL hiển thị cơ sở hiện tại của nhân sự trong danh sách lựa chọn
EVEN IF cơ sở đó đã được gán cho chính nhân sự đang được chỉnh sửa.

WHEN nhân sự không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi EMPLOYEE_NOT_FOUND.

WHEN Admin thay đổi vai trò của nhân sự
THE SYSTEM SHALL cập nhật toàn bộ quyền truy cập theo vai trò mới.

WHEN Admin cập nhật nhân sự có vai trò MANAGER hoặc OPERATOR
THE SYSTEM SHALL cho phép Admin cập nhật cơ sở quản lý.

WHEN Admin cập nhật nhân sự có vai trò MANAGER hoặc OPERATOR
AND không chọn cơ sở quản lý
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_REQUIRED.

WHEN Admin cập nhật vai trò MANAGER vào cơ sở đã có MANAGER khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_MANAGER_ALREADY_EXISTS.

WHEN Admin cập nhật vai trò OPERATOR vào cơ sở đã có OPERATOR khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_OPERATOR_ALREADY_EXISTS.

WHEN Admin cập nhật cơ sở quản lý cho nhân sự
AND cơ sở không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_NOT_FOUND.

WHEN Admin cập nhật cơ sở quản lý cho nhân sự
AND cơ sở không ở trạng thái ACTIVE
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi FACILITY_NOT_ACTIVE.

WHEN email không đúng định dạng email hợp lệ
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_EMAIL_FORMAT.

WHEN email đã tồn tại ở nhân sự khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi EMAIL_ALREADY_EXISTS.

WHEN số điện thoại chứa ký tự không phải chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_PHONE_FORMAT.

WHEN số điện thoại đã tồn tại ở nhân sự khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi PHONE_ALREADY_EXISTS.

WHEN số điện thoại có độ dài khác 10 chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_PHONE_FORMAT.

WHEN CCCD đã tồn tại ở nhân sự khác
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi IDENTITY_NUMBER_ALREADY_EXISTS.

WHEN ngày sinh lớn hơn ngày hiện tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_DATE_OF_BIRTH.

WHEN CCCD không gồm 12 chữ số
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_IDENTITY_NUMBER.

WHEN thiếu trường bắt buộc
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi VALIDATION_ERROR.

WHEN vai trò được gửi lên là ADMIN
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi INVALID_ROLE.

### 3.6 Khóa/Mở khóa tài khoản

WHEN Admin thực hiện khóa tài khoản nhân sự
THE SYSTEM SHALL chuyển trạng thái tài khoản sang "Không hoạt động".

WHEN Admin thực hiện khóa tài khoản của chính mình
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi CANNOT_DEACTIVATE_SELF.

WHEN tài khoản đang ở trạng thái "Không hoạt động"
THE SYSTEM SHALL ngăn người dùng đăng nhập hệ thống.

WHEN Admin mở khóa tài khoản
THE SYSTEM SHALL chuyển trạng thái tài khoản sang "Hoạt động".

WHEN nhân sự không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và hiển thị lỗi EMPLOYEE_NOT_FOUND.

WHEN Admin khóa hoặc mở khóa thành công
THE SYSTEM SHALL ghi nhận lịch sử thao tác.

### 3.7 Phân quyền

WHILE người dùng không phải Admin
THE SYSTEM SHALL từ chối truy cập chức năng Quản lý Nhân sự.

WHEN nhân sự có vai trò MANAGER hoặc OPERATOR đăng nhập vào hệ thống
THE SYSTEM SHALL chỉ cho phép nhân sự xem và thao tác dữ liệu thuộc cơ sở được phân công.


## 4. API Contract

### 4.1 Lấy danh sách nhân sự

Endpoint

```http
GET /api/v1/employees?page=0&size=10&keyword=minh
```

Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "personnelId": "EMP001",
        "fullName": "Nguyen Van A",
        "email": "a@example.com",
        "phone": "0901234567",
        "role": "MANAGER",
        "status": "ACTIVE",
        "facility": {
          "id": "FAC001",
          "name": "Cơ sở Hòa Lạc"
        }
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

### 4.2 Lấy chi tiết nhân sự

Endpoint

```http
GET /api/v1/employees/{id}
```

Response 200

```json
{
  "success": true,
  "data": {
    "personnelId": "EMP001",
    "fullName": "Nguyen Van A",
    "dateOfBirth": "1995-01-01",
    "identityNumber": "012345678901",
    "gender": "MALE",
    "permanentAddress": "Hà Nội",
    "email": "a@example.com",
    "phone": "0901234567",
    "role": "MANAGER",
    "status": "ACTIVE",
    "facility":{
        "id": "FAC001",
        "name": "Cơ sở Hòa Lạc"
      },
    "createdAt": "2026-01-01T08:00:00Z",
    "updatedAt": "2026-01-10T09:00:00Z"
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "EMPLOYEE_NOT_FOUND",
    "message": "Không tìm thấy nhân sự"
  }
}
```

### 4.3 Tạo nhân sự

Endpoint

```http
POST /api/v1/employees
```

Request

```json
{
    "fullName": "Nguyen Van A",
    "dateOfBirth": "1995-01-01",
    "identityNumber": "012345678901",
    "gender": "MALE",
    "permanentAddress": "Hà Nội",
    "email": "a@example.com",
    "phone": "0901234567",
  "role": "MANAGER",
  "facilityId": "FAC001"
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "personnelId": "EMP001"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "EMAIL_ALREADY_EXISTS",
    "message": "Email đã tồn tại"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "PHONE_ALREADY_EXISTS",
    "message": "Số điện thoại đã tồn tại"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_EMAIL_FORMAT",
    "message": "Email không đúng định dạng"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_PHONE_FORMAT",
    "message": "Số điện thoại không đúng định dạng"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_ROLE",
    "message": "Vai trò không hợp lệ"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_REQUIRED",
    "message": "Nhân sự phải được gán một cơ sở quản lý"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "Không tìm thấy cơ sở"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_ACTIVE",
    "message": "Chỉ được gán cơ sở đang hoạt động"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_MANAGER_ALREADY_EXISTS",
    "message": "Cơ sở đã có Ban Quản Lý"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "FACILITY_OPERATOR_ALREADY_EXISTS",
    "message": "Cơ sở đã có Nhân viên vận hành"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_DATE_OF_BIRTH",
    "message": "Ngày sinh không hợp lệ"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_IDENTITY_NUMBER",
    "message": "CCCD phải gồm đúng 12 chữ số"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Thiếu thông tin bắt buộc"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "IDENTITY_NUMBER_ALREADY_EXISTS",
    "message": "CCCD đã tồn tại"
  }
}
### 4.4 Cập nhật nhân sự

Endpoint

```http
PUT /api/v1/employees/{id}
```

Request

```json
{
  "fullName": "Nguyen Van A",
  "dateOfBirth": "1995-01-01",
  "identityNumber": "012345678901",
  "gender": "MALE",
  "permanentAddress": "Ha Noi",
  "email": "a@example.com",
  "phone": "0909999999",
  "role": "OPERATOR",
  "facilityId": "FAC001"
}
```

Response 200

```json
{
  "success": true
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_EMAIL_FORMAT",
    "message": "Email không đúng định dạng"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_PHONE_FORMAT",
    "message": "Số điện thoại không đúng định dạng"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_MANAGER_ALREADY_EXISTS",
    "message": "Cơ sở đã có Ban Quản Lý"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "FACILITY_OPERATOR_ALREADY_EXISTS",
    "message": "Cơ sở đã có Nhân viên vận hành"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "FACILITY_REQUIRED",
    "message": "Nhân sự phải được gán một cơ sở quản lý"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "Không tìm thấy cơ sở"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_ACTIVE",
    "message": "Chỉ được gán cơ sở đang hoạt động"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "EMAIL_ALREADY_EXISTS",
    "message": "Email đã tồn tại"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "PHONE_ALREADY_EXISTS",
    "message": "Số điện thoại đã tồn tại"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_ROLE",
    "message": "Vai trò không hợp lệ"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_DATE_OF_BIRTH",
    "message": "Ngày sinh không hợp lệ"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "INVALID_IDENTITY_NUMBER",
    "message": "CCCD phải gồm đúng 12 chữ số"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "IDENTITY_NUMBER_ALREADY_EXISTS",
    "message": "CCCD đã tồn tại"
  }
}
```
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Thiếu thông tin bắt buộc"
  }
}
```
Response 404

```json
{
  "success": false,
  "error": {
    "code": "EMPLOYEE_NOT_FOUND",
    "message": "Không tìm thấy nhân sự"
  }
}
```

### 4.5 Khóa/Mở khóa tài khoản

Endpoint

```http
PATCH /api/v1/employees/{id}/status
```

Request

```json
{
  "status": "ACTIVE"
}
```

hoặc

```json
{
  "status": "INACTIVE"
}
```

Response 200

```json
{
  "success": true
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "CANNOT_DEACTIVATE_SELF",
    "message": "Không thể khóa tài khoản của chính mình"
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "EMPLOYEE_NOT_FOUND",
    "message": "Không tìm thấy nhân sự"
  }
}
```

### 4.6 Lỗi phân quyền

Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Vui lòng đăng nhập để tiếp tục"
  }
}
```

Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

## 5. Technical Constraints

Thời gian phản hồi tối đa: 500ms (P95).

Hỗ trợ phân trang tối thiểu 10 bản ghi/trang.

Mã nhân sự được hệ thống tự động sinh.

Mã nhân sự không cho phép chỉnh sửa.

Email phải đúng định dạng email hợp lệ.

Email phải là duy nhất trong hệ thống.

Số điện thoại chỉ gồm chữ số và có độ dài đúng 10 chữ số.

Số điện thoại phải là duy nhất trong hệ thống.

Không được xóa cứng nhân sự khỏi cơ sở dữ liệu.

Mọi thao tác tạo, cập nhật, khóa/mở khóa và gán cơ sở quản lý phải được ghi log.

Hệ thống phải tạo mật khẩu tạm thời khi Admin tạo mới nhân sự.

Mật khẩu tạm thời phải được gửi đến email của nhân sự.

Nhân sự phải đổi mật khẩu trong lần đăng nhập đầu tiên bằng mật khẩu tạm thời.

Hệ thống chỉ hỗ trợ 03 vai trò cố định:

* ADMIN
* MANAGER
* OPERATOR

Chỉ tồn tại duy nhất 01 tài khoản ADMIN trong hệ thống.

Không cho phép tạo mới tài khoản ADMIN từ chức năng Quản lý Nhân sự.

Không cho phép thay đổi vai trò của bất kỳ nhân sự nào thành ADMIN.

Nhân sự có vai trò MANAGER hoặc OPERATOR phải được gán một cơ sở quản lý.

Mỗi cơ sở chỉ được phép có tối đa một MANAGER.

Mỗi cơ sở chỉ được phép có tối đa một OPERATOR.

Một nhân sự chỉ có thể được gán vào một cơ sở.

Chỉ được gán nhân sự vào các cơ sở đang ở trạng thái ACTIVE.

Dữ liệu mà nhân sự được phép xem và thao tác phải được giới hạn theo cơ sở được phân công.


## 6. Dependencies

### 6.1 Xác thực và Đăng nhập

Tính năng Quản lý Nhân sự phụ thuộc vào hệ thống Xác thực và Đăng nhập để kiểm soát khả năng truy cập của người dùng.

WHEN tài khoản ở trạng thái "Không hoạt động"
THE SYSTEM SHALL ngăn người dùng đăng nhập hệ thống.

WHEN nhân sự đăng nhập lần đầu bằng mật khẩu tạm thời
THE SYSTEM SHALL yêu cầu đổi mật khẩu trước khi tiếp tục sử dụng hệ thống.

### 6.2 Audit Log

Tính năng Quản lý Nhân sự phụ thuộc vào hệ thống Audit Log để ghi nhận lịch sử thao tác.

WHEN Admin tạo mới nhân sự
THE SYSTEM SHALL ghi nhận lịch sử thao tác.

WHEN Admin cập nhật thông tin nhân sự
THE SYSTEM SHALL ghi nhận lịch sử thao tác.

WHEN Admin khóa hoặc mở khóa tài khoản nhân sự
THE SYSTEM SHALL ghi nhận lịch sử thao tác.

WHEN Admin gán hoặc cập nhật cơ sở quản lý cho nhân sự
THE SYSTEM SHALL ghi nhận lịch sử thao tác.

### 6.3 Email Service

Tính năng Quản lý Nhân sự phụ thuộc vào Email Service để gửi mật khẩu tạm thời cho nhân sự sau khi tài khoản được tạo mới.

WHEN hệ thống tạo mật khẩu tạm thời
THE SYSTEM SHALL gửi mật khẩu tạm thời đến email của nhân sự.

### 6.4 Quản lý Cơ sở

Tính năng Quản lý Nhân sự phụ thuộc vào tính năng Quản lý Cơ sở để lấy danh sách cơ sở đang hoạt động phục vụ việc gán phạm vi quản lý cho nhân sự.

WHEN Admin gán cơ sở quản lý cho nhân sự
THE SYSTEM SHALL kiểm tra cơ sở có tồn tại trong hệ thống.

WHEN Admin gán cơ sở quản lý cho nhân sự
THE SYSTEM SHALL kiểm tra cơ sở đang ở trạng thái ACTIVE.

WHEN nhân sự có vai trò MANAGER hoặc OPERATOR truy cập dữ liệu cơ sở
THE SYSTEM SHALL kiểm tra nhân sự có được phân công vào cơ sở đó hay không.

## 7. Out of Scope

Quản lý chấm công.

Quản lý lương và phúc lợi.

Quản lý hồ sơ nhân sự chi tiết.

Đánh giá hiệu suất nhân viên.

Quy trình phê duyệt nghỉ phép.

Tạo mới vai trò.

Chỉnh sửa vai trò.

Xóa vai trò.

Quản lý phân quyền động.

Quản lý chi tiết thông tin cơ sở.

Tạo mới cơ sở trong màn hình Quản lý Nhân sự.

Cập nhật thông tin cơ sở trong màn hình Quản lý Nhân sự.

Xóa cơ sở khỏi hệ thống.
