# Feature: Quản lý công nợ

**Status:** Draft **Author:** Bùi Đỉnh **Reviewer:** \[Tên\] **Date:** \[YYYY-MM-DD\] **Priority:** Medium

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, công nợ là các hóa đơn chưa được thanh toán hoặc đã quá hạn thanh toán. Feature Quản lý công nợ không tạo ra một bảng công nợ riêng. Danh sách công nợ được hệ thống truy xuất trực tiếp từ bảng `invoices`. Một hóa đơn được xem là công nợ khi hóa đơn có trạng thái:

- `UNPAID`: Chưa thanh toán
- `OVERDUE`: Đã quá hạn thanh toán Khi Ban quản lý truy cập chức năng Quản lý công nợ, hệ thống sẽ lấy danh sách các hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`, sau đó kết nối dữ liệu với các bảng liên quan để hiển thị đầy đủ thông tin công nợ. Các bảng dữ liệu liên quan bao gồm:
- `invoices`: Lưu thông tin hóa đơn, tổng tiền phải nộp, hạn thanh toán, trạng thái.
- `rooms`: Lưu thông tin phòng, mã phòng.
- `users`: Lưu thông tin người thuê.
- `facilities`: Lưu thông tin cơ sở mà phòng thuộc về.
- `payments`: Lưu thông tin thanh toán liên quan đến hóa đơn. Ban quản lý có thể xem:
- Người thuê đang nợ.
- Phòng đang phát sinh công nợ.
- Cơ sở phát sinh công nợ.
- Hóa đơn nào đang chưa thanh toán hoặc quá hạn.
- Số tiền còn nợ.
- Số ngày nợ.
- Phí chậm nộp tạm tính để tham khảo.
- Chi tiết hóa đơn đang nợ.

**Phí chậm nộp tạm tính**: Nếu hóa đơn chưa thanh toán và đã quá hạn, hệ thống sẽ tự động tính phí chậm nộp dựa trên số ngày quá hạn (1% tiền phòng mỗi ngày). Khoản phí này sẽ được **cộng dồn** vào `Tạm tính` và `Tổng tiền phải nộp` để đảm bảo đồng bộ với hiển thị ở bên danh sách hóa đơn. Khi thanh toán thành công (chuyển sang `PAID`), giá trị `late_fee` này sẽ được chốt và lưu cố định vào cơ sở dữ liệu.

Feature này giúp Ban quản lý theo dõi các khoản chưa thu, kiểm tra hóa đơn quá hạn và chủ động xử lý công nợ với người thuê.

## Làm rõ thông tin (Clarifications)

### Phiên làm việc ngày 2026-07-25
- Hỏi: Phí phạt chậm nộp hiện tại được quy định là 1% tiền phòng mỗi ngày. Mức 1% này nên được gắn cố định (hardcoded) hay có thể cấu hình được? → Đáp: Gắn cố định 1% (Hardcoded constant)
- Hỏi: Đặc tả đã định nghĩa tính năng lọc và tìm kiếm cho danh sách công nợ, nhưng chưa xác định thứ tự sắp xếp mặc định. Danh sách công nợ nên được sắp xếp mặc định như thế nào? → Đáp: Sắp xếp theo hạn thanh toán (Cũ nhất xếp trước)
- Hỏi: Phí tạm tính (Phí phạt chậm nộp) có nên cộng dồn vào Tổng tiền không? → Đáp: Có, phải cộng cả phí tạm tính vào "Tạm tính" và "Tổng tiền phải nộp" để đồng bộ với bên hóa đơn.

### Phiên làm việc ngày 2026-07-25 (Tiếp)
- Q: Hành động "Nhắc nợ" đối với các hóa đơn quá hạn sẽ được thực hiện qua kênh giao tiếp nào? → A: Gửi thông báo trực tiếp trên hệ thống (In-app notification) cho tài khoản của người thuê.

---

# 2. User Stories

## Story 1: Xem danh sách công nợ

Là Ban quản lý, tôi muốn xem danh sách các hóa đơn chưa thanh toán hoặc quá hạn để theo dõi các khoản tiền người thuê còn nợ.

## Story 2: Xem thông tin người thuê của công nợ

Là Ban quản lý, tôi muốn xem thông tin người thuê liên quan đến hóa đơn nợ để biết cần liên hệ với ai khi xử lý công nợ.

## Story 3: Xem thông tin phòng và cơ sở của công nợ

Là Ban quản lý, tôi muốn xem mã phòng và cơ sở phát sinh công nợ để xác định đúng nơi phát sinh khoản nợ.

## Story 4: Xem số ngày nợ

Là Ban quản lý, tôi muốn xem hóa đơn đang nợ bao nhiêu ngày để đánh giá mức độ quá hạn của công nợ.

## Story 5: Xem số tiền còn nợ

Là Ban quản lý, tôi muốn xem hóa đơn còn nợ bao nhiêu tiền để biết số tiền cần thu từ người thuê.

## Story 6: Xem phí chậm nộp tạm tính

Là Ban quản lý, tôi muốn hệ thống hiển thị phí chậm nộp tạm tính nếu hóa đơn nộp muộn quá 03 ngày để có cơ sở tham khảo khi xử lý công nợ.

## Story 7: Xem chi tiết hóa đơn nợ

Là Ban quản lý, tôi muốn xem chi tiết hóa đơn đang nợ để kiểm tra tiền phòng, điện, nước, phí dịch vụ, tiền Internet, phí khác và tổng tiền phải nộp.

## Story 8: Tìm kiếm và lọc công nợ

Là Ban quản lý, tôi muốn tìm kiếm công nợ theo mã hóa đơn hoặc mã phòng để nhanh chóng tra cứu khoản công nợ cần kiểm tra.

## Story 9: Kiểm tra quyền truy cập

Là hệ thống, khi người dùng chưa đăng nhập hoặc không có quyền Ban quản lý, tôi muốn từ chối truy cập chức năng Quản lý công nợ để bảo vệ dữ liệu tài chính của hệ thống.

## Story 10: Gửi nhắc nhở thanh toán

Là Ban quản lý, tôi muốn có thể gửi nhắc nhở thanh toán cho các hóa đơn quá hạn để yêu cầu người thuê thanh toán.

# 3. Acceptance Criteria (EARS)

## 3.1 Xem danh sách công nợ

KHI Ban quản lý truy cập màn hình Quản lý công nợ, THE SYSTEM SHALL truy xuất dữ liệu từ bảng `invoices`. KHI truy xuất danh sách công nợ, THE SYSTEM SHALL chỉ lấy các hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`.

