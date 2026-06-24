# Feature: Quản lý hợp đồng

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** \[YYYY-MM-DD\]\
**Priority:** High

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, hợp đồng thuê phòng là tài liệu dùng để ghi nhận thỏa thuận giữa Ban quản lý hoặc chủ cơ sở cho thuê và người thuê phòng.

Hợp đồng chứa các thông tin quan trọng như:

- Thông tin người thuê.

- Phòng được thuê.

- Ngày lập hợp đồng.

- Ngày bắt đầu hợp đồng.

- Ngày hết hạn hợp đồng.

- Tiền phòng.

- Tiền cọc.

- Các khoản phí áp dụng.

- Quy định thanh toán.

- Trách nhiệm của các bên.

Feature Quản lý hợp đồng cho phép Ban quản lý:

- Xem danh sách hợp đồng thuộc cơ sở mà mình phụ trách.

- Xem chi tiết thông tin hợp đồng.

- Tạo hợp đồng mới cho người thuê.

- In hợp đồng theo mẫu hợp đồng thuê phòng trọ.

- Quản lý trạng thái hợp đồng `ACTIVE` hoặc `INACTIVE`.

Khi lập hợp đồng, Ban quản lý nhập thông tin cá nhân của khách thuê, chọn phòng cần thuê, nhập số tiền bằng chữ, ngày lập hợp đồng và ngày hết hạn. Hệ thống tự động lấy thông tin phòng như mã phòng, tầng, tiền phòng và tiền cọc từ dữ liệu phòng/cơ sở để hiển thị và in trong nội dung hợp đồng.

Sau khi lưu thành công, hợp đồng được tạo với trạng thái mặc định là `ACTIVE`.

---

# 2. User Stories

## Story 1: Xem danh sách hợp đồng

Là Ban quản lý,\
tôi muốn xem danh sách các hợp đồng thuộc cơ sở mà tôi phụ trách\
để theo dõi tình trạng thuê phòng của khách thuê.

---

## Story 2: Xem chi tiết hợp đồng

Là Ban quản lý,\
tôi muốn xem chi tiết một hợp đồng\
để kiểm tra thông tin khách thuê, phòng thuê, thời hạn hợp đồng và trạng thái hợp đồng.

---

## Story 3: Tạo hợp đồng mới

Là Ban quản lý,\
tôi muốn tạo hợp đồng mới cho một khách thuê và một phòng cụ thể\
để ghi nhận việc khách thuê bắt đầu thuê phòng.

---

## Story 4: Tự động lấy thông tin phòng khi lập hợp đồng

Là Ban quản lý,\
khi tôi chọn phòng trong form tạo hợp đồng,\
tôi muốn hệ thống tự lấy mã phòng, tầng, tiền phòng và tiền cọc\
để giảm nhập liệu thủ công và tránh sai thông tin.

---

## Story 5: In hợp đồng

Là Ban quản lý,\
tôi muốn in hợp đồng theo mẫu hợp đồng thuê phòng trọ\
để lưu trữ hoặc đưa cho người thuê ký xác nhận.

---

## Story 6: Kiểm tra quyền truy cập

Là hệ thống,\
khi người dùng chưa đăng nhập hoặc không có quyền Ban quản lý,\
tôi muốn từ chối truy cập chức năng Quản lý hợp đồng\
để bảo vệ dữ liệu hợp đồng của cơ sở.

---

# 3. Acceptance Criteria (EARS)

## 3.1 Xem danh sách hợp đồng

KHI Ban quản lý truy cập màn hình Quản lý hợp đồng,\
THE SYSTEM SHALL hiển thị danh sách hợp đồng thuộc cơ sở mà Ban quản lý đó phụ trách.

KHI danh sách hợp đồng được hiển thị,\
THE SYSTEM SHALL hiển thị các thông tin sau:

- Mã hợp đồng

- Mã phòng

- Tên khách thuê

- Số CMND/CCCD

- Số điện thoại

- Ngày lập hợp đồng

- Ngày bắt đầu hợp đồng

- Ngày hết hạn hợp đồng

- Trạng thái hợp đồng

- Hành động xem chi tiết

- Hành động in hợp đồng

KHI Ban quản lý không phụ trách cơ sở nào,\
THE SYSTEM SHALL hiển thị thông báo:

```text
Bạn chưa được phân quyền quản lý cơ sở nào.
```

