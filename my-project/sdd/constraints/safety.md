# SAFETY CONSTRAINTS - Guardrails (Rào chắn an toàn cho Agent)
# LƯU Ý CHO AI: Đây là "last line of defense" (chốt chặn cuối cùng). TUYỆT ĐỐI KHÔNG ĐƯỢC VI PHẠM!

## 1. DATA SAFETY (An toàn cơ sở dữ liệu SQL Server)
**KHÔNG ĐƯỢC PHÉP (Blocking - Cần Human xác nhận):**
- TUYỆT ĐỐI KHÔNG sinh ra câu lệnh `DROP TABLE`, `TRUNCATE` trong các file SQL migration [3].
- TUYỆT ĐỐI KHÔNG viết câu lệnh `DELETE FROM` hoặc `UPDATE` mà không có mệnh đề `WHERE` đi kèm (Thảm họa mất dữ liệu) [3].
- KHÔNG tự ý thay đổi kiểu dữ liệu (column type) của các bảng đang có sẵn dữ liệu [3].

**PHẢI LÀM:**
- Luôn nhắc nhở con người: "Bạn đã backup database chưa?" trước khi yêu cầu chạy các script thay đổi cấu trúc bảng [3].

## 2. CODE & BUILD SAFETY (An toàn mã nguồn)
**KHÔNG ĐƯỢC tự ý thực hiện các hành động sau:**
- KHÔNG tự ý chỉnh sửa file `build.xml` (của Apache Ant) để thêm thư viện mới (file `.jar`) nếu chưa hỏi ý kiến con người [3].
- KHÔNG tự ý xóa bất kỳ file `.java` hay `.jsp` nào trừ khi con người trực tiếp ra lệnh [3].
- KHÔNG push hoặc commit thẳng mã nguồn vào branch `main` hoặc `master` [3].

## 3. PRODUCTION SAFETY (An toàn vận hành)
- **Hardcode Secrets:** KHÔNG BAO GIỜ được hardcode thông tin kết nối SQL Server (URL, username, password) trực tiếp vào trong các file Servlet (`.java`) hay JSP. Bắt buộc đọc từ file cấu hình ngoài hoặc biến môi trường [4].
- **Bypass Auth:** KHÔNG BAO GIỜ được tự ý bỏ qua (bypass) các bộ lọc xác thực (như `AuthFilter`) để "test code cho nhanh" [4].
- **Logging:** Không được log các thông tin nhạy cảm của người dùng (PII) ra console của Tomcat [4].

## 4. KHI KHÔNG CHẮC CHẮN (When in doubt)
- **DỪNG LẠI VÀ BÁO CÁO:** Đừng bao giờ tự suy đoán (assume). Hãy hỏi: "Tôi không chắc về điều khoản an toàn X. Bạn muốn xử lý thế nào?" [4].
- Chậm mà chắc chắn đúng, còn hơn tự đưa ra quyết định nhanh nhưng làm hỏng hệ thống [4].
