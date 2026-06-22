# Feature: Quản lý công nợ

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** \[YYYY-MM-DD\]\
**Priority:** Medium

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, công nợ là các hóa đơn chưa được thanh toán hoặc đã quá hạn thanh toán.

Feature Quản lý công nợ không tạo ra một bảng công nợ riêng. Danh sách công nợ được hệ thống truy xuất trực tiếp từ bảng `invoices`.

Một hóa đơn được xem là công nợ khi hóa đơn có trạng thái:

- `UNPAID`: Chưa thanh toán

- `OVERDUE`: Đã quá hạn thanh toán

Khi Ban quản lý truy cập chức năng Quản lý công nợ, hệ thống sẽ lấy danh sách các hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`, sau đó kết nối dữ liệu với các bảng liên quan để hiển thị đầy đủ thông tin công nợ.

Các bảng dữ liệu liên quan bao gồm:

- `invoices`: Lưu thông tin hóa đơn, tổng tiền phải nộp, hạn thanh toán, trạng thái.

- `rooms`: Lưu thông tin phòng, mã phòng.

- `users`: Lưu thông tin người thuê.

- `facilities`: Lưu thông tin cơ sở mà phòng thuộc về.

- `payments`: Lưu thông tin thanh toán liên quan đến hóa đơn.

Ban quản lý có thể xem:

- Người thuê đang nợ.

- Phòng đang phát sinh công nợ.

- Cơ sở phát sinh công nợ.

- Hóa đơn nào đang chưa thanh toán hoặc quá hạn.

- Số tiền còn nợ.

- Số ngày nợ.

- Phí chậm nộp tạm tính để tham khảo.

- Chi tiết hóa đơn đang nợ.

Nếu hóa đơn nộp muộn quá 03 ngày kể từ ngày đến hạn, hệ thống sẽ hiển thị phí chậm nộp tạm tính. Mỗi ngày muộn sau thời gian 03 ngày sẽ được tính bằng 1% giá trị tiền phòng/tháng.

Phí chậm nộp này chỉ hiển thị để Ban quản lý tham khảo. Hệ thống không lưu khoản phí chậm nộp này vào bất kỳ bảng nào và không tự động cộng vào tổng tiền hóa đơn. Nếu Ban quản lý muốn thu khoản phí này, Ban quản lý sẽ tự nhập vào mục `Khoản phí khác` trong hóa đơn.

Feature này giúp Ban quản lý theo dõi các khoản chưa thu, kiểm tra hóa đơn quá hạn và chủ động xử lý công nợ với người thuê.

---

# 2. User Stories

## Story 1: Xem danh sách công nợ

Là Ban quản lý,\
tôi muốn xem danh sách các hóa đơn chưa thanh toán hoặc quá hạn\
để theo dõi các khoản tiền người thuê còn nợ.

---

## Story 2: Xem thông tin người thuê của công nợ

Là Ban quản lý,\
tôi muốn xem thông tin người thuê liên quan đến hóa đơn nợ\
để biết cần liên hệ với ai khi xử lý công nợ.

---

## Story 3: Xem thông tin phòng và cơ sở của công nợ

Là Ban quản lý,\
tôi muốn xem mã phòng và cơ sở phát sinh công nợ\
để xác định đúng nơi phát sinh khoản nợ.

---

## Story 4: Xem số ngày nợ

Là Ban quản lý,\
tôi muốn xem hóa đơn đang nợ bao nhiêu ngày\
để đánh giá mức độ quá hạn của công nợ.

---

## Story 5: Xem số tiền còn nợ

Là Ban quản lý,\
tôi muốn xem hóa đơn còn nợ bao nhiêu tiền\
để biết số tiền cần thu từ người thuê.

---

## Story 6: Xem phí chậm nộp tạm tính

Là Ban quản lý,\
tôi muốn hệ thống hiển thị phí chậm nộp tạm tính nếu hóa đơn nộp muộn quá 03 ngày\
để có cơ sở tham khảo khi xử lý công nợ.

---

## Story 7: Xem chi tiết hóa đơn nợ

Là Ban quản lý,\
tôi muốn xem chi tiết hóa đơn đang nợ\
để kiểm tra tiền phòng, điện, nước, phí dịch vụ, phí khác, thuế và tổng tiền phải nộp.

---

## Story 8: Tìm kiếm và lọc công nợ

Là Ban quản lý,\
tôi muốn tìm kiếm công nợ theo mã hóa đơn, mã phòng hoặc tên người thuê\
để nhanh chóng tra cứu khoản công nợ cần kiểm tra.

---

## Story 9: Kiểm tra quyền truy cập

Là hệ thống,\
khi người dùng chưa đăng nhập hoặc không có quyền Ban quản lý,\
tôi muốn từ chối truy cập chức năng Quản lý công nợ\
để bảo vệ dữ liệu tài chính của hệ thống.

---

# 3. Acceptance Criteria (EARS)

## 3.1 Xem danh sách công nợ

KHI Ban quản lý truy cập màn hình Quản lý công nợ,\
THE SYSTEM SHALL truy xuất dữ liệu từ bảng `invoices`.

KHI truy xuất danh sách công nợ,\
THE SYSTEM SHALL chỉ lấy các hóa đơn có trạng thái:

- `UNPAID`

- `OVERDUE`

KHI danh sách công nợ được hiển thị,\
THE SYSTEM SHALL hiển thị các thông tin sau:

- `invoiceId`

- Mã hóa đơn

- Mã phòng

- Tên người thuê

- Tên cơ sở

- Kỳ hóa đơn

- Tổng tiền hóa đơn

- Số tiền đã thanh toán

- Số tiền còn nợ

- Hạn thanh toán

- Số ngày nợ

- Phí chậm nộp tạm tính

- Trạng thái hóa đơn

- Hành động xem chi tiết

KHI không có hóa đơn nào có trạng thái `UNPAID` hoặc `OVERDUE`,\
THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại không có công nợ nào.
```

