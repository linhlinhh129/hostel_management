# Feature: Tiếp nhận và xử lý yêu cầu người thuê

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Bối cảnh nghiệp vụ (Business Context)

Tính năng Tiếp nhận và xử lý yêu cầu người thuê cho phép người thuê gửi các yêu cầu hỗ trợ lên hệ thống và cho phép Ban quản lý tiếp nhận, theo dõi, phân công và xử lý các yêu cầu đó.

Tính năng này giúp chuẩn hóa quy trình xử lý yêu cầu trong nhà trọ, đảm bảo mọi yêu cầu đều được ghi nhận, phân công rõ ràng và theo dõi minh bạch trong toàn bộ quá trình xử lý.

Feature này hỗ trợ mục tiêu nâng cao chất lượng dịch vụ quản lý nhà trọ, cải thiện trải nghiệm người thuê và tăng hiệu quả vận hành của Ban quản lý.

## 2. User Stories

### Story 1 (Luồng chính)

Là Người thuê, tôi muốn gửi yêu cầu hỗ trợ để Ban quản lý có thể tiếp nhận và xử lý vấn đề của tôi.

### Story 2 (Luồng chính)

Là Ban quản lý, tôi muốn tiếp nhận các yêu cầu từ người thuê để có thể xử lý hoặc phân công xử lý phù hợp.

### Story 3 (Luồng chính)

Là Ban quản lý, tôi muốn phân công yêu cầu cho nhân sự phụ trách để đảm bảo yêu cầu được xử lý đúng người và đúng trách nhiệm.

### Story 4 (Luồng chính)

Là Nhân sự, tôi muốn cập nhật trạng thái xử lý yêu cầu được phân công để Ban quản lý có thể theo dõi tiến độ xử lý.

### Story 5 (Luồng chính)

Là Ban quản lý, tôi muốn xem lịch sử xử lý yêu cầu để theo dõi toàn bộ quá trình xử lý.

### Story 6 (Ngoại lệ)

Là Ban quản lý, khi yêu cầu không hợp lệ hoặc không thuộc phạm vi hỗ trợ, tôi muốn từ chối yêu cầu và ghi rõ lý do từ chối.

### Story 7 (Ngoại lệ)

Là Nhân sự, khi không được phân công xử lý yêu cầu, tôi không được phép cập nhật thông tin xử lý của yêu cầu đó.

## 3. Tiêu chí chấp nhận (Acceptance Criteria - EARS)

### AC-01 Gửi yêu cầu

KHI Người thuê gửi yêu cầu với thông tin hợp lệ
HỆ THỐNG PHẢI tạo yêu cầu mới với trạng thái NEW và liên kết yêu cầu với người thuê, phòng và cơ sở tương ứng.

### AC-02 Kiểm tra dữ liệu yêu cầu

KHI Người thuê gửi yêu cầu thiếu thông tin bắt buộc
HỆ THỐNG PHẢI từ chối yêu cầu và hiển thị lỗi kiểm tra dữ liệu.

Các trường bắt buộc bao gồm:

* Tiêu đề yêu cầu
* Nội dung mô tả
* Loại yêu cầu

### AC-03 Xem danh sách yêu cầu

KHI Ban quản lý truy cập màn hình quản lý yêu cầu
HỆ THỐNG PHẢI hiển thị danh sách các yêu cầu của người thuê theo phân trang.

Danh sách hỗ trợ lọc theo:

* Từ khóa
* Trạng thái yêu cầu
* Loại yêu cầu
* Cơ sở
* Phòng
* Nhân sự được phân công

### AC-04 Tiếp nhận yêu cầu

KHI Ban quản lý tiếp nhận một yêu cầu có trạng thái NEW
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu từ NEW sang RECEIVED.

### AC-05 Phân công xử lý

KHI Ban quản lý phân công yêu cầu có trạng thái RECEIVED cho nhân sự
HỆ THỐNG PHẢI lưu thông tin nhân sự được phân công và cập nhật trạng thái yêu cầu sang ASSIGNED.

### AC-06 Cập nhật đang xử lý