**Quy tắc xác định công nợ:** Công nợ không được lưu ở một bảng riêng. Một bản ghi được xem là công nợ khi bản ghi đó nằm trong bảng `invoices` và có trạng thái `UNPAID` hoặc `OVERDUE`. Các hóa đơn có trạng thái `PAID` không hiển thị trong danh sách công nợ.

**Logic truy vấn dữ liệu (Tham khảo)**:Danh sách công nợ được lấy từ bảng `invoices`, sau đó hệ thống join sang các bảng liên quan: `rooms` (mã phòng), `users` (người thuê), `facilities` (cơ sở), `payments` (thông tin thanh toán).

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

*(Lưu ý: Tên cột thực tế có thể điều chỉnh theo database thật của dự án).*

KHI danh sách công nợ được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:

- `invoiceId`
- Mã hóa đơn
- Mã phòng
- Tên người thuê
- Kỳ hóa đơn
- Tổng tiền phải nộp
- Hạn thanh toán
- Số ngày nợ
- Phí chậm nộp tạm tính
- Trạng thái hóa đơn
- Hành động xem chi tiết
- Hành động nhắc nợ (chỉ hiển thị với hóa đơn quá hạn - OVERDUE)

KHI không có hóa đơn nào có trạng thái `UNPAID` hoặc `OVERDUE`, THE SYSTEM SHALL hiển thị thông báo:

```text
Không có công nợ nào
Không tìm thấy dữ liệu công nợ phù hợp.
```

KHI số lượng công nợ lớn hơn kích thước một trang, THE SYSTEM SHALL hiển thị dữ liệu theo phân trang. KHI danh sách công nợ được truy xuất, THE SYSTEM SHALL mặc định sắp xếp danh sách theo hạn thanh toán tăng dần (Hóa đơn quá hạn lâu nhất hiển thị đầu tiên).

