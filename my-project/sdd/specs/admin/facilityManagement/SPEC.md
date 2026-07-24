# Feature: Quản lý Cơ sở

**Status:** Draft
**Reviewer:** Chưa duyệt
**Date:** 2026-06-10
**Priority:** High

---

## 1. Business Context

Hệ thống quản lý nhà trọ cần quản lý nhiều cơ sở khác nhau. Mỗi cơ sở có thể là một nhà trọ, một khu nhà trọ hoặc một cơ sở cho thuê.

Feature Quản lý Cơ sở giúp Admin khai báo thông tin nền của từng cơ sở, bao gồm:

* Mã cơ sở
* Tên cơ sở
* Địa chỉ
* Số tầng tối đa
* Số phòng tối đa mỗi tầng

Các thông tin này sẽ được hệ thống sử dụng để tự động sinh danh sách phòng khi cơ sở được kích hoạt.

Mã cơ sở do Admin nhập khi tạo cơ sở. Sau khi lưu thành công, hệ thống sẽ tự động chuyển mã cơ sở sang chữ in hoa để thống nhất dữ liệu.

Khi cơ sở được chuyển sang trạng thái `ACTIVE`, hệ thống sẽ tự động sinh toàn bộ danh sách phòng dựa trên:

* Mã cơ sở
* Số tầng tối đa
* Số phòng tối đa mỗi tầng

Mã phòng được sinh theo cấu trúc:

```text
[Mã cơ sở][Tầng 2 chữ số][Số phòng 2 chữ số]
```

Ví dụ:

```text
facilityCode = HL
floor = 1
roomNumber = 1
roomCode = HL0101

facilityCode = HL
floor = 5
roomNumber = 4
roomCode = HL0504

facilityCode = MD
floor = 12
roomNumber = 15
roomCode = MD1215
```

Feature này giúp:

* Giảm lỗi nhập liệu
* Tránh tạo phòng sai mã
* Tránh tạo trùng phòng
* Tránh tạo phòng vượt quá số tầng hoặc số phòng cho phép

Đồng thời đây là dữ liệu nền cho:

* Quản lý Phòng
* Quản lý Người thuê

Mỗi cơ sở có thể được gán cho một nhân sự quản lý phụ trách.

Nhân sự quản lý là người chịu trách nhiệm vận hành cơ sở và được sử dụng để phân công, theo dõi và báo cáo trong hệ thống.

---

## 2. User Stories

### Story 1: Tạo cơ sở mới

Là Admin, tôi muốn tạo mới cơ sở với mã cơ sở, tên cơ sở, địa chỉ, số tầng tối đa và số phòng tối đa mỗi tầng để hệ thống có thể lưu thông tin của từng nhà trọ.

Ví dụ:

```text
Mã cơ sở: HL
Tên cơ sở: Nhà trọ Hòa Lạc
Địa chỉ: Xã Hòa Lạc
Số tầng tối đa: 5
Số phòng tối đa mỗi tầng: 4
```

### Story 2: Tạo cơ sở ở trạng thái nháp

Là Admin, tôi muốn cơ sở mới tạo có trạng thái `DRAFT` để tôi có thể kiểm tra và chỉnh sửa thông tin trước khi đưa vào sử dụng chính thức.

### Story 3: Chỉnh sửa cơ sở nháp

Là Admin, khi cơ sở còn ở trạng thái `DRAFT`, tôi muốn chỉnh sửa:

* Mã cơ sở
* Tên cơ sở
* Địa chỉ
* Số tầng tối đa
* Số phòng tối đa mỗi tầng

để sửa các thông tin nhập sai.

### Story 4: Kích hoạt cơ sở

Là Admin, tôi muốn kích hoạt cơ sở từ `DRAFT` sang `ACTIVE` để cơ sở đó có thể được sử dụng trong hệ thống.

