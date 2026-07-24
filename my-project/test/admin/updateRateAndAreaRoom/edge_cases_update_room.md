# Phân tích 10 Edge Cases "Chết Người" trong Tính năng Cập nhật Thông tin Phòng

Dựa trên tài liệu `SPEC.md` của tính năng **Update Rate & Area Room**, việc cập nhật 2 trường tưởng chừng đơn giản (Diện tích và Giá phòng) lại ẩn chứa rất nhiều cạm bẫy kỹ thuật. Dưới đây là 10 Edge Cases (Trường hợp biên) mà Developer thường bỏ qua, độ rủi ro, và đề xuất Unit Test cụ thể.

---

## 1. Lỗi phân cách phần nghìn theo Locale (Locale-dependent Number Format)
**⚠️ Tại sao nguy hiểm:** Ở VN, người dùng có thói quen nhập `1,500,000` (có dấu phẩy). Nếu Servlet dùng `new BigDecimal(request.getParameter("roomFee"))`, hệ thống sẽ lập tức văng `NumberFormatException` (Lỗi 500) vì Java mặc định không hiểu dấu phẩy là phân cách phần nghìn nếu không dùng `NumberFormat`.
**✅ Đề xuất Test Case:** 
- Gửi POST với `roomFee = "1,500,000"`.
- **Mong đợi:** Tùy vào quy định, hệ thống phải tự động gỡ bỏ dấu phẩy trước khi parse, hoặc ném Validation Exception thông báo "Vui lòng không nhập dấu phẩy".

## 2. Khoảng trắng ẩn trong Input (Trailing/Leading Whitespaces)
**⚠️ Tại sao nguy hiểm:** Nếu Copy/Paste từ Excel, giá trị thường dính khoảng trắng (`" 1500000 "`). Nếu Developer quên `.trim()` trước khi ép kiểu, `BigDecimal` sẽ văng lỗi crash ứng dụng.
**✅ Đề xuất Test Case:** 
- Gửi POST với `area = "  25.5  "`.
- **Mong đợi:** Hệ thống tự động trim và lưu thành công giá trị `25.5` thay vì báo lỗi.

## 3. Tràn cấu trúc giới hạn Database (Data Truncation / Precision Exceeded)
**⚠️ Tại sao nguy hiểm:** Trong CSDL, cột giá phòng thường là `DECIMAL(10,2)`. Nếu Hacker truyền giá trị `999999999999` (hàng tỷ tỷ), Database sẽ ném lỗi `Data truncation: Out of range value`. Lỗi này không bắt bằng Java Validation sẽ làm sập request và lộ stack trace DB.
**✅ Đề xuất Test Case:**
- Gửi POST với `roomFee = 999999999999`.
- **Mong đợi:** Servlet ném `ValidationException` "Giá phòng vượt quá giới hạn hệ thống cho phép".

## 4. Quá nhiều số thập phân (Fractional Limits)
**⚠️ Tại sao nguy hiểm:** User vô tình nhập diện tích là `25.3333333333333`. Khi lưu vào CSDL, nếu CSDL chỉ cho 2 số thập phân, nó sẽ tự làm tròn hoặc văng lỗi. Nếu đọc lên để tính tiền, có thể gây sai lệch tài chính lũy kế.
**✅ Đề xuất Test Case:**
- Gửi POST với `area = "25.333333"`.
- **Mong đợi:** Ném lỗi Validation yêu cầu "Chỉ cho phép tối đa 2 số thập phân" hoặc hệ thống chủ động `setScale(2, RoundingMode.HALF_UP)`.

## 5. Rò rỉ trạng thái đồng thời (Time-Of-Check to Time-Of-Use - TOCTOU)
**⚠️ Tại sao nguy hiểm:** Theo Spec: "Không được cập nhật nếu Facility INACTIVE". 
Giả sử Admin A mở form (lúc này Facility đang ACTIVE). Admin B vào tắt Facility thành INACTIVE. Sau đó Admin A mới bấm nút "Lưu". Nếu Backend chỉ ẩn nút ở Giao diện mà không kiểm tra lại trạng thái ở hàm `doPost`, Admin A vẫn lưu thành công vào cơ sở INACTIVE (Vi phạm Spec).
**✅ Đề xuất Test Case:**
- Ở hàm `doPost`, Mock `FacilityDAO.getStatus()` trả về `INACTIVE`.
- **Mong đợi:** Từ chối lưu và ném `ValidationException("Cơ sở đã bị vô hiệu hóa")` dù form đã được submit.

