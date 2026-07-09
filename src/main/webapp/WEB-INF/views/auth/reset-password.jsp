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
                    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp"/>
                </div>

                <form action="${ctx}/reset-password" method="post" class="auth-stagger-3" onsubmit="return validatePasswords(event)">
                    <input type="hidden" name="token" value="<c:out value='${resetToken}'/>"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <div class="input-group" style="position: relative;">
                            <input type="password" class="form-control" id="newPassword"
                                   name="newPassword" required minlength="8" maxlength="50"
                                   pattern="(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,50}"
                                   title="Từ 8 đến 50 ký tự, bao gồm 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt (@#$%^&+=!)"
                                   autocomplete="new-password" placeholder="Từ 8 đến 50 ký tự"
                                   style="border-radius: 16px; padding: 0.75rem 1rem; padding-right: 3rem;">
                            <button type="button" class="btn btn-outline-secondary toggle-password"
                                    style="position: absolute; right: 0; top: 0; height: 100%; border: none; background: transparent; z-index: 10; border-radius: 0 16px 16px 0;"
                                    onclick="togglePasswordVisibility('newPassword', this)">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                            </button>
                        </div>
                        <small class="text-muted" style="font-size: 0.8rem; margin-top: 0.5rem; display: block;">Mật khẩu phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt.</small>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu</label>
                        <div class="input-group" style="position: relative;">
                            <input type="password" class="form-control" id="confirmPassword"
                                   name="confirmPassword" required minlength="8" maxlength="50"
                                   autocomplete="new-password" placeholder="Nhập lại mật khẩu mới"
                                   style="border-radius: 16px; padding: 0.75rem 1rem; padding-right: 3rem;">
                            <button type="button" class="btn btn-outline-secondary toggle-password"
                                    style="position: absolute; right: 0; top: 0; height: 100%; border: none; background: transparent; z-index: 10; border-radius: 0 16px 16px 0;"
                                    onclick="togglePasswordVisibility('confirmPassword', this)">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                            </button>
                        </div>
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
// Logic tính độ mạnh mật khẩu
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

// Highlight viền nếu khớp
document.getElementById('confirmPassword').addEventListener('input', function () {
    var match = this.value === document.getElementById('newPassword').value;
    this.style.borderColor = this.value ? (match ? 'var(--hms-accent)' : 'var(--hms-danger)') : '';
});

// Validate 2 pass trùng nhau khi ấn submit
function validatePasswords(event) {
    var newPassword = document.getElementById('newPassword').value;
    var confirmPassword = document.getElementById('confirmPassword').value;
    if (newPassword !== confirmPassword) {
        alert("Xác nhận mật khẩu không khớp.");
        event.preventDefault();
        return false;
    }
    
    // Đổi chữ nút thành Đang xử lý
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...';
    return true;
}

// Hàm Show/Hide con mắt
function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);
    
    if (type === 'text') {
        btn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>';
    } else {
        btn.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>';
    }
}
</script>
</body>
</html>
