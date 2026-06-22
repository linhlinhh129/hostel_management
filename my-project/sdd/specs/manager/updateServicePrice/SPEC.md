# Feature: Quản lý khoản phí và giá dịch vụ

**Status:** Draft  
**Author:** Bùi Đỉnh  
**Reviewer:** [Tên]  
**Date:** [YYYY-MM-DD]  
**Priority:** High

---

# 1. Business Context

Trong quá trình vận hành chung cư hoặc cơ sở cho thuê, các mức giá như giá điện, giá nước và phí dịch vụ có thể thay đổi theo quy định của Ban Quản Lý hoặc theo chính sách vận hành từng thời kỳ.

Nếu các mức giá này không được cập nhật kịp thời trên hệ thống, hóa đơn và công nợ của cư dân có thể bị tính sai, gây ảnh hưởng đến tính minh bạch và độ chính xác trong hoạt động quản lý tài chính.

Mỗi Ban quản lý chỉ phụ trách một hoặc một số cơ sở nhất định. Vì vậy, khi truy cập chức năng quản lý khoản phí và giá dịch vụ, hệ thống cần hiển thị danh sách các khoản phí và mức giá hiện tại của đúng cơ sở mà Ban quản lý đó phụ trách.

Các mức giá này được lưu trực tiếp trong bảng `facilities`. Khi Ban quản lý thay đổi giá điện, giá nước hoặc phí dịch vụ, hệ thống sẽ cập nhật dữ liệu giá tương ứng trong bảng `facilities`.

Tính năng này cho phép Ban quản lý:

- Xem danh sách các khoản phí và giá hiện tại của cơ sở mình phụ trách.
- Chọn một khoản phí/dịch vụ cần thay đổi.
- Mở pop-up cập nhật giá mới.
- Lưu giá mới vào hệ thống.
- Theo dõi thông tin người cập nhật và thời gian cập nhật.

Tính năng này giúp đảm bảo các hóa đơn phát sinh sau thời điểm cập nhật sẽ sử dụng đúng mức giá mới nhất.

---

# 2. User Stories

## Story 1: Xem danh sách khoản phí và giá dịch vụ hiện tại

Là Ban quản lý,  
tôi muốn xem danh sách các khoản phí và giá dịch vụ hiện tại của cơ sở mà tôi phụ trách  
để biết hệ thống đang áp dụng mức giá nào khi tính hóa đơn.

---

## Story 2: Mở pop-up thay đổi giá

Là Ban quản lý,  
tôi muốn chọn nút thay đổi tại một khoản phí/dịch vụ  
để mở pop-up cập nhật giá mới cho khoản phí/dịch vụ đó.

---

## Story 3: Cập nhật giá điện

Là Ban quản lý,  
tôi muốn cập nhật giá điện của cơ sở  
để hệ thống sử dụng giá điện mới khi tạo hóa đơn sau thời điểm cập nhật.

---

## Story 4: Cập nhật giá nước

Là Ban quản lý,  
tôi muốn cập nhật giá nước của cơ sở  
để hệ thống sử dụng giá nước mới khi tạo hóa đơn sau thời điểm cập nhật.

---

## Story 5: Cập nhật phí dịch vụ

Là Ban quản lý,  
tôi muốn cập nhật phí dịch vụ của cơ sở  
để hệ thống sử dụng phí dịch vụ mới khi tạo hóa đơn sau thời điểm cập nhật.

---

## Story 6: Kiểm tra dữ liệu không hợp lệ

Là Ban quản lý,  
khi nhập giá không hợp lệ,  
tôi muốn hệ thống từ chối thao tác và hiển thị thông báo lỗi phù hợp.

---

## Story 7: Theo dõi lịch sử thay đổi

Là Ban quản lý,  
tôi muốn hệ thống lưu lại giá cũ, giá mới, người thực hiện và thời gian thay đổi  
để dễ dàng kiểm tra khi cần.

---

## Story 8: Kiểm tra quyền truy cập

Là hệ thống,  
khi người dùng không có quyền truy cập chức năng này,  
tôi muốn từ chối truy cập để đảm bảo dữ liệu giá không bị thay đổi sai quyền.

---

# 3. Acceptance Criteria (EARS)

## AC-01: Hiển thị danh sách khoản phí và giá dịch vụ hiện tại

