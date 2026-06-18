# AGENTS.md — Quanlynhatro

> **Phiên bản:** 1.0
> **Cập nhật:** 2026-05
> **Sprint:** 1

---

# 1. TỔNG QUAN DỰ ÁN

## Thông tin dự án

* **Dự án:** Quanlynhatro - HomeX
* **Loại:** Ứng dụng Web
* **Giai đoạn:** Đang phát triển (Sprint 1)

## Vai trò AI

Kỹ sư Full-Stack cấp Senior, chuyên Java.

## Giới hạn phạm vi hoạt động

* TUYỆT ĐỐI KHÔNG tự động thực thi `DROP`, `DELETE`, `TRUNCATE` trên bất kỳ database nào khi chưa có xác nhận của con người.
* TUYỆT ĐỐI KHÔNG commit hoặc push trực tiếp lên nhánh `main` hoặc `master`.
* TUYỆT ĐỐI KHÔNG chỉnh sửa file `.env` hoặc nhúng secret vào mã nguồn.

---

# 2. TECH STACK

> **NGHIÊM NGẶT — KHÔNG ĐƯỢC PHÉP THAY ĐỔI**

| Tầng          | Công nghệ                                                                            |
| ------------- | ------------------------------------------------------------------------------------ |
| Backend       | Java 17 + Jakarta Servlet 6.0                                                        |
| Frontend      | JSP + JSTL + Bootstrap 5                                                             |
| Cơ sở dữ liệu | SQL Server 2022                                                                   |
| ORM / DB      | JDBC thuần, KHÔNG dùng Hibernate/JPA                                                 |
| Xác thực      | Session-Based Authentication + bcrypt                                                |
| Test/BE       | Mockito for HttpServletRequest/HttpServletResponse hoặc integration test trên Tomcat |
| Test/FE       | Manual UI Testing + Selenium/WebDriver optional                                      |
| Server        | Apache Tomcat 10.1                                                                   |
| Tài liệu      | Swagger / OpenAPI 3.0                                                                |

---

# 3. NGUYÊN TẮC KIẾN TRÚC

## Điều hướng request

Dự án sử dụng mô hình:

```text
JSP + Jakarta Servlet + Service + DAO + JDBC
```

Servlet chịu trách nhiệm:

* Nhận request từ client.
* Lấy dữ liệu từ request.
* Validate dữ liệu cơ bản.
* Gọi tầng Service.
* Forward sang JSP hoặc redirect sang URL khác.

JSP chỉ chịu trách nhiệm hiển thị giao diện.

* TUYỆT ĐỐI KHÔNG viết business logic trong JSP.
* TUYỆT ĐỐI KHÔNG gọi DAO trực tiếp trong JSP.

## URL Mapping

Các Servlet PHẢI đặt URL rõ ràng, dễ hiểu.

Ví dụ:

```text
/login
/logout
/dashboard
/users
/users/create
/users/edit
/rooms
/rooms/create
/tickets
/tickets/create
```

Với các chức năng quản trị, nên dùng prefix:

```text
/admin/users
/admin/facilities
/admin/rooms
/admin/reports
```

Nếu có API trả JSON thì mới dùng pattern:

```text
/api/v1/{resource}
```

Ví dụ:

```text
/api/v1/users
/api/v1/rooms
/api/v1/tickets
```

## Response

Với request hiển thị giao diện, Servlet PHẢI dùng:

```java
request.getRequestDispatcher("/WEB-INF/views/page.jsp").forward(request, response);
```

Hoặc:

```java
response.sendRedirect(request.getContextPath() + "/dashboard");
```

Với API trả JSON, response nên tuân theo cấu trúc:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "meta": {}
}
```

Không bắt buộc tất cả Servlet đều trả JSON.

* JSP page trả HTML.
* API endpoint mới trả JSON.

## Xử lý lỗi

TẤT CẢ lỗi phải được xử lý theo hướng tập trung, không hiển thị exception thô cho người dùng.

Có thể xử lý lỗi bằng một trong các cách sau:

* `BaseServlet`
* `ErrorHandler`
* `error.jsp`
* Cấu hình error page trong `web.xml`

Ví dụ trong `web.xml`:

```xml
<error-page>
    <exception-type>java.lang.Exception</exception-type>
    <location>/WEB-INF/views/error/500.jsp</location>
