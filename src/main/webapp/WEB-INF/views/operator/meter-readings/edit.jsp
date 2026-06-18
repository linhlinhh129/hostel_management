<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Cập nhật chỉ số"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1>Cập nhật chỉ số</h1><p><c:out value="${reading.roomCode}"/> · <c:out value="${reading.facilityName}"/></p></div>
<div class="data-surface" style="max-width:600px"><form method="post" action="${ctx}/operator/meter-readings/${reading.roomId}/edit" class="p-4" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${csrfToken}"/>
<div class="mb-3"><label class="form-label">Chỉ số cũ (hệ thống)</label>
<div class="row"><div class="col-6"><input type="text" class="form-control" value="<c:out value="${reading.previousElectric}"/> kWh" disabled></div>
<div class="col-6"><input type="text" class="form-control" value="<c:out value="${reading.previousWater}"/> m³" disabled></div></div></div>
<div class="mb-3"><label class="form-label">Chỉ số mới <span class="text-danger">*</span></label>
<div class="row"><div class="col-6"><div class="form-label small">Điện (kWh)</div><input type="number" class="form-control" name="electricNew" step="0.1" min="${reading.previousElectric}" required></div>
<div class="col-6"><div class="form-label small">Nước (m³)</div><input type="number" class="form-control" name="waterNew" step="0.1" min="${reading.previousWater}" required></div></div></div>
<div class="mb-3"><label class="form-label">Ảnh xác thực <span class="text-danger">*</span></label>
<div class="row"><div class="col-6 mb-2"><label class="form-label small">Ảnh công tơ điện</label><input type="file" class="form-control" name="electricImage" accept="image/*" required></div>
<div class="col-6 mb-2"><label class="form-label small">Ảnh công tơ nước</label><input type="file" class="form-control" name="waterImage" accept="image/*" required></div></div></div>
<div class="d-flex gap-2 mt-3"><button type="submit" class="btn btn-mintlify-primary" style="width:auto">Hoàn tất</button><a href="${ctx}/operator/meter-readings" class="btn-mintlify-secondary text-decoration-none">Hủy</a></div>
</form></div></main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
