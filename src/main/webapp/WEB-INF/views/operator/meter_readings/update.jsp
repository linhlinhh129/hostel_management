<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Cập nhật điện nước" />
    </jsp:include>
</head>
<body>
    <div class="app-shell">
        <!-- Sidebar -->
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp">
            <jsp:param name="menu" value="meter-readings-update" />
        </jsp:include>

        <div class="sidebar-overlay"></div>
        <!-- Main Content -->
        <div class="main-wrapper">
            <!-- Top Navigation -->
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

            <!-- Page Content -->
            <main class="page-content">
                <!-- Header -->
                <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
                    <h1>Cập nhật chỉ số điện nước</h1>
                    <p>Nhập mã phòng và các chỉ số điện nước mới nhất.</p>
                </div>
                <div class="d-flex justify-content-start mb-4">
                    <a href="${ctx}/operator/meter-readings" class="mintlify-btn-secondary" style="text-decoration: none;">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-1"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
                        Quay lại danh sách
                    </a>
                </div>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger" style="background-color: #fff2f0; color: #d32f2f; border: 1px solid #ffcdd2; border-radius: 8px; font-size: 14px;">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-2" style="vertical-align: text-bottom;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                        ${sessionScope.error}
                    </div>
                    <c:remove var="error" scope="session" />
                </c:if>

                <div class="mintlify-card-base">
                    <div class="card-body p-4">
                        <form action="${ctx}/operator/meter-readings/update?csrfToken=${csrfToken}" method="POST" enctype="multipart/form-data">
                            
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Mã phòng <span class="text-danger">*</span></label>
                                    <input type="text" name="roomCode" class="mintlify-text-input shadow-sm" required placeholder="Ví dụ: P101" value="${not empty roomCode ? roomCode : param.roomCode}">
                                    <div style="font-size: 12px; color: var(--color-steel); margin-top: 6px;">
                                        Hệ thống sẽ tự động tra cứu chỉ số cũ dựa trên mã phòng này.
                                    </div>
                                </div>
                            </div>

                            <hr style="border-color: var(--color-hairline-soft); margin: 24px 0;">

                            <h5 style="font-weight: 600; font-size: 16px; margin-bottom: 20px; color: var(--color-ink);">Chỉ số Điện</h5>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Số điện mới (kWh) <span class="text-danger">*</span></label>
                                    <input type="number" name="newElectric" class="mintlify-text-input shadow-sm" required min="0" placeholder="Nhập số điện mới">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Ảnh minh chứng công tơ điện <span class="text-danger">*</span></label>
                                    <input type="file" name="electricMeterImage" class="mintlify-file-input shadow-sm" accept="image/jpeg, image/png, image/jpg" required>
                                </div>
                            </div>

                            <hr style="border-color: var(--color-hairline-soft); margin: 24px 0;">

                            <h5 style="font-weight: 600; font-size: 16px; margin-bottom: 20px; color: var(--color-ink);">Chỉ số Nước</h5>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Số nước mới (m³) <span class="text-danger">*</span></label>
                                    <input type="number" name="newWater" class="mintlify-text-input shadow-sm" required min="0" placeholder="Nhập số nước mới">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Ảnh minh chứng công tơ nước <span class="text-danger">*</span></label>
                                    <input type="file" name="waterMeterImage" class="mintlify-file-input shadow-sm" accept="image/jpeg, image/png, image/jpg" required>
                                </div>
                            </div>

                            <div class="mt-4 pt-3 border-top" style="border-color: var(--color-hairline-soft) !important;">
                                <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                                    <a href="${ctx}/operator/meter-readings" class="mintlify-btn-secondary text-center text-decoration-none">Hủy bỏ</a>
                                    <button type="submit" class="mintlify-btn-primary text-center border-0">
                                        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-2"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>
                                        Lưu chỉ số
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

            </main>

            <!-- Footer -->
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>
</body>
</html>