</error-page>

<error-page>
    <error-code>404</error-code>
    <location>/WEB-INF/views/error/404.jsp</location>
</error-page>
```

TUYỆT ĐỐI KHÔNG dùng `System.out.println()` để debug lỗi.

BẮT BUỘC dùng SLF4J Logger để log lỗi.

Ví dụ:

```java
private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
```

TUYỆT ĐỐI KHÔNG trả message exception thô ra giao diện.

Sai:

```java
request.setAttribute("error", e.getMessage());
```

Đúng:

```java
logger.error("Error while processing user request", e);
request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau.");
```

## Xác thực

Dự án sử dụng Session-Based Authentication.

* TUYỆT ĐỐI KHÔNG dùng JWT nếu chưa được team thống nhất.
* TUYỆT ĐỐI KHÔNG dùng `SecurityContextHolder`.
* TUYỆT ĐỐI KHÔNG hardcode `userId`.

Sau khi đăng nhập thành công, thông tin user phải được lưu vào `HttpSession`.

Ví dụ:

```java
HttpSession session = request.getSession();
session.setAttribute("currentUser", user);
```

Khi cần lấy user đang đăng nhập:

```java
HttpSession session = request.getSession(false);

if (session == null || session.getAttribute("currentUser") == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}

User currentUser = (User) session.getAttribute("currentUser");
```

Các trang cần đăng nhập PHẢI đi qua:

```text
AuthenticationFilter.java
```

Các trang cần phân quyền PHẢI đi qua:

```text
RoleFilter.java
```

## Phân quyền

Role của user phải được lấy từ session.

Ví dụ:

```java
User currentUser = (User) session.getAttribute("currentUser");
String role = currentUser.getRole();
```

* TUYỆT ĐỐI KHÔNG hardcode quyền trong JSP.
* Kiểm tra quyền nên đặt trong Filter hoặc Service.

Nếu user chưa đăng nhập, redirect về:

```text
/login
```

Nếu user không có quyền, forward sang:

```text
/WEB-INF/views/error/403.jsp
```

## Cơ sở dữ liệu

TẤT CẢ truy vấn database PHẢI đi qua tầng DAO.

* TUYỆT ĐỐI KHÔNG viết SQL trực tiếp trong Servlet.
* TUYỆT ĐỐI KHÔNG viết SQL trực tiếp trong JSP.
* TUYỆT ĐỐI KHÔNG viết SQL trực tiếp trong Service nếu không cần thiết.

DAO sử dụng JDBC thuần với:

* `Connection`
* `PreparedStatement`
* `ResultSet`

Ví dụ:

```java
String sql = "SELECT * FROM users WHERE email = ?";

try (
    Connection conn = DBConnectionUtil.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql)
) {
    ps.setString(1, email);

    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            // map ResultSet to User object
        }
    }
}
```

BẮT BUỘC dùng `PreparedStatement`.

TUYỆT ĐỐI KHÔNG nối chuỗi SQL trực tiếp từ input người dùng.

Sai:

```java
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
```

Đúng:

```java
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, email);
```

## Transaction

Với các nghiệp vụ có nhiều thao tác database liên quan nhau, phải dùng transaction.

Ví dụ:

* Tạo hóa đơn + tạo chi tiết hóa đơn.
* Tạo căn hộ + tạo thành viên căn hộ.
* Tạo phiếu sửa chữa + tạo file đính kèm.

Nguyên tắc:

```java
conn.setAutoCommit(false);

try {
    // thao tác 1
    // thao tác 2
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
}
```

## Migration / Script database

TUYỆT ĐỐI KHÔNG tự động chạy script:

* `DROP`
* `DELETE`
* `TRUNCATE`
* `ALTER`
* Migration thay đổi cấu trúc bảng

Nếu chưa được Tech Lead hoặc người phụ trách database xác nhận.

Script database phải được lưu trong thư mục:

```text
database/
├── schema.sql
├── seed.sql
└── migration/
```

## Quy tắc phân tầng

* Servlet chỉ xử lý request/response.
* Service xử lý nghiệp vụ.
* DAO xử lý truy vấn database.
* Model đại diện cho dữ liệu.
* JSP chỉ hiển thị giao diện.

Luồng chuẩn:

```text
Browser
   ↓