Khi cơ sở được kích hoạt thành công, hệ thống sẽ tự động sinh danh sách phòng tương ứng với cấu hình của cơ sở.

### Story 5: Khóa cấu hình sau khi ACTIVE

Là Admin, khi cơ sở đã `ACTIVE`, tôi muốn hệ thống khóa:

* Mã cơ sở
* Địa chỉ
* Số tầng tối đa
* Số phòng tối đa mỗi tầng

để tránh làm sai lệch dữ liệu phòng sau này.

### Story 6: Vô hiệu hóa cơ sở

Là Admin, khi cơ sở không còn sử dụng hoặc bị nhập sai cấu hình sau khi ACTIVE, tôi muốn chuyển cơ sở sang `INACTIVE` để hệ thống không cho tạo thêm dữ liệu mới thuộc cơ sở đó.

### Story 7: Liên kết với Quản lý Phòng

Là Admin, khi vào feature Quản lý Phòng, tôi muốn chọn cơ sở đang `ACTIVE` và xem danh sách phòng đã được hệ thống sinh tự động để không cần nhập mã phòng thủ công.


---

## 3. Acceptance Criteria (EARS)

### 3.1 Tạo cơ sở

* KHI Admin gửi form tạo cơ sở với dữ liệu hợp lệ gồm mã cơ sở, tên cơ sở, địa chỉ, số tầng tối đa và số phòng tối đa mỗi tầng, THE SYSTEM SHALL tạo mới cơ sở và đặt trạng thái mặc định là `DRAFT`.
* KHI Admin nhập mã cơ sở bằng chữ thường, THE SYSTEM SHALL tự động chuyển mã cơ sở sang chữ in hoa khi lưu.
* KHI Admin tạo cơ sở thành công, THE SYSTEM SHALL hiển thị mã cơ sở ở dạng chữ in hoa.
* KHI Admin bỏ trống mã cơ sở, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_CODE_REQUIRED`.
* KHI Admin bỏ trống tên cơ sở, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_NAME_REQUIRED`.
* KHI Admin bỏ trống số tầng tối đa, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_FLOORS_REQUIRED`.
* KHI Admin bỏ trống số phòng tối đa mỗi tầng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_ROOMS_REQUIRED`.
* KHI Admin nhập mã cơ sở đã tồn tại, THE SYSTEM SHALL trả về HTTP 409 với mã lỗi `FACILITY_CODE_ALREADY_EXISTS`.
* KHI Admin nhập số tầng tối đa nhỏ hơn 1 hoặc lớn hơn 99, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_FLOORS_OUT_OF_RANGE`.
* KHI Admin nhập số phòng tối đa mỗi tầng nhỏ hơn 1 hoặc lớn hơn 99, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_ROOMS_OUT_OF_RANGE`.
* KHI Admin nhập mã cơ sở chứa số, khoảng trắng hoặc ký tự đặc biệt, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_CODE_INVALID_FORMAT`.
* KHI Admin nhập tên cơ sở chỉ gồm khoảng trắng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_NAME_BLANK`.
* KHI Admin bỏ trống địa chỉ, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `ADDRESS_REQUIRED`.
* KHI Admin nhập địa chỉ chỉ gồm khoảng trắng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `ADDRESS_BLANK`.
* KHI Admin nhập mã cơ sở có ít hơn 2 hoặc nhiều hơn 10 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_CODE_LENGTH_INVALID`.
* KHI Admin nhập tên cơ sở dài hơn 255 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_NAME_TOO_LONG`.
* KHI Admin nhập địa chỉ dài hơn 500 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `ADDRESS_TOO_LONG`.
---

### 3.2 Xem danh sách cơ sở

* KHI Admin truy cập màn hình Quản lý Cơ sở, THE SYSTEM SHALL hiển thị danh sách cơ sở.
* KHI danh sách cơ sở được hiển thị, THE SYSTEM SHALL hiển thị:

  * Mã cơ sở
  * Tên cơ sở
  * Địa chỉ
  * Số tầng tối đa
  * Số phòng tối đa mỗi tầng
  * Tổng số phòng
  * Nhân sự quản lý
  * Trạng thái