KHI không có hợp đồng nào trong cơ sở mà Ban quản lý phụ trách,\
THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại chưa có hợp đồng nào.
```

---

## 3.2 Phân quyền dữ liệu theo cơ sở

KHI Ban quản lý xem danh sách hợp đồng,\
THE SYSTEM SHALL chỉ truy xuất các hợp đồng có `room_id` thuộc phòng của cơ sở mà Ban quản lý đó phụ trách.

KHI Ban quản lý cố truy cập hợp đồng thuộc cơ sở khác,\
THE SYSTEM SHALL từ chối truy cập và trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

KHI hệ thống truy xuất hợp đồng,\
THE SYSTEM SHALL join bảng `contracts` với bảng `rooms` để xác định cơ sở của phòng.

---

## 3.3 Xem chi tiết hợp đồng

KHI Ban quản lý chọn một hợp đồng,\
THE SYSTEM SHALL hiển thị chi tiết hợp đồng.

KHI chi tiết hợp đồng được hiển thị,\
THE SYSTEM SHALL hiển thị các thông tin sau:

- `contract_id`

- Mã hợp đồng

- Mã phòng

- Tầng

- Tiền phòng

- Tiền cọc

- Tên khách thuê

- Ngày sinh khách thuê

- Hộ khẩu thường trú

- Số CMND/CCCD

- Ngày cấp CMND/CCCD

- Nơi cấp CMND/CCCD

- Số điện thoại

- Số tiền bằng chữ

- Ngày lập hợp đồng

- Ngày bắt đầu hợp đồng

- Ngày hết hạn hợp đồng

- Trạng thái hợp đồng

- Người tạo

- Ngày tạo

- Ngày cập nhật

KHI hợp đồng không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `CONTRACT_NOT_FOUND`.

KHI hợp đồng tồn tại nhưng không thuộc cơ sở mà Ban quản lý phụ trách,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

---

## 3.4 Tạo hợp đồng mới

KHI Ban quản lý truy cập màn hình Tạo hợp đồng,\
THE SYSTEM SHALL hiển thị form tạo hợp đồng.

KHI form tạo hợp đồng được hiển thị,\
THE SYSTEM SHALL cho phép Ban quản lý nhập các thông tin sau:

- Họ tên khách thuê

- Ngày sinh

- Nơi đăng ký hộ khẩu thường trú

- Số CMND/CCCD

- Ngày cấp CMND/CCCD

- Nơi cấp CMND/CCCD

- Số điện thoại

- Phòng thuê

- Số tiền bằng chữ

- Ngày lập hợp đồng

- Ngày hết hạn hợp đồng

KHI Ban quản lý chọn phòng,\
THE SYSTEM SHALL kiểm tra phòng tồn tại.

KHI Ban quản lý chọn phòng,\
THE SYSTEM SHALL kiểm tra phòng thuộc cơ sở mà Ban quản lý phụ trách.

KHI Ban quản lý chọn phòng hợp lệ,\
THE SYSTEM SHALL tự động lấy các thông tin phòng sau:

- Mã phòng

- Tầng

- Tiền phòng

- Tiền cọc

- Thông tin cơ sở của phòng

KHI Ban quản lý lưu hợp đồng với dữ liệu hợp lệ,\
THE SYSTEM SHALL tạo bản ghi mới trong bảng `contracts`.

KHI hợp đồng được tạo,\
THE SYSTEM SHALL tự động sinh `contract_id` theo thứ tự tăng dần.

KHI hợp đồng được tạo,\
THE SYSTEM SHALL tự động sinh mã hợp đồng duy nhất cho trường `code`.

KHI hợp đồng được tạo,\
THE SYSTEM SHALL lưu trạng thái mặc định là `ACTIVE`.

KHI hợp đồng được tạo,\
THE SYSTEM SHALL lưu `created_by` là người dùng đang đăng nhập.

KHI hợp đồng được tạo,\
THE SYSTEM SHALL lưu `created_at` và `updated_at` theo thời gian hiện tại.

KHI Ban quản lý không nhập ngày bắt đầu hợp đồng riêng,\
THE SYSTEM SHALL mặc định `start_date` bằng `signed_date`.

---

## 3.5 Validation khi tạo hợp đồng

KHI Ban quản lý bỏ trống họ tên khách thuê,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `TENANT_NAME_REQUIRED`.

KHI Ban quản lý bỏ trống số CMND/CCCD,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `TENANT_IDENTITY_REQUIRED`.

KHI Ban quản lý bỏ trống phòng thuê,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `ROOM_REQUIRED`.

KHI Ban quản lý bỏ trống ngày lập hợp đồng,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `SIGNED_DATE_REQUIRED`.

KHI Ban quản lý bỏ trống ngày hết hạn hợp đồng,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `END_DATE_REQUIRED`.

KHI ngày hết hạn nhỏ hơn ngày lập hợp đồng,\
THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `INVALID_CONTRACT_DATE`.

KHI phòng không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `ROOM_NOT_FOUND`.

KHI phòng không thuộc cơ sở mà Ban quản lý phụ trách,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `ROOM_ACCESS_DENIED`.

KHI phòng đã có hợp đồng `ACTIVE`,\
THE SYSTEM SHALL từ chối tạo hợp đồng mới và trả về HTTP 400 với mã lỗi `ROOM_ALREADY_HAS_ACTIVE_CONTRACT`.

---

## 3.6 In hợp đồng

KHI Ban quản lý chọn chức năng In hợp đồng,\
THE SYSTEM SHALL hiển thị bản hợp đồng theo mẫu in.

KHI bản in hợp đồng được hiển thị,\
THE SYSTEM SHALL điền dữ liệu hợp đồng vào mẫu hợp đồng thuê phòng trọ.

KHI in hợp đồng,\
THE SYSTEM SHALL hiển thị đầy đủ các phần sau:

- Quốc hiệu, tiêu ngữ

- Tên hợp đồng: `HỢP ĐỒNG THUÊ PHÒNG TRỌ`

- Ngày lập hợp đồng

- Địa chỉ cơ sở/phòng trọ

- Thông tin bên A

- Thông tin bên B

- Điều 1: Thông tin phòng thuê

- Điều 2: Giá thuê, phí dịch vụ, tiền cọc và thời hạn hợp đồng

- Điều 3: Trách nhiệm của các bên

- Điều 4: Trách nhiệm chung

- Phần chữ ký của đại diện bên A và đại diện bên B

KHI hợp đồng không tồn tại,\
THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `CONTRACT_NOT_FOUND`.

KHI hợp đồng không thuộc cơ sở mà Ban quản lý phụ trách,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

---

## 3.7 Nội dung mẫu hợp đồng in ra

KHI hệ thống in hợp đồng,\
THE SYSTEM SHALL sử dụng nội dung mẫu hợp đồng gồm các phần chính sau:

### Phần mở đầu

```text
CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM
Độc lập – Tự do – Hạnh phúc