KHI số lượng công nợ lớn hơn kích thước một trang,\
THE SYSTEM SHALL hiển thị dữ liệu theo phân trang.

---

## 3.2 Truy xuất thông tin phòng

KHI hệ thống lấy danh sách công nợ từ bảng `invoices`,\
THE SYSTEM SHALL nối sang bảng `rooms` để lấy thông tin phòng.

KHI nối sang bảng `rooms`,\
THE SYSTEM SHALL hiển thị mã phòng tương ứng với hóa đơn.

KHI không tìm thấy phòng tương ứng với hóa đơn,\
THE SYSTEM SHALL vẫn hiển thị hóa đơn nhưng hiển thị mã phòng là:

```text
Không xác định
```

---

## 3.3 Truy xuất thông tin người thuê

KHI hệ thống lấy danh sách công nợ,\
THE SYSTEM SHALL nối sang bảng `users` để lấy thông tin người thuê.

KHI thông tin người thuê được tìm thấy,\
THE SYSTEM SHALL hiển thị:

- Tên người thuê

- Số điện thoại

- Email

KHI không tìm thấy thông tin người thuê,\
THE SYSTEM SHALL hiển thị tên người thuê là:

```text
Không xác định
```

---

## 3.4 Truy xuất thông tin cơ sở

KHI hệ thống lấy danh sách công nợ,\
THE SYSTEM SHALL nối sang bảng `facilities` để lấy thông tin cơ sở.

KHI thông tin cơ sở được tìm thấy,\
THE SYSTEM SHALL hiển thị:

- Mã cơ sở

- Tên cơ sở

KHI không tìm thấy thông tin cơ sở,\
THE SYSTEM SHALL hiển thị tên cơ sở là:

```text
Không xác định
```

---

## 3.5 Truy xuất thông tin thanh toán

KHI hệ thống lấy danh sách công nợ,\
THE SYSTEM SHALL nối sang bảng `payments` để lấy thông tin thanh toán của hóa đơn.

