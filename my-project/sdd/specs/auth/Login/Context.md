"""# CONTEXT.md Feature Đăng nhập
# Người viết: @Phạm Anh Tú | Ngày: 2026-06-11

## 1. PROBLEM STATEMENT
* **Nguy cơ an toàn thông tin từ tài khoản cấp sẵn:** Người dùng được cấp tài khoản ban đầu thường sử dụng mật khẩu tạm thời có tính bảo mật thấp hoặc dễ bị lộ. Nếu không có cơ chế bắt buộc thay đổi ngay trong lần đầu truy cập, tài khoản cá nhân và tài nguyên nội bộ của hệ thống sẽ đứng trước rủi ro bị xâm nhập trái phép.
* **Nguy cơ bị tấn công dò mật khẩu (Brute-force):** Nếu hệ thống cho phép thử mật khẩu vô hạn lần, kẻ tấn công có thể sử dụng các công cụ tự động để dò tìm thông tin đăng nhập thành công, gây rò rỉ dữ liệu và làm quá tải hệ thống.

## 2. DOMAIN KNOWLEDGE
* **Mật khẩu tạm thời (Temporary Password):** Mật khẩu ban đầu do hệ thống hoặc quản trị viên cấp sẵn cho người dùng mới.
* **Token giới hạn quyền (Restricted JWT Token):** Chuỗi mã hóa được cấp riêng cho trường hợp đăng nhập bằng mật khẩu tạm thời. Token này có thời hạn ngắn (600 giây) và chỉ có quyền duy nhất là gọi API đổi mật khẩu, bị chặn hoàn toàn khỏi các tài nguyên nghiệp vụ khác.
* **Lockout Timestamp (Mốc thời gian khóa):** Điểm thời gian được ghi nhận lại khi một tài khoản nhập sai mật khẩu quá số lần quy định, dùng làm căn cứ để từ chối mọi yêu cầu đăng nhập từ tài khoản đó trong một khoảng thời gian cố định.
* **Chuẩn EARS (Easy Approach to Requirements Syntax):** Quy cách viết tiêu chí chấp thuận theo cấu trúc điều kiện rõ ràng (KHI... HỆ THỐNG PHẢI...) để giảm thiểu sự mơ hồ cho đội ngũ phát triển.

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Đối tượng cần truy cập vào hệ thống một cách an toàn và tiện lợi, đồng thời được bảo vệ thông tin cá nhân.
* **Đội ngũ Phát triển (Frontend & Backend Engineers):** Những người chịu trách nhiệm hiện thực hóa giao diện, logic xác thực, API contract và cơ chế bảo mật cho tính năng.
* **Đội ngũ Kiểm thử (QA/Tester):** Cần các kịch bản chuẩn (Happy Path) và ngoại lệ (Edge Cases) rõ ràng để thiết kế test case.
* **Tech Lead / Người duyệt:** Chịu trách nhiệm kiểm tra tính khả thi về mặt kỹ thuật, hiệu năng hệ thống và phê duyệt tài liệu đặc tả.

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * Thời gian phản hồi của API đăng nhập tối đa không quá 500ms (ở mức p95).
  * Cơ chế đếm và khóa phải theo dõi theo từng `username` cụ thể.
  * Khi tài khoản bị khóa, hệ thống bắt buộc phải trả về mã HTTP 403 cùng mã nội bộ `LOGIN_DISABLED_1MIN`.
  * Thời gian khóa tạm thời cố định là đúng 60 giây sau 5 lần sai liên tiếp.
* **Ràng buộc Phạm vi (Scope Constraints):**
  * **Nằm ngoài phạm vi (Out of Scope):** Tính năng "Quên mật khẩu" (Forgot Password) và "Đăng nhập bằng bên thứ ba" (Google) không được xử lý trong chu kỳ phát triển này.

## 5. ASSUMPTIONS
* Giả định rằng hệ thống đã có sẵn một phân hệ lưu trữ phân tán (ví dụ: Redis hoặc cơ chế bộ nhớ đệm tương đương) để đếm số lần đăng nhập sai và lưu mốc thời gian khóa nhanh chóng mà không làm ảnh hưởng đến hiệu năng database chính.
* Giả định rằng phía Frontend sẽ luôn tự động bắt gói tin phản hồi có flag `requirePasswordChange: true` để điều hướng người dùng trực tiếp đến trang đổi mật khẩu, không cho phép họ quay lại trang tổng quan bằng các đường dẫn URL thủ công.
* Giả định rằng dữ liệu tài khoản cấp sẵn trong cơ sở dữ liệu đã có một trường đánh dấu trạng thái (ví dụ: `is_first_login` hoặc `password_status`) để backend nhận biết và yêu cầu đổi mật khẩu.

## 6. OPEN QUESTIONS
* **Cơ chế reset lượt sập bẫy khóa:** Sau khi hết thời gian khóa 1 phút, nếu người dùng đăng nhập lại và tiếp tục nhập sai 1 lần nữa, hệ thống sẽ khóa tiếp ngay lập tức hay sẽ cho phép thử lại đủ 5 lần mới khóa tiếp?
* **Thời gian hết hạn của số lần đếm sai:** Số lần nhập sai mật khẩu có bị xóa theo thời gian không? (Ví dụ: Nếu người dùng nhập sai 4 lần vào buổi sáng, đến buổi chiều nhập sai thêm 1 lần nữa thì có bị kích hoạt cơ chế khóa hay không?).
* **Thời hạn của mật khẩu tạm thời:** Mật khẩu tạm được cấp ban đầu có thời hạn hiệu lực tối đa hay không (ví dụ: phải kích hoạt tài khoản trong vòng 7 ngày), hay có giá trị vô hạn cho đến khi người dùng đăng nhập lần đầu?
"""