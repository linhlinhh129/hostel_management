# Graph Report - hostel_management  (2026-08-04)

## Corpus Check
- 206 files · ~109,183 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2978 nodes · 7617 edges · 130 communities (88 shown, 42 thin omitted)
- Extraction: 71% EXTRACTED · 29% INFERRED · 0% AMBIGUOUS · INFERRED: 2206 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5507d6a1`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .getId
- DebtDetailDTO
- Invoice
- Notification
- Request
- NotificationServiceImpl
- Contract
- CommentDTO
- DebtListItemDTO
- NewsFeedDTO
- CommunityPost
- RoomDetailDTO
- CommunityPostDTO
- Room
- .mapRow
- PersonnelDAO
- PaymentDetailDTO
- .getConnection
- Facility
- MeterReading
- PaymentTransaction
- AuditLog
- Dependent
- ServicePriceHistoryDTO
- ManagerTicketsServlet
- InvoiceDetailDTO
- InvoiceDAO
- FacilityRevenueStatDTO
- UserSessionDTO
- RoomService
- TenantServiceImpl
- RequestServiceImpl
- .doGet
- PostComment
- ContractService
- PostInteractionService
- MeterStatusDTO
- AdminFacilityServlet
- .doGet
- InvoiceListItemDTO
- PasswordUtil.java
- AdminNotificationServlet
- EmailConfigDTO
- .sendError
- AdminAuditLogServlet
- RequestService
- HttpSession
- MeterReadingHistoryServlet.java
- AdminDashboardServiceImpl
- NewsFeedServiceImpl
- RoomDTO
- .count
- NotificationDAO
- .doGet
- PaymentListItemDTO
- ServicePriceDTO
- PageResult
- PaymentServlet.java
- PageDTO
- PersonnelFormDTO
- .sendRemindNotification
- CommunityPostServiceImpl
- PostCommentDTO
- HttpServlet
- .login
- AuthFilter
- .doPost
- UpdateMeterReadingServlet.java
- AppException
- VNPayConfigDTO
- PostReactionDAO
- UserServiceImpl.java
- EditIncidentReportServlet.java
- SystemConfigDAO
- DatabaseUtil.java
- ManagerDashboardServlet.java
- NewsFeedApiServlet.java
- .doPost
- PreparedStatement
- EmailService
- CommunityPostService
- TenantCommentServlet.java
- RoomDAO
- RoomOccupancyStatDTO
- .insert
- PostReactionDTO
- CommunityPostServiceImpl.java
- .doPost
- TenantPostDetailServlet.java
- ResetPasswordServlet.java
- BaseDAO
- AdminSystemConfigServlet.java
- .doPost
- DebtPageServlet.java
- MeterReadingService
- Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết
- BaseServlet
- ContractDAO
- Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy
- Q: giải thích phần jsp của logout qua graphify
- NotificationListServlet.java
- EncodingFilter
- Q: chỉ class LoginServlet giống lệnh graphify
- Q: giải thích các hàm trong LoginServlet
- AdminPersonnelServlet
- Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend
- UserDAO
- PaymentService
- .doGet
- PostReaction
- ResetTokenManager.java
- ResetPasswordApiServlet.java
- DependentDAO
- PasswordValidator.java
- TenantLikeServlet.java
- Q: giải thích luồng forgot-password
- Q: JSP của ProfileServlet nằm ở đâu
- Q: Giải thích luồng ProfileServlet và profile.jsp
- DebtDAO.java
- .unlockTenantAccount
- User
- SQLFixtureHelper
- sw.js

## God Nodes (most connected - your core abstractions)
1. `Invoice` - 106 edges
2. `BaseServlet` - 97 edges
3. `Contract` - 96 edges
4. `InvoiceDetailDTO` - 95 edges
5. `DebtDetailDTO` - 88 edges
6. `Facility` - 84 edges
7. `User` - 80 edges
8. `Request` - 76 edges
9. `Notification` - 65 edges
10. `Room` - 64 edges

## Surprising Connections (you probably didn't know these)
- `AdminAuditLogServlet` --inherits--> `BaseServlet`  [EXTRACTED]
  src/main/java/com/quanlyphongtro/controller/admin/AdminAuditLogServlet.java → src/main/java/com/quanlyphongtro/controller/BaseServlet.java
- `AdminDashboardServlet` --inherits--> `BaseServlet`  [EXTRACTED]
  src/main/java/com/quanlyphongtro/controller/admin/AdminDashboardServlet.java → src/main/java/com/quanlyphongtro/controller/BaseServlet.java
- `AdminFacilityServlet` --inherits--> `BaseServlet`  [EXTRACTED]
  src/main/java/com/quanlyphongtro/controller/admin/AdminFacilityServlet.java → src/main/java/com/quanlyphongtro/controller/BaseServlet.java
- `AdminNotificationServlet` --inherits--> `BaseServlet`  [EXTRACTED]
  src/main/java/com/quanlyphongtro/controller/admin/AdminNotificationServlet.java → src/main/java/com/quanlyphongtro/controller/BaseServlet.java
- `AdminPersonnelServlet` --inherits--> `BaseServlet`  [EXTRACTED]
  src/main/java/com/quanlyphongtro/controller/admin/AdminPersonnelServlet.java → src/main/java/com/quanlyphongtro/controller/BaseServlet.java

## Import Cycles
- None detected.

## Communities (130 total, 42 thin omitted)

### Community 0 - ".getId"
Cohesion: 0.17
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerTenantsServlet, TenantService

### Community 5 - "Request"
Cohesion: 0.07
Nodes (4): HttpServletRequest, HttpServletResponse, Override, Request

### Community 12 - "CommunityPost"
Cohesion: 0.08
Nodes (4): CommunityPostCreateDTO, CommunityPost, Part, Part

### Community 15 - "Room"
Cohesion: 0.07
Nodes (3): Override, ResultSet, Room

### Community 17 - "PersonnelDAO"
Cohesion: 0.12
Nodes (4): PersonnelDAO, Logger, Override, PersonnelServiceImpl

### Community 19 - ".getConnection"
Cohesion: 0.12
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListRequestServlet, Override, RequestDAO

### Community 20 - "Facility"
Cohesion: 0.07
Nodes (7): AuditLogDAO, FacilityDAO, Connection, Facility, Override, Override, ServicePriceServiceImpl

### Community 21 - "MeterReading"
Cohesion: 0.07
Nodes (3): ResultSet, MeterReadingDAO, MeterReading

### Community 26 - "ManagerTicketsServlet"
Cohesion: 0.21
Nodes (9): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ManagerTicketsServlet, Pattern (+1 more)

### Community 28 - "InvoiceDAO"
Cohesion: 0.14
Nodes (7): InvoiceDAO, InvoicePriceSnapshot, InvoiceRoomSnapshot, Connection, ResultSet, InvoiceServiceImpl, Override

### Community 29 - "FacilityRevenueStatDTO"
Cohesion: 0.05
Nodes (11): AdminRevenueServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueDAO, FacilityRevenueStatDTO, SystemRevenueDTO (+3 more)

### Community 31 - "RoomService"
Cohesion: 0.14
Nodes (9): HttpServletRequest, HttpServletResponse, Override, HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerRoomsServlet (+1 more)

### Community 33 - "RequestServiceImpl"
Cohesion: 0.19
Nodes (3): Logger, Override, RequestServiceImpl

### Community 34 - ".doGet"
Cohesion: 0.18
Nodes (5): InvoiceDetailServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 37 - "ContractService"
Cohesion: 0.13
Nodes (11): ContractServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, HttpServletResponse, Override (+3 more)

### Community 38 - "PostInteractionService"
Cohesion: 0.14
Nodes (13): HttpServletRequest, HttpServletResponse, Logger, Override, WebServlet, PostCommentServlet, HttpServletRequest, HttpServletResponse (+5 more)

### Community 40 - "AdminFacilityServlet"
Cohesion: 0.07
Nodes (15): AdminFacilityServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, FacilityFormDTO, HttpServletRequest, FilterChain (+7 more)

### Community 41 - ".doGet"
Cohesion: 0.18
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDependentServlet, DependentService

### Community 42 - "InvoiceListItemDTO"
Cohesion: 0.08
Nodes (7): InvoiceServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, InvoiceListItemDTO, InvoiceService

### Community 43 - "PasswordUtil.java"
Cohesion: 0.13
Nodes (10): Filter, SecureRandom, ErrorMessageConstant, CsrfFilter, FilterChain, Override, ServletRequest, ServletResponse (+2 more)

### Community 44 - "AdminNotificationServlet"
Cohesion: 0.38
Nodes (4): AdminNotificationServlet, HttpServletRequest, HttpServletResponse, Override

### Community 46 - ".sendError"
Cohesion: 0.12
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerNotificationsServlet, Override, NotificationService

### Community 47 - "AdminAuditLogServlet"
Cohesion: 0.19
Nodes (6): AdminAuditLogServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AuditLogService

### Community 48 - "RequestService"
Cohesion: 0.10
Nodes (11): DetailRequestServlet, HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, RequestService (+3 more)

### Community 49 - "HttpSession"
Cohesion: 0.24
Nodes (9): HttpSession, HttpSessionAttributeListener, HttpSessionBindingEvent, HttpSessionEvent, HttpSessionListener, Override, WebListener, UserSessionListener (+1 more)

### Community 50 - "MeterReadingHistoryServlet.java"
Cohesion: 0.23
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, MeterReadingHistoryServlet, Timestamp

### Community 52 - "NewsFeedServiceImpl"
Cohesion: 0.15
Nodes (8): CommentDAO, Logger, Logger, NewsFeedDAO, Logger, ReactionDAO, Override, NewsFeedServiceImpl

### Community 56 - ".doGet"
Cohesion: 0.11
Nodes (8): AdminDashboardServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueActivityDTO, AdminDashboardService, Logger

### Community 60 - "PaymentServlet.java"
Cohesion: 0.36
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentServlet

### Community 61 - "PageDTO"
Cohesion: 0.09
Nodes (7): WebServlet, WebServlet, AdminRoomServlet, WebServlet, ResultSet, PageDTO, Logger

### Community 64 - ".sendRemindNotification"
Cohesion: 0.13
Nodes (3): DebtDAO, DebtServiceImpl, Override

### Community 65 - "CommunityPostServiceImpl"
Cohesion: 0.23
Nodes (3): CommunityPostDAO, CommunityPostServiceImpl, Override

### Community 67 - "HttpServlet"
Cohesion: 0.21
Nodes (11): HttpServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, LogoutServlet, HttpServletRequest, HttpServletResponse (+3 more)

### Community 69 - "AuthFilter"
Cohesion: 0.23
Nodes (7): AuthFilter, FilterChain, FilterConfig, Override, ServletRequest, ServletResponse, WebFilter

### Community 71 - ".doPost"
Cohesion: 0.11
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentReturnServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 72 - "UpdateMeterReadingServlet.java"
Cohesion: 0.27
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, UpdateMeterReadingServlet

### Community 73 - "AppException"
Cohesion: 0.29
Nodes (3): AppException, NotFoundException, ValidationException

### Community 75 - "PostReactionDAO"
Cohesion: 0.22
Nodes (4): PostCommentDAO, PostReactionDAO, Override, PostInteractionServiceImpl

### Community 76 - "UserServiceImpl.java"
Cohesion: 0.13
Nodes (10): RoleConstant, HttpServletRequest, HttpServletResponse, Override, WebServlet, LoginServlet, ForbiddenException, Logger (+2 more)

### Community 77 - "EditIncidentReportServlet.java"
Cohesion: 0.24
Nodes (8): EditIncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 78 - "SystemConfigDAO"
Cohesion: 0.24
Nodes (6): ConfigMetadata, Connection, Logger, Timestamp, SystemConfigDAO, Override

### Community 79 - "DatabaseUtil.java"
Cohesion: 0.17
Nodes (7): DataSource, SimpleDateFormat, Logger, SystemConfigServiceImpl, DatabaseUtil, Connection, Logger

### Community 80 - "ManagerDashboardServlet.java"
Cohesion: 0.17
Nodes (9): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerDashboardServlet, ManagerDashboardDAO, DashboardService, DashboardServiceImpl (+1 more)

### Community 81 - "NewsFeedApiServlet.java"
Cohesion: 0.19
Nodes (8): Gson, HttpServletRequest, HttpServletResponse, Logger, Override, WebServlet, NewsFeedApiServlet, NewsFeedService

### Community 83 - ".doPost"
Cohesion: 0.27
Nodes (8): IncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 84 - "PreparedStatement"
Cohesion: 0.20
Nodes (4): PreparedStatement, PaymentDAO, Override, PaymentServiceImpl

### Community 85 - "EmailService"
Cohesion: 0.28
Nodes (3): Session, EmailService, Logger

### Community 86 - "CommunityPostService"
Cohesion: 0.13
Nodes (15): CommunityPostServlet, Gson, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, WebServlet (+7 more)

### Community 87 - "TenantCommentServlet.java"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantCommentServlet

### Community 88 - "RoomDAO"
Cohesion: 0.15
Nodes (3): RoomDAO, Override, RoomServiceImpl

### Community 92 - "CommunityPostServiceImpl.java"
Cohesion: 0.15
Nodes (11): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantMyPostsServlet, HttpServletRequest, HttpServletResponse, Override (+3 more)

### Community 93 - ".doPost"
Cohesion: 0.32
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, TenantCreatePostServlet

### Community 94 - "TenantPostDetailServlet.java"
Cohesion: 0.27
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPostDetailServlet

### Community 95 - "ResetPasswordServlet.java"
Cohesion: 0.42
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordServlet

### Community 96 - "BaseDAO"
Cohesion: 0.43
Nodes (4): BaseDAO, Logger, ResultSet, Timestamp

### Community 97 - "AdminSystemConfigServlet.java"
Cohesion: 0.21
Nodes (6): AdminSystemConfigServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, SystemConfigService

### Community 98 - ".doPost"
Cohesion: 0.15
Nodes (12): FirstLoginServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, HttpServletResponse, MultipartConfig (+4 more)

### Community 99 - "DebtPageServlet.java"
Cohesion: 0.22
Nodes (6): DebtPageServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, DebtService

### Community 100 - "MeterReadingService"
Cohesion: 0.12
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListElectricServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 101 - "Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết"
Cohesion: 0.50
Nodes (3): Answer, Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết, Source Nodes

### Community 103 - "BaseServlet"
Cohesion: 0.10
Nodes (23): BaseServlet, HttpServletRequest, HttpServletResponse, Logger, HttpServletRequest, HttpServletResponse, WebServlet, MyIncidentListServlet (+15 more)

### Community 104 - "ContractDAO"
Cohesion: 0.10
Nodes (4): ContractDAO, ResultSet, ContractServiceImpl, Override

### Community 105 - "Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy"
Cohesion: 0.50
Nodes (3): Answer, Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy, Source Nodes

### Community 107 - "Q: giải thích phần jsp của logout qua graphify"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích phần jsp của logout qua graphify, Source Nodes

### Community 108 - "NotificationListServlet.java"
Cohesion: 0.27
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, NotificationListServlet

### Community 109 - "EncodingFilter"
Cohesion: 0.15
Nodes (13): ServletContextEvent, ServletContextListener, EncodingFilter, FilterChain, FilterConfig, Override, ServletRequest, ServletResponse (+5 more)

### Community 110 - "Q: chỉ class LoginServlet giống lệnh graphify"
Cohesion: 0.50
Nodes (3): Answer, Q: chỉ class LoginServlet giống lệnh graphify, Source Nodes

### Community 111 - "Q: giải thích các hàm trong LoginServlet"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích các hàm trong LoginServlet, Source Nodes

### Community 112 - "AdminPersonnelServlet"
Cohesion: 0.20
Nodes (5): AdminPersonnelServlet, HttpServletRequest, HttpServletResponse, Override, PersonnelService

### Community 113 - "Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend"
Cohesion: 0.50
Nodes (3): Answer, Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend, Source Nodes

### Community 114 - "UserDAO"
Cohesion: 0.11
Nodes (15): StatusConstant, ForgotPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ForgotPasswordServlet, HttpServletRequest (+7 more)

### Community 115 - "PaymentService"
Cohesion: 0.20
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentDetailServlet, PaymentService

### Community 116 - ".doGet"
Cohesion: 0.27
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ServicePricePageServlet, ServicePriceService

### Community 119 - "ResetPasswordApiServlet.java"
Cohesion: 0.29
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordApiServlet

### Community 120 - "DependentDAO"
Cohesion: 0.18
Nodes (3): DependentDAO, DependentServiceImpl, Override

### Community 123 - "TenantLikeServlet.java"
Cohesion: 0.29
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantLikeServlet

### Community 124 - "Q: giải thích luồng forgot-password"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích luồng forgot-password, Source Nodes

### Community 125 - "Q: JSP của ProfileServlet nằm ở đâu"
Cohesion: 0.50
Nodes (3): Answer, Q: JSP của ProfileServlet nằm ở đâu, Source Nodes

### Community 126 - "Q: Giải thích luồng ProfileServlet và profile.jsp"
Cohesion: 0.50
Nodes (3): Answer, Q: Giải thích luồng ProfileServlet và profile.jsp, Source Nodes

### Community 131 - "User"
Cohesion: 0.14
Nodes (3): ResultSet, User, Override

### Community 134 - "sw.js"
Cohesion: 0.50
Nodes (3): networkFirst(), offlineFallback(), STATIC_ASSETS

## Knowledge Gaps
- **19 isolated node(s):** `STATIC_ASSETS`, `Answer`, `Source Nodes`, `Answer`, `Source Nodes` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **42 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BaseServlet` connect `BaseServlet` to `.getId`, `.getConnection`, `ManagerTicketsServlet`, `FacilityRevenueStatDTO`, `RoomService`, `.doGet`, `ContractService`, `PostInteractionService`, `AdminFacilityServlet`, `.doGet`, `InvoiceListItemDTO`, `AdminNotificationServlet`, `.sendError`, `AdminAuditLogServlet`, `.doGet`, `PaymentServlet.java`, `PageDTO`, `HttpServlet`, `.doPost`, `UserServiceImpl.java`, `ManagerDashboardServlet.java`, `CommunityPostService`, `TenantCommentServlet.java`, `CommunityPostServiceImpl.java`, `.doPost`, `TenantPostDetailServlet.java`, `ResetPasswordServlet.java`, `AdminSystemConfigServlet.java`, `.doPost`, `MeterReadingService`, `AdminPersonnelServlet`, `UserDAO`, `PaymentService`, `.doGet`, `TenantLikeServlet.java`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **Why does `Contract` connect `Contract` to `User`, `ContractService`, `ContractDAO`, `Room`, `.mapRow`, `Facility`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Why does `AuditLogDAO` connect `Facility` to `.getId`, `BaseDAO`, `RequestServiceImpl`, `ContractService`, `UpdateMeterReadingServlet.java`, `.sendError`, `AdminAuditLogServlet`, `RequestService`, `AdminDashboardServiceImpl`, `MeterReading`, `.count`, `AuditLog`, `.doGet`, `ServicePriceHistoryDTO`, `InvoiceDAO`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `STATIC_ASSETS`, `Answer`, `Source Nodes` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `DebtDetailDTO` be split into smaller, more focused modules?**
  _Cohesion score 0.0463768115942029 - nodes in this community are weakly interconnected._
- **Should `.findById` be split into smaller, more focused modules?**
  _Cohesion score 0.045454545454545456 - nodes in this community are weakly interconnected._
- **Should `Invoice` be split into smaller, more focused modules?**
  _Cohesion score 0.05030181086519115 - nodes in this community are weakly interconnected._