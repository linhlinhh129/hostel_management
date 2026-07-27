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

# 4. Servlet Routes & Page Controller Contract

## 4.1 Màn hình Danh sách hợp đồng của tôi

### Servlet Mapping

```http
GET /tenant/contracts
```

### Xử lý Request & View
- **Servlet:** `TenantContractListServlet`
- **Mô tả:** Tiếp nhận request từ người thuê, lấy `tenant_id` từ `HttpSession`, gọi Service lấy danh sách hợp đồng thuộc người thuê này và chuyển dữ liệu sang giao diện JSP.
- **Scope & Attribute Name:** `request.setAttribute("contractList", List<ContractDTO>)`
- **Forward View:** `/WEB-INF/views/tenant/contract-list.jsp`

---

## 4.2 Màn hình Chi tiết hợp đồng

### Servlet Mapping

```http
GET /tenant/contract-detail?id={contractId}
```

### Xử lý Request & View
- **Servlet:** `TenantContractDetailServlet`
- **Parameter:** `id` (mã định danh hợp đồng `contractId`)
- **Mô tả:** Lấy thông tin chi tiết hợp đồng theo `id`. Kiểm tra xem hợp đồng đó có thuộc `tenant_id` của người dùng hiện tại không.
- **Scope & Attribute Name:** `request.setAttribute("contract", ContractDetailDTO)`
- **Forward View:** `/WEB-INF/views/tenant/contract-detail.jsp`
- **Trường hợp lỗi:**
  - Nếu `id` không tồn tại: Chuyển hướng hoặc Forward đến trang lỗi 404 (Contract Not Found).
  - Nếu hợp đồng không thuộc về `tenant_id` đang đăng nhập: Chuyển hướng hoặc Forward đến trang lỗi 403 (Access Denied).

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

- Chỉ người dùng có vai trò `TENANT` trong `HttpSession` mới được truy cập các Servlet này.

- Chỉ truy vấn các hợp đồng có `tenant_id = currentUser.id`.

- Toàn bộ dữ liệu hiển thị ở chế độ chỉ đọc.

- Không cho phép thay đổi dữ liệu thông qua bất kỳ Servlet/Action nào của người thuê.

---

# 8. Error Codes & Redirection

| Error Code | Status / Action | Description |
| --- | --- | --- |
| UNAUTHORIZED | Redirect `/login` | Chưa đăng nhập (Session không tồn tại) |
| FORBIDDEN | Forward 403 Page | Không có vai trò TENANT |
| CONTRACT_ACCESS_DENIED | Forward 403 Page | Hợp đồng yêu cầu không thuộc về tài khoản đang đăng nhập |
| CONTRACT_NOT_FOUND | Forward 404 Page | Không tìm thấy mã hợp đồng tương ứng |

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