<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết Hợp đồng - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="contracts"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="page-header">
        <div>
          <h1>Chi tiết Hợp đồng</h1>
          <p>Phòng <strong><c:out value="${contract.roomCode}"/></strong> — <c:out value="${contract.facilityName}"/></p>
        </div>
        <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary">← Quay lại</a>
      </div>

      <div class="row g-4">
        <!-- Thông tin hợp đồng -->
        <div class="col-md-6">
          <div class="data-surface">
            <h5 class="mb-3">Thông tin hợp đồng</h5>
            <dl class="detail-list">
              <dt>Phòng</dt>           <dd><c:out value="${contract.roomCode}"/></dd>
              <dt>Cơ sở</dt>           <dd><c:out value="${contract.facilityName}"/></dd>
              <dt>Người thuê</dt>      <dd><c:out value="${contract.tenantName}"/></dd>
              <dt>Giá thuê</dt>
              <dd><fmt:formatNumber value="${contract.rentPrice}" type="number" groupingUsed="true"/>đ/tháng</dd>
              <dt>Ngày vào</dt>        <dd><c:out value="${contract.moveInDate}"/></dd>
              <dt>Ngày hết hạn</dt>   <dd><c:out value="${contract.expiryDate}"/></dd>
              <dt>Chu kỳ TT</dt>      <dd><c:out value="${contract.billingCycle}"/> tháng</dd>
              <dt>Trạng thái</dt>
              <dd>
                <span class="badge-hms
                  <c:choose>
                    <c:when test="${contract.status == 'ACTIVE'}">success</c:when>
                    <c:when test="${contract.status == 'EXPIRING_SOON'}">warning</c:when>
                    <c:when test="${contract.status == 'OVERDUE'}">danger</c:when>
                    <c:otherwise>secondary</c:otherwise>
                  </c:choose>">
                  <c:choose>
                    <c:when test="${contract.status == 'ACTIVE'}">Đang hiệu lực</c:when>
                    <c:when test="${contract.status == 'EXPIRING_SOON'}">Sắp hết hạn</c:when>
                    <c:when test="${contract.status == 'OVERDUE'}">Quá hạn</c:when>
                    <c:otherwise>Đã thanh lý</c:otherwise>
                  </c:choose>
                </span>
              </dd>
              <dt>Ngày tạo</dt>       <dd><c:out value="${contract.createdAt}"/></dd>
              <dt>Người tạo</dt>      <dd><c:out value="${contract.createdByName}"/></dd>
            </dl>
          </div>
        </div>

        <!-- Tiền cọc + Tài liệu -->
        <div class="col-md-6">
          <div class="data-surface mb-4">
            <h5 class="mb-3">Tiền cọc</h5>
            <dl class="detail-list">
              <dt>Số tiền cọc</dt>
              <dd><fmt:formatNumber value="${contract.depositAmount}" type="number" groupingUsed="true"/>đ</dd>
              <dt>Trạng thái</dt>
              <dd>
                <span class="badge-hms
                  <c:choose>
                    <c:when test="${contract.depositStatus == 'PAID'}">success</c:when>
                    <c:when test="${contract.depositStatus == 'PARTIAL'}">warning</c:when>
                    <c:otherwise>danger</c:otherwise>
                  </c:choose>">
                  <c:choose>
                    <c:when test="${contract.depositStatus == 'PAID'}">Đã đóng đủ</c:when>
                    <c:when test="${contract.depositStatus == 'PARTIAL'}">Đóng 1 phần</c:when>
                    <c:otherwise>Chưa đóng</c:otherwise>
                  </c:choose>
                </span>
              </dd>
            </dl>

            <!-- Cập nhật tiền cọc (chỉ hiện khi hợp đồng chưa thanh lý) -->
            <c:if test="${contract.status != 'TERMINATED'}">
              <form method="post" action="${ctx}/manager/contracts/deposit" class="mt-3 d-flex gap-2 align-items-end">
                <input type="hidden" name="contractId" value="${contract.id}"/>
                <div>
                  <label class="form-label small">Cập nhật trạng thái cọc</label>
                  <select name="depositStatus" class="form-select form-select-sm">
                    <option value="UNPAID"  ${contract.depositStatus == 'UNPAID'  ? 'selected' : ''}>Chưa đóng</option>
                    <option value="PARTIAL" ${contract.depositStatus == 'PARTIAL' ? 'selected' : ''}>Đóng 1 phần</option>
                    <option value="PAID"    ${contract.depositStatus == 'PAID'    ? 'selected' : ''}>Đã đóng đủ</option>
                  </select>
                </div>
                <button type="submit" class="btn-mintlify-secondary btn-sm">Lưu</button>
              </form>
            </c:if>
          </div>

          <!-- Tài liệu -->
          <div class="data-surface">
            <h5 class="mb-3">Tài liệu hợp đồng</h5>
            <c:choose>
              <c:when test="${not empty contract.documentPath}">
                <p><a href="${ctx}${contract.documentPath}" target="_blank">📄 Xem tài liệu</a></p>
              </c:when>
              <c:otherwise>
                <p class="text-muted small">Chưa có tài liệu đính kèm.</p>
              </c:otherwise>
            </c:choose>
            <c:if test="${contract.status != 'TERMINATED'}">
              <form method="post" action="${ctx}/manager/contracts/${contract.id}/upload"
                    enctype="multipart/form-data" class="mt-2 d-flex gap-2 align-items-end">
                <div>
                  <label class="form-label small">Upload file (PDF/JPG/PNG, max 5MB)</label>
                  <input type="file" name="contractFile" class="form-control form-control-sm"
                         accept=".pdf,.jpg,.jpeg,.png"/>
                </div>
                <button type="submit" class="btn-mintlify-secondary btn-sm">Tải lên</button>
              </form>
            </c:if>
          </div>
        </div>
      </div>

      <!-- Thanh lý hợp đồng -->
      <c:if test="${contract.status != 'TERMINATED'}">
        <div class="data-surface mt-4">
          <h5 class="text-danger mb-2">⚠ Thanh lý hợp đồng</h5>
          <p class="text-muted small">Thanh lý sẽ giải phóng phòng và vô hiệu hóa tài khoản người thuê. Thao tác này <strong>không thể hoàn tác</strong>.</p>
          <form method="post" action="${ctx}/manager/contracts/${contract.id}/terminate"
                onsubmit="return confirm('Bạn có chắc muốn thanh lý hợp đồng này?')">
            <button type="submit" class="btn btn-danger btn-sm">Thanh lý hợp đồng</button>
          </form>
        </div>
      </c:if>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
