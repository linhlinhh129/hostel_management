<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
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

            <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
                <div>
                    <h1>Doanh thu theo cơ sở</h1>
                    <p>Kỳ: <strong><c:out value="${selectedPeriod}"/></strong></p>
                </div>
                <div class="d-flex gap-2 flex-wrap">
                    <form method="get" action="${ctx}/admin/revenue/by-facility"
                          style="display:flex;gap:8px;align-items:center">
                        <input type="text" class="form-control" name="period"
                               placeholder="MM/YYYY" value="<c:out value='${selectedPeriod}'/>"
                               style="max-width:120px">
                        <button type="submit" class="btn-mintlify-secondary">Xem</button>
                    </form>
                    <a href="${ctx}/admin/revenue" class="quick-action-btn">← Tổng quan</a>
                </div>
            </div>

            <div class="data-surface">
                <c:choose>
                    <c:when test="${not empty facilityRevenues}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>#</th>
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
                                <c:forEach var="rev" items="${facilityRevenues}" varStatus="st">
                                    <tr>
                                        <td style="color:var(--hms-stone);font-size:0.75rem">
                                            <c:out value="${st.index + 1}"/>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/facilities/${rev.facilityId}" style="font-weight:700">
                                                <c:out value="${rev.facilityCode}"/>
                                            </a>
                                            <div style="font-size:0.75rem;color:var(--hms-stone)">
                                                <c:out value="${rev.facilityName}"/>
                                            </div>
                                        </td>
                                        <td style="font-weight:800;font-size:1rem">
                                            <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0"/> đ
                                        </td>
                                        <td><span class="badge-hms badge-success"><c:out value="${rev.paidCount}"/> HĐ</span></td>
                                        <td><span class="badge-hms badge-warning"><c:out value="${rev.unpaidCount}"/> HĐ</span></td>
                                        <td>
                                            <span class="badge-hms ${rev.overdueCount > 0 ? 'badge-danger' : 'badge-neutral'}">
                                                <c:out value="${rev.overdueCount}"/> HĐ
                                            </span>
                                        </td>
                                        <td>
                                            <div style="display:flex;align-items:center;gap:8px;min-width:120px">
                                                <div style="flex:1;height:8px;background:var(--hms-border);border-radius:99px;overflow:hidden">
                                                    <div style="height:100%;width:${rev.collectionRate}%;
                                                                background:${rev.collectionRate >= 80 ? 'var(--hms-success)' : rev.collectionRate >= 50 ? 'var(--hms-warning)' : 'var(--hms-danger)'};
                                                                border-radius:99px"></div>
                                                </div>
                                                <span style="font-weight:700;font-size:0.875rem;min-width:36px">
                                                    <c:out value="${rev.collectionRate}"/>%
                                                </span>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${rev.growthRate > 0}">
                                                    <span class="badge-hms badge-success" style="font-weight:700">
                                                        ↑ +<c:out value="${rev.growthRate}"/>%
                                                    </span>
                                                </c:when>
                                                <c:when test="${rev.growthRate < 0}">
                                                    <span class="badge-hms badge-danger" style="font-weight:700">
                                                        ↓ <c:out value="${rev.growthRate}"/>%
                                                    </span>
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
                            <div style="font-size:2rem;margin-bottom:0.5rem">🏢</div>
                            <h4>Chưa có dữ liệu</h4>
                            <p>Không có hóa đơn nào trong kỳ <c:out value="${selectedPeriod}"/>.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
