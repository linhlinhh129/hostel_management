<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết người thuê - BQL"/>
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
          <h1><c:out value="${tenant.fullName}"/></h1>
          <p>Mã người thuê: <strong><c:out value="${tenant.tenantCode}"/></strong></p>
        </div>
        <a href="${ctx}/manager/tenants" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
      </div>

      <div class="row g-3">

        <!-- Cột trái: Thông tin cá nhân -->
        <div class="col-lg-5">
          <div class="widget-surface">
            <div class="widget-surface-header d-flex justify-content-between align-items-center">
              <h3>Thông tin cá nhân</h3>
              <div class="d-flex gap-2">
                <c:if test="${tenant.status == 'ACTIVE'}">
                  <button type="button" class="btn-mintlify-secondary"
                          data-bs-toggle="modal" data-bs-target="#editTenantModal"
                          style="padding:4px 12px;font-size:0.8125rem">
                    Sửa
                  </button>
                  <form method="post" action="${ctx}/manager/tenants/${tenant.id}/lock" style="display:inline; margin:0;">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <button type="submit" class="btn btn-outline-warning" style="padding:4px 12px; font-size:0.8125rem;">Khóa</button>
                  </form>
                </c:if>
                <c:if test="${tenant.status == 'LOCKED'}">
                  <form method="post" action="${ctx}/manager/tenants/${tenant.id}/unlock" style="display:inline; margin:0;">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <button type="submit" class="btn btn-outline-success" style="padding:4px 12px; font-size:0.8125rem;">Mở khóa</button>
                  </form>
                </c:if>
                <c:if test="${tenant.status == 'INACTIVE'}">
                  <form method="post" action="${ctx}/manager/tenants/${tenant.id}/delete" style="display:inline; margin:0;"
                        onsubmit="return confirm('Bạn có chắc chắn muốn xóa vĩnh viễn người thuê này khỏi danh sách quản lý?');">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <button type="submit" class="btn btn-outline-danger" style="padding:4px 12px; font-size:0.8125rem;">Xóa</button>
                  </form>
                </c:if>
              </div>
            </div>
            <div class="widget-surface-body">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr><td style="padding:6px 0;color:var(--hms-text-muted);width:44%">Họ tên</td>
                    <td style="padding:6px 0;font-weight:500"><c:out value="${tenant.fullName}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Ngày sinh</td>
                    <td style="padding:6px 0"><c:out value="${tenant.dob}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Giới tính</td>
                    <td style="padding:6px 0">
                      <c:choose>
                        <c:when test="${tenant.gender == 'MALE'}">Nam</c:when>
                        <c:when test="${tenant.gender == 'FEMALE'}">Nữ</c:when>
                        <c:otherwise><c:out value="${tenant.gender}"/></c:otherwise>
                      </c:choose>
                    </td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Số điện thoại</td>
                    <td style="padding:6px 0"><c:out value="${tenant.phone}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Email</td>
                    <td style="padding:6px 0"><c:out value="${tenant.email}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">CCCD/CMND</td>
                    <td style="padding:6px 0;font-family:monospace"><c:out value="${tenant.identityNumber}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Địa chỉ thường trú</td>
                    <td style="padding:6px 0"><c:out value="${tenant.permanentAddress}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Trạng thái</td>
                    <td style="padding:6px 0">
                      <c:choose>
                        <c:when test="${tenant.status == 'ACTIVE'}">
                          <span class="badge-hms badge-success">Đang thuê</span>
                        </c:when>
                        <c:when test="${tenant.status == 'LOCKED'}">
                          <span class="badge-hms badge-danger">Đã khóa</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge-hms badge-neutral">Ngừng thuê</span>
                        </c:otherwise>
                      </c:choose>
                    </td></tr>
              </table>
            </div>
          </div>
        </div>

        <!-- Cột phải -->
        <div class="col-lg-7">

          <!-- Thông tin thuê -->
          <div class="widget-surface mb-3">
            <div class="widget-surface-header"><h3>Thông tin thuê</h3></div>
            <div class="widget-surface-body">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr><td style="padding:6px 0;color:var(--hms-text-muted);width:44%">Phòng đang thuê</td>
                    <td style="padding:6px 0">
                      <c:choose>
                        <c:when test="${not empty tenant.roomCode}">
                          <a href="${ctx}/manager/rooms/${tenant.roomId}" style="font-weight:600">
                            <c:out value="${tenant.roomCode}"/>
                          </a>
                        </c:when>
                        <c:otherwise><em class="text-muted">Chưa gán phòng</em></c:otherwise>
                      </c:choose>
                    </td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Ngày bắt đầu thuê</td>
                    <td style="padding:6px 0"><c:out value="${tenant.contractStartDate}"/></td></tr>
                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Trạng thái</td>
                    <td style="padding:6px 0">
                      <c:choose>
                        <c:when test="${tenant.status == 'ACTIVE'}">
                          <span class="badge-hms badge-success">Đang thuê</span>
                        </c:when>
                        <c:when test="${tenant.status == 'LOCKED'}">
                          <span class="badge-hms badge-danger">Đã khóa</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge-hms badge-neutral">Ngừng thuê</span>
                        </c:otherwise>
                      </c:choose>
                    </td></tr>
              </table>
            </div>
          </div>

          <!-- Người phụ thuộc -->
          <div class="widget-surface mb-3">
            <div class="widget-surface-header">
              <h3>Người phụ thuộc
                <c:if test="${not empty dependents}">
                  <span class="badge-hms badge-neutral ms-2">${dependents.size()}</span>
                </c:if>
              </h3>
              <c:if test="${tenant.status == 'ACTIVE'}">
                <button type="button" class="btn-mintlify-secondary"
                        data-bs-toggle="modal" data-bs-target="#addDependentModal"
                        style="padding:4px 12px;font-size:0.8125rem">
                  + Thêm
                </button>
              </c:if>
            </div>
            <div class="widget-surface-body p-0">
              <c:choose>
                <c:when test="${not empty dependents}">
                  <div class="table-responsive">
                    <table class="table-mintlify">
                      <thead>
                        <tr>
                          <th>Họ tên</th>
                          <th>Quan hệ</th>
                          <th>SĐT</th>
                          <th>Thao tác</th>
                        </tr>
                      </thead>
                      <tbody>
                        <c:forEach var="dep" items="${dependents}">
                          <tr>
                            <td>
                              <%-- Tên phải click được theo Manager.md §6 --%>
                              <a href="javascript:void(0)"
                                 onclick="openDependentDetail(${dep.id}, '<c:out value="${dep.fullName}"/>', '<c:out value="${dep.relationship}"/>', '<c:out value="${dep.phone}"/>', '<c:out value="${dep.gender}"/>', '<c:out value="${dep.dob}"/>')"
                                 style="font-weight:500">
                                <c:out value="${dep.fullName}"/>
                              </a>
                            </td>
                            <td><c:out value="${dep.relationship}"/></td>
                            <td><c:out value="${dep.phone}"/></td>
                            <td>
                              <button type="button"
                                      class="btn-mintlify-secondary"
                                      style="padding:3px 10px;font-size:0.8125rem"
                                      onclick="openDependentDetail(${dep.id}, '<c:out value="${dep.fullName}"/>', '<c:out value="${dep.relationship}"/>', '<c:out value="${dep.phone}"/>', '<c:out value="${dep.gender}"/>', '<c:out value="${dep.dob}"/>')">
                                Chi tiết
                              </button>
                            </td>
                          </tr>
                        </c:forEach>
                      </tbody>
                    </table>
                  </div>

                </c:when>
                <c:otherwise>
                  <div class="p-3 text-center text-muted" style="font-size:0.875rem">
                    Chưa có người phụ thuộc nào.
                    <c:if test="${tenant.status == 'ACTIVE'}">
                      <br>
                      <button type="button" class="btn-mintlify-secondary mt-2"
                              data-bs-toggle="modal" data-bs-target="#addDependentModal"
                              style="padding:6px 16px;font-size:0.8125rem">
                        + Thêm người phụ thuộc đầu tiên
                      </button>
                    </c:if>
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <!-- Hợp đồng -->
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Hợp đồng</h3></div>
            <div class="widget-surface-body">
              <a href="${ctx}/manager/contracts?roomId=${tenant.roomId}" class="btn-mintlify-secondary text-decoration-none" style="display:inline-flex;align-items:center;gap:6px">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
                Xem hợp đồng của phòng
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- Kết thúc thuê — chỉ hiện khi ACTIVE -->
      <c:if test="${tenant.status == 'ACTIVE'}">
        <div id="end-rental" class="data-surface mt-4"
             style="border-left:4px solid #f59e0b;padding:1.25rem 1.5rem">
          <div class="d-flex align-items-center gap-2 mb-2">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#f59e0b" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <h5 style="margin:0;font-weight:600;color:var(--hms-ink)">Kết thúc thuê</h5>
            <span class="badge-hms badge-warning">Không thể hoàn tác</span>
          </div>
          <p class="text-muted" style="font-size:0.875rem;margin-bottom:1rem">
            Hành động này sẽ giải phóng phòng và vô hiệu hóa tài khoản đăng nhập của người thuê. Dữ liệu lịch sử sẽ được giữ lại.
          </p>
          <form method="post" action="${ctx}/manager/tenants/${tenant.id}/end-rental"
                onsubmit="return confirm('Bạn có chắc muốn kết thúc hợp đồng thuê của ${tenant.fullName}? Thao tác này không thể hoàn tác.')">
            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
            <div class="row g-3">
              <div class="col-sm-4">
                <label for="endDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                <input type="date" class="form-control" id="endDate" name="endDate" required>
              </div>
              <div class="col-sm-8">
                <label for="endReason" class="form-label">Lý do</label>
                <input type="text" class="form-control" id="endReason" name="reason"
                       maxlength="500" placeholder="Ghi chú lý do kết thúc thuê (tuỳ chọn)">
              </div>
            </div>
            <div class="mt-3">
              <button type="submit" class="btn btn-warning fw-semibold">
                Xác nhận kết thúc thuê
              </button>
            </div>
          </form>
        </div>
      </c:if>

    </main>
  </div>
