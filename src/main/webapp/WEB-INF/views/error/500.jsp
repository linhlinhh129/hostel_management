<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="500 - Lỗi hệ thống"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page">
    <div class="auth-card text-center">
        <h1 style="font-size:3rem;color:var(--hms-danger);margin-bottom:0.5rem">500</h1>
        <h2 style="font-size:1.25rem;margin-bottom:0.75rem">Đã xảy ra lỗi</h2>
        <p class="text-muted mb-4">Hệ thống gặp sự cố. Vui lòng thử lại sau hoặc liên hệ bộ phận hỗ trợ.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-mintlify-primary text-decoration-none d-inline-block" style="width:auto;padding:0.625rem 2rem">Về trang đăng nhập</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
