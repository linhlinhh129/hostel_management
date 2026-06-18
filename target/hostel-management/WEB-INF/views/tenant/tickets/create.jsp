<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Gửi yêu cầu - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell tenant-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Gửi yêu cầu hỗ trợ</h1>
                <p>Báo cáo sự cố hoặc yêu cầu hỗ trợ đến Ban quản lý</p>
            </div>

            <div class="data-surface" style="max-width:640px">
                <form method="post" action="${ctx}/tenant/tickets/create" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <%-- Thông tin phòng auto-fill --%>
                    <c:if test="${not empty room}">
                        <div class="alert alert-info mb-3" style="font-size:0.8125rem">
                            Yêu cầu sẽ được gửi từ phòng <strong><c:out value="${room.code}"/></strong>
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label">
                            Loại yêu cầu <span class="text-danger">*</span>
                        </label>
                        <select class="form-select" name="category" required>
                            <option value="">-- Chọn loại --</option>
                            <option value="ELECTRIC">⚡ Điện</option>
                            <option value="WATER">💧 Nước</option>
                            <option value="INTERNET">🌐 Internet</option>
                            <option value="INFRASTRUCTURE">🏗 Cơ sở vật chất</option>
                            <option value="OTHER">📌 Khác</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">
                            Tiêu đề <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" name="title"
                               required placeholder="Mô tả ngắn gọn vấn đề...">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">
                            Nội dung chi tiết <span class="text-danger">*</span>
                        </label>
                        <textarea class="form-control" name="content" rows="5"
                                  required placeholder="Mô tả chi tiết tình trạng, vị trí, thời gian xảy ra..."
                                  maxlength="2000"></textarea>
                    </div>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">
                            Gửi yêu cầu
                        </button>
                        <a href="${ctx}/tenant/tickets"
                           class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
    <jsp:include page="/WEB-INF/views/layout/tenant-bottom-nav.jsp"/>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
