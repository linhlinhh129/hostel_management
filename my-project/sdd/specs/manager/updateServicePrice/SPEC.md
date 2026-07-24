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
Là Ban quản lý, tôi muốn xem danh sách các khoản phí và giá dịch vụ hiện tại của cơ sở mà tôi phụ trách để biết hệ thống đang áp dụng mức giá nào khi tính hóa đơn.

## Story 2: Mở pop-up thay đổi giá
Là Ban quản lý, tôi muốn chọn nút thay đổi tại một khoản phí/dịch vụ để mở pop-up cập nhật giá mới cho khoản phí/dịch vụ đó.

## Story 3: Cập nhật giá điện
Là Ban quản lý, tôi muốn cập nhật giá điện của cơ sở để hệ thống sử dụng giá điện mới khi tạo hóa đơn sau thời điểm cập nhật.

## Story 4: Cập nhật giá nước
Là Ban quản lý, tôi muốn cập nhật giá nước của cơ sở để hệ thống sử dụng giá nước mới khi tạo hóa đơn sau thời điểm cập nhật.

## Story 5: Cập nhật phí dịch vụ
Là Ban quản lý, tôi muốn cập nhật phí dịch vụ của cơ sở để hệ thống sử dụng phí dịch vụ mới khi tạo hóa đơn sau thời điểm cập nhật.

## Story 6: Kiểm tra dữ liệu không hợp lệ
Là Ban quản lý, khi nhập giá không hợp lệ, tôi muốn hệ thống từ chối thao tác và hiển thị thông báo lỗi phù hợp.

## Story 7: Theo dõi lịch sử thay đổi
Là Ban quản lý, tôi muốn hệ thống lưu lại giá cũ, giá mới, người thực hiện và thời gian thay đổi để dễ dàng kiểm tra khi cần.

## Story 8: Kiểm tra quyền truy cập
Là hệ thống, khi người dùng không có quyền truy cập chức năng này, tôi muốn từ chối truy cập để đảm bảo dữ liệu giá không bị thay đổi sai quyền.

# 3. Acceptance Criteria (EARS)
## AC-01: Hiển thị danh sách khoản phí và giá dịch vụ hiện tại

KHI Ban quản lý truy cập màn hình Quản lý khoản phí và giá dịch vụ, THE SYSTEM SHALL hiển thị danh sách các khoản phí và giá hiện tại của cơ sở mà Ban quản lý đó phụ trách.

KHI danh sách được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:
- Loại phí (Tên khoản phí/dịch vụ)
- Giá hiện tại
- Đơn vị tính
- Thời gian cập nhật gần nhất (Cập nhật lần cuối)
- Nút "Cập nhật"
- Nút "Lịch sử"

Ví dụ danh sách hiển thị:
| Loại phí | Giá hiện tại | Đơn vị | Cập nhật lần cuối | Thao tác |
| --- | ---: | --- | --- | --- |
| Giá điện | 3,500 | VNĐ/kWh | 13/07/2026 10:00 | [Cập nhật] [Lịch sử] |
| Giá nước | 15,000 | VNĐ/m³ | 10/06/2026 15:30 | [Cập nhật] [Lịch sử] |
| Phí dịch vụ | 100,000 | VNĐ/tháng | 01/01/2026 08:00 | [Cập nhật] [Lịch sử] |

KHI cơ sở không có dữ liệu giá, THE SYSTEM SHALL hiển thị thông báo:
```text
Hiện tại chưa có dữ liệu phí và giá dịch vụ.
```

---

## AC-02: Chỉ hiển thị dữ liệu của cơ sở mà Ban quản lý phụ trách

KHI Ban quản lý truy cập màn hình Quản lý khoản phí và giá dịch vụ, THE SYSTEM SHALL chỉ hiển thị dữ liệu giá của cơ sở mà Ban quản lý đó phụ trách.

KHI Ban quản lý không phụ trách cơ sở nào, THE SYSTEM SHALL hiển thị thông báo:
```text
Bạn chưa được phân quyền quản lý cơ sở nào.
```

KHI Ban quản lý cố truy cập dữ liệu của cơ sở không thuộc quyền quản lý, THE SYSTEM SHALL từ chối truy cập và trả về HTTP 403 với mã lỗi `FACILITY_ACCESS_DENIED`.

---

## AC-03: Mở pop-up thay đổi giá

KHI Ban quản lý chọn nút Thay đổi tại một khoản phí/dịch vụ, THE SYSTEM SHALL mở pop-up cập nhật giá.

KHI pop-up cập nhật giá được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:
- Loại phí (Tên khoản phí/dịch vụ)
- Giá hiện tại và Đơn vị tính
- Ô nhập Giá mới và Đơn vị tính
- Ghi chú / Lý do thay đổi
- Nút Lưu thay đổi
- Nút Hủy

KHI pop-up được mở, THE SYSTEM SHALL không cho phép chỉnh sửa trực tiếp tên khoản phí/dịch vụ và loại khoản phí/dịch vụ.

