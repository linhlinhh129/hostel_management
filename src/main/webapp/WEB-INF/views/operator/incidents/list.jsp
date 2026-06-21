<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử Báo cáo Sự cố - Kỹ thuật"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="my-incidents"/>

<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    
    <main class="page-content">
      <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg); margin-bottom: 1.75rem;">
        <h1>Sự cố tôi đã báo cáo</h1>
        <p>Danh sách các sự cố bạn đã ghi nhận tại hiện trường</p>
      </div>

      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="data-surface">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3 p-3 gap-3">
            <h5 class="m-0 text-center text-md-start" style="font-weight: 600;">Lịch sử báo cáo</h5>
            <div class="d-grid d-md-block">
                <a href="${ctx}/operator/incidents/create" class="btn-mintlify-primary text-center text-decoration-none">Báo cáo sự cố mới</a>
            </div>
        </div>

        <c:choose>
          <c:when test="${not empty items}">
            <div class="table-responsive">
              <table class="table-mintlify">
                <thead>
                  <tr>
                    <th class="d-none d-md-table-cell">Mã SC</th>
                    <th class="d-none d-md-table-cell">Loại</th>
                    <th>Tiêu đề / Vị trí</th>
                    <th class="d-none d-md-table-cell">Ngày gửi</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="item" items="${items}">
                    <tr>
                      <td class="d-none d-md-table-cell" style="font-family: monospace; font-size: 0.8125rem;">
                        <c:out value="${item.code}"/>
                      </td>
                      <td class="d-none d-md-table-cell">
                        <span class="badge-hms badge-neutral"><c:out value="${item.category}"/></span>
                      </td>
                      <td style="max-width: 200px;">
                        <div style="font-weight: 500; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${item.title}"><c:out value="${item.title}"/></div>
                      </td>
                      <td class="d-none d-md-table-cell" style="font-size: 0.8125rem; color: var(--hms-text-muted);">
                        <fmt:formatDate value="${item.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${item.status == 'PENDING'}">
                            <span class="badge-hms badge-info">Chờ quản lý duyệt</span>
                          </c:when>
                          <c:when test="${item.status == 'IN_PROGRESS'}">
                            <span class="badge-hms badge-warning">Đang xử lý</span>
                          </c:when>
                          <c:when test="${item.status == 'COMPLETED' or item.status == 'RESOLVED'}">
                            <span class="badge-hms badge-success">Đã hoàn thành</span>
                          </c:when>
                          <c:when test="${item.status == 'CANCELLED' or item.status == 'REJECTED'}">
                            <span class="badge-hms badge-danger">Đã hủy</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral"><c:out value="${item.status}"/></span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <c:if test="${item.status == 'PENDING'}">
                            <a href="${ctx}/operator/incidents/edit?id=${item.requestId}" 
                               class="mintlify-btn-secondary text-decoration-none" style="padding: 4px 12px; font-size: 12px;">Sửa</a>
                        </c:if>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            
            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
              <span class="text-muted" style="font-size: 0.875rem;">
                Tổng <fmt:formatNumber value="${totalItems}" groupingUsed="true"/> sự cố · Trang ${currentPage} / ${totalPages}
              </span>
              <div class="d-flex gap-1">
                <c:if test="${currentPage > 1}">
                  <a href="${ctx}/operator/incidents/my-reports?page=${currentPage - 1}" class="btn-mintlify-secondary text-decoration-none" style="padding: 6px 14px;">Trước</a>
                </c:if>
                <c:if test="${currentPage < totalPages}">
                  <a href="${ctx}/operator/incidents/my-reports?page=${currentPage + 1}" class="btn-mintlify-secondary text-decoration-none" style="padding: 6px 14px;">Sau</a>
                </c:if>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state p-5 text-center">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom: 12px;">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
              <h4>Bạn chưa báo cáo sự cố nào</h4>
              <p class="text-muted mb-4">Các sự cố phát sinh tại hiện trường do bạn báo cáo sẽ hiển thị tại đây.</p>
              <a href="${ctx}/operator/incidents/create" class="btn-mintlify-primary text-decoration-none">Báo cáo ngay</a>
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
