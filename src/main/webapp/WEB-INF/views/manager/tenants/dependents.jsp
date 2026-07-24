<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Người phụ thuộc - Manager"/>
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
                    <h1>Người phụ thuộc của <c:out value="${tenant.fullName}"/></h1>
                    <p>
                        Mã người thuê:
                        <strong style="font-family:var(--hms-font-mono);color:var(--hms-accent-deep)">
                            <c:out value="${tenant.tenantCode}"/>
                        </strong>
                        &nbsp;·&nbsp;
                        <c:choose>
                            <c:when test="${not empty dependents}">
                                <c:out value="${dependents.size()}"/> người phụ thuộc
                            </c:when>
                            <c:otherwise>Không có người phụ thuộc</c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${ctx}/manager/tenants/${tenant.id}" class="quick-action-btn">
                        ← Chi tiết người thuê
                    </a>
                    <a href="${ctx}/manager/tenants" class="quick-action-btn">Danh sách</a>
                </div>
            </div>

            <!-- Danh sách người phụ thuộc -->
            <div class="data-surface">
                <div class="data-surface-header">
                    <h2>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--hms-accent-deep)"
                             stroke-width="2" style="margin-right:6px;vertical-align:-2px">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
                        </svg>
                        Danh sách người phụ thuộc
                    </h2>
                </div>

                <c:choose>
                    <c:when test="${not empty dependents}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Họ tên</th>
                                    <th>Ngày sinh</th>
                                    <th>Giới tính</th>
                                    <th>Quan hệ</th>
                                    <th>Số điện thoại</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="dep" items="${dependents}" varStatus="st">
                                    <tr>
                                        <td style="color:var(--hms-stone);font-size:0.75rem">
                                            <c:out value="${st.index + 1}"/>
                                        </td>
                                        <td>
                                            <a href="${ctx}/manager/tenants/dependents/${dep.id}"
                                               style="font-weight:600">
                                                <c:out value="${dep.fullName}"/>
                                            </a>
                                        </td>
                                        <td style="color:var(--hms-stone);font-size:0.8125rem">
                                            <fmt:parseDate value="${dep.dob}" pattern="yyyy-MM-dd" var="parsedDepDob" type="date" />
                                            <fmt:formatDate value="${parsedDepDob}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${dep.gender == 'MALE'}">
                                                    <span class="badge-hms badge-info">Nam</span>
                                                </c:when>
                                                <c:when test="${dep.gender == 'FEMALE'}">
                                                    <span class="badge-hms badge-accent">Nữ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral"><c:out value="${dep.gender}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="badge-hms badge-neutral">
                                                <c:out value="${dep.relationship}"/>
                                            </span>
                                        </td>
                                        <td style="font-size:0.8125rem">
                                            <c:choose>
                                                <c:when test="${not empty dep.phone}">
                                                    <c:out value="${dep.phone}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <em style="color:var(--hms-stone)">—</em>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${ctx}/manager/tenants/dependents/${dep.id}"
                                               style="font-size:0.8125rem;color:var(--hms-info);font-weight:600">
                                                Chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2.5rem;margin-bottom:0.75rem;animation:bounceSoft 2s ease-in-out infinite">👨‍👩‍👧</div>
                            <h4>Không có người phụ thuộc</h4>
                            <p>Người thuê <strong><c:out value="${tenant.fullName}"/></strong> chưa có người phụ thuộc đăng ký.</p>
                            <a href="${ctx}/manager/tenants/${tenant.id}" class="quick-action-btn">
                                ← Quay lại hồ sơ người thuê
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Card người thuê chính -->
            <div class="widget-surface" style="max-width:480px">
                <div class="widget-surface-header"><h3>Người thuê chính</h3></div>
                <div class="widget-surface-body">
                    <div style="display:flex;align-items:center;gap:1rem;margin-bottom:1rem">
                        <div style="width:44px;height:44px;border-radius:var(--hms-radius);
                                    background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft));
                                    display:flex;align-items:center;justify-content:center;
                                    color:#fff;font-size:1rem;font-weight:800;flex-shrink:0;
                                    box-shadow:0 2px 8px rgba(0,212,164,0.28)">
                            ${not empty tenant.fullName ? tenant.fullName.charAt(0) : 'T'}
                        </div>
                        <div>
                            <div style="font-weight:700;font-size:1rem;color:var(--hms-ink)">
                                <c:out value="${tenant.fullName}"/>
                            </div>
                            <div style="font-size:0.75rem;color:var(--hms-stone);
                                        font-family:var(--hms-font-mono);margin-top:2px">
                                <c:out value="${tenant.tenantCode}"/>
                            </div>
                        </div>
                    </div>
                    <div style="display:flex;gap:8px;flex-wrap:wrap">
                        <c:if test="${not empty tenant.phone}">
                            <span style="font-size:0.8125rem;color:var(--hms-slate)">
                                📞 <c:out value="${tenant.phone}"/>
                            </span>
                        </c:if>
                        <c:if test="${not empty tenant.roomCode}">
                            <span class="badge-hms badge-neutral">
                                🚪 <c:out value="${tenant.roomCode}"/>
                            </span>
                        </c:if>
                        <c:if test="${tenant.status == 'ACTIVE'}">
                            <span class="badge-hms badge-success">Đang thuê</span>
                        </c:if>
                    </div>
                    <a href="${ctx}/manager/tenants/${tenant.id}"
                       class="quick-action-btn primary mt-3"
                       style="width:100%;justify-content:center">
                        Xem đầy đủ hồ sơ →
                    </a>
                </div>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