KHI Ban quản lý chọn Hủy, THE SYSTEM SHALL đóng pop-up và không thay đổi dữ liệu.

## AC-04: Cập nhật giá thành công
KHI Ban quản lý nhập giá mới hợp lệ và chọn Lưu thay đổi, THE SYSTEM SHALL cập nhật giá mới vào bảng `facilities`.

KHI cập nhật thành công, THE SYSTEM SHALL đóng pop-up.

KHI cập nhật thành công, THE SYSTEM SHALL hiển thị thông báo:
```text
Cập nhật giá thành công.
```

KHI cập nhật thành công, THE SYSTEM SHALL tải lại danh sách khoản phí và giá dịch vụ để hiển thị giá mới nhất.

KHI cập nhật thành công, THE SYSTEM SHALL lưu thông tin:
- Giá cũ
- Giá mới
- Loại giá được cập nhật
- Ghi chú thay đổi
- Thời gian cập nhật
- Người cập nhật

## AC-05: Cập nhật giá điện
KHI Ban quản lý cập nhật giá của loại `ELECTRICITY`, THE SYSTEM SHALL cập nhật giá điện của cơ sở trong bảng `facilities`.

KHI giá điện được cập nhật thành công, THE SYSTEM SHALL sử dụng giá điện mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật giá điện, THE SYSTEM SHALL không tự động thay đổi lại tiền điện của hóa đơn cũ.

## AC-06: Cập nhật giá nước
KHI Ban quản lý cập nhật giá của loại `WATER`, THE SYSTEM SHALL cập nhật giá nước của cơ sở trong bảng `facilities`.

KHI giá nước được cập nhật thành công, THE SYSTEM SHALL sử dụng giá nước mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật giá nước, THE SYSTEM SHALL không tự động thay đổi lại tiền nước của hóa đơn cũ.

## AC-07: Cập nhật phí dịch vụ
KHI Ban quản lý cập nhật giá của loại `SERVICE_FEE`, THE SYSTEM SHALL cập nhật phí dịch vụ của cơ sở trong bảng `facilities`.

KHI phí dịch vụ được cập nhật thành công, THE SYSTEM SHALL sử dụng phí dịch vụ mới cho các hóa đơn được tạo sau thời điểm cập nhật.

KHI hóa đơn đã được tạo trước thời điểm cập nhật phí dịch vụ, THE SYSTEM SHALL không tự động thay đổi lại phí dịch vụ của hóa đơn cũ.

## AC-08: Giá không hợp lệ
KHI Ban quản lý nhập giá nhỏ hơn hoặc bằng 0, THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE`.

KHI Ban quản lý nhập giá không phải là số, THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE_FORMAT`.

KHI Ban quản lý bỏ trống giá mới, THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `REQUIRED_FIELD_MISSING`.

## AC-09: Loại giá không hợp lệ
KHI Ban quản lý cập nhật loại giá không tồn tại trong hệ thống, THE SYSTEM SHALL từ chối yêu cầu và trả về HTTP 400 với mã lỗi `INVALID_PRICE_TYPE`.

Các loại giá hợp lệ bao gồm:
- `ELECTRICITY`
- `WATER`
- `SERVICE_FEE`

## AC-10: Cơ sở không tồn tại
KHI cơ sở không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `FACILITY_NOT_FOUND`.

## AC-11: Không đủ quyền truy cập
KHI người dùng chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`.

KHI người dùng đã đăng nhập nhưng không có vai trò Ban quản lý, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

KHI người dùng có vai trò Ban quản lý nhưng không phụ trách cơ sở được yêu cầu, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FACILITY_ACCESS_DENIED`.

## AC-12: Lưu lịch sử thay đổi
KHI khoản phí hoặc giá dịch vụ được cập nhật thành công, THE SYSTEM SHALL lưu lịch sử thay đổi.

Thông tin lịch sử thay đổi lưu trong DB bao gồm:
- Loại giá được thay đổi
- Giá cũ
- Giá mới
- Ghi chú thay đổi
- Người thực hiện
- Thời gian thực hiện

## AC-12.1: Xem lịch sử thay đổi
KHI Ban quản lý bấm vào nút "Lịch sử" của một loại phí cụ thể, THE SYSTEM SHALL chuyển hướng sang trang Lịch sử thay đổi.

KHI màn hình Lịch sử thay đổi được hiển thị, THE SYSTEM SHALL hiển thị:
- Tiêu đề phụ: Tên loại phí đang xem
- Bảng lịch sử bao gồm: Ngày thay đổi, Giá cũ, Giá mới, Người thay đổi, Ghi chú.
- Nút "Quay lại danh sách".

## AC-13: Ngăn gửi trùng yêu cầu
WHILE hệ thống đang xử lý yêu cầu cập nhật giá, THE SYSTEM SHALL vô hiệu hóa nút Lưu thay đổi trên pop-up.

