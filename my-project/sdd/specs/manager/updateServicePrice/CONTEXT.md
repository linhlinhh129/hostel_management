# CONTEXT.md [Quản lý khoản phí và giá dịch vụ]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

* **Hệ lụy sai lệch hóa đơn khi chậm cập nhật giá**: Các mức giá như giá điện, giá nước và phí dịch vụ biến động liên tục theo từng thời kỳ vận hành[cite: 8]. Nếu hệ thống không cập nhật kịp thời, các hóa đơn và công nợ phát sinh của cư dân sẽ bị tính toán sai, làm mất tính minh bạch tài chính và gây khiếu nại[cite: 8].
* **Nguy cơ nhầm lẫn dữ liệu giữa các cơ sở**: Mỗi Ban quản lý chỉ phụ trách một hoặc một vài cơ sở nhất định[cite: 8]. Nếu hệ thống không phân tách bộ lọc dữ liệu chặt chẽ, quản lý có thể nhìn thấy hoặc sửa đổi nhầm giá dịch vụ của cơ sở khác không thuộc quyền hạn của mình[cite: 8].
* **Thất thoát dấu vết kiểm toán khi điều chỉnh giá**: Khi có sự thay đổi về đơn giá tài chính, nếu không lưu trữ lại dữ liệu lịch sử (ai sửa, sửa lúc nào, lý do gì), Ban quản lý và Chủ cơ sở sẽ không có căn cứ đối chiếu khi xảy ra tranh chấp số liệu[cite: 8].
* **Hành vi gửi yêu cầu trùng lặp (Double Submitting)**: Trong quá trình xử lý lưu giá mới, nếu mạng chập chờn hoặc hệ thống phản hồi chậm, người dùng có xu hướng bấm liên tiếp vào nút lưu, dễ gây ra xung đột dữ liệu hoặc tạo ra các bản ghi lịch sử trùng lặp trong database[cite: 8].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

* **Cơ sở lưu trữ giá nền tảng**: Các thông số đơn giá dịch vụ hiện tại không nằm ở một bảng cấu hình riêng mà được lưu trực tiếp trong bảng `facilities`[cite: 8].
* **Loại giá hợp lệ (Price Type)**: Hệ thống giới hạn nghiêm ngặt 3 loại danh mục phí cố định gồm: `ELECTRICITY` (Điện), `WATER` (Nước), và `SERVICE_FEE` (Phí dịch vụ)[cite: 8]. Hệ thống không hỗ trợ tạo mới loại khoản phí động[cite: 8].
* **Quy tắc hiệu lực của giá mới**: Giá dịch vụ sau khi cập nhật thành công chỉ được áp dụng cho các hóa đơn được tạo *sau thời điểm cập nhật*[cite: 8]. 
* **Tính đóng băng của hóa đơn cũ**: Quy định bất thành văn là toàn bộ hóa đơn đã được tạo từ trước thời điểm thay đổi giá phải được giữ nguyên tiền, hệ thống tuyệt đối không tự động tính toán lại dữ liệu quá khứ[cite: 8].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

* **Ban quản lý (Manager)**: Người trực tiếp theo dõi, thực hiện thao tác cập nhật đơn giá qua pop-up và tra cứu lịch sử biến động giá của cơ sở phụ trách[cite: 8].
* **Cư dân / Người thuê**: Đối tượng chịu ảnh hưởng gián tiếp về mặt chi phí, được đảm bảo tính minh bạch khi hóa đơn hàng kỳ sử dụng đúng mức giá niêm yết[cite: 8].
* **Chủ cơ sở / Admin**: Thụ hưởng dữ liệu kiểm toán chính xác, kiểm soát được dòng tiền và lịch sử thay đổi của các quản lý cấp dưới[cite: 8].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

* **Cấu trúc Servlet & Rendering**: Module bắt buộc phải chạy qua Servlet tập trung `ServicePricePageServlet` với URL pattern cố định `/manager/service-prices` và sử dụng cơ chế render Server-side (JSP)[cite: 8].
* **Ràng buộc an toàn dữ liệu (Transaction)**: Thao tác cập nhật đơn giá bắt buộc phải đi kèm đồng thời hai hành động: ghi nhận giá mới vào bảng `facilities` và sinh một bản ghi log vào bảng lịch sử thay đổi trong cùng một Database Transaction[cite: 8].
* **Kiểm soát giá trị nhập liệu**: Hệ thống nghiêm cấm cập nhật giá nhỏ hơn hoặc bằng 0, giá trống hoặc sai định dạng số[cite: 8].
* **Chặn gửi yêu cầu liên tiếp**: Hệ thống bắt buộc phải vô hiệu hóa (disable) nút lưu trên pop-up ngay khi nhận được lệnh submit để ngăn chặn spam request từ cùng một phiên làm việc[cite: 8].
* **Hiệu năng hệ thống (SLA)**: 
  * Tác vụ load hiển thị danh sách và lịch sử phân trang không được vượt quá **1 giây (P95)**[cite: 8].
  * Tác vụ submit form cập nhật thay đổi giá không được vượt quá **500ms (P95)**[cite: 8].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

* **Giả định 1**: Giả định rằng thông tin định danh của người dùng (`currentUser`) và vai trò (`MANAGER`) luôn được lưu trữ sẵn sàng trong Session để Servlet thực hiện ghi vết lịch sử và phân quyền[cite: 8]. *Rủi ro nếu sai:* Hệ thống sẽ không xác định được ai là người thực hiện đổi giá, dẫn đến việc ghi log lịch sử bị trống thông tin tác giả.
* **Giả định 2**: Giả định rằng cấu trúc bảng `facilities` hiện tại đã có sẵn các trường tương ứng cho Điện, Nước, Phí dịch vụ cho từng cơ sở riêng biệt mà không bị gộp chung toàn hệ thống[cite: 8].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

* **Câu hỏi 1**: Hệ thống có cơ chế phân trang cụ thể như thế nào đối với màn hình xem lịch sử thay đổi (`history.jsp`)[cite: 8]? Kích thước dữ liệu mặc định của một trang (Page size) là bao nhiêu bản ghi?
* **Câu hỏi 2**: Khi một Ban quản lý bị thu hồi quyền quản lý một cơ sở (`FACILITY_ACCESS_DENIED`), các bản ghi lịch sử do tài khoản đó từng cập nhật trong quá khứ có được giữ nguyên tên hiển thị hay sẽ ẩn đi[cite: 8]?
* **Câu hỏi 3**: Đối với phần nhập "Ghi chú / Lý do thay đổi" trên giao diện pop-up[cite: 8], trường này có bắt buộc Ban quản lý phải nhập hay được phép để trống khi lưu giá mới?