KHI hóa đơn có thanh toán thành công,\
THE SYSTEM SHALL tính tổng số tiền đã thanh toán từ các bản ghi thanh toán hợp lệ.

KHI hóa đơn chưa có thanh toán thành công,\
THE SYSTEM SHALL xem số tiền đã thanh toán là `0`.

KHI tính số tiền còn nợ,\
THE SYSTEM SHALL tính theo công thức:

```text
Số tiền còn nợ = Tổng tiền hóa đơn - Tổng tiền đã thanh toán thành công
```

KHI số tiền còn nợ nhỏ hơn 0,\
THE SYSTEM SHALL hiển thị số tiền còn nợ là `0`.

---

## 3.6 Tính số ngày nợ

KHI hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`,\
THE SYSTEM SHALL tính số ngày nợ dựa trên ngày hiện tại và hạn thanh toán.

Công thức:

```text
Số ngày nợ = Ngày hiện tại - Hạn thanh toán
```

KHI ngày hiện tại nhỏ hơn hoặc bằng hạn thanh toán,\
THE SYSTEM SHALL hiển thị số ngày nợ là `0`.

KHI ngày hiện tại lớn hơn hạn thanh toán,\
THE SYSTEM SHALL hiển thị số ngày nợ là số ngày đã vượt quá hạn thanh toán.

Ví dụ:

```text
Hạn thanh toán: 2026-06-10
Ngày hiện tại: 2026-06-15

Số ngày nợ = 5 ngày
```

---

## 3.7 Tính phí chậm nộp tạm tính

KHI hóa đơn quá hạn thanh toán không quá 03 ngày,\
THE SYSTEM SHALL hiển thị phí chậm nộp tạm tính là `0`.

KHI hóa đơn nộp muộn quá 03 ngày kể từ ngày đến hạn,\
THE SYSTEM SHALL tính phí chậm nộp tạm tính từ ngày thứ 4 trở đi.

Công thức:

```text
Số ngày tính phí chậm nộp = Số ngày nợ - 3
```

```text
Phí chậm nộp tạm tính = Số ngày tính phí chậm nộp × 1% × Tiền phòng/tháng
```

KHI số ngày nợ nhỏ hơn hoặc bằng 3,\
THE SYSTEM SHALL hiển thị:

```text
Phí chậm nộp tạm tính = 0
```

KHI số ngày nợ lớn hơn 3,\
THE SYSTEM SHALL hiển thị phí chậm nộp tạm tính để Ban quản lý tham khảo.

Ví dụ:

```text
Tiền phòng/tháng = 3,000,000
Hạn thanh toán = 2026-06-10
Ngày hiện tại = 2026-06-15

Số ngày nợ = 5
Số ngày tính phí chậm nộp = 5 - 3 = 2

Phí chậm nộp tạm tính = 2 × 1% × 3,000,000 = 60,000
```

KHI phí chậm nộp tạm tính được hiển thị,\
THE SYSTEM SHALL không lưu giá trị này vào bảng `invoices`, `payments` hoặc bất kỳ bảng nào khác.

KHI Ban quản lý muốn thu phí chậm nộp,\
THE SYSTEM SHALL yêu cầu Ban quản lý tự nhập khoản phí này vào `Khoản phí khác` của hóa đơn.

---

## 3.8 Tìm kiếm công nợ

KHI Ban quản lý tìm kiếm theo mã hóa đơn,\
THE SYSTEM SHALL trả về các công nợ có mã hóa đơn phù hợp.

KHI Ban quản lý tìm kiếm theo mã phòng,\
THE SYSTEM SHALL trả về các công nợ có mã phòng phù hợp.

KHI Ban quản lý tìm kiếm theo tên người thuê,\
THE SYSTEM SHALL trả về các công nợ có tên người thuê phù hợp.

KHI không có công nợ nào phù hợp với điều kiện tìm kiếm,\
THE SYSTEM SHALL trả về danh sách rỗng.

---

## 3.9 Lọc công nợ theo trạng thái

KHI Ban quản lý lọc theo trạng thái `UNPAID`,\
THE SYSTEM SHALL chỉ hiển thị các hóa đơn có trạng thái `UNPAID`.

KHI Ban quản lý lọc theo trạng thái `OVERDUE`,\
THE SYSTEM SHALL chỉ hiển thị các hóa đơn có trạng thái `OVERDUE`.

KHI Ban quản lý không chọn trạng thái,\
THE SYSTEM SHALL hiển thị tất cả hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`.

