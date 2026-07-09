# CONTEXT.md

## 1. PROBLEM STATEMENT

- **Nỗi đau của User (Ban quản lý & Người thuê):**
  - Hiện tại, chưa có cơ chế kiểm soát chất lượng và độ chính xác của các thông tin, thông báo phát ra trong cộng đồng khu trọ/chung cư.
  - Nếu để thông tin được đăng tải tự do hoặc không qua kiểm duyệt, có nguy cơ lớn xuất hiện nội dung sai lệch, tin giả, ngôn từ không phù hợp hoặc vi phạm nội quy của khu nhà, dẫn đến sự hoang mang, hiểu lầm và làm giảm chất lượng truyền thông nội bộ.
  - Ban quản lý thiếu một công cụ tập trung để khởi tạo, theo dõi danh sách chờ và phê duyệt các nội dung truyền thông một cách chính thống trước khi hiển thị tới cư dân.
- **Tránh Solution Thinking:** Mục tiêu ở bước này chỉ tập trung vào việc giải quyết bài toán "kiểm soát nội dung và đảm bảo tính chính xác, phù hợp của thông tin trước khi tiếp cận người thuê", tránh việc can thiệp sâu vào cách thức thiết kế giao diện hay tối ưu hạ tầng ở giai đoạn cô đọng ngữ cảnh này.

## 2. DOMAIN KNOWLEDGE

- **Ban quản lý (BQL):** Nhóm người dùng có thẩm quyền cao nhất trong hệ thống nội bộ, chịu trách nhiệm vận hành khu nhà và kiểm duyệt thông tin.
- **Cư dân / Người thuê:** Đối tượng tiếp nhận thông tin cuối cùng sau khi nội dung đã được phê duyệt.
- **Trạng thái bài viết:**
  - `PENDING`: Trạng thái mặc định ban đầu khi bài viết vừa được tạo, nằm trong danh sách chờ duyệt và chưa hiển thị cho cư dân.
  - `APPROVED`: Trạng thái sau khi được BQL phê duyệt thành công, bài viết chính thức có hiệu lực và hiển thị công khai.
- **Soft Delete (Xóa mềm):** Cơ chế không xóa hoàn toàn bản ghi khỏi cơ sở dữ liệu mà chỉ đánh dấu thông qua trường thời gian `deleted_at`, giúp giữ lại lịch sử đối soát thông tin khi cần thiết.

## 3. STAKEHOLDERS

- **Người được lợi:**
  - **Ban quản lý:** Có công cụ quản lý thông tin chính thống, giữ vững trật tự và quy định truyền thông.
  - **Cư dân / Người thuê:** Tiếp cận nguồn tin đáng tin cậy, chính xác, sạch sẽ và an toàn.
- **Người chịu ảnh hưởng:** Đội ngũ nhân sự thuộc Ban quản lý (phải trực tiếp vận hành, đọc và duyệt tin hàng ngày).
- **Người có quyền quyết định:** Chủ đầu tư / Trưởng Ban quản lý khu nhà (quyết định về luồng nghiệp vụ và quy định kiểm duyệt).

## 4. CONSTRAINTS (Ràng buộc không thể thay đổi)

- **Nghiệp vụ & Dữ liệu:**
  - Tiêu đề bài viết bắt buộc phải nhập và không được vượt quá **250 ký tự**.
  - Nội dung bài viết không được phép để trống.
  - Bài viết mới tạo luôn luôn có trạng thái mặc định là `PENDING`.
  - Phải áp dụng cơ chế **Soft Delete** sử dụng trường `deleted_at`.
- **Kỹ thuật & Tệp tin:**
  - Chỉ chấp nhận các định dạng hình ảnh: **JPG, JPEG, PNG**.
  - Dung lượng ảnh tối đa cho phép là **5 MB**.
  - Hỗ trợ cả hai hình thức: Chụp trực tiếp từ thiết bị hoặc Tải lên từ thư viện máy.
- **Hiệu năng & Bảo mật (Non-functional):**
  - Chỉ người dùng có vai trò thuộc Ban quản lý mới được phép thao tác (Tạo, Xem danh sách chờ, Duyệt, Xóa).
  - Thời gian phản hồi của API (Response Time) phải đảm bảo không vượt quá **500 ms (P95)**.
  - Tần suất gửi yêu cầu tối đa (Rate limit): **100 requests/phút/người dùng**.

## 5. ASSUMPTIONS (Giả định cần confirm)

- **Giả định 1:** Giả định rằng hệ thống hiện tại đã có sẵn module quản lý phân quyền (Role-based Access Control - RBAC) hoạt động ổn định để phân biệt rõ ràng giữa BQL và Cư dân dựa trên Token/Session.
- **Giả định 2:** Giả định rằng khi một bài viết bị Xóa mềm (`deleted_at` được cập nhật), hệ thống ở các module hiển thị cho cư dân sẽ tự động lọc bỏ bài viết này mà không cần thêm logic phức tạp.
- **Giả định 3:** Giả định rằng kích thước ảnh tối đa 5MB là đủ cho nhu cầu sử dụng thực tế của BQL và hạ tầng lưu trữ (ví dụ: S3, Cloudinary) có khả năng xử lý tốt mà không làm nghẽn băng thông.

## 6. OPEN QUESTIONS (Câu hỏi chưa có câu trả lời)

- **Câu hỏi 1:** Hiện tại tính năng Từ chối bài viết (`REJECTED`) và nhập lý do đang là *Out of Scope*. Vậy nếu bài viết có nội dung không phù hợp, BQL bắt buộc phải chọn "Xóa bài viết" luôn hay có cần một trạng thái trung gian nào để lưu vết lý do không duyệt hay không?
- **Câu hỏi 2:** Hệ thống có giới hạn số lượng ảnh tối đa được đính kèm trong một bài viết hay không, hay chỉ cho phép duy nhất 1 ảnh (`imageUrl` dạng chuỗi đơn lẻ như trong API Contract)?
- **Câu hỏi 3:** Đối với luồng chụp ảnh trực tiếp trên thiết bị di động, ứng dụng Client sẽ tự xử lý nén ảnh trước khi up lên hay Server sẽ chịu trách nhiệm tối ưu hóa dung lượng để đảm bảo constraint dưới 5MB và response time dưới 500ms?