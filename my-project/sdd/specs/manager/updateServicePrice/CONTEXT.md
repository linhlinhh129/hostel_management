# Người viết: Bùi Đỉnh | Ngày: 2026-06-13

## 1. PROBLEM STATEMENT
Trong quá trình vận hành và quản lý một hoặc nhiều tòa nhà/chung cư, Ban quản lý liên tục đối mặt với những khó khăn lớn liên quan đến việc kiểm soát danh mục chi phí:
- **Rủi ro tính sai hóa đơn và công nợ:** Đơn giá các dịch vụ thiết yếu như điện, nước thường xuyên biến động theo thị trường hoặc quy định mới. Khi việc cập nhật các mức giá này trên hệ thống bị chậm trễ hoặc thực hiện thủ công, toàn bộ hóa đơn và công nợ của cư dân trong kỳ đó sẽ bị tính toán sai lệch.
- **Mất uy tín và minh bạch tài chính:** Việc tính sai chi phí dẫn đến các khiếu nại kéo dài từ phía cư dân, gây căng thẳng trong mối quan hệ giữa cư dân và Ban quản lý, đồng thời làm giảm tính minh bạch của hoạt động tài chính.
- **Thiếu linh hoạt khi phát sinh chi phí mới:** Trong thực tế vận hành, Ban quản lý thường xuyên cần thu thêm các khoản phí phát sinh tùy chỉnh (như phí gửi xe phát sinh, phí vệ sinh đột xuất, bảo trì định kỳ...). Nếu hệ thống cứng nhắc không cho phép tạo mới hoặc điều chỉnh linh hoạt danh mục này, Ban quản lý sẽ rơi vào tình trạng thất thoát nguồn thu hoặc phải quản lý ngoài hệ thống bằng Excel rất phân mảnh.
- **Thất lạc dấu vết lịch sử thay đổi:** Khi có sự cố sai lệch số liệu, việc không biết ai đã thay đổi giá dịch vụ, thay đổi vào thời điểm nào và mức giá cũ là bao nhiêu khiến công tác hậu kiểm và giải trình tài chính trở nên bế tắc.

## 2. DOMAIN KNOWLEDGE
- **Khoản phí (Service Fee):** Danh mục các loại chi phí tùy chỉnh do Ban quản lý tạo thêm phục vụ việc thu tiền cư dân (Ví dụ: Phí gửi xe, phí vệ sinh, phí bảo trì...).
- **Giá dịch vụ (Service Price):** Đơn giá áp dụng cho các loại dịch vụ hệ thống mặc định sẵn bao gồm: Điện (`ELECTRICITY`), Nước (`WATER`), và Phí dịch vụ chung (`SERVICE`).
- **Nguyên tắc không hồi tố hóa đơn (Bất thành văn):** Mọi sự thay đổi về giá dịch vụ hoặc khoản phí chỉ có hiệu lực và áp dụng cho các hóa đơn được phát hành *sau* thời điểm cập nhật. Các hóa đơn đã phát hành trước đó hoàn toàn không bị ảnh hưởng để đảm bảo tính toàn vẹn của dữ liệu tài chính quá khứ.
- **Idempotency (Ngăn gửi trùng yêu cầu):** Khi hệ thống đang xử lý một request cập nhật hoặc tạo mới, toàn bộ các request trùng lặp gửi liên tiếp từ cùng một phiên làm việc phải bị chặn lại để tránh tạo dữ liệu rác hoặc sai lệch bản ghi lịch sử.