HỢP ĐỒNG THUÊ PHÒNG TRỌ
```

### Thông tin lập hợp đồng

```text
Hôm nay ngày {signedDate}, tại địa chỉ: {facilityAddress}
Chúng tôi gồm:
```

### Thông tin bên A

Thông tin bên A là thông tin đại diện bên cho thuê hoặc Ban quản lý cơ sở.

Hệ thống hiển thị:

- Họ tên bên A

- Ngày sinh bên A nếu có

- Địa chỉ/hộ khẩu bên A nếu có

- Số CMND/CCCD bên A nếu có

- Số điện thoại bên A nếu có

### Thông tin bên B

Thông tin bên B lấy từ dữ liệu Ban quản lý nhập khi tạo hợp đồng.

Hệ thống hiển thị:

- Họ tên khách thuê

- Ngày sinh

- Nơi đăng ký hộ khẩu thường trú

- Số CMND/CCCD

- Ngày cấp

- Nơi cấp

- Số điện thoại

### Điều 1: Thông tin phòng thuê

Hệ thống hiển thị nội dung:

```text
Bên A đồng ý cho bên B thuê 01 phòng ở tại địa chỉ: {facilityAddress}.
```

Hệ thống hiển thị thêm:

- Mã phòng

- Tầng

- Danh sách thiết bị/cơ sở vật chất trong phòng nếu hệ thống có dữ liệu

Nếu hệ thống chưa có bảng thiết bị phòng,\
THE SYSTEM SHALL hiển thị nội dung thiết bị theo mẫu mặc định:

```text
Trong phòng gồm có: 01 bình nóng lạnh, 01 máy điều hòa, 01 tủ quần áo,
01 tủ bếp, 01 giường ngủ, 01 bàn học + ghế tựa, thiết bị vệ sinh
và đèn chiếu sáng đầy đủ.
```

### Điều 2: Giá thuê và các khoản phí

Hệ thống hiển thị:

- Giá thuê phòng theo tháng

- Số tiền bằng chữ

- Mã phòng

- Tầng

- Hình thức thanh toán

- Thời hạn hợp đồng

- Tiền điện

- Tiền nước

- Tiền Internet

- Tiền thu gom rác thải nếu có

- Tiền máy giặt nếu có

- Tiền điện chiếu sáng chung nếu có

- Tiền vệ sinh chung nếu có

- Tiền cọc

Nội dung mẫu:

```text
Điều 2: Giá thuê: {rentPrice} đ/tháng

