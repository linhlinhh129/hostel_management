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

      <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
           style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <div>
          <h1>Người thuê</h1>
          <p>Danh sách người thuê trong cơ sở được phân công</p>
        </div>

      </div>

      <div class="data-surface">
        <form method="get" action="${ctx}/manager/tenants" id="filterForm" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
          <div class="row g-3 align-items-end">
            <div class="col-12 col-md-4">
              <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
              <input type="text" class="form-control" name="keyword"
                     placeholder="Tên / SĐT / Email..."
                     value="<c:out value='${keyword}'/>">
            </div>
            <div class="col-12 col-md-4">
              <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Trạng thái</label>
              <select class="form-select" name="status">
                <option value="">Tất cả</option>
                <option value="ACTIVE"   ${selectedStatus == 'ACTIVE'   ? 'selected' : ''}>Đang thuê</option>
                <option value="LOCKED"   ${selectedStatus == 'LOCKED'   ? 'selected' : ''}>Đã khóa</option>
                <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>Ngừng thuê</option>
              </select>
            </div>
            <div class="col-12 col-md-4 d-flex justify-content-md-end gap-2">
              <a href="${ctx}/manager/tenants" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
              <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
            </div>
          </div>
        </form>

        <c:choose>
          <c:when test="${not empty page.items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>Mã NT</th>
                    <th>Họ tên</th>
                    <th class="d-none d-md-table-cell">SĐT</th>
                    <th class="d-none d-md-table-cell">Email</th>
                    <th class="d-none d-md-table-cell">Phòng</th>
                    <th class="d-none d-md-table-cell">Ngày bắt đầu thuê</th>
                    <th>Trạng thái</th>
                    <th class="d-none d-md-table-cell">Thao tác</th>
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
                      <td class="d-none d-md-table-cell"><c:out value="${tenant.phone}"/></td>
                      <td class="d-none d-md-table-cell" style="font-size:0.8125rem"><c:out value="${tenant.email}"/></td>
                      <td class="d-none d-md-table-cell">
                        <c:choose>
                          <c:when test="${not empty tenant.roomCode}">
                            <a href="${ctx}/manager/rooms/${tenant.roomId}">
                              <c:out value="${tenant.roomCode}"/>
                            </a>
                          </c:when>
                          <c:otherwise><em class="text-muted">Chưa gán phòng</em></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="d-none d-md-table-cell" style="font-size:0.8125rem;color:var(--hms-text-muted)">
                        <c:out value="${tenant.contractStartDate}"/>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${tenant.status == 'ACTIVE'}">
                            <span class="badge-hms badge-success">Đang thuê</span>
                          </c:when>
                          <c:when test="${tenant.status == 'LOCKED'}">
                            <span class="badge-hms badge-danger">Đã khóa</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral">Ngừng thuê</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <div class="d-inline-flex gap-1 align-items-center">
                          <a href="${ctx}/manager/tenants/${tenant.id}"
                             class="btn-mintlify-secondary text-decoration-none"
                             style="padding:4px 12px;font-size:0.8125rem">Chi tiết</a>
                        </div>
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
              <p class="text-muted">Vui lòng tạo hợp đồng trước, sau đó tạo tài khoản người thuê tại trang chi tiết hợp đồng.</p>
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
