# CONTEXT.md [Quản lý công nợ]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

* **Khó khăn trong việc theo dõi dòng tiền chưa thu hồi**: Ban quản lý gặp trở ngại khi muốn có một góc nhìn tập trung về tất cả các khoản tiền người thuê chưa thanh toán hoặc đã quá hạn từ nhiều phòng và cơ sở khác nhau[cite: 3].
* **Tốn thời gian liên lạc và định vị nguồn nợ**: Khi phát sinh nợ, Ban quản lý mất nhiều công sức để tra cứu thủ công xem khoản nợ đó thuộc về phòng nào, cơ sở nào và ai là người chịu trách nhiệm thanh toán để tiến hành nhắc nhợ[cite: 3].
* **Rủi ro tính toán sai lệch mức độ quá hạn**: Ban quản lý khó đánh giá chính xác mức độ nghiêm trọng của từng khoản nợ (nợ bao nhiêu ngày, quá hạn bao lâu) nếu phải tính toán thủ công bằng tay[cite: 3].
* **Thiếu cơ sở tham khảo để phạt chậm nộp**: Không có công cụ tự động tính toán nhanh mức phí phạt chậm nộp làm căn cứ răn đe hoặc thảo luận với người thuê, dẫn đến việc thất thoát nguồn thu hoặc gây tranh cãi do thiếu minh bạch[cite: 3].
* **Dữ liệu phân tán, dễ sai sót**: Khi cần xem chi tiết lý do nợ, Ban quản lý phải lục tìm lại từng danh mục tiền phòng, chỉ số điện, nước, internet gốc của hóa đơn đó, gây mất thời gian và dễ nhầm lẫn[cite: 3].
* **Nguy cơ rò rỉ dữ liệu tài chính**: Dữ liệu công nợ nhạy cảm có nguy cơ bị truy cập trái phép bởi những người dùng chưa đăng nhập hoặc không có thẩm quyền quản lý[cite: 3].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

* **Công nợ (Debt)**: Không phải là một bảng dữ liệu độc lập, mà là tập hợp các hóa đơn trong bảng `invoices` đang ở trạng thái chưa thanh toán hoặc quá hạn[cite: 3].
* **Trạng thái công nợ hợp lệ**:
  * `UNPAID`: Hóa đơn chưa thanh toán[cite: 3].
  * `OVERDUE`: Hóa đơn chưa thanh toán và đã quá hạn so với `due_date`[cite: 3].
* **Tính toán on-the-fly (Thời gian thực)**: Các chỉ số như *Số ngày nợ*, *Số tiền còn nợ*, và *Phí chậm nộp tạm tính* phải được tính toán trực tiếp khi chạy truy vấn, tuyệt đối không được lưu cứng vào database để tránh dư thừa và sai lệch dữ liệu[cite: 3].
* **Thời gian ân hạn chậm nộp**: Quy định bất thành văn là phí chậm nộp chỉ bắt đầu được tạm tính nếu hóa đơn bị nộp muộn quá 03 ngày kể từ ngày đến hạn[cite: 3].
* **Tỷ lệ phạt chậm nộp**: Được tính bằng $1\%$ giá trị tiền phòng/tháng cho mỗi ngày muộn sau thời gian ân hạn[cite: 3].
* **Tính chất của Phí chậm nộp tạm tính**: Chỉ mang giá trị hiển thị tham khảo cho Ban quản lý, hệ thống không tự động cộng dồn vào tổng tiền hóa đơn hay tạo payment tự động[cite: 3].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

* **Ban quản lý (Management Board / MANAGER)**: Đối tượng trực tiếp sử dụng tính năng để theo dõi, tìm kiếm, lọc công nợ, xem chi tiết hóa đơn nợ và chủ động thực hiện hành động nhắc nợ[cite: 3].
* **Người thuê (Tenant)**: Đối tượng chịu ảnh hưởng trực tiếp, bị thúc nợ hoặc chịu phí phạt nếu thanh toán trễ hạn[cite: 3].
* **Chủ cơ sở / Hệ thống**: Được lợi từ việc đảm bảo dòng tiền được thu hồi đúng hạn, giảm thiểu tỷ lệ nợ xấu[cite: 3].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