Bằng chữ: {amountInWords}

Phòng số: {roomCode}    Tầng: {floor}

Hình thức thanh toán: Tiền mặt hoặc chuyển khoản vào đầu tháng
từ ngày 01 đến ngày 05 hằng tháng.

Hợp đồng có giá trị kể từ ngày {startDate} đến ngày {endDate}.

Tiền điện: {electricityPrice} đ/số tính theo chỉ số công tơ,
thanh toán vào cuối các tháng.

Tiền nước: {waterPrice} đ/người/tháng.

Tiền Internet: {internetFee} đ/người/tháng.

Tiền thu gom rác thải: {garbageFee} đ/người/tháng.

Tiền máy giặt: {washingFee} đ/người/tháng.

Tiền điện chiếu sáng chung: {commonElectricityFee} đ/người/tháng.

Tiền vệ sinh chung: {cleaningFee} đ/người/tháng.

Bên B đặt cọc cho bên A số tiền là: {depositAmount} đ.
```

### Quy định hoàn trả tiền cọc

```text
Tiền cọc sẽ được hoàn trả đầy đủ cho bên thuê khi hợp đồng này kết thúc
và bên thuê hoàn trả đầy đủ chi phí thuê bao gồm tiền phòng, điện, nước,
phí dịch vụ và các chi phí khác liên quan.
```

```text
Trường hợp bên B hủy hợp đồng trước thời hạn, bên B sẽ không được hoàn trả
số tiền đã đặt cọc.
```

### Điều 3: Trách nhiệm của các bên

#### Trách nhiệm của bên A

```text
- Tạo mọi điều kiện thuận lợi để bên B thực hiện theo hợp đồng.
- Cung cấp nguồn điện, nước, wifi cho bên B sử dụng.
- Hướng dẫn bên B chấp hành đúng các quy định của địa phương.
```

#### Trách nhiệm của bên B

```text
- Thanh toán đầy đủ các khoản tiền theo đúng thỏa thuận từ ngày 01 đến 05 hằng tháng.
- Nếu nộp muộn quá 03 ngày kể từ ngày đến hạn, mỗi ngày muộn sẽ tính bằng 1%
  giá trị tiền phòng/tháng.
- Bảo quản các trang thiết bị và cơ sở vật chất của bên A trang bị ban đầu.
- Không được tự ý sửa chữa, cải tạo cơ sở vật chất khi chưa có sự đồng ý của bên A.
- Giữ gìn vệ sinh trong và ngoài khuôn viên phòng trọ.
- Tự bảo quản đồ đạc và phương tiện đi lại của mình.
- Chấp hành mọi quy định của pháp luật Nhà nước và quy định của địa phương.
- Nếu cho khách ở qua đêm thì phải báo và được sự đồng ý của chủ nhà.
- Không được cờ bạc, buôn bán, tàng trữ ma túy hoặc các chất cấm.
- Tuân thủ quy định phòng cháy chữa cháy.
- Không đánh cãi chửi nhau, gây mất trật tự an ninh trong khu vực cư trú.
- Chỉ được sử dụng bếp điện đun nấu trong khuôn viên phòng ở.
```

### Điều 4: Trách nhiệm chung

```text
- Hai bên phải tạo điều kiện cho nhau thực hiện hợp đồng.
- Một trong hai bên muốn chấm dứt hợp đồng trước thời hạn thì phải báo trước
  cho bên kia ít nhất 30 ngày và hai bên phải có sự thống nhất.
- Trường hợp tranh chấp hoặc một bên vi phạm hợp đồng thì hai bên cùng nhau
  giải quyết tranh chấp. Nếu không giải quyết được thì yêu cầu cơ quan có thẩm
  quyền giải quyết.
- Hợp đồng được lập thành 02 bản có giá trị pháp lý như nhau, mỗi bên giữ một bản.
```

### Phần ký tên

```text
ĐẠI DIỆN BÊN B                         ĐẠI DIỆN BÊN A

