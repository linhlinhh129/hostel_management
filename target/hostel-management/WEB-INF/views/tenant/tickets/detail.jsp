<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết yêu cầu"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell tenant-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1><c:out value="${ticket.title}"/></h1><p><c:out value="${ticket.typeLabel}"/> · <span class="badge-hms ${ticket.statusBadgeClass}"><c:out value="${ticket.statusLabel}"/></span></p></div>
<div class="tenant-card"><p><c:out value="${ticket.content}"/></p><small class="text-muted">Gửi lúc: <c:out value="${ticket.createdDateLabel}"/></small><c:if test="${not empty ticket.attachmentUrls1}"><div class="mt-3"><img src="${ctx}${ticket.attachmentUrls1}" alt="Đính kèm" style="max-width: 100%; border-radius: 8px;"></div></c:if></div>
<c:if test="${not empty ticket.assignedTo}"><div class="tenant-card"><small class="text-muted">Người phụ trách: <c:out value="${ticket.assignedTo}"/></small></div></c:if>
<a href="${ctx}/tenant/tickets" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
</main></div><jsp:include page="/WEB-INF/views/layout/tenant-bottom-nav.jsp"/></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
