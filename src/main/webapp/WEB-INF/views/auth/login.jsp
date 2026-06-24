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
                               placeholder="Nhập tên đăng nhập" required autocomplete="username"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label-modern">Mật khẩu</label>
                        <div class="position-relative">
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="Nhập mật khẩu" required autocomplete="current-password"
                                   style="border-radius: 16px; padding: 0.75rem 2.5rem 0.75rem 1rem;">
                            <span id="togglePassword" style="position: absolute; right: 15px; top: 50%; transform: translateY(-50%); cursor: pointer; color: var(--hms-text-muted);">
                                <svg id="eye-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                                <svg id="eye-off-icon" style="display:none;" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                    <line x1="1" y1="1" x2="23" y2="23"></line>
                                </svg>
                            </span>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-4 auth-stagger-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="remember" name="remember">
                            <label class="form-check-label text-muted" for="remember" style="font-size:0.875rem">Ghi nhớ đăng nhập</label>
                        </div>
                        <a href="${ctx}/forgot-password" class="text-decoration-none" style="font-size:0.875rem;color:var(--hms-accent);font-weight:500;">Quên mật khẩu?</a>
                    </div>
                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit" class="btn btn-mintlify-primary py-2 px-5" style="font-size:1rem; min-width: 220px;">Đăng nhập</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="auth-banner-side">
            <img src="${ctx}/assets/img/login-illustration.png" alt="Hostel Management Illustration" class="auth-illustration">
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const togglePassword = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');
        const eyeIcon = document.getElementById('eye-icon');
        const eyeOffIcon = document.getElementById('eye-off-icon');

        if(togglePassword && passwordInput) {
            togglePassword.addEventListener('click', function() {
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    eyeIcon.style.display = 'none';
                    eyeOffIcon.style.display = 'block';
                } else {
                    passwordInput.type = 'password';
                    eyeIcon.style.display = 'block';
                    eyeOffIcon.style.display = 'none';
                }
            });
        }
    });
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
