<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Gửi nhắc nhở thanh toán - BQL"/>
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
        <h1>Gửi nhắc nhở thanh toán quá hạn</h1>
        <p>Gửi thông báo nhắc đóng tiền trực tiếp tới cư dân của phòng có hóa đơn quá hạn</p>
      </div>

      <div class="row g-4">
        <%-- Cột trái: Form soạn thảo nhắc nợ --%>
        <div class="col-lg-7">
          <div class="data-surface" style="height:100%">
            <form method="post" action="${ctx}/manager/notifications/send-debt-reminder" class="p-4">
              <input type="hidden" name="csrfToken" value="${csrfToken}"/>
              <input type="hidden" name="invoiceId" value="${invoice.id}"/>

              <%-- Tiêu đề yêu cầu --%>
              <div class="mb-3">
                <label for="title" class="form-label">Tiêu đề thông báo <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="title" name="title"
                       required maxlength="200"
                       value="<c:out value='${defaultTitle}'/>">
              </div>

              <%-- Nội dung yêu cầu --%>
              <div class="mb-3">
                <label for="content" class="form-label">Nội dung chi tiết <span class="text-danger">*</span></label>
                <textarea class="form-control" id="content" name="content"
                          rows="10" required maxlength="5000"><c:out value="${defaultContent}"/></textarea>
                <div class="form-text">Bạn có thể tùy chỉnh thêm nội dung nhắc nhở để phù hợp với thực tế trước khi gửi.</div>
              </div>

              <div class="d-flex gap-2 mt-4">
                <button type="submit" class="quick-action-btn primary" style="background-color:#d97706;border-color:#d97706;color:#ffffff;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                       stroke="currentColor" stroke-width="2.5" style="margin-right:4px">
                    <line x1="22" y1="2" x2="11" y2="13"/>
                    <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                  </svg>
                  Gửi nhắc nợ
                </button>
                <a href="${ctx}/manager/debts" class="btn-mintlify-secondary text-decoration-none">Hủy bỏ</a>
              </div>
            </form>
          </div>
        </div>

        <%-- Cột phải: Chi tiết hóa đơn nợ --%>
        <div class="col-lg-5">
          <div class="widget-surface">
            <div class="widget-surface-header">
              <h3>Thông tin hóa đơn quá hạn</h3>
            </div>
            <div class="widget-surface-body p-0">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted);width:40%">Mã hóa đơn</td>
                  <td style="padding:12px 16px;font-weight:600">
                    <span class="code-badge"><c:out value="${invoice.code}"/></span>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Cơ sở</td>
                  <td style="padding:12px 16px"><c:out value="${invoice.facilityName}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Phòng</td>
                  <td style="padding:12px 16px;font-weight:700">
                    <span class="badge-hms badge-neutral"><c:out value="${invoice.roomCode}"/></span>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Khách thuê đại diện</td>
                  <td style="padding:12px 16px;font-weight:500"><c:out value="${invoice.tenantName}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Số điện thoại</td>
                  <td style="padding:12px 16px"><c:out value="${invoice.tenantPhone}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Tổng tiền cần đóng</td>
                  <td style="padding:12px 16px;font-weight:700;color:var(--hms-danger-color,#ef4444);font-size:0.9375rem">
                    <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Hạn thanh toán</td>
                  <td style="padding:12px 16px;font-weight:500"><c:out value="${invoice.dueDateLabel}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:12px 16px;color:var(--hms-text-muted)">Thời gian quá hạn</td>
                  <td style="padding:12px 16px;font-weight:700;color:var(--hms-warning-color,#d97706)">
                    <c:out value="${invoice.overdueDays}"/> ngày
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
