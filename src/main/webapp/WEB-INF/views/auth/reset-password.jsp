<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đặt lại mật khẩu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout">
        <div class="auth-form-side">
            <div class="auth-card">

                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-icon mb-3" style="width:48px;height:48px;font-size:1.25rem;">HT</div>
                    <h1 style="font-size:2rem;font-weight:600;letter-spacing:-1px;margin-bottom:0.5rem;">Đặt lại mật khẩu</h1>
                    <p style="color:var(--hms-text-muted);font-size:1rem;">Nhập mật khẩu mới cho tài khoản của bạn</p>
                </div>

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                </div>

                <form action="${ctx}/reset-password" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken"  value="${csrfToken}"/>
                    <input type="hidden" name="token"      value="<c:out value='${resetToken}'/>"/>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <input type="password" class="form-control" id="newPassword"
                               name="newPassword" required minlength="8"
                               autocomplete="new-password" placeholder="Ít nhất 8 ký tự"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" required minlength="8"
                               autocomplete="new-password" placeholder="Nhập lại mật khẩu mới"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                    </div>

                    <!-- Strength bar -->
                    <div style="height:4px;border-radius:99px;background:var(--hms-border);margin-bottom:1.5rem;overflow:hidden">
                        <div id="bar" style="height:100%;width:0;border-radius:99px;background:var(--hms-danger);transition:width 0.3s,background 0.3s"></div>
                    </div>

                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit" class="btn btn-mintlify-primary py-2 px-5"
                                style="font-size:1rem; min-width: 220px;">
                            Đặt lại mật khẩu
                        </button>
                    </div>
                    <div class="text-center auth-stagger-4">
                        <a href="${ctx}/login" class="text-decoration-none"
                           style="font-size:0.875rem;color:var(--hms-stone);">
                            ← Quay lại đăng nhập
                        </a>
                    </div>
                </form>
            </div>
        </div>
        <div class="auth-banner-side">
            <img src="${ctx}/assets/img/login-illustration.png" alt="Hostel Management Illustration" class="auth-illustration">
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
document.getElementById('newPassword').addEventListener('input', function () {
    var pw = this.value, s = 0;
    if (pw.length >= 8) s++;
    if (pw.length >= 12) s++;
    if (/[A-Z]/.test(pw)) s++;
    if (/[0-9]/.test(pw)) s++;
    if (/[^A-Za-z0-9]/.test(pw)) s++;
    var b = document.getElementById('bar');
    b.style.width = (s * 20) + '%';
    b.style.background = ['','#dc2626','#d97706','#d97706','#059669','#059669'][s] || '#dc2626';
});
document.getElementById('confirmPassword').addEventListener('input', function () {
    var match = this.value === document.getElementById('newPassword').value;
    this.style.borderColor = this.value ? (match ? 'var(--hms-accent)' : 'var(--hms-danger)') : '';
});
</script>
