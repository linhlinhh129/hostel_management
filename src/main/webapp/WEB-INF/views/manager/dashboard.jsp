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
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Dashboard Ban Quản lý</h1>
                <p><c:out value="${facilityName}"/> (<c:out value="${facilityCode}"/>) · Tổng quan vận hành cơ sở</p>
            </div>

            <div class="quick-actions">
                <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary">Tạo thông báo</a>
                <a href="${ctx}/manager/tenants/create" class="quick-action-btn">Thêm người thuê</a>
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
                        <div class="d-flex gap-3">
                            <span class="badge-hms badge-success"><fmt:formatNumber value="${occupiedRooms}" groupingUsed="true"/> thuê</span>
                            <span class="badge-hms badge-neutral"><fmt:formatNumber value="${vacantRooms}" groupingUsed="true"/> trống</span>
                        </div>
                    </div>
                </div>
                <div class="widget-surface-surface widget-surface-surface-span-6">
                    <div class="widget-surface-surface-header"><h3>Yêu cầu theo trạng thái</h3><a href="${ctx}/manager/tickets" class="widget-surface-surface-link">Xem tất cả</a></div>
                    <div class="widget-surface-surface-body">
                        <div class="d-flex flex-wrap gap-2">
                            <span class="badge-hms badge-info">Mới: <c:out value="${ticketCountNew}"/></span>
                            <span class="badge-hms badge-warning">Đang xử lý: <c:out value="${ticketCountInProgress}"/></span>
                            <span class="badge-hms badge-success">Hoàn thành: <c:out value="${ticketCountDone}"/></span>
                            <span class="badge-hms badge-danger">Từ chối: <c:out value="${ticketCountRejected}"/></span>
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
