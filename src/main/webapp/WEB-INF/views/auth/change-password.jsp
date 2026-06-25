<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đổi mật khẩu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout">
        <div class="auth-form-side">
            <div class="auth-card">

                <!-- Brand -->
                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-icon mb-3" style="width:48px;height:48px;font-size:1.25rem;">HT</div>
                    <h1 style="font-size:2rem;font-weight:600;letter-spacing:-1px;margin-bottom:0.5rem;">
                        <c:choose>
                            <c:when test="${forceChange}">Đặt mật khẩu mới</c:when>
                            <c:otherwise>Đổi mật khẩu</c:otherwise>
                        </c:choose>
                    </h1>
                    <p style="color:var(--hms-text-muted);font-size:1rem;">
                        <c:choose>
                            <c:when test="${forceChange}">
                                Đây là lần đăng nhập đầu tiên. Vui lòng đặt mật khẩu mới trước khi tiếp tục.
                            </c:when>
                            <c:otherwise>
                                Cập nhật mật khẩu tài khoản của bạn
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <!-- Error -->
                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                </div>

                <!-- Form -->
                <form action="${ctx}/change-password" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <input type="hidden" name="forceChange" value="${forceChange}"/>

                    <div class="mb-4">
                        <label for="currentPassword" class="form-label-modern">
                            Mật khẩu hiện tại
                            <c:if test="${forceChange}">
                                <span style="color:var(--hms-stone);font-weight:400">(mật khẩu tạm thời)</span>
                            </c:if>
                        </label>
                        <input type="password" class="form-control" id="currentPassword"
                               name="currentPassword" required autocomplete="current-password"
                               placeholder="Nhập mật khẩu hiện tại"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                    </div>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <input type="password" class="form-control" id="newPassword"
                               name="newPassword" required minlength="8"
                               autocomplete="new-password"
                               placeholder="Ít nhất 8 ký tự"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                        <%-- Checklist yêu cầu mật khẩu --%>
                        <ul id="pwChecklist" style="list-style:none;padding:0.5rem 0 0;margin:0;font-size:0.8rem;display:grid;grid-template-columns:1fr 1fr;gap:2px 8px">
                            <li id="chk-len"     style="color:var(--hms-stone)">&#10007; Ít nhất 8 ký tự</li>
                            <li id="chk-upper"   style="color:var(--hms-stone)">&#10007; 1 chữ hoa (A-Z)</li>
                            <li id="chk-lower"   style="color:var(--hms-stone)">&#10007; 1 chữ thường (a-z)</li>
                            <li id="chk-digit"   style="color:var(--hms-stone)">&#10007; 1 chữ số (0-9)</li>
                            <li id="chk-special" style="color:var(--hms-stone)">&#10007; 1 ký tự đặc biệt</li>
                        </ul>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu mới</label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" required minlength="8"
                               autocomplete="new-password"
                               placeholder="Nhập lại mật khẩu mới"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
                        <div id="confirmMsg" style="font-size:0.8rem;margin-top:4px;min-height:1rem"></div>
                    </div>

                    <!-- Password strength indicator -->
                    <div id="pwStrength" style="height:4px;border-radius:99px;background:var(--hms-border);
                                                margin-bottom:1.5rem;overflow:hidden">
                        <div id="pwStrengthBar" style="height:100%;width:0;border-radius:99px;
                                                       background:var(--hms-danger);
                                                       transition:width 0.3s,background 0.3s"></div>
                    </div>

                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit" class="btn btn-mintlify-primary py-2 px-5"
                                style="font-size:1rem; min-width: 220px;">
                            Cập nhật mật khẩu
                        </button>
                    </div>

                    <c:if test="${!forceChange}">
                        <div class="text-center auth-stagger-4">
                            <a href="${ctx}/login"
                               class="text-decoration-none"
                               style="font-size:0.875rem;color:var(--hms-stone);">
                                ← Quay lại
                            </a>
                        </div>
                    </c:if>
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
(function () {
    var pwInput  = document.getElementById('newPassword');
    var cfInput  = document.getElementById('confirmPassword');
    var bar      = document.getElementById('pwStrengthBar');
    var submitBtn = document.querySelector('button[type="submit"]');

    var checks = {
        len:     { el: document.getElementById('chk-len'),     test: function(v){ return v.length >= 8; } },
        upper:   { el: document.getElementById('chk-upper'),   test: function(v){ return /[A-Z]/.test(v); } },
        lower:   { el: document.getElementById('chk-lower'),   test: function(v){ return /[a-z]/.test(v); } },
        digit:   { el: document.getElementById('chk-digit'),   test: function(v){ return /[0-9]/.test(v); } },
        special: { el: document.getElementById('chk-special'), test: function(v){ return /[^A-Za-z0-9]/.test(v); } }
    };

    function updateChecklist(pw) {
        var passed = 0;
        Object.keys(checks).forEach(function(k) {
            var c = checks[k];
            var ok = c.test(pw);
            if (ok) {
                c.el.style.color = '#059669';
                c.el.innerHTML   = '&#10003; ' + c.el.innerHTML.slice(2);
                passed++;
            } else {
                c.el.style.color = 'var(--hms-stone)';
                c.el.innerHTML   = '&#10007; ' + c.el.innerHTML.slice(2);
            }
        });
        return passed;
    }

    function updateStrengthBar(passed) {
        var colors = ['', '#dc2626', '#d97706', '#d97706', '#059669', '#059669'];
        bar.style.width      = (passed * 20) + '%';
        bar.style.background = colors[passed] || '#dc2626';
    }

    function allPassed(pw) {
        return Object.keys(checks).every(function(k){ return checks[k].test(pw); });
    }

    pwInput.addEventListener('input', function () {
        var passed = updateChecklist(this.value);
        updateStrengthBar(passed);
        updateConfirm();
        submitBtn.disabled = !allPassed(this.value);
    });

    cfInput.addEventListener('input', updateConfirm);

    function updateConfirm() {
        var msg   = document.getElementById('confirmMsg');
        var match = cfInput.value === pwInput.value;
        if (!cfInput.value) { msg.textContent = ''; return; }
        if (match) {
            msg.style.color = '#059669';
            msg.textContent = '\u2713 M\u1EADt kh\u1EA9u kh\u1EDBp';
        } else {
            msg.style.color = '#dc2626';
            msg.textContent = '\u2717 M\u1EADt kh\u1EA9u ch\u01B0a kh\u1EDBp';
        }
    }

    // Block submit nếu chưa đủ điều kiện
    document.querySelector('form').addEventListener('submit', function (e) {
        if (!allPassed(pwInput.value)) {
            e.preventDefault();
            pwInput.focus();
            return false;
        }
        if (cfInput.value !== pwInput.value) {
            e.preventDefault();
            cfInput.focus();
            return false;
        }
    });

    // Init
    submitBtn.disabled = true;
})();
</script>
