<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Ngư�?i phụ thuộc"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="tenants"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1>Ngư�?i phụ thuộc</h1></div>
<c:choose><c:when test="${not empty dependents}"><c:forEach var="dep" items="${dependents}">
<a href="${ctx}/tenant/dependents/${dep.id}" class="tenant-card"><div class="d-flex justify-content-between"><div><strong><c:out value="${dep.fullName}"/></strong><br><small class="text-muted"><c:out value="${dep.relationship}"/></small></div><small class="text-muted"><c:out value="${dep.dobLabel}"/></small></div></a>
</c:forEach></c:when><c:otherwise><jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp"><jsp:param name="message" value="Chưa có ngư�?i phụ thuộc"/></jsp:include></c:otherwise></c:choose>
</main></div></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