(Ký, ghi rõ họ tên)                     (Ký, ghi rõ họ tên)
```

---

## 3.8 Trạng thái hợp đồng

KHI hợp đồng được tạo mới,\
THE SYSTEM SHALL gán trạng thái mặc định là `ACTIVE`.

KHI hợp đồng không còn hiệu lực sử dụng,\
THE SYSTEM SHALL cho phép cập nhật trạng thái thành `INACTIVE`.

Các trạng thái hợp đồng hợp lệ gồm:

| Trạng thái | Ý nghĩa |
| --- | --- |
| `ACTIVE` | Hợp đồng đang có hiệu lực |
| `INACTIVE` | Hợp đồng không còn hiệu lực |

---

## 3.9 Phân quyền

KHI người dùng có vai trò Ban quản lý,\
THE SYSTEM SHALL cho phép truy cập chức năng Quản lý hợp đồng.

KHI người dùng chưa đăng nhập,\
THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`.

KHI người dùng không có vai trò Ban quản lý,\
THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

---

# 4. API Contract

## 4.1 Lấy danh sách hợp đồng

### Endpoint

```http
GET /api/v1/contracts
```

### Description

API này trả về danh sách hợp đồng thuộc cơ sở mà Ban quản lý đang đăng nhập phụ trách.

Dữ liệu được lấy từ bảng `contracts`, sau đó join sang `rooms`, `facilities` và `users` để hiển thị thông tin phòng, cơ sở và người thuê.

### Query Parameters

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `keyword` | string | No | Tìm kiếm theo mã hợp đồng, mã phòng, tên khách thuê hoặc số CMND/CCCD |
| `status` | string | No | Lọc theo `ACTIVE` hoặc `INACTIVE` |
| `roomCode` | string | No | Lọc theo mã phòng |
| `page` | number | No | Trang hiện tại |
| `size` | number | No | Số bản ghi trên một trang |

### Response 200

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "contractId": 1,
        "code": "HD-HN0101-20240223-001",
        "roomId": 1,
        "roomCode": "402",
        "floor": 4,
        "tenantId": 10,
        "tenantFullName": "Đoàn Minh Quốc",
        "tenantIdentityNumber": "042205002822",
        "tenantPhone": "0900000000",
        "signedDate": "2024-02-23",
        "startDate": "2024-02-23",
        "endDate": "2024-08-31",
        "status": "ACTIVE"
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

## 4.2 Xem chi tiết hợp đồng

### Endpoint

```http
GET /api/v1/contracts/{contractId}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "contractId": 1,
    "code": "HD-HN0101-20240223-001",
    "room": {
      "roomId": 1,
      "roomCode": "402",
      "floor": 4,
      "rentPrice": 2100000,
      "depositAmount": 3000000
    },
    "tenant": {
      "tenantId": 10,
      "tenantFullName": "Đoàn Minh Quốc",
      "tenantDob": "2005-01-12",
      "tenantPermanentAddress": "Đan Trường, Nghi Xuân, Hà Tĩnh",
      "tenantIdentityNumber": "042205002822",
      "tenantIdentityIssueDate": null,
      "tenantIdentityIssuePlace": "Hà Tĩnh",
      "tenantPhone": "0900000000"
    },
    "amountInWords": "Hai triệu một trăm nghìn đồng",
    "signedDate": "2024-02-23",
    "startDate": "2024-02-23",
    "endDate": "2024-08-31",
    "status": "ACTIVE",
    "createdBy": 5,
    "createdAt": "2026-06-13T10:00:00",
    "updatedAt": "2026-06-13T10:00:00"
  }
}
```

---

## 4.3 Tạo hợp đồng mới

### Endpoint

```http
POST /api/v1/contracts
```

### Description

API này tạo hợp đồng mới cho một khách thuê và một phòng.

Ban quản lý nhập thông tin khách thuê, chọn phòng, nhập số tiền bằng chữ, ngày lập hợp đồng và ngày hết hạn.

Hệ thống tự lấy thông tin phòng như mã phòng, tầng, tiền phòng và tiền cọc từ dữ liệu phòng để phục vụ hiển thị chi tiết và in hợp đồng.

### Request

```json
{
  "roomId": 1,
  "tenantFullName": "Đoàn Minh Quốc",
  "tenantDob": "2005-01-12",
  "tenantPermanentAddress": "Đan Trường, Nghi Xuân, Hà Tĩnh",
  "tenantIdentityNumber": "042205002822",
  "tenantIdentityIssueDate": null,
  "tenantIdentityIssuePlace": "Hà Tĩnh",
  "tenantPhone": "0900000000",
  "amountInWords": "Hai triệu một trăm nghìn đồng",
  "signedDate": "2024-02-23",
  "endDate": "2024-08-31"
}
```

