<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Doanh thu theo kỳ - Admin"/>
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
                        <h1>Doanh thu theo tháng</h1>
                        <p>Xu hướng tăng trưởng <strong><c:out value="${selectedMonths}"/> tháng</strong> gần nhất</p>
                    </div>
                    <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;position:relative;z-index:1">
                        <form method="get" action="${ctx}/admin/revenue/by-period"
                              style="display:flex;gap:8px;align-items:center;margin:0">
                            <select class="form-select" name="months"
                                    style="width:120px;padding:7px 10px;font-size:0.875rem">
                                <option value="3"  ${selectedMonths == 3  ? 'selected' : ''}>3 tháng</option>
                                <option value="6"  ${selectedMonths == 6  ? 'selected' : ''}>6 tháng</option>
                                <option value="12" ${selectedMonths == 12 ? 'selected' : ''}>12 tháng</option>
                            </select>
                            <button type="submit" class="rev-period-btn">Xem</button>
                        </form>
                        <a href="${ctx}/admin/revenue" class="quick-action-btn">&#8592; Tổng quan</a>
                    </div>
                </div>
            </div>

            <div class="data-surface">
                <div class="data-surface-header">
                    <h2>Tổng doanh thu toàn hệ thống theo tháng</h2>
                </div>
                <c:choose>
                    <c:when test="${not empty periodRevenues}">
                        <div class="table-responsive">
                            <table class="table-mintlify period-tbl">
                                <thead>
                                    <tr>
                                        <th>Tháng</th>
                                        <th>Đã thu</th>
                                        <th>Chưa thu</th>
                                        <th>Tổng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${periodRevenues}" varStatus="st">
                                        <tr class="${st.first ? 'row-highlight' : ''}">
                                            <td style="font-weight:700;font-size:0.9375rem;white-space:nowrap">
                                                <c:out value="${p.facilityCode}"/>
                                                <c:if test="${st.first}">
                                                    <span class="badge-hms badge-accent ms-1"
                                                          style="font-size:0.5625rem">Mới</span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <div class="rev-amount--paid">
                                                    <fmt:formatNumber value="${p.totalRevenue}" pattern="#,##0"/> đ
                                                </div>
                                                <div class="rev-sub">
                                                    <span style="font-weight:600;color:var(--hms-success)">${p.paidCount}</span> HĐ
                                                </div>
                                            </td>
                                            <td>
                                                <div class="rev-amount--outstanding">
                                                    <fmt:formatNumber value="${p.totalOutstanding}" pattern="#,##0"/> đ
                                                </div>
                                                <c:set var="unpaidTotal" value="${p.unpaidCount + p.overdueCount}"/>
                                                <div class="rev-sub">
                                                    <c:choose>
                                                        <c:when test="${unpaidTotal > 0}">
                                                            ${unpaidTotal} HĐ<c:if test="${p.overdueCount > 0}">
                                                                · <span style="color:var(--hms-danger);font-weight:600">${p.overdueCount} trễ</span>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>0 HĐ</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td class="rev-amount--total">
                                                <fmt:formatNumber value="${p.totalBilledAmount}" pattern="#,##0"/> đ
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2rem;margin-bottom:0.5rem">&#128200;</div>
                            <h4>Chưa có dữ liệu</h4>
                            <p>Chưa có hóa đơn nào được ghi nhận.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