KHI Ban quản lý truyền trạng thái khác `UNPAID` hoặc `OVERDUE`,\
THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_DEBT_STATUS`.

---

## 3.10 Xem chi tiết hóa đơn nợ

KHI Ban quản lý chọn một công nợ,\
THE SYSTEM SHALL mở màn hình chi tiết hóa đơn nợ.

KHI chi tiết hóa đơn nợ được hiển thị,\
THE SYSTEM SHALL hiển thị:

- `invoiceId`

- Mã hóa đơn

- Mã phòng

- Tên người thuê

- Số điện thoại người thuê

- Email người thuê

- Mã cơ sở

- Tên cơ sở

- Kỳ hóa đơn

- Tiền phòng

- Chỉ số điện cũ

- Chỉ số điện mới

- Số điện tiêu thụ

- Đơn giá điện

- Thành tiền điện

- Chỉ số nước cũ

- Chỉ số nước mới

- Số nước tiêu thụ

- Đơn giá nước

- Thành tiền nước

- Phí dịch vụ

- Khoản phí khác

- Thuế

- Tiền thuế

- Tổng tiền hóa đơn

- Số tiền đã thanh toán

- Số tiền còn nợ

- Hạn thanh toán

- Số ngày nợ

- Phí chậm nộp tạm tính

- Trạng thái hóa đơn

- Ghi chú hóa đơn

- Thông tin thanh toán liên quan nếu có

KHI hóa đơn không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `INVOICE_NOT_FOUND`.

KHI hóa đơn tồn tại nhưng trạng thái không phải `UNPAID` hoặc `OVERDUE`,\
THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `NOT_A_DEBT_INVOICE`.

---

## 3.11 Phân quyền

KHI người dùng có vai trò `Management Board`,\
THE SYSTEM SHALL cho phép truy cập chức năng Quản lý công nợ.

KHI người dùng chưa đăng nhập,\
THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`.

KHI người dùng không có vai trò `Management Board`,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

---

# 4. API Contract

## 4.1 Lấy danh sách công nợ

### Endpoint

```http
GET /api/v1/debts
```

### Description

API này trả về danh sách công nợ.

Dữ liệu công nợ được lấy từ bảng `invoices`, chỉ bao gồm các hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`.

Hệ thống sẽ join dữ liệu sang các bảng:

- `rooms`

- `users`

- `facilities`

- `payments`

để hiển thị thông tin phòng, người thuê, cơ sở và thanh toán.

---

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `keyword` | string | No | Tìm kiếm theo mã hóa đơn, mã phòng hoặc tên người thuê |
| `status` | string | No | Lọc theo `UNPAID` hoặc `OVERDUE` |
| `facilityId` | number | No | Lọc theo cơ sở |
| `roomCode` | string | No | Lọc theo mã phòng |
| `page` | number | No | Trang hiện tại |
| `size` | number | No | Số bản ghi trên một trang |

---

### Response 200

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "invoiceId": 1,
        "invoiceCode": "INV-HN0101-202606",
        "roomCode": "HN0101",
        "tenantId": 10,
        "tenantName": "Nguyễn Văn A",
        "tenantPhone": "0900000001",
        "facilityId": 1,
        "facilityCode": "HN01",
        "facilityName": "Cơ sở Hà Nội 01",
        "billingPeriod": "202606",
        "invoiceTotalAmount": 4147000,
        "paidAmount": 0,
        "debtAmount": 4147000,
        "roomFee": 3000000,
        "dueDate": "2026-06-30",
        "overdueDays": 5,
        "lateFeePreview": 60000,
        "status": "OVERDUE"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### Response 400 - Invalid Status

```json
{
  "success": false,
  "error": {
    "code": "INVALID_DEBT_STATUS",
    "message": "Trạng thái công nợ không hợp lệ."
  }
}
```

---

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

---

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Không có quyền truy cập chức năng Quản lý công nợ."
  }
}
```

