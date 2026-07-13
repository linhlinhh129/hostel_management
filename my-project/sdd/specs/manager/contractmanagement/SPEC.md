# Feature: Quản lý hợp đồng

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** \[YYYY-MM-DD\]\
**Priority:** High

---

# 1. Business Context

Trong hệ thống quản lý nhà trọ, hợp đồng thuê phòng là tài liệu dùng để ghi nhận thỏa thuận giữa Ban quản lý hoặc chủ cơ sở cho thuê và người thuê phòng. Hợp đồng chứa các thông tin quan trọng như:

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
- Tạo tài khoản hệ thống cho người thuê trực tiếp từ hợp đồng.
- Xóa (soft-delete) các hợp đồng đã hết hiệu lực (`INACTIVE`).
- Quản lý trạng thái hợp đồng `ACTIVE` hoặc `INACTIVE`.

Khi lập hợp đồng, Ban quản lý nhập thông tin cá nhân của khách thuê, chọn phòng cần thuê, nhập số tiền bằng chữ, ngày lập hợp đồng và ngày hết hạn. Hệ thống tự động lấy thông tin phòng như mã phòng, tầng, tiền phòng và tiền cọc từ dữ liệu phòng/cơ sở để hiển thị và in trong nội dung hợp đồng.

Sau khi lưu thành công, hợp đồng được tạo với trạng thái mặc định là `ACTIVE`.

---

# 2. User Stories

## Story 1: Xem danh sách hợp đồng

Là Ban quản lý,tôi muốn xem danh sách các hợp đồng thuộc cơ sở mà tôi phụ trách để theo dõi tình trạng thuê phòng của khách thuê.

## Story 2: Xem chi tiết hợp đồng

Là Ban quản lý, tôi muốn xem chi tiết một hợp đồng để kiểm tra thông tin khách thuê, phòng thuê, thời hạn hợp đồng và trạng thái hợp đồng.

## Story 3: Tạo hợp đồng mới

Là Ban quản lý, tôi muốn tạo hợp đồng mới cho một khách thuê và một phòng cụ thể để ghi nhận việc khách thuê bắt đầu thuê phòng.

## Story 4: Tự động lấy thông tin phòng khi lập hợp đồng

Là Ban quản lý, khi tôi chọn phòng trong form tạo hợp đồng, tôi muốn hệ thống tự lấy mã phòng, tầng, tiền phòng và tiền cọc để giảm nhập liệu thủ công và tránh sai thông tin.

## Story 5: In hợp đồng

Là Ban quản lý, tôi muốn in hợp đồng theo mẫu hợp đồng thuê phòng trọ để lưu trữ hoặc đưa cho người thuê ký xác nhận.

## Story 6: Kiểm tra quyền truy cập

Là hệ thống, khi người dùng chưa đăng nhập hoặc không có quyền Ban quản lý, tôi muốn từ chối truy cập chức năng Quản lý hợp đồng để bảo vệ dữ liệu hợp đồng của cơ sở.

## Story 7: Tạo tài khoản người thuê từ hợp đồng

Là Ban quản lý, tôi muốn tạo nhanh tài khoản đăng nhập cho người thuê dựa trên thông tin hợp đồng để họ có thể truy cập hệ thống mà không cần tự đăng ký.

## Story 8: Xóa hợp đồng đã hết hiệu lực

Là Ban quản lý, tôi muốn xóa các hợp đồng đã hết hiệu lực (INACTIVE) để dọn dẹp danh sách quản lý.

# 3. Acceptance Criteria (EARS)

## 3.1 Xem danh sách hợp đồng

KHI Ban quản lý truy cập màn hình Quản lý hợp đồng, THE SYSTEM SHALL hiển thị danh sách hợp đồng thuộc cơ sở mà Ban quản lý đó phụ trách. KHI danh sách hợp đồng được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:

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
- Hành động xóa hợp đồng (khi trạng thái là INACTIVE)

KHI Ban quản lý không phụ trách cơ sở nào, THE SYSTEM SHALL hiển thị thông báo:

```text
Bạn chưa được phân quyền quản lý cơ sở nào.
```

KHI không có hợp đồng nào trong cơ sở mà Ban quản lý phụ trách, THE SYSTEM SHALL hiển thị thông báo:

```text
Hiện tại chưa có hợp đồng nào.
```