* KHI Admin tìm kiếm theo mã cơ sở, tên cơ sở hoặc địa chỉ, THE SYSTEM SHALL hiển thị các cơ sở phù hợp.
* KHI Admin lọc theo trạng thái DRAFT, ACTIVE hoặc INACTIVE, THE SYSTEM SHALL hiển thị các cơ sở đúng với trạng thái đã chọn.
* KHI không có dữ liệu cơ sở, THE SYSTEM SHALL hiển thị thông báo không có cơ sở nào.
* KHI Admin vô hiệu hóa cơ sở đã có phòng, người thuê hoặc hợp đồng, THE SYSTEM SHALL không xóa dữ liệu liên quan và vẫn cho phép xem lịch sử.
* TRONG KHI cơ sở ở trạng thái INACTIVE, THE SYSTEM SHALL không cho tạo phòng mới, không cho tạo hợp đồng mới thuộc cơ sở đó, nhưng vẫn cho phép xem dữ liệu lịch sử.

---

### 3.3 Xem chi tiết cơ sở

* KHI Admin xem chi tiết một cơ sở, THE SYSTEM SHALL hiển thị:

  * Mã cơ sở
  * Tên cơ sở
  * Địa chỉ
  * Số tầng tối đa
  * Số phòng tối đa mỗi tầng
  * Tổng số phòng
  * Trạng thái
  * Ngày tạo
  * Ngày cập nhật

* KHI cơ sở ở trạng thái DRAFT, THE SYSTEM SHALL hiển thị thông tin rằng cơ sở này chưa được kích hoạt và chưa sinh danh sách phòng.

* KHI cơ sở ở trạng thái ACTIVE, THE SYSTEM SHALL hiển thị thông tin rằng cơ sở này đang được sử dụng và đã sinh danh sách phòng tự động.

* KHI cơ sở ở trạng thái INACTIVE, THE SYSTEM SHALL hiển thị thông tin rằng cơ sở này đã bị vô hiệu hóa và không thể tạo dữ liệu mới.

* KHI cơ sở không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `FACILITY_NOT_FOUND`.

* Họ tên quản lý

* KHI cơ sở đã được gán quản lý,
THE SYSTEM SHALL hiển thị thông tin quản lý hiện tại.

* KHI cơ sở chưa được gán quản lý,
THE SYSTEM SHALL hiển thị trạng thái "Chưa phân công quản lý".
---

### 3.4 Cập nhật cơ sở DRAFT

* TRONG KHI cơ sở ở trạng thái DRAFT, THE SYSTEM SHALL cho phép Admin chỉnh sửa:

  * Mã cơ sở
  * Tên cơ sở
  * Địa chỉ
  * Số tầng tối đa
  * Số phòng tối đa mỗi tầng

* KHI Admin cập nhật cơ sở DRAFT với dữ liệu hợp lệ, THE SYSTEM SHALL lưu thông tin mới và cập nhật thời gian chỉnh sửa.

