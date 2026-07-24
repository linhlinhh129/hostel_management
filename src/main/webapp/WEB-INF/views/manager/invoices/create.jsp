<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Tạo Hóa đơn - BQL" />
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
            <h1>Tạo Hóa Đơn Mới</h1>
            <p>Hệ thống sẽ tự động lấy đơn giá hiện tại và chỉ số điện nước để tính toán</p>
          </div>
          <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none" style="position:relative;z-index:1">← Quay lại danh sách</a>
        </div>

        <div class="data-surface p-4" style="max-width: 800px;">
          <form action="${ctx}/manager/invoices" method="post">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="csrfToken" value="${csrfToken}">

            <div class="row g-4">
              <div class="col-md-6">
                <label class="form-label fw-bold">Mã phòng <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="roomCode" required placeholder="VD: HN0101">
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Kỳ hóa đơn (YYYYMM) <span class="text-danger">*</span></label>
                <input type="text" class="form-control" name="billingPeriod" required pattern="\d{6}" placeholder="VD: 202606">
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Hạn thanh toán <span class="text-danger">*</span></label>
                <input type="date" class="form-control" name="dueDate" required>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Thuế (%) <span class="text-danger">*</span></label>
                <input type="number" class="form-control" name="taxRate" value="0" min="0" step="0.1" required>
              </div>

              <div class="col-md-6">
                <label class="form-label fw-bold">Phí khác (VNĐ)</label>
                <input type="number" class="form-control" name="otherFee" value="0" min="0" step="1000">
              </div>

              <div class="col-12">
                <label class="form-label fw-bold">Ghi chú</label>
                <textarea class="form-control" name="note" rows="3" placeholder="Ghi chú thêm nếu có..."></textarea>
              </div>
            </div>

            <div class="mt-4 pt-3 border-top d-flex gap-2">
              <button type="submit" class="btn-mintlify-primary">Tạo Hóa Đơn</button>
              <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none">Hủy bỏ</a>
            </div>
          </form>
        </div>

      </main>
    </div>
  </div>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
