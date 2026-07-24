<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="pageTitle" value="Quên mật khẩu - Quản lý Nhà trọ" />
        <jsp:include page="/WEB-INF/views/layout/head.jsp" />

        <body>
            <div class="auth-page-wrapper">
                <div class="auth-split-layout">
                    <div class="auth-form-side">
                        <div class="auth-card">

                            <!-- Brand -->
                            <div class="auth-brand auth-stagger-1 mb-4">
                                <div class="auth-brand-icon mb-3" style="width:48px;height:48px;font-size:1.25rem;">
                                    HomeX</div>
                                <h1 style="font-size:2rem;font-weight:600;letter-spacing:-1px;margin-bottom:0.5rem;">
                                    Quên mật khẩu</h1>
                                <p style="color:var(--hms-text-muted);font-size:1rem;">Nhập email đã đăng ký để nhận
                                    link đặt lại</p>
                            </div>

                            <div id="alertContainer" class="auth-stagger-2">
                                <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp" />
                            </div>

                            <!-- Success state -->
                            <div id="successState" class="auth-stagger-2"
                                style="display:none;text-align:center;padding:1.5rem 0">
                                <div style="font-size:3rem;margin-bottom:1rem">📬</div>
                                <h2 style="font-size:1.125rem;font-weight:700;margin:0 0 0.5rem">Email đã được gửi</h2>
                                <p id="successMessageText"
                                    style="font-size:0.875rem;color:var(--hms-stone);margin:0 0 1.5rem;line-height:1.6">
                                    Link đặt lại mật khẩu đã được gửi. Kiểm tra hộp thư (kể cả Spam).
                                </p>
                                <a href="${ctx}/login" class="btn-mintlify-secondary text-decoration-none"
                                    style="display:inline-flex;padding:9px 20px">
                                    ← Quay lại đăng nhập
                                </a>
                            </div>

                            <!-- Form state -->
                            <form id="forgotPasswordForm" class="auth-stagger-3">
                                <div class="mb-4">
                                    <label for="email" class="form-label">Địa chỉ Email</label>
                                    <input type="email" class="form-control" id="email" name="email"
                                        placeholder="email@example.com" maxlength="100" required autocomplete="email">
                                </div>

                                <button type="submit" id="submitBtn" class="btn btn-mintlify-primary w-100"
                                    style="border-radius:var(--hms-radius-full);padding:11px">
                                    Gửi link đặt lại mật khẩu
                                </button>
                                <a href="${ctx}/login" class="d-block text-center mt-3"
                                    style="font-size:0.8125rem;color:var(--hms-stone);text-decoration:none">
                                    ← Quay lại đăng nhập
                                </a>
                            </form>

                        </div>
                    </div>
                    <div class="auth-banner-side">
                        <img src="${ctx}/assets/img/login-illustration.png" alt="Hostel Management Illustration"
                            class="auth-illustration">
                    </div>
                </div>
            </div>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
            <script>
                document.getElementById('forgotPasswordForm').addEventListener('submit', function (e) {
                    e.preventDefault();
                    var btn = document.getElementById('submitBtn');
                    var email = document.getElementById('email').value;

                    btn.disabled = true;
                    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> \u0110ang g\u1EED\u0069...';

                    // Lấy CSRF token từ meta tag hoặc cookie nếu có
                    var csrfToken = '${csrfToken}';

                    fetch('${ctx}/api/v1/auth/forgot-password', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                            'X-CSRF-Token': csrfToken
                        },
                        body: JSON.stringify({ email: email })
                    })
                        .then(function (response) { return response.json(); })
                        .then(function (data) {
                            if (data.success) {
                                document.getElementById('forgotPasswordForm').style.display = 'none';
                                document.getElementById('successState').style.display = 'block';
                                document.getElementById('successMessageText').innerHTML =
                                    'N\u1EBFu \u0111\u1ECBa ch\u1EC9 <strong>' + email + '</strong> t\u1ED3n t\u1EA1i trong h\u1EC7 th\u1ED1ng, link \u0111\u1EB7t l\u1EA1i m\u1EADt kh\u1EA9u \u0111\u00E3 \u0111\u01B0\u1EE3c g\u1EED\u0069. Ki\u1EC3m tra h\u1ED9p th\u01B0 (k\u1EC3 c\u1EA3 Spam).';
                            } else {
                                alert(data.error ? data.error.message : 'C\u00F3 l\u1ED7i x\u1EA3y ra, vui l\u00F2ng th\u1EED l\u1EA1i.');
                                btn.disabled = false;
                                btn.innerHTML = 'G\u1EED\u0069 link \u0111\u1EB7t l\u1EA1i m\u1EADt kh\u1EA9u';
                            }
                        })
                        .catch(function () {
                            alert('L\u1ED7i k\u1EBFt n\u1ED1i. Vui l\u00F2ng th\u1EED l\u1EA1i sau.');
                            btn.disabled = false;
                            btn.innerHTML = 'G\u1EED\u0069 link \u0111\u1EB7t l\u1EA1i m\u1EADt kh\u1EA9u';
                        });
                });
            </script>
        </body>

        </html>