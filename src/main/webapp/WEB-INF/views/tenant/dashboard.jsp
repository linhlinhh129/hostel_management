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

            <!-- Hero header -->
            <div class="page-header hero-sky-gradient">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>
                            Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/> 👋
                        </h1>
                        <p>Phòng <c:out value="${roomCode}"/> · <c:out value="${facilityName}"/></p>
                    </div>
                </div>
            </div>

            <!-- KPI Cards -->
            <div class="kpi-grid">
                <!-- Hóa đơn chưa TT -->
                <div class="kpi-surface-card ${unpaidAmount > 0 ? 'highlight-danger' : ''}"
                     onclick="location.href='${ctx}/tenant/invoices'" style="cursor:pointer">
                    <div class="kpi-icon" style="font-size:1.125rem">💰</div>
                    <span class="kpi-label">Hóa đơn chưa TT</span>
                    <span class="kpi-value" style="font-size:1.5rem;letter-spacing:-1.5px">
                        <fmt:formatNumber value="${unpaidAmount}" pattern="#,##0"/>đ
                    </span>
                    <c:if test="${not empty dueDateLabel}">
                        <span class="kpi-trend down">Hạn: <c:out value="${dueDateLabel}"/></span>
                    </c:if>
                </div>

                <!-- Thông báo mới -->
                <div class="kpi-surface-card" onclick="location.href='${ctx}/tenant/notifications'" style="cursor:pointer">
                    <div class="kpi-icon">🔔</div>
                    <span class="kpi-label">Thông báo mới</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${unreadNotifications}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend up">Xem ngay</span>
                </div>

                <!-- Yêu cầu đang xử lý -->
                <div class="kpi-surface-card" onclick="location.href='${ctx}/tenant/tickets'" style="cursor:pointer">
                    <div class="kpi-icon">📋</div>
                    <span class="kpi-label">Yêu cầu đang xử lý</span>
                    <span class="kpi-value">
                        <fmt:formatNumber value="${pendingTickets}" groupingUsed="true"/>
                    </span>
                    <span class="kpi-trend up">Xem ngay</span>
                </div>
            </div>

            <!-- Quick Actions -->
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
            </div>

            <!-- Main content 2 cột -->
            <div class="row g-3">

                <!-- Thông báo mới nhất -->
                <div class="col-lg-8">
                    <div class="widget-surface">
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
                                    <c:forEach var="notification" items="${latestNotifications}">
                                        <a href="${ctx}/tenant/notifications/${notification.id}"
                                           class="tenant-card${notification.unread ? ' unread' : ''}">
                                            <div class="d-flex justify-content-between">
                                                <strong><c:out value="${notification.title}"/></strong>
                                                <c:if test="${notification.unread}"><span class="badge-hms badge-info">Mới</span></c:if>
                                            </div>
                                            <p class="text-muted mb-0 mt-1" style="font-size:0.875rem"><c:out value="${notification.summary}"/></p>
                                            <small class="text-muted"><c:out value="${notification.createdDateLabel}"/></small>
                                        </a>
                                    </c:forEach>
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

                <!-- Hóa đơn kỳ này -->
                <div class="col-lg-4">
                    <div class="widget-surface">
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
                                    <div class="tenant-card">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <div class="text-muted" style="font-size:0.8125rem">Kỳ <c:out value="${currentInvoice.periodLabel}"/></div>
                                                <div style="font-size:1.5rem;font-weight:700;color:${currentInvoice.overdue ? 'var(--hms-danger)' : 'var(--hms-primary)'}">
                                                    <fmt:formatNumber value="${currentInvoice.totalAmount}" pattern="#,##0"/>đ
                                                </div>
                                            </div>
                                            <span class="badge-hms ${currentInvoice.statusBadgeClass}"><c:out value="${currentInvoice.statusLabel}"/></span>
                                        </div>
                                        <div class="mt-2 pt-2 border-top" style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                            Hạn thanh toán: <strong><c:out value="${currentInvoice.dueDateLabel}"/></strong>
                                        </div>
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
                    <div class="widget-surface" style="margin-top:1rem">
                        <div class="widget-surface-header">
                            <h3>Thông tin phòng</h3>
                        </div>
                        <div class="widget-surface-body">
                            <div class="d-grid gap-2">
                                <div class="d-flex justify-content-between">
                                    <span class="text-muted">Phòng</span>
                                    <strong><c:out value="${roomCode}"/></strong>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <span class="text-muted">Cơ sở</span>
                                    <strong><c:out value="${facilityName}"/></strong>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <span class="text-muted">Trạng thái</span>
                                    <span class="badge-hms badge-success">Đang thuê</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