### Request Parameters

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `roomId` | integer | Yes | ID phòng được chọn |
| `tenantFullName` | string | Yes | Họ tên khách thuê |
| `tenantDob` | date | No | Ngày sinh khách thuê |
| `tenantPermanentAddress` | string | No | Nơi đăng ký hộ khẩu thường trú |
| `tenantIdentityNumber` | string | Yes | Số CMND/CCCD |
| `tenantIdentityIssueDate` | date | No | Ngày cấp CMND/CCCD |
| `tenantIdentityIssuePlace` | string | No | Nơi cấp CMND/CCCD |
| `tenantPhone` | string | No | Số điện thoại khách thuê |
| `amountInWords` | string | No | Số tiền bằng chữ |
| `signedDate` | date | Yes | Ngày lập hợp đồng |
| `endDate` | date | Yes | Ngày hết hạn hợp đồng |

### Response 201

```json
{
  "success": true,
  "data": {
    "contractId": 1,
    "code": "HD-HN0101-20240223-001",
    "roomId": 1,
    "roomCode": "402",
    "tenantId": 10,
    "tenantFullName": "Đoàn Minh Quốc",
    "signedDate": "2024-02-23",
    "startDate": "2024-02-23",
    "endDate": "2024-08-31",
    "status": "ACTIVE",
    "createdAt": "2026-06-13T10:00:00",
    "createdBy": 5
  }
}
```

---

## 4.4 In hợp đồng

### Endpoint

```http
GET /api/v1/contracts/{contractId}/print
```

### Description

API này trả về dữ liệu hoặc trang in hợp đồng theo mẫu hợp đồng thuê phòng trọ.

Hệ thống có thể render ra HTML print view hoặc PDF tùy thiết kế kỹ thuật.

### Response 200

```json
{
  "success": true,
  "data": {
    "contractId": 1,
    "code": "HD-HN0101-20240223-001",
    "printUrl": "/contracts/1/print-view"
  }
}
```

---

# 5. Business Rules

## 5.1 Quy tắc tạo hợp đồng

Khi tạo hợp đồng, Ban quản lý bắt buộc nhập:

- Họ tên khách thuê

- Số CMND/CCCD

- Phòng thuê

- Ngày lập hợp đồng

- Ngày hết hạn hợp đồng

Các trường còn lại có thể nhập nếu có thông tin.

Sau khi tạo thành công, hợp đồng mặc định có trạng thái `ACTIVE`.

---

## 5.2 Quy tắc sinh mã hợp đồng

Trường `code` trong bảng `contracts` là bắt buộc và duy nhất.

Hệ thống phải tự động sinh mã hợp đồng.

Định dạng đề xuất:

```text
HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}
```

Ví dụ:

```text
HD-402-20240223-001
```

Trong đó:

| Thành phần | Ý nghĩa |
| --- | --- |
| `HD` | Tiền tố hợp đồng |
| `402` | Mã phòng |
| `20240223` | Ngày lập hợp đồng |
| `001` | Số thứ tự tránh trùng |

---

## 5.3 Quy tắc chọn phòng

Ban quản lý chỉ được chọn phòng thuộc cơ sở mà mình phụ trách.

Không được tạo hợp đồng cho phòng thuộc cơ sở khác.

Không được tạo hợp đồng mới cho phòng đang có hợp đồng `ACTIVE`.

---

## 5.4 Quy tắc lấy thông tin phòng

Khi Ban quản lý chọn phòng, hệ thống tự động lấy:

- Mã phòng

- Tầng

- Tiền phòng

- Tiền cọc

- Thông tin cơ sở

Các thông tin này phục vụ cho:

- Màn chi tiết hợp đồng

- Màn in hợp đồng

- Nội dung hợp đồng

Trong bảng `contracts` hiện tại, hệ thống lưu `room_id` để liên kết với bảng `rooms`.

Nếu team muốn lưu snapshot tiền phòng và tiền cọc tại thời điểm tạo hợp đồng, nên bổ sung thêm các cột:

```sql
rent_price DECIMAL(18,2) NULL,
deposit_amount DECIMAL(18,2) NULL,
room_code_snapshot NVARCHAR(50) NULL,
floor_snapshot INT NULL
```

---

## 5.5 Quy tắc ngày hợp đồng

`end_date` phải lớn hơn hoặc bằng `signed_date`.

Nếu người dùng không nhập `start_date`, hệ thống mặc định:

```text
start_date = signed_date
```

---

## 5.6 Quy tắc trạng thái hợp đồng

Các trạng thái hợp lệ:

