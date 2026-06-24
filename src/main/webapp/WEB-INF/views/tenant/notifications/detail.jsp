<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết thông báo"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1><c:out value="${notification.title}"/></h1><small class="text-muted"><c:out value="${notification.createdDateLabel}"/></small></div>
<div class="tenant-card"><c:out value="${notification.content}"/></div>
<a href="${ctx}/tenant/notifications" class="btn-mintlify-secondary text-decoration-none">�? Danh sách</a>
</main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