## 3.2 Phân quyền dữ liệu theo cơ sở

KHI Ban quản lý xem danh sách hợp đồng, THE SYSTEM SHALL chỉ truy xuất các hợp đồng có `room_id` thuộc phòng của cơ sở mà Ban quản lý đó phụ trách.

KHI Ban quản lý cố truy cập hợp đồng thuộc cơ sở khác, THE SYSTEM SHALL từ chối truy cập và trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

KHI hệ thống truy xuất hợp đồng, THE SYSTEM SHALL join bảng `contracts` với bảng `rooms` để xác định cơ sở của phòng.

## 3.3 Xem chi tiết hợp đồng

KHI Ban quản lý chọn một hợp đồng, THE SYSTEM SHALL hiển thị chi tiết hợp đồng. KHI chi tiết hợp đồng được hiển thị, THE SYSTEM SHALL hiển thị các thông tin sau:

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
- Hành động Tạo tài khoản người thuê (nếu hợp đồng chưa liên kết tenant_id)
- Hành động Xóa hợp đồng (nếu trạng thái là INACTIVE)

KHI hợp đồng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `CONTRACT_NOT_FOUND`. KHI hợp đồng tồn tại nhưng không thuộc cơ sở mà Ban quản lý phụ trách, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

## 3.4 Tạo hợp đồng mới

KHI Ban quản lý truy cập màn hình Tạo hợp đồng, THE SYSTEM SHALL hiển thị form tạo hợp đồng. KHI form tạo hợp đồng được hiển thị, THE SYSTEM SHALL cho phép Ban quản lý nhập các thông tin sau:

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

KHI Ban quản lý chọn phòng, THE SYSTEM SHALL kiểm tra phòng tồn tại.

KHI Ban quản lý chọn phòng, THE SYSTEM SHALL kiểm tra phòng thuộc cơ sở mà Ban quản lý phụ trách.

KHI Ban quản lý chọn phòng hợp lệ, THE SYSTEM SHALL tự động lấy các thông tin phòng sau:

- Mã phòng
- Tầng
- Tiền phòng
- Tiền cọc
- Thông tin cơ sở của phòng

KHI Ban quản lý lưu hợp đồng với dữ liệu hợp lệ, THE SYSTEM SHALL tạo bản ghi mới trong bảng `contracts`.

KHI hợp đồng được tạo, THE SYSTEM SHALL tự động sinh `contract_id` theo thứ tự tăng dần.

KHI hợp đồng được tạo, THE SYSTEM SHALL tự động sinh mã hợp đồng duy nhất cho trường `code` theo định dạng `HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}`.

Ví dụ: `HD-402-20240223-001`. Trong đó:

- `HD`: Tiền tố hợp đồng
- `402`: Mã phòng
- `20240223`: Ngày lập hợp đồng
- `001`: Số thứ tự tránh trùng

KHI hệ thống lấy thông tin phòng để tạo hợp đồng, THE SYSTEM SHALL lưu lại giá trị tại thời điểm tạo hợp đồng (như `rent_price`, `deposit_amount`, `room_code_snapshot`, `floor_snapshot` nếu có) để tránh sai lệch khi giá phòng thay đổi sau này.

KHI hợp đồng được tạo, THE SYSTEM SHALL lưu trạng thái mặc định là `ACTIVE`.

KHI hợp đồng được tạo, THE SYSTEM SHALL lưu `created_by` là người dùng đang đăng nhập.

KHI hợp đồng được tạo, THE SYSTEM SHALL lưu `created_at` và `updated_at` theo thời gian hiện tại.

KHI Ban quản lý không nhập ngày bắt đầu hợp đồng riêng, THE SYSTEM SHALL mặc định `start_date` bằng `signed_date`.

## 3.5 Validation khi tạo hợp đồng

KHI Ban quản lý bỏ trống họ tên khách thuê, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `TENANT_NAME_REQUIRED`.

KHI Ban quản lý bỏ trống số CMND/CCCD, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `TENANT_IDENTITY_REQUIRED`.

KHI Ban quản lý bỏ trống phòng thuê, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `ROOM_REQUIRED`.

KHI Ban quản lý bỏ trống ngày lập hợp đồng, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `SIGNED_DATE_REQUIRED`.

KHI Ban quản lý bỏ trống ngày hết hạn hợp đồng, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `END_DATE_REQUIRED`.

