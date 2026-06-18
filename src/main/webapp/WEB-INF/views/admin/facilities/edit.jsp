<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Sửa cơ sở - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
<c:set var="activeMenu" value="hostels"/>
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
                    <c:if test="${facility.status == 'ACTIVE'}">
                        — Cơ sở đã ACTIVE: chỉ được sửa tên
                    </c:if>
                </p>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/facilities/${facility.id}/edit" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <!-- Mã cơ sở: readonly nếu ACTIVE -->
                    <div class="mb-3">
                        <label for="code" class="form-label">Mã cơ sở</label>
                        <c:choose>
                            <c:when test="${facility.status == 'ACTIVE'}">
                                <input type="text" class="form-control" id="code" name="code"
                                       value="<c:out value='${facility.code}'/>" readonly
                                       style="background:#f5f5f5;cursor:not-allowed">
                                <div class="form-text text-warning">Không thể sửa sau khi đã ACTIVE</div>
                            </c:when>
                            <c:otherwise>
                                <input type="text" class="form-control" id="code" name="code"
                                       value="<c:out value='${facility.code}'/>" maxlength="10" required>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Tên: luôn được sửa -->
                    <div class="mb-3">
                        <label for="name" class="form-label">Tên cơ sở <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" required
                               value="<c:out value='${facility.name}'/>">
                    </div>

                    <!-- Địa chỉ: readonly nếu ACTIVE -->
                    <div class="mb-3">
                        <label for="address" class="form-label">Địa chỉ</label>
                        <c:choose>
                            <c:when test="${facility.status == 'ACTIVE'}">
                                <textarea class="form-control" id="address" name="address" rows="2"
                                          readonly style="background:#f5f5f5;cursor:not-allowed"><c:out value="${facility.address}"/></textarea>
                                <div class="form-text text-warning">Không thể sửa sau khi đã ACTIVE</div>
                            </c:when>
                            <c:otherwise>
                                <textarea class="form-control" id="address" name="address" rows="2" required><c:out value="${facility.address}"/></textarea>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="floorCount" class="form-label">Số tầng tối đa</label>
                            <c:choose>
                                <c:when test="${facility.status == 'ACTIVE'}">
                                    <input type="number" class="form-control" id="floorCount" name="floorCount"
                                           value="<c:out value='${facility.floorCount}'/>" readonly
                                           style="background:#f5f5f5;cursor:not-allowed">
                                </c:when>
                                <c:otherwise>
                                    <input type="number" class="form-control" id="floorCount" name="floorCount"
                                           min="1" max="99" required value="<c:out value='${facility.floorCount}'/>">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="roomsPerFloor" class="form-label">Số phòng / tầng</label>
                            <c:choose>
                                <c:when test="${facility.status == 'ACTIVE'}">
                                    <input type="number" class="form-control" id="roomsPerFloor" name="roomsPerFloor"
                                           value="<c:out value='${facility.roomsPerFloor}'/>" readonly
                                           style="background:#f5f5f5;cursor:not-allowed">
                                </c:when>
                                <c:otherwise>
                                    <input type="number" class="form-control" id="roomsPerFloor" name="roomsPerFloor"
                                           min="1" max="99" required value="<c:out value='${facility.roomsPerFloor}'/>">
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-2">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Lưu thay đổi</button>
                        <a href="${ctx}/admin/facilities/${facility.id}" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
