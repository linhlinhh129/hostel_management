# CONTEXT.md [Quản lý thanh toán]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

* **Rủi ro thất thoát tài chính và sai lệch công nợ**: Ban quản lý dễ gặp tình trạng thông tin nộp tiền của khách thuê bị ghi nhận sai lệch hoặc không đồng bộ với hóa đơn gốc, gây mất kiểm soát dòng tiền và trạng thái công nợ thực tế trong hệ thống[cite: 6].
* **Mất thời gian đối chiếu thủ công**: Việc kiểm tra, đối soát thông tin giao dịch (số tiền, ngày nộp, người nộp) với các hóa đơn/công nợ liên quan một cách rời rạc làm tốn nhiều công sức của Ban quản lý và dễ phát sinh nhầm lẫn[cite: 6].
* **Hệ lụy từ thao tác sai sót (Ấn nhầm từ chối)**: Khi xử lý lượng lớn yêu cầu, Ban quản lý có thể thao tác nhầm lẫn bấm từ chối một giao dịch hợp lệ. Nếu hệ thống không có cơ chế sửa sai (duyệt lại), Ban quản lý sẽ không có cách nào khắc phục dữ liệu lịch sử nhanh chóng[cite: 6].
* **Nguy cơ can thiệp trái phép vào luồng tiền**: Dữ liệu tài chính, thanh toán nhạy cảm có nguy cơ bị các tài khoản không có thẩm quyền (người dùng chưa đăng nhập hoặc vai trò khác) truy cập và thay đổi trạng thái bừa bãi[cite: 6].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

* **Luồng chuyển đổi trạng thái giao dịch (State Machine)**: 
  * Các giao dịch ở trạng thái khởi tạo `PENDING` hoặc đã bị `REJECTED` đều được phép Duyệt tiến thẳng lên `SUCCESS`[cite: 6].
  * **Quy tắc bất biến**: Giao dịch đã ở trạng thái `SUCCESS` thì đóng băng hoàn toàn, không cho phép Từ chối hoặc hủy bỏ ngược lại dưới bất kỳ hình thức nào[cite: 6].
* **Tính đồng bộ Hóa đơn - Giao dịch**: Khi một giao dịch thanh toán được xác nhận `SUCCESS`, hệ thống có quy định bất thành văn là phải tự động đồng bộ và quét trạng thái hóa đơn/công nợ liên quan trực tiếp sang trạng thái `PAID`[cite: 6].
* **Dấu vết kiểm toán (Audit Footprint)**: Mọi thao tác Duyệt hoặc Từ chối từ phía Ban quản lý bắt buộc phải ghi nhận chính xác định danh người thực hiện (`approvedBy` lấy từ Session) và mốc thời gian thực thi (`approvedAt`)[cite: 6].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

* **Ban quản lý (Management Board / MANAGER)**: Đối tượng vận hành trực tiếp, có quyền hạn theo dõi danh sách, đối chiếu hóa đơn, thực hiện duyệt hoặc từ chối các giao dịch thanh toán[cite: 6].
* **Người thuê (Tenant)**: Người thực hiện chuyển tiền (qua VNPAY hoặc Ngân hàng), chịu ảnh hưởng trực tiếp từ việc giao dịch của mình có được phê duyệt chính xác và kịp thời để xóa nợ hay không[cite: 6].
* **Hệ thống / Chủ cơ sở**: Thụ hưởng luồng quản lý dòng tiền minh bạch, chính xác và giảm thiểu rủi ro tranh chấp tài chính.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

* **Cấu trúc Công nghệ bắt buộc**: Phải được triển khai thông qua cặp Servlet cụ thể: `PaymentServlet` (Xử lý danh sách) và `PaymentDetailServlet` (Xử lý chi tiết/duyệt/từ chối) kết hợp các view JSP chỉ định[cite: 6].
* **Ràng buộc Phân quyền**: Chỉ duy nhất tài khoản mang vai trò Ban quản lý (`MANAGER`) mới được phép truy cập và thực thi các API/URL của module này[cite: 6].
* **Toàn vẹn giao dịch (Database Transaction)**: Thao tác duyệt giao dịch bắt buộc phải đặt trong một Database Transaction duy nhất để đảm bảo tính đồng bộ tuyệt đối (Cùng thành công hoặc cùng thất bại) giữa trạng thái giao dịch (`SUCCESS`) và trạng thái hóa đơn (`PAID`)[cite: 6].
* **Thời gian phản hồi (SLA Hiệu năng)**: 
  * Tác vụ tải danh sách giao dịch (bao gồm chạy bộ lọc và phân trang) không được vượt quá **1 giây (P95)**[cite: 6].
  * Tác vụ xử lý logic bấm Duyệt/Từ chối cập nhật trạng thái không được vượt quá **500ms (P95)**[cite: 6].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

* **Giả định 1**: Giả định rằng hệ thống chỉ xử lý quan hệ $1-1$ hoặc $N-1$ giữa giao dịch và hóa đơn (Một giao dịch thanh toán cho một hoặc một cụm hóa đơn trọn gói)[cite: 6]. *Rủi ro nếu sai:* Nếu một hóa đơn được phép thanh toán nhiều lần bằng nhiều giao dịch nhỏ lẻ (thanh toán từng phần), logic tự động cập nhật hóa đơn sang `PAID` ngay khi một giao dịch `SUCCESS` sẽ làm sai lệch bản chất số tiền còn nợ.
* **Giả định 2**: Giả định rằng dữ liệu giao dịch từ cổng VNPAY hoặc chuyển khoản ngân hàng đã được hệ thống ghi nhận sẵn vào DB dưới trạng thái `PENDING` trước khi Ban quản lý vào đối soát[cite: 6].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

* **Câu hỏi 1**: Chức năng cho phép "Duyệt lại các giao dịch đã bị từ chối trước đó" (Story 5)[cite: 6] có cần giới hạn khoảng thời gian hợp lệ (ví dụ: chỉ được duyệt lại trong vòng 24 giờ kể từ khi bấm từ chối) để tránh việc thay đổi dữ liệu kế toán quá cũ không?
* **Câu hỏi 2**: Khi Ban quản lý bấm "Từ chối giao dịch" (`POST /manager/payments/{id}/reject`)[cite: 6], hệ thống có bắt buộc hiển thị một trường yêu cầu nhập lý do từ chối (Ví dụ: Sai số tiền, sai nội dung) để hiển thị phản hồi cho khách thuê biết hay không?
* **Câu hỏi 3**: Đối với các giao dịch thanh toán qua cổng VNPAY, hệ thống có cơ chế IPN (Instant Payment Notification) tự động cập nhật trạng thái `SUCCESS` hay tất cả mọi giao dịch (bất kể phương thức) đều đang bắt buộc Ban quản lý phải nhấn Duyệt thủ công[cite: 6]?