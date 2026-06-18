# Owner: @tech-lead | Version: 1.0.1

## 1. TECHNOLOGY STACK (Bất biến trừ khi có RFC)
### Backend & Core
- Language: Java 17+
- HTTP & Web: Java Servlet API (javax.servlet.* hoặc jakarta.servlet.*)
- View Engine: JSP (JavaServer Pages) kết hợp bắt buộc với JSTL 1.2+
- Web Server: Apache Tomcat 10.1.x
- Build Tool: Apache Ant (sử dụng file `build.xml`. TUYỆT ĐỐI KHÔNG dùng Maven/pom.xml)

### Data & Utilities
- Database: Microsoft SQL Server 2022
- Data Access: JDBC thuần (sử dụng PreparedStatement để chống SQL Injection)
- JSON Handling: com.google.gson (Gson)
- Testing: JUnit 5

## 2. NAMING CONVENTIONS
- **Packages:** chữ thường toàn bộ, phân chia theo layer (VD: `controller`, `service`, `dao`, `dto`).
- **Java Classes/Interfaces:** `PascalCase`.
  - Servlet bắt buộc có hậu tố `Servlet` (VD: `ExportReportServlet.java`).
  - Lớp truyền dữ liệu bắt buộc có hậu tố `DTO` (VD: `ReportDTO.java`).
  - Lớp truy xuất CSDL bắt buộc có hậu tố `DAO` (VD: `ReportDAO.java`).
- **JSP Files:** `kebab-case.jsp` (VD: `report-form.jsp`, `export-result.jsp`). Tuyệt đối không dùng camelCase hay khoảng trắng.
- **Variables/Methods:** `camelCase` (VD: `exportReport()`, `startDate`).
- **Constants:** `SCREAMING_SNAKE_CASE` (VD: `MAX_UPLOAD_SIZE`).

## 3. APPROVED EXTERNAL PACKAGES (Danh sách cho phép)
AI CHỈ được phép sử dụng các thư viện sau trong `pom.xml`:
- `javax.servlet-api` / `jakarta.servlet-api` (Core Web)
- `jstl` và `standard` (Cho các thẻ `<c:forEach>`, `<c:if>` trong JSP)
- `com.google.code.gson` (Xử lý chuỗi JSON)
- `com.microsoft.sqlserver:mssql-jdbc` (Database Driver chính thức cho SQL Server)
- `org.junit.jupiter` (Testing)

## 4. BANNED PACKAGES & ANTI-PATTERNS (Danh sách CẤM)
AI **TUYỆT ĐỐI KHÔNG ĐƯỢC** sử dụng các công nghệ sau:
- **Cấm:** `org.springframework.*` / Spring Boot. (Lý do: Dự án sử dụng Servlet thuần, không dùng framework cấp cao).
- **Cấm:** `org.hibernate.*` / JPA / Entity Manager. (Lý do: Bắt buộc dùng JDBC thuần để kiểm soát câu truy vấn T-SQL).
- **Cấm Anti-pattern JSP:** Tuyệt đối KHÔNG viết mã Java Scriptlet `<% ... %>` trực tiếp vào file JSP. (Lý do: Phải tách biệt logic và view. Bắt buộc dùng JSTL và EL `${...}`).

## 5. ADDING NEW PACKAGES (Quy trình thêm thư viện)
- Quy trình: Bất kỳ thư viện nào mới đều phải được viết thành RFC (Request for Comment) -> Tech Lead duyệt -> Update file này.
- AI Agent **KHÔNG ĐƯỢC PHÉP** tự ý thêm dependency mới vào `pom.xml` dưới bất kỳ hình thức nào nếu chưa có sự đồng ý của con người.