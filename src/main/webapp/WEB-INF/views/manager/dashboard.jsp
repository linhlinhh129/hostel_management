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
                .quick-actions {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 0.75rem;
                    margin-top: 1.25rem;
                    margin-bottom: 1.75rem;
                }
            </style>

            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Dashboard Ban Quản lý</h1>
                <p><c:out value="${facilityName}"/> (<c:out value="${facilityCode}"/>) · Tổng quan vận hành cơ sở</p>
            </div>

            <div class="mb-4">
                <h3 style="font-size: 0.95rem; font-weight: 600; color: #0f172a; margin-top: 1.5rem; margin-bottom: 0.75rem;">Phím tắt Vận hành</h3>
                <div class="quick-actions" style="margin-top: 0; margin-bottom: 0.75rem;">
                    <a href="${ctx}/manager/rooms" class="quick-action-btn primary">Danh sách căn hộ</a>
                    <a href="${ctx}/manager/tenants" class="quick-action-btn">Quản lý người thuê</a>
                    <a href="${ctx}/manager/tickets" class="quick-action-btn">Danh sách yêu cầu</a>
                    <a href="${ctx}/manager/notifications/create" class="quick-action-btn">Tạo thông báo</a>
                </div>
            </div>

            <div class="mb-4">
                <h3 style="font-size: 0.95rem; font-weight: 600; color: #0f172a; margin-top: 1rem; margin-bottom: 0.75rem;">Phím tắt Tài chính</h3>
                <div class="quick-actions" style="margin-top: 0; margin-bottom: 1.5rem;">
                    <a href="${ctx}/manager/contracts/create" class="quick-action-btn primary">Tạo hợp đồng mới</a>
                    <a href="${ctx}/manager/invoices?action=create" class="quick-action-btn">Tạo hóa đơn mới</a>
                    <a href="${ctx}/manager/payments" class="quick-action-btn">Lịch sử giao dịch</a>
                    <a href="${ctx}/manager/service-prices" class="quick-action-btn">Thiết lập giá dịch vụ</a>
                    <a href="${ctx}/manager/debts" class="quick-action-btn">Báo cáo công nợ</a>
                </div>
            </div>

            <h2 style="font-size: 1.1rem; font-weight: 600; color: #0f172a; margin-top: 1.75rem; margin-bottom: 0.75rem;">Thông số Vận hành</h2>
            <div class="kpi-grid mb-4">
                <div class="kpi-surface-card"><span class="kpi-label">Tổng phòng</span><span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Đang thuê</span><span class="kpi-value"><fmt:formatNumber value="${occupiedRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Phòng trống</span><span class="kpi-value"><fmt:formatNumber value="${vacantRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Người thuê</span><span class="kpi-value"><fmt:formatNumber value="${totalTenants}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Hợp đồng hiệu lực</span><span class="kpi-value"><fmt:formatNumber value="${activeContracts}" groupingUsed="true"/></span></div>
            </div>

            <h2 style="font-size: 1.1rem; font-weight: 600; color: #0f172a; margin-top: 1.75rem; margin-bottom: 0.75rem;">Thông số Tài chính</h2>
            <div class="kpi-grid">
                <div class="kpi-surface-card"><span class="kpi-label">Doanh thu tháng này</span><span class="kpi-value"><fmt:formatNumber value="${monthlyRevenue}" pattern="#,##0"/> đ</span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Tổng công nợ</span><span class="kpi-value"><fmt:formatNumber value="${totalOutstanding}" pattern="#,##0"/> đ</span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Hóa đơn chưa thanh toán</span><span class="kpi-value"><fmt:formatNumber value="${unpaidInvoices}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Hóa đơn quá hạn</span><span class="kpi-value"><fmt:formatNumber value="${overdueInvoices}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card highlight-warning"><span class="kpi-label">Giao dịch chờ duyệt</span><span class="kpi-value"><fmt:formatNumber value="${pendingPayments}" groupingUsed="true"/></span></div>
            </div>

            <div class="widget-surface-surface-grid">
                <div class="widget-surface-surface widget-surface-surface-span-12">
                    <div class="widget-surface-surface-header">
                        <h3>Hiệu suất lấp đầy & Thống kê yêu cầu</h3>
                        <a href="${ctx}/manager/tickets" class="widget-surface-surface-link">Xem tất cả yêu cầu</a>
                    </div>
                    <div class="widget-surface-surface-body" style="padding: 1.5rem !important;">
                        <div class="row align-items-center">
                            <!-- Left: Occupancy Rate -->
                            <div class="col-12 col-md-6 pr-md-4" style="border-right: 1px solid #e2e8f0;">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span style="font-size: 0.875rem; font-weight: 500; color: #475569;">Tỷ lệ lấp đầy phòng</span>
                                    <strong style="color: #0f172a; font-size: 1rem;"><c:out value="${occupancyRate}"/>%</strong>
                                </div>
                                <div class="progress-hms mb-3" style="height: 8px !important; margin-bottom: 0.75rem !important;"><div class="progress-hms-bar" style="width:${occupancyRate}%"></div></div>
                                <div class="d-flex gap-3">
                                    <span style="font-size: 0.8125rem; color: #166534; font-weight: 600; display: inline-flex; align-items: center; gap: 0.375rem;">
                                        <span class="dot-indicator bg-success" style="width: 8px; height: 8px;"></span>
                                        <fmt:formatNumber value="${occupiedRooms}" groupingUsed="true"/> Đang thuê
                                    </span>
                                    <span style="font-size: 0.8125rem; color: #475569; font-weight: 600; display: inline-flex; align-items: center; gap: 0.375rem;">
                                        <span class="dot-indicator bg-secondary" style="width: 8px; height: 8px;"></span>
                                        <fmt:formatNumber value="${vacantRooms}" groupingUsed="true"/> Phòng trống
                                    </span>
                                </div>
                            </div>
                            <!-- Right: Ticket status stats -->
                            <div class="col-12 col-md-6 pl-md-4 mt-3 mt-md-0">
                                <div style="font-size: 0.875rem; font-weight: 500; color: #475569; margin-bottom: 0.75rem; padding-left: 0.5rem;">Yêu cầu theo trạng thái</div>
                                <div class="status-badge-grid" style="grid-template-columns: repeat(2, 1fr); gap: 0.5rem;">
                                    <span class="badge-hms badge-info status-summary-badge" style="padding: 0.5rem 0.75rem !important; border-radius: 8px !important;">
                                        <span>Mới</span>
                                        <span class="badge-count" style="font-size: 0.75rem; font-weight: 700; padding: 0.125rem 0.375rem;"><c:out value="${ticketCountNew}"/></span>
                                    </span>
                                    <span class="badge-hms badge-warning status-summary-badge" style="padding: 0.5rem 0.75rem !important; border-radius: 8px !important;">
                                        <span>Đang xử lý</span>
                                        <span class="badge-count" style="font-size: 0.75rem; font-weight: 700; padding: 0.125rem 0.375rem;"><c:out value="${ticketCountInProgress}"/></span>
                                    </span>
                                    <span class="badge-hms badge-success status-summary-badge" style="padding: 0.5rem 0.75rem !important; border-radius: 8px !important;">
                                        <span>Hoàn thành</span>
                                        <span class="badge-count" style="font-size: 0.75rem; font-weight: 700; padding: 0.125rem 0.375rem;"><c:out value="${ticketCountDone}"/></span>
                                    </span>
                                    <span class="badge-hms badge-danger status-summary-badge" style="padding: 0.5rem 0.75rem !important; border-radius: 8px !important;">
                                        <span>Từ chối</span>
                                        <span class="badge-count" style="font-size: 0.75rem; font-weight: 700; padding: 0.125rem 0.375rem;"><c:out value="${ticketCountRejected}"/></span>
                                    </span>
                                </div>
                            </div>
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
                                        <tr data-href="${ctx}/manager/tickets/${ticket.id}">
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
