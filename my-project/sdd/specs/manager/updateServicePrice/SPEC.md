# Feature: Quản lý khoản phí và giá dịch vụ

Status: Draft

Author: Bùi Đỉnh

Reviewer: \[Tên\]

Date: \[YYYY-MM-DD\]

Priority: High

---

## 1. Business Context

Trong quá trình vận hành chung cư, đơn giá điện, nước và các loại phí dịch vụ có thể thay đổi theo quy định của Ban Quản Lý hoặc theo chính sách vận hành từng thời kỳ. Nếu các mức giá này không được cập nhật kịp thời trên hệ thống, hóa đơn và công nợ của cư dân có thể bị tính sai, gây ảnh hưởng đến tính minh bạch và độ chính xác của hoạt động quản lý tài chính.

Bên cạnh đó, Ban Quản Lý có thể phát sinh nhu cầu thu thêm các khoản phí mới như phí gửi xe, phí vệ sinh, phí bảo trì hoặc các khoản thu khác. Hệ thống cần hỗ trợ tạo mới các khoản phí này và cho phép điều chỉnh mức giá khi cần thiết.

Tính năng này cho phép Quản lý tài chính quản lý danh mục các khoản phí, cập nhật giá dịch vụ và theo dõi lịch sử thay đổi nhằm đảm bảo các khoản thu được tính toán chính xác theo mức giá mới nhất.

---

## 2. User Stories

### Story 1 (Cập nhật giá dịch vụ)

Là Ban quản lý, tôi muốn cập nhật giá điện, nước hoặc phí dịch vụ để hệ thống sử dụng mức giá mới khi tính toán hóa đơn.

### Story 2 (Thêm khoản phí mới)

Là Ban quản lý, tôi muốn tạo mới một khoản phí để hệ thống có thể sử dụng khoản phí đó khi lập hóa đơn và thu tiền cư dân.

### Story 3 (Điều chỉnh giá khoản phí)

Là Ban quản lý, tôi muốn thay đổi mức giá của một khoản phí hiện có để hệ thống áp dụng mức giá mới cho các hóa đơn phát sinh sau thời điểm cập nhật.

### Story 4 (Kiểm tra dữ liệu không hợp lệ)

Là Ban quản lý, khi nhập dữ liệu không hợp lệ, tôi muốn hệ thống từ chối thao tác và hiển thị thông báo lỗi phù hợp.

### Story 5 (Theo dõi lịch sử thay đổi)

Là Ban quản lý, tôi muốn hệ thống lưu lại thông tin người thực hiện và thời gian thay đổi để dễ dàng kiểm tra khi cần.

---

## 3. Acceptance Criteria (EARS)

### AC-01: Cập nhật giá dịch vụ thành công

WHEN Ban quản lý gửi yêu cầu cập nhật giá dịch vụ với dữ liệu hợp lệ

THE SYSTEM SHALL cập nhật giá dịch vụ mới và trả về thông báo thành công.

### AC-02: Tạo khoản phí mới thành công

WHEN Ban quản lý gửi yêu cầu tạo khoản phí mới với dữ liệu hợp lệ

THE SYSTEM SHALL tạo khoản phí mới và trả về thông báo thành công.

### AC-03: Điều chỉnh giá khoản phí

WHEN Ban quản lý cập nhật giá của một khoản phí hiện có

THE SYSTEM SHALL lưu mức giá mới và áp dụng cho các hóa đơn được tạo sau thời điểm cập nhật.

### AC-04: Giá không hợp lệ

WHEN Ban quản lý nhập giá nhỏ hơn hoặc bằng 0

THE SYSTEM SHALL từ chối yêu cầu và trả về mã lỗi INVALID_PRICE.

### AC-05: Thiếu thông tin bắt buộc

WHEN Ban quản lý gửi yêu cầu mà không nhập tên khoản phí hoặc giá

THE SYSTEM SHALL trả về mã lỗi REQUIRED_FIELD_MISSING.

### AC-06: Trùng tên khoản phí

WHEN Ban quản lý tạo khoản phí có tên đã tồn tại

THE SYSTEM SHALL từ chối yêu cầu và trả về mã lỗi DUPLICATE_SERVICE_FEE.

### AC-07: Không đủ quyền truy cập

WHEN người dùng không có quyền Ban quản lý thực hiện thao tác