KHI Ban quản lý truy cập màn hình Quản lý khoản phí và giá dịch vụ,  
THE SYSTEM SHALL hiển thị danh sách các khoản phí và giá hiện tại của cơ sở mà Ban quản lý đó phụ trách.

KHI danh sách được hiển thị,  
THE SYSTEM SHALL hiển thị các thông tin sau:

- Tên cơ sở
- Mã cơ sở
- Tên khoản phí/dịch vụ
- Loại khoản phí/dịch vụ
- Đơn vị tính
- Giá hiện tại
- Thời gian cập nhật gần nhất
- Người cập nhật gần nhất
- Nút Thay đổi

Ví dụ danh sách hiển thị:

| Tên khoản phí/dịch vụ | Loại | Đơn vị tính | Giá hiện tại | Hành động |
| --- | --- | --- | ---: | --- |
| Giá điện | ELECTRICITY | VNĐ/kWh | 3,500 | Thay đổi |
| Giá nước | WATER | VNĐ/m³ | 15,000 | Thay đổi |
| Phí dịch vụ | SERVICE_FEE | VNĐ/tháng | 100,000 | Thay đổi |

KHI cơ sở không có dữ liệu giá,  
THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại chưa có dữ liệu phí và giá dịch vụ.
```

---

## AC-02: Chỉ hiển thị dữ liệu của cơ sở mà Ban quản lý phụ trách

KHI Ban quản lý truy cập màn hình Quản lý khoản phí và giá dịch vụ,\
THE SYSTEM SHALL chỉ hiển thị dữ liệu giá của cơ sở mà Ban quản lý đó phụ trách.

KHI Ban quản lý không phụ trách cơ sở nào,\
THE SYSTEM SHALL hiển thị thông báo:

```text
Bạn chưa được phân quyền quản lý cơ sở nào.
```

KHI Ban quản lý cố truy cập dữ liệu của cơ sở không thuộc quyền quản lý,\
THE SYSTEM SHALL từ chối truy cập và trả về HTTP 403 với mã lỗi `FACILITY_ACCESS_DENIED`.

---

## AC-03: Mở pop-up thay đổi giá

KHI Ban quản lý chọn nút Thay đổi tại một khoản phí/dịch vụ,\
THE SYSTEM SHALL mở pop-up cập nhật giá.

KHI pop-up cập nhật giá được hiển thị,\
THE SYSTEM SHALL hiển thị các thông tin sau:

- Tên khoản phí/dịch vụ

- Loại khoản phí/dịch vụ

- Đơn vị tính

- Giá hiện tại

- Ô nhập giá mới

- Ghi chú thay đổi

- Nút Lưu thay đổi

- Nút Hủy

KHI pop-up được mở,\
THE SYSTEM SHALL không cho phép chỉnh sửa trực tiếp tên khoản phí/dịch vụ và loại khoản phí/dịch vụ.

KHI Ban quản lý chọn Hủy,\
THE SYSTEM SHALL đóng pop-up và không thay đổi dữ liệu.

---

## AC-04: Cập nhật giá thành công

KHI Ban quản lý nhập giá mới hợp lệ và chọn Lưu thay đổi,\
THE SYSTEM SHALL cập nhật giá mới vào bảng `facilities`.

KHI cập nhật thành công,\
THE SYSTEM SHALL đóng pop-up.

KHI cập nhật thành công,\
THE SYSTEM SHALL hiển thị thông báo:

```text
Cập nhật giá thành công.
```

KHI cập nhật thành công,\
THE SYSTEM SHALL tải lại danh sách khoản phí và giá dịch vụ để hiển thị giá mới nhất.

KHI cập nhật thành công,\
THE SYSTEM SHALL lưu thông tin:

- Giá cũ

- Giá mới

- Loại giá được cập nhật

- Ghi chú thay đổi

- Thời gian cập nhật

- Người cập nhật

---

## AC-05: Cập nhật giá điện

KHI Ban quản lý cập nhật giá của loại `ELECTRICITY`,\
THE SYSTEM SHALL cập nhật giá điện của cơ sở trong bảng `facilities`.

KHI giá điện được cập nhật thành công,\
THE SYSTEM SHALL sử dụng giá điện mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật giá điện,\
THE SYSTEM SHALL không tự động thay đổi lại tiền điện của hóa đơn cũ.

---

## AC-06: Cập nhật giá nước

KHI Ban quản lý cập nhật giá của loại `WATER`,\
THE SYSTEM SHALL cập nhật giá nước của cơ sở trong bảng `facilities`.

KHI giá nước được cập nhật thành công,\
THE SYSTEM SHALL sử dụng giá nước mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật giá nước,\
THE SYSTEM SHALL không tự động thay đổi lại tiền nước của hóa đơn cũ.

---

## AC-07: Cập nhật phí dịch vụ

KHI Ban quản lý cập nhật giá của loại `SERVICE_FEE`,\
THE SYSTEM SHALL cập nhật phí dịch vụ của cơ sở trong bảng `facilities`.

KHI phí dịch vụ được cập nhật thành công,\
THE SYSTEM SHALL sử dụng phí dịch vụ mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật phí dịch vụ,\
THE SYSTEM SHALL không tự động thay đổi lại phí dịch vụ của hóa đơn cũ.

---

## AC-08: Giá không hợp lệ

KHI Ban quản lý nhập giá nhỏ hơn hoặc bằng 0,\
THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE`.