</div>

<!-- Modal Thêm người phụ thuộc -->
<div class="modal fade" id="addDependentModal" tabindex="-1"
     aria-labelledby="addDependentLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="addDependentLabel">Thêm người phụ thuộc</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
      </div>
      <form method="post" action="${ctx}/manager/tenants/${tenant.id}/dependents/add">
        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
        <div class="modal-body">
          <div class="mb-3">
            <label for="dep_fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="dep_fullName" name="fullName"
                   required maxlength="200">
          </div>
          <div class="mb-3">
            <label for="dep_relationship" class="form-label">Quan hệ <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="dep_relationship" name="relationship"
                   required maxlength="100" placeholder="VD: Con, Vợ/Chồng, Cha/Mẹ...">
          </div>
          <div class="mb-3">
            <label for="dep_phone" class="form-label">Số điện thoại</label>
            <input type="tel" class="form-control" id="dep_phone" name="phone" maxlength="20">
          </div>
          <div class="row g-3">
            <div class="col-sm-6">
              <label for="dep_gender" class="form-label">Giới tính</label>
              <select class="form-select" id="dep_gender" name="gender">
                <option value="">-- Chọn --</option>
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </div>
            <div class="col-sm-6">
              <label for="dep_dob" class="form-label">Ngày sinh</label>
              <input type="date" class="form-control" id="dep_dob" name="dob">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="quick-action-btn primary">Thêm người phụ thuộc</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Sửa thông tin Tenant -->
