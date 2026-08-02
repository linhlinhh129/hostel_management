# CONSTITUTION.md

**Hiến pháp Dự án — Tài liệu Thẩm quyền Tối cao**
Phiên bản: 1.0 | Trạng thái: ACTIVE
Cập nhật lần cuối: 2025-01-01 | Người sở hữu: Tech Lead

Bất kỳ Pull Request (Yêu cầu kéo) nào vi phạm bất kỳ quy tắc nào dưới đây SẼ tự động bị từ chối.

Bất kỳ ngoại lệ nào đều yêu cầu một RFC (Request for Comments) trong `/docs/rfcs/` có chữ ký phê duyệt từ Tech Lead.

---

# LỚP 1 — CÁC QUY TẮC CỨNG (HARD RULES)

Vi phạm = Chặn merge (gộp code) ngay lập tức, không có ngoại lệ.

---

## SEC-01: Lưu trữ Mật khẩu

Mật khẩu SẼ PHẢI được băm (hash) bằng **argon2id** (theo khuyến nghị của OWASP) hoặc **bcrypt** với độ khó (cost) >= 12.

Mật khẩu dạng văn bản thuần túy (plaintext) KHÔNG ĐƯỢC PHÉP lưu trữ, ghi log, hoặc truyền tải trong bất kỳ hoàn cảnh hay môi trường nào.

Các thông tin nhạy cảm (secrets) KHÔNG ĐƯỢC PHÉP xuất hiện trong mã nguồn, file cấu hình, hoặc file log.

Các thông tin nhạy cảm chỉ được phép tham chiếu thông qua biến môi trường (`$ENV_VARIABLE`) hoặc một giải pháp quản lý vault.

---

## SEC-02: Bảo mật Đường truyền

Môi trường Production (Môi trường thực tế) SẼ PHẢI bắt buộc sử dụng HTTPS với TLS 1.2 trở lên.

Cookie phiên làm việc (Session cookies) CHỈ SẼ ĐƯỢC truyền qua các kết nối TLS 1.2+ trên môi trường production.

Cookie phiên làm việc SẼ PHẢI sử dụng `HttpOnly`, `Secure`, và chính sách `SameSite` phù hợp.

CORS SẼ PHẢI chỉ định rõ ràng các nguồn (origins) được phép.

Tuyệt đối CẤM sử dụng ký tự đại diện (`*`) cho origins trong môi trường production.

---

## SEC-03: Bắt buộc Xác thực

Hệ thống SẼ PHẢI yêu cầu xác thực (authentication) và phân quyền (authorization) cho mọi API endpoint có thao tác thay đổi dữ liệu:

* POST
* PUT
* PATCH
* DELETE

Xác thực SẼ PHẢI sử dụng Xác thực Dựa trên Phiên (Session-Based Authentication) bằng Jakarta Servlet `HttpSession`.

KHÔNG ĐƯỢC SỬ DỤNG Xác thực bằng JWT trừ khi có RFC trong tương lai được Tech Lead phê duyệt.

Kiểm soát Truy cập Dựa trên Vai trò (RBAC - Role-Based Access Control) là bắt buộc đối với:

* Admin (Quản trị viên)
* Building Management (Quản lý tòa nhà)
* Resident (Cư dân)
* Financial Manager (Quản lý tài chính)
* Security Guard (Bảo vệ)

Các public endpoint SẼ PHẢI tài liệu hóa rõ ràng lý do cho phép truy cập công khai trong đặc tả OpenAPI.

---

## SEC-04: Quy tắc về Phiên làm việc (Session)

Thời gian tồn tại của phiên SẼ PHẢI được kiểm soát ở phía server.

Các phiên đã hết hạn SẼ PHẢI bị từ chối.

Thời gian chờ (idle timeout) của phiên KHÔNG ĐƯỢC vượt quá 30 phút.

Định danh phiên (Session identifiers) SẼ PHẢI do servlet container tạo ra và KHÔNG ĐƯỢC ghi log hoặc phơi bày ra mã nguồn ứng dụng vượt ngoài quy trình xử lý session thông thường.