---

## 4.2 Xem chi tiết hóa đơn nợ

### Endpoint

```http
GET /api/v1/debts/{invoiceId}
```

### Description

API này cho phép Ban quản lý xem chi tiết một hóa đơn đang là công nợ.

Một hóa đơn chỉ được xem là công nợ nếu trạng thái của hóa đơn là:

- `UNPAID`

- `OVERDUE`

---

### Response 200

```json
{
  "success": true,
  "data": {
    "invoiceId": 1,
    "invoiceCode": "INV-HN0101-202606",
    "roomCode": "HN0101",
    "tenant": {
      "tenantId": 10,
      "fullName": "Nguyễn Văn A",
      "phone": "0900000001",
      "email": "nguyenvana@example.com"
    },
    "facility": {
      "facilityId": 1,
      "facilityCode": "HN01",
      "facilityName": "Cơ sở Hà Nội 01"
    },
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
    "paidAmount": 0,
    "debtAmount": 4147000,
    "dueDate": "2026-06-30",
    "overdueDays": 5,
    "lateFeePreview": 60000,
    "lateFeeNote": "Phí chậm nộp chỉ hiển thị tham khảo, không được lưu vào hệ thống.",
    "status": "OVERDUE",
    "note": "Phí vệ sinh cuối tháng",
    "payments": [
      {
        "paymentId": 1,
        "amount": 0,
        "method": "CASH",
        "status": "PENDING",
        "paidAt": null
      }
    ],
    "createdAt": "2026-06-10T10:00:00",
    "createdBy": 5,
    "updatedAt": "2026-06-10T10:00:00",
    "updatedBy": 5
  }
}
```

---

### Response 400 - Not A Debt Invoice

```json
{
  "success": false,
  "error": {
    "code": "NOT_A_DEBT_INVOICE",
    "message": "Hóa đơn này không thuộc danh sách công nợ."
  }
}
```

---

### Response 404

```json
{
  "success": false,
  "error": {
    "code": "INVOICE_NOT_FOUND",
    "message": "Không tìm thấy hóa đơn."
  }
}
```

---

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

---

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Không có quyền xem chi tiết công nợ."
  }
}
```

---

# 5. Business Rules

## 5.1 Quy tắc xác định công nợ

Công nợ không được lưu ở một bảng riêng.

Một bản ghi được xem là công nợ khi bản ghi đó nằm trong bảng `invoices` và có trạng thái:

- `UNPAID`

- `OVERDUE`

Các hóa đơn có trạng thái `PAID` không hiển thị trong danh sách công nợ.

---

## 5.2 Quy tắc lấy dữ liệu công nợ

Danh sách công nợ được lấy từ bảng `invoices`.

Sau đó hệ thống join sang các bảng liên quan:

| Bảng | Mục đích |
| --- | --- |
| `rooms` | Lấy mã phòng |
| `users` | Lấy thông tin người thuê |
| `facilities` | Lấy thông tin cơ sở |
| `payments` | Lấy thông tin thanh toán |

Ví dụ logic truy vấn:

```sql
SELECT
    i.invoice_id,
    i.invoice_code,
    r.room_code,
    u.user_id AS tenant_id,
    u.full_name AS tenant_name,
    u.phone AS tenant_phone,
    f.facility_id,
    f.facility_code,
    f.facility_name,
    i.billing_period,
    i.total_amount,
    i.room_fee,
    i.due_date,
    i.status,
    COALESCE(SUM(CASE WHEN p.status = 'SUCCESS' THEN p.amount ELSE 0 END), 0) AS paid_amount
