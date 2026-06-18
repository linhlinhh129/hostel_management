<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Hồ sơ cá nhân"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="profile"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body><div class="app-shell tenant-shell"><jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="main-wrapper"><jsp:include page="/WEB-INF/views/layout/topbar.jsp"/><main class="page-content"><jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
<div class="page-header hero-sky-gradient"><h1>Hồ sơ cá nhân</h1></div>
<div class="tenant-card"><h5><c:out value="${profile.fullName}"/></h5>
<p class="text-muted small mb-0"><c:out value="${profile.code}"/> · <c:out value="${profile.roomCode}"/></p></div>
<div class="tenant-card"><table style="width:100%"><tr><td>Ngày sinh</td><td class="text-end"><c:out value="${profile.dobLabel}"/></td></tr>
<tr><td>SĐT</td><td class="text-end"><c:out value="${profile.phone}"/></td></tr>
<tr><td>CCCD</td><td class="text-end"><c:out value="${profile.idCardNumber}"/></td></tr>
<tr><td>Email</td><td class="text-end"><c:out value="${profile.email}"/></td></tr>
<tr><td>Phòng</td><td class="text-end"><c:out value="${profile.roomCode}"/></td></tr>
<tr><td>Hợp đồng</td><td class="text-end"><c:out value="${profile.contractStartDateLabel}"/> - <c:out value="${profile.contractEndDateLabel}"/></td></tr></table></div>
</main></div><jsp:include page="/WEB-INF/views/layout/tenant-bottom-nav.jsp"/></div><jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