KHI Ban quản lý nhập giá không phải là số,\
THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE_FORMAT`.

KHI Ban quản lý bỏ trống giá mới,\
THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `REQUIRED_FIELD_MISSING`.

---

## AC-09: Loại giá không hợp lệ

KHI Ban quản lý cập nhật loại giá không tồn tại trong hệ thống,\
THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE_TYPE`.

Các loại giá hợp lệ bao gồm:

- `ELECTRICITY`

- `WATER`

- `SERVICE_FEE`

---

## AC-10: Cơ sở không tồn tại

KHI cơ sở không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `FACILITY_NOT_FOUND`.

---

## AC-11: Không đủ quyền truy cập

KHI người dùng chưa đăng nhập,\
THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`.

KHI người dùng đã đăng nhập nhưng không có vai trò Ban quản lý,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

KHI người dùng có vai trò Ban quản lý nhưng không phụ trách cơ sở được yêu cầu,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FACILITY_ACCESS_DENIED`.

---

## AC-12: Lưu lịch sử thay đổi

KHI khoản phí hoặc giá dịch vụ được cập nhật thành công,\
THE SYSTEM SHALL lưu lịch sử thay đổi.

Thông tin lịch sử thay đổi bao gồm:

- Mã cơ sở

- Tên cơ sở

- Loại giá được thay đổi

- Giá cũ

- Giá mới

- Ghi chú thay đổi

- Người thực hiện

- Thời gian thực hiện

---

## AC-13: Ngăn gửi trùng yêu cầu

WHILE hệ thống đang xử lý yêu cầu cập nhật giá,\
THE SYSTEM SHALL vô hiệu hóa nút Lưu thay đổi trên pop-up.

WHILE hệ thống đang xử lý yêu cầu cập nhật giá,\
THE SYSTEM SHALL không cho phép gửi nhiều yêu cầu liên tiếp từ cùng một phiên làm việc.

---

# 4. API Contract

## 4.1 Lấy danh sách khoản phí và giá dịch vụ hiện tại của cơ sở

### Endpoint

```http
GET /api/v1/facilities/current/prices
```

### Description

API này trả về danh sách các khoản phí và giá dịch vụ hiện tại của cơ sở mà Ban quản lý đang đăng nhập phụ trách.

Hệ thống xác định cơ sở dựa trên thông tin tài khoản đăng nhập của Ban quản lý.

### Request Headers

| Header | Required | Description |
| --- | --- | --- |
| Authorization | Yes | Bearer token của người dùng đang đăng nhập |

### Response 200

```json
{
  "success": true,
  "data": {
    "facilityId": 1,
    "facilityCode": "HN01",
    "facilityName": "Chung cư Hà Nội 01",
    "prices": [
      {
        "priceType": "ELECTRICITY",
        "priceName": "Giá điện",
        "unit": "VNĐ/kWh",
        "currentPrice": 3500,
        "updatedAt": "2026-06-11T10:30:00Z",
        "updatedBy": 5,
        "updatedByName": "Nguyễn Văn A"
      },
      {
        "priceType": "WATER",
        "priceName": "Giá nước",
        "unit": "VNĐ/m3",
        "currentPrice": 15000,
        "updatedAt": "2026-06-11T10:30:00Z",
        "updatedBy": 5,
        "updatedByName": "Nguyễn Văn A"
      },
      {
        "priceType": "SERVICE_FEE",
        "priceName": "Phí dịch vụ",
        "unit": "VNĐ/tháng",
        "currentPrice": 100000,
        "updatedAt": "2026-06-11T10:30:00Z",
        "updatedBy": 5,
        "updatedByName": "Nguyễn Văn A"
      }
    ]
  }
}
```

### Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Người dùng chưa đăng nhập."
  }
}
```

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Người dùng không có quyền truy cập chức năng này."
  }
}
```

### Response 404

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "Không tìm thấy cơ sở mà người dùng phụ trách."
  }
}
```

---

## 4.2 Cập nhật giá dịch vụ của cơ sở

### Endpoint

```http
PUT /api/v1/facilities/current/prices/{priceType}
```

### Description

API này cập nhật giá của một loại phí/dịch vụ thuộc cơ sở mà Ban quản lý đang đăng nhập phụ trách.

Dữ liệu giá được cập nhật trực tiếp vào bảng `facilities`.

### Path Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| priceType | string | Yes | Loại giá cần cập nhật: `ELECTRICITY`, `WATER`, `SERVICE_FEE` |

### Request Headers

| Header | Required | Description |
| --- | --- | --- |
| Authorization | Yes | Bearer token của người dùng đang đăng nhập |

### Request Body

```json
{
  "newPrice": 3800,
  "note": "Cập nhật giá điện theo quy định mới"
}
```

### Request Body Parameters

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| newPrice | decimal | Yes | Giá mới cần cập nhật |
| note | string | No | Ghi chú lý do thay đổi giá |

### Response 200

```json
{
  "success": true,
  "data": {
    "facilityId": 1,
    "facilityCode": "HN01",
    "priceType": "ELECTRICITY",
    "priceName": "Giá điện",
    "oldPrice": 3500,
    "newPrice": 3800,
    "unit": "VNĐ/kWh",
    "updatedAt": "2026-06-11T11:00:00Z",
    "updatedBy": 5,
    "updatedByName": "Nguyễn Văn A"
  }
}
```

### Response 400 - Giá không hợp lệ

```json
{
  "success": false,
  "error": {
    "code": "INVALID_PRICE",
    "message": "Giá phải lớn hơn 0."
  }
}
```

### Response 400 - Thiếu trường bắt buộc

```json
{
  "success": false,
  "error": {
    "code": "REQUIRED_FIELD_MISSING",
    "message": "Vui lòng nhập giá mới."
  }
}
```

### Response 400 - Loại giá không hợp lệ

```json
{
  "success": false,
  "error": {
    "code": "INVALID_PRICE_TYPE",
    "message": "Loại giá không hợp lệ."
  }
}
```

### Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Người dùng chưa đăng nhập."
  }
}
```

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Người dùng không có quyền thực hiện chức năng này."
  }
}
```

### Response 404

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "Không tìm thấy cơ sở cần cập nhật."
  }
}
```

---

## 4.3 Lấy lịch sử thay đổi giá của cơ sở

### Endpoint

```http
GET /api/v1/facilities/current/prices/history
```

### Description

API này trả về lịch sử thay đổi giá điện, giá nước và phí dịch vụ của cơ sở mà Ban quản lý đang đăng nhập phụ trách.

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| priceType | string | No | Lọc theo loại giá: `ELECTRICITY`, `WATER`, `SERVICE_FEE` |
| page | number | No | Trang hiện tại |
| size | number | No | Số bản ghi trên một trang |

### Response 200

```json
{
  "success": true,
  "data": [
    {
      "historyId": 1,
      "facilityId": 1,
      "facilityCode": "HN01",
      "priceType": "ELECTRICITY",
      "priceName": "Giá điện",
      "oldPrice": 3500,
      "newPrice": 3800,
      "unit": "VNĐ/kWh",
      "note": "Cập nhật giá điện theo quy định mới",
      "changedAt": "2026-06-11T11:00:00Z",
      "changedBy": 5,
      "changedByName": "Nguyễn Văn A"
    }
  ]
}
```

---

# 5. UI/UX Specification

## 5.1 Màn hình danh sách khoản phí và giá dịch vụ

Màn hình Quản lý khoản phí và giá dịch vụ hiển thị danh sách giá hiện tại của cơ sở mà Ban quản lý phụ trách.

### Thành phần giao diện

- Tiêu đề màn hình: `Quản lý khoản phí và giá dịch vụ`

