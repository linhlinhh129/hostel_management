# Feature: Quản lý hóa đơn

**Status:** Draft  
**Author:** Bùi Đỉnh  
**Reviewer:** [Tên]  
**Date:** [YYYY-MM-DD]  
**Priority:** High

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, hóa đơn là tài liệu tài chính được sử dụng để ghi nhận các khoản phí mà người thuê phải thanh toán trong từng kỳ.

Mỗi hóa đơn được tạo dựa trên thông tin phòng thuê, kỳ hạn hóa đơn, hạn thanh toán, tiền phòng cố định, chỉ số điện, chỉ số nước, đơn giá điện, đơn giá nước, phí dịch vụ, phí khác phát sinh trong kỳ, thuế áp dụng theo quy định của hệ thống và ghi chú nếu có.

Khi Ban quản lý tạo hóa đơn, người dùng không cần nhập thủ công toàn bộ thông tin tiền điện, tiền nước và phí dịch vụ. Hệ thống sẽ tự động truy xuất dữ liệu từ các bảng liên quan:

- Thông tin phòng từ bảng phòng.
- Thông tin cơ sở của phòng.
- Đơn giá điện, đơn giá nước và phí dịch vụ hiện tại từ bảng `facilities`.
- Chỉ số điện cũ, chỉ số điện mới, chỉ số nước cũ và chỉ số nước mới từ bảng ghi nhận chỉ số điện nước.
- Tiền phòng cố định từ thông tin phòng, hợp đồng thuê hoặc cấu hình giá phòng của hệ thống.

Sau khi truy xuất đủ dữ liệu, hệ thống tự động tính:

- Số điện tiêu thụ.
- Thành tiền điện.
- Số nước tiêu thụ.
- Thành tiền nước.
- Phí dịch vụ.
- Tiền thuế.
- Tổng tiền phải nộp.

Hóa đơn đóng vai trò là căn cứ để người thuê thực hiện thanh toán và để Ban quản lý theo dõi tình trạng thu tiền.

Feature Quản lý hóa đơn cho phép Ban quản lý tạo hóa đơn, xem danh sách hóa đơn, tìm kiếm hóa đơn, xem chi tiết hóa đơn, điều chỉnh thông tin hóa đơn trước khi phát hành và xuất hóa đơn dưới dạng tài liệu PDF để lưu trữ hoặc gửi cho các bên liên quan.

Feature này giúp chuẩn hóa quy trình quản lý tài chính, đảm bảo tính chính xác của dữ liệu hóa đơn và hỗ trợ kiểm soát công nợ trong hệ thống nhà trọ.

---

# 2. User Stories

## Story 1: Tạo hóa đơn

Là Ban quản lý,  
tôi muốn tạo hóa đơn cho một phòng theo từng kỳ hạn  
để ghi nhận các khoản phí mà người thuê cần thanh toán.

---

## Story 2: Tự động truy xuất dữ liệu khi tạo hóa đơn

Là Ban quản lý,  
tôi muốn hệ thống tự động lấy đơn giá điện, đơn giá nước, phí dịch vụ và chỉ số điện nước từ các bảng liên quan  
để hóa đơn được tính chính xác mà không cần nhập thủ công toàn bộ dữ liệu.

---

## Story 3: Xem danh sách hóa đơn

Là Ban quản lý,  
tôi muốn xem danh sách hóa đơn trong hệ thống  
để theo dõi các khoản phí cần thu của người thuê.

---

## Story 4: Xem chi tiết hóa đơn

Là Ban quản lý,  
tôi muốn xem chi tiết hóa đơn  
để kiểm tra thông tin tính phí.

---

## Story 5: Điều chỉnh hóa đơn

Là Ban quản lý,  
tôi muốn điều chỉnh thông tin hóa đơn  
để sửa các sai sót trước khi phát hành hóa đơn.

---

## Story 6: Xuất hóa đơn

Là Ban quản lý,  
tôi muốn xuất hóa đơn  
để lưu trữ hoặc cung cấp cho các bên liên quan.

---

## Story 7: Tìm kiếm hóa đơn

Là Ban quản lý,  
tôi muốn tìm kiếm hóa đơn theo mã hóa đơn hoặc phòng  
để nhanh chóng tra cứu dữ liệu.

---

