<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Hồ sơ cá nhân - Innolvia Home"/>
<c:set var="activeMenu" value="profile"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- Page Header --%>
            <div class="page-header hero-sky-gradient dash-hero">
                <div class="dash-hero-inner">
                    <div>
                        <h1>Hồ sơ cá nhân</h1>
                        <p>Cập nhật thông tin và bảo mật tài khoản</p>
                    </div>
                </div>
            </div>

            <%-- Two-column layout --%>
            <div class="row g-3">

                <%-- Col 1: Thông tin cá nhân --%>
                <div class="col-lg-8">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Thông tin cá nhân</h3>
                        </div>
                        <div class="widget-surface-body">
                            <form action="${ctx}/profile" method="POST" enctype="multipart/form-data"
                                  class="needs-validation" novalidate>
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="action"    value="update_profile">

                                <%-- Avatar row --%>
                                <div class="profile-avatar-row d-flex align-items-center gap-3 mb-4">
                                    <div>
                                        <c:choose>
                                            <c:when test="${not empty userProfile.avatarUrl}">
                                                <img src="${ctx}${userProfile.avatarUrl}"
                                                     class="profile-avatar-img" id="avatarPreview"
                                                     alt="Avatar">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="profile-avatar-initials" id="avatarInitials">
                                                    <c:out value="${sessionScope.currentUser.initials}"/>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <label for="avatarInput" class="profile-avatar-btn">
                                            Đổi ảnh đại diện
                                        </label>
                                        <input type="file" id="avatarInput" name="avatar"
                                               class="d-none" accept="image/*">
                                        <p class="profile-hint">Định dạng JPEG, PNG. Tối đa 5 MB.</p>
                                    </div>
                                </div>

                                <%-- Fields grid --%>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="prof-username" class="profile-field-label">
                                            Tên đăng nhập
                                        </label>
                                        <input type="text" id="prof-username" class="profile-input"
                                               value="<c:out value='${userProfile.username}'/>" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-fullname" class="profile-field-label">
                                            Họ và tên <span class="profile-required">*</span>
                                        </label>
                                        <input type="text" id="prof-fullname" class="profile-input"
                                               name="fullName"
                                               value="<c:out value='${userProfile.fullName}'/>" required>
                                        <div class="invalid-feedback">Vui lòng nhập họ và tên.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-email" class="profile-field-label">Email</label>
                                        <input type="email" id="prof-email" class="profile-input"
                                               value="<c:out value='${userProfile.email}'/>" readonly>
                                        <p class="profile-hint">Liên hệ Admin nếu muốn đổi email.</p>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-phone" class="profile-field-label">
                                            Số điện thoại <span class="profile-required">*</span>
                                        </label>
                                        <input type="text" id="prof-phone" class="profile-input"
                                               name="phone"
                                               value="<c:out value='${userProfile.phone}'/>" required>
                                        <div class="invalid-feedback">Vui lòng nhập số điện thoại.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-identity" class="profile-field-label">
                                            Số CCCD / CMND <span class="profile-required">*</span>
                                        </label>
                                        <input type="text" id="prof-identity" class="profile-input"
                                               name="identityNumber"
                                               value="<c:out value='${userProfile.identityNumber}'/>" required>
                                        <div class="invalid-feedback">Vui lòng nhập số CCCD / CMND.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-dob" class="profile-field-label">
                                            Ngày sinh <span class="profile-required">*</span>
                                        </label>
                                        <input type="date" id="prof-dob" class="profile-input"
                                               name="dob"
                                               value="<c:out value='${userProfile.dob}'/>" required>
                                        <div class="invalid-feedback">Vui lòng chọn ngày sinh.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="prof-gender" class="profile-field-label">Giới tính</label>
                                        <select id="prof-gender" class="profile-select" name="gender">
                                            <option value="" ${empty userProfile.gender ? 'selected' : ''}>
                                                -- Chọn giới tính --
                                            </option>
                                            <option value="Nam"  ${userProfile.gender == 'Nam'  ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ"   ${userProfile.gender == 'Nữ'   ? 'selected' : ''}>Nữ</option>
                                            <option value="Khác" ${userProfile.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <label for="prof-address" class="profile-field-label">
                                            Địa chỉ thường trú <span class="profile-required">*</span>
                                        </label>
                                        <textarea id="prof-address" class="profile-textarea"
                                                  name="permanentAddress" rows="2" required><c:out value="${userProfile.permanentAddress}"/></textarea>
                                        <div class="invalid-feedback">Vui lòng nhập địa chỉ thường trú.</div>
                                    </div>
                                </div>

                                <div class="profile-form-actions">
                                    <button type="submit" class="btn-mintlify-primary">Lưu thay đổi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <%-- Col 2: Đổi mật khẩu --%>
                <div class="col-lg-4">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Đổi mật khẩu</h3>
                        </div>
                        <div class="widget-surface-body">
                            <form action="${ctx}/profile" method="POST"
                                  class="needs-validation" novalidate>
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="action"    value="change_password">

                                <div class="profile-field-group mb-3">
                                    <label for="prof-curpw" class="profile-field-label">
                                        Mật khẩu hiện tại <span class="profile-required">*</span>
                                    </label>
                                    <input type="password" id="prof-curpw" class="profile-input"
                                           name="currentPassword" required autocomplete="current-password">
                                </div>

                                <div class="profile-field-group mb-3">
                                    <label for="profileNewPw" class="profile-field-label">
                                        Mật khẩu mới <span class="profile-required">*</span>
                                    </label>
                                    <input type="password" id="profileNewPw" class="profile-input"
                                           name="newPassword" minlength="8" required
                                           autocomplete="new-password">
                                    <ul id="profilePwChecklist" class="pw-checklist">
                                        <li id="p-chk-len">&#10007; Ít nhất 8 ký tự</li>
                                        <li id="p-chk-upper">&#10007; 1 chữ hoa (A-Z)</li>
                                        <li id="p-chk-lower">&#10007; 1 chữ thường (a-z)</li>
                                        <li id="p-chk-digit">&#10007; 1 chữ số (0-9)</li>
                                        <li id="p-chk-special">&#10007; 1 ký tự đặc biệt</li>
                                    </ul>
                                    <div class="pw-strength-track mt-2">
                                        <div class="pw-strength-bar" id="profilePwBar"></div>
                                    </div>
                                </div>

                                <div class="profile-field-group mb-4">
                                    <label for="profileConfirmPw" class="profile-field-label">
                                        Xác nhận mật khẩu mới <span class="profile-required">*</span>
                                    </label>
                                    <input type="password" id="profileConfirmPw" class="profile-input"
                                           name="confirmPassword" minlength="8" required
                                           autocomplete="new-password">
                                    <div id="profileConfirmMsg" class="pw-confirm-msg"></div>
                                </div>

                                <button type="submit" class="btn-mintlify-primary profile-pw-submit">
                                    Cập nhật mật khẩu
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

            </div><%-- /row --%>
        </main>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
document.addEventListener('DOMContentLoaded', function () {

    /* ── Avatar preview ──────────────────────────────────────── */
    var avatarInput    = document.getElementById('avatarInput');
    var avatarPreview  = document.getElementById('avatarPreview');
    var avatarInitials = document.getElementById('avatarInitials');

    avatarInput.addEventListener('change', function () {
        if (!this.files || !this.files.length) return;
        var file = this.files[0];
        if (!file.type.startsWith('image/')) return;
        var reader = new FileReader();
        reader.onload = function (e) {
            if (avatarPreview) {
                avatarPreview.src = e.target.result;
            } else if (avatarInitials) {
                var img      = document.createElement('img');
                img.src      = e.target.result;
                img.className = 'profile-avatar-img';
                img.id        = 'avatarPreview';
                avatarInitials.parentNode.replaceChild(img, avatarInitials);
                avatarPreview = img;
            }
        };
        reader.readAsDataURL(file);
    });

    /* ── Bootstrap validation ────────────────────────────────── */
    document.querySelectorAll('.needs-validation').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    /* ── Password strength & checklist ──────────────────────── */
    (function () {
        var pwInput = document.getElementById('profileNewPw');
        var cfInput = document.getElementById('profileConfirmPw');
        var bar     = document.getElementById('profilePwBar');

        var COLORS  = ['', 'var(--hms-danger)', 'var(--hms-warning)', 'var(--hms-warning)',
                        'var(--hms-success)', 'var(--hms-success)'];

        var checks = {
            len:     { el: document.getElementById('p-chk-len'),     test: function (v) { return v.length >= 8; } },
            upper:   { el: document.getElementById('p-chk-upper'),   test: function (v) { return /[A-Z]/.test(v); } },
            lower:   { el: document.getElementById('p-chk-lower'),   test: function (v) { return /[a-z]/.test(v); } },
            digit:   { el: document.getElementById('p-chk-digit'),   test: function (v) { return /[0-9]/.test(v); } },
            special: { el: document.getElementById('p-chk-special'), test: function (v) { return /[^A-Za-z0-9]/.test(v); } }
        };

        function updateChecklist(pw) {
            var passed = 0;
            Object.keys(checks).forEach(function (k) {
                var c = checks[k], ok = c.test(pw);
                c.el.style.color = ok ? 'var(--hms-success)' : 'var(--hms-text-muted)';
                c.el.innerHTML   = (ok ? '&#10003; ' : '&#10007; ') + c.el.innerHTML.slice(2);
                if (ok) passed++;
            });
            return passed;
        }

        function allPassed(pw) {
            return Object.keys(checks).every(function (k) { return checks[k].test(pw); });
        }

        pwInput.addEventListener('input', function () {
            var passed           = updateChecklist(this.value);
            bar.style.width      = (passed * 20) + '%';
            bar.style.background = COLORS[passed] || 'var(--hms-danger)';
            updateConfirm();
        });

        cfInput.addEventListener('input', updateConfirm);

        function updateConfirm() {
            var msg   = document.getElementById('profileConfirmMsg');
            var match = cfInput.value === pwInput.value;
            if (!cfInput.value) { msg.textContent = ''; return; }
            msg.style.color = match ? 'var(--hms-success)' : 'var(--hms-danger)';
            msg.textContent = match
                ? '\u2713 M\u1EADt kh\u1EA9u kh\u1EDBp'
                : '\u2717 M\u1EADt kh\u1EA9u ch\u01B0a kh\u1EDBp';
        }

        pwInput.closest('form').addEventListener('submit', function (e) {
            if (!allPassed(pwInput.value) || cfInput.value !== pwInput.value) {
                e.preventDefault();
                e.stopPropagation();
                if (!allPassed(pwInput.value)) pwInput.focus();
                else cfInput.focus();
            }
        });
    }());
});
</script>
</body>
</html>
