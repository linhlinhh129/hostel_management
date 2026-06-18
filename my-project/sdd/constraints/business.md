# BUSINESS CONSTRAINTS & DOMAIN GLOSSARY

**Document Reference:** `.sdd/constraints/business.md`  
**Version:** 1.0.0  
**Classification:** Internal Confidendial  
**Target Audience:** AI Code Agents, Software Engineers, System Architects

---

## 1. PURPOSE & ENFORCEMENT

Tài liệu này quy định các giới hạn nghiệp vụ (Business Constraints), tiêu chuẩn bảo mật dữ liệu và bộ từ vựng miền (Domain Glossary) cốt lõi của hệ thống. 

### 1.1 Nguyên tắc tuân thủ
- **AI Agent và Lập trình viên BẮT BUỘC (SHALL)** tuân thủ tuyệt đối các quy định trong tài liệu này khi thiết kế cơ sở dữ liệu, viết mã nguồn, cấu hình API và ghi log.
- **CẤM KHÔNG ĐƯỢC (SHALL NOT)** tự ý thay đổi, bỏ qua hoặc tối ưu hóa mã nguồn theo hướng vi phạm các ràng buộc dưới đây nếu không có sự phê duyệt bằng văn bản từ Tech Lead hoặc Senior Business Analyst.

---

## 2. AUTHENTICATION & AUTHORIZATION

Phân hệ xác thực và phân quyền là chốt chặn bảo mật đầu tiên của hệ thống. Mọi luồng xử lý mã nguồn liên quan đến tài khoản BẮT BUỘC phải thực thi theo các tiêu chuẩn sau:

### 2.1 Tiêu chuẩn Mã hóa Mật khẩu
- **BẮT BUỘC (SHALL)** sử dụng thuật toán **Argon2id** (tuân thủ khuyến nghị hiện tại của OWASP) để băm (hash) mật khẩu của người dùng trước khi lưu trữ vào cơ sở dữ liệu.
- Cấu hình tham số tối thiểu cho Argon2id:
  - Memory cost ($m$): `65536` KB (64 MB)
  - Time cost ($t$): `3` iterations
  - Parallelism factor ($p$): `4` threads
- **CẤM TUYỆT ĐỐI (SHALL NOT)** sử dụng các thuật toán lỗi thời hoặc kém an toàn bao gồm: `MD5`, `SHA-1`, `SHA-256` (không muối) ? , và `Bcrypt` cho các tài khoản tạo mới.

### 2.2 Tiêu chuẩn JSON Web Token (JWT)
Hệ thống sử dụng cơ chế xác thực không trạng thái (Stateless Authentication) dựa trên cặp mã thông báo JWT với các ràng buộc nghiêm ngặt:

- **Access Token:**
  - Thời gian sống (TTL) **BẮT BUỘC (SHALL)** được giới hạn ở mức tối đa là **15 phút** (`15m`).
  - **BẮT BUỘC** sử dụng thuật toán mã hóa bất đối xứng `RS256` (hoặc `EdDSA`) để ký mã thông báo. Khóa bí mật (Private Key) dùng để ký phải được quản lý tập trung trong AWS Secrets Manager hoặc HashiCorp Vault.
  - Phải chứa các claim tiêu chuẩn: `iss` (issuer), `sub` (user_id), `aud` (audience), `exp` (expiration time), và `tenant_id`.

- **Refresh Token:**
  - Thời gian sống (TTL) **BẮT BUỘC (SHALL)** là **7 ngày** (`7d`).
  - **BẮT BUỘC** lưu trữ Refresh Token trong `HttpOnly`, `Secure`, và `SameSite=Strict` Cookie ở phía Client để ngăn chặn triệt để các cuộc tấn công XSS (Cross-Site Scripting).
  - **BẮT BUỘC** triển khai cơ chế **Refresh Token Rotation (RTR)**. Khi một Refresh Token được sử dụng để lấy Access Token mới, Refresh Token cũ đó phải bị vô hiệu hóa ngay lập tức. Nếu phát hiện một Refresh Token cũ bị sử dụng lại, hệ thống phải lập tức thu hồi toàn bộ các token thuộc phiên đăng nhập đó để ngăn chặn hành vi tấn công giả mạo.