<div class="modal fade" id="editTenantModal" tabindex="-1"
     aria-labelledby="editTenantLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editTenantLabel">Sửa thông tin cá nhân</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
      </div>
      <form method="post" action="${ctx}/manager/tenants/${tenant.id}/edit">
        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
        <div class="modal-body">
          <div class="mb-3">
            <label for="tenant_fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="tenant_fullName" name="fullName"
                   required maxlength="100" value="<c:out value='${tenant.fullName}'/>">
          </div>
          <div class="mb-3">
            <label for="tenant_email" class="form-label">Email (Tên đăng nhập) <span class="text-danger">*</span></label>
            <input type="email" class="form-control" id="tenant_email" name="email"
                   required maxlength="100" value="<c:out value='${tenant.email}'/>">
          </div>
          <div class="mb-3">
            <label for="tenant_phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
            <input type="tel" class="form-control" id="tenant_phone" name="phone"
                   required maxlength="20" value="<c:out value='${tenant.phone}'/>"
                   pattern="^0[0-9]{9}$" title="Số điện thoại phải gồm 10 chữ số bắt đầu bằng số 0.">
          </div>
          <div class="mb-3">
            <label for="tenant_identityNumber" class="form-label">CCCD/CMND <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="tenant_identityNumber" name="identityNumber"
                   required maxlength="50" value="<c:out value='${tenant.identityNumber}'/>"
                   pattern="^([0-9]{9}|[0-9]{12})$" title="Số CCCD/CMND phải là 9 hoặc 12 chữ số.">
          </div>
          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="tenant_gender" class="form-label">Giới tính</label>
              <select class="form-select" id="tenant_gender" name="gender">
                <option value="">-- Chọn --</option>
                <option value="MALE" <c:if test="${tenant.gender == 'MALE'}">selected</c:if>>Nam</option>
                <option value="FEMALE" <c:if test="${tenant.gender == 'FEMALE'}">selected</c:if>>Nữ</option>
                <option value="OTHER" <c:if test="${tenant.gender == 'OTHER'}">selected</c:if>>Khác</option>
              </select>
            </div>
            <div class="col-sm-6">
              <label for="tenant_dob" class="form-label">Ngày sinh</label>
              <input type="date" class="form-control" id="tenant_dob" name="dob" value="<c:out value='${tenant.dob}'/>">
            </div>
          </div>
          <div class="mb-3">
            <label for="tenant_permanentAddress" class="form-label">Địa chỉ thường trú</label>
            <input type="text" class="form-control" id="tenant_permanentAddress" name="permanentAddress"
                   maxlength="500" value="<c:out value='${tenant.permanentAddress}'/>">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="quick-action-btn primary">Lưu thay đổi</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal Chi tiết người phụ thuộc -->
