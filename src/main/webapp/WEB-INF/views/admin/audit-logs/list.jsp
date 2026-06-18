<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Nhật ký kiểm tra - Admin"/>
<c:set var="pageRole"  value="ADMIN"/>
<c:set var="activeMenu" value="audit-logs"/>
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
                <h1>Nhật ký kiểm tra</h1>
                <p>Toàn bộ hoạt động thao tác trong hệ thống</p>
            </div>

            <div class="data-surface">
                <!-- Filter bar -->
                <form class="filter-bar" method="get" action="${ctx}/admin/audit-logs">
                    <input type="text" class="form-control" name="actor"
                           placeholder="Người thực hiện..."
                           value="<c:out value='${filterActor}'/>"
                           style="max-width:200px">
                    <select class="form-select" name="entityType" style="max-width:160px">
                        <option value="">Chức năng</option>
                        <option value="facilities"     ${filterEntityType == 'facilities'     ? 'selected' : ''}>Cơ sở</option>
                        <option value="rooms"          ${filterEntityType == 'rooms'          ? 'selected' : ''}>Phòng</option>
                        <option value="users"          ${filterEntityType == 'users'          ? 'selected' : ''}>Nhân sự</option>
                        <option value="notifications"  ${filterEntityType == 'notifications'  ? 'selected' : ''}>Thông báo</option>
                        <option value="invoices"       ${filterEntityType == 'invoices'       ? 'selected' : ''}>Hóa đơn</option>
                        <option value="payments"       ${filterEntityType == 'payments'       ? 'selected' : ''}>Thanh toán</option>
                    </select>
                    <select class="form-select" name="action" style="max-width:160px">
                        <option value="">Hành động</option>
                        <option value="CREATE"          ${filterAction == 'CREATE'          ? 'selected' : ''}>Tạo mới</option>
                        <option value="UPDATE"          ${filterAction == 'UPDATE'          ? 'selected' : ''}>Cập nhật</option>
                        <option value="DELETE"          ${filterAction == 'DELETE'          ? 'selected' : ''}>Xóa</option>
                        <option value="ACTIVATE"        ${filterAction == 'ACTIVATE'        ? 'selected' : ''}>Kích hoạt</option>
                        <option value="DEACTIVATE"      ${filterAction == 'DEACTIVATE'      ? 'selected' : ''}>Vô hiệu hóa</option>
                        <option value="LOCK_EMPLOYEE"   ${filterAction == 'LOCK_EMPLOYEE'   ? 'selected' : ''}>Khóa tài khoản</option>
                        <option value="UNLOCK_EMPLOYEE" ${filterAction == 'UNLOCK_EMPLOYEE' ? 'selected' : ''}>Mở khóa</option>
                        <option value="CREATE_EMPLOYEE" ${filterAction == 'CREATE_EMPLOYEE' ? 'selected' : ''}>Tạo nhân sự</option>
                    </select>
                    <input type="date" class="form-control" name="dateFrom"
                           value="<c:out value='${filterDateFrom}'/>" style="max-width:160px"
                           title="Từ ngày">
                    <input type="date" class="form-control" name="dateTo"
                           value="<c:out value='${filterDateTo}'/>" style="max-width:160px"
                           title="Đến ngày">
                    <button type="submit" class="btn-mintlify-secondary">Lọc</button>
                    <a href="${ctx}/admin/audit-logs" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
                </form>

                <c:choose>
                    <c:when test="${not empty auditLogs}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>Log ID</th>
                                    <th>Thời gian</th>
                                    <th>Người thực hiện</th>
                                    <th>Chức năng</th>
                                    <th>Hành động</th>
                                    <th>Đối tượng</th>
                                    <th>Chi tiết</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="log" items="${auditLogs}">
                                    <tr>
                                        <td>
                                            <a href="${ctx}/admin/audit-logs/${log.id}"
                                               style="font-family:var(--hms-font-mono);font-size:0.75rem;font-weight:700">
                                                #<c:out value="${log.id}"/>
                                            </a>
                                        </td>
                                        <td style="white-space:nowrap;font-size:0.8125rem;color:var(--hms-text-muted)">
                                            <c:out value="${log.createdAt}"/>
                                        </td>
                                        <td>
                                            <div style="display:flex;align-items:center;gap:8px">
                                                <div style="width:26px;height:26px;border-radius:6px;
                                                            background:linear-gradient(135deg,var(--hms-hero-from),var(--hms-accent));
                                                            display:flex;align-items:center;justify-content:center;
                                                            color:#fff;font-size:0.625rem;font-weight:700;flex-shrink:0">
                                                    <c:choose>
                                                        <c:when test="${not empty log.createdBy}">${log.createdBy}</c:when>
                                                        <c:otherwise>S</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <span style="font-size:0.8125rem;font-weight:500">
                                                    <c:out value="${log.createdBy}"/>
                                                </span>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="badge-hms badge-neutral" style="font-family:var(--hms-font-mono);font-size:0.6875rem">
                                                <c:out value="${log.entityType}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.action == 'CREATE' or log.action == 'CREATE_EMPLOYEE'}">
                                                    <span class="badge-hms badge-success"><c:out value="${log.action}"/></span>
                                                </c:when>
                                                <c:when test="${log.action == 'DELETE' or log.action == 'LOCK_EMPLOYEE' or log.action == 'DEACTIVATE'}">
                                                    <span class="badge-hms badge-danger"><c:out value="${log.action}"/></span>
                                                </c:when>
                                                <c:when test="${log.action == 'UPDATE' or log.action == 'UPDATE_STATUS' or log.action == 'UPDATE_AREA'}">
                                                    <span class="badge-hms badge-info"><c:out value="${log.action}"/></span>
                                                </c:when>
                                                <c:when test="${log.action == 'ACTIVATE' or log.action == 'UNLOCK_EMPLOYEE'}">
                                                    <span class="badge-hms badge-accent"><c:out value="${log.action}"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral"><c:out value="${log.action}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="font-size:0.8125rem;max-width:180px;
                                                   overflow:hidden;text-overflow:ellipsis;white-space:nowrap">
                                            <c:out value="${log.newValue}"/>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/audit-logs/${log.id}"
                                               style="font-size:0.8125rem;color:var(--hms-info);font-weight:600">
                                                Chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Phân trang -->
                        <div class="table-footer">
                            <span>Hiển thị <fmt:formatNumber value="${auditLogs.size()}" groupingUsed="true"/> mục</span>
                            <div class="d-flex gap-1">
                                <c:if test="${currentPage > 1}">
                                    <a href="${ctx}/admin/audit-logs?page=${currentPage - 1}&actor=${filterActor}&action=${filterAction}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:5px 12px">Trước</a>
                                </c:if>
                                <c:if test="${hasNextPage}">
                                    <a href="${ctx}/admin/audit-logs?page=${currentPage + 1}&actor=${filterActor}&action=${filterAction}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:5px 12px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2.5rem;margin-bottom:0.75rem">📋</div>
                            <h4>Chưa có nhật ký nào</h4>
                            <p>Các thao tác trong hệ thống sẽ được ghi lại tại đây.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
