# ASKS: Phân chia Chi tiết Đầu Việc - Dashboard cho Ban quản lý

**Total Story Points:** ~28 points (Completed)  
**Sprint Duration:** 2 weeks (Completed)

---

## Epic 1: Backend Infrastructure & Aggregations (14 points) - Completed

### Task 1.1: SQL Query Optimizations (4 points)
- [x] Write SQL queries to aggregate room occupancy and count vacant rooms
- [x] Write SQL query to calculate monthly revenue based on PAID invoices in the current calendar month
- [x] Write SQL query to calculate total outstanding debts (UNPAID + OVERDUE invoices)
- [x] Write SQL query to group tickets by status and fetch the 5 most recent requests

---

### Task 1.2: Service & DAO Implementations (6 points)
- [x] Implement DAO queries in `DashboardDAO.java`
- [x] Implement service method in `DashboardServiceImpl.java`
- [x] Add checks to ensure no null values are returned when a manager has no facilities assigned

---

### Task 1.3: Controller Routing (4 points)
- [x] Implement `ManagerDashboardServlet.java` mapping `/manager/dashboard`
- [x] Extract active manager session and query stats
- [x] Set request attributes matching the names expected by the frontend JSP

---

## Epic 2: Frontend & Integration (14 points) - Completed

### Task 2.1: JSP Layout Design (6 points)
- [x] Create JSP layout `dashboard.jsp` in `/WEB-INF/views/manager/`
- [x] Format metrics card containers for room counts, financial values, and ticket stats
- [x] Render recent tickets table with status badge color mappings

---

### Task 2.2: Phân quyền & Bảo mật (4 points)
- [x] Restrict dashboard viewing to users holding the MANAGER role
- [x] Enforce CSRF and Session checks at servlet entry

---

### Task 2.3: Verification & Quality (4 points)
- [x] Perform integration test cases for dashboard loading times
- [x] Verify financial aggregates matching database records
- [x] Test UI layouts under mobile and desktop widths
