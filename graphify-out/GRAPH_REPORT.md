# Graph Report - .  (2026-07-23)

## Corpus Check
- 202 files · ~102,059 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2765 nodes · 6702 edges · 124 communities (83 shown, 41 thin omitted)
- Extraction: 73% EXTRACTED · 27% INFERRED · 0% AMBIGUOUS · INFERRED: 1824 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Community 0
- Community 1
- Community 2
- Community 3
- Community 4
- Community 5
- Community 6
- Community 7
- Community 8
- Community 9
- Community 10
- Community 11
- Community 12
- Community 14
- Community 15
- Community 16
- Community 17
- Community 18
- Community 19
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 25
- Community 26
- Community 27
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35
- Community 36
- Community 37
- Community 38
- Community 39
- Community 40
- Community 41
- Community 42
- Community 43
- Community 44
- Community 45
- Community 46
- Community 47
- Community 48
- Community 49
- Community 50
- Community 51
- Community 52
- Community 53
- Community 54
- Community 55
- Community 56
- Community 57
- Community 58
- Community 59
- Community 60
- Community 61
- Community 62
- Community 63
- Community 64
- Community 65
- Community 66
- Community 67
- Community 68
- Community 69
- Community 70
- Community 71
- Community 72
- Community 73
- Community 74
- Community 75
- Community 77
- Community 78
- Community 79
- Community 80
- Community 81
- Community 82
- Community 83
- Community 84
- Community 85
- Community 86
- Community 87
- Community 88
- Community 89
- Community 90
- Community 91
- Community 92
- Community 94
- Community 96
- Community 97
- Community 98
- Community 99
- Community 101
- Community 102
- Community 103
- Community 104
- Community 105
- Community 106
- Community 107
- Community 108
- Community 109
- Community 110
- Community 111
- Community 113
- Community 115
- Community 116
- Community 117
- Community 118
- Community 119
- Community 120

## God Nodes (most connected - your core abstractions)
1. `BaseServlet` - 95 edges
2. `InvoiceDetailDTO` - 89 edges
3. `DebtDetailDTO` - 88 edges
4. `Invoice` - 87 edges
5. `Facility` - 83 edges
6. `User` - 80 edges
7. `Contract` - 78 edges
8. `Request` - 73 edges
9. `Notification` - 65 edges
10. `Room` - 62 edges

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

