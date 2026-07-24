<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - Cổng cư dân"/>
<c:set var="pageRole" value="TENANT"/>
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

      <%-- Page Header --%>
      <div class="page-header hero-sky-gradient dash-hero">
        <div class="dash-hero-inner">
          <div>
            <h1>Thông báo</h1>
            <p>Các thông báo từ Ban quản lý dành cho bạn</p>
          </div>
        </div>
      </div>

      <div class="data-surface">

        <%-- Filter bar — chỉ tìm theo từ khóa --%>
        <form class="filter-bar" method="get" action="${ctx}/tenant/notifications">
          <input type="text" class="form-control" name="keyword"
                 placeholder="Tìm theo tiêu đề..."
                 value="<c:out value='${keyword}'/>">
          <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
          <a href="${ctx}/tenant/notifications"
             class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
        </form>

        <c:choose>
          <c:when test="${not empty notifications}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th class="d-none d-md-table-cell notif-col-code">Mã TB</th>
                    <th>Tiêu đề</th>
                    <th class="d-none d-md-table-cell notif-col-date">Ngày gửi</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="notif" items="${notifications}">
                    <tr class="tr-clickable"
                        data-href="${ctx}/tenant/notifications/${notif.id}">
                      <td class="d-none d-md-table-cell notif-code">
                        <c:out value="${notif.code}"/>
                      </td>
                      <td>
                        <div class="notif-title">
                          <c:out value="${notif.title}"/>
                        </div>
                        <div class="notif-summary">
                          <c:out value="${notif.summary}"/>
                        </div>
                      </td>
                      <td class="d-none d-md-table-cell notif-date">
                        <c:out value="${notif.createdDateLabel}"/>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>

            <%-- Pagination --%>
            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
              <span class="text-muted" style="font-size:0.875rem">
                Tổng <strong>${totalNotifications}</strong> thông báo
                <c:if test="${totalPages > 1}"> · Trang ${currentPage} / ${totalPages}</c:if>
              </span>
              <div class="d-flex gap-1">
                <c:if test="${currentPage > 1}">
                  <a href="${ctx}/tenant/notifications?page=${currentPage - 1}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                </c:if>
                <c:if test="${currentPage < totalPages}">
                  <a href="${ctx}/tenant/notifications?page=${currentPage + 1}&keyword=${keyword}"
                     class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                </c:if>
              </div>
            </div>

          </c:when>
          <c:otherwise>
            <div class="empty-state p-5 text-center">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none"
                   stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
              </svg>
              <h4>Không có thông báo nào</h4>
              <p class="text-muted">
                <c:choose>
                  <c:when test="${not empty keyword}">
                    Không tìm thấy thông báo phù hợp.
                    <a href="${ctx}/tenant/notifications">Xóa bộ lọc</a>
                  </c:when>
                  <c:otherwise>
                    Bạn chưa có thông báo nào từ Ban quản lý.
                  </c:otherwise>
                </c:choose>
              </p>
            </div>
          </c:otherwise>
        </c:choose>

      </div><%-- /data-surface --%>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
