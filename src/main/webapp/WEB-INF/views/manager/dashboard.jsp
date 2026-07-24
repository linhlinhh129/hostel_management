<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Dashboard - Ban Quản lý"/>
<c:set var="pageRole"   value="MANAGER"/>
<c:set var="activeMenu" value="dashboard"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- ── Hero ─────────────────────────────────────────── --%>
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/></h1>
                        <p>
                            <c:out value="${facilityName}"/>
                            <c:if test="${not empty facilityCode}">
                                (<c:out value="${facilityCode}"/>)
                            </c:if>
                            · Tổng quan vận hành cơ sở
                        </p>
                    </div>
                    <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                        <a href="${ctx}/manager/invoices" class="btn-accent">Quản lý hóa đơn</a>
                    </div>
                </div>
            </div>

            <%-- ── Quick Actions ─────────────────────────────────── --%>
            <div style="display:flex;flex-wrap:wrap;gap:10px;margin-bottom:1.5rem;align-items:center">
                <a href="${ctx}/manager/rooms" class="quick-action-btn primary" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/>
                        <rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/>
                    </svg>
                    Danh sách phòng
                </a>
                <a href="${ctx}/manager/contracts/create" class="quick-action-btn primary" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Tạo hợp đồng
                </a>
                <a href="${ctx}/manager/invoices?action=create" class="quick-action-btn primary" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Tạo hóa đơn
                </a>
                <a href="${ctx}/manager/notifications/create" class="quick-action-btn" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    </svg>
                    Tạo thông báo
                </a>
                <a href="${ctx}/manager/tenants" class="quick-action-btn" style="white-space:nowrap">Người thuê</a>
                <a href="${ctx}/manager/debts" class="quick-action-btn" style="white-space:nowrap">Công nợ</a>
            </div>

            <%-- ── KPI Cards — Vận hành ──────────────────────────── --%>
            <div class="kpi-grid" style="margin-bottom:1rem">
                <div class="kpi-surface-card highlight-success">
                    <span class="kpi-label">Doanh thu tháng này</span>
                    <span class="kpi-value" style="font-size:1.5rem;letter-spacing:-1.5px">
                        <fmt:formatNumber value="${monthlyRevenue}" pattern="#,##0"/>đ
                    </span>
                    <span class="kpi-trend up">Tháng hiện tại · đã thu</span>
                </div>
                <div class="kpi-surface-card">
                    <span class="kpi-label">Tổng phòng</span>
                    <span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span>
                    <span class="kpi-trend"><c:out value="${occupiedRooms}"/> đang thuê</span>
                </div>
                <div class="kpi-surface-card">
                    <span class="kpi-label">Người thuê</span>
                    <span class="kpi-value"><fmt:formatNumber value="${totalTenants}" groupingUsed="true"/></span>
                    <span class="kpi-trend"><c:out value="${activeContracts}"/> hợp đồng hiệu lực</span>
                </div>
                <div class="kpi-surface-card ${overdueInvoices > 0 ? 'highlight-danger' : ''}">
                    <span class="kpi-label">Hóa đơn quá hạn</span>
                    <span class="kpi-value"><fmt:formatNumber value="${overdueInvoices}" groupingUsed="true"/></span>
                    <span class="kpi-trend"><c:out value="${unpaidInvoices}"/> chưa thanh toán</span>
                </div>
            </div>

            <%-- ── Main 2 cột ─────────────────────────────────────── --%>
            <div class="row g-3">

                <%-- Cột trái: Tỷ lệ lấp đầy + Tài chính --%>
                <div class="col-lg-8">

                    <%-- Widget: Tỷ lệ lấp đầy + Ticket stats --%>
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Tình trạng cơ sở</h3>
                            <a href="${ctx}/manager/tickets"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Xem tất cả yêu cầu →
                            </a>
                        </div>
                        <div class="widget-surface-body">
                            <div class="row align-items-start g-4">

                                <%-- Tỷ lệ lấp đầy --%>
                                <div class="col-md-6">
                                    <div style="font-size:0.8125rem;font-weight:600;color:var(--hms-stone);margin-bottom:8px">
                                        Tỷ lệ lấp đầy phòng
                                    </div>
                                    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px">
                                        <span style="font-size:0.875rem;color:var(--hms-ink)">
                                            <strong><c:out value="${occupiedRooms}"/></strong> / <c:out value="${totalRooms}"/> phòng
                                        </span>
                                        <strong style="font-size:1rem;color:var(--hms-ink)"><c:out value="${occupancyRate}"/>%</strong>
                                    </div>
                                    <div style="height:8px;background:var(--hms-bg-soft);border-radius:999px;overflow:hidden;margin-bottom:12px">
                                        <div style="width:${occupancyRate}%;height:100%;
                                                    background:linear-gradient(90deg,var(--hms-accent),#38bdf8);
                                                    border-radius:999px;transition:width 0.8s ease"></div>
                                    </div>
                                    <div style="display:flex;gap:16px;font-size:0.8125rem">
                                        <span style="color:var(--hms-success);font-weight:600">
                                            ● <c:out value="${occupiedRooms}"/> Đang thuê
                                        </span>
                                        <span style="color:var(--hms-stone);font-weight:600">
                                            ● <c:out value="${vacantRooms}"/> Trống
                                        </span>
                                    </div>
                                </div>

                                <%-- Ticket stats --%>
                                <div class="col-md-6">
                                    <div style="font-size:0.8125rem;font-weight:600;color:var(--hms-stone);margin-bottom:8px">
                                        Yêu cầu theo trạng thái
                                    </div>
                                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px">
                                        <div style="background:#eff6ff;border:1px solid #dbeafe;border-radius:10px;
                                                    padding:10px 12px;display:flex;justify-content:space-between;align-items:center">
                                            <span style="font-size:0.8125rem;font-weight:600;color:#1d4ed8">Mới</span>
                                            <span style="font-size:0.875rem;font-weight:800;color:#1d4ed8">
                                                <c:out value="${ticketCountNew}"/>
                                            </span>
                                        </div>
                                        <div style="background:#fffbeb;border:1px solid #fef3c7;border-radius:10px;
                                                    padding:10px 12px;display:flex;justify-content:space-between;align-items:center">
                                            <span style="font-size:0.8125rem;font-weight:600;color:#b45309">Đang xử lý</span>
                                            <span style="font-size:0.875rem;font-weight:800;color:#b45309">
                                                <c:out value="${ticketCountInProgress}"/>
                                            </span>
                                        </div>
                                        <div style="background:#f0fdf4;border:1px solid #dcfce7;border-radius:10px;
                                                    padding:10px 12px;display:flex;justify-content:space-between;align-items:center">
                                            <span style="font-size:0.8125rem;font-weight:600;color:#166534">Hoàn thành</span>
                                            <span style="font-size:0.875rem;font-weight:800;color:#166534">
                                                <c:out value="${ticketCountDone}"/>
                                            </span>
                                        </div>
                                        <div style="background:#fef2f2;border:1px solid #fee2e2;border-radius:10px;
                                                    padding:10px 12px;display:flex;justify-content:space-between;align-items:center">
                                            <span style="font-size:0.8125rem;font-weight:600;color:#b91c1c">Từ chối</span>
                                            <span style="font-size:0.875rem;font-weight:800;color:#b91c1c">
                                                <c:out value="${ticketCountRejected}"/>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Widget: Yêu cầu mới nhất --%>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Yêu cầu mới nhất</h3>
                            <a href="${ctx}/manager/tickets"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Xem tất cả →
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty recentTickets}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify" style="font-size:0.8125rem">
                                            <thead>
                                            <tr>
                                                <th>Mã</th>
                                                <th>Tiêu đề</th>
                                                <th>Phòng</th>
                                                <th class="d-none d-md-table-cell">Ngày gửi</th>
                                                <th>Trạng thái</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="ticket" items="${recentTickets}">
                                                <tr>
                                                    <td>
                                                        <a href="${ctx}/manager/tickets/${ticket.id}">
                                                            <c:out value="${ticket.code}"/>
                                                        </a>
                                                    </td>
                                                    <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
                                                        <c:out value="${ticket.title}"/>
                                                    </td>
                                                    <td><c:out value="${ticket.roomCode}"/></td>
                                                    <td class="d-none d-md-table-cell" style="color:var(--hms-stone)">
                                                        <c:out value="${ticket.createdDateLabel}"/>
                                                    </td>
                                                    <td>
                                                        <span class="badge-hms <c:out value='${ticket.statusBadgeClass}'/>">
                                                            <c:out value="${ticket.statusLabel}"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Chưa có yêu cầu nào"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>

                <%-- Cột phải: Tài chính + Tổng quan --%>
                <div class="col-lg-4">

                    <%-- Widget: Tổng quan tài chính --%>
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Tổng quan tài chính</h3>
                            <a href="${ctx}/manager/debts"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Công nợ →
                            </a>
                        </div>
                        <div class="widget-surface-body" style="padding:0">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Doanh thu tháng</td>
                                    <td style="padding:10px 16px;font-weight:700;text-align:right;white-space:nowrap">
                                        <fmt:formatNumber value="${monthlyRevenue}" pattern="#,##0"/>đ
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Tổng công nợ</td>
                                    <td style="padding:10px 16px;font-weight:700;text-align:right;white-space:nowrap;color:var(--hms-warning)">
                                        <fmt:formatNumber value="${totalOutstanding}" pattern="#,##0"/>đ
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">HĐ chưa thanh toán</td>
                                    <td style="padding:10px 16px;text-align:right">
                                        <span class="badge-hms badge-warning"><c:out value="${unpaidInvoices}"/></span>
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">HĐ quá hạn</td>
                                    <td style="padding:10px 16px;text-align:right">
                                        <span class="badge-hms ${overdueInvoices > 0 ? 'badge-danger' : 'badge-neutral'}">
                                            <c:out value="${overdueInvoices}"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Giao dịch chờ duyệt</td>
                                    <td style="padding:10px 16px;text-align:right">
                                        <span class="badge-hms ${pendingPayments > 0 ? 'badge-warning' : 'badge-neutral'}">
                                            <c:out value="${pendingPayments}"/>
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <%-- Widget: Tổng quan cơ sở --%>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Tổng quan cơ sở</h3>
                            <a href="${ctx}/manager/rooms"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Quản lý →
                            </a>
                        </div>
                        <div class="widget-surface-body" style="padding:0">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Tổng phòng</td>
                                    <td style="padding:10px 16px;font-weight:700;text-align:right">
                                        <c:out value="${totalRooms}"/>
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Đang thuê</td>
                                    <td style="padding:10px 16px;text-align:right">
                                        <span class="badge-hms badge-info"><c:out value="${occupiedRooms}"/></span>
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Phòng trống</td>
                                    <td style="padding:10px 16px;text-align:right">
                                        <span class="badge-hms badge-success"><c:out value="${vacantRooms}"/></span>
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Người thuê</td>
                                    <td style="padding:10px 16px;font-weight:700;text-align:right">
                                        <c:out value="${totalTenants}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:10px 16px;color:var(--hms-stone)">Hợp đồng hiệu lực</td>
                                    <td style="padding:10px 16px;font-weight:700;text-align:right">
                                        <c:out value="${activeContracts}"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                </div>
            </div><%-- /row --%>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