Hệ thống SẼ PHẢI làm mới định danh phiên sau khi xác thực thành công bằng cách vô hiệu hóa bất kỳ phiên ẩn danh/tiền-đăng-nhập nào và tạo một phiên đã xác thực mới.

Đăng xuất SẼ PHẢI vô hiệu hóa (invalidate) phiên ở phía server.

---

## SEC-05: Kiểm tra Dữ liệu Đầu vào (Input Validation)

Hệ thống SẼ PHẢI kiểm tra và làm sạch (sanitize) toàn bộ dữ liệu đầu vào có nguồn gốc từ:

* Form JSP/HTML
* Tham số truy vấn (Query parameters)
* Biến đường dẫn (Path variables)
* File tải lên (File uploads)
* JSON payloads

Kiểm tra hợp lệ SẼ PHẢI tồn tại ở:

* Frontend (kiểm tra UX)
* Backend (kiểm tra bảo mật)

SQL Injection SẼ PHẢI được ngăn chặn thông qua:

* PreparedStatement
* Parameterized queries
* Các mẫu truy vấn an toàn bằng ORM

Tuyệt đối CẤM việc cộng chuỗi SQL trực tiếp với dữ liệu người dùng nhập.

---

## SEC-06: Giới hạn Tần suất (Rate Limiting)

Các endpoint xác thực (`/login`, `/login/2fa`) SẼ PHẢI bị giới hạn ở:

**10 request mỗi phút cho mỗi địa chỉ IP.**

Các request vượt quá giới hạn SẼ PHẢI trả về:

* HTTP 429
* Kèm theo header Retry-After

---

## SEC-07: Bảo mật File Tải lên

Hệ thống SẼ PHẢI kiểm tra:

* Đuôi mở rộng của file
* MIME type
* Kích thước file
* Kết quả quét mã độc (Malware scan)

Chỉ cho phép các loại file sau:

* PDF
* JPG
* PNG

Các file thực thi SẼ PHẢI bị chặn mà không có ngoại lệ.

---

## DATA-01: Xóa Mềm (Soft Delete)

Hệ thống SẼ PHẢI sử dụng xóa mềm cho tất cả các thực thể quan trọng trong nghiệp vụ:

* Resident (Cư dân)
* Apartment (Căn hộ)
* Invoice (Hóa đơn)
* Payment (Thanh toán)
* Complaint (Khiếu nại)
* Vehicle Registration (Đăng ký xe)
* Announcement (Thông báo)

Xóa cứng (Hard-delete) chỉ được phép đối với:

* File tạm thời
* Các phiên đã hết hạn
* Log cũ hơn 90 ngày

---

## DATA-02: Bắt buộc Ghi Nhật ký Kiểm toán (Audit Logging)

Hệ thống SẼ PHẢI duy trì nhật ký kiểm toán không thể thay đổi đối với:

* Đăng nhập/Đăng xuất
* Thanh toán
* Tạo hóa đơn
* Chỉnh sửa thông tin cư dân
* Phân công vai trò
* Xử lý khiếu nại

Nhật ký kiểm toán (Audit logs) SẼ PHẢI là bất biến (immutable).

---

# LỚP 2 — CÁC RÀNG BUỘC VỀ KIẾN TRÚC

Vi phạm = Phải tạo ticket nợ kỹ thuật (technical debt) và giải quyết trước khi phát hành.

---

## ARCH-01: Ranh giới Dịch vụ (Service Boundaries)

Các dịch vụ SẼ PHẢI giao tiếp thông qua Service Layer và DAO Layer tuân theo kiến trúc phân tầng.

Tuyệt đối CẤM truy cập cơ sở dữ liệu trực tiếp từ Controller/Servlet.

Quy trình ngoại lệ:

Cần có RFC trong `/docs/rfcs/` + phê duyệt từ Tech Lead.

Backend SẼ PHẢI tuân theo:

* Controller Layer
* Service Layer
* DAO Layer
* Database Layer

Controller/Servlet CHỈ SẼ xử lý:

* Request/Response
* Xác thực (Authentication)
* Phân quyền (Authorization)
* Kiểm tra hợp lệ cơ bản

