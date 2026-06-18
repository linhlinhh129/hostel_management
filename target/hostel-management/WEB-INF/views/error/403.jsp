<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="403 - Không có quyền truy cập"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page">
    <div class="auth-card text-center">
        <h1 style="font-size:3rem;color:var(--hms-danger);margin-bottom:0.5rem">403</h1>
        <h2 style="font-size:1.25rem;margin-bottom:0.75rem">Không có quyền truy cập</h2>
        <p class="text-muted mb-4">Bạn không có quyền xem trang này. Vui lòng liên hệ quản trị viên nếu cần hỗ trợ.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-mintlify-primary text-decoration-none d-inline-block" style="width:auto;padding:0.625rem 2rem">Về trang đăng nhập</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
