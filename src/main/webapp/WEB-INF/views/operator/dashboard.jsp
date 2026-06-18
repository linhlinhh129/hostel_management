<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Dashboard Vận hành"/>
<c:set var="pageRole" value="OPERATOR"/>
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
                <h1>Dashboard Vận hành</h1>
                <p>Kỳ đo đạc <c:out value="${billingPeriodLabel}"/> · <c:out value="${facilityCode}"/> <c:out value="${facilityName}"/></p>
            </div>

            <div class="kpi-grid">
                <div class="kpi-surface-card"><span class="kpi-label">Phòng phụ trách</span><span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Đã cập nhật ĐN</span><span class="kpi-value"><fmt:formatNumber value="${updatedMeterRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card highlight-warning"><span class="kpi-label">Chưa cập nhật</span><span class="kpi-value"><fmt:formatNumber value="${pendingMeterRooms}" groupingUsed="true"/></span></div>
                <div class="kpi-surface-card"><span class="kpi-label">Yêu cầu đang xử lý</span><span class="kpi-value"><fmt:formatNumber value="${pendingTickets}" groupingUsed="true"/></span></div>
            </div>

            <div class="widget-surface-surface-grid">
                <div class="widget-surface-surface widget-surface-surface-span-6">
                    <div class="widget-surface-surface-header"><h3>Tiến độ cập nhật điện nước</h3><a href="${ctx}/operator/meter-readings" class="widget-surface-surface-link">Danh sách</a></div>
                    <div class="widget-surface-surface-body">
                        <div class="d-flex justify-content-between mb-2"><span>Đã cập nhật</span><strong><c:out value="${meterUpdateProgress}"/>%</strong></div>
                        <div class="progress-hms mb-3"><div class="progress-hms-bar" style="width:${meterUpdateProgress}%"></div></div>
                        <div class="d-flex gap-2">
                            <span class="badge-hms badge-success"><fmt:formatNumber value="${updatedMeterRooms}" groupingUsed="true"/> đã cập nhật</span>
                            <span class="badge-hms badge-warning"><fmt:formatNumber value="${pendingMeterRooms}" groupingUsed="true"/> chưa cập nhật</span>
                        </div>
                    </div>
                </div>
                <div class="widget-surface-surface widget-surface-surface-span-6">
                    <div class="widget-surface-surface-header"><h3>Yêu cầu theo trạng thái</h3></div>
                    <div class="widget-surface-surface-body">
                        <div class="d-flex flex-wrap gap-2">
                            <span class="badge-hms badge-info">Mới: <c:out value="${ticketCountNew}"/></span>
                            <span class="badge-hms badge-warning">Đang xử lý: <c:out value="${ticketCountInProgress}"/></span>
                            <span class="badge-hms badge-success">Hoàn thành: <c:out value="${ticketCountDone}"/></span>
                        </div>
                    </div>
                </div>
                <div class="widget-surface-surface widget-surface-surface-span-12">
                    <div class="widget-surface-surface-header">
                        <h3>Phòng chưa cập nhật chỉ số</h3>
                        <a href="${ctx}/operator/meter-readings?status=PENDING" class="quick-action-btn primary" style="padding:0.375rem 0.75rem;font-size:0.8125rem">Cập nhật ngay</a>
                    </div>
                    <div class="widget-surface-surface-body p-0">
                        <c:choose>
                            <c:when test="${not empty pendingMeterRoomList}">
                                <table class="table-mintlify mb-0">
                                    <thead><tr><th>Mã phòng</th><th>Điện kỳ trước</th><th>Nước kỳ trước</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
                                    <tbody>
                                    <c:forEach var="room" items="${pendingMeterRoomList}">
                                        <tr>
                                            <td><c:out value="${room.roomCode}"/></td>
                                            <td><c:out value="${room.previousElectricReading}"/> kWh</td>
                                            <td><c:out value="${room.previousWaterReading}"/> m³</td>
                                            <td><span class="badge-hms badge-warning">Chưa cập nhật</span></td>
                                            <td><a href="${ctx}/operator/meter-readings/${room.id}/edit">Cập nhật</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="p-3"><jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp"><jsp:param name="message" value="Tất cả phòng đã được cập nhật chỉ số"/></jsp:include></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
