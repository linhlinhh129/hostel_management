<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Hóa đơn - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="invoices"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Hóa đơn của bạn</h1>
                        <p>Lịch sử hóa đơn hàng tháng</p>
                    </div>
                </div>
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
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Danh sách hóa đơn</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <div class="table-responsive">
                                <table class="table-mintlify table-hover">
                                    <thead>
                                    <tr>
                                        <th>Kỳ thanh toán</th>
                                        <th>Hạn nộp</th>
                                        <th class="text-end">Số tiền</th>
                                        <th class="text-center">Trạng thái</th>
                                        <th class="text-center">Hành động</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="inv" items="${invoices}" varStatus="st">
                                        <tr style="animation:fadeInUp 0.4s ease ${st.index * 0.04}s both">
                                            <td style="font-weight:600;color:var(--hms-ink)">
                                                <c:out value="${inv.billingPeriod}"/>
                                            </td>
                                            <td style="font-size:0.875rem;color:var(--hms-stone)">
                                                <c:out value="${inv.dueDate}"/>
                                            </td>
                                            <td class="text-end" style="font-weight:700;
                                                color:${inv.status == 'OVERDUE' ? 'var(--hms-danger)' : inv.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-ink)'}">
                                                <fmt:formatNumber value="${inv.totalAmount}" pattern="#,##0"/> đ
                                            </td>
                                            <td class="text-center">
                                                <c:choose>
                                                    <c:when test="${inv.hasPendingPayment}">
                                                        <span class="badge-hms badge-info">⌛ Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${inv.status == 'PAID'}">
                                                        <span class="badge-hms badge-success">✓ Đã thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${inv.status == 'OVERDUE'}">
                                                        <span class="badge-hms badge-danger">⚠ Quá hạn</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-hms badge-warning">Chưa thanh toán</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <a href="${ctx}/tenant/invoices/${inv.id}" class="btn-mintlify-secondary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Chi tiết</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                        <jsp:param name="message" value="Chưa có hóa đơn nào"/>
                    </jsp:include>
                </c:otherwise>
            </c:choose>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
