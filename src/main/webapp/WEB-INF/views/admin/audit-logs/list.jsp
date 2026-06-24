<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
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
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Nhật ký kiểm tra</h1>
                        <p>Hoạt động thao tác của Manager và Operator</p>
                    </div>
                    <a href="${ctx}/admin/audit-logs" class="quick-action-btn" style="position:relative;z-index:1">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        </svg>
                        Làm mới
                    </a>
                </div>
            </div>

            <div class="data-surface">
                <!-- Filter bar -->
                <form class="filter-bar" method="get" action="${ctx}/admin/audit-logs">
                    <input type="text" class="form-control" name="actor"
                           placeholder="Tên người thực hiện..."
                           value="<c:out value='${filterActor}'/>"
                           style="max-width:200px">
                    <select class="form-select" name="role" style="max-width:150px">
                        <option value="">Vai trò</option>
                        <option value="MANAGER"  ${filterRole == 'MANAGER'  ? 'selected' : ''}>Manager</option>
                        <option value="OPERATOR" ${filterRole == 'OPERATOR' ? 'selected' : ''}>Operator</option>
                    </select>
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
                                    <th>Đối tượng</th>
                                    <th>Hành động</th>
                                    <th>Chi tiết</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="log" items="${auditLogs}">
                                    <tr data-href="${ctx}/admin/audit-logs/${log.id}">
                                        <td>
                                            <a href="${ctx}/admin/audit-logs/${log.id}"
                                               style="font-family:var(--hms-font-mono);font-size:0.75rem;font-weight:700">
                                                #<c:out value="${log.id}"/>
                                            </a>
                                        </td>
                                        <td style="white-space:nowrap;font-size:0.8125rem;color:var(--hms-text-muted)">
                                            <c:if test="${not empty log.createdAt}">
                                                <span>${fn:substring(log.createdAt, 8, 10)}/${fn:substring(log.createdAt, 5, 7)}/${fn:substring(log.createdAt, 0, 4)}</span>
                                                <br/>
                                                <span style="font-size:0.75rem">${fn:substring(log.createdAt, 11, 16)}</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div style="display:flex;align-items:center;gap:8px">
                                                <div style="width:26px;height:26px;border-radius:6px;
                                                            background:linear-gradient(135deg,var(--hms-hero-from),var(--hms-accent));
                                                            display:flex;align-items:center;justify-content:center;
                                                            color:#fff;font-size:0.625rem;font-weight:700;flex-shrink:0">
                                                    <c:choose>
                                                        <c:when test="${not empty log.createdByName}">${log.createdByName.charAt(0)}</c:when>
                                                        <c:otherwise>S</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <span style="font-size:0.8125rem;font-weight:500">
                                                    <c:out value="${log.createdByName}"/>
                                                </span>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty log.entityName}">
                                                    <span style="font-size:0.8125rem;font-weight:600;
                                                                 color:var(--hms-text)">
                                                        <c:out value="${log.entityName}"/>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                                        ID: <c:out value="${log.entityId}"/>
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.action == 'CREATE' or log.action == 'CREATE_EMPLOYEE'}">
                                                    <span class="badge-hms badge-success">
                                                        <c:choose>
                                                            <c:when test="${log.action == 'CREATE'}">Tạo mới</c:when>
                                                            <c:otherwise>Tạo nhân sự</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.action == 'DELETE' or log.action == 'LOCK_EMPLOYEE' or log.action == 'DEACTIVATE' or log.action == 'DELETE_EMPLOYEE'}">
                                                    <span class="badge-hms badge-danger">
                                                        <c:choose>
                                                            <c:when test="${log.action == 'DELETE'}">Xóa</c:when>
                                                            <c:when test="${log.action == 'DELETE_EMPLOYEE'}">Xóa nhân sự</c:when>
                                                            <c:when test="${log.action == 'LOCK_EMPLOYEE'}">Khóa tài khoản</c:when>
                                                            <c:otherwise>Vô hiệu hóa</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.action == 'UPDATE' or log.action == 'UPDATE_STATUS' or log.action == 'UPDATE_AREA' or log.action == 'UPDATE_EMPLOYEE'}">
                                                    <span class="badge-hms badge-info">
                                                        <c:choose>
                                                            <c:when test="${log.action == 'UPDATE'}">Cập nhật</c:when>
                                                            <c:when test="${log.action == 'UPDATE_STATUS'}">Đổi trạng thái</c:when>
                                                            <c:when test="${log.action == 'UPDATE_AREA'}">Cập nhật diện tích</c:when>
                                                            <c:otherwise>Sửa nhân sự</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </c:when>
                                                <c:when test="${log.action == 'ACTIVATE' or log.action == 'UNLOCK_EMPLOYEE'}">
                                                    <span class="badge-hms badge-accent">
                                                        <c:choose>
                                                            <c:when test="${log.action == 'ACTIVATE'}">Kích hoạt</c:when>
                                                            <c:otherwise>Mở khóa</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral"><c:out value="${log.action}"/></span>
                                                </c:otherwise>
                                            </c:choose>
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
                                    <a href="${ctx}/admin/audit-logs?page=${currentPage - 1}&actor=${filterActor}&action=${filterAction}&role=${filterRole}&entityType=${filterEntityType}&dateFrom=${filterDateFrom}&dateTo=${filterDateTo}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:5px 12px">Trước</a>
                                </c:if>
                                <c:if test="${hasNextPage}">
                                    <a href="${ctx}/admin/audit-logs?page=${currentPage + 1}&actor=${filterActor}&action=${filterAction}&role=${filterRole}&entityType=${filterEntityType}&dateFrom=${filterDateFrom}&dateTo=${filterDateTo}"
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
