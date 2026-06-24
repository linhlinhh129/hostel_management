<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1>Thông báo</h1></div>
<c:choose><c:when test="${not empty notifications}"><c:forEach var="notif" items="${notifications}">
<a href="${ctx}/tenant/notifications/${notif.id}" class="tenant-card${notif.unread ? ' unread' : ''}">
<div class="d-flex justify-content-between"><strong><c:out value="${notif.title}"/></strong><c:if test="${notif.unread}"><span class="badge-hms badge-info">Mới</span></c:if></div>
<p class="text-muted mb-0 mt-1" style="font-size:0.875rem"><c:out value="${notif.summary}"/></p>
<small class="text-muted"><c:out value="${notif.createdDateLabel}"/></small></a>
</c:forEach></c:when><c:otherwise><jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp"><jsp:param name="message" value="Không có thông báo"/></jsp:include></c:otherwise></c:choose>
</main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
