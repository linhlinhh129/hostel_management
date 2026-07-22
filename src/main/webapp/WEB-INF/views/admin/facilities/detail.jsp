<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết cơ sở - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="hostels"/>
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
            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1><c:out value="${facility.code}"/> · <c:out value="${facility.name}"/></h1>
                    <p><c:out value="${facility.address}"/></p>
                </div>
                <div class="d-flex gap-2 flex-wrap" style="position:relative;z-index:1">
                    <c:if test="${facility.status == 'DRAFT'}">
                        <a href="${ctx}/admin/facilities/${facility.id}/edit" class="quick-action-btn">Sửa</a>
                        <form method="post" action="${ctx}/admin/facilities/${facility.id}/activate" style="margin:0">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <button type="submit" class="quick-action-btn primary"
                                    onclick="return confirm('Kích hoạt cơ sở và sinh danh sách phòng?')">
                                Kích hoạt
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${facility.status == 'ACTIVE'}">
                        <a href="${ctx}/admin/facilities/${facility.id}/edit" class="quick-action-btn">Sửa</a>
                        <form method="post" action="${ctx}/admin/facilities/${facility.id}/deactivate" style="margin:0">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <button type="submit" class="quick-action-btn" style="color:var(--hms-danger)"
                                    onclick="return confirm('Vô hiệu hóa cơ sở này?')">
                                Vô hiệu hóa
                            </button>
                        </form>
                    </c:if>
                    <a href="${ctx}/admin/facilities" class="btn-mintlify-secondary text-decoration-none">&#8592; Danh sách</a>
                </div>
            </div>

            <div class="row g-3">

                <%-- Cột trái: Thông tin cơ sở --%>
                <div class="col-lg-4">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Thông tin cơ sở</h3>
                        </div>
                        <div class="widget-surface-body">
                            <table class="info-table">
                                <tr>
                                    <td class="info-label">Mã cơ sở</td>
                                    <td class="info-value info-value--bold"><c:out value="${facility.code}"/></td>
                                </tr>
                                <tr>
                                    <td class="info-label">Tên</td>
                                    <td class="info-value"><c:out value="${facility.name}"/></td>
                                </tr>
                                <tr>
                                    <td class="info-label">Địa chỉ</td>
                                    <td class="info-value"><c:out value="${facility.address}"/></td>
                                </tr>
                                <tr>
                                    <td class="info-label">Số tầng</td>
                                    <td class="info-value"><c:out value="${facility.floorCount}"/> tầng</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Phòng / tầng</td>
                                    <td class="info-value"><c:out value="${facility.roomsPerFloor}"/> phòng</td>
                                </tr>
                                <tr>
                                    <td class="info-label">Tổng phòng</td>
                                    <td class="info-value info-value--bold">
                                        <fmt:formatNumber value="${facility.totalRooms}" groupingUsed="true"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">Trạng thái</td>
                                    <td class="info-value">
                                        <jsp:include page="_facility-status-badge.jsp">
                                            <jsp:param name="statusValue" value="${facility.status}"/>
                                        </jsp:include>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">Ngày tạo</td>
                                    <td class="info-value" style="font-size:0.8125rem">
                                        <fmt:formatDate value="${facility.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </td>
                                </tr>
                            </table>

                            <c:if test="${facility.status == 'DRAFT'}">
                                <div class="alert alert-warning mt-3" style="font-size:0.8125rem;border-radius:8px">
                                    Cơ sở chưa được kích hoạt. Nhấn <strong>Kích hoạt</strong> để hệ thống sinh phòng tự động.
                                </div>
                            </c:if>
                            <c:if test="${facility.status == 'INACTIVE'}">
                                <div class="alert alert-secondary mt-3" style="font-size:0.8125rem;border-radius:8px">
                                    Cơ sở đã bị vô hiệu hóa. Không thể tạo dữ liệu mới nhưng vẫn xem được lịch sử.
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <%-- Cột phải: Danh sách phòng --%>
                <div class="col-lg-8">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Danh sách phòng</h3>
                            <c:if test="${not empty rooms}">
                                <span style="font-size:0.8rem;color:var(--hms-text-muted);font-weight:400">
                                    Nhấn vào dòng để xem chi tiết
                                </span>
                            </c:if>
                        </div>
                        <div class="widget-surface-body p-0">
                            <c:choose>
                                <c:when test="${facility.status == 'DRAFT'}">
                                    <div class="p-3">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Phòng chưa được sinh — kích hoạt cơ sở trước"/>
                                        </jsp:include>
                                    </div>
                                </c:when>
                                <c:when test="${not empty rooms}">
                                    <div class="table-responsive">
                                        <table class="table-mintlify">
                                            <thead>
                                                <tr>
                                                    <th>Mã phòng</th>
                                                    <th>Tầng</th>
                                                    <th>Diện tích</th>
                                                    <th>Người thuê</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="room" items="${rooms}">
                                                    <tr data-href="${ctx}/admin/rooms/${room.id}">
                                                        <td style="font-weight:600"><c:out value="${room.code}"/></td>
                                                        <td>Tầng ${room.floorLabel}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty room.area}">
                                                                    <fmt:formatNumber value="${room.area}" maxFractionDigits="1"/> m²
                                                                </c:when>
                                                                <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty room.tenantName}">
                                                                    <c:out value="${room.tenantName}"/>
                                                                </c:when>
                                                                <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${room.status == 'OCCUPIED'}">
                                                                    <span class="badge-hms badge-info">Đang thuê</span>
                                                                </c:when>
                                                                <c:when test="${room.status == 'AVAILABLE'}">
                                                                    <span class="badge-hms badge-success">Trống</span>
                                                                </c:when>
                                                                <c:when test="${room.status == 'MAINTENANCE'}">
                                                                    <span class="badge-hms badge-warning">Bảo trì</span>
                                                                </c:when>
                                                                <c:when test="${room.status == 'RESERVED'}">
                                                                    <span class="badge-hms badge-accent">Đặt trước</span>
                                                                </c:when>
                                                                <c:when test="${room.status == 'INACTIVE'}">
                                                                    <span class="badge-hms badge-neutral">Vô hiệu</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge-hms badge-neutral"><c:out value="${room.status}"/></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="p-3">
                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                            <jsp:param name="message" value="Chưa có phòng nào"/>
                                        </jsp:include>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
