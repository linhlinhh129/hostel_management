<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo hệ thống"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="notifications"/>
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
                <h1>Thông báo hệ thống</h1>
                <p>Thông báo từ Admin quản trị hệ thống</p>
            </div>

            <div class="data-surface">
                <%-- font: Inter toàn trang, Geist Mono riêng cột mã --%>
                <style>
                    .page-content td { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; }
                    .page-content td.col-code,
                    .page-content td.col-code * { font-family: 'Geist Mono', 'JetBrains Mono', monospace !important; }
                </style>
                <c:choose>
                    <c:when test="${not empty notifications}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                    <tr>
                                        <th class="d-none d-md-table-cell">Mã</th>
                                        <th>Tiêu đề</th>
                                        <th class="d-none d-md-table-cell">Người gửi</th>
                                        <th class="d-none d-md-table-cell">Ngày gửi</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${notifications}">
                                        <tr data-href="${ctx}/operator/notifications/${item.id}">
                                            <td class="d-none d-md-table-cell col-code" style="font-size:13px; color:var(--hms-stone);">
                                                <c:out value="${item.code}"/>
                                            </td>
                                            <td style="max-width:320px; font-weight:500; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                                                <c:out value="${item.title}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <span class="badge-hms badge-neutral">Admin</span>
                                            </td>
                                            <td class="d-none d-md-table-cell"
                                                style="font-size:0.8125rem; color:var(--hms-stone);">
                                                <c:out value="${item.createdDateLabel}"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${totalItems}" groupingUsed="true"/> thông báo
                                · Trang ${currentPage} / ${totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${currentPage > 1}">
                                    <a href="${ctx}/operator/notifications?page=${currentPage - 1}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${currentPage < totalPages}">
                                    <a href="${ctx}/operator/notifications?page=${currentPage + 1}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-5 text-center">
                            <svg width="48" height="48" viewBox="0 0 24 24" fill="none"
                                 stroke="var(--hms-stone)" stroke-width="1.5" style="margin-bottom:12px">
                                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
                            </svg>
                            <h4>Chưa có thông báo nào</h4>
                            <p class="text-muted mb-4">Bạn hiện chưa nhận được thông báo hệ thống nào.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