Logic nghiệp vụ SẼ PHẢI nằm ở trong Service Layer.

DAO Layer CHỈ SẼ thực hiện các thao tác truy cập dữ liệu.

Tuyệt đối CẤM để logic nghiệp vụ bên trong JSP.

JSP CHỈ SẼ được sử dụng cho:

* Render giao diện (UI)
* Hiển thị dữ liệu
* Gửi form
* Các biểu thức JSTL/EL

---

## ARCH-02: Giao tiếp Frontend & Backend

JSP ở frontend SẼ PHẢI giao tiếp với Servlet Controllers.

Các request AJAX (nếu có) SẼ PHẢI giao tiếp với backend services thông qua REST APIs.

TUYỆT ĐỐI CẤM truy cập database trực tiếp từ frontend.

Giao ước API (API contracts) SẼ PHẢI được tài liệu hóa bằng OpenAPI/Swagger.

---

## ARCH-03: Thao tác Bất đồng bộ (Asynchronous Operations)

Các thao tác có thời gian xử lý dự kiến lớn hơn 2 giây SẼ PHẢI được xử lý bất đồng bộ thông qua message queue (Kafka hoặc RabbitMQ).

Các lệnh gọi HTTP đồng bộ với thời gian chờ vượt quá 2 giây được coi là vi phạm kiến trúc.

---

## ARCH-04: Tính Luỹ đẳng (Idempotency)

Các endpoint sau SẼ PHẢI triển khai các cơ chế luỹ đẳng để ngăn chặn thao tác trùng lặp:

* Payments (Thanh toán)
* Invoice creation (Tạo hóa đơn)
* Resident registration (Đăng ký cư dân)

---

## ARCH-05: Giao ước Phản hồi Lỗi (Error Response Contract)

Các phản hồi lỗi SẼ PHẢI sử dụng cấu trúc sau:

```json
{
  "error_code": "RESOURCE_NOT_FOUND",
  "message": "Mô tả thân thiện với người dùng",
  "request_id": "uuid-v4"
}
```

Dấu vết ngăn xếp (Stack traces) KHÔNG ĐƯỢC PHÉP xuất hiện trong bất kỳ phản hồi nào hướng đến client.

Chi tiết lỗi nội bộ KHÔNG ĐƯỢC PHÉP phơi bày ra bên ngoài server logs.

---

## ARCH-06: Kiến trúc Xác thực

Xác thực SẼ PHẢI sử dụng:

* Jakarta Servlet `HttpSession`
* Lưu trữ phiên ở phía server quản lý bởi Apache Tomcat
* Cookie phiên an toàn với HTTP-Only
* `AuthenticationFilter` đối với các trang yêu cầu xác thực
* `RoleFilter` đối với phân quyền theo vai trò

Hết hạn phiên:

* Thời gian chờ (Idle timeout): tối đa 30 phút
* Hết hạn tuyệt đối (Absolute timeout): định nghĩa theo chính sách deploy nếu cần

Đăng xuất:

* Phiên làm việc hiện tại SẼ PHẢI bị vô hiệu hóa.
* Các trang yêu cầu xác thực SẼ PHẢI từ chối các phiên bị thiếu hoặc hết hạn và chuyển hướng (redirect) về `/login`.

---

# LỚP 3 — TIÊU CHUẨN KỸ THUẬT

Vi phạm = Góp ý trong quá trình review PR; phải được sửa trước khi merge.

---

## ENG-00: Công nghệ được Phê duyệt (Approved Technology Stack)

| Lớp (Layer)        | Công nghệ (Technology)| Phiên bản |
| ------------------ | ------------------- | --------- |
| Runtime            | Java                | 17 |
| Framework          | Jakarta Servlet     | >= 6.0    |
| Application Server | Apache Tomcat       | >= 10.1   |
| Database           | SQL Server          | 2019      |
| Data Access        | JDBC                | >= 13.2   |
| Cache              | Tùy chọn Redis      | >= 7      |
| Queue              | Tùy chọn (RabbitMQ) | >= 5.x    |
| Authentication     | Session-Based Auth  | HttpSession |

