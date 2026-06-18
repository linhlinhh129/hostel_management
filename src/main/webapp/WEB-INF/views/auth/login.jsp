<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng nhập - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout">
        <div class="auth-form-side">
            <div class="auth-card">
                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-icon mb-3" style="width:48px;height:48px;font-size:1.25rem;">HT</div>
                    <h1 style="font-size:2rem;font-weight:600;letter-spacing:-1px;margin-bottom:0.5rem;">Quản lý Nhà trọ</h1>
                    <p style="color:var(--hms-text-muted);font-size:1rem;">Hệ thống quản lý thông minh</p>
                </div>

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                </div>

                <form action="${ctx}/login" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <div class="mb-4">
                        <label for="username" class="form-label-modern">Tên đăng nhập</label>
                        <input type="text" class="form-control" id="username" name="username"
                               value="<c:out value='${username}'/>"
                               placeholder="Nhập tên đăng nhập" required autocomplete="username">
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label-modern">Mật khẩu</label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Nhập mật khẩu" required autocomplete="current-password">
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-4 auth-stagger-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="remember" name="remember">
                            <label class="form-check-label text-muted" for="remember" style="font-size:0.875rem">Ghi nhớ đăng nhập</label>
                        </div>
                        <a href="${ctx}/forgot-password" class="text-decoration-none" style="font-size:0.875rem;color:var(--hms-accent);font-weight:500;">Quên mật khẩu?</a>
                    </div>
                    <button type="submit" class="btn btn-mintlify-primary w-100 py-2 mb-3 auth-stagger-4" style="font-size:1rem;">Đăng nhập</button>
                </form>
            </div>
        </div>
        <div class="auth-banner-side">
            <img src="${ctx}/assets/img/login-illustration.png" alt="Hostel Management Illustration" class="auth-illustration">
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
