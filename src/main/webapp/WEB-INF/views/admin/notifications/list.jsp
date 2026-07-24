<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Thông báo - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
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

            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Thông báo</h1>
                        <p>Quản lý thông báo gửi đến cư dân</p>
                    </div>
                    <a href="${ctx}/admin/notifications/create" class="quick-action-btn primary"
                       style="position:relative;z-index:1">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        Tạo thông báo
                    </a>
                </div>
            </div>

            <div class="data-surface">
                <%-- Filter bar --%>
                <form method="get" action="${ctx}/admin/notifications"
                      style="background:#fff;border:1px solid var(--hms-border-soft);
                             border-radius:8px;padding:20px;margin-bottom:20px;
                             box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                    <div style="display:flex;flex-wrap:wrap;gap:20px;margin-bottom:20px">
                        <div style="flex:1;min-width:200px">
                            <label class="filter-label">Tìm kiếm</label>
                            <input type="text" class="form-control" name="keyword"
                                   placeholder="Tiêu đề thông báo..."
                                   value="<c:out value='${keyword}'/>" style="width:100%">
                        </div>
                    </div>
                    <div style="display:flex;justify-content:flex-end;gap:12px;
                                border-top:1px dashed var(--hms-border-soft);padding-top:20px">
                        <a href="${ctx}/admin/notifications"
                           style="display:inline-flex;align-items:center;background:#fff;
                                  border:1px solid var(--hms-border);border-radius:20px;
                                  padding:6px 20px;color:var(--hms-text);text-decoration:none;
                                  font-size:14px;font-weight:500;transition:all 0.2s">Xóa bộ lọc</a>
                        <button type="submit"
                                style="display:inline-flex;align-items:center;background:#fff;
                                       border:1px solid var(--hms-border);border-radius:20px;
                                       padding:6px 20px;color:var(--hms-text);font-size:14px;
                                       font-weight:500;cursor:pointer;transition:all 0.2s">Tìm kiếm</button>
                    </div>
                </form>

                <c:choose>
                    <c:when test="${not empty page.items}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Tiêu đề</th>
                                        <th class="d-none d-md-table-cell">Người tạo</th>
                                        <th class="d-none d-md-table-cell">Đối tượng</th>
                                        <th>Trạng thái</th>
                                        <th class="d-none d-md-table-cell">Ngày tạo</th>
                                        <th class="d-none d-md-table-cell"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="notif" items="${page.items}">
                                        <tr data-href="${ctx}/admin/notifications/${notif.id}">
                                            <td>
                                                <a href="${ctx}/admin/notifications/${notif.id}">
                                                    <c:out value="${notif.code}"/>
                                                </a>
                                            </td>
                                            <td style="max-width:280px;white-space:normal;word-break:break-word">
                                                <c:out value="${notif.title}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <c:out value="${notif.createdByName}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <jsp:include page="_notif-badges.jsp">
                                                    <jsp:param name="type"          value="recipient"/>
                                                    <jsp:param name="recipientType" value="${notif.recipientType}"/>
                                                </jsp:include>
                                            </td>
                                            <td>
                                                <jsp:include page="_notif-badges.jsp">
                                                    <jsp:param name="type"        value="status"/>
                                                    <jsp:param name="statusValue" value="${notif.status}"/>
                                                </jsp:include>
                                            </td>
                                            <td class="d-none d-md-table-cell"
                                                style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                                <c:out value="${notif.createdDateLabel}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <a href="${ctx}/admin/notifications/${notif.id}"
                                                   style="font-size:0.8125rem;color:var(--hms-info);font-weight:600">
                                                    Xem
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <%-- Phân trang --%>
                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> thông báo
                                · Trang ${page.page} / ${page.totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${page.page > 1}">
                                    <a href="${ctx}/admin/notifications?page=${page.page - 1}&keyword=<c:out value='${keyword}'/>"
                                       class="btn-mintlify-secondary text-decoration-none"
                                       style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${page.page < page.totalPages}">
                                    <a href="${ctx}/admin/notifications?page=${page.page + 1}&keyword=<c:out value='${keyword}'/>"
                                       class="btn-mintlify-secondary text-decoration-none"
                                       style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-4 text-center">
                            <h4>Chưa có thông báo nào</h4>
                            <p class="text-muted">Tạo thông báo đầu tiên để gửi đến cư dân.</p>
                            <a href="${ctx}/admin/notifications/create"
                               class="quick-action-btn primary mt-2">Tạo thông báo</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