Việc thay đổi công nghệ yêu cầu một RFC và sự phê duyệt của Tech Lead trước khi triển khai.

---

## ENG-01: Độ bao phủ Test (Test Coverage)

Logic nghiệp vụ SẼ PHẢI duy trì độ bao phủ số dòng code (line coverage) tối thiểu 80%.

Các luồng quan trọng (xác thực, thanh toán, sửa đổi dữ liệu) SẼ PHẢI duy trì độ bao phủ 100%.

Các mã nguồn mang tính thử nghiệm (Proof-of-concept) KHÔNG ĐƯỢC PHÉP merge vào nhánh main.

Ngưỡng bao phủ SẼ PHẢI được áp dụng trong quá trình CI.

Quá trình Build SẼ PHẢI báo lỗi (fail) khi độ bao phủ giảm xuống dưới mức yêu cầu.

---

## ENG-02: Quy trình Git (Git Workflow)

Tuyệt đối CẤM push code trực tiếp lên các nhánh `main` và `develop`.

Tên nhánh SẼ PHẢI sử dụng một trong các tiền tố sau:

* feat/
* fix/
* chore/
* hotfix/

Tin nhắn commit (Commit messages) SẼ PHẢI tuân theo chuẩn Conventional Commits:

```text
type(scope): description
```

Các PR SẼ PHẢI yêu cầu ít nhất một lần phê duyệt (approve review) trước khi merge.

Các PR SẼ PHẢI vượt qua tất cả các bài kiểm tra CI trước khi được phép merge.

---

## ENG-03: Tài liệu API (API Documentation)

Tất cả các API endpoint công khai SẼ PHẢI có tài liệu OpenAPI 3.x.

Tài liệu SẼ PHẢI được tự động tạo (generate) từ các annotations.

Các endpoint không có tài liệu KHÔNG ĐƯỢC PHÉP merge vào nhánh main.

---

## ENG-04: Quản lý Thư viện (Dependency Management)

Tất cả các thư viện của bên thứ ba SẼ PHẢI được cố định (pinned) ở các phiên bản cụ thể.

CẤM sử dụng các khoảng phiên bản như `^` và `~`.

`pom.xml` SẼ PHẢI được quản lý tập trung trong repo.

Mỗi dependency SẼ PHẢI khai báo một phiên bản cụ thể.

Các bản cập nhật phiên bản lớn (Major version upgrades) yêu cầu:

* Kiểm tra bảo mật (Security review)
* Tạo PR riêng

Dependencies SẼ PHẢI được quét bằng:

* OWASP Dependency Check
* Snyk
* Dependabot

Các lỗ hổng ở mức HIGH (Cao) và CRITICAL (Nghiêm trọng) SẼ CHẶN việc merge.

---

## ENG-05: Tiêu chuẩn Log

Log SẼ PHẢI sử dụng định dạng JSON có cấu trúc.

Chỉ cho phép các cấp độ log (log levels) sau:

* ERROR
* WARN
* INFO
* DEBUG

Thông tin Định danh Cá nhân (PII - Personally Identifiable Information) KHÔNG ĐƯỢC PHÉP xuất hiện trong log.

Mỗi bản ghi log SẼ PHẢI bao gồm:

* timestamp
* level
* request_id
* service

---

# LỚP 4 — CHÍNH SÁCH DÀNH CHO AI AGENT

Áp dụng cho tất cả các AI agent (Claude, Copilot, Cursor, v.v.) hoạt động trong mã nguồn này.

---

## AI-01: Các Hành động Được Phép (Không Yêu cầu Phê duyệt)

* Tạo mới, sửa đổi và tái cấu trúc (refactor) mã nguồn ứng dụng
* Viết hoặc cập nhật unit tests và integration tests
* Cập nhật tài liệu OpenAPI và các comment trong code
* Gợi ý cập nhật dependencies (chỉ gợi ý, không tự động áp dụng)
* Đọc bất kỳ file nào trong repository
* Tạo các file mới dưới các thư mục:

  * src/
  * test/
  * docs/

---