## Communities (124 total, 41 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.05
Nodes (13): DataSource, FacilityDAO, Connection, ResultSet, ResultSet, Facility, FacilityServiceImpl, Override (+5 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (11): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDependentServlet, DependentDAO, ResultSet, Dependent (+3 more)

### Community 5 - "Community 5"
Cohesion: 0.06
Nodes (30): Filter, HttpSession, HttpSessionAttributeListener, HttpSessionBindingEvent, HttpSessionEvent, HttpSessionListener, ErrorMessageConstant, AuthFilter (+22 more)

### Community 7 - "Community 7"
Cohesion: 0.10
Nodes (15): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ManagerTicketsServlet, DetailRequestServlet (+7 more)

### Community 8 - "Community 8"
Cohesion: 0.10
Nodes (8): AdminPersonnelServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, PersonnelFormDTO, PersonnelService

### Community 9 - "Community 9"
Cohesion: 0.11
Nodes (3): NotificationDAO, Override, NotificationServiceImpl

### Community 12 - "Community 12"
Cohesion: 0.11
Nodes (4): ResultSet, ResultSet, User, Override

### Community 15 - "Community 15"
Cohesion: 0.10
Nodes (6): PersonnelDAO, Logger, Override, PersonnelServiceImpl, Pattern, ValidationUtil

### Community 17 - "Community 17"
Cohesion: 0.17
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerTenantsServlet, TenantService

### Community 18 - "Community 18"
Cohesion: 0.08
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListRequestServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 22 - "Community 22"
Cohesion: 0.16
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerNotificationsServlet, NotificationService

### Community 24 - "Community 24"
Cohesion: 0.09
Nodes (3): Override, TenantServiceImpl, MapStringConsumer

### Community 28 - "Community 28"
Cohesion: 0.09
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentServlet, InvoiceDAO, Override

### Community 33 - "Community 33"
Cohesion: 0.14
Nodes (13): HttpServletRequest, HttpServletResponse, Override, WebServlet, ResetPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override (+5 more)

### Community 34 - "Community 34"
Cohesion: 0.13
Nodes (12): InvoiceServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 36 - "Community 36"
Cohesion: 0.13
Nodes (12): ForgotPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ForgotPasswordServlet, HttpServletRequest, HttpServletResponse (+4 more)

### Community 38 - "Community 38"
Cohesion: 0.14
Nodes (9): RoleConstant, HttpServletRequest, HttpServletResponse, Override, WebServlet, LoginServlet, Logger, UserServiceImpl (+1 more)

### Community 39 - "Community 39"
Cohesion: 0.11
Nodes (10): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDashboardServlet, HttpServletRequest, HttpServletResponse, Override (+2 more)

### Community 40 - "Community 40"
Cohesion: 0.15
Nodes (4): ContractDAO, ResultSet, ContractServiceImpl, Override

### Community 41 - "Community 41"
Cohesion: 0.18
Nodes (3): Logger, Override, RequestServiceImpl

### Community 42 - "Community 42"
Cohesion: 0.18
Nodes (6): AdminRevenueServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueService

### Community 43 - "Community 43"
Cohesion: 0.20
Nodes (6): ContractServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ContractService

### Community 44 - "Community 44"
Cohesion: 0.17
Nodes (7): EditIncidentServlet, HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet

### Community 46 - "Community 46"
Cohesion: 0.15
Nodes (9): CommunityPostServlet, Gson, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, WebServlet (+1 more)

### Community 47 - "Community 47"
Cohesion: 0.19
Nodes (8): Gson, HttpServletRequest, HttpServletResponse, Logger, Override, WebServlet, NewsFeedApiServlet, NewsFeedService

### Community 48 - "Community 48"
Cohesion: 0.14
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantNewsFeedServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 49 - "Community 49"
Cohesion: 0.15
Nodes (8): CommentDAO, Logger, Logger, NewsFeedDAO, Logger, ReactionDAO, Override, NewsFeedServiceImpl

### Community 50 - "Community 50"
Cohesion: 0.18
Nodes (6): CommunityPostDAO, PostCommentDAO, PostReactionDAO, CommunityPostServiceImpl, Logger, Override

### Community 51 - "Community 51"
Cohesion: 0.15
Nodes (3): RoomDAO, Override, RoomServiceImpl

### Community 52 - "Community 52"
Cohesion: 0.19
Nodes (6): AdminAuditLogServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AuditLogService

### Community 53 - "Community 53"
Cohesion: 0.28
Nodes (4): AdminFacilityServlet, HttpServletRequest, HttpServletResponse, Override

### Community 54 - "Community 54"
Cohesion: 0.21
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerRoomsServlet, RoomService

### Community 55 - "Community 55"
Cohesion: 0.16
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, MeterReadingHistoryServlet, MeterReadingDAO, MeterReadingService

### Community 56 - "Community 56"
Cohesion: 0.16
Nodes (7): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentReturnServlet, HttpServletRequest, VNPayConfig

### Community 57 - "Community 57"
Cohesion: 0.19
Nodes (7): SecureRandom, FirstLoginServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, PasswordUtil

### Community 61 - "Community 61"
Cohesion: 0.20
Nodes (6): AdminDashboardServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, AdminDashboardService

### Community 62 - "Community 62"
Cohesion: 0.15
Nodes (5): InvoiceDetailServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 63 - "Community 63"
Cohesion: 0.17
Nodes (9): HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerDashboardServlet, DashboardDAO, DashboardService, DashboardServiceImpl (+1 more)

### Community 66 - "Community 66"
Cohesion: 0.21
Nodes (9): SimpleDateFormat, ConfigMetadata, Connection, Logger, Timestamp, SystemConfigDAO, Logger, Override (+1 more)

### Community 67 - "Community 67"
Cohesion: 0.21
Nodes (6): AdminSystemConfigServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, SystemConfigService

### Community 68 - "Community 68"
Cohesion: 0.19
Nodes (9): BaseServlet, HttpServletRequest, HttpServletResponse, Logger, HttpServletRequest, HttpServletResponse, Override, WebServlet (+1 more)

### Community 69 - "Community 69"
Cohesion: 0.20
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentDetailServlet, PaymentService

### Community 70 - "Community 70"
Cohesion: 0.18
Nodes (6): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, WebServlet, TenantRequestServlet

### Community 71 - "Community 71"
Cohesion: 0.21
Nodes (3): AdminDashboardServiceImpl, Logger, Override

### Community 73 - "Community 73"
Cohesion: 0.17
Nodes (7): FilterChain, FilterConfig, Override, ServletRequest, ServletResponse, WebFilter, RoleFilter

### Community 74 - "Community 74"
Cohesion: 0.31
Nodes (5): AdminNotificationServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 75 - "Community 75"
Cohesion: 0.23
Nodes (6): DebtPageServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, DebtService

### Community 77 - "Community 77"
Cohesion: 0.21
Nodes (11): HttpServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, LogoutServlet, HttpServletRequest, HttpServletResponse (+3 more)

### Community 78 - "Community 78"
Cohesion: 0.26
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, ServicePricePageServlet, ServicePriceService

### Community 79 - "Community 79"
Cohesion: 0.25
Nodes (8): IncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 80 - "Community 80"
Cohesion: 0.23
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, UpdateMeterReadingServlet

### Community 83 - "Community 83"
Cohesion: 0.22
Nodes (10): Find-SpecifyRoot(), Format-SpecKitCommand(), Get-CurrentBranch(), Get-FeaturePathsEnv(), Get-InvokeSeparator(), Get-Python3Command(), Get-RepoRoot(), Resolve-SpecifyInitDir() (+2 more)

### Community 84 - "Community 84"
Cohesion: 0.22
Nodes (3): PaymentDAO, Override, PaymentServiceImpl

### Community 85 - "Community 85"
Cohesion: 0.25
Nodes (3): RevenueDAO, Override, RevenueServiceImpl

### Community 86 - "Community 86"
Cohesion: 0.28
Nodes (3): Session, EmailService, Logger

### Community 87 - "Community 87"
Cohesion: 0.22
Nodes (3): AuditLogDAO, AuditLogServiceImpl, Override

### Community 89 - "Community 89"
Cohesion: 0.26
Nodes (3): WebServlet, NotFoundException, Logger

### Community 90 - "Community 90"
Cohesion: 0.21
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, NotificationListServlet

### Community 91 - "Community 91"
Cohesion: 0.27
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, OperatorDashboardServlet, OperatorDashboardDAO

### Community 92 - "Community 92"
Cohesion: 0.32
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, TenantCreatePostServlet

### Community 94 - "Community 94"
Cohesion: 0.29
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ProfileServlet

### Community 99 - "Community 99"
Cohesion: 0.33
Nodes (5): AdminRoomServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 102 - "Community 102"
Cohesion: 0.33
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListElectricServlet

### Community 103 - "Community 103"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantCommentServlet

### Community 104 - "Community 104"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDeletePostServlet

### Community 105 - "Community 105"
Cohesion: 0.33
Nodes (6): Gson, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantLikeServlet

### Community 106 - "Community 106"
Cohesion: 0.39
Nodes (4): BaseDAO, Logger, ResultSet, Timestamp

### Community 108 - "Community 108"
Cohesion: 0.36
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentServlet

### Community 109 - "Community 109"
Cohesion: 0.36
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantMyPostsServlet

### Community 110 - "Community 110"
Cohesion: 0.36
Nodes (3): DebtDAO, DebtServiceImpl, Override

### Community 111 - "Community 111"
Cohesion: 0.25
Nodes (3): AppException, ForbiddenException, ValidationException

### Community 116 - "Community 116"
Cohesion: 0.50
Nodes (3): AuditLogHelper, HttpServletRequest, Logger

### Community 118 - "Community 118"
Cohesion: 0.50
Nodes (3): networkFirst(), offlineFallback(), STATIC_ASSETS

## Knowledge Gaps
- **2 isolated node(s):** `update-agent-context.sh script`, `STATIC_ASSETS`
  These have ≤1 connection - possible missing edges or undocumented components.
- **41 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BaseServlet` connect `Community 68` to `Community 2`, `Community 7`, `Community 8`, `Community 17`, `Community 18`, `Community 22`, `Community 28`, `Community 33`, `Community 34`, `Community 36`, `Community 38`, `Community 39`, `Community 42`, `Community 43`, `Community 44`, `Community 46`, `Community 48`, `Community 52`, `Community 53`, `Community 54`, `Community 56`, `Community 57`, `Community 61`, `Community 62`, `Community 63`, `Community 67`, `Community 69`, `Community 70`, `Community 74`, `Community 77`, `Community 78`, `Community 89`, `Community 91`, `Community 92`, `Community 94`, `Community 99`, `Community 103`, `Community 104`, `Community 105`, `Community 108`, `Community 109`?**
  _High betweenness centrality (0.157) - this node is a cross-community bridge._
- **Why does `DebtListItemDTO` connect `Community 16` to `Community 75`, `Community 110`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Why does `AuditLogDAO` connect `Community 87` to `Community 0`, `Community 32`, `Community 34`, `Community 7`, `Community 71`, `Community 41`, `Community 106`, `Community 43`, `Community 80`, `Community 17`, `Community 52`, `Community 116`, `Community 22`, `Community 23`?**
  _High betweenness centrality (0.029) - this node is a cross-community bridge._
- **What connects `update-agent-context.sh script`, `STATIC_ASSETS` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.05292929292929293 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.042206590151795634 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.07062146892655367 - nodes in this community are weakly interconnected._