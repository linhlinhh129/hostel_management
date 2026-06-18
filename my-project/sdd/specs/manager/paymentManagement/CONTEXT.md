# Người viết: Bùi Đỉnh | Ngày: 2026-06-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

Trong công tác quản lý tài chính nhà trọ, Ban quản lý gặp nhiều khó khăn và "nỗi đau" lớn trong việc xác thực dòng tiền:
- **Rủi ro thất thoát tài chính và gian lận:** Người thuê thực hiện chuyển khoản tự do nhưng Ban quản lý không có công cụ tập trung để đối soát[cite: 8]. Nếu không có bằng chứng đối chiếu rõ ràng (ảnh chụp màn hình giao dịch), rất dễ xảy ra tình trạng người thuê báo ảo hoặc nhầm lẫn giữa các phòng[cite: 8].
- **Cập nhật dữ liệu thủ công, chậm trễ:** Khi người thuê đã đóng tiền, trạng thái công nợ không được cập nhật kịp thời, dẫn đến việc Ban quản lý tiếp tục giục nợ nhầm, gây phiền hà cho cư dân và làm giảm tính chuyên nghiệp[cite: 8].
- **Thiếu minh bạch và dấu vết kiểm toán:** Quá trình xác nhận thu tiền nội bộ nếu chỉ trao đổi miệng hoặc qua chat group sẽ không lưu lại được ai là người đã duyệt, duyệt vào thời gian nào, gây khó khăn khi xảy ra thất thoát hoặc cần đối chiếu số liệu[cite: 8].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

- **Giao dịch thanh toán (Payment Transaction):** Bản ghi ghi nhận một lần nộp tiền của người thuê phòng cho một hoặc nhiều khoản chi phí trong hệ thống[cite: 8].
- **Ảnh xác nhận thanh toán (Payment Proof Image):** Bằng chứng chuyển khoản (thường là ảnh chụp màn hình bill ngân hàng) do người thuê tải lên để chứng minh đã thanh toán[cite: 8]. Đây là điều kiện tiên quyết, bắt buộc phải tồn tại để Ban quản lý tiến hành duyệt[cite: 8].
- **Trạng thái giao dịch:**
  - `PENDING`: Giao dịch mới được tạo, đang chờ Ban quản lý kiểm tra bill và phê duyệt[cite: 8].
  - `PAID`: Giao dịch đã được Ban quản lý xác nhận khớp tiền thành công[cite: 8].
- **Nguyên tắc đồng bộ công nợ:** Một quy tắc nghiệp vụ cốt lõi của module này: Ngay khi giao dịch thanh toán được chuyển sang trạng thái `PAID`, hệ thống bắt buộc phải tự động cập nhật trạng thái của khoản công nợ (Debt) liên quan sang `PAID`[cite: 8].
- **Tính đóng đóng của giao dịch:** Giao dịch một khi đã được duyệt (`PAID`) thì không được phép chỉnh sửa, hủy bỏ hoặc thay đổi ảnh xác nhận[cite: 8].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

- **Ban quản lý (Management Board):** Người vận hành và có toàn quyền kiểm tra danh sách, đối chiếu ảnh xác nhận và ra quyết định duyệt giao dịch trên hệ thống[cite: 8].
- **Người thuê phòng (Tenants):** Người chịu ảnh hưởng trực tiếp (cần tải ảnh chứng minh và chờ Ban quản lý duyệt để được xóa nợ)[cite: 8].
- **Kế toán / Chủ nhà trọ:** Người được lợi từ luồng dữ liệu minh bạch, có Audit Log rõ ràng để kiểm soát dòng tiền[cite: 8].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

- **Ràng buộc phân quyền tài chính:** Chức năng này **chỉ** dành riêng cho vai trò `Management Board`[cite: 8]. Hệ thống bắt buộc từ chối thao tác và trả về `401` (chưa đăng nhập) hoặc `403` (sai role) ở tầng Backend[cite: 8].
- **Ràng buộc trạng thái duyệt:** Hệ thống chỉ cho phép duyệt (`approve`) đối với các giao dịch đang ở trạng thái `PENDING`[cite: 8]. Nếu giao dịch đã duyệt rồi, bắt buộc phải chặn và trả về lỗi `PAYMENT_ALREADY_APPROVED`[cite: 8].
- **Ràng buộc toàn vẹn dữ liệu (Data Integrity):** Mỗi giao dịch bắt buộc phải liên kết trực tiếp với một khoản công nợ hợp lệ và một người thuê hợp lệ tồn tại trong hệ thống[cite: 8].
- **Hiệu năng hệ thống (Performance p95):**
  - API lấy danh sách giao dịch và API duyệt giao dịch phải phản hồi < 1 giây[cite: 8].
  - API xem chi tiết giao dịch (chứa link ảnh proof) phải phản hồi < 500ms[cite: 8].
- **An toàn hệ thống:** Áp dụng Rate limit chặt chẽ ở mức 100 requests/phút/người dùng[cite: 8].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

- **Giả định 1:** Giả định rằng luồng tải lên ảnh xác nhận thanh toán (`paymentProofUrl`) đã được xử lý hoàn tất ở phía người thuê phòng, và module này chỉ chịu trách nhiệm đọc/hiển thị url ảnh đó[cite: 8].
- **Giả định 2:** Giả định rằng số tiền người thuê chuyển khoản (`amount`) luôn luôn khớp 100% với giá trị của khoản công nợ liên quan, vì hệ thống hiện tại chưa đề cập đến luồng xử lý khi số tiền trên bill lệch so với công nợ[cite: 8].
- **Giả định 3:** Giả định rằng định danh người duyệt (`approvedBy`) là một ID số nguyên (đại diện cho tài khoản Ban quản lý) lấy từ token đăng nhập[cite: 8].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

- **Câu hỏi 1:** Trong trường hợp Ban quản lý kiểm tra ảnh xác nhận thanh toán và phát hiện bill giả hoặc sai thông tin, hệ thống có cần chức năng "Từ chối" (`Reject`) giao dịch và yêu cầu người thuê tải lại ảnh khác không? Hiện tại spec mới chỉ có luồng Duyệt thành công (`Approve`)[cite: 8].
- **Câu hỏi 2:** Một khoản công nợ có thể được thanh toán bằng nhiều giao dịch nhỏ hay không, hay bắt buộc một giao dịch thanh toán phải bao trọn toàn bộ một khoản công nợ[cite: 8]?
- **Câu hỏi 3:** Định dạng ảnh xác nhận thanh toán được hệ thống chấp nhận là gì (PNG, JPG) và có cần giới hạn dung lượng ảnh hiển thị để đảm bảo tốc độ tải trang detail < 500ms không[cite: 8]?