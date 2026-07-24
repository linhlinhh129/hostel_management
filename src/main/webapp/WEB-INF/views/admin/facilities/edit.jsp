<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Sửa cơ sở - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="hostels"/>
<%-- Cờ: field có bị lock khi ACTIVE không --%>
<c:set var="isActive"   value="${facility.status == 'ACTIVE'}"/>
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
                <h1>Sửa cơ sở</h1>
                <p>
                    <c:out value="${facility.code}"/> · <c:out value="${facility.name}"/>
                    <c:if test="${isActive}"> — Cơ sở đã ACTIVE: chỉ được sửa tên</c:if>
                </p>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/facilities/${facility.id}/edit" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <%-- Mã cơ sở --%>
                    <div class="mb-3">
                        <label for="code" class="form-label">Mã cơ sở</label>
                        <input type="text" class="form-control ${isActive ? 'field-readonly' : ''}"
                               id="code" name="code"
                               value="<c:out value='${facility.code}'/>"
                               maxlength="10"
                               ${isActive ? 'readonly' : 'required'}>
                        <c:if test="${isActive}">
                            <div class="form-text text-warning">Không thể sửa sau khi đã ACTIVE</div>
                        </c:if>
                    </div>

                    <%-- Tên cơ sở: luôn được sửa --%>
                    <div class="mb-3">
                        <label for="name" class="form-label">Tên cơ sở <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" required
                               value="<c:out value='${facility.name}'/>">
                    </div>

                    <%-- Địa chỉ --%>
                    <div class="mb-3">
                        <label for="address" class="form-label">Địa chỉ</label>
                        <textarea class="form-control ${isActive ? 'field-readonly' : ''}"
                                  id="address" name="address" rows="2"
                                  ${isActive ? 'readonly' : 'required'}><c:out value="${facility.address}"/></textarea>
                        <c:if test="${isActive}">
                            <div class="form-text text-warning">Không thể sửa sau khi đã Hoạt động</div>
                        </c:if>
                    </div>

                    <div class="row">
                        <%-- Số tầng --%>
                        <div class="col-md-6 mb-3">
                            <label for="floorCount" class="form-label">Số tầng tối đa</label>
                            <input type="number"
                                   class="form-control ${isActive ? 'field-readonly' : ''}"
                                   id="floorCount" name="floorCount"
                                   value="<c:out value='${facility.floorCount}'/>"
                                   min="1" max="10"
                                   ${isActive ? 'readonly' : 'required'}>
                        </div>

                        <%-- Phòng / tầng --%>
                        <div class="col-md-6 mb-3">
                            <label for="roomsPerFloor" class="form-label">Số phòng / tầng</label>
                            <input type="number"
                                   class="form-control ${isActive ? 'field-readonly' : ''}"
                                   id="roomsPerFloor" name="roomsPerFloor"
                                   value="<c:out value='${facility.roomsPerFloor}'/>"
                                   min="1" max="30"
                                   ${isActive ? 'readonly' : 'required'}>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-2">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Lưu thay đổi</button>
                        <a href="${ctx}/admin/facilities/${facility.id}"
                           class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
