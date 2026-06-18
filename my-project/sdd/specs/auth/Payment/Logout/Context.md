# CONTEXT.md Feature Đăng xuất
# Người viết: @Phạm Anh Tú | Ngày: 2026-06-11

## 1. PROBLEM STATEMENT
* **Nguy cơ rò rỉ dữ liệu trên thiết bị dùng chung:** Người dùng thường xuyên đăng nhập trên các máy tính công cộng hoặc thiết bị mượn. Nếu không có cơ chế thoát an toàn và triệt để, phiên đăng nhập có thể bị kẻ gian lợi dụng để truy cập trái phép vào dữ liệu cá nhân.
* **Trạng thái "treo" (Dangling Session) ở máy khách:** Nếu chỉ xóa token ở server mà không dọn sạch dữ liệu tạm (cache, state) trên trình duyệt, các thông tin nhạy cảm vẫn có thể hiển thị hoặc gây lỗi sai lệch luồng nghiệp vụ ở lần đăng nhập của người khác.

## 2. DOMAIN KNOWLEDGE
* **Token Blacklist / Token Revocation:** Cơ chế lưu trữ các Refresh Token đã bị thu hồi (đã đăng xuất) vào danh sách đen ở phía server, đảm bảo kẻ gian không thể dùng token cũ để xin cấp lại Access Token mới.
* **Client State (Trạng thái máy khách):** Toàn bộ dữ liệu của người dùng đang được lưu trữ ở trình duyệt (như `localStorage`, `sessionStorage`, Cookies, hoặc các Global Store như Redux/Zustand) cần được dọn dẹp sạch sẽ (purge) khi phiên làm việc kết thúc.
* **HttpOnly Cookie (Nếu có):** Loại cookie bảo mật chỉ có thể đọc/ghi bởi server. Để xóa loại cookie này khi đăng xuất, backend bắt buộc phải trả về header `Set-Cookie` với tham số `Max-Age=0`.

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Cần một trải nghiệm thoát mượt mà, nhanh chóng, không bị kẹt lại màn hình vì những lỗi kỹ thuật (như token hết hạn).
* **Đội ngũ Frontend:** Chịu trách nhiệm vô hiệu hóa UI (nút bấm), gọi API và quét sạch (clear) toàn bộ dữ liệu lưu trữ ở trình duyệt.
* **Đội ngũ Backend:** Xử lý logic đưa token vào danh sách đen (Blacklist) và xử lý dọn dẹp Cookie (nếu dùng Cookie thay cho bộ nhớ nội bộ).

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * Backend **bắt buộc** phải có cơ chế thu hồi (revoke) hoặc Blacklist đối với Refresh Token.
  * Phía Frontend **bắt buộc** phải xóa trắng các state lưu trữ thông tin nhạy cảm.
  * Thời gian phản hồi của API `/api/v1/auth/logout` không được vượt quá 300ms (p95) để tránh làm treo giao diện.
  * Trong trường hợp API trả về lỗi do hết hạn (401), Frontend vẫn **bắt buộc** phải thực hiện luồng dọn dẹp state và đá người dùng về trang Đăng nhập.
* **Ràng buộc Phạm vi (Scope Constraints):**
  * Tính năng "Đăng xuất khỏi tất cả các thiết bị khác" (Force logout all devices) không nằm trong phạm vi của module này.

## 5. ASSUMPTIONS
* Giả định rằng hệ thống quản lý JWT ở backend đã có sẵn một module hoặc dịch vụ (như Redis) để lưu trữ danh sách Token Blacklist với hiệu năng cao, đủ để đáp ứng constraint 300ms.
* Giả định rằng nếu kiến trúc đang dùng HttpOnly Cookie, thì backend và frontend đã thống nhất được chính sách CORS và Domain để đảm bảo trình duyệt chấp nhận lệnh xóa Cookie (`Max-Age=0`) từ server trả về.

## 6. OPEN QUESTIONS
* **Xử lý lỗi hệ thống:** Nếu gọi API đăng xuất mà server trả về lỗi `500 Internal Server Error` (lỗi server, không phải lỗi 401 do hết hạn token), thì Frontend có nên tiếp tục tự động xóa local state và chuyển về trang Login không, hay hiện thông báo lỗi và yêu cầu thử lại?
* **Dọn dẹp Analytics/Tracking:** Có cần thiết phải gửi một event tracking (ví dụ: `user_logged_out`) lên các nền tảng phân tích (Google Analytics, Mixpanel...) trước khi chính thức xóa state hay không?