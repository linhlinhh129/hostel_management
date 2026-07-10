# Graph Report - .  (2026-07-09)

## Corpus Check
- 350 files · ~300,086 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1973 nodes · 4088 edges · 97 communities (61 shown, 36 thin omitted)
- Extraction: 80% EXTRACTED · 20% INFERRED · 0% AMBIGUOUS · INFERRED: 798 edges (avg confidence: 0.8)
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
- Community 13
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
- Community 76
- Community 77
- Community 78
- Community 79
- Community 80
- Community 81
- Community 82
- Community 83
- Community 84
- Community 85
- Community 87
- Community 89
- Community 91
- Community 92
- Community 93

## God Nodes (most connected - your core abstractions)
1. `DebtDetailDTO` - 87 edges
2. `InvoiceDetailDTO` - 87 edges
3. `Invoice` - 85 edges
4. `Request` - 79 edges
5. `BaseServlet` - 77 edges
6. `Contract` - 76 edges
7. `Facility` - 66 edges
8. `User` - 66 edges
9. `Notification` - 60 edges
10. `Room` - 55 edges

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

## Communities (97 total, 36 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (18): AdminPersonnelServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, HttpServletRequest, HttpServletResponse, Override (+10 more)

### Community 4 - "Community 4"
Cohesion: 0.05
Nodes (11): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantDependentServlet, DependentDAO, ResultSet, Dependent (+3 more)

### Community 5 - "Community 5"
Cohesion: 0.08
Nodes (23): DataSource, HttpServletRequest, HttpServletResponse, Override, WebServlet, ManagerDashboardServlet, HttpServletRequest, HttpServletResponse (+15 more)

### Community 6 - "Community 6"
Cohesion: 0.08
Nodes (23): HttpSession, HttpSessionAttributeListener, HttpSessionBindingEvent, HttpSessionEvent, HttpSessionListener, HttpServletRequest, HttpServletResponse, Override (+15 more)

### Community 8 - "Community 8"
Cohesion: 0.06
Nodes (3): ResultSet, RoomDAO, Room

### Community 18 - "Community 18"
Cohesion: 0.15
Nodes (4): InvoiceDAO, ResultSet, InvoiceServiceImpl, Override

### Community 19 - "Community 19"
Cohesion: 0.10
Nodes (3): DashboardDAO, DashboardSummaryDTO, DashboardService

### Community 21 - "Community 21"
Cohesion: 0.13
Nodes (10): StatusConstant, ForgotPasswordApiServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, ResultSet, UserDAO (+2 more)

### Community 22 - "Community 22"
Cohesion: 0.25
Nodes (5): AdminFacilityServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 23 - "Community 23"
Cohesion: 0.14
Nodes (12): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListRequestServlet, HttpServletRequest, HttpServletResponse, Override (+4 more)

### Community 24 - "Community 24"
Cohesion: 0.12
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, WebServlet, TenantRequestServlet, RequestService

### Community 26 - "Community 26"
Cohesion: 0.12
Nodes (6): InvoiceDetailServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, InvoiceService

### Community 27 - "Community 27"
Cohesion: 0.12
Nodes (14): Filter, FilterConfig, AuthFilter, FilterChain, Override, ServletRequest, ServletResponse, WebFilter (+6 more)

### Community 31 - "Community 31"
Cohesion: 0.15
Nodes (8): BaseServlet, HttpServletRequest, HttpServletResponse, Logger, AppException, ForbiddenException, NotFoundException, ValidationException

### Community 32 - "Community 32"
Cohesion: 0.13
Nodes (6): AdminDashboardServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RevenueActivityDTO

### Community 33 - "Community 33"
Cohesion: 0.21
Nodes (7): EditIncidentServlet, HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet

### Community 34 - "Community 34"
Cohesion: 0.20
Nodes (3): FacilityDAO, Connection, ResultSet

### Community 35 - "Community 35"
Cohesion: 0.19
Nodes (4): ResultSet, NotificationDAO, Override, NotificationServiceImpl

### Community 36 - "Community 36"
Cohesion: 0.15
Nodes (8): SecureRandom, CsrfFilter, FilterChain, Override, ServletRequest, ServletResponse, WebFilter, PasswordUtil

### Community 37 - "Community 37"
Cohesion: 0.31
Nodes (5): AdminNotificationServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 38 - "Community 38"
Cohesion: 0.21
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, ProfileServlet

### Community 39 - "Community 39"
Cohesion: 0.15
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantContractServlet, ContractService

### Community 42 - "Community 42"
Cohesion: 0.18
Nodes (8): ResourceBundle, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentReturnServlet, HttpServletRequest, VNPayConfig

### Community 43 - "Community 43"
Cohesion: 0.29
Nodes (5): AdminRevenueServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 44 - "Community 44"
Cohesion: 0.17
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentServlet, PaymentService

### Community 45 - "Community 45"
Cohesion: 0.17
Nodes (6): Override, HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantNotificationServlet

### Community 46 - "Community 46"
Cohesion: 0.17
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantInvoiceServlet, TenantService

### Community 47 - "Community 47"
Cohesion: 0.18
Nodes (4): DebtDAO, DebtService, DebtServiceImpl, Override

