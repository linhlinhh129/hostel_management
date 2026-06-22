<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
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

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Doanh thu theo kỳ</h1>
                        <p>Xu hướng tăng trưởng <strong><c:out value="${selectedMonths}"/> kỳ</strong> gần nhất</p>
                    </div>
                    <div class="d-flex gap-2 flex-wrap" style="position:relative;z-index:1">
                        <form method="get" action="${ctx}/admin/revenue/by-period"
                              style="display:flex;gap:8px;align-items:center">
                            <select class="form-select" name="months" style="max-width:140px">
                                <option value="3"  ${selectedMonths == 3  ? 'selected' : ''}>3 kỳ</option>
                                <option value="6"  ${selectedMonths == 6  ? 'selected' : ''}>6 kỳ</option>
                                <option value="12" ${selectedMonths == 12 ? 'selected' : ''}>12 kỳ</option>
                            </select>
                            <button type="submit"
                                    style="background:rgba(255,255,255,0.2);border:1px solid rgba(255,255,255,0.35);
                                           padding:7px 16px;border-radius:var(--hms-radius-full);
                                           font-size:0.8125rem;font-weight:600;cursor:pointer;white-space:nowrap">
                                Xem
                            </button>
                        </form>
                        <a href="${ctx}/admin/revenue" class="quick-action-btn">← Tổng quan</a>
                    </div>
                </div>
            </div>

            <!-- Bảng doanh thu từng kỳ -->
            <div class="data-surface">
                <div class="data-surface-header">
                    <h2>Tổng doanh thu toàn hệ thống theo kỳ</h2>
                </div>
                <c:choose>
                    <c:when test="${not empty periodRevenues}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>Kỳ</th>
                                    <th>Doanh thu đã thu</th>
                                    <th>Đã TT</th>
                                    <th>Chưa TT</th>
                                    <th>Quá hạn</th>
                                    <th>Tăng trưởng</th>
                                    <th>Biểu đồ</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%-- Tìm max để normalize bar width --%>
                                <c:set var="maxRev" value="1"/>
                                <c:forEach var="p" items="${periodRevenues}">
                                    <c:if test="${p.totalRevenue > maxRev}">
                                        <c:set var="maxRev" value="${p.totalRevenue}"/>
                                    </c:if>
                                </c:forEach>
                                <c:forEach var="p" items="${periodRevenues}" varStatus="st">
                                    <tr style="${st.first ? 'background:var(--hms-accent-bg)' : ''}">
                                        <td style="font-family:var(--hms-font-mono);font-weight:700;font-size:0.9375rem">
                                            <c:out value="${p.facilityCode}"/>
                                            <c:if test="${st.first}">
                                                <span class="badge-hms badge-accent ms-1" style="font-size:0.625rem">Mới nhất</span>
                                            </c:if>
                                        </td>
                                        <td style="font-weight:800;font-size:1rem">
                                            <fmt:formatNumber value="${p.totalRevenue}" pattern="#,##0"/> đ
                                        </td>
                                        <td><span class="badge-hms badge-success"><c:out value="${p.paidCount}"/></span></td>
                                        <td><span class="badge-hms badge-warning"><c:out value="${p.unpaidCount}"/></span></td>
                                        <td>
                                            <span class="badge-hms ${p.overdueCount > 0 ? 'badge-danger' : 'badge-neutral'}">
                                                <c:out value="${p.overdueCount}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.growthRate > 0}">
                                                    <span class="badge-hms badge-success">↑ +<c:out value="${p.growthRate}"/>%</span>
                                                </c:when>
                                                <c:when test="${p.growthRate < 0}">
                                                    <span class="badge-hms badge-danger">↓ <c:out value="${p.growthRate}"/>%</span>
                                                </c:when>
                                                <c:otherwise><span class="badge-hms badge-neutral">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="min-width:120px">
                                            <%-- Bar chart inline --%>
                                            <div style="height:8px;background:var(--hms-border);border-radius:99px;overflow:hidden">
                                                <div style="height:100%;background:var(--hms-accent);border-radius:99px;
                                                            width:${maxRev > 0 ? (p.totalRevenue / maxRev * 100) : 0}%">
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2rem;margin-bottom:0.5rem">📈</div>
                            <h4>Chưa có dữ liệu</h4>
                            <p>Chưa có hóa đơn nào được ghi nhận.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Tăng trưởng theo từng cơ sở -->
            <c:if test="${not empty growthData}">
                <div class="data-surface">
                    <div class="data-surface-header">
                        <h2>Tăng trưởng theo cơ sở</h2>
                    </div>
                    <div class="table-responsive">
                        <table class="table-mintlify">
                            <thead>
                            <tr><th>Cơ sở</th><th>Kỳ</th><th>Doanh thu</th><th>Tăng trưởng</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach var="g" items="${growthData}">
                                <tr>
                                    <td>
                                        <a href="${ctx}/admin/facilities/${g.facilityId}" style="font-weight:600">
                                            <c:out value="${g.facilityCode}"/>
                                        </a>
                                        <div style="font-size:0.75rem;color:var(--hms-stone)">
                                            <c:out value="${g.facilityName}"/>
                                        </div>
                                    </td>
                                    <td style="font-family:var(--hms-font-mono)"><c:out value="${g.facilityCode}"/></td>
                                    <td style="font-weight:700">
                                        <fmt:formatNumber value="${g.totalRevenue}" pattern="#,##0"/> đ
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${g.growthRate > 0}">
                                                <span class="badge-hms badge-success">↑ +<c:out value="${g.growthRate}"/>%</span>
                                            </c:when>
                                            <c:when test="${g.growthRate < 0}">
                                                <span class="badge-hms badge-danger">↓ <c:out value="${g.growthRate}"/>%</span>
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
            </c:if>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
