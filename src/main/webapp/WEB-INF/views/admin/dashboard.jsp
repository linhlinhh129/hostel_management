<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
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

            <!-- ── Hero ────────────────────────────────────────── -->
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>
                            Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/>
                        </h1>
                        <p>Tổng quan tháng <c:out value="${currentPeriodLabel}"/></p>
                    </div>
                    <a href="${ctx}/admin/revenue" class="btn-accent"
                       style="position:relative;z-index:1">
                        Báo cáo doanh thu
                    </a>
                </div>
            </div>

            <!-- ── KPI Cards (Admin.md §4) ─────────────────────── -->
            <div class="kpi-grid">
                <!-- Tổng doanh thu tháng — KPI chính mới -->
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

                <!-- Tổng cơ sở -->
                <div class="kpi-surface-card">
                    <span class="kpi-label">Tổng cơ sở</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${totalFacilities}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend"><c:out value="${activeFacilities}"/> đang hoạt động</span>
                </div>


                <!-- Thông báo -->
                <div class="kpi-surface-card">
                    <span class="kpi-label">Thông báo</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${totalNotifications}" groupingUsed="true"/>
                    </span>
                </div>

                <!-- Audit log hôm nay -->
                <div class="kpi-surface-card">
                    <span class="kpi-label">Nhật ký hôm nay</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${todayAuditLogs}" groupingUsed="true"/>
                    </span>
                </div>
            </div>

            <!-- ── Quick Actions ────────────────────────────────── -->
            <div class="d-flex flex-wrap gap-2 mb-4">
                <a href="${ctx}/admin/facilities/create"     class="quick-action-btn primary">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Thêm cơ sở
                </a>
                <a href="${ctx}/admin/personnel/create"      class="quick-action-btn primary">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Thêm nhân sự
                </a>
                <a href="${ctx}/admin/notifications/create"  class="quick-action-btn">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    </svg>
                    Tạo thông báo
                </a>
            </div>

            <!-- ── Main content 2 cột ───────────────────────────── -->
            <div class="row g-3">

                <!-- Doanh thu từng cơ sở (Admin.md §4 widget "Doanh thu tháng") -->
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
                                Xem báo cáo đầy đủ →
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty facilityRevenueStats}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify">
                                            <thead>
                                            <tr>
                                                <th>Cơ sở</th>
                                                <th>Doanh thu đã thu</th>
                                                <th>Tỷ lệ thu hồi</th>
                                                <th>Chưa thanh toán</th>
                                                <th>Quá hạn</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="stat" items="${facilityRevenueStats}">
                                                <tr>
                                                    <td>
                                                        <a href="${ctx}/admin/facilities/${stat.facilityId}"
                                                           style="font-weight:600">
                                                            <c:out value="${stat.facilityCode}"/>
                                                        </a>
                                                        <div style="font-size:0.75rem;color:var(--hms-stone)">
                                                            <c:out value="${stat.facilityName}"/>
                                                        </div>
                                                    </td>
                                                    <td style="font-weight:700">
                                                        <fmt:formatNumber value="${stat.totalRevenue}"
                                                                          pattern="#,##0"/> đ
                                                    </td>
                                                    <td>
                                                        <div style="display:flex;align-items:center;gap:6px">
                                                            <div style="width:60px;height:6px;background:var(--hms-border);
                                                                        border-radius:99px;overflow:hidden">
                                                                <div style="height:100%;width:${stat.collectionRate}%;
                                                                            background:${stat.collectionRate >= 80 ? 'var(--hms-success)' : stat.collectionRate >= 50 ? 'var(--hms-warning)' : 'var(--hms-danger)'};
                                                                            border-radius:99px"></div>
                                                            </div>
                                                            <span style="font-size:0.75rem;font-weight:700">
                                                                <c:out value="${stat.collectionRate}"/>%
                                                            </span>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span class="badge-hms badge-warning">
                                                            <c:out value="${stat.unpaidCount}"/>
                                                        </span>
                                                    </td>
                                                    <td>
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

                <!-- Hoạt động gần đây + Thống kê nhân sự -->
                <div class="col-lg-4">
                    <!-- Thống kê nhân sự -->
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Thống kê nhân sự</h3>
                            <a href="${ctx}/admin/personnel"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Quản lý →
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

                    <!-- Hoạt động gần đây -->
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Hoạt động gần đây</h3>
                            <a href="${ctx}/admin/audit-logs"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Xem chi tiết →
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty recentActivities}">
                                    <ul style="list-style:none;margin:0;padding:0">
                                        <c:forEach var="act" items="${recentActivities}" varStatus="st">
                                            <li style="padding:0.625rem 1.25rem;
                                                       border-bottom:1px solid var(--hms-border-soft);
                                                       display:flex;gap:0.625rem;align-items:flex-start;
                                                       animation:fadeInUp 0.4s ease ${st.index * 0.04}s both">
                                                <div style="width:28px;height:28px;border-radius:var(--hms-radius-sm);
                                                            background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft));
                                                            display:flex;align-items:center;justify-content:center;
                                                            color:#fff;font-size:0.625rem;font-weight:800;flex-shrink:0">
                                                    <c:choose>
                                                        <c:when test="${not empty act.actorName}">${act.actorName.charAt(0)}</c:when>
                                                        <c:otherwise>S</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div style="flex:1;min-width:0">
                                                    <p style="margin:0;font-size:0.75rem;font-weight:600;
                                                               overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
                                                        <c:out value="${act.actorName}"/>
                                                    </p>
                                                    <p style="margin:0;font-size:0.6875rem;color:var(--hms-stone)">
                                                        <c:out value="${act.actionDescription}"/>
                                                    </p>
                                                    <time style="font-size:0.625rem;color:var(--hms-muted)">
                                                        <c:out value="${act.timeLabel}"/>
                                                    </time>
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

            </div><!-- /row -->
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
