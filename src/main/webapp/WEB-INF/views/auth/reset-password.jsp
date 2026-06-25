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

                <form id="resetPasswordForm" class="auth-stagger-3">
                    <input type="hidden" id="token" value="<c:out value='${resetToken}'/>"/>

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

                    <button type="submit" id="submitBtn" class="btn btn-mintlify-primary w-100"
                            style="border-radius:var(--hms-radius-full);padding:11px">
                        Đặt lại mật khẩu
                    </button>
                    <a href="${ctx}/login" class="d-block text-center mt-3"
                       style="font-size:0.8125rem;color:var(--hms-stone);text-decoration:none">
                        ← Quay lại đăng nhập
                    </a>
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
document.getElementById('resetPasswordForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const btn = document.getElementById('submitBtn');
    const token = document.getElementById('token').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        alert("Xác nhận mật khẩu không khớp.");
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...';

    fetch('${ctx}/api/v1/auth/reset-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ token: token, newPassword: newPassword })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            window.location.href = '${ctx}/login?success=reset';
        } else {
            alert(data.error ? data.error.message : 'Có lỗi xảy ra, vui lòng thử lại.');
            btn.disabled = false;
            btn.innerHTML = 'Đặt lại mật khẩu';
        }
    })
    .catch(error => {
        alert('Lỗi kết nối. Vui lòng thử lại sau.');
        btn.disabled = false;
        btn.innerHTML = 'Đặt lại mật khẩu';
    });
});
</script>
