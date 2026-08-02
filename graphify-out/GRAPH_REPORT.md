# Graph Report - hostel_management  (2026-07-28)

## Corpus Check
- 211 files · ~111,086 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2928 nodes · 7365 edges · 135 communities (93 shown, 42 thin omitted)
- Extraction: 72% EXTRACTED · 28% INFERRED · 0% AMBIGUOUS · INFERRED: 2077 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b24a062a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .getId
- DebtDetailDTO
- Invoice
- Notification
- Request
- .getConnection
- Contract
- CommentDTO
- DebtListItemDTO
- NewsFeedDTO
- CommunityPost
- RoomDetailDTO
- CommunityPostDTO
- Room
- AdminRevenueServlet
- PersonnelDAO
- PaymentDetailDTO
- RequestDAO
- Facility
- MeterReading
- PaymentTransaction
- AuditLog
- Dependent
- ServicePriceHistoryDTO
- SystemRevenueDTO
- InvoiceDetailDTO
- InvoiceDAO
- FacilityRevenueStatDTO
- UserSessionDTO
- ManagerRoomsServlet.java
- Override
- RequestServiceImpl
- CommunityPostServiceImpl.java
- PostComment
- ContractService
- PostInteractionService
- MeterStatusDTO
- AdminFacilityServlet
- RevenueDAO
- InvoiceListItemDTO
- .doFilter
- AdminNotificationServlet
- EmailConfigDTO
- .sendError
- NotFoundException.java
- .trim
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
- ListElectricServlet.java
- BaseServlet
- FacilityDAO
- .sendRemindNotification
- .mapRow
- PostCommentDTO
- HttpServlet
- .login
- .doPost
- .doPost
- MeterReadingService
- SystemConfigDAO
- VNPayConfigDTO
- PostReactionDAO
- UserServiceImpl.java
- EditIncidentReportServlet.java
- common.ps1
- DatabaseUtil.java
- ManagerDashboardServlet.java
- NewsFeedApiServlet.java
- .doPost
- PaymentDAO
- EmailService
- CommunityPostService
- TenantPostDetailServlet.java
- RoomDAO
- RoomOccupancyStatDTO
- .doGet
- PostReactionDTO
- TenantMyPostsServlet.java
- .doPost
- AppTimeZoneListener.java
- AuthFilter
- BaseDAO
- AdminSystemConfigServlet.java
- .doPost
- .getRole
- OperatorDashboardServlet.java
- Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết
- RevenueActivityDTO
- .doPost
- .findById
- Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy
- UserSessionDTO.java
- Q: giải thích phần jsp của logout qua graphify
- NotificationListServlet.java
- EncodingFilter
- Q: chỉ class LoginServlet giống lệnh graphify
- Q: giải thích các hàm trong LoginServlet
- PageDTO
- Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend
- UserDAO
- PaymentService
- .doPost
- PostReaction
- RoomService
- ResetPasswordApiServlet.java
- MeterReadingHistoryServlet.java
- TenantCommentServlet.java
- TenantDeletePostServlet.java
- TenantLikeServlet.java
- Q: giải thích luồng forgot-password
- Q: JSP của ProfileServlet nằm ở đâu
- Q: Giải thích luồng ProfileServlet và profile.jsp
- User
- sw.js
- create-new-feature.ps1
- update-agent-context.sh

## God Nodes (most connected - your core abstractions)
1. `BaseServlet` - 97 edges
2. `Invoice` - 96 edges
3. `InvoiceDetailDTO` - 91 edges
4. `DebtDetailDTO` - 88 edges
5. `Facility` - 84 edges
6. `User` - 80 edges
7. `Contract` - 78 edges
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

## Communities (135 total, 42 thin omitted)

### Community 0 - ".getId"
Cohesion: 0.32
Nodes (5): Override, HttpServletRequest, HttpServletResponse, Override, ManagerTenantsServlet

### Community 7 - ".getConnection"
Cohesion: 0.10
Nodes (3): NotificationDAO, Override, NotificationServiceImpl

### Community 11 - "NewsFeedDTO"
Cohesion: 0.09
Nodes (3): CommunityPostDAO, ResultSet, NewsFeedDTO

### Community 16 - "AdminRevenueServlet"
Cohesion: 0.18
Nodes (6): AdminRevenueServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueService

### Community 17 - "PersonnelDAO"
Cohesion: 0.11
Nodes (5): PersonnelDAO, Logger, Override, PersonnelServiceImpl, ValidationUtil

