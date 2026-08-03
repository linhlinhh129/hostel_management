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

                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-logo mb-3">IH</div>
                    <h1 class="auth-heading">
                        <c:choose>
                            <c:when test="${forceChange}">Đặt mật khẩu mới</c:when>
                            <c:otherwise>Đổi mật khẩu</c:otherwise>
                        </c:choose>
                    </h1>
                    <p class="auth-subtitle">
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

                <div class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp"/>
                </div>

                <form action="${ctx}/change-password" method="post" class="auth-stagger-3">
                    <input type="hidden" name="csrfToken"    value="${csrfToken}"/>
                    <input type="hidden" name="forceChange"  value="${forceChange}"/>

                    <div class="mb-4">
                        <label for="currentPassword" class="form-label-modern">
                            Mật khẩu hiện tại
                            <c:if test="${forceChange}">
                                <span class="auth-force-note">(mật khẩu tạm thời)</span>
                            </c:if>
                        </label>
                        <input type="password" class="form-control auth-input"
                               id="currentPassword" name="currentPassword"
                               required autocomplete="current-password"
                               placeholder="Nhập mật khẩu hiện tại">
                    </div>

                    <div class="mb-4">
                        <label for="newPassword" class="form-label-modern">Mật khẩu mới</label>
                        <input type="password" class="form-control auth-input"
                               id="newPassword" name="newPassword"
                               required minlength="8" autocomplete="new-password"
                               pattern="(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}"
                               title="Ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
                               placeholder="Ít nhất 8 ký tự">
                        <div class="text-muted small mt-2">
                            * Mật khẩu cần ít nhất 8 ký tự, bao gồm chữ hoa (A-Z), chữ thường (a-z), chữ số (0-9) và ký tự đặc biệt.
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label-modern">Xác nhận mật khẩu mới</label>
                        <input type="password" class="form-control auth-input"
                               id="confirmPassword" name="confirmPassword"
                               required minlength="8" autocomplete="new-password"
                               placeholder="Nhập lại mật khẩu mới">

                    <div class="text-center auth-stagger-4 mb-3">
                        <button type="submit"
                                class="btn btn-mintlify-primary py-2 px-5 auth-submit-btn">
                            Cập nhật mật khẩu
                        </button>
                    </div>

                    <c:if test="${!forceChange}">
                        <div class="text-center auth-stagger-4">
                            <a href="${ctx}/login" class="auth-link-sm">← Quay lại</a>
                        </div>
                    </c:if>
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
</script>
</body>
</html>
