<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Người thuê - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="tenants"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
        <div>
          <h1>Người thuê</h1>
          <p>Danh sách người thuê trong cơ sở được phân công</p>
        </div>
        <a href="${ctx}/manager/tenants/create" class="quick-action-btn primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Thêm người thuê
        </a>
      </div>

      <div class="data-surface">
        <form class="filter-bar" method="get" action="${ctx}/manager/tenants">
          <input type="text" class="form-control" name="keyword"
                 placeholder="Tên / SĐT / Email..."
                 value="<c:out value='${keyword}'/>">
          <select class="form-select" name="status" style="max-width:160px">
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE"   ${selectedStatus == 'ACTIVE'   ? 'selected' : ''}>Đang thuê</option>
            <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>Ngừng thuê</option>
          </select>
          <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
          <a href="${ctx}/manager/tenants" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
        </form>

        <c:choose>
          <c:when test="${not empty page.items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>Mã NT</th>
                    <th>Họ tên</th>
                    <th>SĐT</th>
                    <th>Email</th>
                    <th>Phòng</th>
                    <th>Ngày bắt đầu thuê</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="tenant" items="${page.items}">
                    <tr data-href="${ctx}/manager/tenants/${tenant.id}">
                      <td>
                        <a href="${ctx}/manager/tenants/${tenant.id}">
                          <c:out value="${tenant.tenantCode}"/>
                        </a>
                      </td>
                      <td><strong><c:out value="${tenant.fullName}"/></strong></td>
                      <td><c:out value="${tenant.phone}"/></td>
                      <td style="font-size:0.8125rem"><c:out value="${tenant.email}"/></td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty tenant.roomCode}">
                            <a href="${ctx}/manager/rooms/${tenant.roomId}">
                              <c:out value="${tenant.roomCode}"/>
                            </a>
                          </c:when>
                          <c:otherwise><em class="text-muted">Chưa gán phòng</em></c:otherwise>
                        </c:choose>
                      </td>
                      <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                        <c:out value="${tenant.contractStartDate}"/>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${tenant.status == 'ACTIVE'}">
                            <span class="badge-hms badge-success">Đang thuê</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral">Ngừng thuê</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <a href="${ctx}/manager/tenants/${tenant.id}"
                           class="btn-mintlify-secondary text-decoration-none me-1"
                           style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                        <c:if test="${tenant.status == 'ACTIVE'}">
                          <a href="${ctx}/manager/tenants/${tenant.id}#end-rental"
                             class="text-decoration-none"
                             style="padding:4px 12px;font-size:0.8125rem;color:var(--hms-danger)">Kết thúc thuê</a>
                        </c:if>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
              <span class="text-muted" style="font-size:0.875rem">
                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> người thuê
                · Trang ${page.page} / ${page.totalPages}
              </span>
              <div class="d-flex gap-1">
                <c:if test="${page.page > 1}">
                  <a href="${ctx}/manager/tenants?page=${page.page - 1}&keyword=${keyword}&status=${selectedStatus}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                </c:if>
                <c:if test="${page.page < page.totalPages}">
                  <a href="${ctx}/manager/tenants?page=${page.page + 1}&keyword=${keyword}&status=${selectedStatus}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                </c:if>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state p-4 text-center">
              <h4>Chưa có người thuê nào</h4>
              <p class="text-muted">Thêm người thuê đầu tiên để bắt đầu quản lý cơ sở.</p>
              <a href="${ctx}/manager/tenants/create" class="quick-action-btn primary mt-2">Thêm người thuê</a>
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
