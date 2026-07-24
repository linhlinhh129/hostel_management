# Feature: Quản lý hóa đơn

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** \[YYYY-MM-DD\]\
**Priority:** High

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, hóa đơn là tài liệu tài chính được sử dụng để ghi nhận các khoản phí mà người thuê phải thanh toán trong từng kỳ. Mỗi hóa đơn được tạo dựa trên thông tin phòng thuê, kỳ hạn hóa đơn, hạn thanh toán, tiền phòng cố định, chỉ số điện, chỉ số nước, đơn giá điện, đơn giá nước, phí dịch vụ, tiền Internet, phí khác phát sinh trong kỳ, thuế áp dụng theo quy định của hệ thống và ghi chú nếu có. Khi Ban quản lý tạo hóa đơn, người dùng không cần nhập thủ công toàn bộ thông tin tiền điện, tiền nước, phí dịch vụ, tiền Internet. Hệ thống sẽ tự động truy xuất dữ liệu từ các bảng liên quan:

- Thông tin phòng từ bảng phòng.
- Thông tin cơ sở của phòng.
- Đơn giá điện, đơn giá nước, phí dịch vụ và tiền Internet hiện tại từ bảng `facilities`.
- Chỉ số điện cũ, chỉ số điện mới, chỉ số nước cũ, chỉ số nước mới và ảnh minh chứng công tơ từ bảng ghi nhận chỉ số điện nước.
- Tiền phòng cố định từ thông tin phòng, hợp đồng thuê hoặc cấu hình giá phòng của hệ thống.
- Thông tin người thuê (họ tên, số điện thoại, email) từ dữ liệu hợp đồng/người dùng. Sau khi truy xuất đủ dữ liệu, hệ thống tự động tính:
- Số điện tiêu thụ và Thành tiền điện.
- Số nước tiêu thụ và Thành tiền nước.
- Phí dịch vụ và Tiền Internet.
- Tạm tính.
- Tiền thuế.
- Tổng tiền phải nộp.

Hóa đơn đóng vai trò là căn cứ để người thuê thực hiện thanh toán và để Ban quản lý theo dõi tình trạng thu tiền. Feature Quản lý hóa đơn cho phép Ban quản lý tạo hóa đơn, xem danh sách hóa đơn, tìm kiếm hóa đơn, xem chi tiết hóa đơn, điều chỉnh thông tin hóa đơn trước khi phát hành và xuất hóa đơn dưới dạng tài liệu PDF để lưu trữ hoặc gửi cho các bên liên quan. Feature này giúp chuẩn hóa quy trình quản lý tài chính, đảm bảo tính chính xác của dữ liệu hóa đơn và hỗ trợ kiểm soát công nợ trong hệ thống nhà trọ.

---

# 2. User Stories

## Story 1: Tạo hóa đơn

Là Ban quản lý, tôi muốn tạo hóa đơn cho một phòng theo từng kỳ hạn để ghi nhận các khoản phí mà người thuê cần thanh toán.

## Story 2: Tự động truy xuất dữ liệu khi tạo hóa đơn

Là Ban quản lý, tôi muốn hệ thống tự động lấy đơn giá điện, đơn giá nước, phí dịch vụ và chỉ số điện nước từ các bảng liên quan để hóa đơn được tính chính xác mà không cần nhập thủ công toàn bộ dữ liệu.

## Story 3: Xem danh sách hóa đơn

Là Ban quản lý, tôi muốn xem danh sách hóa đơn trong hệ thống để theo dõi các khoản phí cần thu của người thuê.

## Story 4: Xem chi tiết hóa đơn

Là Ban quản lý, tôi muốn xem chi tiết hóa đơn để kiểm tra thông tin tính phí.

## Story 5: Điều chỉnh hóa đơn

Là Ban quản lý, tôi muốn điều chỉnh thông tin hóa đơn để sửa các sai sót trước khi phát hành hóa đơn.

## Story 6: In / Xuất PDF hóa đơn

Là Ban quản lý, tôi muốn in hoặc xuất PDF hóa đơn trực tiếp từ trình duyệt để lưu trữ hoặc cung cấp cho các bên liên quan, với giao diện được tối ưu cho bản in.

## Story 7: Tìm kiếm hóa đơn

Là Ban quản lý, tôi muốn tìm kiếm hóa đơn theo mã hóa đơn hoặc phòng để nhanh chóng tra cứu dữ liệu.

## Story 8: Kiểm tra hóa đơn không tồn tại

Là Ban quản lý, khi hóa đơn không tồn tại, tôi muốn hệ thống hiển thị lỗi phù hợp.

## Story 9: Kiểm tra quyền truy cập