THE SYSTEM SHALL trả về HTTP 401 Unauthorized.

### AC-08: Lưu lịch sử thay đổi

WHEN khoản phí hoặc giá dịch vụ được tạo mới hoặc cập nhật thành công

THE SYSTEM SHALL lưu giá cũ, giá mới, thời gian thay đổi và người thực hiện.

### AC-09: Ngăn gửi trùng yêu cầu

WHILE hệ thống đang xử lý yêu cầu

THE SYSTEM SHALL không cho phép gửi nhiều yêu cầu liên tiếp từ cùng một phiên làm việc.

---

## 4. API Contract

### API 1: Tạo khoản phí mới

#### Endpoint

POST /api/v1/service-fees

#### Request

```json
{
  "feeName": "Phí gửi xe",
  "price": 100000,
  "description": "Phí gửi xe tháng"
}
```

#### Parameters

| Trường | Kiểu dữ liệu | Bắt buộc | Mô tả |
| --- | --- | --- | --- |
| feeName | string | Có | Tên khoản phí |
| price | decimal | Có | Giá khoản phí |
| description | string | Không | Mô tả khoản phí |

#### Response 201

```json
{
  "success": true,
  "data": {
    "id": 1,
    "feeName": "Phí gửi xe",
    "price": 100000,
    "createdAt": "2026-06-11T10:30:00Z"
  }
}
```

---

### API 2: Cập nhật giá khoản phí

#### Endpoint

PUT /api/v1/service-fees/{feeId}

#### Request

```json
{
  "price": 120000
}
```

#### Parameters

| Trường | Kiểu dữ liệu | Bắt buộc | Mô tả |
| --- | --- | --- | --- |
| feeId | integer | Có | ID khoản phí |
| price | decimal | Có | Giá mới |

#### Response 200

```json
{
  "success": true,
  "data": {
    "feeId": 1,
    "price": 120000,
    "updatedAt": "2026-06-11T11:00:00Z"
  }
}
```

---

### API 3: Cập nhật giá dịch vụ

#### Endpoint

PUT /api/v1/service-prices/{serviceType}

#### Request

```json
{
  "price": 3500
}
```

#### Parameters

| Trường | Kiểu dữ liệu | Bắt buộc | Mô tả |
| --- | --- | --- | --- |
| serviceType | string | Có | ELECTRICITY, WATER, SERVICE |
| price | decimal | Có | Giá dịch vụ mới |

#### Response 200

```json
{
  "success": true,
  "data": {
    "serviceType": "ELECTRICITY",
    "price": 3500,
    "updatedAt": "2026-06-11T10:30:00Z"
  }
}
```

---

### Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_PRICE",
    "message": "Giá phải lớn hơn 0."
  }
}
```

### Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Người dùng không có quyền thực hiện chức năng này."
  }
}
```

### Response 409

```json
{
  "success": false,
  "error": {
    "code": "DUPLICATE_SERVICE_FEE",
    "message": "Khoản phí đã tồn tại."
  }
}
```

---

## 5. Technical Constraints

- Thời gian phản hồi tối đa: 500ms (p95)
- Giới hạn: 100 yêu cầu/phút/người dùng
- Giá khoản phí hoặc giá dịch vụ phải lớn hơn 0
- Tên khoản phí không được trùng với khoản phí đã tồn tại
- Chỉ người dùng có vai trò Quản lý tài chính được phép thực hiện chức năng
- Mọi thay đổi phải được lưu vào lịch sử hệ thống
- Các hóa đơn đã phát hành trước thời điểm cập nhật không bị ảnh hưởng bởi giá mới
- Hệ thống hỗ trợ các loại dịch vụ mặc định:
  - Điện
  - Nước
  - Phí dịch vụ
- Hệ thống hỗ trợ tạo thêm các khoản phí tùy chỉnh phục vụ việc thu tiền cư dân

---

## 6. Out of Scope

- Quy trình phê duyệt thay đổi giá hoặc tạo khoản phí
- Gửi thông báo tự động cho cư dân khi thay đổi giá
- Import danh sách khoản phí từ Excel
- Thiết lập giá có hiệu lực trong tương lai
- Tích hợp với hệ thống kế toán hoặc thanh toán bên thứ ba
- Tự động tính toán hóa đơn định kỳ