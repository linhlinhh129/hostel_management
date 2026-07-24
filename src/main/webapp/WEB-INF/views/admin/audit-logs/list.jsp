<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="ctx" value="${pageContext.request.contextPath}" />
                <c:set var="pageTitle" value="Nhật ký kiểm tra - Admin" />
                <c:set var="pageRole" value="ADMIN" />
                <c:set var="activeMenu" value="audit-logs" />
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
                                    <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                                        <div>
                                            <h1>Nhật ký kiểm tra</h1>
                                            <p>Hoạt động thao tác</p>
                                        </div>
                                        <a href="${ctx}/admin/audit-logs" class="quick-action-btn"
                                            style="position:relative;z-index:1">
                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                                            </svg>
                                            Làm mới
                                        </a>
                                    </div>
                                </div>

                                <div class="data-surface">
                                    <%-- Filter bar --%>
                                        <form method="get" action="${ctx}/admin/audit-logs" class="hms-filter-form">
                                            <div style="display:flex;flex-wrap:wrap;gap:20px;margin-bottom:20px">

                                                <%-- Tìm kiếm --%>
                                                    <div style="flex:1;min-width:200px">
                                                        <label class="filter-label">Tìm kiếm</label>
                                                        <input type="text" class="form-control" name="actor"
                                                            placeholder="Tên người thực hiện..."
                                                            value="<c:out value='${filterActor}'/>" style="width:100%">
                                                    </div>

                                                    <%-- Vai trò --%>
                                                        <div style="flex:1;min-width:150px">
                                                            <label class="filter-label">Vai trò</label>
                                                            <select class="form-select" name="role" style="width:100%">
                                                                <option value="">Tất cả</option>
                                                                <option value="MANAGER" ${filterRole=='MANAGER'
                                                                    ? 'selected' : '' }>Manager</option>
                                                                <option value="OPERATOR" ${filterRole=='OPERATOR'
                                                                    ? 'selected' : '' }>Operator</option>
                                                            </select>
                                                        </div>

                                                        <%-- Đối tượng --%>
                                                            <div style="flex:1;min-width:150px">
                                                                <label class="filter-label">Đối tượng</label>
                                                                <select class="form-select" name="entityType"
                                                                    style="width:100%">
                                                                    <option value="">Tất cả</option>
                                                                    <option value="facilities"
                                                                        ${filterEntityType=='facilities' ? 'selected'
                                                                        : '' }>Cơ sở</option>
                                                                    <option value="rooms" ${filterEntityType=='rooms'
                                                                        ? 'selected' : '' }>Phòng</option>
                                                                    <option value="users" ${filterEntityType=='users'
                                                                        ? 'selected' : '' }>Nhân sự</option>
                                                                    <option value="notifications"
                                                                        ${filterEntityType=='notifications' ? 'selected'
                                                                        : '' }>Thông báo</option>
                                                                    <option value="invoices"
                                                                        ${filterEntityType=='invoices' ? 'selected' : ''
                                                                        }>Hóa đơn</option>
                                                                    <option value="payments"
                                                                        ${filterEntityType=='payments' ? 'selected' : ''
                                                                        }>Thanh toán</option>
                                                                </select>
                                                            </div>

                                                            <%-- Hành động --%>
                                                                <div style="flex:1;min-width:150px">
                                                                    <label class="filter-label">Hành động</label>
                                                                    <select class="form-select" name="action"
                                                                        style="width:100%">
                                                                        <option value="">Tất cả</option>
                                                                        <option value="CREATE" ${filterAction=='CREATE'
                                                                            ? 'selected' : '' }>Tạo mới</option>
                                                                        <option value="UPDATE" ${filterAction=='UPDATE'
                                                                            ? 'selected' : '' }>Cập nhật</option>
                                                                        <option value="DELETE" ${filterAction=='DELETE'
                                                                            ? 'selected' : '' }>Xóa</option>
                                                                    </select>
                                                                </div>

                                                                <%-- Thời gian --%>
                                                                    <div style="flex:1.5;min-width:200px">
                                                                        <label class="filter-label">Thời gian</label>
                                                                        <div style="display:flex;align-items:stretch;border:1px solid var(--hms-border-soft);
                                        border-radius:6px;overflow:hidden">
                                                                            <input type="date" class="form-control"
                                                                                name="dateFrom"
                                                                                value="<c:out value='${filterDateFrom}'/>"
                                                                                style="border:none;border-radius:0;flex:1;min-width:0;
                                              padding-left:8px;padding-right:8px">
                                                                            <div style="background:#f8f9fa;padding:0 8px;display:flex;align-items:center;
                                            border-left:1px solid var(--hms-border-soft);
                                            border-right:1px solid var(--hms-border-soft);
                                            color:#6c757d;font-size:13px;white-space:nowrap">đến</div>
                                                                            <input type="date" class="form-control"
                                                                                name="dateTo"
                                                                                value="<c:out value='${filterDateTo}'/>"
                                                                                style="border:none;border-radius:0;flex:1;min-width:0;
                                              padding-left:8px;padding-right:8px">
                                                                        </div>
                                                                    </div>
                                            </div>

                                            <%-- Nút hành động --%>
                                                <div class="d-flex justify-content-end gap-2 border-top pt-3 mt-4"
                                                    style="border-top-style:dashed!important;border-color:var(--hms-border-soft)!important">
                                                    <a href="${ctx}/admin/audit-logs"
                                                        class="btn-mintlify-secondary text-decoration-none"
                                                        style="border-radius:20px">Xóa bộ lọc</a>
                                                    <button type="submit" class="btn-mintlify-secondary"
                                                        style="border-radius:20px">Lọc dữ liệu</button>
                                                </div>

                                                <%-- Hidden fields giữ nguyên filter khi phân trang dùng JS submit --%>
                                                    <input type="hidden" name="page" value="1" id="filterPageInput" />
                                        </form>

                                        <c:choose>
                                            <c:when test="${not empty auditLogs}">
                                                <div class="table-responsive">
                                                    <table class="table-mintlify">
                                                        <thead>
                                                            <tr>
                                                                <th>Log ID</th>
                                                                <th class="d-none d-md-table-cell">Thời gian</th>
                                                                <th class="d-none d-md-table-cell">Người thực hiện</th>
                                                                <th>Đối tượng</th>
                                                                <th>Hành động</th>
                                                                <th class="d-none d-md-table-cell">Chi tiết</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="log" items="${auditLogs}">
                                                                <tr data-href="${ctx}/admin/audit-logs/${log.id}">
                                                                    <td>
                                                                        <a href="${ctx}/admin/audit-logs/${log.id}"
                                                                            class="audit-log-link">
                                                                            #
                                                                            <c:out value="${log.id}" />
                                                                        </a>
                                                                    </td>
                                                                    <td class="d-none d-md-table-cell audit-date-cell">
                                                                        <c:if test="${not empty log.createdAt}">
                                                                            <span>
                                                                                <fmt:formatDate
                                                                                    value="${log.createdAtAsDate}"
                                                                                    pattern="dd/MM/yyyy" />
                                                                            </span>
                                                                            <br />
                                                                            <span class="audit-date-time">
                                                                                <fmt:formatDate
                                                                                    value="${log.createdAtAsDate}"
                                                                                    pattern="HH:mm" />
                                                                            </span>
                                                                        </c:if>
                                                                    </td>
                                                                    <td class="d-none d-md-table-cell">
                                                                        <c:out value="${log.createdByName}" />
                                                                    </td>
                                                                    <td>
                                                                        <span
                                                                            style="font-size:0.8125rem;font-weight:500">
                                                                            <jsp:include page="_audit-badges.jsp">
                                                                                <jsp:param name="type" value="entity" />
                                                                                <jsp:param name="entityTypeValue"
                                                                                    value="${log.entityType}" />
                                                                            </jsp:include>
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <jsp:include page="_audit-badges.jsp">
                                                                            <jsp:param name="type" value="action" />
                                                                            <jsp:param name="actionValue"
                                                                                value="${log.action}" />
                                                                        </jsp:include>
                                                                    </td>
                                                                    <td class="d-none d-md-table-cell">
                                                                        <a href="${ctx}/admin/audit-logs/${log.id}"
                                                                            class="audit-detail-link">Chi tiết</a>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>

                                                <%-- Phân trang --%>
                                                    <div class="table-footer">
                                                        <span>Hiển thị
                                                            <fmt:formatNumber value="${auditLogs.size()}"
                                                                groupingUsed="true" /> mục
                                                        </span>
                                                        <div class="d-flex gap-1">
                                                            <c:if test="${currentPage > 1}">
                                                                <a href="${ctx}/admin/audit-logs?page=${currentPage - 1}&actor=<c:out value='${filterActor}'/>&action=<c:out value='${filterAction}'/>&role=<c:out value='${filterRole}'/>&entityType=<c:out value='${filterEntityType}'/>&dateFrom=<c:out value='${filterDateFrom}'/>&dateTo=<c:out value='${filterDateTo}'/>"
                                                                    class="btn-mintlify-secondary text-decoration-none"
                                                                    style="padding:5px 12px">Trước</a>
                                                            </c:if>
                                                            <c:if test="${hasNextPage}">
                                                                <a href="${ctx}/admin/audit-logs?page=${currentPage + 1}&actor=<c:out value='${filterActor}'/>&action=<c:out value='${filterAction}'/>&role=<c:out value='${filterRole}'/>&entityType=<c:out value='${filterEntityType}'/>&dateFrom=<c:out value='${filterDateFrom}'/>&dateTo=<c:out value='${filterDateTo}'/>"
                                                                    class="btn-mintlify-secondary text-decoration-none"
                                                                    style="padding:5px 12px">Sau</a>
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
                    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

                </body>

                </html>