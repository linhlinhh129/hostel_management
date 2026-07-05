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
                        <p>Hoạt động thao tác</p>
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
                <!-- Filter bar (Thiết kế tối ưu theo ảnh mẫu) -->
                <form method="get" action="${ctx}/admin/audit-logs" style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        
                        <!-- Tìm kiếm -->
                        <div style="flex:1; min-width:200px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Tìm kiếm</label>
                            <input type="text" class="form-control" name="actor"
                                   placeholder="Tên người thực hiện..."
                                   value="<c:out value='${filterActor}'/>" style="width:100%">
                        </div>

                        <!-- Vai trò -->
                        <div style="flex:1; min-width:150px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Vai trò</label>
                            <select class="form-select" name="role" style="width:100%">
                                <option value="">Tất cả</option>
                                <option value="MANAGER"  ${filterRole == 'MANAGER'  ? 'selected' : ''}>Manager</option>
                                <option value="OPERATOR" ${filterRole == 'OPERATOR' ? 'selected' : ''}>Operator</option>
                            </select>
                        </div>

                        <!-- Đối tượng -->
                        <div style="flex:1; min-width:150px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Đối tượng</label>
                            <select class="form-select" name="entityType" style="width:100%">
                                <option value="">Tất cả</option>
                                <option value="facilities"     ${filterEntityType == 'facilities'     ? 'selected' : ''}>Cơ sở</option>
                                <option value="rooms"          ${filterEntityType == 'rooms'          ? 'selected' : ''}>Phòng</option>
                                <option value="users"          ${filterEntityType == 'users'          ? 'selected' : ''}>Nhân sự</option>
                                <option value="notifications"  ${filterEntityType == 'notifications'  ? 'selected' : ''}>Thông báo</option>
                                <option value="invoices"       ${filterEntityType == 'invoices'       ? 'selected' : ''}>Hóa đơn</option>
                                <option value="payments"       ${filterEntityType == 'payments'       ? 'selected' : ''}>Thanh toán</option>
                            </select>
                        </div>
                        


                        <!-- Thời gian -->
                        <div style="flex:1.5; min-width:200px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Thời gian</label>
                            <div style="display:flex; align-items:stretch; border:1px solid var(--hms-border-soft); border-radius:6px; overflow:hidden;">
                                <input type="date" class="form-control" name="dateFrom"
                                       value="<c:out value='${filterDateFrom}'/>" style="border:none; border-radius:0; flex:1; min-width:0; padding-left:8px; padding-right:8px;">
                                <div style="background:#f8f9fa; padding:0 8px; display:flex; align-items:center; border-left:1px solid var(--hms-border-soft); border-right:1px solid var(--hms-border-soft); color:#6c757d; font-size:13px; white-space:nowrap;">đến</div>
                                <input type="date" class="form-control" name="dateTo"
                                       value="<c:out value='${filterDateTo}'/>" style="border:none; border-radius:0; flex:1; min-width:0; padding-left:8px; padding-right:8px;">
                            </div>
                        </div>
                    </div>
                    
                    <!-- Nút hành động -->
                    <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:20px;">
                        <a href="${ctx}/admin/audit-logs" style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500; transition:all 0.2s">Xóa bộ lọc</a>
                        <button type="submit" style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer; transition:all 0.2s">Lọc dữ liệu</button>
                    </div>
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
                                               style="font-family:var(--hms-font-mono);font-size:0.75rem;font-weight:700">
                                                #<c:out value="${log.id}"/>
                                            </a>
                                        </td>
                                        <td class="d-none d-md-table-cell" style="white-space:nowrap;font-size:0.8125rem;color:var(--hms-text-muted)">
                                            <c:if test="${not empty log.createdAt}">
                                                <span>${fn:substring(log.createdAt, 8, 10)}/${fn:substring(log.createdAt, 5, 7)}/${fn:substring(log.createdAt, 0, 4)}</span>
                                                <br/>
                                                <span style="font-size:0.75rem">${fn:substring(log.createdAt, 11, 16)}</span>
                                            </c:if>
                                        </td>
                                        <td class="d-none d-md-table-cell">
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
                                            <span style="font-size:0.8125rem;font-weight:500">
                                                <c:choose>
                                                    <c:when test="${log.entityType == 'facilities'}">Cơ sở</c:when>
                                                    <c:when test="${log.entityType == 'rooms'}">Phòng</c:when>
                                                    <c:when test="${log.entityType == 'users'}">Nhân sự</c:when>
                                                    <c:when test="${log.entityType == 'notifications'}">Thông báo</c:when>
                                                    <c:when test="${log.entityType == 'invoices'}">Hóa đơn</c:when>
                                                    <c:when test="${log.entityType == 'payments'}">Thanh toán</c:when>
                                                    <c:when test="${log.entityType == 'requests'}">Yêu cầu</c:when>
                                                    <c:when test="${log.entityType == 'meter_readings'}">Số điện nước</c:when>
                                                    <c:otherwise>Hệ thống</c:otherwise>
                                                </c:choose>
                                            </span>
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
                                                <c:when test="${log.action == 'UPDATE' or log.action == 'UPDATE_STATUS' or log.action == 'UPDATE_AREA' or log.action == 'UPDATE_EMPLOYEE' or log.action == 'UPDATE_ELECTRICITY' or log.action == 'UPDATE_WATER'}">
                                                    <span class="badge-hms badge-info">
                                                        <c:choose>
                                                            <c:when test="${log.action == 'UPDATE'}">Cập nhật</c:when>
                                                            <c:when test="${log.action == 'UPDATE_STATUS'}">Đổi trạng thái</c:when>
                                                            <c:when test="${log.action == 'UPDATE_AREA'}">Cập nhật diện tích</c:when>
                                                            <c:when test="${log.action == 'UPDATE_ELECTRICITY'}">Cập nhật số điện</c:when>
                                                            <c:when test="${log.action == 'UPDATE_WATER'}">Cập nhật số nước</c:when>
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
                                        <td class="d-none d-md-table-cell">
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