## Story 8: Kiểm tra hóa đơn không tồn tại

Là Ban quản lý,  
khi hóa đơn không tồn tại,  
tôi muốn hệ thống hiển thị lỗi phù hợp.

---

## Story 9: Kiểm tra quyền truy cập

Là hệ thống,  
khi người dùng không có quyền,  
tôi muốn từ chối truy cập chức năng quản lý hóa đơn.

---

# 3. Acceptance Criteria (EARS)

## 3.1 Tạo hóa đơn

KHI Ban quản lý truy cập màn hình Tạo hóa đơn,  
THE SYSTEM SHALL hiển thị form tạo hóa đơn.

KHI form tạo hóa đơn được hiển thị,  
THE SYSTEM SHALL cho phép Ban quản lý nhập/chọn các thông tin sau:

- Mã phòng
- Kỳ hạn hóa đơn
- Hạn thanh toán
- Phí khác
- Thuế (%)
- Note

KHI Ban quản lý chọn mã phòng,  
THE SYSTEM SHALL kiểm tra phòng tồn tại và hợp lệ.

KHI Ban quản lý chọn kỳ hạn hóa đơn,  
THE SYSTEM SHALL sử dụng kỳ hạn đó để tạo hóa đơn cho phòng được chọn.

KHI Ban quản lý nhập phí khác,  
THE SYSTEM SHALL kiểm tra phí khác lớn hơn hoặc bằng 0.

KHI Ban quản lý nhập thuế,  
THE SYSTEM SHALL kiểm tra thuế lớn hơn hoặc bằng 0.

KHI Ban quản lý tạo hóa đơn với dữ liệu hợp lệ,  
THE SYSTEM SHALL tự động truy xuất các dữ liệu cần thiết từ các bảng liên quan để tính hóa đơn.

KHI hóa đơn được tạo,  
THE SYSTEM SHALL tự động sinh `invoice_id` theo thứ tự tăng dần: `1, 2, 3, ...`.

KHI hóa đơn được tạo,  
THE SYSTEM SHALL tự động sinh mã hóa đơn theo định dạng:

```text
INV-{roomCode}-{billingPeriod}
```

Ví dụ:

```text
INV-HN0101-202606
```

KHI hóa đơn được tạo thành công,  
THE SYSTEM SHALL gán trạng thái thanh toán mặc định là `UNPAID`.

KHI hóa đơn được tạo thành công,  
THE SYSTEM SHALL lưu hóa đơn mới vào hệ thống.

KHI đã tồn tại hóa đơn của cùng phòng trong cùng kỳ hạn,  
THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `INVOICE_ALREADY_EXISTS`.

KHI mã phòng không tồn tại,  
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `ROOM_NOT_FOUND`.

KHI hạn thanh toán nhỏ hơn ngày hiện tại,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_DUE_DATE`.

KHI thuế nhỏ hơn 0,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_TAX_RATE`.

