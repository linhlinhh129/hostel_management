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

                <!-- Brand -->
                <div class="auth-brand auth-stagger-1 mb-4">
                    <div class="auth-brand-icon mb-3" style="width:48px;height:48px;font-size:1.25rem;">HT</div>
                    <h1 style="font-size:2rem;font-weight:600;letter-spacing:-1px;margin-bottom:0.5rem;">Quên mật khẩu</h1>
                    <p style="color:var(--hms-text-muted);font-size:1rem;">Nhập email đã đăng ký để nhận link đặt lại</p>
                </div>

                <div id="alertContainer" class="auth-stagger-2">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                </div>

                <c:choose>
                    <c:when test="${emailSent}">
                        <!-- Success state -->
                        <div id="successState" class="auth-stagger-2" style="text-align:center;padding:1.5rem 0">
                            <div style="font-size:3rem;margin-bottom:1rem">📬</div>
                            <h2 style="font-size:1.125rem;font-weight:700;margin:0 0 0.5rem">Email đã được gửi</h2>
                            <p id="successMessageText" style="font-size:0.875rem;color:var(--hms-stone);margin:0 0 1.5rem;line-height:1.6">
                                Nếu địa chỉ <strong><c:out value="${submittedEmail}"/></strong> tồn tại trong hệ thống, link đặt lại mật khẩu đã được gửi. Kiểm tra hộp thư (kể cả Spam).
                            </p>
                            <a href="${ctx}/login"
                               class="btn-mintlify-secondary text-decoration-none"
                               style="display:inline-flex;padding:9px 20px">
                                ← Quay lại đăng nhập
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Form state -->
                        <form action="${ctx}/forgot-password" method="post" class="auth-stagger-3">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            
                            <div class="mb-4">
                                <label for="email" class="form-label-modern">Địa chỉ Email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       placeholder="email@example.com"
                                       required autocomplete="email"
                                       style="border-radius: 16px; padding: 0.75rem 1rem;">
                            </div>
                            <button type="submit" id="submitBtn"
                                    class="btn btn-mintlify-primary w-100"
                                    style="border-radius:var(--hms-radius-full);padding:11px">
                                Gửi link đặt lại mật khẩu
                            </button>
                            <a href="${ctx}/login"
                               class="d-block text-center mt-3"
                               style="font-size:0.8125rem;color:var(--hms-stone);text-decoration:none">
                                ← Quay lại đăng nhập
                            </a>
                        </form>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
        <div class="auth-banner-side">
            <img src="${ctx}/assets/img/login-illustration.png" alt="Hostel Management Illustration" class="auth-illustration">
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
