<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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

            <%-- Page header --%>
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Báo cáo doanh thu</h1>
                        <p>Tổng quan doanh thu toàn hệ thống — tháng <c:out value="${selectedPeriod}"/></p>
                    </div>
                    <form method="get" action="${ctx}/admin/revenue"
                          style="display:flex;gap:8px;align-items:center;position:relative;z-index:1">
                        <input type="month" class="form-control" name="period" id="periodPicker"
                               style="max-width:160px;padding:7px 12px;font-size:0.875rem"
                               value="${not empty selectedPeriod ? selectedPeriod.substring(3).concat('-').concat(selectedPeriod.substring(0,2)) : ''}">
                        <button type="submit" class="rev-period-btn">Xem tháng</button>
                    </form>
                </div>
            </div>

            <%-- KPI cards --%>
            <c:if test="${not empty systemRevenue}">
                <div class="kpi-grid" style="margin-bottom:1.75rem">
                    <div class="kpi-surface-card highlight-success">
                        <span class="kpi-label">Tổng doanh thu</span>
                        <span class="kpi-value" style="font-size:1.5rem">
                            <fmt:formatNumber value="${systemRevenue.totalRevenue}" pattern="#,##0"/> đ
                        </span>
                    </div>
                    <div class="kpi-surface-card highlight-warning">
                        <span class="kpi-label">Chưa thanh toán</span>
                        <span class="kpi-value" style="font-size:1.5rem">
                            <fmt:formatNumber value="${systemRevenue.totalOutstanding}" pattern="#,##0"/> đ
                        </span>
                    </div>
                    <div class="kpi-surface-card highlight-accent">
                        <span class="kpi-label">Tổng phát sinh</span>
                        <span class="kpi-value" style="font-size:1.5rem">
                            <fmt:formatNumber value="${systemRevenue.totalBilledAmount}" pattern="#,##0"/> đ
                        </span>
                    </div>
                </div>
            </c:if>

            <%-- 2-column grid: Cơ sở | Theo tháng --%>
            <div class="revenue-grid">

                <%-- Bảng doanh thu từng cơ sở --%>
                <div class="data-surface" style="margin:0">
                    <div class="data-surface-header">
                        <h2 style="font-size:0.9375rem">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                 stroke="var(--hms-accent-deep)" stroke-width="2"
                                 style="margin-right:5px;vertical-align:-2px">
                                <path d="M3 21h18M5 21V7l8-4v18M19 21V11l-6-4"/>
                            </svg>
                            Cơ sở — tháng <c:out value="${selectedPeriod}"/>
                        </h2>
                        <a href="${ctx}/admin/revenue/by-facility?period=${selectedPeriod}"
                           style="font-size:0.75rem;color:var(--hms-accent-deep);font-weight:600;
                                  text-decoration:none;white-space:nowrap">
                            Xem đầy đủ &#8594;
                        </a>
                    </div>
                    <c:choose>
                        <c:when test="${not empty facilityRevenues}">
                            <div class="table-responsive">
                                <table class="table-mintlify" style="font-size:0.8125rem">
                                    <thead>
                                        <tr>
                                            <th>Cơ sở</th>
                                            <th>Đã thu</th>
                                            <th>Chưa thu</th>
                                            <th>Tổng</th>
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
                                                    <div style="font-size:0.6875rem;color:var(--hms-stone)">
                                                        <c:out value="${rev.facilityName}"/>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="rev-amount">
                                                        <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0"/> đ
                                                    </div>
                                                    <div class="rev-sub">
                                                        <span style="color:var(--hms-success);font-weight:600">${rev.paidCount}</span> HĐ
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="rev-amount--outstanding">
                                                        <fmt:formatNumber value="${rev.totalOutstanding}" pattern="#,##0"/> đ
                                                    </div>
                                                    <c:set var="unpaidTotal" value="${rev.unpaidCount + rev.overdueCount}"/>
                                                    <div class="rev-sub">
                                                        ${unpaidTotal} HĐ<c:if test="${rev.overdueCount > 0}">
                                                            (<span style="color:var(--hms-danger);font-weight:600">${rev.overdueCount} trễ</span>)
                                                        </c:if>
                                                    </div>
                                                </td>
                                                <td class="rev-amount--total">
                                                    <fmt:formatNumber value="${rev.totalBilledAmount}" pattern="#,##0"/> đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="padding:2rem 1rem">
                                <div style="font-size:1.5rem;margin-bottom:0.5rem">&#128202;</div>
                                <h4 style="font-size:0.9375rem">Chưa có dữ liệu</h4>
                                <p style="font-size:0.8125rem">Tháng <c:out value="${selectedPeriod}"/></p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- Bảng doanh thu theo tháng --%>
                <div class="data-surface" style="margin:0">
                    <div class="data-surface-header">
                        <h2 style="font-size:0.9375rem">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                 stroke="var(--hms-accent-deep)" stroke-width="2"
                                 style="margin-right:5px;vertical-align:-2px">
                                <line x1="12" y1="1" x2="12" y2="23"/>
                                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                            </svg>
                            Doanh thu 6 tháng gần nhất
                        </h2>
                        <a href="${ctx}/admin/revenue/by-period"
                           style="font-size:0.75rem;color:var(--hms-accent-deep);font-weight:600;
                                  text-decoration:none;white-space:nowrap">
                            Xem chi tiết &#8594;
                        </a>
                    </div>
                    <c:choose>
                        <c:when test="${not empty periodRevenues}">
                            <div class="table-responsive">
                                <table class="table-mintlify" style="font-size:0.8125rem">
                                    <thead>
                                        <tr>
                                            <th>Tháng</th>
                                            <th>Đã thu</th>
                                            <th>Chưa thu</th>
                                            <th>Tổng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="rev" items="${periodRevenues}">
                                            <tr>
                                                <td style="font-weight:700;color:var(--hms-accent-deep)">
                                                    <c:out value="${rev.facilityCode}"/>
                                                </td>
                                                <td>
                                                    <div class="rev-amount">
                                                        <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0"/> đ
                                                    </div>
                                                    <div class="rev-sub">
                                                        <span style="color:var(--hms-success);font-weight:600">${rev.paidCount}</span> HĐ
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="rev-amount--outstanding">
                                                        <fmt:formatNumber value="${rev.totalOutstanding}" pattern="#,##0"/> đ
                                                    </div>
                                                    <c:set var="unpaidTotal" value="${rev.unpaidCount + rev.overdueCount}"/>
                                                    <div class="rev-sub">
                                                        ${unpaidTotal} HĐ<c:if test="${rev.overdueCount > 0}">
                                                            (<span style="color:var(--hms-danger);font-weight:600">${rev.overdueCount} trễ</span>)
                                                        </c:if>
                                                    </div>
                                                </td>
                                                <td class="rev-amount--total">
                                                    <fmt:formatNumber value="${rev.totalBilledAmount}" pattern="#,##0"/> đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="padding:2rem 1rem">
                                <div style="font-size:1.5rem;margin-bottom:0.5rem">&#128201;</div>
                                <h4 style="font-size:0.9375rem">Chưa có dữ liệu</h4>
                                <p style="font-size:0.8125rem">Không có dữ liệu các tháng trước.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div><%-- /revenue-grid --%>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
(function () {
    var el = document.getElementById('periodPicker');
    if (el && !el.value) {
        var now = new Date();
        el.value = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
    }
}());
</script>