| Status | Ý nghĩa |
| --- | --- |
| `ACTIVE` | Hợp đồng đang hiệu lực |
| `INACTIVE` | Hợp đồng không còn hiệu lực |

Khi tạo mới, trạng thái mặc định là `ACTIVE`.

---

## 5.7 Quy tắc in hợp đồng

Khi in hợp đồng, hệ thống lấy dữ liệu từ:

| Dữ liệu | Nguồn |
| --- | --- |
| Thông tin hợp đồng | `contracts` |
| Thông tin phòng | `rooms` |
| Thông tin khách thuê | `contracts` và `users` |
| Thông tin cơ sở | `facilities` |
| Thông tin giá điện, nước, dịch vụ | `facilities` hoặc cấu hình phí hiện tại |
| Thông tin bên A | `facilities` hoặc cấu hình đại diện cơ sở |

---

# 6. Database Impact

## 6.1 Bảng chính: `contracts`

Bảng `contracts` được sử dụng để lưu hợp đồng.

```sql
contracts
---------
contract_id
code
room_id
tenant_id
tenant_full_name
tenant_dob
tenant_permanent_address
tenant_identity_number
tenant_identity_issue_date
tenant_identity_issue_place
tenant_phone
amount_in_words
signed_date
start_date
end_date
status
created_by
created_at
updated_at
deleted_at
```

---

## 6.2 Mapping dữ liệu tạo hợp đồng

| Field trong form | Cột trong `contracts` |
| --- | --- |
| Họ tên khách thuê | `tenant_full_name` |
| Ngày sinh | `tenant_dob` |
| Nơi đăng ký hộ khẩu | `tenant_permanent_address` |
| Số CMND/CCCD | `tenant_identity_number` |
| Ngày cấp | `tenant_identity_issue_date` |
| Nơi cấp | `tenant_identity_issue_place` |
| Số điện thoại | `tenant_phone` |
| Phòng thuê | `room_id` |
| Số tiền bằng chữ | `amount_in_words` |
| Ngày lập hợp đồng | `signed_date` |
| Ngày bắt đầu | `start_date` |
| Ngày hết hạn | `end_date` |
| Trạng thái | `status` |

---

## 6.3 Quan hệ dữ liệu

### `contracts.room_id`

Liên kết tới:

```sql
rooms.room_id
```

Dùng để lấy:

- Mã phòng

- Tầng

- Giá phòng

- Tiền cọc

- Cơ sở của phòng

---

### `contracts.tenant_id`

Liên kết tới:

```sql
users.user_id
```

Dùng để xác định người thuê trong hệ thống.

Nếu khách thuê chưa có tài khoản, hệ thống cần tạo hoặc chọn user tenant tương ứng trước khi tạo hợp đồng.

---

### `contracts.created_by`

Liên kết tới:

```sql
users.user_id
```

Dùng để lưu người tạo hợp đồng.

---

# 7. UI/UX Specification

## 7.1 Màn hình danh sách hợp đồng

### Thành phần giao diện

- Tiêu đề: `Quản lý hợp đồng`

- Ô tìm kiếm

- Bộ lọc trạng thái

- Nút tạo hợp đồng mới

- Bảng danh sách hợp đồng

- Phân trang

### Cột trong bảng

| Cột | Mô tả |
| --- | --- |
| STT | Số thứ tự |
| Mã hợp đồng | `contracts.code` |
| Mã phòng | Lấy từ `rooms` |
| Khách thuê | `contracts.tenant_full_name` |
| Số CMND/CCCD | `contracts.tenant_identity_number` |
| Số điện thoại | `contracts.tenant_phone` |
| Ngày lập | `contracts.signed_date` |
| Ngày hết hạn | `contracts.end_date` |
| Trạng thái | `ACTIVE` hoặc `INACTIVE` |
| Hành động | Xem chi tiết, In hợp đồng |

---

## 7.2 Màn hình tạo hợp đồng

### Thành phần form

| Trường | Bắt buộc | Ghi chú |
| --- | --- | --- |
| Họ tên khách thuê | Có | Nhập text |
| Ngày sinh | Không | Chọn date |
| Nơi đăng ký hộ khẩu | Không | Nhập text |
| Số CMND/CCCD | Có | Nhập text |
| Ngày cấp | Không | Chọn date |
| Nơi cấp | Không | Nhập text |
| Số điện thoại | Không | Nhập text |
| Phòng thuê | Có | Chọn từ danh sách phòng thuộc cơ sở |
| Số tiền bằng chữ | Không | Nhập text |
| Ngày lập hợp đồng | Có | Chọn date |
| Ngày hết hạn | Có | Chọn date |

