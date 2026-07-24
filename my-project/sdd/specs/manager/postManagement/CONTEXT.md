# CONTEXT.md \[Quản lý bài viết cộng đồng\]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT

- **Rủi ro rò rỉ nội dung độc hại, sai quy định**: Ban quản lý thiếu một phân hệ kiểm soát trung tâm để sàng lọc, phê duyệt bài viết trước khi chúng xuất hiện tới cư dân, dễ dẫn đến việc lan truyền tin tức giả, nội dung không phù hợp với quy định khu trọ/chung cư.

- **Gánh nặng nhập liệu và đính kèm đa phương tiện**: Khi cần thông báo gấp, Ban quản lý gặp bất tiện nếu quy trình tải ảnh phức tạp hoặc không thể bắt giữ hình ảnh thực tế trực tiếp từ camera của thiết bị di động để làm bằng chứng/minh họa.</span>

- **Thiếu linh hoạt trong quản trị dữ liệu bài viết**: Ban quản lý không có góc nhìn tổng quan để theo dõi song song các bài viết ở nhiều trạng thái khác nhau (</span>`PENDING`, </span>`APPROVED`), dẫn đến việc bỏ sót các thông báo quan trọng đang chờ duyệt.</span>

- **Bất tiện khi xử lý sai sót thông tin**: Ban quản lý không có cách nào gỡ bỏ nhanh chóng các bài viết cũ, lỗi thời hoặc nội dung không còn phù hợp ra khỏi hệ thống hiển thị công khai.</span>

- **Mất an toàn an ninh thông tin**: Hệ thống đứng trước nguy cơ bị các đối tượng cư dân hoặc người dùng ngoài xâm nhập trái phép vào phân hệ quản trị để tự ý đăng tải, phê duyệt hoặc xóa bỏ bài viết của khu trọ.</span>

## 2. DOMAIN KNOWLEDGE

- **Bài viết (Community Post / Article)**: Nội dung thông báo, tin tức nội bộ do Ban quản lý khởi tạo, chứa tiêu đề, nội dung văn bản và hình ảnh đính kèm (nếu có).</span>

- **Vòng đời trạng thái bài viết**:

  - `PENDING`: Trạng thái mặc định bắt buộc đối với mọi bài viết ngay sau khi khởi tạo thành công. Ở trạng thái này, bài viết nằm trong danh sách chờ và chưa hiển thị tới cư dân.</span>

  - `APPROVED`: Trạng thái sau khi được Ban quản lý phê duyệt thành công thông qua AJAX POST, cho phép bài viết xuất hiện công khai trên bảng tin cộng đồng.</span>

- **Cơ chế Soft Delete (Xóa mềm)**: Khi thực hiện hành động xóa bài viết, hệ thống không xóa vật lý bản ghi khỏi database mà tiến hành cập nhật mốc thời gian vào trường </span>`deleted_at`  để ẩn bài viết khỏi giao diện nhưng vẫn giữ lại lịch sử kiểm toán.</span>

- **Payload đa phần (Multipart Data)**: Do form tạo bài viết chứa tệp tin hình ảnh, luồng dữ liệu truyền lên bắt buộc phải sử dụng định dạng </span>`multipart/form-data` .</span>

## 3. STAKEHOLDERS

- **Ban quản lý (Manager)**: Người vận hành trực tiếp, có toàn quyền khởi tạo bài viết, duyệt các nội dung chờ và thực hiện xóa mềm các bài viết không phù hợp.</span>

- **Cư dân / Người thuê phòng**: Chịu ảnh hưởng gián tiếp bởi chất lượng và tính chính xác của các nguồn thông tin truyền thông được Ban quản lý phê duyệt phát hành.</span>

- **Hệ thống / Đội ngũ kỹ thuật**: Đảm bảo luồng xử lý ảnh upload và hiệu năng tải API đạt chuẩn.</span>

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

-  **Cấu trúc Servlet & Endpoint**: Toàn bộ module phải vận hành tập trung qua Servlet </span>`CommunityPostServlet`  xử lý các dải URL pattern quy định (</span>`/manager/articles` , </span>`/manager/articles/*` , v.v.).</span>

-  **Kiểm soát định dạng và dung lượng ảnh**: Hệ thống chỉ chấp nhận các tệp tin hình ảnh thuộc định dạng </span>`JPG` , </span>`JPEG` , </span>`PNG`  và giới hạn dung lượng tối đa không vượt quá **5 MB**.</span>

-  **Giới hạn ký tự tiêu đề**: Trường tiêu đề bài viết (</span>`title` ) bắt buộc không được vượt quá **250 ký tự**.</span>

-  **Phân quyền nghiêm ngặt**: Chỉ những tài khoản đã đăng nhập và được xác thực vai trò Ban quản lý (</span>`Manager` ) thông qua </span>`UserSessionDTO`  mới có quyền thực thi các tác vụ. Người dùng sai quyền tuyệt đối không được tiếp cận hệ thống.</span>

-  **Chỉ số hiệu năng (SLA)**: Thời gian phản hồi xử lý đối với các API Endpoint không được phép vượt quá **500 ms (P95)**.</span>

-  **Giới hạn tần suất (Rate limit)**: Áp đặt ngưỡng tối đa **100 requests/phút/người dùng** để bảo vệ hạ tầng.</span>

## 5. ASSUMPTIONS (giả định cần confirm)

-  **Giả định 1**: Giao diện thiết bị phần cứng của Ban quản lý (Trình duyệt máy tính/Điện thoại) hỗ trợ đầy đủ các API HTML5 cần thiết để kích hoạt quyền chụp ảnh trực tiếp từ camera. *Rủi ro nếu sai:* Tính năng chụp ảnh trực tiếp trên trang </span>`create.jsp`  sẽ bị lỗi hoặc không thể kích hoạt, buộc người dùng phải chuyển qua tải ảnh thủ công từ thư viện.</span>

-  **Giản định 2**: Giả định rằng khi một bài viết bị xóa mềm (</span>`Soft delete` ), hệ thống tự động loại bỏ bài viết đó ra khỏi tất cả các luồng truy vấn hiển thị ở trang danh sách chung mà không cần viết thêm các hàm lọc phức tạp.</span>

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

-  **Câu hỏi 1**: Hiện tại hệ thống chỉ có nút "Duyệt" và "Xóa" bài viết. Vậy đối với các bài viết </span>`PENDING`  có nội dung chưa đạt yêu cầu nhưng không đến mức phải xóa, Ban quản lý có cần thêm tính năng "Từ chối" (Reject) để chuyển bài viết về trạng thái nháp và yêu cầu sửa đổi không?</span>

- **Câu hỏi 2**: Luồng xử lý hình ảnh tải lên (`multipart/form-data`) sẽ lưu tệp trực tiếp vào thư mục cục bộ của Server (Local Deployment) hay lưu trữ tập trung qua các dịch vụ Cloud Storage (như AWS S3, Cloudinary)?

-  **Câu hỏi 3**: Khi hiển thị danh sách bài viết dưới dạng bảng (Table) ở trang </span>`list-pending.jsp` , hệ thống có cần bổ sung bộ lọc theo trạng thái (</span>`PENDING` , </span>`APPROVED` ) hoặc thanh tìm kiếm theo tiêu đề để tối ưu trải nghiệm tra cứu khi số lượng bài viết tăng lên quá nhiều không?</span>