- **Token Revocation (Đăng xuất & Thu hồi):**
  - Khi người dùng đăng xuất, Access Token tương ứng **BẮT BUỘC** phải được đưa vào danh sách đen (Blacklist) lưu trữ trên Redis cache với thời gian hết hạn bằng đúng thời gian sống còn lại của token đó.

---

## 3. API RULES

Tất cả các API Endpoints được phát triển cho hệ thống phải tuân thủ kiến trúc RESTful chuẩn hóa và đảm bảo tính sẵn sàng cao thông qua các ràng buộc kỹ thuật dưới đây.

### 3.1 Quy chuẩn Rate Limit Headers
Để bảo vệ hệ thống khỏi các cuộc tấn công từ chối dịch vụ (DoS/DDoS) và brute-force, mọi phản hồi HTTP từ API Gateway hoặc Application Server **BẮT BUỘC (SHALL)** trả về các thông tin giới hạn tần suất (Rate Limit) trong HTTP Headers:

| Header Name | Kiểu dữ liệu | Mô tả chi tiết |
| :--- | :--- | :--- |
| `X-RateLimit-Limit` | Integer | Số lượng request tối đa mà Client được phép gửi trong một cửa sổ thời gian (ví dụ: 60 request/phút). |
| `X-RateLimit-Remaining` | Integer | Số lượng request còn lại được phép thực hiện trong cửa sổ thời gian hiện tại. |
| `X-RateLimit-Reset` | Integer (Epoch) | Thời gian Unix Timestamp tính bằng giây khi cửa sổ hiện tại được reset lại từ đầu. |

- Khi một Client vượt quá hạn mức quy định, API Gateway **BẮT BUỘC** chặn yêu cầu và trả về mã trạng thái `HTTP 429 Too Many Requests` kèm theo body phản hồi chuẩn hóa dạng JSON thông báo lỗi hệ thống.

### 3.2 Quy chuẩn Phân trang (API Pagination)
Hệ thống nghiêm cấm việc trả về toàn bộ danh dách dữ liệu mà không có cơ chế giới hạn số lượng bản ghi.

- **Cursor-based Pagination (Phân trang dựa trên con trỏ):**
  - **BẮT BUỘC (SHALL)** áp dụng cho tất cả các API truy vấn danh sách có tập dữ liệu lớn, dữ liệu thời gian thực hoặc có tần suất tăng trưởng liên tục (Ví dụ: Danh sách `Order`, lịch sử Log, thông báo hệ thống).
  - API nhận vào tham số `cursor` (thường là một chuỗi mã hóa Base64 chứa ID của bản ghi cuối cùng kết hợp với trường sắp xếp) và `limit`.
  - Phản hồi **BẮT BUỘC** chứa thuộc tính `next_cursor` và `has_more` để Client thực hiện truy vấn trang tiếp theo.
- **Offset-based Pagination (`limit`/`offset`):**
  - **CẤM KHÔNG ĐƯỢC (SHALL NOT)** áp dụng cho tập dữ liệu lớn do hệ lụy suy giảm hiệu năng cơ sở dữ liệu nghiêm trọng khi giá trị `offset` lớn (Database phải quét qua tất cả các dòng trước đó).
  - Chỉ cho phép áp dụng đối với các bảng cấu hình hệ thống, bảng danh mục tĩnh, hoặc dữ liệu có số lượng bản ghi được khống chế ở quy mô nhỏ (ví dụ dưới 500 bản ghi) như danh sách phòng ban nội bộ, cấu hình quyền.

---

## 4. DATA MANAGEMENT & PRIVACY

Bảo mật dữ liệu và quyền riêng tư của khách hàng là ưu tiên tối cao. Mọi hành vi thao tác với dữ liệu nền tảng phải tuân thủ các nguyên tắc thiết kế sau:

