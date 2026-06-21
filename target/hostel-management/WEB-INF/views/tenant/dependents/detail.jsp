<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết người phụ thuộc"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="tenants"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell tenant-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1><c:out value="${dependent.fullName}"/></h1></div>
<div class="tenant-card"><p><strong>Ngày sinh:</strong> <c:out value="${dependent.dobLabel}"/></p><p><strong>SĐT:</strong> <c:out value="${dependent.phone}"/></p><p><strong>CCCD:</strong> <c:out value="${dependent.maskedIdentityNumber}"/></p><p><strong>Quan hệ:</strong> <c:out value="${dependent.relationship}"/></p></div>
<a href="${ctx}/tenant/dependents" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
</main></div><jsp:include page="/WEB-INF/views/layout/tenant-bottom-nav.jsp"/></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
