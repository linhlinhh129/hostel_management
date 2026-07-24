<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Cấu hình hệ thống - Quản lý Nhà trọ"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="system-config"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- Page header --%>
            <div class="page-header hero-sky-gradient dash-hero">
                <div class="dash-hero-inner">
                    <div>
                        <h1>Cấu hình hệ thống</h1>
                        <p>Quản lý thông số kết nối Email SMTP và cổng thanh toán VNPay</p>
                    </div>
                </div>
            </div>

            <%-- Tabs --%>
            <ul class="nav nav-pills mb-4" id="config-tabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link ${empty param.tab or param.tab == 'email' ? 'active' : ''}"
                            id="email-tab" data-bs-toggle="pill" data-bs-target="#email-pane"
                            type="button" role="tab" aria-controls="email-pane"
                            aria-selected="${empty param.tab or param.tab == 'email'}">
                        ✉ Cấu hình Email SMTP
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link ${param.tab == 'vnpay' ? 'active' : ''}"
                            id="vnpay-tab" data-bs-toggle="pill" data-bs-target="#vnpay-pane"
                            type="button" role="tab" aria-controls="vnpay-pane"
                            aria-selected="${param.tab == 'vnpay'}">
                        💳 Cấu hình VNPay
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="config-tabs-content">

                <%-- ── Tab Email ──────────────────────────────────── --%>
                <div class="tab-pane fade ${empty param.tab or param.tab == 'email' ? 'show active' : ''}"
                     id="email-pane" role="tabpanel" aria-labelledby="email-tab">
                    <div class="row g-3">

                        <%-- View card --%>
                        <div class="col-lg-7">
                            <div class="widget-surface">
                                <div class="widget-surface-header">
                                    <h3>Thông số Email SMTP hiện tại</h3>
                                    <button type="button" class="btn-edit-sm"
                                            data-bs-toggle="modal" data-bs-target="#emailEditModal">
                                        Chỉnh sửa
                                    </button>
                                </div>
                                <div class="widget-surface-body">
                                    <dl class="row mb-0 config-dl config-dl--sm">
                                        <dt class="col-sm-4">SMTP Host</dt>
                                        <dd class="col-sm-8"><c:out value="${emailConfig.host}"/></dd>

                                        <dt class="col-sm-4">SMTP Port</dt>
                                        <dd class="col-sm-8"><c:out value="${emailConfig.port}"/></dd>

                                        <dt class="col-sm-4">Tài khoản</dt>
                                        <dd class="col-sm-8"><c:out value="${emailConfig.username}"/></dd>

                                        <dt class="col-sm-4">Mật khẩu</dt>
                                        <dd class="col-sm-8 config-dl__hidden">
                                            ••••••••
                                            <span class="badge-hms badge-neutral badge-hms--xs">Đã ẩn</span>
                                        </dd>
                                    </dl>

                                    <c:if test="${not empty emailConfig.updatedAt}">
                                        <div class="config-updated">
                                            Cập nhật lần cuối:
                                            <strong><c:out value="${emailConfig.updatedAt}"/></strong>
                                            bởi <strong><c:out value="${emailConfig.updatedBy}"/></strong>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <%-- Info side card --%>
                        <div class="col-lg-5">
                            <div class="widget-surface">
                                <div class="widget-surface-header">
                                    <h3>Hướng dẫn</h3>
                                </div>
                                <div class="widget-surface-body config-guide-body">
                                    <p class="config-guide-intro">Cấu hình này dùng để gửi:</p>
                                    <ul class="config-guide-list">
                                        <li>Mật khẩu tạm thời khi tạo nhân sự mới</li>
                                        <li>Link khôi phục mật khẩu</li>
                                    </ul>
                                    <div class="config-hint config-hint--warning">
                                        <strong class="config-hint__title">Lưu ý:</strong>
                                        Nhập <em>App Password</em> (không phải mật khẩu Gmail thường).
                                        Bỏ trống ô mật khẩu nếu không muốn thay đổi.
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                <%-- ── Tab VNPay ───────────────────────────────────── --%>
                <div class="tab-pane fade ${param.tab == 'vnpay' ? 'show active' : ''}"
                     id="vnpay-pane" role="tabpanel" aria-labelledby="vnpay-tab">
                    <div class="row g-3">

                        <%-- View card --%>
                        <div class="col-lg-7">
                            <div class="widget-surface">
                                <div class="widget-surface-header">
                                    <h3>Thông số kết nối VNPay hiện tại</h3>
                                    <button type="button" class="btn-edit-sm"
                                            data-bs-toggle="modal" data-bs-target="#vnpayEditModal">
                                        Chỉnh sửa
                                    </button>
                                </div>
                                <div class="widget-surface-body">
                                    <dl class="row mb-0 config-dl config-dl--sm">
                                        <dt class="col-sm-4">Pay URL</dt>
                                        <dd class="col-sm-8 config-dl__url">
                                            <c:out value="${vnpayConfig.payUrl}"/>
                                        </dd>

                                        <dt class="col-sm-4">Return URL</dt>
                                        <dd class="col-sm-8 config-dl__url">
                                            <c:out value="${vnpayConfig.returnUrl}"/>
                                        </dd>

                                        <dt class="col-sm-4">Mã Merchant</dt>
                                        <dd class="col-sm-8"><c:out value="${vnpayConfig.tmnCode}"/></dd>

                                        <dt class="col-sm-4">Khóa bí mật</dt>
                                        <dd class="col-sm-8 config-dl__hidden">
                                            ••••••••
                                            <span class="badge-hms badge-neutral badge-hms--xs">Đã ẩn</span>
                                        </dd>

                                        <dt class="col-sm-4">API URL</dt>
                                        <dd class="col-sm-8 config-dl__url">
                                            <c:out value="${vnpayConfig.apiUrl}"/>
                                        </dd>
                                    </dl>

                                    <c:if test="${not empty vnpayConfig.updatedAt}">
                                        <div class="config-updated">
                                            Cập nhật lần cuối:
                                            <strong><c:out value="${vnpayConfig.updatedAt}"/></strong>
                                            bởi <strong><c:out value="${vnpayConfig.updatedBy}"/></strong>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <%-- Info side card --%>
                        <div class="col-lg-5">
                            <div class="widget-surface">
                                <div class="widget-surface-header">
                                    <h3>Hướng dẫn</h3>
                                </div>
                                <div class="widget-surface-body config-guide-body">
                                    <p class="config-guide-intro">Cấu hình này dùng để:</p>
                                    <ul class="config-guide-list">
                                        <li>Tạo URL thanh toán QR cho người thuê</li>
                                        <li>Xác thực kết quả giao dịch từ VNPay</li>
                                    </ul>
                                    <div class="config-hint config-hint--danger">
                                        <strong class="config-hint__title">Cảnh báo:</strong>
                                        Sai <em>Pay URL</em> hoặc <em>API URL</em> sẽ làm hỏng
                                        toàn bộ luồng thanh toán. Kiểm tra kỹ trước khi lưu.
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

            </div><%-- /tab-content --%>

        </main>
    </div>
