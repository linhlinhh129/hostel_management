<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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

        <div class="data-surface p-4" style="max-width: 800px;margin:0 auto">

          <%-- Cảnh báo tiền nợ cũ --%>
          <c:if test="${previousDebt != null and previousDebt > 0}">
            <div class="alert alert-warning d-flex align-items-start gap-2 mb-4" style="border-radius:8px;background:#fff8e1;border:1px solid #ffd54f;color:#7c5c00;">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#e65100" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink:0;margin-top:2px">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                <line x1="12" y1="9" x2="12" y2="13"></line>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
              </svg>
              <div>
                <strong>Phòng <c:out value="${prefilledRoomCode}"/> còn nợ:</strong>
                <span style="font-size:1.05rem;font-weight:700;color:#e65100;margin-left:6px">
                  <fmt:formatNumber value="${previousDebt}" pattern="#,##0"/> đ
                </span>
                <br>
                <small>Số tiền này đã được tự động cộng vào <strong>Phí khác</strong> của hóa đơn mới.</small>
              </div>
            </div>
          </c:if>

          <form action="${ctx}/manager/invoices" method="post" id="createInvoiceForm">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="csrfToken" value="${csrfToken}">

            <div class="row g-4">
              <div class="col-md-6">
                <label class="form-label fw-bold">Mã phòng <span class="text-danger">*</span></label>
                <div class="input-group">
                  <input type="text" class="form-control" name="roomCode" id="roomCodeInput" required placeholder="VD: HN0101"
                    value="<c:out value='${prefilledRoomCode}'/>">
                  <button type="button" class="btn btn-outline-secondary" id="checkDebtBtn" title="Kiểm tra tiền nợ">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="11" cy="11" r="8"></circle>
                      <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                  </button>
                </div>
                <small id="debtHint" class="text-muted mt-1 d-block"></small>
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
                <input type="number" class="form-control" name="otherFee" id="otherFeeInput"
                  value="<c:choose><c:when test='${previousDebt != null}'>${previousDebt}</c:when><c:otherwise>0</c:otherwise></c:choose>"
                  min="0" step="1000">
                <c:if test="${previousDebt != null and previousDebt > 0}">
                  <small class="text-warning fw-bold">
                    ⚠ Đã bao gồm tiền nợ cũ: <fmt:formatNumber value="${previousDebt}" pattern="#,##0"/> đ
                  </small>
                </c:if>
                <small id="debtInOtherFeeHint" class="d-block mt-1" style="color:#e65100;font-weight:600;display:none!important"></small>
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

  <script>
    (function () {
      var ctx = '<c:out value="${ctx}"/>';
      var roomCodeInput = document.getElementById('roomCodeInput');
      var otherFeeInput = document.getElementById('otherFeeInput');
      var debtHint = document.getElementById('debtHint');
      var debtInOtherFeeHint = document.getElementById('debtInOtherFeeHint');
      var checkDebtBtn = document.getElementById('checkDebtBtn');

      // Khi nhấn nút tìm kiếm tiền nợ, reload form với roomCode để server tính nợ
      checkDebtBtn.addEventListener('click', function () {
        var roomCode = roomCodeInput.value.trim();
        if (!roomCode) {
          debtHint.textContent = 'Vui lòng nhập mã phòng.';
          debtHint.style.color = '#dc3545';
          return;
        }
        var url = ctx + '/manager/invoices?action=create&roomCode=' + encodeURIComponent(roomCode);
        window.location.href = url;
      });

      // Cho phép Enter trong ô mã phòng cũng trigger kiểm tra nợ
      roomCodeInput.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') {
          e.preventDefault();
          checkDebtBtn.click();
        }
      });
    })();
  </script>
</body>
</html>
