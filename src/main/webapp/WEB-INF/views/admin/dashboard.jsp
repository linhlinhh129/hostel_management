<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<c:set var="ctx"         value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"   value="Dashboard Admin - Quản lý Nhà trọ"/>
<c:set var="pageRole"    value="ADMIN"/>
<c:set var="activeMenu"  value="dashboard"/>
<c:set var="pageHeading" value="Dashboard"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- Hero --%>
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/></h1>
                        <p>Tổng quan tháng <c:out value="${currentPeriodLabel}"/></p>
                    </div>
                    <a href="${ctx}/admin/revenue" class="btn-accent" style="position:relative;z-index:1">
                        Báo cáo doanh thu
                    </a>
                </div>
            </div>

            <%-- KPI Cards --%>
            <div class="kpi-grid">
                <div class="kpi-surface-card highlight-success">
                    <span class="kpi-label">Doanh thu tháng này</span>
                    <span class="kpi-value" style="font-size:1.5rem;letter-spacing:-1.5px">
                        <c:choose>
                            <c:when test="${not empty monthlyRevenue}">
                                <fmt:formatNumber value="${monthlyRevenue}" pattern="#,##0"/>đ
                            </c:when>
                            <c:otherwise>0đ</c:otherwise>
                        </c:choose>
                    </span>
                    <span class="kpi-trend up">Tháng <c:out value="${currentPeriodLabel}"/></span>
                </div>
                <div class="kpi-surface-card">
                    <span class="kpi-label">Tổng cơ sở</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${totalFacilities}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend"><c:out value="${activeFacilities}"/> đang hoạt động</span>
                </div>
                <div class="kpi-surface-card">
                    <span class="kpi-label">Thông báo</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${totalNotifications}" groupingUsed="true"/>
                    </span>
                </div>
                <div class="kpi-surface-card">
                    <span class="kpi-label">Nhật ký hôm nay</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${todayAuditLogs}" groupingUsed="true"/>
                    </span>
                </div>
            </div>

            <%-- Quick Actions --%>
            <div class="quick-actions-bar">
                <a href="${ctx}/admin/facilities/create" class="quick-action-btn primary"
                   style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Thêm cơ sở
                </a>
                <a href="${ctx}/admin/personnel/create" class="quick-action-btn primary"
                   style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Thêm nhân sự
                </a>
                <a href="${ctx}/admin/notifications/create" class="quick-action-btn primary"
                   style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    </svg>
                    Tạo thông báo
                </a>
            </div>

            <%-- Main content 2 cột --%>
            <div class="row g-3">

                <%-- Doanh thu từng cơ sở --%>
                <div class="col-lg-8">
                    <div class="widget-surface" style="height:100%">
                        <div class="widget-surface-header">
                            <h3>
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent)" stroke-width="2"
                                     style="margin-right:6px;vertical-align:-2px">
                                    <line x1="12" y1="1" x2="12" y2="23"/>
                                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                                </svg>
                                Doanh thu — tháng <c:out value="${currentPeriodLabel}"/>
                            </h3>
                            <a href="${ctx}/admin/revenue"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);
                                      font-weight:600;text-decoration:none">
                                Xem báo cáo đầy đủ &#8594;
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty facilityRevenueStats}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify" style="font-size:0.8125rem">
                                            <thead>
                                                <tr>
                                                    <th style="min-width:120px">Cơ sở</th>
                                                    <th style="min-width:110px;text-align:right">Đã thu</th>
                                                    <th style="text-align:center">Chưa TT</th>
                                                    <th style="text-align:center">Quá hạn</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="stat" items="${facilityRevenueStats}">
                                                    <tr>
                                                        <td style="max-width:140px">
                                                            <a href="${ctx}/admin/facilities/${stat.facilityId}"
                                                               style="font-weight:700;font-size:0.8125rem;
                                                                      display:block;overflow:hidden;
                                                                      text-overflow:ellipsis;white-space:nowrap">
                                                                <c:out value="${stat.facilityCode}"/>
                                                            </a>
                                                            <div style="font-size:0.6875rem;color:var(--hms-stone);
                                                                        overflow:hidden;text-overflow:ellipsis;
                                                                        white-space:nowrap;max-width:130px">
                                                                <c:out value="${stat.facilityName}"/>
                                                            </div>
                                                        </td>
                                                        <td style="font-weight:700;text-align:right;white-space:nowrap">
                                                            <fmt:formatNumber value="${stat.totalRevenue}" pattern="#,##0"/>đ
                                                        </td>
                                                        <td style="text-align:center">
                                                            <span class="badge-hms badge-warning">
                                                                <c:out value="${stat.unpaidCount}"/>
                                                            </span>
                                                        </td>
                                                        <td style="text-align:center">
                                                            <span class="badge-hms ${stat.overdueCount > 0 ? 'badge-danger' : 'badge-neutral'}">
                                                                <c:out value="${stat.overdueCount}"/>
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
                                            <jsp:param name="message" value="Chưa có dữ liệu doanh thu kỳ này"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <%-- Thống kê nhân sự + Hoạt động gần đây --%>
                <div class="col-lg-4">

                    <%-- Thống kê nhân sự --%>
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Thống kê nhân sự</h3>
                            <a href="${ctx}/admin/personnel"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);
                                      font-weight:600;text-decoration:none">
                                Quản lý &#8594;
                            </a>
                        </div>
                        <div class="widget-surface-body">
                            <div class="d-flex justify-content-around">
                                <div class="text-center">
                                    <div style="font-size:1.75rem;font-weight:800;color:var(--hms-ink)">
                                        <fmt:formatNumber value="${totalPersonnel}" groupingUsed="true"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone)">Tổng nhân sự</div>
                                </div>
                                <div style="width:1px;background:var(--hms-border)"></div>
                                <div class="text-center">
                                    <div style="font-size:1.75rem;font-weight:800;color:var(--hms-info)">
                                        <fmt:formatNumber value="${managerCount}" groupingUsed="true"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone)">Ban Quản lý</div>
                                </div>
                                <div style="width:1px;background:var(--hms-border)"></div>
                                <div class="text-center">
                                    <div style="font-size:1.75rem;font-weight:800;color:var(--hms-warning)">
                                        <fmt:formatNumber value="${operatorCount}" groupingUsed="true"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone)">Vận hành</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Hoạt động gần đây --%>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Hoạt động gần đây</h3>
                            <a href="${ctx}/admin/audit-logs"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);
                                      font-weight:600;text-decoration:none">
                                Xem chi tiết &#8594;
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty recentActivities}">
                                    <ul style="list-style:none;margin:0;padding:0">
                                        <c:forEach var="act" items="${recentActivities}" varStatus="st">
                                            <li class="activity-item" data-anim-index="${st.index}">
                                                <div class="activity-avatar">
                                                    <c:choose>
                                                        <c:when test="${not empty act.actorName}">
                                                            ${fn:substring(act.actorName, 0, 1)}
                                                        </c:when>
                                                        <c:otherwise>S</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="activity-body">
                                                    <p class="activity-actor"><c:out value="${act.actorName}"/></p>
                                                    <p class="activity-desc"><c:out value="${act.actionDescription}"/></p>
                                                    <time class="activity-time"><c:out value="${act.timeLabel}"/></time>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-3">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Chưa có hoạt động nào"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>

            </div><%-- /row --%>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
document.querySelectorAll('[data-anim-index]').forEach(function (el) {
    var delay = (parseInt(el.getAttribute('data-anim-index'), 10) * 0.04) + 's';
    el.style.animation = 'fadeInUp 0.4s ease ' + delay + ' both';
});
</script>