KHI Nhân sự được phân công bắt đầu xử lý yêu cầu
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu từ ASSIGNED sang IN_PROGRESS.

### AC-07 Cập nhật đã xử lý

KHI Nhân sự được phân công hoàn thành xử lý yêu cầu
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu từ IN_PROGRESS sang RESOLVED.

### AC-08 Từ chối yêu cầu

KHI Ban quản lý từ chối yêu cầu
HỆ THỐNG PHẢI bắt buộc nhập lý do từ chối và cập nhật trạng thái yêu cầu sang REJECTED.

### AC-09 Xem chi tiết yêu cầu

KHI Ban quản lý xem chi tiết yêu cầu
HỆ THỐNG PHẢI hiển thị:

* Thông tin yêu cầu
* Thông tin người thuê gửi yêu cầu
* Thông tin phòng
* Thông tin cơ sở
* Loại yêu cầu
* File đính kèm nếu có
* Trạng thái hiện tại
* Nhân sự được phân công
* Lịch sử xử lý

### AC-10 Xem lịch sử xử lý

KHI Ban quản lý xem lịch sử xử lý
HỆ THỐNG PHẢI hiển thị toàn bộ các lần thay đổi trạng thái và thao tác đã thực hiện theo thứ tự thời gian.

### AC-11 Nhân sự không được phân công

KHI Nhân sự không được phân công cập nhật yêu cầu
HỆ THỐNG PHẢI từ chối thao tác và trả về lỗi không có quyền xử lý yêu cầu này.

### AC-12 Phân quyền

TRONG KHI người dùng không có quyền xử lý yêu cầu
HỆ THỐNG PHẢI từ chối truy cập các chức năng tiếp nhận, phân công, từ chối và cập nhật trạng thái yêu cầu.

## 4. API Contract

### 4.1 Tạo yêu cầu

Endpoint

```http
POST /api/v1/requests
```

Request

```json
{
  "title": "Máy lạnh không hoạt động",
  "description": "Máy lạnh không làm mát",
  "category": "MAINTENANCE",
  "attachmentUrls": [
    "https://example.com/files/air-conditioner.jpg"
  ]
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "title": "Máy lạnh không hoạt động",
    "status": "NEW"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "REQUEST_VALIDATION_ERROR",
    "message": "Thông tin yêu cầu không hợp lệ"
  }
}
```

### 4.2 Lấy danh sách yêu cầu

Endpoint

```http
GET /api/v1/requests?page=0&size=10&status=NEW&category=MAINTENANCE&facilityId=1&roomId=101&assignedStaffId=15&keyword=may%20lanh
```

Tham số truy vấn

| Trường          | Kiểu dữ liệu | Bắt buộc | Mô tả                                       |
| --------------- | ------------ | -------- | ------------------------------------------- |
| page            | number       | Không    | Số trang                                    |
| size            | number       | Không    | Số lượng bản ghi trên một trang             |
| status          | string       | Không    | Lọc theo trạng thái yêu cầu                 |
| category        | string       | Không    | Lọc theo loại yêu cầu                       |
| facilityId      | number       | Không    | Lọc theo cơ sở                              |
| roomId          | number       | Không    | Lọc theo phòng                              |
| assignedStaffId | number       | Không    | Lọc theo nhân sự được phân công             |
| keyword         | string       | Không    | Tìm kiếm theo tiêu đề hoặc nội dung yêu cầu |

Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "requestId": 101,
        "title": "Máy lạnh không hoạt động",
        "category": "MAINTENANCE",
        "status": "NEW",
        "tenant": {
          "tenantId": 15,
          "tenantCode": "NT000001",
          "fullName": "Nguyen Van A"
        },
        "room": {
          "roomId": 101,
          "roomCode": "HL0101"
        },
        "facility": {
          "facilityId": 1,
          "facilityName": "Hoa Lac Facility"
        },
        "assignedStaffId": null,
        "createdAt": "2026-06-11T09:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 4.3 Tiếp nhận yêu cầu

Endpoint

