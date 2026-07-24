# 10 Edge Cases Cực Kỳ Nguy Hiểm: Cập Nhật Chỉ Số Điện Nước (UpdateMeterReading)

Trong quá trình triển khai Servlet `UpdateMeterReadingServlet`, các Dev thường tập trung vào Happy Path mà bỏ quên các lỗ hổng logic/kỹ thuật sau đây có thể làm vỡ tính toàn vẹn của báo cáo doanh thu:

## 1. Âm mưu gian lận: Chỉ số "cưa" lùi qua tháng (Integer Overflow)
**⚠️ Tại sao nguy hiểm:** Một số phòng tiêu thụ quá lớn, vượt quá giới hạn số nguyên Integer (rất hiếm nhưng có thể xảy ra ở hệ thống công nghiệp). Nếu nhập số quá lớn, biến sẽ bị tràn (Integer Overflow) thành số âm. Lệnh kiểm tra `newElectric < prevElectric` có thể bị đánh lừa hoặc tính ra số tiền âm.
**✅ Đề xuất Test Case:** Gửi `newElectric = 2147483647` rồi gửi tiếp `2147483648` (hoặc số vượt MAX_INT) để xem Java parse thành ngoại lệ `NumberFormatException` an toàn không.

## 2. Mã phòng (RoomCode) có khoảng trắng ẩn
**⚠️ Tại sao nguy hiểm:** Lập trình viên thiết kế giao diện nhưng Operator vô tình nhập `roomCode = " P101 "`. Khi so sánh Database, hàm `getReadingBeforeCurrentMonth` sẽ không tìm thấy phòng này vì dư khoảng trắng, văng ra 404 (Room Not Found) gây bực bội cho người dùng.
**✅ Đề xuất Test Case:** Truyền `roomCode = "  P101  "`, Servlet bắt buộc phải dùng `.trim()` trước khi query.

## 3. Chèn mã độc (Path Traversal) vào tên file
**⚠️ Tại sao nguy hiểm:** Hàm `getFileName(part)` tách tên file từ Header `content-disposition`. Hacker có thể chặn bắt Request và sửa tên file thành `../../../../../windows/system32/cmd.exe` hoặc `.jsp`. Nếu Server lưu nguyên tên, nó sẽ chèn mã độc vào gốc máy chủ.
**✅ Đề xuất Test Case:** Gửi file có tên chứa ký tự thư mục `../` và đảm bảo Servlet đổi tên thành UUID độc lập.

## 4. Thiếu dữ liệu ảnh nhưng Size vẫn lọt lưới
**⚠️ Tại sao nguy hiểm:** Nếu Frontend truyền `electricMeterImage` là rỗng, đôi khi thư viện Servlet vẫn tạo đối tượng `Part` nhưng có `size = 0`. Nếu lập trình viên chỉ check `if (electricPart == null)`, code vẫn pass, kết quả là lưu 1 file rỗng 0 bytes lên Server, rác hệ thống.
**✅ Đề xuất Test Case:** Gửi Multipart request có field ảnh nhưng `size = 0`. Đảm bảo bị chặn lại với lỗi `ELECTRIC_METER_IMAGE_REQUIRED`.

## 5. Cập nhật "Lần đầu tiên" của phòng mới xây
**⚠️ Tại sao nguy hiểm:** Phòng mới xây, tháng này mới bắt đầu ghi điện. `getReadingBeforeCurrentMonth()` trả về NULL. Nếu không xử lý, code sẽ văng `NullPointerException` khi gọi `previousReading.getRoomId()`. Nếu chặn luôn báo "Phòng không tồn tại" thì lại không đúng nghiệp vụ.
**✅ Đề xuất Test Case:** Mock `getReadingBeforeCurrentMonth` trả về null, nhưng `getPreviousReadingByRoomCode` trả về DTO. Phải set điện nước cũ về 0 và cho qua.

## 6. Null Pointer ở currentUser 
**⚠️ Tại sao nguy hiểm:** Đoạn code `UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser"); operatorId = currentUser.getId();` nếu session timeout, `currentUser` có thể bị null. Mặc dù đầu Servlet kiểm tra, nhưng nếu có lỗ hổng bypass, sẽ văng lỗi 500 trắng trang thay vì đưa về login.
**✅ Đề xuất Test Case:** Xóa `currentUser` khỏi Session, xem Servlet có try-catch báo lỗi 500 mềm không.

## 7. Submit liên thanh (Double Click) tạo ra hàng loạt bản nháp
**⚠️ Tại sao nguy hiểm:** Mạng lag, Operator ấn nút Cập nhật 3 lần. Nếu `checkCurrentMonthReadingExists` chạy chậm (Race Condition), hệ thống sẽ tạo ra 3 bản ghi (Insert) cho cùng 1 tháng của 1 phòng, làm hỏng hoàn toàn logic tính tiền cuối tháng.
**✅ Đề xuất Test Case:** Dùng `ExecutorService` đẩy 2 Request Insert cùng lúc. Mong đợi DB khóa (Unique Constraint roomId + month + year) hoặc Service tự văng Exception.

## 8. Lừa hệ thống bằng file `.txt` giả `.jpg`
**⚠️ Tại sao nguy hiểm:** Đính kèm file có tên `anh.jpg` nhưng Content-Type của file lại là `text/plain`. Servlet lưu xuống bình thường. Khi Admin mở trang xem ảnh, trình duyệt sẽ render nó thành văn bản, hoặc nguy hiểm hơn là chạy script nếu bị giả thành `.svg`.
**✅ Đề xuất Test Case:** Cần kiểm tra Mime Type `getContentType().startsWith("image/")`. (Mặc dù Servlet hiện tại chưa làm, nhưng đây là lỗ hổng cần ghi nhận).

## 9. Đột biến độ phân giải (Billion Laughs) ảnh 5MB
**⚠️ Tại sao nguy hiểm:** Kẻ tấn công tạo 1 file Zip-bomb định dạng PNG (1 hình ảnh 50,000 x 50,000 pixels nhưng nén lại chỉ 1MB). Server lưu thành công 1MB. Nếu sau này có Module Java Image Resize đụng vào file này, nó sẽ xả nén vào RAM gây OutOfMemory (OOM) làm sập toàn bộ Tomcat.
**✅ Đề xuất Test Case:** Vượt ngoài phạm vi Unit Test, nhưng phải cảnh báo.

## 10. IDOR phòng không được phân công
**⚠️ Tại sao nguy hiểm:** Vận hành viên của Tòa A chỉ được cập nhật Tòa A. Nếu họ cố tình truyền `roomCode = B101` (thuộc Tòa B), và Servlet không check `facility_id` của họ, họ sẽ sửa được điện nước của Tòa khác.
**✅ Đề xuất Test Case:** Code hiện tại chưa có truy vấn check Facility của User. Cần log lại rủi ro này!
