<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Quản lý nhân sự - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
<c:set var="activeMenu" value="personnel"/>
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
                        <h1>Quản lý nhân sự</h1>
                        <p>Danh sách nhân sự trong hệ thống</p>
                    </div>
                    <a href="${ctx}/admin/personnel/create" class="quick-action-btn primary" style="position:relative;z-index:1">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        Thêm nhân sự
                    </a>
                </div>
            </div>

            <div class="data-surface">
                <form class="filter-bar" method="get" action="${ctx}/admin/personnel">
                    <input type="text" class="form-control" name="keyword" placeholder="Họ tên, email, SĐT..."
                            value="<c:out value='${keyword}'/>">
                    <select class="form-select" name="role" style="max-width:180px">
                        <option value="">Tất cả vai trò</option>
                        <option value="MANAGER"      ${selectedRole == 'MANAGER'      ? 'selected' : ''}>Ban Quản lý</option>
                        <option value="OPERATOR"     ${selectedRole == 'OPERATOR'     ? 'selected' : ''}>Nhân viên vận hành</option>
                    </select>
                    <select class="form-select" name="status" style="max-width:160px">
                        <option value="">Tất cả trạng thái</option>
                        <option value="ACTIVE"   ${selectedStatus == 'ACTIVE'   ? 'selected' : ''}>Hoạt động</option>
                        <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>Ngừng hoạt động</option>
                    </select>
                    <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
                    <a href="${ctx}/admin/personnel" class="btn-mintlify-secondary text-decoration-none">Xóa</a>
                </form>

                <c:choose>
                    <c:when test="${not empty page.items}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>SĐT</th>
                                    <th>Vai trò</th>
                                    <th>Cơ sở phụ trách</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="emp" items="${page.items}">
                                    <tr data-href="${ctx}/admin/personnel/${emp.id}">
                                        <td><a href="${ctx}/admin/personnel/${emp.id}">#<c:out value="${emp.id}"/></a></td>
                                        <td><c:out value="${emp.fullName}"/></td>
                                        <td><c:out value="${emp.email}"/></td>
                                        <td><c:out value="${emp.phone}"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${emp.role == 'MANAGER'}"><span class="badge-hms badge-info">Ban Quản lý</span></c:when>
                                                <c:when test="${emp.role == 'OPERATOR'}"><span class="badge-hms badge-neutral">Nhân viên vận hành</span></c:when>
                                                <c:otherwise><c:out value="${emp.role}"/></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty emp.facilityNames}">
                                                    <c:forEach var="fname" items="${emp.facilityNames}" varStatus="st">
                                                        <c:out value="${fname}"/><c:if test="${!st.last}">, </c:if>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${emp.status == 'ACTIVE'}"><span class="badge-hms badge-success">Hoạt động</span></c:when>
                                                <c:otherwise><span class="badge-hms badge-danger">Ngừng hoạt động</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${ctx}/admin/personnel/${emp.id}" class="me-2">Chi tiết</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> nhân sự
                                · Trang ${page.page} / ${page.totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${page.page > 1}">
                                    <a href="${ctx}/admin/personnel?page=${page.page - 1}&keyword=${keyword}&role=${selectedRole}&status=${selectedStatus}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${page.page < page.totalPages}">
                                    <a href="${ctx}/admin/personnel?page=${page.page + 1}&keyword=${keyword}&role=${selectedRole}&status=${selectedStatus}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-4 text-center">
                            <h4>Chưa có nhân sự nào</h4>
                            <p class="text-muted">Thêm nhân sự đầu tiên vào hệ thống.</p>
                            <a href="${ctx}/admin/personnel/create" class="quick-action-btn primary mt-2">Thêm nhân sự</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
