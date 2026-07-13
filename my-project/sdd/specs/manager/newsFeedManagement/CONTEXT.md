# CONTEXT.md [Bảng tin cộng đồng]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT
<!-- Vấn đề thực sự là gì? User đang bị đau ở đâu? -->
<!-- Tránh solution thinking ở bước này. Chỉ mô tả pain. -->

* **Truyền thông nội bộ bị giới hạn thời gian**: Ban quản lý gặp khó khăn trong việc duy trì dòng thông tin tươi mới trên bảng tin chính do hệ thống chỉ cho phép hiển thị các bài viết được phê duyệt trong vòng 24 giờ qua[cite: 5]. Điều này khiến các thông báo quan trọng phát hành trước đó dễ bị biến mất đột ngột khỏi News Feed của cư dân.
* **Gánh nặng quản lý nội dung độc hại hoặc tiêu cực**: Sự thiếu hụt cơ chế tự động chặn/lọc từ ngữ thô tục buộc Ban quản lý phải tốn thời gian kiểm duyệt thủ công từng bình luận, gây áp lực trong việc duy trì môi trường thảo luận văn minh[cite: 5].
* **Trải nghiệm tương tác nghèo nàn và chậm trễ**: Hệ thống thiếu tính năng cập nhật theo thời gian thực (Real-time notifications/Websocket) khiến Ban quản lý không thể nắm bắt tức thời khi có lượt thích hoặc bình luận mới phát sinh, làm giảm hiệu suất tương tác qua lại[cite: 5].
* **Bất tiện khi tra cứu thông tin cũ**: Việc không hỗ trợ công cụ tìm kiếm và bộ lọc bài viết trên bảng tin khiến Ban quản lý mất nhiều thời gian nếu muốn xem lại các thông báo hoặc hoạt động cũ của cộng đồng[cite: 5].

## 2. DOMAIN KNOWLEDGE
<!-- Các thuật ngữ domain-specific mà AI cần biết -->
<!-- Ví dụ: "invoice" trong hệ thống này nghĩa là gì? -->
<!-- Các quy tắc nghiệp vụ bất thành văn -->

* **Bài viết hợp lệ (Approved Post)**: Các bài viết truyền tải thông báo, tin tức đã được Ban quản lý phê duyệt và chuyển trạng thái sang `APPROVED`[cite: 5]. Chỉ những bài viết này mới được phép hiển thị công khai trên bảng tin[cite: 5].
* **Giới hạn hiển thị 24h (24-hour Window)**: Quy tắc nghiệp vụ cố định yêu cầu hệ thống lọc và chỉ hiển thị các bài viết có thời gian phê duyệt nằm trong vòng 24 giờ đổ lại so với thời điểm truy cập[cite: 5].
* **Toggle Reaction**: Cơ chế thích/bỏ thích bài viết hoạt động dưới dạng chuyển đổi (toggle)[cite: 5]. Hệ thống cần kiểm tra sự tồn tại của bản ghi trong bảng `post_reactions` để quyết định thêm mới hoặc xóa bỏ tương ứng, đảm bảo mỗi người dùng chỉ được tương tác duy nhất một lần trên một bài viết[cite: 5].
* **Phí tổn tải động (Dynamic Loading)**: Để tối ưu hóa băng thông, danh sách bài viết không được tải toàn bộ mà phải đi qua cơ chế phân trang (Pagination) thông qua cặp tham số `offset` và `limit` của AJAX API[cite: 5].

## 3. STAKEHOLDERS
<!-- Ai được lợi? Ai chịu ảnh hưởng? Ai có quyền quyết định? -->

* **Ban quản lý (Manager)**: Người vận hành trực tiếp, sử dụng phân hệ để theo dõi nội dung công bố, đánh giá mức độ quan tâm qua widget nổi bật và thực hiện quyền kiểm duyệt (xóa bình luận)[cite: 5].
* **Cư dân / Người dùng hệ thống**: Người tiếp nhận thông tin, tương tác và trao đổi ý kiến trực tiếp dưới các bài đăng[cite: 5].
* **Hệ thống / Phát triển sản phẩm**: Chịu trách nhiệm đảm bảo hiệu năng truyền tải dữ liệu động và an toàn thông tin[cite: 5].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)
<!-- Tech: "Phải dùng PostgreSQL vì infrastructure hiện tại" -->
<!-- Business: "Phải comply với Thông tư 06/2023/TT-NHNN" -->
<!-- Time: "Phải live trước 30/06" -->

