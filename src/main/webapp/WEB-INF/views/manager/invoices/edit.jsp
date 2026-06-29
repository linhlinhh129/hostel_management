<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Chỉnh sửa Hóa đơn - BQL" />
<c:set var="pageRole" value="MANAGER" />
<c:set var="activeMenu" value="invoices" />
<jsp:include page="/WEB-INF/views/layout/head.jsp" />

<body>
  <div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
      <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
      <main class="page-content">
        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

        <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
          <div>
            <h1>Chỉnh Sửa Hóa Đơn: <c:out value="${invoice.invoiceCode}" /></h1>
          </div>
          <a href="${ctx}/manager/invoices/${invoice.invoiceId}" class="btn-mintlify-secondary text-decoration-none" style="position:relative;z-index:1">← Quay lại chi tiết</a>
        </div>

        <div class="data-surface p-4" style="max-width: 800px;">
          <form action="${ctx}/manager/invoices/${invoice.invoiceId}/edit" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <div class="row g-4">
              <div class="col-md-6">
                <label class="form-label fw-bold">Mã phòng</label>
                <input type="text" class="form-control" value="<c:out value="${invoice.roomCode}"/>" disabled>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Kỳ hóa đơn</label>
                <input type="text" class="form-control" value="<c:out value="${invoice.billingPeriod}"/>" disabled>
              </div>
              
              <div class="col-md-6">
                <label class="form-label fw-bold">Tiền phòng cố định (VNĐ)</label>
                <input type="number" class="form-control" value="<c:out value="${invoice.roomFee}"/>" disabled>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Hạn thanh toán <span class="text-danger">*</span></label>
                <input type="date" class="form-control" name="dueDate" value="<c:out value="${invoice.dueDateISO}"/>" required>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Thuế (%) <span class="text-danger">*</span></label>
                <input type="number" class="form-control" name="taxRate" value="<c:out value="${invoice.taxRate}"/>" min="0" step="0.1" required>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Phí khác (VNĐ)</label>
                <input type="number" class="form-control" name="otherFee" value="<c:out value="${invoice.otherFee}"/>" min="0" step="1000">
              </div>

              <div class="col-12">
                <label class="form-label fw-bold">Ghi chú</label>
                <textarea class="form-control" name="note" rows="3"><c:out value="${invoice.note}"/></textarea>
              </div>
            </div>

            <div class="mt-4 pt-3 border-top d-flex gap-2">
              <button type="submit" class="btn-mintlify-primary">Lưu Thay Đổi</button>
              <a href="${ctx}/manager/invoices/${invoice.invoiceId}" class="btn-mintlify-secondary text-decoration-none">Hủy bỏ</a>
            </div>
          </form>
        </div>

      </main>
    </div>
  </div>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
