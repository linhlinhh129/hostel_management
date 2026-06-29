<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết thông báo - BQL"/>
<c:set var="pageRole" value="OPERATOR"/>
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

      <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <div>
          <h1><c:out value="${notification.title}"/></h1>
          <p>Mã: <strong><c:out value="${notification.code}"/></strong></p>
        </div>
        <a href="${ctx}/operator/notifications" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
      </div>

      <div class="row g-3">

        <%-- Cột chính: Nội dung --%>
        <div class="col-lg-8">
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Nội dung thông báo</h3></div>
            <div class="widget-surface-body">
              <div style="white-space:pre-line;font-size:0.9375rem;line-height:1.7;color:var(--hms-ink)">
                <c:out value="${notification.content}"/>
              </div>
            </div>
          </div>
        </div>

        <%-- Cột phụ: Thông tin --%>
        <div class="col-lg-4">
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Thông tin</h3></div>
            <div class="widget-surface-body p-0">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted);white-space:nowrap">Mã</td>
                  <td style="padding:10px 16px;font-weight:500"><c:out value="${notification.code}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Người tạo</td>
                  <td style="padding:10px 16px"><c:out value="${notification.createdByName}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Trạng thái</td>
                  <td style="padding:10px 16px">
                    <c:choose>
                      <c:when test="${notification.status == 'SENT'}">
                        <span class="badge-hms badge-success">Đã gửi</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge-hms badge-warning">Nháp</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Ngày tạo</td>
                  <td style="padding:10px 16px;font-size:0.8125rem"><c:out value="${notification.createdAtLabel}"/></td>
                </tr>
                <tr>
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Gửi lúc</td>
                  <td style="padding:10px 16px;font-size:0.8125rem"><c:out value="${notification.sentAtLabel}"/></td>
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
