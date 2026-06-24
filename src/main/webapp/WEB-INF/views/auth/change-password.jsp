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
                        <div class="form-text" style="font-size: 0.8rem; margin-top: 0.5rem;">Tối thiểu 8 ký tự, nên có chữ hoa, số và ký tự đặc biệt</div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu mới</label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" required minlength="8"
                               autocomplete="new-password"
                               placeholder="Nhập lại mật khẩu mới"
                               style="border-radius: 16px; padding: 0.75rem 1rem;">
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
// Password strength bar
document.getElementById('newPassword').addEventListener('input', function () {
    var pw = this.value;
    var score = 0;
    if (pw.length >= 8)  score++;
    if (pw.length >= 12) score++;
    if (/[A-Z]/.test(pw)) score++;
    if (/[0-9]/.test(pw)) score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;

    var bar = document.getElementById('pwStrengthBar');
    var colors = ['', '#dc2626', '#d97706', '#d97706', '#059669', '#059669'];
    bar.style.width  = (score * 20) + '%';
    bar.style.background = colors[score] || '#dc2626';
});
// Confirm match
document.getElementById('confirmPassword').addEventListener('input', function () {
    var match = this.value === document.getElementById('newPassword').value;
    this.style.borderColor = this.value ? (match ? 'var(--hms-accent)' : 'var(--hms-danger)') : '';
});
</script>