KHI phí khác nhỏ hơn 0,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_OTHER_FEE`.

---

## 3.2 Tự động truy xuất dữ liệu khi tạo hóa đơn

KHI Ban quản lý tạo hóa đơn cho một phòng,  
THE SYSTEM SHALL truy xuất thông tin phòng dựa trên `roomCode`.

KHI truy xuất được thông tin phòng,  
THE SYSTEM SHALL xác định cơ sở mà phòng thuộc về.

KHI xác định được cơ sở của phòng,  
THE SYSTEM SHALL truy xuất đơn giá điện, đơn giá nước và phí dịch vụ hiện tại từ bảng `facilities`.

KHI tạo hóa đơn theo kỳ hạn,  
THE SYSTEM SHALL truy xuất chỉ số điện cũ, chỉ số điện mới, chỉ số nước cũ và chỉ số nước mới của phòng trong kỳ hạn đó từ bảng ghi nhận chỉ số điện nước.

KHI truy xuất được chỉ số điện,  
THE SYSTEM SHALL tính số điện tiêu thụ theo công thức:

```text
Số điện tiêu thụ = Chỉ số điện mới - Chỉ số điện cũ
```

KHI truy xuất được đơn giá điện,  
THE SYSTEM SHALL tính thành tiền điện theo công thức:

```text
Thành tiền điện = Số điện tiêu thụ × Đơn giá điện
```

KHI truy xuất được chỉ số nước,  
THE SYSTEM SHALL tính số nước tiêu thụ theo công thức:

```text
Số nước tiêu thụ = Chỉ số nước mới - Chỉ số nước cũ
```

KHI truy xuất được đơn giá nước,  
THE SYSTEM SHALL tính thành tiền nước theo công thức:

```text
Thành tiền nước = Số nước tiêu thụ × Đơn giá nước
```

KHI truy xuất được phí dịch vụ hiện tại của cơ sở,  
THE SYSTEM SHALL đưa phí dịch vụ vào hóa đơn.

KHI truy xuất được tiền phòng cố định,  
THE SYSTEM SHALL đưa tiền phòng cố định vào hóa đơn.

KHI đã có tiền phòng, tiền điện, tiền nước, phí dịch vụ và phí khác,  
THE SYSTEM SHALL tính tạm tính.

KHI đã có tạm tính và thuế,  
THE SYSTEM SHALL tính tiền thuế.

KHI đã có tạm tính và tiền thuế,  
THE SYSTEM SHALL tính tổng tiền phải nộp.

KHI hóa đơn được tạo,  
THE SYSTEM SHALL lưu lại snapshot của đơn giá điện, đơn giá nước và phí dịch vụ tại thời điểm tạo hóa đơn.

KHI giá điện, giá nước hoặc phí dịch vụ trong bảng `facilities` thay đổi sau khi hóa đơn đã được tạo,  
THE SYSTEM SHALL không tự động thay đổi dữ liệu của hóa đơn đã tạo trước đó.

KHI không tìm thấy dữ liệu đơn giá điện, đơn giá nước hoặc phí dịch vụ của cơ sở,  
THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `FACILITY_PRICE_NOT_FOUND`.

KHI không tìm thấy chỉ số điện nước của phòng trong kỳ hạn đã chọn,  
THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `METER_READING_NOT_FOUND`.

KHI chỉ số điện mới nhỏ hơn chỉ số điện cũ,  
THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `INVALID_ELECTRIC_READING`.

KHI chỉ số nước mới nhỏ hơn chỉ số nước cũ,  
THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `INVALID_WATER_READING`.

---

## 3.3 Xem danh sách hóa đơn

KHI Ban quản lý truy cập màn hình Quản lý hóa đơn,  
THE SYSTEM SHALL hiển thị danh sách hóa đơn.

KHI danh sách hóa đơn được hiển thị,  
THE SYSTEM SHALL hiển thị:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán

KHI Ban quản lý tìm kiếm theo mã hóa đơn,  
THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI Ban quản lý tìm kiếm theo phòng,  
THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI Ban quản lý lọc theo trạng thái thanh toán,  
THE SYSTEM SHALL hiển thị các hóa đơn phù hợp với trạng thái được chọn.

KHI không tồn tại hóa đơn nào,  
THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại chưa có hóa đơn nào.
```

---

## 3.4 Xem chi tiết hóa đơn

KHI Ban quản lý chọn một hóa đơn,  
THE SYSTEM SHALL hiển thị:

- `invoice_id`
- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tiền phòng cố định
- Chỉ số điện cũ
- Chỉ số điện mới
- Số điện tiêu thụ
- Đơn giá điện tại thời điểm tạo hóa đơn
- Thành tiền điện
- Chỉ số nước cũ
- Chỉ số nước mới
- Số nước tiêu thụ
- Đơn giá nước tại thời điểm tạo hóa đơn
- Thành tiền nước
- Phí dịch vụ tại thời điểm tạo hóa đơn
- Phí khác
- Thuế (%)
- Tiền thuế
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán
- Note
- Ngày tạo
- Người tạo
- Ngày cập nhật
- Người cập nhật

KHI hóa đơn không tồn tại,  
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `INVOICE_NOT_FOUND`.

---

## 3.5 Điều chỉnh hóa đơn

KHI Ban quản lý cập nhật hóa đơn với dữ liệu hợp lệ,  
THE SYSTEM SHALL lưu thông tin hóa đơn mới.

KHI chỉ số điện mới được thay đổi,  
THE SYSTEM SHALL tính lại số điện tiêu thụ và thành tiền điện.

KHI chỉ số nước mới được thay đổi,  
THE SYSTEM SHALL tính lại số nước tiêu thụ và thành tiền nước.