</div>

<%-- Modal: Chỉnh sửa Email --%>
<div class="modal fade" id="emailEditModal" tabindex="-1"
     aria-labelledby="emailEditModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content config-modal-content">
            <form action="${ctx}/admin/system-config/email" method="post"
                  id="formEmailEdit" novalidate>
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                <div class="modal-header config-modal-header">
                    <h5 class="modal-title config-modal-title" id="emailEditModalLabel">
                        Chỉnh sửa cấu hình Email SMTP
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>

                <div class="modal-body config-modal-body">
                    <div class="row g-3">
                        <div class="col-md-8">
                            <label for="emailHost" class="form-label">
                                SMTP Host <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="emailHost" name="host"
                                   value="<c:out value='${emailConfig.host}'/>"
                                   placeholder="smtp.gmail.com" required>
                        </div>
                        <div class="col-md-4">
                            <label for="emailPort" class="form-label">
                                SMTP Port <span class="text-danger">*</span>
                            </label>
                            <input type="number" class="form-control" id="emailPort" name="port"
                                   value="<c:out value='${emailConfig.port}'/>"
                                   placeholder="587" min="1" max="65535" required>
                        </div>
                        <div class="col-12">
                            <label for="emailUsername" class="form-label">
                                Tài khoản Email <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="emailUsername" name="username"
                                   value="<c:out value='${emailConfig.username}'/>"
                                   placeholder="email@example.com" required>
                        </div>
                        <div class="col-12">
                            <label for="emailPassword" class="form-label">
                                🔒 Mật khẩu ứng dụng (App Password) <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="emailPassword"
                                       name="password" placeholder="••••••••"
                                       autocomplete="new-password" required>
                                <button type="button" class="btn btn-outline-secondary btn-toggle-password"
                                        onclick="togglePassword('emailPassword', this)"
                                        title="Ẩn / Hiện mật khẩu">👁</button>
                            </div>
                            <div class="form-text">
                                Nhập App Password của Gmail — không phải mật khẩu đăng nhập thông thường.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer config-modal-footer">
                    <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-mintlify-primary" id="btnSaveEmail">
                        Lưu cấu hình Email
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Modal: Chỉnh sửa VNPay --%>
<div class="modal fade" id="vnpayEditModal" tabindex="-1"
     aria-labelledby="vnpayEditModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content config-modal-content">
            <form action="${ctx}/admin/system-config/vnpay" method="post"
                  id="formVNPayEdit" novalidate>
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                <div class="modal-header config-modal-header">
                    <h5 class="modal-title config-modal-title" id="vnpayEditModalLabel">
                        Chỉnh sửa cấu hình VNPay
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>

                <div class="modal-body config-modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label for="vnpayPayUrl" class="form-label">
                                Pay URL <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="vnpayPayUrl" name="payUrl"
                                   value="<c:out value='${vnpayConfig.payUrl}'/>"
                                   placeholder="https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" required>
                        </div>
                        <div class="col-12">
                            <label for="vnpayReturnUrl" class="form-label">
                                Return URL <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="vnpayReturnUrl" name="returnUrl"
                                   value="<c:out value='${vnpayConfig.returnUrl}'/>"
                                   placeholder="http://localhost:8080/hostel-management/tenant/invoices/vnpay-return"
                                   required>
                        </div>
                        <div class="col-md-6">
                            <label for="vnpayTmnCode" class="form-label">
                                Mã Merchant (TmnCode) <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="vnpayTmnCode" name="tmnCode"
                                   value="<c:out value='${vnpayConfig.tmnCode}'/>" required>
                        </div>
                        <div class="col-md-6">
                            <label for="vnpaySecretKey" class="form-label">
                                🔒 Khóa bí mật (Secret Key) <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <input type="password" class="form-control" id="vnpaySecretKey"
                                       name="secretKey" placeholder="••••••••"
                                       autocomplete="new-password" required>
                                <button type="button" class="btn btn-outline-secondary btn-toggle-password"
                                        onclick="togglePassword('vnpaySecretKey', this)"
                                        title="Ẩn / Hiện khóa bí mật">👁</button>
                            </div>
                        </div>
                        <div class="col-12">
                            <label for="vnpayApiUrl" class="form-label">
                                API URL <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="vnpayApiUrl" name="apiUrl"
                                   value="<c:out value='${vnpayConfig.apiUrl}'/>"
                                   placeholder="https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"
                                   required>
                        </div>
                    </div>
                </div>

                <div class="modal-footer config-modal-footer">
                    <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-mintlify-primary" id="btnSaveVNPay">
                        Lưu cấu hình VNPay
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    if (!input) return;
    var isHidden = input.type === 'password';
    input.type      = isHidden ? 'text'    : 'password';
    btn.textContent = isHidden ? '🙈'     : '👁';
    btn.title       = isHidden ? 'Ẩn'     : 'Hiện';
}

(function () {
    'use strict';

    /* Mở đúng tab theo success param */
    var success = new URL(window.location.href).searchParams.get('success');
    if (success === 'vnpay_updated') {
        var vnpayTab = document.getElementById('vnpay-tab');
        if (vnpayTab) bootstrap.Tab.getOrCreateInstance(vnpayTab).show();
    }

    /* Disable nút submit khi đang gửi + validate */
    function lockOnSubmit(formId, btnId) {
        var form = document.getElementById(formId);
        var btn  = document.getElementById(btnId);
        if (!form || !btn) return;
        form.addEventListener('submit', function (e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                form.classList.add('was-validated');
                return;
            }
            btn.disabled    = true;
            btn.textContent = 'Đang lưu…';
        });
    }
    lockOnSubmit('formEmailEdit', 'btnSaveEmail');
    lockOnSubmit('formVNPayEdit', 'btnSaveVNPay');
}());
</script>

</body>
</html>
