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
                        <ul id="pwChecklist" class="pw-checklist">
                            <li id="chk-len">&#10007; Ít nhất 8 ký tự</li>
                            <li id="chk-upper">&#10007; 1 chữ hoa (A-Z)</li>
                            <li id="chk-lower">&#10007; 1 chữ thường (a-z)</li>
                            <li id="chk-digit">&#10007; 1 chữ số (0-9)</li>
                            <li id="chk-special">&#10007; 1 ký tự đặc biệt</li>
                        </ul>
                        <div class="pw-strength-track">
                            <div id="pwStrengthBar" class="pw-strength-bar"></div>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control auth-input" id="confirmPassword" name="confirmPassword"
                               placeholder="Nhập lại mật khẩu mới" required autocomplete="new-password">
                        <div id="confirmMsg" class="pw-confirm-msg"></div>
                    </div>

                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit" id="submitBtn"
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
(function () {
    var pwInput   = document.getElementById('newPassword');
    var cfInput   = document.getElementById('confirmPassword');
    var bar       = document.getElementById('pwStrengthBar');
    var submitBtn = document.getElementById('submitBtn');

    var checks = {
        len:     { el: document.getElementById('chk-len'),     test: function(v){ return v.length >= 8; } },
        upper:   { el: document.getElementById('chk-upper'),   test: function(v){ return /[A-Z]/.test(v); } },
        lower:   { el: document.getElementById('chk-lower'),   test: function(v){ return /[a-z]/.test(v); } },
        digit:   { el: document.getElementById('chk-digit'),   test: function(v){ return /[0-9]/.test(v); } },
        special: { el: document.getElementById('chk-special'), test: function(v){ return /[^A-Za-z0-9]/.test(v); } }
    };

    var COLORS = ['', '#dc2626', '#d97706', '#d97706', '#059669', '#059669'];

    function updateChecklist(pw) {
        var passed = 0;
        Object.keys(checks).forEach(function (k) {
            var c = checks[k], ok = c.test(pw);
            c.el.style.color = ok ? '#059669' : 'var(--hms-stone)';
            c.el.innerHTML   = (ok ? '&#10003; ' : '&#10007; ') + c.el.innerHTML.slice(2);
            if (ok) passed++;
        });
        return passed;
    }

    function allPassed(pw) {
        return Object.keys(checks).every(function (k) { return checks[k].test(pw); });
    }

    pwInput.addEventListener('input', function () {
        var passed = updateChecklist(this.value);
        bar.style.width      = (passed * 20) + '%';
        bar.style.background = COLORS[passed] || '#dc2626';
        updateConfirm();
        submitBtn.disabled = !allPassed(this.value);
    });

    cfInput.addEventListener('input', updateConfirm);

    function updateConfirm() {
        var msg   = document.getElementById('confirmMsg');
        var match = cfInput.value === pwInput.value;
        if (!cfInput.value) { msg.textContent = ''; return; }
        msg.style.color = match ? '#059669' : '#dc2626';
        msg.textContent = match ? '\u2713 M\u1EADt kh\u1EA9u kh\u1EDBp' : '\u2717 M\u1EADt kh\u1EA9u ch\u01B0a kh\u1EDBp';
    }

    document.querySelector('form').addEventListener('submit', function (e) {
        if (!allPassed(pwInput.value) || cfInput.value !== pwInput.value) {
            e.preventDefault();
            pwInput.focus();
        }
    });

    submitBtn.disabled = true;
}());
</script>
</body>
</html>
