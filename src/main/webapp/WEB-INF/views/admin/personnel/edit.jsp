<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Sửa nhân sự - Admin"/>
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
                <h1>Chỉnh sửa nhân sự</h1>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/personnel/${user.id}/edit" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <h2 class="h6 mb-3">Thông tin cá nhân</h2>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName" required
                                   value="<c:out value='${user.fullName}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="dob" class="form-label">Ngày sinh</label>
                            <input type="date" class="form-control" id="dob" name="dob"
                                   value="<c:out value='${user.dob}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="identityNumber" class="form-label">CCCD <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="identityNumber" name="identityNumber"
                                   placeholder="12 chữ số" maxlength="12"
                                   value="<c:out value='${user.identityNumber}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="gender" class="form-label">Giới tính</label>
                            <select class="form-select" id="gender" name="gender">
                                <option value="">Chọn giới tính</option>
                                <option value="MALE"   ${user.gender == 'MALE'   ? 'selected' : ''}>Nam</option>
                                <option value="FEMALE" ${user.gender == 'FEMALE' ? 'selected' : ''}>Nữ</option>
                                <option value="OTHER"  ${user.gender == 'OTHER'  ? 'selected' : ''}>Khác</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   placeholder="0901234567" maxlength="10"
                                   value="<c:out value='${user.phone}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="<c:out value='${user.email}'/>">
                        </div>
                        <div class="col-md-12 mb-3">
                            <label for="permanentAddress" class="form-label">Địa chỉ thường trú</label>
                            <input type="text" class="form-control" id="permanentAddress" name="permanentAddress"
                                   placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành"
                                   value="<c:out value='${user.permanentAddress}'/>">
                        </div>
                    </div>

                    <h2 class="h6 mb-3 mt-2">Vai trò & Cơ sở</h2>
                    <div class="mb-3">
                        <label for="role" class="form-label">Vai trò <span class="text-danger">*</span></label>
                        <select class="form-select" id="role" name="role" required
                                onchange="toggleFacilitySection(this.value)">
                            <option value="MANAGER"  ${user.role == 'MANAGER'  ? 'selected' : ''}>Ban Quản lý</option>
                            <option value="OPERATOR" ${user.role == 'OPERATOR' ? 'selected' : ''}>Nhân viên vận hành</option>
                        </select>
                    </div>

                    <div id="facilitySection">
                        <div class="mb-3">
                            <label for="facilityId" class="form-label">
                                Cơ sở phụ trách <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="facilityId" name="facilityId">
                                <option value="">— Chọn cơ sở —</option>
                                <c:forEach var="fac" items="${availableFacilities}">
                                    <option value="${fac.id}"
                                            ${fac.id == currentFacilityId ? 'selected' : ''}>
                                        <c:out value="${fac.code}"/> — <c:out value="${fac.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${empty availableFacilities}">
                                <div class="form-text text-warning">
                                    Không có cơ sở khả dụng cho vai trò này.
                                    <a href="${ctx}/admin/facilities">Quản lý cơ sở →</a>
                                </div>
                            </c:if>
                            <div class="form-text">Mỗi nhân sự chỉ được phân công một cơ sở. Danh sách chỉ hiển thị các cơ sở ACTIVE chưa có người phụ trách.</div>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Lưu thay đổi</button>
                        <a href="${ctx}/admin/personnel/${user.id}" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
function toggleFacilitySection(role) {
    var show = (role === 'MANAGER' || role === 'OPERATOR');
    document.getElementById('facilitySection').style.display = show ? 'block' : 'none';
}
toggleFacilitySection(document.getElementById('role').value);
</script>
</body>
</html>