### 4.1 Cơ chế Xóa Dữ liệu (Soft Delete vs Hard Delete)
- **Soft Delete (Xóa mềm):**
  - Tất cả các thực thể nghiệp vụ cốt lõi (Core Business Entities) và dữ liệu có liên quan đến khách hàng (bao gồm `Tenant`, `User`, `Order`) **BẮT BUỘC (SHALL)** sử dụng cơ chế Xóa mềm.
  - Cấu trúc bảng phải chứa trường `deleted_at` với kiểu dữ liệu `TIMESTAMP WITH TIME ZONE`, mặc định là `NULL`.
  - Khi có yêu cầu xóa từ người dùng, mã nguồn **CẤM KHÔNG ĐƯỢC** thực thi lệnh SQL `DELETE`. Thay vào đó, **BẮT BUỘC** thực thi lệnh `UPDATE` để gán giá trị thời gian hiện tại vào trường `deleted_at`.
  - Tất cả các câu lệnh `SELECT` truy vấn dữ liệu nghiệp vụ hoạt động **BẮT BUỘC** phải tự động lọc bỏ các bản ghi có `deleted_at IS NOT NULL` thông qua Global Query Filters của ORM.
- **Hard Delete (Xóa vật lý):**
  - **CHỈ ĐƯỢC PHÉP (MAY)** sử dụng lệnh `DELETE` vật lý đối với: Các bảng trung gian/bảng tạm (Temporary/Staging Tables), dữ liệu lưu trữ đệm (Cache), các bản ghi nháp chưa từng tham gia vào bất kỳ luồng nghiệp vụ chính thức nào, hoặc trong các script dọn dẹp dữ liệu (Data Purging Job) tự động chạy định kỳ đối với các bản ghi đã xóa mềm quá thời hạn lưu trữ pháp lý (ví dụ: sau 5 năm).

### 4.2 Bảo vệ Dữ liệu Định danh Cá nhân (PII Data) & Quy chuẩn Logging
Hệ thống giám sát và ghi log lỗi tuyệt đối không được trở thành nguồn rò rỉ thông tin bảo mật.

- **CẤM TUYỆT ĐỐI (SHALL NOT) ghi log** dưới mọi hình thức (cho dù là debug log) các thông tin nhạy cảm đặc biệt sau:
  - Mật khẩu dạng tường minh (Plaintext Password).
  - Mã khóa bảo mật, Token bí mật, Client Secret.
  - Thông tin thẻ thanh toán đầy đủ (Số thẻ PAN, ngày hết hạn, mã bảo mật CVV/CVC).
  - Mã OTP xác thực giao dịch.
- **Quy chuẩn Che dữ liệu nhạy cảm (PII Masking):**
  - Khi ghi nhận các thông tin định danh cá nhân của khách hàng vào hệ thống Log (Stdout, ElasticSearch, Datadog,...), mã nguồn **BẮT BUỘC (SHALL)** thực hiện cơ chế mặt nạ che dữ liệu (Masking Engine) theo quy chuẩn sau:
    - **Số điện thoại khách hàng:** **CẤM** log đầy đủ. **BẮT BUỘC** che 3 số cuối cùng bằng ký tự `*`. 
      * *Ví dụ:* `0912345678` thành `0912345***`.
    - **Địa chỉ Email:** **BẮT BUỘC** che phần định danh (local-part) trước ký tự `@`. Giữ lại ký tự đầu tiên, ký tự cuối cùng của local-part và toàn bộ domain.
      * *Ví dụ:* `nguyenvanan@gmail.com` thành `n*********n@gmail.com`.
    - **Số CMND/CCCD/Hộ chiếu:** **BẮT BUỘC** che toàn bộ, chỉ giữ lại 4 số cuối cùng.
      * *Ví dụ:* `037199001234` thành `********1234`.

---

## 5. DOMAIN GLOSSARY