```http
PUT /api/v1/requests/{id}/receive
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "status": "RECEIVED"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST_STATUS",
    "message": "Chỉ yêu cầu có trạng thái NEW mới được tiếp nhận"
  }
}
```

### 4.4 Phân công xử lý

Endpoint

```http
PUT /api/v1/requests/{id}/assign
```

Request

```json
{
  "staffId": 15
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "staffId": 15,
    "status": "ASSIGNED"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST_STATUS",
    "message": "Chỉ yêu cầu đã tiếp nhận mới được phân công"
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "STAFF_NOT_FOUND",
    "message": "Không tìm thấy nhân sự được phân công"
  }
}
```

### 4.5 Cập nhật đang xử lý

Endpoint

```http
PUT /api/v1/requests/{id}/start-processing
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "status": "IN_PROGRESS"
  }
}
```

Response 403

```json
{
  "success": false,
  "error": {
    "code": "NOT_ASSIGNED_STAFF",
    "message": "Nhân sự không được phân công không được phép cập nhật yêu cầu này"
  }
}
```

### 4.6 Cập nhật đã xử lý

Endpoint

```http
PUT /api/v1/requests/{id}/resolve
```

Request

```json
{
  "resolutionNote": "Đã kiểm tra và sửa lại máy lạnh"
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "status": "RESOLVED"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "RESOLUTION_NOTE_REQUIRED",
    "message": "Bắt buộc nhập nội dung xử lý"
  }
}
```

### 4.7 Từ chối yêu cầu

Endpoint

```http
PUT /api/v1/requests/{id}/reject
```

Request

```json
{
  "reason": "Không thuộc phạm vi hỗ trợ"
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "status": "REJECTED"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "REJECTION_REASON_REQUIRED",
    "message": "Bắt buộc nhập lý do từ chối"
  }
}
```

### 4.8 Xem chi tiết yêu cầu

Endpoint

