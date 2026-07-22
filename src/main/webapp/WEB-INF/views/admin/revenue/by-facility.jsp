<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Doanh thu theo cơ sở - Admin"/>
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

            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Doanh thu theo cơ sở</h1>
                        <p>Tháng: <strong><c:out value="${selectedPeriod}"/></strong></p>
                    </div>
                    <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;position:relative;z-index:1">
                        <form method="get" action="${ctx}/admin/revenue/by-facility"
                              style="display:flex;gap:8px;align-items:center;margin:0">
                            <input type="month" class="form-control" name="period" id="byFacilityPicker"
                                   style="width:150px;padding:7px 10px;font-size:0.875rem"
                                   value="${not empty selectedPeriod ? selectedPeriod.substring(3).concat('-').concat(selectedPeriod.substring(0,2)) : ''}">
                            <button type="submit" class="rev-period-btn">Xem</button>
                        </form>
                        <a href="${ctx}/admin/revenue" class="quick-action-btn">&#8592; Tổng quan</a>
                    </div>
                </div>
            </div>

            <div class="data-surface">
                <c:choose>
                    <c:when test="${not empty facilityRevenues}">
                        <div class="table-responsive">
                            <table class="table-mintlify facility-tbl">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Cơ sở</th>
                                        <th>Đã thu</th>
                                        <th>Chưa thu</th>
                                        <th>Tổng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="rev" items="${facilityRevenues}" varStatus="st">
                                        <tr>
                                            <td style="color:var(--hms-stone);font-size:0.75rem">
                                                <c:out value="${st.index + 1 + (page.page - 1) * page.pageSize}"/>
                                            </td>
                                            <td>
                                                <a href="${ctx}/admin/facilities/${rev.facilityId}"
                                                   style="font-weight:700;white-space:nowrap">
                                                    <c:out value="${rev.facilityCode}"/>
                                                </a>
                                                <div style="font-size:0.75rem;color:var(--hms-stone)">
                                                    <c:out value="${rev.facilityName}"/>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="rev-amount--paid">
                                                    <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0"/> đ
                                                </div>
                                                <div class="rev-sub">
                                                    <span style="font-weight:600;color:var(--hms-success)">${rev.paidCount}</span> HĐ
                                                </div>
                                            </td>
                                            <td>
                                                <div class="rev-amount--outstanding">
                                                    <fmt:formatNumber value="${rev.totalOutstanding}" pattern="#,##0"/> đ
                                                </div>
                                                <c:set var="unpaidTotal" value="${rev.unpaidCount + rev.overdueCount}"/>
                                                <div class="rev-sub">
                                                    <c:choose>
                                                        <c:when test="${unpaidTotal > 0}">
                                                            ${unpaidTotal} HĐ<c:if test="${rev.overdueCount > 0}">
                                                                · <span style="color:var(--hms-danger);font-weight:600">${rev.overdueCount} trễ</span>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>0 HĐ</c:otherwise>
                                                    </c:choose>
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

                        <%-- Phân trang --%>
                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <strong>${page.total}</strong> cơ sở
                                · Trang <strong>${page.page}</strong> / <strong>${page.totalPages}</strong>
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${page.hasPreviousPage()}">
                                    <a href="?period=${selectedPeriod}&page=${page.page - 1}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${page.hasNextPage()}">
                                    <a href="?period=${selectedPeriod}&page=${page.page + 1}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2rem;margin-bottom:0.5rem">&#127962;</div>
                            <h4>Chưa có dữ liệu</h4>
                            <p>Không có hóa đơn nào trong tháng <c:out value="${selectedPeriod}"/>.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
(function () {
    var el = document.getElementById('byFacilityPicker');
    if (el && !el.value) {
        var now = new Date();
        el.value = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
    }
}());
</script>
