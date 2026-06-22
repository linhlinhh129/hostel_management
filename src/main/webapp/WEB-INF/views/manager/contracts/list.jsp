<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Hợp đồng - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="contracts"/>
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
          <h1>Quản lý Hợp đồng</h1>
          <p>Danh sách hợp đồng thuê phòng trong cơ sở được phân công</p>
        </div>
        <a href="${ctx}/manager/contracts/create" class="quick-action-btn primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg> Tạo hợp đồng
        </a>
      </div>

      <div class="data-surface">
        <!-- Bộ lọc -->
        <form class="filter-bar" method="get" action="${ctx}/manager/contracts">
          <select class="form-select" name="status">
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE"        ${filterStatus == 'ACTIVE'         ? 'selected' : ''}>Đang hiệu lực</option>
            <option value="EXPIRING_SOON" ${filterStatus == 'EXPIRING_SOON'  ? 'selected' : ''}>Sắp hết hạn</option>
            <option value="OVERDUE"       ${filterStatus == 'OVERDUE'        ? 'selected' : ''}>Quá hạn</option>
            <option value="TERMINATED"    ${filterStatus == 'TERMINATED'     ? 'selected' : ''}>Đã thanh lý</option>
          </select>
          <button type="submit" class="btn-mintlify-secondary">Lọc</button>
          <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
        </form>

        <!-- Bảng danh sách -->
        <c:choose>
          <c:when test="${not empty page.items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>Phòng</th>
                    <th>Cơ sở</th>
                    <th>Người thuê</th>
                    <th>Giá thuê</th>
                    <th>Tiền cọc</th>
                    <th>Ngày vào</th>
                    <th>Ngày hết hạn</th>
                    <th>Trạng thái cọc</th>
                    <th>Trạng thái HĐ</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="c" items="${page.items}">
                    <tr data-href="${ctx}/manager/contracts/${c.id}">
                      <td><strong><c:out value="${c.roomCode}"/></strong></td>
                      <td><c:out value="${c.facilityName}"/></td>
                      <td><c:out value="${c.tenantName}"/></td>
                      <td>
                        <fmt:formatNumber value="${c.rentPrice}" type="number" groupingUsed="true"/>đ
                      </td>
                      <td>
                        <fmt:formatNumber value="${c.depositAmount}" type="number" groupingUsed="true"/>đ
                      </td>
                      <td><c:out value="${c.moveInDate}"/></td>
                      <td><c:out value="${c.expiryDate}"/></td>
                      <td>
                        <span class="badge-hms
                          <c:choose>
                            <c:when test="${c.depositStatus == 'PAID'}">success</c:when>
                            <c:when test="${c.depositStatus == 'PARTIAL'}">warning</c:when>
                            <c:otherwise>danger</c:otherwise>
                          </c:choose>">
                          <c:choose>
                            <c:when test="${c.depositStatus == 'PAID'}">Đã cọc</c:when>
                            <c:when test="${c.depositStatus == 'PARTIAL'}">Cọc 1 phần</c:when>
                            <c:otherwise>Chưa cọc</c:otherwise>
                          </c:choose>
                        </span>
                      </td>
                      <td>
                        <span class="badge-hms
                          <c:choose>
                            <c:when test="${c.status == 'ACTIVE'}">success</c:when>
                            <c:when test="${c.status == 'EXPIRING_SOON'}">warning</c:when>
                            <c:when test="${c.status == 'OVERDUE'}">danger</c:when>
                            <c:otherwise>secondary</c:otherwise>
                          </c:choose>">
                          <c:choose>
                            <c:when test="${c.status == 'ACTIVE'}">Đang hiệu lực</c:when>
                            <c:when test="${c.status == 'EXPIRING_SOON'}">Sắp hết hạn</c:when>
                            <c:when test="${c.status == 'OVERDUE'}">Quá hạn</c:when>
                            <c:otherwise>Đã thanh lý</c:otherwise>
                          </c:choose>
                        </span>
                      </td>
                      <td>
                        <a href="${ctx}/manager/contracts/${c.id}">Xem</a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>

            <!-- Phân trang -->
            <c:if test="${page.totalPages > 1}">
              <nav class="pagination-bar">
                <c:if test="${page.page > 1}">
                  <a href="?page=${page.page - 1}&status=${filterStatus}">&laquo;</a>
                </c:if>
                <c:forEach begin="1" end="${page.totalPages}" var="p">
                  <a href="?page=${p}&status=${filterStatus}"
                     class="${p == page.page ? 'active' : ''}">${p}</a>
                </c:forEach>
                <c:if test="${page.page < page.totalPages}">
                  <a href="?page=${page.page + 1}&status=${filterStatus}">&raquo;</a>
                </c:if>
              </nav>
            </c:if>
          </c:when>
          <c:otherwise>
            <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
              <jsp:param name="title" value="Chưa có hợp đồng nào"/>
              <jsp:param name="description" value="Tạo hợp đồng đầu tiên để bắt đầu quản lý"/>
            </jsp:include>
          </c:otherwise>
        </c:choose>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
