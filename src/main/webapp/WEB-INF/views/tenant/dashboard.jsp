<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Trang chủ - Cổng cư dân"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="dashboard"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <!-- ── Hero ────────────────────────────────────────── -->
            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1>Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/> 👋</h1>
                    <p>Phòng <c:out value="${roomCode}"/> · <c:out value="${facilityName}"/></p>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${unpaidAmount > 0}">
                            <a href="${ctx}/tenant/invoices" class="btn-mintlify-primary" style="background-color: var(--hms-danger); border-color: var(--hms-danger);">
                                💳 Thanh toán hóa đơn nợ
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/tenant/invoices" class="btn-mintlify-primary">
                                💳 Xem hóa đơn
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- ── KPI Cards ─────────────────────── -->
            <div class="kpi-grid">
                <!-- Hóa đơn chưa TT -->
                <div class="kpi-surface-card ${unpaidAmount > 0 ? 'highlight-danger' : 'highlight-success'}"
                     onclick="location.href='${ctx}/tenant/invoices'" style="cursor:pointer">
                    <div class="kpi-icon" style="font-size:1.125rem">💰</div>
                    <span class="kpi-label">Hóa đơn chưa thanh toán</span>
                    <span class="kpi-value" style="font-size:1.5rem;letter-spacing:-1.5px">
                        <fmt:formatNumber value="${unpaidAmount}" pattern="#,##0"/>đ
                    </span>
                    <c:choose>
                        <c:when test="${not empty dueDateLabel}">
                            <span class="kpi-trend down">Hạn: <c:out value="${dueDateLabel}"/></span>
                        </c:when>
                        <c:otherwise>
                            <span class="kpi-trend up">Đã thanh toán đủ</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Thông báo mới -->
                <div class="kpi-surface-card"
                     onclick="location.href='${ctx}/tenant/notifications'" style="cursor:pointer">
                    <div class="kpi-icon">🔔</div>
                    <span class="kpi-label">Thông báo mới</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${unreadNotifications}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend ${unreadNotifications > 0 ? 'up' : ''}">Xem ngay</span>
                </div>

                <!-- Yêu cầu đang xử lý -->
                <div class="kpi-surface-card"
                     onclick="location.href='${ctx}/tenant/tickets'" style="cursor:pointer">
                    <div class="kpi-icon">📋</div>
                    <span class="kpi-label">Yêu cầu đang xử lý</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${pendingTickets}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend ${pendingTickets > 0 ? 'up' : ''}">Xem ngay</span>
                </div>
            </div>

            <!-- ── Quick Actions ────────────────────────────────── -->
            <div class="d-flex flex-wrap gap-3 mb-4">
                <a href="${ctx}/tenant/tickets/create" class="btn-mintlify-primary text-decoration-none">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="me-1">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Gửi yêu cầu hỗ trợ
                </a>
                <a href="${ctx}/tenant/notifications" class="btn-mintlify-secondary text-decoration-none">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    </svg>
                    Xem thông báo
                </a>
                <a href="${ctx}/tenant/invoices" class="btn-mintlify-secondary text-decoration-none">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                    </svg>
                    Xem hóa đơn
                </a>
                <a href="${ctx}/tenant/dependents" class="btn-mintlify-secondary text-decoration-none">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                    </svg>
                    Người phụ thuộc
                </a>
            </div>

            <!-- ── Main content 2 cột ───────────────────────────── -->
            <div class="row g-3">

                <!-- Thông báo mới nhất -->
                <div class="col-lg-8">
                    <div class="widget-surface" style="height:100%">
                        <div class="widget-surface-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <h3 class="mb-0">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent)" stroke-width="2"
                                     style="margin-right:6px;vertical-align:-2px">
                                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                                </svg>
                                Thông báo mới nhất
                            </h3>
                            <a href="${ctx}/tenant/notifications" class="text-decoration-none" style="font-size: 13px; font-weight: 500; color: var(--hms-primary);">
                                Xem tất cả &rarr;
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty latestNotifications}">
                                    <div class="list-group list-group-flush">
                                        <c:forEach var="notification" items="${latestNotifications}" varStatus="st">
                                            <div class="list-group-item list-group-item-action d-flex gap-3 align-items-start border-0 border-bottom py-3"
                                                 style="cursor:pointer; animation:fadeInUp 0.4s ease ${st.index * 0.04}s both"
                                                 onclick="location.href='${ctx}/tenant/notifications/${notification.id}'">
                                                <div class="rounded d-flex align-items-center justify-content-center text-white" 
                                                     style="width: 28px; height: 28px; background: var(--hms-primary); font-size: 0.625rem; font-weight: 800; flex-shrink: 0;">
                                                    🔔
                                                </div>
                                                <div class="flex-grow-1 min-w-0">
                                                    <p class="mb-0 fw-bold text-truncate" style="font-size: 0.875rem; color: ${notification.unread ? 'var(--hms-ink)' : 'var(--hms-slate)'}">
                                                        <c:out value="${notification.title}"/>
                                                    </p>
                                                    <p class="mb-1 text-muted small text-truncate">
                                                        <c:out value="${notification.summary}"/>
                                                    </p>
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <time class="text-muted" style="font-size: 0.6875rem;">
                                                            <c:out value="${notification.createdDateLabel}"/>
                                                        </time>
                                                        <c:if test="${notification.unread}">
                                                            <span class="badge-hms badge-info" style="font-size:0.625rem;padding:0.15rem 0.4rem">Mới</span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Chưa có thông báo"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Cột phụ bên phải -->
                <div class="col-lg-4">
                    <!-- Hóa đơn kỳ này -->
                    <div class="widget-surface mb-4">
                        <div class="widget-surface-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <h3 class="mb-0">Hóa đơn kỳ này</h3>
                            <a href="${ctx}/tenant/invoices" class="text-decoration-none" style="font-size: 13px; font-weight: 500; color: var(--hms-primary);">Xem chi tiết &rarr;</a>
                        </div>
                        <div class="widget-surface-body">
                            <c:choose>
                                <c:when test="${not empty currentInvoice}">
                                    <div class="text-center mb-3">
                                        <div class="fw-bold mb-1" style="font-size: 1.75rem; color: ${currentInvoice.overdue ? 'var(--hms-danger)' : currentInvoice.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-primary)'}">
                                            <fmt:formatNumber value="${currentInvoice.totalAmount}" pattern="#,##0"/>đ
                                        </div>
                                        <div class="text-muted small">Tổng cộng kỳ <c:out value="${currentInvoice.periodLabel}"/></div>
                                    </div>
                                    <div class="d-flex flex-column gap-2 text-center">
                                        <div class="small">
                                            Trạng thái: <span class="badge-hms ${currentInvoice.statusBadgeClass}"><c:out value="${currentInvoice.statusLabel}"/></span>
                                        </div>
                                        <div class="small text-muted mb-2">
                                            Hạn thanh toán: <strong><c:out value="${currentInvoice.dueDateLabel}"/></strong>
                                        </div>
                                        <c:if test="${currentInvoice.status == 'UNPAID' or currentInvoice.status == 'OVERDUE'}">
                                            <form method="post" action="${ctx}/tenant/payment/create">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                                <input type="hidden" name="invoiceId" value="${currentInvoice.id}"/>
                                                <input type="hidden" name="amount" value="${currentInvoice.totalAmount}"/>
                                                <button type="submit" class="btn-mintlify-primary w-100 justify-content-center">
                                                    💳 Thanh toán VNPAY
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center p-3 text-muted small border rounded">
                                        Chưa có hóa đơn kỳ này
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Quick info -->
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3 class="mb-0">Thông tin phòng</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <div class="d-flex justify-content-between p-3 border-bottom">
                                <span class="text-muted small">Cơ sở</span>
                                <strong class="small text-ink"><c:out value="${facilityName}"/></strong>
                            </div>
                            <div class="d-flex justify-content-between p-3 border-bottom">
                                <span class="text-muted small">Phòng</span>
                                <strong class="small text-ink"><c:out value="${roomCode}"/></strong>
                            </div>
                            <div class="d-flex justify-content-between p-3">
                                <span class="text-muted small">Trạng thái</span>
                                <span class="badge-hms badge-success">Đang thuê</span>
                            </div>
                        </div>
                    </div>
                </div>

            </div><!-- /row -->
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