- Thông tin cơ sở:

  - Mã cơ sở

  - Tên cơ sở

- Bảng danh sách phí và giá dịch vụ

- Nút Thay đổi tại từng dòng

### Cột trong bảng

| Cột | Mô tả |
| --- | --- |
| STT | Số thứ tự |
| Tên khoản phí/dịch vụ | Ví dụ: Giá điện, Giá nước, Phí dịch vụ |
| Loại | `ELECTRICITY`, `WATER`, `SERVICE_FEE` |
| Đơn vị tính | VNĐ/kWh, VNĐ/m3, VNĐ/tháng |
| Giá hiện tại | Mức giá đang áp dụng |
| Cập nhật lần cuối | Thời gian cập nhật gần nhất |
| Người cập nhật | Người cập nhật gần nhất |
| Hành động | Nút Thay đổi |

---

## 5.2 Pop-up cập nhật giá

Khi Ban quản lý chọn nút Thay đổi, hệ thống hiển thị pop-up cập nhật giá.

### Thành phần pop-up

- Tiêu đề: `Thay đổi giá dịch vụ`

- Tên khoản phí/dịch vụ

- Loại khoản phí/dịch vụ

- Đơn vị tính

- Giá hiện tại

- Ô nhập giá mới

- Ô nhập ghi chú

- Nút Hủy

- Nút Lưu thay đổi

### Quy tắc hiển thị

- Tên khoản phí/dịch vụ chỉ đọc.

- Loại khoản phí/dịch vụ chỉ đọc.

- Giá hiện tại chỉ đọc.

- Giá mới bắt buộc nhập.

- Ghi chú không bắt buộc.

- Khi đang xử lý cập nhật, nút Lưu thay đổi bị disable.

---

# 6. Business Rules

## 6.1 Quy tắc dữ liệu theo cơ sở

Mỗi Ban quản lý chỉ được xem và cập nhật giá của cơ sở mà mình phụ trách.

Hệ thống không cho phép Ban quản lý cập nhật giá của cơ sở khác.

---

## 6.2 Quy tắc lưu dữ liệu giá

Các giá dịch vụ được lưu trong bảng `facilities`.

Ví dụ mapping dữ liệu:

| Loại giá | Cột trong bảng `facilities` | Đơn vị |
| --- | --- | --- |
| `ELECTRICITY` | `electricity_price` | VNĐ/kWh |
| `WATER` | `water_price` | VNĐ/m3 |
| `SERVICE_FEE` | `service_fee` | VNĐ/tháng |

Khi cập nhật giá, hệ thống chỉ cập nhật đúng cột tương ứng với `priceType`.

Ví dụ:

```text
priceType = ELECTRICITY
=> cập nhật facilities.electricity_price
```

```text
priceType = WATER
=> cập nhật facilities.water_price
```

```text
priceType = SERVICE_FEE
=> cập nhật facilities.service_fee
```

---

## 6.3 Quy tắc giá hợp lệ

Giá mới phải lớn hơn 0.

Giá mới phải là số hợp lệ.

Giá mới không được để trống.

Hệ thống không cho phép nhập giá âm hoặc bằng 0.

---

## 6.4 Quy tắc áp dụng giá mới

Giá mới chỉ được áp dụng cho các hóa đơn được tạo sau thời điểm cập nhật.

Các hóa đơn đã được tạo trước thời điểm cập nhật không bị tự động thay đổi lại.

Ví dụ:

```text
Ngày 01/06/2026: Tạo hóa đơn tháng 06 với giá điện 3,500 VNĐ/kWh
Ngày 10/06/2026: Ban quản lý cập nhật giá điện thành 3,800 VNĐ/kWh

=> Hóa đơn đã tạo ngày 01/06/2026 vẫn giữ giá điện 3,500 VNĐ/kWh
=> Hóa đơn tạo sau ngày 10/06/2026 sử dụng giá điện 3,800 VNĐ/kWh
```

---

## 6.5 Quy tắc lịch sử thay đổi

Mỗi lần cập nhật giá thành công, hệ thống phải lưu lịch sử thay đổi.

Thông tin cần lưu gồm:

- Cơ sở được cập nhật

- Loại giá được cập nhật

- Giá cũ

- Giá mới

- Ghi chú thay đổi

- Người cập nhật

- Thời gian cập nhật

---

# 7. Database Impact

