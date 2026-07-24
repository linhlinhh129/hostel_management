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
                    <div class="auth-brand-logo mb-3">IH</div>
                    <h1 class="auth-heading">Đặt lại mật khẩu</h1>
                    <p class="auth-subtitle">Nhập mật khẩu mới cho tài khoản của bạn</p>
                </div>

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp"/>
                </div>

                <form action="${ctx}/reset-password" method="post" class="auth-stagger-3"
                      onsubmit="return validatePasswords(event)">
                    <input type="hidden" name="token"     value="<c:out value='${resetToken}'/>"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <div class="position-relative">
                            <input type="password" class="form-control auth-input--with-toggle"
                                   id="newPassword" name="newPassword"
                                   required minlength="8" maxlength="50"
                                   pattern="(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,50}"
                                   title="Từ 8 đến 50 ký tự, bao gồm 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt (@#$%^&+=!)"
                                   autocomplete="new-password" placeholder="Từ 8 đến 50 ký tự">
                            <button type="button" class="auth-toggle-btn"
                                    onclick="togglePasswordVisibility('newPassword', this)"
                                    aria-label="Ẩn/Hiện mật khẩu">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                     stroke="currentColor" stroke-width="2"
                                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
                        </div>
                        <small class="form-text">
                            Mật khẩu phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt.
                        </small>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu</label>
                        <div class="position-relative">
                            <input type="password" class="form-control auth-input--with-toggle"
                                   id="confirmPassword" name="confirmPassword"
                                   required minlength="8" maxlength="50"
                                   autocomplete="new-password"
                                   placeholder="Nhập lại mật khẩu mới">
                            <button type="button" class="auth-toggle-btn"
                                    onclick="togglePasswordVisibility('confirmPassword', this)"
                                    aria-label="Ẩn/Hiện mật khẩu">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                     stroke="currentColor" stroke-width="2"
                                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                            </button>
                        </div>
                    </div>

                    <div class="pw-strength-track pw-strength-track--spaced">
                        <div id="bar" class="pw-strength-bar"></div>
                    </div>

                    <button type="submit" id="submitBtn"
                            class="btn btn-mintlify-primary w-100 auth-submit-btn">
                        Đặt lại mật khẩu
                    </button>

                    <a href="${ctx}/login" class="d-block text-center mt-3 auth-link-sm">
                        ← Quay lại đăng nhập
                    </a>
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
document.getElementById('newPassword').addEventListener('input', function () {
    var pw = this.value, s = 0;
    if (pw.length >= 8)          s++;
    if (pw.length >= 12)         s++;
    if (/[A-Z]/.test(pw))        s++;
    if (/[0-9]/.test(pw))        s++;
    if (/[^A-Za-z0-9]/.test(pw)) s++;
    var b = document.getElementById('bar');
    b.style.width      = (s * 20) + '%';
    b.style.background = ['','#dc2626','#d97706','#d97706','#059669','#059669'][s] || '#dc2626';
});

document.getElementById('confirmPassword').addEventListener('input', function () {
    var match = this.value === document.getElementById('newPassword').value;
    this.style.borderColor = this.value
        ? (match ? 'var(--hms-accent)' : 'var(--hms-danger)')
        : '';
});

function validatePasswords(event) {
    if (document.getElementById('newPassword').value !==
        document.getElementById('confirmPassword').value) {
        alert('X\u00E1c nh\u1EADn m\u1EADt kh\u1EA9u kh\u00F4ng kh\u1EDBp.');
        event.preventDefault();
        return false;
    }
    var btn = document.getElementById('submitBtn');
    btn.disabled  = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> \u0110ang x\u1EED l\u00FD...';
    return true;
}

function togglePasswordVisibility(inputId, btn) {
    var input   = document.getElementById(inputId);
    var isText  = input.type === 'text';
    input.type  = isText ? 'password' : 'text';
    var eyeOpen = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>';
    var eyeOff  = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>';
    btn.innerHTML = isText ? eyeOpen : eyeOff;
}
</script>
</body>
</html>
