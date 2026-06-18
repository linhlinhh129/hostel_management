<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thêm cơ sở - Admin"/>
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
                <h1>Thêm cơ sở mới</h1>
                <p>Tạo cơ sở nhà trọ — trạng thái mặc định là DRAFT, kích hoạt để sinh phòng tự động</p>
            </div>

            <div class="data-surface" style="max-width:720px">
                <form method="post" action="${ctx}/admin/facilities/create" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <h2 class="h6 mb-3">Thông tin cơ sở</h2>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="code" class="form-label">Mã cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="code" name="code"
                                   placeholder="VD: HL, MD, HT" maxlength="10" required
                                   value="<c:out value='${dto.code}'/>">
                            <div class="form-text">Chỉ chữ cái A-Z, hệ thống tự chuyển in hoa</div>
                        </div>
                        <div class="col-md-8 mb-3">
                            <label for="name" class="form-label">Tên cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" required
                                   value="<c:out value='${dto.name}'/>">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="address" class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="address" name="address" rows="2" required><c:out value="${dto.address}"/></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="floorCount" class="form-label">Số tầng tối đa <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="floorCount" name="floorCount"
                                   min="1" max="99" required value="<c:out value='${dto.floorCount}'/>">
                            <div class="form-text">1 – 99 tầng</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="roomsPerFloor" class="form-label">Số phòng tối đa / tầng <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="roomsPerFloor" name="roomsPerFloor"
                                   min="1" max="99" required value="<c:out value='${dto.roomsPerFloor}'/>">
                            <div class="form-text">1 – 99 phòng</div>
                        </div>
                    </div>

                    <div class="alert alert-info" style="font-size:0.875rem;border-radius:8px">
                        <strong>Lưu ý:</strong> Sau khi tạo, cơ sở ở trạng thái <strong>DRAFT</strong>.
                        Vào trang chi tiết và nhấn <em>Kích hoạt</em> để hệ thống tự động sinh danh sách phòng.
                    </div>

                    <div class="d-flex gap-2 mt-2">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Tạo cơ sở</button>
                        <a href="${ctx}/admin/facilities" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