Để đảm bảo sự nhất quán tuyệt đối giữa mã nguồn, cơ sở dữ liệu và tài liệu thiết kế, các AI Agent và Kỹ sư phần mềm **BẮT BUỘC (SHALL)** sử dụng chính xác các thuật ngữ miền dưới đây. KHÔNG ĐƯỢC tự ý sáng tạo các danh từ đồng nghĩa trong code (ví dụ: dùng `Company` thay cho `Tenant`, dùng `Customer` thay cho `User`).

### 5.1 Các thuật ngữ cốt lõi

#### Tenant
- **Định nghĩa:** Là một thực thể khách hàng doanh nghiệp hoặc tổ chức hoạt động theo mô hình B2B (Business-to-Business) thuê sử dụng nền tảng của chúng ta dưới dạng Phần mềm dịch vụ (SaaS).
- **Phạm vi dữ liệu:** Mỗi Tenant đại diện cho một không gian làm việc độc lập. Toàn bộ dữ liệu cấu hình, nhân sự và đơn hàng giữa các Tenant **BẮT BUỘC** phải được cô lập hoàn toàn ở tầng Logical Database hoặc Physical Database. Một Tenant có thể có nhiều cửa hàng hoặc chi nhánh nhưng dùng chung một mã định danh doanh nghiệp duy nhất.

#### User
- **Định nghĩa:** Là một tài khoản người dùng cụ thể, đại diện cho một **nhân viên thuộc quyền quản lý của Tenant**.
- **Hành vi & Quyền hạn:** User đăng nhập vào hệ thống để thao tác các tính năng nghiệp vụ được phân công. Quyền hạn của User được kiểm soát nghiêm ngặt thông qua cơ chế phân quyền dựa trên vai trò (RBAC - Role-Based Access Control) do Tenant thiết lập. Một User chỉ thuộc về duy nhất một Tenant tại một thời điểm và không có quyền truy cập chéo sang dữ liệu của Tenant khác.

#### Order
- **Định nghĩa:** Là một đơn hàng thương mại **đã được chốt thành công** giữa hệ thống và đối tác/khách hàng.
- **Điều kiện ghi nhận:** Một thực thể chỉ được chuyển đổi thành `Order` khi và chỉ khi hệ thống đã thực hiện thành công việc kiểm tra tính sẵn sàng của tồn kho (Inventory Validation), đồng thời nhận được xác nhận thanh toán hợp lệ (Payment Confirmation) hoặc có cam kết công nợ hợp pháp từ Tenant.
- **Phân biệt cấu trúc:** Loại trừ hoàn toàn các trạng thái như giỏ hàng nháp (`Cart`), báo giá tạm thời (`Quotation`), hay cơ hội kinh doanh (`Lead`). Tất cả các trạng thái nháp này phải được quản lý ở các bảng dữ liệu riêng biệt trước khi được chuyển đổi trạng thái thành `Order` chính thức.

---

## 6. COMPLIANCE CHECKLIST FOR AI AGENTS

Trước khi xuất xuất mã nguồn (pull request), các AI Agent phải tự kiểm tra mã nguồn dựa trên bảng kiểm nhanh dưới đây:

- [ ] Mật khẩu được mã hóa bằng Argon2id với tham số m=65536, t=3, p=4?
- [ ] Access token hết hạn đúng sau 15 phút và Refresh Token hết hạn đúng sau 7 ngày?
- [ ] Mọi API lấy danh sách có khả năng tăng trưởng đã được cấu hình Cursor-based pagination?
- [ ] API phản hồi đầy đủ 3 Headers của Rate Limit?
- [ ] Tuyệt đối không sử dụng lệnh SQL `DELETE` cho các bảng Tenant, User, Order?
- [ ] Mã nguồn ghi log đã tích hợp hàm Che dữ liệu (Masking) cho Số điện thoại (che 3 số cuối) và Email?
- [ ] Tên biến, tên bảng, tên class tuân thủ đúng từ vựng: `Tenant`, `User`, `Order`?