Là hệ thống, khi người dùng không có quyền, tôi muốn từ chối truy cập chức năng quản lý hóa đơn.

# 3. Acceptance Criteria (EARS)

## 3.1 Tạo hóa đơn

KHI Ban quản lý truy cập màn hình Tạo hóa đơn, THE SYSTEM SHALL hiển thị form tạo hóa đơn.

KHI form tạo hóa đơn được hiển thị, THE SYSTEM SHALL cho phép Ban quản lý nhập/chọn các thông tin sau:

- Mã phòng
- Kỳ hạn hóa đơn
- Hạn thanh toán
- Phí khác
- Thuế (%)
- Note

KHI Ban quản lý chọn mã phòng, THE SYSTEM SHALL kiểm tra phòng tồn tại và hợp lệ.

KHI Ban quản lý chọn kỳ hạn hóa đơn, THE SYSTEM SHALL sử dụng kỳ hạn đó để tạo hóa đơn cho phòng được chọn.

KHI Ban quản lý nhập phí khác, THE SYSTEM SHALL kiểm tra phí khác lớn hơn hoặc bằng 0.

KHI Ban quản lý nhập thuế, THE SYSTEM SHALL kiểm tra thuế lớn hơn hoặc bằng 0.

KHI Ban quản lý tạo hóa đơn với dữ liệu hợp lệ, THE SYSTEM SHALL tự động truy xuất các dữ liệu cần thiết từ các bảng liên quan để tính hóa đơn.

KHI hóa đơn được tạo thành công, THE SYSTEM SHALL gán trạng thái thanh toán mặc định là `UNPAID`.

KHI hóa đơn được tạo thành công, THE SYSTEM SHALL lưu hóa đơn mới vào hệ thống.

KHI mã phòng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `ROOM_NOT_FOUND`.

KHI hạn thanh toán nhỏ hơn ngày hiện tại, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_DUE_DATE`.

KHI thuế nhỏ hơn 0, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_TAX_RATE`.

