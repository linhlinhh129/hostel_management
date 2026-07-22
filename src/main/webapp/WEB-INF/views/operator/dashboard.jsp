<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Dashboard Vận hành"/>
<c:set var="pageRole"  value="OPERATOR"/>
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

            <%-- ── Hero ─────────────────────────────────────────── --%>
            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="position:relative;z-index:1">
                    <h1>Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/></h1>
                    <p>Dashboard Vận hành · Theo dõi tiến độ điện nước và xử lý yêu cầu</p>
                </div>
                <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                    <a href="${ctx}/operator/meter-readings/update" class="btn-accent">Cập nhật Điện Nước</a>
                </div>
            </div>

            <%-- ── Quick Actions ─────────────────────────────────── --%>
            <div style="display:flex;flex-wrap:wrap;gap:10px;margin-bottom:1.5rem;align-items:center">
                <a href="${ctx}/operator/requests" class="quick-action-btn primary" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        <polyline points="14 2 14 8 20 8"/>
                    </svg>
                    Yêu cầu sửa chữa
                </a>
                <a href="${ctx}/operator/meter-readings" class="quick-action-btn" style="white-space:nowrap">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                    </svg>
                    Chỉ số điện nước
                </a>
            </div>

            <%-- ── KPI Cards ──────────────────────────────────────── --%>
            <div class="kpi-grid" style="margin-bottom:1.5rem">
                <div class="kpi-surface-card">
                    <span class="kpi-label">Phòng phụ trách</span>
                    <span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span>
                    <span class="kpi-trend">Tổng số phòng</span>
                </div>
                <div class="kpi-surface-card highlight-success">
                    <span class="kpi-label">Đã chốt điện nước</span>
                    <span class="kpi-value"><fmt:formatNumber value="${updatedMeterRooms}" groupingUsed="true"/></span>
                    <span class="kpi-trend up">Kỳ ${billingPeriodLabel}</span>
                </div>
                <div class="kpi-surface-card ${pendingMeterRooms > 0 ? 'highlight-danger' : ''}">
                    <span class="kpi-label">Chưa cập nhật</span>
                    <span class="kpi-value"><fmt:formatNumber value="${pendingMeterRooms}" groupingUsed="true"/></span>
                    <span class="kpi-trend">Còn lại</span>
                </div>
                <div class="kpi-surface-card ${ticketCountInProgress > 0 ? 'highlight-warning' : ''}">
                    <span class="kpi-label">Yêu cầu đang xử lý</span>
                    <span class="kpi-value"><fmt:formatNumber value="${ticketCountInProgress}" groupingUsed="true"/></span>
                    <span class="kpi-trend">Đang xử lý</span>
                </div>
            </div>

            <%-- ── Main row ───────────────────────────────────────── --%>
            <div class="row g-3">

                <%-- Cột trái: Tiến độ điện nước --%>
                <div class="col-lg-8">
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Tiến độ chốt Điện Nước tháng ${billingPeriodLabel}</h3>
                            <a href="${ctx}/operator/meter-readings"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Xem toàn bộ →
                            </a>
                        </div>
                        <div class="widget-surface-body">
                            <%-- Progress bar --%>
                            <div class="mb-4">
                                <div class="d-flex justify-content-between" style="font-size:0.875rem;font-weight:500;margin-bottom:6px">
                                    <span>Hoàn thành <strong>${meterUpdateProgress}%</strong></span>
                                    <span style="color:var(--hms-stone)">${updatedMeterRooms} / ${totalRooms} phòng</span>
                                </div>
                                <div style="height:8px;background:var(--hms-border-soft);border-radius:999px;overflow:hidden">
                                    <div style="width:${meterUpdateProgress}%;height:100%;
                                                background:linear-gradient(90deg,var(--hms-accent),#38bdf8);
                                                border-radius:999px;transition:width 0.8s ease"></div>
                                </div>
                            </div>

                            <%-- Bảng phòng chưa cập nhật --%>
                            <div style="font-size:0.875rem;font-weight:600;color:var(--hms-ink);margin-bottom:10px">
                                Phòng chưa cập nhật (Top 5)
                            </div>
                            <c:choose>
                                <c:when test="${not empty pendingMeterRoomList}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify">
                                            <thead>
                                                <tr>
                                                    <th>Mã phòng</th>
                                                    <th>Điện kỳ trước</th>
                                                    <th>Nước kỳ trước</th>
                                                    <th class="text-end">Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="room" items="${pendingMeterRoomList}">
                                                    <tr>
                                                        <td style="font-weight:600;color:var(--hms-ink)">${room.roomCode}</td>
                                                        <td style="color:var(--hms-stone)">
                                                            <c:choose>
                                                                <c:when test="${not empty room.previousElectricReading}">${room.previousElectricReading}</c:when>
                                                                <c:otherwise>—</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td style="color:var(--hms-stone)">
                                                            <c:choose>
                                                                <c:when test="${not empty room.previousWaterReading}">${room.previousWaterReading}</c:when>
                                                                <c:otherwise>—</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-end">
                                                            <span class="badge-hms badge-warning">CHƯA CẬP NHẬT</span>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Tất cả các phòng đã được chốt điện nước!"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <%-- Lịch hẹn sắp tới --%>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Lịch hẹn sắp tới</h3>
                            <span class="badge-hms badge-warning">${upcomingAppointments.size()} lịch hẹn</span>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${not empty upcomingAppointments}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify" style="font-size:0.8125rem">
                                            <thead>
                                                <tr>
                                                    <th>Phòng</th>
                                                    <th>Nội dung yêu cầu</th>
                                                    <th>Ngày giờ hẹn</th>
                                                    <th class="text-end">Hành động</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="req" items="${upcomingAppointments}">
                                                    <tr>
                                                        <td style="font-weight:600;color:var(--hms-ink)">${req.roomCode}</td>
                                                        <td style="max-width:220px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">
                                                            ${req.title}
                                                        </td>
                                                        <td style="color:var(--hms-accent-deep);font-weight:600">
                                                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px;vertical-align:-2px">
                                                                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                                                            </svg>
                                                            ${req.dashboardAppointmentTime}
                                                        </td>
                                                        <td class="text-end">
                                                            <a href="${ctx}/operator/requests/detail?id=${req.requestId}"
                                                               class="btn-mintlify-secondary text-decoration-none"
                                                               style="padding:4px 12px;font-size:0.75rem">Xử lý</a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-4">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Hiện tại không có lịch hẹn sửa chữa nào."/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <%-- Cột phải: Thống kê yêu cầu --%>
                <div class="col-lg-4">
                    <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                            <h3>Yêu cầu &amp; Sự cố</h3>
                            <a href="${ctx}/operator/requests"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Chi tiết →
                            </a>
                        </div>
                        <div class="widget-surface-body" style="padding:0">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:12px 16px;color:var(--hms-stone)">Chờ xử lý</td>
                                    <td style="padding:12px 16px;text-align:right">
                                        <span class="badge-hms badge-info">${ticketCountNew}</span>
                                    </td>
                                </tr>
                                <tr style="border-bottom:1px solid var(--hms-border-soft)">
                                    <td style="padding:12px 16px;color:var(--hms-stone)">Đang xử lý</td>
                                    <td style="padding:12px 16px;text-align:right">
                                        <span class="badge-hms badge-warning">${ticketCountInProgress}</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:12px 16px;color:var(--hms-stone)">Hoàn thành</td>
                                    <td style="padding:12px 16px;text-align:right">
                                        <span class="badge-hms badge-success">${ticketCountDone}</span>
                                    </td>
                                </tr>
                            </table>
                        </div>

                        <div style="padding:1.25rem;border-top:1px solid var(--hms-border-soft)">
                            <p style="font-size:0.8125rem;color:var(--hms-stone);margin-bottom:12px">
                                Theo dõi và tiếp nhận yêu cầu nhanh chóng để cải thiện trải nghiệm khách thuê.
                            </p>
                            <a href="${ctx}/operator/requests"
                               class="btn-mintlify-primary text-decoration-none"
                               style="display:block;text-align:center">
                                Đi tới Danh sách
                            </a>
                        </div>
                    </div>
                </div>

            </div><%-- /row --%>

        </main>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
    </div>
</div>
</body>
</html>
