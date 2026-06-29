<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Dashboard Vận hành"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="dashboard"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="${pageTitle}" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                    <!-- Hero Header -->
                    <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
                        <h1>Dashboard Vận hành</h1>
                    </div>

                    <!-- Thao tác nhanh (Quick Actions) -->
                    <div class="d-flex flex-wrap gap-3 mb-4">
                        <a href="${ctx}/operator/incidents/create" class="btn-mintlify-primary text-decoration-none">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                <line x1="12" y1="9" x2="12" y2="13"></line>
                                <line x1="12" y1="17" x2="12.01" y2="17"></line>
                            </svg>
                            Báo cáo sự cố mới
                        </a>
                        <a href="${ctx}/operator/meter-readings/update" class="btn-mintlify-secondary text-decoration-none" style="background-color: var(--color-surface); border: 1px solid var(--color-hairline);">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polyline points="12 6 12 12 16 14"></polyline>
                            </svg>
                            Cập nhật Điện Nước
                        </a>
                    </div>

                    <!-- KPI Cards Row -->
                    <div class="kpi-grid mb-4">
                        <div class="kpi-surface-card">
                            <span class="kpi-label">Phòng phụ trách</span>
                            <span class="kpi-value"><fmt:formatNumber value="${totalRooms}" groupingUsed="true"/></span>
                        </div>
                        <div class="kpi-surface-card">
                            <span class="kpi-label">Đã chốt điện nước</span>
                            <span class="kpi-value"><fmt:formatNumber value="${updatedMeterRooms}" groupingUsed="true"/></span>
                        </div>
                        <div class="kpi-surface-card kpi-card-featured" style="border: 1px solid var(--hms-success); box-shadow: 0 4px 12px rgba(0, 212, 164, 0.1);">
                            <span class="kpi-label text-success">Chưa cập nhật</span>
                            <span class="kpi-value text-success"><fmt:formatNumber value="${pendingMeterRooms}" groupingUsed="true"/></span>
                        </div>
                        <div class="kpi-surface-card">
                            <span class="kpi-label">Yêu cầu đang xử lý</span>
                            <span class="kpi-value"><fmt:formatNumber value="${ticketCountInProgress}" groupingUsed="true"/></span>
                        </div>
                    </div>

                    <!-- Widgets Row -->
                    <div class="row g-4">
                        <!-- Cột Trái: Tiến độ điện nước + Bảng -->
                        <div class="col-lg-8">
                            <div class="widget-surface h-100">
                                <div class="widget-surface-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                                    <h3 class="mb-0">Tiến độ chốt Điện Nước tháng ${billingPeriodLabel}</h3>
                                    <a href="${ctx}/operator/meter-readings" class="btn-mintlify-secondary text-decoration-none" style="font-size: 13px;">Xem toàn bộ</a>
                                </div>
                                <div class="widget-surface-body">
                                    <div class="mb-4">
                                        <div class="d-flex justify-content-between text-sm" style="font-size: 14px; font-weight: 500;">
                                            <span>Hoàn thành ${meterUpdateProgress}%</span>
                                            <span class="text-muted">${updatedMeterRooms} / ${totalRooms} phòng</span>
                                        </div>
                                        <div class="progress mt-2 mb-3" style="height: 8px;">
                                            <div class="progress-bar bg-success" role="progressbar" style="width: ${meterUpdateProgress}%;"></div>
                                        </div>
                                    </div>

                                    <h4 style="font-size: 14px; font-weight: 600; margin-bottom: 12px; margin-top: 1rem;">Phòng chưa cập nhật (Top 5)</h4>
                                    <c:choose>
                                        <c:when test="${not empty pendingMeterRoomList}">
                                            <div class="table-responsive">
                                                <table class="table-mintlify">
                                                    <thead>
                                                        <tr>
                                                            <th>Mã phòng</th>
                                                            <th>Số điện kỳ trước</th>
                                                            <th>Số nước kỳ trước</th>
                                                            <th class="text-end">Hành động</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="room" items="${pendingMeterRoomList}">
                                                            <tr>
                                                                <td class="fw-bold text-ink">${room.roomCode}</td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${not empty room.previousElectricReading}">${room.previousElectricReading}</c:when>
                                                                        <c:otherwise>-</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${not empty room.previousWaterReading}">${room.previousWaterReading}</c:when>
                                                                        <c:otherwise>-</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="text-end">
                                                                    <span class="badge-hms badge-neutral">CHƯA CẬP NHẬT</span>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state border rounded" style="border-color: var(--color-hairline-soft) !important;">
                                            🎉 Tuyệt vời! Tất cả các phòng đã được chốt số điện nước.
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                </div>
                            </div>
                        </div>

                        <!-- Cột Phải: Thống kê yêu cầu -->
                        <div class="col-lg-4">
                            <div class="widget-surface h-100">
                                <div class="widget-surface-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                                    <h3 class="mb-0">Yêu cầu & Sự cố</h3>
                                    <a href="${ctx}/operator/requests" class="text-decoration-none" style="color: var(--color-brand-tag); font-size: 13px; font-weight: 500;">Chi tiết &rarr;</a>
                                </div>
                                <div class="widget-surface-body">

                                <div class="d-flex flex-column gap-3">
                                    <div class="d-flex justify-content-between align-items-center p-3 rounded" style="background-color: var(--color-surface); border: 1px solid var(--color-hairline-soft);">
                                        <span style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Sự cố báo cáo (Pending)</span>
                                        <span class="mintlify-badge-status-pending" style="font-size: 14px;">${ticketCountNew}</span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center p-3 rounded" style="background-color: var(--color-surface); border: 1px solid var(--color-hairline-soft);">
                                        <span style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Đang xử lý</span>
                                        <span class="mintlify-badge-status-inprogress" style="font-size: 14px;">${ticketCountInProgress}</span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center p-3 rounded" style="background-color: var(--color-surface); border: 1px solid var(--color-hairline-soft);">
                                        <span style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Hoàn thành</span>
                                        <span class="mintlify-badge-status-resolved" style="background-color: var(--color-surface-soft); color: var(--color-brand-annotate); padding: 2px 8px; border-radius: 4px; font-weight: 600; font-size: 14px;">${ticketCountDone}</span>
                                    </div>
                                </div>
                                
                                <div class="mt-4 p-4 rounded text-center" style="background-color: var(--color-canvas); border: 1px dashed var(--color-hairline);">
                                    <p style="font-size: 13px; color: var(--color-steel); margin-bottom: 16px;">Theo dõi và tiếp nhận yêu cầu nhanh chóng để cải thiện trải nghiệm khách thuê.</p>
                                    <a href="${ctx}/operator/requests" class="btn-mintlify-primary" style="width: 100%; display: block; text-align: center; text-decoration: none;">Đi tới Danh sách</a>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Row: Lịch hẹn hôm nay -->
                    <div class="row g-4 mt-2 mb-5">
                        <div class="col-12">
                            <div class="widget-surface h-100">
                                <div class="widget-surface-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                                    <h3 class="mb-0">Lịch hẹn hôm nay</h3>
                                    <span class="badge-hms badge-warning">${todaysAppointments.size()} lịch hẹn</span>
                                </div>
                                <div class="widget-surface-body">
                                    <c:choose>
                                        <c:when test="${not empty todaysAppointments}">
                                            <div class="table-responsive">
                                                <table class="table-mintlify">
                                                    <thead>
                                                        <tr>
                                                            <th>Phòng</th>
                                                            <th>Nội dung yêu cầu</th>
                                                            <th>Ngày giờ hẹn</th>
                                                            <th class="text-end">Hành động</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="req" items="${todaysAppointments}">
                                                            <tr>
                                                                <td class="fw-bold text-ink">${req.roomCode}</td>
                                                                <td style="max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${req.title}">${req.title}</td>
                                                                <td class="text-accent fw-bold">
                                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 4px; margin-top: -2px;">
                                                                        <circle cx="12" cy="12" r="10"></circle>
                                                                        <polyline points="12 6 12 12 16 14"></polyline>
                                                                    </svg>
                                                                    ${req.rejectionReason}
                                                                </td>
                                                                <td class="text-end">
                                                                    <a href="${ctx}/operator/requests/detail?id=${req.requestId}" class="btn-mintlify-secondary text-decoration-none" style="padding: 4px 12px; font-size: 12px;">Xử lý</a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state border rounded" style="border-color: var(--color-hairline-soft) !important;">
                                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="var(--color-steel)" stroke-width="1.5" class="mb-2">
                                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                                <line x1="3" y1="10" x2="21" y2="10"></line>
                                            </svg><br>
                                            Hôm nay bạn không có lịch hẹn sửa chữa nào.
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>
</body>
</html>