## 6. Lỗi Empty String "" so với Null
**⚠️ Tại sao nguy hiểm:** Spec yêu cầu: "Để trống thì lưu là NULL". Trong HTTP Servlet, khi user xóa trắng 1 input text, nó truyền lên chuỗi rỗng `""` chứ không phải null. Nếu developer viết `if (area == null)`, điều kiện bị sai, hệ thống sẽ cố parse `""` sang số và văng lỗi 500.
**✅ Đề xuất Test Case:**
- Gửi POST với tham số `area=""` và `roomFee="    "`.
- **Mong đợi:** Code phải bắt được rỗng/khoảng trắng, gọi `room.setArea(null)` và không văng `NumberFormatException`.

## 7. Zero và Negative Zero (Biên 0)
**⚠️ Tại sao nguy hiểm:** Spec ghi "nhập số dương hoặc để trống". Số `0` có phải số dương không? (Toán học là không). Nhưng trong code Java nếu dùng `value.signum() < 0` thì `0` sẽ vượt qua. Nếu phòng bị gán diện tích = 0 hoặc giá = 0, nó có thể gây lỗi chia cho 0 (Divide by Zero) ở phân hệ Báo cáo Doanh thu sau này.
**✅ Đề xuất Test Case:**
- Gửi POST với `area = "0"`.
- **Mong đợi:** Phải ném `ValidationException("Diện tích phải lớn hơn 0")` (Strict Validation).

## 8. Cập nhật phòng đã bị Xóa Mềm (Soft Deleted Room)
**⚠️ Tại sao nguy hiểm:** Một phòng đã bị xóa (deleted = true) khỏi hệ thống, nhưng Admin vẫn lưu bookmark đường link `/admin/rooms/5/update`. Nếu Backend chỉ check phòng "có tồn tại" mà không check "còn hoạt động", Admin sẽ âm thầm thay đổi giá của 1 căn phòng "Ma".
**✅ Đề xuất Test Case:**
- Gửi GET/POST vào `roomId` của một phòng có `isDeleted = true`.
- **Mong đợi:** Trả về HTTP 404 Not Found (Bảo vệ thông tin phòng đã xóa).

## 9. IDOR (Insecure Direct Object Reference) của Manager
**⚠️ Tại sao nguy hiểm:** Nếu hệ thống có cấp quyền cho Manager (Quản lý cấp cơ sở) sửa phòng. Manager của Cơ sở A có thể đoán được `roomId` thuộc Cơ sở B (do đối thủ quản lý), rồi gọi POST qua Postman để phá hoại giá phòng (giảm giá xuống 1 VNĐ). 
**✅ Đề xuất Test Case:**
- Mock `Session` là Manager của Facility A. Submit POST update tới `Room` thuộc Facility B.
- **Mong đợi:** HTTP 403 Forbidden hoặc ném lỗi "Bạn không có quyền quản lý phòng này".

## 10. Type Confusion (Ký tự khoa học - Scientific Notation)
**⚠️ Tại sao nguy hiểm:** Nếu user ngứa tay nhập vào giá phòng `1E6` (Ký hiệu khoa học của 1 triệu). Hàm `new BigDecimal("1E6")` của Java vẫn parse thành công (không văng exception). Nhưng nếu hiển thị ra màn hình không qua format, UI sẽ hiện chữ `1E6 VNĐ` cực kỳ khó hiểu cho người dùng cuối, hoặc bị lỗi khi lưu xuống DB nếu ORM không map chuẩn.
**✅ Đề xuất Test Case:**
- Gửi POST với `roomFee = "1E6"`.
- **Mong đợi:** Hệ thống hoặc phải cấm định dạng này bằng Regex, hoặc phải ép nó về PlainString `1000000` trước khi lưu và hiển thị.
