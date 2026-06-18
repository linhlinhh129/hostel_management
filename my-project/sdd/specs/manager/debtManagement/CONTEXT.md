# Người viết: Bùi Đỉnh | Ngày: 2026-06-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

Trong quá trình vận hành hệ thống nhà trọ/chung cư, Ban quản lý gặp nhiều khó khăn và "nỗi đau" lớn trong việc kiểm soát tài chính[cite: 6]:
- **Thất lạc và phân mảnh thông tin:** Có quá nhiều loại chi phí phát sinh định kỳ và thực tế (tiền phòng, điện, nước, internet, vệ sinh...) khiến việc theo dõi thủ công dễ dẫn đến sai sót hoặc bỏ sót khoản thu[cite: 6].
- **Mất kiểm soát nợ xấu/quá hạn:** Ban quản lý không có cái nhìn tập trung để phát hiện ngay các trường hợp quá hạn (OVERDUE), dẫn đến việc chậm trễ trong khâu thu hồi công nợ và ảnh hưởng trực tiếp đến dòng tiền vận hành[cite: 6].
- **Phối hợp nội bộ kém hiệu quả:** Khi phát hiện công nợ bất thường hoặc cần hỗ trợ từ cấp trên, quy trình luân chuyển thông tin nội bộ chưa được lưu vết, dễ gây hiểu lầm hoặc quên việc trong nội bộ Ban quản lý[cite: 6].
- **Xử lý thủ công phức tạp:** Không có công cụ hỗ trợ tìm kiếm nhanh theo mã phòng hay tên người thuê, gây mất thời gian khi cần tra cứu thông tin khẩn cấp[cite: 6].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

- **Công nợ (Debt Record):** Một khoản tiền bắt buộc phải thu gắn liền với một phòng thuê, một hợp đồng và một người thuê cụ thể[cite: 6].
- **Nguyên tắc "Không thanh toán từng phần":** Một quy tắc nghiệp vụ nghiêm ngặt trong hệ thống này. Công nợ chỉ có một giá trị tổng duy nhất (`totalAmount`), không tồn tại khái niệm "số tiền đã trả" (`paidAmount`) hay "số tiền còn lại" (`remainingAmount`)[cite: 6]. Người thuê bắt buộc phải đóng đủ 100% số tiền một lần[cite: 6].
- **Ma trận trạng thái công nợ:**
  - `PENDING`: Công nợ chưa thanh toán nhưng vẫn nằm trong hạn (Ngày hiện tại <= Hạn thanh toán)[cite: 6].
  - `PAID`: Công nợ đã được xác nhận thanh toán đủ 100%[cite: 6].
  - `OVERDUE`: Công nợ chưa thanh toán và đã vượt quá hạn (Ngày hiện tại > Hạn thanh toán)[cite: 6].
- **Thông báo công nợ nội bộ:** Là hành động gửi thông tin cảnh báo/ghi nhận lên hệ thống giữa các thành viên hoặc cấp bậc trong nội bộ Ban quản lý để phối hợp xử lý trực tiếp với người thuê[cite: 6].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

- **Ban quản lý (Management Board):** Đối tượng trực tiếp sử dụng tính năng, được lợi từ việc nắm bắt dòng tiền, lọc danh sách nợ xấu và lưu vết lịch sử thông báo nội bộ[cite: 6].
- **Người thuê phòng (Tenants):** Chịu ảnh hưởng gián tiếp (phải thanh toán đúng hạn, đúng số tiền), tuy nhiên họ không thao tác trên tính năng này[cite: 6].
- **Ban quản lý cấp trên / Chủ đầu tư:** Nhận thông báo phối hợp để đưa ra quyết định xử lý các ca nợ khó đòi[cite: 6].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

- **Phân quyền nghiêm ngặt (Role-based):** Feature này **chỉ** dành riêng cho tài xế/người dùng có vai trò `Management Board`[cite: 6]. Backend bắt buộc phải check quyền này ở mọi tầng API, không chỉ ẩn trên UI[cite: 6].
- **Ràng buộc dữ liệu (Data Integrity):** Mỗi khoản công nợ bắt buộc phải map khớp với một phòng thuê hợp lệ và một người thuê hợp lệ[cite: 6].
- **Ràng buộc độ dài thông báo:** Tiêu đề thông báo nội bộ giới hạn tối đa 100 ký tự; Nội dung thông báo tối đa 500 ký tự[cite: 6].
- **Hiệu năng hệ thống (Performance p95):** 
  - API lấy danh sách và API gửi thông báo phải phản hồi < 1 giây[cite: 6].
  - API xem chi tiết công nợ phải phản hồi < 500ms[cite: 6].
- **An toàn hệ thống:** Giới hạn Rate limit ở mức 100 requests/phút/người dùng[cite: 6].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

- **Giả định 1:** Giả định rằng hệ thống đã có sẵn một cơ chế/cronjob tự động quét mỗi ngày để chuyển đổi trạng thái từ `PENDING` sang `OVERDUE` khi `current_date > due_date`[cite: 6].
- **Giả định 2:** Giả định rằng việc xác nhận công nợ chuyển sang trạng thái `PAID` sẽ do một module khác (ví dụ: Module Quản lý thanh toán) kích hoạt và cập nhật sang, module này chỉ đọc và hiển thị[cite: 6].
- **Giả định 3:** Giả định rằng toàn bộ các thành viên thuộc `Management Board` đều có quyền hạn ngang nhau trong việc xem và gửi thông báo, không chia cấp bậc chi tiết trên hệ thống[cite: 6].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

- **Câu hỏi 1:** Khi Ban quản lý bấm "Gửi thông báo công nợ", hệ thống gửi đích danh đến một tài khoản cụ thể trong Ban quản lý hay tất cả những ai có role `Management Board` đều sẽ nhận được trong danh sách thông báo?[cite: 6]
- **Câu hỏi 2:** Trạng thái công nợ khi đã chuyển thành `OVERDUE`, nếu sau đó người thuê thanh toán đủ thì trạng thái sẽ chuyển thẳng sang `PAID` hay cần qua một trạng thái trung gian nào khác không?[cite: 6]
- **Câu hỏi 3:** Có cần cơ chế giới hạn tần suất gửi thông báo nội bộ cho cùng một mã công nợ không (ví dụ: tránh việc một công nợ bị bấm gửi thông báo trùng lặp nhiều lần trong ngày)?[cite: 6]