### Community 19 - "RequestDAO"
Cohesion: 0.08
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListRequestServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 23 - "AuditLog"
Cohesion: 0.09
Nodes (3): AuditLogDAO, ResultSet, AuditLog

### Community 24 - "Dependent"
Cohesion: 0.06
Nodes (11): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDependentServlet, DependentDAO, ResultSet, Dependent (+3 more)

### Community 28 - "InvoiceDAO"
Cohesion: 0.12
Nodes (6): InvoiceDAO, InvoicePriceSnapshot, InvoiceRoomSnapshot, Connection, InvoiceServiceImpl, Override

### Community 30 - "UserSessionDTO"
Cohesion: 0.08
Nodes (8): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ProfileServlet, UserSessionDTO

### Community 31 - "ManagerRoomsServlet.java"
Cohesion: 0.29
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerRoomsServlet

### Community 33 - "RequestServiceImpl"
Cohesion: 0.20
Nodes (3): Logger, Override, RequestServiceImpl

### Community 35 - "CommunityPostServiceImpl.java"
Cohesion: 0.23
Nodes (4): CommunityPostServiceImpl, Logger, Override, Part

### Community 37 - "ContractService"
Cohesion: 0.12
Nodes (11): ContractServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, HttpServletResponse, Override (+3 more)

### Community 38 - "PostInteractionService"
Cohesion: 0.14
Nodes (13): HttpServletRequest, HttpServletResponse, Logger, Override, WebServlet, PostCommentServlet, HttpServletRequest, HttpServletResponse (+5 more)

### Community 40 - "AdminFacilityServlet"
Cohesion: 0.07
Nodes (15): AdminFacilityServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, FacilityFormDTO, HttpServletRequest, FilterChain (+7 more)

### Community 41 - "RevenueDAO"
Cohesion: 0.25
Nodes (3): RevenueDAO, Override, RevenueServiceImpl

### Community 43 - ".doFilter"
Cohesion: 0.19
Nodes (7): ErrorMessageConstant, CsrfFilter, FilterChain, Override, ServletRequest, ServletResponse, WebFilter

### Community 44 - "AdminNotificationServlet"
Cohesion: 0.33
Nodes (5): AdminNotificationServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 46 - ".sendError"
Cohesion: 0.15
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerNotificationsServlet, NotificationService

### Community 47 - "NotFoundException.java"
Cohesion: 0.18
Nodes (7): AdminAuditLogServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, NotFoundException, AuditLogService

### Community 48 - ".trim"
Cohesion: 0.11
Nodes (15): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ManagerTicketsServlet, DetailRequestServlet (+7 more)

### Community 49 - "HttpSession"
Cohesion: 0.25
Nodes (9): HttpSession, HttpSessionAttributeListener, HttpSessionBindingEvent, HttpSessionEvent, HttpSessionListener, Override, WebListener, UserSessionListener (+1 more)

### Community 50 - "ResetPasswordServlet.java"
Cohesion: 0.25
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordServlet, Pattern, PasswordValidator

### Community 52 - "NewsFeedServiceImpl"
Cohesion: 0.15
Nodes (8): CommentDAO, Logger, Logger, NewsFeedDAO, Logger, ReactionDAO, Override, NewsFeedServiceImpl

### Community 56 - ".doGet"
Cohesion: 0.18
Nodes (7): AdminDashboardServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AdminDashboardService, Logger

### Community 60 - "PaymentServlet.java"
Cohesion: 0.24
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentServlet

### Community 61 - "ListElectricServlet.java"
Cohesion: 0.39
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListElectricServlet

### Community 62 - "BaseServlet"
Cohesion: 0.05
Nodes (27): StatusConstant, BaseServlet, HttpServletRequest, HttpServletResponse, Logger, WebServlet, HttpServletRequest, HttpServletResponse (+19 more)

### Community 63 - "FacilityDAO"
Cohesion: 0.12
Nodes (6): FacilityDAO, Connection, FacilityServiceImpl, Logger, Override, ServicePriceServiceImpl

### Community 64 - ".sendRemindNotification"
Cohesion: 0.14
Nodes (3): DebtDAO, DebtServiceImpl, Override

### Community 67 - "HttpServlet"
Cohesion: 0.21
Nodes (11): HttpServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, LogoutServlet, HttpServletRequest, HttpServletResponse (+3 more)

### Community 69 - ".doPost"
Cohesion: 0.23
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, UpdateMeterReadingServlet

### Community 71 - ".doPost"
Cohesion: 0.17
Nodes (5): HttpServletRequest, HttpServletResponse, Override, HttpServletRequest, VNPayConfig

