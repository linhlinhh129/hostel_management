# CONTEXT.md Feature Đăng xuất
# Người viết: AI Assistant | Ngày: 2026-07-27

## 1. PROBLEM STATEMENT
* **Nguy cơ rò rỉ thông tin từ phiên làm việc cũ:** Nếu người dùng chỉ đóng trình duyệt mà không đăng xuất, hoặc đăng xuất nhưng trình duyệt vẫn lưu cache, người khác sử dụng chung máy tính có thể nhấn nút "Back" (Quay lại) để xem các thông tin nhạy cảm đã truy cập trước đó.
* **Chấm dứt phiên làm việc an toàn:** Cần một cơ chế chính thức để hủy bỏ hoàn toàn phiên làm việc (session) và xóa bỏ các dữ liệu định danh trên bộ nhớ tạm (cookie/session) nhằm bảo vệ tài khoản người dùng khỏi các nguy cơ chiếm đoạt phiên (Session Hijacking).

## 2. DOMAIN KNOWLEDGE
* **Phiên đăng nhập (Session):** Phiên làm việc của người dùng được duy trì trên Server thông qua `HttpSession`.
* **Hủy phiên (Session Invalidation):** Hành động xóa bỏ hoàn toàn đối tượng `HttpSession` hiện tại trên Server, làm cho mọi dữ liệu liên quan đến phiên (như thông tin người dùng đăng nhập) bị xóa sạch.
* **Bộ nhớ đệm của trình duyệt (Browser Caching):** Cơ chế trình duyệt lưu lại các trang HTML đã tải. Để ngăn chặn nút "Back", hệ thống cần sử dụng các Header HTTP đặc biệt (`Cache-Control: no-cache, no-store, must-revalidate`) để ép trình duyệt không lưu cache các trang yêu cầu đăng nhập.

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Muốn có một nút đăng xuất rõ ràng để kết thúc quá trình sử dụng hệ thống một cách an toàn, đặc biệt trên thiết bị công cộng.
* **Đội ngũ Phát triển (Frontend & Backend Engineers):** Chịu trách nhiệm vô hiệu hóa Session ở phía Server, xóa Cookie và cấu hình Filter để chống lưu cache trang.
* **Đội ngũ Kiểm thử (QA/Tester):** Cần kiểm tra kịch bản đăng xuất thành công và kịch bản cố tình nhấn nút "Back" sau khi đăng xuất.
* **Tech Lead / Người duyệt:** Phê duyệt giải pháp chống cache và bảo mật phiên.

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * Hệ thống phải gọi `session.invalidate()` tại endpoint đăng xuất.
  * Hệ thống phải xóa bỏ mọi cookie liên quan đến phiên đăng nhập (như JSESSIONID) nếu cần thiết.
  * Cần áp dụng cơ chế thiết lập HTTP Headers (`Cache-Control`, `Pragma`, `Expires`) trên toàn bộ các trang yêu cầu bảo mật (thường thông qua Servlet Filter) để ngăn trình duyệt lưu lại lịch sử trang.
* **Ràng buộc Nghiệp vụ (Business Constraints):** 
  * Sau khi đăng xuất thành công, người dùng bắt buộc phải được điều hướng (Redirect) về màn hình đăng nhập chính của hệ thống.
  * Không cho phép truy cập lại bất kỳ chức năng nào nếu chưa đăng nhập lại.

## 5. ASSUMPTIONS
* Giả định rằng hệ thống hiện tại đã có một `AuthFilter` (hoặc tương đương) chặn các request yêu cầu đăng nhập. Ta có thể tái sử dụng Filter này để tiêm (inject) các header chống cache.

## 6. OPEN QUESTIONS
* **Ghi log đăng xuất:** Hệ thống có cần ghi nhận thời điểm đăng xuất của người dùng vào bảng Audit Log để theo dõi thời lượng sử dụng hệ thống không?