FROM invoices i
JOIN rooms r ON i.room_id = r.room_id
JOIN users u ON r.tenant_id = u.user_id
JOIN facilities f ON r.facility_id = f.facility_id
LEFT JOIN payments p ON i.invoice_id = p.invoice_id
WHERE i.status IN ('UNPAID', 'OVERDUE')
GROUP BY
    i.invoice_id,
    i.invoice_code,
    r.room_code,
    u.user_id,
    u.full_name,
    u.phone,
    f.facility_id,
    f.facility_code,
    f.facility_name,
    i.billing_period,
    i.total_amount,
    i.room_fee,
    i.due_date,
    i.status;
```

Lưu ý: tên cột thực tế có thể điều chỉnh theo database thật của dự án.

---

## 5.3 Quy tắc tính số tiền còn nợ

Số tiền còn nợ được tính từ tổng tiền hóa đơn và tổng tiền đã thanh toán thành công.

Công thức:

```text
Số tiền còn nợ = Tổng tiền hóa đơn - Tổng tiền đã thanh toán thành công
```

Nếu hệ thống không hỗ trợ thanh toán từng phần, `Tổng tiền đã thanh toán thành công` thường là `0` đối với hóa đơn `UNPAID` hoặc `OVERDUE`.

Nếu kết quả nhỏ hơn `0`, hệ thống hiển thị số tiền còn nợ là `0`.

Công thức hiển thị:

```text
Số tiền còn nợ = MAX(0, totalAmount - paidAmount)
```

---

## 5.4 Quy tắc tính số ngày nợ

Số ngày nợ chỉ dùng để hiển thị trên màn hình công nợ.

Công thức:

```text
Số ngày nợ = MAX(0, Ngày hiện tại - Hạn thanh toán)
```

Ví dụ:

```text
Ngày hiện tại = 2026-07-05
Hạn thanh toán = 2026-06-30

Số ngày nợ = 5 ngày
```

Nếu hóa đơn chưa đến hạn hoặc đúng hạn, số ngày nợ hiển thị là `0`.

---

## 5.5 Quy tắc tính phí chậm nộp tạm tính

Phí chậm nộp tạm tính chỉ được tính khi hóa đơn nộp muộn quá 03 ngày kể từ ngày đến hạn.

Trong 03 ngày đầu sau hạn thanh toán, phí chậm nộp tạm tính bằng `0`.

Từ ngày thứ 4 trở đi, mỗi ngày muộn được tính bằng `1%` giá trị tiền phòng/tháng.

Công thức:

```text
Số ngày tính phí chậm nộp = MAX(0, Số ngày nợ - 3)
```

```text
Phí chậm nộp tạm tính = Số ngày tính phí chậm nộp × 1% × Tiền phòng/tháng
```

Ví dụ:

```text
Tiền phòng/tháng = 3,000,000
Hạn thanh toán = 2026-06-30
Ngày hiện tại = 2026-07-05

Số ngày nợ = 5
Số ngày tính phí chậm nộp = 5 - 3 = 2