* KHI Admin cập nhật mã cơ sở thành một mã đã tồn tại, THE SYSTEM SHALL trả về HTTP 409 với mã lỗi `FACILITY_CODE_ALREADY_EXISTS`.
* KHI Admin nhập tên cơ sở chỉ gồm khoảng trắng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_NAME_BLANK`.
* KHI Admin cập nhật mã cơ sở bằng chữ thường, THE SYSTEM SHALL tự động chuyển sang chữ in hoa khi lưu.

* KHI cơ sở còn ở trạng thái DRAFT, THE SYSTEM SHALL chưa sinh danh sách phòng.
* KHI Admin cập nhật mã cơ sở có ít hơn 2 hoặc nhiều hơn 10 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_CODE_LENGTH_INVALID`.
* KHI Admin cập nhật tên cơ sở dài hơn 255 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_NAME_TOO_LONG`.
* KHI Admin cập nhật địa chỉ dài hơn 500 ký tự, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `ADDRESS_TOO_LONG`.
---

### 3.5 Cập nhật cơ sở ACTIVE

* TRONG KHI cơ sở ở trạng thái ACTIVE, THE SYSTEM SHALL chỉ cho phép Admin chỉnh sửa tên cơ sở.

* TRONG KHI cơ sở ở trạng thái ACTIVE, THE SYSTEM SHALL không cho phép chỉnh sửa:

  * Mã cơ sở
  * Địa chỉ
  * Số tầng tối đa
  * Số phòng tối đa mỗi tầng

* KHI Admin gửi yêu cầu chỉnh sửa mã cơ sở, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_CODE_IMMUTABLE`.