KHI tiền phòng cố định thay đổi,  
THE SYSTEM SHALL cập nhật tổng tiền phải nộp.

KHI phí dịch vụ thay đổi,  
THE SYSTEM SHALL cập nhật tổng tiền phải nộp.

KHI phí khác thay đổi,  
THE SYSTEM SHALL cập nhật tổng tiền phải nộp.

KHI thuế thay đổi,  
THE SYSTEM SHALL tính lại tiền thuế và cập nhật tổng tiền phải nộp.

KHI Note thay đổi,  
THE SYSTEM SHALL lưu lại nội dung Note mới.

KHI hóa đơn đã thanh toán,  
THE SYSTEM SHALL từ chối điều chỉnh và trả về HTTP 400 với mã lỗi `PAID_INVOICE_CANNOT_BE_UPDATED`.

KHI chỉ số điện mới nhỏ hơn chỉ số điện cũ,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_ELECTRIC_READING`.

KHI chỉ số nước mới nhỏ hơn chỉ số nước cũ,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_WATER_READING`.

KHI hạn thanh toán nhỏ hơn ngày hiện tại,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_DUE_DATE`.

KHI thuế nhỏ hơn 0,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_TAX_RATE`.

KHI phí khác nhỏ hơn 0,  
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_OTHER_FEE`.

---

## 3.6 Xuất hóa đơn

KHI Ban quản lý yêu cầu xuất hóa đơn,  
THE SYSTEM SHALL tạo file hóa đơn PDF.

KHI xuất hóa đơn thành công,  
THE SYSTEM SHALL trả về file hóa đơn.

KHI hóa đơn được xuất,  
THE SYSTEM SHALL chứa đầy đủ:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tiền phòng
- Chỉ số điện cũ
- Chỉ số điện mới
- Số điện tiêu thụ
- Đơn giá điện
- Tiền điện
- Chỉ số nước cũ
- Chỉ số nước mới
- Số nước tiêu thụ
- Đơn giá nước
- Tiền nước
- Phí dịch vụ
- Phí khác
- Thuế
- Tiền thuế
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán
- Note

KHI hóa đơn không tồn tại,  
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `INVOICE_NOT_FOUND`.

KHI hệ thống không thể tạo file hóa đơn,  
THE SYSTEM SHALL trả về HTTP 500 với mã lỗi `INVOICE_EXPORT_FAILED`.

---

## 3.7 Trạng thái thanh toán

KHI hóa đơn chưa được thanh toán,  
THE SYSTEM SHALL gán trạng thái `UNPAID`.

KHI hóa đơn đã được xác nhận thanh toán,  
THE SYSTEM SHALL gán trạng thái `PAID`.

KHI hóa đơn chưa thanh toán và đã vượt quá hạn thanh toán,  
THE SYSTEM SHALL gán trạng thái `OVERDUE`.

---

## 3.8 Phân quyền

KHI người dùng có vai trò `Management Board`,  
THE SYSTEM SHALL cho phép truy cập chức năng quản lý hóa đơn.

KHI người dùng chưa đăng nhập,  
THE SYSTEM SHALL trả về HTTP 401.

KHI người dùng không có vai trò `Management Board`,  
THE SYSTEM SHALL trả về HTTP 403.

---

# 4. API Contract

## 4.1 Tạo hóa đơn

### Endpoint

```http
POST /api/v1/invoices
```

### Description

API này tạo hóa đơn cho một phòng theo kỳ hạn.

Người dùng chỉ nhập:

- Mã phòng
- Kỳ hạn hóa đơn
- Hạn thanh toán
- Phí khác
- Thuế
- Note

Hệ thống tự động truy xuất:

- Tiền phòng cố định
- Đơn giá điện từ bảng `facilities`
- Đơn giá nước từ bảng `facilities`
- Phí dịch vụ từ bảng `facilities`
- Chỉ số điện cũ/mới từ bảng ghi nhận chỉ số điện nước
- Chỉ số nước cũ/mới từ bảng ghi nhận chỉ số điện nước

Sau đó hệ thống tự động tính và lưu hóa đơn.

### Request

```json
{
  "roomCode": "HN0101",
  "billingPeriod": "202606",
  "dueDate": "2026-06-30",
  "otherFee": 200000,
  "taxRate": 10,
  "note": "Phí vệ sinh cuối tháng"
}
```

### Request Parameters

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `roomCode` | string | Yes | Mã phòng cần tạo hóa đơn |
| `billingPeriod` | string | Yes | Kỳ hạn hóa đơn, định dạng `YYYYMM` |
| `dueDate` | date | Yes | Hạn thanh toán |
| `otherFee` | decimal | No | Phí khác phát sinh trong kỳ |
| `taxRate` | decimal | Yes | Thuế theo phần trăm |
| `note` | string | No | Ghi chú hóa đơn |

### Response 201

```json
{
  "success": true,
  "data": {
    "invoiceId": 1,
    "invoiceCode": "INV-HN0101-202606",
    "roomCode": "HN0101",
    "billingPeriod": "202606",
    "roomFee": 3000000,
    "oldElectricReading": 250,
    "newElectricReading": 320,
    "electricUsage": 70,
    "electricUnitPrice": 3500,
    "electricAmount": 245000,
    "oldWaterReading": 120,
    "newWaterReading": 145,
    "waterUsage": 25,
    "waterUnitPrice": 5000,
    "waterAmount": 125000,
    "serviceFee": 200000,
    "otherFee": 200000,
    "subtotal": 3770000,
    "taxRate": 10,
    "taxAmount": 377000,
    "totalAmount": 4147000,
    "dueDate": "2026-06-30",
    "status": "UNPAID",
    "note": "Phí vệ sinh cuối tháng",
    "createdAt": "2026-06-10T10:00:00",
    "createdBy": 5
  }
}
```

---

## 4.2 Danh sách hóa đơn

### Endpoint

```http
GET /api/v1/invoices
```

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `keyword` | string | No | Tìm kiếm theo mã hóa đơn |
| `roomCode` | string | No | Tìm kiếm/lọc theo mã phòng |
| `status` | string | No | Lọc theo trạng thái `UNPAID`, `PAID`, `OVERDUE` |
| `billingPeriod` | string | No | Lọc theo kỳ hóa đơn |
| `page` | number | No | Trang hiện tại |
| `size` | number | No | Số bản ghi trên một trang |

### Response 200

```json
{
  "success": true,
  "data": [
    {
      "invoiceId": 1,
      "invoiceCode": "INV-HN0101-202606",
      "roomCode": "HN0101",
      "billingPeriod": "202606",
      "totalAmount": 4147000,
      "dueDate": "2026-06-30",
      "status": "UNPAID"
    }
  ]
}
```

---

## 4.3 Xem chi tiết hóa đơn

### Endpoint

```http
GET /api/v1/invoices/{invoiceId}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "invoiceId": 1,
    "invoiceCode": "INV-HN0101-202606",
    "roomCode": "HN0101",
    "billingPeriod": "202606",
    "roomFee": 3000000,
    "oldElectricReading": 250,
    "newElectricReading": 320,
    "electricUsage": 70,
    "electricUnitPrice": 3500,
    "electricAmount": 245000,
    "oldWaterReading": 120,
    "newWaterReading": 145,
    "waterUsage": 25,
    "waterUnitPrice": 5000,
    "waterAmount": 125000,
    "serviceFee": 200000,
    "otherFee": 200000,
    "subtotal": 3770000,
    "taxRate": 10,
    "taxAmount": 377000,
    "totalAmount": 4147000,
    "dueDate": "2026-06-30",
    "status": "UNPAID",
    "note": "Phí vệ sinh cuối tháng",
    "createdAt": "2026-06-10T10:00:00",
    "createdBy": 5,
    "updatedAt": "2026-06-10T10:00:00",
    "updatedBy": 5
  }
}
```

---

## 4.4 Điều chỉnh hóa đơn

### Endpoint

```http
PUT /api/v1/invoices/{invoiceId}
```

### Request

```json
{
  "roomFee": 3200000,
  "newElectricReading": 330,
  "newWaterReading": 150,
  "serviceFee": 200000,
  "otherFee": 250000,
  "taxRate": 10,
  "dueDate": "2026-07-05",
  "note": "Điều chỉnh phí phát sinh"
}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "invoiceId": 1,
    "invoiceCode": "INV-HN0101-202606",
    "updatedAt": "2026-06-10T10:00:00",
    "updatedBy": 5
  }
}
```

---

## 4.5 Xuất hóa đơn

### Endpoint

```http
GET /api/v1/invoices/{invoiceId}/export
```

### Response 200

```json
{
  "success": true,
  "data": {
    "fileName": "INV-HN0101-202606.pdf",
    "downloadUrl": "/files/invoices/INV-HN0101-202606.pdf"
  }
}
```

---

# 5. Business Rules

## 5.1 Quy tắc tạo mã hóa đơn

Mã hóa đơn được hệ thống tự động sinh theo định dạng:

```text
INV-{roomCode}-{billingPeriod}
```

Ví dụ:

```text
INV-HN0101-202606
```

Trong đó:

| Thành phần | Ý nghĩa |
| --- | --- |
| `INV` | Tiền tố hóa đơn |
| `HN0101` | Mã phòng |
| `202606` | Kỳ hạn hóa đơn, định dạng `YYYYMM` |

---

## 5.2 Quy tắc invoice_id

`invoice_id` do hệ thống hoặc database tự động sinh theo thứ tự tăng dần.

Ví dụ:

| invoice_id | invoice_code |
| ---: | --- |
| 1 | `INV-HN0101-202606` |
| 2 | `INV-HN0102-202606` |
| 3 | `INV-HN0201-202606` |

Người dùng không được nhập trực tiếp `invoice_id`.

---

## 5.3 Quy tắc không trùng hóa đơn

Mỗi phòng chỉ được có một hóa đơn trong một kỳ hạn.

Ví dụ:

```text
Phòng HN0101 đã có hóa đơn kỳ 202606
=> Không được tạo thêm hóa đơn INV-HN0101-202606
```

Khi tạo trùng, hệ thống trả về lỗi:

```text
HTTP 400
INVOICE_ALREADY_EXISTS
```

---

## 5.4 Quy tắc truy xuất dữ liệu khi tạo hóa đơn

Khi tạo hóa đơn, hệ thống phải tự động truy xuất dữ liệu từ các bảng liên quan.

Các dữ liệu cần truy xuất gồm:

| Dữ liệu | Nguồn dữ liệu |
| --- | --- |
| Mã phòng | Bảng phòng |
| Thông tin cơ sở của phòng | Bảng phòng / bảng cơ sở |
| Tiền phòng cố định | Bảng phòng / hợp đồng thuê / cấu hình giá phòng |
| Đơn giá điện | Bảng `facilities` |
| Đơn giá nước | Bảng `facilities` |
| Phí dịch vụ | Bảng `facilities` |
| Chỉ số điện cũ | Bảng ghi nhận chỉ số điện nước |
| Chỉ số điện mới | Bảng ghi nhận chỉ số điện nước |
| Chỉ số nước cũ | Bảng ghi nhận chỉ số điện nước |
| Chỉ số nước mới | Bảng ghi nhận chỉ số điện nước |

Hệ thống không yêu cầu Ban quản lý nhập thủ công:

- Tiền phòng
- Đơn giá điện
- Đơn giá nước
- Phí dịch vụ
- Chỉ số điện cũ
- Chỉ số điện mới
- Chỉ số nước cũ
- Chỉ số nước mới
- Thành tiền điện
- Thành tiền nước
- Tiền thuế
- Tổng tiền phải nộp

---

## 5.5 Quy tắc lưu snapshot giá

Khi hóa đơn được tạo, hệ thống phải lưu lại đơn giá tại thời điểm tạo hóa đơn.

Các giá cần lưu snapshot trong hóa đơn gồm:

- Đơn giá điện
- Đơn giá nước
- Phí dịch vụ
- Tiền phòng cố định

Mục đích là để hóa đơn cũ không bị thay đổi khi bảng `facilities` hoặc cấu hình giá phòng thay đổi sau này.

Ví dụ:

```text
Ngày 01/06/2026:
- Giá điện trong facilities = 3,500 VNĐ/kWh
- Hóa đơn INV-HN0101-202606 được tạo với đơn giá điện 3,500 VNĐ/kWh

