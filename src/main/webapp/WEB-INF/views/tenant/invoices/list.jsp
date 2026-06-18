<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Hóa đơn - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="invoices"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell tenant-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Hóa đơn của bạn</h1>
                <p>Lịch sử hóa đơn hàng tháng</p>
            </div>

            <%-- Tổng nợ nếu có --%>
            <c:if test="${not empty unpaidTotal and unpaidTotal > 0}">
                <div class="alert alert-danger mb-3" style="border-radius:var(--hms-radius-md)">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                         stroke-width="2" style="margin-right:6px;flex-shrink:0">
                        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                        <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                    </svg>
                    Tổng hóa đơn chưa thanh toán:
                    <strong><fmt:formatNumber value="${unpaidTotal}" pattern="#,##0"/> đ</strong>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty invoices}">
                    <c:forEach var="inv" items="${invoices}" varStatus="st">
                        <a href="${ctx}/tenant/invoices/${inv.id}"
                           class="tenant-card"
                           style="animation-delay:${st.index * 0.05}s">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <div style="font-size:0.6875rem;font-weight:700;text-transform:uppercase;
                                                letter-spacing:0.05em;color:var(--hms-stone);margin-bottom:4px">
                                        Kỳ thanh toán
                                    </div>
                                    <div style="font-size:1.0625rem;font-weight:700;color:var(--hms-ink)">
                                        <c:out value="${inv.billingPeriod}"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone);margin-top:4px">
                                        Hạn: <c:out value="${inv.dueDate}"/>
                                    </div>
                                </div>
                                <div class="text-end">
                                    <div style="font-size:1.25rem;font-weight:800;
                                                color:${inv.status == 'OVERDUE' ? 'var(--hms-danger)' : inv.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-ink)'}">
                                        <fmt:formatNumber value="${inv.totalAmount}" pattern="#,##0"/> đ
                                    </div>
                                    <div class="mt-1">
                                        <c:choose>
                                            <c:when test="${inv.status == 'PAID'}">
                                                <span class="badge-hms badge-success">✓ Đã thanh toán</span>
                                            </c:when>
                                            <c:when test="${inv.status == 'OVERDUE'}">
                                                <span class="badge-hms badge-danger">⚠ Quá hạn</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-warning">Chưa TT</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                        <jsp:param name="message" value="Chưa có hóa đơn nào"/>
                    </jsp:include>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
    <jsp:include page="/WEB-INF/views/layout/tenant-bottom-nav.jsp"/>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