### Community 73 - "SystemConfigDAO"
Cohesion: 0.16
Nodes (8): ConfigMetadata, Connection, Logger, Timestamp, SystemConfigDAO, AppException, ValidationException, Override

### Community 75 - "PostReactionDAO"
Cohesion: 0.22
Nodes (4): PostCommentDAO, PostReactionDAO, Override, PostInteractionServiceImpl

### Community 76 - "UserServiceImpl.java"
Cohesion: 0.13
Nodes (10): RoleConstant, HttpServletRequest, HttpServletResponse, Override, WebServlet, LoginServlet, ForbiddenException, Logger (+2 more)

### Community 77 - "EditIncidentReportServlet.java"
Cohesion: 0.24
Nodes (8): EditIncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 78 - "common.ps1"
Cohesion: 0.22
Nodes (10): Find-SpecifyRoot(), Format-SpecKitCommand(), Get-CurrentBranch(), Get-FeaturePathsEnv(), Get-InvokeSeparator(), Get-Python3Command(), Get-RepoRoot(), Resolve-SpecifyInitDir() (+2 more)

### Community 79 - "DatabaseUtil.java"
Cohesion: 0.19
Nodes (7): DataSource, SimpleDateFormat, Logger, SystemConfigServiceImpl, DatabaseUtil, Connection, Logger

### Community 80 - "ManagerDashboardServlet.java"
Cohesion: 0.19
Nodes (8): HttpServletRequest, HttpServletResponse, WebServlet, ManagerDashboardServlet, ManagerDashboardDAO, DashboardService, DashboardServiceImpl, Override

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

### Community 87 - "TenantPostDetailServlet.java"
Cohesion: 0.27
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPostDetailServlet

### Community 88 - "RoomDAO"
Cohesion: 0.15
Nodes (3): RoomDAO, Override, RoomServiceImpl

### Community 90 - ".doGet"
Cohesion: 0.20
Nodes (3): HttpServletRequest, HttpServletResponse, Override

### Community 92 - "TenantMyPostsServlet.java"
Cohesion: 0.16
Nodes (10): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantMyPostsServlet, HttpServletRequest, HttpServletResponse, Override (+2 more)

### Community 93 - ".doPost"
Cohesion: 0.28
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, TenantCreatePostServlet

### Community 94 - "AppTimeZoneListener.java"
Cohesion: 0.43
Nodes (5): ServletContextEvent, ServletContextListener, AppTimeZoneListener, Override, WebListener

### Community 95 - "AuthFilter"
Cohesion: 0.21
Nodes (8): Filter, AuthFilter, FilterChain, FilterConfig, Override, ServletRequest, ServletResponse, WebFilter

### Community 96 - "BaseDAO"
Cohesion: 0.43
Nodes (4): BaseDAO, Logger, ResultSet, Timestamp

### Community 97 - "AdminSystemConfigServlet.java"
Cohesion: 0.21
Nodes (6): AdminSystemConfigServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, SystemConfigService

### Community 98 - ".doPost"
Cohesion: 0.19
Nodes (7): SecureRandom, FirstLoginServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, PasswordUtil

### Community 99 - ".getRole"
Cohesion: 0.21
Nodes (6): DebtPageServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, DebtService

### Community 100 - "OperatorDashboardServlet.java"
Cohesion: 0.31
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, OperatorDashboardServlet, OperatorDashboardDAO

### Community 101 - "Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết"
Cohesion: 0.50
Nodes (3): Answer, Q: những class nào phụ trách cái tính năng auth này vậy, cụ thể cho mình biết đi và nhớ comment vào để cho mình biết, Source Nodes

### Community 103 - ".doPost"
Cohesion: 0.27
Nodes (6): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, WebServlet, TenantRequestServlet

### Community 104 - ".findById"
Cohesion: 0.12
Nodes (4): ContractDAO, ResultSet, ContractServiceImpl, Override

### Community 105 - "Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy"
Cohesion: 0.50
Nodes (3): Answer, Q: ấn đăng xuất ở web thì nó xuất hiện ở class nào vậy mà có 2 tận 2 đăng xuất là sao vậy, Source Nodes

### Community 106 - "UserSessionDTO.java"
Cohesion: 0.10
Nodes (16): InvoiceDetailServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, InvoiceServlet, HttpServletRequest, HttpServletResponse (+8 more)

### Community 107 - "Q: giải thích phần jsp của logout qua graphify"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích phần jsp của logout qua graphify, Source Nodes

### Community 108 - "NotificationListServlet.java"
Cohesion: 0.24
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, NotificationListServlet