## 3.2 Truy xuất thông tin phòng

KHI hệ thống lấy danh sách công nợ từ bảng `invoices`, THE SYSTEM SHALL nối sang bảng `rooms` để lấy thông tin phòng. KHI nối sang bảng `rooms`, THE SYSTEM SHALL hiển thị mã phòng tương ứng với hóa đơn.

## 3.3 Truy xuất thông tin người thuê

KHI hệ thống lấy danh sách công nợ, THE SYSTEM SHALL nối sang bảng `users` để lấy thông tin người thuê. KHI thông tin người thuê được tìm thấy, THE SYSTEM SHALL hiển thị:

- Tên người thuê
- Số điện thoại
- Email

## 3.4 Truy xuất thông tin cơ sở

KHI hệ thống lấy danh sách công nợ, THE SYSTEM SHALL nối sang bảng `facilities` để lấy thông tin cơ sở. KHI thông tin cơ sở được tìm thấy, THE SYSTEM SHALL hiển thị:

- Tên cơ sở

## 3.5 Truy xuất thông tin thanh toán

KHI hệ thống lấy danh sách công nợ, THE SYSTEM SHALL nối sang bảng `payments` để lấy thông tin thanh toán của hóa đơn. KHI hóa đơn có thanh toán thành công, THE SYSTEM SHALL tính tổng số tiền đã thanh toán từ các bản ghi thanh toán hợp lệ. KHI hóa đơn chưa có thanh toán thành công, THE SYSTEM SHALL xem số tiền đã thanh toán là `0`. KHI tính số tiền còn nợ, THE SYSTEM SHALL tính theo công thức:

```text
Số tiền còn nợ = MAX(0, Tổng tiền phải nộp - Tổng tiền đã thanh toán thành công)
```

*(Lưu ý: Nếu hệ thống không hỗ trợ thanh toán từng phần, Tổng tiền đã thanh toán thành công thường là 0 đối với hóa đơn* `UNPAID` *hoặc* `OVERDUE`*).*

KHI số tiền còn nợ nhỏ hơn 0, THE SYSTEM SHALL hiển thị số tiền còn nợ là `0`.

## 3.6 Tính số ngày nợ

KHI hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`, THE SYSTEM SHALL tính số ngày nợ dựa trên ngày hiện tại và hạn thanh toán. Công thức:

```text
Số ngày nợ = MAX(0, Ngày hiện tại - Hạn thanh toán)
```

KHI ngày hiện tại nhỏ hơn hoặc bằng hạn thanh toán, THE SYSTEM SHALL hiển thị số ngày nợ là `0`. KHI ngày hiện tại lớn hơn hạn thanh toán, THE SYSTEM SHALL hiển thị số ngày nợ là số ngày đã vượt quá hạn thanh toán.

Ví dụ:

```text
Ngày hiện tại = 2026-07-05
Hạn thanh toán = 2026-06-30

Số ngày nợ = 5 ngày
```

## 3.7 Tính phí chậm nộp tạm tính

KHI hóa đơn ở trạng thái `OVERDUE` (đã quá hạn thanh toán), THE SYSTEM SHALL tính phí chậm nộp tạm tính. Phí chậm nộp được tính ngay từ ngày đầu tiên quá hạn. Công thức tính dựa trên số ngày nợ và `1%` tiền phòng cho mỗi ngày chậm nộp:

```text
Phí chậm nộp tạm tính = Số ngày nợ * (Tiền phòng * 0.01)
```

*(Lưu ý: Mức phạt 1% là hằng số được gán cố định (hardcoded) trong hệ thống, không yêu cầu chức năng cấu hình).*

**Quy tắc đóng băng phí phạt:**
KHI có một giao dịch thanh toán cho hóa đơn đang ở trạng thái chờ duyệt (`PENDING`), THE SYSTEM SHALL tạm thời đóng băng việc tính phí quá hạn. Số ngày nợ sẽ được tính từ ngày đến hạn đến ngày tạo giao dịch thanh toán đó thay vì ngày hiện tại.

Ví dụ:
```text
Ngày hiện tại = 2026-07-05
Hạn thanh toán = 2026-06-30
Ngày tạo giao dịch thanh toán (PENDING) = 2026-07-02