* **Kiến trúc & Công nghệ**: Phải triển khai theo mô hình Servlet (ví dụ: `DebtPageServlet`) và hiển thị qua trang JSP (như `index.jsp`, `detail.jsp`)[cite: 3].
* **Hiệu năng hệ thống**: Thời gian phản hồi tối đa (`Max response time`) không được vượt quá 500ms (P95) khi tải danh sách hoặc xem chi tiết công nợ[cite: 3].
* **Tần suất yêu cầu (Rate limit)**: Giới hạn tối đa 100 requests/phút/người dùng để bảo vệ tài nguyên hệ thống[cite: 3].
* **Ràng buộc lưu trữ**: Nghiêm cấm tạo bảng công nợ riêng hoặc lưu trữ các trường dữ liệu tính toán thời gian thực (`Số ngày nợ`, `Phí chậm nộp`) vào database[cite: 3].
* **Phân quyền nghiêm ngặt**: Chỉ những người dùng đã đăng nhập và có vai trò là `Management Board` (MANAGER) mới được phép truy cập[cite: 3]. Mọi hành vi sai quyền phải trả về HTTP 403 hoặc điều hướng phù hợp[cite: 3].
* **Bắt buộc phân trang**: Danh sách công nợ bắt buộc phải hỗ trợ phân trang để tối ưu hóa hiệu suất dữ liệu[cite: 3].
* **Nhật ký hệ thống**: Phải ghi nhận `Audit Log` cho các thao tác xem chi tiết công nợ và kích hoạt gửi nhắc nợ thủ công[cite: 3].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

* **Giả định 1**: Hệ thống hiện tại không hỗ trợ thanh toán từng phần, nên tổng tiền đã thanh toán thành công đối với hóa đơn `UNPAID` hoặc `OVERDUE` luôn bằng 0[cite: 3]. *Rủi ro nếu sai:* Nếu có tính năng thanh toán từng phần, công thức tính số tiền còn nợ sẽ cần bổ sung hàm tính tổng `SUM(amount)` từ bảng `payments` một cách phức tạp hơn[cite: 3].
* **Giả định 2**: Giả định rằng mọi hóa đơn quá hạn đều gắn liền với một người thuê (`tenant_id`) còn hoạt động để có thể lấy được thông tin liên hệ (Họ tên, SĐT, Email) từ bảng `users`[cite: 3]. *Rủi ro nếu sai:* Nếu tài khoản người thuê đã bị xóa hoặc vô hiệu hóa trước khi thanh toán xong hóa đơn, hệ thống có thể bị lỗi khi `JOIN` dữ liệu hoặc hiển thị trống thông tin[cite: 3].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

* **Câu hỏi 1**: Nút "Nhắc nợ" thủ công sẽ điều hướng Ban quản lý tới chức năng cụ thể nào? Hệ thống sẽ gửi thông báo qua kênh nào (Email, SMS, thông báo nội bộ ứng dụng Zalo/Viber) hay chỉ hiển thị một form để soạn thảo nội dung[cite: 3]?
* **Câu hỏi 2**: Khi Ban quản lý muốn thu phí chậm nộp và tự nhập vào mục "Khoản phí khác" trong hóa đơn, việc cập nhật này có làm thay đổi trạng thái hóa đơn hay yêu cầu phê duyệt lại từ phía Admin hay không[cite: 3]?
* **Câu hỏi 3**: Đối với việc tính số ngày nợ dựa trên "Ngày hiện tại", hệ thống sẽ sử dụng múi giờ nào (Timezone) của Server để đảm bảo tính nhất quán, tránh việc chênh lệch ngày giờ dẫn đến tính sai phí phạt chậm nộp[cite: 3]?