<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Quên mật khẩu - Quản lý Nhà trọ"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="auth-page-wrapper">
    <div class="auth-split-layout" style="max-width:600px;min-height:auto">
        <div class="auth-form-side" style="flex:1;padding:2.5rem">
            <div class="auth-card">

                <!-- Brand -->
                <div class="auth-stagger-1 mb-4 text-center">
                    <div style="width:52px;height:52px;border-radius:var(--hms-radius-md);
                                background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft));
                                display:flex;align-items:center;justify-content:center;
                                color:#fff;font-weight:800;font-size:1.125rem;
                                margin:0 auto 1rem;
                                box-shadow:0 4px 16px rgba(0,212,164,0.30)">HT</div>
                    <h1 style="font-size:1.5rem;font-weight:700;letter-spacing:-0.5px;margin:0 0 0.375rem">
                        Quên mật khẩu
                    </h1>
                    <p style="color:var(--hms-stone);font-size:0.875rem;margin:0;line-height:1.5">
                        Nhập email đã đăng ký để nhận link đặt lại mật khẩu
                    </p>
                </div>

                <!-- Success state -->
                <c:if test="${emailSent}">
                    <div class="auth-stagger-2" style="text-align:center;padding:1.5rem 0">
                        <div style="font-size:3rem;margin-bottom:1rem">📬</div>
                        <h2 style="font-size:1.125rem;font-weight:700;margin:0 0 0.5rem">Email đã được gửi</h2>
                        <p style="font-size:0.875rem;color:var(--hms-stone);margin:0 0 1.5rem;line-height:1.6">
                            Nếu địa chỉ <strong><c:out value="${submittedEmail}"/></strong> tồn tại trong hệ thống,
                            link đặt lại mật khẩu đã được gửi. Kiểm tra hộp thư (kể cả Spam).
                        </p>
                        <a href="${ctx}/login"
                           class="btn-mintlify-secondary text-decoration-none"
                           style="display:inline-flex;padding:9px 20px">
                            ← Quay lại đăng nhập
                        </a>
                    </div>
                </c:if>

                <!-- Form state -->
                <c:if test="${!emailSent}">
                    <div class="auth-stagger-2">
                        <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
                    </div>

                    <form action="${ctx}/forgot-password" method="post" class="auth-stagger-3">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <div class="mb-4">
                            <label for="email" class="form-label">Địa chỉ Email</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<c:out value='${email}'/>"
                                   placeholder="email@example.com"
                                   required autocomplete="email">
                        </div>
                        <button type="submit"
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
                </c:if>

            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