### Thông tin tự động hiển thị sau khi chọn phòng

- Mã phòng

- Tầng

- Tiền phòng

- Tiền cọc

- Tên cơ sở

- Địa chỉ cơ sở

---

## 7.3 Màn hình chi tiết hợp đồng

Hiển thị đầy đủ thông tin hợp đồng và có nút:

- Quay lại danh sách

- In hợp đồng

---

## 7.4 Màn hình in hợp đồng

Màn hình in hợp đồng hiển thị theo layout giấy A4.

Nút thao tác:

- In

- Quay lại chi tiết hợp đồng

---

# 8. Technical Constraints

- Chỉ Ban quản lý được truy cập chức năng Quản lý hợp đồng.

- Ban quản lý chỉ được xem hợp đồng của cơ sở mình phụ trách.

- Không được xem hoặc tạo hợp đồng cho phòng thuộc cơ sở khác.

- Mỗi hợp đồng phải liên kết với một phòng hợp lệ.

- Mỗi hợp đồng phải liên kết với một người thuê hợp lệ.

- Mỗi hợp đồng phải có mã hợp đồng duy nhất.

- Hệ thống phải tự sinh `contract_id`.

- Hệ thống phải tự sinh `code`.

- Khi tạo mới, hợp đồng có trạng thái mặc định `ACTIVE`.

- `end_date` phải lớn hơn hoặc bằng `signed_date`.

- Không được tạo hợp đồng mới cho phòng đang có hợp đồng `ACTIVE`.

- Khi in hợp đồng, hệ thống không được yêu cầu nhập lại dữ liệu thủ công.

- Dữ liệu in hợp đồng phải lấy từ database.

- API danh sách hợp đồng phải phản hồi dưới `1000ms (p95)`.

- API chi tiết hợp đồng phải phản hồi dưới `500ms (p95)`.

- API tạo hợp đồng phải phản hồi dưới `1000ms (p95)`.

- API in hợp đồng phải phản hồi dưới `2000ms (p95)`.

---

# 9. Error Codes

| Error Code | HTTP Status | Description |
| --- | --- | --- |
| `UNAUTHORIZED` | 401 | Người dùng chưa đăng nhập |
| `FORBIDDEN` | 403 | Người dùng không có quyền truy cập |
| `CONTRACT_ACCESS_DENIED` | 403 | Không có quyền truy cập hợp đồng này |
| `ROOM_ACCESS_DENIED` | 403 | Không có quyền tạo hợp đồng cho phòng này |
| `CONTRACT_NOT_FOUND` | 404 | Không tìm thấy hợp đồng |
| `ROOM_NOT_FOUND` | 404 | Không tìm thấy phòng |
| `TENANT_NAME_REQUIRED` | 400 | Thiếu họ tên khách thuê |
| `TENANT_IDENTITY_REQUIRED` | 400 | Thiếu số CMND/CCCD |
| `ROOM_REQUIRED` | 400 | Thiếu phòng thuê |
| `SIGNED_DATE_REQUIRED` | 400 | Thiếu ngày lập hợp đồng |
| `END_DATE_REQUIRED` | 400 | Thiếu ngày hết hạn hợp đồng |
| `INVALID_CONTRACT_DATE` | 400 | Ngày hết hạn không hợp lệ |
| `ROOM_ALREADY_HAS_ACTIVE_CONTRACT` | 400 | Phòng đã có hợp đồng đang hiệu lực |
| `CONTRACT_CREATE_FAILED` | 500 | Không thể tạo hợp đồng |
| `CONTRACT_PRINT_FAILED` | 500 | Không thể in hợp đồng |

---

# 10. Out Of Scope

Các chức năng sau không nằm trong phạm vi feature Quản lý hợp đồng:

- Ký hợp đồng điện tử.

- Chữ ký số.

- Upload file scan hợp đồng.

- Gia hạn hợp đồng.

- Thanh lý hợp đồng.

- Tự động chuyển hợp đồng hết hạn sang `INACTIVE`.

- Tự động gửi thông báo sắp hết hạn hợp đồng.

- Tự động tạo hóa đơn từ hợp đồng.

- Quản lý phụ lục hợp đồng.

- Quản lý nhiều người thuê trong cùng một hợp đồng.

- Chỉnh sửa nội dung mẫu hợp đồng động trên giao diện.

- OCR hợp đồng giấy.

- Tích hợp cơ quan pháp lý hoặc hệ thống thuế.