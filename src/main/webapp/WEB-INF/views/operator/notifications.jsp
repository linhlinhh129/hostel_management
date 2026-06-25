<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Thông báo hệ thống" />
      <c:set var="pageRole" value="OPERATOR" />
      <c:set var="activeMenu" value="notifications" />

      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

            <main class="page-content">
              <div class="page-header hero-sky-gradient"
                style="border-radius: var(--hms-radius-lg); margin-bottom: 1.75rem;">
                <h1>Thông báo hệ thống</h1>
                <p>Thông báo từ Admin quản trị hệ thống</p>
              </div>

              <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

              <div class="data-surface">
                <div
                  class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3 p-3 gap-3">
                  <h5 class="m-0 text-center text-md-start" style="font-weight: 600;">Danh sách thông báo</h5>
                </div>

                <c:choose>
                  <c:when test="${not empty notifications}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th class="d-none d-md-table-cell" style="width: 15%">Mã TB</th>
                            <th style="width: 15%">Phạm vi</th>
                            <th style="width: 50%">Tiêu đề</th>
                            <th style="width: 15%">Ngày gửi</th>
                            <th style="width: 5%"></th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="item" items="${notifications}">
                            <tr>
                              <td class="d-none d-md-table-cell" style="font-family: monospace; font-size: 0.8125rem;">
                                <c:out value="${item.code}" />
                              </td>
                              <td>
                                <c:choose>
                                  <c:when test="${item.targetType == 'ALL'}">
                                    <span class="badge-hms badge-danger">Toàn hệ thống</span>
                                  </c:when>
                                  <c:otherwise>
                                    <span class="badge-hms badge-info">Khu trọ</span>
                                  </c:otherwise>
                                </c:choose>
                              </td>
                              <td>
                                <div style="font-weight: 600; margin-bottom: 4px;">
                                  <c:out value="${item.title}" />
                                </div>
                                <!-- Nội dung chi tiết ẩn mặc định (sẽ dùng collapse hoặc modal) -->
                                <div id="noti-content-${item.id}" class="d-none mt-2 text-muted"
                                  style="font-size: 0.9rem; line-height: 1.5; white-space: pre-wrap; padding: 12px; background: var(--hms-bg-subtle); border-radius: 8px;">
                                  <c:out value="${item.content}" />
                                </div>
                              </td>
                              <td style="font-size: 0.8125rem; color: var(--hms-text-muted);">
                                <fmt:formatDate value="${item.sentAtAsDate}" pattern="dd/MM/yyyy HH:mm" />
                              </td>
                              <td class="text-end">
                                <button type="button" class="btn btn-sm btn-outline-secondary toggle-content-btn"
                                  data-target="noti-content-${item.id}"
                                  style="border-radius: 8px; padding: 4px 8px; font-size: 12px;">
                                  Chi tiết
                                </button>
                              </td>
                            </tr>
                          </c:forEach>
                        </tbody>
                      </table>
                    </div>

                    <div
                      class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
                      <span class="text-muted" style="font-size: 0.875rem;">
                        Trang ${currentPage} / ${totalPages}
                      </span>
                      <div class="d-flex gap-1">
                        <c:if test="${currentPage > 1}">
                          <a href="${ctx}/operator/notifications?page=${currentPage - 1}"
                            class="btn-mintlify-secondary text-decoration-none" style="padding: 6px 14px;">Trước</a>
                        </c:if>
                        <c:if test="${currentPage < totalPages}">
                          <a href="${ctx}/operator/notifications?page=${currentPage + 1}"
                            class="btn-mintlify-secondary text-decoration-none" style="padding: 6px 14px;">Sau</a>
                        </c:if>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="empty-state p-5 text-center">
                      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                        stroke-width="1.5" style="margin-bottom: 12px;">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                      </svg>
                      <h4>Chưa có thông báo nào</h4>
                      <p class="text-muted mb-4">Bạn hiện chưa nhận được thông báo hệ thống nào.</p>
                    </div>
                  </c:otherwise>
                </c:choose>
              </div>

            </main>
          </div>
        </div>
        <script>
          document.addEventListener('DOMContentLoaded', function () {
            const buttons = document.querySelectorAll('.toggle-content-btn');
            buttons.forEach(btn => {
              btn.addEventListener('click', function () {
                const targetId = this.getAttribute('data-target');
                const contentDiv = document.getElementById(targetId);
                if (contentDiv.classList.contains('d-none')) {
                  contentDiv.classList.remove('d-none');
                  this.textContent = 'Thu gọn';
                } else {
                  contentDiv.classList.add('d-none');
                  this.textContent = 'Chi tiết';
                }
              });
            });
          });
        </script>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>