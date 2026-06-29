# CONTEXT.md Feature Đăng nhập
# Người viết: @Phạm Anh Tú | Ngày: 2026-06-11

## 1. PROBLEM STATEMENT
* **Nguy cơ an toàn thông tin từ tài khoản cấp sẵn:** Người dùng được cấp tài khoản ban đầu thường sử dụng mật khẩu tạm thời có tính bảo mật thấp hoặc dễ bị lộ. Nếu không có cơ chế bắt buộc thay đổi ngay trong lần đầu truy cập, tài khoản cá nhân và tài nguyên nội bộ của hệ thống sẽ đứng trước rủi ro bị xâm nhập trái phép.
* **Nguy cơ bị tấn công dò mật khẩu (Brute-force):** Nếu hệ thống cho phép thử mật khẩu vô hạn lần, kẻ tấn công có thể sử dụng các công cụ tự động để dò tìm thông tin đăng nhập thành công, gây rò rỉ dữ liệu và làm quá tải hệ thống.

## 2. DOMAIN KNOWLEDGE
* **Mật khẩu tạm thời (Temporary Password):** Mật khẩu ban đầu do hệ thống hoặc quản trị viên cấp sẵn cho người dùng mới.
* **Phiên đăng nhập (Session):** Phiên làm việc của người dùng được duy trì trên Server thông qua `HttpSession`, không sử dụng REST API hay JWT.
* **Khóa tài khoản (Account Lockout):** Hành động chuyển trạng thái của người dùng dưới cơ sở dữ liệu thành `LOCKED` khi nhập sai mật khẩu quá số lần quy định.
* **Chuẩn EARS (Easy Approach to Requirements Syntax):** Quy cách viết tiêu chí chấp thuận theo cấu trúc điều kiện rõ ràng (KHI... HỆ THỐNG PHẢI...) để giảm thiểu sự mơ hồ cho đội ngũ phát triển.

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Đối tượng cần truy cập vào hệ thống một cách an toàn và tiện lợi, đồng thời được bảo vệ thông tin cá nhân.
* **Đội ngũ Phát triển (Frontend & Backend Engineers):** Những người chịu trách nhiệm hiện thực hóa giao diện, logic xác thực, API contract và cơ chế bảo mật cho tính năng.
* **Đội ngũ Kiểm thử (QA/Tester):** Cần các kịch bản chuẩn (Happy Path) và ngoại lệ (Edge Cases) rõ ràng để thiết kế test case.
* **Tech Lead / Người duyệt:** Chịu trách nhiệm kiểm tra tính khả thi về mặt kỹ thuật, hiệu năng hệ thống và phê duyệt tài liệu đặc tả.

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * Hệ thống sử dụng Servlet/JSP Form-based authentication, không phải REST API.
  * Cơ chế đếm số lần sai sẽ theo dõi theo từng `username` cụ thể trên bộ nhớ tạm (RAM).
  * Khi tài khoản bị khóa do nhập sai 5 lần, hệ thống bắt buộc phải cập nhật trường `status` dưới Database thành `LOCKED`. Tài khoản sẽ bị vô hiệu hóa hoàn toàn cho đến khi Admin mở khóa.
  * Hệ thống sẽ hiển thị thông báo lỗi trực tiếp trên giao diện JSP bằng thẻ `<c:if>`.
* **Ràng buộc Nghiệp vụ - Chính sách Mật khẩu (Business Constraints - Password Policy):** 
  * **Độ dài:** Tối thiểu 8 ký tự (lưu ý: nhiều hệ thống hiện nay yêu cầu 10–12 ký tự, dự án có thể cân nhắc nâng cấp sau).
  * **Chữ hoa:** Có ít nhất 1 chữ cái viết hoa (A-Z).
  * **Chữ thường:** Có ít nhất 1 chữ cái viết thường (a-z).
  * **Chữ số:** Có ít nhất 1 chữ số (0-9).
  * **Ký tự đặc biệt:** Có ít nhất 1 ký tự đặc biệt (!, @, #, $, %, ^, &, *, v.v.).
  * **Khoảng trắng:** Tuyệt đối không được chứa khoảng trắng (space).
  * **Bảo mật chéo:** Mật khẩu mới không được trùng với tên đăng nhập (username) hoặc email của người dùng.
* **Ràng buộc Phạm vi (Scope Constraints):**
  * **Nằm ngoài phạm vi (Out of Scope):** Tính năng "Quên mật khẩu" (Forgot Password) và "Đăng nhập bằng bên thứ ba" (Google) không được xử lý trong chu kỳ phát triển này. Tính năng "Admin mở khóa tài khoản" cũng nằm ở module Quản lý người dùng.

## 5. ASSUMPTIONS
* Giả định rằng hệ thống sử dụng Servlet Filter hoặc logic tại Controller để tự động điều hướng người dùng có cờ `force_change_pass = true` đến trang đổi mật khẩu, không cho phép họ quay lại trang tổng quan bằng các đường dẫn URL thủ công.
* Giả định rằng dữ liệu tài khoản cấp sẵn trong cơ sở dữ liệu đã đánh dấu sẵn cột `force_change_pass = 1`.

## 6. OPEN QUESTIONS
* **Ghi log bảo mật:** Khi tài khoản bị đổi trạng thái thành `LOCKED`, hệ thống có cần lưu lại IP và thời điểm vào một bảng Log riêng biệt để Audit không, hay chỉ cần ghi file log hệ thống là đủ?
* **Cơ chế thông báo cho Admin:** Khi một tài khoản bị khóa vì sai mật khẩu 5 lần, hệ thống có cần gửi tự động một email hoặc thông báo (Notification) nội bộ cho Admin biết để họ chủ động liên hệ hỗ trợ người dùng không?