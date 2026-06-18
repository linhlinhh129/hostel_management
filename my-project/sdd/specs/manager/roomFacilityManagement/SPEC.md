# Feature: Vận hành Cơ sở được phân công

**Status:** Draft
**Author:** [Tên] | **Reviewer:** [Tên] | **Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Quản lý phòng cơ sở cho phép Ban Quản Lý xem danh sách cơ sở được phân công và theo dõi thông tin phòng thuộc các cơ sở đó.

Ban Quản Lý chỉ được xem dữ liệu cơ sở và phòng trong phạm vi cơ sở được Admin phân công. Hệ thống không cho phép Ban Quản Lý truy cập dữ liệu của cơ sở ngoài phạm vi quản lý.

Tính năng này giúp đảm bảo việc quản lý cơ sở và phòng được phân quyền rõ ràng, hỗ trợ Ban Quản Lý theo dõi tình trạng phòng một cách chính xác và đúng phạm vi trách nhiệm.

## 2. User Stories

### Story 1 (Happy Path)

As a Ban Quản Lý, I want to xem cơ sở được phân công so that tôi biết phạm vi cơ sở mình cần quản lý.

### Story 2 (Happy Path)

As a Ban Quản Lý, I want to xem danh sách phòng trong cơ sở được phân công so that tôi có thể theo dõi tình trạng phòng.

### Story 3 (Happy Path)

As a Ban Quản Lý, I want to xem chi tiết phòng trong cơ sở được phân công so that tôi có thể nắm được thông tin cụ thể của từng phòng.

### Story 4 (Edge Case)

As a Ban Quản Lý, when tôi truy cập cơ sở ngoài phạm vi được phân công, I want hệ thống từ chối truy cập để đảm bảo đúng phạm vi quyền hạn.

### Story 5 (Edge Case)

As a Ban Quản Lý, when tôi chưa được phân công cơ sở nào, I want hệ thống hiển thị thông báo phù hợp để biết rằng tôi chưa có phạm vi quản lý.

## 3. Acceptance Criteria (EARS)

### 3.1 Xem danh sách cơ sở được phân công

WHEN Ban Quản Lý truy cập màn hình Quản lý vận hành cơ sở
THE SYSTEM SHALL hiển thị danh sách cơ sở mà Ban Quản Lý được phân công.

WHEN Ban Quản Lý chưa được phân công cơ sở nào
THE SYSTEM SHALL hiển thị thông báo "Bạn chưa được phân công cơ sở quản lý".

WHEN hệ thống hiển thị danh sách cơ sở được phân công
THE SYSTEM SHALL hiển thị tối thiểu các thông tin:

* Mã cơ sở
* Tên cơ sở
* Địa chỉ
* Trạng thái cơ sở
* Tổng số phòng

### 3.2 Xem danh sách phòng theo cơ sở

WHEN Ban Quản Lý chọn một cơ sở được phân công
THE SYSTEM SHALL hiển thị danh sách phòng thuộc cơ sở đó.

WHEN hiển thị danh sách phòng
THE SYSTEM SHALL hiển thị tối thiểu các thông tin:

* Mã phòng
* Số phòng
* Trạng thái phòng

WHEN danh sách phòng có nhiều dữ liệu
THE SYSTEM SHALL hỗ trợ phân trang.

WHEN cơ sở không thuộc phạm vi được phân công
THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi FACILITY_ACCESS_DENIED.

WHEN cơ sở không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi FACILITY_NOT_FOUND.

### 3.3 Xem chi tiết phòng

WHEN Ban Quản Lý chọn một phòng thuộc cơ sở được phân công
THE SYSTEM SHALL hiển thị thông tin chi tiết của phòng.

WHEN hiển thị chi tiết phòng
THE SYSTEM SHALL hiển thị tối thiểu các thông tin:

* Mã phòng
* Cơ sở
* Số phòng
* Trạng thái phòng
* Ngày tạo
* Ngày cập nhật

WHEN phòng không tồn tại
THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi ROOM_NOT_FOUND.

WHEN phòng thuộc cơ sở ngoài phạm vi được phân công
THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi FACILITY_ACCESS_DENIED.

### 3.4 Phân quyền dữ liệu