KHI phí khác nhỏ hơn 0, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_OTHER_FEE`.

**Quy tắc sinh mã hóa đơn**:Mã hóa đơn được hệ thống tự động sinh theo định dạng: `INV-{roomCode}-{billingPeriod}`Ví dụ: `INV-HN0101-202606`

- `INV`: Tiền tố hóa đơn
- `HN0101`: Mã phòng
- `202606`: Kỳ hạn hóa đơn, định dạng `YYYYMM`

**Quy tắc invoice_id:**`invoice_id` do hệ thống hoặc database tự động sinh theo thứ tự tăng dần (VD: 1, 2, 3...). Người dùng không được nhập trực tiếp `invoice_id`.

**Quy tắc không trùng hóa đơn**:Mỗi phòng chỉ được có một hóa đơn trong một kỳ hạn. Ví dụ: Phòng HN0101 đã có hóa đơn kỳ 202606 =&gt; Không được tạo thêm hóa đơn INV-HN0101-202606. KHI tạo trùng, THE SYSTEM SHALL trả về HTTP 400 `INVOICE_ALREADY_EXISTS`.

## 3.2 Tự động truy xuất dữ liệu khi tạo hóa đơn

KHI Ban quản lý tạo hóa đơn cho một phòng, THE SYSTEM SHALL truy xuất thông tin phòng dựa trên `roomCode`.

KHI truy xuất được thông tin phòng, THE SYSTEM SHALL xác định cơ sở mà phòng thuộc về.

KHI xác định được cơ sở của phòng, THE SYSTEM SHALL truy xuất đơn giá điện, đơn giá nước, phí dịch vụ và tiền Internet hiện tại từ bảng `facilities`.

KHI tạo hóa đơn theo kỳ hạn, THE SYSTEM SHALL truy xuất chỉ số điện cũ, chỉ số điện mới, chỉ số nước cũ, chỉ số nước mới và ảnh minh chứng công tơ của phòng trong kỳ hạn đó từ bảng ghi nhận chỉ số điện nước.

KHI truy xuất được chỉ số điện, THE SYSTEM SHALL tính số điện tiêu thụ theo công thức:

```text
Số điện tiêu thụ = Chỉ số điện mới - Chỉ số điện cũ
```

KHI truy xuất được đơn giá điện, THE SYSTEM SHALL tính thành tiền điện theo công thức:

```text
Thành tiền điện = Số điện tiêu thụ × Đơn giá điện
```

KHI truy xuất được chỉ số nước, THE SYSTEM SHALL tính số nước tiêu thụ theo công thức:

```text
Số nước tiêu thụ = Chỉ số nước mới - Chỉ số nước cũ
```

KHI truy xuất được đơn giá nước, THE SYSTEM SHALL tính thành tiền nước theo công thức:

```text
Thành tiền nước = Số nước tiêu thụ × Đơn giá nước
```

KHI truy xuất được phí dịch vụ hiện tại của cơ sở, THE SYSTEM SHALL đưa phí dịch vụ vào hóa đơn.

KHI truy xuất được tiền Internet, THE SYSTEM SHALL đưa tiền Internet vào hóa đơn.

KHI truy xuất được tiền phòng cố định, THE SYSTEM SHALL đưa tiền phòng cố định vào hóa đơn.

KHI đã có tiền phòng, tiền điện, tiền nước, phí dịch vụ, tiền Internet và phí khác, THE SYSTEM SHALL tính tạm tính.

KHI đã có tạm tính và thuế, THE SYSTEM SHALL tính tiền thuế.

KHI đã có tạm tính và tiền thuế, THE SYSTEM SHALL tính tổng tiền phải nộp.

KHI không tìm thấy dữ liệu đơn giá điện, đơn giá nước, phí dịch vụ, hoặc tiền Internet của cơ sở, THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `FACILITY_PRICE_NOT_FOUND`.

KHI không tìm thấy chỉ số điện nước của phòng trong kỳ hạn đã chọn, THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `METER_READING_NOT_FOUND`.

KHI chỉ số điện mới nhỏ hơn chỉ số điện cũ, THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `INVALID_ELECTRIC_READING`.

KHI chỉ số nước mới nhỏ hơn chỉ số nước cũ, THE SYSTEM SHALL từ chối tạo hóa đơn và trả về HTTP 400 với mã lỗi `INVALID_WATER_READING`.

**Nguồn dữ liệu truy xuất tự động**:Hệ thống không yêu cầu Ban quản lý nhập thủ công các phí dịch vụ và chỉ số. Các dữ liệu được lấy từ:

- **Bảng phòng / Cấu hình giá:** Tiền phòng cố định
- **Bảng** `facilities` **/ Hợp đồng:** Đơn giá điện, Đơn giá nước, Phí dịch vụ, Tiền Internet
- **Bảng ghi nhận chỉ số điện nước:** Chỉ số điện/nước cũ và mới, Ảnh minh chứng công tơ
- **Dữ liệu người dùng/hợp đồng:** Thông tin người thuê

**Quy tắc lưu snapshot giá**:KHI hóa đơn được tạo, THE SYSTEM SHALL lưu lại snapshot của đơn giá điện, đơn giá nước, phí dịch vụ, tiền Internet và tiền phòng tại thời điểm tạo hóa đơn. Mục đích là để hóa đơn cũ không bị thay đổi khi bảng `facilities` hoặc giá phòng thay đổi sau này. (VD: Đổi giá điện ngày 10/06 thì các hóa đơn tạo trước ngày 10/06 vẫn giữ giá cũ).

**Quy tắc tính tiền tự động**:Người dùng không được nhập trực tiếp các loại tiền, hệ thống tính dựa trên:

- `Số tiêu thụ` = `Chỉ số mới` - `Chỉ số cũ`
- `Thành tiền điện/nước` = `Số tiêu thụ` × `Đơn giá tương ứng`
- `Tạm tính` = `Tiền phòng` + `Tiền điện` + `Tiền nước` + `Phí dịch vụ` + `Tiền Internet` + `Phí khác`
- `Tiền thuế` = `Tạm tính` × `Thuế (%)`
- `Tổng tiền phải nộp` = `Tạm tính` + `Tiền thuế`

## 3.3 Xem danh sách hóa đơn

KHI Ban quản lý truy cập màn hình Quản lý hóa đơn, THE SYSTEM SHALL hiển thị danh sách hóa đơn.

KHI danh sách hóa đơn được hiển thị, THE SYSTEM SHALL hiển thị:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán KHI Ban quản lý tìm kiếm theo mã hóa đơn, THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI Ban quản lý tìm kiếm theo phòng, THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI Ban quản lý lọc theo trạng thái thanh toán, THE SYSTEM SHALL hiển thị các hóa đơn phù hợp với trạng thái được chọn.

KHI không tồn tại hóa đơn nào, THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại chưa có hóa đơn nào.
```

## 3.4 Xem chi tiết hóa đơn

KHI Ban quản lý chọn một hóa đơn, THE SYSTEM SHALL hiển thị:

