<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Chi tiết người phụ thuộc - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="dependents" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
              <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

              <div
                class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                  <h1>
                    <c:out value="${dependent.fullName}" />
                  </h1>
                  <p>Người phụ thuộc của <a href="${ctx}/manager/tenants/${dependent.tenantId}">
                      <c:out value="${dependent.tenantName}" />
                    </a></p>
                </div>
                <a href="${ctx}/manager/dependents" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
              </div>

              <div class="row g-3">

                <%-- Cột trái: Thông tin cá nhân --%>
                  <div class="col-lg-5">
                    <div class="widget-surface">
                      <div class="widget-surface-header">
                        <h3>Thông tin cá nhân</h3>
                      </div>
                      <div class="widget-surface-body">
                        <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted);width:44%">Họ tên</td>
                            <td style="padding:6px 0;font-weight:500">
                              <c:out value="${dependent.fullName}" />
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted)">Ngày sinh</td>
                            <td style="padding:6px 0">
                              <c:out value="${dependent.dob}" />
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted)">Giới tính</td>
                            <td style="padding:6px 0">
                              <c:choose>
                                <c:when test="${dependent.gender == 'MALE'}">Nam</c:when>
                                <c:when test="${dependent.gender == 'FEMALE'}">Nữ</c:when>
                                <c:otherwise>
                                  <c:out value="${dependent.gender}" />
                                </c:otherwise>
                              </c:choose>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted)">Quan hệ</td>
                            <td style="padding:6px 0">
                              <c:out value="${dependent.relationship}" />
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted)">Số điện thoại</td>
                            <td style="padding:6px 0">
                              <c:out value="${dependent.phone}" />
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;color:var(--hms-text-muted)">Số CCCD/CMND</td>
                            <td style="padding:6px 0">
                              <c:out value="${dependent.identityNumber}" />
                            </td>
                          </tr>
                        </table>
                      </div>
                    </div>
                  </div>

                  <%-- Cột phải --%>
                    <div class="col-lg-7">

                      <%-- Người thuê chính --%>
                        <div class="widget-surface mb-3">
                          <div class="widget-surface-header">
                            <h3>Người thuê chính</h3>
                          </div>
                          <div class="widget-surface-body">
                            <div class="d-flex align-items-center gap-3">
                              <div>
                                <div style="font-weight:600;font-size:1rem">
                                  <a href="${ctx}/manager/tenants/${dependent.tenantId}"
                                    style="text-decoration:none;color:var(--hms-accent-deep)">
                                    <c:out value="${dependent.tenantName}" />
                                  </a>
                                </div>
                                <div style="font-size:0.8125rem;color:var(--hms-text-muted);margin-top:2px">
                                  <c:out value="${dependent.tenantCode}" />
                                </div>
                              </div>
                              <a href="${ctx}/manager/tenants/${dependent.tenantId}"
                                class="btn-mintlify-secondary text-decoration-none ms-auto"
                                style="padding:6px 14px;font-size:0.8125rem">
                                Xem hồ sơ →
                              </a>
                            </div>
                          </div>
                        </div>

                        <%-- Cập nhật thông tin --%>
                          <div class="widget-surface mb-3">
                            <div class="widget-surface-header">
                              <h3>Cập nhật thông tin</h3>
                            </div>
                            <div class="widget-surface-body">
                              <form method="post" action="${ctx}/manager/dependents/${dependent.id}/edit">
                                <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                <div class="mb-3">
                                  <label for="edit_fullName" class="form-label">Họ tên <span
                                      class="text-danger">*</span></label>
                                  <input type="text" class="form-control" id="edit_fullName" name="fullName" required
                                    maxlength="200" value="<c:out value='${dependent.fullName}'/>">
                                </div>
                                <div class="mb-3">
                                  <label for="edit_relationship" class="form-label">Quan hệ <span
                                      class="text-danger">*</span></label>
                                  <input type="text" class="form-control" id="edit_relationship" name="relationship"
                                    required maxlength="100" value="<c:out value='${dependent.relationship}'/>">
                                </div>
                                <div class="mb-3">
                                  <label for="edit_phone" class="form-label">Số điện thoại</label>
                                  <input type="tel" class="form-control" id="edit_phone" name="phone" maxlength="20"
                                    value="<c:out value='${dependent.phone}'/>">
                                </div>
                                <div class="mb-3">
                                  <label for="edit_identityNumber" class="form-label">Số CCCD/CMND</label>
                                  <input type="text" class="form-control" id="edit_identityNumber" name="identityNumber"
                                    maxlength="50" value="<c:out value='${dependent.identityNumber}'/>"
                                    placeholder="Nhập số CCCD/CMND (tùy chọn)">
                                </div>
                                <div class="row g-3 mb-3">
                                  <div class="col-sm-6">
                                    <label for="edit_gender" class="form-label">Giới tính</label>
                                    <select class="form-select" id="edit_gender" name="gender">
                                      <option value="">-- Chọn --</option>
                                      <option value="MALE" ${dependent.gender=='MALE' ? 'selected' : '' }>Nam</option>
                                      <option value="FEMALE" ${dependent.gender=='FEMALE' ? 'selected' : '' }>Nữ
                                      </option>
                                      <option value="OTHER" ${dependent.gender=='OTHER' ? 'selected' : '' }>Khác
                                      </option>
                                    </select>
                                  </div>
                                  <div class="col-sm-6">
                                    <label for="edit_dob" class="form-label">Ngày sinh</label>
                                    <input type="date" class="form-control" id="edit_dob" name="dob"
                                      value="<c:out value='${dependent.dob}'/>">
                                  </div>
                                </div>
                                <button type="submit" class="quick-action-btn primary">
                                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2.5" style="margin-right:4px">
                                    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" />
                                    <polyline points="17 21 17 13 7 13 7 21" />
                                    <polyline points="7 3 7 8 15 8" />
                                  </svg>
                                  Lưu thay đổi
                                </button>
                              </form>
                            </div>
                          </div>

                          <%-- Xóa người phụ thuộc --%>
                            <div class="data-surface"
                              style="border-left:4px solid var(--hms-danger);padding:1rem 1.25rem">
                              <h6 style="font-weight:600;color:var(--hms-danger);margin-bottom:0.5rem">Xóa người phụ
                                thuộc</h6>
                              <p class="text-muted" style="font-size:0.8125rem;margin-bottom:0.75rem">
                                Thao tác này sẽ xóa vĩnh viễn hồ sơ người phụ thuộc này.
                              </p>
                              <form method="post" action="${ctx}/manager/dependents/${dependent.id}/remove"
                                onsubmit="return confirm('Bạn có chắc muốn xóa người phụ thuộc này?')">
                                <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                <button type="submit" class="btn btn-danger btn-sm">Xóa người phụ thuộc</button>
                              </form>
                            </div>

                    </div><%-- end col-lg-7 --%>
              </div>

            </main>
          </div>
        </div>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>