WHILE người dùng có vai trò MANAGER
THE SYSTEM SHALL chỉ cho phép xem dữ liệu cơ sở và phòng thuộc các cơ sở được phân công.

WHILE người dùng không có vai trò MANAGER
THE SYSTEM SHALL từ chối truy cập chức năng Quản lý vận hành cơ sở.

WHEN Ban Quản Lý chưa được gán cơ sở quản lý
THE SYSTEM SHALL không cho phép truy cập dữ liệu của bất kỳ cơ sở nào.

## 4. API Contract

### 4.1 Lấy danh sách cơ sở được phân công

Endpoint:

```http
GET /api/v1/manager/facilities
```

Response 200:

```json
{
  "success": true,
  "data": [
    {
      "id": "FAC001",
      "name": "Cơ sở Hòa Lạc",
      "address": "Hòa Lạc, Hà Nội",
      "status": "ACTIVE",
      "totalRooms": 20
    }
  ]
}
```

Response 403:

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

### 4.2 Lấy danh sách phòng theo cơ sở

Endpoint:

```http
GET /api/v1/manager/facilities/{facilityId}/rooms?page=1&size=20
```

Response 200:

```json
{
  "success": true,
  "data": [
    {
      "id": "ROOM001",
      "roomCode": "HL0101",
      "floor": 1,
      "roomNumber": 1,
      "status": "AVAILABLE"
    }
  ],
  "pagination": {
    "page": 1,
    "size": 20,
    "totalItems": 100,
    "totalPages": 5
  }
}
```

Response 403:

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_ACCESS_DENIED",
    "message": "Bạn không có quyền truy cập cơ sở này"
  }
}
```

Response 404:

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "Không tìm thấy cơ sở"
  }
}
```

### 4.3 Lấy chi tiết phòng

Endpoint:

```http
GET /api/v1/manager/rooms/{roomId}
```

Response 200:

```json
{
  "success": true,
  "data": {
    "id": "ROOM001",
    "roomCode": "HL0101",
    "facilityId": "FAC001",
    "facilityName": "Cơ sở Hòa Lạc",
    "floor": 1,
    "roomNumber": 1,
    "status": "AVAILABLE",
    "createdAt": "2026-01-01T08:00:00Z",
    "updatedAt": "2026-01-10T09:00:00Z"
  }
}
```

Response 403:

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_ACCESS_DENIED",
    "message": "Bạn không có quyền truy cập cơ sở này"
  }
}
```

Response 404:

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_FOUND",
    "message": "Không tìm thấy phòng"
  }
}
```

### 4.4 Lỗi chưa đăng nhập

Response 401:

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Người dùng chưa đăng nhập"
  }
}
```

## 5. Technical Constraints

Max response time: 500ms, P95.

Rate limit: 100 requests/minute per user.

Ban Quản Lý chỉ được xem dữ liệu thuộc các cơ sở được phân công.

Hệ thống phải kiểm tra quyền truy cập cơ sở trước khi trả về dữ liệu.

Không cho phép Ban Quản Lý tự gán hoặc thay đổi cơ sở quản lý của mình.

Không cho phép Ban Quản Lý truy cập dữ liệu toàn hệ thống.

Không cho phép người dùng không có vai trò MANAGER truy cập chức năng Quản lý vận hành cơ sở.

Danh sách phòng phải được lọc theo facilityId thuộc phạm vi được phân công.

API phải trả về mã lỗi rõ ràng khi người dùng truy cập sai phạm vi quyền hạn.

Mọi thao tác truy cập dữ liệu quan trọng của Ban Quản Lý phải được ghi log.

## 6. Out of Scope

* Tạo mới cơ sở.
* Cập nhật thông tin cơ sở.
* Xóa cơ sở.
* Gán cơ sở quản lý cho nhân sự.
* Tạo mới tài khoản nhân sự.
* Cập nhật vai trò nhân sự.
* Khóa hoặc mở khóa tài khoản nhân sự.
* Tạo mới phòng.
* Cập nhật thông tin phòng.
* Xóa phòng.
* Quản lý người thuê.
* Xem danh sách người thuê.
* Cập nhật thông tin người thuê.
* Quản lý yêu cầu từ người thuê.
* Quản lý tài chính toàn hệ thống.
* Quản lý phân quyền động.
