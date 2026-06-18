<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chỉ số điện nước"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1>Chỉ số điện nước</h1><p>Kỳ <c:out value="${billingPeriodLabel}"/></p></div>
<div class="data-surface"><form class="filter-bar" method="get" action="${ctx}/operator/meter-readings">
<input type="text" class="form-control" name="roomCode" placeholder="Mã phòng" value="<c:out value='${filterRoomCode}'/>">
<select class="form-select" name="status"><option value="">Trạng thái</option><option value="PENDING" ${filterStatus == 'PENDING' ? 'selected' : ''}>Chưa cập nhật</option><option value="DONE" ${filterStatus == 'DONE' ? 'selected' : ''}>Đã cập nhật</option></select>
<button type="submit" class="btn-mintlify-secondary">Lọc</button><a href="${ctx}/operator/meter-readings" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a></form>
<c:choose><c:when test="${not empty readings}"><div class="table-responsive"><table class="table-mintlify"><thead><tr><th>Phòng</th><th>Điện kỳ trước</th><th>Nước kỳ trước</th><th>Trạng thái</th><th>Cập nhật</th><th>Thao tác</th></tr></thead>
<tbody><c:forEach var="r" items="${readings}"><tr><td><c:out value="${r.roomCode}"/></td><td><c:out value="${r.previousElectric}"/> kWh</td><td><c:out value="${r.previousWater}"/> m³</td>
<td><span class="badge-hms ${r.statusBadgeClass}"><c:out value="${r.statusLabel}"/></span></td><td><c:out value="${r.updatedAtLabel}"/></td>
<td><a href="${ctx}/operator/meter-readings/${r.roomId}/edit">Cập nhật</a></td></tr>
</c:forEach></tbody></table></div></c:when>
<c:otherwise><div class="empty-state"><h4>Tất cả phòng đã được cập nhật</h4></div></c:otherwise></c:choose></div>
</main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
