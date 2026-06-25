<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Tạo tài khoản người thuê - BQL"/>
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

      <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
        <div>
          <h1>Tạo tài khoản người thuê</h1>
          <p>Tạo tài khoản thành viên dựa trên thông tin hợp đồng</p>
        </div>
        <a href="${ctx}/manager/contracts/detail?id=${prefilledContract.contractId}" class="btn-mintlify-secondary text-decoration-none">← Chi tiết hợp đồng</a>
      </div>

      <div class="data-surface" style="max-width:700px">
        <form method="post" action="${ctx}/manager/contracts/add-tenant" class="p-4">
          <input type="hidden" name="csrfToken" value="${csrfToken}"/>
          <input type="hidden" name="contractId" value="${prefilledContract.contractId}"/>



          <c:if test="${showReactivateConfirmation}">
            <div class="alert alert-warning mb-3" style="border-left: 5px solid #ffb300; background-color: #fffde7; color: #663c00; border-radius: 6px; padding: 1rem;">
              <h5 style="font-weight: 700; margin-bottom: 8px; color: #b78103;" class="d-flex align-items-center gap-2">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                  <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
                Cảnh báo: Phát hiện Email của Người thuê cũ
              </h5>
              <p class="mb-2" style="font-size: 0.875rem;">Email này trùng khớp với tài khoản người thuê cũ đã ngưng thuê trên hệ thống:</p>
              <ul class="mb-3" style="font-size: 0.875rem; padding-left: 20px;">
                <li><strong>Họ tên cũ:</strong> <c:out value="${existingUserFullName}"/></li>
                <li><strong>Số CMND/CCCD cũ:</strong> <c:out value="${existingUserIdentity}"/></li>
              </ul>
              <div class="form-check">
                <input class="form-check-input" type="checkbox" name="confirmReactivate" id="confirmReactivate" value="true" required>
                <label class="form-check-label text-danger font-weight-bold" for="confirmReactivate" style="font-size: 0.875rem; cursor: pointer;">
                  Tôi xác nhận đây đúng là người thuê cũ quay lại và đồng ý kích hoạt lại tài khoản này.
                </label>
              </div>
            </div>
          </c:if>

          <%-- ── Section 1: Thông tin cá nhân ─────────────────────── --%>
          <h5 style="font-weight:600;color:var(--hms-ink);padding-bottom:8px;
                     border-bottom:1px solid var(--hms-border);margin-bottom:1rem">
            Thông tin cá nhân
          </h5>

          <div class="mb-3">
            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="fullName" name="fullName"
                   required maxlength="200" placeholder="Nguyễn Văn A"
                   value="<c:out value="${dto != null ? dto.fullName : prefilledContract.tenantFullName}"/>">
          </div>

          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
              <input type="tel" class="form-control" id="phone" name="phone"
                     required maxlength="20" placeholder="0901234567"
                     value="<c:out value="${dto != null ? dto.phone : prefilledContract.tenantPhone}"/>">
            </div>
            <div class="col-sm-6">
              <label for="email" class="form-label">Email đăng nhập <span class="text-danger">*</span></label>
              <input type="email" class="form-control" id="email" name="email"
                     required maxlength="200" placeholder="tenant@email.com"
                     value="<c:out value="${dto != null ? dto.email : ''}"/>">
            </div>
          </div>

          <div class="mb-3">
            <label for="identityNumber" class="form-label">Số CCCD / CMND <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="identityNumber" name="identityNumber"
                   required maxlength="20" placeholder="012345678901"
                   value="<c:out value="${dto != null ? dto.identityNumber : prefilledContract.tenantIdentityNumber}"/>">
          </div>

          <div class="mb-3">
            <label for="permanentAddress" class="form-label">Địa chỉ thường trú</label>
            <input type="text" class="form-control" id="permanentAddress" name="permanentAddress"
                   maxlength="500" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành"
                   value="<c:out value="${dto != null ? dto.permanentAddress : prefilledContract.tenantPermanentAddress}"/>">
          </div>

          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="gender" class="form-label">Giới tính</label>
              <select class="form-select" id="gender" name="gender">
                <option value="">-- Chọn --</option>
                <option value="MALE" ${(dto != null ? dto.gender : '') == 'MALE' ? 'selected' : ''}>Nam</option>
                <option value="FEMALE" ${(dto != null ? dto.gender : '') == 'FEMALE' ? 'selected' : ''}>Nữ</option>
                <option value="OTHER" ${(dto != null ? dto.gender : '') == 'OTHER' ? 'selected' : ''}>Khác</option>
              </select>
            </div>
            <div class="col-sm-6">
              <label for="dob" class="form-label">Ngày sinh</label>
              <input type="date" class="form-control" id="dob" name="dob"
                     value="<c:out value="${dto != null ? dto.dob : prefilledContract.tenantDob}"/>">
            </div>
          </div>

          <%-- ── Section 2: Thông tin thuê ──────────────────────────── --%>
          <h5 style="font-weight:600;color:var(--hms-ink);padding-bottom:8px;
                     border-bottom:1px solid var(--hms-border);margin-bottom:1rem;margin-top:1.5rem">
            Thông tin thuê
          </h5>

          <%-- Chọn phòng — Khóa đối với hợp đồng --%>
          <div class="mb-3">
            <label class="form-label">Phòng <span class="text-danger">*</span></label>
            <input type="hidden" name="roomId" value="${prefilledContract.roomId}"/>
            <input type="text" class="form-control" value="Phòng ${prefilledContract.roomCode}" readonly style="background-color: var(--hms-neutral-bg);"/>
          </div>

          <div class="mb-3">
            <label for="contractStartDate" class="form-label">Ngày bắt đầu thuê</label>
            <input type="date" class="form-control" id="contractStartDate" name="contractStartDate"
                   value="<c:out value="${prefilledContract.startDate}"/>" readonly style="background-color: var(--hms-neutral-bg);">
          </div>

          <%-- Info box: mật khẩu tạm --%>
          <div style="background:var(--hms-accent-bg);border:1px solid var(--hms-border-accent);
                      border-radius:var(--hms-radius);padding:0.75rem 1rem;
                      font-size:0.8125rem;color:var(--hms-ink);margin-bottom:1.5rem">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                 stroke="var(--hms-accent-deep)" stroke-width="2"
                 style="margin-right:6px;vertical-align:-2px">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <strong>Mật khẩu tạm thời</strong> được tạo ngẫu nhiên bảo mật và gửi qua email đăng nhập của người thuê.
            Người thuê nên đổi mật khẩu sau khi đăng nhập lần đầu.
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="quick-action-btn primary">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                   stroke-width="2.5" style="margin-right:4px">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <line x1="19" y1="8" x2="19" y2="14"/>
                <line x1="22" y1="11" x2="16" y2="11"/>
              </svg>
              Tạo tài khoản người thuê
            </button>
            <a href="${ctx}/manager/contracts/detail?id=${prefilledContract.contractId}" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
          </div>
        </form>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
