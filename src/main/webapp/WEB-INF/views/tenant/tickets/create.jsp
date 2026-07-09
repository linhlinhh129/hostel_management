<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Gửi yêu cầu - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1>Gửi yêu cầu hỗ trợ</h1>
                        <p>Báo cáo sự cố hoặc yêu cầu hỗ trợ đến Ban quản lý</p>
                    </div>
                    <div>
                        <a href="${ctx}/tenant/tickets" class="btn-mintlify-secondary text-decoration-none">
                            ← Hủy
                        </a>
                    </div>
                </div>
            </div>

            <div class="widget-surface" style="max-width: 800px; margin: 0 auto;">
                <div class="widget-surface-header">
                    <h3>Thông tin yêu cầu</h3>
                </div>
                <div class="widget-surface-body">
                    <form method="post" action="${ctx}/tenant/tickets/create" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                        <%-- Thông tin phòng auto-fill --%>
                        <c:if test="${not empty room}">
                            <div class="alert alert-info mb-4" style="font-size:0.875rem">
                                Yêu cầu sẽ được gửi từ phòng <strong><c:out value="${room.code}"/></strong>
                            </div>
                        </c:if>



                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold">
                                    Loại yêu cầu <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" name="category" required>
                                    <option value="">-- Chọn loại --</option>
                                    <option value="ELECTRIC">⚡ Điện</option>
                                    <option value="WATER">💧 Nước</option>
                                    <option value="INTERNET">🌐 Internet</option>
                                    <option value="INFRASTRUCTURE">🏢 Cơ sở vật chất</option>
                                    <option value="OTHER">💬 Khác</option>
                                </select>
                            </div>
                            
                            <div class="col-md-6">
                                <label class="form-label fw-bold">
                                    Gửi đến (Người phụ trách) <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" name="assignedStaffId" required>
                                    <option value="">-- Chọn người nhận --</option>
                                    <c:forEach var="staff" items="${staffUsers}">
                                        <option value="${staff.id}">
                                            ${staff.fullName} - 
                                            <c:choose>
                                                <c:when test="${staff.role == 'MANAGER'}">Quản lý</c:when>
                                                <c:otherwise>Nhân viên kỹ thuật</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">
                                Tiêu đề <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" name="title"
                                   required placeholder="Mô tả ngắn gọn vấn đề...">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold">
                                Nội dung chi tiết <span class="text-danger">*</span>
                            </label>
                            <textarea class="form-control" name="content" rows="6"
                                      required placeholder="Mô tả chi tiết tình trạng, vị trí, thời gian xảy ra..."
                                      maxlength="2000"></textarea>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold">Hình ảnh đính kèm</label>
                            <input type="file" class="form-control" name="attachment" accept="image/*">
                            <div class="form-text text-muted mt-1">Định dạng hỗ trợ: JPG, PNG, GIF (Tối đa 10MB)</div>
                        </div>

                        <div class="d-flex gap-2 justify-content-end pt-3 border-top">
                            <a href="${ctx}/tenant/tickets" class="btn-mintlify-secondary text-decoration-none">Hủy bỏ</a>
                            <button type="submit" class="btn btn-mintlify-primary">
                                Gửi yêu cầu
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
