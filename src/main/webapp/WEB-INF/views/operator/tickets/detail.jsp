<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết yêu cầu"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header d-flex justify-content-between align-items-start"><div><h1><c:out value="${ticket.title}"/></h1><p><c:out value="${ticket.code}"/> · <c:out value="${ticket.typeLabel}"/></p></div><a href="${ctx}/operator/tickets" class="quick-action-btn">← Danh sách</a></div>
<div class="widget-surface-surface-grid"><div class="widget-surface-surface widget-surface-surface-span-7"><div class="widget-surface-surface-header"><h3>Chi tiết</h3></div><div class="widget-surface-surface-body"><p><c:out value="${ticket.content}"/></p>
<c:if test="${not empty ticket.imageUrl}"><img src="${ctx}/uploads/<c:out value='${ticket.imageUrl}'/>" class="img-fluid  mt-2" style="max-height:300px"></c:if></div></div>
<div class="widget-surface-surface widget-surface-surface-span-5"><div class="widget-surface-surface-header"><h3>Thông tin xử lý</h3></div><div class="widget-surface-surface-body">
<p><strong>Phòng:</strong> <c:out value="${ticket.roomCode}"/></p><p><strong>Người gửi:</strong> <c:out value="${ticket.senderName}"/></p><p><strong>Ngày gửi:</strong> <c:out value="${ticket.createdDateLabel}"/></p>
<p><strong>Trạng thái:</strong> <span class="badge-hms ${ticket.statusBadgeClass}"><c:out value="${ticket.statusLabel}"/></span></p>
<form method="post" action="${ctx}/operator/tickets/${ticket.id}/update" class="mt-3">
<input type="hidden" name="csrfToken" value="${csrfToken}"/>
<c:if test="${ticket.status == 'NEW'}"><button type="submit" name="status" value="IN_PROGRESS" class="btn btn-mintlify-primary w-100 mb-2" style="width:auto">Tiếp nhận</button></c:if>
<c:if test="${ticket.status == 'IN_PROGRESS'}"><div class="mb-2"><label class="form-label">Ngày hẹn sửa</label><input type="date" class="form-control" name="scheduledDate"></div>
<div class="mb-2"><label class="form-label">Kết quả xử lý</label><textarea class="form-control" name="resolution" rows="2"></textarea></div>
<button type="submit" name="status" value="DONE" class="btn btn-mintlify-primary w-100 mb-2" style="width:auto">Hoàn thành</button></c:if>
<c:if test="${ticket.status == 'NEW' || ticket.status == 'IN_PROGRESS'}"><button type="submit" name="status" value="REJECTED" class="btn-mintlify-secondary w-100">Từ chối</button></c:if>
</form></div></div></div></main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