KHI ngày hết hạn nhỏ hơn ngày lập hợp đồng, THE SYSTEM SHALL từ chối tạo hợp đồng và trả về mã lỗi `INVALID_CONTRACT_DATE`.

KHI phòng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `ROOM_NOT_FOUND`.

KHI phòng không thuộc cơ sở mà Ban quản lý phụ trách, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `ROOM_ACCESS_DENIED`.

KHI phòng đã có hợp đồng `ACTIVE`, THE SYSTEM SHALL từ chối tạo hợp đồng mới và trả về HTTP 400 với mã lỗi `ROOM_ALREADY_HAS_ACTIVE_CONTRACT`.

## 3.6 In hợp đồng

KHI Ban quản lý chọn chức năng In hợp đồng, THE SYSTEM SHALL hiển thị bản hợp đồng theo mẫu in.

KHI bản in hợp đồng được hiển thị, THE SYSTEM SHALL điền dữ liệu hợp đồng vào mẫu hợp đồng thuê phòng trọ.

KHI in hợp đồng, THE SYSTEM SHALL hiển thị đầy đủ các phần sau:

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

KHI hợp đồng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 với mã lỗi `CONTRACT_NOT_FOUND`.

KHI hợp đồng không thuộc cơ sở mà Ban quản lý phụ trách, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `CONTRACT_ACCESS_DENIED`.

## 3.7 Nội dung mẫu hợp đồng in ra

KHI hệ thống in hợp đồng, THE SYSTEM SHALL lấy dữ liệu từ các nguồn sau:

| Dữ liệu | Nguồn |
| --- | --- |
| Thông tin hợp đồng | Bảng `contracts` |
| Thông tin phòng | Bảng `rooms` |
| Thông tin khách thuê | Bảng `contracts` và `users` |
| Thông tin cơ sở | Bảng `facilities` |
| Thông tin giá điện, nước, dịch vụ | Bảng `facilities` hoặc cấu hình phí hiện tại |
| Thông tin bên A | Bảng `facilities` hoặc cấu hình đại diện cơ sở |

KHI hệ thống in hợp đồng, THE SYSTEM SHALL sử dụng nội dung mẫu hợp đồng gồm các phần chính sau:

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

Thông tin bên B lấy từ dữ liệu Ban quản lý nhập khi tạo hợp đồng. Hệ thống hiển thị:

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

Nếu hệ thống chưa có bảng thiết bị phòng, THE SYSTEM SHALL hiển thị nội dung thiết bị theo mẫu mặc định:

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
- Tiền cọc Nội dung mẫu:

```text
Điều 2: Giá thuê: {rentPrice} đ/tháng
Bằng chữ: {amountInWords}
Phòng số: {roomCode}    Tầng: {floor}
Hình thức thanh toán: Tiền mặt hoặc chuyển khoản vào đầu tháng từ ngày 01 đến ngày 05 hằng tháng.
Hợp đồng có giá trị kể từ ngày {startDate} đến ngày {endDate}.
Tiền điện: {electricityPrice} đ/số tính theo chỉ số công tơ, thanh toán vào cuối các tháng.
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
Tiền cọc sẽ được hoàn trả đầy đủ cho bên thuê khi hợp đồng này kết thúc và bên thuê hoàn trả đầy đủ chi phí thuê bao gồm tiền phòng, điện, nước, phí dịch vụ và các chi phí khác liên quan.
```

```text
Trường hợp bên B hủy hợp đồng trước thời hạn, bên B sẽ không được hoàn trả số tiền đã đặt cọc.
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
- Một trong hai bên muốn chấm dứt hợp đồng trước thời hạn thì phải báo trước cho bên kia ít nhất 30 ngày và hai bên phải có sự thống nhất.
- Trường hợp tranh chấp hoặc một bên vi phạm hợp đồng thì hai bên cùng nhau giải quyết tranh chấp. Nếu không giải quyết được thì yêu cầu cơ quan có thẩm quyền giải quyết.
- Hợp đồng được lập thành 02 bản có giá trị pháp lý như nhau, mỗi bên giữ một bản.
```

### Phần ký tên

```text
ĐẠI DIỆN BÊN B                         ĐẠI DIỆN BÊN A

(Ký, ghi rõ họ tên)                     (Ký, ghi rõ họ tên)
```

---

## 3.8 Trạng thái hợp đồng