### Community 109 - "EncodingFilter"
Cohesion: 0.24
Nodes (8): EncodingFilter, FilterChain, FilterConfig, Override, ServletRequest, ServletResponse, WebFilter, TimeZone

### Community 110 - "Q: chỉ class LoginServlet giống lệnh graphify"
Cohesion: 0.50
Nodes (3): Answer, Q: chỉ class LoginServlet giống lệnh graphify, Source Nodes

### Community 111 - "Q: giải thích các hàm trong LoginServlet"
Cohesion: 0.50
Nodes (3): Answer, Q: giải thích các hàm trong LoginServlet, Source Nodes

### Community 112 - "PageDTO"
Cohesion: 0.06
Nodes (9): AdminPersonnelServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, PageDTO, HttpServletRequest, PersonnelFormDTO (+1 more)

### Community 113 - "Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend"
Cohesion: 0.50
Nodes (3): Answer, Q: FirstLoginServlet jsp đoạn nằm ở đâu và luồng backend, Source Nodes

### Community 114 - "UserDAO"
Cohesion: 0.13
Nodes (13): ForgotPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ForgotPasswordServlet, HttpServletRequest, HttpServletResponse (+5 more)

### Community 115 - "PaymentService"
Cohesion: 0.25
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentDetailServlet, PaymentService

### Community 116 - ".doPost"
Cohesion: 0.24
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ServicePricePageServlet, ServicePriceService

### Community 118 - "RoomService"
Cohesion: 0.19
Nodes (6): AdminRoomServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RoomService

### Community 119 - "ResetPasswordApiServlet.java"
Cohesion: 0.19
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordApiServlet, ResetTokenManager, TokenData

### Community 120 - "MeterReadingHistoryServlet.java"
Cohesion: 0.39
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, MeterReadingHistoryServlet

### Community 121 - "TenantCommentServlet.java"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantCommentServlet

### Community 122 - "TenantDeletePostServlet.java"
Cohesion: 0.39
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

### Community 131 - "User"
Cohesion: 0.13
Nodes (4): ResultSet, ResultSet, User, Override

### Community 134 - "sw.js"
Cohesion: 0.50
Nodes (3): networkFirst(), offlineFallback(), STATIC_ASSETS

## Knowledge Gaps
- **20 isolated node(s):** `update-agent-context.sh script`, `STATIC_ASSETS`, `Answer`, `Source Nodes`, `Answer` (+15 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **42 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BaseServlet` connect `BaseServlet` to `.getId`, `AdminRevenueServlet`, `RequestDAO`, `Dependent`, `UserSessionDTO`, `ManagerRoomsServlet.java`, `ContractService`, `PostInteractionService`, `AdminFacilityServlet`, `AdminNotificationServlet`, `.sendError`, `NotFoundException.java`, `.trim`, `ResetPasswordServlet.java`, `.doGet`, `PaymentServlet.java`, `HttpServlet`, `UserServiceImpl.java`, `ManagerDashboardServlet.java`, `CommunityPostService`, `TenantPostDetailServlet.java`, `TenantMyPostsServlet.java`, `.doPost`, `AdminSystemConfigServlet.java`, `.doPost`, `OperatorDashboardServlet.java`, `.doPost`, `UserSessionDTO.java`, `PageDTO`, `UserDAO`, `PaymentService`, `.doPost`, `RoomService`, `TenantCommentServlet.java`, `TenantDeletePostServlet.java`, `TenantLikeServlet.java`?**
  _High betweenness centrality (0.112) - this node is a cross-community bridge._
- **Why does `Request` connect `Request` to `RequestServiceImpl`, `OperatorDashboardServlet.java`, `.doPost`, `EditIncidentReportServlet.java`, `.trim`, `.insert`, `RequestDAO`, `.doPost`, `BaseServlet`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **Why does `User` connect `User` to `.login`, `.insert`, `Contract`, `.findById`, `UserServiceImpl.java`, `PageDTO`, `PersonnelDAO`, `UserDAO`, `.mapRow`, `BaseServlet`, `UserSessionDTO`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `update-agent-context.sh script`, `STATIC_ASSETS`, `Answer` to the rest of the system?**
  _20 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `DebtDetailDTO` be split into smaller, more focused modules?**
  _Cohesion score 0.04460093896713615 - nodes in this community are weakly interconnected._
- **Should `.findById` be split into smaller, more focused modules?**
  _Cohesion score 0.04878048780487805 - nodes in this community are weakly interconnected._
- **Should `Invoice` be split into smaller, more focused modules?**
  _Cohesion score 0.07827260458839407 - nodes in this community are weakly interconnected._