<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đổi mật khẩu lần đầu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout">
        <div class="auth-form-side">
            <div class="auth-card">

                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-logo mb-3">IH</div>
                    <h1 class="auth-heading">Đổi Mật Khẩu</h1>
                    <p class="auth-subtitle">Đây là lần đăng nhập đầu tiên, bạn cần đổi mật khẩu để bảo vệ tài khoản.</p>
                </div>

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp"/>
                </div>

                <form action="${ctx}/first-login" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <input type="password" class="form-control auth-input" id="newPassword" name="newPassword"
                               placeholder="Nhập mật khẩu mới" required autocomplete="new-password"
                               pattern="(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}"
                               title="Ít nhất 8 ký tự, bao gồm 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt (@#$%^&+=!)">
                        <div class="text-muted small mt-2">
                            * Mật khẩu cần ít nhất 8 ký tự, bao gồm chữ hoa (A-Z), chữ số (0-9) và ký tự đặc biệt (@#$%^&+=!).
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control auth-input" id="confirmPassword" name="confirmPassword"
                               placeholder="Nhập lại mật khẩu mới" required autocomplete="new-password">
                    </div>

                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit"
                                class="btn btn-mintlify-primary py-2 px-5 auth-submit-btn">
                            Cập nhật mật khẩu
                        </button>
                    </div>

                    <div class="text-center auth-stagger-4">
                        <a href="${ctx}/logout" class="auth-link-sm">Đăng xuất</a>
                    </div>
                </form>

            </div>
        </div>
        <div class="auth-banner-side">
            <img src="${ctx}/assets/img/login-illustration.png"
                 alt="Hostel Management Illustration" class="auth-illustration">
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
</script>
</body>
</html>
