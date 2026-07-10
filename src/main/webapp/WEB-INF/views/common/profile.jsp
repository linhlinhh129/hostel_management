<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Hồ sơ cá nhân - Quản lý Nhà trọ"/>
<c:set var="activeMenu" value="profile"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>

<style>
    .avatar-preview {
        width: 88px;
        height: 88px;
        object-fit: cover;
        border-radius: 50%;
        border: 3px solid var(--hms-canvas);
        box-shadow: var(--hms-shadow-md);
    }
    .avatar-initials {
        width: 88px;
        height: 88px;
        border-radius: 50%;
        border: 3px solid var(--hms-canvas);
        box-shadow: var(--hms-shadow-md);
        background: linear-gradient(135deg, var(--hms-accent) 0%, var(--hms-accent-deep) 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.75rem;
        font-weight: 700;
        color: #fff;
        letter-spacing: -1px;
    }
    .profile-field-label {
        font-size: 0.8125rem;
        font-weight: 500;
        color: var(--hms-text-secondary);
        margin-bottom: 4px;
    }
    .profile-field-label .required {
        color: var(--hms-danger);
        margin-left: 2px;
    }
    .profile-input {
        width: 100%;
        padding: 8px 12px;
        font-size: 0.875rem;
        font-family: var(--hms-font);
        color: var(--hms-text);
        background: var(--hms-canvas);
        border: 1px solid var(--hms-border);
        border-radius: var(--hms-radius);
        outline: none;
        transition: border-color 0.15s, box-shadow 0.15s;
    }
    .profile-input:focus {
        border-color: var(--hms-accent);
        box-shadow: 0 0 0 3px var(--hms-accent-glow);
    }
    .profile-input:read-only,
    .profile-input[readonly] {
        background: var(--hms-surface);
        color: var(--hms-text-muted);
        cursor: default;
    }
    .profile-input.is-invalid {
        border-color: var(--hms-danger);
    }
    .profile-select {
        width: 100%;
        padding: 8px 12px;
        font-size: 0.875rem;
        font-family: var(--hms-font);
        color: var(--hms-text);
        background: var(--hms-canvas);
        border: 1px solid var(--hms-border);
        border-radius: var(--hms-radius);
        outline: none;
        transition: border-color 0.15s, box-shadow 0.15s;
        appearance: none;
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%23888' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
        background-repeat: no-repeat;
        background-position: right 12px center;
        padding-right: 32px;
    }
    .profile-select:focus {
        border-color: var(--hms-accent);
        box-shadow: 0 0 0 3px var(--hms-accent-glow);
    }
    .profile-textarea {
        width: 100%;
        padding: 8px 12px;
        font-size: 0.875rem;
        font-family: var(--hms-font);
        color: var(--hms-text);
        background: var(--hms-canvas);
        border: 1px solid var(--hms-border);
        border-radius: var(--hms-radius);
        outline: none;
        resize: vertical;
        transition: border-color 0.15s, box-shadow 0.15s;
    }
    .profile-textarea:focus {
        border-color: var(--hms-accent);
        box-shadow: 0 0 0 3px var(--hms-accent-glow);
    }
    .profile-hint {
        font-size: 0.78rem;
        color: var(--hms-text-muted);
        margin-top: 4px;
    }
    .avatar-upload-btn {
        display: inline-block;
        padding: 7px 18px;
        font-size: 0.8125rem;
        font-weight: 500;
        font-family: var(--hms-font);
        color: var(--hms-text-secondary);
        background: var(--hms-canvas);
        border: 1px solid var(--hms-border);
        border-radius: var(--hms-radius-full);
        cursor: pointer;
        transition: border-color 0.15s, box-shadow 0.15s;
    }
    .avatar-upload-btn:hover {
        border-color: var(--hms-accent);
        color: var(--hms-accent-deep);
        box-shadow: var(--hms-shadow-xs);
    }
    /* password strength bar */
    .pw-checklist {
        list-style: none;
        padding: 0.35rem 0 0;
        margin: 0;
        font-size: 0.775rem;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 2px 8px;
        color: var(--hms-text-muted);
    }
    .pw-strength-bar {
        height: 3px;
        border-radius: 99px;
        background: var(--hms-border);
        margin-top: 6px;
        overflow: hidden;
    }
    .pw-strength-bar-fill {
        height: 100%;
        border-radius: 99px;
        background: var(--hms-danger);
        transition: width 0.3s, background 0.3s;
        width: 0;
    }
</style>

<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <!-- ── Page Header ─────────────────────────────────── -->
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Hồ sơ cá nhân</h1>
                        <p>Cập nhật thông tin và bảo mật tài khoản</p>
                    </div>
                </div>
            </div>

            <!-- ── Two-column layout ───────────────────────────── -->
            <div class="row g-3">

                <!-- Col 1: Thông tin cá nhân -->
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

                                <!-- Avatar row -->
                                <div style="display:flex;align-items:center;gap:1.25rem;margin-bottom:1.5rem">
                                    <div>
                                        <c:choose>
                                            <c:when test="${not empty userProfile.avatarUrl}">
                                                <img src="${ctx}${userProfile.avatarUrl}"
                                                     class="avatar-preview" id="avatarPreview"
                                                     alt="Avatar">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="avatar-initials" id="avatarInitials">
                                                    <c:out value="${sessionScope.currentUser.initials}"/>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <label for="avatarInput" class="avatar-upload-btn">Đổi ảnh đại diện</label>
                                        <input type="file" id="avatarInput" name="avatar"
                                               class="d-none" accept="image/*">
                                        <p class="profile-hint" style="margin-top:6px">Định dạng JPEG, PNG. Tối đa 5 MB.</p>
                                    </div>
                                </div>

                                <!-- Fields grid -->
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Tên đăng nhập</label>
                                        <input type="text" class="profile-input"
                                               value="<c:out value='${userProfile.username}'/>" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Họ và tên<span class="required">*</span></label>
                                        <input type="text" class="profile-input" name="fullName"
                                               value="<c:out value='${userProfile.fullName}'/>" required>
                                        <p class="profile-hint" style="color:var(--hms-danger);display:none" id="fullNameErr">
                                            Vui lòng nhập họ và tên.
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Email</label>
                                        <input type="email" class="profile-input"
                                               value="<c:out value='${userProfile.email}'/>" readonly>
                                        <p class="profile-hint">Liên hệ Admin nếu muốn đổi email.</p>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Số điện thoại</label>
                                        <input type="text" class="profile-input" name="phone"
                                               value="<c:out value='${userProfile.phone}'/>">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Số CCCD / CMND</label>
                                        <input type="text" class="profile-input" name="identityNumber"
                                               value="<c:out value='${userProfile.identityNumber}'/>">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Ngày sinh</label>
                                        <input type="date" class="profile-input" name="dob"
                                               value="<c:out value='${userProfile.dob}'/>">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="profile-field-label">Giới tính</label>
                                        <select class="profile-select" name="gender">
                                            <option value="" ${empty userProfile.gender ? 'selected' : ''}>-- Chọn giới tính --</option>
                                            <option value="Nam"  ${userProfile.gender == 'Nam'  ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ"   ${userProfile.gender == 'Nữ'   ? 'selected' : ''}>Nữ</option>
                                            <option value="Khác" ${userProfile.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <label class="profile-field-label">Địa chỉ thường trú</label>
                                        <textarea class="profile-textarea" name="permanentAddress"
                                                  rows="2"><c:out value="${userProfile.permanentAddress}"/></textarea>
                                    </div>
                                </div>

                                <div style="display:flex;justify-content:flex-end;margin-top:1.25rem">
                                    <button type="submit" class="btn-mintlify-primary">Lưu thay đổi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Col 2: Đổi mật khẩu -->
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

                                <div style="margin-bottom:1rem">
                                    <label class="profile-field-label">Mật khẩu hiện tại<span class="required">*</span></label>
                                    <input type="password" class="profile-input"
                                           name="currentPassword" required>
                                </div>

                                <div style="margin-bottom:1rem">
                                    <label class="profile-field-label">Mật khẩu mới<span class="required">*</span></label>
                                    <input type="password" class="profile-input"
                                           id="profileNewPw" name="newPassword"
                                           minlength="8" required>
                                    <ul id="profilePwChecklist" class="pw-checklist">
                                        <li id="p-chk-len">&#10007; Ít nhất 8 ký tự</li>
                                        <li id="p-chk-upper">&#10007; 1 chữ hoa (A-Z)</li>
                                        <li id="p-chk-lower">&#10007; 1 chữ thường (a-z)</li>
                                        <li id="p-chk-digit">&#10007; 1 chữ số (0-9)</li>
                                        <li id="p-chk-special">&#10007; 1 ký tự đặc biệt</li>
                                    </ul>
                                    <div class="pw-strength-bar">
                                        <div class="pw-strength-bar-fill" id="profilePwBar"></div>
                                    </div>
                                </div>

                                <div style="margin-bottom:1.25rem">
                                    <label class="profile-field-label">Xác nhận mật khẩu mới<span class="required">*</span></label>
                                    <input type="password" class="profile-input"
                                           id="profileConfirmPw" name="confirmPassword"
                                           minlength="8" required>
                                    <div id="profileConfirmMsg"
                                         style="font-size:0.775rem;margin-top:4px;min-height:1rem"></div>
                                </div>

                                <button type="submit" class="btn-mintlify-primary"
                                        style="width:100%">Cập nhật mật khẩu</button>
                            </form>
                        </div>
                    </div>
                </div>

            </div><!-- /row -->
        </main>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>

<script>
document.addEventListener("DOMContentLoaded", function () {

    /* ── Avatar preview ──────────────────────────────────────── */
    var avatarInput    = document.getElementById('avatarInput');
    var avatarPreview  = document.getElementById('avatarPreview');
    var avatarInitials = document.getElementById('avatarInitials');

    avatarInput.addEventListener('change', function () {
        if (this.files && this.files.length > 0) {
            var file = this.files[0];
            if (file.type.startsWith('image/')) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    if (avatarPreview) {
                        avatarPreview.src = e.target.result;
                    } else if (avatarInitials) {
                        var img = document.createElement('img');
                        img.src = e.target.result;
                        img.className = 'avatar-preview';
                        img.id = 'avatarPreview';
                        avatarInitials.parentNode.replaceChild(img, avatarInitials);
                    }
                };
                reader.readAsDataURL(file);
            }
        }
    });

    /* ── Bootstrap validation ────────────────────────────────── */
    document.querySelectorAll('.needs-validation').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    /* ── Password strength & checklist ──────────────────────── */
    (function () {
        var pwInput  = document.getElementById('profileNewPw');
        var cfInput  = document.getElementById('profileConfirmPw');
        var bar      = document.getElementById('profilePwBar');

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
            var passed = updateChecklist(this.value);
            bar.style.width      = (passed * 20) + '%';
            bar.style.background = ['', 'var(--hms-danger)', 'var(--hms-warning)', 'var(--hms-warning)',
                                     'var(--hms-success)', 'var(--hms-success)'][passed] || 'var(--hms-danger)';
            updateConfirm();
        });

        cfInput.addEventListener('input', updateConfirm);

        function updateConfirm() {
            var msg   = document.getElementById('profileConfirmMsg');
            var match = cfInput.value === pwInput.value;
            if (!cfInput.value) { msg.textContent = ''; return; }
            msg.style.color = match ? 'var(--hms-success)' : 'var(--hms-danger)';
            msg.textContent = match ? '\u2713 M\u1EADt kh\u1EA9u kh\u1EDBp' : '\u2717 M\u1EADt kh\u1EA9u ch\u01B0a kh\u1EDBp';
        }

        pwInput.closest('form').addEventListener('submit', function (e) {
            if (!allPassed(pwInput.value)) {
                e.preventDefault(); e.stopPropagation(); pwInput.focus(); return false;
            }
            if (cfInput.value !== pwInput.value) {
                e.preventDefault(); e.stopPropagation(); cfInput.focus(); return false;
            }
        });
    })();
});
</script>
</body>
</html>
