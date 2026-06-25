<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Dashboard Ban Quản lý"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="dashboard"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <style>
                /* Premium CSS overrides for Manager Dashboard Widgets */
                .widget-surface-surface-grid {
                    display: grid !important;
                    grid-template-columns: repeat(12, 1fr) !important;
                    gap: 1.75rem !important;
                    margin-top: 1.75rem !important;
                    animation: fadeInUp 0.4s ease-out both;
                }
                
                .widget-surface-surface {
                    background: #ffffff !important;
                    border-radius: 12px !important;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04), 0 1px 3px rgba(0, 0, 0, 0.01) !important;
                    border: 1px solid #e2e8f0 !important;
                    overflow: hidden;
                    transition: transform 0.22s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.22s cubic-bezier(0.4, 0, 0.2, 1) !important;
                    display: flex;
                    flex-direction: column;
                }
                
                .widget-surface-surface:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.07), 0 2px 6px rgba(0, 0, 0, 0.03) !important;
                    border-color: rgba(0, 212, 164, 0.25) !important;
                }
                
                .widget-surface-surface-span-6 {
                    grid-column: span 6 !important;
                }
                
                .widget-surface-surface-span-12 {
                    grid-column: span 12 !important;
                }
                
                @media (max-width: 991px) {
                    .widget-surface-surface-span-6 {
                        grid-column: span 12 !important;
                    }
                }
                
                .widget-surface-surface-header {
                    padding: 1.25rem 1.5rem !important;
                    border-bottom: 1px solid #f1f5f9 !important;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    background: #fafbfc !important;
                }
                
                .widget-surface-surface-header h3 {
                    font-size: 0.95rem !important;
                    font-weight: 600 !important;
                    color: #0f172a !important;
                    margin: 0 !important;
                    letter-spacing: -0.01em;
                }
                
                .widget-surface-surface-link {
                    font-size: 0.8125rem !important;
                    color: #00b48a !important;
                    font-weight: 600 !important;
                    text-decoration: none !important;
                    transition: color 0.15s ease;
                }
                
                .widget-surface-surface-link:hover {
                    color: #00d4a4 !important;
                    text-decoration: underline !important;
                }
                
                .widget-surface-surface-body {
                    padding: 1.5rem !important;
                    flex-grow: 1;
                    display: flex;
                    flex-direction: column;
                }
                
                .widget-surface-surface-body.p-0 {
                    padding: 0 !important;
                }
                
                /* Progress bar overrides */
                .progress-hms {
                    height: 8px !important;
                    background-color: #f1f5f9 !important;
                    border-radius: 9999px !important;
                    overflow: hidden;
                    display: flex;
                    margin-bottom: 1.25rem !important;
                }
                
                .progress-hms-bar {
                    background: linear-gradient(90deg, #00d4a4 0%, #38bdf8 100%) !important;
                    height: 100% !important;
                    border-radius: 9999px !important;
                    transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1) !important;
                    position: relative;
                }
                
                .progress-hms-bar::after {
                    content: '';
                    position: absolute;
                    top: 0; left: 0; right: 0; bottom: 0;
                    background: linear-gradient(
                        90deg,
                        rgba(255, 255, 255, 0) 0%,
                        rgba(255, 255, 255, 0.25) 50%,
                        rgba(255, 255, 255, 0) 100%
                    );
                    animation: progress-shimmer 2.5s infinite linear;
                    background-size: 200% 100%;
                }
                
                @keyframes progress-shimmer {
                    0% { background-position: 200% 0; }
                    100% { background-position: -200% 0; }
                }
                
                /* Occupancy indicators style */
                .occupancy-badges {
                    display: flex;
                    gap: 0.75rem;
                    margin-top: auto;
                    padding-top: 0.5rem;
                }
                
                .occupancy-badge {
                    padding: 0.375rem 0.75rem !important;
                    font-size: 0.75rem !important;
                    font-weight: 600 !important;
                    border-radius: 9999px !important;
                    display: inline-flex;
                    align-items: center;
                    gap: 0.375rem;
                    background-color: #f8fafc !important;
                    border: 1px solid #e2e8f0 !important;
                    color: #475569 !important;
                }
                
                .occupancy-badge.badge-success {
                    background-color: #f0fdf4 !important;
                    border-color: #bbf7d0 !important;
                    color: #166534 !important;
                }
                
                .dot-indicator {
                    width: 6px;
                    height: 6px;
                    border-radius: 50%;
                    display: inline-block;
                }
                
                .dot-indicator.bg-success {
                    background-color: #166534;
                }
                
                .dot-indicator.bg-secondary {
                    background-color: #475569;
                }
                
                /* Status widget layout */
                .status-badge-grid {
                    display: grid;
                    grid-template-columns: repeat(2, 1fr);
                    gap: 0.75rem;
                    width: 100%;
                }
                
                @media (max-width: 480px) {
                    .status-badge-grid {
                        grid-template-columns: 1fr;
                    }
                }
                
                .status-summary-badge {
                    padding: 0.75rem 1rem !important;
                    font-size: 0.8125rem !important;
                    font-weight: 600 !important;
                    border-radius: 10px !important;
                    border: 1px solid transparent !important;
                    display: flex !important;
                    align-items: center;
                    justify-content: space-between;
                    transition: transform 0.18s ease, box-shadow 0.18s ease;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.01);
                }
                
                .status-summary-badge:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
                }
                
                .status-summary-badge.badge-info {
                    background-color: #eff6ff !important;
                    color: #1d4ed8 !important;
                    border-color: #dbeafe !important;
                }
                
                .status-summary-badge.badge-warning {
                    background-color: #fffbeb !important;
                    color: #b45309 !important;
                    border-color: #fef3c7 !important;
                }
                
                .status-summary-badge.badge-success {
                    background-color: #f0fdf4 !important;
                    color: #166534 !important;
                    border-color: #dcfce7 !important;
                }
                
                .status-summary-badge.badge-danger {
                    background-color: #fef2f2 !important;
                    color: #b91c1c !important;
                    border-color: #fee2e2 !important;
                }
                
                .badge-count {
                    background: rgba(255, 255, 255, 0.8);
                    padding: 0.125rem 0.5rem;
                    border-radius: 6px;
                    font-size: 0.75rem;
                    font-weight: 700;
                    border: 1px solid rgba(0, 0, 0, 0.05);
                }
                
                /* Table improvements */
                .table-mintlify {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 0;
                }
                
                .table-mintlify th {
                    background-color: #fafbfc !important;
                    color: #475569 !important;
                    font-weight: 600 !important;
                    font-size: 0.75rem !important;
                    text-transform: uppercase !important;
                    letter-spacing: 0.05em !important;
                    padding: 1rem 1.5rem !important;
                    border-bottom: 1px solid #e2e8f0 !important;
                }
                
                .table-mintlify td {
                    padding: 1rem 1.5rem !important;
                    border-bottom: 1px solid #f1f5f9 !important;
                    color: #334155 !important;
                    font-size: 0.8125rem !important;
                    vertical-align: middle !important;
                }
                
                .table-mintlify tbody tr {
                    transition: background-color 0.15s ease;
                }
                
                .table-mintlify tbody tr:hover {
                    background-color: #f8fafc !important;
                }
                
                .table-mintlify tbody tr:last-child td {
                    border-bottom: none;
                }
                
                .table-mintlify a {
                    color: #0f172a !important;
                    font-weight: 600 !important;
                    text-decoration: none !important;
                    transition: color 0.15s ease;
                }
                
                .table-mintlify a:hover {
                    color: #00b48a !important;
                    text-decoration: underline !important;
                }
                
                .table-mintlify .badge-hms {
                    padding: 0.25rem 0.625rem !important;
                    font-size: 0.7rem !important;
                    font-weight: 600 !important;
                    border-radius: 6px !important;
                }
            </style>

            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Dashboard Ban Quản lý</h1>
                <p><c:out value="${facilityName}"/> (<c:out value="${facilityCode}"/>) · Tổng quan vận hành cơ sở</p>
            </div>

            <div class="quick-actions">
                <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary">Tạo thông báo</a>
                <a href="${ctx}/manager/contracts/create" class="quick-action-btn">Tạo hợp đồng mới</a>
                <a href="${ctx}/manager/rooms" class="quick-action-btn">Danh sách căn hộ</a>
                <a href="${ctx}/manager/tickets?status=IN_PROGRESS" class="quick-action-btn">Yêu cầu đang xử lý</a>
            </div>

            <div class="kpi-grid">
                <div class="kpi-surface-card"><span class="kpi-label">Tổng phòng</span><span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Đang thuê</span><span class="kpi-value"><fmt:formatNumber value="${occupiedRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Phòng trống</span><span class="kpi-value"><fmt:formatNumber value="${vacantRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Người thuê</span><span class="kpi-value"><fmt:formatNumber value="${totalTenants}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Người phụ thuộc</span><span class="kpi-value"><fmt:formatNumber value="${totalDependents}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card highlight-warning"><span class="kpi-label">Yêu cầu đang xử lý</span><span class="kpi-value"><fmt:formatNumber value="${pendingTickets}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Thông báo đã gửi</span><span class="kpi-value"><fmt:formatNumber value="${sentNotifications}" groupingUsed="true"/></span></div>
            </div>

            <div class="widget-surface-surface-grid">
                <div class="widget-surface-surface widget-surface-surface-span-6">
                    <div class="widget-surface-surface-header"><h3>Tỷ lệ phòng trống / đang thuê</h3></div>
                    <div class="widget-surface-surface-body">
                        <div class="d-flex justify-content-between mb-2"><span>Đang thuê</span><strong><c:out value="${occupancyRate}"/>%</strong></div>
                        <div class="progress-hms mb-3"><div class="progress-hms-bar" style="width:${occupancyRate}%"></div></div>
                        <div class="occupancy-badges">
                            <span class="badge-hms badge-success occupancy-badge">
                                <span class="dot-indicator bg-success"></span>
                                <fmt:formatNumber value="${occupiedRooms}" groupingUsed="true"/> thuê
                            </span>
                            <span class="badge-hms badge-neutral occupancy-badge">
                                <span class="dot-indicator bg-secondary"></span>
                                <fmt:formatNumber value="${vacantRooms}" groupingUsed="true"/> trống
                            </span>
                        </div>
                    </div>
                </div>
                <div class="widget-surface-surface widget-surface-surface-span-6">
                    <div class="widget-surface-surface-header"><h3>Yêu cầu theo trạng thái</h3><a href="${ctx}/manager/tickets" class="widget-surface-surface-link">Xem tất cả</a></div>
                    <div class="widget-surface-surface-body">
                        <div class="status-badge-grid">
                            <span class="badge-hms badge-info status-summary-badge">
                                <span>Mới</span>
                                <span class="badge-count"><c:out value="${ticketCountNew}"/></span>
                            </span>
                            <span class="badge-hms badge-warning status-summary-badge">
                                <span>Đang xử lý</span>
                                <span class="badge-count"><c:out value="${ticketCountInProgress}"/></span>
                            </span>
                            <span class="badge-hms badge-success status-summary-badge">
                                <span>Hoàn thành</span>
                                <span class="badge-count"><c:out value="${ticketCountDone}"/></span>
                            </span>
                            <span class="badge-hms badge-danger status-summary-badge">
                                <span>Từ chối</span>
                                <span class="badge-count"><c:out value="${ticketCountRejected}"/></span>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="widget-surface-surface widget-surface-surface-span-12">
                    <div class="widget-surface-surface-header"><h3>Yêu cầu mới nhất</h3></div>
                    <div class="widget-surface-surface-body p-0">
                        <c:choose>
                            <c:when test="${not empty recentTickets}">
                                <table class="table-mintlify mb-0">
                                    <thead><tr><th>Mã</th><th>Tiêu đề</th><th>Phòng</th><th>Ngày gửi</th><th>Trạng thái</th></tr></thead>
                                    <tbody>
                                    <c:forEach var="ticket" items="${recentTickets}">
                                        <tr>
                                            <td><a href="${ctx}/manager/tickets/${ticket.id}"><c:out value="${ticket.code}"/></a></td>
                                            <td><c:out value="${ticket.title}"/></td>
                                            <td><c:out value="${ticket.roomCode}"/></td>
                                            <td><c:out value="${ticket.createdDateLabel}"/></td>
                                            <td><span class="badge-hms ${ticket.statusBadgeClass}"><c:out value="${ticket.statusLabel}"/></span></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="p-3"><jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp"><jsp:param name="message" value="Chưa có yêu cầu nào"/></jsp:include></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
