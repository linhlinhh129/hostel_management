<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thêm nhân sự - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
<c:set var="activeMenu" value="personnel"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Thêm nhân sự</h1>
                <p>Tạo tài khoản nhân sự mới — mật khẩu tạm thời sẽ được gửi qua email</p>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/personnel/create" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>



                    <h2 class="h6 mb-3">Thông tin cá nhân</h2>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName" required
                                   value="<c:out value='${dto.fullName}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="dob" class="form-label">Ngày sinh</label>
                            <input type="date" class="form-control" id="dob" name="dob"
                                   value="<c:out value='${dto.dob}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="identityNumber" class="form-label">CCCD <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="identityNumber" name="identityNumber"
                                   placeholder="12 chữ số" maxlength="12"
                                   value="<c:out value='${dto.identityNumber}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="gender" class="form-label">Giới tính</label>
                            <select class="form-select" id="gender" name="gender">
                                <option value="">Chọn giới tính</option>
                                <option value="MALE"   ${dto.gender == 'MALE'   ? 'selected' : ''}>Nam</option>
                                <option value="FEMALE" ${dto.gender == 'FEMALE' ? 'selected' : ''}>Nữ</option>
                                <option value="OTHER"  ${dto.gender == 'OTHER'  ? 'selected' : ''}>Khác</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   placeholder="0901234567" maxlength="10"
                                   value="<c:out value='${dto.phone}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="<c:out value='${dto.email}'/>">
                            <div class="form-text">Mật khẩu tạm thời sẽ gửi về địa chỉ này</div>
                        </div>
                        <div class="col-md-12 mb-3">
                            <label for="permanentAddress" class="form-label">Địa chỉ thường trú</label>
                            <input type="text" class="form-control" id="permanentAddress" name="permanentAddress"
                                   placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành"
                                   value="<c:out value='${dto.permanentAddress}'/>">
                        </div>
                    </div>

                    <h2 class="h6 mb-3 mt-2">Vai trò & Cơ sở</h2>
                    <div class="mb-3">
                        <label for="role" class="form-label">Vai trò <span class="text-danger">*</span></label>
                        <select class="form-select" id="role" name="role" required
                                onchange="toggleFacilitySection(this.value)">
                            <option value="">Chọn vai trò</option>
                            <%-- Admin.md §9: chỉ có Ban Quản Lý và Nhân Viên vận hành --%>
                            <option value="MANAGER"  ${dto.role == 'MANAGER'  ? 'selected' : ''}>Ban Quản lý</option>
                            <option value="OPERATOR" ${dto.role == 'OPERATOR' ? 'selected' : ''}>Nhân viên vận hành</option>
                        </select>
                    </div>

                    <div id="facilitySection" style="display:none">
                        <div class="mb-3">
                            <label for="facilityId" class="form-label">
                                Cơ sở phụ trách <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="facilityId" name="facilityId">
                                <option value="">— Chọn cơ sở —</option>
                            </select>
                            <div id="noFacilityWarning" class="form-text text-warning" style="display:none">
                                Tất cả cơ sở đã được phân công cho vai trò này.
                                <a href="${ctx}/admin/facilities">Quản lý cơ sở →</a>
                            </div>
                            <div class="form-text">Mỗi nhân sự chỉ được phân công một cơ sở duy nhất</div>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Tạo nhân sự</button>
                        <a href="${ctx}/admin/personnel" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>

                <%-- Dữ liệu cơ sở cho JS — nằm ngoài form, render thành <option> theo từng nhóm --%>
                <select id="managerFacilitiesSelect" style="display:none" aria-hidden="true">
                    <c:forEach var="fac" items="${managerFacilities}">
                        <option value="${fac.id}"><c:out value="${fac.code}"/> — <c:out value="${fac.name}"/></option>
                    </c:forEach>
                </select>
                <select id="operatorFacilitiesSelect" style="display:none" aria-hidden="true">
                    <c:forEach var="fac" items="${operatorFacilities}">
                        <option value="${fac.id}"><c:out value="${fac.code}"/> — <c:out value="${fac.name}"/></option>
                    </c:forEach>
                </select>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
function toggleFacilitySection(role) {
    var show = (role === 'MANAGER' || role === 'OPERATOR');
    document.getElementById('facilitySection').style.display = show ? 'block' : 'none';
    if (show) populateFacilities(role);
}

function populateFacilities(role) {
    var sourceId = role === 'MANAGER' ? 'managerFacilitiesSelect' : 'operatorFacilitiesSelect';
    var source   = document.getElementById(sourceId);
    var target   = document.getElementById('facilityId');
    var warning  = document.getElementById('noFacilityWarning');

    var opts = source.options;
    // Reset về option mặc định
    target.length = 0;
    target.add(new Option('— Chọn cơ sở —', ''));

    for (var i = 0; i < opts.length; i++) {
        target.add(new Option(opts[i].text, opts[i].value));
    }

    warning.style.display = opts.length === 0 ? 'block' : 'none';
}

// Init on load
toggleFacilitySection(document.getElementById('role').value);
</script>
