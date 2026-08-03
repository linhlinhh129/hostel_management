<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Quên mật khẩu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout">
        <div class="auth-form-side">
            <div class="auth-card">

                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-logo mb-3">IH</div>
                    <h1 class="auth-heading">Quên mật khẩu</h1>
                    <p class="auth-subtitle">Nhập email đã đăng ký để nhận link đặt lại</p>
                </div>

                <div id="alertContainer" class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp"/>
                </div>

                <%-- Success state --%>
                <c:if test="${not empty successMessage}">
                    <div id="successState" class="auth-success-state auth-stagger-2">
                        <div class="auth-success-icon">📬</div>
                        <h2 class="auth-success-title">Email đã được gửi</h2>
                        <p id="successMessageText" class="auth-success-text">
                            ${successMessage}
                        </p>
                        <a href="${ctx}/login" class="btn-mintlify-secondary text-decoration-none auth-back-btn">
                            ← Quay lại đăng nhập
                        </a>
                    </div>
                </c:if>

                <%-- Form state --%>
                <c:if test="${empty successMessage}">
                    <form id="forgotPasswordForm" action="${ctx}/forgot-password" method="post" class="auth-stagger-3">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <div class="mb-4">
                            <label for="email" class="form-label">Địa chỉ Email</label>
                            <input type="email" class="form-control auth-input" id="email" name="email"
                                   placeholder="email@example.com" maxlength="100"
                                   required autocomplete="email">
                        </div>

                        <button type="submit" id="submitBtn"
                                class="btn btn-mintlify-primary w-100 auth-submit-btn">
                            Gửi link đặt lại mật khẩu
                        </button>

                        <a href="${ctx}/login" class="d-block text-center mt-3 auth-link-sm">
                            ← Quay lại đăng nhập
                        </a>
                    </form>
                </c:if>

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
