<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Tạo Hợp đồng - BQL"/>
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
        <div><h1>Tạo Hợp đồng mới</h1></div>
        <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary">← Quay lại</a>
      </div>

      <div class="data-surface" style="max-width: 680px">
        <form method="post" action="${ctx}/manager/contracts/create">

          <div class="mb-3">
            <label class="form-label">Phòng <span class="text-danger">*</span></label>
            <select name="roomId" class="form-select" required>
              <option value="">-- Chọn cơ sở / phòng --</option>
              <c:forEach var="f" items="${assignedFacilities}">
                <optgroup label="${f.name}">
                  <%-- Phòng sẽ được load bằng AJAX hoặc server render tại đây --%>
                </optgroup>
              </c:forEach>
            </select>
            <small class="form-text text-muted">Chỉ hiển thị phòng AVAILABLE trong cơ sở bạn được phân công.</small>
          </div>

          <div class="mb-3">
            <label class="form-label">Người thuê đại diện</label>
            <input type="number" name="tenantRepId" class="form-control" placeholder="ID người thuê"/>
            <small class="form-text text-muted">ID tài khoản người thuê đã tạo sẵn.</small>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <label class="form-label">Giá thuê (đ/tháng) <span class="text-danger">*</span></label>
              <input type="number" name="rentPrice" class="form-control" min="1" required placeholder="3000000"/>
            </div>
            <div class="col-md-6">
              <label class="form-label">Chu kỳ thanh toán (tháng)</label>
              <input type="number" name="billingCycle" class="form-control" value="1" min="1" max="12"/>
            </div>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <label class="form-label">Tiền cọc (đ)</label>
              <input type="number" name="depositAmount" class="form-control" min="0" placeholder="0"/>
            </div>
            <div class="col-md-6">
              <label class="form-label">Trạng thái tiền cọc</label>
              <select name="depositStatus" class="form-select">
                <option value="UNPAID">Chưa đóng</option>
                <option value="PARTIAL">Đóng 1 phần</option>
                <option value="PAID">Đã đóng đủ</option>
              </select>
            </div>
          </div>

          <div class="row g-3 mb-4">
            <div class="col-md-6">
              <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
              <input type="date" name="moveInDate" class="form-control" required/>
            </div>
            <div class="col-md-6">
              <label class="form-label">Ngày hết hạn <span class="text-danger">*</span></label>
              <input type="date" name="expiryDate" class="form-control" required/>
            </div>
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="quick-action-btn primary">Tạo hợp đồng</button>
            <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
          </div>
        </form>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
