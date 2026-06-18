# Feature: Quản lý Người thuê

**Trạng thái:** Draft
**Người viết:** [Tên]
**Người duyệt:** [Tên]
**Ngày:** [YYYY-MM-DD]
**Độ ưu tiên:** High

## 1. Bối cảnh nghiệp vụ

Tính năng Quản lý Người thuê cho phép hệ thống quản lý thông tin người thuê đang đại diện cho một phòng thuộc cơ sở nhà trọ. Người thuê trong feature này là người có tài khoản đăng nhập và được gán trực tiếp với một phòng cụ thể.

Feature này hỗ trợ mục tiêu kinh doanh là số hóa thông tin cư trú, giúp Ban quản lý theo dõi chính xác phòng nào đang có người thuê, người thuê đại diện là ai và trạng thái thuê hiện tại của từng phòng.

Khi tạo mới người thuê, hệ thống bắt buộc phải gán người thuê vào một phòng đang hoạt động. Mỗi phòng chỉ được có một người thuê ở trạng thái ACTIVE tại một thời điểm. Những người ở cùng người thuê chính sẽ được quản lý trong feature Quản lý Người phụ thuộc và không có tài khoản đăng nhập riêng.

Mỗi người thuê sẽ có một mã người thuê riêng do hệ thống tự động sinh để phục vụ việc tìm kiếm, tra cứu và quản lý dữ liệu.

## 2. Câu chuyện người dùng

### Story 1 (Luồng chính)

Là Manager, tôi muốn tạo tài khoản người thuê và gán người thuê vào một phòng để hệ thống có thể quản lý người thuê đại diện cho phòng đó.

### Story 2 (Luồng chính)

Là Manager, tôi muốn xem thông tin chi tiết người thuê để có thể theo dõi thông tin cá nhân, tài khoản đăng nhập, thông tin phòng và danh sách người phụ thuộc liên quan.

### Story 3 (Luồng chính)

Là Manager, tôi muốn xem và tìm kiếm danh sách người thuê để có thể nhanh chóng tra cứu thông tin người thuê theo mã người thuê, số điện thoại, phòng, cơ sở hoặc trạng thái.

### Story 4 (Trường hợp ngoại lệ)

Là Manager, khi một phòng đã có người thuê đang ACTIVE, tôi muốn hệ thống ngăn không cho tạo thêm tài khoản người thuê khác cho phòng đó.

### Story 5 (Trường hợp ngoại lệ)

Là Manager, khi người thuê đã kết thúc thời gian thuê, tôi muốn đánh dấu người thuê là không còn hoạt động và vô hiệu hóa tài khoản đăng nhập mà không xóa dữ liệu lịch sử.

## 3. Tiêu chí chấp nhận (EARS)

### AC-01 Tạo người thuê và gán phòng

KHI Manager gửi biểu mẫu tạo người thuê với thông tin hợp lệ và đã chọn phòng
HỆ THỐNG PHẢI tạo bản ghi người thuê mới, tự động sinh mã người thuê, tạo tài khoản đăng nhập, gán người thuê vào phòng đã chọn và trả về phản hồi thành công.

### AC-02 Sinh mã người thuê

KHI hệ thống tạo mới người thuê
HỆ THỐNG PHẢI tự động sinh một mã người thuê duy nhất.

Mã người thuê không được phép chỉnh sửa thủ công.

### AC-03 Kiểm tra các trường bắt buộc

KHI Manager gửi biểu mẫu thiếu các trường bắt buộc
HỆ THỐNG PHẢI từ chối yêu cầu và hiển thị lỗi kiểm tra dữ liệu.

Các trường bắt buộc bao gồm:

* Họ và tên
* Số điện thoại
* Số CCCD/CMND
* Email đăng nhập
* Phòng

### AC-04 Kiểm tra phòng hợp lệ

KHI Manager chọn phòng cho người thuê
HỆ THỐNG PHẢI kiểm tra phòng được chọn đang ở trạng thái ACTIVE.

### AC-05 Kiểm tra phòng đã có người thuê

KHI phòng được chọn đã có người thuê ở trạng thái ACTIVE
HỆ THỐNG PHẢI ngăn không cho tạo thêm người thuê mới cho phòng đó và trả về phản hồi lỗi.

### AC-06 Xem danh sách người thuê

KHI Manager mở trang danh sách người thuê
HỆ THỐNG PHẢI hiển thị danh sách người thuê có phân trang.