Servlet
   ↓
Service
   ↓
DAO
   ↓
Database
   ↓
DAO
   ↓
Service
   ↓
Servlet
   ↓
JSP
   ↓
Browser
```

---

# 4. QUY TẮC ĐẶT TÊN FILE & CẤU TRÚC

## Backend Java — Jakarta Servlet + JDBC

```text
src/main/java/com/quanlynhatro/
├── controller/        # Servlet xử lý request: {Resource}Servlet.java
├── service/           # Xử lý nghiệp vụ: {Resource}Service.java
├── service/impl/      # Implement service: {Resource}ServiceImpl.java
├── dao/               # Truy vấn database bằng JDBC: {Resource}DAO.java
├── model/             # Entity / JavaBean: {Resource}.java
├── dto/               # DTO nếu cần: {Resource}DTO.java
├── filter/            # AuthenticationFilter.java, RoleFilter.java
├── util/              # DBConnectionUtil.java, PasswordUtil.java, ValidationUtil.java
├── exception/         # Custom exception: AppException.java, ValidationException.java
└── constant/          # Hằng số: RoleConstant.java, StatusConstant.java
```

## Frontend JSP

```text
src/main/webapp/
├── WEB-INF/
│   ├── views/
│   │   ├── layout/
│   │   │   ├── header.jsp
│   │   │   ├── sidebar.jsp
│   │   │   └── footer.jsp
│   │   ├── auth/
│   │   │   └── login.jsp
│   │   ├── dashboard/
│   │   │   └── index.jsp
│   │   ├── users/
│   │   │   ├── list.jsp
│   │   │   ├── create.jsp
│   │   │   ├── edit.jsp
│   │   │   └── detail.jsp
│   │   ├── facilities/
│   │   │   ├── list.jsp
│   │   │   ├── create.jsp
│   │   │   ├── edit.jsp
│   │   │   └── detail.jsp
│   │   ├── rooms/
│   │   │   ├── list.jsp
│   │   │   ├── create.jsp
│   │   │   ├── edit.jsp
│   │   │   └── detail.jsp
│   │   ├── tickets/
│   │   │   ├── list.jsp
│   │   │   ├── create.jsp
│   │   │   ├── edit.jsp
│   │   │   └── detail.jsp
│   │   └── error/
│   │       ├── 403.jsp
│   │       ├── 404.jsp
│   │       └── 500.jsp
│   └── web.xml        # Nếu dùng cấu hình XML thay vì annotation
│
├── assets/
│   ├── css/
│   ├── js/
│   ├── images/
│   └── vendor/
│
└── index.jsp
```

## Database Script

```text
database/
├── schema.sql         # Tạo bảng
├── seed.sql           # Dữ liệu mẫu
└── migration/         # Script thay đổi database theo từng phiên bản
```

## Quy tắc đặt tên Java

Servlet đặt theo dạng:

```text
{Resource}Servlet.java
```

Service đặt theo dạng:

```text
{Resource}Service.java
{Resource}ServiceImpl.java
```

DAO đặt theo dạng:

```text
{Resource}DAO.java
```

Model đặt theo dạng:

```text
{Resource}.java
```

Filter đặt theo dạng:

```text
{Name}Filter.java
```

Util đặt theo dạng:

```text
{Name}Util.java
```

## Quy tắc đặt tên JSP

Tên file JSP dùng chữ thường.

Nếu nhiều từ thì dùng dấu gạch ngang `-`.

Ví dụ đúng:

```text
login.jsp
list.jsp
create.jsp
edit.jsp
detail.jsp
change-password.jsp
access-denied.jsp
```

Ví dụ không nên dùng:

```text
UserList.jsp
CreateUser.jsp
user_list.jsp
```

## Quy tắc JSP

JSP phải đặt trong:

```text
/WEB-INF/views/
```

Không cho người dùng truy cập trực tiếp JSP bằng URL.

Servlet phải forward sang JSP.

Ví dụ:

```java
request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
```

JSP chỉ dùng để hiển thị dữ liệu.

* TUYỆT ĐỐI KHÔNG viết SQL trong JSP.
* TUYỆT ĐỐI KHÔNG gọi DAO trong JSP.
* Hạn chế tối đa scriptlet `<% %>`.
* Ưu tiên dùng JSTL và EL.

Ví dụ đúng:

```jsp
<c:forEach var="user" items="${users}">
    <tr>
        <td>${user.fullName}</td>
        <td>${user.email}</td>
        <td>${user.role}</td>
    </tr>