## 3. STAKEHOLDERS
- **Ban quản lý (Management Board):** Người dùng trực tiếp thao tác, có nhu cầu cập nhật đơn giá, tạo khoản phí mới nhanh chóng để đảm bảo hệ thống tính đúng hóa đơn kỳ tới.
- **Cư dân / Người thuê phòng (Residents/Tenants):** Đối tượng chịu ảnh hưởng trực tiếp về mặt tài chính. Họ cần nhận được hóa đơn tính chính xác theo đúng đơn giá mới nhất được công bố.
- **Bộ phận Kế toán / Chủ đầu tư:** Đối tượng kiểm soát, được lợi từ việc lưu vết lịch sử thay đổi đơn giá rõ ràng phục vụ công tác đối soát doanh thu.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
- **Ràng buộc phân quyền hệ thống:** Mặc dù User Stories ghi nhận nhu cầu của Ban quản lý, ràng buộc kỹ thuật của hệ thống quy định chặt chẽ: Chỉ người dùng có vai trò **Quản lý tài chính (Finance Manager)** mới được cấp quyền thực hiện các API tạo và điều chỉnh giá dịch vụ này. Hệ thống phải trả về lỗi `UNAUTHORIZED` (HTTP 401) nếu sai quyền hạn.
- **Ràng buộc logic dữ liệu (Data Logic Constraints):**
  - Giá trị của mọi khoản phí hoặc giá dịch vụ bắt buộc phải lớn hơn 0 ($> 0$). Giá $\le 0$ phải bị hệ thống từ chối và trả về mã lỗi `INVALID_PRICE`.
  - Tên khoản phí mới (`feeName`) là duy nhất, tuyệt đối không được trùng với khoản phí đã tồn tại trong danh mục hệ thống (`DUPLICATE_SERVICE_FEE`).
- **Ràng buộc lưu vết (Audit Trail):** Mọi hành động thêm mới hoặc cập nhật đơn giá đều bắt buộc phải ghi nhận vào lịch sử hệ thống bao gồm: giá cũ, giá mới, thời gian thay đổi và định danh người thực hiện.
- **Hiệu năng hệ thống (Performance Constraint - p95):** Thời gian phản hồi tối đa của toàn bộ các API thuộc module này phải dưới 500ms.
- **Tải hệ thống (Rate Limit):** Giới hạn tối đa 100 requests/phút trên mỗi người dùng để đảm bảo an toàn hạ tầng.

## 5. ASSUMPTIONS (giả định cần confirm)
- **Giả định 1:** Giả định rằng hệ thống tính hóa đơn tự động (hoặc module lập hóa đơn) sẽ tự động lấy đơn giá mới nhất từ bảng cấu hình của module này ngay khi đến kỳ chạy hóa đơn mà không cần bất kỳ thao tác đồng bộ thủ công nào khác.
- **Giả định 2:** Giả định rằng danh mục 3 dịch vụ mặc định (`ELECTRICITY`, `WATER`, `SERVICE`) là cố định và hệ thống không cho phép xóa bỏ hoặc thay đổi mã định danh (enum) của chúng.
- **Giả định 3:** Giả định rằng "Phí dịch vụ chung" (`SERVICE` trong enum dịch vụ mặc định) được tính theo một đơn giá cố định trên đầu phòng hoặc diện tích, chứ không phải là loại phí biến đổi phức tạp theo từng trường hợp cụ thể.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
- **Câu hỏi 1:** Có sự không nhất quán giữa tài liệu nghiệp vụ và thiết kế kỹ thuật: User Stories ghi vai trò thực hiện là *Ban quản lý*, nhưng phần ràng buộc kỹ thuật (`Technical Constraints`) lại ghi chỉ có role *Quản lý tài chính* mới được phép thực hiện chức năng. Cần làm rõ chính xác role nào sẽ có quyền gọi các API này, hay cả hai role đều được?
- **Câu hỏi 2:** Đơn vị tính (Unit) của các khoản phí tùy chỉnh (ví dụ: Phí gửi xe tính theo "VND/Xe/Tháng", Phí vệ sinh tính theo "VND/Phòng/Tháng") sẽ được quản lý như thế nào? Cần bổ sung thêm trường dữ liệu `unit` trong API tạo khoản phí mới hay không?
- **Câu hỏi 3:** Hệ thống có cần cơ chế lập lịch thay đổi giá trong tương lai không? (Ví dụ: Thiết lập đơn giá nước mới áp dụng kể từ ngày 01 của tháng sau, còn tháng này vẫn tính giá cũ). Hiện tại spec chỉ hỗ trợ cập nhật và có hiệu lực ngay lập tức.