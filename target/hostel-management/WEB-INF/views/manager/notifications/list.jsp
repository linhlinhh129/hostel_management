<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="notifications"/>
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
          <h1>Thông báo</h1>
          <p>Quản lý thông báo trong phạm vi cơ sở được phân công</p>
        </div>
        <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Tạo thông báo
        </a>
      </div>

      <div class="data-surface">

        <%-- Info box phạm vi --%>
        <div style="background:var(--hms-accent-bg);border:1px solid var(--hms-border-accent);
                    border-radius:var(--hms-radius);padding:0.75rem 1rem;
                    font-size:0.8125rem;color:var(--hms-ink);margin-bottom:1rem">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
               stroke="var(--hms-accent-deep)" stroke-width="2"
               style="margin-right:6px;vertical-align:-2px">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          Manager chỉ gửi thông báo trong phạm vi <strong>cơ sở được phân công</strong>.
          Không thể gửi thông báo toàn hệ thống.
        </div>

        <form class="filter-bar" method="get" action="${ctx}/manager/notifications">
          <input type="text" class="form-control" name="keyword"
                 placeholder="Tiêu đề thông báo..."
                 value="<c:out value='${keyword}'/>">
          <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
          <a href="${ctx}/manager/notifications" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
        </form>

        <c:choose>
          <c:when test="${not empty page.items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Tiêu đề</th>
                    <th>Đối tượng</th>
                    <th>Người tạo</th>
                    <th>Trạng thái</th>
                    <th>Ngày tạo</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="notif" items="${page.items}">
                    <tr>
                      <td>
                        <a href="${ctx}/manager/notifications/${notif.id}">
                          <c:out value="${notif.code}"/>
                        </a>
                      </td>
                      <td style="max-width:280px"><c:out value="${notif.title}"/></td>
                      <td>
                        <c:choose>
                          <c:when test="${notif.recipientType == 'FACILITY'}">
                            <span class="badge-hms badge-info">Cơ sở</span>
                          </c:when>
                          <c:when test="${notif.recipientType == 'ROOM'}">
                            <span class="badge-hms badge-neutral">Phòng</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral"><c:out value="${notif.recipientType}"/></span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td><c:out value="${notif.createdByName}"/></td>
                      <td>
                        <c:choose>
                          <c:when test="${notif.status == 'SENT'}">
                            <span class="badge-hms badge-success">Đã gửi</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-warning">Nháp</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                        <c:out value="${notif.createdAt}"/>
                      </td>
                      <td>
                        <a href="${ctx}/manager/notifications/${notif.id}"
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
                Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> thông báo
                · Trang ${page.page} / ${page.totalPages}
              </span>
              <div class="d-flex gap-1">
                <c:if test="${page.page > 1}">
                  <a href="${ctx}/manager/notifications?page=${page.page - 1}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                </c:if>
                <c:if test="${page.page < page.totalPages}">
                  <a href="${ctx}/manager/notifications?page=${page.page + 1}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                </c:if>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state p-4 text-center">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
              </svg>
              <h4>Chưa có thông báo nào</h4>
              <p class="text-muted">Tạo thông báo đầu tiên để gửi đến cư dân.</p>
              <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary mt-2">Tạo thông báo</a>
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