</c:forEach>
```

---

# 5. CÁC MẪU BỊ CẤM

## JSP / Frontend

TUYỆT ĐỐI KHÔNG dùng React trong dự án này nếu stack đã chốt là JSP + Jakarta Servlet.

TUYỆT ĐỐI KHÔNG dùng các thư mục React như:

```text
components/
pages/
hooks/
types/
```

TUYỆT ĐỐI KHÔNG dùng file TypeScript như:

```text
.ts
.tsx
```

TUYỆT ĐỐI KHÔNG viết Java code phức tạp trong JSP.

Hạn chế tối đa dùng scriptlet:

```jsp
<% %>
```

Ưu tiên dùng JSTL và Expression Language.

Đúng:

```jsp
<c:forEach var="user" items="${users}">
    <tr>
        <td>${user.fullName}</td>
        <td>${user.email}</td>
        <td>${user.role}</td>
    </tr>
</c:forEach>
```

Sai:

```jsp
<%
    List<User> users = userDAO.findAll();
    for (User user : users) {
        out.println(user.getFullName());
    }
%>
```

* TUYỆT ĐỐI KHÔNG gọi DAO trực tiếp trong JSP.
* TUYỆT ĐỐI KHÔNG viết SQL trong JSP.
* TUYỆT ĐỐI KHÔNG xử lý đăng nhập trong JSP.
* TUYỆT ĐỐI KHÔNG xử lý phân quyền trong JSP.
* TUYỆT ĐỐI KHÔNG đặt business logic trong JSP.

JSP chỉ được dùng để hiển thị dữ liệu đã được Servlet truyền sang.

## Java / Backend

TUYỆT ĐỐI KHÔNG dùng:

```java
System.out.println()
```

BẮT BUỘC dùng SLF4J Logger.

Ví dụ:

```java
private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
```

TUYỆT ĐỐI KHÔNG trả message của Exception thô ra giao diện hoặc client.

Sai:

```java
request.setAttribute("error", e.getMessage());
```

Đúng:

```java
logger.error("Error while processing request", e);
request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau.");
```

TUYỆT ĐỐI KHÔNG đặt business logic trong Servlet.

Servlet chỉ được:

* Nhận request.
* Lấy parameter.
* Validate cơ bản.
* Gọi Service.
* Forward sang JSP hoặc redirect.

Business logic PHẢI nằm trong tầng Service.

SQL PHẢI nằm trong tầng DAO.

## Servlet

* TUYỆT ĐỐI KHÔNG viết SQL trong Servlet.
* TUYỆT ĐỐI KHÔNG xử lý nghiệp vụ phức tạp trong Servlet.
* TUYỆT ĐỐI KHÔNG hardcode `userId` trong Servlet.
* TUYỆT ĐỐI KHÔNG tự kiểm tra phân quyền lặp lại ở nhiều Servlet nếu có thể xử lý bằng Filter.

Sai:

```java
int userId = 1;
```

Đúng:

```java
HttpSession session = request.getSession(false);
User currentUser = (User) session.getAttribute("currentUser");
```

Servlet cần đăng nhập PHẢI được bảo vệ bởi:

```text
AuthenticationFilter.java
```

Servlet cần phân quyền PHẢI được bảo vệ bởi:

```text
RoleFilter.java
```

## Service

* TUYỆT ĐỐI KHÔNG viết SQL trực tiếp trong Service.
* Service chỉ xử lý nghiệp vụ và gọi DAO.
* TUYỆT ĐỐI KHÔNG nhận trực tiếp `HttpServletRequest` hoặc `HttpServletResponse` trong Service.
* TUYỆT ĐỐI KHÔNG để Service phụ thuộc vào JSP.

Sai:

```java
public List<User> getUsers(HttpServletRequest request) {
    // xử lý request trong service
}
```

Đúng:

```java
public List<User> getUsers() {
    return userDAO.findAll();
}
```

## DAO / JDBC

TẤT CẢ truy vấn database PHẢI nằm trong DAO.

TUYỆT ĐỐI KHÔNG nối chuỗi SQL trực tiếp từ input người dùng.

BẮT BUỘC dùng `PreparedStatement`.

Sai:

```java
String sql = "SELECT * FROM users WHERE email = '" + email + "'";
```

Đúng:

```java
String sql = "SELECT * FROM users WHERE email = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, email);
```

TUYỆT ĐỐI KHÔNG bỏ quên đóng `Connection`, `PreparedStatement`, `ResultSet`.

Ưu tiên dùng try-with-resources.

Đúng:

```java
try (
    Connection conn = DBConnectionUtil.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery()
) {
    while (rs.next()) {
        // map result set
    }
}
```

## Xác thực và phân quyền

Dự án dùng Session-Based Authentication.

TUYỆT ĐỐI KHÔNG dùng JWT nếu chưa được team thống nhất.

TUYỆT ĐỐI KHÔNG dùng:

```text
SecurityContextHolder
JwtAuthFilter
JwtConfig.java
SecurityConfig.java
```

Nếu dự án đang là Jakarta Servlet thuần.

TUYỆT ĐỐI KHÔNG lưu thông tin nhạy cảm trong session như:

* `password`
* `passwordHash`
* `token`
* Mã OTP

Trong session chỉ nên lưu thông tin cần thiết:

```java
session.setAttribute("currentUser", user);
```

Hoặc tốt hơn là lưu object rút gọn:

```java
session.setAttribute("currentUser", userSessionDTO);
```

Khi logout, BẮT BUỘC hủy session:

```java
session.invalidate();
```

## Bảo mật

TUYỆT ĐỐI KHÔNG hardcode:

* Username database
* Password database
* Chuỗi kết nối database
* API key
* Secret key

Thông tin nhạy cảm PHẢI lấy từ biến môi trường hoặc file cấu hình không commit lên Git.

Ví dụ:

```env
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhaTro
DB_USERNAME=sa
DB_PASSWORD=your_password
```

TUYỆT ĐỐI KHÔNG commit file chứa secret thật lên Git.

TUYỆT ĐỐI KHÔNG ghi log các trường nhạy cảm:

* `password`
* `passwordHash`
* `token`
* `OTP`
* Số CCCD/CMND nếu không cần thiết
* Thông tin cá nhân nhạy cảm

Sai:

```java
logger.info("Login password: " + password);
```

Đúng:

```java
logger.info("User login attempt with email: {}", email);
```

## Database

TUYỆT ĐỐI KHÔNG tự động chạy các lệnh nguy hiểm nếu chưa có xác nhận:

```sql
DROP TABLE
DELETE FROM
TRUNCATE TABLE
ALTER TABLE
```

TUYỆT ĐỐI KHÔNG chạy migration script khi chưa được Tech Lead hoặc người phụ trách database phê duyệt.

TUYỆT ĐỐI KHÔNG sửa trực tiếp database production nếu chưa backup.

Script database phải được lưu trong thư mục:

```text
database/
├── schema.sql
├── seed.sql
└── migration/
```

## Các file/class không dùng trong cấu trúc này

KHÔNG dùng:

```text
SecurityConfig.java
JwtConfig.java
GlobalExceptionHandler.java
```

Nếu dự án đang là Jakarta Servlet thuần.

KHÔNG dùng:

```text
Repository
JDBC Template
Hibernate
JPA
Spring Security
```

Nếu project đã chốt là JSP + Servlet + JDBC thuần.

Với JDBC thuần, dùng:

```text
DAO
PreparedStatement
DBConnectionUtil
AuthenticationFilter
RoleFilter
HttpSession
```

---

# 6. ĐỊNH NGHĨA HOÀN THÀNH

Trước khi báo xong việc, PHẢI tự kiểm tra toàn bộ checklist sau:

## Backend — Jakarta Servlet + JDBC

* Servlet xử lý đúng luồng request/response.
* Servlet không chứa business logic phức tạp.
* Servlet không viết SQL trực tiếp.
* Service đã xử lý đúng nghiệp vụ.
* DAO chứa toàn bộ truy vấn database.
* DAO sử dụng PreparedStatement, không nối chuỗi SQL từ input người dùng.
* Connection, PreparedStatement, ResultSet được đóng đúng bằng try-with-resources.
* Các nghiệp vụ nhiều bước đã có transaction nếu cần.
* Không còn `System.out.println()` trong code.
* Đã dùng SLF4J Logger để log lỗi.

## Frontend — JSP/JSTL

* JSP hiển thị đúng dữ liệu do Servlet truyền sang.
* JSP không gọi trực tiếp DAO.
* JSP không viết SQL.
* JSP không chứa business logic.
* Hạn chế tối đa scriptlet `<% %>`.
* Ưu tiên dùng JSTL và Expression Language.
* Các file JSP quan trọng được đặt trong:

```text
/WEB-INF/views/
```

* Giao diện hoạt động đúng trên Bootstrap 5.

## Authentication / Authorization

* Đăng nhập bằng Session hoạt động đúng.
* Sau khi đăng nhập, user được lưu vào HttpSession.
* Không hardcode userId.
* Không lưu password/passwordHash/token/OTP trong session.
* Logout đã gọi `session.invalidate()`.
* AuthenticationFilter chặn được các trang yêu cầu đăng nhập.
* RoleFilter chặn được các trang không đúng quyền.
* User không có quyền được chuyển đến trang `403.jsp`.
* User chưa đăng nhập được chuyển về trang `/login`.

## Database

* Database schema chạy được trên SQL Server 2022.
* Script tạo bảng được lưu trong:

```text
database/schema.sql
```

* Script dữ liệu mẫu được lưu trong:

```text
database/seed.sql
```

* Không tự động chạy `DROP`, `DELETE`, `TRUNCATE`, `ALTER` nếu chưa được xác nhận.
* Các khóa chính, khóa ngoại, unique, check constraint đã được kiểm tra.
* Dữ liệu mẫu đủ để test các màn hình chính.

## Testing

* Unit test đã viết cho các hàm Service quan trọng bằng JUnit 5 + Mockito.
* DAO đã được test với ít nhất các trường hợp chính.
* Login test đủ các case:

  * Đăng nhập đúng.
  * Sai mật khẩu.
  * Tài khoản không tồn tại.
  * Tài khoản bị khóa.
* Phân quyền test đủ các role:

  * ADMIN
  * MANAGER
  * OPERATOR
  * TENANT
* Các chức năng CRUD chính đã test:

  * Users
  * Facilities
  * Rooms
  * Dependents
  * Tickets
  * Notifications
* `mvn test` chạy thành công, không có lỗi.
* Build file `.war` thành công.
* Deploy được lên Apache Tomcat 10.x.

## Code Quality

* Không còn comment TODO / FIXME trong code được nộp.
* Không có code trùng lặp nghiêm trọng.
* Không có secret thật trong source code.
* Không commit file `.env` hoặc file chứa mật khẩu thật.
* Không log thông tin nhạy cảm:

  * `password`
  * `passwordHash`
  * `token`
  * `OTP`
  * Thông tin cá nhân nhạy cảm
* Tên package, class, method rõ ràng, đúng quy ước.
* Code đã format trước khi commit.

## Git / Pull Request

* Code được commit lên nhánh riêng.
* PR nhắm vào nhánh `develop`.
* TUYỆT ĐỐI KHÔNG nhắm vào `main`, `master` hoặc `production`.
* Commit message đúng Conventional Commits.
* Không gộp nhiều tính năng không liên quan vào một commit.
* Nội dung PR mô tả rõ:

  * Đã làm gì.
  * Đã test gì.
  * Còn hạn chế gì nếu có.

---

# 7. QUY ƯỚC GIT

## Đặt tên nhánh

Tên nhánh phải theo cấu trúc:

```text
<type>/<ticket-id>-mo-ta-ngan
```

Các loại nhánh hợp lệ:

```text
feature/
bugfix/
hotfix/
refactor/
docs/
test/
chore/
```

Ví dụ:

```text
feature/HOMEX-42-them-dang-nhap-session
bugfix/HOMEX-55-sua-loi-phan-quyen
refactor/HOMEX-61-tach-user-dao
docs/HOMEX-70-cap-nhat-db-schema
test/HOMEX-73-them-test-user-service
```

## Commit message

Commit message phải theo Conventional Commits:

```text
<type>(<scope>): <noi-dung>
```

Các loại commit hợp lệ:

```text
feat
fix
refactor
test
docs
chore
style
```

Ví dụ đúng:

```text
feat(auth): them dang nhap bang session
fix(user): ngan trung lap email khi tao tai khoan
refactor(apartment): tach logic xu ly sang service
test(ticket): them unit test cho ticket service
docs(db): cap nhat schema chung cu
chore(config): cap nhat cau hinh tomcat
style(jsp): format lai giao dien login
```

Ví dụ không nên dùng:

```text
update code
fix bug
done
sua loi
commit lan 1
```

## Quy tắc commit

* TUYỆT ĐỐI KHÔNG commit trực tiếp lên `main` hoặc `master`.
* TUYỆT ĐỐI KHÔNG commit secret, mật khẩu, file `.env`.
* TUYỆT ĐỐI KHÔNG gộp nhiều tính năng khác nhau vào một commit.
* Mỗi commit nên đại diện cho một thay đổi rõ ràng.
* TẤT CẢ commit nên tham chiếu ticket ID trong scope hoặc phần body nếu có.

Trước khi commit, phải chạy:

```bash
mvn test
```

Nếu có thay đổi database, phải ghi rõ trong commit hoặc PR.

## Pull Request

Tất cả PR phải nhắm vào nhánh:

```text
develop
```

TUYỆT ĐỐI KHÔNG tạo PR trực tiếp vào:

```text
main
master
production
```

PR phải có mô tả rõ ràng:

```markdown
## Đã làm

