<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Quản lý cơ sở - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
<c:set var="activeMenu" value="hostels"/>
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
                        <h1>Quản lý cơ sở</h1>
                        <p>Danh sách cơ sở trọ trong hệ thống</p>
                    </div>
                    <a href="${ctx}/admin/facilities/create" class="quick-action-btn primary" style="position:relative;z-index:1">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        Thêm cơ sở
                    </a>
                </div>
            </div>

            <div class="data-surface">
                <!-- Bộ lọc -->
                <form class="filter-bar" method="get" action="${ctx}/admin/facilities">
                    <input type="text" class="form-control" name="keyword" placeholder="Mã, tên, địa chỉ..."
                           value="<c:out value='${keyword}'/>">
                    <select class="form-select" name="status" style="max-width:160px">
                        <option value="">Tất cả trạng thái</option>
                        <option value="ACTIVE"   ${selectedStatus == 'ACTIVE'   ? 'selected' : ''}>Hoạt động</option>
                        <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>Vô hiệu</option>
                    </select>
                    <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
                    <a href="${ctx}/admin/facilities" class="btn-mintlify-secondary text-decoration-none">Xóa</a>
                </form>

                <c:choose>
                    <c:when test="${not empty page.items}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>Mã cơ sở</th>
                                    <th>Tên cơ sở</th>
                                    <th class="d-none d-md-table-cell">Địa chỉ</th>
                                    <th class="d-none d-md-table-cell">Số tầng</th>
                                    <th class="d-none d-md-table-cell">Phòng/tầng</th>
                                    <th class="d-none d-md-table-cell">Tổng phòng</th>
                                    <th>Trạng thái</th>
                                    <th class="d-none d-md-table-cell">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="f" items="${page.items}">
                                    <tr data-href="${ctx}/admin/facilities/${f.id}">
                                        <td><a href="${ctx}/admin/facilities/${f.id}"><c:out value="${f.code}"/></a></td>
                                        <td><c:out value="${f.name}"/></td>
                                        <td class="d-none d-md-table-cell"><c:out value="${f.address}"/></td>
                                        <td class="d-none d-md-table-cell"><fmt:formatNumber value="${f.floorCount}"/></td>
                                        <td class="d-none d-md-table-cell"><fmt:formatNumber value="${f.roomsPerFloor}"/></td>
                                        <td class="d-none d-md-table-cell"><fmt:formatNumber value="${f.totalRooms}" groupingUsed="true"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${f.status == 'ACTIVE'}">
                                                    <span class="badge-hms badge-success">Hoạt động</span>
                                                </c:when>
                                                <c:when test="${f.status == 'DRAFT'}">
                                                    <span class="badge-hms badge-warning">Chưa kích hoạt</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral">Vô hiệu</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="d-none d-md-table-cell">
                                            <a href="${ctx}/admin/facilities/${f.id}" class="me-2">Chi tiết</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <!-- Phân trang -->
                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> cơ sở
                                · Trang ${page.page} / ${page.totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${page.page > 1}">
                                    <a href="${ctx}/admin/facilities?page=${page.page - 1}&keyword=${keyword}&status=${selectedStatus}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${page.page < page.totalPages}">
                                    <a href="${ctx}/admin/facilities?page=${page.page + 1}&keyword=${keyword}&status=${selectedStatus}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-4 text-center">
                            <h4>Chưa có cơ sở nào</h4>
                            <p class="text-muted">Bắt đầu bằng cách thêm cơ sở đầu tiên vào hệ thống.</p>
                            <a href="${ctx}/admin/facilities/create" class="quick-action-btn primary mt-2">Thêm cơ sở</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
