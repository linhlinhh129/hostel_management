<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Người phụ thuộc - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="dependents"/>
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
          <h1>Người phụ thuộc</h1>
          <p>Danh sách người phụ thuộc của cư dân trong cơ sở</p>
        </div>
      </div>

      <div class="data-surface">
        <form method="get" action="${ctx}/manager/dependents" id="filterForm" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
          <div class="row g-3 align-items-end">
            <div class="col-12 col-md-6">
              <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
              <input type="text" class="form-control" name="keyword"
                     placeholder="Tìm theo tên, quan hệ..."
                     value="<c:out value='${keyword}'/>">
            </div>
            <div class="col-12 col-md-6 d-flex justify-content-md-end gap-2">
              <a href="${ctx}/manager/dependents" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
              <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
            </div>
          </div>
        </form>

        <c:choose>
          <c:when test="${not empty dependents}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Họ tên</th>
                    <th class="d-none d-md-table-cell">Ngày sinh</th>
                    <th class="d-none d-md-table-cell">Giới tính</th>
                    <th>Quan hệ</th>
                    <th class="d-none d-md-table-cell">Người thuê chính</th>
                    <th class="d-none d-md-table-cell">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="dep" items="${dependents}">
                    <tr data-href="${ctx}/manager/dependents/${dep.id}">
                      <td style="font-size:0.8125rem;color:var(--hms-text-muted)">${dep.id}</td>
                      <td><strong><c:out value="${dep.fullName}"/></strong></td>
                      <td class="d-none d-md-table-cell" style="font-size:0.8125rem"><c:out value="${dep.dob}"/></td>
                      <td class="d-none d-md-table-cell">
                        <c:choose>
                          <c:when test="${dep.gender == 'MALE'}">Nam</c:when>
                          <c:when test="${dep.gender == 'FEMALE'}">Nữ</c:when>
                          <c:otherwise><c:out value="${dep.gender}"/></c:otherwise>
                        </c:choose>
                      </td>
                      <td><c:out value="${dep.relationship}"/></td>
                      <td class="d-none d-md-table-cell">
                        <a href="${ctx}/manager/tenants/${dep.tenantId}">
                          <c:out value="${dep.tenantName}"/>
                        </a>
                      </td>
                      <td class="d-none d-md-table-cell">
                        <a href="${ctx}/manager/dependents/${dep.id}"
                           class="btn-mintlify-secondary text-decoration-none"
                           style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
              <span class="text-muted" style="font-size:0.875rem">
                Tổng <strong>${dependents.size()}</strong> người phụ thuộc
              </span>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state p-4 text-center">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
              <h4>Chưa có dữ liệu</h4>
              <p class="text-muted">Không tìm thấy người phụ thuộc phù hợp.</p>
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