<div class="modal fade" id="dependentDetailModal" tabindex="-1"
     aria-labelledby="dependentDetailLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="dependentDetailLabel">Chi tiết người phụ thuộc</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
      </div>
      <div class="modal-body">
        <table class="table table-borderless" style="font-size:0.875rem; margin-bottom: 0;">
          <tr>
            <td style="color:var(--hms-text-muted); width: 35%; padding: 6px 0;">Họ tên:</td>
            <td style="font-weight: 600; padding: 6px 0;" id="view_dep_fullName"></td>
          </tr>
          <tr>
            <td style="color:var(--hms-text-muted); padding: 6px 0;">Quan hệ:</td>
            <td style="padding: 6px 0;" id="view_dep_relationship"></td>
          </tr>
          <tr>
            <td style="color:var(--hms-text-muted); padding: 6px 0;">Ngày sinh:</td>
            <td style="padding: 6px 0;" id="view_dep_dob"></td>
          </tr>
          <tr>
            <td style="color:var(--hms-text-muted); padding: 6px 0;">Giới tính:</td>
            <td style="padding: 6px 0;" id="view_dep_gender"></td>
          </tr>
          <tr>
            <td style="color:var(--hms-text-muted); padding: 6px 0;">Số điện thoại:</td>
            <td style="padding: 6px 0;" id="view_dep_phone"></td>
          </tr>
        </table>
      </div>
      <div class="modal-footer d-flex justify-content-between">
        <div>
          <c:if test="${tenant.status == 'ACTIVE'}">
            <button type="button" class="btn btn-outline-primary"
                    onclick="triggerEditFromDetail()">
              Sửa
            </button>
            <form id="view_dep_delete_form" method="post" action="" style="display:inline; margin:0 0 0 6px;"
                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa người phụ thuộc này?');">
              <input type="hidden" name="csrfToken" value="${csrfToken}"/>
              <button type="submit" class="btn btn-outline-danger">Xóa</button>
            </form>
          </c:if>
        </div>
        <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal" style="margin:0;">Đóng</button>
      </div>
    </div>
  </div>