Số ngày tính nợ (được đóng băng) = 2026-07-02 - 2026-06-30 = 2 ngày
Tiền phòng = 3,000,000

Phí chậm nộp tạm tính = 2 * (3,000,000 * 0.01) = 60,000 đ
```

Nếu giao dịch này bị Ban quản lý từ chối (`REJECTED`), THE SYSTEM SHALL tiếp tục tính phí chậm nộp dựa trên ngày hiện tại.
Nếu giao dịch này được duyệt (`SUCCESS`), THE SYSTEM SHALL lưu cố định mức phí đã đóng băng này.

KHI hóa đơn chuyển sang trạng thái `PAID`, THE SYSTEM SHALL tính toán lần cuối phí chậm nộp và lưu giá trị này vào cột `late_fee` của bảng `invoices`, đồng thời cập nhật `total_amount` cố định trong cơ sở dữ liệu.

KHI Ban quản lý muốn thu phí chậm nộp, THE SYSTEM SHALL yêu cầu Ban quản lý tự nhập khoản phí này vào `Khoản phí khác` của hóa đơn.

## 3.8 Tìm kiếm công nợ

KHI Ban quản lý tìm kiếm theo mã hóa đơn, THE SYSTEM SHALL trả về các công nợ có mã hóa đơn phù hợp. KHI Ban quản lý tìm kiếm theo mã phòng, THE SYSTEM SHALL trả về các công nợ có mã phòng phù hợp. KHI không có công nợ nào phù hợp với điều kiện tìm kiếm, THE SYSTEM SHALL trả về danh sách rỗng.

## 3.9 Lọc công nợ theo trạng thái

Trạng thái công nợ chính là trạng thái của hóa đơn trong bảng `invoices`. Các trạng thái hợp lệ trong feature này:

- `UNPAID`: Hóa đơn chưa thanh toán
- `OVERDUE`: Hóa đơn chưa thanh toán và đã quá hạn

KHI Ban quản lý lọc theo trạng thái `UNPAID`, THE SYSTEM SHALL chỉ hiển thị các hóa đơn có trạng thái `UNPAID`. KHI Ban quản lý lọc theo trạng thái `OVERDUE`, THE SYSTEM SHALL chỉ hiển thị các hóa đơn có trạng thái `OVERDUE`. KHI Ban quản lý không chọn trạng thái, THE SYSTEM SHALL hiển thị tất cả hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`. KHI Ban quản lý truyền trạng thái khác `UNPAID` hoặc `OVERDUE`, THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_DEBT_STATUS`.

## 3.10 Xem chi tiết hóa đơn nợ

KHI Ban quản lý chọn một công nợ, THE SYSTEM SHALL mở màn hình chi tiết hóa đơn nợ. Chi tiết công nợ thực chất là chi tiết hóa đơn đang có trạng thái `UNPAID` hoặc `OVERDUE`. Hệ thống không lấy dữ liệu từ bảng công nợ riêng mà lấy từ: `invoices`, `rooms`, `users`, `facilities`, `payments`.

KHI chi tiết hóa đơn nợ được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:

- Các nút hành động: Quay lại danh sách, Xem hóa đơn gốc.
- Thông tin tính tiền chi tiết (Tiền phòng, Tiền điện, Tiền nước, Phí dịch vụ, Tiền Internet, Phí khác) bao gồm chỉ số cũ, chỉ số mới, mức sử dụng và đơn giá đối với điện/nước.
- Tổng tiền tạm tính và tổng tiền phải nộp.
- Ghi chú hóa đơn (nếu có).
- Thông tin người thuê (Họ tên, Số điện thoại, Email).
- Tình trạng công nợ: Kỳ hóa đơn, Kỳ hợp đồng, Hạn thanh toán, Tổng phải thu, Đã thanh toán và số tiền CÒN NỢ.
- Cảnh báo: Số ngày quá hạn và phí chậm nộp tạm tính (nếu có).

KHI hóa đơn không tồn tại, hoặc không thuộc quyền quản lý, THE SYSTEM SHALL trả về HTTP 404 (Not Found) kèm thông báo lỗi tương ứng.

## 3.11 Phân quyền

KHI người dùng có vai trò `Management Board`, THE SYSTEM SHALL cho phép truy cập chức năng Quản lý công nợ. KHI người dùng chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`. KHI người dùng không có vai trò `Management Board`, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

