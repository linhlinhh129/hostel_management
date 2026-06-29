# Feature: Xem hợp đồng thuê (Tenant Contract)

**Status:** Draft\
**Priority:** Medium

# 1. Business Context

Chức năng **Xem hợp đồng thuê** cho phép người thuê xem thông tin hợp đồng thuê phòng đang có hiệu lực hoặc các hợp đồng trước đây của chính mình.

Người thuê chỉ có quyền xem hợp đồng đã được Ban quản lý tạo. Người thuê không được tạo, chỉnh sửa, xóa hoặc thay đổi trạng thái hợp đồng.

Thông tin hợp đồng được hiển thị nhằm giúp người thuê tra cứu các điều khoản thuê phòng, thời hạn hợp đồng, tiền thuê, tiền cọc và các khoản phí đã được thỏa thuận.

---

# 2. User Stories

## Story 1: Xem danh sách hợp đồng

Là **Người thuê**, tôi muốn xem danh sách các hợp đồng của mình để biết các hợp đồng đang có hoặc đã kết thúc.

---

## Story 2: Xem chi tiết hợp đồng

Là **Người thuê**, tôi muốn xem đầy đủ nội dung của một hợp đồng để kiểm tra các điều khoản đã ký.

---

## Story 3: Kiểm tra quyền truy cập

Là **Hệ thống**, tôi chỉ cho phép người thuê xem các hợp đồng thuộc tài khoản của chính họ nhằm bảo vệ dữ liệu.

---

# 3. Acceptance Criteria (EARS)

## 3.1 Xem danh sách hợp đồng

KHI người thuê truy cập màn hình **Hợp đồng của tôi**,\
THE SYSTEM SHALL hiển thị danh sách hợp đồng có `tenant_id` bằng người dùng đang đăng nhập.

KHI danh sách được hiển thị,\
THE SYSTEM SHALL hiển thị:

- Mã hợp đồng

- Mã phòng

- Ngày bắt đầu

- Ngày hết hạn

- Tiền thuê

- Tiền cọc

- Trạng thái hợp đồng

- Nút **Xem chi tiết**

KHI người thuê chưa có hợp đồng nào,\
THE SYSTEM SHALL hiển thị:

```text
Bạn chưa có hợp đồng thuê nào.
```

---

## 3.2 Xem chi tiết hợp đồng

KHI người thuê chọn một hợp đồng,\
THE SYSTEM SHALL hiển thị:

- Mã hợp đồng

- Thông tin phòng

- Địa chỉ phòng

- Ngày lập hợp đồng

- Ngày bắt đầu

- Ngày hết hạn

- Tiền thuê

- Tiền cọc

- Giá điện

- Giá nước

- Phí Internet

- Phí dịch vụ

- Điều khoản hợp đồng

- Trạng thái hợp đồng

---

## 3.3 Phân quyền

KHI người thuê yêu cầu xem hợp đồng,\
THE SYSTEM SHALL chỉ trả về hợp đồng có `tenant_id` trùng với tài khoản đang đăng nhập.

KHI người thuê cố truy cập hợp đồng của người khác,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi:

```text
CONTRACT_ACCESS_DENIED
```

KHI hợp đồng không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi:

```text
CONTRACT_NOT_FOUND
```

---

# 4. API Contract

## 4.1 Danh sách hợp đồng của tôi

### Endpoint

```http
GET /api/v1/tenant/contracts
```

### Response

```json
{
  "success": true,
  "data": [
    {
      "contractId": 1,
      "code": "HD-0001",
      "roomCode": "A101",
      "startDate": "2026-01-01",
      "endDate": "2026-12-31",
      "status": "ACTIVE"
    }
  ]
}
```

---

## 4.2 Chi tiết hợp đồng

### Endpoint

```http
GET /api/v1/tenant/contracts/{contractId}
```

---

# 5. Business Rules

- Người thuê chỉ xem được hợp đồng của chính mình.

- Không được tạo hợp đồng.

- Không được chỉnh sửa hợp đồng.

- Không được xóa hợp đồng.

- Không được thay đổi trạng thái hợp đồng.

- Không được xem hợp đồng của người thuê khác.

- Thông tin hiển thị là dữ liệu đã được Ban quản lý xác nhận.

---

# 6. UI/UX

## Màn hình "Hợp đồng của tôi"

Hiển thị:

- Mã hợp đồng

- Mã phòng

- Ngày bắt đầu

- Ngày hết hạn

- Trạng thái

- Nút **Xem chi tiết**

---

## Màn hình chi tiết

Hiển thị đầy đủ nội dung hợp đồng ở chế độ chỉ đọc (Read-only).

Không hiển thị các nút:

- Tạo

- Chỉnh sửa

- Xóa

- Cập nhật trạng thái

---

# 7. Technical Constraints

- Chỉ người dùng có vai trò `TENANT` mới được truy cập.

- Chỉ truy vấn các hợp đồng có `tenant_id = currentUser.id`.

- Toàn bộ dữ liệu hiển thị ở chế độ chỉ đọc.

- Không cho phép thay đổi dữ liệu thông qua API của người thuê.

---

# 8. Error Codes

| Error Code | HTTP | Description |
| --- | --- | --- |
| UNAUTHORIZED | 401 | Chưa đăng nhập |
| FORBIDDEN | 403 | Không có quyền truy cập |
| CONTRACT_ACCESS_DENIED | 403 | Không có quyền xem hợp đồng này |
| CONTRACT_NOT_FOUND | 404 | Không tìm thấy hợp đồng |

---

# 9. Out of Scope

- Tạo hợp đồng

- Chỉnh sửa hợp đồng

- Xóa hợp đồng

- Gia hạn hợp đồng

- Thanh lý hợp đồng

- Ký hợp đồng điện tử

- In hợp đồng

- Thay đổi trạng thái hợp đồng