Phí chậm nộp tạm tính = 2 × 1% × 3,000,000 = 60,000
```

Phí chậm nộp tạm tính không được lưu vào database.

Phí chậm nộp tạm tính không được tự động cộng vào `totalAmount`.

Phí chậm nộp tạm tính không được tự động tạo payment.

Nếu Ban quản lý muốn thu khoản phí này, Ban quản lý sẽ tự nhập số tiền đó vào mục `Khoản phí khác` của hóa đơn.

---

## 5.6 Quy tắc trạng thái công nợ

Trạng thái công nợ chính là trạng thái của hóa đơn trong bảng `invoices`.

Các trạng thái hợp lệ trong feature này:

| Trạng thái | Ý nghĩa |
| --- | --- |
| `UNPAID` | Hóa đơn chưa thanh toán |
| `OVERDUE` | Hóa đơn chưa thanh toán và đã quá hạn |

Feature này không hiển thị hóa đơn có trạng thái `PAID`.

---

## 5.7 Quy tắc xem chi tiết công nợ

Chi tiết công nợ thực chất là chi tiết hóa đơn đang có trạng thái `UNPAID` hoặc `OVERDUE`.

Khi xem chi tiết, hệ thống không lấy dữ liệu từ bảng công nợ riêng.

Hệ thống lấy dữ liệu từ:

- `invoices`

- `rooms`

- `users`

- `facilities`

- `payments`

---

# 6. Database Impact

## 6.1 Không tạo bảng công nợ riêng

Feature Quản lý công nợ không yêu cầu tạo bảng `debts`.

Công nợ là dữ liệu được suy ra từ bảng `invoices`.

---

## 6.2 Bảng `invoices`

Bảng `invoices` là nguồn dữ liệu chính của công nợ.

Các trường sử dụng:

```sql
invoices
--------
invoice_id
invoice_code
room_id
billing_period
room_fee
total_amount
due_date
status
note
created_at
created_by
updated_at
updated_by
```

Điều kiện lấy công nợ:

```sql
WHERE invoices.status IN ('UNPAID', 'OVERDUE')
```

---

## 6.3 Bảng `rooms`

Bảng `rooms` được dùng để lấy thông tin phòng.

Các trường sử dụng:

```sql
rooms
-----
room_id
room_code
tenant_id
facility_id
```

---

## 6.4 Bảng `users`

Bảng `users` được dùng để lấy thông tin người thuê.

Các trường sử dụng:

```sql
users
-----
user_id
full_name
phone
email
```

---

## 6.5 Bảng `facilities`

Bảng `facilities` được dùng để lấy thông tin cơ sở.

Các trường sử dụng:

```sql
facilities
----------
facility_id
facility_code
facility_name
```

---

## 6.6 Bảng `payments`

Bảng `payments` được dùng để lấy thông tin thanh toán liên quan đến hóa đơn.

Các trường sử dụng:

```sql
payments
--------
payment_id
invoice_id
amount
method
status
paid_at
created_at
```

Chỉ các thanh toán có trạng thái thành công mới được tính vào `paidAmount`.

Ví dụ:

```sql
WHERE payments.status = 'SUCCESS'
```

---

# 7. UI/UX Specification

## 7.1 Màn hình danh sách công nợ

Màn hình Quản lý công nợ hiển thị danh sách các hóa đơn chưa thanh toán hoặc quá hạn.

### Thành phần chính

- Tiêu đề màn hình: `Quản lý công nợ`

- Ô tìm kiếm theo mã hóa đơn, mã phòng, tên người thuê

- Bộ lọc trạng thái:

  - Tất cả

  - Chưa thanh toán

  - Quá hạn

- Bảng danh sách công nợ

- Phân trang

### Các cột trong bảng

| Cột | Mô tả |
| --- | --- |
| STT | Số thứ tự |
| Mã hóa đơn | Mã hóa đơn từ bảng `invoices` |
| Mã phòng | Mã phòng từ bảng `rooms` |
| Người thuê | Tên người thuê từ bảng `users` |
| Cơ sở | Tên cơ sở từ bảng `facilities` |
| Kỳ hóa đơn | Kỳ hóa đơn |
| Tổng tiền hóa đơn | Tổng tiền phải nộp |
| Đã thanh toán | Tổng tiền đã thanh toán thành công |
| Còn nợ | Số tiền còn nợ |
| Hạn thanh toán | Hạn thanh toán của hóa đơn |
| Số ngày nợ | Số ngày đã quá hạn |
| Phí chậm nộp tạm tính | Chỉ hiển thị, không lưu |
| Trạng thái | `UNPAID` hoặc `OVERDUE` |
| Hành động | Xem chi tiết |

---

## 7.2 Màn hình chi tiết công nợ

Màn hình chi tiết công nợ hiển thị thông tin chi tiết của hóa đơn đang nợ.

### Thành phần chính

- Thông tin hóa đơn

- Thông tin người thuê

- Thông tin phòng

- Thông tin cơ sở

- Chi tiết các khoản tiền trong hóa đơn

- Thông tin thanh toán liên quan

- Thông tin quá hạn

- Phí chậm nộp tạm tính

### Ghi chú hiển thị

Tại phần phí chậm nộp tạm tính, hệ thống cần hiển thị ghi chú:

```text
Phí chậm nộp chỉ là số tiền tham khảo, chưa được lưu vào hóa đơn. Nếu cần thu khoản này, Ban quản lý vui lòng nhập vào Khoản phí khác của hóa đơn.
```

---

# 8. Technical Constraints

- Feature này chỉ dành cho người dùng có vai trò `Management Board`.

- Tất cả API phải yêu cầu authentication token.

- Backend phải kiểm tra quyền truy cập cho tất cả request.

- Không tạo bảng công nợ riêng.

- Danh sách công nợ phải được lấy từ bảng `invoices`.

- Chỉ lấy hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`.

