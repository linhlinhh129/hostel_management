<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Hợp đồng - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="contracts" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
              <div
                class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                <div>
                  <h1>Quản lý Hợp đồng</h1>
                  <p>Danh sách hợp đồng thuê phòng trong cơ sở được phân công</p>
                </div>
                <div class="d-flex gap-2 align-items-center mt-2 mt-md-0">
                  <a href="${ctx}/manager/contracts/create" class="btn-mintlify-primary text-decoration-none">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                      style="margin-right:6px">
                      <line x1="12" y1="5" x2="12" y2="19"></line>
                      <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                    Tạo hợp đồng
                  </a>
                </div>
              </div>

              <div class="data-surface p-3 mb-4">
                <form action="${ctx}/manager/contracts" method="get" class="row g-2 align-items-center">
                  <div class="col-md-6 col-12 position-relative">
                    <svg class="position-absolute top-50 start-0 translate-middle-y ms-3 text-muted" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                    <input type="text" name="searchName" class="form-control ps-5" placeholder="Tìm kiếm theo tên người đại diện..." value="${searchName}">
                  </div>
                  <div class="col-md-2 col-4">
                    <button type="submit" class="btn-mintlify-primary w-100">Tìm kiếm</button>
                  </div>
                  <c:if test="${not empty searchName}">
                    <div class="col-md-2 col-4">
                      <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary text-decoration-none w-100 text-center d-block">Xóa lọc</a>
                    </div>
                  </c:if>
                </form>
              </div>

              <div class="data-surface">
                <c:choose>
                  <c:when test="${not empty contracts}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã HĐ</th>
                            <th>Phòng</th>
                            <th>Người đại diện</th>
                            <th>Ngày ký</th>
                            <th>Thời hạn</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="c" items="${contracts}">
                            <tr>
                              <td>
                                <a href="${ctx}/manager/contracts/detail?id=${c.contractId}"
                                  style="font-weight:600;font-family:monospace">
                                  <c:out value="${c.code}" />
                                </a>
                              </td>
                              <td><span class="badge-hms badge-neutral">
                                  <c:out value="${c.room.code}" />
                                </span></td>
                              <td>
                                <div class="fw-bold">
                                  <c:out value="${c.tenantFullName}" />
                                </div>
                                <small class="text-muted">CCCD:
                                  <c:out value="${c.tenantIdentityNumber}" />
                                </small>
                              </td>
                              <td>
                                <c:out value="${c.signedDate}" />
                              </td>
                              <td>
                                <c:out value="${c.startDate}" /> -
                                <c:out value="${c.endDate}" />
                              </td>
                              <td>
                                <span class="badge-hms ${c.status == 'ACTIVE' ? 'success' : 'secondary'}">
                                  <c:out value="${c.status}" />
                                </span>
                              </td>
                              <td>
                                <div class="d-inline-flex gap-1 align-items-center">
                                  <a href="${ctx}/manager/contracts/detail?id=${c.contractId}"
                                    class="btn-mintlify-secondary text-decoration-none"
                                    style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                                  <c:if test="${c.status == 'INACTIVE'}">
                                    <form method="post" action="${ctx}/manager/contracts/delete?id=${c.contractId}" style="display:inline; margin:0;"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn xóa hợp đồng này không?');">
                                      <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                      <button type="submit" class="btn btn-sm btn-outline-danger" style="padding:4px 10px; font-size:0.8125rem;">Xóa</button>
                                    </form>
                                  </c:if>
                                </div>
                              </td>
                            </tr>
                          </c:forEach>
                        </tbody>
                      </table>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                      <jsp:param name="title" value="Chưa có hợp đồng nào" />
                      <jsp:param name="description" value="Tạo hợp đồng đầu tiên để bắt đầu quản lý" />
                    </jsp:include>
                  </c:otherwise>
                </c:choose>
              </div>
            </main>
          </div>
        </div>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>