- Các nút chức năng trên header: Xuất PDF/In, Báo cáo sai số, Sửa hóa đơn, Xóa hóa đơn (tùy theo trạng thái thanh toán)
- Thông tin tính tiền (Tiền phòng, điện, nước, phí dịch vụ, Internet, phí khác)
- Chỉ số cũ, mới, mức sử dụng, đơn giá, và thành tiền chi tiết cho điện nước
- Tạm tính, Thuế, Tổng tiền phải nộp
- Ghi chú (Note)
- Hình ảnh công tơ điện, công tơ nước (nếu có)
- Thông tin người thuê (Họ tên, SĐT, Email)
- Thông tin chung: Mã hóa đơn, Phòng, Kỳ hóa đơn, Hạn thanh toán, Trạng thái thanh toán, Ngày tạo, Cập nhật cuối

KHI hóa đơn không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `INVOICE_NOT_FOUND`.

## 3.5 Điều chỉnh hóa đơn

KHI Ban quản lý cập nhật hóa đơn với dữ liệu hợp lệ, THE SYSTEM SHALL lưu thông tin hóa đơn mới.

KHI phí khác thay đổi, THE SYSTEM SHALL cập nhật lại mức tạm tính và tổng tiền phải nộp.

KHI thuế thay đổi, THE SYSTEM SHALL tính lại tiền thuế và cập nhật tổng tiền phải nộp.

KHI Note thay đổi, THE SYSTEM SHALL lưu lại nội dung Note mới.

KHI hóa đơn đã thanh toán, THE SYSTEM SHALL từ chối điều chỉnh và trả về HTTP 400 với mã lỗi `PAID_INVOICE_CANNOT_BE_UPDATED`.

KHI chỉ số điện mới nhỏ hơn chỉ số điện cũ, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_ELECTRIC_READING`.

KHI chỉ số nước mới nhỏ hơn chỉ số nước cũ, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_WATER_READING`.

KHI hạn thanh toán nhỏ hơn ngày hiện tại, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_DUE_DATE`.

KHI thuế nhỏ hơn 0, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_TAX_RATE`.

KHI phí khác nhỏ hơn 0, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `INVALID_OTHER_FEE`.

## 3.6 In / Xuất PDF hóa đơn

KHI Ban quản lý click 'Xuất PDF / In', THE SYSTEM SHALL kích hoạt chức năng in của trình duyệt (`window.print()`).

KHI giao diện in được bật, THE SYSTEM SHALL tự động ẩn các thành phần điều hướng (sidebar, topbar, buttons) và chỉ hiển thị nội dung thông tin hóa đơn.

KHI hóa đơn không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `INVOICE_NOT_FOUND`.

## 3.7 Trạng thái hóa đơn

Hệ thống quản lý 3 trạng thái của hóa đơn:

- `UNPAID`: Hóa đơn chưa được thanh toán (Trạng thái mặc định khi vừa tạo).
- `PAID`: Hóa đơn đã được xác nhận thanh toán thành công (Không cho phép điều chỉnh hóa đơn ở trạng thái này).
- `OVERDUE`: Hóa đơn chưa thanh toán và đã quá hạn thanh toán.

KHI hóa đơn chưa được thanh toán, THE SYSTEM SHALL gán trạng thái `UNPAID`.

KHI hóa đơn đã được xác nhận thanh toán, THE SYSTEM SHALL gán trạng thái `PAID`.

KHI hóa đơn chưa thanh toán và đã vượt quá hạn thanh toán, THE SYSTEM SHALL gán trạng thái `OVERDUE`.

## 3.8 Phân quyền

KHI người dùng có vai trò `Management Board`, THE SYSTEM SHALL cho phép truy cập chức năng quản lý hóa đơn.

KHI người dùng chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401.

KHI người dùng không có vai trò `Management Board`, THE SYSTEM SHALL trả về HTTP 403.

# 4. Servlet Contract

## 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `InvoiceServlet` (danh sách và tạo) và `InvoiceDetailServlet` (chi tiết và chỉnh sửa) |
| **URL Pattern** | `GET /manager/invoices` — danh sách hóa đơn |
| **URL Pattern** | `GET /manager/invoices?action=create` — form tạo hóa đơn |
| **URL Pattern** | `POST /manager/invoices?action=create` — lưu tạo hóa đơn mới |
| **URL Pattern** | `GET /manager/invoices/{id}` — chi tiết hóa đơn |
| **URL Pattern** | `GET /manager/invoices/{id}/edit` — form chỉnh sửa hóa đơn |
| **URL Pattern** | `POST /manager/invoices/{id}/edit` — lưu cập nhật hóa đơn |
| **URL Pattern** | `POST /manager/invoices/{id}/update-status` — cập nhật trạng thái hóa đơn |
| **URL Pattern** | `POST /manager/invoices/{id}/delete` — xóa hóa đơn (sẽ giải phóng chỉ số điện nước nếu có) |
| **Phân quyền** | Role = `MANAGER` hoặc `ADMIN` |

