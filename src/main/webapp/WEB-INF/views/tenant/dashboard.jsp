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
            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>
                            Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/> 👋
                        </h1>
                        <p>Phòng <c:out value="${roomCode}"/> · <c:out value="${facilityName}"/></p>
                    </div>
                    <c:choose>
                        <c:when test="${unpaidAmount > 0}">
                            <a href="${ctx}/tenant/invoices" class="btn-accent"
                               style="position:relative;z-index:1;background:var(--hms-danger)">
                                💳 Thanh toán hóa đơn nợ
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/tenant/invoices" class="btn-accent"
                               style="position:relative;z-index:1">
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
            <div class="d-flex flex-wrap gap-2 mb-4">
                <a href="${ctx}/tenant/tickets/create" class="quick-action-btn primary">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Gửi yêu cầu hỗ trợ
                </a>
                <a href="${ctx}/tenant/notifications" class="quick-action-btn">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    </svg>
                    Xem thông báo
                </a>
                <a href="${ctx}/tenant/invoices" class="quick-action-btn">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                    </svg>
                    Xem hóa đơn
                </a>
                <a href="${ctx}/tenant/dependents" class="quick-action-btn">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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
                        <div class="widget-surface-header">
                            <h3>
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent)" stroke-width="2"
                                     style="margin-right:6px;vertical-align:-2px">
                                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                                </svg>
                                Thông báo mới nhất
                            </h3>
                            <a href="${ctx}/tenant/notifications"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);
                                      font-weight:600;text-decoration:none">
                                Xem tất cả →
                            </a>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty latestNotifications}">
                                    <ul style="list-style:none;margin:0;padding:0">
                                        <c:forEach var="notification" items="${latestNotifications}" varStatus="st">
                                            <li style="padding:0.625rem 1.25rem;
                                                       border-bottom:1px solid var(--hms-border-soft);
                                                       display:flex;gap:0.625rem;align-items:flex-start;
                                                       animation:fadeInUp 0.4s ease ${st.index * 0.04}s both">
                                                <div style="width:28px;height:28px;border-radius:var(--hms-radius-sm);
                                                            background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft));
                                                            display:flex;align-items:center;justify-content:center;
                                                            color:#fff;font-size:0.625rem;font-weight:800;flex-shrink:0">
                                                    🔔
                                                </div>
                                                <div style="flex:1;min-width:0;cursor:pointer"
                                                     onclick="location.href='${ctx}/tenant/notifications/${notification.id}'">
                                                    <p style="margin:0;font-size:0.875rem;font-weight:600;
                                                               overflow:hidden;text-overflow:ellipsis;white-space:nowrap;
                                                               color:${notification.unread ? 'var(--hms-ink)' : 'var(--hms-slate)'}">
                                                        <c:out value="${notification.title}"/>
                                                    </p>
                                                    <p style="margin:0;font-size:0.75rem;color:var(--hms-stone)">
                                                        <c:out value="${notification.summary}"/>
                                                    </p>
                                                    <div style="display:flex;justify-content:space-between;align-items:center;margin-top:2px">
                                                        <time style="font-size:0.6875rem;color:var(--hms-muted)">
                                                            <c:out value="${notification.createdDateLabel}"/>
                                                        </time>
                                                        <c:if test="${notification.unread}">
                                                            <span class="badge-hms badge-info" style="font-size:0.625rem;padding:0.15rem 0.4rem">Mới</span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
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
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Hóa đơn kỳ này</h3>
                            <a href="${ctx}/tenant/invoices"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Xem chi tiết →
                            </a>
                        </div>
                        <div class="widget-surface-body">
                            <c:choose>
                                <c:when test="${not empty currentInvoice}">
                                    <div class="text-center" style="margin-bottom:1rem">
                                        <div style="font-size:1.75rem;font-weight:800;
                                                    color:${currentInvoice.overdue ? 'var(--hms-danger)' : currentInvoice.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-primary)'}">
                                            <fmt:formatNumber value="${currentInvoice.totalAmount}" pattern="#,##0"/>đ
                                        </div>
                                        <div style="font-size:0.75rem;color:var(--hms-stone)">Tổng cộng kỳ <c:out value="${currentInvoice.periodLabel}"/></div>
                                    </div>
                                    <div class="d-grid gap-2 text-center">
                                        <div style="font-size:0.8125rem;color:var(--hms-stone)">
                                            Trạng thái: <span class="badge-hms ${currentInvoice.statusBadgeClass}"><c:out value="${currentInvoice.statusLabel}"/></span>
                                        </div>
                                        <div style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                            Hạn thanh toán: <strong><c:out value="${currentInvoice.dueDateLabel}"/></strong>
                                        </div>
                                        <c:if test="${currentInvoice.status == 'UNPAID' or currentInvoice.status == 'OVERDUE'}">
                                            <form method="post" action="${ctx}/tenant/payment/create" style="margin-top:0.5rem">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                                <input type="hidden" name="invoiceId" value="${currentInvoice.id}"/>
                                                <input type="hidden" name="amount" value="${currentInvoice.totalAmount}"/>
                                                <button type="submit" class="btn btn-mintlify-primary" style="width:100%;padding:0.4rem 0.5rem">
                                                    💳 Thanh toán VNPAY
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-3">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Chưa có hóa đơn kỳ này"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Quick info -->
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Thông tin phòng</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <ul style="list-style:none;margin:0;padding:0">
                                <li style="padding:0.75rem 1.25rem;border-bottom:1px solid var(--hms-border-soft);display:flex;justify-content:space-between">
                                    <span style="font-size:0.8125rem;color:var(--hms-stone)">Cơ sở</span>
                                    <strong style="font-size:0.875rem"><c:out value="${facilityName}"/></strong>
                                </li>
                                <li style="padding:0.75rem 1.25rem;border-bottom:1px solid var(--hms-border-soft);display:flex;justify-content:space-between">
                                    <span style="font-size:0.8125rem;color:var(--hms-stone)">Phòng</span>
                                    <strong style="font-size:0.875rem"><c:out value="${roomCode}"/></strong>
                                </li>
                                <li style="padding:0.75rem 1.25rem;display:flex;justify-content:space-between">
                                    <span style="font-size:0.8125rem;color:var(--hms-stone)">Trạng thái</span>
                                    <span class="badge-hms badge-success">Đang thuê</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

            </div><!-- /row -->
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
