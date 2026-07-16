<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Yêu cầu hỗ trợ - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="tickets" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
              <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

              <div class="page-header hero-sky-gradient"
                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Yêu cầu hỗ trợ</h1>
                <p>Tiếp nhận và xử lý yêu cầu từ người thuê</p>
              </div>

              <div class="data-surface">
                <ul class="nav nav-tabs mb-3" id="ticketFlowTabs" role="tablist">
                  <li class="nav-item" role="presentation">
                    <a class="nav-link ${filterType == 'TENANT' ? 'active' : ''}"
                      href="${ctx}/manager/tickets?type=TENANT&status=${filterStatus}&keyword=${keyword}"
                      style="font-weight: 600; color: ${filterType == 'TENANT' ? 'var(--hms-accent-deep)' : 'var(--hms-text-muted)'}">
                      Yêu cầu cư dân
                    </a>
                  </li>
                  <li class="nav-item" role="presentation">
                    <a class="nav-link ${filterType == 'OPERATOR' ? 'active' : ''}"
                      href="${ctx}/manager/tickets?type=OPERATOR&status=${filterStatus}&keyword=${keyword}"
                      style="font-weight: 600; color: ${filterType == 'OPERATOR' ? 'var(--hms-accent-deep)' : 'var(--hms-text-muted)'}">
                      Báo cáo sự cố của trọ
                    </a>
                  </li>
                </ul>

                <form method="get" action="${ctx}/manager/tickets" id="filterForm"
                  style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                  <input type="hidden" name="type" value="${filterType}" />
                  <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                    <div style="flex:2; min-width:200px;">
                      <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Tìm kiếm</label>
                      <input type="text" class="form-control" name="keyword" placeholder="Tiêu đề / mã yêu cầu..."
                        value="<c:out value='${keyword}'/>" style="width:100%">
                    </div>
                    <div style="flex:1; min-width:150px;">
                      <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Trạng thái</label>
                      <select class="form-select" name="status" style="width:100%">
                        <option value="">Tất cả</option>
                        <option value="PENDING" ${filterStatus=='PENDING' ? 'selected' : '' }>Mới</option>
                        <option value="RECEIVED" ${filterStatus=='RECEIVED' ? 'selected' : '' }>Đã tiếp nhận</option>
                        <c:if test="${filterType == 'TENANT'}">
                          <option value="IN_PROGRESS" ${filterStatus=='IN_PROGRESS' ? 'selected' : '' }>Đang xử lý</option>
                        </c:if>
                        <option value="DONE" ${filterStatus=='DONE' ? 'selected' : '' }>Hoàn thành</option>
                        <option value="REJECTED" ${filterStatus=='REJECTED' ? 'selected' : '' }>Từ chối</option>
                      </select>
                    </div>
                  </div>
                  <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                    <a href="${ctx}/manager/tickets?type=${filterType}"
                       style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                    <button type="submit"
                            style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                  </div>
                </form>

                <c:choose>
                  <c:when test="${not empty page.items}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã</th>
                            <th class="d-none d-md-table-cell">Loại</th>
                            <th>Tiêu đề</th>
                            <th class="d-none d-md-table-cell">Người gửi</th>
                            <th class="d-none d-md-table-cell">Phòng</th>
                            <th class="d-none d-md-table-cell">Cơ sở</th>
                            <th class="d-none d-md-table-cell">Ngày gửi</th>
                            <th>Trạng thái</th>
                            <th class="d-none d-md-table-cell">Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="ticket" items="${page.items}">
                            <tr data-href="${ctx}/manager/tickets/${ticket.id}">
                              <td>
                                <a href="${ctx}/manager/tickets/${ticket.id}" class="code-badge">
                                  <c:out value="${ticket.code}" />
                                </a>
                              </td>
                              <td class="d-none d-md-table-cell">
                                <span class="badge-hms badge-neutral">
                                  <c:choose>
                                    <c:when test="${ticket.category == 'ELECTRIC'}">Điện</c:when>
                                    <c:when test="${ticket.category == 'WATER'}">Nước</c:when>
                                    <c:when test="${ticket.category == 'INTERNET'}">Internet</c:when>
                                    <c:when test="${ticket.category == 'INFRASTRUCTURE'}">Cơ sở vật chất</c:when>
                                    <c:when test="${ticket.category == 'MAINTENANCE'}">Sửa chữa / Bảo trì</c:when>
                                    <c:when test="${ticket.category == 'CLEANING'}">Vệ sinh</c:when>
                                    <c:when test="${ticket.category == 'COMPLAINT'}">Khiếu nại / Phản ánh</c:when>
                                    <c:when test="${ticket.category == 'OTHER'}">Khác</c:when>
                                    <c:otherwise>
                                      <c:out value="${ticket.category}" />
                                    </c:otherwise>
                                  </c:choose>
                                </span>
                              </td>
                              <td style="max-width:220px">
                                <c:out value="${ticket.title}" />
                              </td>
                              <td class="d-none d-md-table-cell">
                                <c:out value="${ticket.senderName}" />
                              </td>
                              <td class="d-none d-md-table-cell">
                                <c:if test="${not empty ticket.roomCode}">
                                  <a href="${ctx}/manager/rooms/${ticket.roomId}">
                                    <c:out value="${ticket.roomCode}" />
                                  </a>
                                </c:if>
                              </td>
                              <td class="d-none d-md-table-cell">
                                <c:out value="${ticket.facilityName}" />
                              </td>
                              <td class="d-none d-md-table-cell"
                                style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                <c:out value="${ticket.createdAt}" />
                              </td>
                              <td>
                                <c:choose>
                                  <c:when test="${ticket.status == 'PENDING'}">
                                    <span class="badge-hms badge-info">Mới</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'RECEIVED' or (ticket.senderRole == 'OPERATOR' and (ticket.status == 'ASSIGNED' or ticket.status == 'IN_PROGRESS'))}">
                                    <span class="badge-hms badge-warning">Đã tiếp nhận</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'ASSIGNED'}">
                                    <span class="badge-hms badge-warning">Đã phân công</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'IN_PROGRESS'}">
                                    <span class="badge-hms badge-warning">Đang xử lý</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'DONE'}">
                                    <span class="badge-hms badge-success">Hoàn thành</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'REJECTED'}">
                                    <span class="badge-hms badge-danger">Từ chối</span>
                                  </c:when>
                                  <c:when test="${ticket.status == 'CANCELLED'}">
                                    <span class="badge-hms badge-neutral">Đã hủy</span>
                                  </c:when>
                                  <c:otherwise>
                                    <span class="badge-hms badge-neutral">
                                      <c:out value="${ticket.status}" />
                                    </span>
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
                        Tổng
                        <fmt:formatNumber value="${page.total}" groupingUsed="true" /> yêu cầu
                        · Trang ${page.page} / ${page.totalPages}
                      </span>
                      <div class="d-flex gap-1">
                        <c:if test="${page.page > 1}">
                          <a href="${ctx}/manager/tickets?page=${page.page - 1}&status=${filterStatus}&keyword=${keyword}&type=${filterType}"
                            class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                        </c:if>
                        <c:if test="${page.page < page.totalPages}">
                          <a href="${ctx}/manager/tickets?page=${page.page + 1}&status=${filterStatus}&keyword=${keyword}&type=${filterType}"
                            class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                        </c:if>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="empty-state p-4 text-center">
                      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                        stroke-width="1.5" style="margin-bottom:12px">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                        <polyline points="14 2 14 8 20 8" />
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
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>