- ...

## Đã test

- ...

## Ảnh màn hình nếu có

- ...

## Ghi chú

- ...
```

---

# 8. BỐI CẢNH SPRINT HIỆN TẠI

## Sprint 1

| Field        | Value                                                                                         |
| ------------ | --------------------------------------------------------------------------------------------- |
| Sprint       | Sprint 1 — Foundation                                                                         |
| Focus        | Setup project structure, Session Auth, Role Filter, DB schema                                 |
| Tech Stack   | JSP + Jakarta Servlet + JDBC + SQL Server                                                     |
| Server       | Apache Tomcat 10.x                                                                            |
| Active specs | `docs/specs/auth.spec.md`, `docs/specs/db-schema.spec.md`                                     |
| Blocked      | Cần chốt chi tiết role và luồng phân quyền cho ADMIN, MANAGER, TENANT, OPERATOR|

## Mục tiêu Sprint 1

* Setup project Maven Web Application.
* Cấu hình chạy được trên Apache Tomcat 10.x.
* Tạo cấu trúc package chuẩn:

  * `controller/`
  * `service/`
  * `service/impl/`
  * `dao/`
  * `model/`
  * `dto/`
  * `filter/`
  * `util/`
  * `exception/`
  * `constant/`
* Tạo cấu trúc JSP chuẩn:

```text
src/main/webapp/WEB-INF/views/
```

* Tạo database schema ban đầu cho:

  * `users`
  * `facilities`
  * `rooms`
  * `dependents`
  * `user_buildings`
  * `work_schedules`
  * `notifications`
  * `tickets`
  * `ticket_attachments`
* Làm chức năng đăng nhập bằng Session.
* Làm chức năng logout.
* Làm `AuthenticationFilter`.
* Làm `RoleFilter`.
* Làm trang lỗi:

  * `403.jsp`
  * `404.jsp`
  * `500.jsp`
* Làm dashboard cơ bản theo role.
* Tạo dữ liệu mẫu để test.

## Ghi chú Sprint

Sprint này chưa ưu tiên giao diện đẹp hoàn chỉnh.

Sprint này ưu tiên:

* Chạy đúng kiến trúc.
* Login/logout ổn định.
* Phân quyền đúng.
* Database đúng quan hệ.
* CRUD cơ bản chạy được.
* Không thêm JWT nếu chưa được team thống nhất.
* Không thêm Spring Boot nếu stack đã chốt là Jakarta Servlet thuần.
* Không dùng React nếu frontend đã chốt là JSP/JSTL.
* Cập nhật section này vào đầu mỗi sprint.