```http
GET /api/v1/requests/{id}
```

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": 101,
    "title": "Máy lạnh không hoạt động",
    "description": "Máy lạnh không làm mát",
    "category": "MAINTENANCE",
    "status": "ASSIGNED",
    "tenant": {
      "tenantId": 15,
      "tenantCode": "NT000001",
      "fullName": "Nguyen Van A",
      "phone": "0912345678"
    },
    "room": {
      "roomId": 101,
      "roomCode": "HL0101"
    },
    "facility": {
      "facilityId": 1,
      "facilityCode": "HL",
      "facilityName": "Hoa Lac Facility"
    },
    "assignedStaff": {
      "staffId": 15,
      "fullName": "Tran Van B",
      "role": "TECHNICIAN"
    },
    "attachmentUrls": [
      "https://example.com/files/air-conditioner.jpg"
    ],
    "history": [
      {
        "status": "NEW",
        "action": "CREATE_REQUEST",
        "performedBy": "tenant01",
        "performedAt": "2026-06-11T09:00:00",
        "note": "Người thuê tạo yêu cầu"
      },
      {
        "status": "RECEIVED",
        "action": "RECEIVE_REQUEST",
        "performedBy": "manager01",
        "performedAt": "2026-06-11T09:10:00",
        "note": "Ban quản lý tiếp nhận yêu cầu"
      },
      {
        "status": "ASSIGNED",
        "action": "ASSIGN_REQUEST",
        "performedBy": "manager01",
        "performedAt": "2026-06-11T09:20:00",
        "note": "Phân công cho nhân sự kỹ thuật"
      }
    ],
    "audit": {
      "createdBy": "tenant01",
      "createdAt": "2026-06-11T09:00:00",
      "updatedBy": "manager01",
      "updatedAt": "2026-06-11T09:20:00"
    }
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "REQUEST_NOT_FOUND",
    "message": "Không tìm thấy yêu cầu"
  }
}
```

## 5. Ràng buộc kỹ thuật (Technical Constraints)

Thời gian phản hồi tối đa: 500ms tại p95.

Danh sách yêu cầu phải hỗ trợ phân trang.

Danh sách yêu cầu phải hỗ trợ lọc theo trạng thái, loại yêu cầu, cơ sở, phòng và nhân sự được phân công.

Danh sách yêu cầu phải hỗ trợ tìm kiếm theo tiêu đề hoặc nội dung yêu cầu.

Lịch sử xử lý yêu cầu phải được lưu trữ đầy đủ.

Mọi thay đổi trạng thái phải được ghi nhận vào lịch sử xử lý và Audit Log.

Chỉ người dùng có quyền phù hợp mới được tiếp nhận, phân công, từ chối hoặc cập nhật trạng thái yêu cầu.

Chỉ nhân sự được phân công mới được cập nhật trạng thái xử lý yêu cầu.

Không cho phép xóa cứng dữ liệu yêu cầu.

File đính kèm nếu có phải được lưu dưới dạng URL hoặc đường dẫn file, không lưu trực tiếp nội dung file trong bảng request.

Yêu cầu phải được liên kết với người thuê, phòng và cơ sở tại thời điểm tạo.

## 6. Phụ thuộc (Dependencies)

* Quản lý Người thuê
* Quản lý Phòng
* Quản lý Cơ sở
* Quản lý Nhân sự
* Xác thực và Phân quyền
* Audit Log
* Quản lý File đính kèm nếu hệ thống có upload file

## 7. Quy tắc nghiệp vụ (Business Rules)

### BR-01

Chỉ người thuê có tài khoản ACTIVE mới được gửi yêu cầu.

### BR-02

Khi người thuê gửi yêu cầu, hệ thống phải tự động liên kết yêu cầu với phòng và cơ sở hiện tại của người thuê.

### BR-03

Yêu cầu mới được tạo sẽ có trạng thái mặc định là NEW.

### BR-04

Chỉ các yêu cầu có trạng thái NEW mới được tiếp nhận.

### BR-05

Chỉ các yêu cầu có trạng thái RECEIVED mới được phân công xử lý.

### BR-06

Một yêu cầu chỉ được phân công cho một nhân sự tại cùng một thời điểm.

### BR-07

Chỉ nhân sự được phân công mới được cập nhật trạng thái xử lý yêu cầu.

### BR-08

Yêu cầu có trạng thái ASSIGNED mới được chuyển sang IN_PROGRESS.

### BR-09

Yêu cầu có trạng thái IN_PROGRESS mới được chuyển sang RESOLVED.

### BR-10

Khi từ chối yêu cầu, bắt buộc phải nhập lý do từ chối.

### BR-11

Mọi thay đổi trạng thái phải được lưu vào lịch sử xử lý.

### BR-12

Yêu cầu đã ở trạng thái REJECTED hoặc RESOLVED không được tiếp nhận hoặc phân công lại.

## 8. Định nghĩa trạng thái yêu cầu

| Trạng thái  | Mô tả                                 |
| ----------- | ------------------------------------- |
| NEW         | Người thuê vừa tạo yêu cầu            |
| RECEIVED    | Ban quản lý đã tiếp nhận yêu cầu      |
| ASSIGNED    | Yêu cầu đã được phân công cho nhân sự |
| IN_PROGRESS | Nhân sự đang xử lý yêu cầu            |
| RESOLVED    | Yêu cầu đã được xử lý xong            |
| REJECTED    | Yêu cầu bị từ chối                    |

Luồng trạng thái chính:

```text
NEW → RECEIVED → ASSIGNED → IN_PROGRESS → RESOLVED
```

Luồng từ chối:

```text
NEW / RECEIVED → REJECTED
```

## 9. Ngoài phạm vi (Out of Scope)

* Tự động phân công nhân sự xử lý.
* Theo dõi SLA xử lý yêu cầu.
* Đánh giá mức độ hài lòng của người thuê.
* Tích hợp Email hoặc SMS.
* Báo cáo thống kê hiệu suất xử lý yêu cầu.
* Quy trình nghiệm thu hoặc xác nhận hoàn thành từ phía người thuê.
* Thanh toán chi phí sửa chữa.
* Quản lý kho vật tư sửa chữa.
