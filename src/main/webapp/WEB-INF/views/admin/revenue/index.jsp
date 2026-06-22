<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Báo cáo doanh thu - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="revenue"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <!-- Hero header -->
            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Báo cáo doanh thu</h1>
                        <p>Tổng quan doanh thu toàn hệ thống — tháng <c:out value="${selectedPeriod}"/></p>
                    </div>
                    <!-- Chọn kỳ -->
                    <form method="get" action="${ctx}/admin/revenue"
                          style="display:flex;gap:8px;align-items:center;position:relative;z-index:1">
                        <input type="month" class="form-control" name="period" id="periodPicker"
                               style="max-width:160px;padding:7px 12px;font-size:0.875rem"
                               value="${not empty selectedPeriod ? selectedPeriod.substring(3).concat('-').concat(selectedPeriod.substring(0,2)) : ''}">
                        <script>
                            (function(){
                                var el = document.getElementById('periodPicker');
                                if (!el.value) {
                                    var now = new Date();
                                    var y = now.getFullYear();
                                    var m = String(now.getMonth() + 1).padStart(2, '0');
                                    el.value = y + '-' + m;
                                }
                            })();
                        </script>
                        <button type="submit"
                                style="background:rgba(255,255,255,0.2);border:1px solid rgba(255,255,255,0.35)
                                       ;padding:7px 16px;border-radius:var(--hms-radius-full);
                                       font-size:0.8125rem;font-weight:600;cursor:pointer;white-space:nowrap">
                            Xem tháng
                        </button>
                    </form>
                </div>
            </div>

            <!-- KPI doanh thu hệ thống -->
            <c:if test="${not empty systemRevenue}">
                <div class="kpi-grid" style="margin-bottom:1.75rem">
                    <div class="kpi-surface-card highlight-success">
                        <div class="kpi-icon" style="font-size:1.25rem">💰</div>
                        <span class="kpi-label">Tổng doanh thu đã thu</span>
                        <span class="kpi-value" style="font-size:1.5rem">
                            <fmt:formatNumber value="${systemRevenue.totalRevenue}" pattern="#,##0"/> đ
                        </span>
                        <span class="kpi-trend ${systemRevenue.growthRate >= 0 ? 'up' : 'down'}">
                            <c:choose>
                                <c:when test="${systemRevenue.growthRate >= 0}">↑</c:when>
                                <c:otherwise>↓</c:otherwise>
                            </c:choose>
                            <c:out value="${systemRevenue.growthRate}"/>% so với kỳ trước
                        </span>
                    </div>
                    <div class="kpi-surface-card">
                        <div class="kpi-icon">✅</div>
                        <span class="kpi-label">Hóa đơn đã thanh toán</span>
                        <span class="kpi-value"><fmt:formatNumber value="${systemRevenue.paidCount}"/></span>
                        <span class="kpi-trend up">Tỷ lệ thu hồi <c:out value="${systemRevenue.collectionRate}"/>%</span>
                    </div>
                    <div class="kpi-surface-card highlight-warning">
                        <div class="kpi-icon">⏳</div>
                        <span class="kpi-label">Chưa thanh toán</span>
                        <span class="kpi-value"><fmt:formatNumber value="${systemRevenue.unpaidCount}"/></span>
                    </div>
                    <div class="kpi-surface-card">
                        <div class="kpi-icon">⚠</div>
                        <span class="kpi-label">Quá hạn</span>
                        <span class="kpi-value" style="color:var(--hms-danger)">
                            <fmt:formatNumber value="${systemRevenue.overdueCount}"/>
                        </span>
                    </div>
                </div>
            </c:if>

            <!-- Sub-navigation -->
            <div class="d-flex gap-2 mb-3 flex-wrap">
                <a href="${ctx}/admin/revenue?period=${selectedPeriod}"
                   class="quick-action-btn primary">Tổng quan</a>
                <a href="${ctx}/admin/revenue/by-facility?period=${selectedPeriod}"
                   class="quick-action-btn">Theo cơ sở</a>
                <a href="${ctx}/admin/revenue/by-period?months=${selectedMonths}"
                   class="quick-action-btn">Theo kỳ / Tăng trưởng</a>
            </div>

            <!-- Bảng doanh thu từng cơ sở -->
            <div class="data-surface">
                <div class="data-surface-header">
                    <h2>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                             stroke="var(--hms-accent-deep)" stroke-width="2"
                             style="margin-right:6px;vertical-align:-2px">
                            <path d="M3 21h18M5 21V7l8-4v18M19 21V11l-6-4"/>
                        </svg>
                        Doanh thu theo cơ sở — kỳ <c:out value="${selectedPeriod}"/>
                    </h2>
                    <a href="${ctx}/admin/revenue/by-facility?period=${selectedPeriod}"
                       style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                        Xem đầy đủ →
                    </a>
                </div>

                <c:choose>
                    <c:when test="${not empty facilityRevenues}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>Cơ sở</th>
                                    <th>Doanh thu đã thu</th>
                                    <th>Đã TT</th>
                                    <th>Chưa TT</th>
                                    <th>Quá hạn</th>
                                    <th>Tỷ lệ thu hồi</th>
                                    <th>Tăng trưởng</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="rev" items="${facilityRevenues}">
                                    <tr>
                                        <td>
                                            <a href="${ctx}/admin/facilities/${rev.facilityId}"
                                               style="font-weight:700">
                                                <c:out value="${rev.facilityCode}"/>
                                            </a>
                                            <div style="font-size:0.75rem;color:var(--hms-stone)">
                                                <c:out value="${rev.facilityName}"/>
                                            </div>
                                        </td>
                                        <td style="font-weight:700">
                                            <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0"/> đ
                                        </td>
                                        <td>
                                            <span class="badge-hms badge-success">
                                                <c:out value="${rev.paidCount}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge-hms badge-warning">
                                                <c:out value="${rev.unpaidCount}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge-hms ${rev.overdueCount > 0 ? 'badge-danger' : 'badge-neutral'}">
                                                <c:out value="${rev.overdueCount}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <!-- Mini progress bar -->
                                            <div style="display:flex;align-items:center;gap:8px">
                                                <div style="flex:1;height:6px;background:var(--hms-border);border-radius:99px;overflow:hidden;min-width:60px">
                                                    <div style="height:100%;width:${rev.collectionRate}%;
                                                                background:${rev.collectionRate >= 80 ? 'var(--hms-success)' : rev.collectionRate >= 50 ? 'var(--hms-warning)' : 'var(--hms-danger)'};
                                                                border-radius:99px;transition:width 1s ease"></div>
                                                </div>
                                                <span style="font-size:0.75rem;font-weight:700;min-width:32px">
                                                    <c:out value="${rev.collectionRate}"/>%
                                                </span>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${rev.growthRate > 0}">
                                                    <span class="badge-hms badge-success">↑ <c:out value="${rev.growthRate}"/>%</span>
                                                </c:when>
                                                <c:when test="${rev.growthRate < 0}">
                                                    <span class="badge-hms badge-danger">↓ <c:out value="${rev.growthRate}"/>%</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral">—</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2rem;margin-bottom:0.5rem">📊</div>
                            <h4>Chưa có dữ liệu doanh thu</h4>
                            <p>Chưa có hóa đơn nào trong kỳ <c:out value="${selectedPeriod}"/>.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Biểu đồ tăng trưởng N kỳ gần nhất -->
            <c:if test="${not empty revenueTrend}">
                <div class="widget-surface">
                    <div class="widget-surface-header">
                        <h3>Tăng trưởng doanh thu — <c:out value="${selectedMonths}"/> kỳ gần nhất</h3>
                        <a href="${ctx}/admin/revenue/by-period"
                           style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                            Xem chi tiết →
                        </a>
                    </div>
                    <div class="widget-surface-body">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr><th>Kỳ</th><th>Doanh thu</th><th>Đã TT</th><th>Chưa TT</th><th>Tăng trưởng</th></tr>
                                </thead>
                                <tbody>
                                <c:forEach var="t" items="${revenueTrend}">
                                    <tr>
                                        <td style="font-weight:600;font-family:var(--hms-font-mono)">
                                            <c:out value="${t.facilityCode}"/>
                                        </td>
                                        <td style="font-weight:700">
                                            <fmt:formatNumber value="${t.totalRevenue}" pattern="#,##0"/> đ
                                        </td>
                                        <td><span class="badge-hms badge-success"><c:out value="${t.paidCount}"/></span></td>
                                        <td><span class="badge-hms badge-warning"><c:out value="${t.unpaidCount}"/></span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.growthRate > 0}">
                                                    <span class="badge-hms badge-success">↑ <c:out value="${t.growthRate}"/>%</span>
                                                </c:when>
                                                <c:when test="${t.growthRate < 0}">
                                                    <span class="badge-hms badge-danger">↓ <c:out value="${t.growthRate}"/>%</span>
                                                </c:when>
                                                <c:otherwise><span class="badge-hms badge-neutral">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </c:if>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
