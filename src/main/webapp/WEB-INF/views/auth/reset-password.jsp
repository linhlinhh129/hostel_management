<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đặt lại mật khẩu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout" style="max-width:600px;min-height:auto">
        <div class="auth-form-side" style="flex:1;padding:2.5rem">
            <div class="auth-card">

                <div class="auth-stagger-1 mb-4 text-center">
                    <div style="width:52px;height:52px;border-radius:var(--hms-radius-md);
                                background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft));
                                display:flex;align-items:center;justify-content:center;
                                color:#fff;font-weight:800;font-size:1.125rem;margin:0 auto 1rem;
                                box-shadow:0 4px 16px rgba(0,212,164,0.30)">HT</div>
                    <h1 style="font-size:1.5rem;font-weight:700;letter-spacing:-0.5px;margin:0 0 0.375rem">
                        Đặt lại mật khẩu
                    </h1>
                    <p style="color:var(--hms-stone);font-size:0.875rem;margin:0">
                        Nhập mật khẩu mới cho tài khoản của bạn
                    </p>
                </div>

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                </div>

                <form action="${ctx}/reset-password" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken"  value="${csrfToken}"/>
                    <input type="hidden" name="token"      value="<c:out value='${resetToken}'/>"/>

                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Mật khẩu mới</label>
                        <input type="password" class="form-control" id="newPassword"
                               name="newPassword" required minlength="8"
                               autocomplete="new-password" placeholder="Ít nhất 8 ký tự">
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" required minlength="8"
                               autocomplete="new-password" placeholder="Nhập lại mật khẩu mới">
                    </div>

                    <!-- Strength bar -->
                    <div style="height:4px;border-radius:99px;background:var(--hms-border);margin-bottom:1rem;overflow:hidden">
                        <div id="bar" style="height:100%;width:0;border-radius:99px;background:var(--hms-danger);transition:width 0.3s,background 0.3s"></div>
                    </div>

                    <button type="submit" class="btn btn-mintlify-primary w-100"
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
