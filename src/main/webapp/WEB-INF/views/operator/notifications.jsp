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
                    <div class="d-flex flex-column gap-3 mb-4">
                      <c:forEach var="item" items="${notifications}">
                        <div class="p-4" style="border: 1px solid var(--hms-border, #e2e8f0); border-radius: 12px; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,0.02);">
                          <div class="d-flex justify-content-between align-items-start gap-3">
                            <div class="flex-grow-1">
                                <h6 class="m-0" style="font-weight: 600; font-size: 16px; color: #1e293b;"><c:out value="${item.title}" /></h6>
                                <div class="text-muted mt-1" style="font-size: 12px;">
                                    <fmt:formatDate value="${item.sentAtAsDate}" pattern="dd/MM/yyyy HH:mm" />
                                </div>
                            </div>
                            <button type="button" class="btn btn-sm toggle-content-btn"
                                  data-target="noti-content-${item.id}"
                                  style="border-radius: 8px; padding: 6px 16px; font-size: 13px; font-weight: 500; white-space: nowrap; border: 1px solid #cbd5e1; color: #475569; background: #fff; transition: all 0.2s;" onmouseover="this.style.background='#f8fafc'" onmouseout="this.style.background='#fff'">
                                  Chi tiết
                            </button>
                          </div>
                          
                          <div id="noti-content-${item.id}" class="d-none mt-3 pt-3" style="border-top: 1px solid #e2e8f0;">
                              <div style="font-size: 14.5px; line-height: 1.6; color: #334155; white-space: pre-wrap; font-family: 'Inter', sans-serif;"><c:out value="${item.content}" /></div>
                          </div>
                        </div>
                      </c:forEach>
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