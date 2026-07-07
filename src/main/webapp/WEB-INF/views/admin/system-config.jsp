<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Cấu hình hệ thống - Quản lý Nhà trọ" />
            <c:set var="pageRole" value="ADMIN" />
            <c:set var="activeMenu" value="system-config" />
            <jsp:include page="/WEB-INF/views/layout/head.jsp" />

            <body>
                <div class="app-shell">
                    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay"></div>
                    <div class="main-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                        <main class="page-content">
                            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                            <%-- ── Page Header ──────────────────────────────────── --%>
                                <div class="page-header hero-sky-gradient"
                                    style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                                    <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                                        <div>
                                            <h1>Cấu hình hệ thống</h1>
                                            <p>Quản lý thông số kết nối Email SMTP và cổng thanh toán VNPay</p>
                                        </div>
                                    </div>
                                </div>

                                <%-- ── Flash messages ───────────────────────────────── --%>
                                    <c:if test="${not empty successMessage}">
                                        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert"
                                            id="flash-success">
                                            <c:choose>
                                                <c:when test="${successMessage == 'email_updated'}">Cấu hình Email đã
                                                    được cập nhật thành công.</c:when>
                                                <c:when test="${successMessage == 'vnpay_updated'}">Cấu hình VNPay đã
                                                    được cập nhật thành công.</c:when>
                                                <c:otherwise>
                                                    <c:out value="${successMessage}" />
                                                </c:otherwise>
                                            </c:choose>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                                aria-label="Đóng"></button>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty errorMessage}">
                                        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert"
                                            id="flash-error">
                                            <c:out value="${errorMessage}" />
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                                aria-label="Đóng"></button>
                                        </div>
                                    </c:if>

                                    <%-- ── Tabs ─────────────────────────────────────────── --%>
                                        <style>
                                            #config-tabs .nav-link {
                                                color: var(--hms-stone);
                                                font-weight: 600;
                                                font-size: 0.875rem;
                                                border-radius: var(--hms-radius-full, 999px);
                                                padding: 7px 20px;
                                                border: 1px solid transparent;
                                                transition: all 0.18s ease;
                                                background: transparent;
                                            }

                                            #config-tabs .nav-link:hover:not(.active) {
                                                color: var(--hms-accent-deep, #007a6c);
                                                background: var(--hms-accent-bg, #f0faf8);
                                                border-color: var(--hms-border-soft, #e5e7eb);
                                            }

                                            #config-tabs .nav-link.active {
                                                color: var(--hms-accent-deep, #007a6c);
                                                background: var(--hms-accent-bg, #f0faf8);
                                                border-color: var(--hms-accent, #00c9a7);
                                                box-shadow: none;
                                            }
                                        </style>
                                        <ul class="nav nav-pills mb-4" id="config-tabs" role="tablist">
                                            <li class="nav-item" role="presentation">
                                                <button
                                                    class="nav-link ${empty param.tab or param.tab == 'email' ? 'active' : ''}"
                                                    id="email-tab" data-bs-toggle="pill" data-bs-target="#email-pane"
                                                    type="button" role="tab" aria-controls="email-pane"
                                                    aria-selected="${empty param.tab or param.tab == 'email'}"
                                                    style="font-weight:600">
                                                    ✉ Cấu hình Email SMTP
                                                </button>
                                            </li>
                                            <li class="nav-item" role="presentation">
                                                <button class="nav-link ${param.tab == 'vnpay' ? 'active' : ''}"
                                                    id="vnpay-tab" data-bs-toggle="pill" data-bs-target="#vnpay-pane"
                                                    type="button" role="tab" aria-controls="vnpay-pane"
                                                    aria-selected="${param.tab == 'vnpay'}" style="font-weight:600">
                                                    💳 Cấu hình VNPay
                                                </button>
                                            </li>
                                        </ul>

                                        <div class="tab-content" id="config-tabs-content">

                                            <%-- ══ Tab Email ══════════════════════════════════ --%>
                                                <div class="tab-pane fade ${empty param.tab or param.tab == 'email' ? 'show active' : ''}"
                                                    id="email-pane" role="tabpanel" aria-labelledby="email-tab">
                                                    <div class="row g-3">

                                                        <%-- View card --%>
                                                            <div class="col-lg-7">
                                                                <div class="widget-surface">
                                                                    <div class="widget-surface-header">
                                                                        <h3>Thông số Email SMTP hiện tại</h3>
                                                                        <button type="button"
                                                                            class="btn-mintlify-secondary"
                                                                            style="font-size:0.8125rem;padding:5px 14px"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#emailEditModal">
                                                                            Chỉnh sửa
                                                                        </button>
                                                                    </div>
                                                                    <div class="widget-surface-body">
                                                                        <dl class="row mb-0"
                                                                            style="font-size:0.9rem;row-gap:0.5rem">
                                                                            <dt class="col-sm-4"
                                                                                style="color:var(--hms-stone);font-weight:500">
                                                                                SMTP Host</dt>
                                                                            <dd class="col-sm-8 mb-0"
                                                                                style="font-weight:600;word-break:break-all">
                                                                                <c:out value="${emailConfig.host}" />
                                                                            </dd>

                                                                            <dt class="col-sm-4"
                                                                                style="color:var(--hms-stone);font-weight:500">
                                                                                SMTP Port</dt>
                                                                            <dd class="col-sm-8 mb-0"
                                                                                style="font-weight:600">
                                                                                <c:out value="${emailConfig.port}" />
                                                                            </dd>

                                                                            <dt class="col-sm-4"
                                                                                style="color:var(--hms-stone);font-weight:500">
                                                                                Tài khoản</dt>
                                                                            <dd class="col-sm-8 mb-0"
                                                                                style="font-weight:600;word-break:break-all">
                                                                                <c:out
                                                                                    value="${emailConfig.username}" />
                                                                            </dd>

                                                                            <dt class="col-sm-4"
                                                                                style="color:var(--hms-stone);font-weight:500">
                                                                                Mật khẩu</dt>
                                                                            <dd class="col-sm-8 mb-0"
                                                                                style="color:var(--hms-stone)">
                                                                                ••••••••
                                                                                <span class="badge-hms badge-neutral"
                                                                                    style="font-size:0.625rem;margin-left:4px">Đã
                                                                                    ẩn</span>
                                                                            </dd>

                                                                            <dt class="col-sm-4"
                                                                                style="color:var(--hms-stone);font-weight:500">
                                                                                Email gửi đi</dt>
                                                                            <dd class="col-sm-8 mb-0"
                                                                                style="font-weight:600;word-break:break-all">
                                                                                <c:out value="${emailConfig.from}" />
                                                                            </dd>
                                                                        </dl>

                                                                        <c:if test="${not empty emailConfig.updatedAt}">
                                                                            <div style="margin-top:1.25rem;padding-top:0.875rem;
                                                    border-top:1px solid var(--hms-border-soft);
                                                    font-size:0.75rem;color:var(--hms-stone)">
                                                                                Cập nhật lần cuối:
                                                                                <strong>
                                                                                    <c:out
                                                                                        value="${emailConfig.updatedAt}" />
                                                                                </strong>
                                                                                bởi <strong>
                                                                                    <c:out
                                                                                        value="${emailConfig.updatedBy}" />
                                                                                </strong>
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
                                                                        <div class="widget-surface-body"
                                                                            style="font-size:0.8125rem;color:var(--hms-stone);line-height:1.6">
                                                                            <p style="margin-bottom:0.75rem">Cấu hình
                                                                                này dùng để gửi:</p>
                                                                            <ul
                                                                                style="padding-left:1.25rem;margin-bottom:0.875rem">
                                                                                <li>Mật khẩu tạm thời khi tạo nhân sự
                                                                                    mới</li>
                                                                                <li>Link khôi phục mật khẩu</li>
                                                                            </ul>
                                                                            <div style="background:var(--hms-bg-soft);border-radius:var(--hms-radius-sm);
                                                padding:0.625rem 0.75rem;border-left:3px solid var(--hms-warning)">
                                                                                <strong style="color:var(--hms-ink)">Lưu
                                                                                    ý:</strong>
                                                                                Nhập <em>App Password</em> (không phải
                                                                                mật khẩu Gmail thường).
                                                                                Bỏ trống ô mật khẩu nếu không muốn thay
                                                                                đổi.
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                    </div>
                                                </div>

                                                <%-- ══ Tab VNPay ═══════════════════════════════════ --%>
                                                    <div class="tab-pane fade ${param.tab == 'vnpay' ? 'show active' : ''}"
                                                        id="vnpay-pane" role="tabpanel" aria-labelledby="vnpay-tab">
                                                        <div class="row g-3">

                                                            <%-- View card --%>
                                                                <div class="col-lg-7">
                                                                    <div class="widget-surface">
                                                                        <div class="widget-surface-header">
                                                                            <h3>Thông số kết nối VNPay hiện tại</h3>
                                                                            <button type="button"
                                                                                class="btn-mintlify-secondary"
                                                                                style="font-size:0.8125rem;padding:5px 14px"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#vnpayEditModal">
                                                                                Chỉnh sửa
                                                                            </button>
                                                                        </div>
                                                                        <div class="widget-surface-body">
                                                                            <dl class="row mb-0"
                                                                                style="font-size:0.9rem;row-gap:0.5rem">
                                                                                <dt class="col-sm-4"
                                                                                    style="color:var(--hms-stone);font-weight:500">
                                                                                    Pay URL</dt>
                                                                                <dd class="col-sm-8 mb-0"
                                                                                    style="font-weight:600;word-break:break-all;font-size:0.8125rem">
                                                                                    <c:out
                                                                                        value="${vnpayConfig.payUrl}" />
                                                                                </dd>

                                                                                <dt class="col-sm-4"
                                                                                    style="color:var(--hms-stone);font-weight:500">
                                                                                    Return URL</dt>
                                                                                <dd class="col-sm-8 mb-0"
                                                                                    style="font-weight:600;word-break:break-all;font-size:0.8125rem">
                                                                                    <c:out
                                                                                        value="${vnpayConfig.returnUrl}" />
                                                                                </dd>

                                                                                <dt class="col-sm-4"
                                                                                    style="color:var(--hms-stone);font-weight:500">
                                                                                    Mã Merchant</dt>
                                                                                <dd class="col-sm-8 mb-0"
                                                                                    style="font-weight:600">
                                                                                    <c:out
                                                                                        value="${vnpayConfig.tmnCode}" />
                                                                                </dd>

                                                                                <dt class="col-sm-4"
                                                                                    style="color:var(--hms-stone);font-weight:500">
                                                                                    Khóa bí mật</dt>
                                                                                <dd class="col-sm-8 mb-0"
                                                                                    style="color:var(--hms-stone)">
                                                                                    ••••••••
                                                                                    <span
                                                                                        class="badge-hms badge-neutral"
                                                                                        style="font-size:0.625rem;margin-left:4px">Đã
                                                                                        ẩn</span>
                                                                                </dd>

                                                                                <dt class="col-sm-4"
                                                                                    style="color:var(--hms-stone);font-weight:500">
                                                                                    API URL</dt>
                                                                                <dd class="col-sm-8 mb-0"
                                                                                    style="font-weight:600;word-break:break-all;font-size:0.8125rem">
                                                                                    <c:out
                                                                                        value="${vnpayConfig.apiUrl}" />
                                                                                </dd>
                                                                            </dl>

                                                                            <c:if
                                                                                test="${not empty vnpayConfig.updatedAt}">
                                                                                <div style="margin-top:1.25rem;padding-top:0.875rem;
                                                    border-top:1px solid var(--hms-border-soft);
                                                    font-size:0.75rem;color:var(--hms-stone)">
                                                                                    Cập nhật lần cuối:
                                                                                    <strong>
                                                                                        <c:out
                                                                                            value="${vnpayConfig.updatedAt}" />
                                                                                    </strong>
                                                                                    bởi <strong>
                                                                                        <c:out
                                                                                            value="${vnpayConfig.updatedBy}" />
                                                                                    </strong>
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
                                                                            <div class="widget-surface-body"
                                                                                style="font-size:0.8125rem;color:var(--hms-stone);line-height:1.6">
                                                                                <p style="margin-bottom:0.75rem">Cấu
                                                                                    hình này dùng để:</p>
                                                                                <ul
                                                                                    style="padding-left:1.25rem;margin-bottom:0.875rem">
                                                                                    <li>Tạo URL thanh toán QR cho người
                                                                                        thuê</li>
                                                                                    <li>Xác thực kết quả giao dịch từ
                                                                                        VNPay</li>
                                                                                </ul>
                                                                                <div style="background:var(--hms-bg-soft);border-radius:var(--hms-radius-sm);
                                                padding:0.625rem 0.75rem;border-left:3px solid var(--hms-danger)">
                                                                                    <strong
                                                                                        style="color:var(--hms-ink)">Cảnh
                                                                                        báo:</strong>
                                                                                    Sai <em>Pay URL</em> hoặc <em>API
                                                                                        URL</em> sẽ làm hỏng toàn bộ
                                                                                    luồng thanh toán.
                                                                                    Kiểm tra kỹ trước khi lưu.
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

                <%-- ══ Modal: Chỉnh sửa Email ════════════════════════════════════ --%>
                    <div class="modal fade" id="emailEditModal" tabindex="-1" aria-labelledby="emailEditModalLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                            <div class="modal-content"
                                style="border-radius:var(--hms-radius-lg);border:1px solid var(--hms-border)">
                                <form action="${ctx}/admin/system-config/email" method="post" id="formEmailEdit"
                                    novalidate>
                                    <input type="hidden" name="csrfToken" value="${csrfToken}" />

                                    <div class="modal-header"
                                        style="border-bottom:1px solid var(--hms-border-soft);padding:1.25rem 1.5rem">
                                        <h5 class="modal-title" id="emailEditModalLabel"
                                            style="font-size:1rem;font-weight:700;color:var(--hms-ink)">
                                            Chỉnh sửa cấu hình Email SMTP
                                        </h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Đóng"></button>
                                    </div>

                                    <div class="modal-body" style="padding:1.5rem">
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
                                                    value="<c:out value='${emailConfig.port}'/>" placeholder="587"
                                                    min="1" max="65535" required>
                                            </div>
                                            <div class="col-12">
                                                <label for="emailUsername" class="form-label">
                                                    Tài khoản Email <span class="text-danger">*</span>
                                                </label>
                                                <input type="text" class="form-control" id="emailUsername"
                                                    name="username" value="<c:out value='${emailConfig.username}'/>"
                                                    placeholder="email@example.com" required>
                                            </div>
                                            <div class="col-12">
                                                <label for="emailPassword" class="form-label">
                                                    <span style="margin-right:4px">🔒</span>
                                                    Mật khẩu ứng dụng (App Password)
                                                    <span class="text-danger">*</span>
                                                </label>
                                                <div class="input-group">
                                                    <input type="password" class="form-control" id="emailPassword"
                                                        name="password" placeholder="••••••••"
                                                        autocomplete="new-password" required>
                                                    <button type="button" class="btn btn-outline-secondary"
                                                        onclick="togglePassword('emailPassword', this)"
                                                        title="Ẩn / Hiện mật khẩu"
                                                        style="border-color:#dee2e6;background:#fff;padding:0 12px;font-size:1rem;line-height:1">
                                                        👁
                                                    </button>
                                                </div>
                                                <div class="form-text">
                                                    Nhập App Password của Gmail — không phải mật khẩu đăng nhập thông
                                                    thường.
                                                </div>
                                            </div>
                                            <div class="col-12">
                                                <label for="emailFrom" class="form-label">
                                                    Email gửi đi (From) <span class="text-danger">*</span>
                                                </label>
                                                <input type="text" class="form-control" id="emailFrom" name="from"
                                                    value="<c:out value='${emailConfig.from}'/>"
                                                    placeholder="email@example.com" required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="modal-footer"
                                        style="border-top:1px solid var(--hms-border-soft);padding:1rem 1.5rem;gap:8px">
                                        <button type="button" class="btn-mintlify-secondary"
                                            data-bs-dismiss="modal">Hủy</button>
                                        <button type="submit" class="btn btn-mintlify-primary" id="btnSaveEmail">
                                            Lưu cấu hình Email
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <%-- ══ Modal: Chỉnh sửa VNPay ═══════════════════════════════════ --%>
                        <div class="modal fade" id="vnpayEditModal" tabindex="-1" aria-labelledby="vnpayEditModalLabel"
                            aria-hidden="true">
                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                <div class="modal-content"
                                    style="border-radius:var(--hms-radius-lg);border:1px solid var(--hms-border)">
                                    <form action="${ctx}/admin/system-config/vnpay" method="post" id="formVNPayEdit"
                                        novalidate>
                                        <input type="hidden" name="csrfToken" value="${csrfToken}" />

                                        <div class="modal-header"
                                            style="border-bottom:1px solid var(--hms-border-soft);padding:1.25rem 1.5rem">
                                            <h5 class="modal-title" id="vnpayEditModalLabel"
                                                style="font-size:1rem;font-weight:700;color:var(--hms-ink)">
                                                Chỉnh sửa cấu hình VNPay
                                            </h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Đóng"></button>
                                        </div>

                                        <div class="modal-body" style="padding:1.5rem">
                                            <div class="row g-3">
                                                <div class="col-12">
                                                    <label for="vnpayPayUrl" class="form-label">
                                                        Pay URL <span class="text-danger">*</span>
                                                    </label>
                                                    <input type="text" class="form-control" id="vnpayPayUrl"
                                                        name="payUrl" value="<c:out value='${vnpayConfig.payUrl}'/>"
                                                        placeholder="https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"
                                                        required>
                                                </div>
                                                <div class="col-12">
                                                    <label for="vnpayReturnUrl" class="form-label">
                                                        Return URL <span class="text-danger">*</span>
                                                    </label>
                                                    <input type="text" class="form-control" id="vnpayReturnUrl"
                                                        name="returnUrl"
                                                        value="<c:out value='${vnpayConfig.returnUrl}'/>"
                                                        placeholder="http://localhost:8080/HostelManagement/tenant/invoices/vnpay-return"
                                                        required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="vnpayTmnCode" class="form-label">
                                                        Mã Merchant (TmnCode) <span class="text-danger">*</span>
                                                    </label>
                                                    <input type="text" class="form-control" id="vnpayTmnCode"
                                                        name="tmnCode" value="<c:out value='${vnpayConfig.tmnCode}'/>"
                                                        required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="vnpaySecretKey" class="form-label">
                                                        <span style="margin-right:4px">🔒</span>
                                                        Khóa bí mật (Secret Key)
                                                        <span class="text-danger">*</span>
                                                    </label>
                                                    <div class="input-group">
                                                        <input type="password" class="form-control" id="vnpaySecretKey"
                                                            name="secretKey" placeholder="••••••••"
                                                            autocomplete="new-password" required>
                                                        <button type="button" class="btn btn-outline-secondary"
                                                            onclick="togglePassword('vnpaySecretKey', this)"
                                                            title="Ẩn / Hiện khóa bí mật"
                                                            style="border-color:#dee2e6;background:#fff;padding:0 12px;font-size:1rem;line-height:1">
                                                            👁
                                                        </button>
                                                    </div>
                                                </div>
                                                <div class="col-12">
                                                    <label for="vnpayApiUrl" class="form-label">
                                                        API URL <span class="text-danger">*</span>
                                                    </label>
                                                    <input type="text" class="form-control" id="vnpayApiUrl"
                                                        name="apiUrl" value="<c:out value='${vnpayConfig.apiUrl}'/>"
                                                        placeholder="https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"
                                                        required>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="modal-footer"
                                            style="border-top:1px solid var(--hms-border-soft);padding:1rem 1.5rem;gap:8px">
                                            <button type="button" class="btn-mintlify-secondary"
                                                data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-mintlify-primary" id="btnSaveVNPay">
                                                Lưu cấu hình VNPay
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

                        <script>
                            /* ── Toggle ẩn/hiện password — expose global để onclick gọi được ── */
                            function togglePassword(inputId, btn) {
                                var input = document.getElementById(inputId);
                                if (!input) return;
                                var isHidden = input.type === 'password';
                                input.type = isHidden ? 'text' : 'password';
                                btn.textContent = isHidden ? '🙈' : '👁';
                                btn.title = isHidden ? 'Ẩn' : 'Hiện';
                            }

                            (function () {
                                'use strict';
                                ['flash-success', 'flash-error'].forEach(function (id) {
                                    var el = document.getElementById(id);
                                    if (el) setTimeout(function () {
                                        var bsAlert = bootstrap.Alert.getOrCreateInstance(el);
                                        if (bsAlert) bsAlert.close();
                                    }, 5000);
                                });

                                /* ── Active tab đúng theo success param ── */
                                var url = new URL(window.location.href);
                                var success = url.searchParams.get('success');
                                if (success === 'vnpay_updated') {
                                    var vnpayTab = document.getElementById('vnpay-tab');
                                    if (vnpayTab) bootstrap.Tab.getOrCreateInstance(vnpayTab).show();
                                }

                                /* ── Disable nút submit khi đang gửi ── */
                                function lockOnSubmit(formId, btnId) {
                                    var form = document.getElementById(formId);
                                    var btn = document.getElementById(btnId);
                                    if (!form || !btn) return;
                                    form.addEventListener('submit', function (e) {
                                        if (!form.checkValidity()) { e.preventDefault(); e.stopPropagation(); form.classList.add('was-validated'); return; }
                                        btn.disabled = true;
                                        btn.textContent = 'Đang lưu…';
                                    });
                                }
                                lockOnSubmit('formEmailEdit', 'btnSaveEmail');
                                lockOnSubmit('formVNPayEdit', 'btnSaveVNPay');
                            })();
                        </script>
            </body>

            </html>