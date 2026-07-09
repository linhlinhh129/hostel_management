# CONTEXT.md [Bảng tin cộng đồng]

# Người viết: @Ban_Quản_Lý | Ngày: 2026-07-09

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->
* **Nỗi đau của Ban quản lý và Cư dân:**
    * **Thiếu kênh tương tác hai chiều:** Ban quản lý gặp khó khăn trong việc đo lường mức độ quan tâm của cư dân đối với các thông báo chính thống. Quy trình truyền thông cũ mang tính một chiều (chỉ phát thông tin), khiến BQL không nắm bắt được phản hồi, thắc mắc hoặc tâm tư nguyện vọng của cư dân một cách kịp thời.
    * **Nhiễu loạn thông tin cũ:** Cư dân dễ bị quá tải hoặc nhầm lẫn bởi các thông báo lỗi thời, dẫn đến việc bỏ lỡ các tin tức quan trọng, khẩn cấp diễn ra trong ngày hiện tại.
    * **Tương tác phân tán:** Cư dân phải thảo luận các vấn đề chung của chung cư/khu trọ thông qua các nền tảng chat thứ ba (Zalo, Facebook), khiến dữ liệu phản hồi bị phân tán, BQL khó quản lý tập trung và bỏ sót thông tin.

* **Tránh Solution Thinking:** Giai đoạn này chỉ tập trung giải quyết bài toán "kết nối truyền thông, tạo không gian tương tác tập trung, an toàn trong ngày" chứ không can thiệp sâu vào việc tối ưu cơ sở dữ liệu hay thuật toán phân phối bài viết phức tạp.

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
* **Bảng tin (News Feed):** Không gian tập trung hiển thị các bài viết dạng mạng xã hội nội bộ, nơi thông tin truyền tải đi kèm khả năng tương tác.
* **APPROVED (Đã duyệt):** Trạng thái bắt buộc của bài viết để có thể xuất hiện trên Bảng tin.
* **Bài viết trong ngày hiện tại:** Các bài viết có mốc thời gian phê duyệt trùng với ngày truy cập hiện tại của hệ thống.
* **Tương tác (Engagement):** Bao gồm hành động Thích (`post_reactions`) và Bình luận (`post_comments`) nhằm tạo luồng thảo luận dưới bài viết.

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->
* **Người được lợi:** 
    * **Ban quản lý:** Thu thập được phản hồi nhanh chóng từ cư dân, tăng tính minh bạch và hiệu quả truyền thông.
    * **Cư dân:** Nắm bắt tin tức nóng hổi trong ngày, dễ dàng bày tỏ ý kiến hoặc đặt câu hỏi trực tiếp dưới thông báo.
* **Người chịu ảnh hưởng:** Ban quản lý và Đội ngũ vận hành (phải theo dõi, đọc các bình luận để giải đáp thắc mắc cho cư dân).
* **Người có quyền quyết định:** Trưởng Ban quản lý / Chủ đầu tư (quyết định quy chuẩn nội dung được phép bình luận).

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech, Business, Time... -->
* **Ràng buộc Nghiệp vụ (Business Constraints):**
    * Chỉ hiển thị các bài viết có trạng thái **APPROVED** và được duyệt trong **ngày hiện tại**.
    * Sắp xếp bài viết: Mới nhất lên đầu (Thời gian tạo giảm dần).
    * Sắp xếp bình luận: Thời gian tạo tăng dần (Cũ nhất lên đầu để theo dõi luồng hội thoại liên tục).
    * Giới hạn ký tự bình luận: Tối đa **1000 ký tự** và không được phép để trống.
    * Quy tắc tương tác: Mỗi người dùng chỉ được `Thích` tối đa 1 lần/bài viết, nhưng được `Bình luận` nhiều lần.
* **Ràng buộc Kỹ thuật (Technical Constraints):**
    * Bảo mật phân quyền: Người dùng chưa đăng nhập (Chưa xác thực) thì **không được phép** Thích hoặc Bình luận.
    * Hiệu năng API: Thời gian phản hồi không vượt quá **500 ms (P95)**.
    * Tần suất gửi yêu cầu (Rate Limit): Giới hạn **100 requests/phút/người dùng** để chống spam tương tác.

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
* **Giả định 1:** Giả định rằng "ngày hiện tại" được tính theo múi giờ hệ thống của Server (ví dụ: GMT+7 cho Việt Nam) và đồng bộ chính xác với thời gian trên thiết bị của cư dân.
* **Giả định 2:** Giả định rằng khi bài viết bước sang ngày hôm sau, nó sẽ tự động biến mất khỏi Bảng tin chính mà không cần chạy một job quét data ẩn nào (chỉ lọc theo điều kiện thời gian ở câu lệnh `GET`).
* **Giả định 3:** Giả định rằng cấu trúc bảng dữ liệu `post_reactions` và `post_comments` đã có sẵn cơ chế định danh người dùng (`user_id`) để phục vụ kiểm tra phân quyền và chặn trùng lặp lượt Thích.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->
* **Câu hỏi 1:** Yêu cầu *"chỉ hiển thị bài viết được duyệt trong ngày hiện tại"* có quá ngặt nghèo không? Nếu một ngày BQL không đăng bài mới, Bảng tin sẽ bị trống hoàn toàn. Có nên đổi thành hiển thị bài viết trong vòng 24 giờ - 48 giờ qua, hoặc hiển thị N bài viết mới nhất không?
* **Câu hỏi 2:** Cư dân có thể xem lại các bài viết cũ (đã quá ngày hiện tại) thông qua một mục "Kho lưu trữ/Lịch sử thông báo" nào khác hay không? Vì hiện tại tính năng tìm kiếm và lọc bài viết đang là *Out of Scope*.
* **Câu hỏi 3:** Do tính năng xóa/sửa bình luận thuộc *Out of Scope*, nếu cư dân bình luận nội dung phản cảm, vi phạm pháp luật hoặc từ ngữ thô tục, Ban quản lý sẽ xử lý như thế nào trên giao diện khi không có quyền ẩn/xóa bình luận đó?
* **Câu hỏi 4:** Trong API contract của chi tiết bài viết (`GET /api/v1/news-feed/{postId}`), danh sách bình luận trả về đã có cơ chế phân trang (Pagination) chưa, hay sẽ trả về toàn bộ bình luận một lúc? Nếu bài viết có hàng ngàn bình luận, việc trả về toàn bộ sẽ vi phạm ràng buộc hiệu năng < 500ms.