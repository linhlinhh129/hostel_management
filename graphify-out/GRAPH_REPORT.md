# Graph Report - hostel_management  (2026-08-04)

## Corpus Check
- 206 files · ~108,513 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2978 nodes · 7618 edges · 135 communities (92 shown, 43 thin omitted)
- Extraction: 71% EXTRACTED · 29% INFERRED · 0% AMBIGUOUS · INFERRED: 2207 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e2509046`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .getId
- DebtDetailDTO
- Invoice
- Notification
- Request
- NotificationDAO
- Contract
- CommentDTO
- DebtListItemDTO
- NewsFeedDTO
- CommunityPost
- RoomDetailDTO
- CommunityPostDTO
- Room
- ValidationException.java
- PersonnelDAO
- PaymentDetailDTO
- .getConnection
- Facility
- MeterReading
- PaymentTransaction
- AuditLog
- Dependent
- ServicePriceHistoryDTO
- .trim
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
- ResetPasswordServlet.java
- AdminDashboardServiceImpl
- NewsFeedServiceImpl
- RoomDTO
- .count
- .mapRow
- .doGet
- PaymentListItemDTO
- ServicePriceDTO
- PageResult
- PaymentServlet.java
- PageDTO
- .doGet
- FacilityDAO
- .sendRemindNotification
- PersonnelFormDTO
- PostCommentDTO
- HttpServlet
- .login
- TenantPaymentReturnServlet.java
- MeterReadingService
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
- PaymentDAO
- EmailService
- CommunityPostService
- BaseServlet
- RoomDAO
- RoomOccupancyStatDTO
- .doPost
- PostReactionDTO
- CommunityPostServiceImpl.java
- .doPost
- PreparedStatement
- AuthFilter
- BaseDAO
- AdminSystemConfigServlet.java
- .doPost
- DebtPageServlet.java
- OperatorDashboardServlet.java
- Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết
- RevenueActivityDTO
- TenantRequestServlet.java
- ContractDAO
- Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy
- InvoiceService
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
- RoomServiceImpl.java
- ResetPasswordApiServlet.java
- DependentServiceImpl
- ResetTokenManager.java
- TenantDeletePostServlet.java
- TenantLikeServlet.java
- Q: giải thích luồng forgot-password
- Q: JSP của ProfileServlet nằm ở đâu
- Q: Giải thích luồng ProfileServlet và profile.jsp
- DebtDAO.java
- AuditLogHelper.java
- PasswordValidator.java
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

## Communities (135 total, 43 thin omitted)

### Community 0 - ".getId"
Cohesion: 0.17
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerTenantsServlet, TenantService

### Community 7 - "NotificationDAO"
Cohesion: 0.10
Nodes (3): NotificationDAO, Override, NotificationServiceImpl

### Community 11 - "NewsFeedDTO"
Cohesion: 0.08
Nodes (5): CommunityPostDAO, ResultSet, NewsFeedDTO, CommunityPostServiceImpl, Override

### Community 15 - "Room"
Cohesion: 0.07
Nodes (3): Override, ResultSet, Room

### Community 16 - "ValidationException.java"
Cohesion: 0.18
Nodes (4): WebServlet, WebServlet, ResultSet, Logger

### Community 17 - "PersonnelDAO"
Cohesion: 0.12
Nodes (4): PersonnelDAO, Logger, Override, PersonnelServiceImpl

### Community 19 - ".getConnection"
Cohesion: 0.12
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListRequestServlet, Override, RequestDAO

### Community 20 - "Facility"
Cohesion: 0.10
Nodes (4): AuditLogDAO, Facility, Override, ServicePriceServiceImpl

### Community 26 - ".trim"
Cohesion: 0.22
Nodes (9): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ManagerTicketsServlet, Pattern (+1 more)

### Community 28 - "InvoiceDAO"
Cohesion: 0.14
Nodes (7): InvoiceDAO, InvoicePriceSnapshot, InvoiceRoomSnapshot, Connection, ResultSet, InvoiceServiceImpl, Override

### Community 29 - "FacilityRevenueStatDTO"
Cohesion: 0.05
Nodes (11): AdminRevenueServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueDAO, FacilityRevenueStatDTO, SystemRevenueDTO (+3 more)

### Community 31 - "RoomService"
Cohesion: 0.20
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerRoomsServlet, RoomService

### Community 32 - "TenantServiceImpl"
Cohesion: 0.12
Nodes (3): DependentDAO, Override, TenantServiceImpl

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

### Community 43 - "PasswordUtil.java"
Cohesion: 0.13
Nodes (10): Filter, SecureRandom, ErrorMessageConstant, CsrfFilter, FilterChain, Override, ServletRequest, ServletResponse (+2 more)

### Community 44 - "AdminNotificationServlet"
Cohesion: 0.38
Nodes (4): AdminNotificationServlet, HttpServletRequest, HttpServletResponse, Override

### Community 46 - ".sendError"
Cohesion: 0.15
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerNotificationsServlet, NotificationService

### Community 47 - "AdminAuditLogServlet"
Cohesion: 0.19
Nodes (6): AdminAuditLogServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AuditLogService

### Community 48 - "RequestService"
Cohesion: 0.10
Nodes (12): DetailRequestServlet, HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, HttpServletRequest (+4 more)

### Community 49 - "HttpSession"
Cohesion: 0.24
Nodes (9): HttpSession, HttpSessionAttributeListener, HttpSessionBindingEvent, HttpSessionEvent, HttpSessionListener, Override, WebListener, UserSessionListener (+1 more)

### Community 50 - "ResetPasswordServlet.java"
Cohesion: 0.42
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordServlet

### Community 51 - "AdminDashboardServiceImpl"
Cohesion: 0.26
Nodes (3): AdminDashboardServiceImpl, Logger, Override

### Community 52 - "NewsFeedServiceImpl"
Cohesion: 0.15
Nodes (8): CommentDAO, Logger, Logger, NewsFeedDAO, Logger, ReactionDAO, Override, NewsFeedServiceImpl

### Community 56 - ".doGet"
Cohesion: 0.20
Nodes (6): AdminDashboardServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AdminDashboardService

### Community 60 - "PaymentServlet.java"
Cohesion: 0.27
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentServlet

### Community 62 - ".doGet"
Cohesion: 0.19
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantNotificationServlet

### Community 63 - "FacilityDAO"
Cohesion: 0.15
Nodes (4): FacilityDAO, Connection, FacilityServiceImpl, Override

### Community 64 - ".sendRemindNotification"
Cohesion: 0.13
Nodes (3): DebtDAO, DebtServiceImpl, Override

### Community 67 - "HttpServlet"
Cohesion: 0.12
Nodes (18): HttpServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, LogoutServlet, HttpServletRequest, HttpServletResponse (+10 more)

### Community 71 - "TenantPaymentReturnServlet.java"
Cohesion: 0.16
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentReturnServlet, HttpServletRequest, VNPayConfig

### Community 72 - "MeterReadingService"
Cohesion: 0.11
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListElectricServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 73 - "AppException"
Cohesion: 0.20
Nodes (5): Connection, AppException, NotFoundException, ValidationException, Override

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
Cohesion: 0.38
Nodes (4): ConfigMetadata, Logger, Timestamp, SystemConfigDAO

### Community 79 - "DatabaseUtil.java"
Cohesion: 0.17
Nodes (7): DataSource, SimpleDateFormat, Logger, SystemConfigServiceImpl, DatabaseUtil, Connection, Logger

### Community 80 - "ManagerDashboardServlet.java"
Cohesion: 0.27
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerDashboardServlet, DashboardService

### Community 81 - "NewsFeedApiServlet.java"
Cohesion: 0.19
Nodes (8): Gson, HttpServletRequest, HttpServletResponse, Logger, Override, WebServlet, NewsFeedApiServlet, NewsFeedService

### Community 83 - ".doPost"
Cohesion: 0.27
Nodes (8): IncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 84 - "PaymentDAO"
Cohesion: 0.23
Nodes (3): PaymentDAO, Override, PaymentServiceImpl

### Community 85 - "EmailService"
Cohesion: 0.28
Nodes (3): Session, EmailService, Logger

### Community 86 - "CommunityPostService"
Cohesion: 0.13
Nodes (11): CommunityPostServlet, Gson, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, WebServlet (+3 more)

### Community 87 - "BaseServlet"
Cohesion: 0.10
Nodes (19): BaseServlet, HttpServletRequest, HttpServletResponse, Logger, HttpServletRequest, HttpServletResponse, WebServlet, MyIncidentListServlet (+11 more)

### Community 88 - "RoomDAO"
Cohesion: 0.15
Nodes (3): RoomDAO, Override, RoomServiceImpl

### Community 90 - ".doPost"
Cohesion: 0.10
Nodes (10): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantInvoiceServlet, HttpServletRequest, HttpServletResponse, Override (+2 more)

### Community 92 - "CommunityPostServiceImpl.java"
Cohesion: 0.15
Nodes (11): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantMyPostsServlet, HttpServletRequest, HttpServletResponse, Override (+3 more)

### Community 93 - ".doPost"
Cohesion: 0.28
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, TenantCreatePostServlet

### Community 94 - "PreparedStatement"
Cohesion: 0.28
Nodes (4): PreparedStatement, ManagerDashboardDAO, DashboardServiceImpl, Override

### Community 95 - "AuthFilter"
Cohesion: 0.23
Nodes (7): AuthFilter, FilterChain, FilterConfig, Override, ServletRequest, ServletResponse, WebFilter

### Community 96 - "BaseDAO"
Cohesion: 0.33
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

### Community 100 - "OperatorDashboardServlet.java"
Cohesion: 0.31
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, OperatorDashboardServlet, OperatorDashboardDAO

### Community 101 - "Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết"
Cohesion: 0.50
Nodes (3): Answer, Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết, Source Nodes

### Community 103 - "TenantRequestServlet.java"
Cohesion: 0.21
Nodes (6): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, WebServlet, TenantRequestServlet

### Community 104 - "ContractDAO"
Cohesion: 0.11
Nodes (4): ContractDAO, ResultSet, ContractServiceImpl, Override

### Community 105 - "Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy"
Cohesion: 0.50
Nodes (3): Answer, Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy, Source Nodes

### Community 106 - "InvoiceService"
Cohesion: 0.21
Nodes (6): InvoiceServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, InvoiceService

### Community 107 - "Q: giải thích phần jsp của logout qua graphify"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích phần jsp của logout qua graphify, Source Nodes

### Community 108 - "NotificationListServlet.java"
Cohesion: 0.24
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
Cohesion: 0.21
Nodes (5): AdminPersonnelServlet, HttpServletRequest, HttpServletResponse, Override, PersonnelService

### Community 113 - "Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend"
Cohesion: 0.50
Nodes (3): Answer, Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend, Source Nodes

### Community 114 - "UserDAO"
Cohesion: 0.11
Nodes (15): StatusConstant, ForgotPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ForgotPasswordServlet, HttpServletRequest (+7 more)

### Community 115 - "PaymentService"
Cohesion: 0.25
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentDetailServlet, PaymentService

### Community 116 - ".doGet"
Cohesion: 0.27
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ServicePricePageServlet, ServicePriceService

### Community 118 - "RoomServiceImpl.java"
Cohesion: 0.29
Nodes (5): AdminRoomServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 119 - "ResetPasswordApiServlet.java"
Cohesion: 0.29
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordApiServlet

### Community 122 - "TenantDeletePostServlet.java"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDeletePostServlet

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

### Community 128 - "AuditLogHelper.java"
Cohesion: 0.50
Nodes (3): AuditLogHelper, HttpServletRequest, Logger

### Community 131 - "User"
Cohesion: 0.14
Nodes (3): ResultSet, User, Override

### Community 134 - "sw.js"
Cohesion: 0.50
Nodes (3): networkFirst(), offlineFallback(), STATIC_ASSETS

## Knowledge Gaps
- **19 isolated node(s):** `STATIC_ASSETS`, `Answer`, `Source Nodes`, `Answer`, `Source Nodes` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BaseServlet` connect `BaseServlet` to `.getId`, `ValidationException.java`, `.getConnection`, `.trim`, `FacilityRevenueStatDTO`, `RoomService`, `.doGet`, `ContractService`, `PostInteractionService`, `AdminFacilityServlet`, `.doGet`, `AdminNotificationServlet`, `.sendError`, `AdminAuditLogServlet`, `RequestService`, `ResetPasswordServlet.java`, `.doGet`, `PaymentServlet.java`, `.doGet`, `HttpServlet`, `TenantPaymentReturnServlet.java`, `UserServiceImpl.java`, `ManagerDashboardServlet.java`, `CommunityPostService`, `.doPost`, `CommunityPostServiceImpl.java`, `.doPost`, `AdminSystemConfigServlet.java`, `.doPost`, `OperatorDashboardServlet.java`, `TenantRequestServlet.java`, `InvoiceService`, `AdminPersonnelServlet`, `UserDAO`, `PaymentService`, `.doGet`, `RoomServiceImpl.java`, `TenantDeletePostServlet.java`, `TenantLikeServlet.java`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **Why does `Contract` connect `Contract` to `User`, `ContractService`, `ContractDAO`, `Room`, `Facility`, `.mapRow`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Why does `AuditLogDAO` connect `Facility` to `.getId`, `BaseDAO`, `RequestServiceImpl`, `HttpServlet`, `AuditLogHelper.java`, `ContractService`, `MeterReadingService`, `.sendError`, `AdminAuditLogServlet`, `RequestService`, `AdminDashboardServiceImpl`, `.count`, `AuditLog`, `ServicePriceHistoryDTO`, `InvoiceDAO`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `STATIC_ASSETS`, `Answer`, `Source Nodes` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `DebtDetailDTO` be split into smaller, more focused modules?**
  _Cohesion score 0.0463768115942029 - nodes in this community are weakly interconnected._
- **Should `.findById` be split into smaller, more focused modules?**
  _Cohesion score 0.045454545454545456 - nodes in this community are weakly interconnected._
- **Should `Invoice` be split into smaller, more focused modules?**
  _Cohesion score 0.05030181086519115 - nodes in this community are weakly interconnected._