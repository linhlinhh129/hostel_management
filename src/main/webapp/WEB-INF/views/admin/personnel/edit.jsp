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
                <h1>Sửa nhân sự</h1>
                <p><c:out value="${employee.fullName}"/> · #<c:out value="${employee.id}"/></p>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/personnel/${employee.id}/edit" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <h2 class="h6 mb-3">Thông tin cá nhân</h2>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName" required
                                   value="<c:out value='${employee.fullName}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">Số điện thoại</label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   value="<c:out value='${employee.phone}'/>">
                        </div>
                        <div class="col-md-12 mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="<c:out value='${employee.email}'/>">
                        </div>
                    </div>

                    <h2 class="h6 mb-3 mt-2">Vai trò & Cơ sở</h2>
                    <div class="mb-3">
                        <label for="role" class="form-label">Vai trò <span class="text-danger">*</span></label>
                        <select class="form-select" id="role" name="role" required
                                onchange="toggleFacilitySection(this.value)">
                            <%-- Admin.md §9: chỉ có Ban Quản Lý và Nhân Viên vận hành --%>
                            <option value="MANAGER"  ${employee.role == 'MANAGER'  ? 'selected' : ''}>Ban Quản lý</option>
                            <option value="OPERATOR" ${employee.role == 'OPERATOR' ? 'selected' : ''}>Nhân viên vận hành</option>
                        </select>
                    </div>

                    <div id="facilitySection">
                        <div class="mb-3">
                            <label class="form-label">Cơ sở phụ trách</label>
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach var="fac" items="${activeFacilities}">
                                    <%-- Kiểm tra xem cơ sở này đã được gán chưa --%>
                                    <c:set var="isAssigned" value="false"/>
                                    <c:forEach var="assignedId" items="${employee.facilityIds}">
                                        <c:if test="${assignedId == fac.id}">
                                            <c:set var="isAssigned" value="true"/>
                                        </c:if>
                                    </c:forEach>
                                    <div class="form-check" style="min-width:220px">
                                        <input class="form-check-input" type="checkbox"
                                               name="facilityIds" value="${fac.id}"
                                               id="fac_${fac.id}"
                                               ${isAssigned ? 'checked' : ''}>
                                        <label class="form-check-label" for="fac_${fac.id}">
                                            <c:out value="${fac.code}"/> - <c:out value="${fac.name}"/>
                                        </label>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty activeFacilities}">
                                    <p class="text-muted" style="font-size:0.875rem">
                                        Chưa có cơ sở ACTIVE nào.
                                        <a href="${ctx}/admin/facilities">Quản lý cơ sở</a>
                                    </p>
                                </c:if>
                            </div>
                            <div class="form-text" id="facilityHint">
                                Bắt buộc với vai trò Ban Quản lý và Nhân viên vận hành
                            </div>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Lưu thay đổi</button>
                        <a href="${ctx}/admin/personnel/${employee.id}" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
function toggleFacilitySection(role) {
    var hint = document.getElementById('facilityHint');
    if (hint) {
        hint.textContent = 'Bắt buộc với vai trò Ban Quản lý và Nhân viên vận hành';
    }
}
toggleFacilitySection(document.getElementById('role').value);
</script>