KHI hợp đồng được tạo mới, THE SYSTEM SHALL gán trạng thái mặc định là `ACTIVE`.

KHI hợp đồng không còn hiệu lực sử dụng, THE SYSTEM SHALL cho phép cập nhật trạng thái thành `INACTIVE`.

Các trạng thái hợp đồng hợp lệ gồm:

| Trạng thái | Ý nghĩa |
| --- | --- |
| `ACTIVE` | Hợp đồng đang có hiệu lực |
| `INACTIVE` | Hợp đồng không còn hiệu lực |

---

## 3.9 Thêm tài khoản người thuê từ hợp đồng

KHI Ban quản lý chọn chức năng Tạo tài khoản người thuê từ màn hình chi tiết hợp đồng, THE SYSTEM SHALL hiển thị form điền thông tin người thuê (kế thừa các thông tin có sẵn từ hợp đồng).

KHI Ban quản lý submit form hợp lệ, THE SYSTEM SHALL kiểm tra tài khoản đã tồn tại hay chưa dựa trên Email, SĐT và CCCD.

KHI tài khoản chưa tồn tại, THE SYSTEM SHALL tạo tài khoản mới với role TENANT, sinh mật khẩu tạm thời và gửi qua email.

KHI tài khoản đã tồn tại nhưng bị vô hiệu hóa, THE SYSTEM SHALL hiển thị hộp thoại xác nhận kích hoạt lại (Reactivate).

KHI quá trình thêm người thuê thành công, THE SYSTEM SHALL cập nhật `tenant_id` vào hợp đồng và ghi AuditLog.

## 3.10 Xóa hợp đồng

KHI Ban quản lý chọn Xóa hợp đồng, THE SYSTEM SHALL yêu cầu xác nhận trước khi thực hiện.

KHI Ban quản lý xác nhận xóa, THE SYSTEM SHALL kiểm tra trạng thái của hợp đồng.

KHI trạng thái hợp đồng không phải là `INACTIVE`, THE SYSTEM SHALL từ chối xóa và hiển thị thông báo lỗi.

KHI trạng thái hợp đồng là `INACTIVE`, THE SYSTEM SHALL thực hiện soft delete (đánh dấu xóa) hợp đồng đó và ghi AuditLog.

---

## 3.11 Phân quyền

KHI người dùng có vai trò Ban quản lý, THE SYSTEM SHALL cho phép truy cập chức năng Quản lý hợp đồng.

KHI người dùng chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401 với mã lỗi `UNAUTHORIZED`.

## KHI người dùng không có vai trò Ban quản lý, THE SYSTEM SHALL trả về HTTP 403 với mã lỗi `FORBIDDEN`.

# 4. Servlet Contract

## 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ContractServlet` |
| **URL Pattern** | `GET /manager/contracts` — danh sách |
| **URL Pattern** | `GET /manager/contracts/create` — form tạo |
| **URL Pattern** | `POST /manager/contracts/create` — lưu tạo mới |
| **URL Pattern** | `GET /manager/contracts/detail` — chi tiết (cần param `id`) |
| **URL Pattern** | `GET /manager/contracts/add-tenant` — form thêm người thuê (cần param `contractId`) |
| **URL Pattern** | `POST /manager/contracts/add-tenant` — lưu người thuê vào hợp đồng |
| **URL Pattern** | `POST /manager/contracts/delete` — xóa hợp đồng (cần param `id`) |
| **Phân quyền** | Role = `MANAGER` hoặc `ADMIN` (kiểm tra qua `BaseServlet` và `UserSessionDTO`) |

---

## 4.2 Request Attributes — Danh sách (list.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `contracts` | `List<Contract>` | `ContractService.getContractsByManager(managerId, searchName)` | Danh sách hợp đồng thuộc quyền quản lý |
| `searchName` | `String` | Query param `searchName` | Giữ lại giá trị tìm kiếm trên form |

---

## 4.3 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `contract` | `Contract` | `ContractService.getContractDetail(id, managerId)` | Thông tin đầy đủ hợp đồng (kèm thông tin phòng, cơ sở, quản lý) |

---

## 4.4 Request Attributes — Form tạo (create.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `availableRooms` | `List<Room>` | `ContractDAO.getAvailableRooms(managerId)` | Danh sách các phòng trống của cơ sở thuộc quyền quản lý |
| `preselectedRoomId` | `String` | Query param `roomId` | ID phòng được chọn sẵn từ URL |
| `errorMessage` | `String` | Try-catch khi submit lỗi | Thông báo lỗi nếu có |

