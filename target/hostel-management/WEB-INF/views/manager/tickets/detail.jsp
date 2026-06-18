<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết yêu cầu - BQL"/>
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

      <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
        <div>
          <h1><c:out value="${ticket.title}"/></h1>
          <div class="d-flex align-items-center gap-2 mt-1">
            <span style="font-size:0.875rem;color:var(--hms-text-muted)">
              Mã: <strong><c:out value="${ticket.code}"/></strong>
            </span>
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
            </c:choose>
          </div>
        </div>
        <a href="${ctx}/manager/tickets" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
      </div>

      <div class="row g-3">

        <%-- Cột chính: nội dung + lịch sử --%>
        <div class="col-lg-8">

          <%-- Nội dung yêu cầu --%>
          <div class="widget-surface mb-3">
            <div class="widget-surface-header">
              <h3>Nội dung yêu cầu</h3>
              <span class="badge-hms badge-neutral"><c:out value="${ticket.category}"/></span>
            </div>
            <div class="widget-surface-body">
              <div style="white-space:pre-line;font-size:0.9375rem;line-height:1.7;color:var(--hms-ink)">
                <c:out value="${ticket.content}"/>
              </div>
            </div>
          </div>

          <%-- Lịch sử xử lý --%>
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Lịch sử xử lý</h3></div>
            <div class="widget-surface-body">
              <c:choose>
                <c:when test="${not empty ticket.history}">
                  <div class="timeline" style="position:relative;padding-left:28px">
                    <c:forEach var="h" items="${ticket.history}">
                      <div style="position:relative;padding-bottom:20px">
                        <div style="position:absolute;left:-28px;top:3px;width:12px;height:12px;border-radius:50%;background:var(--hms-accent-deep);border:2px solid white;box-shadow:0 0 0 2px var(--hms-accent-deep)"></div>
                        <div style="background:var(--hms-surface-2);border-radius:var(--hms-radius);padding:10px 14px">
                          <div class="d-flex justify-content-between align-items-start gap-2">
                            <strong style="font-size:0.875rem"><c:out value="${h.action}"/></strong>
                            <span style="font-size:0.75rem;color:var(--hms-text-muted);white-space:nowrap">
                              <c:out value="${h.performedAt}"/>
                            </span>
                          </div>
                          <div style="font-size:0.8125rem;color:var(--hms-text-muted);margin-top:2px">
                            Bởi <strong><c:out value="${h.performedBy}"/></strong>
                          </div>
                          <c:if test="${not empty h.note}">
                            <div style="font-size:0.8125rem;margin-top:6px;color:var(--hms-ink);border-top:1px solid var(--hms-border);padding-top:6px">
                              <c:out value="${h.note}"/>
                            </div>
                          </c:if>
                        </div>
                      </div>
                    </c:forEach>
                  </div>
                </c:when>
                <c:otherwise>
                  <p class="text-muted" style="font-size:0.875rem;margin:0">Chưa có lịch sử xử lý.</p>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

        </div><%-- end col-lg-8 --%>

        <%-- Cột phụ: thông tin + hành động --%>
        <div class="col-lg-4">

          <%-- Thông tin --%>
          <div class="widget-surface mb-3">
            <div class="widget-surface-header"><h3>Thông tin</h3></div>
            <div class="widget-surface-body p-0">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted);white-space:nowrap">Người gửi</td>
                  <td style="padding:9px 14px">
                    <a href="${ctx}/manager/tenants/${ticket.senderId}">
                      <c:out value="${ticket.senderName}"/>
                    </a>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">SĐT</td>
                  <td style="padding:9px 14px"><c:out value="${ticket.senderPhone}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">Phòng</td>
                  <td style="padding:9px 14px">
                    <c:if test="${not empty ticket.roomId}">
                      <a href="${ctx}/manager/rooms/${ticket.roomId}">
                        <c:out value="${ticket.roomCode}"/>
                      </a>
                    </c:if>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">Cơ sở</td>
                  <td style="padding:9px 14px"><c:out value="${ticket.facilityName}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">Loại</td>
                  <td style="padding:9px 14px"><c:out value="${ticket.category}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">Ngày gửi</td>
                  <td style="padding:9px 14px;font-size:0.8125rem"><c:out value="${ticket.createdAt}"/></td>
                </tr>
                <tr>
                  <td style="padding:9px 14px;color:var(--hms-text-muted)">Nhân sự phân công</td>
                  <td style="padding:9px 14px">
                    <c:choose>
                      <c:when test="${not empty ticket.assignedOperatorName}">
                        <c:out value="${ticket.assignedOperatorName}"/>
                      </c:when>
                      <c:otherwise><em class="text-muted">Chưa phân công</em></c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </table>
            </div>
          </div>

          <%-- Hành động theo status --%>
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Hành động</h3></div>
            <div class="widget-surface-body">

              <c:choose>

                <%-- NEW: Tiếp nhận --%>
                <c:when test="${ticket.status == 'NEW'}">
                  <p class="text-muted" style="font-size:0.8125rem;margin-bottom:12px">
                    Tiếp nhận yêu cầu để bắt đầu xử lý.
                  </p>
                  <form method="post" action="${ctx}/manager/tickets/${ticket.id}/receive">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <button type="submit" class="quick-action-btn primary w-100"
                            onclick="return confirm('Tiếp nhận yêu cầu này?')">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px">
                        <polyline points="20 6 9 17 4 12"/>
                      </svg>
                      Tiếp nhận yêu cầu
                    </button>
                  </form>
                </c:when>

                <%-- RECEIVED: Phân công hoặc Từ chối --%>
                <c:when test="${ticket.status == 'RECEIVED'}">
                  <%-- Phân công --%>
                  <form method="post" action="${ctx}/manager/tickets/${ticket.id}/assign" class="mb-3">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <div class="mb-2">
                      <label for="operatorId" class="form-label" style="font-size:0.8125rem;font-weight:600">Phân công cho nhân sự</label>
                      <select class="form-select form-select-sm" id="operatorId" name="operatorId" required>
                        <option value="">-- Chọn nhân sự --</option>
                        <c:forEach var="op" items="${operators}">
                          <option value="${op.id}"><c:out value="${op.fullName}"/></option>
                        </c:forEach>
                      </select>
                    </div>
                    <button type="submit" class="quick-action-btn primary w-100">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <line x1="19" y1="8" x2="19" y2="14"/>
                        <line x1="22" y1="11" x2="16" y2="11"/>
                      </svg>
                      Phân công
                    </button>
                  </form>

                  <hr style="border-color:var(--hms-border)">

                  <%-- Từ chối --%>
                  <form method="post" action="${ctx}/manager/tickets/${ticket.id}/reject"
                        onsubmit="return confirm('Xác nhận từ chối yêu cầu này?')">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <div class="mb-2">
                      <label for="rejectReason" class="form-label" style="font-size:0.8125rem;font-weight:600">Lý do từ chối</label>
                      <textarea class="form-control form-control-sm" id="rejectReason" name="reason"
                                rows="3" maxlength="500"
                                placeholder="Nhập lý do từ chối..."></textarea>
                    </div>
                    <button type="submit" class="btn btn-outline-danger btn-sm w-100">
                      Từ chối yêu cầu
                    </button>
                  </form>
                </c:when>

                <%-- ASSIGNED / IN_PROGRESS --%>
                <c:when test="${ticket.status == 'ASSIGNED' or ticket.status == 'IN_PROGRESS'}">
                  <div style="background:var(--hms-accent-bg);border:1px solid var(--hms-border-accent);
                              border-radius:var(--hms-radius);padding:0.75rem 1rem;text-align:center">
                    <span class="badge-hms badge-info" style="display:inline-block;margin-bottom:6px">
                      <c:choose>
                        <c:when test="${ticket.status == 'ASSIGNED'}">Đã phân công</c:when>
                        <c:otherwise>Đang xử lý</c:otherwise>
                      </c:choose>
                    </span>
                    <p style="font-size:0.8125rem;color:var(--hms-text-muted);margin:0">
                      Yêu cầu đang được xử lý bởi nhân sự.
                    </p>
                  </div>
                </c:when>

                <%-- RESOLVED --%>
                <c:when test="${ticket.status == 'RESOLVED'}">
                  <div style="background:#f0fdf4;border:1px solid #bbf7d0;
                              border-radius:var(--hms-radius);padding:0.75rem 1rem;text-align:center">
                    <span class="badge-hms badge-success" style="display:inline-block;margin-bottom:6px">Đã hoàn thành</span>
                    <p style="font-size:0.8125rem;color:#166534;margin:0">
                      Yêu cầu đã được xử lý thành công.
                    </p>
                  </div>
                </c:when>

                <%-- REJECTED --%>
                <c:when test="${ticket.status == 'REJECTED'}">
                  <div style="background:#fef2f2;border:1px solid #fecaca;
                              border-radius:var(--hms-radius);padding:0.75rem 1rem">
                    <span class="badge-hms badge-danger" style="display:inline-block;margin-bottom:6px">Đã từ chối</span>
                    <c:if test="${not empty ticket.rejectionReason}">
                      <p style="font-size:0.8125rem;color:#7f1d1d;margin:0">
                        <strong>Lý do:</strong> <c:out value="${ticket.rejectionReason}"/>
                      </p>
                    </c:if>
                  </div>
                </c:when>

              </c:choose>

            </div>
          </div>

        </div><%-- end col-lg-4 --%>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