### Community 50 - "Community 50"
Cohesion: 0.24
Nodes (7): ForgotPasswordServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, RateData, RateLimitManager

### Community 51 - "Community 51"
Cohesion: 0.23
Nodes (7): HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet, UpdateMeterReadingServlet

### Community 52 - "Community 52"
Cohesion: 0.20
Nodes (4): ResultSet, AuditLogHelper, HttpServletRequest, Logger

### Community 53 - "Community 53"
Cohesion: 0.22
Nodes (10): Find-SpecifyRoot(), Format-SpecKitCommand(), Get-CurrentBranch(), Get-FeaturePathsEnv(), Get-InvokeSeparator(), Get-Python3Command(), Get-RepoRoot(), Resolve-SpecifyInitDir() (+2 more)

### Community 54 - "Community 54"
Cohesion: 0.19
Nodes (4): RoleConstant, Override, AttemptRecord, LoginAttemptTracker

### Community 55 - "Community 55"
Cohesion: 0.30
Nodes (5): AdminAuditLogServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 59 - "Community 59"
Cohesion: 0.29
Nodes (6): ErrorMessageConstant, HttpServletRequest, HttpServletResponse, Override, WebServlet, LoginServlet

### Community 60 - "Community 60"
Cohesion: 0.31
Nodes (8): IncidentReportServlet, HttpServletRequest, HttpServletResponse, Logger, MultipartConfig, Override, Part, WebServlet

### Community 61 - "Community 61"
Cohesion: 0.22
Nodes (4): ContractDAO, ResultSet, ContractServiceImpl, Override

### Community 63 - "Community 63"
Cohesion: 0.47
Nodes (4): ContractServlet, HttpServletRequest, HttpServletResponse, WebServlet

### Community 64 - "Community 64"
Cohesion: 0.32
Nodes (7): DetailRequestServlet, HttpServletRequest, HttpServletResponse, MultipartConfig, Override, Part, WebServlet

### Community 65 - "Community 65"
Cohesion: 0.21
Nodes (5): HttpServletRequest, HttpServletResponse, WebServlet, TenantDashboardServlet, NotificationService

### Community 66 - "Community 66"
Cohesion: 0.31
Nodes (3): Session, EmailService, Logger

### Community 67 - "Community 67"
Cohesion: 0.40
Nodes (5): FirstLoginServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 68 - "Community 68"
Cohesion: 0.35
Nodes (5): DebtPageServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 69 - "Community 69"
Cohesion: 0.31
Nodes (6): HttpServletRequest, HttpServletResponse, Override, WebServlet, OperatorDashboardServlet, OperatorDashboardDAO

### Community 70 - "Community 70"
Cohesion: 0.47
Nodes (5): InvoiceServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 71 - "Community 71"
Cohesion: 0.27
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, NotificationListServlet

### Community 72 - "Community 72"
Cohesion: 0.29
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, TenantPaymentServlet

### Community 76 - "Community 76"
Cohesion: 0.27
Nodes (6): FilterChain, Override, ServletRequest, ServletResponse, WebFilter, RoleFilter

### Community 77 - "Community 77"
Cohesion: 0.44
Nodes (5): AdminRoomServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet

### Community 78 - "Community 78"
Cohesion: 0.39
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, PaymentDetailServlet

### Community 79 - "Community 79"
Cohesion: 0.42
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ServicePricePageServlet

### Community 80 - "Community 80"
Cohesion: 0.33
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, MeterReadingHistoryServlet

### Community 81 - "Community 81"
Cohesion: 0.39
Nodes (6): HttpServlet, HttpServletRequest, HttpServletResponse, Override, WebServlet, LogoutServlet

### Community 82 - "Community 82"
Cohesion: 0.39
Nodes (5): HttpServletRequest, HttpServletResponse, Override, WebServlet, ListElectricServlet

## Knowledge Gaps
- **2 isolated node(s):** `update-agent-context.sh script`, `com.quanlyphongtro:hostel-management`
  These have ≤1 connection - possible missing edges or undocumented components.
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BaseServlet` connect `Community 31` to `Community 0`, `Community 4`, `Community 5`, `Community 6`, `Community 22`, `Community 23`, `Community 24`, `Community 26`, `Community 32`, `Community 33`, `Community 37`, `Community 38`, `Community 39`, `Community 42`, `Community 43`, `Community 44`, `Community 45`, `Community 46`, `Community 50`, `Community 55`, `Community 59`, `Community 63`, `Community 65`, `Community 67`, `Community 69`, `Community 70`, `Community 72`, `Community 77`, `Community 78`, `Community 79`, `Community 81`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **Why does `Invoice` connect `Community 3` to `Community 18`, `Community 26`, `Community 45`, `Community 46`?**
  _High betweenness centrality (0.084) - this node is a cross-community bridge._
- **Why does `DebtDetailDTO` connect `Community 1` to `Community 68`, `Community 47`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **What connects `update-agent-context.sh script`, `com.quanlyphongtro:hostel-management` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.07907310081223125 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.024691358024691357 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.024691358024691357 - nodes in this community are weakly interconnected._