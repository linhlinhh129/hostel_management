<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết phòng - Admin"/>
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
                    <h1>Phòng <c:out value="${room.code}"/></h1>
                    <div class="d-flex align-items-center gap-2 mt-1">
                        <%-- Badge trạng thái phòng --%>
                        <c:choose>
                            <c:when test="${room.status == 'OCCUPIED'}">
                                <span class="badge-hms badge-info">Đang thuê</span>
                            </c:when>
                            <c:when test="${room.status == 'AVAILABLE' or room.status == 'ACTIVE'}">
                                <span class="badge-hms badge-success">Phòng trống</span>
                            </c:when>
                            <c:when test="${room.status == 'MAINTENANCE'}">
                                <span class="badge-hms badge-warning">Bảo trì</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-hms badge-neutral"><c:out value="${room.status}"/></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <a href="${ctx}/admin/facilities/${room.facilityId}"
                   class="btn-mintlify-secondary text-decoration-none"
                   style="position:relative;z-index:1">&#8592; Danh sách phòng</a>
            </div>

            <div class="row g-3">

                <%-- Cột trái: Thông tin phòng + chỉnh sửa --%>
                <div class="col-lg-5">

                    <%-- Thông tin căn hộ --%>
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Thông tin căn hộ</h3>
                        </div>
                        <div class="widget-surface-body" style="padding:0">
                            <table class="info-table">
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Mã phòng</td>
                                    <td class="info-value info-value--bold" style="padding:10px 16px">
                                        <c:out value="${room.code}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Cơ sở</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <a href="${ctx}/admin/facilities/${room.facilityId}">
                                            <c:out value="${room.facilityCode}"/> — <c:out value="${room.facilityName}"/>
                                        </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Tầng</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <c:out value="${room.floor}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Số phòng</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <c:out value="${room.roomNumber}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Diện tích</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <c:choose>
                                            <c:when test="${not empty room.areaRaw}">
                                                <strong><fmt:formatNumber value="${room.area}" maxFractionDigits="1"/></strong> m²
                                            </c:when>
                                            <c:otherwise><em class="text-muted">Chưa cập nhật</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Giá phòng</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <c:choose>
                                            <c:when test="${not empty room.roomFee}">
                                                <strong><fmt:formatNumber value="${room.roomFee}" pattern="#,###"/></strong> VNĐ
                                            </c:when>
                                            <c:otherwise><em class="text-muted">Chưa cập nhật</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Trạng thái</td>
                                    <td class="info-value" style="padding:10px 16px">
                                        <c:choose>
                                            <c:when test="${room.status == 'OCCUPIED'}">
                                                <span class="badge-hms badge-info">Đang thuê</span>
                                            </c:when>
                                            <c:when test="${room.status == 'AVAILABLE' or room.status == 'ACTIVE'}">
                                                <span class="badge-hms badge-success">Trống</span>
                                            </c:when>
                                            <c:when test="${room.status == 'MAINTENANCE'}">
                                                <span class="badge-hms badge-warning">Bảo trì</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-neutral"><c:out value="${room.status}"/></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Ngày tạo</td>
                                    <td class="info-value" style="padding:10px 16px;font-size:0.8125rem">
                                        <fmt:formatDate value="${room.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label" style="padding:10px 16px">Cập nhật lúc</td>
                                    <td class="info-value" style="padding:10px 16px;font-size:0.8125rem">
                                        <fmt:formatDate value="${room.updatedAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <%-- Chỉnh sửa thông tin (chỉ admin) --%>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Chỉnh sửa thông tin</h3>
                        </div>
                        <div class="widget-surface-body">
                            <c:choose>
                                <c:when test="${room.facilityStatus == 'INACTIVE'}">
                                    <div class="alert alert-secondary mb-0"
                                         style="font-size:0.8125rem;border-radius:8px">
                                        Cơ sở đã bị vô hiệu hoá. Không thể chỉnh sửa thông tin phòng.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${ctx}/admin/rooms/${room.id}/update"
                                          class="d-flex flex-column gap-3">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                        <div>
                                            <label class="form-label" style="font-size:0.8125rem">Diện tích (m²)</label>
                                            <input type="number" name="area" class="form-control"
                                                   step="0.1" min="0" max="9999"
                                                   placeholder="VD: 25.5"
                                                   value="${not empty room.areaRaw ? room.area : ''}"/>
                                        </div>
                                        <div>
                                            <label class="form-label" style="font-size:0.8125rem">Giá phòng (VNĐ)</label>
                                            <input type="number" name="roomFee" class="form-control"
                                                   min="0" step="1000"
                                                   placeholder="VD: 3000000"
                                                   value="${not empty room.roomFee ? room.roomFee : ''}"/>
                                        </div>
                                        <button type="submit" class="btn btn-mintlify-primary mt-1"
                                                style="width:auto">Lưu thay đổi</button>
                                    </form>
                                    <div class="form-text text-muted mt-2" style="font-size:0.8rem">
                                        Để trống ô nếu muốn xóa giá trị đã lưu.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>

                <%-- Cột phải: Người thuê (chỉ xem) --%>
                <div class="col-lg-7">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Người thuê hiện tại</h3>
                            <c:if test="${not empty room.tenantId}">
                                <span class="badge-hms badge-success">Có người thuê</span>
                            </c:if>
                        </div>
                        <div class="widget-surface-body">
                            <c:choose>

                                <%-- Có người thuê --%>
                                <c:when test="${not empty room.tenantId}">
                                    <div style="border:1px solid var(--hms-border-accent);
                                                border-radius:var(--hms-radius);
                                                background:var(--hms-accent-bg);
                                                padding:1.25rem">
                                        <div class="d-flex align-items-start gap-3 flex-wrap">

                                            <%-- Avatar --%>
                                            <div style="width:48px;height:48px;border-radius:50%;
                                                        background:var(--hms-surface-2);
                                                        display:flex;align-items:center;justify-content:center;
                                                        flex-shrink:0;border:1px solid var(--hms-border)">
                                                <svg width="22" height="22" viewBox="0 0 24 24" fill="none"
                                                     stroke="var(--hms-text-muted)" stroke-width="1.5">
                                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                                    <circle cx="12" cy="7" r="4"/>
                                                </svg>
                                            </div>

                                            <div style="flex:1;min-width:0">
                                                <div style="font-size:1.0625rem;font-weight:700;margin-bottom:4px;
                                                            color:var(--hms-ink)">
                                                    <c:out value="${room.tenantName}"/>
                                                </div>
                                                <table class="info-table" style="font-size:0.8125rem">
                                                    <c:if test="${not empty room.tenantCode}">
                                                        <tr>
                                                            <td class="info-label" style="padding:3px 0">Mã tài khoản</td>
                                                            <td class="info-value" style="padding:3px 0;font-weight:500">
                                                                <c:out value="${room.tenantCode}"/>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                    <c:if test="${not empty room.tenantPhone}">
                                                        <tr>
                                                            <td class="info-label" style="padding:3px 0">Số điện thoại</td>
                                                            <td class="info-value" style="padding:3px 0">
                                                                <c:out value="${room.tenantPhone}"/>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                    <c:if test="${not empty room.tenantEmail}">
                                                        <tr>
                                                            <td class="info-label" style="padding:3px 0">Email</td>
                                                            <td class="info-value" style="padding:3px 0">
                                                                <c:out value="${room.tenantEmail}"/>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </table>
                                                <div class="mt-2">
                                                    <span class="badge-hms badge-success">Đang thuê</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <%-- Ghi chú quyền admin --%>
                                    <div style="margin-top:12px;padding:10px 14px;
                                                background:var(--hms-surface-2);
                                                border:1px solid var(--hms-border);
                                                border-radius:var(--hms-radius);
                                                font-size:0.8125rem;color:var(--hms-text-muted)">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                                             stroke="currentColor" stroke-width="2"
                                             style="margin-right:5px;vertical-align:-2px">
                                            <circle cx="12" cy="12" r="10"/>
                                            <line x1="12" y1="8" x2="12" y2="12"/>
                                            <line x1="12" y1="16" x2="12.01" y2="16"/>
                                        </svg>
                                        Admin chỉ có quyền xem thông tin người thuê.
                                        Việc thêm / xóa người thuê do <strong>Manager</strong> quản lý.
                                    </div>
                                </c:when>

                                <%-- Phòng trống --%>
                                <c:otherwise>
                                    <div class="text-center py-4">
                                        <svg width="52" height="52" viewBox="0 0 24 24" fill="none"
                                             stroke="var(--hms-text-muted)" stroke-width="1.2"
                                             style="margin-bottom:12px">
                                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                            <circle cx="12" cy="7" r="4"/>
                                            <line x1="4" y1="4" x2="20" y2="20"
                                                  stroke="var(--hms-text-muted)" stroke-width="1.5"/>
                                        </svg>
                                        <div>
                                            <span class="badge-hms badge-neutral"
                                                  style="font-size:0.9375rem;padding:8px 16px">
                                                Phòng đang trống
                                            </span>
                                        </div>
                                        <p class="text-muted mt-3 mb-0" style="font-size:0.875rem">
                                            Hiện chưa có người thuê nào trong phòng này.
                                        </p>
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