* KHI Admin gửi yêu cầu chỉnh sửa số tầng tối đa, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_FLOORS_IMMUTABLE`.

* KHI Admin gửi yêu cầu chỉnh sửa số phòng tối đa mỗi tầng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `MAX_ROOMS_IMMUTABLE`.

* KHI Admin gửi yêu cầu chỉnh sửa địa chỉ, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `ADDRESS_IMMUTABLE`.

* KHI cơ sở đã ACTIVE, THE SYSTEM SHALL không cho phép thay đổi cấu hình phòng đã sinh.

* KHI Admin nhập sai cấu hình nhưng cơ sở đã ACTIVE, THE SYSTEM SHALL yêu cầu vô hiệu hóa cơ sở cũ và tạo cơ sở mới.

---

### 3.6 Kích hoạt cơ sở

* KHI Admin kích hoạt cơ sở DRAFT với dữ liệu hợp lệ, THE SYSTEM SHALL chuyển trạng thái từ DRAFT sang ACTIVE.
* KHI kích hoạt thành công, THE SYSTEM SHALL khóa:

  * Mã cơ sở
  * Địa chỉ
  * Số tầng tối đa
  * Số phòng tối đa mỗi tầng
* KHI kích hoạt thành công, THE SYSTEM SHALL tự động sinh danh sách phòng.
* KHI Admin cố gắng kích hoạt cơ sở không ở trạng thái DRAFT, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_INVALID_STATUS_FOR_ACTIVATION`.
* KHI cơ sở không có cấu hình hợp lệ để sinh phòng, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_INVALID_CONFIGURATION`.
* KHI quá trình sinh phòng thất bại, THE SYSTEM SHALL không chuyển trạng thái ACTIVE và trả về HTTP 500 với mã lỗi `ROOM_GENERATION_FAILED`.
* KHI kích hoạt thành công, THE SYSTEM SHALL đảm bảo chuyển trạng thái và sinh phòng trong cùng một transaction.

---

### 3.7 Quy tắc sinh phòng tự động

```text
roomCode = facilityCode + format(floor, "00") + format(roomNumber, "00")
```

Ví dụ:

```text
HL0101
HL0504
MD1215
```

Danh sách phòng mẫu:

```text
HL0101 HL0102 HL0103 HL0104
HL0201 HL0202 HL0203 HL0204
HL0301 HL0302 HL0303 HL0304
HL0401 HL0402 HL0403 HL0404
HL0501 HL0502 HL0503 HL0504
```

totalRooms là số phòng đã được sinh thực tế.

DRAFT => totalRooms = 0
ACTIVE/INACTIVE => totalRooms = maxFloors × maxRoomsPerFloor

Ví dụ:

```text
5 * 4 = 20 phòng
```

* KHI hệ thống sinh phòng, THE SYSTEM SHALL gán trạng thái mặc định là `AVAILABLE`.
* KHI hệ thống sinh phòng, THE SYSTEM SHALL đảm bảo mỗi roomCode là duy nhất.
* KHI phát hiện roomCode bị trùng, THE SYSTEM SHALL dừng kích hoạt và trả về lỗi `ROOM_CODE_DUPLICATED`.

---

### 3.8 Vô hiệu hóa cơ sở

* KHI Admin vô hiệu hóa cơ sở ACTIVE, THE SYSTEM SHALL chuyển trạng thái từ ACTIVE sang INACTIVE.
* TRONG KHI cơ sở ở trạng thái INACTIVE, THE SYSTEM SHALL không cho phép tạo dữ liệu mới.
* TRONG KHI cơ sở ở trạng thái INACTIVE, THE SYSTEM SHALL vẫn cho phép xem dữ liệu lịch sử.
* TRONG KHI cơ sở ở trạng thái INACTIVE, THE SYSTEM SHALL giữ nguyên danh sách phòng đã sinh.
* KHI Admin vô hiệu hóa cơ sở đã có dữ liệu liên quan, THE SYSTEM SHALL giữ nguyên toàn bộ dữ liệu lịch sử.
* KHI cơ sở đã chuyển sang INACTIVE, THE SYSTEM SHALL không cho kích hoạt lại trong phiên bản hiện tại.
* KHI Admin cố gắng vô hiệu hóa cơ sở không ở trạng thái ACTIVE, THE SYSTEM SHALL trả về HTTP 400 với mã lỗi `FACILITY_INVALID_STATUS_FOR_DEACTIVATION`.

---

### 3.9 Liên kết với Quản lý Phòng

* KHI feature Quản lý Phòng yêu cầu danh sách cơ sở, THE SYSTEM SHALL chỉ trả về các cơ sở ACTIVE.
* KHI cơ sở ở trạng thái DRAFT hoặc INACTIVE, THE SYSTEM SHALL không hiển thị trong danh sách chọn.
* KHI Admin chọn cơ sở ACTIVE, THE SYSTEM SHALL hiển thị danh sách phòng đã sinh tự động.
* KHI danh sách phòng được hiển thị, THE SYSTEM SHALL hiển thị mã phòng theo format chuẩn.
* KHI Admin quản lý phòng của cơ sở ACTIVE, THE SYSTEM SHALL không yêu cầu nhập mã phòng thủ công.
* KHI Admin cần chọn phòng trong các chức năng liên quan, THE SYSTEM SHALL hiển thị dropdown hoặc danh sách phòng có sẵn.

---

## 4. Servlet Contract

### 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `AdminFacilityServlet` |
| **URL Pattern** | `GET/POST /admin/facilities` — danh sách / tạo mới |
| **URL Pattern** | `GET /admin/facilities/create` — form tạo |
| **URL Pattern** | `GET /admin/facilities/{id}` — chi tiết |
| **URL Pattern** | `GET /admin/facilities/{id}/edit` — form chỉnh sửa |
| **URL Pattern** | `POST /admin/facilities/{id}/edit` — lưu chỉnh sửa |
| **URL Pattern** | `POST /admin/facilities/{id}/activate` — kích hoạt |
| **URL Pattern** | `POST /admin/facilities/{id}/deactivate` — vô hiệu hóa |
| **URL Pattern** | `POST /admin/facilities/{id}/rooms/{roomId}/area` — cập nhật diện tích phòng |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `BaseServlet`) |

---

### 4.2 Request Attributes — Danh sách (list.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `page` | `PageDTO<Facility>` | `FacilityDAO.findAll(keyword, status, page, 10)` | Dữ liệu phân trang, `PAGE_SIZE = 10` |
| `keyword` | `String` | Query param `keyword` | Giữ lại giá trị tìm kiếm trên form |
| `selectedStatus` | `String` | Query param `status` | Giữ lại filter trạng thái (`DRAFT`, `ACTIVE`, `INACTIVE`) |

---

### 4.3 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `facility` | `Facility` | `FacilityDAO.findById(id)` | Thông tin đầy đủ cơ sở |
| `rooms` | `List<Room>` | `FacilityDAO.findRoomsByFacilityId(id)` | Chỉ set khi `status != DRAFT` |

---

### 4.4 Request Attributes — Form tạo/sửa (create.jsp / edit.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `facility` | `Facility` | `FacilityDAO.findById(id)` | Chỉ có ở form edit |
| `errorMessage` | `String` | `ValidationException.getMessage()` | Thông báo lỗi khi submit thất bại |

---

### 4.5 Validation — POST /admin/facilities/create

| Field (form param) | Điều kiện hợp lệ | Lỗi ném ra |
|---|---|---|
| `code` | Không rỗng, khớp `[A-Za-z]{2,10}` | `ValidationException` |
| `code` | Chưa tồn tại trong DB | `ValidationException` |
| `name` | Không rỗng | `ValidationException` |
| `address` | Không rỗng | `ValidationException` |
| `floorCount` | Số nguyên, `1–99` | `ValidationException` |
| `roomsPerFloor` | Số nguyên, `1–99` | `ValidationException` |
| Mã code | Tự động `toUpperCase()` trước khi lưu | — |

---

### 4.6 Trạng thái cơ sở và hành vi

| Trạng thái | Cho phép sửa code/địa chỉ/tầng/phòng | Cho phép sửa tên | Sinh phòng | Kích hoạt | Vô hiệu hóa |
|---|---|---|---|---|---|
| `DRAFT` | ✅ | ✅ | ❌ | ✅ | ❌ |
| `ACTIVE` | ❌ | ✅ | ✅ (đã sinh) | ❌ | ✅ |
| `INACTIVE` | ❌ | ❌ | ✅ (đã sinh) | ❌ | ❌ |

**Activate:** transaction gồm `updateStatus(ACTIVE)` + `generateRooms()` — rollback nếu lỗi.

**Deactivate:** chặn nếu còn phòng đang thuê (`countOccupiedRooms > 0`).

---

### 4.7 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| `{id}` không tồn tại | `NotFoundException` → HTTP 404 |
| Validation thất bại (POST) | Forward về `create.jsp` / `edit.jsp` với `errorMessage` |
| Lỗi không xác định | Flash message `error`, redirect về `/admin/facilities` |

---


## 5. Technical Constraints

* Servlet tạo/cập nhật/kích hoạt/vô hiệu hóa: ≤ 500ms p95
* Servlet danh sách có phân trang: ≤ 1 giây p95
* Rate limit: 100 request/phút/người dùng
* Chỉ Admin được quản lý cơ sở
* Backend phải validate toàn bộ business rule
* Facility Code là duy nhất toàn hệ thống
* Facility Code: 2–10 ký tự
* Facility Code chỉ gồm các chữ cái A–Z
* Facility Name: tối đa 255 ký tự
* Address: tối đa 500 ký tự
* Max Floors: 1 → 99
* Max Rooms Per Floor: 1 → 99

Luồng trạng thái:

```text
DRAFT → ACTIVE → INACTIVE
```

Quy tắc sinh phòng:

```text
[Mã cơ sở][Tầng 2 chữ số][Số phòng 2 chữ số]
```

Ví dụ:

```text
HL0101
HL0504
MD1215
```

---

## 6. Out of Scope

* Quản lý số người tối đa trong phòng
* Trạng thái chi tiết của phòng
* Liên kết phòng với người thuê
* Tạo hợp đồng thuê
* Xóa cơ sở ACTIVE hoặc INACTIVE
* Kích hoạt lại cơ sở INACTIVE
* Audit Log
* Chỉnh sửa lại số tầng hoặc số phòng sau khi ACTIVE
* Xóa riêng từng phòng được sinh tự động
* Thêm phòng thủ công vượt cấu hình