WHILE hệ thống đang xử lý yêu cầu cập nhật giá, THE SYSTEM SHALL không cho phép gửi nhiều yêu cầu liên tiếp từ cùng một phiên làm việc.

# 4. Servlet Contract
## 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `ServicePricePageServlet` |
| **URL Pattern** | `/manager/service-prices` |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua session `currentUser` và role `MANAGER`) |

---

## 4.2 Giao diện và Request Attributes (GET)

### Xem danh sách giá dịch vụ (index.jsp)
- **Endpoint:** `GET /manager/service-prices` (khi không truyền `action`)
- **Attribute:** `servicePrices` (Danh sách các giá dịch vụ hiện tại: Điện, Nước, Phí dịch vụ)
- **Chức năng:** Trả về màn hình chính liệt kê giá, có form ẩn/pop-up để bấm vào và cập nhật.

### Xem lịch sử thay đổi (history.jsp)
- **Endpoint:** `GET /manager/service-prices?action=history`
- **Query Params:**
  - `priceType` (bắt buộc): Vd: `ELECTRICITY`, `WATER`, `SERVICE_FEE`.
  - `page` (tùy chọn): Trang hiện tại.
- **Attributes:**
  - `historyList`: Danh sách lịch sử cập nhật giá (phân trang).
  - `priceType`: Giữ trạng thái hiển thị cho UI.
- **Chức năng:** Trả về màn hình danh sách các lần thay đổi giá của 1 loại dịch vụ cụ thể.

---

## 4.3 Xử lý cập nhật giá (POST)

### Cập nhật giá mới
- **Endpoint:** `POST /manager/service-prices?action=update`
- **Payload (Form Data):**
  - `priceType`: Loại giá cần thay đổi (vd: `ELECTRICITY`).
  - `newPrice`: Mức giá mới.
  - `note`: Ghi chú lý do thay đổi.

### Kết quả thành công
- Hệ thống gọi hàm `updatePrice()`, lưu DB và thực hiện chuyển hướng (`sendRedirect`) về lại trang danh sách `/manager/service-prices`.

### Kết quả thất bại
- Hệ thống gọi hàm `updatePrice()` thất bại hoặc vướng ngoại lệ (ví dụ `newPrice` không hợp lệ).
- Hệ thống thực hiện forward lại trang `index.jsp`, kèm theo 2 Request Attributes:
  - `errorMessage`: Chuỗi thông báo lỗi (ví dụ: `"Dữ liệu không hợp lệ."` hoặc `"Cập nhật thất bại..."`).
  - `servicePrices`: Danh sách giá cũ để vẽ lại UI như lúc chưa cập nhật.

---

## 4.4 Xử lý lỗi chung (Servlet Behavior)

| Tình huống | Mã HTTP | Hành vi |
|---|---|---|
| Chưa đăng nhập hoặc Role không phải MANAGER | 403 | Trả về lỗi bằng `sendError(SC_FORBIDDEN, "Access Denied")`. |
| Gọi POST với `action` không hợp lệ | 400 | Trả về lỗi bằng `sendError(SC_BAD_REQUEST)`. |
| Lỗi dữ liệu `newPrice` (NumberFormatException) | 200 (Forward)| Bắt Exception, đẩy biến `errorMessage` = `"Dữ liệu không hợp lệ."` và tải lại JSP. |

---

# 5. Technical Constraints

- **Phân quyền và Bảo mật:** 
  - Chỉ người dùng có vai trò Ban quản lý (`MANAGER`/`ADMIN`) mới được phép truy cập và thực hiện cập nhật giá.
  - Phân quyền dựa trên `Session` (`currentUser`), sử dụng chuẩn Rendering (Servlet/JSP).
- **Tính toàn vẹn dữ liệu (Transaction):**
  - Mọi thao tác đổi giá phải được lưu thành công vào bảng `facilities` và đồng thời sinh ra một bản ghi trong bảng lịch sử (vd: `service_price_histories`).
- **Giới hạn nghiệp vụ:**
  - Không cho phép cập nhật giá âm.
  - Không thay đổi tiền của các hóa đơn đã được tạo từ trước.
- **Hiệu năng:**
  - Thời gian xử lý load danh sách và lịch sử không vượt quá 1 giây (p95).
  - Tác vụ submit form cập nhật không vượt quá 500 ms (p95).

---

# 6. Out of Scope
- Quy trình phê duyệt thay đổi giá.
- Gửi thông báo tự động cho cư dân khi thay đổi giá.
- Import danh sách khoản phí từ Excel.
- Thiết lập giá có hiệu lực trong tương lai.
- Tích hợp với hệ thống kế toán hoặc thanh toán bên thứ ba.
- Tự động tính toán lại hóa đơn đã phát hành trước đó.
- Tạo mới loại khoản phí động nếu bảng `facilities` không có cột tương ứng.
- Xóa khoản phí hoặc giá dịch vụ khỏi hệ thống.
- Quản lý nhiều bảng giá theo từng thời điểm hiệu lực.