- Hệ thống phải join sang bảng `rooms` để lấy mã phòng.

- Hệ thống phải join sang bảng `users` để lấy thông tin người thuê.

- Hệ thống phải join sang bảng `facilities` để lấy thông tin cơ sở.

- Hệ thống phải join sang bảng `payments` để lấy thông tin thanh toán.

- Số tiền còn nợ được tính khi hiển thị, không lưu vào bảng riêng.

- Số ngày nợ được tính khi hiển thị, không lưu vào bảng riêng.

- Phí chậm nộp tạm tính được tính khi hiển thị, không lưu vào bất kỳ bảng nào.

- Phí chậm nộp tạm tính không được tự động cộng vào tổng tiền hóa đơn.

- Phí chậm nộp tạm tính không được tự động tạo payment.

- Nếu Ban quản lý muốn thu phí chậm nộp, Ban quản lý tự nhập vào `Khoản phí khác` của hóa đơn.

- API danh sách công nợ phải phản hồi dưới `1000ms (p95)`.

- API chi tiết công nợ phải phản hồi dưới `500ms (p95)`.

- Rate limit: `100 requests/phút/người dùng`.

---

# 9. Error Codes

| Error Code | HTTP Status | Description |
| --- | --- | --- |
| `UNAUTHORIZED` | 401 | Người dùng chưa đăng nhập |
| `FORBIDDEN` | 403 | Người dùng không có quyền truy cập chức năng |
| `INVALID_DEBT_STATUS` | 400 | Trạng thái lọc công nợ không hợp lệ |
| `INVOICE_NOT_FOUND` | 404 | Không tìm thấy hóa đơn |
| `NOT_A_DEBT_INVOICE` | 400 | Hóa đơn không thuộc danh sách công nợ |
| `DEBT_LIST_QUERY_FAILED` | 500 | Không thể truy xuất danh sách công nợ |
| `DEBT_DETAIL_QUERY_FAILED` | 500 | Không thể truy xuất chi tiết công nợ |

---

# 10. Out Of Scope

Các chức năng sau không nằm trong phạm vi feature Quản lý công nợ:

- Tạo bảng công nợ riêng.

- Tạo công nợ thủ công.

- Chỉnh sửa công nợ trực tiếp.

- Xóa công nợ.

- Thanh toán trực tuyến.

- Tích hợp cổng thanh toán.

- Ghi nhận thanh toán mới trong màn công nợ.

- Hoàn tiền.

- Quản lý hóa đơn VAT.

- Xuất báo cáo tài chính.

- Gửi email tự động cho người thuê.

- Gửi SMS tự động cho người thuê.

- Gửi thông báo trực tiếp từ hệ thống tới người thuê.

- Gửi thông báo công nợ nội bộ.

- Xem lịch sử thông báo công nợ.

- Tự động cộng phí chậm nộp vào hóa đơn.

- Tự động lưu phí chậm nộp vào database.

- Tự động tạo hóa đơn phạt chậm nộp.

- Tự động khóa hợp đồng thuê khi quá hạn thanh toán.

- Xử lý khiếu nại tài chính.

- Quản lý thu chi nội bộ.

- Chức năng nhắc nợ tự động theo lịch.