## 3.12 Gửi nhắc nhở thanh toán (Nhắc nợ)

KHI Ban quản lý bấm "Nhắc nợ" trên một hóa đơn có trạng thái `OVERDUE` tại danh sách công nợ, THE SYSTEM SHALL điều hướng người dùng tới chức năng gửi thông báo nhắc nhở thanh toán cho hóa đơn đó. Hệ thống sẽ gửi thông báo trực tiếp trên ứng dụng (In-app notification) đến tài khoản của người thuê.

# 4. Servlet Contract

## 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `DebtPageServlet` |
| **URL Pattern** | `GET /manager/debts` — danh sách công nợ |
| **URL Pattern** | `GET /manager/debts?action=detail&id={id}` — chi tiết công nợ |
| **Phân quyền** | Role = `MANAGER` (kiểm tra qua `UserSessionDTO`) |

---

## 4.2 Request Attributes — Danh sách (index.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `debts` | `List<DebtListItemDTO>` | `DebtService.getDebts(...)` | Danh sách công nợ thuộc quản lý của người dùng (từ bảng `invoices`) |
| `currentPage` | `Integer` | Query param `page` | Trang hiện tại để phục vụ phân trang |
| `totalPages` | `Integer` | `DebtService.getTotalPages(...)` | Tổng số trang dựa trên dữ liệu tìm kiếm |
| `keyword` | `String` | Query param `keyword` | Từ khóa tìm kiếm hiện tại để giữ state cho form |
| `status` | `String` | Query param `status` | Trạng thái lọc hiện tại để giữ state cho form |

**Lưu ý:** `DebtPageServlet` chỉ lấy ra các hóa đơn mang trạng thái `UNPAID` hoặc `OVERDUE`.

---

## 4.3 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `debt` | `DebtDetailDTO` | `DebtService.getDebtDetail(managerId, invoiceId)` | Thông tin chi tiết công nợ (hóa đơn, phòng, tiền tính toán, người thuê) |

---

## 4.4 Xử lý lỗi (Servlet Behavior)

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Chuyển hướng về `/login` |
| Role không phải MANAGER | `response.sendError(HttpServletResponse.SC_FORBIDDEN)` |
| `id` (Detail) rỗng hoặc sai format | `response.sendRedirect(request.getContextPath() + "/manager/debts")` |
| Công nợ không tồn tại hoặc không thuộc quyền quản lý | `response.sendError(HttpServletResponse.SC_NOT_FOUND)` |

---

# 5. Technical Constraints

- **Max response time**: 500ms (P95) khi tải danh sách và chi tiết công nợ.
- **Rate limit**: 100 requests/phút/người dùng.
- **Phân trang**: Hỗ trợ phân trang cho danh sách công nợ để tối ưu hiệu suất truy vấn.
- **Tính toán theo thời gian thực**: Việc tính toán số tiền còn nợ, số ngày nợ và phí chậm nộp tạm tính phải được thực hiện on-the-fly (thời gian thực) khi truy vấn, không lưu trực tiếp vào cơ sở dữ liệu để tránh dư thừa dữ liệu.
- **Audit Log**: Ghi nhận log (nhật ký hệ thống) cho các thao tác xem chi tiết công nợ và thực hiện gửi nhắc nợ thủ công.
- **Phân quyền**: Chỉ người dùng có vai trò `Management Board` (MANAGER) mới được phép truy cập các URL và API của chức năng Quản lý công nợ.

# 6. Out Of Scope

Các chức năng sau không nằm trong phạm vi feature Quản lý công nợ:

- Tạo bảng công nợ riêng.
- Tạo công nợ thủ công.
- Chỉnh sửa công nợ trực tiếp.
- Xóa công nợ.
- Ghi nhận thanh toán mới trong màn công nợ.
- Tự động cộng phí chậm nộp vào hóa đơn.
- Tự động lưu phí chậm nộp vào database.
- Tự động tạo hóa đơn phạt chậm nộp.
- Chức năng nhắc nợ tự động theo lịch (tuy nhiên có hỗ trợ hành động nhắc nợ thủ công).