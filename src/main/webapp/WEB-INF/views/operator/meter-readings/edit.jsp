<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Cập nhật chỉ số"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                 style="border-radius: var(--hms-radius-lg); margin-bottom: 1.75rem;">
                <div>
                    <h1>Cập nhật chỉ số</h1>
                    <p><c:out value="${reading.roomCode}"/> · <c:out value="${reading.facilityName}"/></p>
                </div>
                <a href="${ctx}/operator/meter-readings" class="btn-mintlify-secondary text-decoration-none"
                   style="position: relative; z-index: 1;">← Quay lại</a>
            </div>

            <div class="data-surface" style="max-width: 600px;">
                <form method="post" action="${ctx}/operator/meter-readings/${reading.roomId}/edit"
                      class="p-4" enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" style="font-weight: 500;">Chỉ số cũ (hệ thống)</label>
                        <div class="row">
                            <div class="col-6">
                                <input type="text" class="mintlify-text-input"
                                       value="<c:out value='${reading.previousElectric}'/> kWh" disabled
                                       style="background-color: var(--hms-bg-subtle); color: var(--hms-text-muted);">
                            </div>
                            <div class="col-6">
                                <input type="text" class="mintlify-text-input"
                                       value="<c:out value='${reading.previousWater}'/> m³" disabled
                                       style="background-color: var(--hms-bg-subtle); color: var(--hms-text-muted);">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" style="font-weight: 500;">Chỉ số mới <span class="text-danger">*</span></label>
                        <div class="row">
                            <div class="col-6">
                                <label class="form-label" style="font-size: 12px; color: var(--color-steel);">Điện (kWh)</label>
                                <input type="number" class="mintlify-text-input" name="electricNew"
                                       step="0.1" min="${reading.previousElectric}" required>
                            </div>
                            <div class="col-6">
                                <label class="form-label" style="font-size: 12px; color: var(--color-steel);">Nước (m³)</label>
                                <input type="number" class="mintlify-text-input" name="waterNew"
                                       step="0.1" min="${reading.previousWater}" required>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" style="font-weight: 500;">Ảnh xác thực <span class="text-danger">*</span></label>
                        <div class="row">
                            <div class="col-6 mb-2">
                                <label class="form-label" style="font-size: 12px; color: var(--color-steel);">Ảnh công tơ điện</label>
                                <input type="file" class="form-control" name="electricImage" accept="image/*" required>
                            </div>
                            <div class="col-6 mb-2">
                                <label class="form-label" style="font-size: 12px; color: var(--color-steel);">Ảnh công tơ nước</label>
                                <input type="file" class="form-control" name="waterImage" accept="image/*" required>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4 pt-3 border-top" style="border-color: var(--color-hairline-soft) !important;">
                        <button type="submit" class="mintlify-btn-primary border-0">Hoàn tất</button>
                        <a href="${ctx}/operator/meter-readings" class="mintlify-btn-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