Ngày 10/06/2026:
- Ban quản lý cập nhật giá điện trong facilities = 3,800 VNĐ/kWh

Kết quả:
- Hóa đơn INV-HN0101-202606 vẫn giữ đơn giá điện 3,500 VNĐ/kWh
- Hóa đơn tạo sau ngày 10/06/2026 sử dụng đơn giá điện 3,800 VNĐ/kWh
```

---

## 5.6 Quy tắc tính tiền

Tổng tiền phải nộp được tính tự động.

```text
Số điện tiêu thụ = Chỉ số điện mới - Chỉ số điện cũ

Tiền điện = Số điện tiêu thụ × Đơn giá điện

Số nước tiêu thụ = Chỉ số nước mới - Chỉ số nước cũ

Tiền nước = Số nước tiêu thụ × Đơn giá nước

Tạm tính = Tiền phòng + Tiền điện + Tiền nước + Phí dịch vụ + Phí khác

Tiền thuế = Tạm tính × Thuế (%)

Tổng tiền phải nộp = Tạm tính + Tiền thuế
```

Người dùng không được nhập trực tiếp:

- Tiền điện
- Tiền nước
- Tiền thuế
- Tổng tiền phải nộp

---

## 5.7 Quy tắc trạng thái hóa đơn

| Trạng thái | Ý nghĩa |
| --- | --- |
| `UNPAID` | Hóa đơn chưa được thanh toán |
| `PAID` | Hóa đơn đã được xác nhận thanh toán |
| `OVERDUE` | Hóa đơn chưa thanh toán và đã quá hạn thanh toán |

Khi hóa đơn vừa được tạo, trạng thái mặc định là `UNPAID`.

Khi hóa đơn đã thanh toán, hệ thống không cho phép điều chỉnh hóa đơn.

---

# 6. Database Impact

## 6.1 Bảng `invoices`

Bảng `invoices` cần lưu thông tin hóa đơn đã được tính toán tại thời điểm tạo.

Các trường đề xuất:

```sql
invoices
--------
invoice_id
invoice_code
room_id
room_code
billing_period
room_fee
old_electric_reading
new_electric_reading
electric_usage
electric_unit_price
electric_amount
old_water_reading
new_water_reading
water_usage
water_unit_price
water_amount
service_fee
other_fee
subtotal
tax_rate
tax_amount
total_amount
due_date
status
note
created_at
created_by
updated_at
updated_by
```

---

## 6.2 Bảng `facilities`

Bảng `facilities` là nơi lưu đơn giá hiện tại của cơ sở.

Các trường liên quan:

```sql
facilities
----------
facility_id
facility_code
facility_name
electricity_price
water_price
service_fee
updated_at
updated_by
```

Mapping dữ liệu:

| Dữ liệu cần lấy | Cột trong bảng `facilities` |
| --- | --- |
| Đơn giá điện | `electricity_price` |
| Đơn giá nước | `water_price` |
| Phí dịch vụ | `service_fee` |

---

## 6.3 Bảng ghi nhận chỉ số điện nước

Hệ thống cần có bảng lưu chỉ số điện nước theo phòng và kỳ hạn.

Tên bảng có thể là:

```sql
meter_readings
```

Các trường đề xuất:

```sql
meter_readings
--------------
reading_id
room_id
room_code
billing_period
old_electric_reading
new_electric_reading
old_water_reading
new_water_reading
created_at
created_by
updated_at
updated_by
```

Mapping dữ liệu:

| Dữ liệu cần lấy | Nguồn dữ liệu |
| --- | --- |
| Chỉ số điện cũ | `meter_readings.old_electric_reading` |
| Chỉ số điện mới | `meter_readings.new_electric_reading` |
| Chỉ số nước cũ | `meter_readings.old_water_reading` |
| Chỉ số nước mới | `meter_readings.new_water_reading` |

---

# 7. Technical Constraints

- Chỉ `Management Board` được phép quản lý hóa đơn.
- Mỗi hóa đơn phải liên kết với một phòng thuê hợp lệ.
- Mỗi hóa đơn phải có `invoice_id` duy nhất.
- `invoice_id` phải được sinh tự động theo thứ tự tăng dần.
- Mỗi hóa đơn phải có mã hóa đơn duy nhất.
- Mã hóa đơn phải được sinh theo format `INV-{roomCode}-{billingPeriod}`.
- Kỳ hóa đơn được xác định theo tháng và năm.
- Kỳ hóa đơn dùng để sinh mã hóa đơn nên lưu theo định dạng `YYYYMM`.
- Mỗi phòng chỉ được có một hóa đơn trong một kỳ hạn.
- Khi tạo hóa đơn, hệ thống phải tự động truy xuất đơn giá điện, đơn giá nước và phí dịch vụ từ bảng `facilities`.
- Khi tạo hóa đơn, hệ thống phải tự động truy xuất chỉ số điện nước từ bảng ghi nhận chỉ số điện nước.
- Hóa đơn phải lưu snapshot đơn giá điện, đơn giá nước, phí dịch vụ và tiền phòng tại thời điểm tạo.
- Không được tự động cập nhật lại hóa đơn cũ khi giá trong bảng `facilities` thay đổi.
- Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ.
- Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ.
- Phí khác phải lớn hơn hoặc bằng 0.
- Thuế phải lớn hơn hoặc bằng 0.
- Thuế được lưu dưới dạng phần trăm `%`.
- Tiền điện phải được hệ thống tính tự động.
- Tiền nước phải được hệ thống tính tự động.
- Tiền thuế phải được hệ thống tính tự động.
- Tổng tiền phải nộp phải được hệ thống tính tự động.
- Người dùng không được nhập trực tiếp tiền điện, tiền nước, tiền thuế và tổng tiền phải nộp.
- Không được chỉnh sửa hóa đơn đã thanh toán.
- Hóa đơn xuất ra phải ở định dạng PDF.
- Hệ thống phải lưu:
  - `createdAt`
  - `createdBy`
  - `updatedAt`
  - `updatedBy`
- Tất cả thao tác tạo và điều chỉnh hóa đơn phải được ghi Audit Log.
- API tạo hóa đơn: `< 1000ms (p95)`.
- API danh sách hóa đơn: `< 1000ms (p95)`.
- API chi tiết hóa đơn: `< 500ms (p95)`.
- API điều chỉnh hóa đơn: `< 1000ms (p95)`.
- API xuất hóa đơn: `< 2000ms (p95)`.

---

# 8. Error Codes

| Error Code | HTTP Status | Description |
| --- | ---: | --- |
| `UNAUTHORIZED` | 401 | Người dùng chưa đăng nhập |
| `FORBIDDEN` | 403 | Người dùng không có quyền truy cập chức năng |
| `ROOM_NOT_FOUND` | 404 | Không tìm thấy phòng |
| `INVOICE_NOT_FOUND` | 404 | Không tìm thấy hóa đơn |
| `INVOICE_ALREADY_EXISTS` | 400 | Phòng đã có hóa đơn trong kỳ hạn này |
| `FACILITY_PRICE_NOT_FOUND` | 400 | Không tìm thấy đơn giá điện/nước/phí dịch vụ của cơ sở |
| `METER_READING_NOT_FOUND` | 400 | Không tìm thấy chỉ số điện nước của phòng trong kỳ hạn |
| `INVALID_ELECTRIC_READING` | 400 | Chỉ số điện mới nhỏ hơn chỉ số điện cũ |
| `INVALID_WATER_READING` | 400 | Chỉ số nước mới nhỏ hơn chỉ số nước cũ |
| `INVALID_DUE_DATE` | 400 | Hạn thanh toán không hợp lệ |
| `INVALID_TAX_RATE` | 400 | Thuế không hợp lệ |
| `INVALID_OTHER_FEE` | 400 | Phí khác không hợp lệ |
| `PAID_INVOICE_CANNOT_BE_UPDATED` | 400 | Không được chỉnh sửa hóa đơn đã thanh toán |
| `INVOICE_EXPORT_FAILED` | 500 | Không thể xuất file hóa đơn |

---

# 9. Out Of Scope

- Hóa đơn điện tử theo chuẩn thuế.
- Chữ ký số.
- Tích hợp cơ quan thuế.
- Thanh toán trực tiếp từ hóa đơn.
- Hoàn tiền.
- Hủy hóa đơn đã thanh toán.
- Gửi email hóa đơn tự động.
- Gửi SMS hóa đơn.
- OCR hóa đơn.
- AI kiểm tra hóa đơn.
- Báo cáo doanh thu.
- Báo cáo tài chính tổng hợp.
- Tự động cập nhật lại hóa đơn đã tạo khi đơn giá trong bảng `facilities` thay đổi.