## 7.1 Bảng chính: `facilities`

Các giá dịch vụ hiện tại được lưu trong bảng `facilities`.

Các trường giá đề xuất:

```sql
facilities
---------
facility_id
facility_code
facility_name
electricity_price
water_price
service_fee
updated_at
updated_by
```

Trong đó:

| Field | Description |
| --- | --- |
| `facility_id` | ID của cơ sở |
| `facility_code` | Mã cơ sở |
| `facility_name` | Tên cơ sở |
| `electricity_price` | Giá điện hiện tại |
| `water_price` | Giá nước hiện tại |
| `service_fee` | Phí dịch vụ hiện tại |
| `updated_at` | Thời gian cập nhật gần nhất |
| `updated_by` | Người cập nhật gần nhất |

---

## 7.2 Bảng lịch sử thay đổi giá

Để phục vụ audit log, hệ thống nên có bảng lưu lịch sử thay đổi giá.

Tên bảng đề xuất:

```sql
facility_price_histories
```

Các trường đề xuất:

```sql
facility_price_histories
------------------------
history_id
facility_id
price_type
old_price
new_price
unit
note
changed_at
changed_by
```

Trong đó:

| Field | Description |
| --- | --- |
| `history_id` | ID lịch sử |
| `facility_id` | ID cơ sở được cập nhật |
| `price_type` | Loại giá được cập nhật |
| `old_price` | Giá cũ |
| `new_price` | Giá mới |
| `unit` | Đơn vị tính |
| `note` | Ghi chú thay đổi |
| `changed_at` | Thời gian thay đổi |
| `changed_by` | Người thực hiện thay đổi |

---

# 8. Technical Constraints

- Chỉ người dùng có vai trò Ban quản lý được phép truy cập chức năng.

- Ban quản lý chỉ được xem và cập nhật dữ liệu giá của cơ sở mình phụ trách.

- Giá điện, giá nước và phí dịch vụ được lưu trong bảng `facilities`.

- Giá mới phải lớn hơn 0.

- Giá mới không được để trống.

- Giá mới phải là số hợp lệ.

- Hệ thống phải lưu lịch sử thay đổi giá.

- Các hóa đơn đã phát hành trước thời điểm cập nhật không bị ảnh hưởng bởi giá mới.

- Các hóa đơn được tạo sau thời điểm cập nhật sẽ sử dụng giá mới nhất.

- Hệ thống phải ngăn gửi trùng yêu cầu cập nhật trong khi đang xử lý.

- API danh sách giá hiện tại: `< 500ms (p95)`.

- API cập nhật giá: `< 500ms (p95)`.

- API lịch sử thay đổi giá: `< 1000ms (p95)`.

- Giới hạn: `100 requests/phút/người dùng`.

---

# 9. Error Codes

| Error Code | HTTP Status | Description |
| --- | --- | --- |
| `UNAUTHORIZED` | 401 | Người dùng chưa đăng nhập |
| `FORBIDDEN` | 403 | Người dùng không có quyền truy cập chức năng |
| `FACILITY_ACCESS_DENIED` | 403 | Người dùng không phụ trách cơ sở này |
| `FACILITY_NOT_FOUND` | 404 | Không tìm thấy cơ sở |
| `INVALID_PRICE` | 400 | Giá phải lớn hơn 0 |
| `INVALID_PRICE_FORMAT` | 400 | Giá không đúng định dạng số |
| `REQUIRED_FIELD_MISSING` | 400 | Thiếu trường bắt buộc |
| `INVALID_PRICE_TYPE` | 400 | Loại giá không hợp lệ |
| `UPDATE_PRICE_FAILED` | 500 | Cập nhật giá thất bại |

---

# 10. Out of Scope

- Quy trình phê duyệt thay đổi giá.

- Gửi thông báo tự động cho cư dân khi thay đổi giá.

- Import danh sách khoản phí từ Excel.

- Thiết lập giá có hiệu lực trong tương lai.

- Tích hợp với hệ thống kế toán hoặc thanh toán bên thứ ba.

- Tự động tính toán lại hóa đơn đã phát hành trước đó.

- Tạo mới loại khoản phí động nếu bảng `facilities` không có cột tương ứng.

- Xóa khoản phí hoặc giá dịch vụ khỏi hệ thống.

- Quản lý nhiều bảng giá theo từng thời điểm hiệu lực.