Danh sách hỗ trợ lọc theo:

* Từ khóa
* Trạng thái người thuê
* Phòng
* Cơ sở

### AC-07 Xem chi tiết người thuê

KHI Manager mở trang chi tiết người thuê
HỆ THỐNG PHẢI hiển thị:

* Thông tin người thuê
* Mã người thuê
* Thông tin tài khoản đăng nhập
* Thông tin phòng hiện tại
* Thông tin cơ sở
* Danh sách người phụ thuộc
* Trạng thái thuê
* Thông tin audit

### AC-08 Kết thúc thuê

KHI Manager đánh dấu người thuê đã kết thúc thuê
HỆ THỐNG PHẢI chuyển trạng thái người thuê thành INACTIVE, vô hiệu hóa tài khoản đăng nhập của người thuê và giữ lại dữ liệu lịch sử.

### AC-09 Phân quyền

TRONG KHI người dùng không có quyền quản lý người thuê
HỆ THỐNG PHẢI từ chối truy cập vào các chức năng quản lý người thuê.

## 4. Hợp đồng API

### 4.1 Tạo người thuê

Endpoint

```http
POST /api/v1/tenants
```

Request

```json
{
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "identityNumber": "001203000001",
  "email": "tenant01@example.com",
  "roomId": 101
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "tenantId": 15,
    "tenantCode": "TEN00015",
    "fullName": "Nguyen Van A",
    "email": "tenant01@example.com",
    "roomId": 101,
    "status": "ACTIVE",
    "accountStatus": "ACTIVE"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "TENANT_VALIDATION_ERROR",
    "message": "Thông tin người thuê không hợp lệ"
  }
}
```

Response 400 - Phòng đã có người thuê

```json
{
  "success": false,
  "error": {
    "code": "ROOM_ALREADY_HAS_ACTIVE_TENANT",
    "message": "Phòng được chọn đã có người thuê đang hoạt động"
  }
}
```

