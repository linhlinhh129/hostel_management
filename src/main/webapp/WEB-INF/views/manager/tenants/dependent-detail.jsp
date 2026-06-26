<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết người phụ thuộc - Manager"/>
<c:set var="pageRole"   value="MANAGER"/>
<c:set var="activeMenu" value="tenants"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <!-- Header -->
            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1><c:out value="${dependent.fullName}"/></h1>
                    <p>
                        Người phụ thuộc của
                        <a href="${ctx}/manager/tenants/${tenant.id}"
                           style="color:var(--hms-accent-deep);font-weight:700;text-decoration:none">
                            <c:out value="${tenant.fullName}"/>
                        </a>
                        &nbsp;<span style="color:var(--hms-stone)">·</span>&nbsp;
                        <span style="font-family:var(--hms-font-mono);font-size:0.8125rem; color:var(--hms-stone)">
                            <c:out value="${tenant.tenantCode}"/>
                        </span>
                    </p>
                </div>
                <div class="d-flex gap-2 flex-wrap">
                    <a href="${ctx}/manager/tenants/${tenant.id}"
                       class="quick-action-btn primary">
                        ← Người thuê chính
                    </a>
                    <a href="${ctx}/manager/tenants/${tenant.id}/dependents"
                       class="quick-action-btn">
                        Tất cả người phụ thuộc
                    </a>
                    <a href="${ctx}/manager/tenants" class="quick-action-btn">Danh sách</a>
                </div>
            </div>

            <div class="row g-3" style="max-width:800px">

                <!-- Thông tin người phụ thuộc -->
                <div class="col-lg-7">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Thông tin người phụ thuộc</h3>
                            <span class="badge-hms badge-info">
                                <c:out value="${dependent.relationship}"/>
                            </span>
                        </div>
                        <div class="widget-surface-body">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr>
                                    <td style="padding:9px 0;color:var(--hms-stone); width:42%;font-weight:500;vertical-align:top">
                                        Họ tên
                                    </td>
                                    <td style="padding:9px 0;font-weight:700;font-size:0.9375rem">
                                        <c:out value="${dependent.fullName}"/>
                                    </td>
                                </tr>
                                <tr style="border-top:1px solid var(--hms-border-soft)">
                                    <td style="padding:9px 0;color:var(--hms-stone);font-weight:500">
                                        Ngày sinh
                                    </td>
                                    <td style="padding:9px 0">
                                        <c:choose>
                                            <c:when test="${not empty dependent.dob}">
                                                <c:out value="${dependent.dob}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <em style="color:var(--hms-stone)">Chưa cập nhật</em>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr style="border-top:1px solid var(--hms-border-soft)">
                                    <td style="padding:9px 0;color:var(--hms-stone);font-weight:500">
                                        Giới tính
                                    </td>
                                    <td style="padding:9px 0">
                                        <c:choose>
                                            <c:when test="${dependent.gender == 'MALE'}">
                                                <span class="badge-hms badge-info">Nam</span>
                                            </c:when>
                                            <c:when test="${dependent.gender == 'FEMALE'}">
                                                <span class="badge-hms badge-accent">Nữ</span>
                                            </c:when>
                                            <c:when test="${not empty dependent.gender}">
                                                <span class="badge-hms badge-neutral">
                                                    <c:out value="${dependent.gender}"/>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <em style="color:var(--hms-stone)">—</em>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr style="border-top:1px solid var(--hms-border-soft)">
                                    <td style="padding:9px 0;color:var(--hms-stone);font-weight:500">
                                        Quan hệ với người thuê
                                    </td>
                                    <td style="padding:9px 0">
                                        <span class="badge-hms badge-info" style="font-size:0.75rem">
                                            <c:out value="${dependent.relationship}"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr style="border-top:1px solid var(--hms-border-soft)">
                                    <td style="padding:9px 0;color:var(--hms-stone);font-weight:500">
                                        Số điện thoại
                                    </td>
                                    <td style="padding:9px 0">
                                        <c:choose>
                                            <c:when test="${not empty dependent.phone}">
                                                <c:out value="${dependent.phone}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <em style="color:var(--hms-stone)">Chưa có</em>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Card người thuê chính — Manager.md §6 bắt buộc clickable -->
                <div class="col-lg-5">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Người thuê chính</h3>
                        </div>
                        <div class="widget-surface-body">
                            <!-- Avatar + tên -->
                            <div style="display:flex;align-items:center;gap:0.875rem;margin-bottom:1rem">
                                <div style="width:46px;height:46px;border-radius:var(--hms-radius);
                                            background:linear-gradient(135deg,var(--hms-accent) 0%,var(--hms-accent-soft) 100%);
                                            display:flex;align-items:center;justify-content:center;
                                            color:#fff;font-size:1.0625rem;font-weight:800;flex-shrink:0;
                                            box-shadow:0 3px 10px rgba(0,212,164,0.28)">
                                    ${not empty tenant.fullName ? tenant.fullName.charAt(0) : 'T'}
                                </div>
                                <div>
                                    <div style="font-weight:700;font-size:0.9375rem;color:var(--hms-ink)">
                                        <c:out value="${tenant.fullName}"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone);
                                                font-family:var(--hms-font-mono);margin-top:2px">
                                        <c:out value="${tenant.tenantCode}"/>
                                    </div>
                                </div>
                            </div>

                            <!-- Meta info -->
                            <div style="display:flex;flex-direction:column;gap:6px;margin-bottom:1rem">
                                <c:if test="${not empty tenant.phone}">
                                    <div style="font-size:0.8125rem;color:var(--hms-slate);
                                                display:flex;align-items:center;gap:6px">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                                             stroke="var(--hms-stone)" stroke-width="2">
                                            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 13a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.56 2h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/>
                                        </svg>
                                        <c:out value="${tenant.phone}"/>
                                    </div>
                                </c:if>
                                <c:if test="${not empty tenant.roomCode}">
                                    <div style="font-size:0.8125rem;color:var(--hms-slate);
                                                display:flex;align-items:center;gap:6px">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
                                             stroke="var(--hms-stone)" stroke-width="2">
                                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                                            <polyline points="9 22 9 12 15 12 15 22"/>
                                        </svg>
                                        Phòng <strong><c:out value="${tenant.roomCode}"/></strong>
                                    </div>
                                </c:if>
                                <c:if test="${tenant.status == 'ACTIVE'}">
                                    <span class="badge-hms badge-success" style="align-self:flex-start">
                                        Đang thuê
                                    </span>
                                </c:if>
                            </div>

                            <!-- CTA — Manager.md §6: click về người thuê chính -->
                            <a href="${ctx}/manager/tenants/${tenant.id}"
                               class="quick-action-btn primary"
                               style="width:100%;justify-content:center">
                                Xem hồ sơ đầy đủ →
                            </a>
                        </div>
                    </div>
                </div>

            </div><!-- /row -->
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