## AI-02: Các Hành động Bị Cấm (Yêu cầu Người dùng Trực tiếp Phê duyệt)

Quản lý Database (Database migrations):

* KHÔNG ĐƯỢC tạo hoặc thực thi các file migration mà không có hướng dẫn cụ thể cho từng migration.

Thay đổi cấu trúc Schema (Schema changes):

* KHÔNG ĐƯỢC thay đổi định nghĩa schema (vd. prisma/schema.prisma) mà không được phê duyệt.

Quản lý Secret (Secret management):

* KHÔNG ĐƯỢC tạo, sửa đổi, hoặc đọc các file `.env`.
* KHÔNG ĐƯỢC hardcode thông tin xác thực.

Thao tác trên Production (Production operations):

* KHÔNG ĐƯỢC thực thi các lệnh trong môi trường production.

Sửa đổi Dependency (Dependency modifications):

* KHÔNG ĐƯỢC sửa đổi dependencies khi chưa có sự phê duyệt rõ ràng.

Xóa file:

* KHÔNG ĐƯỢC xóa bất kỳ file nào ngoại trừ các file trong:

  * temp/
  * logs/

Thao tác Git:

* KHÔNG ĐƯỢC push, merge, hoặc tạo tag.
* Chỉ có thể cung cấp các khuyến nghị/gợi ý.

Thay đổi Hạ tầng (Infrastructure changes):

* KHÔNG ĐƯỢC sửa đổi các cấu hình CI/CD, Dockerfiles, hoặc Infrastructure-as-Code khi chưa được review.

---

## AI-03: Giao thức Tự Kiểm chứng (Self-Verification Protocol)

Trước khi submit bất kỳ dòng code nào, AI SẼ PHẢI xác minh tất cả các mục dưới đây.

### CHECKLIST BẢO MẬT

* [ ] Không có secrets, API keys, passwords, hoặc IP addresses dạng văn bản thô (plaintext)
* [ ] Không tạo mới các JWT access tokens, refresh tokens, hoặc JWT secrets
* [ ] Cookie phiên sử dụng HttpOnly, Secure trên production, và chính sách SameSite thích hợp
* [ ] Passwords được băm bằng BCrypt
* [ ] Tất cả endpoint có thao tác thay đổi dữ liệu đều yêu cầu xác thực
* [ ] RBAC authorization (phân quyền theo vai trò) hoạt động chính xác
* [ ] Kiểm tra hợp lệ dữ liệu đầu vào (Input validation) tồn tại trước mọi thao tác database
* [ ] Có kiểm tra file tải lên
* [ ] Không có raw SQL kết hợp với chuỗi input từ người dùng chưa được tham số hóa

### CHECKLIST KIẾN TRÚC

* [ ] Tôn trọng kiến trúc phân tầng
* [ ] Không có logic nghiệp vụ nằm trong giao diện React UI
* [ ] Trang JSP giao tiếp thông qua Servlet Controllers
* [ ] Thao tác async > 2 giây sử dụng queue/thread workers
* [ ] Payment/Invoice APIs có triển khai tính luỹ đẳng (idempotency)
* [ ] Tính năng Đăng xuất làm vô hiệu hóa HttpSession

### CHECKLIST TOÀN VẸN DỮ LIỆU

* [ ] Không có thao tác hard-delete trên các thực thể quan trọng của nghiệp vụ
* [ ] Mẫu thiết kế xóa mềm (Soft-delete) được áp dụng chuẩn xác

### CHECKLIST KỸ THUẬT

* [ ] Phải có các bài test cho toàn bộ logic nghiệp vụ mới
* [ ] Độ bao phủ (Coverage) không được giảm xuống dưới 80%
* [ ] Thêm các annotations OpenAPI cho các endpoint mới
* [ ] Không được phép tự ý thêm dependency mới khi chưa được duyệt
* [ ] Không có `console.log` chứa thông tin PII

Nếu bất kỳ mục checklist nào báo lỗi (fail):

**DỪNG LẠI. Khắc phục ngay sự cố trước khi submit. Tuyệt đối không được phép bỏ qua checklist.**
