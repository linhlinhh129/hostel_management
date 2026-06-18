<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Yêu cầu hỗ trợ - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="tickets"/>
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
        <h1>Yêu cầu hỗ trợ</h1>
        <p>Tiếp nhận và xử lý yêu cầu từ người thuê</p>
      </div>

      <div class="data-surface">
        <form class="filter-bar" method="get" action="${ctx}/manager/tickets">
          <select class="form-select" name="status">
            <option value="">Tất cả trạng thái</option>
            <option value="NEW"         ${filterStatus == 'NEW'         ? 'selected' : ''}>Mới</option>
            <option value="RECEIVED"    ${filterStatus == 'RECEIVED'    ? 'selected' : ''}>Tiếp nhận</option>
            <option value="ASSIGNED"    ${filterStatus == 'ASSIGNED'    ? 'selected' : ''}>Đã phân công</option>
            <option value="IN_PROGRESS" ${filterStatus == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
            <option value="RESOLVED"    ${filterStatus == 'RESOLVED'    ? 'selected' : ''}>Hoàn thành</option>
            <option value="REJECTED"    ${filterStatus == 'REJECTED'    ? 'selected' : ''}>Từ chối</option>
          </select>
          <input type="text" class="form-control" name="keyword"
                 placeholder="Tiêu đề / mã yêu cầu..."
                 value="<c:out value='${keyword}'/>">
          <button type="submit" class="btn-mintlify-secondary">Lọc</button>
          <a href="${ctx}/manager/tickets" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
        </form>

        <c:choose>
          <c:when test="${not empty page.items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Loại</th>
                    <th>Tiêu đề</th>
                    <th>Người gửi</th>
                    <th>Phòng</th>
                    <th>Cơ sở</th>
                    <th>Ngày gửi</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="ticket" items="${page.items}">
                    <tr>
                      <td>
                        <a href="${ctx}/manager/tickets/${ticket.id}" style="font-family:monospace;font-size:0.8125rem">
                          <c:out value="${ticket.code}"/>
                        </a>
                      </td>
                      <td>
                        <span class="badge-hms badge-neutral"><c:out value="${ticket.category}"/></span>
                      </td>
                      <td style="max-width:220px">
                        <c:out value="${ticket.title}"/>
                      </td>
                      <td><c:out value="${ticket.senderName}"/></td>
                      <td>
                        <c:if test="${not empty ticket.roomCode}">
                          <a href="${ctx}/manager/rooms/${ticket.roomId}">
                            <c:out value="${ticket.roomCode}"/>
                          </a>
                        </c:if>
                      </td>
                      <td><c:out value="${ticket.facilityName}"/></td>
                      <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                        <c:out value="${ticket.createdAt}"/>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${ticket.status == 'NEW'}">
                            <span class="badge-hms badge-info">Mới</span>
                          </c:when>
                          <c:when test="${ticket.status == 'RECEIVED'}">
                            <span class="badge-hms badge-accent">Tiếp nhận</span>
                          </c:when>
                          <c:when test="${ticket.status == 'ASSIGNED'}">
                            <span class="badge-hms badge-warning">Đã phân công</span>
                          </c:when>
                          <c:when test="${ticket.status == 'IN_PROGRESS'}">
                            <span class="badge-hms badge-warning">Đang xử lý</span>
                          </c:when>
                          <c:when test="${ticket.status == 'RESOLVED'}">
                            <span class="badge-hms badge-success">Hoàn thành</span>
                          </c:when>
                          <c:when test="${ticket.status == 'REJECTED'}">
                            <span class="badge-hms badge-danger">Từ chối</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral"><c:out value="${ticket.status}"/></span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <a href="${ctx}/manager/tickets/${ticket.id}"
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
                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> yêu cầu
                · Trang ${page.page} / ${page.totalPages}
              </span>
              <div class="d-flex gap-1">
                <c:if test="${page.page > 1}">
                  <a href="${ctx}/manager/tickets?page=${page.page - 1}&status=${filterStatus}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                </c:if>
                <c:if test="${page.page < page.totalPages}">
                  <a href="${ctx}/manager/tickets?page=${page.page + 1}&status=${filterStatus}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                </c:if>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state p-4 text-center">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
              </svg>
              <h4>Không có yêu cầu nào</h4>
              <p class="text-muted">Chưa có yêu cầu hỗ trợ nào trong cơ sở.</p>
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
