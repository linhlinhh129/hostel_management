<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Gửi yêu cầu chỉnh sửa - BQL"/>
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

      <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <h1>Gửi yêu cầu chỉnh sửa số điện nước</h1>
        <p>Báo cáo chỉ số sai cho nhân viên vận hành (Operator) để xác minh và cập nhật lại</p>
      </div>

      <div class="row g-4">
        <%-- Cột trái: Form nhập --%>
        <div class="col-lg-7">
          <div class="data-surface" style="padding:2rem">
            <%-- Tiến độ xử lý hiện tại nếu đã gửi --%>
            <c:choose>
              <c:when test="${invoice.meterStatus == 'REPORTED' or invoice.meterStatus == 'UPDATED'}">
                <div class="p-4" style="border:1px solid var(--hms-border); border-radius:var(--hms-radius-lg); background:var(--hms-surface-2);">
                  <h3 style="font-size:1.15rem; font-weight:700; color:var(--hms-ink); margin-bottom:1.25rem;">Tiến độ xử lý hiện tại</h3>
                  
                  <div class="mb-3">
                    <span class="text-muted d-block" style="font-size:0.85rem">Nhân viên phụ trách:</span>
                    <strong style="font-size:1rem; color:var(--hms-ink)"><c:out value="${invoice.operatorName}"/></strong>
                  </div>

                  <div class="mb-3">
                    <span class="text-muted d-block" style="font-size:0.85rem; margin-bottom:0.25rem">Trạng thái công việc:</span>
                    <c:choose>
                      <c:when test="${invoice.ticketStatus == 'PENDING'}">
                        <span class="badge-hms badge-warning">Chờ nhận việc</span>
                      </c:when>
                      <c:when test="${invoice.ticketStatus == 'ASSIGNED'}">
                        <span class="badge-hms badge-info">Đã tiếp nhận</span>
                      </c:when>
                      <c:when test="${invoice.ticketStatus == 'IN_PROGRESS'}">
                        <span class="badge-hms badge-warning">Đang xử lý</span>
                      </c:when>
                      <c:when test="${invoice.ticketStatus == 'DONE'}">
                        <span class="badge-hms badge-success">Hoàn thành</span>
                      </c:when>
                      <c:when test="${invoice.ticketStatus == 'REJECTED'}">
                        <span class="badge-hms badge-danger">Từ chối</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge-hms badge-neutral"><c:out value="${invoice.ticketStatus}"/></span>
                      </c:otherwise>
                    </c:choose>
                  </div>



                  <div class="d-flex gap-2">
                    <a href="${ctx}/manager/notifications?tab=incorrect-utility" class="btn-mintlify-secondary text-decoration-none">Quay lại</a>
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                <%-- Form nhập gửi yêu cầu --%>
                <form method="post" action="${ctx}/manager/notifications/send-operator">
                  <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                  <input type="hidden" name="invoiceId" value="${invoice.id}"/>

                  <%-- Chọn Nhân viên vận hành --%>
                  <div class="mb-3">
                    <label for="operatorId" class="form-label">Chọn nhân viên vận hành <span class="text-danger">*</span></label>
                    <select class="form-select" id="operatorId" name="operatorId" required>
                      <option value="">-- Chọn nhân viên vận hành --</option>
                      <c:forEach var="op" items="${operators}">
                        <option value="${op.id}">
                          <c:out value="${op.fullName}"/>
                        </option>
                      </c:forEach>
                    </select>
                    <div class="form-text">Nhân viên sẽ nhận được yêu cầu xử lý lỗi trong trang tác vụ của họ.</div>
                  </div>

                  <%-- Tiêu đề yêu cầu --%>
                  <div class="mb-3">
                    <label for="title" class="form-label">Tiêu đề yêu cầu <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="title" name="title"
                           required maxlength="200"
                           value="<c:out value='${defaultTitle}'/>">
                  </div>

                  <%-- Nội dung yêu cầu --%>
                  <div class="mb-3">
                    <label for="content" class="form-label">Nội dung chi tiết <span class="text-danger">*</span></label>
                    <textarea class="form-control" id="content" name="content"
                              rows="8" required maxlength="5000"><c:out value="${defaultContent}"/></textarea>
                    <div class="form-text">Mô tả cụ thể lý do sai và yêu cầu đo đạc/chốt số lại.</div>
                  </div>

                  <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="quick-action-btn primary">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                           stroke="currentColor" stroke-width="2.5" style="margin-right:4px">
                        <line x1="22" y1="2" x2="11" y2="13"/>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                      </svg>
                      Gửi Operator
                    </button>
                    <a href="${ctx}/manager/notifications?tab=incorrect-utility" class="btn-mintlify-secondary text-decoration-none">Quay lại</a>
                  </div>
                </form>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <%-- Cột phải: Thông tin hóa đơn bị báo sai --%>
        <div class="col-lg-5">
          <div class="widget-surface">
            <div class="widget-surface-header">
              <h3>Thông tin hóa đơn báo lỗi</h3>
            </div>
            <div class="widget-surface-body p-0">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted);width:40%">Mã hóa đơn</td>
                  <td style="padding:12px 16px;font-weight:600"><c:out value="${invoice.code}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Cơ sở</td>
                  <td style="padding:12px 16px"><c:out value="${invoice.facilityName}"/> (<c:out value="${invoice.facilityCode}"/>)</td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Phòng</td>
                  <td style="padding:12px 16px;font-weight:700"><c:out value="${invoice.roomCode}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Kỳ hạn</td>
                  <td style="padding:12px 16px"><c:out value="${invoice.billingPeriod}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Số điện chốt</td>
                  <td style="padding:12px 16px"><strong><c:out value="${invoice.electric}"/></strong> kWh</td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Số nước chốt</td>
                  <td style="padding:12px 16px"><strong><c:out value="${invoice.water}"/></strong> m³</td>
                </tr>
                <tr style="background:var(--hms-accent-bg)">
                  <td style="padding:12px 16px;font-weight:700;color:var(--hms-ink)">Tổng số tiền</td>
                  <td style="padding:12px 16px;font-weight:800;color:var(--hms-accent-deep)">
                    <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ
                  </td>
                </tr>
              </table>
            </div>
          </div>
         
        </div>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
