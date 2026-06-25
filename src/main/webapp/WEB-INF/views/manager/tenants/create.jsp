<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thêm người thuê - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="tenants"/>
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
          <h1>Thêm người thuê mới</h1>
          <p>Tạo tài khoản và gán vào phòng trong cơ sở được phân công</p>
        </div>
        <a href="${ctx}/manager/tenants" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
      </div>

      <div class="data-surface" style="max-width:700px">
        <form method="post" action="${ctx}/manager/tenants/create" class="p-4">
          <input type="hidden" name="csrfToken" value="${csrfToken}"/>

          <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
          </c:if>

          <%-- ── Section 1: Thông tin cá nhân ─────────────────────── --%>
          <h5 style="font-weight:600;color:var(--hms-ink);padding-bottom:8px;
                     border-bottom:1px solid var(--hms-border);margin-bottom:1rem">
            Thông tin cá nhân
          </h5>

          <div class="mb-3">
            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="fullName" name="fullName"
                   required maxlength="200" placeholder="Nguyễn Văn A">
          </div>

          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
              <input type="tel" class="form-control" id="phone" name="phone"
                     required maxlength="20" placeholder="0901234567">
            </div>
            <div class="col-sm-6">
              <label for="email" class="form-label">Email đăng nhập <span class="text-danger">*</span></label>
              <input type="email" class="form-control" id="email" name="email"
                     required maxlength="200" placeholder="tenant@email.com">
            </div>
          </div>

          <div class="mb-3">
            <label for="identityNumber" class="form-label">Số CCCD / CMND <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="identityNumber" name="identityNumber"
                   required maxlength="20" placeholder="012345678901">
          </div>

          <div class="mb-3">
            <label for="permanentAddress" class="form-label">Địa chỉ thường trú</label>
            <input type="text" class="form-control" id="permanentAddress" name="permanentAddress"
                   maxlength="500" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành">
          </div>

          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="gender" class="form-label">Giới tính</label>
              <select class="form-select" id="gender" name="gender">
                <option value="">-- Chọn --</option>
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </div>
            <div class="col-sm-6">
              <label for="dob" class="form-label">Ngày sinh</label>
              <input type="date" class="form-control" id="dob" name="dob">
            </div>
          </div>

          <%-- ── Section 2: Thông tin thuê ──────────────────────────── --%>
          <h5 style="font-weight:600;color:var(--hms-ink);padding-bottom:8px;
                     border-bottom:1px solid var(--hms-border);margin-bottom:1rem;margin-top:1.5rem">
            Thông tin thuê
          </h5>

          <%-- Chọn phòng — grouped by facility, dùng roomsByFacility map --%>
          <div class="mb-3">
            <label for="roomId" class="form-label">Phòng <span class="text-danger">*</span></label>
            <select class="form-select" id="roomId" name="roomId" required>
              <option value="">-- Chọn phòng --</option>
              <c:choose>
                <c:when test="${not empty assignedFacilities}">
                  <c:forEach var="facility" items="${assignedFacilities}">
                    <c:set var="facilityRooms" value="${roomsByFacility[facility.id]}"/>
                    <optgroup label="${facility.name} (${facility.code})">
                      <c:choose>
                        <c:when test="${not empty facilityRooms}">
                          <c:forEach var="room" items="${facilityRooms}">
                            <option value="${room.id}">
                              <c:out value="${room.code}"/>
                              — Tầng <c:out value="${room.floor}"/>
                              <c:if test="${not empty room.area}">
                                — <fmt:formatNumber value="${room.area}" maxFractionDigits="1"/>m²
                              </c:if>
                            </option>
                          </c:forEach>
                        </c:when>
                        <c:otherwise>
                          <option disabled value="">Không có phòng trống</option>
                        </c:otherwise>
                      </c:choose>
                    </optgroup>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <option disabled>Bạn chưa được phân công cơ sở nào</option>
                </c:otherwise>
              </c:choose>
            </select>
            <div class="form-text">Chỉ hiển thị phòng đang trống trong cơ sở bạn được phân công.</div>
          </div>

          <div class="mb-3">
            <label for="contractStartDate" class="form-label">Ngày bắt đầu thuê</label>
            <input type="date" class="form-control" id="contractStartDate" name="contractStartDate">
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
            <strong>Mật khẩu tạm thời</strong> được tạo tự động từ
            <strong>8 số cuối CCCD</strong> + <code>@Hostel</code>.
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
              Tạo người thuê
            </button>
            <a href="${ctx}/manager/tenants" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
          </div>
        </form>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
