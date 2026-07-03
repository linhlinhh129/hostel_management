# AGENTS.md — Quanlynhatro (HomeX)

> **Phiên bản:** 1.0
> **Cập nhật:** 2026-05
> **Sprint:** 1

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan:
[plan.md](file:///F:/SU26/New%20folder/hostel_management/specs/001-sync-notification-ui/plan.md)
<!-- SPECKIT END -->

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
| Cơ sở dữ liệu | SQL Server 2022                                                                      |
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

```text
/login
/logout
/dashboard
/users, /users/create, /users/edit
/rooms, /rooms/create
/tickets, /tickets/create
```

Với các chức năng quản trị, dùng prefix:

```text
/admin/users
/admin/facilities
/admin/rooms
/admin/reports
```

Nếu có API trả JSON thì dùng pattern:

```text
/api/v1/{resource}
```

## Response

Với request hiển thị giao diện:

```java
request.getRequestDispatcher("/WEB-INF/views/page.jsp").forward(request, response);
// hoặc
response.sendRedirect(request.getContextPath() + "/dashboard");
```

Với API trả JSON:

```json
{
  "success": true,
  "data": {},
  "error": null,
  "meta": {}
}
```

## Xử lý lỗi

TẤT CẢ lỗi phải được xử lý tập trung, không hiển thị exception thô cho người dùng.

* TUYỆT ĐỐI KHÔNG dùng `System.out.println()` để debug.
* BẮT BUỘC dùng SLF4J Logger:

```java
private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
```

* TUYỆT ĐỐI KHÔNG trả message exception thô ra giao diện:

```java
// Sai
request.setAttribute("error", e.getMessage());

// Đúng
logger.error("Error while processing user request", e);
request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau.");
```

## Xác thực

* Dùng Session-Based Authentication.
* TUYỆT ĐỐI KHÔNG dùng JWT nếu chưa được team thống nhất.
* TUYỆT ĐỐI KHÔNG hardcode `userId`.
* Sau khi đăng nhập: `session.setAttribute("currentUser", user);`
* Khi logout: `session.invalidate();`
* Trang cần đăng nhập đi qua `AuthenticationFilter.java`.
* Trang cần phân quyền đi qua `RoleFilter.java`.

## Phân quyền

* Kiểm tra quyền trong Filter hoặc Service, không hardcode trong JSP.
* User chưa đăng nhập → redirect `/login`.
* User không có quyền → forward `/WEB-INF/views/error/403.jsp`.

## Cơ sở dữ liệu

* TẤT CẢ truy vấn database đi qua tầng DAO.
* TUYỆT ĐỐI KHÔNG viết SQL trong Servlet, JSP, hoặc Service.
* BẮT BUỘC dùng `PreparedStatement`.
* TUYỆT ĐỐI KHÔNG nối chuỗi SQL từ input người dùng.
* Dùng try-with-resources để đóng `Connection`, `PreparedStatement`, `ResultSet`.

## Transaction

Với nghiệp vụ nhiều bước liên quan database:

```java
conn.setAutoCommit(false);
try {
    // thao tác 1, 2...
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
}
```

## Quy tắc phân tầng

```text
Browser → Servlet → Service → DAO → Database → DAO → Service → Servlet → JSP → Browser
```

---

# 4. QUY TẮC ĐẶT TÊN FILE & CẤU TRÚC

## Backend Java

```text
src/main/java/com/quanlynhatro/
├── controller/        # {Resource}Servlet.java
├── service/           # {Resource}Service.java
├── service/impl/      # {Resource}ServiceImpl.java
├── dao/               # {Resource}DAO.java
├── model/             # {Resource}.java
├── dto/               # {Resource}DTO.java
├── filter/            # AuthenticationFilter.java, RoleFilter.java
├── util/              # DBConnectionUtil.java, PasswordUtil.java
├── exception/         # AppException.java, ValidationException.java
└── constant/          # RoleConstant.java, StatusConstant.java
```

## Frontend JSP

```text
src/main/webapp/
├── WEB-INF/
│   ├── views/
│   │   ├── layout/     # header.jsp, sidebar.jsp, footer.jsp
│   │   ├── auth/       # login.jsp
│   │   ├── dashboard/  # index.jsp
│   │   ├── users/      # list.jsp, create.jsp, edit.jsp, detail.jsp
│   │   ├── facilities/
│   │   ├── rooms/
│   │   ├── tickets/
│   │   └── error/      # 403.jsp, 404.jsp, 500.jsp
│   └── web.xml
├── assets/
│   ├── css/
│   ├── js/
│   ├── images/
│   └── vendor/
└── index.jsp
```

## Database Script

```text
database/
├── schema.sql
├── seed.sql
└── migration/
```

## Quy tắc đặt tên JSP

* Tên file JSP dùng chữ thường, nhiều từ dùng dấu gạch ngang `-`.
* Đúng: `login.jsp`, `list.jsp`, `change-password.jsp`
* Không dùng: `UserList.jsp`, `user_list.jsp`

---

# 5. CÁC MẪU BỊ CẤM

## JSP / Frontend

* TUYỆT ĐỐI KHÔNG dùng React, TypeScript (`.ts`, `.tsx`), hay các thư mục `components/`, `pages/`, `hooks/`.
* TUYỆT ĐỐI KHÔNG viết Java phức tạp trong JSP, hạn chế tối đa scriptlet `<% %>`.
* Ưu tiên JSTL và Expression Language:

```jsp
<c:forEach var="user" items="${users}">
    <tr><td>${user.fullName}</td><td>${user.email}</td></tr>
</c:forEach>
```

## Java / Backend

* TUYỆT ĐỐI KHÔNG dùng `System.out.println()` — BẮT BUỘC SLF4J Logger.
* TUYỆT ĐỐI KHÔNG đặt business logic trong Servlet.
* TUYỆT ĐỐI KHÔNG viết SQL trong Servlet hoặc Service.
* TUYỆT ĐỐI KHÔNG nhận `HttpServletRequest`/`HttpServletResponse` trong Service.
* TUYỆT ĐỐI KHÔNG hardcode `userId`.

## Xác thực & Bảo mật

* TUYỆT ĐỐI KHÔNG dùng JWT, `SecurityContextHolder`, `JwtAuthFilter`, `SecurityConfig.java`.
* TUYỆT ĐỐI KHÔNG lưu `password`, `passwordHash`, `token`, `OTP` trong session.
* TUYỆT ĐỐI KHÔNG hardcode DB username/password, API key, secret key trong code.
* TUYỆT ĐỐI KHÔNG ghi log các trường nhạy cảm (`password`, `token`, OTP, CCCD).
* TUYỆT ĐỐI KHÔNG commit file `.env` hoặc file chứa secret thật lên Git.

## Database

* TUYỆT ĐỐI KHÔNG tự động chạy `DROP`, `DELETE`, `TRUNCATE`, `ALTER` khi chưa được xác nhận.
* TUYỆT ĐỐI KHÔNG chạy migration script khi chưa được Tech Lead phê duyệt.
* TUYỆT ĐỐI KHÔNG sửa trực tiếp database production khi chưa backup.

## Class không dùng trong stack này

```text
SecurityConfig.java, JwtConfig.java, GlobalExceptionHandler.java
Repository, JdbcTemplate, Hibernate, JPA, Spring Security
```

---

# 6. ĐỊNH NGHĨA HOÀN THÀNH

Trước khi báo xong việc, PHẢI tự kiểm tra toàn bộ checklist sau:

## Backend

* [ ] Servlet xử lý đúng luồng request/response, không chứa business logic, không viết SQL.
* [ ] Service xử lý đúng nghiệp vụ.
* [ ] DAO chứa toàn bộ SQL, dùng PreparedStatement, try-with-resources.
* [ ] Transaction cho nghiệp vụ nhiều bước.
* [ ] Không còn `System.out.println()`, đã dùng SLF4J Logger.

## Frontend JSP

* [ ] JSP hiển thị đúng dữ liệu Servlet truyền sang, không gọi DAO, không viết SQL, không có business logic.
* [ ] Hạn chế scriptlet, ưu tiên JSTL/EL.
* [ ] JSP đặt trong `/WEB-INF/views/`, giao diện đúng Bootstrap 5.

## Auth / Authorization

* [ ] Session đăng nhập hoạt động đúng, không hardcode userId.
* [ ] Không lưu password/token/OTP trong session.
* [ ] Logout gọi `session.invalidate()`.
* [ ] AuthenticationFilter và RoleFilter hoạt động đúng.

## Database

* [ ] Schema chạy được trên SQL Server 2022, lưu đúng thư mục `database/`.
* [ ] Không tự chạy DROP/DELETE/TRUNCATE/ALTER khi chưa xác nhận.

## Testing

* [ ] Unit test Service bằng JUnit 5 + Mockito.
* [ ] `mvn test` chạy thành công.
* [ ] Build `.war` thành công, deploy được lên Tomcat 10.x.

## Code Quality

* [ ] Không còn TODO/FIXME trong code nộp.
* [ ] Không có secret trong source code.
* [ ] Tên package/class/method rõ ràng, đúng quy ước.

## Git

* [ ] Commit lên nhánh riêng, PR nhắm vào `develop`.
* [ ] Commit message đúng Conventional Commits.
* [ ] Không commit `.env` hoặc file secret.

---

# 7. QUY ƯỚC GIT

## Đặt tên nhánh

```text
<type>/<ticket-id>-mo-ta-ngan
```

Các loại: `feature/`, `bugfix/`, `hotfix/`, `refactor/`, `docs/`, `test/`, `chore/`

Ví dụ: `feature/HOMEX-42-them-dang-nhap-session`

## Commit message (Conventional Commits)

```text
<type>(<scope>): <noi-dung>
```

Các loại: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`

Ví dụ:
```text
feat(auth): them dang nhap bang session
fix(user): ngan trung lap email khi tao tai khoan
```

## Quy tắc

* TUYỆT ĐỐI KHÔNG commit trực tiếp lên `main` hoặc `master`.
* Tất cả PR nhắm vào nhánh `develop`.
* Chạy `mvn test` trước khi commit.
* PR phải có mô tả: đã làm gì, đã test gì, còn hạn chế gì.

---

# 8. BỐI CẢNH SPRINT HIỆN TẠI

## Sprint 1 — Foundation

| Field        | Value                                                                        |
| ------------ | ---------------------------------------------------------------------------- |
| Sprint       | Sprint 1 — Foundation                                                        |
| Focus        | Setup project structure, Session Auth, Role Filter, DB schema                |
| Tech Stack   | JSP + Jakarta Servlet + JDBC + SQL Server                                    |
| Server       | Apache Tomcat 10.x                                                           |
| Active specs | `docs/specs/auth.spec.md`, `docs/specs/db-schema.spec.md`                   |
| Blocked      | Cần chốt chi tiết role và luồng phân quyền cho ADMIN, MANAGER, TENANT, OPERATOR |

## Mục tiêu Sprint 1

* Setup Maven Web Application, chạy được trên Tomcat 10.x.
* Tạo cấu trúc package chuẩn.
* Tạo DB schema: `users`, `facilities`, `rooms`, `dependents`, `user_buildings`, `work_schedules`, `notifications`, `tickets`, `ticket_attachments`.
* Làm chức năng đăng nhập/logout bằng Session.
* Làm `AuthenticationFilter` và `RoleFilter`.
* Làm trang lỗi: `403.jsp`, `404.jsp`, `500.jsp`.
* Dashboard cơ bản theo role, dữ liệu mẫu để test.

## Ghi chú Sprint

Sprint này ưu tiên: kiến trúc đúng, login/logout ổn định, phân quyền đúng, DB đúng quan hệ, CRUD cơ bản chạy được. Không thêm JWT, Spring Boot, React nếu stack đã chốt.

> Cập nhật section này vào đầu mỗi sprint.
