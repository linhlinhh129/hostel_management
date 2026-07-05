<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Doanh thu theo cơ sở - Admin" />
            <c:set var="pageRole" value="ADMIN" />
            <c:set var="activeMenu" value="revenue" />
            <jsp:include page="/WEB-INF/views/layout/head.jsp" />

            <body>
                <div class="app-shell">
                    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay"></div>
                    <div class="main-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                        <main class="page-content">
                            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                            <div class="page-header hero-sky-gradient"
                                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                                <div
                                    style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                                    <div>
                                        <h1>Doanh thu theo cơ sở</h1>
                                        <p>Tháng: <strong>
                                                <c:out value="${selectedPeriod}" />
                                            </strong></p>
                                    </div>
                                    <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;position:relative;z-index:1">
                                        <form method="get" action="${ctx}/admin/revenue/by-facility"
                                            style="display:flex;gap:8px;align-items:center;margin:0;padding:0;background:transparent;border:none;box-shadow:none">
                                            <input type="month" class="form-control" name="period" id="byFacilityPicker"
                                                style="width:150px;padding:7px 10px;font-size:0.875rem"
                                                value="${not empty selectedPeriod ? selectedPeriod.substring(3).concat('-').concat(selectedPeriod.substring(0,2)) : ''}">
                                            <script>
                                                (function () {
                                                    var el = document.getElementById('byFacilityPicker');
                                                    if (!el.value) {
                                                        var now = new Date();
                                                        el.value = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
                                                    }
                                                })();
                                            </script>
                                            <button type="submit" style="background:#ffffff;border:1px solid rgba(255,255,255,0.8);
                                           padding:7px 16px;border-radius:var(--hms-radius-full);
                                           font-size:0.8125rem;font-weight:700;cursor:pointer;white-space:nowrap;color:var(--hms-accent-deep);
                                           box-shadow:0 2px 4px rgba(0,0,0,0.05)">
                                                Xem
                                            </button>
                                        </form>
                                        <a href="${ctx}/admin/revenue" class="quick-action-btn">← Tổng quan</a>
                                    </div>
                                </div>
                            </div>

                            <style>
                                @media (max-width: 768px) {
                                    /* Ẩn cột # trên mobile */
                                    .facility-tbl th:nth-child(1),
                                    .facility-tbl td:nth-child(1) { display: none; }
                                    /* Font nhỏ, padding gọn */
                                    .facility-tbl { font-size: 0.75rem; }
                                    .facility-tbl th,
                                    .facility-tbl td { padding: 0.5rem 0.375rem; }
                                    .facility-tbl .amt-main { font-size: 0.8125rem !important; font-weight: 700 !important; }
                                    .facility-tbl .amt-sub  { font-size: 0.625rem !important; }
                                    .facility-tbl .col-name { font-size: 0.8125rem !important; }
                                    .facility-tbl .col-name-sub { font-size: 0.625rem !important; }
                                    /* Input picker gọn hơn trên mobile */
                                    #byFacilityPicker { width: 140px !important; font-size: 0.8125rem !important; }
                                }
                            </style>

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
                                                                <c:out value="${st.index + 1 + (page.page - 1) * page.pageSize}" />
                                                            </td>
                                                            <td>
                                                                <a href="${ctx}/admin/facilities/${rev.facilityId}"
                                                                    class="col-name" style="font-weight:700;white-space:nowrap">
                                                                    <c:out value="${rev.facilityCode}" />
                                                                </a>
                                                                <div class="col-name-sub" style="font-size:0.75rem;color:var(--hms-stone)">
                                                                    <c:out value="${rev.facilityName}" />
                                                                </div>
                                                            </td>
                                                            <td>
                                                                <div class="amt-main" style="font-weight:800;font-size:1rem;white-space:nowrap">
                                                                    <fmt:formatNumber value="${rev.totalRevenue}" pattern="#,##0" /> đ
                                                                </div>
                                                                <div class="amt-sub" style="font-size:0.75rem;color:var(--hms-stone);margin-top:2px">
                                                                    <span style="font-weight:600;color:var(--hms-success)">${rev.paidCount}</span> HĐ
                                                                </div>
                                                            </td>
                                                            <td>
                                                                <div class="amt-main" style="font-weight:600;color:var(--hms-warning);white-space:nowrap">
                                                                    <fmt:formatNumber value="${rev.totalOutstanding}" pattern="#,##0" /> đ
                                                                </div>
                                                                <c:set var="unpaidTotal" value="${rev.unpaidCount + rev.overdueCount}" />
                                                                <div class="amt-sub" style="font-size:0.75rem;color:var(--hms-stone);margin-top:2px">
                                                                    <c:choose>
                                                                        <c:when test="${unpaidTotal > 0}">
                                                                            ${unpaidTotal} HĐ<c:if test="${rev.overdueCount > 0}"> · <span style="color:var(--hms-danger);font-weight:600">${rev.overdueCount} trễ</span></c:if>
                                                                        </c:when>
                                                                        <c:otherwise>0 HĐ</c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </td>
                                                            <td style="font-weight:700;color:var(--hms-accent-deep);white-space:nowrap">
                                                                <fmt:formatNumber value="${rev.totalBilledAmount}" pattern="#,##0" /> đ
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>

                                        <!-- Pagination -->
                                        <c:if test="${page.totalPages > 1}">
                                            <div
                                                style="display:flex;justify-content:space-between;align-items:center;margin-top:1.5rem;padding:0 0.5rem;flex-wrap:wrap;gap:1rem">
                                                <span style="font-size:0.875rem;color:var(--hms-stone)">
                                                    Hiển thị trang <strong>${page.page}</strong> /
                                                    <strong>${page.totalPages}</strong> (Tổng cộng
                                                    <strong>${page.total}</strong> cơ sở)
                                                </span>
                                                <div style="display:flex;gap:6px">
                                                    <c:choose>
                                                        <c:when test="${page.hasPreviousPage()}">
                                                            <a href="?period=${selectedPeriod}&page=${page.page - 1}"
                                                                class="quick-action-btn" style="text-decoration:none">
                                                                &laquo; Trước
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="quick-action-btn"
                                                                style="opacity:0.5;cursor:not-allowed">
                                                                &laquo; Trước
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:forEach var="i" begin="1" end="${page.totalPages}">
                                                        <c:choose>
                                                            <c:when test="${i == page.page}">
                                                                <span class="quick-action-btn active"
                                                                    style="background:var(--hms-accent-deep);color:#fff;border-color:var(--hms-accent-deep)">
                                                                    ${i}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="?period=${selectedPeriod}&page=${i}"
                                                                    class="quick-action-btn"
                                                                    style="text-decoration:none">
                                                                    ${i}
                                                                </a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>

                                                    <c:choose>
                                                        <c:when test="${page.hasNextPage()}">
                                                            <a href="?period=${selectedPeriod}&page=${page.page + 1}"
                                                                class="quick-action-btn" style="text-decoration:none">
                                                                Sau &raquo;
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="quick-action-btn"
                                                                style="opacity:0.5;cursor:not-allowed">
                                                                Sau &raquo;
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state">
                                            <div style="font-size:2rem;margin-bottom:0.5rem">🏢</div>
                                            <h4>Chưa có dữ liệu</h4>
                                            <p>Không có hóa đơn nào trong tháng
                                                <c:out value="${selectedPeriod}" />.
                                            </p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </main>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/views/layout/footer.jsp" />