---

## 4.5 Request Attributes — Form thêm người thuê (add_tenant.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `prefilledContract` | `Map<String, Object>` | Truy vấn từ `ContractDAO` qua `contractId` | Thông tin hợp đồng được điền sẵn |
| `dto` | `Map<String, Object>` | Request params | Dữ liệu form submit lại khi có lỗi validation |
| `errorMessage` | `String` | Lỗi validation/nghiệp vụ | Thông báo lỗi khi submit thất bại |
| `showReactivateConfirmation` | `Boolean` | Logic kiểm tra tài khoản | Cờ hiển thị hộp thoại confirm reactivate người dùng cũ |
| `existingUserFullName` | `String` | Truy vấn từ `users` | Tên người dùng đã tồn tại |
| `existingUserIdentity` | `String` | Truy vấn từ `users` | CMND/CCCD của người dùng đã tồn tại |

---

## 4.6 Validation — POST /manager/contracts/create

| Form param | Điều kiện hợp lệ | Hành vi khi lỗi |
| --- | --- | --- |
| `roomId` | Tồn tại, thuộc quyền quản lý, đang trống | Bắt bằng logic code / Database error |
| `tenantFullName` | Không rỗng | Client bắt rỗng (required) |
| `tenantIdentityNumber` | Bắt buộc, hợp lệ chuẩn VN (9, 12 số) | `IllegalArgumentException` |
| `tenantPhone` | Rỗng hoặc chuẩn VN (10 số) | `IllegalArgumentException` |
| `signedDate`, `startDate`, `endDate` | Không rỗng, chuẩn ngày | `DateTimeParseException` |

**Khi tạo thành công:** `contractService.createContract` tự động xử lý, set status = `ACTIVE`, `createdBy` = ID user hiện tại, sinh tự động `contract_id` và mã `code`. Ghi AuditLog `CREATE` và chuyển hướng về trang detail.

---

## 4.7 Validation — POST /manager/contracts/add-tenant

| Form param | Điều kiện hợp lệ | Lỗi trả về (`errorMessage`) |
| --- | --- | --- |
| `fullName`, `email`, `phone`, `identityNumber` | Không rỗng | Lỗi bắt buộc nhập |
| `email` | Phải đúng định dạng email, chưa tồn tại ở role khác `TENANT` | Lỗi validation / Lỗi tồn tại |
| `phone`, `identityNumber` | Hợp lệ chuẩn VN, không trùng lặp | Lỗi validation / Lỗi trùng lặp |
| `roomId`, `contractId` | Không rỗng | Yêu cầu nhập đủ trường bắt buộc |

**Khi tạo thành công:** Cập nhật hoặc thêm mới `users`, đổi status phòng thành `OCCUPIED`, gán `tenant_id` cho hợp đồng, gửi email pass, ghi AuditLog.

---

## 4.8 Xử lý lỗi (Servlet Behavior)

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Chuyển hướng về `/login` |
| Role không phải MANAGER/ADMIN | Trả về lỗi HTTP 403 (Access Denied) |
| Lỗi ID (Detail, Delete) | `NumberFormatException` -&gt; HTTP 400 hoặc 404 |
| Exception khi POST form | Bắt lỗi, forward lại trang form (như `create.jsp` hoặc `add_tenant.jsp`) kèm `errorMessage` |
| Yêu cầu xóa (delete) khi hợp đồng còn ACTIVE | Báo lỗi qua Flash Message (error) và redirect |

---

# 5. Technical Constraints

Max response time: 500ms (P95)

Sử dụng mô hình MVC (JSP + Servlet + Service + DAO)

Tuyệt đối không dùng Scriptlet trong JSP

Chỉ sử dụng JDBC thuần và PreparedStatement, không dùng ORM

Bắt buộc sử dụng transaction khi cập nhật nhiều bảng dữ liệu

Bắt buộc dùng SLF4J để ghi log, không trả lỗi gốc ra giao diện

Chỉ người dùng có role MANAGER hoặc ADMIN mới được truy cập

Ghi nhận Audit Log cho thao tác tạo hợp đồng, xóa hợp đồng và thêm người thuê

# 6. Out Of Scope

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