</div>

<!-- Modal Sửa người phụ thuộc -->
<div class="modal fade" id="editDependentModal" tabindex="-1"
     aria-labelledby="editDependentLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editDependentLabel">Sửa thông tin người phụ thuộc</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
      </div>
      <form id="editDependentForm" method="post" action="">
        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
        <div class="modal-body">
          <div class="mb-3">
            <label for="edit_dep_fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="edit_dep_fullName" name="fullName"
                   required maxlength="200">
          </div>
          <div class="mb-3">
            <label for="edit_dep_relationship" class="form-label">Quan hệ <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="edit_dep_relationship" name="relationship"
                   required maxlength="100" placeholder="VD: Con, Vợ/Chồng, Cha/Mẹ...">
          </div>
          <div class="mb-3">
            <label for="edit_dep_phone" class="form-label">Số điện thoại</label>
            <input type="tel" class="form-control" id="edit_dep_phone" name="phone" maxlength="20">
          </div>
          <div class="row g-3">
            <div class="col-sm-6">
              <label for="edit_dep_gender" class="form-label">Giới tính</label>
              <select class="form-select" id="edit_dep_gender" name="gender">
                <option value="">-- Chọn --</option>
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </div>
            <div class="col-sm-6">
              <label for="edit_dep_dob" class="form-label">Ngày sinh</label>
              <input type="date" class="form-control" id="edit_dep_dob" name="dob">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="quick-action-btn primary">Lưu thay đổi</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
let currentDep = {};

function openDependentDetail(id, fullName, relationship, phone, gender, dob) {
    currentDep = { id, fullName, relationship, phone, gender, dob };
    
    document.getElementById('view_dep_fullName').innerText = fullName;
    document.getElementById('view_dep_relationship').innerText = relationship;
    document.getElementById('view_dep_dob').innerText = dob || '—';
    
    let genderText = '—';
    if (gender === 'MALE') genderText = 'Nam';
    else if (gender === 'FEMALE') genderText = 'Nữ';
    else if (gender === 'OTHER') genderText = 'Khác';
    document.getElementById('view_dep_gender').innerText = genderText;
    
    document.getElementById('view_dep_phone').innerText = phone || '—';
    
    // Delete form action
    const deleteForm = document.getElementById('view_dep_delete_form');
    if (deleteForm) {
        deleteForm.action = '${ctx}/manager/dependents/' + id + '/remove';
    }
    
    // Open detail modal
    const detailModal = new bootstrap.Modal(document.getElementById('dependentDetailModal'));
    detailModal.show();
}

function triggerEditFromDetail() {
    // Close detail modal
    const detailModalEl = document.getElementById('dependentDetailModal');
    const modalInstance = bootstrap.Modal.getInstance(detailModalEl);
    if (modalInstance) {
        modalInstance.hide();
    }
    
    // Setup edit form
    openEditDependent(currentDep.id, currentDep.fullName, currentDep.relationship, currentDep.phone, currentDep.gender, currentDep.dob);
    
    // Open edit modal
    const editModal = new bootstrap.Modal(document.getElementById('editDependentModal'));
    editModal.show();
}

function openEditDependent(id, fullName, relationship, phone, gender, dob) {
    document.getElementById('edit_dep_fullName').value = fullName;
    document.getElementById('edit_dep_relationship').value = relationship;
    document.getElementById('edit_dep_phone').value = phone;
    document.getElementById('edit_dep_gender').value = gender;
    document.getElementById('edit_dep_dob').value = dob;
    
    // Set form action dynamically
    document.getElementById('editDependentForm').action = '${ctx}/manager/dependents/' + id + '/edit?tenantId=${tenant.id}';
}
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