---

## 4.2 Request Attributes — Danh sách (list.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `invoices` | `List<InvoiceListItemDTO>` | `InvoiceService.getInvoices(...)` | Danh sách hóa đơn |
| `currentPage`, `totalPages` | `int` | Xử lý phân trang | Phục vụ điều hướng trang |
| `keyword`, `status`, `billingPeriod` | `String` | Query params | Giữ lại giá trị bộ lọc |

---

## 4.3 Request Attributes — Chi tiết (detail.jsp) & Form chỉnh sửa (edit.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `invoice` | `InvoiceDetailDTO` | `InvoiceService.getInvoiceDetail(...)` | Dữ liệu chi tiết hóa đơn, gồm tính toán phí, ảnh minh chứng và thông tin người thuê |
| `errorMessage` | `String` | Lỗi exception (nếu có) | Báo lỗi khi cập nhật hoặc lỗi trạng thái |

---

## 4.4 Request Attributes — Form tạo (create.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `errorMessage` | `String` | Lỗi validation | Báo lỗi khi tạo thất bại |

---

## 4.5 Validation — POST Tạo Hóa Đơn (`/manager/invoices?action=create`)

| Form param | Điều kiện hợp lệ | Lỗi trả về (`errorMessage`) |
| --- | --- | --- |
| `roomCode` | Không rỗng, phòng tồn tại | Phòng không hợp lệ |
| `billingPeriod` | Format hợp lệ | Yêu cầu định dạng kỳ hạn |
| `taxRate`, `otherFee` | Số thực lớn hơn hoặc bằng 0 | Không đúng định dạng số lượng/phí |
| Logic | Phòng chưa có Hóa đơn trong kỳ | `IllegalArgumentException` (Phòng đã có hóa đơn trong kỳ này) |
| Liên kết | Lấy tự động chỉ số điện/nước cũ/mới, giá dịch vụ | Báo lỗi nếu chưa nhập điện/nước cho kỳ |

---

## 4.6 Validation — POST Chỉnh sửa Hóa Đơn (`/manager/invoices/{id}/edit`)

| Form param | Điều kiện hợp lệ | Lỗi trả về (`errorMessage`) |
| --- | --- | --- |
| `dueDate` | Không rỗng, date hợp lệ | Ngày hết hạn không hợp lệ |
| `taxRate`, `otherFee` | Số hợp lệ | Không thể parse số tiền / phí |
| Trạng thái | Chỉ cho phép khi hóa đơn chưa thanh toán hoàn tất | Báo lỗi nếu cố cập nhật lúc đã `PAID` |

---

## 4.7 Xử lý lỗi (Servlet Behavior)

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Role không hợp lệ | `403 Access Denied` |
| `id` rỗng/sai format | Trả về HTTP 400 Bad Request |
| Cập nhật, tạo mới thất bại | Bắt `IllegalArgumentException` và Forward lại view JSP kèm `errorMessage` |
| Xóa, thay đổi trạng thái thất bại | Gán `errorMessage` vào Session (hoặc SetFlashMessage) và Redirect |

---

# 5. Technical Constraints

- Chỉ người dùng có vai trò `MANAGER` hoặc `ADMIN` mới được phép truy cập các chức năng quản lý hóa đơn.
- Giao dịch tạo hóa đơn (tạo `invoice` và lấy snapshot giá từ `facilities`) phải được thực hiện trong một Database Transaction duy nhất để đảm bảo tính toàn vẹn dữ liệu.
- Giá tiền của các hóa đơn cũ (snapshot) **phải được giữ nguyên** và không được tự động cập nhật khi quản lý thay đổi đơn giá trong bảng `facilities` hoặc bảng cấu hình giá phòng.
- Logic in ấn và xuất PDF được xử lý hoàn toàn ở frontend (`window.print()` kết hợp CSS `@media print`), không dùng thư viện backend (tránh quá tải server).
- Thời gian phản hồi khi tạo hoặc tải chi tiết hóa đơn không vượt quá **500 ms (p95)**.

---

# 6. Out Of Scope

- Hóa đơn điện tử theo chuẩn thuế.
- Chữ ký số.
- Thanh toán trực tiếp từ hóa đơn.
- Hủy hóa đơn đã thanh toán.
- Gửi email hóa đơn tự động.
- Báo cáo doanh thu.
- Tự động cập nhật lại hóa đơn đã tạo khi đơn giá trong bảng `facilities` thay đổi.