Response 400 - Phòng không hoạt động

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_ACTIVE",
    "message": "Không thể gán người thuê vào phòng không hoạt động"
  }
}
```

Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED"
  }
}
```

Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Người dùng không có quyền quản lý người thuê"
  }
}
```

## 4.2 Lấy danh sách người thuê

Endpoint

```http
GET /api/v1/tenants?page=0&size=10&keyword=Nguyen&status=ACTIVE&facilityId=1&roomId=101
```

Tham số truy vấn

| Trường     | Kiểu dữ liệu | Bắt buộc | Mô tả                                                                               |
| ---------- | ------------ | -------- | ----------------------------------------------------------------------------------- |
| page       | number       | Không    | Số trang                                                                            |
| size       | number       | Không    | Số lượng bản ghi trên một trang                                                     |
| keyword    | string       | Không    | Tìm kiếm theo tên người thuê, mã người thuê, email, số điện thoại hoặc số CCCD/CMND |
| status     | string       | Không    | ACTIVE hoặc INACTIVE                                                                |
| facilityId | number       | Không    | Lọc theo cơ sở                                                                      |
| roomId     | number       | Không    | Lọc theo phòng                                                                      |

Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "tenantId": 15,
        "tenantCode": "TEN00015",
        "fullName": "Nguyen Van A",
        "phone": "0912345678",
        "identityNumber": "001203000001",
        "email": "tenant01@example.com",
        "roomId": 101,
        "roomCode": "HL0101",
        "facilityId": 1,
        "facilityName": "Hoa Lac Facility",
        "status": "ACTIVE",
        "accountStatus": "ACTIVE"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

## 4.3 Lấy chi tiết người thuê

Endpoint

```http
GET /api/v1/tenants/{tenantId}
```

Response 200

```json
{
  "success": true,
  "data": {
    "tenantId": 15,
    "tenantCode": "TEN00015",
    "fullName": "Nguyen Van A",
    "phone": "0912345678",
    "identityNumber": "001203000001",
    "email": "tenant01@example.com",
    "status": "ACTIVE",
    "account": {
      "userId": 20,
      "email": "tenant01@example.com",
      "role": "TENANT",
      "accountStatus": "ACTIVE"
    },
    "room": {
      "roomId": 101,
      "roomCode": "HL0101",
      "floor": 1,
      "roomNumber": 1
    },
    "facility": {
      "facilityId": 1,
      "facilityCode": "HL",
      "facilityName": "Hoa Lac Facility"
    },
    "dependents": [
      {
        "dependentId": 1,
        "fullName": "Nguyen Van B",
        "relationship": "Brother",
        "phone": "0987654321"
      }
    ],
    "audit": {
      "createdBy": "manager01",
      "createdAt": "2026-06-11T09:00:00",
      "updatedBy": "manager01",
      "updatedAt": "2026-06-11T09:30:00"
    }
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "TENANT_NOT_FOUND",
    "message": "Không tìm thấy người thuê"
  }
}
```

## 4.4 Kết thúc thuê

Endpoint

```http
PATCH /api/v1/tenants/{tenantId}/end-rental
```

Request

```json
{
  "endDate": "2026-06-11",
  "reason": "Tenant moved out"
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "tenantId": 15,
    "tenantCode": "TEN00015",
    "fullName": "Nguyen Van A",
    "status": "INACTIVE",
    "accountStatus": "DISABLED",
    "endDate": "2026-06-11"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "TENANT_ALREADY_INACTIVE",
    "message": "Người thuê đã kết thúc thuê trước đó"
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "TENANT_NOT_FOUND",
    "message": "Không tìm thấy người thuê"
  }
}
```

## 5. Ràng buộc kỹ thuật

Thời gian phản hồi tối đa: 500ms tại p95.

Danh sách người thuê phải hỗ trợ phân trang.

Danh sách người thuê phải hỗ trợ tìm kiếm theo mã người thuê, họ tên, email, số điện thoại và số CCCD/CMND.

Mã người thuê phải được hệ thống tự động sinh.

Mã người thuê phải là duy nhất và không được phép chỉnh sửa thủ công.

Mỗi phòng chỉ được có một người thuê ở trạng thái ACTIVE tại một thời điểm.

Người thuê ACTIVE là tài khoản đại diện cho phòng.

Người phụ thuộc không có tài khoản đăng nhập riêng.

Email đăng nhập phải là duy nhất trong hệ thống.

Số CCCD/CMND phải là duy nhất trong toàn hệ thống.

Số điện thoại phải là duy nhất đối với các người thuê đang ACTIVE.

Khi tạo mới, người thuê bắt buộc phải được gán vào một phòng đang hoạt động.

Hệ thống không được cho phép tạo người thuê mới cho phòng đã có người thuê ACTIVE.

Hệ thống không được cho phép gán người thuê vào phòng không hoạt động.

Khi người thuê kết thúc thuê, hệ thống phải chuyển trạng thái người thuê thành INACTIVE.

Khi người thuê kết thúc thuê, tài khoản đăng nhập của người thuê phải bị vô hiệu hóa.

Sau khi người thuê chuyển sang INACTIVE, phòng có thể được gán cho người thuê mới.

Chỉ cho phép xóa mềm, không được xóa vật lý dữ liệu.

Dữ liệu lịch sử phải được giữ lại sau khi người thuê chuyển sang trạng thái INACTIVE.

Thông tin audit phải được ghi nhận, bao gồm createdBy, createdAt, updatedBy và updatedAt.

## 6. Định nghĩa trạng thái người thuê

| Trạng thái | Mô tả                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------ |
| ACTIVE     | Người thuê hiện đang là tài khoản đại diện cho một phòng                                               |
| INACTIVE   | Người thuê đã kết thúc thời gian thuê, tài khoản bị vô hiệu hóa nhưng dữ liệu lịch sử vẫn được giữ lại |

Người thuê có trạng thái ACTIVE được xem là người thuê hiện tại của phòng.

Một phòng chỉ được liên kết với một người thuê ACTIVE tại một thời điểm.

Người thuê có trạng thái INACTIVE không còn được xem là người thuê hiện tại của phòng nhưng vẫn phải có thể tra cứu trong lịch sử.

## 7. Phụ thuộc

* Quản lý Cơ sở
* Quản lý Phòng
* Quản lý Người phụ thuộc
* Xác thực và Phân quyền
* Audit Log

## 8. Ngoài phạm vi

* Quản lý hợp đồng thuê
* Quản lý thanh toán tiền thuê
* Quản lý hóa đơn điện nước
* Quản lý lịch sử chuyển phòng chi tiết
* Đồng bộ dữ liệu với hệ thống bên thứ ba
* Quản lý chi tiết tài khoản nhân sự
* Quản lý người ở cùng dưới dạng tài khoản đăng nhập riêng
