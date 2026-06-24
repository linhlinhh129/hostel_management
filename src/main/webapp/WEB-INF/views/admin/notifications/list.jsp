<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
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
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Thông báo</h1>
                        <p>Quản lý thông báo gửi đến cư dân</p>
                    </div>
                    <a href="${ctx}/admin/notifications/create" class="quick-action-btn primary" style="position:relative;z-index:1">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        Tạo thông báo
                    </a>
                </div>
            </div>

            <div class="data-surface">
                <!-- Tìm kiếm -->
                <form class="filter-bar" method="get" action="${ctx}/admin/notifications">
                    <input type="text" class="form-control" name="keyword" placeholder="Tiêu đề thông báo..."
                           value="<c:out value='${keyword}'/>">
                    <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
                    <a href="${ctx}/admin/notifications" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
                </form>

                <c:choose>
                    <c:when test="${not empty page.items}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>Mã</th>
                                    <th>Tiêu đề</th>
                                    <th>Người tạo</th>
                                    <th>Đối tượng</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="notif" items="${page.items}">
                                    <tr data-href="${ctx}/admin/notifications/${notif.id}">
                                        <td><a href="${ctx}/admin/notifications/${notif.id}"><c:out value="${notif.code}"/></a></td>
                                        <td style="max-width:300px"><c:out value="${notif.title}"/></td>
                                        <td><c:out value="${notif.createdByName}"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${notif.recipientType == 'ALL'}">
                                                    <span class="badge-hms badge-info">Tất cả</span>
                                                </c:when>
                                                <c:when test="${notif.recipientType == 'FACILITY'}">
                                                    <span class="badge-hms badge-neutral">Cơ sở </span>
                                                </c:when>
                                                <c:when test="${notif.recipientType == 'ROOM'}">
                                                    <span class="badge-hms badge-neutral">Phòng</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${notif.status == 'SENT'}">
                                                    <span class="badge-hms badge-success">Đã gửi</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-warning">Nháp</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                            <c:out value="${notif.createdAt}"/>
                                        </td>
                                        <td><a href="${ctx}/admin/notifications/${notif.id}">Xem</a></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> thông báo
                                · Trang ${page.page} / ${page.totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${page.page > 1}">
                                    <a href="${ctx}/admin/notifications?page=${page.page - 1}&keyword=${keyword}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${page.page < page.totalPages}">
                                    <a href="${ctx}/admin/notifications?page=${page.page + 1}&keyword=${keyword}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-4 text-center">
                            <h4>Chưa có thông báo nào</h4>
                            <p class="text-muted">Tạo thông báo đầu tiên để gửi đến cư dân.</p>
                            <a href="${ctx}/admin/notifications/create" class="quick-action-btn primary mt-2">Tạo thông báo</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
