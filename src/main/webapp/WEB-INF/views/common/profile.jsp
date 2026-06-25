<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="activeMenu" value="profile"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Hồ sơ cá nhân" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mintlify.css" />
    <style>
        .avatar-preview {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #fff;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }
    </style>
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper" style="background-color: var(--color-canvas);">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

            <main class="page-content" style="padding: 32px 48px; max-width: 1400px; margin: 0 auto;">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />
                
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h3 mb-0" style="font-family: 'Inter', sans-serif; font-weight: 600; color: var(--color-ink);">Hồ sơ của tôi</h2>
                </div>

                <c:if test="${param.success == 'profile'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Cập nhật thông tin hồ sơ thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'password'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Đổi mật khẩu thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'password_mismatch'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Xác nhận mật khẩu mới không khớp!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'invalid_password'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Mật khẩu hiện tại không chính xác!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'invalid_phone'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'invalid_identity'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'invalid_policy'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Mật khẩu mới không đạt chuẩn bảo mật (cần ít nhất 8 ký tự, có chữ hoa, chữ số và ký tự đặc biệt).
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="row">
                    <!-- Column 1: Chỉnh sửa hồ sơ -->
                    <div class="col-lg-8 mb-4">
                        <div class="card border rounded-4 shadow-sm" style="background-color: #ffffff;">
                            <div class="card-body p-4">
                                <h5 class="card-title fw-bold mb-4">Thông tin cá nhân</h5>
                                
                                <form action="${pageContext.request.contextPath}/profile" method="POST" enctype="multipart/form-data" class="needs-validation" novalidate>
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="action" value="update_profile">
                                    
                                    <div class="d-flex align-items-center gap-4 mb-4">
                                        <div class="position-relative">
                                            <c:choose>
                                                <c:when test="${not empty userProfile.avatarUrl}">
                                                    <img src="${pageContext.request.contextPath}${userProfile.avatarUrl}" class="avatar-preview" id="avatarPreview" alt="Avatar">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="avatar-preview d-flex align-items-center justify-content-center bg-secondary text-white fs-1" id="avatarInitials">
                                                        ${sessionScope.currentUser.initials}
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <label for="avatarInput" class="btn btn-outline-secondary rounded-pill px-4 mb-2">Đổi ảnh đại diện</label>
                                            <input type="file" id="avatarInput" name="avatar" class="d-none" accept="image/*">
                                            <div class="form-text text-muted">Định dạng JPEG, PNG. Tối đa 5MB.</div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Tên đăng nhập</label>
                                            <input type="text" class="form-control" value="${userProfile.username}" readonly style="background-color: #f7f7f7;">
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Họ và tên <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" name="fullName" value="${userProfile.fullName}" required>
                                            <div class="invalid-feedback">Vui lòng nhập họ và tên.</div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Email</label>
                                            <input type="email" class="form-control" value="${userProfile.email}" readonly style="background-color: #f7f7f7;">
                                            <div class="form-text text-muted">Vui lòng liên hệ Admin nếu muốn đổi Email.</div>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Số điện thoại</label>
                                            <input type="text" class="form-control" name="phone" value="${userProfile.phone}">
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Số CCCD / CMND</label>
                                            <input type="text" class="form-control" name="identityNumber" value="${userProfile.identityNumber}">
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Ngày sinh</label>
                                            <input type="date" class="form-control" name="dob" value="${userProfile.dob}">
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Giới tính</label>
                                        <select class="form-select" name="gender">
                                            <option value="" ${empty userProfile.gender ? 'selected' : ''}>-- Chọn giới tính --</option>
                                            <option value="Nam" ${userProfile.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ" ${userProfile.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                            <option value="Khác" ${userProfile.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                                        </select>
                                    </div>

                                    <div class="mb-4">
                                        <label class="form-label fw-medium text-dark">Địa chỉ thường trú</label>
                                        <textarea class="form-control" name="permanentAddress" rows="2">${userProfile.permanentAddress}</textarea>
                                    </div>

                                    <div class="text-end">
                                        <button type="submit" class="btn rounded-pill px-4 text-white" style="background-color: #00d4a4; color: #0a0a0a !important;">Lưu thay đổi</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Column 2: Đổi mật khẩu -->
                    <div class="col-lg-4">
                        <div class="card border rounded-4 shadow-sm" style="background-color: #ffffff;">
                            <div class="card-body p-4">
                                <h5 class="card-title fw-bold mb-4">Đổi mật khẩu</h5>
                                
                                <form action="${pageContext.request.contextPath}/profile" method="POST" class="needs-validation" novalidate>
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="action" value="change_password">
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" name="currentPassword" required>
                                        <div class="invalid-feedback">Vui lòng nhập mật khẩu hiện tại.</div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Mật khẩu mới <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" id="profileNewPw" name="newPassword" minlength="8" required>
                                        <%-- Checklist yêu cầu --%>
                                        <ul id="profilePwChecklist" style="list-style:none;padding:0.4rem 0 0;margin:0;font-size:0.78rem;display:grid;grid-template-columns:1fr 1fr;gap:2px 6px">
                                            <li id="p-chk-len"     style="color:#6c757d">&#10007; Ít nhất 8 ký tự</li>
                                            <li id="p-chk-upper"   style="color:#6c757d">&#10007; 1 chữ hoa (A-Z)</li>
                                            <li id="p-chk-lower"   style="color:#6c757d">&#10007; 1 chữ thường (a-z)</li>
                                            <li id="p-chk-digit"   style="color:#6c757d">&#10007; 1 chữ số (0-9)</li>
                                            <li id="p-chk-special" style="color:#6c757d">&#10007; 1 ký tự đặc biệt</li>
                                        </ul>
                                        <div style="height:3px;border-radius:99px;background:#e9ecef;margin-top:6px;overflow:hidden">
                                            <div id="profilePwBar" style="height:100%;width:0;border-radius:99px;background:#dc3545;transition:width 0.3s,background 0.3s"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-4">
                                        <label class="form-label fw-medium text-dark">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" id="profileConfirmPw" name="confirmPassword" minlength="8" required>
                                        <div id="profileConfirmMsg" style="font-size:0.78rem;margin-top:3px;min-height:1rem"></div>
                                    </div>
                                    
                                    <div class="d-grid">
                                        <button type="submit" class="btn rounded-pill text-white" style="background-color: #0a0a0a;">Cập nhật mật khẩu</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // Avatar preview logic
    const avatarInput = document.getElementById('avatarInput');
    const avatarPreview = document.getElementById('avatarPreview');
    const avatarInitials = document.getElementById('avatarInitials');

    avatarInput.addEventListener('change', function() {
        if (this.files && this.files.length > 0) {
            const file = this.files[0];
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    if (avatarPreview) {
                        avatarPreview.src = e.target.result;
                    } else if (avatarInitials) {
                        // Create image element if it doesn't exist
                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.className = 'avatar-preview';
                        img.id = 'avatarPreview';
                        avatarInitials.parentNode.replaceChild(img, avatarInitials);
                    }
                }
                reader.readAsDataURL(file);
            }
        }
    });

    // Bootstrap validation
    var forms = document.querySelectorAll('.needs-validation')
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            form.classList.add('was-validated')
        }, false)
    });

    // ── Password strength & checklist (profile) ──────────────
    (function () {
        var pwInput   = document.getElementById('profileNewPw');
        var cfInput   = document.getElementById('profileConfirmPw');
        var bar       = document.getElementById('profilePwBar');
        var submitBtn = document.querySelector('#profilePwChecklist').closest('form').querySelector('[type="submit"]');

        var checks = {
            len:     { el: document.getElementById('p-chk-len'),     test: function(v){ return v.length >= 8; } },
            upper:   { el: document.getElementById('p-chk-upper'),   test: function(v){ return /[A-Z]/.test(v); } },
            lower:   { el: document.getElementById('p-chk-lower'),   test: function(v){ return /[a-z]/.test(v); } },
            digit:   { el: document.getElementById('p-chk-digit'),   test: function(v){ return /[0-9]/.test(v); } },
            special: { el: document.getElementById('p-chk-special'), test: function(v){ return /[^A-Za-z0-9]/.test(v); } }
        };

        function updateChecklist(pw) {
            var passed = 0;
            Object.keys(checks).forEach(function(k) {
                var c = checks[k]; var ok = c.test(pw);
                if (ok) { c.el.style.color = '#059669'; c.el.innerHTML = '&#10003; ' + c.el.innerHTML.slice(2); passed++; }
                else    { c.el.style.color = '#6c757d'; c.el.innerHTML = '&#10007; ' + c.el.innerHTML.slice(2); }
            });
            return passed;
        }

        function allPassed(pw) { return Object.keys(checks).every(function(k){ return checks[k].test(pw); }); }

        pwInput.addEventListener('input', function () {
            var passed = updateChecklist(this.value);
            bar.style.width = (passed * 20) + '%';
            bar.style.background = ['','#dc3545','#fd7e14','#fd7e14','#198754','#198754'][passed] || '#dc3545';
            updateConfirm();
        });

        cfInput.addEventListener('input', updateConfirm);

        function updateConfirm() {
            var msg = document.getElementById('profileConfirmMsg');
            var match = cfInput.value === pwInput.value;
            if (!cfInput.value) { msg.textContent = ''; return; }
            msg.style.color = match ? '#198754' : '#dc3545';
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