* **Kiến trúc phân tách View-API**: Giao diện chính bắt buộc phải render thông qua Servlet `ManagerNewsFeedServlet` tới file JSP chỉ định[cite: 5]. Toàn bộ thao tác nghiệp vụ xử lý dữ liệu (Like, Comment, Loading) bắt buộc phải gọi thông qua AJAX tới endpoints của `NewsFeedApiServlet`[cite: 5].
* **Bảo mật truy cập**: Tất cả các URL mẫu `/manager/*` phải được bảo vệ nghiêm ngặt bằng Filter để chặn người dùng chưa đăng nhập hoặc sai vai trò[cite: 5].
* **Định dạng phản hồi lỗi**: Mọi response thất bại từ API bắt buộc phải trả về cấu hình JSON chuẩn chứa hai trường `success: false` và `error: "Thông báo lỗi"` kèm mã trạng thái HTTP tương ứng[cite: 5].
* **Giới hạn ký tự**: Nội dung của mỗi bình luận được gửi lên hệ thống không được phép vượt quá giới hạn **1000 ký tự**[cite: 5].
* **Hiệu năng và Tần suất (SLA & Rate Limit)**: Thời gian phản hồi của các API không được vượt quá ngưỡng **500ms (P95)**[cite: 5]. Đồng thời, hệ thống phải áp đặt giới hạn tần suất tối đa **100 requests/phút/người dùng** để chống tấn công từ chối dịch vụ[cite: 5].

## 5. ASSUMPTIONS (giả định cần confirm)
<!-- Những điều bạn assume là đúng nhưng chưa confirm -->
<!-- Mỗi assumption là một rủi ro nếu sai -->

* **Giả định 1**: Giả định rằng trường `image_url` của bài viết chỉ chứa liên kết của một hình ảnh duy nhất[cite: 5]. *Rủi ro nếu sai:* Nếu một bài viết có nhiều ảnh đính kèm, giao diện render hiện tại và popup phóng to ảnh modal sẽ bị vỡ bố cục hoặc hiển thị thiếu dữ liệu[cite: 5].
* **Giả định 2**: Giả định rằng khi một bình luận bị xóa (`DELETE`), hệ thống chỉ thực hiện xóa vật lý hoặc xóa mềm chính bản ghi đó mà không ảnh hưởng đến các logic thống kê khác[cite: 5]. *Rủi ro nếu sai:* Nếu không có cơ chế giảm trừ đồng bộ, số lượng đếm bình luận hiển thị trên bài viết sẽ bị lệch so với danh sách thực tế.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)
<!-- Những điều cần clarify với stakeholder trước khi viết spec -->

* **Câu hỏi 1**: Tiêu chí xác định "tương tác cao nhất" để xếp hạng Top 5 bài viết nổi bật được tính toán dựa trên trọng số nào[cite: 5]? (Tổng Lượt thích + Lượt bình luận, hay ưu tiên bài viết có lượng bình luận nhiều hơn?)
* **Câu hỏi 2**: Khi một bài viết bị xóa hoặc chuyển trạng thái từ `APPROVED` sang trạng thái khác, các dữ liệu liên quan trong bảng `post_reactions` và `post_comments` sẽ được xử lý cascade (xóa tự động) hay giữ nguyên để làm lịch sử?
* **Câu hỏi 3**: Đối với việc hiển thị toàn bộ nội dung bài viết mà không thu gọn (UC02)[cite: 5], nếu gặp bài viết có độ dài lên tới hàng nghìn từ thì có gây ảnh hưởng xấu đến trải nghiệm cuộn trang (UX) của người dùng trên giao diện Bảng tin hay không?